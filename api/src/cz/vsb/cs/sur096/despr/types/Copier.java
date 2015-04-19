
package cz.vsb.cs.sur096.despr.types;

/**
 * Představuje "kopírku" pro konkrétní typ. Pokud jsou použity výchozí typy
 * javy jako výstupní parametry operací a je třeba aby bylo možné danou hodnotu
 * použít vícekrát je třeba vytvořit {@code Copier} který obsahuje metodu 
 * schopnou vytvořit hlubokou kopii takového objektu. Typicky to jsou
 * {@code java.io.File, java.util.Integer, java.util.Double, ...}. 
 * K těmto základním typům jsou v aplikaci již vytvořeny objekty tohoto
 * typu.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a> 
 * @version 2012/01/07/20:37
 */
public interface Copier<T> {
    
    /**
     * Metoda schopná vytvořit hlubokou kopii daného objektu.
     * @param o objekt který má být zkopírován
     * @return nová instance daného objektu.
     */
    public T makeCopy(T o);
}
