package cz.vsb.cs.sur096.despr.view.portvizualization;

import cz.vsb.cs.sur096.despr.utils.ColorPalete;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Nástroj starající se o dynamické vytváření a úpravy stromu typů. 
 * Dotazy na vizuální informace k typu jsou směřovány na tento na tento
 * nástroj. Ten pokud typ nenajde zařadí jej do stromu a informace vygeneruje.
 * Tak jak tak jsou pro každý typ vždy poskytnu informace pro vygenerování
 * vizuálních informací o portu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public final class Types {
    
    /**
     * Seznam typů na kořenové úrovni. Díky tomu,
	 * že je využíváno mechanismu dědičnosti pro tvorbu stromu
	 * je nutné držet typ {@code java.lang.Object} mimo strom zvlášť
	 * jinak by pohltil všechny ostatní typy pod sebe, což je nežádoucí.
	 * Je tedy uchován seznam kořenových typů, každý z těchto typů pak má
	 * svoji jedinečnou barvu kterou propaguje svoji skupině dceřiných typů.
     */
	
	/**
	 * Poskytne seznam kořenových typů.
	 */
    private List<ITypeNode> types;
    
    private static Types t;
    static {
        t = new Types();
    }
    
	public static List<ITypeNode> getTypes() {
		return t.pGetTypes();
	}
    
    /**
     * Poskytne informace nutné pro vizualizaci portu pro daný typ..
     * @param type typ který bude tvořit model portu.
     * @return vizuální informace přiřazené k typu.
     */
    public static PortVisualInformation getPVI(Class type) {
        if (type.equals(Object.class)) {
            return new PortVisualInformation(new TypeNode(Object.class, Color.WHITE));
        }
        ITypeNode tn = t.getInfo(type);
        PortVisualInformation pvi = tn.getPortVizualInformation();
        if (type.isArray()) {
            pvi.setArray(true);
        }
        return pvi;
    }
    
    /**
     * Iniciuje nástroj pro dynamické vytváření stromu typů.
     */
    private Types() {
        types = new ArrayList<ITypeNode>();
        loadDefaults();
    }
    
    /**
     * Načte defaultní typy, jedná se objektové reprezentace
	 * primitivních typů plus řetězce.
     */
    private void loadDefaults() {
        getInfo(Number.class);
        getInfo(Byte.class);
        getInfo(Short.class);
        getInfo(Integer.class);
        getInfo(Long.class);
        getInfo(Float.class);
        getInfo(Double.class);
        
        getInfo(Boolean.class);
        getInfo(Character.class);
        getInfo(String.class);
    }
    
    /**
     * Získá odkaz na uzel ve stromu reprezentující daný typ.
     * @param type typ o němž mají být zjištěny vizualizační informace.
     * @return ukazatel na uzel do stromu typů.
     */
    private ITypeNode getInfo(Class type) {
        
        boolean isArray = type.isArray();
        Class reType;
        
         if (isArray) {
            reType = getBasicType(type);
        } else {
            reType = type;
        }
        
        for (ITypeNode tn : types) {
            Class nodeType = tn.getType();
            if (nodeType.isAssignableFrom(reType)) {
                if (nodeType.equals(reType)) {
                    return  tn;
                } else {
                    return searchIn(tn, reType);
                }
            } else if (reType.isAssignableFrom(nodeType)) {

                types.remove(tn);
                Color color = tn.getColor();

                ITypeNode newParent = new TypeNode(reType, color);
                newParent.addChild(tn);

                List<ITypeNode> removedTypes = new ArrayList<ITypeNode>();
                int size = types.size();
                for (int i = 0; i < size; i++) {
                    ITypeNode typeAtPosition = types.get(i);
                    if (reType.isAssignableFrom(typeAtPosition.getType())) {
                        removedTypes.add(typeAtPosition);
                        types.remove(typeAtPosition);
                        i--; size--;
                    }
                }
                types.add(newParent);
                for (ITypeNode removed : removedTypes) {
                    add(removed, newParent);
                }

                return newParent;
            }
        }
        
        ITypeNode newType = new TypeNode(reType, ColorPalete.getColor(reType));
        types.add(newType);
        return newType;
    }
    
    /**
     * Zjistí základní typ komponenty v poli. Noří se tak dlouho
	 * dokud nenarazí na typ komponent z kterých se pole skládá.
     * @param type typ reprezentující pole.
     * @return typ komponent pole.
     */
    private Class getBasicType(Class type) {
        if (type.isArray()) {
            return getBasicType(type.getComponentType());
        } else {
            return type;
        }
    }
    
    /**
     * Metoda hledá daný typ v konkrétním uzlu stromu.
     * @param tn uzel stromu.
     * @param type typ který je hledán.
     * @return ukazatel na uzel do stromu typů.
     */
    private ITypeNode searchIn(ITypeNode tn, Class type) {
        
        Class nodeType = tn.getType();
        if (nodeType.equals(type)) {
            return tn;
        } else if (nodeType.isAssignableFrom(type)) {
            List<ITypeNode> childeren = tn.getChilderen();
            for (ITypeNode child : childeren) {
                ITypeNode searched = searchIn(child, type);
                if (searched != null) {
                    return searched;
                } else if (type.isAssignableFrom(child.getType())) {
                    // zde se musi prakticky zopakovat to stejne jako v metode getInfo()
                    // tzn. projit vsechny potomky a zkontrolovat zda nahodou nejsou 
                    // potomkem noveho typu
                    childeren.remove(child);
                    Color color = child.getColor();

                    ITypeNode newParent = new TypeNode(type, color);
                    newParent.addChild(child);
                    
                    List<ITypeNode> removedChilderen = new ArrayList<ITypeNode>();
                    int size = childeren.size();
                    for (int i = 0; i < size; i++) {
                        ITypeNode childAtPosition = childeren.get(i);
                        if (type.isAssignableFrom(childAtPosition.getType())) {
                            removedChilderen.add(childAtPosition);
                            childeren.remove(i);
                            i--; size--;
                        }
                    }
                    
                    tn.addChild(newParent);
                    for (ITypeNode removed : removedChilderen) {
                        add(removed, newParent);
                    }
                    
                    return newParent;
                }
            }

            return add(type, tn);
        } else {
            return null; // this is ok, I must checking all childeren of parent
        }
    }
    
    /**
     * Přidá nový typ do konkrétního uzlu.
     * @param type typ který je přidáván.
     * @param to uzel do kterého má být přidán.
     * @return ukazatel na uzel který byl přidán do stromu.
     */
    private ITypeNode add(Class type, ITypeNode to) {
        ITypeNode node = new TypeNode(type);
        to.addChild(node);
        return node;
    }
    
    /**
     * Slouží pro přidání již existujícího uzlu do jiného uzlu.
     * @param node uzel který má být přidán.
     * @param to uzel do kterého má být přidán.
     */
    private void add(ITypeNode node, ITypeNode to) {
        to.addChild(node);
    }
	
	private List<ITypeNode> pGetTypes() {
		return types;
	}
}