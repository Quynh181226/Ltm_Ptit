package control;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ServerControl {
    private ServerSocket serverSocket;
    private final ExecutorService pool;

    public ServerControl() {
        this.pool = Executors.newFixedThreadPool(20);

        try {
            serverSocket = new ServerSocket(8080);
//           System.out.println("Server running ...");
            showMessage("Server running ...", "Server Status", JOptionPane.INFORMATION_MESSAGE);

            while (!serverSocket.isClosed()) {
                try {
                    Socket client = serverSocket.accept();

                    System.out.println("Client connected: " + client.getInetAddress() + ":" + client.getPort());
                    pool.execute(new ServerThread(client));

                } catch (IOException acceptEx) {
                    if (serverSocket.isClosed()) break;

                    acceptEx.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        //        System.out.println("Shutting down server...");
        showMessage("Shutting down server...", "Server Status", JOptionPane.INFORMATION_MESSAGE);
        try {
            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pool.shutdown();

        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void showMessage(String msg, String title, int messageType) {
        JOptionPane.showMessageDialog(null, msg, title, messageType);
    }
}