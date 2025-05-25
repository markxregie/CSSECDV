package View;

import Service.CaptchaService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class CaptchaDialog extends JDialog {
    private JLabel captchaImageLabel;
    private JTextField captchaInput;
    private JButton verifyBtn;
    private JLabel statusLabel;
    private boolean verifiedSuccessfully = false;
    private int failedAttempts = 0;  // Track failed attempts

    private final CaptchaService captchaService = new CaptchaService();

    public CaptchaDialog(Frame parent) {
        super(parent, "Captcha Verification", true);
        initComponents();
        generateCaptcha();
    }

    public boolean isVerifiedSuccessfully() {
        return verifiedSuccessfully;
    }

    private void initComponents() {
        captchaImageLabel = new JLabel();
        captchaImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        captchaInput = new JTextField(20);

        verifyBtn = new JButton("Verify");
        statusLabel = new JLabel("Enter the captcha text:");
//
verifyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = captchaInput.getText().trim();
                if (input.isEmpty()) {
                    statusLabel.setText("Captcha input cannot be empty.");
                    return;
                }
                if (captchaService.validateCaptcha(input)) {
                    verifiedSuccessfully = true;
                    dispose();
                } else {
                    failedAttempts++;
                    if (failedAttempts >= 5) {
                        statusLabel.setText("Maximum attempts reached. Please try again.");
                        verifyBtn.setEnabled(false);
                        captchaInput.setEnabled(false);
                    } else {
                        statusLabel.setText("Incorrect captcha. Please try again.");
                        generateCaptcha();
                        captchaInput.setText("");
                    }
                }
            }
        });

        setLayout(new BorderLayout(10, 10));
        JPanel captchaPanel = new JPanel(new BorderLayout());
        captchaPanel.add(captchaImageLabel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Captcha:"));
        inputPanel.add(captchaInput);
        inputPanel.add(verifyBtn);

        add(statusLabel, BorderLayout.NORTH);
        add(captchaPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setSize(400, 200);
        setLocationRelativeTo(getParent());
    }

    private void generateCaptcha() {
        BufferedImage captchaImage = captchaService.generateCaptchaImage();
        captchaImageLabel.setIcon(new ImageIcon(captchaImage));
    }
}