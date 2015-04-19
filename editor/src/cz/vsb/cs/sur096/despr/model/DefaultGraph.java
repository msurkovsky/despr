package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.TabuList;
import cz.vsb.cs.sur096.despr.exceptions.IncompatibleDataTypeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgesException;
import cz.vsb.cs.sur096.despr.exceptions.ParameterUsedException;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.structures.Pair;
import cz.vsb.cs.sur096.despr.types.Copyable;
import java.util.ArrayList;
import java.util.List;

/**
 * Výchozí implementace modelu grafu.
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class DefaultGraph implements IGraph {

	/** Seznam lokalizačních zpráv.*/
    private transient LocalizeMessages messages;
    
	/** Seznam kořenových operací */
    private List<IRootOperationModel> rootOperations;
	/** Seznam všech operací */
    private List<IOperationModel> operations;
	/** Seznam hran */
    private List<IEdge> edges;
    
    /**
     * Iniciuje prázdný graf. 
     */
    public DefaultGraph() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        
        rootOperations = new ArrayList<IRootOperationModel>();
        operations = new ArrayList<IOperationModel>();
        edges = new ArrayList<IEdge>();
    }
    
    /**
     * Poskytne seznam kořenových operací.
     * @return seznam kořenových operací.
     */
    @Override
    public List<IRootOperationModel> getRootOperations() {
        return rootOperations;
    }
    
    /**
     * Poskytne seznam všech operací.
     * @return seznam operací.
     */
    @Override
    public List<IOperationModel> getOperations() {
        return operations;
    }

    /**
     * Poskytne seznam hran.
     * @return seznam hran.
     */
    @Override
    public List<IEdge> getEdges() {
        return edges;
    }
    
    /**
     * Přidá operaci do grafu.
     * @param op operace.
     */
    @Override
    public void addOperation(IOperationModel op) {
        // pokud se jedna o rootovskou operaci ulozim je ulozena
        // navic zvlast. Kvuli snadnemu pristupu.
        if (op instanceof IRootOperationModel) {
            rootOperations.add((IRootOperationModel) op);
        }
        operations.add(op);
    }
    
    /**
     * Smaže operaci z grafu.
     * @param op operace která má být smazána.
     * @return seznam hran které <b>z</b>, nebo <b>do</b> operace vedli.
     */
    @Override
    public List<IEdge> removeOperation(IOperationModel op) {
        
        /* Vsechny hrany, ktere jsou incidentni s mazanaou operaci */
        List<IEdge> removeEdges = new ArrayList<IEdge>(edges.size());
        for (IEdge edge : edges) {
            if (edge.getSource().getParent().equals(op) ||
                    edge.getTarget().getParent().equals(op)) {
               
                removeEdges.add(edge);
            }
        }
        
        /* Odstraneni hran */
        for (IEdge edge : removeEdges) {
            removeEdge(edge);
        }
        
        /* Odstraneni operace */
        operations.remove(op);
        
        // pokud se jedna o rootovskou operaci je treba ji smazat
        // navic ze seznamu technto operaci.
        if (op instanceof IRootOperationModel) {
            rootOperations.remove((IRootOperationModel) op);
        }
        
        return removeEdges;
    }
    
    /**
     * Pokusí se nalézt operaci v grafu podle ID.
     * @param operationId ID operace.
     * @return pokud se operaci s hledaným ID podaří nalézt pak vrátí
	 * odkaz na ni, pokud ne vrátí {@code null}.
     */
    @Override
    public IOperationModel findOperation(int operationId) {
        for (IOperationModel operation : operations) {
            if (operation.getId() == operationId) {
                return operation;
            }
        }
        return null;
    }
    
    /**
     * Přidá hranu do grafu.
     * @param edge model hrany.
     * @return odkaz na model hrany, pokud se podařilo hranu přidat. 
     * @throws ParameterUsedException pokud je snaha použít obsazený parametr.
     * @throws IncorrectEdgeException pokud je vkládaná hrana nekorektní.
     * @throws IncompatibleDataTypeException pokud nejsou vstupní a výstupní
	 * parametry kompatibilní.
     */
    @Override
    public IEdge addEdge(IEdge edge) 
            throws ParameterUsedException, IncorrectEdgeException, IncompatibleDataTypeException {
        
        IOutputParameter source = edge.getSource();
        IInputParameter  target = edge.getTarget();
        
        TabuList<Integer> sourceOpTabuList = source.getParent().getTabuList();
        int targetOpId = target.getParent().getId();
        
        for (int tabuValue : sourceOpTabuList) {
            if (tabuValue == targetOpId) {
                throw new IncorrectEdgeException(source, target);
            }
        }
        
        if (target.isUsed()) {
            throw new ParameterUsedException(target);
        } else if (source.isUsed()) {
            throw new ParameterUsedException(target);
        } else {
            
            Class targetDT = target.getDataType();
            Class sourceDT = source.getDataType();
            // pokud se jedna o pole nejakeho typu zjistim typ komponent 
            // ulozenych v poli.
            Pair<Class, Class> baseDataTypes = getBaseDataTypes(sourceDT, targetDT);
            sourceDT = baseDataTypes.getFirst();
            targetDT = baseDataTypes.getSecond();
                        
            // pokud jsou typy rozdilne, provede se pretypovani
            if (!sourceDT.equals(targetDT)) {
                Class type = source.getDataType();
                try {
                    target.setDataType(type);
                } catch (IncorrectEdgesException ex) {
                    // tato chyba nastava pouze pri mazani hran.
                    // jeste jsem nenarazil a ani me nepada pripad kdyby se to 
                    // mohlo stat pri pridavani hrany. Proto je vyjimka 
                    // preposlana dale pouze jako runtime exception
                    throw new RuntimeException(ex);
                }
            }
            
            // pokud by nebyl typ zdrojoveho parametru, kopirovatelny
            // jen nastaven jako pouzity a nelze z daneho parametru pak
            // cerpad data pro jiny parametr.
            Class sourceDataType = source.getDataType();
            if (!isCopyable(sourceDataType)) {
                source.setUsed(true);
            }
            // do ciloveho portu mohou tect pouze jedny data.
            target.setUsed(true);
            
            source.setCountOutputs(source.getCountOutputs() + 1);
            target.setSourceCountEdges(source.getCountOutputs());
            target.setPreviousOpLevel(source.getOperationLevel());
            
            source.addPropertyChangeListener(target);
            source.addValueChangeListener(target);
            source.addValueChangeListener(edge);
                       
            // aktualizace seznamu zakazanych operaci
            target.getParent().updateTabuList(sourceOpTabuList, 
                                              ETabuListUpdateMethod.ADD);
            // pokud ma zdroj jiz data k dispozici, rovnou se namapuji na
            // na cilovy parameter.
            if (source.hasData()) {
                target.setValue(source.getValue());
            }
            
            // pridani hrany do seznamu hran
            edges.add(edge);
            return edge;
        }
    }
    
    /**
     * Přidá hranu do grafu mezi zdrojovým a cílovým parametrem.
     * @param source zdrojový parametr.
     * @param target cílový parametr.
     * @return model hrany pokud se ji podařilo přidat.
     * @throws ParameterUsedException pokud je snaha použít obsazený parametr.
     * @throws IncorrectEdgeException pokud je vkládaná hrana nekorektní.
     * @throws IncompatibleDataTypeException pokud nejsou vstupní a výstupní
     */
    @Override
    public IEdge addEdge(IOutputParameter source, IInputParameter target) 
            throws ParameterUsedException, IncorrectEdgeException, IncompatibleDataTypeException {
        
        return addEdge(new DefaultEdge(source, target));
    }

    /**
     * Smaže hranu z grafu. 
     * @param edge model hrany, která má být smazána.
     * @throws NullPointerException pokud {@code edge == null}.
     */
    @Override
    public void removeEdge(IEdge edge) throws NullPointerException {
        
        if (edge != null) {
            IOutputParameter source = edge.getSource();
            IInputParameter target = edge.getTarget();
            
            try {
                // nastaveni defaultniho datoveho typu
                target.setDataType(null);
            } catch (IncompatibleDataTypeException ex) {
                // tato vyjimka by nikdy nemela nastat. Metoda setDataType
                // umoznuje v pohode nastavit null parameter nastavuje se
                // pomoci nej defaultni datovy typ. Proto jen pro jistotu
                // je vyjimka preposlana jako runtime.
                throw new RuntimeException(ex);
            } catch (IncorrectEdgesException ex) {
                List<IncorrectEdgeException> incorrectEdges = ex.getIncorrectEdges();
                for (IncorrectEdgeException iee : incorrectEdges) {
                    IEdge incorrectEdge = findEdge(iee.getSource(), iee.getTarget());
                    if (incorrectEdge != null) {
                        incorrectEdge.setIncorrect(true);
                    }
                }
            }
            
            target.setUsed(false);
            source.setUsed(false);
            
            source.removeValueChangeListener(edge.getTarget());
            source.removeValueChangeListener(edge);
            source.removePropertyChangeListener(target);
            
            source.setCountOutputs(source.getCountOutputs() - 1);
            target.setSourceCountEdges(0);
            // po smazani hrany je 100% jiste ze nejsem schopen urcit
            // na jake urovni se operace nachazi, proto se nastavi -1.
            target.setPreviousOpLevel(-1);
            
            TabuList sourceOpTabuList = source.getParent().getTabuList();
            target.getParent().updateTabuList(sourceOpTabuList, ETabuListUpdateMethod.REMOVE);
			edges.remove(edge);
		} else {
            throw new NullPointerException(
                    messages.getString("exception.null_edge_model", 
                                       "NULL edge model"));
        }
    }
    
    /**
     * Pokusí se nalézt hranu na základě vstupního a výstupního parametru.
     * @param source zdrojový (výstupní) parametr.
     * @param target cílový (vstupní) parametr.
     * @return model hrany pokud takový existuje, jinak {@code null}.
     */
    @Override
    public IEdge findEdge(IOutputParameter source, IInputParameter target) {
        IEdge out = null;
		for (IEdge e : edges) {
			// existuje hrana definovana danym cilovym a zdrojovym portem?
            // + navic musi porty patrit stejnym operacim
			if (e.getSource().equals(source) && 
                    e.getSource().getParent().equals(source.getParent()) &&
                    e.getTarget().equals(target) &&
                    e.getTarget().getParent().equals(target.getParent())) {
				out = e;
				break;
			}
		}
		return out;
    }
    
    /**
     * Pokusí se nalézt hranu na základě jejího ID.
     * @param edgeId ID hrany.
     * @return model hrany pokud taková existuje, jinak {@code null}.
     */
    @Override
    public IEdge findEdge(int edgeId) {
        for (IEdge edge : edges) {
            if (edge.getId() == edgeId) {
                return edge;
            }
        }
        
        return null;
    }

	/**
	 * Poskytne textovou reprezentaci grafu. Vypíše všechny operace, vč.
	 * parametrů a seznam hran odkud kam vedou.
	 * @return textovou reprezentaci grafu.
	 */
    @Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("\n");
		for (IOperationModel op : operations) {
			strBuilder.append(String.format("%s%n", op.toString()));
		}
		strBuilder.append("\n");

		int maxNameSourceLength = 0;
		int maxKeySourceLenght = 0;
		int maxNameTargetLength = 0;
		int maxKeyTargetLenght = 0;
		for (IEdge edge : edges) {
            String nameSourceOp = edge.getSource().getParent().getDisplayName().trim().replaceAll("\\n", " ");
			int nameSourceLenght = nameSourceOp.length();
            
			if (nameSourceLenght > maxNameSourceLength) {
				maxNameSourceLength = nameSourceLenght;
			}
			int keySourceLength = edge.getSource().getDisplayName().trim().length();
			if (keySourceLength > maxKeySourceLenght) {
				maxKeySourceLenght = keySourceLength;
			}
            String nameTargetOp = edge.getTarget().getParent().getDisplayName().trim().replaceAll("\\n", " ");
			int nameTargetLength = nameTargetOp.length();
			if (nameTargetLength > maxNameTargetLength) {
				maxNameTargetLength = nameTargetLength;
			}
			int keyTargetLength = edge.getTarget().getDisplayName().trim().length();
			if (keyTargetLength > maxKeyTargetLenght) {
				maxKeyTargetLenght = keyTargetLength;
			}
		}

		maxKeySourceLenght += 2; //  kvuli zavorkam po stranach
		for (IEdge e : edges) {
			strBuilder.append(
				String.format("%-" +maxNameSourceLength + "s %5s%-" + maxKeySourceLenght + "s"
						  + "\t-->\t"
						  + "%-" + maxNameTargetLength  + "s %5s%-" +maxKeyTargetLenght + "s%n",

						  e.getSource().getParent().getDisplayName().trim().replaceAll("\\n", " "),
						  "@" + e.getSource().getParent().getId(),
						  "(" + e.getSource().getDisplayName().trim() + ")",

						  e.getTarget().getParent().getDisplayName().trim().replaceAll("\\n", " "),
						  "@" + e.getTarget().getParent().getId(),
						  "(" + e.getTarget().getDisplayName().trim() + ")")

			);
		}
		return strBuilder.toString();
	}
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody

	/**
	 * Zjistí zda je daný typ kopírovatelný.
	 * @return {@code true} pokud je typ instancí {@code Copyable}
	 * nebo má definovaný {@Copier} objekt, jinak {@code false}.
     */
    private boolean isCopyable(Class cls) {
        if (Copyable.class.isAssignableFrom(cls) || 
            CopyableObjects.canMakeCopy(cls)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Získá dvojici základních typů (zdrojový, cílový) parametr.
	 * Jedná se o parametry, mezi kterými by měla být hrana. Tedy
	 * pokud se jedna o pole hodnot zjistí typ hodnot, které jsou v něm
	 * uloženy. 
     * @param sourceDT zdrojový parametr.
     * @param targetDT cílový parametr.
     * @return základní typy zdrojového a cílového parametru.
     */
    private Pair<Class, Class> getBaseDataTypes(Class sourceDT, Class targetDT) {
        if (sourceDT.isArray() && targetDT.isArray()) {
            sourceDT = sourceDT.getComponentType();
            targetDT = targetDT.getComponentType();
            return getBaseDataTypes(sourceDT, targetDT);
        } else {
            return new Pair<Class, Class>(sourceDT, targetDT);
        }
    }
}
