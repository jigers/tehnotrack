package ru.mail.track.client;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import ru.mail.track.*;
import ru.mail.track.command.Command;
import ru.mail.track.command.CommandFiller;
import ru.mail.track.command.CommandType;
import ru.mail.track.protocol.Protocol;
import ru.mail.track.protocol.ProtocolException;
import ru.mail.track.protocol.SerializationProtocol;
import ru.mail.track.server.Session;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

/**
 * Created by egor on 16.11.15.
 */
public class ThreadedClient implements MessageListener {
    public static final int PORT = 19000;
    public static final String HOST = "localhost";
    ConnectionHandler handler;
    int waitingRespondId = -1;
    CommandType waitingRespondType;
    private Protocol protocol = new SerializationProtocol();
    private Map<String, CommandType> commandTypes = CommandFiller.fillStringMap();
    private Map<CommandType, Command> commands = CommandFiller.fillTypeMap();
    private Session session;

    public static void main(String[] args) {
        Protocol protocol = new SerializationProtocol();
        ThreadedClient client = new ThreadedClient();
        client.init();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            try {
                client.processInput(input);
            } catch (IOException e) {
                System.out.println("Error: incorrect input.");
                //TODO
            } catch (WrongNumberArgsException e) {
                //TODO
                e.printStackTrace();
            } catch (ProtocolException e) {
                //TODO
                e.printStackTrace();
            }
        }
    }

    private void processInput(String data) throws IOException, WrongNumberArgsException, ProtocolException {
        Message msg;
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
                waitingRespondId = id;
                waitingRespondType = msg.getType();
                handler.send(msg);
            } else {
                throw new IOException();
            }
        } else {
            throw new IOException();
        }
    }


    @Override
    public void onMessage(Session session, Message message) {
        if (message.getType() == CommandType.RESPOND_EXIT) {
            handler.stop();
            System.out.println(message.getMessage());
            System.exit(0);
        }
        if (message.getId() == waitingRespondId) {
            if (message.getMessage() != null) {
                System.out.println(message.getMessage());
            }

        } else {
            if (message.getType() == CommandType.MSG) {
                System.out.println(message.getMessage());
            } else {
                //TODO logging/exception
            }
        }
    }

    @PostConstruct
    public void init() {
        try {
            System.out.println("Socket started");
            Socket socket = new Socket(HOST, PORT);
            session = new Session(0);
            handler = new SocketConnectionHandler(protocol, session, socket);
            handler.addListener(this);

            Thread socketHandler = new Thread(handler);
            socketHandler.start();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
