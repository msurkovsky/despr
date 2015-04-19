package cz.vsb.cs.sur096.despr.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Uchovává a zpracovává seznam posluchačů typu: {@code MessageListener} a 
 * umožňuje jim rozesílat zprávy.
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/05/12:04
 */
public class MessageSupport {
    
    /**
     * Objekt, který zprávu vyvolal.
     */
    private Object source;
    
    /**
     * Seznam posluchačů.
     */
    private List<MessageListener> listeners;
    
    /**
     * Konstruktor umožnující vytvořit nový objekt pro uchovávaní a zpracování
     * zpráv.
     * @param source objekt, který zprávy rozesílá.
     */
    public MessageSupport(Object source) {
        this.source = source;
        listeners = new ArrayList<MessageListener>();
    }
    
    /**
     * Přidání posluchače.
     * @param l posluchač.
     */
    public void addMessageListener(MessageListener l) {
        if (listeners == null) {
            listeners = new ArrayList<MessageListener>();
        }
        
        boolean contain = false;
        for (MessageListener listener : listeners) {
            if (listener == l) {
                contain = true;
                break;
            }
        }
        
        if (!contain) {
            listeners.add(l);
        }
    }
    
    /**
     * Smazání posluchače.
     * @param l posluchač.
     */
    public void removeMessageListener(MessageListener l) {
        if (listeners != null) {
            
            for (MessageListener listener : listeners) {
                if (listener == l) {
                    listeners.remove(l);
                    break;
                }
            }
        }
    }
    
    /**
     * Rozeslaní dané zprávy všem registrovaným posluchačům.
     * @param message zpráva.
     */
    public void sendMessage(String message) {
        if (listeners != null) {
            for (MessageListener listener : listeners) {
                listener.catchMessage(new MessageEvent(message, source));
            }
        }
    }
    
    /**
     * Poskytuje odkaz na objekt, který zprávy rozesílá.
     * @return objekt rozesílající zprávy.
     */
    public Object getSource() {
        return source;
    }
}
