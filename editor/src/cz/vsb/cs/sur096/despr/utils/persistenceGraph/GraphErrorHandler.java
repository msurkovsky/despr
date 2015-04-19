
package cz.vsb.cs.sur096.despr.utils.persistenceGraph;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Slouží pro informování o chybě (chybách) při načítání XML souboru.
 * 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/26/11:08
 */
public class GraphErrorHandler implements ErrorHandler {
    
	/**
     * Iniciuje error handler.
     */
    public GraphErrorHandler() {
    }

    /**
     * Vypíše varování na standardní výstup.
     * @param exception výjimka která jej vyvolala.
     * @throws SAXException 
     */
    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.err.println("Problem with file (warning):");
        System.err.println("\t" + exception.getMessage());
    }

    /**
     * Vypíše chybu na standardní výstup.
     * @param exception výjimka která ji vyvolala.
     * @throws SAXException 
     */
    @Override
    public void error(SAXParseException exception) throws SAXException {
        System.err.println("Problem with file (error):");
        System.out.println("\tError at " +exception.getLineNumber() + " line.");
        System.out.println("\t" + exception.getMessage());
    }

    /**
     * Vypíše závažnou chybu na standardní výstup.
     * @param exception výjimka která ji vyvolala.
     * @throws SAXException 
     */
    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        System.err.println("Problem with file (fatal error):");
        System.out.println("\tError at " +exception.getLineNumber() + " line.");
        System.out.println("\t" + exception.getMessage());
    }
}
