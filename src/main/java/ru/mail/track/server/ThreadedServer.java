package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import ru.mail.track.ConnectionHandler;
import ru.mail.track.SocketConnectionHandler;
import ru.mail.track.command.CommandHandler;
import ru.mail.track.protocol.Protocol;
import ru.mail.track.protocol.SerializationProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by egor on 12.11.15.
 */
public class ThreadedServer implements Server {
    private static final int PORT = 19000;
    private ServerSocket sSocket;
    private Protocol protocol;
    private SessionManager sessionManager;
    private volatile boolean isRunning;
    private Map<Long, ConnectionHandler> handlers = new HashMap<>();
    private AtomicLong internalCounter = new AtomicLong(0);
    private UserStorage userStorage;
    private ChatStorage chatStorage;
    private MessageStorage messageStorage;

    static Logger log = LoggerFactory.getLogger(CommandHandler.class);

    public static void main(String[] args) {
        PGPoolingDataSource source = new PGPoolingDataSource();

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

        UserStorage userStorage = new DBUserStorage();
        ChatStorage chatStorage = new DBChatStorage();
        MessageStorage messageStorage = new DBMessageStorage();

        try {
            userStorage.init(source);
            messageStorage.init(source);
            chatStorage.init(source);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Protocol protocol = new SerializationProtocol();
        SessionManager sessionManager = new SessionManager();


        ThreadedServer server = new ThreadedServer(protocol, sessionManager, userStorage, chatStorage, messageStorage);

        try {
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        isRunning = true;

        while (isRunning) {
            Socket socket = null;
            try {
                socket = sSocket.accept();
                log.info("Accepted. " + socket.getInetAddress());
                Session session = sessionManager.createSession(userStorage, chatStorage, messageStorage, null);
                ConnectionHandler handler = new SocketConnectionHandler(protocol, session, socket);
                CommandHandler commandHandler = new CommandHandler(handler);
                handler.addListener(commandHandler);
                session.setConnectionHandler(handler);
                handlers.put(internalCounter.incrementAndGet(), handler);

                Thread thread = new Thread(handler);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void destroyServer() {
        //TODO
    }

    public ThreadedServer(Protocol protocol, SessionManager sessionManager,
                          UserStorage userStorage, ChatStorage chatStorage, MessageStorage messageStorage) {
        try {
            this.userStorage = userStorage;
            this.protocol = protocol;
            this.chatStorage = chatStorage;
            this.sessionManager = sessionManager;
            this.messageStorage = messageStorage;
            sSocket = new ServerSocket(PORT);
            sSocket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
