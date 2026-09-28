package models;

import core.WeddingManager;

public class Gift {
    private String giftId, guestId, giftType, description;
    private double amount;
    private boolean special;

    public static final String TYPE_CASH = "Cash";
    public static final String TYPE_PHYSICAL = "Physical";

    private static int nextNumber = 1;

    public Gift() {
        this("", TYPE_CASH, 0.0, "", false);
    }

    public Gift(String guestId, String giftType, double amount, String description, boolean special) {
        this.giftId = String.format("GF-%03d", nextNumber++);
        this.guestId = guestId;
        this.giftType = giftType;
        this.amount = amount;
        this.description = description;
        this.special = special;
    }

    public static Gift createCashGift(String guestId, double amount, boolean special) {
        return new Gift(guestId, TYPE_CASH, amount, "Cash gift", special);
    }

    public static Gift createPhysicalGift(String guestId, String description, boolean special) {
        return new Gift(guestId, TYPE_PHYSICAL, 0.0, description, special);
    }

    public String getGiftId() { return giftId; }
    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }
    public String getGiftType() { return giftType; }
    public void setGiftType(String giftType) { this.giftType = giftType; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isSpecial() { return special; }
    public void setSpecial(boolean special) { this.special = special; }

    public boolean isCash() {
        return giftType.equals(TYPE_CASH);
    }

    public String getDisplayValue() {
        return isCash() ? WeddingManager.formatMoney(amount) : description;
    }

    @Override
    public String toString() {
        return giftId + " from " + guestId + ": " + getDisplayValue();
    }
}