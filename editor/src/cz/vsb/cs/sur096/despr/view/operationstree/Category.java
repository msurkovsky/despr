
package cz.vsb.cs.sur096.despr.view.operationstree;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Implementace rozhraní {@code MutableTreeNode}, která slouží pro rozdělení
 * uživatelský definovaných operací do stromu kategorií.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/13/09:42
 */
public final class Category implements MutableTreeNode {
    
    /** Seznam lokalizačních zpráv.*/
    private transient LocalizeMessages messages;
    /** Odkaz na rodičovskou kategorii.*/
    private MutableTreeNode parent;
    /** Seznam potomků kategorie.*/
    private List<MutableTreeNode> childeren;
    /** Uživatelský objekt, který kategorie uchovává.*/
    private Object userObject;
    
    /**
     * Iniciuje prázdnou kategorii. To znamená seznam potomků je prázdný a
	 * rodičovská kategorie neexistuje, je nastavena na {@code null}.
     */
    public Category() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        childeren = new ArrayList<MutableTreeNode>();
        parent = null;
    }
    
    /**
     * Iniciuje kategorii se seznamem potomků.
     * @param childeren seznam potomků dané kategorie.
     */
    public Category(List<Category> childeren) {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        this.childeren = new ArrayList<MutableTreeNode>(childeren);
        for (MutableTreeNode mtn : this.childeren) {
            mtn.setParent(this);
        }
        parent = null;
    }
    
    /**
     * Vloží nového potomka na zadanou pozici.
     * @param child nový potomek.
     * @param index pozice kde má být vložen.
	 * @throws IllegalArgumentException pokud index menší než nula.
     */
    @Override
    public void insert(MutableTreeNode child, int index) 
			throws IllegalArgumentException {
        int size = childeren.size();
        if (index < 0) {
            throw new IllegalArgumentException(String.format(
                    messages.getString("exception.index_out_of_range", 
                                       "Index '%d' is out of range <%d, %d)!"),
                    index, 0, size));
        }
        
        if (index >= size) {
            childeren.add(child);
        } else {
            List<MutableTreeNode> newChilderenList = 
                    new ArrayList<MutableTreeNode>(size + 1);
            for (int i = 0; i < index; i++) {
                newChilderenList.add(childeren.get(i));
            }
            newChilderenList.add(child);
            for (int i = index; i < size; i++) {
                newChilderenList.add(childeren.get(i));
            }
            childeren.clear();
            childeren.addAll(newChilderenList);
        }
        
        child.setParent(this);
    }

    /**
     * Smaže potomka podle indexu.
     * @param index index potomka v seznamu.
     */
    @Override
    public void remove(int index) {
        childeren.remove(index);
    }

    /**
     * Smaže potomka podle odkazu na objekt.
     * @param node odkaz na potomka.
     */
    @Override
    public void remove(MutableTreeNode node) {
        
        for (int i = 0; i < childeren.size(); i++) {
            if (node == childeren.get(i)) {
                childeren.remove(i);
            }
        }
    }

    /**
     * Nastaví uživatelský objekt.
     * @param object uživatelský objekt, který je uchován
	 * v rámci dané kategorie.
     */
    @Override
    public void setUserObject(Object object) {
        userObject = object;
    }
    
    /**
     * Poskytne uložený uživatelský objekt.
     * @return uživatelský objekt.
     */
    public Object getUserObject() {
        return userObject;
    }

    /**
     * Smaže kategorii s rodičovské kategorie pokud existuje.
     */
    @Override
    public void removeFromParent() {
        if (parent != null) {
            parent.remove(this);
        }
    }

    /**
     * Nastaví rodiče kategorii.
     * @param newParent nová rodičovská kategorie.
     */
    @Override
    public void setParent(MutableTreeNode newParent) {
        parent = newParent;
    }

    /**
     * Poskytne odkaz na potomka podle indexu.
     * @param childIndex index potomka v seznamu.
     * @return odkaz na potomka, pokud existuje.
	 * @throws IllegalArgumentException pokud je index potomka
	 * mimo rozsah seznamu.
     */
    @Override
    public TreeNode getChildAt(int childIndex) 
		throws IllegalArgumentException {

        int size = childeren.size();
        if (childIndex >= 0 && childIndex < size) {
            return childeren.get(childIndex);
        } else {
            throw new IllegalArgumentException(String.format(
                    messages.getString("exception.index_out_of_range", 
                                       "Index '%d' is out of range <%d, %d)!"),
                    childIndex, 0, size));
        }
    }

    /**
     * Zjistí počet potomků kategorie.
     * @return počet potomků.
     */
    @Override
    public int getChildCount() {
        return childeren.size();
    }

    /**
     * Poskytne odkaz na rodičovskou kategorii.
     * @return odkaz na rodičovskou kategorii, pokud
	 * neexistuje pak vrátí {@code null}.
     */
    @Override
    public TreeNode getParent() {
        return parent;
    }

    /**
     * Zjistí index potomka v seznamu na základě odkazu na něj.
     * @param node odkaz na potomka který by měl být v seznamu.
     * @return index potomka v seznamu, pokud existuje, jinak -1.
     */
    @Override
    public int getIndex(TreeNode node) {
        int size = childeren.size();
        for (int i = 0; i < size; i++) {
            if (childeren.get(i) == node) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Zjisti zda kategorie povoluje potomky.
     * @return {@code true} pokud je uživatelským
	 * objektem nastaveno jméno kategorie. Pokud je 
	 * v rámci uživatelského objektu uložen odkaz na operaci pak 
	 * vrátí {@code false}.
     */
    @Override
    public boolean getAllowsChildren() {
		if (userObject instanceof String) return true;
		return false;
    }

    /**
     * Zjistí zda se jedná o list ve stromu.
     * @return {@code true} pokud je uživatelským
	 * objektem objekt typu {@code IOperation}, pokud ne
	 * pak vrátí {@code false}.
     */
    @Override
    public boolean isLeaf() {
        if (userObject instanceof IOperation) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Poskytne výčet potomků.
     * @return výčet potomků.
     */
    @Override
    public Enumeration children() {
        return Collections.enumeration(childeren);
    }
    
    /**
     * Zjistí zda je daná kategorie potomkem této kategorie.
     * @param node kategorie která by měla být potomkem této kategorie.
     * @return {@code true} pokud je {@code node} opravdu potomkem
	 * této kategorie nebo se jedná o stejnou kategorii. Jinak
	 * vrací {@code false}.
     */
    public boolean isNodeChild(Category node) {
        if (this.equals(node)) {
            return true;
        } else {
            if (parent == null) {
                return false;
            } else {
                return ((Category) parent).isNodeChild(node);
            }
        }
    }
    
    /**
     * Porovná tuto kategorii s jinou. Kategorie jsou stejné pokud
	 * obsahují stejné uživatelské objekty.
     * @param o objekt s kterým má být kategorie porovnána.
     * @return {@code true} pokud se shodují uživatelské objekty, jinak
	 * {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Category == false) return false;
        return userObject.equals(((Category)o).getUserObject());
    }

    /**
     * Vypočte hash hodnotu pro kategorii.
     * @return hash hodnotu kategorie.
     */
    @Override
    public int hashCode() {
        int result = 17;
        int tmp = userObject == null ? 0 : userObject.hashCode();
        return 37 * result + tmp;
    }
    
    /**
     * Zjistí úroveň na které se kategorie ve stromu nachází.
     * @return úroveň na které se kategorie v rámci stromu nachází.
     */
    public int getLevel() {
        TreeNode ancestor;
        int levels = 0;

        ancestor = this;
        while((ancestor = ancestor.getParent()) != null) {
            levels++;
        }

        return levels;
    }
    
    /**
     * Přidá nového potomka na konec seznamu.
     * @param newChild nová potomek.
     */
    public void add(MutableTreeNode newChild) {
        if(newChild != null && newChild.getParent() == this)
            insert(newChild, getChildCount() - 1);
        else
            insert(newChild, getChildCount());
    }
    
    /**
     * Poskytne seznam všech operací které se nacházejí v kategorii
	 * i všech podkategorií.
     * @return seznam všech operací které jsou potomky této kategorie.
     */
    public List<IOperation> getAllOperations() {
        List<IOperation> operations = new ArrayList<IOperation>();
        for (MutableTreeNode child : childeren) {
            if (child instanceof Category) {
                Object o = ((Category) child).getUserObject();
                if (o instanceof String) {
                    operations.addAll(((Category) child).getAllOperations());
                } else if (o instanceof IOperation) {
                    operations.add((IOperation) o);
                }
            }
        }
        
        return operations;
    }
    
    /**
     * Poskytne textovou reprezentaci kategorie.
     * @return textovou reprezentaci kategorie.
     */
    @Override
    public String toString() {
        if (userObject instanceof IOperation) {
            return userObject.getClass().getName();
        } else if (userObject instanceof String) {
            return (String) userObject;
        } else {
            return super.toString();
        }
    }
}