package core;


import UI.MainPanel;
import models.User;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.*;
// ... rest of your code

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }

                Color ice = new Color(230, 240, 250);
                Color steel = new Color(70, 130, 180);
                UIManager.put("control", ice);
                UIManager.put("nimbusBase", steel);
                UIManager.put("nimbusLightBackground", Color.WHITE);
                UIManager.put("nimbusSelectionBackground", new Color(100, 150, 200));
                UIManager.put("text", new Color(26, 37, 47));
                UIManager.put("nimbusFocus", steel);
            } catch (Exception e) {
                System.out.println("Could not apply custom theme.");
            }

            JFrame frame = new JFrame(User.APPLICATION_NAME + " - SmartWed Management System");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(1100, 750);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(new Color(230, 240, 250));

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (JOptionPane.showConfirmDialog(frame, "Do you want to exit " + User.APPLICATION_NAME + "?",
                            "Confirm Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            });

            frame.add(new MainPanel(new WeddingManager()));
            frame.setVisible(true);
        });
    }
}