
package cz.vsb.cs.sur096.despr.types.editors;

import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 * Editor numerických typů. Jedná se o abstraktní třídu, kterou využívají 
 * všechny numerické editory.
 * 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/17/15:33
 */
public abstract class NumberCellEditor extends ParameterCellEditor {

    private final JTextField editor;
    
    private final Class type;
    private Object oldValue;
    
	/**
	 * Vytvoří nový editor numerického typu. 
	 * Parameter {@code type} musí být potomkem typu {@code java.lang.Number}.
	 * @param type numerický typ pro který má být editor použit.
	 */
    public NumberCellEditor(Class type) {
        if (!Number.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Type must be a subclass of Number!");
        }
        
        editor = new JTextField();
        editor.getDocument().addDocumentListener(new EditorTextChangeListener());
        Action finishAction = new StopEditingAction();
        editor.addActionListener(finishAction);
        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "stopEditing");
        editor.getActionMap().put("stopEditing", finishAction);
        
        this.type = type;
    }
    
    @Override
    public Object getCellEditorValue() {
        String textValue = editor.getText();
        textValue = textValue.replace(',', '.'); // konverze desetinne carky na tecku
        try {
            Object value = textToNumber(textValue);
            if (value != null) {
                return value;
            } else {
                return oldValue;
            }
        } catch (NumberFormatException ex) {
            return oldValue;
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        if (value instanceof Number) { 
            oldValue = value;
            editor.setText(value.toString());
        }
        return editor;
    }
    
    /**
     * Převádí hodnotu zadanou v editoru na konkretní numerický typ.
     * @param value hodnota v editoru.
     * @return numerická reprezentace hodnoty {@code value}
     * @throws NumberFormatException pokud se nejedná o korektně zadanou hodnotu,
     * tzn. nelze převést z řetězce na konkrétní numerický formát.
     */
    private Object textToNumber(String value) throws NumberFormatException {
        if (Integer.class.equals(type)) {
            return Integer.parseInt(value);
        } else if (Double.class.equals(type)) {
            return Double.parseDouble(value);
        } else if (Byte.class.equals(type)) {
            return Byte.parseByte(value);
        } else if (Float.class.equals(type)) {
            return Float.parseFloat(value);
        } else if (Long.class.equals(type)) {
            return Long.parseLong(value);
        } else if (Short.class.equals(type)) {
            return Short.parseShort(value);
        } else {
            return null;
        }
    }
    
    /**
     * Kontroluje po každé změně korektnost zadávané hodnoty. Pokud by
     * hodnota korektní nebyla označí text červeně.
     */
    private class EditorTextChangeListener implements DocumentListener {
        private boolean formatOK;
        
        public EditorTextChangeListener() {
            formatOK =  true;
        }
        
        @Override
        public void insertUpdate(DocumentEvent e) {
            check(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            check(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
			// neimplementovana
        }
        
        private void check(DocumentEvent e) {
            String value;
            try {
                value = e.getDocument().getText(0, e.getDocument().getLength());
                value = value.replace(',', '.'); // konverze desetinne carky na tecku
                try {
                    textToNumber(value);
                    if (!formatOK) {
                        editor.setForeground(Color.BLACK);
                        editor.revalidate();
                    }
                } catch (NumberFormatException excp) {
                    formatOK = false;
                    editor.setForeground(Color.RED);
                    editor.setToolTipText(excp.toString());
                    editor.revalidate();
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class StopEditingAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            stopCellEditing();
        }
    }
}
