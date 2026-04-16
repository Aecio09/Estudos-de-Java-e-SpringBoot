import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Cliente {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(hostname, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            AtomicBoolean executando = new AtomicBoolean(true);

            Thread leitorMensagens = new Thread(() -> {
                try {
                    String resposta;
                    while (executando.get() && (resposta = in.readLine()) != null) {
                        System.out.println("\n" + resposta);
                        System.out.print("> ");
                    }
                } catch (IOException ignored) {
                }
            });
            leitorMensagens.setDaemon(true);
            leitorMensagens.start();

            System.out.println("Conectado ao servidor em " + hostname + ":" + port);
            System.out.println("Digite uma mensagem e pressione Enter. Digite 'exit' para sair.");
            System.out.print("> ");

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                out.println(userInput);
                System.out.print("> ");
            }

            executando.set(false);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
