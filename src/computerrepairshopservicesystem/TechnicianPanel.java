
package computerrepairshopservicesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class TechnicianPanel extends JPanel {

    private static final Color BACKGROUND = new Color(241, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLUE = new Color(48, 105, 183);
    private static final Color TEXT = new Color(43, 57, 76);
    private static final Color MUTED = new Color(105, 118, 136);
    private static final Color BORDER = new Color(216, 224, 234);
    private static final Color HEADER_BLUE = new Color(43, 78, 120);

    private final DataManager dataManager;
    private final SystemUser authenticatedUser;
    private final boolean admin;

    private final JTextField technicianIDField = new JTextField(18);
    private final JTextField nameField = new JTextField(18);
    private final JTextField searchField = new JTextField(18);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Technician ID", "Name", "Availability"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable technicianTable = new JTable(tableModel);
    private final JButton saveButton = new JButton("Add Technician");
    private final JButton clearButton = new JButton("Clear");
    private final JButton deleteButton = new JButton("Delete Selected");
    private final JButton searchButton = new JButton("Search");
    private final JButton refreshButton = new JButton("Refresh");

    public TechnicianPanel(
            DataManager dataManager,
            SystemUser authenticatedUser) {

        if (dataManager == null || authenticatedUser == null) {
            throw new IllegalArgumentException(
                    "A DataManager and authenticated user are required.");
        }

        this.dataManager = dataManager;
        this.authenticatedUser = authenticatedUser;
        this.admin = SystemUser.ADMIN.equals(authenticatedUser.getRole());

        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(28, 34, 30, 34));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        configureTable();
        configureActions();
        configurePermissions();
        refreshTechnicians();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 5, 0);

        JLabel title = new JLabel("Technicians");
        title.setFont(font(Font.BOLD, 25));
        title.setForeground(TEXT);
        titlePanel.add(title, c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);

        JLabel subtitle = new JLabel(
                "Manage technician records and view their availability.");
        subtitle.setFont(font(Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        titlePanel.add(subtitle, c);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);

        content.add(createFormCard(), BorderLayout.NORTH);
        content.add(createTableCard(), BorderLayout.CENTER);

        return content;
    }

    private JPanel createFormCard() {
        JPanel card = createCard();
        card.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 8, 7, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel sectionTitle = new JLabel("Technician Details");
        sectionTitle.setFont(font(Font.BOLD, 16));
        sectionTitle.setForeground(TEXT);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        card.add(sectionTitle, c);

        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        card.add(createFieldLabel("Technician ID"), c);

        c.gridx = 1;
        card.add(createFieldLabel("Name"), c);

        c.gridy = 2;
        c.gridx = 0;
        technicianIDField.setEditable(false);
        technicianIDField.setBackground(new Color(245, 247, 250));
        styleField(technicianIDField);
        card.add(technicianIDField, c);

        c.gridx = 1;
        styleField(nameField);
        card.add(nameField, c);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(saveButton);
        buttons.add(clearButton);
        buttons.add(deleteButton);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(12, 8, 5, 8);
        c.anchor = GridBagConstraints.WEST;
        card.add(buttons, c);

        return card;
    }

    
private JPanel createTableCard() {
    JPanel card = createCard();
    card.setLayout(new BorderLayout(0, 12));

    JPanel top = new JPanel(new BorderLayout(10, 0));
    top.setOpaque(false);

    JLabel heading = new JLabel("Technician Records");
    heading.setFont(font(Font.BOLD, 16));
    heading.setForeground(TEXT);

    JPanel searchPanel = new JPanel();
    searchPanel.setOpaque(false);

    styleField(searchField);
    searchField.setPreferredSize(new Dimension(190, 34));
    searchField.setToolTipText(
            "Search by technician ID or name");

    searchPanel.add(searchField);
    searchPanel.add(searchButton);
    searchPanel.add(refreshButton);

    top.add(heading, BorderLayout.WEST);
    top.add(searchPanel, BorderLayout.EAST);

    
    technicianTable.setFont(font(Font.BOLD, 15));
    technicianTable.setForeground(Color.BLACK);
    technicianTable.setBackground(WHITE);
    technicianTable.setRowHeight(40);
    technicianTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
    technicianTable.setShowHorizontalLines(true);
    technicianTable.setShowVerticalLines(true);
    technicianTable.setGridColor(Color.BLACK);
    technicianTable.setIntercellSpacing(new Dimension(1, 1));
    technicianTable.setFillsViewportHeight(true);
    technicianTable.setAutoCreateRowSorter(true);
    technicianTable.setSelectionBackground(
            new Color(229, 239, 252));
    technicianTable.setSelectionForeground(
            new Color(43, 57, 76));

    JTableHeader tableHeader = technicianTable.getTableHeader();
    tableHeader.setFont(font(Font.BOLD, 14));
    tableHeader.setForeground(Color.WHITE);
    tableHeader.setBackground(HEADER_BLUE);
    tableHeader.setPreferredSize(new Dimension(100, 42));
    tableHeader.setReorderingAllowed(false);

    JScrollPane scrollPane = new JScrollPane(technicianTable);
    scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
    scrollPane.getViewport().setBackground(WHITE);

    card.add(top, BorderLayout.NORTH);
    card.add(scrollPane, BorderLayout.CENTER);

    return card;
}


    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(15, 16, 15, 16)));
        return card;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.PLAIN, 13));
        label.setForeground(MUTED);
        return label;
    }

    private void styleField(JTextField field) {
        field.setFont(font(Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)));
        field.setPreferredSize(new Dimension(220, 36));
    }

        private void configureTable() {
        technicianTable.setSelectionBackground(
                new Color(229, 239, 252));
        technicianTable.setSelectionForeground(
                new Color(43, 57, 76));

        technicianTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedTechnician();
            }
        });
    }

    private void configureActions() {
        saveButton.addActionListener(event -> saveTechnician());
        clearButton.addActionListener(event -> clearForm());
        deleteButton.addActionListener(event -> deleteTechnician());
        refreshButton.addActionListener(event -> refreshTechnicians());
        searchButton.addActionListener(event -> refreshTechnicians());
        searchField.addActionListener(event -> refreshTechnicians());
    }

    private void configurePermissions() {
        if (!admin) {
            saveButton.setEnabled(false);
            clearButton.setEnabled(false);
            deleteButton.setEnabled(false);
            nameField.setEditable(false);
            nameField.setToolTipText(
                    "Only administrators can manage technician records.");
        }
    }

    private void refreshTechnicians() {
        String keyword = searchField.getText().trim().toLowerCase();

        tableModel.setRowCount(0);

        try {
            List<Technician> technicians = dataManager.getTechnicians();

            for (Technician technician : technicians) {
                String id = technician.getTechnicianID();
                String name = technician.getName();

                if (!id.toLowerCase().contains(keyword)
                        && !name.toLowerCase().contains(keyword)) {
                    continue;
                }

                String availability = technician.isAvailable()
                        ? "Available" : "Busy";

                tableModel.addRow(new Object[]{
                    id, name, availability
                });
            }
        } catch (RuntimeException ex) {
            showError("Unable to load technicians: " + ex.getMessage());
        }
    }

    private void loadSelectedTechnician() {
        int selectedRow = technicianTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow = technicianTable.convertRowIndexToModel(selectedRow);

        technicianIDField.setText(
                tableModel.getValueAt(modelRow, 0).toString());
        nameField.setText(
                tableModel.getValueAt(modelRow, 1).toString());

        saveButton.setText("Update Technician");
    }

    private void saveTechnician() {
        if (!admin) {
            showError("Only administrators can manage technicians.");
            return;
        }

        String technicianID = technicianIDField.getText().trim();
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showError("Enter the technician's name.");
            nameField.requestFocusInWindow();
            return;
        }

        String actorUserID = authenticatedUser.getUserID();

        try {
            if (technicianID.isEmpty()) {
                Technician added = dataManager.addTechnician(
                        actorUserID, name);

                JOptionPane.showMessageDialog(
                        this,
                        "Technician added successfully.\nTechnician ID: "
                                + added.getTechnicianID(),
                        "Technician Added",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                boolean updated = dataManager.updateTechnician(
                        actorUserID, technicianID, name);

                if (!updated) {
                    showError("The selected technician was not found.");
                    refreshTechnicians();
                    return;
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Technician updated successfully.",
                        "Technician Updated",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            clearForm();
            refreshTechnicians();

        } catch (RuntimeException ex) {
            showError("Unable to save technician: " + ex.getMessage());
        }
    }

    private void deleteTechnician() {
        if (!admin) {
            showError("Only administrators can manage technicians.");
            return;
        }

        String technicianID = technicianIDField.getText().trim();

        if (technicianID.isEmpty()) {
            showError("Select a technician from the table first.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete technician " + technicianID + "?\n"
                        + "This action cannot be undone.",
                "Confirm Technician Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = dataManager.deleteTechnician(
                    authenticatedUser.getUserID(), technicianID);

            if (!deleted) {
                showError("The selected technician was not found.");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Technician deleted successfully.",
                        "Technician Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            clearForm();
            refreshTechnicians();

        } catch (RuntimeException ex) {
            showError("Unable to delete technician: " + ex.getMessage());
        }
    }

    private void clearForm() {
        technicianTable.clearSelection();
        technicianIDField.setText("");
        nameField.setText("");
        saveButton.setText("Add Technician");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Technician Management",
                JOptionPane.WARNING_MESSAGE);
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
