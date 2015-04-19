package cz.vsb.cs.sur096.despr.view.operationstree;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.DesprClassLoader;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Nástroj starající se o načtení stromu operací s uloženého souboru na disku. 
 * Strom je načítán ze souboru: {@code $PROJEC_DIR/resources/Operations.xml}.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/20/16:21
 */
public class OperationsReader {

    /** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;
    /** Soubor s uloženým stromem operací. */
    private File operationsTree;
    
    /** Odkaz na loader ze souboru.*/
    private static OperationsReader or;
    static {
        or = new OperationsReader();
    }
    
    /**
     * Poskytne strom kategorii s operacemi.
     */
    public static Category operationsTree() {
        return or.getOperationsTreet();
    }
    
    /**
     * Iniciuje loader.
     */
    private OperationsReader() {
        operationsTree = new File("resources/Operations.xml");
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
    /**
     * Načte operace ze souboru.
     * @return strom kategorií a operací načtených ze souboru.
     */
    private Category getOperationsTreet() {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            doc = builder.parse(operationsTree);
            
        } catch (ParserConfigurationException ex) {
            String title = messages.getString("title.parser_config_excp",
                    "Problem with parser configuration");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (SAXException ex) {
            String title = messages.getString("title.sax_excp", "SAX Exception");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException e) {
            String title = messages.getString("title.io_excp", "I/O problem");
            Despr.showError(title, e, Level.WARNING, true);
        }
        
        Category operations = getCat(null, doc.getChildNodes());
        
        return operations;
    }

    /**
     * Načte dceřinou kategorii z XML dokumentu.
     * @param parent rodičovská kategorie.
     * @param nl seznam potomků XML struktury.
     * @return načtená kategorie.
     */
    private Category getCat(Category parent, NodeList nl) {
        Category category = null;
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node.getNodeName().equals("category")) {
                String name = node.getAttributes().getNamedItem("name").getNodeValue();
                category = new Category();
                category.setUserObject(name);
                if (parent != null) {
                    parent.insert(category, parent.getChildCount());
                }
                getCat(category, node.getChildNodes());
            } else if (node.getNodeName().equals("operation")) {
                try {
                    IOperation op = (IOperation) Class.forName(
                            node.getTextContent().trim(), true, 
                            DesprClassLoader.getClassLoader()).newInstance();
                    
                    Category cat = new Category();
                    cat.setUserObject(op);
                    parent.insert(cat, parent.getChildCount());
                } catch (ClassNotFoundException ex) {
                    String title = messages.getString("title.class_not_found_excp",
                            "Class Not Found");
                    Despr.showError(title, ex, Level.WARNING, true);
                } catch (InstantiationException ex) {
                        String title = messages.getString("title.instantiation_excp",
                                "Problem with instantiation class");
                    Despr.showError(title, ex, Level.WARNING, true);
                } catch (IllegalAccessException ex) {
                    String title = messages.getString("title.illegal_access_excp",
                            "Illegal Access");
                    Despr.showError(title, ex, Level.WARNING, true);
                }
            }
        }
        return (category != null) ? category : parent;
    }
}
