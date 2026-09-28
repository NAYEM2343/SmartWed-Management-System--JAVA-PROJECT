package UI;

import core.WeddingManager;
import models.Wedding;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminPanel extends JPanel {
    private WeddingManager manager;
    private JLabel lblTotalWeddings, lblCompletedWeddings, lblUpcomingWeddings;
    private JLabel lblTotalRevenue, lblTotalExpense, lblGiftedMoney;
    private DefaultTableModel revenueModel;

    public AdminPanel(WeddingManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("SmartWed Business Overview");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshData());

        header.add(title, BorderLayout.WEST);
        header.add(btnRefresh, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(createDashboardPanel(), BorderLayout.CENTER);
        add(createRevenueTablePanel(), BorderLayout.SOUTH);

        refreshData();
    }

    private JPanel createDashboardPanel() {
        JPanel p = new JPanel(new GridLayout(2, 3, 15, 15));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Key Figures",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        Font f = new Font("Arial", Font.PLAIN, 16);

        p.add(lblTotalWeddings = createLabel("Total Weddings Managed: 0", f));
        p.add(lblCompletedWeddings = createLabel("Completed Weddings: 0", f));
        p.add(lblUpcomingWeddings = createLabel("Upcoming Weddings: 0", f));

        p.add(lblTotalRevenue = createLabel("SmartWed Revenue: Tk 0.00", new Font("Arial", Font.BOLD, 18)));
        lblTotalRevenue.setForeground(new Color(0, 102, 0));

        p.add(lblTotalExpense = createLabel("Total Actual Expense: Tk 0.00", f));
        p.add(lblGiftedMoney = createLabel("<html><center>Gifted Money: Tk 0.00<br>"
                + "<font size=2 color=gray>(family's money - not revenue)</font></center></html>", f));

        return p;
    }

    private JLabel createLabel(String text, Font f) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(f);
        return l;
    }

    private JPanel createRevenueTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Revenue Breakdown by models.Wedding"));

        revenueModel = new DefaultTableModel(new String[]{"models.Wedding", "Management Fee", "models.Service Cost (Actual)", "Commission (10%)", "SmartWed Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JScrollPane scroll = new JScrollPane(new JTable(revenueModel));
        scroll.setPreferredSize(new Dimension(100, 150));
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    public void refreshData() {
        lblTotalWeddings.setText("Total Weddings Managed: " + manager.getTotalWeddings());
        lblCompletedWeddings.setText("Completed Weddings: " + manager.getCompletedWeddings());
        lblUpcomingWeddings.setText("Upcoming Weddings: " + manager.getUpcomingWeddings());
        lblTotalRevenue.setText("SmartWed Revenue: " + WeddingManager.formatMoney(manager.getTotalRevenue()));
        lblTotalExpense.setText("Total Actual Expense: " + WeddingManager.formatMoney(manager.getTotalActualExpense()));

        lblGiftedMoney.setText("<html><center>Gifted Money: " + WeddingManager.formatMoney(manager.getTotalGiftedMoney())
                + "<br><font size=2 color=gray>(family's money - not revenue)</font></center></html>");

        revenueModel.setRowCount(0);
        for (Wedding w : manager.getAllWeddings()) {
            String id = w.getWeddingId();
            revenueModel.addRow(new Object[]{
                    w.getDisplayLabel(),
                    WeddingManager.formatMoney(w.getManagementFee()),
                    WeddingManager.formatMoney(manager.getTotalActualExpense(id)),
                    WeddingManager.formatMoney(manager.getCommissionForWedding(id)),
                    WeddingManager.formatMoney(manager.getRevenueForWedding(id))
            });
        }
    }
}