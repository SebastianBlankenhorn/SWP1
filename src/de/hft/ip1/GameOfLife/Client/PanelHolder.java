package de.hft.ip1.GameOfLife.Client;

import de.hft.ip1.GameOfLife.Server.IGameOfLifeEngine;
import de.hft.ip1.GameOfLife.Server.IServer;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

/**
 * Class provides a JTabbedPane and controls the access to all open games (panels)
 */
public class PanelHolder {

    private JPanel pnl_PanelHolder;
    private JTabbedPane tbp_GameOfLifePanels;
    private IServer server = null;

    /**
     * Set the Server, so that the panelHolder has also access to it
     *
     * @param server
     */
    public void setServer(IServer server) {
        this.server = server;
    }

    /**
     * Shows the user a checklist of all games left on the server<br />
     * Chosen games will get open
     *
     * @throws RemoteException
     */
    public void askForNewGameNames() throws RemoteException{

        GameChooser gameChooser = new GameChooser(server.getNamesOfAllGames());
        Set<String> newGameNames = gameChooser.getChoise();
        if(newGameNames != null) {
            for(String name : newGameNames) {
                addNewGame(name);
            }
        }

    }

    /**
     *
     * @return JTabbedPane of Game
     */
    public JTabbedPane getTabbedPane(){
        return this.tbp_GameOfLifePanels;
    }

    /**
     * Creates a new Gamefield for game with passed name and register it.
     *
     * @param name Name of game for the gamefield
     * @throws RemoteException
     */
    public void addNewGame(String name) throws RemoteException {
        if(isGameAlreadyOpen(name) > -1) {
            JOptionPane.showMessageDialog(null,"Spiel " + name + " ist bereits geöffnet");
            getTabbedPane().getComponentAt(isGameAlreadyOpen(name)).repaint();
        }
        else {
            IGameOfLifeEngine engine = server.getGameOfLifeEngine(name);
            GameOfLifePanel panel = new GameOfLifePanel(engine);
            panel.setName(name);
            UnicastRemoteObject.exportObject(panel, 0);
            engine.registerPanel(panel);
            tbp_GameOfLifePanels.add(name, panel);
            tbp_GameOfLifePanels.setSelectedComponent(panel);
        }
    }

    public int isGameAlreadyOpen(String name) throws RemoteException {
        int tabNumber = -1;
        for(int i=0; i<getTabbedPane().getTabCount(); i++) {
            if(getTabbedPane().getComponentAt(i).getName().equals(name)) {
                tabNumber = i;
                break;
            }
        }
        return tabNumber;
    }

    /**
     * Returns the current active gamefield.<br />
     * Returns <i>NULL</i> if there are no games opened
     *
     * @return The active gamefield
     */
    public GameOfLifePanel getActivePanel() {
        GameOfLifePanel panel = null;
        if(hasActivePanel()) {
            panel = (GameOfLifePanel) tbp_GameOfLifePanels.getSelectedComponent();
        }
        return panel;
    }

    /**
     * Check if user has open at least one gamefield
     *
     * @return true if there is an opened gamefield
     */
    public boolean hasActivePanel() {
        boolean hasActivePanel = true;
        if(tbp_GameOfLifePanels.getSelectedComponent() == null) {
            hasActivePanel = false;
        }
        return hasActivePanel;
    }

    /**
     * Unregister gamefield at the server and causes the field to close
     *
     * @throws RemoteException
     */
    public void closeActiveGame() throws RemoteException {
        if(hasActivePanel()) {
            getActivePanel().getGameOfLifeEngine().unregisterPanel((IGameOfLifePanel) getActivePanel());
            closeActivePanel();
        }
    }

    /**
     * Close the gamefield
     */
    public void closeActivePanel() {
        if(hasActivePanel()) {
            tbp_GameOfLifePanels.remove(getActivePanel());
        }
    }
}
