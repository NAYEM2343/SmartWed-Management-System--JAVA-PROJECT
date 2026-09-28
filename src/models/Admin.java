package models;

import core.WeddingManager;

public class Admin extends User {
    private String designation;

    public Admin() {
        super();
        this.designation = "System Administrator";
    }

    public Admin(String id, String name, String username, String password, String designation) {
        super(id, name, username, password);
        this.designation = designation;
    }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    @Override public String getRole() { return "Admin"; }
    @Override public String getRoleDescription() { return "Admin: Full Access"; }

    @Override
    public String getDashboardSummary(WeddingManager manager) {
        return "Weddings: " + manager.getTotalWeddings() +
                " (Completed " + manager.getCompletedWeddings() +
                ", Upcoming " + manager.getUpcomingWeddings() +
                ") | SmartWed Revenue: " + WeddingManager.formatMoney(manager.getTotalRevenue());
    }
}
