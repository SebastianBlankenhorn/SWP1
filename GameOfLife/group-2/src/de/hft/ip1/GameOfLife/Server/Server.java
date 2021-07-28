package de.hft.ip1.GameOfLife.Server;

import de.hft.ip1.GameOfLife.Client.GameOfLifeForm;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class Server implements IServer {

    // Contains all Games on this server
    private HashMap<String, IGameOfLifeEngine> map_engines = new HashMap<>();

    public Server() throws RemoteException {
    }

    /**
     * Return set of names of all games on this server
     * If there are no games yet, the set is empty
     *
     * @return Set of names
     * @throws RemoteException
     */
    @Override
    public HashSet<String> getNamesOfAllGames() throws RemoteException {
        HashSet<String> names = new HashSet<>();
        map_engines.forEach((key, value) -> {
            names.add(key);
        });
        return names;
    }

    /**
     * Return the Game with passed name<br />
     * Return <i>NULL</i> if there is no game with this name
     *
     * @param name Name of game
     * @return GameEngine
     * @throws RemoteException
     */
    @Override
    public IGameOfLifeEngine getGameOfLifeEngine(String name) throws RemoteException {
        IGameOfLifeEngine engine = null;
        if(map_engines.containsKey(name)) {
            engine = map_engines.get(name);
        }
        return engine;
    }

    /**
     * Check if there is a game with this name in the server
     *
     * @param name Name of Game
     * @return <i>true</i> if there exist a game
     * @throws RemoteException
     */
    @Override
    public boolean existName(String name) throws RemoteException {
        boolean nameExist = false;
        if(map_engines.containsKey(name)) {
            nameExist = true;
        }
        return nameExist;
    }

    /**
     * Create a new Game on the server<br /><br />
     * Before creating a new game, check if there is no game with the same name!<br />
     * But its possible, that between the check and this call, another user created a game with this name.
     * Because of this, there is a parameter needed, to overwrite a game if existing yet
     *
     * @param name
     * @param width
     * @param height
     * @param torus
     * @param overwriteOk <i>true</i> = overwrite a game with same name
     * @return <i>true</i> if a new game was created
     * @throws RemoteException
     */
    @Override
    public boolean createNewGame(String name, int width, int height, boolean torus, boolean overwriteOk, boolean[] data) throws RemoteException {
        boolean newGameCreated = false;

        // following code has to be lock map_engines because while executing, no other user is allowed to access to map_engines
        synchronized (map_engines) {
            if(overwriteOk) {
                IGameOfLifeEngine engine = map_engines.get(name);
                engine.setDimensionEmptyData(width, height, torus);
                if(data != null) {
                    engine.setAllData(data);
                }
                engine.repaintAllPanels();
                newGameCreated = true;
            }
            else if(existName(name) == false) {
                IGameOfLifeEngine engine;
                if(data == null) {
                    engine = new GameOfLifeEngine(name, width, height, torus);
                }
                else {
                    engine = new GameOfLifeEngine(name, width, height, torus, data);
                }

                map_engines.put(name, engine);
                newGameCreated = true;
            }
        }

        return newGameCreated;
    }

    /**
     * Testmethod to add 5 empty games on this server
     */
    private void addFiveTestEngines() {
        try {
            for(int i=0; i<5; i++) {
                IGameOfLifeEngine engine = new GameOfLifeEngine("Spiel " + i, 10, 10, true);
                map_engines.put("Spiel " + i, engine);
            }
        } catch (RemoteException remoteException) {
            GameOfLifeForm.showErrorMessage("Verbindungsfehler.", remoteException);
        }
    }
}
