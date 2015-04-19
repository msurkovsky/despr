
package cz.vsb.cs.sur096.despr.types;

/**
 * Výčet typů vektoru.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/11/15:48
 */
public enum EVectorType implements Choosable<EVectorType> {
    ABSOLUTE_VALUES,
    RELATIVE_VALUES;

    private String choosedValue;
    private EVectorType() {
        choosedValue = name();
    }
    
	/**
	 * Poskytne vybraný typ.
	 * @return vybraný typ.
	 */
    @Override
    public EVectorType getChoosedValue() {
        return valueOf(choosedValue);
    }

	/**
	 * Nastaví vybraný typ.
	 * @param choosedValue vybraný typ.
	 */
    @Override
    public void setChooseValue(EVectorType choosedValue) {
        this.choosedValue = choosedValue.name();
    }

	/**
	 * Poskytne seznam všech možností.
	 * @return seznam všech možností.
	 */
    @Override
    public EVectorType[] getAllPossibilities() {
        return values();
    }
}
