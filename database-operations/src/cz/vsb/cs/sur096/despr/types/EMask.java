
package cz.vsb.cs.sur096.despr.types;

/**
 * Výčet typů kruhových masek.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/10/20:03
 */
public enum EMask implements Choosable<EMask> {
    CIRCLE_69,
    CIRCLE_72;
    
    private String choosedValue;
    
    private EMask() {
        choosedValue = name();
    }
    
	/**
	 * Poskytne vybraný typ.
	 * @return vybraný typ.
	 */
    @Override
    public EMask getChoosedValue() {
        return valueOf(choosedValue);
    }

	/**
	 * Nastaví vybraný typ.
	 * @param choosedValue vybraný typ.
	 */
    @Override
    public void setChooseValue(EMask choosedValue) {
        this.choosedValue = choosedValue.name();
    }

	/**
	 * Poskytne seznam všech možností.
	 * @return seznam všech možností.
	 */
    @Override
    public EMask[] getAllPossibilities() {
        return values();
    }
}
