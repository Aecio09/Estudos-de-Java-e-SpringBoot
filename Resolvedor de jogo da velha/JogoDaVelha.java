import java.util.Scanner;

public class JogoDaVelha {

    static char[] board = {' ',' ',' ',' ',' ',' ',' ',' ',' '};

    static int[][] win = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
    };

    static boolean venceu(char p) {
        for (int[] c : win) {
            if (board[c[0]] == p && board[c[1]] == p && board[c[2]] == p)
                return true;
        }
        return false;
    }

    static boolean cheio() {
        for (char c : board)
            if (c == ' ') return false;
        return true;
    }

    // MINIMAX COM ALPHA-BETA (RECURSIVO)
    static int minimax(boolean botTurno, int alpha, int beta, int profundidade) {

        if (venceu('O')) return 10 - profundidade;
        if (venceu('X')) return profundidade - 10;
        if (cheio()) return 0;

        if (botTurno) { // MAX (bot)
            int melhor = -100;

            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {

                    board[i] = 'O';

                    // chamada recursiva explorando próxima jogada
                    int score = minimax(false, alpha, beta, profundidade + 1);

                    board[i] = ' ';

                    melhor = Math.max(melhor, score);
                    alpha = Math.max(alpha, melhor);

                    // PODA: se já é pior que o mínimo, corta
                    if (beta <= alpha) break;
                }
            }
            return melhor;

        } else { // MIN (jogador)
            int melhor = 100;

            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {

                    board[i] = 'X';

                    // chamada recursiva explorando próxima jogada
                    int score = minimax(true, alpha, beta, profundidade + 1);

                    board[i] = ' ';

                    melhor = Math.min(melhor, score);
                    beta = Math.min(beta, melhor);

                    // PODA: se já é pior que o máximo, corta
                    if (beta <= alpha) break;
                }
            }
            return melhor;
        }
    }

    static int melhorJogada() {
        int melhorScore = -100;
        int move = -1;

        int jogadasRestantes = 0;
        for (char c : board)
            if (c == ' ') jogadasRestantes++;

        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {

                board[i] = 'O';

                int score = minimax(false, -100, 100, 0);

                board[i] = ' ';

                // bônus estratégico
                if (i == 4) score += 1; // centro
                if (i == 0 || i == 2 || i == 6 || i == 8) score += 1; // cantos

                // FINAL DO JOGO: evitar empate passivo
                if (jogadasRestantes <= 3 && score == 0) {
                    score -= 1; // penaliza empate fácil
                }

                if (score > melhorScore) {
                    melhorScore = score;
                    move = i;
                }
            }
        }
        return move;
    }

    static void print() {
        for (int i = 0; i < 9; i++) {
            System.out.print(" " + board[i] + " ");
            if (i % 3 != 2) System.out.print("|");
            else if (i != 8) System.out.println("\n-----------");
        }
        System.out.println("\n");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            print();

            System.out.print("Sua jogada (0-8): ");
            int pos = sc.nextInt();

            if (board[pos] != ' ') continue;
            board[pos] = 'X';

            if (venceu('X') || cheio()) break;

            int bot = melhorJogada();
            board[bot] = 'O';

            if (venceu('O') || cheio()) break;
        }

        print();

        if (venceu('X')) System.out.println("Voce venceu!");
        else if (venceu('O')) System.out.println("Bot venceu!");
        else System.out.println("Empate!");

        sc.close();
    }
}