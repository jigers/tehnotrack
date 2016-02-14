package ru.mail.track.server;

/**
 * Created by Egor on 26.01.2016.
 */
public interface Server {
    void startServer() throws Exception;
    void destroyServer() throws Exception;
}
