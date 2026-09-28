package models;

import core.WeddingManager;

public abstract class User {
    public static final String APPLICATION_NAME = "SmartWed";
    protected String id;
    protected String name;
    protected String username;
    private String password;
    private static int userCount = 0;

    public User() {
        this("", "", "", "");
    }

    public User(String id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        userCount++;
    }

    public abstract String getRole();
    public abstract String getRoleDescription();
    public abstract String getDashboardSummary(WeddingManager manager);

    protected String getBaseCredentialsSummary() {
        return "models.User ID: " + this.id + " | System: " + APPLICATION_NAME;
    }

    public boolean checkPassword(String attempt) {
        return this.password.equals(attempt);
    }

    public void setPassword(String password) { this.password = password; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public static int getUserCount() { return userCount; }

    @Override
    public String toString() {
        return getRole() + " [" + id + "] " + name;
    }
}