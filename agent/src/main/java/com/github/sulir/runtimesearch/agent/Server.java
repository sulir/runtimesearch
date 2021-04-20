package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.shared.Check;
import com.github.sulir.runtimesearch.shared.SearchOptions;
import com.github.sulir.runtimesearch.shared.ServerConfig;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Server instance = new Server();

    private Server() { }

    public static Server getInstance() {
        return instance;
    }

    public void start() {
        try {
            ServerSocket socket = new ServerSocket(ServerConfig.PORT, 0, InetAddress.getLoopbackAddress());

            Thread thread = new Thread(() -> {
                while (true) {
                    readOptions(socket);
                }
            }, ServerConfig.THREAD_NAME);

            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readOptions(ServerSocket socket) {
        try {
            Socket client = socket.accept();
            ObjectInputStream input = new ObjectInputStream(client.getInputStream());
            Check.setOptions((SearchOptions) input.readObject());
            client.getOutputStream().write(ServerConfig.CONFIRMATION);
            client.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
