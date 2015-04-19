
package cz.vsb.cs.sur096.despr.events;

import java.util.EventListener;

/**
 * Rozhraní umožňující zachytávání posílaných zpráv.
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/05/12:00
 */
public interface MessageListener extends EventListener {
   
    /**
     * Metoda zpracovávající přijímané zprávy.
     * @param event událost reprezentující zasílanou zprávu.
     */
    public void catchMessage(MessageEvent event);
}
