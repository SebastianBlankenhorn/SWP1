package de.hft.ip1.GameOfLife.Client;

import de.hft.ip1.GameOfLife.Server.IGameOfLifeEngine;
import de.hft.ip1.GameOfLife.Server.IGameOfLifeSaveFile;
import de.hft.ip1.GameOfLife.Server.IServer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class GameOfLifeForm {

    // GUI Components
    private JFrame frm_Main;
    private JPanel pnl_Main;
    private JTabbedPane tbp_Content; // Darstellung des GameOfLife mehrere gleichzeitig tab style
    private JMenuBar menuBar;
    private JMenu datei, hilfe, bearbeiten;
    private JMenuItem mit_Schliesen, mit_Beenden, mit_SpeichernAls, mit_Teilnehmen, mit_Verlassen, mit_Neu, mit_Oeffnen, mit_Speichern, mit_AendereMase, mit_Schneller, mit_Langsamer, mit_Starten, mit_Anhalten, mit_Anleitung, mit_Ueber;
    private PanelHolder panelHolder;

    // Server RMI Object
    IServer server = null;

    Registry registry = null;

    VerbindungsInformation verbindungsInformationServer = null;

    /**
     * Creates a new Gui for the client
     */
    public GameOfLifeForm() {

        // Init gui
        frm_Main = new JFrame("Game of Life Multiplayer ");
        frm_Main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm_Main.setMinimumSize(new Dimension(500, 300));
        frm_Main.setContentPane(pnl_Main);
        frm_Main.setLocationRelativeTo(null);
        frm_Main.pack();
        frm_Main.setVisible(true);

        // ActionListener
        mit_Schliesen.addActionListener(e -> {
            if (testeServerVerbindung()) {
                schlieseSpielfeld();
            }
        });
        mit_Beenden.addActionListener(e -> beendeSpiel());
        mit_Teilnehmen.addActionListener(e -> teilnehmenServer());
        mit_Verlassen.addActionListener(e -> verlasseServer());
        mit_Neu.addActionListener(e -> neuesSpielErstellen());
        mit_Oeffnen.addActionListener(e -> {
            if (testeServerVerbindung()) oeffneLokalesSpiel();
        });
        mit_Speichern.addActionListener(e -> {
            if (testeServerVerbindung()) speichereSpiel(false);
        });
        mit_SpeichernAls.addActionListener(e -> {
            if (testeServerVerbindung()) speichereSpiel(true);
        });
        mit_AendereMase.addActionListener(e -> aendereMase());
        mit_Schneller.addActionListener(e -> spieleSchneller());
        mit_Langsamer.addActionListener(e -> spieleLangsamer());
        mit_Starten.addActionListener(e -> starteSpiel());
        mit_Anhalten.addActionListener(e -> stoppeSpiel());
        mit_Anleitung.addActionListener(e -> zeigeAnleitung());
        mit_Ueber.addActionListener(e -> zeigeUeber());
    }

    private void teilnehmenServer() {

        if (server == null) {
            verbindungsInformationServer = new VerbindungsInformation();
            verbindungsInformationServer.openDialog();

            try {
                registry = LocateRegistry.getRegistry(verbindungsInformationServer.rechnername, verbindungsInformationServer.port);
            } catch (RemoteException | NullPointerException remoteException) {
                showErrorMessage("Es konnte keine Verbindung zum Server hergestellt werden (Verbindungsdaten falsch?)", remoteException);
            }

            if (registry != null) {
                try {
                    server = (IServer) registry.lookup(IServer.class.getName());
                } catch (RemoteException remoteException) {
                    showErrorMessage("Verbindung zum Server ist verloren gegangen.", remoteException);
                } catch (NotBoundException e) {
                    showErrorMessage("Es konnte keine Verbindung zum Server hergestellt werden (Server nicht gestartet?", e);
                }

                if (server != null) {
                    showErrorMessage("Erfolgreich mit dem Server verbunden");
                }
            }

            if (server != null) {
                panelHolder.setServer(server);
            }
        }

        // not else, because if an error occurs while connecting, there is still no server
        if (server != null) {
            try {
                panelHolder.askForNewGameNames();
            } catch (RemoteException remoteException) {
                showErrorMessage("Ein Verbindungsfehler ist aufgetreten, es konnten keine Spiele geladen werden.", remoteException);
            }
        }
    }

    private void schlieseSpielfeld() {
        try {
            if(panelHolder.getActivePanel().getGameOfLifeEngine().getLastChanged() >
                    panelHolder.getActivePanel().getLastSavedTime()){
                int test = JOptionPane.showConfirmDialog(null,"ungespeicherte Aenderung trotzdem verlassen?",
                        "Datenverlust",JOptionPane.YES_NO_OPTION);
                if(test == JOptionPane.YES_OPTION ){
                    try {
                        panelHolder.closeActiveGame();
                    } catch (RemoteException remoteException) {
                        panelHolder.closeActivePanel();
                        showErrorMessage("Ein Verbindungsfehler ist aufgetreten, Spiel konnte auf Server nicht deregistriert werden.\nSpiel wurde lokal entfernt.", remoteException);
                    }
                }
            }else{
                try {
                    panelHolder.closeActiveGame();
                } catch (RemoteException remoteException) {
                    panelHolder.closeActivePanel();
                    showErrorMessage("Ein Verbindungsfehler ist aufgetreten, Spiel konnte auf Server nicht deregistriert werden.\nSpiel wurde lokal entfernt.", remoteException);
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void starteSpiel() {
        if (panelHolder.hasActivePanel()) {
            try {
                panelHolder.getActivePanel().getGameOfLifeEngine().start();
            } catch (RemoteException remoteException) {
                showErrorMessage("Ein Verbindungsfehler ist aufgetreten.", remoteException);
            }
        }
    }

    private void stoppeSpiel() {
        if (panelHolder.hasActivePanel()) {
            try {
                panelHolder.getActivePanel().getGameOfLifeEngine().stop();
            } catch (RemoteException remoteException) {
                showErrorMessage("Ein Verbindungsfehler ist aufgetreten.", remoteException);
            }
        }
    }

    private void spieleLangsamer() {
        if (panelHolder.hasActivePanel()) {
            try {
                panelHolder.getActivePanel().getGameOfLifeEngine().slower();
            } catch (RemoteException remoteException) {
                showErrorMessage("Ein Verbindungsfehler ist aufgetreten.", remoteException);
            }
        }
    }

    private void spieleSchneller() {
        if (panelHolder.hasActivePanel()) {
            try {
                panelHolder.getActivePanel().getGameOfLifeEngine().faster();
            } catch (RemoteException remoteException) {
                showErrorMessage("Ein Verbindungsfehler ist aufgetreten.", remoteException);
            }
        }
    }

    private void neuesSpielErstellen() {

        if (server != null) {
            int breiteI = 0;
            int hoeheI = 0;
            boolean torusB = false;
            JTextField name = new JTextField();
            JTextField hoehe = new JTextField();
            JTextField breite = new JTextField();
            JTextField torus = new JTextField();

            Object[] message = {
                    "Name:", name,
                    "Hoehe:", hoehe,
                    "Breite:", breite,
                    "Torus:(true/false)", torus
            };


            JOptionPane pane = new JOptionPane(message,
                    JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION);
            pane.createDialog(null, "Neu").setVisible(true);

            try {
                torusB = Boolean.parseBoolean(torus.getText());
                breiteI = Integer.parseInt(breite.getText());
                hoeheI = Integer.parseInt(hoehe.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(pnl_Main, ex.getMessage());
            }

            if (hoeheI < 5 || breiteI < 5 || hoeheI > 120 || breiteI > 160) {
                JOptionPane.showMessageDialog(pnl_Main, "Werte nicht in Grenzbereich von (120 zeilen, 160 spalten)");
                return;
            }

            if (name.getText().equals("")) {
                System.out.println("no name");
                fillCorrectly();
            } else if (hoehe.getText().equals("")) {
                System.out.println("no hoehe name");
                fillCorrectly();
            } else if (!hoehe.getText().chars().allMatch(Character::isDigit)) {
                System.out.println("no zahl hoehe");
                fillCorrectly();
            } else if (breite.getText().equals("")) {
                System.out.println("no breite name");
                fillCorrectly();
            } else if (!breite.getText().chars().allMatch(Character::isDigit)) {
                System.out.println("no zahl breite");
                fillCorrectly();
            } else if (torus.getText().equals("")) {
                System.out.println("no torus name");
                fillCorrectly();
            } else if (!torus.getText().equals("true")) {
                if (!torus.getText().equals("false")) {
                    System.out.println("not true or false");
                    fillCorrectly();
                }
            }

            addGameToServer(name.getText(), breiteI, hoeheI, torusB, null);
        } else {
            showErrorMessage("Zuerst mit einem Server verbinden");
        }
    }

    public boolean addGameToServer(String name, int width, int height, boolean torus, boolean[] data) {
        boolean createNewGame = true;
        boolean overwriteGame = false;
        boolean gameAdded = false;
        try {
            if (server.existName(name)) {
                int overwriteExistingGame = JOptionPane.showConfirmDialog(null, "Es existiert schon ein Spiel mit diesem Namen, soll es überschrieben werden?", "Spielname schon vorhanden", JOptionPane.OK_CANCEL_OPTION);
                if (overwriteExistingGame == JOptionPane.OK_OPTION) {
                    overwriteGame = true;
                } else {
                    createNewGame = false;
                }
            }
        } catch (RemoteException remoteException) {
            showErrorMessage("Verbindungsfehler, Spiel kann nicht erstellt werden", remoteException);
            createNewGame = false;
        }

        if (createNewGame) {
            try {
                gameAdded = server.createNewGame(name, width, height, torus, overwriteGame, data);
                if (gameAdded) {
                    panelHolder.addNewGame(name);
                } else {
                    showErrorMessage("Kein Spiel erstellt. Es wurde in der Zwischenzeit schon ein Spiel mit diesem Namen angelegt");
                }
            } catch (RemoteException remoteException) {
                showErrorMessage("Verbindungsfehler, Spiel kann nicht erstellt werden", remoteException);
            }
        }

        return gameAdded;
    }

    /**
     * Display an error message to the user
     *
     * @param message Error message for the user
     */
    public static void showErrorMessage(String message) {
        showErrorMessage(message, null);
    }

    /**
     * Display an error message to the user and write exception message to console
     *
     * @param message Error message for the user
     * @param ex      thrown exception
     */
    public static void showErrorMessage(String message, Exception ex) {
        JOptionPane.showMessageDialog(null, message, "Fehler", JOptionPane.ERROR_MESSAGE);
        if (ex != null) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Methodes below are not checked yet
     **/


    private void fillCorrectly() {
        JOptionPane.showMessageDialog(null, "Bitte korrekt ausfüllen\n" +
                "Hohe Breite nur zahl\n" +
                "torus = false oder true");
    }

    private void zeigeUeber() {
        AboutPage aboutPage = new AboutPage(frm_Main);
    }

    private void zeigeAnleitung() {
        //scrollbar
        JFrame anleitungFrame = new JFrame("Anleitung to GameOfLife Multiplayer");

        anleitungFrame.setContentPane(new AnleitungForm(anleitungFrame).panel1);
        anleitungFrame.setPreferredSize(new Dimension(720,480));
        anleitungFrame.pack();
        anleitungFrame.setVisible(true);
        anleitungFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void aendereMase() {

        GameOfLifePanel active_panel = panelHolder.getActivePanel();
        if (active_panel != null) {
            try {
                if (!active_panel.getGameOfLifeEngine().isTimerStarted()) {
                    String tab_name = active_panel.getName();

                    JTextField zeileFeld = new JTextField();
                    JTextField spalteFeld = new JTextField();

                    Object[] eingabe = {"Zeilen:", zeileFeld,
                            "Spalten:", spalteFeld};

                    JOptionPane pane = new JOptionPane(eingabe,
                            JOptionPane.PLAIN_MESSAGE,
                            JOptionPane.OK_CANCEL_OPTION);
                    pane.createDialog(null, "Maße aendern").setVisible(true);
                    try {
                        int zeile = Integer.parseInt(zeileFeld.getText());
                        int spalte = Integer.parseInt(spalteFeld.getText());
                        if (zeile < 5 || spalte < 5 || zeile > 120 || spalte > 160) {
                            JOptionPane.showMessageDialog(pnl_Main, "Werte nicht in Grenzbereich von (120 zeilen, 160 spalten)");
                            return;
                        }

                        IGameOfLifeEngine engine = server.getGameOfLifeEngine(tab_name);
                        engine.setDimension(spalte, zeile, engine.isTorus());
                        active_panel.getGameOfLifeEngine().repaintAllPanels();


                    } catch (NumberFormatException | RemoteException ex) {
                        JOptionPane.showMessageDialog(pnl_Main, ex.getMessage());
                    }
                }else{
                    showErrorMessage("Spiel anhalten bevor du die Masse aendern willst");
                }
            } catch (RemoteException e) {
                showErrorMessage("remote Exception Verbindungsfehler ");
            }

        }
    }


    private void verlasseServer() {
        if (registry != null) {
            int dialogReturn = JOptionPane.showConfirmDialog(null, "Du bist dabei den Server zu Verlassen\n Fortfahren?", "Warnung", JOptionPane.YES_NO_OPTION);
            if (dialogReturn == JOptionPane.YES_OPTION) {
                try {
                    while (panelHolder.hasActivePanel()) {
                        panelHolder.closeActiveGame();
                    }
                    server = null;
                    registry = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Du bist aktuell mit keinem Server verbunden!");
        }
    }

    private void beendeSpiel() {
        if (panelHolder.getTabbedPane().getTabCount() != 0) {
            JOptionPane.showMessageDialog(frm_Main, "Bitte zuerst alle Tabs Schließen!");
        } else {
            frm_Main.setVisible(false);
            frm_Main.dispose();
            System.exit(0);
        }
    }

    private boolean testeServerVerbindung() {
        return server != null;
    }

    private void speichereSpiel(boolean saveAs) {

        boolean cancelSafeFile = false;

        try {
            if (panelHolder.getActivePanel().getGameOfLifeEngine().isTimerStarted()) {
                showErrorMessage("Spiel muss zum Speichern angehalten werden!");
                cancelSafeFile = true;
            }
        } catch (RemoteException remoteException) {
            showErrorMessage("Es ist ein Verbindungsfehler zum Server aufgetreten!", remoteException);
        }

        String path = panelHolder.getActivePanel().getPath();

        System.err.println(path);

        while (!cancelSafeFile) {

            // If engine was not opened by a file, the user must select a file to safe the game in
            if (panelHolder.getActivePanel().getPath() == null || saveAs) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setApproveButtonText("Speichern");
                fileChooser.setDialogTitle("Spieldatei mit_Speichern");
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileFilter(new FileNameExtensionFilter("'Game of Live' files only (*.gol)", "gol"));

                // Show FileChooser and continue if user selected a file
                if (fileChooser.showSaveDialog(frm_Main) == JFileChooser.APPROVE_OPTION) {

                    if (fileChooser.getSelectedFile().exists()) {
                        if (JOptionPane.showConfirmDialog(frm_Main, "Datei existiert schon - überschreiben?", "Achtung!", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                            path = fileChooser.getSelectedFile().getAbsolutePath();
                        } else {
                            cancelSafeFile = true;
                        }
                    } else {
                        path = fileChooser.getSelectedFile().getAbsolutePath();
                    }

                } else { // If user canceled the FileChooser, stop saving the game
                    cancelSafeFile = true;
                }
            }

            // If saving should proceed
            if (!cancelSafeFile) {

                // Set correct file-extension if necessary
                int lastIndexOfDot = path.lastIndexOf(".");
                if (lastIndexOfDot == -1) {
                    path += ".gol";
                } else if (!path.substring(lastIndexOfDot).equals("gol")) {
                    path = path.substring(0, lastIndexOfDot + 1) + "gol";
                }

                // Save engine to file...
                try (FileOutputStream fos = new FileOutputStream(path);
                     ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                    // Serialize Object and write it in File
                    oos.writeObject(panelHolder.getActivePanel().getGameOfLifeEngine().getSaveFile());

                    // Save path in gamefield
                    panelHolder.getActivePanel().setPath(path);
                    cancelSafeFile = true;
                    JOptionPane.showMessageDialog(frm_Main, "Speichern war erfolgreich!");
                    panelHolder.getActivePanel().setLastSaveTime(System.currentTimeMillis());
                } catch (Exception ex) {
                    // In case of error, ask user whether action should get repeated
                    System.err.println(ex.getMessage());
                    if (JOptionPane.showConfirmDialog(frm_Main, "Fehler beim Speichern der Datei!\nErneut versuchen?", "Fehler", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        cancelSafeFile = true;
                    }
                }
            }
        }
    }

    /**
     *
     * Open a fileChooser and deserialize the selected file as an GameOfLifeEngine-Object
     */
    public void oeffneLokalesSpiel() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setApproveButtonText("Öffnen");
        fileChooser.setDialogTitle("Spieldatei öffnen");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileFilter(new FileNameExtensionFilter("'Game of Live' files only  (*.gol)", "gol"));

        boolean cancelOpenFile = false;

        // Until FileChooser was canceled
        while (!cancelOpenFile) {

            // Show FileChooser and continue if user selected a file
            if (fileChooser.showOpenDialog(frm_Main) == JFileChooser.APPROVE_OPTION) {

                // If selected file exists
                if (fileChooser.getSelectedFile().exists()) {
                    try (FileInputStream fis = new FileInputStream(fileChooser.getSelectedFile());
                         ObjectInputStream ois = new ObjectInputStream(fis)) {

                        // Deserialize Object
                        IGameOfLifeSaveFile saveFile = (IGameOfLifeSaveFile) ois.readObject();

                        if (addGameToServer(saveFile.getName(), saveFile.getDim().width, saveFile.getDim().height, saveFile.getTorus(), saveFile.getData())) {

                            int tabNumber = panelHolder.isGameAlreadyOpen(saveFile.getName());

                            if (tabNumber == -1) {
                                // Open Gamefield
                                panelHolder.addNewGame(saveFile.getName());
                            }

                            // Set path to be able to store game in same file
                            ((GameOfLifePanel) panelHolder.getTabbedPane().getComponentAt(tabNumber)).setPath(fileChooser.getSelectedFile().getAbsolutePath());

                            cancelOpenFile = true;
                            JOptionPane.showMessageDialog(frm_Main, "Öffnen war erfolgreich!");
                        } else {
                            throw new Exception();
                        }

                    } catch (Exception ex) {
                        // In case of error, ask user whether action should get repeated
                        if (JOptionPane.showConfirmDialog(frm_Main, "Fehler beim Laden der Datei!\nErneut versuchen?", "Fehler", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                            cancelOpenFile = true;
                        }
                    }
                } else {
                    // In case that an not existing file was selected, ask user whether action should get repeated
                    if (JOptionPane.showConfirmDialog(frm_Main, "Es muss eine existierende Datei ausgewählt werden!\nErneut versuchen?", "Fehler", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        cancelOpenFile = true;
                    }
                }
            } else { // If user canceled action
                cancelOpenFile = true;
            }

        }

    }

}
