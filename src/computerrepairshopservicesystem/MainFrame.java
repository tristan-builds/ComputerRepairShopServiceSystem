
package computerrepairshopservicesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {

    private static final Color BACKGROUND = new Color(241, 245, 250);
    private static final Color SIDEBAR = new Color(250, 252, 254);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLUE = new Color(48, 105, 183);
    private static final Color RED = new Color(190, 55, 55);
    private static final Color SELECTED = new Color(229, 239, 252);
    private static final Color TEXT = new Color(43, 57, 76);
    private static final Color MUTED = new Color(105, 118, 136);
    private static final Color BORDER = new Color(216, 224, 234);

    private final DataManager dataManager;
    private final SystemUser authenticatedUser;
    private final JPanel contentPanel = new JPanel(new BorderLayout());
    private final Map<String, JButton> navigationButtons = new LinkedHashMap<>();
    private String currentPage = "Dashboard";

    public MainFrame(DataManager dataManager, SystemUser authenticatedUser) {
        if (dataManager == null || authenticatedUser == null) {
            throw new IllegalArgumentException(
                    "A DataManager and authenticated user are required.");
        }

        String activeID = dataManager.getCurrentAuthenticatedUserID();
        if (activeID == null
                || !activeID.equals(authenticatedUser.getUserID())) {
            throw new SecurityException(
                    "The user does not have an active session.");
        }

        this.dataManager = dataManager;
        this.authenticatedUser = authenticatedUser;

        setTitle("Fixwell - " + currentPage);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 680));
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        contentPanel.setBackground(BACKGROUND);
        add(contentPanel, BorderLayout.CENTER);

        showDashboard();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1, BORDER));

        JPanel navigation = new JPanel(new GridBagLayout());
        navigation.setBackground(SIDEBAR);
        navigation.setBorder(
                BorderFactory.createEmptyBorder(20, 12, 20, 12));

        String[] items = {
            "Dashboard",
            "Customers",
            "Devices",
            "Repair Jobs",
            "Technicians",
            "Payments",
            "Users"
        };

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(4, 0, 4, 0);

        int row = 0;

        for (String item : items) {
            JButton button = createNavigationButton(item);
            navigationButtons.put(item, button);
            button.addActionListener(event -> navigateTo(item));

            if ("Users".equals(item)
                    && !SystemUser.ADMIN.equals(
                            authenticatedUser.getRole())) {
                button.setToolTipText(
                        "User management is available to administrators only.");
            }

            c.gridy = row++;
            navigation.add(button, c);
        }

        c.gridy = row;
        c.weighty = 1;
        navigation.add(new JPanel(), c);

        sidebar.add(navigation, BorderLayout.CENTER);

        JButton logoutButton = createNavigationButton("Logout");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(RED);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(event -> logout());

        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setBackground(SIDEBAR);
        logoutPanel.setBorder(
                BorderFactory.createEmptyBorder(8, 12, 16, 12));
        logoutPanel.add(logoutButton, BorderLayout.CENTER);

        sidebar.add(logoutPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(font(Font.PLAIN, 13));
        button.setForeground(TEXT);
        button.setBackground(SIDEBAR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(
                10, 12, 10, 10));
        button.setOpaque(true);

        return button;
    }

    private void navigateTo(String page) {
        if ("Dashboard".equals(page)) {
            showDashboard();
        } else if ("Customers".equals(page)) {
            showCustomers();
        } else if ("Devices".equals(page)) {
            showDevices();
        } else if ("Repair Jobs".equals(page)) {
            showRepairJobs();
        } else if ("Technicians".equals(page)) {
            showTechnicians();
        } else if ("Payments".equals(page)) {
            showPayments();
        } else if ("Users".equals(page)) {
            showUsers();
        } else {
            showNotImplemented(page);
        }
    }

    private void showDashboard() {
        currentPage = "Dashboard";
        setTitle("Fixwell - Dashboard");
        updateNavigationSelection();

        contentPanel.removeAll();
        contentPanel.add(
                new DashboardPanel(dataManager, authenticatedUser),
                BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCustomers() {
        currentPage = "Customers";
        setTitle("Fixwell - Customers");
        updateNavigationSelection();

        contentPanel.removeAll();
        contentPanel.add(
                new CustomerPanel(dataManager, authenticatedUser),
                BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDevices() {
        currentPage = "Devices";
        setTitle("Fixwell - Devices");
        updateNavigationSelection();

        contentPanel.removeAll();
        contentPanel.add(
                new DevicesPanel(dataManager, authenticatedUser),
                BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showTechnicians() {
        currentPage = "Technicians";
        setTitle("CompCare - Technicians");
        updateNavigationSelection();

        contentPanel.removeAll();
        contentPanel.add(
                new TechnicianPanel(dataManager, authenticatedUser),
                BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPayments() {
        currentPage = "Payments";
        setTitle("CompCare - Payments");
        updateNavigationSelection();

        contentPanel.removeAll();
        contentPanel.add(
                new PaymentPanel(dataManager),
                BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showRepairJobs() {
        currentPage = "Repair Jobs";
        setTitle("Fixwell - Repair Jobs");
        updateNavigationSelection();

        contentPanel.removeAll();
        contentPanel.add(
                new RepairJobsPanel(dataManager, authenticatedUser),
                BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUsers() {
        if (!SystemUser.ADMIN.equals(authenticatedUser.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "User management is available to administrators only.",
                    "Access Denied", JOptionPane.WARNING_MESSAGE);
            showDashboard();
            return;
        }

        currentPage = "Users";
        setTitle("Fixwell - Users");
        updateNavigationSelection();

        contentPanel.removeAll();
        contentPanel.add(
                new UsersPanel(dataManager, authenticatedUser),
                BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showNotImplemented(String page) {
        currentPage = page;
        setTitle("Fixwell - " + page);
        updateNavigationSelection();

        contentPanel.removeAll();

        JPanel pagePanel = new JPanel(new GridBagLayout());
        pagePanel.setBackground(BACKGROUND);
        pagePanel.setBorder(BorderFactory.createEmptyBorder(
                30, 34, 30, 34));

        JPanel messageCard = new JPanel(new GridBagLayout());
        messageCard.setBackground(WHITE);
        messageCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(
                        28, 32, 28, 32)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 10, 0);

        JLabel heading = new JLabel(page);
        heading.setFont(font(Font.BOLD, 22));
        heading.setForeground(TEXT);
        messageCard.add(heading, c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);

        JLabel message = new JLabel(
                "This screen has not been implemented yet.");
        message.setFont(font(Font.PLAIN, 14));
        message.setForeground(MUTED);
        messageCard.add(message, c);

        pagePanel.add(messageCard);
        contentPanel.add(pagePanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void updateNavigationSelection() {
        for (Map.Entry<String, JButton> entry
                : navigationButtons.entrySet()) {
            boolean selected = entry.getKey().equals(currentPage);
            JButton button = entry.getValue();

            button.setBackground(selected ? SELECTED : SIDEBAR);
            button.setForeground(selected ? BLUE : TEXT);
            button.setFont(font(
                    selected ? Font.BOLD : Font.PLAIN, 13));
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        dataManager.logout();

        LoginFrame loginFrame = new LoginFrame(dataManager);
        loginFrame.setVisible(true);
        dispose();
    }

    private Font font(int style, int size) {
        String family = Font.SANS_SERIF;

        for (String available : java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames()) {
            if ("Segoe UI".equalsIgnoreCase(available)) {
                family = available;
                break;
            }
        }

        return new Font(family, style, size);
    }
}
