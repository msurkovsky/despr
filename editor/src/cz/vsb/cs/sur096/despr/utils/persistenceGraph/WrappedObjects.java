
package cz.vsb.cs.sur096.despr.utils.persistenceGraph;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.types.Wrapper;
import cz.vsb.cs.sur096.despr.utils.ExtensionsOfTypes;
import java.util.logging.Level;

/**
 * Zpřístupňuje definované wrappery pro svázané typy.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/30/20:30
 */
public class WrappedObjects {
    
    private static transient LocalizeMessages messages;

    private WrappedObjects() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
	/**
	 * Poskytne wrapper pro daný typ.
	 * @param type typ pro který by měl být definován wrapper.
	 * @return pokud takovýto wrapper je opravdu definován pak
	 * poskytne odkaz na jeho instanci. Pokud ne vrátí {@code null}.
	 */
    public static Wrapper getWrapper(Class type) {
        Class<? extends Wrapper> wrapperClass = ExtensionsOfTypes.getConnectedWrapper(type);
        if (wrapperClass != null) {
            try {
                Wrapper wrapper = wrapperClass.newInstance();
                return wrapper;
            } catch (InstantiationException ex) {
                String title = messages.getString("title.instantiation_excp", 
                        "Instantiation Exception");
                Despr.showError(title, ex, Level.WARNING, true);
            } catch (IllegalAccessException ex) {
                String title = messages.getString("title.illegal_access_excp",
                        "Illegal Access");
                Despr.showError(title, ex, Level.WARNING, true);
            }
        }
        return null;
    }
}
