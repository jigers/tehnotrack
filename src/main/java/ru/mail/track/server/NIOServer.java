package ru.mail.track.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.mockito.BDDMockito;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.ConnectionHandler;
import ru.mail.track.Message;
import ru.mail.track.NIOConnectionHandler;
import ru.mail.track.command.CommandHandler;
import ru.mail.track.command.CommandType;
import ru.mail.track.protocol.Protocol;
import ru.mail.track.protocol.ProtocolException;
import ru.mail.track.protocol.SerializationProtocol;

public class NIOServer implements Server, Runnable {
    static Logger log = LoggerFactory.getLogger(NIOServer.class);

    public int PORT = 19000;

    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024); // буфер, с которым будем работать
    private Map<SocketChannel, ByteBuffer> dataToWrite = new ConcurrentHashMap<>(); // Данные для записив канал
    private Map<SocketChannel, NIOConnectionHandler> channelHandlerMap = new ConcurrentHashMap<>();
    private Map<SocketChannel, Session> channelSessionMap = new ConcurrentHashMap<>();
    private Scanner scanner;
    private UserStorage userStorage;
    private ChatStorage chatStorage;
    private MessageStorage messageStorage;
    private Protocol protocol;
    private SessionManager sessionManager;
    private Thread cycle;
    private Thread readingThread;
    private ExecutorService service = Executors.newFixedThreadPool(5);
    private PGPoolingDataSource source;
    private ServerSocketChannel socketChannel;
    public static void main(String[] args) throws Exception {
        Server server = new NIOServer();
        server.startServer();
    }
    public NIOServer() throws Exception {}

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                log.info("Waiting on select()");

                // Блокируемся до получения евента на зарегистрированных каналах
                int num = selector.select();
                log.info("Raised events on {} channels", num);

                // Смторим, кто сгенерил евенты
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                // Проходим по всем источникам
                while (it.hasNext()) {
                    SelectionKey key = it.next();

                    // Если кто-то готов присоединиться
                    if (key.isAcceptable()) {
                        log.info("[acceptable]");

                        // Создаем канал для клиента и регистрируем его в сеоекторе
                        // Для нас интересно событие, когда клиент будет писать в канал
                        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        Session session = sessionManager.createSession(this.userStorage, this.chatStorage, this.messageStorage, null);
                        NIOConnectionHandler handler = new NIOConnectionHandler(session, protocol, this.dataToWrite, socketChannel, selector);
                        CommandHandler commandHandler = new CommandHandler(handler);
                        handler.addListener(commandHandler);
                        session.setConnectionHandler(handler);
                        channelHandlerMap.put(socketChannel, handler);
                        channelSessionMap.put(socketChannel, session);

                    } else if (key.isReadable()) {
                        log.info("[readable]");

                        // По ключу получаем соответствующий канал
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        NIOConnectionHandler handler = channelHandlerMap.get(socketChannel);
                        readBuffer.clear(); // чистим перед использование

                        int numRead;
                        try {

                            // читаем данные в буфер
                            numRead = socketChannel.read(readBuffer);
                        } catch (IOException e) {
                            // Ошибка чтения - закроем это соединений и отменим ключ в селекторе
                            log.error("Failed to read data from channel", e);
                            sessionManager.forgetUser(channelSessionMap.get(key.channel()).getUser().getId());
                            channelHandlerMap.remove(key.channel());
                            key.cancel();
                            socketChannel.close();
                            break;
                        }

                        if (numRead == -1) {
                            // С нами оборвали соединение со стороны клиента
                            log.error("Failed to read data from channel (-1)");
                            if (channelSessionMap.get(key.channel()).getIsLogin()) {
                                sessionManager.forgetUser(channelSessionMap.get(key.channel()).getUser().getId());
                            }
                            channelHandlerMap.remove(key.channel());
                            key.channel().close();
                            key.cancel();
                            break;
                        }



                        // Чтобы читать данные ИЗ буфера, делаем flip()
                        readBuffer.flip();
                        ByteBuffer protocolBuffer = ByteBuffer.allocate(readBuffer.limit());
                        for (int i = 0; i < readBuffer.limit(); i++) {
                            protocolBuffer.put(readBuffer.get());
                        }
                        try {
                            log.info("read: {}", protocol.decode(protocolBuffer.array()).getType());
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        }

                        handler.setInputBuffer(protocolBuffer);
                        service.execute(handler);

                        // В качестве эхо-сервера, кладем то, что получили от клиента обратно в канал на запись
                        //dataToWrite.put(socketChannel, readBuffer);

                        // Меняем состояние канала - теперь он готов для записи и в следующий select() он будет isWritable();


                    } else if (key.isWritable()) {
                        log.info("[writable]");

                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer data = dataToWrite.get(socketChannel);

                        socketChannel.write(data);

                        // Меняем состояние канала - теперь он готов для чтения и в следующий select() он будет isReadable();
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }

                // Нужно почитстить обработанные евенты
                keys.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void startServer() throws Exception{
        this.selector = Selector.open();
        this.source = new PGPoolingDataSource();

        try {
            Class.forName("org.postgresql.Driver");
            source.setDataSourceName("My DB");
            source.setServerName("178.62.140.149");
            source.setDatabaseName("jigers");
            source.setUser("senthil");
            source.setPassword("ubuntu");
            log.info("Connected to DB.");
            source.setMaxConnections(100);
        } catch (ClassNotFoundException e) {
            log.info("Failed to connect to DB.");
            e.printStackTrace();
        }

        this.userStorage = new DBUserStorage();
        this.chatStorage = new DBChatStorage();
        this.messageStorage = new DBMessageStorage();

        try {
            this.userStorage.init(source);
            this.messageStorage.init(source);
            this.chatStorage.init(source);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.protocol = new SerializationProtocol();
        this.sessionManager = new SessionManager();

        // Это серверный сокет
        socketChannel = ServerSocketChannel.open();
        // Привязали его к порту
        socketChannel.socket().setReuseAddress(true);
        socketChannel.socket().bind(new InetSocketAddress("localhost",PORT));

        // Должен быть неблокирующий для работы через selector
        socketChannel.configureBlocking(false);

        // Нас интересует событие коннекта клиента (как и для Socket - ACCEPT)
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        cycle = new Thread(this::run);
        cycle.start();
        this.readingThread = Thread.currentThread();
        this.scanner = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted()) {
            String line = "";
            if (scanner.hasNextLine()) {
                line = scanner.nextLine();
            } else {
                Thread.sleep(100);
            }
            if ("/exit".equals(line)) {
                log.info("Exit!");
                this.destroyServer();
                System.exit(0);
            }
        }
    }
    @Override
    public void destroyServer() {
        cycle.interrupt();
        readingThread.interrupt();
        selector.wakeup();
        for (Map.Entry<SocketChannel, NIOConnectionHandler> entry : channelHandlerMap.entrySet()) {
            service.execute(() -> {
                try {
                    entry.getValue().send(new Message(null, CommandType.RESPOND_EXIT, 0, null));
                    entry.getKey().close();
                } catch (IOException e) {
                    log.info("Failed to send Exit message!");
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                this.channelHandlerMap.remove(entry.getKey());
            });
        }
        try {

            this.userStorage.close();
            this.messageStorage.close();
            this.chatStorage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.source.close();
        try {
            this.socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (this.channelHandlerMap.size() > 0 || socketChannel.isOpen()) {
        }
        service.shutdown();
    }


}
