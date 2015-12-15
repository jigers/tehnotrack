package ru.mail.track.server;

import org.postgresql.ds.PGPoolingDataSource;
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
public class Server {
    private static final int PORT = 19000;
    private ServerSocket sSocket;
    private Protocol protocol;
    private SessionManager sessionManager;
    private volatile boolean isRunning;
    private Map<Long, ConnectionHandler> handlers = new HashMap<>();
    private AtomicLong internalCounter = new AtomicLong(0);
    private UserStorage userStorage;
    private ChatStorage chatStorage;
    public static void main(String[] args) {
        PGPoolingDataSource source = new PGPoolingDataSource();
        try {
            System.out.println("DB init started");
            Class.forName("org.postgresql.Driver");
            source.setDataSourceName("My DB");
            source.setServerName("178.62.140.149");
            source.setDatabaseName("jigers");
            source.setUser("senthil");
            source.setPassword("ubuntu");
            source.setMaxConnections(10);
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to connect to DB.");
            e.printStackTrace();
        }

        UserStorage userStorage = new DBUserStorage();
        try {
            userStorage.init(source);
        } catch (Exception e) {
            System.out.println("Error: failed to connect to DB.");
            e.printStackTrace();
        }

        ChatStorage chatStorage = new MemoryChatStorage();

        Protocol protocol = new SerializationProtocol();
        SessionManager sessionManager = new SessionManager();


        Server server = new Server(protocol, sessionManager, userStorage, chatStorage);

        try {
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws Exception {
        isRunning = true;
        while (isRunning) {
            Socket socket = sSocket.accept();
            Session session = sessionManager.createSession(userStorage, chatStorage, null);
            ConnectionHandler handler = new SocketConnectionHandler(protocol, session, socket);
            CommandHandler commandHandler = new CommandHandler(handler);
            handler.addListener(commandHandler);
            session.setConnectionHandler(handler);
            handlers.put(internalCounter.incrementAndGet(), handler);
            System.out.println("Server started");
            Thread thread = new Thread(handler);
            thread.start();
        }
    }

    public Server(Protocol protocol, SessionManager sessionManager,
                  UserStorage userStorage, ChatStorage chatStorage) {
        try {
            this.userStorage = userStorage;
            this.protocol = protocol;
            this.chatStorage = chatStorage;
            this.sessionManager = sessionManager;
            sSocket = new ServerSocket(PORT);
            sSocket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
