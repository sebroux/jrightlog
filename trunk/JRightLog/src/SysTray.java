
import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author seb
 */
public class SysTray {

    public static void main(String[] args) {

        final TrayIcon trayIcon;

        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("img/misc.png");

            MouseListener mouseListener = new MouseListener() {

                public void mouseClicked(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse clicked!");
                }

                public void mouseEntered(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse entered!");
                }

                public void mouseExited(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse exited!");
                }

                public void mousePressed(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse pressed!");
                }

                public void mouseReleased(MouseEvent e) {
                    System.out.println("Tray Icon - Mouse released!");
                }
            };

            ActionListener exitListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
            };

            ActionListener aboutListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    System.out.println("About...");

                }
            };

            ActionListener showListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    System.out.println("Show");
                    Frame GUIFrame = new Frame();

                    // Hide the frame by setting its visibility as false
                    //GUIFrame.setVisible(false);


                }
            };

            PopupMenu popup = new PopupMenu();
            MenuItem defaultItem = new MenuItem("Exit");
            MenuItem aboutItem = new MenuItem("About");
            MenuItem showItem = new MenuItem("Show");
            defaultItem.addActionListener(exitListener);
            aboutItem.addActionListener(aboutListener);
            showItem.addActionListener(showListener);
            popup.add(defaultItem);
            popup.addSeparator();
            popup.add(aboutItem);
            popup.add(showItem);

            trayIcon = new TrayIcon(image, "Tray Demo", popup);

            ActionListener actionListener = new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    trayIcon.displayMessage("Action Event",
                            "An Action Event Has Been Performed!",
                            TrayIcon.MessageType.INFO);
                }
            };

            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(actionListener);
            trayIcon.addMouseListener(mouseListener);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }

        } else {
            //  System Tray is not supported
        }
    }
}
