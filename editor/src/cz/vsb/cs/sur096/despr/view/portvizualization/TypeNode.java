package cz.vsb.cs.sur096.despr.view.portvizualization;

import cz.vsb.cs.sur096.despr.utils.ColorPalete;
import cz.vsb.cs.sur096.despr.utils.Int2Text;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;

/**
 * Implementace rozhraní {@code ITypeNode}.
 * 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/07/15:46
 */
class TypeNode implements ITypeNode {

	/** Odkaz na nadřazený typ. */
    private ITypeNode parent;
    
	/** Seznam dceřiných typů. */
    private List<ITypeNode> childeren;
	/**
	 * Seznam posluchačů zajímajících se o změnu informací, nashromážděných
	 * k typu.
	 */
    private List<PVIChangeListener> pviChangeListeners;

	/** Typ ke který je v uzlu uložen. */
    private Class type;
	/** Barva která byla typu přidělena. */
    private Color color;
	/** Hloubka ve které se nachází. */
    private int deep;
	/** Specifikační řetězec. */
    private String specified;
    
	/**
	 * Iniciuje uzel ve stromu daným typem.
	 * @param type typ který je do uzlu vložen.
	 */
    public TypeNode(Class type) {
        this(type, null);
    }
    
	/**
	 * Iniciuje uzel ve stromu typem a přiřazenou barvou.
	 * @param type typ který je do uzlu vložen.
	 * @param color barva přiřazená typu.
	 */
    public TypeNode(Class type, Color color) {
        childeren = new ArrayList<ITypeNode>(2);
        pviChangeListeners = new ArrayList<PVIChangeListener>(5);
        
        this.type = type;
        this.color = color;
        this.deep = 0;
        this.specified = "";
        // Je treba novy typ s barvou pridat do palety barev
        ColorPalete.setUsed(type, color, true);
    }
    
    /**
     * Poskytne typ uloženy v uzlu.
     * @return typ uložený v uzlu.
     */
    @Override
    public Class getType() {
        return type;
    }
    
    /**
     * Poskytne nadřazený typ (obecnější typ), pokud existuje.
     * @return nadřazený typ pokud existuje, jinak vrátí {@code null}.
     */
    @Override
    public ITypeNode getParent() {
        return parent;
    }
    
    /**
     * Nastaví nadřazený typ.
     * @param parent nadřazený typ.
     */
    @Override
    public void setParent(ITypeNode parent) {
        this.parent = parent;
    }
    
    /**
     * Přidá dceřiný typ.
     * @param child dceřiny typ (bližší specifikace daného typu).
     */
    @Override
    public void addChild(ITypeNode child) {
        child.setParent(this);
        child.setColor(color);
        child.setDeep(deep + 1);
        childeren.add(child);
        child.overSpecified();
        firePVIChangeListener();
    }
    
    /**
     * Poskytne seznam dceřiných typů.
     * @return seznam dceřiných typů.
     */
    @Override
    public List<ITypeNode> getChilderen() {
        return childeren;
    }

    /**
     * Poskytne seznam sourozeneckých typů, tj. typů kteří mají
	 * společný nadřazený typ.
     * @return seznam typů, které mají stejný nadřazený typ jako aktuální.
     */
    @Override
    public List<ITypeNode> getSiblings() {
        List<ITypeNode> parentChilderens;
        if (parent != null) {
            parentChilderens = parent.getChilderen();
        } else {
            parentChilderens = new ArrayList<ITypeNode>(0);
        }
        return parentChilderens;
    }

    /**
     * Poskytne barvu přiřazenou danému typu. Jedinečnou barvu
	 * si uchovávají pouze typy na nejvyšší úrovni. Dceřiné typy
	 * barvu předka dědí.
     * @return barva přiřazená danému typu.
     */
    @Override
    public Color getColor() {
        return color;
    }

    /**
     * Nastaví barvu typu.
     * @param color barva, která má být přiřazena typu.
     */
    @Override
    public void setColor(Color color) {
        // pri zmene barvy je treba uvolnit stary typ a zapsat novy
        ColorPalete.setUsed(type, this.color, false);
        ColorPalete.setUsed(type, color, true);
        this.color = color;
        for (ITypeNode child : childeren) {
            child.setColor(color);
        }
        firePVIChangeListener();
    }

    /**
     * Poskytne hloubku v jaké se typ ve stromu nachází.
     * @return hloubka typu ve stromu.
     */
    @Override
    public int getDeep() {
        return deep;
    }

    /**
     * Nastaví hloubku typu ve které se ve stromu nachází.
     * @param deep hloubka ve které se daný typ ve stromu nachází.
     */
    @Override
    public void setDeep(int deep) {
        this.deep = deep;
        for (ITypeNode child : childeren) {
            child.setDeep(deep+1);
        }
    }

    /**
     * Poskytne specifikační řetězec typu. Pokud se nachází více typů na
	 * stejné úrovni se společným předkem pak je třeba je nějak odlišit
	 * je jim tedy přiřazen jedinečný řetězec který je od sebe odliší.
     * @return jednoznačný řetězec rozlišující typy na stejné úrovni.
     */
    @Override
    public String getSpecified() {
        return specified;
    }

    /**
     * Nastaví specifikační řetězec typu.
	 * @param specified specifikační řetězec.
     */
    @Override
    public void setSpecified(String specified) {
        String parentSpecified = "";
        if (parent != null) {
            parentSpecified = parent.getSpecified();
        }
        
        this.specified = parentSpecified.equals("") ? 
                specified : parentSpecified + "." + specified;
        
        if (!childeren.isEmpty()) {
            ITypeNode firstChild = childeren.get(0);
            firstChild.overSpecified();
        }
    }
    
    /**
     * Metoda provede novou specifikaci typu. Pokud se změní specifikace předka
	 * je třeba změnu propagovat dále.
     */
    @Override
    public void overSpecified() {
        if (parent != null) {
            List<ITypeNode> siblings = getSiblings();
            if (siblings.size() > 1) {
                int i = 1;
                for (ITypeNode sibling : siblings) {
                    sibling.setSpecified(Int2Text.getText(i));
                    i++;
                }
            }
        }
    }
    
    /**
     * Poskytne balík informací nashromážděných o daném typu, které
	 * použije {@code Port} operace pro své vykreslení.
     * @return kolekci obsahující typ, hloubku na které se nachází 
	 * ve stromu, specifikační řetězec a barvu která mu byla přiřazena.
     */
    @Override
    public PortVisualInformation getPortVizualInformation() {
        return new PortVisualInformation(this);
    }

    /**
     * Přidá posluchače zajímajícího ho se o změnu informací nutných
	 * pro vykreslení portu.
     * @param pviListener posluchač.
     */
    @Override
    public void addPVIChangeListener(PVIChangeListener pviListener) {
        pviChangeListeners.add(pviListener);
    }

    /**
     * Smaže posluchače zajímajícího se o změnu informací nutných pro
	 * vykreslení portu.
     * @param pviListener 
     */
    @Override
    public void removePVIChangeListener(PVIChangeListener pviListener) {
        pviChangeListeners.remove(pviListener);
    }
    
    /**
     * Pošle zprávu o změně informací uložených k typu všem registrovaným
	 * posluchačům.informací uložených k typu všem registrovaným
	 * posluchačům.
     */
    @Override
    public void firePVIChangeListener() {
        for (PVIChangeListener l : pviChangeListeners) {
            l.pviChanged();
        }
        // pokud se nejak zmenil tento uzel pak se zmenily i vsichni jeho 
        for (ITypeNode child : childeren) {
            child.firePVIChangeListener();
        }
    }
    
    /**
     * Vytvoří řetězec popisující typ.
     * @return textový řetězec ve formátu: {code name@(deep | spefied | color)}
     */
    @Override
    public String toString() {
        return type.getSimpleName() + "@(" + deep + "|" + specified + "|" + color + ")";
    }

    ////////////////////////////////////////////////////////////
    // Implementace metod rozhrani TreeNode
    
    /**
     * Poskytne potomka na dané pozici.
     * @param childIndex pozice potomka.
     * @return potomka na dané pozici.
     */
    @Override
    public TreeNode getChildAt(int childIndex) {
        return childeren.get(childIndex);
    }

    /**
     * Zjistí počet potomků.
     * @return počet potomků.
     */
    @Override
    public int getChildCount() {
        return childeren.size();
    }

    /**
     * Poskytne index pozice na které se potomek nachází, pokud existuje.
     * @param node potomek který by měl být v seznamu potomků.
     * @return index na kterém se potomek nachází, nebo pokud nebyl
	 * nalezen pak vrátní -1.
     */
    @Override
    public int getIndex(TreeNode node) {
        return childeren.indexOf(node);
    }

    /**
     * Zjistí zda jsou potomci povolení.
     * @return {@code true}.
     */
    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     * Zjistí zda je uzel listem.
     * @return {@code true} pokud je prázdný seznam potomků, 
	 * jinak {@code false}.
     */
    @Override
    public boolean isLeaf() {
        return childeren.isEmpty();
    }

    /**
     * Poskytne výčet potomků.
     * @return výčet potomků.
     */
    @Override
    public Enumeration children() {
        return Collections.enumeration(childeren);
    }
}
