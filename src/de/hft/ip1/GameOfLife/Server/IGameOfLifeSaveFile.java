package de.hft.ip1.GameOfLife.Server;

import java.awt.*;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGameOfLifeSaveFile extends Remote, Serializable {

    public String getName() throws RemoteException;
    public Dimension getDim() throws RemoteException;
    public boolean getTorus() throws RemoteException;
    public boolean[] getData() throws RemoteException;

}
