package View;

import Controller.SQLite;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerifyDialog extends JDialog {
    private JTextField tokenField;
    private JButton verifyBtn;
    private JLabel statusLabel;
    private Frame parentFrame;

    private boolean verifiedSuccessfully = false;

    public VerifyDialog(Frame parent) {
        super(parent, "Email Verification", true);
        this.parentFrame = parent;
        initComponents();
    }

    public boolean isVerifiedSuccessfully() {
        return verifiedSuccessfully;
    }

    private void initComponents() {
        tokenField = new JTextField(30);
        verifyBtn = new JButton("Verify");
        statusLabel = new JLabel("Enter your verification token:");

        verifyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String token = tokenField.getText().trim();
                if (token.isEmpty()) {
                    statusLabel.setText("Token cannot be empty.");
                    return;
                }
                SQLite db = new SQLite();
                boolean verified = db.verifyUserByToken(token);
                if (verified) {
                    verifiedSuccessfully = true;
                    dispose();
                } else {
                    statusLabel.setText("Invalid or expired token. Please try again.");
                }
            }
        });

        setLayout(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Verification Token:"));
        inputPanel.add(tokenField);
        inputPanel.add(verifyBtn);

        add(statusLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);

        setSize(450, 150);
        setLocationRelativeTo(parentFrame);
    }
}
