package de.hft.ip1.GameOfLife.Client;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class is a JDialog and let the user select some Games
 */
public class GameChooser extends JDialog {

    private JPanel pnl_GameChooser;
    private JPanel pnl_Checkboxes;
    private JButton btn_OK;
    private ArrayList<JCheckBox> checkboxes;
    private Set<String> newGameNames;

    public GameChooser(Set<String> names) {
        super((java.awt.Frame) null, true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setContentPane(pnl_GameChooser);
        setLocationRelativeTo(null);
        insertCheckboxes(names);
        pack();
        revalidate();
        repaint();

        // Actionhandler
        btn_OK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGameNames = new HashSet<>();
                checkboxes.forEach(checkbox -> {
                    if(checkbox.isSelected()) {
                        newGameNames.add(checkbox.getName());
                    }
                });
                setVisible(false);
                dispose();
            }
        });
    }

    private void insertCheckboxes(Set<String> names) {

        checkboxes = new ArrayList<>();

        names.forEach(name -> {
            JCheckBox checkbox = new JCheckBox(name);
            checkbox.setName(name);
            checkboxes.add(checkbox);
            pnl_Checkboxes.add(checkbox);
        });

    }

    /**
     * Display chooser and returns Set with selected games<br />
     * Returns <i>NULL</i> if the user closed the Dialog not via "OK"
     *
     * @return Set of selected games
     */
    public Set<String> getChoise(){
        setVisible(true);
        return newGameNames;
    }
}
