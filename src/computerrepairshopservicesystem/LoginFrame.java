package computerrepairshopservicesystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginFrame extends JFrame {

    private static final Color BLUE = new Color(48, 105, 183);
    private static final Color NAVY_TEXT = new Color(43, 57, 76);
    private static final Color MUTED_TEXT = new Color(105, 118, 136);
    private static final Color BACKGROUND = new Color(241, 245, 250);
    private static final Color BORDER = new Color(213, 222, 233);

    private final DataManager dataManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signInButton;
    private JLabel statusLabel;

    public LoginFrame(DataManager dataManager) {
        if (dataManager == null) {
            throw new IllegalArgumentException("DataManager cannot be null.");
        }
        this.dataManager = dataManager;

        setTitle("CompCare - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560));
        setContentPane(createContent());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel createContent() {
        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(BACKGROUND);
        background.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(30, 34, 28, 34)));
        card.setPreferredSize(new Dimension(410, 445));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);

        JLabel brand = new JLabel("CompCare", SwingConstants.CENTER);
        brand.setFont(font(Font.BOLD, 25));
        brand.setForeground(NAVY_TEXT);
        c.gridy = 0;
        c.insets = new Insets(0, 0, 3, 0);
        card.add(brand, c);

        JLabel subtitle = new JLabel("Computer Repair Services", SwingConstants.CENTER);
        subtitle.setFont(font(Font.PLAIN, 13));
        subtitle.setForeground(MUTED_TEXT);
        c.gridy = 1;
        c.insets = new Insets(0, 0, 28, 0);
        card.add(subtitle, c);

        JLabel heading = new JLabel("Welcome back", SwingConstants.CENTER);
        heading.setFont(font(Font.BOLD, 19));
        heading.setForeground(NAVY_TEXT);
        c.gridy = 2;
        c.insets = new Insets(0, 0, 5, 0);
        card.add(heading, c);

        JLabel support = new JLabel("Sign in to continue to your account", SwingConstants.CENTER);
        support.setFont(font(Font.PLAIN, 13));
        support.setForeground(MUTED_TEXT);
        c.gridy = 3;
        c.insets = new Insets(0, 0, 24, 0);
        card.add(support, c);

        JLabel usernameLabel = fieldLabel("Username");
        c.gridy = 4;
        c.insets = new Insets(0, 0, 6, 0);
        card.add(usernameLabel, c);

        usernameField = new JTextField();
        styleField(usernameField);
        c.gridy = 5;
        c.insets = new Insets(0, 0, 15, 0);
        card.add(usernameField, c);

        JLabel passwordLabel = fieldLabel("Password");
        c.gridy = 6;
        c.insets = new Insets(0, 0, 6, 0);
        card.add(passwordLabel, c);

        passwordField = new JPasswordField();
        styleField(passwordField);
        c.gridy = 7;
        c.insets = new Insets(0, 0, 20, 0);
        card.add(passwordField, c);

        signInButton = new JButton("Sign In");
        signInButton.setFont(font(Font.BOLD, 14));
        signInButton.setForeground(Color.WHITE);
        signInButton.setBackground(BLUE);
        signInButton.setFocusPainted(false);
        signInButton.setPreferredSize(new Dimension(300, 40));
        signInButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        c.gridy = 8;
        c.insets = new Insets(0, 0, 12, 0);
        card.add(signInButton, c);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(font(Font.PLAIN, 12));
        statusLabel.setForeground(MUTED_TEXT);
        c.gridy = 9;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(statusLabel, c);

        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.anchor = GridBagConstraints.CENTER;
        background.add(card, outer);

        signInButton.addActionListener(event -> signIn());
        getRootPane().setDefaultButton(signInButton);
        return background;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.PLAIN, 13));
        label.setForeground(NAVY_TEXT);
        return label;
    }

    private void styleField(JTextField field) {
        field.setFont(font(Font.PLAIN, 14));
        field.setForeground(NAVY_TEXT);
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(300, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(5, 9, 5, 9)));
    }

    private Font font(int style, int size) {
        String family = Font.SANS_SERIF;
        for (String available : java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            if ("Segoe UI".equalsIgnoreCase(available)) {
                family = available;
                break;
            }
        }
        return new Font(family, style, size);
    }

    private void signIn() {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();

        try {
            if (username.isEmpty() || passwordChars.length == 0) {
                statusLabel.setForeground(new Color(164, 101, 29));
                statusLabel.setText("Enter both your username and password.");
                JOptionPane.showMessageDialog(this,
                        "Please enter both your username and password.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // The existing DataManager API accepts String passwords.
            SystemUser authenticatedUser = dataManager.authenticate(
                    username, new String(passwordChars));

            if (authenticatedUser == null) {
                statusLabel.setForeground(new Color(174, 55, 55));
                statusLabel.setText("Sign in failed. Check your credentials or account status.");
                passwordField.setText("");
                passwordField.requestFocusInWindow();
                JOptionPane.showMessageDialog(this,
                        "Sign-in failed. Check your credentials. Three consecutive "
                        + "incorrect password attempts block the account.",
                        "Unable to Sign In", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirm that the backend still recognizes this authenticated session.
            String activeUserID = dataManager.getCurrentAuthenticatedUserID();
            if (activeUserID == null || !activeUserID.equals(authenticatedUser.getUserID())) {
                dataManager.logout();
                throw new SecurityException("The authenticated session is no longer active.");
            }

            MainFrame mainFrame = new MainFrame(dataManager, authenticatedUser);
            mainFrame.setVisible(true);
            dispose();

        } catch (RuntimeException exception) {
            dataManager.logout();
            statusLabel.setForeground(new Color(174, 55, 55));
            statusLabel.setText("A system error occurred. Please try again.");
            passwordField.setText("");
            JOptionPane.showMessageDialog(this,
                    "The system could not complete sign-in. Please try again.",
                    "Sign-In Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }
}

