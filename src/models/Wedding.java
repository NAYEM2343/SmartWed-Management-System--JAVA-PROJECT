package models;

public class Wedding {
    private String weddingId, brideName, groomName, weddingDate, venue, status;
    private double managementFee;

    public static final String STATUS_UPCOMING = "Upcoming";
    public static final String STATUS_ONGOING = "Ongoing";
    public static final String STATUS_COMPLETED = "Completed";

    private static int nextNumber = 1;

    public Wedding() {
        this("", "", "", "", STATUS_UPCOMING, 0.0);
    }

    public Wedding(String brideName, String groomName, String weddingDate, String venue, String status, double managementFee) {
        this.weddingId = String.format("W-%03d", nextNumber++);
        this.brideName = brideName;
        this.groomName = groomName;
        this.weddingDate = weddingDate;
        this.venue = venue;
        this.status = status;
        this.managementFee = managementFee;
    }

    public String getWeddingId() { return weddingId; }
    public String getBrideName() { return brideName; }
    public void setBrideName(String brideName) { this.brideName = brideName; }
    public String getGroomName() { return groomName; }
    public void setGroomName(String groomName) { this.groomName = groomName; }
    public String getWeddingDate() { return weddingDate; }
    public void setWeddingDate(String weddingDate) { this.weddingDate = weddingDate; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getManagementFee() { return managementFee; }
    public void setManagementFee(double managementFee) { this.managementFee = managementFee; }

    public boolean isCompleted() {
        return status.equals(STATUS_COMPLETED);
    }

    public String getDisplayLabel() {
        return weddingId + " - " + brideName + " & " + groomName;
    }

    public static boolean isValidDate(String date) {
        if (date == null) return false;
        String[] parts = date.trim().split("-");
        if (parts.length != 3) return false;

        try {
            int d = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            return d >= 1 && d <= 31 && m >= 1 && m <= 12 && y >= 2000 && y <= 2100;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return getDisplayLabel() + " (" + status + ")";
    }
}