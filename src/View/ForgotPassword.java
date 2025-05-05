package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ForgotPassword extends JPanel {

    public Frame frame;

    private JTextField emailFld;
    private JLabel emailErrorLbl;
    private JButton resetBtn;
    private JButton backBtn;
    private JLabel titleLabel;

    public ForgotPassword() {
        initComponents();
    }

    public void clearFields() {
        emailFld.setText("");
        emailErrorLbl.setText("");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        emailFld = new javax.swing.JTextField();
        emailErrorLbl = new javax.swing.JLabel();
        resetBtn = new javax.swing.JButton();
        backBtn = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();

        emailErrorLbl.setFont(new java.awt.Font("Tahoma", 0, 12));
        emailErrorLbl.setForeground(new java.awt.Color(255, 0, 0));

        emailFld.setBackground(new java.awt.Color(240, 240, 240));
        emailFld.setFont(new java.awt.Font("Tahoma", 0, 18));
        emailFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        emailFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true),
                "EMAIL", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));

        resetBtn.setFont(new java.awt.Font("Tahoma", 1, 24));
        resetBtn.setText("Reset Password");
        resetBtn.setPreferredSize(new java.awt.Dimension(350, 52));
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (frame != null) {
                    frame.handlePasswordResetRequest(emailFld.getText());
                }
            }
        });

        backBtn.setFont(new java.awt.Font("Tahoma", 1, 12));
        backBtn.setText("<Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (frame != null) {
                    emailFld.setText("");
                    emailErrorLbl.setText("");
                    frame.loginNav();
                }
            }
        });

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 48));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Security Svcs");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(200, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(emailErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(emailFld)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(200, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backBtn)
                .addGap(24, 24, 24)
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(emailFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailErrorLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
    }

    public void setEmailError(String message) {
        emailErrorLbl.setText(message);
    }
}
