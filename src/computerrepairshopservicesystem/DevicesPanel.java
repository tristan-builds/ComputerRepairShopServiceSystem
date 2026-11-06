
package computerrepairshopservicesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class DevicesPanel extends JPanel {

    private final DataManager dataManager;
    private final SystemUser authenticatedUser;

    private final JComboBox<String> deviceTypeCombo;
    private final JTextField deviceIDField;
    private final JTextField brandField;
    private final JTextField modelField;
    private final JTextField caseTypeField;
    private final JTextField screenSizeField;
    private final JLabel additionalDetailsLabel;

    private final JTextField searchField;
    private final JTable devicesTable;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;

    private final JPanel fieldsPanel;

    private final JButton addButton;
    private final JButton clearButton;

    private String editingDeviceID = null;
    private boolean loadingForm = false;

    // Match CustomerPanel colors.
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

    public DevicesPanel(
            DataManager dataManager,
            SystemUser authenticatedUser) {

        this.dataManager = dataManager;
        this.authenticatedUser = authenticatedUser;

        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                22, 24, 22, 24));

        // ---------------- PAGE HEADING ----------------

        JPanel pageHeader = new JPanel(
                new BorderLayout(0, 5));
        pageHeader.setBackground(BACKGROUND);

        JLabel pageTitle = new JLabel("Devices");
        pageTitle.setFont(font(Font.BOLD, 25));
        pageTitle.setForeground(TEXT);

        JLabel pageSubtitle = new JLabel(
                "Register, view, search, and edit computer devices.");
        pageSubtitle.setFont(font(Font.PLAIN, 14));
        pageSubtitle.setForeground(MUTED);

        JPanel titleContent = new JPanel(
                new BorderLayout(0, 5));
        titleContent.setBackground(BACKGROUND);
        titleContent.add(pageTitle, BorderLayout.NORTH);
        titleContent.add(pageSubtitle, BorderLayout.CENTER);

        pageHeader.add(titleContent, BorderLayout.CENTER);
        add(pageHeader, BorderLayout.NORTH);

        // ---------------- FORM FIELDS ----------------

        deviceIDField = new JTextField(18);
        deviceIDField.setEditable(false);
        deviceIDField.setFocusable(false);
        styleTextField(deviceIDField);

        deviceTypeCombo = new JComboBox<>(
                new String[]{"Desktop", "Laptop"});
        deviceTypeCombo.setFont(font(Font.PLAIN, 14));
        deviceTypeCombo.setForeground(TEXT);
        deviceTypeCombo.setBackground(WHITE);
        deviceTypeCombo.setPreferredSize(
                new Dimension(220, 36));

        brandField = new JTextField(18);
        modelField = new JTextField(18);
        caseTypeField = new JTextField(18);
        screenSizeField = new JTextField(18);

        styleTextField(brandField);
        styleTextField(modelField);
        styleTextField(caseTypeField);
        styleTextField(screenSizeField);

        additionalDetailsLabel =
                createFieldLabel("Case Type");

        addButton = createPrimaryButton("Add Device");
        clearButton = createSecondaryButton("Clear");

        fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(fieldsPanel, gbc, 0,
                "Device ID", deviceIDField);
        addField(fieldsPanel, gbc, 1,
                "Device Type", deviceTypeCombo);
        addField(fieldsPanel, gbc, 2,
                "Brand", brandField);
        addField(fieldsPanel, gbc, 3,
                "Model", modelField);
        addField(fieldsPanel, gbc, 4,
                additionalDetailsLabel, caseTypeField);

        JPanel buttonsPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 4));
        buttonsPanel.setBackground(WHITE);
        buttonsPanel.add(addButton);
        buttonsPanel.add(clearButton);

        JPanel formContent = new JPanel(
                new BorderLayout(0, 12));
        formContent.setBackground(WHITE);
        formContent.add(fieldsPanel, BorderLayout.CENTER);
        formContent.add(buttonsPanel, BorderLayout.SOUTH);

        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 14));
        formCard.add(
                createSectionTitle("Register Device"),
                BorderLayout.NORTH);
        formCard.add(formContent, BorderLayout.CENTER);

        // ---------------- DEVICE TABLE ----------------

        tableModel = new DefaultTableModel(
                new Object[]{
                    "Device ID",
                    "Device Type",
                    "Brand",
                    "Model",
                    "Additional Details"
                }, 0) {

            @Override
            public boolean isCellEditable(
                    int row, int column) {
                return false;
            }
        };

        devicesTable = new JTable(tableModel);
        configureTable();

        sorter = new TableRowSorter<>(tableModel);
        devicesTable.setRowSorter(sorter);

        JScrollPane tableScrollPane =
                new JScrollPane(devicesTable);
        tableScrollPane.setBorder(
                BorderFactory.createLineBorder(BORDER));
        tableScrollPane.getViewport().setBackground(WHITE);
        tableScrollPane.setPreferredSize(
                new Dimension(600, 280));

        JPanel recordsCard = createCard();
        recordsCard.setLayout(new BorderLayout(0, 14));

        JLabel recordsTitle =
                createSectionTitle("Device Records");

        searchField = new JTextField(20);
        styleTextField(searchField);
        searchField.setToolTipText(
                "Search by device ID, brand, or model");

        JButton clearSearchButton =
                createSecondaryButton("Clear Search");

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(WHITE);

        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(font(Font.BOLD, 14));
        searchLabel.setForeground(TEXT);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(clearSearchButton);

        JPanel recordsHeader = new JPanel(
                new BorderLayout(0, 12));
        recordsHeader.setBackground(WHITE);
        recordsHeader.add(recordsTitle, BorderLayout.NORTH);
        recordsHeader.add(searchPanel, BorderLayout.CENTER);

        recordsCard.add(recordsHeader, BorderLayout.NORTH);
        recordsCard.add(tableScrollPane, BorderLayout.CENTER);

        // ---------------- MAIN CONTENT ----------------

        JPanel contentPanel = new JPanel(
                new BorderLayout(0, 18));
        contentPanel.setBackground(BACKGROUND);
        contentPanel.add(formCard, BorderLayout.NORTH);
        contentPanel.add(recordsCard, BorderLayout.CENTER);

        JScrollPane contentScrollPane = new JScrollPane(contentPanel);
        contentScrollPane.setBorder(null);
        contentScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.getViewport().setBackground(BACKGROUND);
        contentScrollPane.getViewport().setOpaque(true);

        add(contentScrollPane, BorderLayout.CENTER);

        // ---------------- EVENT HANDLERS ----------------

        deviceTypeCombo.addActionListener(e -> {
            if (!loadingForm) {
                updateAdditionalField();
            }
        });

        addButton.addActionListener(e -> {
            if (editingDeviceID == null) {
                addDevice();
            } else {
                saveDeviceChanges();
            }
        });

        clearButton.addActionListener(e -> clearForm());

        devicesTable.getSelectionModel().addListSelectionListener(
                this::handleDeviceSelection);

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterDevices();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterDevices();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterDevices();
            }
        });

        clearSearchButton.addActionListener(
                e -> searchField.setText(""));

        updateAdditionalField();
        refreshDevicesTable();
    }

    // ---------------- STYLING HELPERS ----------------

    private Font font(int style, int size) {
        return new Font("SansSerif", style, size);
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(
                        18, 18, 18, 18)));
        return card;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.BOLD, 18));
        label.setForeground(TEXT);
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.PLAIN, 14));
        label.setForeground(TEXT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(font(Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(WHITE);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(
                        7, 9, 7, 9)));
        field.setPreferredSize(new Dimension(220, 36));
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(BLUE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(
                10, 16, 10, 16));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(font(Font.PLAIN, 13));
        button.setForeground(TEXT);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(
                        9, 15, 9, 15)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void configureTable() {
        devicesTable.setFont(font(Font.BOLD, 15));
        devicesTable.setForeground(TEXT);
        devicesTable.setBackground(WHITE);
        devicesTable.setRowHeight(40);
        devicesTable.setFillsViewportHeight(true);

        devicesTable.setShowHorizontalLines(true);
        devicesTable.setShowVerticalLines(true);
        devicesTable.setGridColor(Color.BLACK);
        devicesTable.setIntercellSpacing(
                new Dimension(1, 1));

        devicesTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        devicesTable.setSelectionBackground(LIGHT_BLUE);
        devicesTable.setSelectionForeground(TEXT);

        JTableHeader header = devicesTable.getTableHeader();
        header.setFont(font(Font.BOLD, 14));
        header.setForeground(WHITE);
        header.setBackground(HEADER_BLUE);
        header.setPreferredSize(new Dimension(100, 42));
        header.setReorderingAllowed(false);
    }

    // ---------------- FORM HELPERS ----------------

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component field) {

        addField(panel, gbc, row,
                createFieldLabel(label), field);
    }

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            JLabel label,
            Component field) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void updateAdditionalField() {
        boolean isDesktop =
                "Desktop".equals(deviceTypeCombo.getSelectedItem());

        additionalDetailsLabel.setText(
                isDesktop ? "Case Type" : "Screen Size (inches)");

        fieldsPanel.remove(caseTypeField);
        fieldsPanel.remove(screenSizeField);

        Component activeField =
                isDesktop ? caseTypeField : screenSizeField;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 5, 8, 12);

        fieldsPanel.add(activeField, gbc);

        fieldsPanel.revalidate();
        fieldsPanel.repaint();
    }

    private void handleDeviceSelection(ListSelectionEvent event) {
        if (event.getValueIsAdjusting() || loadingForm) {
            return;
        }

        int selectedViewRow = devicesTable.getSelectedRow();

        if (selectedViewRow < 0) {
            return;
        }

        int modelRow =
                devicesTable.convertRowIndexToModel(selectedViewRow);

        String deviceID =
                String.valueOf(tableModel.getValueAt(modelRow, 0));

        loadDeviceForEditing(deviceID);
    }

    private void loadDeviceForEditing(String deviceID) {
        try {
            Device device = dataManager.getDeviceByID(deviceID);

            if (device == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "The selected device could not be found.",
                        "Device Not Found",
                        JOptionPane.WARNING_MESSAGE);
                refreshDevicesTable();
                return;
            }

            loadingForm = true;

            editingDeviceID = device.getDeviceID();
            deviceIDField.setText(device.getDeviceID());
            brandField.setText(device.getBrand());
            modelField.setText(device.getModel());

            if (device instanceof Desktop) {
                deviceTypeCombo.setSelectedItem("Desktop");

                caseTypeField.setText(
                        ((Desktop) device).getCaseType());
                screenSizeField.setText("");

            } else if (device instanceof Laptop) {
                deviceTypeCombo.setSelectedItem("Laptop");

                screenSizeField.setText(
                        String.valueOf(
                                ((Laptop) device).getScreenSize()));
                caseTypeField.setText("");

            } else {
                editingDeviceID = null;

                JOptionPane.showMessageDialog(
                        this,
                        "This device type cannot be edited here.",
                        "Unsupported Device",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            updateAdditionalField();
            addButton.setText("Save Changes");

        } catch (SecurityException exception) {
            showError("Access Error", exception.getMessage());
        } finally {
            loadingForm = false;
        }
    }

    private void clearForm() {
        loadingForm = true;

        editingDeviceID = null;
        deviceIDField.setText("");
        brandField.setText("");
        modelField.setText("");
        caseTypeField.setText("");
        screenSizeField.setText("");
        deviceTypeCombo.setSelectedIndex(0);
        addButton.setText("Add Device");

        devicesTable.clearSelection();

        loadingForm = false;
        updateAdditionalField();
    }

    // ---------------- DEVICE OPERATIONS ----------------

    private void addDevice() {
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String actorUserID = authenticatedUser.getUserID();

        if (brand.isEmpty() || model.isEmpty()) {
            showWarning(
                    "Validation Error",
                    "Brand and model are required.");
            return;
        }

        try {
            String selectedType =
                    (String) deviceTypeCombo.getSelectedItem();

            Device addedDevice;

            if ("Desktop".equals(selectedType)) {
                String caseType =
                        caseTypeField.getText().trim();

                if (caseType.isEmpty()) {
                    showWarning(
                            "Validation Error",
                            "Case type is required for a desktop.");
                    return;
                }

                addedDevice = dataManager.addDesktop(
                        actorUserID, brand, model, caseType);

            } else {
                double screenSize = readScreenSize();

                if (Double.isNaN(screenSize)) {
                    return;
                }

                addedDevice = dataManager.addLaptop(
                        actorUserID, brand, model, screenSize);
            }

            refreshDevicesTable();

            JOptionPane.showMessageDialog(
                    this,
                    "Device registered successfully.\nDevice ID: "
                            + addedDevice.getDeviceID(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            clearForm();

        } catch (SecurityException exception) {
            showError("Access Error", exception.getMessage());

        } catch (IllegalArgumentException exception) {
            showWarning("Validation Error", exception.getMessage());

        } catch (RuntimeException exception) {
            showError(
                    "Error",
                    "Unable to register the device: "
                            + exception.getMessage());
        }
    }

    private void saveDeviceChanges() {
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String actorUserID = authenticatedUser.getUserID();

        if (editingDeviceID == null) {
            showWarning(
                    "No Device Selected",
                    "Select a device from the table first.");
            return;
        }

        if (brand.isEmpty() || model.isEmpty()) {
            showWarning(
                    "Validation Error",
                    "Brand and model are required.");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Save changes to device " + editingDeviceID + "?",
                "Confirm Changes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Update the common fields first.
            boolean commonUpdated = dataManager.updateDevice(
                    actorUserID, editingDeviceID, brand, model);

            if (!commonUpdated) {
                showWarning(
                        "Update Failed",
                        "The selected device could not be found.");
                refreshDevicesTable();
                clearForm();
                return;
            }

            // Then update the field specific to the device type.
            if ("Desktop".equals(deviceTypeCombo.getSelectedItem())) {
                String caseType =
                        caseTypeField.getText().trim();

                if (caseType.isEmpty()) {
                    showWarning(
                            "Validation Error",
                            "Case type is required for a desktop.");
                    return;
                }

                boolean detailsUpdated =
                        dataManager.updateDesktopCaseType(
                                actorUserID, editingDeviceID, caseType);

                if (!detailsUpdated) {
                    showWarning(
                            "Update Failed",
                            "The desktop details could not be updated.");
                    return;
                }

            } else {
                double screenSize = readScreenSize();

                if (Double.isNaN(screenSize)) {
                    return;
                }

                boolean detailsUpdated =
                        dataManager.updateLaptopScreenSize(
                                actorUserID, editingDeviceID, screenSize);

                if (!detailsUpdated) {
                    showWarning(
                            "Update Failed",
                            "The laptop details could not be updated.");
                    return;
                }
            }

            String savedID = editingDeviceID;

            refreshDevicesTable();
            clearForm();

            JOptionPane.showMessageDialog(
                    this,
                    "Device " + savedID
                            + " updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SecurityException exception) {
            showError("Access Error", exception.getMessage());

        } catch (IllegalArgumentException exception) {
            showWarning("Validation Error", exception.getMessage());

        } catch (IllegalStateException exception) {
            showWarning("Cannot Edit Device", exception.getMessage());

        } catch (RuntimeException exception) {
            showError(
                    "Error",
                    "Unable to update the device: "
                            + exception.getMessage());
        }
    }

    private double readScreenSize() {
        String screenSizeText = screenSizeField.getText().trim();

        if (screenSizeText.isEmpty()) {
            showWarning(
                    "Validation Error",
                    "Screen size is required for a laptop.");
            return Double.NaN;
        }

        final double screenSize;

        try {
            screenSize = Double.parseDouble(screenSizeText);
        } catch (NumberFormatException exception) {
            showWarning(
                    "Validation Error",
                    "Enter a valid numeric screen size.");
            return Double.NaN;
        }

        if (Double.isNaN(screenSize)
                || Double.isInfinite(screenSize)
                || screenSize <= 0) {
            showWarning(
                    "Validation Error",
                    "Screen size must be a finite number greater than zero.");
            return Double.NaN;
        }

        return screenSize;
    }

    private void refreshDevicesTable() {
        try {
            List<Device> devices = dataManager.getDevices();

            tableModel.setRowCount(0);

            for (Device device : devices) {
                String type;
                String additionalDetails;

                if (device instanceof Desktop) {
                    Desktop desktop = (Desktop) device;
                    type = "Desktop";
                    additionalDetails = desktop.getCaseType();

                } else if (device instanceof Laptop) {
                    Laptop laptop = (Laptop) device;
                    type = "Laptop";
                    additionalDetails =
                            laptop.getScreenSize() + " inches";

                } else {
                    continue;
                }

                tableModel.addRow(new Object[]{
                    device.getDeviceID(),
                    type,
                    device.getBrand(),
                    device.getModel(),
                    additionalDetails
                });
            }

            filterDevices();

        } catch (SecurityException exception) {
            showError("Access Error", exception.getMessage());
        }
    }

    private void filterDevices() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(searchText),
                            0, 2, 3));
        }
    }

    // ---------------- MESSAGE HELPERS ----------------

    private void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
}
