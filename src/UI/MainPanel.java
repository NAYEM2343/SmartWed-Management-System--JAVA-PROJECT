package UI;

import core.WeddingManager;
import models.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class MainPanel extends JPanel {
    private WeddingManager manager;
    private User currentUser;

    // Root Layout for Loading Screen Transition
    private CardLayout rootCardLayout;
    private JProgressBar progressBar;

    private JLabel lblUserInfo;
    private JButton btnLogout;
    private JTabbedPane tabbedPane;

    private FamilyPanel familyPanel;
    private ManagementPanel managementPanel;
    private JPanel mWrap;
    private CardLayout mCard;

    private AdminPanel adminPanel;
    private JPanel aWrap;
    private CardLayout aCard;

    private int lastValidTab = 0;
    private final Color THEME_BG = new Color(230, 240, 250);

    public MainPanel(WeddingManager manager) {
        this.manager = manager;

        // Setup Root Layout
        rootCardLayout = new CardLayout();
        setLayout(rootCardLayout);

        // Add the two main states of the application
        add(createLoadingScreen(), "LOADING");
        add(createMainAppScreen(), "APP");

        // Begin the loading sequence
        startLoadingAnimation();
    }

    // ==========================================
    // LOADING SCREEN LOGIC
    // ==========================================
    private JPanel createLoadingScreen() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(26, 37, 47));

        JLabel title = new JLabel("SmartWed", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 65));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Management System Initializing...", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subtitle.setForeground(new Color(230, 240, 250));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);
        textPanel.setBorder(BorderFactory.createEmptyBorder(250, 0, 0, 0));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setBackground(Color.WHITE);
        progressBar.setPreferredSize(new Dimension(500, 30));

        JPanel progressWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressWrapper.setOpaque(false);
        progressWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 250, 0));
        progressWrapper.add(progressBar);

        p.add(textPanel, BorderLayout.CENTER);
        p.add(progressWrapper, BorderLayout.SOUTH);

        return p;
    }

    private void startLoadingAnimation() {
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(15); // Adjust this to make loading faster or slower
                    final int progress = i;
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Once loading hits 100%, swap the layout card to the main application
            SwingUtilities.invokeLater(() -> rootCardLayout.show(this, "APP"));
        }).start();
    }

    // ==========================================
    // MAIN APPLICATION LOGIC
    // ==========================================
    private JPanel createMainAppScreen() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(THEME_BG);
        p.add(createHeaderPanel(), BorderLayout.NORTH);
        p.add(createTabbedPane(), BorderLayout.CENTER);
        return p;
    }

    private JPanel createHeaderPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(26, 37, 47));
        p.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel title = new JLabel("SmartWed Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);

        lblUserInfo = new JLabel("Public Family View");
        lblUserInfo.setForeground(THEME_BG);

        btnLogout = new JButton("Logout");
        btnLogout.setVisible(false);
        btnLogout.addActionListener(e -> {
            currentUser = null;
            lblUserInfo.setText("Public Family View");
            btnLogout.setVisible(false);

            managementPanel.resetPortalToHome();

            mCard.show(mWrap, "LOCKED");
            aCard.show(aWrap, "LOCKED");
            tabbedPane.setSelectedIndex(lastValidTab = 0);
        });

        right.add(lblUserInfo);
        right.add(btnLogout);
        p.add(title, BorderLayout.CENTER);
        p.add(right, BorderLayout.EAST);

        return p;
    }

    private JTabbedPane createTabbedPane() {
        familyPanel = new FamilyPanel(manager);
        managementPanel = new ManagementPanel(manager);
        adminPanel = new AdminPanel(manager);

        mCard = new CardLayout();
        mWrap = new JPanel(mCard);
        mWrap.setBackground(THEME_BG);
        mWrap.add(createLockedPanel("🔒 Please log in to access the Management Portal."), "LOCKED");
        mWrap.add(managementPanel, "UNLOCKED");

        aCard = new CardLayout();
        aWrap = new JPanel(aCard);
        aWrap.setBackground(THEME_BG);
        aWrap.add(createLockedPanel("🔒 Please log in to access the Admin Dashboard."), "LOCKED");
        aWrap.add(adminPanel, "UNLOCKED");

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.addTab(" Family Dashboard ", familyPanel);
        tabbedPane.addTab(" Management Portal ", mWrap);
        tabbedPane.addTab(" Admin Dashboard ", aWrap);

        tabbedPane.addChangeListener(e -> {
            int i = tabbedPane.getSelectedIndex();
            if ((i == 1 || i == 2) && !hasAccess(i)) {
                SwingUtilities.invokeLater(() -> {
                    if (!showLoginDialog(i)) {
                        tabbedPane.setSelectedIndex(lastValidTab);
                    } else {
                        lastValidTab = i;
                        unlockAllowedTabs();
                        refreshSelectedTab();
                    }
                });
                return;
            }
            lastValidTab = i;
            refreshSelectedTab();
        });

        return tabbedPane;
    }

    private JPanel createLockedPanel(String msg) {
        JPanel p = new JPanel(new GridBagLayout()) {
            private BufferedImage bgImage;

            {
                try {
                    File f = new File("wedding_bg.jpg");
                    if (f.exists()) bgImage = ImageIO.read(f);
                } catch (Exception ex) {
                    System.out.println("Could not load image");
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                    g.setColor(new Color(230, 240, 250, 200));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    setBackground(THEME_BG);
                }
            }
        };

        JPanel txtBox = new JPanel();
        txtBox.setBackground(new Color(255, 255, 255, 220));
        txtBox.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel lbl = new JLabel(msg);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(new Color(70, 130, 180));

        txtBox.add(lbl);
        p.add(txtBox);
        return p;
    }

    private boolean hasAccess(int tabIndex) {
        if (currentUser == null) return false;
        String r = currentUser.getRole();
        return r.equals("Admin") || (r.equals("Manager") && tabIndex == 1);
    }

    private void unlockAllowedTabs() {
        if (currentUser == null) return;
        mCard.show(mWrap, "UNLOCKED");
        aCard.show(aWrap, currentUser.getRole().equals("Admin") ? "UNLOCKED" : "LOCKED");
    }

    private void refreshSelectedTab() {
        int i = tabbedPane.getSelectedIndex();
        if (i == 0) familyPanel.refreshData();
        else if (i == 1) managementPanel.refreshData();
        else if (i == 2) adminPanel.refreshData();
    }

    private boolean showLoginDialog(int tab) {
        JTextField u = new JTextField();
        JPasswordField p = new JPasswordField();
        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));

        form.add(new JLabel("Username:"));
        form.add(u);
        form.add(new JLabel("Password:"));
        form.add(p);
        form.add(new JLabel("Login Option: "));
        form.add(new JLabel("admin / manager "));

        if (JOptionPane.showConfirmDialog(this, form, "SmartWed Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {

            User user = manager.authenticate(u.getText().trim(), new String(p.getPassword()));

            if (user != null) {
                String role = user.getRole();
                if (tab == 2 && !role.equals("Admin")) {
                    JOptionPane.showMessageDialog(this, "Access Denied. Admin privileges required.", "Unauthorized", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                if (tab == 1 && role.equals("Family")) {
                    JOptionPane.showMessageDialog(this, "Access Denied. Staff privileges required.", "Unauthorized", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                currentUser = user;
                lblUserInfo.setText("Signed in: " + user.getName() + "  |  " + user.getRoleDescription());
                btnLogout.setVisible(true);
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
}