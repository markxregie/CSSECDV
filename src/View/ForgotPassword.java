package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ForgotPassword extends JPanel {

    public Frame frame;

    public ForgotPassword() {
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Forgot Password");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 36));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel infoLabel = new JLabel("Please contact admin to reset your password.");
        infoLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Tahoma", Font.BOLD, 18));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (frame != null) {
                    frame.loginNav();
                }
            }
        });

        this.add(titleLabel, BorderLayout.NORTH);
        this.add(infoLabel, BorderLayout.CENTER);
        this.add(backButton, BorderLayout.SOUTH);
    }
}
