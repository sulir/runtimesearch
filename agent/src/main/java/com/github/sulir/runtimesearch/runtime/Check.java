package com.github.sulir.runtimesearch.runtime;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Check {
    public static final int PORT = 4321;
    public static String searchValue;

    public static void initialize() {
        try {
            throw new BreakpointError();
        } catch (BreakpointError e) {
            // exception thrown to trigger a breakpoint in the IDE
        }
    }

    public static void runServer() {
        try {
            ServerSocket server = new ServerSocket(PORT, 0, InetAddress.getLoopbackAddress());

            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Socket client = server.accept();
                        ObjectInputStream input = new ObjectInputStream(client.getInputStream());
                        searchValue = (String) input.readObject();
                        client.close();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void perform(Object object) {
        if (searchValue == null)
            return;

        if (object instanceof String) {
            String string = (String) object;
            if (string.contains(searchValue)) {
                try {
                    searchValue = null;
                    throw new BreakpointError();
                } catch (BreakpointError e) {
                    // exception thrown to trigger a breakpoint in the IDE
                }
            }
        }
    }
}
