package UI;

import core.WeddingManager;
import models.Gift;
import models.Guest;
import models.Service;
import models.Wedding;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ManagementPanel extends JPanel {

    private WeddingManager manager;

    // Layout Management for the new multi-step portal
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Master Filter Variables
    private String globalWeddingFilter = null;
    private JLabel lblActiveWeddingTitle;
    private JComboBox<String> cbPromptWeddingId;

    // models.Wedding Tab Elements
    private JTextField txtBrideName, txtGroomName, txtDate, txtVenue, txtManagementFee, txtWeddingSearch;
    private JComboBox<String> cbWeddingStatus;
    private JCheckBox[] serviceCheckBoxes;
    private JTable tblWeddings;
    private DefaultTableModel weddingTableModel;
    private String selectedWeddingId = null;

    // models.Guest Tab Elements
    private JComboBox<String> cbGuestWedding, cbRsvp, cbArrival;
    private JTextField txtGuestName, txtGuestPhone, txtAccompanying, txtGuestSearch;
    private JRadioButton rbBrideSide, rbGroomSide;
    private JTable tblGuests;
    private DefaultTableModel guestTableModel;
    private String selectedGuestId = null;

    // models.Gift Tab Elements
    private JComboBox<String> cbGiftDonor, cbGiftType;
    private JTextField txtGiftAmount, txtGiftDesc, txtGiftSearch;
    private JCheckBox chkSpecialGift;
    private JTable tblGifts;
    private DefaultTableModel giftTableModel;
    private String selectedGiftId = null;

    // models.Service Tab Elements
    private JComboBox<String> cbServiceWedding, cbServiceName, cbServiceStatus;
    private JTextField txtProvider, txtEstCost, txtActCost, txtServiceSearch;
    private JCheckBox chkServiceRequired;
    private JTable tblServices;
    private DefaultTableModel serviceTableModel;
    private String selectedServiceId = null;

    public ManagementPanel(WeddingManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Add our 4 distinct portal screens
        cardPanel.add(createHomeCard(), "HOME");
        cardPanel.add(createWeddingCrudCard(), "WEDDING_CRUD");
        cardPanel.add(createPromptCard(), "PROMPT_MANAGE");
        cardPanel.add(createManageTabsCard(), "MANAGE_TABS");

        add(cardPanel, BorderLayout.CENTER);
        refreshData();
    }

    // ==========================================
    // SECURITY CONTROLLER
    // ==========================================
    public void resetPortalToHome() {
        globalWeddingFilter = null;
        cardLayout.show(cardPanel, "HOME");
        refreshData();
    }

    // ==========================================
    // PORTAL SCREENS (CARD LAYOUT VIEWS)
    // ==========================================

    private JPanel createHomeCard() {
        JPanel p = new JPanel(new GridBagLayout());
        JPanel inner = new JPanel(new GridLayout(3, 1, 20, 20));

        JLabel title = new JLabel("Staff Management Portal", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(26, 37, 47));

        JButton btnCreate = new JButton("1) Create New Customer Wedding Details");
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreate.addActionListener(e -> cardLayout.show(cardPanel, "WEDDING_CRUD"));

        JButton btnManage = new JButton("2) Enter Details for Customer Wedding (Guests / Gifts / Services)");
        btnManage.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnManage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnManage.addActionListener(e -> {
            reloadPromptCombo();
            cardLayout.show(cardPanel, "PROMPT_MANAGE");
        });

        inner.add(title);
        inner.add(btnCreate);
        inner.add(btnManage);
        p.add(inner);
        return p;
    }

    private JPanel createWeddingCrudCard() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBack = new JButton("← Back to Portal Home");
        btnBack.addActionListener(e -> cardLayout.show(cardPanel, "HOME"));
        header.add(btnBack);

        p.add(header, BorderLayout.NORTH);
        p.add(createWeddingTab(), BorderLayout.CENTER);
        return p;
    }

    private JPanel createPromptCard() {
        JPanel p = new JPanel(new GridBagLayout());
        JPanel inner = new JPanel(new GridLayout(3, 1, 15, 15));
        inner.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Search models.Wedding ID to Open Management"));

        cbPromptWeddingId = new JComboBox<>();

        JButton btnOpen = new JButton("Open Wedding Dashboard");
        btnOpen.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnOpen.addActionListener(e -> {
            if (cbPromptWeddingId.getSelectedItem() == null) {
                showError("Please search for and select a valid wedding ID first.");
                return;
            }

            // Lock the system to this unique ID and open the tabs
            globalWeddingFilter = extractId(cbPromptWeddingId);
            Wedding w = manager.searchWedding(globalWeddingFilter);

            lblActiveWeddingTitle.setText("Currently Managing: " + w.getDisplayLabel());
            refreshData();
            cardLayout.show(cardPanel, "MANAGE_TABS");
        });

        // LOGIC FIX: Custom Search Field that Automates the Click!
        JPanel searchBoxPanel = new JPanel(new BorderLayout(5, 0));
        JTextField txtAutoSearch = new JTextField(12);
        txtAutoSearch.setToolTipText("Type ID & press Enter to open instantly");

        txtAutoSearch.addActionListener(e -> {
            String text = txtAutoSearch.getText().trim().toLowerCase();
            if (text.isEmpty()) return;

            boolean found = false;
            for (int i = 0; i < cbPromptWeddingId.getItemCount(); i++) {
                if (cbPromptWeddingId.getItemAt(i).toLowerCase().contains(text)) {
                    cbPromptWeddingId.setSelectedIndex(i);
                    found = true;
                    txtAutoSearch.setText("");
                    btnOpen.doClick(); // AUTOMATICALLY EXECUTE DASHBOARD OPEN
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(this, "Invalid ID. No matching arrangement found.",
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                txtAutoSearch.setText("");
            }
        });

        searchBoxPanel.add(txtAutoSearch, BorderLayout.WEST);
        searchBoxPanel.add(cbPromptWeddingId, BorderLayout.CENTER);

        JButton btnBack = new JButton("← Back");
        btnBack.addActionListener(e -> cardLayout.show(cardPanel, "HOME"));

        inner.add(new JLabel("Type Wedding ID (e.g. W-001) and press Enter:"));
        inner.add(searchBoxPanel);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btns.add(btnBack);
        btns.add(btnOpen);
        inner.add(btns);

        p.add(inner);
        return p;
    }

    private JPanel createManageTabsCard() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        header.setBackground(new Color(200, 220, 240));

        lblActiveWeddingTitle = new JLabel("Currently Managing: ");
        lblActiveWeddingTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblActiveWeddingTitle.setForeground(new Color(0, 51, 102));

        JButton btnClose = new JButton("Close Dashboard");
        btnClose.addActionListener(e -> {
            globalWeddingFilter = null;
            refreshData();
            reloadPromptCombo();
            cardLayout.show(cardPanel, "PROMPT_MANAGE");
        });

        header.add(lblActiveWeddingTitle, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabs.addTab("Guest Management", createGuestTab());
        tabs.addTab("Gift Management", createGiftTab());
        tabs.addTab("Service Management", createServiceTab());

        p.add(header, BorderLayout.NORTH);
        p.add(tabs, BorderLayout.CENTER);
        return p;
    }

    // ==========================================
    // UI REUSABLE HELPER METHODS
    // ==========================================

    private JPanel createFormPanel(String title, int rows, int cols) {
        JPanel panel = new JPanel(new GridLayout(rows, cols, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
        return panel;
    }

    private JTable createTable(DefaultTableModel model, Runnable onSelection) {
        JTable table = new JTable(model);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onSelection.run();
            }
        });
        return table;
    }

    private JPanel createButtonPanel(Runnable onAdd, Runnable onUpdate, Runnable onDel, Runnable onClear, Runnable onSearch, JTextField searchField) {
        JPanel p = new JPanel(new FlowLayout());

        JButton bAdd = new JButton("Add");
        JButton bUp = new JButton("Update");
        JButton bDel = new JButton("Delete");
        JButton bClr = new JButton("Clear Form");
        JButton bSrch = new JButton("Search within view");

        bAdd.addActionListener(e -> onAdd.run());
        bUp.addActionListener(e -> onUpdate.run());
        bDel.addActionListener(e -> onDel.run());
        bClr.addActionListener(e -> onClear.run());
        bSrch.addActionListener(e -> onSearch.run());

        p.add(bAdd); p.add(bUp); p.add(bDel); p.add(bClr);
        p.add(new JLabel("  Search:")); p.add(searchField); p.add(bSrch);

        return p;
    }

    private JPanel createSearchableCombo(JComboBox<String> combo) {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        JTextField search = new JTextField(6);
        search.setToolTipText("Type ID & press Enter");

        search.addActionListener(e -> {
            String text = search.getText().trim().toLowerCase();
            if (text.isEmpty()) return;

            boolean found = false;
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).toLowerCase().contains(text)) {
                    combo.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(this, "Invalid ID. No matching record found in the list.",
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                search.setText("");
            }
        });

        p.add(search, BorderLayout.WEST);
        p.add(combo, BorderLayout.CENTER);
        return p;
    }

    // ==========================================
    // TAB CREATION (DATA ENTRY FORMS)
    // ==========================================

    private JPanel createWeddingTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = createFormPanel("Step 1: Enter models.Wedding Details (ID is generated automatically)", 3, 4);
        txtBrideName = new JTextField();
        txtGroomName = new JTextField();
        txtDate = new JTextField();
        txtVenue = new JTextField();
        txtManagementFee = new JTextField("0");
        cbWeddingStatus = new JComboBox<>(new String[]{
                Wedding.STATUS_UPCOMING, Wedding.STATUS_ONGOING, Wedding.STATUS_COMPLETED
        });

        formPanel.add(new JLabel("Bride Name:")); formPanel.add(txtBrideName);
        formPanel.add(new JLabel("Groom Name:")); formPanel.add(txtGroomName);
        formPanel.add(new JLabel("Date (dd-mm-yyyy):")); formPanel.add(txtDate);
        formPanel.add(new JLabel("Venue:")); formPanel.add(txtVenue);
        formPanel.add(new JLabel("Initial Deposit (Tk):")); formPanel.add(txtManagementFee);
        formPanel.add(new JLabel("Status:")); formPanel.add(cbWeddingStatus);

        JPanel servicesPanel = createFormPanel("Step 2: Required Services Checklist (applied when Adding)", 2, 4);
        serviceCheckBoxes = new JCheckBox[Service.STANDARD_SERVICES.length];
        for (int i = 0; i < Service.STANDARD_SERVICES.length; i++) {
            serviceCheckBoxes[i] = new JCheckBox(Service.STANDARD_SERVICES[i]);
            servicesPanel.add(serviceCheckBoxes[i]);
        }

        weddingTableModel = new DefaultTableModel(new String[]{"ID", "Bride", "Groom", "Date", "Venue", "Status", "Initial Deposit"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblWeddings = createTable(weddingTableModel, this::loadSelectedWedding);
        txtWeddingSearch = new JTextField(10);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(formPanel, BorderLayout.NORTH);
        top.add(servicesPanel, BorderLayout.CENTER);
        top.add(createButtonPanel(this::handleAddWedding, this::handleUpdateWedding, this::handleDeleteWedding, this::clearWeddingForm, this::handleSearchWedding, txtWeddingSearch), BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblWeddings), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGuestTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = createFormPanel("Guest Form", 4, 4);
        cbGuestWedding = new JComboBox<>();
        txtGuestName = new JTextField();
        txtGuestPhone = new JTextField();
        txtAccompanying = new JTextField("0");
        rbBrideSide = new JRadioButton(Guest.SIDE_BRIDE, true);
        rbGroomSide = new JRadioButton(Guest.SIDE_GROOM);

        ButtonGroup bgSide = new ButtonGroup();
        bgSide.add(rbBrideSide); bgSide.add(rbGroomSide);
        JPanel sidePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sidePanel.add(rbBrideSide); sidePanel.add(rbGroomSide);

        cbRsvp = new JComboBox<>(new String[]{Guest.RSVP_CONFIRMED, Guest.RSVP_NOT_CONFIRMED, Guest.RSVP_NOT_ATTENDING});
        cbArrival = new JComboBox<>(new String[]{Guest.ARRIVAL_NOT_ARRIVED, Guest.ARRIVAL_ARRIVED});

        formPanel.add(new JLabel("Wedding ID (Locked):")); formPanel.add(cbGuestWedding);
        formPanel.add(new JLabel("Name:")); formPanel.add(txtGuestName);
        formPanel.add(new JLabel("Phone:")); formPanel.add(txtGuestPhone);
        formPanel.add(new JLabel("Side:")); formPanel.add(sidePanel);
        formPanel.add(new JLabel("RSVP:")); formPanel.add(cbRsvp);
        formPanel.add(new JLabel("Accompanying People:")); formPanel.add(txtAccompanying);
        formPanel.add(new JLabel("Arrival Status:")); formPanel.add(cbArrival);

        guestTableModel = new DefaultTableModel(new String[]{"ID", "Wedding", "Name", "Phone", "Side", "RSVP", "Accompanying", "Arrival"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblGuests = createTable(guestTableModel, this::loadSelectedGuest);
        txtGuestSearch = new JTextField(10);

        JPanel top = new JPanel(new BorderLayout());
        top.add(formPanel, BorderLayout.CENTER);
        top.add(createButtonPanel(this::handleAddGuest, this::handleUpdateGuest, this::handleDeleteGuest, this::clearGuestForm, this::handleSearchGuest, txtGuestSearch), BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblGuests), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGiftTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = createFormPanel("Gift Form", 3, 4);
        cbGiftDonor = new JComboBox<>();
        cbGiftType = new JComboBox<>(new String[]{Gift.TYPE_CASH, Gift.TYPE_PHYSICAL});
        txtGiftAmount = new JTextField("0");
        txtGiftDesc = new JTextField();
        chkSpecialGift = new JCheckBox("Mark as Special models.Gift");

        formPanel.add(new JLabel("Donor (Guest):")); formPanel.add(createSearchableCombo(cbGiftDonor));
        formPanel.add(new JLabel("Gift Type:")); formPanel.add(cbGiftType);
        formPanel.add(new JLabel("Cash Amount (Tk):")); formPanel.add(txtGiftAmount);
        formPanel.add(new JLabel("Physical Description:")); formPanel.add(txtGiftDesc);
        formPanel.add(new JLabel("Special Status:")); formPanel.add(chkSpecialGift);

        cbGiftType.addActionListener(e -> applyGiftTypeToggle());
        applyGiftTypeToggle();

        giftTableModel = new DefaultTableModel(new String[]{"Gift ID", "Donor", "Type", "Value", "Special"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblGifts = createTable(giftTableModel, this::loadSelectedGift);
        txtGiftSearch = new JTextField(10);

        JPanel top = new JPanel(new BorderLayout());
        top.add(formPanel, BorderLayout.CENTER);
        top.add(createButtonPanel(this::handleAddGift, this::handleUpdateGift, this::handleDeleteGift, this::clearGiftForm, this::handleSearchGift, txtGiftSearch), BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblGifts), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createServiceTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = createFormPanel("Service Vendor Form", 4, 4);
        cbServiceWedding = new JComboBox<>();
        cbServiceName = new JComboBox<>(Service.STANDARD_SERVICES);
        txtProvider = new JTextField();
        txtEstCost = new JTextField("0");
        txtActCost = new JTextField("0");
        chkServiceRequired = new JCheckBox("Required Service");
        cbServiceStatus = new JComboBox<>(new String[]{Service.STATUS_PENDING, Service.STATUS_CONFIRMED, Service.STATUS_COMPLETED});

        formPanel.add(new JLabel("Wedding ID (Locked):")); formPanel.add(cbServiceWedding);
        formPanel.add(new JLabel("Service Name:")); formPanel.add(cbServiceName);
        formPanel.add(new JLabel("Provider:")); formPanel.add(txtProvider);
        formPanel.add(new JLabel("Estimated Cost (Tk):")); formPanel.add(txtEstCost);
        formPanel.add(new JLabel("Actual Cost (Tk):")); formPanel.add(txtActCost);
        formPanel.add(new JLabel("Requirement:")); formPanel.add(chkServiceRequired);
        formPanel.add(new JLabel("Status:")); formPanel.add(cbServiceStatus);

        serviceTableModel = new DefaultTableModel(new String[]{"ID", "Wedding", "Service", "Provider", "Est. Cost", "Act. Cost", "Required", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tblServices = createTable(serviceTableModel, this::loadSelectedService);
        txtServiceSearch = new JTextField(10);

        JPanel top = new JPanel(new BorderLayout());
        top.add(formPanel, BorderLayout.CENTER);
        top.add(createButtonPanel(this::handleAddService, this::handleUpdateService, this::handleDeleteService, this::clearServiceForm, this::handleSearchService, txtServiceSearch), BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblServices), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // DATA REFRESHING & LOGIC
    // ==========================================

    public void refreshData() {
        refreshWeddingTable(manager.getAllWeddings());

        if (globalWeddingFilter == null || globalWeddingFilter.isEmpty()) {
            refreshGuestTable(manager.getAllGuests());
            refreshGiftTable(manager.getAllGifts());
            refreshServiceTable(manager.getAllServices());
        } else {
            refreshGuestTable(manager.getGuestsByWedding(globalWeddingFilter));
            refreshGiftTable(manager.getGiftsByWedding(globalWeddingFilter));
            refreshServiceTable(manager.getServicesByWedding(globalWeddingFilter));
        }

        reloadWeddingCombos();
        reloadDonorCombo();
    }

    private void applyGiftTypeToggle() {
        boolean isCash = Gift.TYPE_CASH.equals(cbGiftType.getSelectedItem());
        txtGiftAmount.setEnabled(isCash);
        txtGiftDesc.setEnabled(!isCash);
    }

    // ==========================================
    // WEDDING CRUD
    // ==========================================

    private boolean validateWedding() {
        if (isBlank(txtBrideName) || isBlank(txtGroomName)) { showError("Bride and Groom names cannot be empty!"); return false; }
        if (isBlank(txtVenue)) { showError("Venue cannot be empty!"); return false; }
        if (!Wedding.isValidDate(txtDate.getText())) { showError("Please enter the date as dd-mm-yyyy (e.g. 15-12-2026)."); return false; }
        if (!isNonNegativeDouble(txtManagementFee.getText())) { showError("Initial Deposit must be a valid, non-negative number."); return false; }
        return true;
    }

    private Wedding buildWeddingFromForm() {
        return new Wedding(
                txtBrideName.getText().trim(), txtGroomName.getText().trim(), txtDate.getText().trim(),
                txtVenue.getText().trim(), cbWeddingStatus.getSelectedItem().toString(), Double.parseDouble(txtManagementFee.getText().trim())
        );
    }

    private void handleAddWedding() {
        if (!validateWedding()) return;
        Wedding wedding = buildWeddingFromForm();
        manager.addWedding(wedding);
        int created = 0;

        for (int i = 0; i < serviceCheckBoxes.length; i++) {
            if (serviceCheckBoxes[i].isSelected()) {
                manager.addService(new Service(wedding.getWeddingId(), Service.STANDARD_SERVICES[i], "", 0.0, 0.0, true, Service.STATUS_PENDING));
                created++;
            }
        }
        refreshData();
        clearWeddingForm();
        showInfo("Customer Wedding Details Saved! \nGenerated Unique ID: " + wedding.getWeddingId() + "\nRequired Services Generated: " + created);
    }

    private void handleUpdateWedding() {
        if (selectedWeddingId == null) { showError("Please select a wedding from the table first."); return; }
        if (!validateWedding()) return;
        manager.updateWedding(selectedWeddingId, buildWeddingFromForm());
        refreshData();
        clearWeddingForm();
        showInfo("Wedding details updated successfully.");
    }

    private void handleDeleteWedding() {
        if (selectedWeddingId == null) { showError("Please select a wedding from the table first."); return; }
        if (!confirmDelete("Deleting this wedding will also remove its guests, their gifts and its services.\nAre you sure?")) return;
        manager.deleteWedding(selectedWeddingId);
        refreshData();
        clearWeddingForm();
        showInfo("Wedding deleted successfully.");
    }

    private void handleSearchWedding() {
        String text = txtWeddingSearch.getText().trim();
        if (text.isEmpty()) {
            refreshWeddingTable(manager.getAllWeddings());
        } else {
            refreshWeddingTable(manager.searchWeddings(text));
        }
    }

    private void loadSelectedWedding() {
        int row = tblWeddings.getSelectedRow();
        if (row == -1) return;
        selectedWeddingId = weddingTableModel.getValueAt(row, 0).toString();
        Wedding w = manager.searchWedding(selectedWeddingId);
        if (w == null) return;
        txtBrideName.setText(w.getBrideName());
        txtGroomName.setText(w.getGroomName());
        txtDate.setText(w.getWeddingDate());
        txtVenue.setText(w.getVenue());
        txtManagementFee.setText(String.valueOf(w.getManagementFee()));
        cbWeddingStatus.setSelectedItem(w.getStatus());
    }

    private void clearWeddingForm() {
        txtBrideName.setText(""); txtGroomName.setText(""); txtDate.setText(""); txtVenue.setText(""); txtManagementFee.setText("0");
        cbWeddingStatus.setSelectedIndex(0);
        for (JCheckBox box : serviceCheckBoxes) box.setSelected(false);
        txtWeddingSearch.setText(""); tblWeddings.clearSelection(); selectedWeddingId = null;
        refreshWeddingTable(manager.getAllWeddings());
    }

    private void refreshWeddingTable(ArrayList<Wedding> list) {
        weddingTableModel.setRowCount(0);
        for (Wedding w : list) {
            weddingTableModel.addRow(new Object[]{
                    w.getWeddingId(), w.getBrideName(), w.getGroomName(), w.getWeddingDate(), w.getVenue(), w.getStatus(), WeddingManager.formatMoney(w.getManagementFee())
            });
        }
    }

    // ==========================================
    // GUEST CRUD
    // ==========================================

    private boolean validateGuest() {
        if (cbGuestWedding.getSelectedItem() == null) { showError("Please select a wedding for this guest!"); return false; }
        if (isBlank(txtGuestName)) { showError("Name cannot be empty!"); return false; }
        if (!isValidPhone(txtGuestPhone.getText())) { showError("Phone number must contain 7 to 15 digits only."); return false; }
        if (!isNonNegativeInt(txtAccompanying.getText())) { showError("Accompanying people must be a valid, non-negative whole number."); return false; }
        return true;
    }

    private Guest buildGuestFromForm() {
        return new Guest(
                extractId(cbGuestWedding), txtGuestName.getText().trim(), txtGuestPhone.getText().trim(),
                rbBrideSide.isSelected() ? Guest.SIDE_BRIDE : Guest.SIDE_GROOM, cbRsvp.getSelectedItem().toString(),
                Integer.parseInt(txtAccompanying.getText().trim()), cbArrival.getSelectedItem().toString()
        );
    }

    private void handleAddGuest() {
        if (!validateGuest()) return;
        manager.addGuest(buildGuestFromForm());
        refreshData();
        clearGuestForm();
        showInfo("Guest added successfully.");
    }

    private void handleUpdateGuest() {
        if (selectedGuestId == null) { showError("Please select a guest from the table first."); return; }
        if (!validateGuest()) return;
        manager.updateGuest(selectedGuestId, buildGuestFromForm());
        refreshData();
        clearGuestForm();
        showInfo("Guest updated successfully.");
    }

    private void handleDeleteGuest() {
        if (selectedGuestId == null) { showError("Please select a guest from the table first."); return; }
        if (!confirmDelete("Deleting this guest will also remove their gifts.\nAre you sure?")) return;
        manager.deleteGuest(selectedGuestId);
        refreshData();
        clearGuestForm();
        showInfo("Guest deleted successfully.");
    }

    private void handleSearchGuest() {
        String text = txtGuestSearch.getText().trim();
        if (text.isEmpty()) {
            refreshData();
        } else {
            refreshGuestTable(manager.searchGuests(text));
        }
    }

    private void loadSelectedGuest() {
        int row = tblGuests.getSelectedRow();
        if (row == -1) return;
        selectedGuestId = guestTableModel.getValueAt(row, 0).toString();
        Guest g = manager.searchGuest(selectedGuestId);
        if (g == null) return;
        selectComboById(cbGuestWedding, g.getWeddingId());
        txtGuestName.setText(g.getName());
        txtGuestPhone.setText(g.getPhone());
        if (g.getSide().equals(Guest.SIDE_BRIDE)) rbBrideSide.setSelected(true); else rbGroomSide.setSelected(true);
        cbRsvp.setSelectedItem(g.getRsvpStatus());
        txtAccompanying.setText(String.valueOf(g.getAccompanyingPeople()));
        cbArrival.setSelectedItem(g.getArrivalStatus());
    }

    private void clearGuestForm() {
        txtGuestName.setText(""); txtGuestPhone.setText(""); txtAccompanying.setText("0"); rbBrideSide.setSelected(true);
        cbRsvp.setSelectedIndex(0); cbArrival.setSelectedIndex(0); txtGuestSearch.setText(""); tblGuests.clearSelection(); selectedGuestId = null;
        refreshData();
    }

    private void refreshGuestTable(ArrayList<Guest> list) {
        guestTableModel.setRowCount(0);
        for (Guest g : list) {
            guestTableModel.addRow(new Object[]{
                    g.getGuestId(), g.getWeddingId(), g.getName(), g.getPhone(), g.getSide(), g.getRsvpStatus(), g.getAccompanyingPeople(), g.getArrivalStatus()
            });
        }
    }

    // ==========================================
    // GIFT CRUD
    // ==========================================

    private boolean validateGift() {
        if (cbGiftDonor.getSelectedItem() == null) { showError("Please select a donor guest!"); return false; }
        if (Gift.TYPE_CASH.equals(cbGiftType.getSelectedItem())) {
            if (!isNonNegativeDouble(txtGiftAmount.getText()) || Double.parseDouble(txtGiftAmount.getText().trim()) <= 0) { showError("A cash gift needs an amount greater than zero."); return false; }
        } else {
            if (isBlank(txtGiftDesc)) { showError("A physical gift needs a description."); return false; }
        }
        return true;
    }

    private Gift buildGiftFromForm() {
        String donorId = extractId(cbGiftDonor);
        boolean special = chkSpecialGift.isSelected();
        if (Gift.TYPE_CASH.equals(cbGiftType.getSelectedItem())) return Gift.createCashGift(donorId, Double.parseDouble(txtGiftAmount.getText().trim()), special);
        return Gift.createPhysicalGift(donorId, txtGiftDesc.getText().trim(), special);
    }

    private void handleAddGift() {
        if (!validateGift()) return;
        manager.addGift(buildGiftFromForm());
        refreshData();
        clearGiftForm();
        showInfo("Gift added successfully.");
    }

    private void handleUpdateGift() {
        if (selectedGiftId == null) { showError("Please select a gift from the table first."); return; }
        if (!validateGift()) return;
        manager.updateGift(selectedGiftId, buildGiftFromForm());
        refreshData();
        clearGiftForm();
        showInfo("Gift updated successfully.");
    }

    private void handleDeleteGift() {
        if (selectedGiftId == null) { showError("Please select a gift from the table first."); return; }
        if (!confirmDelete("Are you sure you want to delete this gift record?")) return;
        manager.deleteGift(selectedGiftId);
        refreshData();
        clearGiftForm();
        showInfo("Gift deleted successfully.");
    }

    private void handleSearchGift() {
        String text = txtGiftSearch.getText().trim();
        if (text.isEmpty()) {
            refreshData();
        } else {
            refreshGiftTable(manager.searchGifts(text));
        }
    }

    private void loadSelectedGift() {
        int row = tblGifts.getSelectedRow();
        if (row == -1) return;
        selectedGiftId = giftTableModel.getValueAt(row, 0).toString();
        Gift g = manager.searchGift(selectedGiftId);
        if (g == null) return;
        selectComboById(cbGiftDonor, g.getGuestId());
        cbGiftType.setSelectedItem(g.getGiftType());
        applyGiftTypeToggle();
        txtGiftAmount.setText(String.valueOf(g.getAmount()));
        txtGiftDesc.setText(g.getDescription());
        chkSpecialGift.setSelected(g.isSpecial());
    }

    private void clearGiftForm() {
        txtGiftAmount.setText("0"); txtGiftDesc.setText(""); chkSpecialGift.setSelected(false); cbGiftType.setSelectedIndex(0); applyGiftTypeToggle();
        txtGiftSearch.setText(""); tblGifts.clearSelection(); selectedGiftId = null;
        refreshData();
    }

    private void refreshGiftTable(ArrayList<Gift> list) {
        giftTableModel.setRowCount(0);
        for (Gift g : list) {
            Guest donor = manager.searchGuest(g.getGuestId());
            giftTableModel.addRow(new Object[]{
                    g.getGiftId(), donor != null ? donor.getDisplayLabel() : g.getGuestId(), g.getGiftType(), g.getDisplayValue(), g.isSpecial() ? "Yes" : "No"
            });
        }
    }

    // ==========================================
    // SERVICE CRUD
    // ==========================================

    private boolean validateService() {
        if (cbServiceWedding.getSelectedItem() == null) { showError("Please select a wedding for this service!"); return false; }
        if (isBlank(txtProvider)) { showError("Provider cannot be empty!"); return false; }
        if (!isNonNegativeDouble(txtEstCost.getText()) || !isNonNegativeDouble(txtActCost.getText())) { showError("Costs must be valid, non-negative numbers."); return false; }
        return true;
    }

    private Service buildServiceFromForm() {
        return new Service(
                extractId(cbServiceWedding), cbServiceName.getSelectedItem().toString(), txtProvider.getText().trim(),
                Double.parseDouble(txtEstCost.getText().trim()), Double.parseDouble(txtActCost.getText().trim()),
                chkServiceRequired.isSelected(), cbServiceStatus.getSelectedItem().toString()
        );
    }

    private void handleAddService() {
        if (!validateService()) return;
        manager.addService(buildServiceFromForm());
        refreshData();
        clearServiceForm();
        showInfo("Service added successfully.");
    }

    private void handleUpdateService() {
        if (selectedServiceId == null) { showError("Please select a service from the table first."); return; }
        if (!validateService()) return;
        manager.updateService(selectedServiceId, buildServiceFromForm());
        refreshData();
        clearServiceForm();
        showInfo("Service updated successfully.");
    }

    private void handleDeleteService() {
        if (selectedServiceId == null) { showError("Please select a service from the table first."); return; }
        if (!confirmDelete("Are you sure you want to delete this service record?")) return;
        manager.deleteService(selectedServiceId);
        refreshData();
        clearServiceForm();
        showInfo("Service deleted successfully.");
    }

    private void handleSearchService() {
        String text = txtServiceSearch.getText().trim();
        if (text.isEmpty()) {
            refreshData();
        } else {
            refreshServiceTable(manager.searchServices(text));
        }
    }

    private void loadSelectedService() {
        int row = tblServices.getSelectedRow();
        if (row == -1) return;
        selectedServiceId = serviceTableModel.getValueAt(row, 0).toString();
        Service s = manager.searchService(selectedServiceId);
        if (s == null) return;
        selectComboById(cbServiceWedding, s.getWeddingId());
        cbServiceName.setSelectedItem(s.getServiceName());
        txtProvider.setText(s.getProvider());
        txtEstCost.setText(String.valueOf(s.getEstimatedCost()));
        txtActCost.setText(String.valueOf(s.getActualCost()));
        chkServiceRequired.setSelected(s.isRequired());
        cbServiceStatus.setSelectedItem(s.getStatus());
    }

    private void clearServiceForm() {
        txtProvider.setText(""); txtEstCost.setText("0"); txtActCost.setText("0"); chkServiceRequired.setSelected(false);
        cbServiceName.setSelectedIndex(0); cbServiceStatus.setSelectedIndex(0); txtServiceSearch.setText(""); tblServices.clearSelection(); selectedServiceId = null;
        refreshData();
    }

    private void refreshServiceTable(ArrayList<Service> list) {
        serviceTableModel.setRowCount(0);
        for (Service s : list) {
            serviceTableModel.addRow(new Object[]{
                    s.getServiceId(), s.getWeddingId(), s.getServiceName(), s.getProvider(),
                    WeddingManager.formatMoney(s.getEstimatedCost()), WeddingManager.formatMoney(s.getActualCost()), s.isRequired() ? "Yes" : "No", s.getStatus()
            });
        }
    }

    // ==========================================
    // VALIDATION & COMBOBOX SYNCHRONIZATION
    // ==========================================

    private void reloadPromptCombo() {
        if (cbPromptWeddingId == null) return;
        cbPromptWeddingId.removeAllItems();
        for (Wedding w : manager.getAllWeddings()) {
            cbPromptWeddingId.addItem(w.getDisplayLabel());
        }
    }

    private void reloadWeddingCombos() {
        reloadWeddingCombo(cbGuestWedding);
        reloadWeddingCombo(cbServiceWedding);
    }

    private void reloadWeddingCombo(JComboBox<String> combo) {
        if (combo == null) return;
        combo.removeAllItems();

        // Lock these dropdowns to ONLY show the currently active wedding ID
        if (globalWeddingFilter != null && !globalWeddingFilter.isEmpty()) {
            Wedding w = manager.searchWedding(globalWeddingFilter);
            if (w != null) combo.addItem(w.getDisplayLabel());
        } else {
            for (Wedding w : manager.getAllWeddings()) {
                combo.addItem(w.getDisplayLabel());
            }
        }
    }

    private void reloadDonorCombo() {
        if (cbGiftDonor == null) return;
        String previous = (String) cbGiftDonor.getSelectedItem();
        cbGiftDonor.removeAllItems();

        // Lock models.Gift Donors to only show guests from the currently active wedding ID
        ArrayList<Guest> guestList = (globalWeddingFilter != null && !globalWeddingFilter.isEmpty()) ?
                manager.getGuestsByWedding(globalWeddingFilter) : manager.getAllGuests();
        for (Guest g : guestList) {
            cbGiftDonor.addItem(g.getDisplayLabel());
        }
        if (previous != null) cbGiftDonor.setSelectedItem(previous);
    }

    private String extractId(JComboBox<String> combo) {
        Object selected = combo.getSelectedItem();
        return selected == null ? "" : selected.toString().split(" - ", 2)[0].trim();
    }

    private void selectComboById(JComboBox<String> combo, String id) {
        String prefix = id + " - ";
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).startsWith(prefix)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private boolean isBlank(JTextField field) { return field.getText() == null || field.getText().trim().isEmpty(); }
    private boolean isNonNegativeDouble(String text) { try { return Double.parseDouble(text.trim()) >= 0; } catch (Exception e) { return false; } }
    private boolean isNonNegativeInt(String text) { try { return Integer.parseInt(text.trim()) >= 0; } catch (Exception e) { return false; } }
    private boolean isValidPhone(String text) {
        if (text == null) return false;
        String t = text.trim();
        if (t.length() < 7 || t.length() > 15) return false;
        for (char c : t.toCharArray()) { if (!Character.isDigit(c)) return false; }
        return true;
    }

    private void showError(String message) { JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE); }
    private void showInfo(String message) { JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private boolean confirmDelete(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
}