package Model;

public class User {
    private int id;
    private String username;
    private String password;
    private int role = 2;
    private int locked = 0;
    private int failedAttempts = 0;
    private long lockoutTime = 0L;
    private int lockoutMultiplier = 0;
    private String verificationToken;
    private boolean verified = false;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public User(int id, String username, String password, int role, int locked, int failedAttempts, long lockoutTime, int lockoutMultiplier, String verificationToken, boolean verified){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.locked = locked;
        this.failedAttempts = failedAttempts;
        this.lockoutTime = lockoutTime;
        this.lockoutMultiplier = lockoutMultiplier;
        this.verificationToken = verificationToken;
        this.verified = verified;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public long getLockoutTime() {
        return lockoutTime;
    }

    public void setLockoutTime(long lockoutTime) {
        this.lockoutTime = lockoutTime;
    }

    public int getLockoutMultiplier() {
        return lockoutMultiplier;
    }

    public void setLockoutMultiplier(int lockoutMultiplier) {
        this.lockoutMultiplier = lockoutMultiplier;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
