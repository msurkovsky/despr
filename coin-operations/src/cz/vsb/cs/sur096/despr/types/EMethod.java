
package cz.vsb.cs.sur096.despr.types;

import cz.vsb.cs.sur096.despr.types.Choosable;

/**
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/04/16/08:34
 */
public enum EMethod implements Choosable<EMethod> {

    CROSS, SQUARE;

    private String choosed;
    
    private EMethod() {
        choosed = name();
    }
    
    @Override
    public EMethod getChoosedValue() {
        return valueOf(choosed);
    }

    @Override
    public void setChooseValue(EMethod t) {
        this.choosed = t.name();
    }

    @Override
    public EMethod[] getAllPossibilities() {
        return values();
    }
}
