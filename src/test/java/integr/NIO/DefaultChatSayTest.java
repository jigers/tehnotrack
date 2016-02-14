package integr.NIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.mail.track.Message;
import ru.mail.track.MessageListener;
import ru.mail.track.client.NIOClient;
import ru.mail.track.command.CommandType;
import ru.mail.track.server.NIOServer;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Egor on 29.01.2016.
 */
public class DefaultChatSayTest implements MessageListener {
    private NIOClient client1, client2;
    private NIOServer server;
    private List<Message> messages = new ArrayList<>();

    @Before
    public void setup() throws Exception {
        server = new NIOServer();
        server.PORT = 19004;
        new Thread(() -> {
            try {
                server.startServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000);
        client1 = new NIOClient();
        client2 = new NIOClient();
        client1.PORT = 19004;
        client2.PORT = 19004;
        new Thread(() -> {
            client1.startClient();
        }).start();
        new Thread(() -> {
            client2.startClient();
        }).start();
        Thread.sleep(1000);
        client1.registerListener(this);
        client2.registerListener(this);
    }


    public void process(NIOClient client, String input) throws Exception {
        client.addInputLine(input);
        Thread.sleep(1000);
    }

    @Test
    public void defaultChatSayTest() throws Exception {
        process(client1, "/l egor 12345");
        process(client1, "/chat_say 1 Hello!!!");
        assertTrue(messages.get(0).getType() == CommandType.RESPOND_OK &&
                messages.get(1).getType() == CommandType.RESPOND_OK && messages.get(1).getMessage() == null);
    }


    @Override
    public void onMessage(Session session, Message message) {
        messages.add(message);
    }

    @After
    public void close() throws InterruptedException {
        server.destroyServer();
    }
}
