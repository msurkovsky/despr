
package cz.vsb.cs.sur096.despr.utils;

import java.util.*;

/**
 * Třída generující jednoznačné ID čísla v rámci jednoho běhu JVM.
 * Poskytuje generátory spojitých řad čísel. Ve výchozím stavu obsahuje jeden
 * generátor, ke kterému je možné přistupovat přes bezparametrické metody. 
 * Metody s parametry přistupují ke konkrétním generátorům.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/09/9:31
 */
public final class ID {

    private static int DEFAULT_GENERATOR = 0;
    
	/**
	 * Jako druhý "defaultní" generátor je generátor id čísel pro
	 * indexované body.
	 */
    public final static int POINT_GENERATOR = 1;
    
	/** aktuální volné id */
    private int id = 0;

    private Set<Integer> usedId;
    
    private static final List<ID> idGenerators;
    static {
        idGenerators = new ArrayList<ID>();
        idGenerators.add(new ID());
        idGenerators.add(new ID());
    }
    
    private ID() {
        usedId = new HashSet<Integer>();
    }
    
    /**
     * Vrátí další volné číslo z defaultního generátoru.
     * @return další volné číslo.
     */
    public static int getNextID() {
        return getNextID(DEFAULT_GENERATOR);
    }
    
    /**
     * Vrátí další volné číslo z konkrétního generátoru.
     * @param generatorId id generátoru.
     * @return další volné číslo.
     */
    public static int getNextID(int generatorId) {
        if (generatorId >= 0 && generatorId < idGenerators.size()) {
            ID idGenerator = idGenerators.get(generatorId);
            idGenerator.id += 1;
            return idGenerator.id;
        } else {
            throw new IndexOutOfBoundsException("Bad ID of generator.");
        }
    }

    /**
     * Přidá dané id do defaultního generátoru.
     * @param id číslo které má být přidáno.
     * @return {@code true} pokud se id podařilo vložit, jinak {@code false}.
     */
    public static boolean addId(int id) {
        return addId(id, DEFAULT_GENERATOR);
    }
    
    /**
     * Přidá dané id do konkrétního generátoru.
     * @param id číslo které má být přidáno.
     * @param generatorId id konkrétního generátoru.
     * @return {@code true} pokud se id podařilo vložit, jinak {@code false}.
     */
    public static boolean addId(int id, int generatorId) {
        if (generatorId >= 0 && generatorId < idGenerators.size()) {
            ID idGenerator = idGenerators.get(generatorId);
            boolean addOk =  idGenerator.usedId.add(id);
            if (!addOk) {
                return false;
            }

            idGenerator.id = Collections.max(idGenerator.usedId);

            return true;
        } else {
            throw new IndexOutOfBoundsException("Bad ID of generator.");
        }
    }
    
    /**
     * Metoda vytvoří nový generátor a vrátí číslo (id) nového 
	 * generátoru.
     * @return číslo nového generátoru, přes které je možné
	 * k němu dále přistupovat.
     */
    public static int createNewIDGenerator() {
        idGenerators.add(new ID());
        return idGenerators.size() - 1;
    }

    /**
     * Resetuje defaultní generátor.
     */
    public static void resetIds() {
        resetIds(DEFAULT_GENERATOR);
    }
    
    /**
	 * Resetuje konkrétní generátor.
     * @param generatorId id generátoru, který má byt resetován.
     */
    public static void resetIds(int generatorId) {
        if (generatorId >= 0 && generatorId < idGenerators.size()) {
            idGenerators.set(generatorId, new ID());
        } else {
            throw new IndexOutOfBoundsException("Bad ID of generator.");
        }
    }
}
