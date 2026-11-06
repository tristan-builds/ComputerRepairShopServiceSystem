
package computerrepairshopservicesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class CustomerPanel extends JPanel {

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
    private static final Color RED =
            new Color(153, 67, 67);

    private final DataManager dataManager;
    private final SystemUser authenticatedUser;

    private final JTextField customerIDField =
            new JTextField(22);
    private final JTextField nameField =
            new JTextField(22);
    private final JTextField contactNumberField =
            new JTextField(22);
    private final JTextField searchField =
            new JTextField(24);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                        "Customer ID",
                        "Customer Name",
                        "Contact Number"
                    }, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable customerTable =
            new JTable(tableModel);

    private final List<Customer> allCustomers =
            new ArrayList<>();

    private String selectedCustomerID;

    public CustomerPanel(
            DataManager dataManager,
            SystemUser authenticatedUser) {

        if (dataManager == null || authenticatedUser == null) {
            throw new IllegalArgumentException(
                    "A DataManager and authenticated user are required.");
        }

        this.dataManager = dataManager;
        this.authenticatedUser = authenticatedUser;

        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                28, 32, 28, 32));

        customerIDField.setEditable(false);
        customerIDField.setBackground(
                new Color(246, 248, 251));
        customerIDField.setToolTipText(
                "Customer IDs are generated automatically.");

        buildLayout();
        configureTable();
        configureSearch();
        configureTableSelection();

        refreshCustomers();
    }

    private void buildLayout() {

        JPanel pageHeading = new JPanel(
                new BorderLayout(0, 5));
        pageHeading.setOpaque(false);

        JLabel title = new JLabel("Customers");
        title.setFont(font(Font.BOLD, 25));
        title.setForeground(TEXT);

        JLabel description = new JLabel(
                "Create, update, search, and manage customer records.");
        description.setFont(font(Font.PLAIN, 13));
        description.setForeground(MUTED);

        JPanel headingText = new JPanel(
                new GridLayout(2, 1, 0, 5));
        headingText.setOpaque(false);
        headingText.add(title);
        headingText.add(description);

        pageHeading.add(headingText, BorderLayout.WEST);
        add(pageHeading, BorderLayout.NORTH);

        JPanel content = new JPanel(
                new BorderLayout(0, 18));
        content.setOpaque(false);

        content.add(createCustomerForm(), BorderLayout.NORTH);
        content.add(createRecordsPanel(), BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createCustomerForm() {

        JPanel formCard = new JPanel(new BorderLayout(0, 16));
        formCard.setBackground(WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(
                        20, 22, 18, 22)));

        JLabel formTitle = new JLabel("Customer Information");
        formTitle.setFont(font(Font.BOLD, 16));
        formTitle.setForeground(TEXT);

        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 12, 18);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        addFormField(fields, c, 0, 0,
                "Customer ID", customerIDField);
        addFormField(fields, c, 1, 0,
                "Customer Name *", nameField);
        addFormField(fields, c, 0, 1,
                "Contact Number *", contactNumberField);

        formCard.add(fields, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        JButton addButton = createButton(
                "Add Customer", BLUE, WHITE);
        JButton updateButton = createButton(
                "Update", LIGHT_BLUE, BLUE);
        JButton deleteButton = createButton(
                "Delete", WHITE, RED);
        JButton clearButton = createButton(
                "Clear", WHITE, TEXT);

        addButton.addActionListener(event -> addCustomer());
        updateButton.addActionListener(event -> updateCustomer());
        deleteButton.addActionListener(event -> deleteCustomer());
        clearButton.addActionListener(event -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        formCard.add(buttonPanel, BorderLayout.SOUTH);

        return formCard;
    }

    private void addFormField(
            JPanel panel,
            GridBagConstraints base,
            int column,
            int row,
            String labelText,
            JTextField field) {

        JPanel fieldPanel = new JPanel(
                new BorderLayout(0, 7));
        fieldPanel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(font(Font.BOLD, 12));
        label.setForeground(TEXT);

        field.setFont(font(Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setPreferredSize(new Dimension(220, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(5, 9, 5, 9)));

        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);

        GridBagConstraints c =
                (GridBagConstraints) base.clone();

        c.gridx = column;
        c.gridy = row;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        panel.add(fieldPanel, c);
    }

    private JPanel createRecordsPanel() {

        JPanel recordsCard = new JPanel(
                new BorderLayout(0, 14));
        recordsCard.setBackground(WHITE);
        recordsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(
                        18, 20, 18, 20)));

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);

        JLabel recordsTitle = new JLabel("Customer Records");
        recordsTitle.setFont(font(Font.BOLD, 16));
        recordsTitle.setForeground(TEXT);

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);

        searchField.setFont(font(Font.PLAIN, 13));
        searchField.setToolTipText(
                "Search by customer ID or customer name.");
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(
                                5, 9, 5, 9)));

        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(font(Font.PLAIN, 12));
        searchLabel.setForeground(MUTED);

        JButton clearSearchButton = createButton(
                "Clear Search", LIGHT_BLUE, BLUE);
        clearSearchButton.addActionListener(event ->
                searchField.setText(""));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(clearSearchButton);

        top.add(recordsTitle, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.EAST);

        recordsCard.add(top, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));

        recordsCard.add(scrollPane, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "Select a row to edit a customer.");
        footer.setFont(font(Font.PLAIN, 12));
        footer.setForeground(MUTED);

        recordsCard.add(footer, BorderLayout.SOUTH);

        return recordsCard;
    }

   
   
private void configureTable() {

    // Customer details
    customerTable.setFont(font(Font.BOLD, 15));
    customerTable.setForeground(TEXT);
    customerTable.setBackground(WHITE);
    customerTable.setRowHeight(40);
    customerTable.setFillsViewportHeight(true);

    // Black horizontal and vertical separators
    customerTable.setShowHorizontalLines(true);
    customerTable.setShowVerticalLines(true);
    customerTable.setGridColor(Color.BLACK);
    customerTable.setIntercellSpacing(new Dimension(1, 1));

    // Selection appearance
    customerTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
    customerTable.setSelectionBackground(LIGHT_BLUE);
    customerTable.setSelectionForeground(TEXT);

    // Enable sorting by clicking column headers
    customerTable.setAutoCreateRowSorter(true);

    // Column header appearance
    JTableHeader header = customerTable.getTableHeader();
    header.setFont(font(Font.BOLD, 14));
    header.setForeground(Color.WHITE);
    header.setBackground(new Color(43, 78, 120));
    header.setPreferredSize(new Dimension(100, 42));
    header.setReorderingAllowed(false);
}


    private void configureSearch() {

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent event) {
                applySearch();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                applySearch();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                applySearch();
            }
        });
    }

    private void configureTableSelection() {

        customerTable.getSelectionModel()
                .addListSelectionListener(event -> {

            if (event.getValueIsAdjusting()) {
                return;
            }

            int selectedRow = customerTable.getSelectedRow();

            if (selectedRow < 0) {
                selectedCustomerID = null;
                return;
            }

            int modelRow =
                    customerTable.convertRowIndexToModel(selectedRow);

            String customerID = String.valueOf(
                    tableModel.getValueAt(modelRow, 0));

            populateForm(customerID);
        });
    }

    private void addCustomer() {

        String name = nameField.getText().trim();
        String contactNumber =
                contactNumberField.getText().trim();

        if (!validateInputs(name, contactNumber)) {
            return;
        }

        Customer created;

        try {
            created = dataManager.addCustomer(
                    authenticatedUser.getUserID(),
                    name,
                    contactNumber);

        } catch (RuntimeException ex) {
            showOperationError("add this customer", ex);
            return;
        }

        if (!refreshCustomers()) {
            return;
        }

        clearForm();

        showMessage(
                "Customer added successfully.\nCustomer ID: "
                        + created.getCustomerID(),
                "Customer Added",
                JOptionPane.INFORMATION_MESSAGE);

        nameField.requestFocusInWindow();
    }

    private void updateCustomer() {

        if (!hasVisibleSelection()) {
            showMessage(
                    "Select a customer from the visible table before updating.",
                    "No Customer Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String contactNumber =
                contactNumberField.getText().trim();

        if (!validateInputs(name, contactNumber)) {
            return;
        }

        String customerID = selectedCustomerID;
        boolean updated;

        try {
            updated = dataManager.updateCustomer(
                    authenticatedUser.getUserID(),
                    customerID,
                    name,
                    contactNumber);

        } catch (RuntimeException ex) {
            showOperationError("update this customer", ex);
            return;
        }

        if (!updated) {
            showMessage(
                    "The selected customer could not be found.",
                    "Update Failed",
                    JOptionPane.WARNING_MESSAGE);
            refreshCustomers();
            clearForm();
            return;
        }

        if (!refreshCustomers()) {
            return;
        }

        selectCustomerInTable(customerID);

        showMessage(
                "Customer updated successfully.",
                "Customer Updated",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteCustomer() {

        if (!hasVisibleSelection()) {
            showMessage(
                    "Select a customer from the visible table before deleting.",
                    "No Customer Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerID = selectedCustomerID;
        String customerName = nameField.getText().trim();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete this customer?\n\n"
                        + "Customer ID: " + customerID + "\n"
                        + "Name: " + customerName
                        + "\n\nThis action cannot be undone.",
                "Confirm Customer Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        boolean deleted;

        try {
            deleted = dataManager.deleteCustomer(
                    authenticatedUser.getUserID(),
                    customerID);

        } catch (RuntimeException ex) {
            showOperationError("delete this customer", ex);
            return;
        }

        if (!deleted) {
            showMessage(
                    "The selected customer could not be found.",
                    "Delete Failed",
                    JOptionPane.WARNING_MESSAGE);
            refreshCustomers();
            clearForm();
            return;
        }

        if (!refreshCustomers()) {
            return;
        }

        clearForm();

        showMessage(
                "Customer deleted successfully.",
                "Customer Deleted",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validateInputs(
            String name, String contactNumber) {

        if (name.isEmpty()) {
            showMessage(
                    "Enter the customer's name.",
                    "Required Information",
                    JOptionPane.WARNING_MESSAGE);
            nameField.requestFocusInWindow();
            return false;
        }

        if (!name.matches("[\\p{L} .'-]+")) {
            showMessage(
                    "Customer name must contain letters, spaces, periods, "
                    + "apostrophes, or hyphens only.",
                    "Invalid Customer Name",
                    JOptionPane.WARNING_MESSAGE);
            nameField.requestFocusInWindow();
            return false;
        }

        if (contactNumber.isEmpty()) {
            showMessage(
                    "Enter the customer's contact number.",
                    "Required Information",
                    JOptionPane.WARNING_MESSAGE);
            contactNumberField.requestFocusInWindow();
            return false;
        }

        if (!contactNumber.matches("\\+?\\d+")) {
            showMessage(
                    "Contact number must contain digits only, "
                    + "with an optional leading +.",
                    "Invalid Contact Number",
                    JOptionPane.WARNING_MESSAGE);
            contactNumberField.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private boolean refreshCustomers() {

        try {
            List<Customer> refreshedCustomers =
                    dataManager.getCustomers();

            allCustomers.clear();
            allCustomers.addAll(refreshedCustomers);

            applySearch();
            return true;

        } catch (RuntimeException ex) {
            showMessage(
                    "Customer records could not be refreshed.\n"
                    + "A preceding operation may already have been saved.\n\n"
                    + errorMessage(ex),
                    "Refresh Failed",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void applySearch() {

        String query = searchField.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        tableModel.setRowCount(0);

        selectedCustomerID = null;
        customerTable.clearSelection();
        customerIDField.setText("");
        nameField.setText("");
        contactNumberField.setText("");

        for (Customer customer : allCustomers) {

            String id = safe(customer.getCustomerID())
                    .toLowerCase(Locale.ROOT);
            String name = safe(customer.getName())
                    .toLowerCase(Locale.ROOT);

            boolean matches =
                    id.contains(query) || name.contains(query);

            if (matches) {
                tableModel.addRow(new Object[]{
                    customer.getCustomerID(),
                    customer.getName(),
                    customer.getContactNumber()
                });
            }
        }
    }

    private void populateForm(String customerID) {

        for (Customer customer : allCustomers) {

            if (customerID.equals(customer.getCustomerID())) {

                selectedCustomerID = customer.getCustomerID();

                customerIDField.setText(customer.getCustomerID());
                nameField.setText(customer.getName());
                contactNumberField.setText(
                        customer.getContactNumber());

                return;
            }
        }

        selectedCustomerID = null;
    }

    private boolean hasVisibleSelection() {

        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow < 0 || selectedCustomerID == null) {
            selectedCustomerID = null;
            return false;
        }

        int modelRow =
                customerTable.convertRowIndexToModel(selectedRow);

        String visibleCustomerID = String.valueOf(
                tableModel.getValueAt(modelRow, 0));

        if (!selectedCustomerID.equals(visibleCustomerID)) {
            selectedCustomerID = null;
            return false;
        }

        return true;
    }

    private void selectCustomerInTable(String customerID) {

        for (int modelRow = 0;
                modelRow < tableModel.getRowCount();
                modelRow++) {

            String rowCustomerID = String.valueOf(
                    tableModel.getValueAt(modelRow, 0));

            if (customerID.equals(rowCustomerID)) {

                int viewRow =
                        customerTable.convertRowIndexToView(modelRow);

                if (viewRow >= 0) {
                    customerTable.setRowSelectionInterval(
                            viewRow, viewRow);
                    customerTable.scrollRectToVisible(
                            customerTable.getCellRect(
                                    viewRow, 0, true));
                }

                return;
            }
        }

        selectedCustomerID = null;
    }

    private void clearForm() {

        selectedCustomerID = null;
        customerTable.clearSelection();

        customerIDField.setText("");
        nameField.setText("");
        contactNumberField.setText("");

        nameField.requestFocusInWindow();
    }

    private JButton createButton(
            String text, Color background, Color foreground) {

        JButton button = new JButton(text);

        button.setFont(font(Font.BOLD, 12));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        background.equals(WHITE) ? BORDER : background),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        return button;
    }

    private void showOperationError(
            String operation, RuntimeException ex) {

        showMessage(
                "Could not " + operation + ".\n\n"
                        + errorMessage(ex),
                "Operation Failed",
                JOptionPane.ERROR_MESSAGE);
    }

    private String errorMessage(RuntimeException ex) {

        String detail = ex.getMessage();

        if (detail == null || detail.trim().isEmpty()) {
            return "An unexpected error occurred.";
        }

        return detail;
    }

    private void showMessage(
            String message, String title, int messageType) {

        JOptionPane.showMessageDialog(
                this, message, title, messageType);
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
