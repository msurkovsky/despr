
package cz.vsb.cs.sur096.despr.utils;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.DesprClassLoader;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.types.Copier;
import cz.vsb.cs.sur096.despr.types.Wrapper;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import cz.vsb.cs.sur096.despr.window.pluginmanager.ExtensionsForType;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Struktura slouží pro uchování informací o definovaná rozšíření jednotlivých 
 * typů. Zároveň ji je možné použít jako model pro {@code JList}.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/19/18:25
 */
public class ExtensionsOfTypes 
        extends AbstractListModel
        implements Iterable<ExtensionsForType>, ListModel {

    /**
     * Odkaz na soubor, kde jsou uloženy informace o propojení jednotlivých
	 * rozšíření s danými typy.
     */
    public static final String EXTENSIONS_FILE = "resources/extensions_of_types.xml";
    
    /**
     * Metoda poskytne odkaz na kolekci.
     * @return kolekce typů a jejich rozšíření.
     */
    public static ExtensionsOfTypes getData() {
        return collection;
    }
    
    /**
     * Poskytne Wrapper pro daný typ.
     * @param type typ pro který má být definován wrapper.
     * @return pokud je pro daný typ opravdu wrapper definován pak je vrácen
	 * odkaz na třídu která jej reprezentuje, jinak vrací {@code null}
     */
    public static Class<? extends Wrapper> getConnectedWrapper(Class type) {
        return collection.pGetConnectedWrapper(type);
    }
    
    /**
     * Poskytne Copier objekt, který je schopen vytvořit kopii daného typu.
     * @param type typ pro který by měl daný objekt existovat.
     * @return odkaz na třídu reprezentující objekt Copier pro daný typ, pokud
	 * existuje, jinak {@code null}.
     */
    public static Class<? extends Copier> getConnectedCopier(Class type) {
        return collection.pGetConnectedCopier(type);
    }
    
    /**
     * Poskytne Renderer hodnoty pro objekt {@code JTable}.
     * @param type typ pro který by měl daný renderer existovat.
     * @return renderer hodnoty pro tabulku, pokud existuje, jinak {@code null}.
     */
    public static Class<? extends ParameterCellRenderer> getConnectedParameterCellRenderer(Class type) {
        return collection.pGetConnectedParameterCellRenderer(type);
    }
    
    /**
     * Poskytne Editor hodnoty pro objekt {@code JTable}.
     * @param type typ pro který by měl daný editor exitovat.
     * @return editor hodnoty pro tabulku, pokud existuje, jinak {@code null}.
     */
    public static Class<? extends ParameterCellEditor> getConnectedParameterCellEditor(Class type) {
        return collection.pGetConnectedParameterCellEditor(type);
    }
    
    /**
     * Uloží nastavenou konfiguraci na disk.
     */
    public static void save() {
        collection.pSave();
    }
    
    /** Seznam lokalizačních zpráv.*/
    private static LocalizeMessages messages;
    /** Odkaz na danou strukturu.*/
    private static ExtensionsOfTypes collection;
    static {
        collection = new ExtensionsOfTypes();
    }
    
    /** Seznam dvojic (typ - rozšíření).*/
    private Map<Class, ExtensionsForType> extensions;

    /**
     * Konstruktor inicializuje strukturu a načte data z disku.
	 * Avšak neumožní vytvořit novou instanci, celá aplikace tak
	 * přistupuje k jedné struktuře. Seznam je vláknově bezpečný.
     */
    private ExtensionsOfTypes() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        extensions = Collections.synchronizedMap(
                new LinkedHashMap<Class, ExtensionsForType>());
        pLoad();
    }
    
    /**
     * Poskytne iterátor přes všechna rozšíření.
     * @return iterátor přes všechna rozšíření.
     */
    @Override
    public Iterator<ExtensionsForType> iterator() {
        Collection<ExtensionsForType> values = extensions.values();
        return values.iterator();
    }

    /**
     * Poskytne velikost celé kolekce.
     * @return velikost kolekce.
     */
    @Override
    public int getSize() {
        return extensions.size();
    }

    /**
     * Poskytne hodnotu na dané pozici.
     * @param index pozice.
     * @return hodnota na dané pozici v tabulce.
     */
    @Override
    public Object getElementAt(int index) {
        return extensions.values().toArray()[index];
    }
    
    /**
     * Přidá nový typ do kolekce.
     * @param type typ pro který mají být definována rozšíření.
     * @throws RuntimeException pokud je již daný typ v kolekci obsažen.
     */
    public void addNewType(Class type) throws RuntimeException {
        
        if (extensions.containsKey(type)) {
            throw new RuntimeException(
                    String.format(messages.getString("exception.type_is_already_contained", 
                                                     "Type '%s' is already contained in the extensions collection!"), 
                                  type.getCanonicalName()));
        } else {
            int extSize = extensions.size();
            ExtensionsForType extType = new ExtensionsForType(type);
            extensions.put(type, extType);
            fireIntervalAdded(extType, 0, extSize);
        }
    }
    
    /**
     * Smaže daný typ z kolekce.
     * @param type typ který má být smazán.
     */
    public void removeType(Class type) {
        if (extensions.containsKey(type)) {
            int size = extensions.size();
            ExtensionsForType extType = extensions.get(type);
            extensions.remove(type);
            fireContentsChanged(extType, 0, size);
        }
    }
    
    /**
     * Připojí wrapper k danému typu.
     * @param type typ ke kterému má být wrapper připojen.
     * @param wrapper wrapper který má být připojen
     * @throws IllegalArgumentException  pokud kolekce daný 
	 * typ neobsahuje.
     */
    public void connectWrapper(Class type, Class<? extends Wrapper> wrapper) 
            throws IllegalArgumentException {
        
        if (extensions.containsKey(type)) {
            ExtensionsForType extType = extensions.get(type);
            extType.setWrapper(wrapper);
            int index = getIndex(extensions.values(), extType);
            fireContentsChanged(extType, index, index);
        } else {
            throw new IllegalArgumentException(String.format(
                    messages.getString("exception.type_is_not_contained", 
                                       "Type '%s' is not contained in the extensions collection!"),
                    type.getCanonicalName()));
        }
    }

    /**
     * Připojí copier k danému typu.
     * @param type typ ke kterému má být napojen.
     * @param copier copier který má být napojen.
     * @throws IllegalArgumentException  pokud v kolekci daný typ
	 * není obsažen.
     */
    public void connectCopier(Class type, Class<? extends Copier> copier) 
            throws IllegalArgumentException {
        if (extensions.containsKey(type)) {
            ExtensionsForType extType = extensions.get(type);
            extType.setCopier(copier);
            int index = getIndex(extensions.values(), extType);
            fireContentsChanged(extType, index, index);
        } else {
            throw new IllegalArgumentException(String.format(
                    messages.getString("exception.type_is_not_contained", 
                                       "Type '%s' is not contained in the extensions collection!"),
                    type.getCanonicalName()));
        }
    }
    
    /**
     * Připojí renderer k danému typu.
     * @param type typ ke kterému má být připojen.
     * @param renderer renderer, který má být připojen.
     * @throws IllegalArgumentException pokud v kolekci daný typ 
	 * není obsažen.
     */
    public void connectParameterCellRenderer(Class type, 
            Class<? extends ParameterCellRenderer> renderer) 
            throws IllegalArgumentException {
        
        if (extensions.containsKey(type)) {
            ExtensionsForType extType = extensions.get(type);
            extType.setRenderer(renderer);
            int index = getIndex(extensions.values(), extType);
            fireContentsChanged(extType, index, index);
        } else {
            throw new IllegalArgumentException(String.format(
                    messages.getString("exception.type_is_not_contained", 
                                       "Type '%s' is not contained in the extensions collection!"),
                    type.getCanonicalName()));
        }
    }
    
    /**
     * Připojí editor k danému typu.
     * @param type typ ke kterému má být připojen.
     * @param editor editor který má být připojen.
     * @throws IllegalArgumentException pokud daný typ v kolekci
	 * není obsažen.
     */
    public void connectParameterCellEditor(Class type, 
            Class<? extends ParameterCellEditor> editor) 
            throws IllegalArgumentException {
        
        if (extensions.containsKey(type)) {
            ExtensionsForType extType = extensions.get(type);
            extType.setEditor(editor);
            int index = getIndex(extensions.values(), extType);
            fireContentsChanged(extType, index, index);
        } else {
            throw new IllegalArgumentException(String.format(
                    messages.getString("exception.type_is_not_contained", 
                                       "Type '%s' is not contained in the extensions collection!"),
                    type.getCanonicalName()));
        }
    }
    
    /**
     * Poskytne seznam všech napoených typů.
     * @return seznam všech napojených typuů.
     */
    public List<Class> getTypes() {
        return new ArrayList<Class>(extensions.keySet());
    }

	////////////////////////////////////////////////////////////
	// Soukromé pomocné metody
	//
    private Class<? extends Wrapper> pGetConnectedWrapper(Class type) {
        ExtensionsForType extType = extensions.get(type);
        if (extType != null) {
            return extensions.get(type).getWrapper();
        } else {
            return null;
        }
    }
    
    private Class<? extends Copier> pGetConnectedCopier(Class type) {
        ExtensionsForType extType = extensions.get(type);
        if (extType != null) {
            return extensions.get(type).getCopier();
        } else {
            return null;
        }
    }
    
    private Class<? extends ParameterCellRenderer> pGetConnectedParameterCellRenderer(
            Class type) {
        ExtensionsForType extType = extensions.get(type);
        if (extType != null) {
            return extensions.get(type).getRenderer();
        } else {
            return null;
        }
    }
    
    private Class<? extends ParameterCellEditor> pGetConnectedParameterCellEditor(
            Class type) {
        ExtensionsForType extType = extensions.get(type);
        if (extType != null) {
            return extensions.get(type).getEditor();
        } else {
            return null;
        }
    }
    
    private void pSave() {
        File f = new File(EXTENSIONS_FILE);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Despr.showError(
                        messages.getString("title.exception_saving",
                        "Saving extensions of types problem."), ex, Level.SEVERE, true);
            }
        }
        
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element rootElem = doc.createElement("extensions_of_types");
            
            Collection<ExtensionsForType> values = extensions.values();
            for (ExtensionsForType extType : values) {
                rootElem.appendChild(extensionsForTypeToXML(extType, doc));
            }
            doc.appendChild(rootElem);
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource domSource = new DOMSource(doc);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(new BufferedOutputStream(baos));

            transformer.transform(domSource, result);

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(baos.toByteArray());
            fos.close();
            
        } catch (ParserConfigurationException ex) {
            
            Despr.showError(
                        messages.getString("title.exception_saving",
                        "Saving extensions of types problem."), ex, Level.SEVERE, true);
        } catch (TransformerConfigurationException ex) {
            Despr.showError(
                        messages.getString("title.exception_saving",
                        "Saving extensions of types problem."), ex, Level.SEVERE, true);
        } catch (TransformerException ex) {
            Despr.showError(
                        messages.getString("title.exception_saving",
                        "Saving extensions of types problem."), ex, Level.SEVERE, true);
        } catch (FileNotFoundException ex) {
            Despr.showError(
                        messages.getString("title.exception_saving",
                        "Saving extensions of types problem."), ex, Level.SEVERE, true);
        } catch (IOException ex) {
            Despr.showError(
                        messages.getString("title.exception_saving",
                        "Saving extensions of types problem."), ex, Level.SEVERE, true);
        }

    }
    
    private void pLoad() {
        File f = new File(EXTENSIONS_FILE);
        if (!f.exists()) {
            // File not exist thus does not any extensions for loading.
            return;
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(f);
            
            Node extensionsOfTypesNode = doc.getChildNodes().item(0);
            if (!extensionsOfTypesNode.getNodeName().equals("extensions_of_types")) {
                throw new RuntimeException(String.format(messages.getString(
                        "exception.unexcepted_structure","Unexpected structure of file '%s'!"), 
                        f.getAbsolutePath()));
            }
            
            NodeList extensionsNodes = extensionsOfTypesNode.getChildNodes();
            for (int i = 0; i < extensionsNodes.getLength(); i++) {
                Node extensionNode = extensionsNodes.item(i);
                ExtensionsForType extType = extensionsForTypeFromXML(extensionNode);
                if (extType != null) {
                    extensions.put(extType.getType(), extType);
                }
            }
            
        } catch (ParserConfigurationException ex) {
            Despr.showError(messages.getString("title.exception_loading", 
                    "Loading extensions problem."), ex, Level.SEVERE, true);
        } catch (SAXException ex) {
            Despr.showError(messages.getString("title.exception_loading", 
                    "Loading extensions problem."), ex, Level.SEVERE, true);
        } catch (IOException ex) {
            Despr.showError(messages.getString("title.exception_loading", 
                    "Loading extensions problem."), ex, Level.SEVERE, true);
        }
    }
    
    private Element extensionsForTypeToXML(ExtensionsForType extType, Document doc) {
        Element extTypeElem = doc.createElement("extensions_for_type");
        extTypeElem.setAttribute("type", extType.getType().getCanonicalName());
        
        Class wrapper = extType.getWrapper();
        if (wrapper != null) {
            Element wrapperElem = doc.createElement("wrapper");
            wrapperElem.appendChild(doc.createTextNode(wrapper.getCanonicalName()));
            extTypeElem.appendChild(wrapperElem);
        }
        
        Class copier = extType.getCopier();
        if (copier != null) {
            Element copierElem = doc.createElement("copier");
            copierElem.appendChild(doc.createTextNode(copier.getCanonicalName()));
            extTypeElem.appendChild(copierElem);
        }
        
        Class renderer = extType.getRenderer();
        if (renderer != null) {
            Element rendererElem = doc.createElement("renderer");
            rendererElem.appendChild(doc.createTextNode(renderer.getCanonicalName()));
            extTypeElem.appendChild(rendererElem);
        }
        
        Class editor = extType.getEditor();
        if (editor != null) {
            Element editorElem = doc.createElement("editor");
            editorElem.appendChild(doc.createTextNode(editor.getCanonicalName()));
            extTypeElem.appendChild(editorElem);
        }
        
        return extTypeElem;
    }
    
    private ExtensionsForType extensionsForTypeFromXML(Node node) {
        if (node.getNodeName().equals("extensions_for_type")) {
            String typeName = node.getAttributes().getNamedItem("type").getNodeValue();

            ExtensionsForType extType = null;
            try {
                Class type = Class.forName(typeName, true, DesprClassLoader.getClassLoader());
                extType = new ExtensionsForType(type);

                NodeList nl = node.getChildNodes();
                for (int i = 0; i < nl.getLength(); i++) {
                    Node nExtension = nl.item(i);
                    String extensionType = nExtension.getNodeName();
                    String extensionName = nExtension.getTextContent().trim();
                    if (extensionName.equals("")) {
                        continue;
                    }
                    Class cls = Class.forName(extensionName, true, 
                            DesprClassLoader.getClassLoader());

                    if (extensionType.equals("wrapper")) {
                        extType.setWrapper(cls);
                    } else if (extensionType.equals("copier")) {
                        extType.setCopier(cls);
                    } else if (extensionType.equals("renderer")) {
                        extType.setRenderer(cls);
                    } else if (extensionType.equals("editor")) {
                        extType.setEditor(cls);
                    }
                }
                
            } catch (ClassNotFoundException ex) {
                Despr.showError(messages.getString("title.exception_loading", 
                    "Loading extensions problem."), ex, Level.SEVERE, true);
            }
            
            return extType;
        } else {
            return null;
        }
    }
    
    private int getIndex(Collection collect, Object data) {
        int i = 0;
        for (Object o : collect) {
            if (o.equals(data)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}