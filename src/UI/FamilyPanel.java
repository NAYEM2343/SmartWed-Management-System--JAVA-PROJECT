package UI;
import core.WeddingManager;
import models.*;
import models.Gift;
import models.Guest;
import models.Wedding;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FamilyPanel extends JPanel {
    private WeddingManager manager;
    private JTextField txtWeddingIdSearch = new JTextField(8);
    private JTextField txtCoupleNameSearch = new JTextField(12);
    private String authenticatedWeddingId = null;

    private JLabel lblBrideGroom, lblDate, lblVenue, lblStatus;
    private JLabel lblTotalGuests, lblArrivedGuests, lblNotArrived, lblHeadCount;
    private JLabel lblTotalGifts, lblCashGifts, lblPhysicalGifts, lblTotalMoney, lblSpecialGiftsCount;

    // Financial Labels
    private JLabel lblInitialDeposit, lblTotalExpenses, lblBalance;

    private JTable tblSpecialGifts;
    private DefaultTableModel tableModel;

    public FamilyPanel(WeddingManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createSelectorPanel(), BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(4, 1, 10, 10));
        stats.add(createWeddingInfoPanel());
        stats.add(createExpenseStatsPanel());
        stats.add(createGuestStatsPanel());
        stats.add(createGiftStatsPanel());

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(stats, BorderLayout.NORTH);
        center.add(createSpecialGiftsTablePanel(), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        clearDisplays();
    }

    private JPanel createSelectorPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton bAcc = new JButton("Access Dashboard");
        JButton bRef = new JButton("Refresh");

        bAcc.addActionListener(e -> attemptFamilyLogin());
        bRef.addActionListener(e -> refreshDisplayOnly());

        p.add(new JLabel("Couple/Wedding ID (e.g. W-001):"));
        p.add(txtWeddingIdSearch);
        p.add(new JLabel("Couple Name (Bride or Groom):"));
        p.add(txtCoupleNameSearch);
        p.add(bAcc);
        p.add(bRef);

        return p;
    }

    private void attemptFamilyLogin() {
        String id = txtWeddingIdSearch.getText().trim();
        String name = txtCoupleNameSearch.getText().trim().toLowerCase();

        if (id.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Wedding ID and a Couple Name.",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Wedding w = manager.searchWedding(id);
        // SECURITY FIX: Requires exact name match (ignoring case)
        if (w != null && (w.getBrideName().equalsIgnoreCase(name) || w.getGroomName().equalsIgnoreCase(name))) {
            authenticatedWeddingId = w.getWeddingId();
            refreshDisplayOnly();
            JOptionPane.showMessageDialog(this, "Welcome to the dashboard for " + w.getBrideName() + " & " + w.getGroomName() + "!",
                    "Access Granted", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No matching wedding found. Check ID and exact Name.",
                    "Access Denied", JOptionPane.ERROR_MESSAGE);
            authenticatedWeddingId = null;
            clearDisplays();
        }
    }

    private JPanel createWeddingInfoPanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Wedding Information"));
        p.add(lblBrideGroom = new JLabel("Bride & Groom: N/A"));
        p.add(lblDate = new JLabel("Date: N/A"));
        p.add(lblVenue = new JLabel("Venue: N/A"));
        p.add(lblStatus = new JLabel("Status: N/A"));
        return p;
    }

    private JPanel createExpenseStatsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 3, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Event Financial Overview"));

        p.add(lblInitialDeposit = new JLabel("Planner Base Fee: Tk 0.00"));
        p.add(lblTotalExpenses = new JLabel("Vendor Expenses: Tk 0.00"));
        p.add(lblBalance = new JLabel("Total Event Cost: Tk 0.00"));

        lblBalance.setFont(lblBalance.getFont().deriveFont(Font.BOLD));
        return p;
    }

    private JPanel createGuestStatsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 4, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Guest Statistics"));
        p.add(lblTotalGuests = new JLabel("Total Guests: 0"));
        p.add(lblArrivedGuests = new JLabel("Arrived: 0"));
        p.add(lblNotArrived = new JLabel("Not Arrived: 0"));
        p.add(lblHeadCount = new JLabel("Total Head Count: 0"));
        return p;
    }

    private JPanel createGiftStatsPanel() {
        JPanel p = new JPanel(new GridLayout(2, 3, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Gift & Monetary Statistics"));
        p.add(lblTotalGifts = new JLabel("Total Gifts: 0"));
        p.add(lblCashGifts = new JLabel("Cash Gifts: 0"));
        p.add(lblPhysicalGifts = new JLabel("Physical Gifts: 0"));
        p.add(lblTotalMoney = new JLabel("Total Gifted Money: Tk 0.00"));
        p.add(lblSpecialGiftsCount = new JLabel("Special Gifts: 0"));
        lblTotalMoney.setFont(lblTotalMoney.getFont().deriveFont(Font.BOLD));
        lblTotalMoney.setForeground(new Color(0, 102, 0));
        return p;
    }

    private JPanel createSpecialGiftsTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Special Gifts Register"));
        tableModel = new DefaultTableModel(new String[]{"models.Gift ID", "Donor", "Type", "Value"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSpecialGifts = new JTable(tableModel);
        p.add(new JScrollPane(tblSpecialGifts), BorderLayout.CENTER);
        return p;
    }

    public void refreshData() {
        refreshDisplayOnly();
    }

    private void refreshDisplayOnly() {
        if (authenticatedWeddingId == null) {
            clearDisplays();
            return;
        }

        Wedding w = manager.searchWedding(authenticatedWeddingId);
        if (w != null) {
            lblBrideGroom.setText("Bride & Groom: " + w.getBrideName() + " & " + w.getGroomName());
            lblDate.setText("Date: " + w.getWeddingDate());
            lblVenue.setText("Venue: " + w.getVenue());
            lblStatus.setText("Status: " + w.getStatus());

            // FINANCIAL MATH FIX
            double fixedFee = w.getManagementFee();
            double expenses = manager.getTotalActualExpense(authenticatedWeddingId);
            double totalWeddingCost = fixedFee + expenses;

            lblInitialDeposit.setText("Planner Base Fee: " + WeddingManager.formatMoney(fixedFee));
            lblTotalExpenses.setText("Vendor Expenses: " + WeddingManager.formatMoney(expenses));
            lblBalance.setText("Total Event Cost: " + WeddingManager.formatMoney(totalWeddingCost));
            lblBalance.setForeground(Color.RED);
        }

        lblTotalGuests.setText("Total Guests: " + manager.getTotalGuests(authenticatedWeddingId));
        lblArrivedGuests.setText("Arrived: " + manager.getArrivedGuests(authenticatedWeddingId));
        lblNotArrived.setText("Not Arrived: " + manager.getNotArrivedGuests(authenticatedWeddingId));
        lblHeadCount.setText("Total Head Count: " + manager.getTotalHeadCount(authenticatedWeddingId));

        lblTotalGifts.setText("Total Gifts: " + manager.getTotalGifts(authenticatedWeddingId));
        lblCashGifts.setText("Cash Gifts: " + manager.getCashGiftCount(authenticatedWeddingId));
        lblPhysicalGifts.setText("Physical Gifts: " + manager.getPhysicalGiftCount(authenticatedWeddingId));
        lblTotalMoney.setText("Total Gifted Money: " + WeddingManager.formatMoney(manager.getTotalGiftedMoney(authenticatedWeddingId)));
        lblSpecialGiftsCount.setText("Special Gifts: " + manager.getSpecialGiftCount(authenticatedWeddingId));

        tableModel.setRowCount(0);
        for (Gift g : manager.getSpecialGifts(authenticatedWeddingId)) {
            Guest donor = manager.searchGuest(g.getGuestId());
            tableModel.addRow(new Object[]{
                    g.getGiftId(), donor != null ? donor.getName() : g.getGuestId(), g.getGiftType(), g.getDisplayValue()
            });
        }
    }

    private void clearDisplays() {
        lblBrideGroom.setText("Bride & Groom: N/A"); lblDate.setText("Date: N/A");
        lblVenue.setText("Venue: N/A"); lblStatus.setText("Status: N/A");

        lblInitialDeposit.setText("Planner Base Fee: Tk 0.00");
        lblTotalExpenses.setText("Vendor Expenses: Tk 0.00");
        lblBalance.setText("Total Event Cost: Tk 0.00");
        lblBalance.setForeground(Color.BLACK);

        lblTotalGuests.setText("Total Guests: 0"); lblArrivedGuests.setText("Arrived: 0");
        lblNotArrived.setText("Not Arrived: 0"); lblHeadCount.setText("Total Head Count: 0");

        lblTotalGifts.setText("Total Gifts: 0"); lblCashGifts.setText("Cash Gifts: 0"); lblPhysicalGifts.setText("Physical Gifts: 0");
        lblTotalMoney.setText("Total Gifted Money: Tk 0.00"); lblSpecialGiftsCount.setText("Special Gifts: 0");

        if (tableModel != null) { tableModel.setRowCount(0); }
    }
}