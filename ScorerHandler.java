import java.net.*;
import java.io.*;

public class ScorerHandler implements Runnable {

    private final Socket         socket;
    private final int            scorerIndex;   // 0, 1, or 2
    private final BasketballScoreServer server;
    private final BufferedReader reader;
    private final PrintWriter    writer;

    public ScorerHandler(Socket socket, int index, BasketballScoreServer server)
            throws IOException {
        this.socket       = socket;
        this.scorerIndex  = index;
        this.server       = server;
        this.reader       = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer       = new PrintWriter(socket.getOutputStream(), true);
    }

    /** Called by the server to push messages/state to this specific scorer. */
    public PrintWriter getWriter() { return writer; }

    @Override
    public void run() {
        try {
            // Handshake — tell the client who it is
            writer.println("CONNECTED|SCORER_" + (scorerIndex + 1));
            server.log("[Scorer " + (scorerIndex + 1) + "] Handshake sent.");

            String line;
            while ((line = reader.readLine()) != null) {
                String cmd = line.trim().toUpperCase();
                if (!cmd.isEmpty()) {
                    server.handleCommand(cmd, scorerIndex);
                }
            }
        } catch (IOException e) {
            // Client closed connection — normal on disconnect
        } finally {
            server.scorerDisconnected(scorerIndex);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
