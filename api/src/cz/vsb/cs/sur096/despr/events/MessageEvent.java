package cz.vsb.cs.sur096.despr.events;

import java.util.EventObject;


/**
 * Reprezentuje událost obsahující zasílanou zprávu a odkaz na objekt, který ji
 * poslal.
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/05/12:20
 */
public class MessageEvent extends EventObject {
    
    /**
     * Zpráva
     */
    protected String message;
    
    /**
     * Konstruktor přejímající zprávu a objekt, který jí posílá.
     * @param message zpráva.
     * @param source zdrojový objekt.
     */
    public MessageEvent(String message, Object source) {
        super(source);
        this.message = message;
    }
    
    /**
     * Vyzvednutí zprávy z události.
     * @return posílaná zpráva.
     */
    public String getMessage() {
        return message;
    }
}
