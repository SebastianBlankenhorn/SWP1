package de.hft.ip1.GameOfLife.Server;

import de.hft.ip1.GameOfLife.Client.IGameOfLifePanel;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * class stands for one game on the server and provide controlling methods
 */
public class GameOfLifeEngine extends UnicastRemoteObject implements Serializable, IGameOfLifeEngine {

    private Dimension dim;
    private boolean torus;
    private boolean[] data;

    private long lastChanged;
    private boolean timerStarted = false;
    private Timer timer;
    private int timerDelay = 1000;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(40);
    private String name;

    /**
     * Contains all registered Gamefields for this game
     */
    private HashSet<IGameOfLifePanel> panels = new HashSet<>();

    /**
     * Contains all Gamefields with lost connection to the client<br />
     * The will get removed from panels
     */
    private HashSet<IGameOfLifePanel> toRemove = new HashSet<>();

    /**
     * Constructor that initializes an instance of the class by allocating as
     * many elements as needed to store the data.
     *
     * @param width  Number of columns in the array
     * @param height Number of rows in the array
     * @param torus  Set to true, if the array is treated as torus
     */
    public GameOfLifeEngine(String name, int width, int height, boolean torus) throws RemoteException {
        this(name, width, height, torus, new boolean[width * height]);
    }

    /**
     * Constructor that initializes an instance of the class by using the
     * predefined array for data storage.
     *
     * @param width  Number of columns in the array
     * @param height Number of rows in the array
     * @param torus  Set to true, if the array is treated as torus
     * @param data   Initial data, array can be assumed to contain at least
     *               width * height elements
     */
    public GameOfLifeEngine(String name, int width, int height, boolean torus, boolean[] data) throws RemoteException {
        super(0);
        this.name = name;
        this.dim = new Dimension(width, height);
        this.torus = torus;
        this.data = data;
    }

    /**
     * Resizes the current array such that data is preserved. Data preservation
     * means that for all items that are in the old as well as in the new data
     * field the values of data remain the same. Note however that this applies
     * to the array values as determined by the indices. As an example consider
     * the following array of size 8 x 3 which gets reformatted to an array
     * of size 3 x 8. Even though the size of the field data[] remains the same
     * the data itself changes:
     *
     * <ul>
     *   <li> Old data, where the items are marked by lowercase letters
     *      <pre>
     *        a b c d e f g h
     *        i j k l m n o p
     *        q r s t u v w x
     *     </pre>
     *   </li>
     *   <li>New data, where the items are marked by lowercase letters or 0
     *   for newly created cells:
     *     <pre>
     *       a b c
     *       i j k
     *       q r s
     *       0 0 0
     *       0 0 0
     *       0 0 0
     *       0 0 0
     *       0 0 0
     *     </pre>
     *    </li>
     * </ul>
     *
     * @param newWidth  The new width of the rectangle, in every row at most
     *                  <code>Math.min(width, newWidth)</code> items are copied
     * @param newHeight The new height of the rectangle, at most <code>
     *                  Math.min(width, newWidth) </code> rows are copied
     * @param newTorus  Whether the data will behave as torus.
     */
    @Override
    public void setDimension(int newWidth, int newHeight, boolean newTorus) throws RemoteException {
        setLastChanged();
        boolean[] newData = new boolean[newWidth * newHeight];
        int minHeight = Math.min(newHeight, dim.height);
        int minWidth = Math.min(newWidth, dim.width);
        for (int i = 0; i < minHeight; i++) {
            System.arraycopy(data, i * dim.width, newData, i * newWidth,
                    minWidth);
        }
        dim.setSize(newWidth, newHeight);
        data = newData;
        this.torus = newTorus;
    }

    public void setDimensionEmptyData(int newWidth, int newHeight, boolean newTorus) throws RemoteException {
        boolean[] newData = new boolean[newWidth * newHeight];
        dim.setSize(newWidth, newHeight);
        data = newData;
        this.torus = newTorus;
    }

    /**
     * Returns a copy of the current dimension value.
     *
     * @return A copy of field dim
     */
    @Override
    public Dimension getDimension() throws RemoteException {
        return new Dimension(dim);
    }

    /**
     * Returns the current torus value
     *
     * @return A copy of field torus
     */
    @Override
    public boolean isTorus() throws RemoteException {
        return torus;
    }


    public void setLastChanged() {
        this.lastChanged = System.currentTimeMillis();
    }

    /**
     * Computes the value of the neighbours of the cell at position x, y.
     * Neighbours are numerated like on the telephone keypad, i.e.
     * <pre>
     *     1 2 4
     *     4 5 6
     *     7 8 9
     * </pre>
     * If the torus mode is not activated, those neighbours of a valid cell
     * that are outside the array are always returned as false. If the torus
     * mode is activated the left neighbours of cells in the first column
     * are the cells in the last column, vice versa the right neighbours
     * of the last column are the cells of the first column. The same holds,
     * mutatis mutandis, for the first and the last row.
     *
     * @param x     Row index, betpween 0 and dim.width-1, inclusive
     * @param y     Row index, between 0 and dim.height-1, inclusive
     * @param index Neighbour index
     * @return The value of the given cell. Note that if x and y are
     * not in the ranges given above, the function behaviour is undefined
     */
    @Override
    public boolean getNeighbourData(int x, int y, int index) throws RemoteException {
        Point pt = getPoint(x, y, index);
        if (pt == null) {
            return false;
        } else {
            return data[pt.y * dim.width + pt.x];
        }
    }

    /**
     * Getter for Data of this game
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean[] getData() throws RemoteException {
        return data;
    }

    /**
     * Method to set the item at the specific point.
     *
     * @param x     The column, must be between 0 and dim.width-1, inclusive
     * @param y     The row, must be between 0 and dim.height-1, inclusive
     * @param value The value to set
     */
    @Override
    public void setData(int x, int y, boolean value) throws RemoteException {
        setLastChanged();
        setData(data, x, y, value);
    }

    /**
     * @return lastChanged
     * @throws RemoteException
     */
    @Override
    public long getLastChanged() throws RemoteException {
        return this.lastChanged;
    }

    /**
     * Invert the data at coordinate x,y
     *
     * @param x
     * @param y
     * @throws RemoteException
     */
    @Override
    public void invertData(int x, int y) throws RemoteException {
        setLastChanged();
        setData(x, y, !getNeighbourData(x, y, MooreNeighborhood.C));
    }

    /**
     * Method to set the item at the specific point.
     *
     * @param data  The data to be set
     * @param x     The column, must be between 0 and dim.width-1, inclusive
     * @param y     The row, must be between 0 and dim.height-1, inclusive
     * @param value The value to set
     */
    @Override
    public void setData(boolean[] data, int x, int y, boolean value) throws RemoteException {
        setLastChanged();
        data[y * dim.width + x] = value;
    }

    /**
     * Method to compute the resulting position of an item. Even though
     * this method is neither tested nor required to implement we strongly
     * encourage you to implement this method in order to check whether
     * your logics of computing a position is correct.
     *
     * @param x     Valid cell column between 0 and dim.width-1, inclusive
     * @param y     Valid cell row between 0 and dim.height-1, inclusive
     * @param index Position of the cell w.r.t. to x and y
     * @return Point object with row and column of the neighbour cell,
     * if this cell is inside the array, or null, if the
     * cell is not inside the array and torus mode is not activated.
     */
    private Point getPoint(int x, int y, int index) {
        x += ((index - 1) % 3) - 1;
        y += ((index - 1) / 3) - 1;
        if (!torus) {
            if ((x < 0 || x >= dim.width || y < 0 || y >= dim.height)) {
                return null;
            } else {
                return new Point(x, y);
            }
        } else {
            return new Point((x + dim.width) % dim.width, (y + dim.height) % dim.height);
        }
    }

    /**
     * Method to compute the next configuration of the array based on the current
     * one
     */
    @Override
    public void step() throws RemoteException {
        setLastChanged();
        boolean[] newData = new boolean[dim.width * dim.height];
        for (int y = 0; y < dim.height; y++) {
            for (int x = 0; x < dim.width; x++) {
                int count = 0;
                for (int i = 1; i <= 9; i++) {
                    if (i != 5 && getNeighbourData(x, y, i)) {
                        count++;
                    }
                }
                if (count < 2 || count > 3) {
                    setData(newData, x, y, false);
                } else if (count == 3) {
                    setData(newData, x, y, true);
                } else if (count == 2) {
                    setData(newData, x, y, getNeighbourData(x, y, 5));
                }
            }
        }
        data = newData;
    }


    /**
     * Method to transform the data array into a String object representing
     * the current data. If data was arranged like a chessboard, output
     * should look as follows:
     * <pre>
     *     0 1 0 1 0 1 0 1
     *     1 0 1 0 1 0 1 0
     *     0 1 0 1 0 1 0 1
     *     1 0 1 0 1 0 1 0
     *     0 1 0 1 0 1 0 1
     *     1 0 1 0 1 0 1 0
     *     0 1 0 1 0 1 0 1
     *     1 0 1 0 1 0 1 0
     * </pre>
     * The following modifications apply before the data is tested:
     * <ul>
     *     <li>\r\n is replaced by \n</li>
     *     <li>\r is replaced by \n</li>
     *     <li>any whitespace char (except \n) is replaced by a ' '</li>
     *     <li>multiple space chars are replaced by single ones</li>
     *     <li>space at the start or end of a line is removed</li>
     *     <li>space at the start or end of the string is removed</li>
     * </ul>
     *
     * @return The string representing the data
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();

        try {
            for (int y = 0; y < dim.height; y++) {
                for (int x = 0; x < dim.width; x++) {
                    builder.append(String.format("%d ", getNeighbourData(x, y, 5) ? 1 : 0));
                }
                builder.append(System.lineSeparator());
            }
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
        }

        return builder.toString();
    }

    /**
     * Returns true if timer ist started yet
     *
     * @return
     */
    public boolean isTimerStarted() throws RemoteException {
        return timerStarted;
    }

    /**
     * Set the property <i>timerStarted</i>
     *
     * @param timerStarted
     */
    private void setTimerStarted(boolean timerStarted) {
        this.timerStarted = timerStarted;
    }

    /**
     * Return the name of this game
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public String getName() throws RemoteException {
        return this.name;
    }

    /**
     * Register passed gemfield at this game
     *
     * @param panel
     * @throws RemoteException
     */
    @Override
    public void registerPanel(IGameOfLifePanel panel) throws RemoteException {
        panels.add(panel);
    }

    /**
     * Unregister passed gamefield from this game
     *
     * @param panel
     * @throws RemoteException
     */
    @Override
    public void unregisterPanel(IGameOfLifePanel panel) throws RemoteException {

        panels.remove(panel);
    }

    /**
     * Start the game
     *
     * @throws RemoteException
     */
    @Override
    public void start() throws RemoteException {

        setTimerStarted(true);
        startTimer();

    }

    /**
     * Starts timer<br />
     * Timer will wait the delay and then work the task. When work is done start the timer again
     */
    private void startTimer() {

        if (timerStarted) {

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        step();
                        repaintAllPanels();
                        startTimer();
                    } catch (RemoteException remoteException) {
                        System.err.println(remoteException.getMessage());
                    }
                }
            }, timerDelay);

        }
    }

    /**
     * Stop the game
     *
     * @throws RemoteException
     */
    @Override
    public void stop() throws RemoteException {
        setTimerStarted(false);
    }

    /**
     * Decrease the timer delay, timer runs faster
     *
     * @throws RemoteException
     */
    @Override
    public void faster() throws RemoteException {
        timerDelay = (int) (timerDelay / Math.sqrt(2));
    }

    /**
     * Increase the timer delay, timer runs slower
     *
     * @throws RemoteException
     */
    @Override
    public void slower() throws RemoteException {
        timerDelay = (int) (timerDelay * Math.sqrt(2));
    }

    /**
     * Repaint all gamefields
     *
     * @throws RemoteException
     */
    @Override
    public void repaintAllPanels() throws RemoteException {
        for (IGameOfLifePanel panel : panels) {
            executor.submit(() -> {
                try {
                    panel.repaintPanel();
                } catch (RemoteException remoteException) {
                    toRemove.add(panel);
                }
                return null;
            });
        }
        removePanelsWithNoConnection();
    }

    @Override
    public void setAllData(boolean[] data) throws RemoteException {
        this.data = data;
    }

    @Override
    public IGameOfLifeSaveFile getSaveFile() throws RemoteException {
        return new GameOfLifeSaveFile(name, dim, torus, data);
    }

    /**
     * Remove all gamefields with lost connection
     */
    private void removePanelsWithNoConnection() {
        synchronized (panels) {
            synchronized (toRemove) {
                if (toRemove.size() > 0) {
                    panels.removeAll(toRemove);
                    toRemove.clear();
                }

            }
        }
    }
}