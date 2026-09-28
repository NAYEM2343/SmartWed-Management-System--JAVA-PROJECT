package core;

import models.*;

import java.util.ArrayList;

public class WeddingManager {

    private ArrayList<Wedding> weddings = new ArrayList<>();
    private ArrayList<Guest> guests = new ArrayList<>();
    private ArrayList<Gift> gifts = new ArrayList<>();
    private ArrayList<Service> services = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();

    public static final double SERVICE_COMMISSION_RATE = 0.10;

    public WeddingManager() {
        loadSampleData();
    }

    public static String formatMoney(double amount) {
        return String.format("Tk %.2f", amount);
    }

    public User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username) && u.checkPassword(password)) {
                return u;
            }
        }
        return null;
    }

    public ArrayList<User> getAllUsers() { return users; }
    public ArrayList<Wedding> getAllWeddings() { return weddings; }
    public ArrayList<Guest> getAllGuests() { return guests; }
    public ArrayList<Gift> getAllGifts() { return gifts; }
    public ArrayList<Service> getAllServices() { return services; }

    // ==========================================
    // WEDDING CRUD
    // ==========================================

    public boolean addWedding(Wedding w) {
        if (w == null) return false;
        weddings.add(w);
        return true;
    }

    public boolean updateWedding(String id, Wedding u) {
        Wedding e = searchWedding(id);
        if (e == null || u == null) return false;

        e.setBrideName(u.getBrideName());
        e.setGroomName(u.getGroomName());
        e.setWeddingDate(u.getWeddingDate());
        e.setVenue(u.getVenue());
        e.setStatus(u.getStatus());
        e.setManagementFee(u.getManagementFee());
        return true;
    }

    public boolean deleteWedding(String id) {
        Wedding e = searchWedding(id);
        if (e == null) return false;

        for (Guest g : getGuestsByWedding(id)) {
            deleteGuest(g.getGuestId());
        }

        services.removeAll(getServicesByWedding(id));
        weddings.remove(e);
        return true;
    }

    public Wedding searchWedding(String id) {
        for (Wedding w : weddings) {
            if (w.getWeddingId().equalsIgnoreCase(id)) {
                return w;
            }
        }
        return null;
    }

    public ArrayList<Wedding> searchWeddings(String keyword) {
        ArrayList<Wedding> res = new ArrayList<>();
        String k = keyword.trim().toLowerCase();
        for (Wedding w : weddings) {
            if (w.getWeddingId().toLowerCase().contains(k) ||
                    w.getBrideName().toLowerCase().contains(k) ||
                    w.getGroomName().toLowerCase().contains(k)) {
                res.add(w);
            }
        }
        return res;
    }

    // ==========================================
    // GUEST CRUD
    // ==========================================

    public boolean addGuest(Guest g) {
        if (g == null) return false;
        guests.add(g);
        return true;
    }

    public boolean updateGuest(String id, Guest u) {
        Guest e = searchGuest(id);
        if (e == null || u == null) return false;

        e.setWeddingId(u.getWeddingId());
        e.setName(u.getName());
        e.setPhone(u.getPhone());
        e.setSide(u.getSide());
        e.setRsvpStatus(u.getRsvpStatus());
        e.setAccompanyingPeople(u.getAccompanyingPeople());
        e.setArrivalStatus(u.getArrivalStatus());
        return true;
    }

    public boolean deleteGuest(String id) {
        Guest e = searchGuest(id);
        if (e == null) return false;

        gifts.removeAll(getGiftsByGuest(id));
        guests.remove(e);
        return true;
    }

    public Guest searchGuest(String id) {
        for (Guest g : guests) {
            if (g.getGuestId().equalsIgnoreCase(id)) {
                return g;
            }
        }
        return null;
    }

    public ArrayList<Guest> searchGuests(String keyword) {
        ArrayList<Guest> res = new ArrayList<>();
        String k = keyword.trim().toLowerCase();
        for (Guest g : guests) {
            if (g.getGuestId().toLowerCase().contains(k) ||
                    g.getName().toLowerCase().contains(k) ||
                    g.getWeddingId().toLowerCase().contains(k)) {
                res.add(g);
            }
        }
        return res;
    }

    public ArrayList<Guest> getGuestsByWedding(String id) {
        ArrayList<Guest> res = new ArrayList<>();
        for (Guest g : guests) {
            if (g.getWeddingId().equalsIgnoreCase(id)) {
                res.add(g);
            }
        }
        return res;
    }

    // ==========================================
    // GIFT CRUD
    // ==========================================

    public boolean addGift(Gift g) {
        if (g == null) return false;
        gifts.add(g);
        return true;
    }

    public boolean updateGift(String id, Gift u) {
        Gift e = searchGift(id);
        if (e == null || u == null) return false;

        e.setGuestId(u.getGuestId());
        e.setGiftType(u.getGiftType());
        e.setAmount(u.getAmount());
        e.setDescription(u.getDescription());
        e.setSpecial(u.isSpecial());
        return true;
    }

    public boolean deleteGift(String id) {
        Gift e = searchGift(id);
        if (e == null) return false;

        gifts.remove(e);
        return true;
    }

    public Gift searchGift(String id) {
        for (Gift g : gifts) {
            if (g.getGiftId().equalsIgnoreCase(id)) {
                return g;
            }
        }
        return null;
    }

    public ArrayList<Gift> searchGifts(String keyword) {
        ArrayList<Gift> res = new ArrayList<>();
        String k = keyword.trim().toLowerCase();
        for (Gift g : gifts) {
            if (g.getGiftId().toLowerCase().contains(k) || g.getGuestId().toLowerCase().contains(k)) {
                res.add(g);
            }
        }
        return res;
    }

    public ArrayList<Gift> getGiftsByGuest(String id) {
        ArrayList<Gift> res = new ArrayList<>();
        for (Gift g : gifts) {
            if (g.getGuestId().equalsIgnoreCase(id)) {
                res.add(g);
            }
        }
        return res;
    }

    public ArrayList<Gift> getGiftsByWedding(String id) {
        ArrayList<Gift> res = new ArrayList<>();
        for (Guest g : getGuestsByWedding(id)) {
            res.addAll(getGiftsByGuest(g.getGuestId()));
        }
        return res;
    }

    public ArrayList<Gift> getSpecialGifts() {
        ArrayList<Gift> res = new ArrayList<>();
        for (Gift g : gifts) {
            if (g.isSpecial()) {
                res.add(g);
            }
        }
        return res;
    }

    public ArrayList<Gift> getSpecialGifts(String id) {
        ArrayList<Gift> res = new ArrayList<>();
        for (Gift g : getGiftsByWedding(id)) {
            if (g.isSpecial()) {
                res.add(g);
            }
        }
        return res;
    }

    // ==========================================
    // SERVICE CRUD
    // ==========================================

    public boolean addService(Service s) {
        if (s == null) return false;
        services.add(s);
        return true;
    }

    public boolean updateService(String id, Service u) {
        Service e = searchService(id);
        if (e == null || u == null) return false;

        e.setWeddingId(u.getWeddingId());
        e.setServiceName(u.getServiceName());
        e.setProvider(u.getProvider());
        e.setEstimatedCost(u.getEstimatedCost());
        e.setActualCost(u.getActualCost());
        e.setRequired(u.isRequired());
        e.setStatus(u.getStatus());
        return true;
    }

    public boolean deleteService(String id) {
        Service e = searchService(id);
        if (e == null) return false;

        services.remove(e);
        return true;
    }

    public Service searchService(String id) {
        for (Service s : services) {
            if (s.getServiceId().equalsIgnoreCase(id)) {
                return s;
            }
        }
        return null;
    }

    public ArrayList<Service> searchServices(String keyword) {
        ArrayList<Service> res = new ArrayList<>();
        String k = keyword.trim().toLowerCase();
        for (Service s : services) {
            if (s.getServiceId().toLowerCase().contains(k) ||
                    s.getServiceName().toLowerCase().contains(k) ||
                    s.getProvider().toLowerCase().contains(k) ||
                    s.getWeddingId().toLowerCase().contains(k)) {
                res.add(s);
            }
        }
        return res;
    }

    public ArrayList<Service> getServicesByWedding(String id) {
        ArrayList<Service> res = new ArrayList<>();
        for (Service s : services) {
            if (s.getWeddingId().equalsIgnoreCase(id)) {
                res.add(s);
            }
        }
        return res;
    }

    // ==========================================
    // CALCULATIONS & OVERLOADING
    // ==========================================

    public int getTotalGuests() { return guests.size(); }
    public int getTotalGuests(String id) { return getGuestsByWedding(id).size(); }

    public int getArrivedGuests() {
        int count = 0;
        for (Guest g : guests) if (g.hasArrived()) count++;
        return count;
    }

    public int getArrivedGuests(String id) {
        int count = 0;
        for (Guest g : getGuestsByWedding(id)) if (g.hasArrived()) count++;
        return count;
    }

    public int getNotArrivedGuests() { return getTotalGuests() - getArrivedGuests(); }
    public int getNotArrivedGuests(String id) { return getTotalGuests(id) - getArrivedGuests(id); }

    public int getTotalHeadCount(String id) {
        int total = 0;
        for (Guest g : getGuestsByWedding(id)) total += g.getHeadCount();
        return total;
    }

    public int getTotalGifts() { return gifts.size(); }
    public int getTotalGifts(String id) { return getGiftsByWedding(id).size(); }

    public int getCashGiftCount() {
        int count = 0;
        for (Gift g : gifts) if (g.isCash()) count++;
        return count;
    }

    public int getCashGiftCount(String id) {
        int count = 0;
        for (Gift g : getGiftsByWedding(id)) if (g.isCash()) count++;
        return count;
    }

    public int getPhysicalGiftCount() { return getTotalGifts() - getCashGiftCount(); }
    public int getPhysicalGiftCount(String id) { return getTotalGifts(id) - getCashGiftCount(id); }

    public double getTotalGiftedMoney() {
        double total = 0;
        for (Gift g : gifts) if (g.isCash()) total += g.getAmount();
        return total;
    }

    public double getTotalGiftedMoney(String id) {
        double total = 0;
        for (Gift g : getGiftsByWedding(id)) if (g.isCash()) total += g.getAmount();
        return total;
    }

    public int getSpecialGiftCount() { return getSpecialGifts().size(); }
    public int getSpecialGiftCount(String id) { return getSpecialGifts(id).size(); }

    public double getTotalEstimatedExpense() {
        double total = 0;
        for (Service s : services) total += s.getEstimatedCost();
        return total;
    }

    public double getTotalEstimatedExpense(String id) {
        double total = 0;
        for (Service s : getServicesByWedding(id)) total += s.getEstimatedCost();
        return total;
    }

    public double getTotalActualExpense() {
        double total = 0;
        for (Service s : services) total += s.getActualCost();
        return total;
    }

    public double getTotalActualExpense(String id) {
        double total = 0;
        for (Service s : getServicesByWedding(id)) total += s.getActualCost();
        return total;
    }

    public double getCommissionForWedding(String id) { return getTotalActualExpense(id) * SERVICE_COMMISSION_RATE; }

    public double getRevenueForWedding(String id) {
        Wedding w = searchWedding(id);
        if (w == null) return 0;
        return w.getManagementFee() + getCommissionForWedding(id);
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Wedding w : weddings) total += getRevenueForWedding(w.getWeddingId());
        return total;
    }

    public int getTotalWeddings() { return weddings.size(); }

    public int getCompletedWeddings() {
        int count = 0;
        for (Wedding w : weddings) if (w.isCompleted()) count++;
        return count;
    }

    public int getUpcomingWeddings() {
        int count = 0;
        for (Wedding w : weddings) if (w.getStatus().equals(Wedding.STATUS_UPCOMING)) count++;
        return count;
    }

    // ==========================================
    // 15 DEMO WEDDINGS (5 WITH FULL DETAILS)
    // ==========================================

    private void loadSampleData() {
        // 1. Create 15 Weddings
        Wedding w1 = new Wedding("Ayesha", "Rahim", "15-12-2026", "Community Convention Hall", Wedding.STATUS_UPCOMING, 25000.0);
        Wedding w2 = new Wedding("Nabila", "Kamal", "05-03-2026", "Lake View Garden", Wedding.STATUS_COMPLETED, 20000.0);
        Wedding w3 = new Wedding("Tanjila", "Hasan", "20-01-2027", "Royal Palace Hall", Wedding.STATUS_UPCOMING, 30000.0);
        Wedding w4 = new Wedding("Sadia", "Tariq", "14-02-2026", "Sea Breeze Resort", Wedding.STATUS_COMPLETED, 45000.0);
        Wedding w5 = new Wedding("Nusrat", "Farhan", "10-11-2026", "Grand Hotel Savar", Wedding.STATUS_ONGOING, 35000.0);

        Wedding w6 = new Wedding("Farah", "Salman", "25-12-2026", "Blue Moon Center", Wedding.STATUS_UPCOMING, 15000.0);
        Wedding w7 = new Wedding("Riya", "Jamil", "12-08-2026", "Greenwood Club", Wedding.STATUS_COMPLETED, 22000.0);
        Wedding w8 = new Wedding("Maliha", "Zeeshan", "01-01-2027", "Skyline Tower", Wedding.STATUS_UPCOMING, 50000.0);
        Wedding w9 = new Wedding("Samira", "Kawsar", "18-09-2026", "Heritage Garden", Wedding.STATUS_COMPLETED, 18000.0);
        Wedding w10 = new Wedding("Ishita", "Imran", "30-10-2026", "City Center Hall", Wedding.STATUS_UPCOMING, 28000.0);

        Wedding w11 = new Wedding("Fariha", "Naim", "05-05-2026", "Elite Convention", Wedding.STATUS_COMPLETED, 32000.0);
        Wedding w12 = new Wedding("Sumaiya", "Rakib", "22-11-2026", "Savar Golf Club", Wedding.STATUS_UPCOMING, 40000.0);
        Wedding w13 = new Wedding("Jannat", "Shafiq", "14-07-2026", "Riverside Resort", Wedding.STATUS_COMPLETED, 26000.0);
        Wedding w14 = new Wedding("Mitu", "Tushar", "09-09-2026", "Dhaka Banquet", Wedding.STATUS_COMPLETED, 21000.0);
        Wedding w15 = new Wedding("Lamia", "Ariful", "11-12-2026", "Mirpur Indoor", Wedding.STATUS_UPCOMING, 19000.0);

        addWedding(w1); addWedding(w2); addWedding(w3); addWedding(w4); addWedding(w5);
        addWedding(w6); addWedding(w7); addWedding(w8); addWedding(w9); addWedding(w10);
        addWedding(w11); addWedding(w12); addWedding(w13); addWedding(w14); addWedding(w15);

        // ---------------------------------------------------------
        // FULL DETAILS FOR WEDDING 1: Ayesha & Rahim
        // ---------------------------------------------------------
        Guest g1_1 = new Guest(w1.getWeddingId(), "Hasan Mahmud", "01711000001", Guest.SIDE_BRIDE, Guest.RSVP_CONFIRMED, 2, Guest.ARRIVAL_ARRIVED);
        Guest g1_2 = new Guest(w1.getWeddingId(), "Kamrul Islam", "01811000002", Guest.SIDE_GROOM, Guest.RSVP_CONFIRMED, 1, Guest.ARRIVAL_NOT_ARRIVED);
        Guest g1_3 = new Guest(w1.getWeddingId(), "Farhana Akter", "01933000003", Guest.SIDE_BRIDE, Guest.RSVP_NOT_CONFIRMED, 0, Guest.ARRIVAL_NOT_ARRIVED);
        addGuest(g1_1); addGuest(g1_2); addGuest(g1_3);

        addGift(Gift.createCashGift(g1_1.getGuestId(), 5000.0, false));
        addGift(Gift.createPhysicalGift(g1_2.getGuestId(), "Gold Necklace", true));
        addGift(Gift.createCashGift(g1_3.getGuestId(), 3000.0, false));

        addService(new Service(w1.getWeddingId(), "Photography", "DreamWeaver Studios", 15000.0, 16000.0, true, Service.STATUS_CONFIRMED));
        addService(new Service(w1.getWeddingId(), "Catering", "Royal Feast", 50000.0, 48000.0, true, Service.STATUS_CONFIRMED));
        addService(new Service(w1.getWeddingId(), "Decoration", "Dream Decor", 20000.0, 20000.0, true, Service.STATUS_PENDING));

        // ---------------------------------------------------------
        // FULL DETAILS FOR WEDDING 2: Nabila & Kamal
        // ---------------------------------------------------------
        Guest g2_1 = new Guest(w2.getWeddingId(), "Tania Akter", "01922000001", Guest.SIDE_BRIDE, Guest.RSVP_CONFIRMED, 3, Guest.ARRIVAL_ARRIVED);
        Guest g2_2 = new Guest(w2.getWeddingId(), "Sajib Rahman", "01633000002", Guest.SIDE_GROOM, Guest.RSVP_NOT_CONFIRMED, 0, Guest.ARRIVAL_NOT_ARRIVED);
        addGuest(g2_1); addGuest(g2_2);

        addGift(Gift.createCashGift(g2_1.getGuestId(), 10000.0, true));
        addGift(Gift.createPhysicalGift(g2_2.getGuestId(), "Wall Clock", false));

        addService(new Service(w2.getWeddingId(), "Venue", "Lake View Garden", 20000.0, 20000.0, true, Service.STATUS_COMPLETED));
        addService(new Service(w2.getWeddingId(), "Transportation", "City Rides", 5000.0, 5500.0, false, Service.STATUS_COMPLETED));

        // ---------------------------------------------------------
        // FULL DETAILS FOR WEDDING 3: Tanjila & Hasan
        // ---------------------------------------------------------
        Guest g3_1 = new Guest(w3.getWeddingId(), "Rafiqul Islam", "01744000003", Guest.SIDE_GROOM, Guest.RSVP_CONFIRMED, 1, Guest.ARRIVAL_NOT_ARRIVED);
        Guest g3_2 = new Guest(w3.getWeddingId(), "Salma Begum", "01855000004", Guest.SIDE_BRIDE, Guest.RSVP_CONFIRMED, 2, Guest.ARRIVAL_ARRIVED);
        addGuest(g3_1); addGuest(g3_2);

        addGift(Gift.createCashGift(g3_1.getGuestId(), 2000.0, false));
        addGift(Gift.createPhysicalGift(g3_2.getGuestId(), "Crystal Showpiece", false));

        addService(new Service(w3.getWeddingId(), "Decoration", "Floral Designs", 12000.0, 15000.0, true, Service.STATUS_PENDING));
        addService(new Service(w3.getWeddingId(), "Music", "DJ Sound", 8000.0, 8000.0, false, Service.STATUS_CONFIRMED));

        // ---------------------------------------------------------
        // FULL DETAILS FOR WEDDING 4: Sadia & Tariq
        // ---------------------------------------------------------
        Guest g4_1 = new Guest(w4.getWeddingId(), "Mehedi Hasan", "01855000004", Guest.SIDE_BRIDE, Guest.RSVP_NOT_ATTENDING, 0, Guest.ARRIVAL_NOT_ARRIVED);
        Guest g4_2 = new Guest(w4.getWeddingId(), "Anika Tabassum", "01566000005", Guest.SIDE_GROOM, Guest.RSVP_CONFIRMED, 2, Guest.ARRIVAL_ARRIVED);
        addGuest(g4_1); addGuest(g4_2);

        addGift(Gift.createPhysicalGift(g4_2.getGuestId(), "Microwave Oven", true));

        addService(new Service(w4.getWeddingId(), "Drone Photography", "SkyEye", 10000.0, 10500.0, false, Service.STATUS_COMPLETED));

        // ---------------------------------------------------------
        // FULL DETAILS FOR WEDDING 5: Nusrat & Farhan
        // ---------------------------------------------------------
        Guest g5_1 = new Guest(w5.getWeddingId(), "Zahid Hossain", "01977000006", Guest.SIDE_BRIDE, Guest.RSVP_CONFIRMED, 4, Guest.ARRIVAL_ARRIVED);
        addGuest(g5_1);

        addGift(Gift.createCashGift(g5_1.getGuestId(), 15000.0, true));

        // FIXED THE ERROR HERE: Changed models.Service.STATUS_ONGOING to models.Service.STATUS_PENDING
        addService(new Service(w5.getWeddingId(), "Security", "SafeGuard", 5000.0, 5000.0, true, Service.STATUS_PENDING));

        // ==========================================
        // SYSTEM USERS
        // ==========================================
        users.add(new Admin("U-001", "ThousandYearsofDeath", "admin", "1234", "System Administrator"));
        users.add(new Manager("U-002", "Rahim Uddin", "manager", "1234", w1.getWeddingId()));
        users.add(new FamilyMember("U-003", "Karim Uddin", "family", "1234", w1.getWeddingId(), "Bride's Father"));
    }
}