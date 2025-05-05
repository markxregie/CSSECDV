package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResetPassword extends JPanel {

    public Frame frame;

    private JPasswordField passwordFld;
    private JPasswordField confirmPasswordFld;
    private JCheckBox showPasswordChk;
    private JLabel passwordErrorLbl;
    private JLabel confirmPasswordErrorLbl;
    private JButton resetBtn;
    private JButton backBtn;
    private JLabel titleLabel;
    private JLabel tokenLabel;
    private JTextField tokenFld;
    private JLabel tokenErrorLbl;

    public ResetPassword() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        passwordFld = new JPasswordField();
        confirmPasswordFld = new JPasswordField();
        passwordErrorLbl = new JLabel();
        confirmPasswordErrorLbl = new JLabel();
        resetBtn = new JButton();
        backBtn = new JButton();
        titleLabel = new JLabel();
        tokenLabel = new JLabel();
        tokenFld = new JTextField();
        tokenErrorLbl = new JLabel();

        passwordErrorLbl.setFont(new Font("Tahoma", 0, 12));
        passwordErrorLbl.setForeground(new Color(255, 0, 0));

        confirmPasswordErrorLbl.setFont(new Font("Tahoma", 0, 12));
        confirmPasswordErrorLbl.setForeground(new Color(255, 0, 0));

        tokenErrorLbl.setFont(new Font("Tahoma", 0, 12));
        tokenErrorLbl.setForeground(new Color(255, 0, 0));

        passwordFld.setBackground(new Color(240, 240, 240));
        passwordFld.setFont(new Font("Tahoma", 0, 18));
        passwordFld.setHorizontalAlignment(JTextField.CENTER);
        passwordFld.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2, true),
                "NEW PASSWORD", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 12)));

        confirmPasswordFld.setBackground(new Color(240, 240, 240));
        confirmPasswordFld.setFont(new Font("Tahoma", 0, 18));
        confirmPasswordFld.setHorizontalAlignment(JTextField.CENTER);
        confirmPasswordFld.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2, true),
                "CONFIRM PASSWORD", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 12)));

        showPasswordChk = new JCheckBox("Show Password");
        showPasswordChk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                boolean show = showPasswordChk.isSelected();
                passwordFld.setEchoChar(show ? (char) 0 : '•');
                confirmPasswordFld.setEchoChar(show ? (char) 0 : '•');
            }
        });

        tokenFld.setBackground(new Color(240, 240, 240));
        tokenFld.setFont(new Font("Tahoma", 0, 18));
        tokenFld.setHorizontalAlignment(JTextField.CENTER);
        tokenFld.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2, true),
                "RESET TOKEN", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 12)));

        resetBtn.setFont(new Font("Tahoma", 1, 24));
        resetBtn.setText("Reset Password");
        resetBtn.setPreferredSize(new java.awt.Dimension(450, 52));
        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (frame != null) {
                    frame.handleResetPassword(
                        tokenFld.getText(),
                        new String(passwordFld.getPassword()),
                        new String(confirmPasswordFld.getPassword())
                    );
                }
            }
        });

        backBtn.setFont(new Font("Tahoma", 1, 12));
        backBtn.setText("<Back");
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (frame != null) {
                    clearFields();
                    frame.loginNav();
                }
            }
        });

        titleLabel.setFont(new Font("Tahoma", 1, 48));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setText("Security Svcs");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(150, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tokenErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(confirmPasswordErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tokenFld)
                    .addComponent(confirmPasswordFld)
                    .addComponent(passwordFld)
                    .addComponent(showPasswordChk)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(150, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resetBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(tokenFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tokenErrorLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passwordFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordErrorLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(confirmPasswordFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confirmPasswordErrorLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(showPasswordChk)
                .addGap(18, 18, 18)
                .addComponent(resetBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(150, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tokenErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(confirmPasswordErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tokenFld)
                    .addComponent(confirmPasswordFld)
                    .addComponent(passwordFld)
                    .addComponent(showPasswordChk, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(150, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resetBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    public void setTokenError(String message) {
        tokenErrorLbl.setText(message);
    }

    public void setPasswordError(String message) {
        passwordErrorLbl.setText(message);
    }

    public void setConfirmPasswordError(String message) {
        confirmPasswordErrorLbl.setText(message);
    }

    public void clearFields() {
        tokenFld.setText("");
        passwordFld.setText("");
        confirmPasswordFld.setText("");
        tokenErrorLbl.setText("");
        passwordErrorLbl.setText("");
        confirmPasswordErrorLbl.setText("");
    }
}
