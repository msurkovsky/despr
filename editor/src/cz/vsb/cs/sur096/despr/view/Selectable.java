package cz.vsb.cs.sur096.despr.view;

import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;

/**
 * Rozhraní pro prvky plátna, které je možné vybrat.
 * 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/06/20/16:23
 */
public interface Selectable {
    
    /**
     * Metoda Zjišťující zda je daný objekt vybrán, či nikoli.
		 * @return {@code true} pokud je daný objekt vybrán, jinak {@code false}.
		 */
		public boolean isSelected();
		
		/**
		 * Metoda nastavující příznak objektu, podle toho zda je vybrán nebo ne.
		 * Metoda by také měla informovat o změně stavu všechny registrované
		 * posluchače pomocí rozhraní {@code PropertyChangeListener}.
		 * Posluchači reagují na jméno proměnné tedy "selected".
		 * Příklad:
		 * {@code listener.firePropertyChange("selected", true, false);}
		 * @param selected {@code true} pokud ma byt objekt vybrán, 
		 * {@code false} pokud vybrán není.
		 */
		public void setSelected(boolean selected);
		
		/**
		 * Objekt by měl definovat metodu pro registraci posluchače na 
		 * kliknutí myši. Obslužný kód je dodán automaticky ({@code SelectableHandler}).
     * 
     * @param l posluchač.
     * @see SelectableHandler
     */
    public void addMouseListener(MouseListener l);

    /**
     * Objekt by měl být schopný dát vědět jiným objektům o změně svého stavu.
     * @param l objekt, který se zajímá o změnu stavu vybratelného objektu.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Smazání posluchače zajímajícího se o změnu stavu.
     * @param l posluchač.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
}
