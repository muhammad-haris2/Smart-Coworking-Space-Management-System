package com.trinova.scms.view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Centralized design-system for all SCMS UI components.
 * Only visual constants and factory helpers — no business logic.
 */
public final class UITheme {

    private UITheme() {}

    /* ── Colour Palette ────────────────────────────────────── */
    // Backgrounds
    public static final Color BG_DARK        = new Color(13, 27, 42);    // #0D1B2A  sidebar / topbar
    public static final Color BG_DARKER      = new Color(10, 20, 33);    // deepest
    public static final Color BG_CONTENT     = new Color(241, 245, 249); // #F1F5F9  main area
    public static final Color BG_CARD        = Color.WHITE;
    public static final Color BG_CARD_ALT    = new Color(248, 250, 252);

    // Admin-specific (purple accent for sidebar)
    public static final Color ADMIN_DARK     = new Color(30, 15, 60);
    public static final Color ADMIN_ACCENT   = new Color(139, 92, 246);  // violet-500

    // Accents
    public static final Color ACCENT         = new Color(0, 180, 216);   // teal #00B4D8
    public static final Color ACCENT_DARK    = new Color(0, 150, 190);
    public static final Color ACCENT_LIGHT   = new Color(202, 240, 248);

    // Semantic
    public static final Color SUCCESS        = new Color(16, 185, 129);  // emerald
    public static final Color SUCCESS_BG     = new Color(220, 252, 231);
    public static final Color DANGER         = new Color(239, 68, 68);   // red-500
    public static final Color DANGER_BG      = new Color(254, 226, 226);
    public static final Color WARNING        = new Color(245, 158, 11);  // amber
    public static final Color WARNING_BG     = new Color(254, 243, 199);

    // Text
    public static final Color TEXT_PRIMARY   = new Color(30, 41, 59);    // slate-800
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139); // slate-500
    public static final Color TEXT_MUTED     = new Color(148, 163, 184); // slate-400
    public static final Color TEXT_ON_DARK   = new Color(226, 232, 240); // slate-200
    public static final Color TEXT_WHITE     = Color.WHITE;

    // Borders
    public static final Color BORDER_LIGHT   = new Color(226, 232, 240); // slate-200
    public static final Color BORDER_MEDIUM  = new Color(203, 213, 225); // slate-300

    // Table
    public static final Color TABLE_HEADER_BG = new Color(30, 41, 59);
    public static final Color TABLE_ROW_ALT   = new Color(248, 250, 252);
    public static final Color TABLE_SELECTED  = new Color(224, 242, 254);

    /* ── Fonts ─────────────────────────────────────────────── */
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADING  = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TINY     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BTN      = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TABLE    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_TABLE_H  = new Font("Segoe UI", Font.BOLD, 13);

    /* ── Dimensions ────────────────────────────────────────── */
    public static final int SIDEBAR_WIDTH  = 220;
    public static final int TOPBAR_HEIGHT  = 56;
    public static final int CARD_RADIUS    = 16;
    public static final int BTN_HEIGHT     = 42;

    /* ── Factory: Buttons ──────────────────────────────────── */

    /** Gradient-painted primary action button. */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, ACCENT, getWidth(), getHeight(), ACCENT_DARK);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(TEXT_WHITE);
        btn.setFont(FONT_BTN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, BTN_HEIGHT));
        return btn;
    }

    /** Red danger button. */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DANGER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(TEXT_WHITE);
        btn.setFont(FONT_BTN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, BTN_HEIGHT));
        return btn;
    }

    /** Ghost (text) button — no background. */
    public static JButton ghostButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_SMALL);
        btn.setForeground(color);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Outline-style secondary button. */
    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(ACCENT);
        btn.setBackground(BG_CARD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1, true),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        return btn;
    }

    /* ── Factory: Input Fields ─────────────────────────────── */

    public static JTextField styledField(int columns) {
        JTextField f = new JTextField(columns);
        f.setFont(FONT_BODY);
        f.setBackground(BG_CARD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setPreferredSize(new Dimension(0, 40));
        return f;
    }

    public static JPasswordField styledPasswordField(int columns) {
        JPasswordField f = new JPasswordField(columns);
        f.setFont(FONT_BODY);
        f.setBackground(BG_CARD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_MEDIUM, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        f.setPreferredSize(new Dimension(0, 40));
        return f;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(TEXT_SECONDARY);
        return l;
    }

    /* ── Factory: Tables ───────────────────────────────────── */

    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(36);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER_LIGHT);
        table.setSelectionBackground(TABLE_SELECTED);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        // Alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus,
                    int row, int col) {
                Component c = super.getTableCellRendererComponent(
                    t, value, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? BG_CARD : TABLE_ROW_ALT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_H);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus,
                    int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                    t, value, sel, focus, row, col);
                l.setBackground(TABLE_HEADER_BG);
                l.setForeground(TEXT_WHITE);
                l.setFont(FONT_TABLE_H);
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                l.setHorizontalAlignment(SwingConstants.LEFT);
                return l;
            }
        });
    }

    public static JScrollPane tableScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1, true));
        sp.getViewport().setBackground(BG_CARD);
        return sp;
    }

    /* ── Factory: Panels / Cards ───────────────────────────── */

    /** Rounded-corner card panel. */
    public static JPanel cardPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), CARD_RADIUS, CARD_RADIUS));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        return p;
    }

    /** Content panel background. */
    public static JPanel contentPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_CONTENT);
        return p;
    }

    /* ── Factory: Top Bars ─────────────────────────────────── */

    /** Dark gradient top bar. */
    public static JPanel topBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                    0, 0, BG_DARK, getWidth(), 0, new Color(20, 40, 70)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, TOPBAR_HEIGHT));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        return bar;
    }

    /** Admin-coloured gradient top bar. */
    public static JPanel adminTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                    0, 0, ADMIN_DARK, getWidth(), 0, new Color(55, 30, 100)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, TOPBAR_HEIGHT));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        return bar;
    }

    /* ── Factory: Sidebars ─────────────────────────────────── */

    public static JPanel sidebar(Color bg) {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(bg);
        sb.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        sb.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return sb;
    }

    /** Sidebar nav button — highlights when hovered. */
    public static JButton sidebarButton(String text, Color bg, Color accentColor) {
        JButton btn = new JButton(text) {
            boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hovered = true; repaint();
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hovered = false; repaint();
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 15));
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_ON_DARK);
        btn.setFocusPainted(false);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 12));
        return btn;
    }

    /* ── Factory: Stat Cards ───────────────────────────────── */

    public static JPanel statCard(String label, String value,
                                   Color accentColor, String iconLetter) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 16, 16));
                // accent left stripe
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(BG_CARD);
        card.setPreferredSize(new Dimension(210, 100));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 16));

        // Painted circle icon with letter
        String letter = (iconLetter != null && !iconLetter.isEmpty())
            ? iconLetter.substring(0, 1).toUpperCase() : "?";
        JLabel iconLbl = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(),
                                      accentColor.getBlue(), 30));
                g2.fillOval(0, 4, 40, 40);
                g2.setColor(accentColor);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(letter, (40 - fm.stringWidth(letter)) / 2,
                    4 + (40 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        iconLbl.setPreferredSize(new Dimension(44, 48));
        card.add(iconLbl, BorderLayout.WEST);

        // Text
        JPanel textP = new JPanel();
        textP.setOpaque(false);
        textP.setLayout(new BoxLayout(textP, BoxLayout.Y_AXIS));
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valLbl.setForeground(TEXT_PRIMARY);
        textP.add(valLbl);
        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(FONT_SMALL);
        lblLbl.setForeground(TEXT_SECONDARY);
        textP.add(lblLbl);
        card.add(textP, BorderLayout.CENTER);

        return card;
    }

    /* ── Factory: Section Title ─────────────────────────────── */

    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SUBTITLE);
        l.setForeground(TEXT_PRIMARY);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return l;
    }

    /* ── Factory: Button Bar ───────────────────────────────── */

    public static JPanel buttonBar(JButton... buttons) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setBackground(BG_CONTENT);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        for (JButton b : buttons) bar.add(b);
        return bar;
    }

    /* ── Factory: Initials Avatar ──────────────────────────── */

    public static JLabel initialsAvatar(String name, Color bg) {
        String initials = "";
        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            initials = String.valueOf(parts[0].charAt(0));
            if (parts.length > 1) initials += parts[parts.length - 1].charAt(0);
            initials = initials.toUpperCase();
        }
        String finalInitials = initials;
        JLabel lbl = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(finalInitials)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(finalInitials, x, y);
                g2.dispose();
            }
        };
        lbl.setPreferredSize(new Dimension(36, 36));
        return lbl;
    }

    /* ── Gradient Background Panel ─────────────────────────── */

    /** Full-frame gradient background (for login/register). */
    public static JPanel gradientBackground() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                    0, 0, BG_DARK,
                    getWidth(), getHeight(), new Color(0, 60, 90)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
    }

    /* ── Logout Button (dark theme) ────────────────────────── */

    public static JButton logoutButton() {
        JButton btn = new JButton("Logout");
        btn.setFont(FONT_SMALL);
        btn.setForeground(TEXT_ON_DARK);
        btn.setBackground(new Color(255, 255, 255, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 60), 1, true),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        return btn;
    }
}
