package de.hft.ip1.GameOfLife.Client;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGameOfLifePanel extends Remote {

    public void repaintPanel() throws RemoteException;

}
