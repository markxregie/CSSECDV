package View;

import View.Frame.ValidationResult;
import javax.swing.*;

public class Register extends javax.swing.JPanel {

    public Frame frame;

    public Register() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        registerBtn = new javax.swing.JButton();
        passwordFld = new javax.swing.JPasswordField();
        usernameFld = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        confpassFld = new javax.swing.JPasswordField();
        backBtn = new javax.swing.JButton();
        showPasswordChk = new javax.swing.JCheckBox("Show Passwords");

        usernameErrorLbl = new javax.swing.JLabel();
        passwordErrorLbl = new javax.swing.JLabel();
        confpassErrorLbl = new javax.swing.JLabel();

        usernameErrorLbl.setFont(new java.awt.Font("Tahoma", 0, 12));
        usernameErrorLbl.setForeground(new java.awt.Color(255, 0, 0));
        passwordErrorLbl.setFont(new java.awt.Font("Tahoma", 0, 12));
        passwordErrorLbl.setForeground(new java.awt.Color(255, 0, 0));
        confpassErrorLbl.setFont(new java.awt.Font("Tahoma", 0, 12));
        confpassErrorLbl.setForeground(new java.awt.Color(255, 0, 0));

        registerBtn.setFont(new java.awt.Font("Tahoma", 1, 24)); 
        registerBtn.setText("REGISTER");
        registerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerBtnActionPerformed(evt);
            }
        });

        passwordFld.setBackground(new java.awt.Color(240, 240, 240));
        passwordFld.setFont(new java.awt.Font("Tahoma", 0, 18)); 
        passwordFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        passwordFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), 
                "PASSWORD", javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                new java.awt.Font("Tahoma", 0, 12)));

        usernameFld.setBackground(new java.awt.Color(240, 240, 240));
        usernameFld.setFont(new java.awt.Font("Tahoma", 0, 18)); 
        usernameFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        usernameFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), 
                "USERNAME", javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                new java.awt.Font("Tahoma", 0, 12)));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); 
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SECURITY Svcs");

        confpassFld.setBackground(new java.awt.Color(240, 240, 240));
        confpassFld.setFont(new java.awt.Font("Tahoma", 0, 18)); 
        confpassFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        confpassFld.setBorder(javax.swing.BorderFactory.createTitledBorder(
                new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), 
                "CONFIRM PASSWORD", javax.swing.border.TitledBorder.CENTER, 
                javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                new java.awt.Font("Tahoma", 0, 12)));

        showPasswordChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boolean show = showPasswordChk.isSelected();
                passwordFld.setEchoChar(show ? (char) 0 : '•');
                confpassFld.setEchoChar(show ? (char) 0 : '•');
            }
        });

        backBtn.setFont(new java.awt.Font("Tahoma", 1, 12)); 
        backBtn.setText("<Back");
        backBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(200, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(usernameErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usernameFld)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordFld, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(confpassErrorLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(confpassFld, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(showPasswordChk, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(200, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backBtn)
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(usernameFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameErrorLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passwordFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordErrorLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(confpassFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confpassErrorLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showPasswordChk)
                .addGap(18, 18, 18)
                .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
    }

    private void registerBtnActionPerformed(java.awt.event.ActionEvent evt) {
        // Clear previous error messages
        usernameErrorLbl.setText("");
        passwordErrorLbl.setText("");
        confpassErrorLbl.setText("");

        String username = usernameFld.getText();
        String password = new String(passwordFld.getPassword());
        String confirmPassword = new String(confpassFld.getPassword());

        ValidationResult result = frame.registerAction(username, password, confirmPassword);

        if (result.success) {
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            usernameFld.setText("");
            passwordFld.setText("");
            confpassFld.setText("");
            frame.loginNav();
        } else {
            // Display error messages under respective fields
            if (result.usernameError != null) {
                usernameErrorLbl.setText(result.usernameError);
            }
            if (result.passwordError != null) {
                passwordErrorLbl.setText(result.passwordError);
            }
            if (result.confirmPasswordError != null) {
                confpassErrorLbl.setText(result.confirmPasswordError);
            }
        }
    }

    private void backBtnActionPerformed(java.awt.event.ActionEvent evt) {
        usernameFld.setText("");
        passwordFld.setText("");
        confpassFld.setText("");
        usernameErrorLbl.setText("");
        passwordErrorLbl.setText("");
        confpassErrorLbl.setText("");
        frame.loginNav();
    }

    // Variables declaration
    private javax.swing.JButton backBtn;
    private javax.swing.JPasswordField confpassFld;
    private javax.swing.JLabel confpassErrorLbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPasswordField passwordFld;
    private javax.swing.JLabel passwordErrorLbl;
    private javax.swing.JButton registerBtn;
    private javax.swing.JTextField usernameFld;
    private javax.swing.JLabel usernameErrorLbl;
    private javax.swing.JCheckBox showPasswordChk;
    // End of variables declaration
}
