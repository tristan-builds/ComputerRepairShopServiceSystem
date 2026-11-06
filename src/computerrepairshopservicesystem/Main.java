
package computerrepairshopservicesystem;


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        SwingUtilities.invokeLater(() -> {
            DataManager dataManager = new DataManager();

            boolean initialized = dataManager.initializeDefaultAccounts();
            if (!initialized) {
                JOptionPane.showMessageDialog(
                        null,
                        "Startup failed. Default accounts could not be initialized.",
                        "Initialization Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LoginFrame loginFrame = new LoginFrame(dataManager);
            loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            loginFrame.setVisible(true);
        });
    }
}
