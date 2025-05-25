package Service;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaService {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int WIDTH = 200;
    private static final int HEIGHT = 50;
    private static final int LENGTH = 6;

    private String captchaText;

    public BufferedImage generateCaptchaImage() {
        captchaText = generateRandomText();
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Fill background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        Random rand = new Random();

        // Draw text with random font, color, and rotation for distortion
        for (int i = 0; i < LENGTH; i++) {
            int fontSize = 30 + rand.nextInt(10);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            g2d.setColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));

            // Apply rotation for distortion
            AffineTransform original = g2d.getTransform();
            double angle = (rand.nextDouble() - 0.5) * Math.PI / 4; // Rotate between -45 to 45 degrees
            g2d.rotate(angle, 30 * i + 20, HEIGHT / 2);

            int x = 30 * i + 10;
            int y = 30 + rand.nextInt(15);
            g2d.drawString(String.valueOf(captchaText.charAt(i)), x, y);

            // Reset transform to original
            g2d.setTransform(original);
        }

        // Add more noise lines for increased distortion
        for (int i = 0; i < 20; i++) {
            g2d.setColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
            int x1 = rand.nextInt(WIDTH);
            int y1 = rand.nextInt(HEIGHT);
            int x2 = rand.nextInt(WIDTH);
            int y2 = rand.nextInt(HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.dispose();
        return image;
    }

    private String generateRandomText() {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARS.charAt(rand.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public boolean validateCaptcha(String input) {
        if (input == null) return false;
        return input.equalsIgnoreCase(captchaText);
    }
}