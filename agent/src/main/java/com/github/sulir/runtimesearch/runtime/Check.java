package com.github.sulir.runtimesearch.runtime;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Check {
    private static boolean searchActive;
    private static SearchOptions options;

    public static void initialize() {
        try {
            throw new BreakpointError();
        } catch (BreakpointError e) {
            // exception thrown to trigger a breakpoint in the IDE
        }
    }

    public static void runServer() {
        try {
            ServerSocket server = new ServerSocket(SearchOptions.PORT, 0, InetAddress.getLoopbackAddress());

            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Socket client = server.accept();
                        ObjectInputStream input = new ObjectInputStream(client.getInputStream());

                        options = (SearchOptions) input.readObject();
                        searchActive = options.isActive();

                        client.getOutputStream().write(SearchOptions.CONFIRMATION);
                        client.close();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }, "RuntimeSearch");

            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void perform(Object object) {
        if (!searchActive)
            return;

        if (object instanceof String) {
            String string = (String) object;
            if (string.contains(options.getText())) {
                try {
                    searchActive = false;
                    throw new BreakpointError();
                } catch (BreakpointError e) {
                    // exception thrown to trigger a breakpoint in the IDE
                }
            }
        }
    }
}
