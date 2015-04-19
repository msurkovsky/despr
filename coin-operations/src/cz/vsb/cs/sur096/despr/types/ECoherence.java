
package cz.vsb.cs.sur096.despr.types;

import cz.vsb.cs.sur096.despr.types.Choosable;

/**
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/04/16/08:38
 */
public enum ECoherence implements Choosable<ECoherence> {

    NONE, X_AXIS;

    private String choosed;
    
    private ECoherence() {
        choosed = name();
    }
    
    @Override
    public ECoherence getChoosedValue() {
        return valueOf(choosed);
    }

    @Override
    public void setChooseValue(ECoherence t) {
        this.choosed = t.name();
    }

    @Override
    public ECoherence[] getAllPossibilities() {
        return values();
    }
}
