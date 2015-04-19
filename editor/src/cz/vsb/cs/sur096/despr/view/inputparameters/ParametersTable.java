
package cz.vsb.cs.sur096.despr.view.inputparameters;

import cz.vsb.cs.sur096.despr.collections.IParameters;
import cz.vsb.cs.sur096.despr.model.IInputParameter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Implementace tabulky sloužící pro editaci vstupních parametrů operací.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/17/08:44
 */
public class ParametersTable extends JTable {

    /** Model tabulky. */
    private ParametersTableModel model;
    
    /**
     * Iniciuje tabulku s prázdným (nenaplněným) modelem.
     */
    public ParametersTable() {
        this(new ParametersTableModel());
    }
    
    /**
     * Iniciuje tabulku známým modelem.
     * @param model model tabulky.
     */
    public ParametersTable(ParametersTableModel model) {
        super(model);

        this.model = model;
        
        setRowHeight(30);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setColumnSelectionAllowed(true);
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "nextValue");
        getActionMap().put("nextValue", new TabAction(this));
                
        TableColumn nameColumn = getColumnModel().getColumn(ParametersTableModel.NAME_COLUMN);
        nameColumn.setMinWidth(150);
        nameColumn.setMaxWidth(300);
        nameColumn.setCellRenderer(new NameRenderer());
        
        TableColumn typeColumn = getColumnModel().getColumn(ParametersTableModel.TYPE_COLUMN);
        typeColumn.setMinWidth(75);
        typeColumn.setMaxWidth(75);
        typeColumn.setCellEditor(new TypeEditor());
        typeColumn.setCellRenderer(new TypeRenderer());
        
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        
    }
    
    /**
     * Nastaví seznam vstupních parametrů.
     * @param parameters  seznam vstupních parametrů.
     */
    public void setInputParameters(IParameters<IInputParameter> parameters) {
        model.setInputParameters(parameters);
    }
    
    /**
     * Poskytne renderer buňky v tabulce.
     * @param row index řádku.
     * @param column index sloupce.
     * @return renderer buňky v tabulce.
     */
    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (column == ParametersTableModel.VALUE_COLUMN) {
            Class c = model.getDataTypeAt(row, column);
            if (c != null) {
                return ValueRendererFactory.createTableCellRenderer(c);
            } else {
                return super.getDefaultRenderer(c);
            }
        } else {
            return super.getCellRenderer(row, column);
        }
    }

    /**
     * Poskytne editor buňky v tabulce.
     * @param row index řádku.
     * @param column index sloupce.
     * @return editor buňky v tabulce.
     */
    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (column == ParametersTableModel.VALUE_COLUMN) {
            Class c = model.getDataTypeAt(row, column);
            if (c != null) {
                return ValueEditorFactory.createTableCellEditor(c);
            } else {
                return new DefaultInputParameterCellEditor();
            }
        } else {
            return super.getCellEditor(row, column);
        }
    }
    
    /**
     * Rozšíření původního ukončení editace, tak aby po skončení byla vybrána
     * poslední editovaná buňka.
     * @param e událost.
     */
    @Override
    public void editingStopped(ChangeEvent e) {
        // Take in the new value
        int row = editingRow, column = editingColumn;
        super.editingStopped(e);
        if (row > -1 && column > -1) {
            changeSelection(row, column, false, false);
        }
    }
    
    /**
     * V tabulce je změněn pohyb pomocí tabulátoru. Provádí se pouze 
	 * ve sloupci s hodnotami hodnot. První sloupec není vůbec editovatelný
	 * a v druhém se přepínají pouze typy vstupních parametrů. Navíc je v prvních 
	 * dvou sloupcích potlačeno zvýraznění výběru. 
     */
    private class TabAction extends AbstractAction {

        private final JTable table;
        
        public TabAction(JTable table) {
            this.table = table;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int rowCount = table.getRowCount();
            int row = (table.getSelectedRow() + 1) % rowCount;
            int col = ParametersTableModel.VALUE_COLUMN;
            changeSelection(row, col, false, false);
        }
    }
}
