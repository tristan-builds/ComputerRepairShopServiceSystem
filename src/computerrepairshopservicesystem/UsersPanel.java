
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class UsersPanel extends JPanel {

    private static final Color BACKGROUND =
            new Color(241, 245, 250);
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER =
            new Color(216, 224, 234);
    private static final Color TEXT =
            new Color(43, 57, 76);
    private static final Color MUTED =
            new Color(105, 118, 136);
    private static final Color BLUE =
            new Color(48, 105, 183);
    private static final Color LIGHT_BLUE =
            new Color(229, 239, 252);
    private static final Color HEADER_BLUE =
            new Color(43, 78, 120);
    private static final Color RED =
            new Color(190, 55, 55);

    private final DataManager dataManager;
    private final SystemUser authenticatedUser;

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);
    private final JTextField searchField = new JTextField(20);
    private final JLabel countLabel = new JLabel("0 users");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                        "User ID",
                        "Username",
                        "Role",
                        "Account Status",
                        "Failed Attempts"
                    }, 0) {
                @Override
                public boolean isCellEditable(
                        int row, int column) {
                    return false;
                }
            };

    private final JTable userTable = new JTable(tableModel);

    private final JButton createButton =
            new JButton("Create Staff Account");
    private final JButton unblockButton =
            new JButton("Unblock Account");
    private final JButton deleteButton =
            new JButton("Delete Account");
    private final JButton refreshButton =
            new JButton("Refresh");
    private final JButton clearButton =
            new JButton("Clear");

    public UsersPanel(
            DataManager dataManager,
            SystemUser authenticatedUser) {

        if (dataManager == null || authenticatedUser == null) {
            throw new IllegalArgumentException(
                    "Data manager and authenticated user are required.");
        }

        if (!SystemUser.ADMIN.equals(authenticatedUser.getRole())) {
            throw new SecurityException(
                    "Only administrators can manage user accounts.");
        }

        this.dataManager = dataManager;
        this.authenticatedUser = authenticatedUser;

        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
                22, 24, 22, 24));

        styleTextField(usernameField);
        styleTextField(passwordField);
        styleTextField(searchField);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        configureTable();

        createButton.addActionListener(
                event -> createStaffAccount());
        unblockButton.addActionListener(
                event -> unblockSelectedUser());
        deleteButton.addActionListener(
                event -> deleteSelectedUser());
        refreshButton.addActionListener(
                event -> refreshUsers());
        clearButton.addActionListener(
                event -> clearForm());

        searchField.addActionListener(
                event -> refreshUsers());

        userTable.getSelectionModel().addListSelectionListener(
                event -> {
                    if (!event.getValueIsAdjusting()) {
                        updateSelectionButtons();
                    }
                });

        refreshUsers();
    }

    // ---------------- PAGE HEADER ----------------

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(BACKGROUND);

        JLabel title = new JLabel("User Management");
        title.setFont(font(Font.BOLD, 25));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Create staff accounts and manage account access.");
        subtitle.setFont(font(Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        JPanel labels = new JPanel(new BorderLayout(0, 5));
        labels.setBackground(BACKGROUND);
        labels.add(title, BorderLayout.NORTH);
        labels.add(subtitle, BorderLayout.CENTER);

        panel.add(labels, BorderLayout.CENTER);
        return panel;
    }

    // ---------------- MAIN CONTENT ----------------

    private JPanel createContent() {
        JPanel content = new JPanel(
                new BorderLayout(0, 18));
        content.setBackground(BACKGROUND);

        // Staff account form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(
                        16, 18, 16, 18)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 5, 8, 12);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        form.add(label("New staff username"), c);

        c.gridx = 1;
        c.weightx = 1;
        form.add(usernameField, c);

        c.gridx = 2;
        c.weightx = 0;
        form.add(label("Initial password"), c);

        c.gridx = 3;
        c.weightx = 1;
        form.add(passwordField, c);

        c.gridx = 4;
        c.weightx = 0;
        c.insets = new Insets(8, 8, 8, 5);
        form.add(createPrimaryButton(createButton), c);

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 4));
        actions.setBackground(WHITE);

        actions.add(createSecondaryButton(unblockButton));
        actions.add(createDangerButton(deleteButton));
        actions.add(createSecondaryButton(clearButton));

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 5;
        c.weightx = 1;
        c.insets = new Insets(8, 5, 0, 5);
        form.add(actions, c);

        // User records table card
        JPanel tableCard = new JPanel(
                new BorderLayout(0, 14));
        tableCard.setBackground(WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(
                        18, 18, 18, 18)));

        JLabel listTitle = new JLabel("System Accounts");
        listTitle.setFont(font(Font.BOLD, 18));
        listTitle.setForeground(TEXT);

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(WHITE);

        JLabel searchLabel = label("Search");
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(createSecondaryButton(refreshButton));

        JPanel toolbar = new JPanel(
                new BorderLayout(0, 12));
        toolbar.setBackground(WHITE);
        toolbar.add(listTitle, BorderLayout.NORTH);
        toolbar.add(searchPanel, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(userTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        scroll.setPreferredSize(new Dimension(700, 300));

        countLabel.setFont(font(Font.PLAIN, 12));
        countLabel.setForeground(MUTED);

        tableCard.add(toolbar, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);
        tableCard.add(countLabel, BorderLayout.SOUTH);

        content.add(form, BorderLayout.NORTH);
        content.add(tableCard, BorderLayout.CENTER);

        return content;
    }

    // ---------------- TABLE STYLING ----------------

    private void configureTable() {
        userTable.setFont(font(Font.BOLD, 15));
        userTable.setForeground(TEXT);
        userTable.setBackground(WHITE);
        userTable.setRowHeight(40);
        userTable.setFillsViewportHeight(true);

        userTable.setShowHorizontalLines(true);
        userTable.setShowVerticalLines(true);
        userTable.setGridColor(Color.BLACK);
        userTable.setIntercellSpacing(
                new Dimension(1, 1));

        userTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        userTable.setSelectionBackground(LIGHT_BLUE);
        userTable.setSelectionForeground(TEXT);

        JTableHeader header = userTable.getTableHeader();
        header.setFont(font(Font.BOLD, 14));
        header.setForeground(WHITE);
        header.setBackground(HEADER_BLUE);
        header.setPreferredSize(new Dimension(100, 42));
        header.setReorderingAllowed(false);
    }

    // ---------------- STYLING HELPERS ----------------

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

    private JLabel label(String text) {
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

    private JButton createPrimaryButton(JButton button) {
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

    private JButton createSecondaryButton(JButton button) {
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

    private JButton createDangerButton(JButton button) {
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(RED);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(
                10, 16, 10, 16));
        return button;
    }

    // ---------------- ACCOUNT OPERATIONS ----------------

    private void createStaffAccount() {
        String username = usernameField.getText().trim();
        String password =
                new String(passwordField.getPassword());

        try {
            SystemUser created = dataManager.createStaffAccount(
                    authenticatedUser.getUserID(),
                    username,
                    password);

            JOptionPane.showMessageDialog(
                    this,
                    "Staff account created.\nUser ID: "
                            + created.getUserID(),
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            refreshUsers();

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void unblockSelectedUser() {
        SystemUser selected = getSelectedUser();

        if (selected == null) {
            return;
        }

        if (!selected.isBlocked()) {
            JOptionPane.showMessageDialog(
                    this,
                    "The selected account is not blocked.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Unblock account '" + selected.getUsername() + "'?",
                "Confirm Unblock",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dataManager.unblockUser(
                    authenticatedUser.getUserID(),
                    selected.getUserID());

            refreshUsers();

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void deleteSelectedUser() {
        SystemUser selected = getSelectedUser();

        if (selected == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete account '" + selected.getUsername()
                        + "' (" + selected.getUserID() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (dataManager.deleteSystemUser(
                    authenticatedUser.getUserID(),
                    selected.getUserID())) {
                refreshUsers();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "The account could not be found.");
            }

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private SystemUser getSelectedUser() {
        int row = userTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a user account first.");
            return null;
        }

        String id = String.valueOf(
                tableModel.getValueAt(
                        userTable.convertRowIndexToModel(row), 0));

        try {
            return dataManager.getSystemUserByID(id);
        } catch (RuntimeException exception) {
            showError(exception);
            return null;
        }
    }

    private void refreshUsers() {
        try {
            String query = searchField.getText()
                    .trim().toLowerCase();

            tableModel.setRowCount(0);

            List<SystemUser> users =
                    dataManager.getSystemUsers();

            int shown = 0;

            for (SystemUser user : users) {
                String status =
                        user.isBlocked() ? "Blocked" : "Active";

                String attempts =
                        String.valueOf(user.getFailedLoginAttempts());

                String searchable = (
                        user.getUserID() + " "
                        + user.getUsername() + " "
                        + user.getRole() + " "
                        + status
                ).toLowerCase();

                if (!searchable.contains(query)) {
                    continue;
                }

                tableModel.addRow(new Object[]{
                    user.getUserID(),
                    user.getUsername(),
                    user.getRole(),
                    status,
                    attempts
                });

                shown++;
            }

            countLabel.setText(
                    shown + " of " + users.size() + " users");

            updateSelectionButtons();

        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void updateSelectionButtons() {
        int row = userTable.getSelectedRow();
        boolean selected = row >= 0;

        deleteButton.setEnabled(selected);
        unblockButton.setEnabled(false);

        if (selected) {
            Object status = tableModel.getValueAt(
                    userTable.convertRowIndexToModel(row), 3);

            unblockButton.setEnabled(
                    "Blocked".equals(status));
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        userTable.clearSelection();
        updateSelectionButtons();
    }

    private void showError(RuntimeException exception) {
        JOptionPane.showMessageDialog(
                this,
                exception.getMessage() == null
                        ? "The operation could not be completed."
                        : exception.getMessage(),
                "User Management Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
