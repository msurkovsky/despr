
package cz.vsb.cs.sur096.despr.exceptions;

import java.util.List;

/**
 * Výjimka postihující celou kolekci nekorektních hran.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="maito:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/30/20:57
 */
public class IncorrectEdgesException extends Exception {
    
	/** Seznam nekorektních hran */
    private List<IncorrectEdgeException> incorrectEdges;
    
	/**
     * Konstruktor, který inicializuje výjimku. 
     * @param incorrectEdges seznam výjimek s nekorektními hranami.
     */
    public IncorrectEdgesException(List<IncorrectEdgeException> incorrectEdges) {
        super();
        this.incorrectEdges = incorrectEdges;
    }
    
    /**
     * Poskytne seznam nekorektních výjimek s nekorektními hranami.
     * @return seznam výjimek s nekorektními hranami.
     */
    public List<IncorrectEdgeException> getIncorrectEdges() {
        return incorrectEdges;
    }
    
    @Override
    public String getMessage() {
        
        StringBuilder sb = new StringBuilder();
        for (IncorrectEdgeException iee : incorrectEdges) {
            sb.append(String.format("%s --> %s\n", iee.getSource(), iee.getTarget()));
        }
        return sb.toString();
    }
}
