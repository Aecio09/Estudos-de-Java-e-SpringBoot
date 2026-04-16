import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            String message;

            while ((message = in.readLine()) != null) {
                sendMessage("Servidor recebeu: " + message);
                Server.broadcastMessage("Cliente " + socket.getPort() + ": " + message, this);
            }
        } catch (IOException e) {
            System.out.println("Conexao encerrada com " + socket.getInetAddress());
        } finally {
            Server.removeClient(this);
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}

