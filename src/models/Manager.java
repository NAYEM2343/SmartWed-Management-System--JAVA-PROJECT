package models;

import core.WeddingManager;

public class Manager extends User {
    private String assignedWeddingId;

    public Manager() {
        super();
        this.assignedWeddingId = "";
    }

    public Manager(String id, String name, String username, String password, String assignedWeddingId) {
        super(id, name, username, password);
        this.assignedWeddingId = assignedWeddingId;
    }

    public String getAssignedWeddingId() { return assignedWeddingId; }
    public void setAssignedWeddingId(String assignedWeddingId) { this.assignedWeddingId = assignedWeddingId; }

    @Override public String getRole() { return "models.Manager"; }
    @Override public String getRoleDescription() { return "models.Manager: Operational Access"; }

    @Override
    public String getDashboardSummary(WeddingManager manager) {
        return "Assigned models.Wedding " + assignedWeddingId +
                " | Guests: " + manager.getTotalGuests(assignedWeddingId) +
                " (Arrived " + manager.getArrivedGuests(assignedWeddingId) +
                ") | Actual Expense: " + WeddingManager.formatMoney(manager.getTotalActualExpense(assignedWeddingId));
    }
}