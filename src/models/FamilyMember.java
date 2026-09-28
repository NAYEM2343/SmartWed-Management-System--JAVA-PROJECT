package models;

import core.WeddingManager;

public class FamilyMember extends User {
    private String weddingId;
    private String relation;

    public FamilyMember() {
        super();
        this.weddingId = "";
        this.relation = "";
    }

    public FamilyMember(String id, String name, String username, String password, String weddingId, String relation) {
        super(id, name, username, password);
        this.weddingId = weddingId;
        this.relation = relation;
    }

    public String getWeddingId() { return weddingId; }
    public void setWeddingId(String weddingId) { this.weddingId = weddingId; }
    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    @Override public String getRole() { return "Family"; }
    @Override public String getRoleDescription() { return "Family: View-only access to wedding statistics and gifted money."; }

    @Override
    public String getDashboardSummary(WeddingManager manager) {
        return relation + " | Guests Arrived: " + manager.getArrivedGuests(weddingId) +
                " / " + manager.getTotalGuests(weddingId) +
                " | Gifted Money: " + WeddingManager.formatMoney(manager.getTotalGiftedMoney(weddingId));
    }
}