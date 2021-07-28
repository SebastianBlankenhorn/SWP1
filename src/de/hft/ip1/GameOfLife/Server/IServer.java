package de.hft.ip1.GameOfLife.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface IServer extends Remote {

    public Set<String> getNamesOfAllGames() throws RemoteException;

    public IGameOfLifeEngine getGameOfLifeEngine(String name) throws RemoteException;

    public boolean createNewGame(String name, int width, int height, boolean torus, boolean overwriteOk, boolean[] data) throws RemoteException;

    public boolean existName(String name) throws RemoteException;

}
