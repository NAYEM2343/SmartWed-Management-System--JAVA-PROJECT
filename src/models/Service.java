package models;

public class Service {
    private String serviceId, weddingId, serviceName, provider, status;
    private double estimatedCost, actualCost;
    private boolean required;

    public static final String[] STANDARD_SERVICES = {
            "Venue", "Catering", "Decoration", "Photography", "Transportation", "Music", "Security", "Drone Photography"
    };
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_COMPLETED = "Completed";

    private static int nextNumber = 1;

    public Service() {
        this("", "", "", 0.0, 0.0, false, STATUS_PENDING);
    }

    public Service(String weddingId, String serviceName, String provider, double estimatedCost, double actualCost, boolean required, String status) {
        this.serviceId = String.format("S-%03d", nextNumber++);
        this.weddingId = weddingId;
        this.serviceName = serviceName;
        this.provider = provider;
        this.estimatedCost = estimatedCost;
        this.actualCost = actualCost;
        this.required = required;
        this.status = status;
    }

    public String getServiceId() { return serviceId; }
    public String getWeddingId() { return weddingId; }
    public void setWeddingId(String weddingId) { this.weddingId = weddingId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(double estimatedCost) { this.estimatedCost = estimatedCost; }
    public double getActualCost() { return actualCost; }
    public void setActualCost(double actualCost) { this.actualCost = actualCost; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCostDifference() {
        return actualCost - estimatedCost;
    }

    public boolean isOverBudget() {
        return actualCost > estimatedCost;
    }

    @Override
    public String toString() {
        return serviceId + " " + serviceName + " (" + status + ")";
    }
}