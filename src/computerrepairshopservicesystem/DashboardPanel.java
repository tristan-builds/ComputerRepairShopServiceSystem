
package computerrepairshopservicesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class DashboardPanel extends JPanel {

    private static final Color BACKGROUND =
            new Color(241, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLUE =
            new Color(48, 105, 183);
    private static final Color TEXT =
            new Color(43, 57, 76);
    private static final Color MUTED =
            new Color(105, 118, 136);
    private static final Color BORDER =
            new Color(216, 224, 234);
    private static final Color LIGHT_BLUE =
            new Color(229, 239, 252);
    private static final Color HEADER_BLUE =
            new Color(43, 78, 120);

    private final DataManager dataManager;
    private final SystemUser authenticatedUser;

    private final JPanel summaryPanel =
            new JPanel(new GridLayout(1, 4, 14, 0));

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                        "Repair Job",
                        "Customer",
                        "Device",
                        "Status"
                    }, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public DashboardPanel(
            DataManager dataManager,
            SystemUser authenticatedUser) {

        this.dataManager = dataManager;
        this.authenticatedUser = authenticatedUser;

        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                28, 32, 28, 32));

        buildLayout();
        refreshDashboard();
    }

    private void buildLayout() {

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(font(Font.BOLD, 25));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Overview of customers and repair activity");
        subtitle.setFont(font(Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel titleStack = new JPanel(new GridBagLayout());
        titleStack.setOpaque(false);

        GridBagConstraints titleC = new GridBagConstraints();
        titleC.gridx = 0;
        titleC.gridy = 0;
        titleC.anchor = GridBagConstraints.WEST;
        titleC.insets = new Insets(0, 0, 5, 0);

        titleStack.add(title, titleC);

        titleC.gridy = 1;
        titleC.insets = new Insets(0, 0, 0, 0);

        titleStack.add(subtitle, titleC);

        top.add(titleStack, BorderLayout.WEST);

        JLabel role = new JLabel(authenticatedUser.getRole());
        role.setFont(font(Font.BOLD, 12));
        role.setForeground(BLUE);
        role.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(
                        7, 12, 7, 12)));
        role.setOpaque(true);
        role.setBackground(WHITE);

        top.add(role, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);

        summaryPanel.setOpaque(false);
        center.add(summaryPanel, BorderLayout.NORTH);

        // ---------------- RECENT REPAIR JOBS ----------------

        JPanel recentPanel = new JPanel(
                new BorderLayout(0, 14));

        recentPanel.setBackground(WHITE);
        recentPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(
                                18, 18, 18, 18)));

        JLabel recentTitle = new JLabel("Recent Repair Jobs");
        recentTitle.setFont(font(Font.BOLD, 18));
        recentTitle.setForeground(TEXT);

        recentPanel.add(recentTitle, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        configureTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(WHITE);

        recentPanel.add(scrollPane, BorderLayout.CENTER);

        center.add(recentPanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    // ---------------- TABLE STYLING ----------------

    private void configureTable(JTable table) {

        table.setFont(font(Font.BOLD, 15));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setRowHeight(40);
        table.setFillsViewportHeight(true);

        // Distinct horizontal and vertical separation lines.
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setGridColor(Color.BLACK);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(LIGHT_BLUE);
        table.setSelectionForeground(TEXT);

        table.setRowMargin(1);
        table.setAutoCreateRowSorter(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(font(Font.BOLD, 14));
        header.setForeground(WHITE);
        header.setBackground(HEADER_BLUE);
        header.setPreferredSize(new Dimension(100, 42));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
    }

    // ---------------- DASHBOARD DATA ----------------

    private void refreshDashboard() {

        List<Customer> customers = dataManager.getCustomers();
        List<RepairJob> jobs = dataManager.getRepairJobs();

        int completedCount = 0;
        int pendingCount = 0;

        for (RepairJob job : jobs) {

            if (RepairJob.COMPLETED.equals(job.getStatus())) {
                completedCount++;
            }

            if (RepairJob.PENDING.equals(job.getStatus())) {
                pendingCount++;
            }
        }

        summaryPanel.removeAll();

        summaryPanel.add(
                createSummaryCard(
                        "Total Customers",
                        customers.size(),
                        BLUE));

        summaryPanel.add(
                createSummaryCard(
                        "Total Repair Jobs",
                        jobs.size(),
                        BLUE));

        summaryPanel.add(
                createSummaryCard(
                        "Completed Jobs",
                        completedCount,
                        new Color(44, 128, 91)));

        summaryPanel.add(
                createSummaryCard(
                        "Pending Jobs",
                        pendingCount,
                        new Color(178, 119, 36)));

        tableModel.setRowCount(0);

        List<RepairJob> recentJobs =
                new ArrayList<>(jobs);

        Collections.reverse(recentJobs);

        int limit = Math.min(8, recentJobs.size());

        for (int i = 0; i < limit; i++) {

            RepairJob job = recentJobs.get(i);

            Customer customer = job.getCustomer();
            Device device = job.getDevice();

            String customerName = customer == null
                    ? "—"
                    : safe(customer.getName());

            String deviceDescription = formatDevice(device);

            tableModel.addRow(new Object[]{
                "RJ-" + job.getRepairJobID(),
                customerName,
                deviceDescription,
                job.getStatus()
            });
        }

        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    // ---------------- SUMMARY CARDS ----------------

    private JPanel createSummaryCard(
            String title,
            int value,
            Color accent) {

        JPanel card = new JPanel(
                new BorderLayout(0, 10));

        card.setBackground(WHITE);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(
                                17, 17, 17, 17)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(font(Font.PLAIN, 13));
        titleLabel.setForeground(MUTED);

        JLabel valueLabel =
                new JLabel(String.valueOf(value));

        valueLabel.setFont(font(Font.BOLD, 27));
        valueLabel.setForeground(accent);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        card.setPreferredSize(new Dimension(160, 105));

        return card;
    }

    // ---------------- DISPLAY HELPERS ----------------

    private String formatDevice(Device device) {

        if (device == null) {
            return "—";
        }

        String type = device instanceof Laptop
                ? "Laptop"
                : device instanceof Desktop
                        ? "Desktop"
                        : "Device";

        String brand = safe(device.getBrand());
        String model = safe(device.getModel());

        String details = (brand + " " + model).trim();

        return details.isEmpty()
                ? type
                : type + " · " + details;
    }

    private String safe(String value) {

        return value == null || value.trim().isEmpty()
                ? "—"
                : value.trim();
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
