package integr.NIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.mail.track.Message;
import ru.mail.track.client.ClientOutputListener;
import ru.mail.track.client.NIOClient;
import ru.mail.track.server.NIOServer;
import ru.mail.track.server.Session;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Egor on 29.01.2016.
 */
public class ChatCreateAndSayTest {
    private NIOClient client1, client2, client3;
    private NIOServer server;
    private List<Message> messages = new ArrayList<>();
    ClientOutputListener listener1, listener2, listener3;
    @Before
    public void setup() throws Exception {
        server = new NIOServer();
        server.PORT = 19001;
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
        client3 = new NIOClient();

        client1.PORT = 19001;
        client2.PORT = 19001;
        client3.PORT = 19001;
        new Thread(() -> {
            client1.startClient();
        }).start();
        new Thread(() -> {
            client2.startClient();
        }).start();
        new Thread(() -> {
            client3.startClient();
        }).start();
        Thread.sleep(1000);

        listener1 = new ClientOutputListener();
        listener2 = new ClientOutputListener();
        listener3 = new ClientOutputListener();

        client1.registerListener(listener1);
        client2.registerListener(listener2);
        client3.registerListener(listener3);
    }


    public void process(NIOClient client, String input) throws Exception {
        client.addInputLine(input);
        Thread.sleep(1000);
    }

    @Test
    public void chatCreateAndSayTest() throws Exception {
        process(client1, "/l egor 12345");
        process(client2, "/l jigers 12345");
        process(client3, "/l nesev 12345");
        process(client1, "/chat_create 1,2");
        String s = listener1.messageList.get(1).getMessage();
        String[] number;
        number = s.split("Chat was successfully created. ID = ");
        process(client2, "/chat_say " + number[1] + " hellotest");
        assertTrue(
                listener1.messageList.get(2).getMessage().endsWith("hellotest") &&
                listener3.messageList.get(2).getMessage().endsWith("hellotest")
        );
    }


    @After
    public void close() throws InterruptedException {
        server.destroyServer();
    }
}
