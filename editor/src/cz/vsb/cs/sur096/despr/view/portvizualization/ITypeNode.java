package cz.vsb.cs.sur096.despr.view.portvizualization;

import java.awt.Color;
import java.util.List;
import javax.swing.tree.TreeNode;

/**
 * Definice stromové struktury pro uchování hierarchie typů 
 * vstupních/výstupních parametrů operace a informací ovlivňujících
 * vizuální podobou portu operace. Uzel stromu lze použít i jako uzel 
 * modelu stromu implementuje totiž rozhraní {@code TreeNode}.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/07/15:47
 */
public interface ITypeNode extends TreeNode {
    
    /**
     * Poskytne typ uloženy v uzlu.
     * @return typ uložený v uzlu.
     */
    public Class getType();
    
    /**
     * Poskytne nadřazený typ (obecnější typ), pokud existuje.
     * @return nadřazený typ pokud existuje, jinak vrátí {@code null}.
     */
    @Override
    public ITypeNode getParent();
    
    /**
     * Nastaví nadřazený typ.
     * @param parent nadřazený typ.
     */
    public void setParent(ITypeNode parent);
    
    /**
     * Přidá dceřiný typ.
     * @param child dceřiny typ (bližší specifikace daného typu).
     */
    public void addChild(ITypeNode child);
    
    /**
     * Poskytne seznam dceřiných typů.
     * @return seznam dceřiných typů.
     */
    public List<ITypeNode> getChilderen();
    
    /**
     * Poskytne seznam sourozeneckých typů, tj. typů kteří mají
	 * společný nadřazený typ.
     * @return seznam typů, které mají stejný nadřazený typ jako aktuální.
     */
    public List<ITypeNode> getSiblings();
    
    /**
     * Poskytne barvu přiřazenou danému typu. Jedinečnou barvu
	 * si uchovávají pouze typy na nejvyšší úrovni. Dceřiné typy
	 * barvu předka dědí.
     * @return barva přiřazená danému typu.
     */
    public Color getColor();
    
    /**
     * Nastaví barvu typu.
     * @param color barva, která má být přiřazena typu.
     */
    public void setColor(Color color);
    
    /**
     * Poskytne hloubku v jaké se typ ve stromu nachází.
     * @return hloubka typu ve stromu.
     */
    public int getDeep();
    
    /**
     * Nastaví hloubku typu ve které se ve stromu nachází.
     * @param deep hloubka ve které se daný typ ve stromu nachází.
     */
    public void setDeep(int deep);
    
    /**
     * Poskytne specifikační řetězec typu. Pokud se nachází více typů na
	 * stejné úrovni se společným předkem pak je třeba je nějak odlišit
	 * je jim tedy přiřazen jedinečný řetězec který je od sebe odliší.
     * @return jednoznačný řetězec rozlišující typy na stejné úrovni.
     */
    public String getSpecified();
    
    /**
     * Nastaví specifikační řetězec typu.
	 * @param specified specifikační řetězec.
     */
    public void setSpecified(String specified);
    
    /**
     * Metoda provede novou specifikaci typu. Pokud se změní specifikace předka
	 * je třeba změnu propagovat dále.
     */
    public void overSpecified();
    
    /**
     * Poskytne balík informací nashromážděných o daném typu, které
	 * použije {@code Port} operace pro své vykreslení.
     * @return kolekci obsahující typ, hloubku na které se nachází 
	 * ve stromu, specifikační řetězec a barvu která mu byla přiřazena.
     */
    public PortVisualInformation getPortVizualInformation();
    
    /**
     * Přidá posluchače zajímajícího ho se o změnu informací nutných
	 * pro vykreslení portu.
     * @param pviListener posluchač.
     */
    public void addPVIChangeListener(PVIChangeListener pviListener);
    
    /**
     * Smaže posluchače zajímajícího se o změnu informací nutných pro
	 * vykreslení portu.
     * @param pviListener 
     */
    public void removePVIChangeListener(PVIChangeListener pviListener);
    
    /**
     * Pošle zprávu o změně informací uložených k typu všem registrovaným
	 * posluchačům.informací uložených k typu všem registrovaným
	 * posluchačům.
     */
    public void firePVIChangeListener();
}
