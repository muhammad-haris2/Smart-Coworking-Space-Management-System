package com.trinova.scms.view;

import com.trinova.scms.model.Member;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MemberDashboard extends JFrame {

    private final Member member;

    public MemberDashboard(Member member) {
        this.member = member;
        setTitle("SCMS - Member Dashboard");
        setSize(1100, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ── Top bar ──────────────────────────────────────────
        JPanel topBar = UITheme.topBar();

        JLabel titleLabel = new JLabel("⬡  SCMS");
        titleLabel.setForeground(UITheme.TEXT_WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        String planInfo = member.hasActivePlan()
            ? member.getPlanType() + " Plan"
            : "No Plan";
        JLabel welcomeLabel = new JLabel(
            member.getFullName() + "  ·  " + planInfo);
        welcomeLabel.setForeground(UITheme.TEXT_ON_DARK);
        welcomeLabel.setFont(UITheme.FONT_SMALL);

        JButton logoutBtn = UITheme.logoutButton();
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JPanel rightItems = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightItems.setOpaque(false);
        rightItems.add(UITheme.initialsAvatar(member.getFullName(), UITheme.ACCENT));
        rightItems.add(welcomeLabel);
        rightItems.add(logoutBtn);
        // Wrap in GridBagLayout for vertical centering
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(rightItems);
        topBar.add(rightWrapper, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Content panels ───────────────────────────────────
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(UITheme.BG_CONTENT);
        contentPanel.add(buildWelcomePanel(),              "Dashboard");
        contentPanel.add(new SpaceBrowserPanel(member),   "Browse Spaces");
        contentPanel.add(new BookingHistoryPanel(member),  "My Bookings");
        contentPanel.add(new SubscriptionPanel(member),    "Subscription");
        contentPanel.add(new InvoicePanel(member),         "My Invoices");
        contentPanel.add(buildProfilePanel(),              "My Profile");

        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();

        // ── Sidebar ──────────────────────────────────────────
        JPanel sidebar = UITheme.sidebar(UITheme.BG_DARK);

        // Logo area
        JLabel logoLabel = new JLabel("Smart Coworking", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoLabel.setForeground(UITheme.TEXT_MUTED);
        logoLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menuItems = {
            "Dashboard", "Browse Spaces",
            "My Bookings", "Subscription",
            "My Invoices", "My Profile"
        };

        for (int i = 0; i < menuItems.length; i++) {
            String item = menuItems[i];
            JButton btn = UITheme.sidebarButton(
                item, UITheme.BG_DARK, UITheme.ACCENT);
            btn.addActionListener(e ->
                cardLayout.show(contentPanel, item));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel buildWelcomePanel() {
        JPanel outer = UITheme.contentPanel();
        outer.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Greeting card
        JPanel greetCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, UITheme.BG_DARK,
                    getWidth(), getHeight(), new Color(0, 80, 120));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        greetCard.setOpaque(false);
        greetCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        greetCard.setMaximumSize(new Dimension(800, 180));
        greetCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel greetText = new JPanel();
        greetText.setOpaque(false);
        greetText.setLayout(new BoxLayout(greetText, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome back, " + member.getFullName() + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcome.setForeground(Color.WHITE);
        greetText.add(welcome);
        greetText.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel role = new JLabel(
            member.getRole() + "  ·  " + member.getEmail());
        role.setFont(UITheme.FONT_BODY);
        role.setForeground(UITheme.TEXT_ON_DARK);
        greetText.add(role);
        greetText.add(Box.createRigidArea(new Dimension(0, 8)));

        String planMsg = member.hasActivePlan()
            ? "[Active] Plan: " + member.getPlanType() +
              " -- expires " + member.getPlanExpiry()
            : "No active plan -- you are on pay-as-you-go rates.";
        JLabel planLabel = new JLabel(planMsg);
        planLabel.setFont(UITheme.FONT_SMALL);
        planLabel.setForeground(member.hasActivePlan()
            ? UITheme.SUCCESS : UITheme.WARNING);
        greetText.add(planLabel);

        greetCard.add(greetText, BorderLayout.CENTER);
        inner.add(greetCard);
        inner.add(Box.createRigidArea(new Dimension(0, 20)));

        // Info tip
        JLabel info = new JLabel(
            "Use the sidebar to browse spaces, manage bookings, and view invoices.");
        info.setFont(UITheme.FONT_BODY);
        info.setForeground(UITheme.TEXT_SECONDARY);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(info);

        outer.add(inner);
        return outer;
    }

    private JPanel buildProfilePanel() {
        JPanel outer = UITheme.contentPanel();
        outer.setLayout(new GridBagLayout());

        JPanel card = UITheme.cardPanel();
        card.setLayout(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(580, 590));

        // ── Avatar header ────────────────────────────────────
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(10, 0, 16, 0)));

        JLabel avatar = UITheme.initialsAvatar(member.getFullName(), UITheme.ACCENT);
        avatar.setPreferredSize(new Dimension(56, 56));
        avatar.setMinimumSize(new Dimension(56, 56));
        avatar.setMaximumSize(new Dimension(56, 56));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(avatar);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel nameHeader = new JLabel(member.getFullName());
        nameHeader.setFont(UITheme.FONT_SUBTITLE);
        nameHeader.setForeground(UITheme.TEXT_PRIMARY);
        nameHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(nameHeader);

        JLabel emailHeader = new JLabel(member.getEmail());
        emailHeader.setFont(UITheme.FONT_SMALL);
        emailHeader.setForeground(UITheme.TEXT_MUTED);
        emailHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(emailHeader);

        card.add(headerPanel, BorderLayout.NORTH);

        // ── Form body ────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;

        JTextField nameField = UITheme.styledField(30);
        nameField.setText(member.getFullName());
        JTextField emailField = UITheme.styledField(30);
        emailField.setText(member.getEmail());
        emailField.setEditable(false);
        emailField.setBackground(UITheme.BG_CARD_ALT);
        JTextField phoneField = UITheme.styledField(30);
        phoneField.setText(member.getPhone() != null ? member.getPhone() : "");
        JTextField bioField = UITheme.styledField(30);
        bioField.setText(member.getBio() != null ? member.getBio() : "");

        String[] labels = {"FULL NAME", "EMAIL (read-only)", "PHONE", "BIO"};
        JTextField[] fields = {nameField, emailField, phoneField, bioField};

        int row = 0;
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = row++;
            gbc.insets = new Insets(10, 0, 4, 0);
            formPanel.add(UITheme.fieldLabel(labels[i]), gbc);

            gbc.gridy = row++;
            gbc.insets = new Insets(0, 0, 4, 0);
            formPanel.add(fields[i], gbc);
        }

        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(UITheme.SUCCESS);
        statusLabel.setFont(UITheme.FONT_SMALL);
        gbc.gridy = row++;
        gbc.insets = new Insets(12, 0, 4, 0);
        formPanel.add(statusLabel, gbc);

        JButton saveBtn = UITheme.primaryButton("Save Changes");
        gbc.gridy = row;
        gbc.insets = new Insets(4, 0, 0, 0);
        formPanel.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                com.trinova.scms.dao.MemberDAO dao =
                    new com.trinova.scms.dao.MemberDAO();
                member.setFullName(nameField.getText().trim());
                member.setPhone(phoneField.getText().trim());
                member.setBio(bioField.getText().trim());
                dao.updateProfile(member);
                statusLabel.setForeground(UITheme.SUCCESS);
                statusLabel.setText("Profile updated successfully!");
            } catch (Exception ex) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        card.add(formPanel, BorderLayout.CENTER);
        outer.add(card);
        return outer;
    }
}