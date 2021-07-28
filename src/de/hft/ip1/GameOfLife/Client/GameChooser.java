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
        super((Frame) null, true);
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
                    if (checkbox.isSelected()) {
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
    public Set<String> getChoise() {
        setVisible(true);
        return newGameNames;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        pnl_GameChooser = new JPanel();
        pnl_GameChooser.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnl_Checkboxes = new JPanel();
        pnl_Checkboxes.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        pnl_GameChooser.add(pnl_Checkboxes, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btn_OK = new JButton();
        btn_OK.setText("ok");
        pnl_GameChooser.add(btn_OK, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        pnl_GameChooser.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("folgende Spiele existieren noch auf dem Server");
        pnl_GameChooser.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return pnl_GameChooser;
    }
}
