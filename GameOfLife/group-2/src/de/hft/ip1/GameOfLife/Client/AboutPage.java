package de.hft.ip1.GameOfLife.Client;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class AboutPage {
    public AboutPage(JFrame parent){
        JOptionPane.showMessageDialog(parent,
                "Unser Team im IP1 Projekt:\n" +
                        "Sebastian Blankenhorn " +
                        "@92blse1bif" +
                        "\n-----------------------\n" +
                        "Vajos Amarantidis " +
                        "@01amva1bif" +
                        "\n-----------------------\n" +
                        "Jochen Spender " +
                        "@92spjo1bif" +
                        "\n-----------------------\n" +
                        "Yasin Arslan " +
                        "@91arya1bif" +
                        "\n-----------------------\n" +
                        "Quellcoderepository:\n" +
                        "Klick auf ok dann kannst du auswählen ob du die Seite aufrufen möchtest\n" +
                        "https://gitlab.rz.hft-stuttgart.de/IP1/SS20/group-2/","Über uns" ,JOptionPane.DEFAULT_OPTION);

        if (JOptionPane.showConfirmDialog(null, "Link zum Repo", "Link oeffnen?",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            // yes option
            if( !Desktop.isDesktopSupported() ) {

                System.err.println( "Desktop is not supported (fatal)" );

            }

            Desktop desktop = Desktop.getDesktop();

            if( !desktop.isSupported( Desktop.Action.BROWSE ) ) {

                System.err.println( "Desktop doesn't support the browse action (fatal)" );
                System.exit( 1 );
            }

            try {

                URI uri = new URI( "https://gitlab.rz.hft-stuttgart.de/IP1/SS20/group-2/-/tree/master" );
                desktop.browse( uri );
            }
            catch ( Exception es ) {

                System.err.println( es.getMessage() );
            }
        } else {
            // no option
        }
    }
}
