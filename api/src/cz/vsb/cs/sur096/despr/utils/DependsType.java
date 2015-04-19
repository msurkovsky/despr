
package cz.vsb.cs.sur096.despr.utils;

import cz.vsb.cs.sur096.despr.exceptions.IncompatibleDataTypeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Nástroj umožňující vytvořit novou instanci závislého typu.
 * V operacích může být výstupní port jedné operace závislý na 
 * vstupním portu té samé operace. Touto závislostí je myšlena 
 * datově typová závislost. Jelikož vstupní port může přijímat nejen stejné
 * datové typy ale i potomky tohoto typu (specifikace daného typu) je v
 * určitých případech vhodné, aby se takto specifikovaný typ projevil 
 * i na výstupním portu. 
 *
 * Tato třída poskytuje statickou metodu, která umí vytvořit novou instanci
 * specifického podtypu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/02/14:19
 */
public final class DependsType {

    /** Nelze vytvořit instanci této třídy. */
    private DependsType() { }
    
    /**
     * Metoda vytvoří novou instanci podtypu {@code subType} daného
	 * nadtypu {@code superType}. 
     * @param superType nadřazený typ.
     * @param subType specifikace nadřazeného typu.
     * @param parameters vstupní parametry.
     * @return pokud nadřazený typ a jeho specifikace mají konstruktory
	 * se stejnými parametry, pak vrátí novou instanci specifického typu.
	 * Jinak vrátí {@code null}.
     * @throws IncompatibleDataTypeException pokud neplatí, že {@code subType}
	 * je "potomkem" (specifikací) {@code superType}.
     * @throws NoSuchMethodException pokud oba objekty neobsahují veřejný konstruktoru
	 * se stejnými parametry.
     */
    public static Object createNewInstance(Class superType,
            Class subType, Object... parameters) throws IncompatibleDataTypeException, NoSuchMethodException {
        
        if (superType.isAssignableFrom(subType)) { // O.K.
            
            Class[] parametersType = new Class[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                parametersType[i] = parameters[i].getClass();
            }
            
            Constructor superTypeConstr;
            Constructor subTypeConstr;
            
            try {
                superTypeConstr = superType.getConstructor(parametersType);
                subTypeConstr = subType.getConstructor(parametersType);
            } catch (NoSuchMethodException e) {
                // pokud se konstruktory nenajdou klasickou cestou, zkusim
                // pohledat konstruktory, do kterych by sly predat dane argumenty
                superTypeConstr = findConstructor(superType, parametersType);
                subTypeConstr = findConstructor(subType, parametersType);
            }
            
            if (superTypeConstr != null && subType != null) {
                try {
                    return subTypeConstr.newInstance(parameters);
                } catch (InstantiationException ex) {
                    throw new RuntimeException("Problem in DependsType util." ,ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException("Problem in DependsType util." ,ex);
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException("Problem in DependsType util." ,ex);
                } catch (InvocationTargetException ex) {
                    throw new RuntimeException("Problem in DependsType util." ,ex);
                }
            } else {
                throw new NoSuchMethodException("Subtype have to contains the "
                            + "construcor with the same arguments such as the supertype!");
            }
        } else {
            throw new IncompatibleDataTypeException(
                    "\"" + superType.getName() + "\"" + 
                    " and " + 
                    "\"" + subType.getName() + "\"" + " is not compatible!", superType, subType);
        }
    }
    
    /**
     * Metoda slouží pro vyhledání konstruktoru na základě typu parametrů.
     * Bere v úvahu jak dědičnost parametrů (do konstruktoru může být dán jako 
	 * parametr potomek nějaké konkretní třídy). Tak stírá rozdíl mezi 
	 * primitivními typy a jejich objektovými reprezentacemi.
     * 
     * @param clazz třída reprezentující objekt
     * @param parameters typy parametru
     * @return konstruktor pokud existuje, jinak {@code null}.
     */
    private static Constructor findConstructor(Class clazz, Class... parameters) {
        
        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (Constructor c : constructors) {
            Class[] types = c.getParameterTypes();
            int parametersLength = parameters.length;
            if (parametersLength == types.length) {
                boolean ok = true;
                for (int i = 0; i < parametersLength; i++) {
                    if (!types[i].isAssignableFrom(parameters[i])) {
                        Field[] fields = parameters[i].getFields();
                        for (Field f : fields) {
                            if (f.getName().equals("TYPE")) {
                                try {
                                    Class clz = (Class) f.get(null);
                                    if (!clz.equals(types[i])) {
                                        ok = false;
                                    }
                                } catch (IllegalArgumentException ex) {
                                } catch (IllegalAccessException ex) {
                                }
                                break;
                            }
                        }
                        if (!ok) break;
                    }
                }
                
                if (ok) {
                    return c;
                }
            }
        }
        return null;
    }
    
    
}
