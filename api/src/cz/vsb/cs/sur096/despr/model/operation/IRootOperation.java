
package cz.vsb.cs.sur096.despr.model.operation;

/**
 * Rozhraní definující tzv. kořenové operace. Jedná se o operace, které zpravidla
 * poskytuje celou kolekci vstupních dat. Obsahuje tak navíc hlavně metody pro 
 * procházení této kolekce.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/10/15:58
 */
public interface IRootOperation extends IOperation {
    
    /**
     * Zjistí zda existuje další prvek ke zpracování. Pokud je v jednom
     * grafu použito více kořenových operací jsou zpracovány do té doby
     * dokud mají všechny data. Pokud jedna z nich vrátí {@code false}
     * je celý proces zpracovávaní ukončen.
     * @return {@code true} pokud existuje další prvek k zpracovaní,
     * jinak {@code false}.
     */
    public boolean hasNext();
    
    /**
     * Posune ukazatel pozice na další prvek. Tak může být znovu zavolána
     * metoda {@code IOperatin.execute()} a proces se opakuje.
     * @throws IndexOutOfBoundsException pokud by se pokusil přeci 
     * jen přistoupit k dalšímu prvku, který již neexistuje.
     */
    public void setNext() throws IndexOutOfBoundsException ;
    
    /**
     * Nastaví ukazatel na začátek kolekce. Tato metoda je volána pokaždé
     * na konci zpracování celého cyklu. Tak je možné proces znovu spustit.
     */
    public void resetIterator();
    
    /**
     * Vrátí celkový počet zpracovávaných prvků.
     * @return celkový počet prvků v kolekci, nebo -1 pokud by se mělo jednat
     * o nekonečnou smyčku.
     */
    public int getCount();

    /**
     * Metoda by měla nastavit všechny nutné počáteční podmínky před samotným
     * spuštěním metody {@code IOperation.execute()}. Tzn. načíst nebo
     * vygenerovat data nastavit ukazatel na požadovanou pozici, zjistit
     * velikost kolekce, atd. ať je možné volat výše uvedené metody.
     */
    public void init();

    /**
     * Zjistí zda byla operace inicializována. Hodí se v případech kdy změna
     * vstupních parametrů zapříčíní to, že je nutné operaci znova inicializovat.
     * @return {@code true} pokud operace byla inicializována, pokud vyžaduje
     * novou inicializaci pak {@code false}.
     */
    public boolean wasInit();
}
