package de.hft.ip1.GameOfLife.Server;

import de.hft.ip1.GameOfLife.Client.IGameOfLifePanel;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IGameOfLifeEngine extends Remote {

    public void setDimension(int newWidth, int newHeight, boolean newTorus) throws RemoteException;

    public Dimension getDimension() throws RemoteException;

    public boolean isTorus() throws RemoteException;

    public boolean getNeighbourData(int x, int y, int index) throws RemoteException;

    public boolean[] getData() throws RemoteException;

    public void setData(int x, int y, boolean value) throws RemoteException;

    public long getLastChanged() throws RemoteException;

    public void invertData(int x, int y) throws RemoteException;

    public void setData(boolean[] data, int x, int y, boolean value) throws RemoteException;

    public void step() throws RemoteException;

    public String getName() throws RemoteException;

    public void registerPanel(IGameOfLifePanel panel) throws RemoteException;

    public void unregisterPanel(IGameOfLifePanel panel) throws RemoteException;

    public void start() throws RemoteException;

    public void stop() throws RemoteException;

    public void faster() throws RemoteException;

    public void slower() throws RemoteException;

    public void repaintAllPanels() throws RemoteException;

    public void setAllData(boolean[] data) throws RemoteException;

    public void setDimensionEmptyData(int newWidth, int newHeight, boolean newTorus) throws RemoteException;

    public IGameOfLifeSaveFile getSaveFile() throws RemoteException;

    public boolean isTimerStarted() throws RemoteException;


}
