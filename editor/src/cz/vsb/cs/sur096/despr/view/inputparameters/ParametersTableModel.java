package cz.vsb.cs.sur096.despr.view.inputparameters;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.IParameters;
import cz.vsb.cs.sur096.despr.collections.Parameters;
import cz.vsb.cs.sur096.despr.model.IInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import javax.swing.table.AbstractTableModel;

/**
 * Implementace modelu tabulky sloužící pro editaci vstupních parametrů
 * operací. Tabulka má tří sloupce:
 * <ol>
 *  <li>Jméno</li>
 *  <li>Typ (<b>ne datový typ</b>, nýbrž typ vnitřní/vnější)</li>
 *  <li>Hodnota</li>
 * </ol>
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/15/10:46
 */
public class ParametersTableModel extends AbstractTableModel {

    /** Index sloupce se jménem parametru. */
    public static final int NAME_COLUMN = 0;
    /** Index sloupce s typem parametru. */
    public static final int TYPE_COLUMN = 1;
    /** Index sloupce s hodnotou parametru. */
    public static final int VALUE_COLUMN = 2;
    
    /** Celkový počet sloupců. */
    private final int COUNT_COLUMN = 3;
    
    /** Seznam jmen sloupců. */
    private String[] columnNames;
    
    /** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;
    
    /** Seznam vstupních parametrů dané operace. */
    private IParameters<IInputParameter> inputParameters;
    
    /**
     * Iniciuje model tabulky.
     */
    public ParametersTableModel() {
        inputParameters = new Parameters<IInputParameter>(0);
        columnNames = new String[COUNT_COLUMN];
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        columnNames[NAME_COLUMN] = messages.getString("parameter_name", "Name");
        columnNames[TYPE_COLUMN] = messages.getString("parameter_type", "Type");
        columnNames[VALUE_COLUMN] = messages.getString("parameter_value", "Value");
    }
    
    /**
     * Nastaví seznam vstupních parametrů tabulce.
     * @param inputParameters seznam vstupních parametrů.
     */
    public void setInputParameters(IParameters<IInputParameter> inputParameters) {
        this.inputParameters = inputParameters;
        this.inputParameters.sort();
    }
    
    /**
     * Poskytne počet řádků tabulky.
	 * @return počet řádků tabulky.
     */
    @Override
    public int getRowCount() {
        return inputParameters.size();
    }

    /**
     * Poskytne počet sloupců tabulky.
	 * @return počet sloupců tabulky (=3).
     */
    @Override
    public int getColumnCount() {
        return COUNT_COLUMN;
    }
    
    /**
     * Poskytne jméno sloupce.
     * @param columnIndex index sloupce.
     * @return jméno sloupce.
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Poskytne hodnotu buňky na daných souřadnicích.
	 * @param rowIndex index řádku.
	 * @param columnIndex index sloupce.
	 * @return hodnotu buňky na daných souřadnicích.
     * @throws IllegalArgumentException pokud je index sloupce větší
	 * nebo roven třem.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) 
            throws IllegalArgumentException {
        
        Object result = null;
        
        IInputParameter parameter = inputParameters.get(rowIndex);
        
        if (inputParameters != null) {
            switch(columnIndex) {
                case NAME_COLUMN:
                    result = parameter;
                    break;
                case TYPE_COLUMN:
                    result = parameter.getType();
                    break;
                case VALUE_COLUMN:
                    result = parameter.getValue();
                    break;
                default:
                    throw new IllegalArgumentException(
                            String.format(messages.getString(
                                    "exception.unexpected_colum_index", 
                                    "Unexpected colum index '%d'!"), 
                            columnIndex));
            }
        }
        return result;
    }
    
    /**
     * Nastaví hodnotu buňky na daných souřadnicích, vyjma hodnoty
	 * ve sloupci se jménem. Ten je získán se jména parametru dané
	 * operace a není editovatelný.
     * @param value nová hodnota buňky.
     * @param rowIndex souřadnice řádku.
     * @param columnIndex souřadnice sloupce.
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        IInputParameter parameter = inputParameters.get(rowIndex);
        if (columnIndex == VALUE_COLUMN) {
            parameter.setValue(value);
        } else if (columnIndex == TYPE_COLUMN) {
            parameter.setType((EInputParameterType) value);
        }
        fireTableDataChanged();
    }
    
    /**
     * Zjistí zda je daná buňka editovatelná.
     * @param rowIndex index řádku.
     * @param columnIndex index sloupce.
     * @return {@code true} pokud je daná buňka editovatelná,
	 *  v opačném případě vrátí {@code false}.
     * @throws IllegalArgumentException pokud bude index sloupce
	 * větší nebo roven třem.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) 
            throws IllegalArgumentException {
        
        IInputParameter parameter = inputParameters.get(rowIndex);
        
        if (columnIndex == NAME_COLUMN) {
            return false;
        } else if (columnIndex == TYPE_COLUMN) {
            if (parameter.isLockChangeType()) {
                return false;
            } else {
                return ((!parameter.isUsed() && parameter.getType() == EInputParameterType.OUTER) || 
                        parameter.getType() == EInputParameterType.INNER);
            }
        } else if (columnIndex == VALUE_COLUMN) {
            // pokud hodnota nema editor pak neni editovatelna
            if (ValueEditorFactory.createTableCellEditor(getDataTypeAt(rowIndex, columnIndex)) == null) {
                return false;
            }
            // pokud editor ma pak se musi jednat o vnitrni parameter
            return parameter.getType() == EInputParameterType.INNER;
        } else {
            throw new IllegalArgumentException(
                            String.format(messages.getString(
                                    "exception.unexpected_colum_index", 
                                    "Unexpected colum index '%d'!"), 
                            columnIndex));
        }
    }
    
    /**
     * Poskytne datový typ parametru vloženém v buňce o daných
	 * souřadnicích.
     * @param rowIndex index řádku.
     * @param columnIndex index sloupce.
     * @return datový typ hodnoty v dané buňce.
     */
    public Class getDataTypeAt(int rowIndex, int columnIndex) {
        if (columnIndex == NAME_COLUMN) {
            return String.class;
        } else if (columnIndex == TYPE_COLUMN) {
            return EInputParameterType.class;
        } else if (columnIndex == VALUE_COLUMN) {
            return inputParameters.get(rowIndex).getDataType();
        } else {
            return null;
        }
    }
}
