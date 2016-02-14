package ru.mail.track.protocol;

import jdk.nashorn.internal.ir.annotations.Ignore;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import ru.mail.track.Message;
import ru.mail.track.command.CommandType;

import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by egor on 01.12.15.
 */
public class SerializationProtocolTest {
    private final Map<CommandType, Message> messages = new HashMap<>();

    @Before
    public void setup() {
        List<String> loginArgs = new ArrayList<String>(Arrays.asList("egor", "12345"));
        Message login = new Message("", CommandType.USER_LOGIN, 123, loginArgs);
        messages.put(CommandType.USER_LOGIN, login);

        List<String> chatSayArgs = new ArrayList<String>(Arrays.asList("1", "abahalabama"));
        Message chatSay = new Message("", CommandType.MSG, 321, chatSayArgs);
        messages.put(CommandType.MSG, chatSay);
    }

    @Test
    public void encodeLogin() throws Exception {
        Message origin = messages.get(CommandType.USER_LOGIN);
        Protocol protocol = new SerializationProtocol();
        byte[] data = protocol.encode(origin);
        Message copy = protocol.decode(data);
        System.out.println(copy);
        System.out.println(origin);
        assertTrue(copy.toString().equals(origin.toString()));
    }

    @Test
    public void encodeSend() throws Exception {
        Message origin = messages.get(CommandType.MSG);
        Protocol protocol = new SerializationProtocol();
        byte[] data = protocol.encode(origin);
        Message copy = protocol.decode(data);
        System.out.println(copy);
        System.out.println(origin);
        assertTrue(copy.toString().equals(origin.toString()));
    }

}