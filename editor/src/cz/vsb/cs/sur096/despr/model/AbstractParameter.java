
package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.exceptions.IncompatibleDataTypeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgesException;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Abstraktní implementace rozhraní {@code IParameter}.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/08/16:25
 */
public abstract class AbstractParameter extends PropertyDescriptor implements IParameter {
    
    protected transient LocalizeMessages messages;
    
	/** Odkaz na rodičovskou operaci.*/
    private IOperationModel operationModel;

    /** 
     * Informace o tom zda je parametr již použitý, tzn. 
	 * nelze do něj zapisovat nebo z něj číst data.
     */
    private boolean used;

	/** Zařazení parametru. */
    private int order;

	/** Seznam posluchačů. */
    protected transient PropertyChangeSupport pcs;
    
    /**
     * Odkaz na originální uživatelem definovanou operaci. 
	 * Potomci využívají toho to odkazu ke zprostředkování komunikace.
     */
    protected IOperation operation;
    
    /** Slouží pro uchování upraveného typu. */
    protected Class customizedType;
    
    /**
     * Iniciuje parametr. Informace nutné pro přístup k parametru, přepošle
	 * rodičovské třídě a zde nastaví společné vlastnosti parametru.
	 *
     * @param propertyName jméno parametru.
     * @param order řazení parametru. Uživatel může v rámci anotace definovat
	 * pořadí v jakém má byt parametr zařazen, pokud ji neuvede anotace definuje
	 * výchozí hodnotu rovnou 100.
     * @param operationModel model operace (mezivrstva mezi uživatelsky definovanou
	 * operací a aplikací.
     * @throws IntrospectionException pokud by bylo {@code propertyName} prázdné
     * nebo by nebyla nalezena implementace přístupových metod (konvence JavaBean).
     */
    public AbstractParameter(String propertyName, int order, IOperationModel operationModel) 
            throws IntrospectionException {
        
        super(propertyName, operationModel.getOperation().getClass());
        
        // nactou se zpravy lokalizacni zpravy abstraktniho parametru
        messages = Despr.loadLocalizeMessages(AbstractParameter.class, null, false);
        // k nim se nactou jeste zpravy dane tridy.
        messages = Despr.loadLocalizeMessages(getClass(), messages, false);
        pcs = new PropertyChangeSupport(this);
        this.used = false;
        this.operationModel = operationModel;
        this.operation = operationModel.getOperation();
        this.order = order;
    }
    
    /**
     * Poskytne lokalizované jméno parametru, pokud existuje.
     * @return lokalizované jméno parametru, pokud existuje,
	 * jinak jméno z {@code getName()}.
     */ 
    @Override
    public String getDisplayName() {
        String name = getName();
        String displayName = operation.getLocalizeMessage(name);
        if (displayName == null) {
            return name;
        } else {
            return displayName;
        }
    }
    
    /**
     * Metoda vracející odkaz na rodičovské operaci
     * @return odkaz na rodičovskou operaci.
     */
    @Override
    public IOperationModel getParent() {
        return operationModel;
    }
    
    /**
     * Poskytne hodnotu parametru. Hodnota je uložena v původním modelu
     * tato metoda by měla pouze zprostředkovat komunikaci.
     * @return hodnota parametru.
     * @throws RuntimeException pokud selže přístup ke čtecí metodě dané operace.
     */
    @Override
    public Object getValue() throws RuntimeException {
        Object ret = null;
        try {
            ret = getReadMethod().invoke(operation);
        } catch (IllegalAccessException ex) {
            
            String message = String.format("%s '%s' (operation = '%s')!",
                    messages.getString("exception.illegal_access",
                                       "Read method is not accessible for"
                    + "this operation"),
                    getReadMethod().getName(), getOperationDisplayName(operation));
            throw new RuntimeException(message, ex);
        } catch (IllegalArgumentException ex) {
            String message = String.format("%s '%s' (operation = '%s')!",
                    messages.getString("exception.illegal_argument",
                                       "Operation does have that method"),
                    getReadMethod().getName(), getOperationDisplayName(operation));
            throw new RuntimeException(message, ex);
        } catch (InvocationTargetException ex) {
            String message = String.format("Read method = '%s' (operatio = '%s')",
                    getReadMethod().getName(), getOperationDisplayName(operation));
            throw new RuntimeException(message, ex);
        }
        return ret;
    }
    
    private String getOperationDisplayName(IOperation op) {
        String opName = op.getLocalizeMessage("name");
        if (opName == null) {
            opName = op.getClass().getSimpleName();
        } else {
            opName = opName.trim().replaceAll("\\n", " ");
        }
        return opName;
    }
    
    /**
     * Poskytne datový typ parametru.
     * @return datový typ parametru.
     */
    @Override
    public Class getDataType() {
        return customizedType == null ? getPropertyType() : customizedType;
    }
    
    /**
	 * Vzhledem k tomu, že je možné typy určitým způsobem měnit
	 * (konkretizovat čí zobecňovat zpět k výchozímu typu) 
	 * parametr si vždy pamatuje výchozí datový typ.
     * @return výchozí datový typ.
     */
    @Override
    public Class getDefaultDataType() {
        return getPropertyType();
    }
    
    /**
	 * Nastaví nový datový typ.
     * @param dataType nový datový typ.
     * @throws IncompatibleDataTypeException pokud je typ nekompatibilní
	 * s aktuálním typem. Není možné typy měnit libovolně.
     * @throws IncorrectEdgesException změna typu může vyvolat situaci kdy
     * dva spojené parametry hranou měli před změnou kompatibilní datové typy a
	 * po změně ne.
     */
    @Override
    public void setDataType(Class dataType) throws IncompatibleDataTypeException, IncorrectEdgesException {

        Class oldDataType = getDataType();
        if (dataType == null) {
            // bude pouzit defaultni typ
            customizedType = null;
        } else if (getDefaultDataType().isArray() && dataType.isArray()) {
            Class c1 = getDefaultDataType().getComponentType();
            Class c2 = dataType.getComponentType();
            
            if (c1.isAssignableFrom(c2)) {
                customizedType = dataType;
            }
        } else if (getDefaultDataType().isAssignableFrom(dataType)) {
            customizedType = dataType;
        } else {
            String message = String.format("%s\n'%s --> %s'!", 
                    messages.getString("exception.incompatible_data_types",
                                       "Incompatible data types, new type must be compatible with default type"),
                    dataType, getDefaultDataType());
            throw new IncompatibleDataTypeException(message, dataType, getDataType());            
        }
        pcs.firePropertyChange("data_type", oldDataType, dataType);
    }
    
    /**
     * Zjistí zda je daný parametr použitý. Myšleno tak zda 
	 * je možné z něj číst či do něj zapisovat data.
     * @return {@code true} pokud je možné z parametru číst nebo
	 * do něj zapisovat, jinak {@code false}.
     */
    @Override
    public boolean isUsed() {
        return used;
    }

    /**
     * Nastaví použitelnost parametru.
     * @param used je možné z parametru číst či do něj zapisovat?
     */
    @Override
    public void setUsed(boolean used) {
        boolean old = this.used;
        this.used = used;
        pcs.firePropertyChange("used", old, used);
    }
    
    /**
     * Poskytne pořadové číslo parametru.
     * @return pořadové číslo parametru.
     */
    @Override
    public int getOrder() {
        return order;
    }
    
    /**
     * Přidá posluchače na změnu hodnoty vlastností parametru
     * @param l posluchač.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    
    /**
     * Smaže posluchače.
     * @param l posluchač.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
    /**
     * Poskytne textový popis parmetru.
     * @return textový popis ve formátu:
	 * {@code name of operation[name of port]}
     */
    @Override
    public String toString() {
        IOperationModel opModel = getParent();
        String operationName = "";
        if (opModel != null) {
            operationName = opModel.getDisplayName().trim().replaceAll("\\n", " ");
        }
			
        return String.format("%s[%s]", operationName, getDisplayName());
    }
}
