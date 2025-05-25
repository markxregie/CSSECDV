package View;

import Model.User;
import Controller.SQLite;
import javax.swing.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class Login extends javax.swing.JPanel {

    public Frame frame;

    public Login() {
        initComponents();
    }

    // Import CaptchaDialog
    private CaptchaDialog captchaDialog;

    // Utility method to convert long timestamp to formatted date/time string
    private String formatLockoutTime(long timestamp) {
        if (timestamp <= 0) {
            return "N/A";
        }
        Instant instant = Instant.ofEpochMilli(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    public void clearFields() {
        usernameFld.setText("");
        passwordFld.setText("");
    }

    public void clearPasswordField() {
        passwordFld.setText("");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        usernameFld = new javax.swing.JTextField();
        passwordFld = new javax.swing.JPasswordField();
        showPasswordChk = new javax.swing.JCheckBox();
        registerBtn = new javax.swing.JButton();
        loginBtn = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SECURITY Svcs");
        jLabel1.setToolTipText("");

        usernameFld.setBackground(new java.awt.Color(240, 240, 240));
        usernameFld.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        usernameFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        usernameFld.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "USERNAME", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        passwordFld.setBackground(new java.awt.Color(240, 240, 240));
        passwordFld.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        passwordFld.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        passwordFld.setEchoChar('•');
        passwordFld.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), "PASSWORD", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N

        showPasswordChk.setText("Show Password");
        showPasswordChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (showPasswordChk.isSelected()) {
                    passwordFld.setEchoChar((char) 0); // Show characters
                } else {
                    passwordFld.setEchoChar('•'); // Hide characters
                }
            }
        });

        registerBtn.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        registerBtn.setText("REGISTER");
        registerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerBtnActionPerformed(evt);
            }
        });

        loginBtn.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        loginBtn.setText("LOGIN");
        loginBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginBtnActionPerformed(evt);
            }
        });

        forgotPasswordLbl = new javax.swing.JLabel();
        forgotPasswordLbl.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
forgotPasswordLbl.setForeground(new java.awt.Color(0, 0, 0));
        forgotPasswordLbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        forgotPasswordLbl.setText("Forgot Password?");
        forgotPasswordLbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        forgotPasswordLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                forgotPasswordLblMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(200, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(usernameFld)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordFld, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(registerBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(showPasswordChk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                        .addComponent(forgotPasswordLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(200, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(88, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(usernameFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passwordFld, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(showPasswordChk)
                    .addComponent(forgotPasswordLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(95, Short.MAX_VALUE))
        );
    }

    private void loginBtnActionPerformed(java.awt.event.ActionEvent evt) {
        // Show captcha dialog before login validation
        captchaDialog = new CaptchaDialog(frame);
        captchaDialog.setVisible(true);
        if (!captchaDialog.isVerifiedSuccessfully()) {
            JOptionPane.showMessageDialog(this, "Captcha verification failed. Please try again.", "Captcha Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = usernameFld.getText().trim();
        String password = new String(passwordFld.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            clearPasswordField();
            return;
        }

        SQLite db = new SQLite();
        db.addLockoutColumnsIfNotExist(); // Ensure columns exist in DB

        User authenticatedUser = null;

        for (User user : db.getUsers()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                if (user.getLocked() == 1 || user.getRole() == 1) {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                    clearPasswordField();
                    return;
                }
                long currentTime = System.currentTimeMillis();
                // Calculate progressive lockout duration based on failed attempts and multiplier
                int failedAttempts = user.getFailedAttempts();
                int lockoutMultiplier = user.getLockoutMultiplier();
                long lockoutDuration = 15 * 60 * 1000L * lockoutMultiplier;

                System.out.println("User: " + username + ", Failed Attempts: " + failedAttempts + ", Lockout Time: " + user.getLockoutTime() + ", Lockout Duration: " + lockoutDuration + ", Lockout Multiplier: " + lockoutMultiplier);
                System.out.println("Formatted Lockout Time: " + formatLockoutTime(user.getLockoutTime()));

                // Check if user is locked out
                if (user.getLockoutTime() > 0 && currentTime < user.getLockoutTime() + lockoutDuration) {
                    long remainingTimeSeconds = (user.getLockoutTime() + lockoutDuration - currentTime) / 1000;
                    long minutes = remainingTimeSeconds / 60;
                    long seconds = remainingTimeSeconds % 60;
                    String timeString = "";
                    if (minutes > 0) {
                        timeString += minutes + " minute" + (minutes > 1 ? "s" : "");
                    }
                    if (seconds > 0) {
                        if (!timeString.isEmpty()) {
                            timeString += " ";
                        }
                        timeString += seconds + " second" + (seconds > 1 ? "s" : "");
                    }
            JOptionPane.showMessageDialog(this, "Account locked due to multiple failed login attempts. Please try again after " + timeString + ".", "Account Locked", JOptionPane.ERROR_MESSAGE);
            clearPasswordField();
            return;
        } else if (user.getLockoutTime() > 0 && currentTime >= user.getLockoutTime() + lockoutDuration) {
                    // Lockout period expired, reset failed attempts and lockout time, but increment multiplier
                    db.updateFailedAttempts(username, 0);
                    db.updateLockoutTime(username, 0L);
                    db.updateLockoutMultiplier(username, lockoutMultiplier + 1);
                    user.setFailedAttempts(0);
                    user.setLockoutTime(0L);
                    user.setLockoutMultiplier(lockoutMultiplier + 1);
                }

                // Check password
                if (Frame.verifyPasswordSHA256(password, user.getPassword())) {
                    authenticatedUser = user;
                    // Reset failed attempts, lockout time, and multiplier on successful login
                    db.updateFailedAttempts(username, 0);
                    db.updateLockoutTime(username, 0L);
                    db.updateLockoutMultiplier(username, 0);
                    break;
                } else {
                    // Increment failed attempts
                    int newFailedAttempts = user.getFailedAttempts() + 1;
                    db.updateFailedAttempts(username, newFailedAttempts);

                    // Lock account if failed attempts reach multiple of 5
                    if (newFailedAttempts % 5 == 0) {
                        db.updateLockoutTime(username, currentTime);
                        long lockoutMinutes = 15 * (lockoutMultiplier + 1);
                        db.updateLockoutMultiplier(username, lockoutMultiplier + 1);
                        JOptionPane.showMessageDialog(this, "Account locked due to multiple failed login attempts. Please try again after " + lockoutMinutes + " minute" + (lockoutMinutes > 1 ? "s" : "") + ".", "Account Locked", JOptionPane.ERROR_MESSAGE);
                        clearPasswordField();
                        return;
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                        clearPasswordField();
                        return;
                    }
                }
            }
        }

if (authenticatedUser != null) {
            JOptionPane.showMessageDialog(this, "Login successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
            int role = authenticatedUser.getRole();
            switch (role) {
                case 5:
                    frame.showAdminHome(role);
                    break;
                case 4:
                    frame.showManagerHome(role);
                    break;
                case 3:
                    frame.showStaffHome(role);
                    break;
                case 2:
                    frame.showClientHome(role, authenticatedUser.getUsername());
                    break;
                default:
                    frame.showClientHome(2, authenticatedUser.getUsername());
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            clearPasswordField();
        }
    }

    private void forgotPasswordLblMouseClicked(java.awt.event.MouseEvent evt) {
        clearFields();
        if (frame != null) {
            frame.forgotPasswordNav();
        }
    }

    private void registerBtnActionPerformed(java.awt.event.ActionEvent evt) {
        clearFields();
        frame.registerNav();
    }

    // Variables declaration
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel forgotPasswordLbl;
    private javax.swing.JButton loginBtn;
    private javax.swing.JPasswordField passwordFld;
    private javax.swing.JButton registerBtn;
    private javax.swing.JTextField usernameFld;
    private javax.swing.JCheckBox showPasswordChk;
    // End of variables declaration
}