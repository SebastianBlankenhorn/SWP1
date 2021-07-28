package de.hft.ip1.GameOfLife.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnleitungForm {
    public JPanel panel1;
    private JTextPane textPane1;
    private JButton anleitungSchliessenButton;
    private JScrollPane scrollPane;
    private GameOfLifeForm anleitungForm;

    public AnleitungForm(JFrame anleitungFrame) {
        String message =
                "------------------------------------------------------------------------------------------------\n" +
                "Spielerklärung:\n" +
                "------------------------------------------------------------------------------------------------\n" +
                "Im GameOfLife kann jede Gitterzelle einen von zwei Zuständen einnehmen: tot oder lebendig.             \n" +
                "GameOfLife wird durch vier einfache Regeln kontrolliert, die auf jede Gitterzelle im Simulationsbereich\n" +
                "angewendet werden:\n" +
                "1. Regel:\n" +
                "Eine lebendige Zelle stirbt, wenn sie weniger als zwei lebendige Nachbarzellen hat:\n" +
                "2. Regel:\n" +
                "Eine lebendige Zelle mit zwei oder drei lebenden Nachbarn lebt weiter\n" +
                "3. Regel:\n" +
                "Eine lebendige Zelle mit mehr als drei lebenden Nachbarzellen stirbt im nächsten Zeitschritt\n" +
                "4. Regel:\n" +
                "Eine tote Zelle wird wiederbelebt, wenn sie genau drei lebende Nachbarzellen hat.\n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Bedienerklärung:\n" +
                "------------------------------------------------------------------------------------------------\n" +
                "Teilnehmen:\n" +
                "Damit können sie sich mit einem Server verbinden IPAdresse und im nächsten Dialog Port eingeben\n" +
                "Danach können sie wenn mehrere Spielfelder zur Verfügung stehen eines oder mehrere auswählen  \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Verlassen:\n" +
                "Hiermit Verlassen sie den Server und alle Spielfelder werden geschlossen                       \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Neu:\n" +
                "Hier können sie die Maße des neuen Spielfeldes angeben und den Namen                           \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Öffnen:\n" +
                "Hiermit können sie von ihrem lokalen Rechner eine Spieldatei öffnen und spielen                \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Speichern:\n" +
                "Damit wird das aktuell im Vordergrund laufende Spiel gespeichert                               \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Speichern als:\n" +
                "Hier können sie den Ort für das Speichern des aktuell im vordergrund laufende Spiel            \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Schließen:\n" +
                "Hiermit wird das im Vordergrund laufende Spiel geschlossen                                     \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Beenden:\n" +
                "Beendet das Programm nachdem alle Spielfelder geschlossen wurden                               \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Maße ändern:\n" +
                "Dialog für eingabe mit den neuen Maßen vom aktuellen Spielfeld Vergrößerung und Verkleinerung  \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Schneller:\n" +
                "Erhöht vom aktuellen Spielfeld die Spielgeschwindigkeit                                        \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Langsamer:\n" +
                "Verringert vom aktuellen Spielfeld die Spielgeschwindigkeit                                    \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Starten:\n" +
                "Startet vom aktuellen Spielfeld die Simulation                                                 \n" +
                "-----------------------------------------------------------------------------------------------\n" +
                "Anhalten:\n" +
                "Hält vom aktuellen Spielfeld die Simulation an                                                 \n";

        anleitungSchliessenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel1.getParent().setVisible(false);
                anleitungFrame.dispose();
            }
        });
        textPane1.setText(message);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
