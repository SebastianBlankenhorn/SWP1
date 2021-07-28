package de.hft.ip1.GameOfLife.Client;

import de.hft.ip1.GameOfLife.Server.GameOfLifeEngine;
import de.hft.ip1.GameOfLife.Server.IGameOfLifeEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

/**
 * Class creates an JPanel and stands for the Gamefield
 */
public class GameOfLifePanel extends JPanel implements IGameOfLifePanel{

    // Gameobject on server
    private IGameOfLifeEngine engine;
    private long lastSavedTime = 0;
    // Variables below are temporally set to reduce rmi-calls
    private boolean[] data_tmp;
    private Dimension dimEngine_tmp;

    // Path if game is saved local
    private String path;

    public GameOfLifePanel(IGameOfLifeEngine engine) {

        this.engine = engine;

        addMouseListener((new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    Dimension dimPanel = getSize();
                    Dimension dimEngine = engine.getDimension();

                    int xWidth = dimPanel.width / dimEngine.width;
                    int yWidth = dimPanel.height / dimEngine.height;

                    int x = e.getX() * dimEngine.width / ((xWidth ) * dimEngine.width + 1);
                    int y = e.getY() * dimEngine.height / ((yWidth ) * dimEngine.height + 1);

                    if(x >= 0 && x < dimEngine.width && y >= 0 && y < dimEngine.height){
                        engine.invertData(x, y);
                        engine.repaintAllPanels();
                    }
                } catch (RemoteException remoteException) {
                    GameOfLifeForm.showErrorMessage("Verbindungsfehler!", remoteException);
                }
            }
        }));
    }

    @Override
    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        try {
            data_tmp = engine.getData();
            dimEngine_tmp = engine.getDimension();
            Graphics2D g2d = (Graphics2D) gc;
            Dimension dimPanel = getSize();
            int xWidth = dimPanel.width / dimEngine_tmp.width;
            int yWidth = dimPanel.height / dimEngine_tmp.height;
            for(int x = 0; x < dimEngine_tmp.width; x++){
                for (int y = 0; y < dimEngine_tmp.height; y++){
                    if(data_tmp[x+y*dimEngine_tmp.width]){
                        g2d.setBackground(Color.yellow);
                        g2d.setColor(Color.yellow);
                    } else {
                        g2d.setBackground(Color.gray);
                        g2d.setColor(Color.gray);
                    }
                    g2d.fillRect( x * xWidth + 1, y * yWidth + 1,xWidth - 2,yWidth - 2 );
                }
            }
        } catch (RemoteException remoteException) {
            GameOfLifeForm.showErrorMessage("Verbindungsfehler.", remoteException);
        }
    }

    /**
     * Provide the repaint method to the server
     * @throws RemoteException
     */
    @Override
    public void repaintPanel() throws RemoteException {
        repaint();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Getter GameOfLifeEngine
     * @return engine
     */
    public IGameOfLifeEngine getGameOfLifeEngine() {
        return engine;
    }

    /**
     * set lastSavedtime
     */
    public void setLastSaveTime(long currentTimeMillis) {
        this.lastSavedTime = currentTimeMillis;
    }

    /**
     * Getter lastSavedtime
     * @return lastSavedtime
     */
    public long getLastSavedTime(){
        return this.lastSavedTime;
    }
}