package Controller;

import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLite {

    private static Connection connect() {
    String url = "jdbc:sqlite:database.db";
    try {
        return DriverManager.getConnection(url);
    } catch (SQLException e) {
        System.out.println("Connection failed: " + e.getMessage());
        return null;
    }
}

    
    public int DEBUG_MODE = 0;
    String driverURL = "jdbc:sqlite:" + "database.db";
    
    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    public void addLockoutColumnsIfNotExist() {
        String sql1 = "ALTER TABLE users ADD COLUMN failed_attempts INTEGER DEFAULT 0";
        String sql2 = "ALTER TABLE users ADD COLUMN lockout_time INTEGER";
        String sql3 = "ALTER TABLE users ADD COLUMN lockout_multiplier INTEGER DEFAULT 0";
        try (Connection conn = DriverManager.getConnection(driverURL);
             Statement stmt = conn.createStatement()) {
            try {
                stmt.execute(sql1);
                System.out.println("Column failed_attempts added to users table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println("Error adding failed_attempts column: " + e.getMessage());
                }
            }
            try {
                stmt.execute(sql2);
                System.out.println("Column lockout_time added to users table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println("Error adding lockout_time column: " + e.getMessage());
                }
            }
            try {
                stmt.execute(sql3);
                System.out.println("Column lockout_multiplier added to users table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println("Error adding lockout_multiplier column: " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    // New method to add password reset token and expiration columns if not exist
    public void addPasswordResetColumnsIfNotExist() {
        String sql1 = "ALTER TABLE users ADD COLUMN reset_token TEXT";
        String sql2 = "ALTER TABLE users ADD COLUMN reset_token_expiry INTEGER";
        try (Connection conn = DriverManager.getConnection(driverURL);
             Statement stmt = conn.createStatement()) {
            try {
                stmt.execute(sql1);
                System.out.println("Column reset_token added to users table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println("Error adding reset_token column: " + e.getMessage());
                }
            }
            try {
                stmt.execute(sql2);
                System.out.println("Column reset_token_expiry added to users table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println("Error adding reset_token_expiry column: " + e.getMessage());
                }
            }
            // Add forgot password lockout columns
            String sql3 = "ALTER TABLE users ADD COLUMN forgot_failed_attempts INTEGER DEFAULT 0";
            String sql4 = "ALTER TABLE users ADD COLUMN forgot_lockout_time INTEGER";
            String sql5 = "ALTER TABLE users ADD COLUMN forgot_lockout_multiplier INTEGER DEFAULT 0";
            try {
                stmt.execute(sql3);
                System.out.println("Column forgot_failed_attempts added to users table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println("Error adding forgot_failed_attempts column: " + e.getMessage());
                }
            }
            try {
                stmt.execute(sql4);
                System.out.println("Column forgot_lockout_time added to users table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println("Error adding forgot_lockout_time column: " + e.getMessage());
                }
            }
            try {
                stmt.execute(sql5);
                System.out.println("Column forgot_lockout_multiplier added to users table.");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    System.out.println("Error adding forgot_lockout_multiplier column: " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }

    // Method to set password reset token and expiry for a user
    public void setPasswordResetToken(String email, String token, long expiry) {
        String sql = "UPDATE users SET reset_token = ?, reset_token_expiry = ? WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            pstmt.setLong(2, expiry);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error setting password reset token: " + e.getMessage());
        }
    }

    // Method to get user by reset token
public User getUserByResetToken(String token) {
    String sql = "SELECT id, username, password, role, locked, failed_attempts, lockout_time, lockout_multiplier, verification_token, verified, reset_token, reset_token_expiry FROM users WHERE reset_token = ?";
    try (Connection conn = DriverManager.getConnection(driverURL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, token);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            long expiry = rs.getLong("reset_token_expiry");
            long now = System.currentTimeMillis();
            if (expiry < now) {
                return null; // token expired
            }
            User user = new User(rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getInt("role"),
                            rs.getInt("locked"),
                            rs.getInt("failed_attempts"),
                            rs.getLong("lockout_time"),
                            rs.getInt("lockout_multiplier"),
                            rs.getString("verification_token"),
                            rs.getInt("verified") == 1);
            return user;
        }
    } catch (Exception e) {
        System.out.println("Error fetching user by reset token: " + e.getMessage());
    }
    return null;
}

    // Method to clear reset token after password reset
    public void clearResetToken(int userId) {
        String sql = "UPDATE users SET reset_token = NULL, reset_token_expiry = NULL WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error clearing reset token: " + e.getMessage());
        }
    }

    // Method to update user's password by user ID
    public void updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating password: " + e.getMessage());
        }
    }

    public boolean emailColumnExists() {
        String sql = "PRAGMA table_info(users)";
        try (Connection conn = DriverManager.getConnection(driverURL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("email".equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error checking email column: " + ex.getMessage());
        }
        return false;
    }

    public void addEmailColumnIfNotExist() {
        if (!emailColumnExists()) {
            // SQLite does not support adding UNIQUE constraint via ALTER TABLE
            // Workaround: create new table with email column and UNIQUE constraint, copy data, drop old table, rename new table
            String createNewTableSql = "CREATE TABLE users_new (\n"
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + " username TEXT NOT NULL UNIQUE,\n"
                    + " email TEXT UNIQUE,\n"
                    + " password TEXT NOT NULL,\n"
                    + " role INTEGER DEFAULT 2,\n"
                    + " locked INTEGER DEFAULT 0,\n"
                    + " verification_token TEXT,\n"
                    + " verified INTEGER DEFAULT 0\n"
                    + ");";
            String copyDataSql = "";
            try (Connection conn = DriverManager.getConnection(driverURL);
                 Statement stmt = conn.createStatement()) {
                // Get existing columns in old users table
                ArrayList<String> existingColumns = new ArrayList<>();
                String pragmaSql = "PRAGMA table_info(users);";
                try (ResultSet rs = stmt.executeQuery(pragmaSql)) {
                    while (rs.next()) {
                        existingColumns.add(rs.getString("name"));
                    }
                }
                // Build column lists for insert and select
                ArrayList<String> insertColumns = new ArrayList<>();
                ArrayList<String> selectColumns = new ArrayList<>();
                insertColumns.add("id");
                selectColumns.add("id");
                insertColumns.add("username");
                selectColumns.add("username");
                insertColumns.add("email");
                if (existingColumns.contains("email")) {
                    selectColumns.add("email");
                } else {
                    selectColumns.add("NULL AS email");
                }
                insertColumns.add("password");
                selectColumns.add("password");
                insertColumns.add("role");
                if (existingColumns.contains("role")) {
                    selectColumns.add("role");
                } else {
                    selectColumns.add("2 AS role");
                }
                insertColumns.add("locked");
                if (existingColumns.contains("locked")) {
                    selectColumns.add("locked");
                } else {
                    selectColumns.add("0 AS locked");
                }
                insertColumns.add("verification_token");
                if (existingColumns.contains("verification_token")) {
                    selectColumns.add("verification_token");
                } else {
                    selectColumns.add("NULL AS verification_token");
                }
                insertColumns.add("verified");
                if (existingColumns.contains("verified")) {
                    selectColumns.add("verified");
                } else {
                    selectColumns.add("0 AS verified");
                }
                copyDataSql = "INSERT INTO users_new (" + String.join(", ", insertColumns) + ") SELECT " + String.join(", ", selectColumns) + " FROM users;";
                conn.setAutoCommit(false);
                stmt.execute(createNewTableSql);
                stmt.execute(copyDataSql);
                stmt.execute("DROP TABLE users;");
                stmt.execute("ALTER TABLE users_new RENAME TO users;");
                conn.commit();
                System.out.println("Rebuilt users table with email column.");
            } catch (Exception ex) {
                System.out.println("Error rebuilding users table with email column: " + ex.getMessage());
            }
        }
    }

    public void initializeDatabase() {
        createUserTable();
        addEmailColumnIfNotExist();
        addLockoutColumnsIfNotExist();
        createHistoryTable();
        createLogsTable();
        createProductTable();
    }
    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking user: " + e.getMessage());
        }
        return false;
    }
    
    public void createHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS history (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL,\n"
            + " name TEXT NOT NULL,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void createLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS logs (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " event TEXT NOT NULL,\n"
            + " username TEXT NOT NULL,\n"
            + " desc TEXT NOT NULL,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
     
    public void createProductTable() {
        String sql = "CREATE TABLE IF NOT EXISTS product (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " name TEXT NOT NULL UNIQUE,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " price REAL DEFAULT 0.00\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
     
    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL UNIQUE,\n"
            + " email TEXT NOT NULL UNIQUE,\n"
            + " password TEXT NOT NULL,\n"
            + " role INTEGER DEFAULT 2,\n"
            + " locked INTEGER DEFAULT 0,\n"
            + " verification_token TEXT,\n"
            + " verified INTEGER DEFAULT 0\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db created.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropHistoryTable() {
        String sql = "DROP TABLE IF EXISTS history;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropLogsTable() {
        String sql = "DROP TABLE IF EXISTS logs;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropProductTable() {
        String sql = "DROP TABLE IF EXISTS product;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void dropUserTable() {
        String sql = "DROP TABLE IF EXISTS users;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db dropped.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void addHistory(String username, String name, int stock, String timestamp) {
        String sql = "INSERT INTO history(username,name,stock,timestamp) VALUES('" + username + "','" + name + "','" + stock + "','" + timestamp + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void addLogs(String event, String username, String desc, String timestamp) {
        if (DEBUG_MODE == 1) {
            desc = "[DEBUG] " + desc;
        }
        String sql = "INSERT INTO logs(event,username,desc,timestamp) VALUES('" + event + "','" + username + "','" + desc + "','" + timestamp + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public boolean addProduct(String name, int stock, double price) {
        String sql = "INSERT INTO product(name,stock,price) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, price);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception ex) {
            System.out.print(ex);
            return false;
        }
    }

    public boolean updateProduct(String originalName, String newName, int stock, double price) {
        String sql = "UPDATE product SET name = ?, stock = ?, price = ? WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, stock);
            pstmt.setDouble(3, price);
            pstmt.setString(4, originalName);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception ex) {
            System.out.print(ex);
            return false;
        }
    }

    public boolean deleteProduct(String name) {
        String sql = "DELETE FROM product WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception ex) {
            System.out.print(ex);
            return false;
        }
    }
    
    public void addUser(String username, String password) {
        String sql = "INSERT INTO users(username,password) VALUES('" + username + "','" + password + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
            
//      PREPARED STATEMENT EXAMPLE
//      String sql = "INSERT INTO users(username,password) VALUES(?,?)";
//      PreparedStatement pstmt = conn.prepareStatement(sql)) {
//      pstmt.setString(1, username);
//      pstmt.setString(2, password);
//      pstmt.executeUpdate();
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public static boolean registerUser(String username, String hashedPassword, String role) {
        if (new SQLite().userExists(username)) {
            System.out.println("User already exists.");
            return false;
        }
    
        String query = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Registration Error: " + e.getMessage());
            return false;
        }
}

    public ArrayList<History> getHistory(){
        String sql = "SELECT id, username, name, stock, timestamp FROM history";
        ArrayList<History> histories = new ArrayList<History>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                histories.add(new History(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return histories;
    }
    
    public ArrayList<Logs> getLogs(){
        String sql = "SELECT id, event, username, desc, timestamp FROM logs";
        ArrayList<Logs> logs = new ArrayList<Logs>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                logs.add(new Logs(rs.getInt("id"),
                                   rs.getString("event"),
                                   rs.getString("username"),
                                   rs.getString("desc"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return logs;
    }
    
    public ArrayList<Product> getProduct(){
        String sql = "SELECT id, name, stock, price FROM product";
        ArrayList<Product> products = new ArrayList<Product>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                products.add(new Product(rs.getInt("id"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getFloat("price")));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return products;
    }
    
    public ArrayList<User> getUsers(){
        String sql = "SELECT id, username, password, role, locked, failed_attempts, lockout_time, lockout_multiplier, verification_token, verified FROM users";
        ArrayList<User> users = new ArrayList<User>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                users.add(new User(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("password"),
                                   rs.getInt("role"),
                                   rs.getInt("locked"),
                                   rs.getInt("failed_attempts"),
                                   rs.getLong("lockout_time"),
                                   rs.getInt("lockout_multiplier"),
                                   rs.getString("verification_token"),
                                   rs.getInt("verified") == 1));
            }
        } catch (Exception ex) {}
        return users;
    }
    
    public void addUser(String username, String password, int role) {
        String sql = "INSERT INTO users(username,password,role) VALUES('" + username + "','" + password + "','" + role + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
            
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username='" + username + "';";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User " + username + " has been deleted.");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
    
    public Product getProduct(String name){
        String sql = "SELECT name, stock, price FROM product WHERE name='" + name + "';";
        Product product = null;
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            if (rs.next()) {
                product = new Product(rs.getString("name"),
                                       rs.getInt("stock"),
                                       rs.getFloat("price"));
            }
        } catch (Exception ex) {
            System.out.print(ex);
        }
        return product;
    }

    public boolean insertUser(String username, String email, String hashedPassword, String verificationToken) {
        if (userExists(username)) {
            System.out.println("User already exists.");
            return false;
        }
        if (emailExists(email)) {
            System.out.println("Email already registered.");
            return false;
        }
        String sql = "INSERT INTO users(username, email, password, verification_token, verified) VALUES(?, ?, ?, ?, 0)";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, verificationToken);
            pstmt.executeUpdate();
            return true;
        } catch (Exception ex) {
            System.out.println("Error inserting user: " + ex.getMessage());
            return false;
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("Error checking email: " + e.getMessage());
        }
        return false;
    }

    public void updateFailedAttempts(String username, int failedAttempts) {
        String sql = "UPDATE users SET failed_attempts = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, failedAttempts);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating failed attempts: " + e.getMessage());
        }
    }

    public void updateLockoutTime(String username, long lockoutTime) {
        String sql = "UPDATE users SET lockout_time = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lockoutTime);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating lockout time: " + e.getMessage());
        }
    }

    public void updateLockoutMultiplier(String username, int lockoutMultiplier) {
        String sql = "UPDATE users SET lockout_multiplier = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lockoutMultiplier);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating lockout multiplier: " + e.getMessage());
        }
    }

    // Methods for forgot password lockout tracking by email
    public void updateForgotFailedAttempts(String email, int failedAttempts) {
        String sql = "UPDATE users SET forgot_failed_attempts = ? WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, failedAttempts);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating forgot_failed_attempts: " + e.getMessage());
        }
    }

    public void updateForgotLockoutTime(String email, long lockoutTime) {
        String sql = "UPDATE users SET forgot_lockout_time = ? WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, lockoutTime);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating forgot_lockout_time: " + e.getMessage());
        }
    }

    public void updateForgotLockoutMultiplier(String email, int lockoutMultiplier) {
        String sql = "UPDATE users SET forgot_lockout_multiplier = ? WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lockoutMultiplier);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error updating forgot_lockout_multiplier: " + e.getMessage());
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT id, username, password, role, locked, failed_attempts, lockout_time, lockout_multiplier, verification_token, verified, forgot_failed_attempts, forgot_lockout_time, forgot_lockout_multiplier FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"),
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getInt("role"),
                                    rs.getInt("locked"),
                                    rs.getInt("failed_attempts"),
                                    rs.getLong("lockout_time"),
                                    rs.getInt("lockout_multiplier"),
                                    rs.getString("verification_token"),
                                    rs.getInt("verified") == 1,
                                    rs.getInt("forgot_failed_attempts"),
                                    rs.getLong("forgot_lockout_time"),
                                    rs.getInt("forgot_lockout_multiplier"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching user by email: " + e.getMessage());
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT id, username, password, role, locked, failed_attempts, lockout_time, lockout_multiplier, verification_token, verified FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"),
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getInt("role"),
                                    rs.getInt("locked"),
                                    rs.getInt("failed_attempts"),
                                    rs.getLong("lockout_time"),
                                    rs.getInt("lockout_multiplier"),
                                    rs.getString("verification_token"),
                                    rs.getInt("verified") == 1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user by username: " + e.getMessage());
        }
        return null;
    }

    public boolean verifyUserByToken(String token) {
        String selectSql = "SELECT id FROM users WHERE verification_token = ?";
        String updateSql = "UPDATE users SET verified = 1, verification_token = NULL WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, token);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, userId);
                        int rowsUpdated = updateStmt.executeUpdate();
                        return rowsUpdated > 0;
                    }
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error verifying user by token: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserRole(String username, int newRole) {
        String sql = "UPDATE users SET role = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newRole);
            pstmt.setString(2, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception ex) {
            System.out.println("Error updating user role: " + ex.getMessage());
            return false;
        }
    }

    public boolean updateUserLocked(String username, int locked) {
        String sql = "UPDATE users SET locked = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, locked);
            pstmt.setString(2, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception ex) {
            System.out.println("Error updating user locked state: " + ex.getMessage());
            return false;
        }
    }

    public boolean updatePasswordByUsername(String username, String hashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(driverURL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception ex) {
            System.out.println("Error updating password: " + ex.getMessage());
            return false;
        }
    }

    public void clearLogs() {
        String sql = "DELETE FROM logs";
        try (Connection conn = DriverManager.getConnection(driverURL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception ex) {
            System.out.println("Error clearing logs: " + ex.getMessage());
        }
    }
}