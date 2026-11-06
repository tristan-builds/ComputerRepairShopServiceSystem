
package computerrepairshopservicesystem;

public class SystemUser {

    public static final String ADMIN = "ADMIN";
    public static final String STAFF = "STAFF";

    private String userID;
    private String username;
    private String passwordHash;
    private String role;
    private int failedLoginAttempts;
    private boolean isBlocked;

    public SystemUser() {
    }

    public SystemUser(String userID, String username,
                      String passwordHash, String role,
                      int failedLoginAttempts, boolean isBlocked) {

        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        setRole(role);
        this.failedLoginAttempts = failedLoginAttempts;
        this.isBlocked = isBlocked;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (!ADMIN.equals(role) && !STAFF.equals(role)) {
            throw new IllegalArgumentException(
                "Invalid user role: " + role
            );
        }

        this.role = role;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
}
