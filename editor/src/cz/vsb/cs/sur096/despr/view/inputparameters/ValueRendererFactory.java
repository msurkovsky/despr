
package cz.vsb.cs.sur096.despr.view.inputparameters;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.utils.ExtensionsOfTypes;
import java.util.logging.Level;
import javax.swing.table.TableCellRenderer;

/**
 * Implementace továrny poskytující renderery pro hodnoty v tabulce daných
 * datových typů.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkvovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/17/08:12
 */
class ValueRendererFactory {
    
    /** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;
    
    /** Iniciuje továrnu. */
    private ValueRendererFactory() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
    /** Odkaz na iniciovanou továrnu. */
    private static ValueRendererFactory factory;
    static {
        factory = new ValueRendererFactory();
    }
    
    /**
     * Poskytne renderer pro hodnotu parametru daného datového typu,
	 * pokud existuje jinak vrátní {@code null}.
     * @param type datový typ pro který by měl existovat renderer.
     * @return renderer pro daný datový typ, pokud existuje, 
	 * jinak vrací {@code null}.
     */
    public static TableCellRenderer createTableCellRenderer(Class type) {
        return factory.pCreateTableCellRenderer(type);
    }
    
    private TableCellRenderer pCreateTableCellRenderer(Class type) {
        Class<? extends ParameterCellRenderer> rendererClass = 
                ExtensionsOfTypes.getConnectedParameterCellRenderer(type);
        
        if (rendererClass != null) {
            try {
                TableCellRenderer renderer = rendererClass.newInstance();
                return renderer;
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
        }
        return new DefaultInputParameterCellRenderer();
    }
}
