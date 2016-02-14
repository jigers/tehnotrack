package ru.mail.track.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.sun.javafx.scene.SubSceneHelper;
import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.track.Message;
import ru.mail.track.MessageListener;
import ru.mail.track.command.Command;
import ru.mail.track.command.CommandFiller;
import ru.mail.track.command.CommandType;
import ru.mail.track.protocol.Protocol;
import ru.mail.track.protocol.ProtocolException;
import ru.mail.track.protocol.SerializationProtocol;
import ru.mail.track.server.Session;

public class NIOClient implements Client {

    static Logger log = LoggerFactory.getLogger(NIOClient.class);


    public int PORT = 19000;

    private Selector selector;
    private SocketChannel channel;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private Protocol protocol;
    private Map<String, CommandType> commandTypes = CommandFiller.fillStringMap();
    private Map<CommandType, Command> commands = CommandFiller.fillTypeMap();
    private List<MessageListener> listeners = new ArrayList<>();
    private Thread connectionThread;
    private Thread readingThread;
    private boolean stop = false;
    BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);

    // TODO: Нужно создать блокирующую очередь, в которую складывать данные для обмена между потоками

    @Override
    public void init() {


    }

    public void addInputLine(String line) {
        try {
            queue.put(line);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SelectionKey key = channel.keyFor(selector);
        log.info("Add input line: {}", line);
        key.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    public static void main(String[] args) throws Exception {
        NIOClient client = new NIOClient();
        client.startClient();
    }

    public void print(Message msg) {
        this.listeners.forEach(messageListener -> messageListener.onMessage(null, msg));
        if (msg.getMessage() != null) {
            log.info("From server: message with ID = {} ", msg.getId());
            System.out.println(msg.getMessage());
        }
        if (msg.getType() == CommandType.RESPOND_EXIT) {
            System.out.println("Server aborted connection. Exiting...");
            this.close();
        }

    }

    public void registerListener(MessageListener listener) {
        this.listeners.add(listener);
    }

    private Message processInput(String data) throws IOException {
        Message msg = null;
        List<String> args ;
        Random r = new Random();
        int id = r.nextInt();
        if (data.startsWith("/")) {
            int argNumber = 1000000;
            if (data.startsWith("/say") || data.startsWith("/chat_say")) {
                argNumber = 3;
            }
            String[] tokens = data.split(" ", argNumber);
            args = new ArrayList<>(Arrays.asList(tokens));
            String commandName = args.get(0);
            args.remove(0);
            if (commandTypes.containsKey(commandName)) {
                Command cmd = commands.get(commandTypes.get(commandName));
                msg = new Message("", commandTypes.get(commandName), id, args);
            } else {
                throw new IOException();
            }
        } else {
            throw new IOException();
        }
        return msg;
    }
    public void startConnection() {
        while (!Thread.currentThread().isInterrupted()) {
            log.info("Waiting on select()...");
            int num = 0;
            try {
                num = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("Raised {} events", num);


            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey sKey = keyIterator.next();

                if (sKey.isConnectable() && sKey.channel().isOpen()) {
                    log.info("[connectable] {}", sKey.hashCode());

                    try {
                        channel.finishConnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // теперь в канал можно писать
                    sKey.interestOps(SelectionKey.OP_WRITE);
                } else if (sKey.isReadable()) {
                    log.info("[readable]");
                    buffer.clear();
                    int numRead = 0;
                    try {
                        numRead = channel.read(buffer);
                        if (numRead < 0) {
                            log.info("Connection to server lost. Exiting...");
                            this.close();
                            //System.exit(0);
                            break;

                        }
                        if (numRead > 0) {
                            buffer.flip();
                            ByteBuffer protocolBuffer = ByteBuffer.allocate(buffer.limit());
                            for (int i = 0; i < buffer.limit(); i++) {
                                //System.out.println(i);
                                protocolBuffer.put(buffer.get());
                            }
                            Message message = protocol.decode(protocolBuffer.array());
                            buffer.clear();
                            print(message);
                        }
                    } catch (IOException e) {
                        this.close();
                        break;
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                        this.close();
                        break;
                    }

                } else if (sKey.isWritable()) {
                    log.info("[writable]");

                    //TODO: здесь нужно вытащить данные из очереди и отдать их на сервер


                    String line = queue.poll();
                    if (line != null) {
                        try {
                            channel.write(ByteBuffer.wrap(protocol.encode(processInput(line))));
                        } catch (IOException e) {
                            System.out.println("Error: incorrect input.");
                            log.error("Error: incorrect input.");
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        }
                    }
                    // Ждем записи в канал
                    sKey.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }
    public void startClient() {
        protocol = new SerializationProtocol();
        try {
            selector = Selector.open();
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_CONNECT);

            channel.connect(new InetSocketAddress("localhost", PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        connectionThread = new Thread(() -> {
            this.startConnection();
        });
        connectionThread.start();

        // Слушаем ввод данных с консоли
        readingThread = Thread.currentThread();
        Scanner scanner = new Scanner(System.in);
        while (!Thread.currentThread().isInterrupted()) {
            String line = null;
            if (scanner.hasNextLine()) {
                line = scanner.nextLine();
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.info("Exiting reading loop.");
                }
            }
            if ("/exit".equals(line)) {
                log.info("Exit!");
                this.close();
                break;
            } else {
                addInputLine(line);
            }
        }



    }
    public void close() {
        selector.keys().forEach(key -> {
            try {
                key.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        readingThread.interrupt();
        //this.addInputLine("/exit");

        connectionThread.interrupt();
        selector.wakeup();
        log.info("Closed");
    }
}
