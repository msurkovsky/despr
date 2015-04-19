
package cz.vsb.cs.sur096.despr.types;

import cz.vsb.cs.sur096.despr.types.Choosable;

/**
 *
 * @author Martin Šurkovský, sur096
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public enum EReduceMethod implements Choosable<EReduceMethod> {

    HISTOGRAM, AVERAGE, MEDIAN;

    private String choosedValue;

    private EReduceMethod() {
        choosedValue = name();
    }
    @Override
    public EReduceMethod getChoosedValue() {
        return valueOf(choosedValue);
    }

    @Override
    public void setChooseValue(EReduceMethod t) {
        choosedValue = t.name();
        
    }

    @Override
    public EReduceMethod[] getAllPossibilities() {
        return values();
    }
}
