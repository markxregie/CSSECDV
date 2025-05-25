package View;

import Controller.Main;
import Controller.SQLite;
import Model.User;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

public class Frame extends javax.swing.JFrame {

    private Timer idleTimer;
    private final int IDLE_TIMEOUT = 15 * 60 * 1000; // 15 minutes in milliseconds

    // Constructor to initialize the frame and components
    public Frame() {
        initComponents();
        setupIdleTimer();
    }

    private void setupIdleTimer() {
        // Create a Swing Timer that triggers logout after IDLE_TIMEOUT
        idleTimer = new Timer(IDLE_TIMEOUT, e -> {
            // Perform automatic logout on idle timeout
            automaticLogout();
        });
        idleTimer.setRepeats(false);
        idleTimer.start();

        // Add global AWT event listener to detect user activity (mouse and keyboard)
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                if (event instanceof MouseEvent || event instanceof KeyEvent) {
                    resetIdleTimer();
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }

    private void resetIdleTimer() {
        if (idleTimer != null) {
            idleTimer.restart();
        }
    }

    private void automaticLogout() {
        // Show confirmation dialog or directly logout
        // Here, we directly logout without confirmation
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Clear login fields using public method
            loginPnl.clearFields();
            // Show login panel
            frameView.show(Container, "loginPnl");
            // Optionally show a message
            javax.swing.JOptionPane.showMessageDialog(this, "You have been logged out due to inactivity.", "Logged Out", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        });
    }

    // Utility method to generate 16-bit (2 bytes) salt
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[2]; // 16 bits = 2 bytes
        random.nextBytes(salt);
        return salt;
    }

    // Utility method to hash password with salt using SHA-256
    public static String hashPasswordSHA256(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes());
            // Combine salt + hash and encode as hex for storage
            byte[] saltAndHash = new byte[salt.length + hashed.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashed, 0, saltAndHash, salt.length, hashed.length);
            return bytesToHex(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    // Utility method to verify password with stored hex salt+hash
    public static boolean verifyPasswordSHA256(String password, String stored) {
        try {
            byte[] saltAndHash = hexToBytes(stored);
            byte[] salt = new byte[2];
            System.arraycopy(saltAndHash, 0, salt, 0, 2);
            byte[] hash = new byte[saltAndHash.length - 2];
            System.arraycopy(saltAndHash, 2, hash, 0, hash.length);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedInput = md.digest(password.getBytes());

            if (hashedInput.length != hash.length) {
                return false;
            }
            for (int i = 0; i < hash.length; i++) {
                if (hashedInput[i] != hash[i]) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Utility method to convert byte array to hex string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Utility method to convert hex string to byte array
    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    /* =================== Start of Password Reset Request Logic =================== */
    // Handles the password reset request logic
    public void handlePasswordResetRequest(String email) {
        // Clear previous error message
        forgotPasswordPnl.setEmailError("");

        if (email == null || email.isEmpty()) {
            forgotPasswordPnl.setEmailError("Email is required.");
            return;
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            forgotPasswordPnl.setEmailError("Invalid email format.");
            return;
        }

        SQLite db = new SQLite();
        User user = db.getUserByEmail(email);

        if (user == null) {
            // Show generic message to avoid user enumeration
            JOptionPane.showMessageDialog(this,
                "If the email is registered, you will receive a password reset email.",
                "Password Reset",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long now = System.currentTimeMillis();
        long lockoutTime = user.getForgotLockoutTime();

        if (lockoutTime > now) {
            long remainingMillis = lockoutTime - now;
            forgotPasswordPnl.startLockoutTimer(remainingMillis);
            return;
        } else if (lockoutTime != 0 && lockoutTime <= now) {
            // Lockout expired, reset counters
            db.updateForgotFailedAttempts(email, 0);
            db.updateForgotLockoutMultiplier(email, 0);
            db.updateForgotLockoutTime(email, 0);
            // Refresh user object after reset
            user = db.getUserByEmail(email);
        }

        // Increment failed attempts on every reset request to limit abuse
        int failedAttempts = user.getForgotFailedAttempts() + 1;
        db.updateForgotFailedAttempts(email, failedAttempts);

        int multiplier = user.getForgotLockoutMultiplier();
            if (failedAttempts >= 3) {
                multiplier++;
                long lockoutDuration = 30 * 60 * 1000L * (long)Math.pow(2, multiplier - 1); // exponential backoff starting at 30 minutes
                long newLockoutTime = now + lockoutDuration;
                db.updateForgotLockoutTime(email, newLockoutTime);
                db.updateForgotLockoutMultiplier(email, multiplier);
                forgotPasswordPnl.setEmailError("Too many attempts. Please try again after " + (lockoutDuration / 60000) + " minutes.");
                return;
            }

        // Proceed with password reset token generation and email sending
        String token = java.util.UUID.randomUUID().toString();
        long expiry = now + 3600 * 1000; // 1 hour expiry

        db.setPasswordResetToken(email, token, expiry);

        try {
            sendPasswordResetEmail(email, token);

        } catch (javax.mail.MessagingException e) {
            // On failure, increment failed attempts and possibly lockout
            // This block is now redundant due to increment above, but kept for safety
            int failedAttemptsCatch = user.getForgotFailedAttempts() + 1;
            db.updateForgotFailedAttempts(email, failedAttemptsCatch);

            int multiplierCatch = user.getForgotLockoutMultiplier();
            if (failedAttemptsCatch >= 3) {
                multiplierCatch++;
                long lockoutDurationCatch = 5 * 60 * 1000L * (long)Math.pow(2, multiplierCatch - 1);
                long newLockoutTimeCatch = now + lockoutDurationCatch;
                db.updateForgotLockoutTime(email, newLockoutTimeCatch);
                db.updateForgotLockoutMultiplier(email, multiplierCatch);
                forgotPasswordPnl.setEmailError("Too many attempts. Please try again after " + (lockoutDurationCatch / 60000) + " minutes.");
            } else {
                forgotPasswordPnl.setEmailError("Failed to send password reset email. Please try again later.");
            }
            return;
        }

        JOptionPane.showMessageDialog(this,
            "A password reset token has been sent to your email.\n" +
            "Please check your email for the token and use the Reset Password screen to reset your password.",
            "Password Reset",
            JOptionPane.INFORMATION_MESSAGE);

        resetPasswordNav();
    }
    

    /* =================== Start of Password Reset Handling Logic =================== */
    // Handles the logic for resetting the password using the token
    public void handleResetPassword(String token, String newPassword, String confirmPassword) {
        // Clear previous error messages
        resetPasswordPnl.setTokenError("");
        resetPasswordPnl.setPasswordError("");
        resetPasswordPnl.setConfirmPasswordError("");

        boolean hasError = false;

        if (token == null || token.isEmpty()) {
            resetPasswordPnl.setTokenError("Reset token is required.");
            hasError = true;
        }

        if (newPassword == null || newPassword.isEmpty()) {
            resetPasswordPnl.setPasswordError("New password is required.");
            hasError = true;
        } else {
            if (newPassword.length() < 8 || newPassword.length() > 64) {
                resetPasswordPnl.setPasswordError("Password must be between 8 and 64 characters.");
                hasError = true;
            }
            if (!newPassword.matches(".*[a-z].*")) {
                resetPasswordPnl.setPasswordError("Password must contain at least one lowercase letter.");
                hasError = true;
            }
            if (!newPassword.matches(".*[A-Z].*")) {
                resetPasswordPnl.setPasswordError("Password must contain at least one uppercase letter.");
                hasError = true;
            }
            if (!newPassword.matches(".*\\d.*")) {
                resetPasswordPnl.setPasswordError("Password must contain at least one digit.");
                hasError = true;
            }
            if (!newPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
                resetPasswordPnl.setPasswordError("Password must contain at least one special character.");
                hasError = true;
            }
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            resetPasswordPnl.setConfirmPasswordError("Confirm password is required.");
            hasError = true;
        } else if (!newPassword.equals(confirmPassword)) {
            resetPasswordPnl.setConfirmPasswordError("Passwords do not match.");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        SQLite db = new SQLite();
        User user = db.getUserByResetToken(token);
        if (user == null) {
            resetPasswordPnl.setTokenError("Invalid or expired reset token.");
            return;
        }

        // Hash the new password with SHA-256 + 16-bit salt
        byte[] salt = generateSalt();
        String hashedPassword = hashPasswordSHA256(newPassword, salt);

        // Update password in DB
        db.updatePassword(user.getId(), hashedPassword);

        // Log the password reset event
        String event = "Password Reset";
        String desc = "User " + user.getUsername() + " reset their password.";
        String timestamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
        db.addLogs(event, user.getUsername(), desc, timestamp);

        // Clear reset token
        db.clearResetToken(user.getId());

        JOptionPane.showMessageDialog(this,
            "Password has been reset successfully. You can now log in with your new password.",
            "Password Reset Successful",
            JOptionPane.INFORMATION_MESSAGE);

        resetPasswordPnl.clearFields();
        loginNav();
    }

    private void sendPasswordResetEmail(String recipientEmail, String token) throws javax.mail.MessagingException {
        // Configure email properties
        String host = "smtp.gmail.com"; // Replace with your SMTP server
        String from = "marklegend029@gmail.com"; // Replace with your sender email
        String pass = "cupl ooiy kqmd irbk"; // Replace with your email password

        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        javax.mail.Session session = javax.mail.Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(from, pass);
            }
          });

        javax.mail.Message message = new javax.mail.internet.MimeMessage(session);
        message.setFrom(new javax.mail.internet.InternetAddress(from));
        message.setRecipients(javax.mail.Message.RecipientType.TO,
            javax.mail.internet.InternetAddress.parse(recipientEmail));
        message.setSubject("Password Reset Request");
        String emailContent = "You requested a password reset.\n\n"
                + "Please use the following reset token in the Reset Password screen of the application:\n\n"
                + token + "\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "This token will expire in 1 hour.";
        message.setText(emailContent);

        javax.mail.Transport.send(message);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        Container = new javax.swing.JPanel();
        HomePnl = new javax.swing.JPanel();
        Content = new javax.swing.JPanel();
        Navigation = new javax.swing.JPanel();
        adminBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        managerBtn = new javax.swing.JButton();
        staffBtn = new javax.swing.JButton();
        clientBtn = new javax.swing.JButton();
        logoutBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(153, 153, 153));
        setMinimumSize(new java.awt.Dimension(800, 700));

        HomePnl.setBackground(new java.awt.Color(102, 102, 102));
        HomePnl.setPreferredSize(new java.awt.Dimension(801, 750));

        javax.swing.GroupLayout ContentLayout = new javax.swing.GroupLayout(Content);
        Content.setLayout(ContentLayout);
        ContentLayout.setHorizontalGroup(
            ContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 544, Short.MAX_VALUE)
        );
        ContentLayout.setVerticalGroup(
            ContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        Navigation.setBackground(new java.awt.Color(204, 204, 204));

        adminBtn.setBackground(new java.awt.Color(250, 250, 250));
        adminBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        adminBtn.setText("Admin Home");
        adminBtn.setFocusable(false);
        adminBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminBtnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SECURITY Svcs");
        jLabel1.setToolTipText("");

        managerBtn.setBackground(new java.awt.Color(250, 250, 250));
        managerBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        managerBtn.setText("Manager Home");
        managerBtn.setFocusable(false);
        managerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                managerBtnActionPerformed(evt);
            }
        });

        staffBtn.setBackground(new java.awt.Color(250, 250, 250));
        staffBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        staffBtn.setText("Staff Home");
        staffBtn.setFocusable(false);
        staffBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staffBtnActionPerformed(evt);
            }
        });

        clientBtn.setBackground(new java.awt.Color(250, 250, 250));
        clientBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        clientBtn.setText("Client Home");
        clientBtn.setFocusable(false);
        clientBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientBtnActionPerformed(evt);
            }
        });

        logoutBtn.setBackground(new java.awt.Color(250, 250, 250));
        logoutBtn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        logoutBtn.setText("LOGOUT");
        logoutBtn.setFocusable(false);
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NavigationLayout = new javax.swing.GroupLayout(Navigation);
        Navigation.setLayout(NavigationLayout);
        NavigationLayout.setHorizontalGroup(
            NavigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavigationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NavigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(adminBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                    .addComponent(managerBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(staffBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clientBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logoutBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        NavigationLayout.setVerticalGroup(
            NavigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavigationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(adminBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(managerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(staffBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clientBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                .addComponent(logoutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout HomePnlLayout = new javax.swing.GroupLayout(HomePnl);
        HomePnl.setLayout(HomePnlLayout);
        HomePnlLayout.setHorizontalGroup(
            HomePnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HomePnlLayout.createSequentialGroup()
                .addComponent(Navigation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        HomePnlLayout.setVerticalGroup(
            HomePnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Navigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout ContainerLayout = new javax.swing.GroupLayout(Container);
        Container.setLayout(ContainerLayout);
        ContainerLayout.setHorizontalGroup(
            ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 980, Short.MAX_VALUE)
            .addGroup(ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(HomePnl, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE))
        );
        ContainerLayout.setVerticalGroup(
            ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
            .addGroup(ContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(HomePnl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Container, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    private void adminBtnActionPerformed(java.awt.event.ActionEvent evt) {                                         
        adminHomePnl.showPnl("home");
        contentView.show(Content, "adminHomePnl");
    }                                        

    private void managerBtnActionPerformed(java.awt.event.ActionEvent evt) {                                           
        managerHomePnl.showPnl("home");
        contentView.show(Content, "managerHomePnl");
    }                                          

    private void staffBtnActionPerformed(java.awt.event.ActionEvent evt) {                                         
        staffHomePnl.showPnl("home");
        contentView.show(Content, "staffHomePnl");
    }                                        

    private void clientBtnActionPerformed(java.awt.event.ActionEvent evt) {                                          
        clientHomePnl.showPnl("home");
        contentView.show(Content, "clientHomePnl");
    }                                         

    private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {                                          
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            // Clear login fields using public method
            loginPnl.clearFields();
            // Show login panel
            frameView.show(Container, "loginPnl");
        }
        // If NO_OPTION or closed dialog, do nothing
    }                                         

    public Main main;
    public Login loginPnl = new Login();
    public Register registerPnl = new Register();
    public ForgotPassword forgotPasswordPnl = new ForgotPassword();
    public ResetPassword resetPasswordPnl = new ResetPassword();

    private AdminHome adminHomePnl = new AdminHome();
    private ManagerHome managerHomePnl = new ManagerHome();
    private StaffHome staffHomePnl = new StaffHome();
    private ClientHome clientHomePnl = new ClientHome();

    private CardLayout contentView = new CardLayout();
    private CardLayout frameView = new CardLayout();

    public void init(Main controller){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("CSSECDV - SECURITY Svcs");
        this.setLocationRelativeTo(null);
        this.setSize(1200, 800); // Increase width and height for all users

        this.main = controller;
        loginPnl.frame = this;
        registerPnl.frame = this;
        forgotPasswordPnl.frame = this;
        resetPasswordPnl.frame = this;

        // Initialize database schema and migrations
        main.sqlite.initializeDatabase();
        main.sqlite.addPasswordResetColumnsIfNotExist();

        adminHomePnl.init(main.sqlite);
        clientHomePnl.init(main.sqlite);
        managerHomePnl.init(main.sqlite);
        staffHomePnl.init(main.sqlite);

        Container.setLayout(frameView);
        Container.add(loginPnl, "loginPnl");
        Container.add(registerPnl, "registerPnl");
        Container.add(forgotPasswordPnl, "forgotPasswordPnl");
        Container.add(resetPasswordPnl, "resetPasswordPnl");
        Container.add(HomePnl, "homePnl");
        frameView.show(Container, "loginPnl");

        Content.setLayout(contentView);
        Content.add(adminHomePnl, "adminHomePnl");
        Content.add(managerHomePnl, "managerHomePnl");
        Content.add(staffHomePnl, "staffHomePnl");
        Content.add(clientHomePnl, "clientHomePnl");

        this.setVisible(true);
    }
    
    public void mainNav(){
        frameView.show(Container, "homePnl");
    }

    public void loginNav(){
        frameView.show(Container, "loginPnl");
    }

    public void registerNav(){
        frameView.show(Container, "registerPnl");
    }

    public void forgotPasswordNav(){
        forgotPasswordPnl.clearFields();
        frameView.show(Container, "forgotPasswordPnl");
    }

    public void resetPasswordNav(){
        frameView.show(Container, "resetPasswordPnl");
    }
    
    
/* =================== Start of Registration Logic =================== */
public ValidationResult registerAction(String username, String email, String password, String confirmPassword) {
        ValidationResult result = new ValidationResult();

        String usernameLower = username.toLowerCase();
        if (usernameLower.length() < 3 || usernameLower.length() > 20) {
            result.usernameError = "Username must be between 3 and 20 characters.";
            return result;
        }
        if (!usernameLower.matches("^(?=.*[a-z0-9])[a-z0-9_]+$")) {
            result.usernameError = "Invalid Input";
            return result;
        }

        if (email == null || email.isEmpty()) {
            result.emailError = "Email is required.";
            return result;
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            result.emailError = "Invalid email format.";
            return result;
        }

        SQLite db = new SQLite();
        if (db.userExists(usernameLower)) {
            result.usernameError = "Username is already taken.";
            return result;
        }
        if (db.emailExists(email)) {
            result.emailError = "Email is already registered.";
            return result;
        }

        if (password.length() < 8 || password.length() > 64) {
            result.passwordError = "Password must be between 8 and 64 characters.";
            return result;
        }
        if (!password.matches(".*[a-z].*")) {
            result.passwordError = "Password must contain at least one lowercase letter.";
            return result;
        }
        if (!password.matches(".*[A-Z].*")) {
            result.passwordError = "Password must contain at least one uppercase letter.";
            return result;
        }
        if (!password.matches(".*\\d.*")) {
            result.passwordError = "Password must contain at least one digit.";
            return result;
        }
if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
    result.passwordError = "Password must contain at least one special character.";
    return result;
}
if (!password.equals(confirmPassword)) {
    result.confirmPasswordError = "Passwords do not match.";
    return result;
}

        byte[] salt = generateSalt();
        String hashedPassword = hashPasswordSHA256(password, salt);

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();

            boolean success = db.insertUser(usernameLower, email, hashedPassword, verificationToken);

            if (success) {
                // Log the registration event
                String event = "User Registration";
                String desc = "User " + usernameLower + " registered an account.";
                String timestamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
                db.addLogs(event, usernameLower, desc, timestamp);

                // Send verification email
                try {
                    sendVerificationEmail(email, verificationToken);
                    // Show VerifyDialog to enter token manually
                    VerifyDialog verifyDialog = new VerifyDialog(this);
                    verifyDialog.setVisible(true);
                    if (verifyDialog.isVerifiedSuccessfully()) {
                        JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        result.success = true;
                    } else {
                        // Delete unverified user record
                        SQLite dbDelete = new SQLite();
                        dbDelete.removeUser(usernameLower);
                        result.success = false;
                        result.passwordError = "Email verification is required to complete registration.";
                    }
                } catch (Exception e) {
                    result.passwordError = "Failed to send verification email.";
                }
            } else {
                result.passwordError = "Failed to register user.";
            }

        return result;
    }

    private void sendVerificationEmail(String recipientEmail, String token) throws MessagingException {
        // Configure email properties
        String host = "smtp.gmail.com"; // Replace with your SMTP server
        String from = "marklegend029@gmail.com"; // Replace with your sender email
        String pass = "cupl ooiy kqmd irbk"; // Replace with your email password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
          });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO,
            InternetAddress.parse(recipientEmail));
        message.setSubject("Email Verification");
        // Adjust email content to instruct user to use the VerifyDialog
        String emailContent = "Thank you for registering.\n\n"
                + "Please verify your email by entering the following verification token in the application:\n\n"
                + token + "\n\n"
                + "Open the app and enter this token in the verification dialog to activate your account.";
        message.setText(emailContent);

        Transport.send(message);
    }

public static class ValidationResult {
        public boolean success = false;
        public String usernameError = null;
        public String emailError = null;
        public String passwordError = null;
        public String confirmPasswordError = null;
    }
    
    // Variables declaration - do not modify                     
    private javax.swing.JPanel Container;
    private javax.swing.JPanel Content;
    private javax.swing.JPanel HomePnl;
    private javax.swing.JPanel Navigation;
    private javax.swing.JButton adminBtn;
    private javax.swing.JButton clientBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JButton managerBtn;
    private javax.swing.JButton staffBtn;
    // End of variables declaration                   

    public void showAdminHome(int role) {
        adminBtn.doClick();
        mainNav();
        // Show only admin button, hide others
        adminBtn.setVisible(true);
        managerBtn.setVisible(false);
        staffBtn.setVisible(false);
        clientBtn.setVisible(false);
        // Set admin mode in adminHomePnl based on role
        if (role == 5) {
            adminHomePnl.setAdminMode(true);
        } else {
            adminHomePnl.setAdminMode(false);
        }
    }

    public void showManagerHome(int role) {
        managerBtn.doClick();
        mainNav();
        // Show only manager button, hide others
        adminBtn.setVisible(false);
        managerBtn.setVisible(true);
        staffBtn.setVisible(false);
        clientBtn.setVisible(false);
        // Set manager mode in managerHomePnl based on role
        if (role == 4) {
            managerHomePnl.setManagerMode(true);
        } else {
            managerHomePnl.setManagerMode(false);
        }
    }

    public void showStaffHome(int role) {
        staffBtn.doClick();
        mainNav();
        // Show only staff button, hide others
        adminBtn.setVisible(false);
        managerBtn.setVisible(false);
        staffBtn.setVisible(true);
        clientBtn.setVisible(false);
        // Set staff mode in staffHomePnl based on role
        if (role == 3) {
            staffHomePnl.setStaffMode(true);
        } else {
            staffHomePnl.setStaffMode(false);
        }
    }

    public void showClientHome(int role, String username) {
        clientBtn.doClick();
        mainNav();
        // Show only client button, hide others
        adminBtn.setVisible(false);
        managerBtn.setVisible(false);
        staffBtn.setVisible(false);
        clientBtn.setVisible(true);
        // Set client mode in clientHomePnl based on role
        if (role == 2) {
            clientHomePnl.setClientMode(true);
            clientHomePnl.setClientUsername(username);
        } else {
            clientHomePnl.setClientMode(false);
            clientHomePnl.setClientUsername(null);
        }
    }
}