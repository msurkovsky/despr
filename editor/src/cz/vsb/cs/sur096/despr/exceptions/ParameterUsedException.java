
package cz.vsb.cs.sur096.despr.exceptions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.IParameter;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;

/**
 * Výjimka zachycující případ přidaní hrany na již obsazený port.
 * 
 * @author Martin Šurkovský,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/02/12/18:12
 */
public class ParameterUsedException extends Exception {

    private transient LocalizeMessages messages;
    private boolean messageSet;
    
	/** Použitý parametr*/
    IParameter usedParameter;
    
	/**
	 * Konstruktor bez parametru.
	 */
	public ParameterUsedException(IParameter usedParameter) {
		super();
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        messageSet = false;
        this.usedParameter = usedParameter;
	}

	/**
	 * Konstruktor předávající chybovou zprávu.
	 * @param message chybová zprava.
	 */
	public ParameterUsedException(String message, IParameter usedParameter) {
		super(message);
        messageSet = true;
        this.usedParameter = usedParameter;
	}
    
    /**
     * Poskytne použitý parametr.
     * @return použitý parametr.
     */
    public IParameter getUsedParameter() {
        return usedParameter;
    }
    
    /**
     * Pokud nebyla nastavena zráva je vygenerována defultní loaklizovaná verze.
     * @return eng: "Parameter is used 'parameter'!"
     */
    @Override
    public String getMessage() {
        if (messageSet) {
            return super.getMessage();
        } else {
            return String.format("%s '%s'!",
                    messages.getString("exceptionMessage", 
                                       "Parameter is used"), 
                    usedParameter.toString());
        }
    }
}
