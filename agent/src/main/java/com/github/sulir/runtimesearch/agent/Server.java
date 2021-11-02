package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.shared.SearchOptions;
import com.github.sulir.runtimesearch.shared.SharedConfig;

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

    public void start(int port) {
        try {
            ServerSocket socket = new ServerSocket(port, 0, InetAddress.getLoopbackAddress());

            Thread thread = new Thread(() -> {
                while (true) {
                    readOptions(socket);
                }
            }, SharedConfig.SERVER_THREAD);

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
            client.getOutputStream().write(SharedConfig.CONFIRMATION_BYTE);
            client.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
