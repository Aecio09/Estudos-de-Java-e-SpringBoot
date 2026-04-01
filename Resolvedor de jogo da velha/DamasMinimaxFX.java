import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class DamasMinimaxFX extends Application {

    static final int SIZE = 8;
    static final int TILE = 80;

    int[][] board = new int[SIZE][SIZE]; // 0 vazio, 1 jogador, 2 bot

    int selectedX = -1, selectedY = -1;

    GridPane root = new GridPane();

    @Override
    public void start(Stage stage) {
        initBoard();
        draw();

        stage.setScene(new Scene(root));
        stage.setTitle("Damas com Minimax");
        stage.show();
    }

    void initBoard() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if ((x + y) % 2 == 1) {
                    if (y < 3) board[y][x] = 2;
                    else if (y > 4) board[y][x] = 1;
                }
            }
        }
    }

    void draw() {
        root.getChildren().clear();

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {

                Rectangle r = new Rectangle(TILE, TILE);
                r.setFill((x + y) % 2 == 0 ? Color.BEIGE : Color.BROWN);

                int px = x, py = y;
                r.setOnMouseClicked(e -> click(px, py));

                root.add(r, x, y);

                if (board[y][x] != 0) {
                    Circle c = new Circle(TILE / 2 - 10);
                    c.setFill(board[y][x] == 1 ? Color.WHITE : Color.BLACK);
                    root.add(c, x, y);
                }
            }
        }
    }

    void click(int x, int y) {
        if (selectedX == -1 && board[y][x] == 1) {
            selectedX = x;
            selectedY = y;
            return;
        }

        if (selectedX != -1) {
            if (move(selectedX, selectedY, x, y)) {
                botMove();
            }
            selectedX = -1;
            selectedY = -1;
            draw();
        }
    }

    boolean move(int x1, int y1, int x2, int y2) {
        if (board[y2][x2] != 0) return false;

        int dx = x2 - x1;
        int dy = y2 - y1;

        // movimento simples
        if (Math.abs(dx) == 1 && dy == -1) {
            board[y2][x2] = board[y1][x1];
            board[y1][x1] = 0;
            return true;
        }

        // captura
        if (Math.abs(dx) == 2 && dy == -2) {
            int mx = (x1 + x2) / 2;
            int my = (y1 + y2) / 2;

            if (board[my][mx] == 2) {
                board[my][mx] = 0;
                board[y2][x2] = board[y1][x1];
                board[y1][x1] = 0;
                return true;
            }
        }

        return false;
    }

    // ================== MINIMAX ==================

    int minimax(int depth, boolean bot, int alpha, int beta) {

        if (depth == 0) return evaluate();

        List<int[]> moves = getMoves(bot ? 2 : 1);
        if (moves.isEmpty()) return evaluate();

        if (bot) {
            int max = -999;

            for (int[] m : moves) {
                int[][] copy = copyBoard();
                applyMove(m);

                // RECURSÃO: simula próximo turno
                int score = minimax(depth - 1, false, alpha, beta);

                board = copy;

                max = Math.max(max, score);
                alpha = Math.max(alpha, score);

                if (beta <= alpha) break; // PODA
            }
            return max;

        } else {
            int min = 999;

            for (int[] m : moves) {
                int[][] copy = copyBoard();
                applyMove(m);

                // RECURSÃO: simula próximo turno
                int score = minimax(depth - 1, true, alpha, beta);

                board = copy;

                min = Math.min(min, score);
                beta = Math.min(beta, score);

                if (beta <= alpha) break; // PODA
            }
            return min;
        }
    }

    int evaluate() {
        int score = 0;
        for (int[] row : board)
            for (int c : row)
                if (c == 2) score++;
                else if (c == 1) score--;
        return score;
    }

    void botMove() {
        int bestScore = -999;
        int[] bestMove = null;

        for (int[] m : getMoves(2)) {
            int[][] copy = copyBoard();
            applyMove(m);

            int score = minimax(3, false, -999, 999);

            board = copy;

            if (score > bestScore) {
                bestScore = score;
                bestMove = m;
            }
        }

        if (bestMove != null) applyMove(bestMove);
    }

    List<int[]> getMoves(int player) {
        List<int[]> moves = new ArrayList<>();

        int dir = (player == 1) ? -1 : 1;

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {

                if (board[y][x] == player) {

                    // simples
                    addMove(moves, x, y, x + 1, y + dir);
                    addMove(moves, x, y, x - 1, y + dir);

                    // captura
                    addMove(moves, x, y, x + 2, y + 2 * dir);
                    addMove(moves, x, y, x - 2, y + 2 * dir);
                }
            }
        }

        return moves;
    }

    void addMove(List<int[]> moves, int x1, int y1, int x2, int y2) {
        if (x2 >= 0 && x2 < SIZE && y2 >= 0 && y2 < SIZE) {
            if (board[y2][x2] == 0) {
                moves.add(new int[]{x1, y1, x2, y2});
            }
        }
    }

    void applyMove(int[] m) {
        int x1 = m[0], y1 = m[1], x2 = m[2], y2 = m[3];

        board[y2][x2] = board[y1][x1];
        board[y1][x1] = 0;

        if (Math.abs(x2 - x1) == 2) {
            board[(y1 + y2) / 2][(x1 + x2) / 2] = 0;
        }
    }

    int[][] copyBoard() {
        int[][] newB = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++)
            newB[i] = board[i].clone();
        return newB;
    }

    public static void main(String[] args) {
        launch();
    }
}