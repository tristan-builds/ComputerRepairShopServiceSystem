package computerrepairshopservicesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class RepairJobsPanel extends JPanel {

    private static final Color BACKGROUND = new Color(241, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLUE = new Color(48, 105, 183);
    private static final Color TEXT = new Color(43, 57, 76);
    private static final Color MUTED = new Color(105, 118, 136);
    private static final Color BORDER = new Color(216, 224, 234);
    private static final Color LIGHT_BLUE = new Color(229, 239, 252);
    private static final Color HEADER_BLUE = new Color(43, 78, 120);

    private final DataManager dataManager;
    private final SystemUser authenticatedUser;

    private final JComboBox<Customer> customerCombo = new JComboBox<>();
    private final JComboBox<Device> deviceCombo = new JComboBox<>();
    private final JTextArea reportedProblemArea = new JTextArea(3, 24);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{
                "Job ID",
                "Customer",
                "Device",
                "Technician",
                "Reported Problem",
                "Estimated Cost",
                "Final Cost",
                "Status"
            }, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable jobsTable = new JTable(tableModel);

    private final DefaultTableModel queueTableModel = new DefaultTableModel(
            new Object[]{
                "Position",
                "Job ID",
                "Customer",
                "Device",
                "Reported Problem"
            }, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable queueTable = new JTable(queueTableModel);

    public RepairJobsPanel(
            DataManager dataManager,
            SystemUser authenticatedUser) {

        if (dataManager == null || authenticatedUser == null) {
            throw new IllegalArgumentException(
                    "A DataManager and authenticated user are required.");
        }

        this.dataManager = dataManager;
        this.authenticatedUser = authenticatedUser;

        setLayout(new BorderLayout(0, 16));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        add(createHeader(), BorderLayout.NORTH);

        
        JPanel mainContent = createMainContent();

        JScrollPane contentScrollPane = new JScrollPane(mainContent);
        contentScrollPane.setBorder(null);
        contentScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.getViewport().setBackground(BACKGROUND);
        contentScrollPane.getViewport().setOpaque(true);

        add(contentScrollPane, BorderLayout.CENTER);

        configureTable();
        configureComboRenderers();
        refreshAll();
    }

   

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Repair Jobs");
        title.setFont(font(Font.BOLD, 25));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Register jobs, start the queue, and track repair progress.");
        subtitle.setFont(font(Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        JPanel titles = new JPanel(new GridBagLayout());
        titles.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 5, 0);
        titles.add(title, c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        titles.add(subtitle, c);

        JButton refreshButton = createButton("Refresh", false);
        refreshButton.addActionListener(event -> refreshAll());

        header.add(titles, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        return header;
    }

    

    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);

        content.add(createCreateJobCard(), BorderLayout.NORTH);

        JPanel recordsArea = new JPanel(new BorderLayout(0, 12));
        recordsArea.setOpaque(false);

        recordsArea.add(createQueueCard(), BorderLayout.NORTH);
        recordsArea.add(createJobsCard(), BorderLayout.CENTER);

        content.add(recordsArea, BorderLayout.CENTER);
        content.add(createActionPanel(), BorderLayout.SOUTH);

        return content;
    }

  

    private JPanel createCreateJobCard() {
        JPanel card = createCard();
        card.setLayout(new GridBagLayout());

        JLabel heading = new JLabel("Create Repair Job");
        heading.setFont(font(Font.BOLD, 18));
        heading.setForeground(TEXT);

        reportedProblemArea.setFont(font(Font.PLAIN, 14));
        reportedProblemArea.setForeground(TEXT);
        reportedProblemArea.setCaretColor(TEXT);
        reportedProblemArea.setLineWrap(true);
        reportedProblemArea.setWrapStyleWord(true);
        reportedProblemArea.setBorder(
                BorderFactory.createEmptyBorder(7, 8, 7, 8));
        reportedProblemArea.setBackground(WHITE);

        JScrollPane problemScroll = new JScrollPane(reportedProblemArea);
        problemScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        problemScroll.setPreferredSize(new Dimension(300, 72));

        styleComboBox(customerCombo);
        styleComboBox(deviceCombo);

        JButton createButton = createButton("Create Job", true);
        createButton.addActionListener(event -> createRepairJob());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 5, 6, 12);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 4;
        c.weightx = 1;
        card.add(heading, c);

        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        c.weightx = 0;
        card.add(label("Customer"), c);

        c.gridx = 1;
        c.weightx = 1;
        card.add(customerCombo, c);

        c.gridx = 2;
        c.weightx = 0;
        card.add(label("Device"), c);

        c.gridx = 3;
        c.weightx = 1;
        card.add(deviceCombo, c);

        c.gridy = 2;
        c.gridx = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        card.add(label("Reported Problem"), c);

        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        card.add(problemScroll, c);

        c.gridx = 3;
        c.gridwidth = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        card.add(createButton, c);

        return card;
    }


    private JPanel createQueueCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 10));

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setOpaque(false);

        JLabel heading = new JLabel("Waiting Queue");
        heading.setFont(font(Font.BOLD, 18));
        heading.setForeground(TEXT);

        JLabel hint = new JLabel(
                "Oldest pending job is first; a technician is assigned when started.");
        hint.setFont(font(Font.PLAIN, 13));
        hint.setForeground(MUTED);

        headingPanel.add(heading, BorderLayout.WEST);
        headingPanel.add(hint, BorderLayout.EAST);

        configureTable(queueTable);

        JScrollPane scroll = new JScrollPane(queueTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        scroll.setPreferredSize(new Dimension(600, 125));

        card.add(headingPanel, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel createJobsCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 12));

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setOpaque(false);

        JLabel heading = new JLabel("Repair Job Records");
        heading.setFont(font(Font.BOLD, 18));
        heading.setForeground(TEXT);

        JLabel hint = new JLabel("Select a row to perform an action.");
        hint.setFont(font(Font.PLAIN, 13));
        hint.setForeground(MUTED);

        headingPanel.add(heading, BorderLayout.WEST);
        headingPanel.add(hint, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(jobsTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        scroll.setPreferredSize(new Dimension(600, 280));

        card.add(headingPanel, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }


    private void configureTable() {
        configureTable(jobsTable);
    }

    private void configureTable(JTable table) {
        table.setFont(font(Font.BOLD, 15));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setRowHeight(40);
        table.setFillsViewportHeight(true);

        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setGridColor(Color.BLACK);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(LIGHT_BLUE);
        table.setSelectionForeground(TEXT);

        JTableHeader header = table.getTableHeader();
        header.setFont(font(Font.BOLD, 14));
        header.setForeground(WHITE);
        header.setBackground(HEADER_BLUE);
        header.setPreferredSize(new Dimension(100, 42));
        header.setReorderingAllowed(false);
    }


    private JPanel createActionPanel() {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        JButton assignButton = createButton("Start Next in Queue", true);
        assignButton.addActionListener(event -> assignNextJob());

        JButton diagnosisButton = createButton(
                "Diagnosis & Estimate", false);
        diagnosisButton.addActionListener(event -> submitDiagnosis());

        JButton approveButton = createButton(
                "Approve Estimate", false);
        approveButton.addActionListener(
                event -> decideEstimate(true));

        JButton rejectButton = createButton(
                "Reject Estimate", false);
        rejectButton.addActionListener(
                event -> decideEstimate(false));

        JButton completeButton = createButton(
                "Complete Repair", false);
        completeButton.addActionListener(event -> completeRepair());

        JButton deleteButton = createButton(
                "Delete Pending Job", false);
        deleteButton.setForeground(new Color(153, 67, 67));
        deleteButton.addActionListener(event -> deleteSelectedJob());

        panel.add(assignButton);
        panel.add(diagnosisButton);
        panel.add(approveButton);
        panel.add(rejectButton);
        panel.add(completeButton);
        panel.add(deleteButton);

        return panel;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(font(Font.PLAIN, 14));
        comboBox.setForeground(TEXT);
        comboBox.setBackground(WHITE);
        comboBox.setPreferredSize(new Dimension(220, 36));
    }

    private void configureComboRenderers() {
        ListCellRenderer<? super Customer> customerRenderer =
                new javax.swing.DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel cell = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                cell.setFont(font(Font.PLAIN, 14));

                if (value instanceof Customer) {
                    Customer customer = (Customer) value;

                    cell.setText(customer.getCustomerID()
                            + " - " + customer.getName());
                } else {
                    cell.setText(value == null
                            ? "Select customer" : value.toString());
                }

                return cell;
            }
        };

        customerCombo.setRenderer(customerRenderer);

        ListCellRenderer<? super Device> deviceRenderer =
                new javax.swing.DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JLabel cell = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                cell.setFont(font(Font.PLAIN, 14));

                if (value instanceof Device) {
                    Device device = (Device) value;

                    cell.setText(device.getDeviceID()
                            + " - " + device.getBrand()
                            + " " + device.getModel()
                            + (device instanceof Laptop
                                    ? " (Laptop)" : " (Desktop)"));
                } else {
                    cell.setText(value == null
                            ? "Select device" : value.toString());
                }

                return cell;
            }
        };

        deviceCombo.setRenderer(deviceRenderer);
    }


    private void refreshAll() {
        refreshDropdowns();
        refreshWaitingQueueTable();
        refreshJobsTable();
    }

    private void refreshDropdowns() {
        try {
            DefaultComboBoxModel<Customer> customerModel =
                    new DefaultComboBoxModel<>();

            for (Customer customer : dataManager.getCustomers()) {
                customerModel.addElement(customer);
            }

            customerCombo.setModel(customerModel);

            DefaultComboBoxModel<Device> deviceModel =
                    new DefaultComboBoxModel<>();

            for (Device device : dataManager.getDevices()) {
                deviceModel.addElement(device);
            }

            deviceCombo.setModel(deviceModel);

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void refreshWaitingQueueTable() {
        queueTableModel.setRowCount(0);

        try {
            int position = 1;

            for (RepairJob job : dataManager.getWaitingQueue()) {
                Customer customer = job.getCustomer();
                Device device = job.getDevice();

                queueTableModel.addRow(new Object[]{
                    position++,
                    job.getRepairJobID(),
                    customer == null ? "-" : customer.getName(),
                    device == null
                            ? "-"
                            : device.getDeviceID() + " "
                                    + device.getBrand() + " "
                                    + device.getModel(),
                    shorten(job.getReportedProblem(), 55)
                });
            }

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void refreshJobsTable() {
        tableModel.setRowCount(0);

        try {
            for (RepairJob job : dataManager.getRepairJobs()) {
                Customer customer = job.getCustomer();
                Device device = job.getDevice();
                Technician technician = job.getTechnician();

                tableModel.addRow(new Object[]{
                    job.getRepairJobID(),
                    customer == null ? "-" : customer.getName(),
                    device == null
                            ? "-"
                            : device.getDeviceID() + " "
                                    + device.getBrand() + " "
                                    + device.getModel(),
                    technician == null ? "-" : technician.getName(),
                    shorten(job.getReportedProblem(), 45),
                    money(job.getEstimatedCost()),
                    money(job.getFinalCost()),
                    job.getStatus()
                });
            }

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    // ---------------- CREATE REPAIR JOB ----------------

    private void createRepairJob() {
        Customer customer =
                (Customer) customerCombo.getSelectedItem();
        Device device = (Device) deviceCombo.getSelectedItem();
        String problem = reportedProblemArea.getText().trim();

        if (customer == null || device == null) {
            showMessage(
                    "Add at least one customer and one device before creating a repair job.",
                    "Missing Records",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (problem.isEmpty()) {
            showMessage(
                    "Enter the reported problem.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            RepairJob created = dataManager.createRepairJob(
                    authenticatedUser.getUserID(),
                    customer.getCustomerID(),
                    device.getDeviceID(),
                    problem);

            reportedProblemArea.setText("");
            refreshAll();
            selectJob(created.getRepairJobID());

            showMessage(
                    "Repair job #" + created.getRepairJobID()
                            + " was added to the waiting queue. "
                            + "A technician will be assigned when it starts.",
                    "Repair Job Created",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    // ---------------- START NEXT JOB ----------------

    private void assignNextJob() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Start the oldest pending job in queue order? "
                        + "An available technician will be assigned automatically.",
                "Assign Next Job",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            RepairJob job = dataManager.assignNextWaitingJob(
                    authenticatedUser.getUserID());

            refreshAll();

            if (job == null) {
                showMessage(
                        "No job was started. The queue may be empty, "
                                + "or all technicians are busy. "
                                + "The oldest job remains at the front of the queue.",
                        "No Assignment",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                selectJob(job.getRepairJobID());

                String technicianName = job.getTechnician() == null
                        ? "an available technician"
                        : job.getTechnician().getName();

                showMessage(
                        "Job #" + job.getRepairJobID()
                                + " was started with "
                                + technicianName + ".",
                        "Job Assigned",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    // ---------------- DIAGNOSIS AND ESTIMATE ----------------

    private void submitDiagnosis() {
        RepairJob job = getSelectedJob();

        if (job == null) {
            return;
        }

        if (!RepairJob.DIAGNOSING.equals(job.getStatus())) {
            showMessage(
                    "Select a job with DIAGNOSING status.",
                    "Invalid Job Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextArea diagnosisArea = new JTextArea(4, 28);
        styleDialogTextArea(diagnosisArea);

        JTextArea descriptionArea = new JTextArea(4, 28);
        styleDialogTextArea(descriptionArea);

        JTextField estimateField = new JTextField(15);
        styleDialogTextField(estimateField);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        form.add(dialogFieldLabel("Diagnosis"), c);

        c.gridx = 1;
        c.weightx = 1;
        form.add(createDialogScrollPane(diagnosisArea, 90), c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        form.add(dialogFieldLabel("Repair Description"), c);

        c.gridx = 1;
        c.weightx = 1;
        form.add(createDialogScrollPane(descriptionArea, 90), c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        form.add(dialogFieldLabel("Estimated Cost (₱)"), c);

        c.gridx = 1;
        c.weightx = 1;
        form.add(estimateField, c);

        JPanel dialogContent = new JPanel(new BorderLayout(0, 12));
        dialogContent.setBackground(WHITE);
        dialogContent.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel heading = new JLabel("Diagnosis & Estimate — Job #"
                + job.getRepairJobID());
        heading.setFont(font(Font.BOLD, 18));
        heading.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Enter the diagnosis, repair details, and estimated cost.");
        subtitle.setFont(font(Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(WHITE);

        GridBagConstraints hc = new GridBagConstraints();
        hc.gridx = 0;
        hc.gridy = 0;
        hc.anchor = GridBagConstraints.WEST;
        hc.fill = GridBagConstraints.HORIZONTAL;
        hc.weightx = 1;
        hc.insets = new Insets(0, 0, 5, 0);
        header.add(heading, hc);

        hc.gridy = 1;
        hc.insets = new Insets(0, 0, 0, 0);
        header.add(subtitle, hc);

        dialogContent.add(header, BorderLayout.NORTH);
        dialogContent.add(form, BorderLayout.CENTER);

        int choice = JOptionPane.showConfirmDialog(
                this,
                dialogContent,
                "Diagnosis & Estimate",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        double estimate;

        try {
            estimate = parseMoney(
                    estimateField.getText(), "Estimated cost");
        } catch (IllegalArgumentException exception) {
            showMessage(
                    exception.getMessage(),
                    "Invalid Cost",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            dataManager.submitDiagnosisAndEstimate(
                    authenticatedUser.getUserID(),
                    job.getRepairJobID(),
                    diagnosisArea.getText().trim(),
                    descriptionArea.getText().trim(),
                    estimate);

            refreshAll();
            selectJob(job.getRepairJobID());

            showMessage(
                    "Diagnosis and estimate saved. The job is awaiting approval.",
                    "Estimate Submitted",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    // ---------------- APPROVE OR REJECT ESTIMATE ----------------

    private void decideEstimate(boolean approved) {
        RepairJob job = getSelectedJob();

        if (job == null) {
            return;
        }

        if (!RepairJob.AWAITING_APPROVAL.equals(job.getStatus())) {
            showMessage(
                    "Select a job with AWAITING_APPROVAL status.",
                    "Invalid Job Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice;

        if (approved) {
            JPanel details = new JPanel(new GridBagLayout());
            details.setBackground(WHITE);
            details.setBorder(
                    BorderFactory.createEmptyBorder(8, 8, 8, 8));

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(8, 8, 8, 8);
            c.anchor = GridBagConstraints.WEST;
            c.fill = GridBagConstraints.HORIZONTAL;

            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0;
            details.add(dialogFieldLabel("Repair Job"), c);

            c.gridx = 1;
            c.weightx = 1;
            JLabel jobIDLabel = new JLabel(
                    "#" + job.getRepairJobID());
            jobIDLabel.setFont(font(Font.BOLD, 15));
            jobIDLabel.setForeground(TEXT);
            details.add(jobIDLabel, c);

            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 0;
            details.add(dialogFieldLabel("Estimated Cost"), c);

            c.gridx = 1;
            c.weightx = 1;
            JLabel estimateLabel = new JLabel(
                    money(job.getEstimatedCost()));
            estimateLabel.setFont(font(Font.BOLD, 18));
            estimateLabel.setForeground(BLUE);
            details.add(estimateLabel, c);

            JPanel dialogContent = createActionDialog(
                    "Approve Estimate — Job #"
                            + job.getRepairJobID(),
                    "Review the estimate before authorizing the repair.",
                    details);

            choice = JOptionPane.showConfirmDialog(
                    this,
                    dialogContent,
                    "Approve Estimate",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

        } else {
            choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to reject the estimate for job #"
                            + job.getRepairJobID() + "?",
                    "Confirm Estimate Decision",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
        }

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dataManager.decideEstimate(
                    authenticatedUser.getUserID(),
                    job.getRepairJobID(),
                    approved);

            refreshAll();
            selectJob(job.getRepairJobID());

            showMessage(
                    approved
                            ? "Estimate approved. The job is now REPAIRING."
                            : "Estimate rejected. The job is marked REJECTED.",
                    "Estimate Decision Recorded",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    // ---------------- COMPLETE REPAIR ----------------

    private void completeRepair() {
        RepairJob job = getSelectedJob();

        if (job == null) {
            return;
        }

        if (!RepairJob.REPAIRING.equals(job.getStatus())) {
            showMessage(
                    "Select a job with REPAIRING status.",
                    "Invalid Job Status",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField finalCostField = new JTextField(15);
        styleDialogTextField(finalCostField);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        form.add(dialogFieldLabel("Repair Job"), c);

        c.gridx = 1;
        c.weightx = 1;
        JLabel jobIDLabel = new JLabel("#" + job.getRepairJobID());
        jobIDLabel.setFont(font(Font.BOLD, 15));
        jobIDLabel.setForeground(TEXT);
        form.add(jobIDLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        form.add(dialogFieldLabel("Approved Estimate"), c);

        c.gridx = 1;
        c.weightx = 1;
        JLabel estimateLabel = new JLabel(
                money(job.getEstimatedCost()));
        estimateLabel.setFont(font(Font.BOLD, 18));
        estimateLabel.setForeground(BLUE);
        form.add(estimateLabel, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        form.add(dialogFieldLabel("Final Cost (₱)"), c);

        c.gridx = 1;
        c.weightx = 1;
        form.add(finalCostField, c);

        JLabel hint = new JLabel(
                "The final cost cannot exceed the approved estimate.");
        hint.setFont(font(Font.PLAIN, 12));
        hint.setForeground(MUTED);

        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        form.add(hint, c);

        JPanel dialogContent = createActionDialog(
                "Complete Repair — Job #"
                        + job.getRepairJobID(),
                "Confirm the final cost after the repair is finished.",
                form);

        int choice = JOptionPane.showConfirmDialog(
                this,
                dialogContent,
                "Complete Repair",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        double finalCost;

        try {
            finalCost = parseMoney(
                    finalCostField.getText(), "Final cost");
        } catch (IllegalArgumentException exception) {
            showMessage(
                    exception.getMessage(),
                    "Invalid Cost",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (finalCost > job.getEstimatedCost()) {
            showMessage(
                    "Final cost cannot exceed the approved estimate of "
                            + money(job.getEstimatedCost()) + ".",
                    "Cost Exceeds Estimate",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            dataManager.completeRepair(
                    authenticatedUser.getUserID(),
                    job.getRepairJobID(),
                    finalCost);

            refreshAll();
            selectJob(job.getRepairJobID());

            showMessage(
                    "Repair completed. The job can now proceed to payment.",
                    "Repair Completed",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    // ---------------- DELETE PENDING JOB ----------------

    private void deleteSelectedJob() {
        RepairJob job = getSelectedJob();

        if (job == null) {
            return;
        }

        if (!RepairJob.PENDING.equals(job.getStatus())) {
            showMessage(
                    "Only untouched PENDING jobs can be deleted.",
                    "Cannot Delete Job",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete pending repair job #" + job.getRepairJobID()
                        + "?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dataManager.deleteRepairJob(
                    authenticatedUser.getUserID(),
                    job.getRepairJobID());

            refreshAll();

            showMessage(
                    "Repair job deleted.",
                    "Deleted",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    // ---------------- SELECTED JOB HELPERS ----------------

    private RepairJob getSelectedJob() {
        int viewRow = jobsTable.getSelectedRow();

        if (viewRow < 0) {
            showMessage(
                    "Select a repair job from the table first.",
                    "No Job Selected",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int modelRow = jobsTable.convertRowIndexToModel(viewRow);

        int jobID = ((Number) tableModel.getValueAt(modelRow, 0))
                .intValue();

        try {
            RepairJob job = dataManager.getRepairJobByID(jobID);

            if (job == null) {
                showMessage(
                        "The selected repair job could not be found.",
                        "Job Not Found",
                        JOptionPane.WARNING_MESSAGE);
                refreshJobsTable();
            }

            return job;

        } catch (RuntimeException exception) {
            showError(exception);
            return null;
        }
    }

    private void selectJob(int jobID) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (((Number) tableModel.getValueAt(row, 0))
                    .intValue() == jobID) {

                int viewRow = jobsTable.convertRowIndexToView(row);

                if (viewRow >= 0) {
                    jobsTable.setRowSelectionInterval(viewRow, viewRow);
                }

                return;
            }
        }
    }

    // ---------------- VALIDATION HELPERS ----------------

    private double parseMoney(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " is required.");
        }

        try {
            double value = Double.parseDouble(text.trim());

            if (Double.isNaN(value)
                    || Double.isInfinite(value)
                    || value < 0) {

                throw new IllegalArgumentException(
                        fieldName
                                + " must be a finite, non-negative number.");
            }

            return value;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be a valid number, such as 1500 or 1500.50.");
        }
    }

    // ---------------- CUSTOM ACTION DIALOG HELPERS ----------------

    private JPanel createActionDialog(
            String title,
            String subtitle,
            JPanel body) {

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setBackground(WHITE);
        content.setBorder(
                BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(HEADER_BLUE);
        header.setBorder(
                BorderFactory.createEmptyBorder(14, 16, 14, 16));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 6, 0);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(font(Font.BOLD, 20));
        titleLabel.setForeground(WHITE);
        header.add(titleLabel, c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(font(Font.PLAIN, 13));
        subtitleLabel.setForeground(WHITE);
        header.add(subtitleLabel, c);

        body.setBackground(WHITE);

        content.add(header, BorderLayout.NORTH);
        content.add(body, BorderLayout.CENTER);
        content.setPreferredSize(new Dimension(540, 260));

        return content;
    }

    private JLabel dialogFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.BOLD, 13));
        label.setForeground(TEXT);
        return label;
    }

    private void styleDialogTextArea(JTextArea area) {
        area.setFont(font(Font.PLAIN, 14));
        area.setForeground(TEXT);
        area.setCaretColor(TEXT);
        area.setBackground(WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(
                BorderFactory.createEmptyBorder(8, 9, 8, 9));
    }

    private void styleDialogTextField(JTextField field) {
        field.setFont(font(Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBackground(WHITE);
        field.setPreferredSize(new Dimension(220, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    private JScrollPane createDialogScrollPane(
            JTextArea area,
            int height) {

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        scroll.setPreferredSize(new Dimension(300, height));

        return scroll;
    }

    // ---------------- UI HELPERS ----------------

    private JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        return panel;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.PLAIN, 14));
        label.setForeground(TEXT);
        return label;
    }

    private JButton createButton(String text, boolean primary) {
        JButton button = new JButton(text);

        button.setFont(font(primary ? Font.BOLD : Font.PLAIN, 13));
        button.setForeground(primary ? WHITE : TEXT);
        button.setBackground(
                primary ? BLUE : new Color(246, 248, 251));
        button.setFocusPainted(false);
        button.setOpaque(true);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? BLUE : BORDER),
                BorderFactory.createEmptyBorder(9, 13, 9, 13)));

        return button;
    }

    private String money(double amount) {
        return String.format("₱%,.2f", amount);
    }

    private String shorten(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        String singleLine = text.replace('\n', ' ')
                .replace('\r', ' ')
                .trim();

        if (singleLine.length() <= maxLength) {
            return singleLine;
        }

        return singleLine.substring(0, maxLength - 3) + "...";
    }

    private void showError(RuntimeException exception) {
        String message = exception.getMessage();

        if (message == null || message.trim().isEmpty()) {
            message = "The operation could not be completed.";
        }

        showMessage(
                message,
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showMessage(
            String message,
            String title,
            int messageType) {

        JOptionPane.showMessageDialog(
                this, message, title, messageType);
    }

    private Font font(int style, int size) {
        return new Font("SansSerif", style, size);
    }
} 