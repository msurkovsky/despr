
package cz.vsb.cs.sur096.despr.utils.persistenceGraph;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Poskytuje konstanty se jmény pro tvorbu XML souboru a několik pomocných,
 * které jsou společné jak pro načítání tak ukládání grafu.
 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/26/10:17
 */
public final class GraphPersistence {

    
	/**
	 * Filtr položek v operaci. Jsou vybrány pouze vstupní parametry operace.
	 */
    public static final FieldFilter operationFieldFilter;
    static {
        operationFieldFilter = new FieldFilter() {

            @Override
            public boolean accept(Field field) {
                Annotation annotation = field.getAnnotation(AInputParameter.class);
                return annotation != null ? true : false;
            }
        };
    }
    
	/** Zabrání vytvoření instance. */
    private GraphPersistence() { }
    
    // jména xml soborů, které budou uloženy v souboru s příponou *.despr.zip
    public static final String MODEL_FILE = "model.xml";
    public static final String VIEW_FILE  = "view.xml";
    
	// společná jména elementů jak pro model tak pro pohled
    public static final String ELEM_GRAPH      = "graph";
    public static final String ELEM_OPERATIONS = "operations";
    public static final String ELEM_EDGES      = "edges";
    public static final String ELEM_OPERATION  = "operation";
    public static final String ELEM_EDGE       = "edge";
    
    public static final String ATTR_ID = "id";
    
    
    // jména elementu a atributů pro pohled (předpona GM).
    public static final String GM_ELEM_OBJECT      = "object";
    public static final String GM_ELEM_NULL_OBJECT = "null_object";
    
    public static final String GM_ELEM_PRIMITIVE  = "primitive";
    public static final String GM_ELEM_BYTE       = "byte";
    public static final String GM_ELEM_SHORT      = "short";
    public static final String GM_ELEM_INT        = "int";
    public static final String GM_ELEM_LONG       = "long";
    public static final String GM_ELEM_FLOAT      = "float";
    public static final String GM_ELEM_DOUBLE     = "double";
    public static final String GM_ELEM_BOOL       = "bool";
    public static final String GM_ELEM_CHAR       = "char";
    public static final String GM_ELEM_STRING     = "string";
    
    public static final String GM_ELEM_SOURCE     = "source";
    public static final String GM_ELEM_TARGET     = "target";
    
    public static final String GM_ELEM_COLLECTION = "collection";
    public static final String GM_ELEM_DICTIONARY = "dictionary";
    public static final String GM_ELEM_ARRAY      = "array";
    public static final String GM_ELEM_ENUM       = "enum";
    public static final String GM_ELEM_ITEM       = "item";
    public static final String GM_ELEM_KEY        = "key";
    public static final String GM_ELEM_VALUE      = "value";
    
    public static final String GM_ATTR_NAME         = "name";
    public static final String GM_ATTR_TYPE         = "type";
    public static final String GM_ATTR_OPERATION_ID = "operation_id";
    public static final String GM_ATTR_GENERIC_TYPE = "generic_type";
    public static final String GM_ATTR_INDEX        = "index";
    public static final String GM_ATTR_SIZE         = "size";
    public static final String GM_ATTR_ROOT         = "root";
    
    // jména elementů a atributů pro pohled (předpona GV)
    public static final String GV_ELEM_POSITION = "position";
    public static final String GV_ELEM_POINT    = "point";
    public static final String GV_ELEM_X        = "x";
    public static final String GV_ELEM_Y        = "y";
    public static final String GV_ELEM_WIDTH    = "width";
    public static final String GV_ELEM_HEIGHT   = "height";
    
    public static final String GV_ATTR_EDGE_COLOR = "color";
    public static final String GV_ELEM_INPUT_PORT_BOUNDS = "input_port_bounds";
    
    
    
    /**
	 * Převede jméno typu na jeho jeho textovou reprezentaci.
     * @param type odkazy na primitivní typy plus navíc řetězce,
	 * které jsou považovány taky jako primitivní typ.
     * @return jméno pro XML element nebo prázdný řetězec pro
	 * nedefinovaný typ.
     */
    public static String getPrimitiveElementName(Class type) {
        
        if (type.equals(Byte.class)             || type.equals(byte.class)) {
            return GM_ELEM_BYTE;
            
        } else if (type.equals(Short.class)     || type.equals(short.class)) {
            return GM_ELEM_SHORT;
            
        } else if (type.equals(Integer.class)   || type.equals(int.class)) {
            return GM_ELEM_INT;
            
        } else if (type.equals(Long.class)      || type.equals(long.class)) {
            return GM_ELEM_LONG;
            
        } else if (type.equals(Float.class)     || type.equals(float.class)) {
            return GM_ELEM_FLOAT;
            
        } else if (type.equals(Double.class)    || type.equals(double.class)) {
            return GM_ELEM_DOUBLE;
            
        } else if (type.equals(Boolean.class)   || type.equals(boolean.class)) {
            return GM_ELEM_BOOL;
            
        } else if (type.equals(Character.class) || type.equals(char.class)) {
            return GM_ELEM_CHAR;
            
        } else if (type.equals(String.class)) {
            return GM_ELEM_STRING;
            
        } else {
            // undefined type
            return "";
        }
    }
    
    /**
	 * Vytvoří ze jména typu a hodnoty v textové reprezentaci hodnotu
	 * původního typu. Je ji možno použít pro základní typy a řetězce.
     * @param type řetězec popisující typ.
     * @param value hodnota v textové formě.
     * @return objekt reprezentující původní datový typ s nastavenou hodnotou.
	 * @throws IllegalArgumentException pokud není typ definován nebo
	 * selže převod typu {@code char} je předán řetězec délky větší než 1.
     */
    public static Object getPrimitiveValue(String type, String value) {
        
        if (type.equals(GM_ELEM_BYTE)) {
            return Byte.parseByte(type);
            
        } else if (type.equals(GM_ELEM_SHORT)) {
            return Short.parseShort(value);
            
        } else if (type.equals(GM_ELEM_INT)) {
            return Integer.parseInt(value);
            
        } else if (type.equals(GM_ELEM_LONG)) {
            return Long.parseLong(value);
            
        } else if (type.equals(GM_ELEM_FLOAT)) {
            return Float.parseFloat(value);
            
        } else if (type.equals(GM_ELEM_DOUBLE)) {
            return Double.parseDouble(value);
            
        } else if (type.equals(GM_ELEM_BOOL)) {
            return Boolean.parseBoolean(value);
            
        } else if (type.equals(GM_ELEM_CHAR)) {
            if (value.length() == 1) {
                return value.charAt(0);
            } else {
                throw new IllegalArgumentException("Parameter 'value' = " + 
                        value + " is not type of char!");
            }
        } else if (type.equals(GM_ELEM_STRING)) {
            return value;
        } else {
            throw new IllegalArgumentException("Parameter 'type' = " + type + 
                    " is ot defined!");
        }
    }
    
    /**
     * Smaže bílé znaky z dokumentu
     * @param doc XML dokument, ze kterého mají být smazány bíle znaky.
     */
    public static void deleteWhiteSpaces(Document doc) {
        Node n = doc.getDocumentElement();
        
        NodeIterator ni = ((DocumentTraversal) doc).createNodeIterator(n, 
                NodeFilter.SHOW_TEXT, new NodeFilter() {

            @Override
            public short acceptNode(Node n) {
                if (n.getNodeValue().trim().length() == 0) {
                    return NodeFilter.FILTER_ACCEPT;
                } else {
                    return NodeFilter.FILTER_SKIP;
                }
            }
        }, false);
        
        while ((n = ni.nextNode()) != null) {
            Node parent = n.getParentNode();
            parent.removeChild(n);
        }
    }
}
