package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.model.operation.IRootOperation;
import java.awt.*;
import javax.swing.*;



/**
 * Renderer seznamu nepoužitých operací. Položky jsou vykresleny jako světle
 * zelené obdélníky s lokalizovaným názvem operace. Kořenové operace
 * jsou pak znázorněny tučným písmem.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/12/20:20
 */
public class UnusedOperationsListRenderer implements ListCellRenderer {

    JPanel renderer;
    JLabel operationName;
    JPanel pnl;
    Color background, foreground, selectBackground, selectForeground;

    /**
     * Iniciuje renderer.
     */
    public UnusedOperationsListRenderer() {

        background = new Color(0, 255, 0, 30);
        foreground = UIManager.getColor("List.foreground");
        selectBackground = UIManager.getColor("List.selectionBackground");
        selectForeground =  UIManager.getColor("List.selectionForeground");

        renderer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(1, 1, 0, 1);

        operationName = new JLabel();
        pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl.add(operationName);
        pnl.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        renderer.add(pnl, gbc);
        renderer.setBackground(Color.WHITE);
    }
    
    /**
     * Poskytne grafickou komponentu která vizualizuje nepoužitou operaci.
     * @param list zdrojový seznam.
     * @param value hodnota.
     * @param index pozice v seznamu.
     * @param isSelected je položka vybrána?
     * @param cellHasFocus má položka focus?
     * @return grafickou komponentu vizualizující nepoužitou operaci.
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {

        if (value instanceof IOperation) {
            IOperation op = (IOperation) value;
            String opName = op.getLocalizeMessage("name");
            if (opName == null) {
                opName = op.getClass().getSimpleName();
            }
            opName = opName.replaceAll("\\n", " ");
            operationName.setText(opName);
            pnl.setBackground(background);
            operationName.setForeground(foreground);

            if (isSelected) {
                pnl.setBackground(selectBackground);
                operationName.setForeground(selectForeground);
            }

            if (op instanceof IRootOperation) {
                Font f = operationName.getFont();
                f = f.deriveFont(Font.BOLD);
                operationName.setFont(f);
            } else {
                Font f = operationName.getFont();
                f = f.deriveFont(Font.PLAIN);
                operationName.setFont(f);
            }
        } else {
            operationName.setText("Bad operation type");
        }

        return renderer;
    }
}