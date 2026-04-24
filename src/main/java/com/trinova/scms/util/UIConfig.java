package com.trinova.scms.util;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UIConfig {

    // Colors from images
    public static final Color PURPLE_DARK = new Color(60, 30, 100);    // Admin Header
    public static final Color PURPLE_LIGHT = new Color(245, 240, 252); // Admin Sidebar
    public static final Color NAVY_DARK = new Color(16, 64, 110);      // User Header
    public static final Color TEXT_DARK = new Color(50, 50, 50);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color ACCENT_GREEN = new Color(0, 128, 0);
    public static final Color ACCENT_ORANGE = new Color(180, 80, 0);
    public static final Color ACCENT_PURPLE = new Color(120, 80, 200);

    // Fonts
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    public static void setup() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // Global overrides
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("CheckBox.arc", 4);
            UIManager.put("ScrollBar.trackArc", 999);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackInsets", new Insets(2, 4, 2, 4));
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            
            // Table styling
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
            UIManager.put("Table.selectionBackground", new Color(240, 245, 255));
            UIManager.put("Table.selectionForeground", TEXT_DARK);
            UIManager.put("TableHeader.font", FONT_BODY.deriveFont(Font.BOLD));
            UIManager.put("TableHeader.background", Color.WHITE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void styleSidebarButton(JButton btn) {
        btn.setFont(FONT_BODY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setForeground(TEXT_DARK);
        
        // Hover effect using FlatLaf client properties
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
    }

    public static JPanel createCard(Color borderColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderColor, 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        // Soft shadow (simulated with border if needed, but FlatLaf helps)
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: #ffffff");
        return card;
    }

    public static void stylePrimaryButton(JButton btn) {
        btn.setBackground(PURPLE_DARK);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BODY.deriveFont(Font.BOLD));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
    }

    public static void styleSecondaryButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(PURPLE_DARK);
        btn.setBorder(new LineBorder(PURPLE_DARK, 1, true));
        btn.setFont(FONT_BODY.deriveFont(Font.BOLD));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
    }
}
