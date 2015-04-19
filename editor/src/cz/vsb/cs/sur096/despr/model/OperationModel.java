
package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.IParameters;
import cz.vsb.cs.sur096.despr.collections.Parameters;
import cz.vsb.cs.sur096.despr.collections.TabuList;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.utils.ID;
import java.beans.ExceptionListener;
import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Rozšířený model operace, který přidává funkcionalitu, nutnou pro 
 * komunikaci v rámci aplikace, objektu typu: {@code IOperation}.
 * 
 * @author Martin Surkovsky, sur096 <martin.surkovsky at gmail.com>
 * @version 2011/08/09/09:00
 * @version 2011/11/15/20:07 dodano rozsireni o tzv. svazene typy
 */
public class OperationModel implements IOperationModel {

    /** Seznam lokalizačních zpráv.*/
    protected transient LocalizeMessages messages;
	/** Seznam posluchačů zajímajících se o vyvolané výjimky.*/
    protected transient List<ExceptionListener> exceptionListeners;
    
	/** Uživatelská operace.*/
    protected IOperation op;
	/** Seznam vstupních parametrů.*/
    protected IParameters<IInputParameter> inputParameters;
	/** Seznam výstupních parametrů.*/
    protected IParameters<IOutputParameter> outputParameters;
    
	/** ID operace.*/
    protected int id;
	/** Číslo úrovně na které se operace nachází*/
    protected int level;
	/** Seznam zakázaných operací*/
    protected TabuList<Integer> tabuList;
    
    /**
     * Konstruktor, který inicializuje mezivrstvu mezi
	 * uživatelskou operací a aplikací.
     * @param op uživatelská operace.
     */
    public OperationModel(IOperation op) {
        this(op, ID.getNextID());
    }
    
    /**
     * Konstruktor, který inicializuje mezivrstvu mezi
	 * uživatelskou operací a aplikací. Tento konstruktor
	 * se používá pro znovu načtení již uložených postupů.
	 * Je silně nedoporučeno předávat nějaká svoje ID.
     * @param op uživatelská operace.
     * @param id ID operace.
	 * @throws IllegalArgumentException pokud je použito
	 * vadné ID, tj. takové se kterým nebude spolupracovat
	 * utilita {@code cz.vsb.cs.sur096.despr.util.ID}.
     */
    public OperationModel(IOperation op, int id)
			throws IllegalArgumentException {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        exceptionListeners = new ArrayList<ExceptionListener>();
        
        this.op = op;
        inputParameters = new Parameters<IInputParameter>();
        outputParameters = new Parameters<IOutputParameter>();
        
        boolean idOk = ID.addId(id);
        if (!idOk) {
            throw new IllegalArgumentException(String.format(
                    "%s '%d'!", 
                    messages.getString("exception.bad_id", "ID could not be use!"),
                    id));
        }
        this.id = id;
        tabuList = new TabuList<Integer>();
        tabuList.add(id);

        readOperation(op.getClass());
        level = pComputeLevel();
    }
    
    /**
     * Poskytne id operace. 
     * @return unikátní celé kladné číslo podle nějž je možné identifikovat
	 * operaci v rámci aplikace.
     */
    @Override
    public int getId() {
        return id;
    }
    
    /**
     * Poskytne jméno operace. Je použito jméno třídy.
     * @return jméno operace.
     */
    @Override
    public String getName() {
        return op.getClass().getSimpleName();
    }
    
    /**
     * Poskytne jméno operace, které je použito pro komunikaci
	 * s uživatelem. Jméno je načteno z lokalizačního souboru dané operace.
     * @return uživatelsky přívětivé jméno. Pokud záznam neexistuje vrátí 
     * jméno třídy.
     */
    @Override
    public String getDisplayName() {
        String displayName = op.getLocalizeMessage("name");
        if (displayName == null) {
            return getName();
        } else {
            return displayName;
        }
    }

    /**
     * Poskytne krátký popisek operace. Popisek je získán z lokalizačního 
     * souboru dané operace.
     * @return krátký popisek operace. Pokud neexistuje je vrácen prázdný
     * řetězec.
     */
    @Override
    public String getDescription() {
        String displayDescription = op.getLocalizeMessage("description");
        if (displayDescription == null) {
            return "";
        }else {
            return displayDescription;
        }
    }
    
    /**
     * Vrátí odkaz na uživatelsky definovanou operaci, kterou rozšiřuje.
     * @return odkaz na původní operaci.
     */
    @Override
    public IOperation getOperation() {
        return op;
    }
    
    /**
     * Poskytne kolekci vstupních parametrů.
     * @return kolekci vstupních parametrů.
     */
    @Override
    public IParameters<IInputParameter> getInputParameters() {
        return inputParameters;
    }

    /**
     * Poskytne kolekci výstupních parametrů.
     * @return kolekci výstupních parametrů.
     */
    @Override
    public IParameters<IOutputParameter> getOutputParameters() {
        return outputParameters;
    }

    /**
     * Poskytne číslo úrovně, tj. hloubku ve stromě operací.
     * @return celé kladné číslo úrovně na které se operace nachází 
	 * v rámci grafu. Pokud svou úroveň není schopna určit
	 * vrátí -1.
     */
    @Override
    public int getLevel() {
        return level;
    }

    /**
     * Vypočte úroveň, hloubku na které se operace nachází v rámci grafu.
     * Hloubka se počítá na základě hloubky všech předchozích operací,
     * které do dané operace posílají nějaká data. Pokud všechny předchozí
     * operace mají známou hloubku, tj. {@code > -1} je vybrána ta největší
     * ke které se přičte jednička. Pokud některá z předchozích operací nezná
     * svou hloubku pak se nepočítá ani hloubka této operace. Pozn. kořenové
     * operace mají vždy hloubku nula. Takže se od nich začíná.
     */
    @Override
    public void computeLevel() {
        int oldLevel = this.level;
        this.level = pComputeLevel();
        
        if (oldLevel != this.level) {
            for (IOutputParameter outputParameter : outputParameters) {
                outputParameter.fireChangeOperationLevel(level);
            }
        }
    }
    
    private int pComputeLevel() {
        
        int maxLevel = -1; 
        boolean allInputParametersInner = true;
        for (IInputParameter ip : inputParameters) {
            if (ip.getType() == EInputParameterType.OUTER) {
                allInputParametersInner = false;
                int previousOpLevel = ip.getPreviousOpLevel();
                if (previousOpLevel > maxLevel) {
                    maxLevel = previousOpLevel;
                } else if (previousOpLevel == -1) {
                    maxLevel = -1;
                    break;
                }
            }
        }
        
        if (maxLevel > -1) {
            return maxLevel + 1;
        } else if (allInputParametersInner) {
            return 0;
        } else {
            // je nutne vzdy kdyz se resetuje port resetovat i 
            // hodnotu levelu jinak to ve specifickych pripadech zacne
            // blbnout. Nastedujici podminka se totiz vyhodnoti jako false
            // (zustanou oldLevel a nove vypocteny maxLevel) v nasledujicim kroku
            /// stejne a hodnota se nepropaguje dale
//            this.level = -1;
            return -1;
        }
    }
    
    /**
     * Poskytne seznam zakázaných operací.
     * @return seznam zakázaných operací.
     */
    @Override
    public TabuList<Integer> getTabuList() {
        return tabuList;
    }

    /**
     * Metoda aktualizuje seznam zakázaných operací a dá o tom vědět 
     * ostatním napojeným operacím.
     * 
     * @param list seznam zakázaných operací který má být přidán/odebrán
     * z aktuálního seznamu.
     * @param method metoda aktualizace buď seznam rozšířen nebo zúžen.
     */
    @Override
    public void updateTabuList(TabuList list, ETabuListUpdateMethod method) {
        
        if (method == ETabuListUpdateMethod.ADD) {
            tabuList.addValues(list);
        } else if (method == ETabuListUpdateMethod.REMOVE) {
            tabuList.removeValues(list);
        } else {
            throw new IllegalArgumentException(
                    "OperationModel.updateTabuList bad method paramater");
        }
        
        for (IOutputParameter outputParameter : outputParameters) {
            // sending to output parameter and next to other operations
            outputParameter.fireTabuListChange(list, method);
        }
    }

    /**
     * Přidá posluchače výjimek.
     * @param l posluchač.
     */
    @Override
    public void addExceptionListener(ExceptionListener l) {
        if (exceptionListeners == null) {
            exceptionListeners = new ArrayList<ExceptionListener>();
        }
        
        exceptionListeners.add(l);
    }

    /**
     * Smaže posluchače výjimek.
     * @param l posluchač.
     */
    @Override
    public void removeExceptionListener(ExceptionListener l) {
        exceptionListeners.remove(l);
    }

    /**
     * Rozešle posluchačům výjimek informaci o vyvolané výjimce.
     * @param e výjimka.
     */
    @Override
    public void fireException(Exception e) {
        for (ExceptionListener l : exceptionListeners) {
            l.exceptionThrown(e);
        }
    }
    
    /**
     * Spustí zpracování operace a po dokončení přepošle výstupní
	 * data napojeným operacím.
     * @return prázdný objekt {@code null}.
     * @throws Exception pokud v rámci operace nastane jakákoliv chyba.
     */
    @Override
    public Void call() throws Exception {
        try {
            op.execute();
        } catch (Exception ex) {
            // do zpravy chyby je pridana informace o tom v ktere
            // operaci chyba nastala a chyba je poslana jak posluchacum
            // tak k dalsimu zpracovani.
            String opName = getDisplayName().trim().replaceAll("\\n", " ");
            String msg = String.format("%s: (%s@%d)",
                    ex.getMessage(), opName, getId());
            
            Exception excp = new Exception(msg, ex);
            fireException(excp);
            throw excp;
        }
        
        // dam vedet ze na vystupnich parametetrech operace by meli byt k
        // dispozici data.
        for (IOutputParameter outputp : outputParameters) {
            outputp.fireOutputParameterValueChanged();
        }
        return null;
    }
    
    /**
     * Porovnává dvě operace, prvně podle úrovně, na které
	 * se nachází a pokud jsou na stejné úrovni tak podle ID.
     * @param o operace s kterou má byt tato porovnána
     * @return -1 pokud je aktuální úroveň menší než úroveň
	 * srovnávané operace. 1 naopak. Pokud jsou stejné porovnají
	 * se podle ID -1 pokud je aktuální ID operace menší než srovnávané
	 * 1 pokud je tomu naopak a 0 pokud jsou stejné (toto je v aplikaci
	 * ovšem vyloučené je totiž použit generátor jednoznačných ID).
     */
    @Override
    public int compareTo(IOperationModel o) {
        int currentLevel = getLevel();
		int opLevel = o.getLevel();

		if (currentLevel < opLevel) {
			return -1;
		} else if (currentLevel > opLevel) {
			return 1;
		} else {
			int currentId = getId();
			int opId = o.getId();

			if (currentId < opId) {
				return -1;
			} else if (currentId > opId) {
				return 1;
			} else {
				return 0;
			}
		}
    }
    
    /**
     * Textová reprezentace operace.
     * @return vrátí textovou reprezentaci operace. Ve formátu:
	 * jméno operace[level]@ID\n jména parametrů, jejich typy a hodnoty
     */
    @Override
	public String toString() {
        StringBuilder builder = new StringBuilder();
        String opName = getDisplayName().trim().replaceAll("\\n", " ");
        builder.append(opName);
        builder.append("[");
        builder.append(Integer.toString(level));
        builder.append("]");
        builder.append("@");
        builder.append(id);
        builder.append("\n");
        for (IInputParameter param : inputParameters) {
            if (param.getType() == EInputParameterType.INNER) {
                builder.append("  ");
                builder.append(param.getDisplayName());
                builder.append(":");
                builder.append(param.getDataType().getName());
                builder.append(" = ");
                builder.append(param.getValue());
                builder.append("\n");
            }
        }
        
        return builder.toString();
	}
    
    /**
     * Pomocí reflexe projde uživatelsky definovanou operaci.
	 * Rozdělí vstupní, výstupní parametry a vytvoří kolekce
	 * parametru.
     * @param c odkaz na třídu reprezentující uživatelsky definovanou
	 * operaci.
     */
    private void readOperation(Class c) {
        
        Class superClass = c.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            readOperation(superClass);
        }
        
        List<Field> fields = new ArrayList<Field>(Arrays.asList(c.getDeclaredFields()));
        int fieldsCount = fields.size();
        
        // nacteni vstupnich parametru
        for (int i = 0; i < fieldsCount; i++) {
            Field field = fields.get(i);
            if (field.isAnnotationPresent(AInputParameter.class)) {
                AInputParameter inputAnnotation = field.getAnnotation(AInputParameter.class);
                try {
                    InputParameter ip = new InputParameter(field.getName(), 
                                                           inputAnnotation.enteringAs(),
                                                           this, 
                                                           inputAnnotation.value(),
                                                           inputAnnotation.lock());
                
                    inputParameters.put(ip);
                    fields.remove(i); i--; fieldsCount--;
                } catch (IntrospectionException ex) {
                    Despr.showError(
                            messages.getString("exception.creating_input_parameter_problem_title", 
                                               "Loading the input parameter from the operation."),
                            ex, Level.SEVERE, true);
                }
            }
        }
        
        // nacteni vystupnich parametru
        for (int i = 0; i < fieldsCount; i++) {
            Field field = fields.get(i);
            if (field.isAnnotationPresent(AOutputParameter.class)) {
                AOutputParameter outputAnnotation = field.getAnnotation(AOutputParameter.class);
                String dependsInputParamName = outputAnnotation.depends();
                
                if (!dependsInputParamName.equals("")) {
                    IInputParameter inputParameterModel = inputParameters.get(dependsInputParamName);
                    // existuje vstupni parameter se jmenem 'dependsInputParamName'?
                    if (inputParameterModel != null) { 
                        // pridani posluchace k zavislemu parametru
                        try {
                            IOutputParameter outputParameter = new OutputParameter(
                                    field.getName(), outputAnnotation.enteringAs(), 
                                    this);
                            inputParameterModel.addInputParameterTypeChangeListener(outputParameter);
                            outputParameters.put(outputParameter);
                            continue; // vystupni parameter byl pridan 
                        } catch (IntrospectionException ex) {
                            Despr.showError(
                                    messages.getString("exception.creating_input_parameter_problem_title", 
                                                       "Loading the input parameter from the operation."),
                                    ex, Level.SEVERE, true);
                        }
                    } else {
                        // Tato vyjimka je definovana pouze pro to kdyby uzivatel
                        // v definici sve operace udelal preklep a napsal neexistujici
                        // jmeno vstupniho parameteru. V takovem pripade se mu 
                        // da timto vedet, ale vyjimka jinak neni v aplikaci dale
                        // odchytavana.
                        throw new RuntimeException(String.format("%s '%s'!",
                                messages.getString("exception.bad_name_of_depends_parameter", 
                                                   "Bad name of the depends parameter. "
                                                   + "That parameter does not exists"),
                                dependsInputParamName));
                    }
                }
                
                try {
                    outputParameters.put(new OutputParameter(field.getName(), 
                            outputAnnotation.enteringAs(), this));
                
                } catch (IntrospectionException ex) {
                    Despr.showError(
                            messages.getString("exception.creating_output_parameter_problem_title",
                                               "Loading the output parameter from the operation."),
                            ex, Level.SEVERE, true);
                }
            }
        }

        // parametry se jednou setridi pri inicializaci a pro dalsi pouziti uz se jejich poradi nemeni!
        inputParameters.sort();
    }
}
