package vetcare.gui;

import javax.swing.SwingUtilities;

public class MainGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
