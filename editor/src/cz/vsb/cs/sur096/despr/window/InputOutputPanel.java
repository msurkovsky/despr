
package cz.vsb.cs.sur096.despr.window;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.Parameters;
import cz.vsb.cs.sur096.despr.model.IInputParameter;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.view.Operation;
import cz.vsb.cs.sur096.despr.view.Selectable;
import cz.vsb.cs.sur096.despr.view.SelectedObjectsChangeListener;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParametersTable;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParametersTableModel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.*;

/**
 * Panel obsahující záložku se standardním výstupem a seznam vstupních
 * parametrů vybrané operace s možností jejich editace.
 *
 * Implementuje rozhrní {@code SelectedObjectsChangeListener} a regauje
 * tak na změnu vybraných komponent, pokud je vybrána operace. Pak jsou
 * načteny její vstupní parametry, které je možné editovat.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovskyk at gmail.com</a>
 * @version 2012/01/25/19:37
 */
public class InputOutputPanel 
            extends JTabbedPane 
            implements SelectedObjectsChangeListener {

    /** Index záložky se standardním výstupem. */
    public static final int STDOUT_TAB = 0;
    
    /** Index záložky s tabulkou vstupních parametrů vybrané operace.*/
    public static final int OPERATION_PROPETIES_TAB = 1;
    
    private ParametersTableModel propertiesTableModel;
    private ParametersTable propertiesTable;
    private LocalizeMessages messages;
    /**
     * Iniciuje vstupně/výstupní panel. Vytvoří {@code JTabbedPane} se dvěmi
	 * záložkami, jednou pro standardní výstup a druhou pro nastavení vstupních
	 * parametrů vybrané operace.
     * @param clear akce sloužící pro smazání obsahu standardního výstupu.
     * @param export akce sloužící pro export obsahu standardního výstupu
	 * do souboru.
     * @param stdout odkaz na komponentu, do které je přesměrován standardní
	 * výstup.
     */
    public InputOutputPanel(Action clear, Action export, JTextPane stdout) {
        super();
        
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        JPanel pnlStdout = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_START;
        JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPnl.add(new JButton(clear));
        buttonPnl.add(new JButton(export));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        pnlStdout.add(buttonPnl, gbc);
        
        stdout.setBackground(Color.WHITE);
        stdout.setEditable(false);
        stdout.setAutoscrolls(true);
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        pnlStdout.add(new JScrollPane(stdout), gbc);
        addTab(messages.getString("tab.title.output", "Output"), pnlStdout);
        
        propertiesTableModel = new ParametersTableModel();
        propertiesTable = new ParametersTable(propertiesTableModel);
        addTab(messages.getString("tab.title.properties", "Properties"), 
                new JScrollPane(propertiesTable));
    }
    
    /**
     * Nastaví zda je tabulka se vstupními parametry vybrané operace
	 * editovatelná či nikoli.
     * @param editable je tabulka se vstupními parametry editovatelná.
     */
    public void setEditablePropertiesTable(boolean editable) {
        propertiesTable.setEnabled(editable);
    }
    
    /**
     * Reaguje na změnu množiny vybraných objektů. Pokud je počet vybraných
	 * objektů roven nule pak je použita prázdna tabulka vstupních parametru,
	 * pokud je roven jedné pak jsou načteny vstupní parametry operace.
     * @param selectedObjects seznam vybraných objektů.
     */
    @Override
    public void selectedObjectsChange(List<Selectable> selectedObjects) {
        int countSelectedObjects = selectedObjects.size();
        String titleProps = messages.getString("tab.title.properties", "Properties");
        if (countSelectedObjects == 0 || countSelectedObjects > 1) {
            propertiesTableModel.setInputParameters(new Parameters<IInputParameter>(0));
            propertiesTable.revalidate();
            propertiesTable.repaint();
            
            setTitleAt(OPERATION_PROPETIES_TAB, titleProps);
            setSelectedIndex(STDOUT_TAB);
        } else if (countSelectedObjects == 1) {
            Selectable selected = selectedObjects.get(0);
            if (selected instanceof Operation) {
                Operation op = (Operation) selected;
                propertiesTableModel.setInputParameters(op.getModel().getInputParameters());
                propertiesTable.revalidate();
                propertiesTable.repaint();
                
                String opName = op.getModel().getDisplayName().trim().replaceAll("\\n", " ");
                setTitleAt(OPERATION_PROPETIES_TAB, String.format("%s - %s", 
                        titleProps, opName));
                setSelectedIndex(OPERATION_PROPETIES_TAB);
            }
        }
    }
}