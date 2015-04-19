
package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.types.Copier;
import cz.vsb.cs.sur096.despr.utils.ExtensionsOfTypes;
import java.lang.reflect.Array;

/**
 * Umožní vytvořit kopii hlubokou kopii daného objektu čí zjistit,
 * zda je to možné.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/07/20:21
 */
final class CopyableObjects {
    
	/** Lokalizační zprávy */
    private static LocalizeMessages messages;
	/** Zabrání vytvořit instanci objektu */
    private CopyableObjects() { 
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
    /**
     * Vytvoří kopii požadovaného objektu.
     * @param o objekt, který má být zkopírován.
     * @return pokud je definován {@code Copier} objekt pro daný typ
	 * pak pomocí něj vytvoří kopii daného objektu, pokud ne vrátí {@code null}.
	 * @throws RuntimeException pokud selže inicializace {@code Copier} objektu.
     */
    public static Object copy(Object o) throws RuntimeException {
        Class type = o.getClass();
        Class<? extends Copier> copierClass = ExtensionsOfTypes.getConnectedCopier(type);
        if (copierClass != null) {
            try {
                Copier copier = copierClass.newInstance();
                return copier.makeCopy(o);
            } catch (InstantiationException ex) {
                String message = messages.getString("exception.inistatiation", 
                        "It can not creating new instance Copier!");
                throw new RuntimeException(
                        String.format("%s (type = %s)", message, type.getName()));
                
            } catch (IllegalAccessException ex) {
                String message = messages.getString("exception.illegal_acces", 
                        "Illegal access to copier type!");
                throw new RuntimeException(String.format("%s (type = %s)", message, type.getName()));
            }
        } else if (o.getClass().isArray()) {
            Class componentType = type.getComponentType();
            copierClass = ExtensionsOfTypes.getConnectedCopier(componentType);
            if (copierClass != null) {
                int lenght = Array.getLength(o);
                Object array = Array.newInstance(componentType, lenght);
                for (int i = 0; i < lenght; i++) {
                    Array.set(array, i, copy(Array.get(o, i)));
                }
                return array;
            } else {
                return null;
            }
        }
        else {
            return null;
        }
    }
    
    /**
     * Zjistí zda pro daný typ je možné vytvořit kopii.
     * @param c typ který by měl být zkopírován.
     * @return {@code true} pokud najde {@code Copier} objekt pro daný typ,
	 * jinak {@code false}.
     */
    public static boolean canMakeCopy(Class c) {
        Class copier = ExtensionsOfTypes.getConnectedCopier(c);
        if (c.isArray()) {
            return canMakeCopy(c.getComponentType());
        } else if (copier != null) {
            return true;
        } else {
            return false;
        }
    }
}