package cz.vsb.cs.sur096.despr.types;

/**
 * Definuje typy mezi jejichž hodnotami je možné vybírat a
 * vybranou hodnotu si zapamatovat. Uživatel tak může definovat 
 * výčet konstant a pomocí {@code enum} který bude implementovat 
 * toto rozhraní. V aplikaci pak bude možné z takto definovaného
 * seznamu parametrů vybrat v editoru parametrů.
 * <br>
 * Příklad:
 * <pre>
 * <code>
 * public enum EChoose implements Choosable&lt;EChoose&gt; {
 * 
 *   CHOOSE_1, CHOOSE_2, CHOOSE3;
 * 
 *   private String choosedValue;
 *   
 *   private EChoose() {
 *      choosedValue = name();
 *   }
 *   
 *   &#064;Override
 *   public EChoose getChoosedValue() {
 *      return valueOf(choosedValue);
 *   }
 *
 *   &#064;Override
 *   public void setChooseValue(EChoose choosedValue) {
 *      this.choosedValue = choosedValue.name();
 *   }
 *
 *   &#064;Override
 *   public EChoose[] getAllPossibilities() {
 *      return values();
 *   }
 * }
 * </code>
 * </pre>
 * S typem se v aplikaci pracuje normálně jako s jakýmkoliv jiným type. Navíc
 * v grafickém rozhraní je možné u takto definovaných parametrů automaticky
 * vygenerovat komponentu pro nastaveni hodnoty.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a> 
 * @version 2012/01/27/18:51
 */
public interface Choosable<T extends Enum<T>> {
    
    /**
     * Poskytne vybranou konstantu.
     * @return konstanta, která byla vybrána.
     */
    public T getChoosedValue();
    
    /**
     * Nastaví vybranou hodnotu.
     * @param chooseValue vybrana hodnota.
     */
    public void setChooseValue(T chooseValue);
    
    /**
     * Poskytne seznam všech dostupných možností.
     * @return seznam definovaných konstant.
     */
    public T[] getAllPossibilities();
}
