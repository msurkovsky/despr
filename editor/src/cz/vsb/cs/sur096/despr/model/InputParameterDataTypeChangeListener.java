
package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgesException;

/**
 * Posluchač změny datového typu vstupního parametru.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/27/15:57
 */
public interface InputParameterDataTypeChangeListener {

    /**
     * Reaguje na změnu datového typu vázaného vstupního parametru.
     * @param dataType nový typ.
     * @throws IncorrectEdgesException pokud změnou typu vzniknout
	 * nekorektní hrany, tj. hrany mezi nekompatibilními typy.
     */
    public void inputParameterDataTypeChanged(Class dataType) throws IncorrectEdgesException;
}
