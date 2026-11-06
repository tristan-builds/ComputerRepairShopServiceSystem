package computerrepairshopservicesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class PaymentPanel extends JPanel {

    private static final Color BACKGROUND = new Color(241, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLUE = new Color(48, 105, 183);
    private static final Color TEXT = new Color(43, 57, 76);
    private static final Color MUTED = new Color(105, 118, 136);
    private static final Color BORDER = new Color(216, 224, 234);
    private static final Color LIGHT_BLUE = new Color(229, 239, 252);
    private static final Color HEADER_BLUE = new Color(43, 78, 120);

    private final DataManager dataManager;

    private JComboBox<RepairJobOption> repairJobComboBox;
    private JLabel customerValueLabel;
    private JLabel deviceValueLabel;
    private JLabel amountValueLabel;

    private JLabel totalPaymentsValueLabel;
    private JLabel pendingPaymentsValueLabel;

    private JTable paymentsTable;
    private DefaultTableModel tableModel;

    private JButton createPaymentButton;
    private JButton confirmPaymentButton;
    private JButton refreshButton;

    private List<RepairJob> availableRepairJobs = new ArrayList<>();

    public PaymentPanel(DataManager dataManager) {
        this.dataManager = dataManager;

        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        buildUI();
        refreshData();
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BACKGROUND);

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(BACKGROUND);

        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setBackground(BACKGROUND);

        topPanel.add(createSummaryPanel(), BorderLayout.NORTH);
        topPanel.add(createPaymentForm(), BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(createPaymentsTablePanel(), BorderLayout.CENTER);

        // Make the content accessible on shorter laptop screens.
        JScrollPane contentScrollPane = new JScrollPane(contentPanel);
        contentScrollPane.setBorder(null);
        contentScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.getViewport().setBackground(BACKGROUND);
        contentScrollPane.getViewport().setOpaque(true);

        mainPanel.add(contentScrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel("Payments");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel(
                "Record payments and manage completed repair transactions."
        );
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(MUTED);

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBackground(BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        textPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        textPanel.add(subtitleLabel, gbc);

        panel.add(textPanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);

        totalPaymentsValueLabel = new JLabel("0");
        pendingPaymentsValueLabel = new JLabel("0");

        JPanel totalCard = createSummaryCard(
                "Total Payments",
                totalPaymentsValueLabel
        );

        JPanel pendingCard = createSummaryCard(
                "Pending Payments",
                pendingPaymentsValueLabel
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 12);

        gbc.gridx = 0;
        panel.add(totalCard, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(pendingCard, gbc);

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(MUTED);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 27));
        valueLabel.setForeground(BLUE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(valueLabel, gbc);

        return card;
    }

    private JPanel createPaymentForm() {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)
        ));

        JLabel titleLabel = new JLabel("Create Payment");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(
                "Select a completed repair job that does not have a payment record."
        );
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descriptionLabel.setForeground(MUTED);

        JPanel headingPanel = new JPanel(new GridBagLayout());
        headingPanel.setBackground(WHITE);

        GridBagConstraints headingGbc = new GridBagConstraints();
        headingGbc.gridx = 0;
        headingGbc.gridy = 0;
        headingGbc.anchor = GridBagConstraints.WEST;
        headingGbc.insets = new Insets(0, 0, 5, 0);
        headingPanel.add(titleLabel, headingGbc);

        headingGbc.gridy = 1;
        headingGbc.insets = new Insets(0, 0, 0, 0);
        headingPanel.add(descriptionLabel, headingGbc);

        card.add(headingPanel, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(WHITE);

        repairJobComboBox = new JComboBox<>();
        repairJobComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        repairJobComboBox.setPreferredSize(new Dimension(300, 36));
        repairJobComboBox.addActionListener(e -> updateSelectedJobDetails());

        customerValueLabel = createValueLabel("—");
        deviceValueLabel = createValueLabel("—");
        amountValueLabel = createValueLabel("₱0.00");

        addFormField(fieldsPanel, "Completed Repair Job", repairJobComboBox, 0, 0);
        addFormField(fieldsPanel, "Customer", customerValueLabel, 1, 0);
        addFormField(fieldsPanel, "Device", deviceValueLabel, 2, 0);
        addFormField(fieldsPanel, "Final Cost / Payment Amount", amountValueLabel, 3, 0);

        card.add(fieldsPanel, BorderLayout.CENTER);

        createPaymentButton = createButton("Create Payment", BLUE);
        createPaymentButton.addActionListener(e -> createPayment());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(WHITE);
        buttonPanel.add(createPaymentButton);

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private void addFormField(
            JPanel panel,
            String fieldName,
            java.awt.Component fieldComponent,
            int row,
            int column
    ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 18);
        gbc.weightx = 0.0;

        JLabel label = new JLabel(fieldName);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXT);

        panel.add(label, gbc);

        gbc.gridx = column + 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);

        panel.add(fieldComponent, gbc);
    }

    private JLabel createValueLabel(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(TEXT);
        return label;
    }

    private JPanel createPaymentsTablePanel() {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)
        ));

        JLabel titleLabel = new JLabel("Payment Records");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT);

        refreshButton = createButton("Refresh", HEADER_BLUE);
        refreshButton.addActionListener(e -> refreshData());

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setBackground(WHITE);
        headingPanel.add(titleLabel, BorderLayout.WEST);
        headingPanel.add(refreshButton, BorderLayout.EAST);

        card.add(headingPanel, BorderLayout.NORTH);

        String[] columns = {
            "Payment ID",
            "Repair Job",
            "Customer",
            "Amount",
            "Payment Date",
            "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentsTable = new JTable(tableModel);
        paymentsTable.setFont(new Font("SansSerif", Font.BOLD, 15));
        paymentsTable.setRowHeight(40);
        paymentsTable.setBackground(WHITE);
        paymentsTable.setForeground(TEXT);
        paymentsTable.setSelectionBackground(LIGHT_BLUE);
        paymentsTable.setSelectionForeground(TEXT);
        paymentsTable.setGridColor(Color.BLACK);
        paymentsTable.setShowGrid(true);
        paymentsTable.setIntercellSpacing(new Dimension(1, 1));
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentsTable.setAutoCreateRowSorter(true);
        paymentsTable.setFillsViewportHeight(true);

        paymentsTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        paymentsTable.getTableHeader().setBackground(HEADER_BLUE);
        paymentsTable.getTableHeader().setForeground(WHITE);
        paymentsTable.getTableHeader().setPreferredSize(
                new Dimension(0, 42)
        );
        paymentsTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(WHITE);

        // Keep the table large enough to be useful while allowing it to shrink
        // within the outer scroll pane on a laptop screen.
        scrollPane.setPreferredSize(new Dimension(0, 260));

        card.add(scrollPane, BorderLayout.CENTER);

        confirmPaymentButton = createButton(
                "Confirm Payment Received",
                BLUE
        );
        confirmPaymentButton.addActionListener(e -> confirmPaymentReceived());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setBackground(WHITE);
        actionPanel.add(confirmPaymentButton);

        card.add(actionPanel, BorderLayout.SOUTH);

        return card;
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        return button;
    }

    private void refreshData() {
        try {
            String actorUserID = dataManager.getCurrentAuthenticatedUserID();

            if (actorUserID == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Your session has expired. Please log in again.",
                        "Session Expired",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            List<RepairJob> repairJobs = dataManager.getRepairJobs();
            List<Payment> payments = dataManager.getPayments();

            populateAvailableRepairJobs(repairJobs, payments);
            populatePaymentsTable(payments);
            updateSummary(payments);

        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "You are not authorized to view payment records.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load payment records: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void populateAvailableRepairJobs(
            List<RepairJob> repairJobs,
            List<Payment> payments
    ) {
        Integer previouslySelectedID = null;
        RepairJobOption previousOption =
                (RepairJobOption) repairJobComboBox.getSelectedItem();

        if (previousOption != null) {
            previouslySelectedID = previousOption.getRepairJob().getRepairJobID();
        }

        List<RepairJob> eligibleJobs = new ArrayList<>();

        for (RepairJob job : repairJobs) {
            if (!RepairJob.COMPLETED.equals(job.getStatus())) {
                continue;
            }

            boolean paymentExists = false;

            for (Payment payment : payments) {
                if (payment.getRepairJob().getRepairJobID()
                        == job.getRepairJobID()) {
                    paymentExists = true;
                    break;
                }
            }

            if (!paymentExists) {
                eligibleJobs.add(job);
            }
        }

        availableRepairJobs = eligibleJobs;

        repairJobComboBox.removeAllItems();

        RepairJobOption optionToRestore = null;

        for (RepairJob job : availableRepairJobs) {
            RepairJobOption option = new RepairJobOption(job);
            repairJobComboBox.addItem(option);

            if (previouslySelectedID != null
                    && job.getRepairJobID() == previouslySelectedID) {
                optionToRestore = option;
            }
        }

        if (optionToRestore != null) {
            repairJobComboBox.setSelectedItem(optionToRestore);
        } else if (repairJobComboBox.getItemCount() > 0) {
            repairJobComboBox.setSelectedIndex(0);
        }

        boolean hasEligibleJobs = repairJobComboBox.getItemCount() > 0;
        createPaymentButton.setEnabled(hasEligibleJobs);

        if (!hasEligibleJobs) {
            customerValueLabel.setText("No eligible repair jobs");
            deviceValueLabel.setText("—");
            amountValueLabel.setText("₱0.00");
        } else {
            updateSelectedJobDetails();
        }
    }

    private void updateSelectedJobDetails() {
        if (repairJobComboBox == null
                || customerValueLabel == null
                || deviceValueLabel == null
                || amountValueLabel == null) {
            return;
        }

        RepairJobOption selectedOption =
                (RepairJobOption) repairJobComboBox.getSelectedItem();

        if (selectedOption == null) {
            customerValueLabel.setText("No eligible repair jobs");
            deviceValueLabel.setText("—");
            amountValueLabel.setText("₱0.00");
            return;
        }

        RepairJob job = selectedOption.getRepairJob();

        customerValueLabel.setText(job.getCustomer().getName());
        deviceValueLabel.setText(formatDevice(job.getDevice()));
        amountValueLabel.setText(formatCurrency(job.getFinalCost()));
    }

    private void populatePaymentsTable(List<Payment> payments) {
        tableModel.setRowCount(0);

        for (Payment payment : payments) {
            RepairJob job = payment.getRepairJob();

            String customerName = job.getCustomer().getName();

            tableModel.addRow(new Object[]{
                payment.getPaymentID(),
                job.getRepairJobID(),
                customerName,
                formatCurrency(payment.getAmountPaid()),
                payment.getPaymentDate(),
                payment.getPaymentStatus()
            });
        }
    }

    private void updateSummary(List<Payment> payments) {
        int totalPayments = payments.size();
        int pendingPayments = 0;

        for (Payment payment : payments) {
            if (Payment.PENDING.equals(payment.getPaymentStatus())) {
                pendingPayments++;
            }
        }

        totalPaymentsValueLabel.setText(String.valueOf(totalPayments));
        pendingPaymentsValueLabel.setText(String.valueOf(pendingPayments));
    }

    private void createPayment() {
        RepairJobOption selectedOption =
                (RepairJobOption) repairJobComboBox.getSelectedItem();

        if (selectedOption == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "There are no completed repair jobs available for payment.",
                    "No Eligible Repair Jobs",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        RepairJob job = selectedOption.getRepairJob();

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Create a payment record for Repair Job #"
                        + job.getRepairJobID()
                        + "?\n\nCustomer: " + job.getCustomer().getName()
                        + "\nAmount: " + formatCurrency(job.getFinalCost())
                        + "\nPayment Date: " + LocalDate.now()
                        + "\n\nThe payment will initially be marked PENDING.",
                "Create Payment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String actorUserID = dataManager.getCurrentAuthenticatedUserID();

            if (actorUserID == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Your session has expired. Please log in again.",
                        "Session Expired",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            dataManager.createPayment(
                    actorUserID,
                    job.getRepairJobID(),
                    job.getFinalCost(),
                    LocalDate.now()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Payment record created successfully.\n"
                            + "Status: PENDING",
                    "Payment Created",
                    JOptionPane.INFORMATION_MESSAGE
            );

            refreshData();

        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "You are not authorized to create a payment.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to create payment: " + ex.getMessage(),
                    "Payment Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void confirmPaymentReceived() {
        int selectedRow = paymentsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a payment record first.",
                    "No Payment Selected",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int modelRow = paymentsTable.convertRowIndexToModel(selectedRow);

        String paymentID = tableModel.getValueAt(modelRow, 0).toString();
        String status = tableModel.getValueAt(modelRow, 5).toString();

        if (!Payment.PENDING.equals(status)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only PENDING payments can be confirmed.\n"
                            + "Selected payment status: " + status,
                    "Payment Cannot Be Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String amount = tableModel.getValueAt(modelRow, 3).toString();

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Confirm that the full payment has been received?\n\n"
                        + "Payment ID: " + paymentID
                        + "\nAmount: " + amount
                        + "\n\nThis action will mark the payment as PAID.",
                "Confirm Payment Received",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String actorUserID = dataManager.getCurrentAuthenticatedUserID();

            if (actorUserID == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Your session has expired. Please log in again.",
                        "Session Expired",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            boolean success = dataManager.markPaymentPaid(
                    actorUserID,
                    paymentID
            );

            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Payment marked as PAID successfully.",
                        "Payment Confirmed",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "The payment could not be confirmed. "
                                + "Check the repair job status and payment amount.",
                        "Payment Not Confirmed",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            refreshData();

        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "You are not authorized to confirm this payment.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to confirm payment: " + ex.getMessage(),
                    "Payment Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String formatDevice(Device device) {
        String type = device instanceof Laptop
                ? "Laptop"
                : device instanceof Desktop
                    ? "Desktop"
                    : "Device";

        String brand = device.getBrand() == null
                ? ""
                : device.getBrand().trim();

        String model = device.getModel() == null
                ? ""
                : device.getModel().trim();

        String details = (brand + " " + model).trim();

        return details.isEmpty() ? type : type + " - " + details;
    }

    private String formatCurrency(double amount) {
        return String.format("₱%,.2f", amount);
    }

    private static class RepairJobOption {

        private final RepairJob repairJob;

        public RepairJobOption(RepairJob repairJob) {
            this.repairJob = repairJob;
        }

        public RepairJob getRepairJob() {
            return repairJob;
        }

        @Override
        public String toString() {
            return "Job #" + repairJob.getRepairJobID()
                    + " - " + repairJob.getCustomer().getName()
                    + " - " + String.format(
                            "₱%,.2f",
                            repairJob.getFinalCost()
                    );
        }
    }
}  
