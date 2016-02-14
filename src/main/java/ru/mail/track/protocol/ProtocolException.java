package ru.mail.track.protocol;

/**
 * Created by egor on 12.11.15.
 */
public class ProtocolException extends Exception {

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(String message) {
        super(message);
    }
}
