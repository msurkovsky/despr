
package cz.vsb.cs.sur096.despr.exceptions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.IInputParameter;
import cz.vsb.cs.sur096.despr.model.IOutputParameter;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;

/**
 * Výjimka pro postihující situaci, kdy je do grafu vkládána nebo
 * se v ní objeví nekorektní hrana.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/30/19:48
 */
public class IncorrectEdgeException extends Exception {
    
    private transient LocalizeMessages messages;
    private boolean messageSet;
    
	// počátek a cíl nekorektní hrany.
    private IOutputParameter source;
    private IInputParameter target;
    /**
     * Konstruktor, který inicializuje výjimku.
     * @param msg zpráva.
     * @param source zdrojový parametr hrany.
     * @param target cílový parametr hrany.
     */
    public IncorrectEdgeException(String msg, IOutputParameter source, IInputParameter target) {
        super(msg);
        messageSet = true;
        this.source = source;
        this.target = target;
    }
    
    /**
     * Konstruktor, který inicializuje výjimku.
     * @param source zdrojový parametr hrany.
     * @param target cílový parametr hrany.
     */
    public IncorrectEdgeException(IOutputParameter source, IInputParameter target) {
        this(null, source, target);
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        messageSet = false;
    }
    
    /**
     * Poskytne parametr ze kterého hrana vychází.
     * @return zdrojový parametr.
     */
    public IOutputParameter getSource() {
        return source;
    }
    
    /**
     * Poskytne parametr do kterého hrana vstupuje.
     * @return cílový parametr.
     */
    public IInputParameter getTarget() {
        return target;
    }
    
    /**
     * Pokud nebyla nastavena zpráva, je vygenerována defaultní zpráava.
     * @return "Between ports can not be an edge 'source -> target'!".
     */
    @Override
    public String getMessage() {
        if (messageSet) {
            return super.getMessage();
        } else {
            return String.format("%s '%s -> %s'!",
                    messages.getString("exceptionMessage", 
                                       "Between ports can not be an edge"),
                    source.toString(), target.toString());
        }
    }
}
