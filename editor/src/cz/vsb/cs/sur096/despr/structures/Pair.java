
package cz.vsb.cs.sur096.despr.structures;

/**
 * Jednoduchá struktura pro uložení dvojice objektu. Je možné jej využít
 * pokud je třeba vrátit dva objekty z nějaké metody.
 * 
 * @author Martin Surkovsky, sur096 <martin.surkovsky at gmail.com>
 * @version 2012/01/20/21:27
 */
public class Pair<A, B> {
   
    private A first;
    private B second;

    /**
     * Vytvoří novou dvojici objektů.
     * @param first
     * @param second 
     */
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
    
    /**
     * Vrátí první objekt z dvojice.
     * @return první objekt dvojice.
     */
    public A getFirst() {
        return first;
    }
    
    /**
     * Nastaví první objekt z dvojice.
     * @param first objekt vložený na prnví pozici.
     */
    public void setFirst(A first) {
        this.first = first;
    }
    
    /**
     * Vrátí druhý objekt z dvojice.
     * @return druhý objeckt.
     */
    public B getSecond() {
        return second;
    }
    
    /**
     * Nastaví druhý objekt z dvojice.
     * @param second objekt vložený na druhou pozici.
     */
    public void setSecond(B second) {
        this.second = second;
    }
}
