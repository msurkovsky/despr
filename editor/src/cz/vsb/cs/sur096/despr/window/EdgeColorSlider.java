
package cz.vsb.cs.sur096.despr.window;

import cz.vsb.cs.sur096.despr.view.Edge;
import cz.vsb.cs.sur096.despr.view.Selectable;
import cz.vsb.cs.sur096.despr.view.SelectedObjectsChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import javax.swing.JSlider;

/**
 *  Komponenta sloužící pro nastavení barvy hrany spojující operace.
 *  Slouží tzv. pro vizuální potlačení hrany. V grafu se mohou nalézat hrany
 *  které slouží pouze pro předávání nějakých pomocných informací, pak může být
 *  někdy vhodné, hlavně u složitějších grafů, takovéto hrany vizuálně potlačit
 *  aby neodváděli pozornost.
 *
 *  Komponenta také implementuje rozhraní {@code SelectedObjectsChangeListener},
 *  takže reaguje na změnu vybraných komponent. Pokud je vybrána jedna hrana
 *  pak je umožněno ji změnit jas (barvu).
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/02/11:14
 */
public class EdgeColorSlider extends JSlider implements SelectedObjectsChangeListener {
    
    /** Seznam posluchačů reagujících změny hodnoty slideru.*/
    private PropertyChangeSupport pcs;
    
    /** Poslední vybraná hrana.*/
    private Edge lastEdge;
    
    /**
     * Iniciuje slider pro nastavení barvy (jasu). Je omezen
	 * na hodnoty v rozmezí 0 až 230 aby nebyly hrany vůči plátnu zcela
	 * neviditelné.
     */
    public EdgeColorSlider() {
        setMinimum(0);
        setMaximum(230);
        setValue(0);
        setPaintLabels(true);
        setPaintTrack(true);
        setPaintTicks(false);
        setEnabled(false);
    }
    
    /**
     * Překrytá metoda navíc informuje posluchače o změně hodnoty.
     * @param n nová hodnota.
     */    
    @Override
    public final void setValue(int n) {
        int old = getValue();
        super.setValue(n);
        pcs.firePropertyChange("EdgeColorSlider.value", old, n);
    }
    
    /**
     * Reaguje na změnu vybraných objektů. Pokud je vybrán jeden 
	 * objekt a pokud se jedná o hranu pak je umožněno změnit pomocí
	 * slideru barvu hrany. Pokud je počet nulový nebo větší než jedna
	 * pak je slider uzamčen a smazán odkaz na poslední vybranou hranu.
     * @param selectedObjects seznam vybraných objektů.
     */
    @Override
    public void selectedObjectsChange(List<Selectable> selectedObjects) {
        int size = selectedObjects.size();
        
        if (size == 0 || size > 1) {
            if (lastEdge != null) {
                removePropertyChangeListener(lastEdge);
                lastEdge = null;
                setValue(0);
                setEnabled(false);
            }
        } else if (size == 1) {
            Selectable selectable = selectedObjects.get(0);
            if (selectable instanceof Edge) {
                Edge edge = (Edge) selectable;
                lastEdge = edge;
                addPropertyChangeListener(edge);
                setValue(edge.getColor());
                setEnabled(true);
            }
        }
    }
    
    /**
     * Přidá posluchače na změnu hodnoty barvy.
     * @param l posluchač změny hodnoty barvy.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(l);
    }
    
    /**
     * Smaže posluchače na změnu hodnoty barvy.
     * @param l posluchač změny hodnoty barvy.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

}
