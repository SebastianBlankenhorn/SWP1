package de.hft.ip1.GameOfLife.Client;

import javax.swing.*;

/**
 *
 * JSP: Nur mittelmäßig bearbeitet, damit lauffähig
 */
public class VerbindungsInformation {

    public String rechnername;
    public Integer port;
    public static final int MAX_PORT_VALUE = 65535;

    public void openDialog() {

        rechnername = JOptionPane.showInputDialog("Rechnername: ");

        String port_tmp = null;

        port_tmp = JOptionPane.showInputDialog("Port: ");
        try {
            port = Integer.parseInt(port_tmp);
            if (port > MAX_PORT_VALUE) {
                throw new NumberFormatException("port out of range");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Als Port sind nur ganze Zahlen bis 65535 erlaubt");
            port = null;
        }

    }

}
