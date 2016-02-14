package integr.NIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.mail.track.Message;
import ru.mail.track.MessageListener;
import ru.mail.track.command.CommandType;
import ru.mail.track.server.NIOServer;
import ru.mail.track.client.NIOClient;
import ru.mail.track.server.Session;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;

public class SuccessLoginTest implements MessageListener {

    private NIOClient client;
    private NIOServer server;
    private List<Message> messages = new ArrayList<>();

    @Before
    public void setup() throws Exception {
        server = new NIOServer();
        server.PORT = 19007;
        new Thread(() -> {
            try {
                server.startServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000);
        client = new NIOClient();
        client.PORT = 19007;
        new Thread(() -> {
            client.startClient();
        }).start();
        Thread.sleep(1000);
        client.registerListener(this);
    }


    public void process(String input) throws Exception {
        client.addInputLine(input);
        Thread.sleep(1000);
    }

    @Test
    public void successLoginTest() throws Exception {
        process("/l egor 12345");
        assertTrue(messages.get(0).getType() == CommandType.RESPOND_OK);
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


