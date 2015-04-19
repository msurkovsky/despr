
package cz.vsb.cs.sur096.despr.utils.persistenceGraph;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;

/**
 * Slouží pro zpracování parametru konkrétního objektu. Vezme objekt,
 * projde všechny jeho parametry a ty které nejsou označeny jako
 * {@code transient}, {@code static} nebo by neprošly filtrem, tak 
 * je spáruje s jejich čtecí a zapisovací metodou. To je důvod proč
 * všechny parametry které mají být uloženy musí mít definovány veřejé
 * přístupové metody ve formátu JavaBeans. Např. u vestavěných typů, které
 * to tak nemají, např. {@code java.io.File} je možné definovat Wrapper,
 * který danou funkcionalitu zařídí. Je tak možné uložit hodnotu prakticky 
 * jakéhokoliv typu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/21/14:44
 */
public class ObjectProperties implements Iterable<String> {
    
    private transient LocalizeMessages messages;
    
    private Map<String, ReadWriteMethod> properties;
    
    private FieldFilter filter;
    
    /**
     * Iniciuje vlastnosti typu.
     * @param type typ který má být zpracován.
     * @param filter filtr pro vlastnosti které mají být zařazeny.
     */
    public ObjectProperties(Class type, FieldFilter filter) {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        properties = new HashMap<String, ReadWriteMethod>();
        this.filter = filter;
        init(type);
        
    }
    
    /**
     * Iniciuje vlastnosti typu s výchozím filtrem. Který
	 * vždy vrací {@code true}, takže pustí všechny metody.
     * @param type typ který má být zpracován.
     */
    public ObjectProperties(Class type) {
        this(type, new FieldFilter() {

            @Override
            public boolean accept(Field field) {
                return true;
            }
        });
    }
    
    /**
     * Přečte typ a vytvoří dvojice (jméno typu, čtecí a zapisovací metoda).
     * @param type typ který má být zpracován.
     * @throws NullPointerException v případě že parametr který projde filtrem
	 * nemá definovanou čtecí nebo zapisovací metodu.
     */
    private void init(Class type) throws NullPointerException {
        
        // load parameters from super class
        Class superClass = type.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            init(superClass);
        }
        
        Field[] fields = type.getDeclaredFields();
        // notice. Arrays.asList don't return ArrayList, but some inner interpretation!
        List<Method> methods = new ArrayList<Method>(Arrays.asList(type.getDeclaredMethods()));
        
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isTransient(modifiers) && !Modifier.isStatic(modifiers) && filter.accept(field)) {
                String fieldName = field.getName();
                
                int READ_METHOD = 1;
                int WRITE_METHOD = 2;
                int readAndWrite = 0;
                
                String fieldNameUC = fieldName.toUpperCase();
                String getMethodName = "GET" + fieldNameUC;
                String isMethodName  = "IS" + fieldNameUC;
                String setMethodName = "SET" + fieldNameUC;
                
                Method readMethod = null, writeMethod = null;
                
                for (int i = 0; i < methods.size(); i++) {
                
                    Method method = methods.get(i);
                    String methodName = method.getName().toUpperCase();
                    
                    if ((readAndWrite & READ_METHOD) == 0 && 
                            (methodName.equals(getMethodName) || methodName.equals(isMethodName))) {
                        
                        readMethod = method;
                        readAndWrite += READ_METHOD;
                        methods.remove(i); 
                        i--;
                    } else if ((readAndWrite & WRITE_METHOD) == 0 && 
                            methodName.equals(setMethodName)) {
                        
                        writeMethod = method;
                        readAndWrite += WRITE_METHOD;
                        methods.remove(i); i--;
                    }
                    
                    if (readAndWrite == READ_METHOD + WRITE_METHOD) {
                        break;
                    }
                }
                
                if (readAndWrite != READ_METHOD + WRITE_METHOD) {
                    String message = String.format(messages.getString(
                            "exception_read_and_write_method", "For the parameter '%s'"), fieldName);
                    
                    if (readAndWrite == 0) {
                        message = String.format("%s %s", message, 
                                messages.getString("exception_both_method", 
                                "was not found neither read method nor write method!"));
                    } else if ((readAndWrite & READ_METHOD) == 0) {
                        message = String.format("%s %s", message,
                                messages.getString("exception_read_method",
                                "was not found read method!"));
                    } else if ((readAndWrite & WRITE_METHOD) == 0) {
                        message = String.format("%s %s", message, 
                                messages.getString("exception_write_method",
                                "was not found write method!"));
                    }
                    
                    throw new NullPointerException(message);
                } else {
                    properties.put(fieldName, new ReadWriteMethod(readMethod, writeMethod));
                }
            }
        }
    }
    
    /**
     * Poskytne čtecí metodu pro parametr se zadaným jménem.
     * @param parameterName jméno parametru.
     * @return čtecí metoda.
     */
    public Method getReadMethod(String parameterName) {
        return properties.get(parameterName).getReadMethod();
    }
    
    /**
     * Poskytne zapisovací metodu pro parametr se zadaným jménem.
     * @param parameterName jméno parametru.
     * @return zapisovací metodu.
     */
    public Method getWriteMethod(String parameterName) {
        return properties.get(parameterName).getWriteMethod();
    }
    
    /**
     * Nastaví hodnotu parametru danému objektu.
     * @param parameterName jméno parametru.
     * @param obj konkrétní instance daného typu.
     * @param arg hodnota která má být nastavena.
     */
    public void set(String parameterName, Object obj, Object arg) {
        try {
            ReadWriteMethod rwMethod = properties.get(parameterName);
            Method writeMethod = rwMethod.getWriteMethod();
            writeMethod.invoke(obj, arg);
        } catch (IllegalAccessException ex) {
            String title = messages.getString("title.illegal_access_excp", "Illegal Access");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IllegalArgumentException ex) {
            String title = messages.getString("title.illegal_argument_excp", "Illegal Argument");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (InvocationTargetException ex) {
            String title = messages.getString("title.invoaction_target_excp", "Set parameter failed");
            Despr.showError(title, ex.getCause() != null ? ex.getCause() : ex, Level.WARNING, true);
        } catch (NullPointerException ex) {
            String title = messages.getString("title.null_pointer_excp", "Null Pointer");
            String msg = String.format(
                    messages.getString("exception.rw_method_does_not_exists", 
                    "Read and write method does not exist for parameter: '%s'"), 
                    parameterName);
            Despr.showError(title, new NullPointerException(msg), Level.WARNING, true);
        }
    }
    
    /**
     * Získá hodnotu z parametru.
     * @param parameterName jméno parametru.
     * @param obj konkrétní instance daného typu.
     * @return hodnotu parametru, konkrétní instance.
     */
    public Object get(String parameterName, Object obj) {
        Object ret = null;
        try {
            ret = properties.get(parameterName).getReadMethod().invoke(obj);
        } catch (IllegalAccessException ex) {
            String title = messages.getString("title.illegal_access_excp", "Illegal Access");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IllegalArgumentException ex) {
            String title = messages.getString("title.illegal_argument_excp", "Illegal Argument");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (InvocationTargetException ex) {
            String title = messages.getString("title.invoaction_target_excp", "Invocation target");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (NullPointerException ex) {
            String title = messages.getString("title.null_pointer_excp", "Null Pointer");
            String msg = String.format(
                    messages.getString("exception.rw_method_does_not_exists", 
                    "Read and write method does not exist for parameter: '%s'"), 
                    parameterName);
            Despr.showError(title, new NullPointerException(msg), Level.WARNING, true);
        }
        
        return ret;
    }

    /**
     * Iterátor přes jména parametrů.
     * @return iterátor přes jména parametrů.
     */
    @Override
    public Iterator<String> iterator() {
        Set<String> keys = properties.keySet();
        return keys.iterator();
    }
    
    /**
     * Struktura pro uložení čtecí a zapisovací metody.
     */
    private class ReadWriteMethod {
        
        private Method readMethod;
        private Method writeMethod;
        
        public ReadWriteMethod(Method readMethod, Method writeMethod) {
            this.readMethod = readMethod;
            this.writeMethod = writeMethod;
        }
        
        public Method getReadMethod() {
            return readMethod;
        }
        
        public Method getWriteMethod() {
            return writeMethod;
        }
    }
}
