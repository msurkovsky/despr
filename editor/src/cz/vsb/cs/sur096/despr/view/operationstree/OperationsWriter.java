
package cz.vsb.cs.sur096.despr.view.operationstree;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Stará se o uložení stromu operací do souboru na disk.
 * Strom je uložen do souboru: {@code $PROJECT_DIR/resources/Operations.xml}
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/12/14:49
 */
public class OperationsWriter {
    
    /** Seznam lokalizačních zpráv.*/
    private static transient LocalizeMessages messages;
    /** Soubor do kterého má být strom uložen.*/
    private static File operationsTree;
    /** Odkaz na writer.*/
    private static OperationsWriter ow;
	static {
		ow = new OperationsWriter();
	}
    
    /**
     * Iniciuje nástroj pro zápis do souboru.
     */
    private OperationsWriter() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        operationsTree = new File("resources/Operations.xml");
    }
    
    /**
     * Zapíše strom operací do souboru.
     * @param cat kořenová kategorie stromu operací.
     */
    public static void saveOperationsTree(Category cat) {
        // vytvoreni zalohy
        File backupFile = new File("resources/Operations.xml_bck");
        ow.copyFile(operationsTree, backupFile);
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element rootElem = ow.createOperationsTree(cat, doc);
            doc.appendChild(rootElem);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource domSource = new DOMSource(doc);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(new BufferedOutputStream(baos));

            transformer.transform(domSource, result);

            FileOutputStream fos = new FileOutputStream(operationsTree);
            fos.write(baos.toByteArray());
            fos.close();
            backupFile.delete();

        } catch (ParserConfigurationException ex) {
            String title = messages.getString("title.parser_config_excp",
                    "Problem with parser configuration");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (TransformerConfigurationException ex) {
            String title = messages.getString("title.transformer_config_excp",
                    "Problem with transformer configuration");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (TransformerException ex) {
            String title = messages.getString("title.trnasformer_excp", 
                    "Problem with transformer");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (FileNotFoundException ex) {
            String title = messages.getString("title.file_not_found_excp",
                    "File not found");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = messages.getString("title.io_excp", "I/O problem");
            Despr.showError(title, ex, Level.WARNING, true);
        }
    }
    
    /**
     * Vytvoří XML element z dané kategorie.
     * @param category kategorie která má být uložena.
     * @param doc dokument reprezentující XML strukturu stromu.
     * @return XML element reprezentující danou kategorii.
     */
    private Element createOperationsTree(Category category, Document doc) {
        Object userObj = category.getUserObject();
        if (userObj instanceof String) {
            Element categoryElem = doc.createElement("category");
            categoryElem.setAttribute("name", (String) userObj);
            
            int childCount = category.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Category child = (Category) category.getChildAt(i);
                categoryElem.appendChild(createOperationsTree(child, doc));
            }
            return categoryElem;
        } else if (userObj instanceof IOperation) {
            String operationFullName = userObj.getClass().getCanonicalName();
            Element operationElem = doc.createElement("operation");
            operationElem.appendChild(doc.createTextNode(operationFullName));
            return operationElem;
        } else {
            String excpMessage = messages.getString("exception.illegal_user_object_type",
                    "Illegal user object type '%s'");
            throw new IllegalArgumentException(String.format(
                    excpMessage, userObj.getClass().getName()));
        }
    }
    
    /**
     * Vytvoří kopii souboru.
     * @param sourceFile zdrojový soubor.
     * @param destFile nová kopie.
     */
    private void copyFile(File sourceFile, File destFile) {
        try {
            FileChannel source, destination;
            source = new FileInputStream(sourceFile).getChannel();
            if (!destFile.exists()) {
                try {
                    destFile.createNewFile();
                } catch (IOException ex) {
                    String title = messages.getString("title.io_excp", "I/O problem");
                    Despr.showError(title, ex, Level.WARNING, true);
                }
            }
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            
            source.close();
            destination.close();
        } catch (FileNotFoundException ex) {
            String title = messages.getString("title.file_not_found_excp",
                    "File not found");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = messages.getString("title.io_excp", "I/O problem");
            Despr.showError(title, ex, Level.WARNING, true);
        }
        
    }
}
