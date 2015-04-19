
package cz.vsb.cs.sur096.despr.view.inputparameters;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.utils.ExtensionsOfTypes;
import java.util.logging.Level;
import javax.swing.table.TableCellEditor;

/**
 * Továrna poskytující editory pro případné datové typy parametrů.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/17/09:43
 */
class ValueEditorFactory {
    
	/** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;

    /**
     * Pokusí se nalézt editor hodnoty daného datového typu. Pokud exsituje
	 * poskytne odkaz na tento editor, pokud ne vrátí {@code null}.
	 *
     * @param type datový typ parametru.
     * @return editor hodnoty, pokud takový existuje, jinak {@code fasle}.
     */
	public static TableCellEditor createTableCellEditor(Class type) {
		return factory.pCreateTableCellEditor(type);
	}

    /** Iniciuje továrnu. */
    private ValueEditorFactory() { 
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }

	/** Odkaz na továrnu poskytující editory hodnot parametrů. */
	private static ValueEditorFactory factory;
	static {
		factory = new ValueEditorFactory();
	}
    
    private TableCellEditor pCreateTableCellEditor(Class type) {
        
        Class<? extends ParameterCellEditor> editorClass =
                ExtensionsOfTypes.getConnectedParameterCellEditor(type);
        
        if (editorClass != null) {
            try {
                ParameterCellEditor editor = editorClass.newInstance();
                return editor;
            } catch (InstantiationException e) {
                String tile = messages.getString("title.instantiation_excp",
                        "It cannot possible to create new instance");
                Despr.showError(tile, e, Level.WARNING, true);
                return null;
            } catch (IllegalAccessException e) {
                String title = messages.getString("title.illegal_access_excp", 
                        "Illegal access to editor");
                Despr.showError(title, e, Level.WARNING, true);
                return null;
            }
        } else {
            return null;
        }
    }
}
