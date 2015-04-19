
package cz.vsb.cs.sur096.despr.model;

import cz.vsb.cs.sur096.despr.exceptions.IncompatibleDataTypeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgeException;
import cz.vsb.cs.sur096.despr.exceptions.ParameterUsedException;
import java.util.List;

/**
 * Rozhraní definující model grafu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/09/17:22
 */
public interface IGraph {
    
    /**
     * Poskytne seznam kořenových operací.
     * @return seznam kořenových operací.
     */
    public List<IRootOperationModel> getRootOperations();
    
		/**
     * Poskytne seznam všech operací.
     * @return seznam operací.
     */
    public List<IOperationModel> getOperations();
    
    /**
     * Poskytne seznam hran.
     * @return seznam hran.
     */
    public List<IEdge> getEdges();
    
    /**
     * Přidá operaci do grafu.
     * @param op operace.
     */
    public void addOperation(IOperationModel op);
    
    /**
     * Smaže operaci z grafu.
     * @param op operace která má být smazána.
     * @return seznam hran které <b>z</b>, nebo <b>do</b> operace vedli.
     */
    public List<IEdge> removeOperation(IOperationModel op);
    
    /**
     * Pokusí se nalézt operaci v grafu podle ID.
     * @param operationId ID operace.
     * @return pokud se operaci s hledaným ID podaří nalézt pak vrátí
	 * odkaz na ni, pokud ne vrátí {@code null}.
     */
    public IOperationModel findOperation(int operationId);
    
    /**
     * Přidá hranu do grafu.
     * @param edge model hrany.
     * @return odkaz na model hrany, pokud se podařilo hranu přidat. 
     * @throws ParameterUsedException pokud je snaha použít obsazený parametr.
     * @throws IncorrectEdgeException pokud je vkládaná hrana nekorektní.
     * @throws IncompatibleDataTypeException pokud nejsou vstupní a výstupní
	 * parametry kompatibilní.
     */
    public IEdge addEdge(IEdge edge) 
            throws ParameterUsedException, IncorrectEdgeException, IncompatibleDataTypeException;
    
    /**
     * Přidá hranu do grafu mezi zdrojovým a cílovým parametrem.
     * @param source zdrojový parametr.
     * @param target cílový parametr.
     * @return model hrany pokud se ji podařilo přidat.
     * @throws ParameterUsedException pokud je snaha použít obsazený parametr.
     * @throws IncorrectEdgeException pokud je vkládaná hrana nekorektní.
     * @throws IncompatibleDataTypeException pokud nejsou vstupní a výstupní
     */
    public IEdge addEdge(IOutputParameter source, IInputParameter target) 
            throws ParameterUsedException, IncorrectEdgeException, IncompatibleDataTypeException;
    
    /**
     * Smaže hranu z grafu. 
     * @param edge model hrany, která má být smazána.
     * @throws NullPointerException pokud {@code edge == null}.
     */
    public void removeEdge(IEdge edge);
    
    /**
     * Pokusí se nalézt hranu na základě vstupního a výstupního parametru.
     * @param source zdrojový (výstupní) parametr.
     * @param target cílový (vstupní) parametr.
     * @return model hrany pokud takový existuje, jinak {@code null}.
     */
    public IEdge findEdge(IOutputParameter source, IInputParameter target);
    
    /**
     * Pokusí se nalézt hranu na základě jejího ID.
     * @param edgeId ID hrany.
     * @return model hrany pokud taková existuje, jinak {@code null}.
     */
    public IEdge findEdge(int edgeId);
}
