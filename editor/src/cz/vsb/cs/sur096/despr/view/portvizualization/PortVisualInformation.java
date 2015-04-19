package cz.vsb.cs.sur096.despr.view.portvizualization;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.awt.Color;

/**
 * Struktura zpřístupňující informace k vizualizaci portu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/18/20:20
 */
public class PortVisualInformation {

    private transient LocalizeMessages messages;
    
    /** Odkaz na uzel stromu s typy. */
    private ITypeNode typeInfo;
    
    /**
	 * Příznak zda se jedná o pole. Pokud ano použijí se 
	 * informace o typu komponenty pole. A port bude mít
	 * zdvojený rámeček.
	 */
    private boolean array;
    
    /**
     * Iniciuje strukturu s odkazem na uzel ve stromě typů.
     * @param typeInfo odkaz na uzel se stromem typů.
     */
    public PortVisualInformation(ITypeNode typeInfo) {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        this.typeInfo = typeInfo;
        this.array = false;
    }
    
    /**
     * Poskytne přiřazenou barvu typu.
     * @return barva která byla typu přiřazena.
     */
    public Color getColor() {
        return typeInfo.getColor();
    }
    
    /**
     * Poskytne hloubku na které se typ nachází.
     * @return hloubka na které se typ ve stromu nachází.
     */
    public int getDeep() {
        return typeInfo.getDeep();
    }
    
    /**
     * Poskytne specifikaci pro daný typ.
     * @return specifikace typu.
     */
    public String getSpecified() {
        return typeInfo.getSpecified().toUpperCase();
    }
    
    /**
     * Zjistí zda se jedná o pole.
     * @return {@code true} pokud se jedná o pole, jinak {@code false}.
     */
    public boolean isArray() {
        return array;
    }
    
    /**
     * Nastaví příznak toho že se jedná o pole.
     * @param isArray hodnota příznaku.
     */
    public void setArray(boolean isArray) {
        this.array = isArray;
    }
    
    /**
     * Přidá posluchače zajímajícího se změnu informací nutných
	 * k vizualizaci typu.
	 * @param pviListener posluchač.
     */
    public void addPVIChangeListener(PVIChangeListener pviListener) {
        typeInfo.addPVIChangeListener(pviListener);
    }
    
    /**
     * Smaže posluchače zajímajícího se o změnu informací nutných
	 * k vizualizaci typu.
     * @param pviListener posluchač.
     */
    public void removePVIChangeListener(PVIChangeListener pviListener) {
        typeInfo.removePVIChangeListener(pviListener);
    }
    
    /**
     * Poskytne textovou reprezentaci struktury.
     * @return řetězec {@code "Color: value\nDeep: value\nSpecified: value\n"};
     */
    @Override
    public String toString() {
        return String.format("%s:%s\n%s:%s\n%s:%s\n",
                messages.getString("title.color", "Color"), getColor(),
                messages.getString("title.deep", "Deep"), getDeep(),
                messages.getString("title.specified", "Specified"), getSpecified());
    }
}