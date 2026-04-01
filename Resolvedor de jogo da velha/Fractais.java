import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Scanner;

public class Fractais {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("Escolha uma opção:");
        System.out.println("1 - Árvore Fractal");
        System.out.println("2 - Triângulo de Sierpinski");

        int opcao = sc.nextInt();

        if (opcao == 1) {
            gerarArvore();
            System.out.println("Árvore gerada: arvore.png");
        } else if (opcao == 2) {
            gerarSierpinski();
            System.out.println("Sierpinski gerado: sierpinski.png");
        } else {
            System.out.println("Opção inválida");
        }

        sc.close();
    }

    // =========================
    // 🌳 ÁRVORE FRACTAL
    // =========================
    public static void gerarArvore() throws Exception {
        int largura = 800;
        int altura = 600;

        BufferedImage img = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        // Fundo degradê simples
        Color corTopo = new Color(
                (int)(Math.random() * 256),
                (int)(Math.random() * 256),
                (int)(Math.random() * 256)
        );

        Color corBase = new Color(
                (int)(Math.random() * 256),
                (int)(Math.random() * 256),
                (int)(Math.random() * 256)
        );

        for (int y = 0; y < altura; y++) {
            float t = (float) y / altura;

            int r = (int) (corTopo.getRed() * (1 - t) + corBase.getRed() * t);
            int gCor = (int) (corTopo.getGreen() * (1 - t) + corBase.getGreen() * t);
            int b = (int) (corTopo.getBlue() * (1 - t) + corBase.getBlue() * t);

            g.setColor(new Color(r, gCor, b));
            g.drawLine(0, y, largura, y);
        }

        desenharArvore(g, largura / 2, altura - 50, -90, 120);

        ImageIO.write(img, "png", new File("arvore.png"));
    }

    public static void desenharArvore(Graphics2D g, int x1, int y1, double angulo, int tamanho) {

        if (tamanho < 10) return;

        int x2 = x1 + (int) (Math.cos(Math.toRadians(angulo)) * tamanho);
        int y2 = y1 + (int) (Math.sin(Math.toRadians(angulo)) * tamanho);

        // Cor varia conforme o tamanho (tronco → galhos)
        if (tamanho > 40) {
            g.setColor(new Color(101, 67, 33)); // marrom
        } else {
            g.setColor(new Color(34, 139, 34)); // verde
        }

        g.drawLine(x1, y1, x2, y2);

        desenharArvore(g, x2, y2, angulo - 25, tamanho - 15);
        desenharArvore(g, x2, y2, angulo + 25, tamanho - 15);
    }

    // =========================
    // 🔺 SIERPINSKI
    // =========================
    public static void gerarSierpinski() throws Exception {
        int tamanho = 600;

        BufferedImage img = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, tamanho, tamanho);

        Point p1 = new Point(tamanho / 2, 50);
        Point p2 = new Point(50, tamanho - 50);
        Point p3 = new Point(tamanho - 50, tamanho - 50);

        desenharSierpinski(g, p1, p2, p3, 6);

        ImageIO.write(img, "png", new File("sierpinski.png"));
    }

    public static void desenharSierpinski(Graphics2D g, Point p1, Point p2, Point p3, int nivel) {

        if (nivel == 0) {
            int[] x = {p1.x, p2.x, p3.x,};
            int[] y = {p1.y, p2.y, p3.y};

            // Cor aleatória (dá um efeito legal)
            g.setColor(new Color(
                    (int)(Math.random() * 255),
                    (int)(Math.random() * 255),
                    (int)(Math.random() * 255)
            ));

            g.fillPolygon(x, y, 3);
            return;
        }

        Point m12 = meio(p1, p2);
        Point m23 = meio(p2, p3);
        Point m31 = meio(p3, p1);

        desenharSierpinski(g, p1, m12, m31, nivel - 1);
        desenharSierpinski(g, m12, p2, m23, nivel - 1);
        desenharSierpinski(g, m31, m23, p3, nivel - 1);
    }

    public static Point meio(Point a, Point b) {
        return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
    }
}