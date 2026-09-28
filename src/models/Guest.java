package models;

public class Guest {
    private String guestId, weddingId, name, phone, side, rsvpStatus, arrivalStatus;
    private int accompanyingPeople;

    public static final String SIDE_BRIDE = "Bride";
    public static final String SIDE_GROOM = "Groom";
    public static final String RSVP_CONFIRMED = "Confirmed";
    public static final String RSVP_NOT_CONFIRMED = "Not Confirmed";
    public static final String RSVP_NOT_ATTENDING = "Not Attending";
    public static final String ARRIVAL_ARRIVED = "Arrived";
    public static final String ARRIVAL_NOT_ARRIVED = "Not Arrived";

    private static int nextNumber = 1;

    public Guest() {
        this("", "", "", SIDE_BRIDE, RSVP_NOT_CONFIRMED, 0, ARRIVAL_NOT_ARRIVED);
    }

    public Guest(String weddingId, String name, String phone, String side, String rsvpStatus, int accompanyingPeople, String arrivalStatus) {
        this.guestId = String.format("G-%03d", nextNumber++);
        this.weddingId = weddingId;
        this.name = name;
        this.phone = phone;
        this.side = side;
        this.rsvpStatus = rsvpStatus;
        this.accompanyingPeople = accompanyingPeople;
        this.arrivalStatus = arrivalStatus;
    }

    public String getGuestId() { return guestId; }
    public String getWeddingId() { return weddingId; }
    public void setWeddingId(String weddingId) { this.weddingId = weddingId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }
    public String getRsvpStatus() { return rsvpStatus; }
    public void setRsvpStatus(String rsvpStatus) { this.rsvpStatus = rsvpStatus; }
    public int getAccompanyingPeople() { return accompanyingPeople; }
    public void setAccompanyingPeople(int accompanyingPeople) { this.accompanyingPeople = accompanyingPeople; }
    public String getArrivalStatus() { return arrivalStatus; }
    public void setArrivalStatus(String arrivalStatus) { this.arrivalStatus = arrivalStatus; }

    public boolean hasArrived() {
        return arrivalStatus.equals(ARRIVAL_ARRIVED);
    }

    public int getHeadCount() {
        return 1 + accompanyingPeople;
    }

    public String getDisplayLabel() {
        return guestId + " - " + name;
    }

    @Override
    public String toString() {
        return getDisplayLabel() + " (" + side + ", " + arrivalStatus + ")";
    }
}