package de.hft.ip1.GameOfLife.Server;

import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;

public class GameOfLifeSaveFile implements IGameOfLifeSaveFile {

    public String name;
    public Dimension dim;
    public boolean torus;
    public boolean[] data;

    public GameOfLifeSaveFile(String name, Dimension dim, boolean torus, boolean[] data) {
        this.name = name;
        this.dim = dim;
        this.data = data;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public Dimension getDim() throws RemoteException {
        return dim;
    }

    @Override
    public boolean getTorus() throws RemoteException {
        return torus;
    }

    @Override
    public boolean[] getData() throws RemoteException {
        return data;
    }
}
