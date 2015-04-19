package cz.vsb.cs.sur096.despr.utils.persistenceGraph;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.DesprClassLoader;
import cz.vsb.cs.sur096.despr.collections.IParameters;
import cz.vsb.cs.sur096.despr.exceptions.IncompatibleDataTypeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgeException;
import cz.vsb.cs.sur096.despr.exceptions.ParameterUsedException;
import cz.vsb.cs.sur096.despr.model.*;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.model.operation.IRootOperation;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.structures.IdPoint;
import cz.vsb.cs.sur096.despr.structures.IdRectangle;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.structures.Pair;
import cz.vsb.cs.sur096.despr.types.Wrapper;
import cz.vsb.cs.sur096.despr.view.Edge;
import cz.vsb.cs.sur096.despr.view.Operation;
import java.awt.Point;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

/**
 * Stará se o načtení grafu uloženém na disku. Uložený graf se 
 * skládá ze dvou částí. Je zvlášť uložen model (nastavení
 * jednotlivých operace) a pohled (informace o tom kde se 
 * která komponenta na plátně nachází). Oba soubory jsou uloženy
 * v rámci jednoho zip souboru s příponou 'despr.zip'.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/26/10:09
 */
public final class GraphLoader {
    
	/** Seznam lokalizovaných zpráv. */
    private transient LocalizeMessages messages;
	/** Odkaz na soubor s uloženými daty */
    private ZipFile zipFile;
    
	/** Iniciuje loader grafu. */
    private GraphLoader() { 
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }

	/** Odkaz na loader grafu. */
    private static GraphLoader graphLoader;
    static {
        graphLoader = new GraphLoader();
    }
    
	/**
     * Načte model grafu.
     * @param f soubor ve kterém je model uložen.
     * @return pokud vše proběhne v pořádku poskytne model
	 * grafu.
     */
    public static IGraph loadGraphModel(File f) {
        return graphLoader.loadModel(f);
    }
    
    /**
     * Načte pohled grafu. Nevrací ovšem celý pohled, odkaz na {@code GraphCanvas},
	 * ale seznam hran a operací. V pohledu grafu se pak model s těmito infomacemi
	 * zkompletují do jednoho celku.
     * @param f soubor ve kterém jsou informace o pohledu uloženy.
     * @param model model grafu.
     * @return dvojici (seznam operací, seznam hran).
     */
    public static Pair<List<Operation>, List<Edge>> loadGraphView(File f, IGraph model) {
        return graphLoader.loadView(f, model);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Loading the model of a graph
    private IGraph loadModel(File f) {

        
        ZipEntry graphModelFile = getZipEntry(f, GraphPersistence.MODEL_FILE);
        InputStream is = null;
        try {
            is = zipFile.getInputStream(graphModelFile);
        } catch (IOException ex) {
            String title = messages.getString("title.loading_zip_problem", 
                                              "Problem with load zip file!");
            Despr.showError(title, ex, Level.WARNING, true);
        }
        
        IGraph graph = new DefaultGraph();
            
        // TreeWolker for element graph (GraphPeristance.ELEM_GRAPH)
        TreeWalker tw = initializeTreeWolker(is, GraphPersistence.MODEL_FILE);

        Node operationsNode = tw.firstChild();
        mLoadOperations(operationsNode, tw, graph);

        tw.setCurrentNode(operationsNode);

        Node edgesNode = tw.nextSibling();
        mLoadEdges(edgesNode, tw, graph);

        return graph;
    }
    
    private void mLoadOperations(Node operationsNode, TreeWalker tw, IGraph graph) {
        
        FieldFilter operationFilter = GraphPersistence.operationFieldFilter;

        tw.setCurrentNode(operationsNode);
        
        List<Node> operations = loadChilderenNodes(operationsNode, tw);
        for (Node operation : operations) {
            int operationId = Integer.parseInt(getAttributeValue(operation.getAttributes(), GraphPersistence.ATTR_ID));
            boolean isRoot = Boolean.parseBoolean(getAttributeValue(operation.getAttributes(), GraphPersistence.GM_ATTR_ROOT));
            
            if (isRoot) {
                IRootOperation rootOp = (IRootOperation) getObjectValue(operation, tw, operationFilter);
                IRootOperationModel rootOpModel = new RootOperationModel(rootOp, operationId);
                setTypeOfInnerPatarameters(rootOpModel, operation, tw);
                graph.addOperation(rootOpModel);
            } else {
                IOperation op = (IOperation) getObjectValue(operation, tw, operationFilter);
                IOperationModel opModel = new OperationModel(op, operationId);
                setTypeOfInnerPatarameters(opModel, operation, tw);
                graph.addOperation(opModel);
            }
            
        }
    }
    
    private void setTypeOfInnerPatarameters(IOperationModel operationModel, Node operation, TreeWalker tw) {
        
        List<String> parametersName = new ArrayList<String>();
        
        List<Node> parameters = loadChilderenNodes(operation, tw);
        for (Node parameter : parameters) {
            String name = getAttributeValue(parameter.getAttributes(), GraphPersistence.GM_ATTR_NAME);
            parametersName.add(name);
        }
        
        IParameters<IInputParameter> inputParamters = operationModel.getInputParameters();
        for (IInputParameter inputParam : inputParamters) {
            if (parametersName.contains(inputParam.getName())) {
                inputParam.setType(EInputParameterType.INNER);
            } else {
                inputParam.setType(EInputParameterType.OUTER);
            }
        }
    }
    
    private void mLoadEdges(Node edgesNode, TreeWalker tw, IGraph graph) {
        tw.setCurrentNode(edgesNode);
        
        Node edge = tw.firstChild();
        
        if (edge == null) {
            return;
        }
        
        tw.setCurrentNode(edge);
        
        do {
            int edgeId = Integer.parseInt(
                    getAttributeValue(edge.getAttributes(), 
                                      GraphPersistence.ATTR_ID));
            
            Node source = tw.firstChild();
            int sourceOperationId = Integer.parseInt(
                    getAttributeValue(source.getAttributes(), 
                                      GraphPersistence.GM_ATTR_OPERATION_ID));
            String nameSourceParameter = source.getTextContent();
            
            Node target = tw.nextSibling();
            int targetOperationId = Integer.parseInt(
                    getAttributeValue(target.getAttributes(), 
                                      GraphPersistence.GM_ATTR_OPERATION_ID));
            String nameTargetParameter = target.getTextContent();
            
            
            IEdge edgeModel = new DefaultEdge(
                    graph.findOperation(sourceOperationId).getOutputParameters().get(nameSourceParameter),
                    graph.findOperation(targetOperationId).getInputParameters().get(nameTargetParameter),
                    edgeId);
            
            try {
                graph.addEdge(edgeModel);
            } catch (IncorrectEdgeException ex) {
                String title = messages.getString("title.incorrect_edge", 
                                                  "Incorrect edge!");
                Despr.showError(title, ex, Level.WARNING, true);
            } catch (ParameterUsedException ex) {
                String title = messages.getString("title.parameter_used",
                                                  "Parameter is used!");
                Despr.showError(title, ex, Level.WARNING, true);
            } catch (IncompatibleDataTypeException ex) {
                String title = messages.getString("title.incompatible_data_types",
                                                  "Incompatible data types!");
                Despr.showError(title, ex, Level.WARNING, true);
            }
            
            tw.setCurrentNode(edge);
        } while ((edge = tw.nextSibling()) != null);
    }
    
    private Object getObjectValue(Node objectNode, TreeWalker tw, FieldFilter filter) {
        
        // objectNode can be a primitive type
        // thus i don't have to recognize prmitive type in collection, dictionary, etc.
        if (objectNode.getNodeName().equals(GraphPersistence.GM_ELEM_PRIMITIVE)) {
            return getPrimitiveValue(objectNode);
        } else if (objectNode.getNodeName().equals(GraphPersistence.GM_ELEM_ENUM)) {
            // if objectNode is enum, must be recognize here because i can't create new
            // instance of an enum type.
            return getEnumValue(objectNode);
        }
        
        String type = getAttributeValue(objectNode.getAttributes(), GraphPersistence.GM_ATTR_TYPE);
        
        Class typeClass = createClassByName(type);
        if (typeClass == null) {
            return null;
        }
        
        Object objectInstance;

        // I try if exist a wrapper for the typeClass
        Wrapper wrapper = WrappedObjects.getWrapper(typeClass);
        if (wrapper != null) {
            // ok? objectInstance is new Wrapper
            objectInstance = wrapper;
        } else {
            // no ok? I use standard object
            objectInstance = createNewInstance(typeClass);
        }
        
        if (objectInstance == null) {
            return null;
        }
        
        ObjectProperties objectProperties;
        if (filter != null) {
            objectProperties = new ObjectProperties(objectInstance.getClass(), filter);
        } else {
            objectProperties = new ObjectProperties(objectInstance.getClass());
        }

        List<Node> parameters = loadChilderenNodes(objectNode, tw);
        for (Node parameter : parameters) {
            String name = getAttributeValue(parameter.getAttributes(), GraphPersistence.GM_ATTR_NAME);
                String nodeName = parameter.getNodeName();

                if (nodeName.equals(GraphPersistence.GM_ELEM_OBJECT)) {
                    // for objects
                    objectProperties.set(name, objectInstance, getObjectValue(parameter, tw, null));
                } else if (nodeName.equals(GraphPersistence.GM_ELEM_PRIMITIVE)) {
                    // for primitives
                    objectProperties.set(name, objectInstance, getPrimitiveValue(parameter));
                } else if (nodeName.equals(GraphPersistence.GM_ELEM_COLLECTION)) {
                    // for collections
                    objectProperties.set(name, objectInstance, getCollectionValue(parameter, tw));
                } else if (nodeName.equals(GraphPersistence.GM_ELEM_DICTIONARY)) {
                    // for dictionaries
                    objectProperties.set(name, objectInstance, getDictionaryValue(parameter, tw));
                } else if (nodeName.equals(GraphPersistence.GM_ELEM_ARRAY)) {
                    // for array
                    objectProperties.set(name, objectInstance, getArrayValue(parameter, tw));
                } else if (nodeName.equals(GraphPersistence.GM_ELEM_ENUM)) {
                    // for enum
                    objectProperties.set(name, objectInstance, getEnumValue(parameter));
                } else if (nodeName.equals(GraphPersistence.GM_ELEM_NULL_OBJECT)) {
                    // for null object  
                    objectProperties.set(name, objectInstance, null);
                } else {
                    String message = String.format(
                            messages.getString("exception.unexpected_element_name",
                            "Unexpected name element: '%s'!\nAre expection either '%s' or '%s'."), 
                            nodeName, 
                            GraphPersistence.GM_ELEM_OBJECT, 
                            GraphPersistence.GM_ELEM_PRIMITIVE);
                    throw new IllegalArgumentException(message);
                }
        }
        
        // if wrapper is not null i unwrap origninal object
        if (wrapper != null) {
            return wrapper.unwrap();
        } else {
            return objectInstance;
        }
    }
    
    private Object getPrimitiveValue(Node primitiveNode) {
        // primitive element has only one child
        Node entry = primitiveNode.getFirstChild();
        String type = entry.getNodeName();
        String value = entry.getTextContent();
        
        return GraphPersistence.getPrimitiveValue(type, value);
    }
    
    private Object getCollectionValue(Node collectionNode, TreeWalker tw) {
        
        String typeName = getAttributeValue(collectionNode.getAttributes(), GraphPersistence.GM_ATTR_TYPE);
        Class type = createClassByName(typeName);
        Collection collect = (Collection) createNewInstance(type);
        
        boolean isIndexed = false;
        if (List.class.isAssignableFrom(type)) {
            isIndexed = true;
        }
        
        List<Node> items = loadChilderenNodes(collectionNode, tw);
        for (Node item : items) {
            
            Node itemValue = item.getFirstChild();
            if (isIndexed) {
                int idx = Integer.parseInt(
                    getAttributeValue(item.getAttributes(), 
                                      GraphPersistence.GM_ATTR_INDEX));
                ((List) collect).add(idx, getObjectValue(itemValue, tw, null));
            } else {
                collect.add(getObjectValue(itemValue, tw, null));
            }
        }
        
        return collect;
    }
    
    private Object getDictionaryValue(Node dictionaryNode, TreeWalker tw) {
        
        String typeName = getAttributeValue(dictionaryNode.getAttributes(), 
                                            GraphPersistence.GM_ATTR_TYPE);
        Class type = createClassByName(typeName);
        Map dict = (Map) createNewInstance(type);
        
        List<Node> keysValues = loadChilderenNodes(dictionaryNode, tw);
        int size = keysValues.size();
        for (int i = 0; i < size; i += 2) {
            Node keyNode = keysValues.get(i);
            Node valueNode = keysValues.get(i+1);
            
            
            Object key = getObjectValue(keyNode.getFirstChild(), tw, null);
            Object value = getObjectValue(valueNode.getFirstChild(), tw, null);
            
            dict.put(key, value);
        }
        
        return dict;
    }
    
    private Object getArrayValue(Node arrayNode, TreeWalker tw) {
        
        NamedNodeMap attributes = arrayNode.getAttributes();
        String typeName = getAttributeValue(attributes, 
                                            GraphPersistence.GM_ATTR_TYPE);
        
        int size = Integer.parseInt(getAttributeValue(attributes, 
                                                      GraphPersistence.GM_ATTR_SIZE));
        
        Class type = createClassByName(typeName);
        Object array = Array.newInstance(type, size);
        
        List<Node> items = loadChilderenNodes(arrayNode, tw);
        for (Node item : items) {
            int idx = Integer.parseInt(getAttributeValue(item.getAttributes(), 
                                        GraphPersistence.GM_ATTR_INDEX));
            
            Object itemValue = getObjectValue(item.getFirstChild(), tw, null);
            Array.set(array, idx, itemValue);
        }
        
        return array;
    }
    
    private Enum getEnumValue(Node enumNode) {
        
        String typeName = getAttributeValue(enumNode.getAttributes(), 
                                            GraphPersistence.GM_ATTR_TYPE);
        
        Class type = createClassByName(typeName);
        String value = enumNode.getTextContent();
        return Enum.valueOf(type, value);
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Loading the view of a graph
    
    private Pair<List<Operation>, List<Edge>> loadView(File f, IGraph model) {
        
        ZipEntry graphViewFile = getZipEntry(f, GraphPersistence.VIEW_FILE);
        InputStream is = null;
        try {
            is = zipFile.getInputStream(graphViewFile);
        } catch (IOException ex) {
            String title = messages.getString("title.loading_zip_problem", 
                                              "Problem with load zip file!");
            Despr.showError(title, ex, Level.WARNING, true);
        }
        
        TreeWalker tw = initializeTreeWolker(is, GraphPersistence.VIEW_FILE);
        
        try {
            // after loaded information is zip file closed.
            zipFile.close();
            zipFile = null;
        } catch (IOException ex) {
            String title = messages.getString("title.closing_zip_problem", 
                    "Problem with closing zip file.");
            Despr.showError(title, ex, Level.WARNING, true);   
        }
        
        Node operationsNode = tw.firstChild();
        List<Operation> operations = vLoadOperations(operationsNode, tw, model);
        
        tw.setCurrentNode(operationsNode);
        
        Node edgesNode = tw.nextSibling();
        List<Edge> edges = vLoadEdges(edgesNode, tw, model);
        
        return new Pair(operations, edges);
    }
    
    private List<Operation> vLoadOperations(Node operationsNode, TreeWalker tw, IGraph model) {
        
        List<Node> operations = loadChilderenNodes(operationsNode, tw);
        List<Operation> operationsList = new ArrayList<Operation>(operations.size());
        for (Node operation : operations) {
            int id = Integer.parseInt(getAttributeValue(operation.getAttributes(), GraphPersistence.ATTR_ID));
            
            Node position = operation.getFirstChild();
            Point opPosition = loadXY(position, tw);
            
            IOperationModel opModel = model.findOperation(id);
            Operation opView = new Operation(opModel);
            opView.setLocation(opPosition);
            opView.setSize(opView.getPreferredSize());
//            gCanvas.addComponentToCanvas(opView);
            operationsList.add(opView);
        }
        
        return operationsList;
    }
    
    private List<Edge> vLoadEdges(Node edgesNode, TreeWalker tw, IGraph model) {
     
        List<Node> edges = loadChilderenNodes(edgesNode, tw);
        List<Edge> edgesList = new ArrayList<Edge>(edges.size());
        for (Node edge : edges) {
            List<IdPoint> edgePoints = new ArrayList<IdPoint>();
            IdRectangle inputPortBounds = null;
            // load points of the edge
            List<Node> positions = loadChilderenNodes(edge, tw);
            
            for (Node position : positions) {
                if (position.getNodeName().equals(GraphPersistence.GV_ELEM_POINT)) {
                    int pId = Integer.parseInt(getAttributeValue(position.getAttributes(), GraphPersistence.ATTR_ID));
                    Point p = loadXY(position, tw);
                    edgePoints.add(new IdPoint(p, pId));
                } else if (position.getNodeName().equals(GraphPersistence.GV_ELEM_INPUT_PORT_BOUNDS)) {
                    inputPortBounds = loadRectangle(position, tw);
                }
            }
            
            // finding edge model
            int edgeId = Integer.parseInt(getAttributeValue(edge.getAttributes(), GraphPersistence.ATTR_ID));
            int color = Integer.parseInt(getAttributeValue(edge.getAttributes(), GraphPersistence.GV_ATTR_EDGE_COLOR));
            IEdge modelEdge = model.findEdge(edgeId);
            
            Edge edgeView = new Edge(modelEdge, edgePoints, inputPortBounds);
            edgeView.setColor(color);
            
            edgesList.add(edgeView);

        }
        return edgesList;
    }
    
    private Point loadXY(Node position, TreeWalker tw) {
                
        int x = -1; int y = -1;
        List<Node> coordinates = loadChilderenNodes(position, tw);
        for (Node coordinate : coordinates) {
            String coordinateName = coordinate.getNodeName();
            if (coordinateName.equals(GraphPersistence.GV_ELEM_X)) {
                x = Integer.parseInt(coordinate.getTextContent());
            } else if (coordinateName.equals(GraphPersistence.GV_ELEM_Y)) {
                y = Integer.parseInt(coordinate.getTextContent());
            }
        }
        
        return new Point(x, y);
    }
    
    private IdRectangle loadRectangle(Node rectangle, TreeWalker tw) {
        int x = -1, y = -1, width = -1, height = -1;
        List<Node> attributes = loadChilderenNodes(rectangle, tw);
        for (Node attribute : attributes) {
            if (attribute.getNodeName().equals(GraphPersistence.GV_ELEM_X)) {
                x = Integer.parseInt(attribute.getTextContent());
            } else if (attribute.getNodeName().equals(GraphPersistence.GV_ELEM_Y)) {
                y = Integer.parseInt(attribute.getTextContent());
            } else if (attribute.getNodeName().equals(GraphPersistence.GV_ELEM_WIDTH)) {
                width = Integer.parseInt(attribute.getTextContent());
            } else if (attribute.getNodeName().equals(GraphPersistence.GV_ELEM_HEIGHT)) {
                height = Integer.parseInt(attribute.getTextContent());
            }
        }
        int id = Integer.parseInt(getAttributeValue(rectangle.getAttributes(), GraphPersistence.ATTR_ID));
        return new IdRectangle(x, y, width, height, id);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Auxiliary functions
    
    private ZipEntry getZipEntry(File f, String name) {
        if (zipFile == null) {
            try {
                zipFile = new ZipFile(f);
            } catch (ZipException ex) {
                 String title = messages.getString("title.loading_zip_problem", 
                                              "Problem with load zip file!");
                Despr.showError(title, ex, Level.WARNING, true);
            } catch (IOException ex) {
                String title = messages.getString("title.loading_zip_problem", 
                                              "Problem with load zip file!");
                Despr.showError(title, ex, Level.WARNING, true);
            }
        }
        
        ZipEntry ze = zipFile.getEntry(name);
        
        if (ze == null) {
            String message = String.format(
                    messages.getString("exception.entry_does_not_exist", 
                                       "Entry with name '%s' does not exist!"),
                    name);
            throw new NullPointerException(message);
        } else {
            return ze;
        }
    }
    private TreeWalker initializeTreeWolker(InputStream is, String type) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            
            DocumentBuilder builder = dbf.newDocumentBuilder();
            builder.setErrorHandler(new GraphErrorHandler());
            Document doc = builder.parse(is);
            
            Reader r = docConvertor(doc);
            
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema sch = null;
            if (type.equals(GraphPersistence.MODEL_FILE)) {
                sch = sf.newSchema(new File("resources/graph_model.xsd"));
            } else if (type.equals(GraphPersistence.VIEW_FILE)) {
                sch = sf.newSchema(new File("resources/graph_view.xsd"));
            }
            
            if (sch != null) {
                Validator val = sch.newValidator();
                val.setErrorHandler(new GraphErrorHandler());
                val.validate(new StreamSource(r));
            }
            
            GraphPersistence.deleteWhiteSpaces(doc);
            
            TreeWalker tw = ((DocumentTraversal) doc).createTreeWalker(
                    // xml has always one tag with name 'operations'
                    doc.getElementsByTagName(GraphPersistence.ELEM_GRAPH).item(0),  
                    NodeFilter.SHOW_ELEMENT + NodeFilter.SHOW_TEXT, 
                    new Filter(), false);
        
            return tw;
        } catch (SAXException ex) {
            String title = messages.getString("title.sax_exception", "SAX Exception");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = messages.getString("title.io_exception", "I/O Exception");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (ParserConfigurationException ex) {
            String title = messages.getString("title.parse_configuration_excp", "Parse Configuration Exception");
            Despr.showError(title, ex, Level.WARNING, true);
        }
        
        return null;
    }
    
    /**
     * Konvertor dokumentu kvůli korektnímu provedení validace.
     * @param source zdrojový dokument.
     * @return transformovaný dokument.
     */
    private Reader docConvertor(Document source) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer writer = tf.newTransformer();
            writer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter sw = new StringWriter();
            StreamResult stres = new StreamResult(sw);
            writer.transform(new DOMSource(source), stres);
            return new StringReader(sw.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    private List<Node> loadChilderenNodes(Node parent, TreeWalker tw) {
        
        List<Node> childerenList = new ArrayList<Node>();
        
        tw.setCurrentNode(parent);
        Node child = tw.firstChild();
        
        if (child != null) {
            tw.setCurrentNode(child);
            do {
                childerenList.add(child);
            } while ((child = tw.nextSibling()) != null);
        }
        
        return childerenList;
    }
    
    private String getAttributeValue(NamedNodeMap attributes, String attributeName) {
        return attributes.getNamedItem(attributeName).getTextContent();
    }
    
    private Class createClassByName(String className) {
        try {
            return Class.forName(className, true, DesprClassLoader.getClassLoader());
        } catch (ClassNotFoundException ex) {
            String title = messages.getString("title.class_not_found_excp", "Class not found");
            Despr.showError(title, ex, Level.WARNING, true);
            return null;
        }
    }
    
    private Object createNewInstance(Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException ex) {
            String title = messages.getString("title.instatiation_excp", "Instantiation Exception");
            Despr.showError(title, ex, Level.WARNING, true);
            return null;
        } catch (IllegalAccessException ex) {
            String title = messages.getString("title.illegal_access_excp", "Illegal access");
            Despr.showError(title, ex, Level.WARNING, true);
            return null;
        }
    }

    private class Filter implements NodeFilter {

        @Override
        public short acceptNode(Node n) {
            if (n.getNodeType() == Node.TEXT_NODE && 
                    n.getNodeValue().trim().length() == 0) {

                return NodeFilter.FILTER_SKIP;
            }
            return NodeFilter.FILTER_ACCEPT;
        }
    }
}