
package cz.vsb.cs.sur096.despr.utils.persistenceGraph;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.IParameters;
import cz.vsb.cs.sur096.despr.model.*;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.structures.IdPoint;
import cz.vsb.cs.sur096.despr.structures.IdRectangle;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.types.Wrapper;
import cz.vsb.cs.sur096.despr.view.Edge;
import cz.vsb.cs.sur096.despr.view.GraphCanvas;
import cz.vsb.cs.sur096.despr.view.Operation;
import java.awt.Component;
import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Slouží pro uložení grafu na disk ve formě XML souborů.
 * 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/29/18:59
 */
public final class GraphSaver {
    
	/** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;
    private GraphSaver() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
    private static GraphSaver graphSaver;
    static {
        graphSaver = new GraphSaver();
    }
    
	/**
	 * Vytvoří stream do kterého připraví kompletně model grafu k uložení.
	 * @param model model grafu.
	 * @return výstupní stream v podobě pole bajtů, který je možné následně
	 * uložit do souboru, pokud ukládání selže vrátí {@code null}.
	 */
    public static ByteArrayOutputStream saveGraphModel(IGraph model) {
        return graphSaver.saveGraph(model);
    }
    
	/**
	 * Vytvoří stream do kterého připraví kompletně pohled grafu k uložní.
	 * @param gCanvas pohled grafu.
	 * @return výstupní stream v podobě pole bajtů, který je možné následně
	 * uložit do souboru. pokud ukládání selže vrátí {@code null}.
	 */
    public static ByteArrayOutputStream saveGraphView(GraphCanvas gCanvas) {
        return graphSaver.saveGraph(gCanvas);
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // saving model of graph
    
    private ByteArrayOutputStream saveGraph(IGraph model) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            
            Element graphElem = doc.createElement(GraphPersistence.ELEM_GRAPH);
            
            Element operationsElem = doc.createElement(GraphPersistence.ELEM_OPERATIONS);
            List<IOperationModel> operations = model.getOperations();
            for (IOperationModel opModel : operations) {
                operationsElem.appendChild(operationModelToXML(opModel, doc));
            }
            graphElem.appendChild(operationsElem);

            Element edgesElem = doc.createElement(GraphPersistence.ELEM_EDGES);
            List<IEdge> edges = model.getEdges();
            for (IEdge edge : edges) {
                edgesElem.appendChild(edgeToXML(edge, doc));
            }
            graphElem.appendChild(edgesElem);
            
            doc.appendChild(graphElem);
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(new BufferedOutputStream(baos));
            
            transformer.transform(source, result);
            return baos;
        } catch (TransformerException ex) {
            String title = messages.getString("title.transformer_exception", 
                    "Transformer Exception");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (ParserConfigurationException ex) {
            String title = messages.getString("title.parse_configuration_exception",
                    "Parse Configuration Exception");
            Despr.showError(title, ex, Level.WARNING, true);
        }
        
        return null;
    }
    
    private ByteArrayOutputStream saveGraph(GraphCanvas gCanvas) {
         try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            
            Element graphElem = graphToXML(gCanvas, doc);
            doc.appendChild(graphElem);
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(new BufferedOutputStream(baos));
            
            transformer.transform(source, result);
            
            return baos;
        } catch (TransformerException ex) {
            String title = messages.getString("title.transformer_exception", 
                    "Transformer Exception");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (ParserConfigurationException ex) {
            String title = messages.getString("title.parse_configuration_exception",
                    "Parse Configuration Exception");
            Despr.showError(title, ex, Level.WARNING, true);
        }
         
         return null;
    }
    
    private Element operationModelToXML(IOperationModel opModel, Document doc) {

        IOperation op = opModel.getOperation();
        IParameters<IInputParameter> inputParams = opModel.getInputParameters();
        
        Class opType = op.getClass();
        
        Element operationElem = doc.createElement(GraphPersistence.ELEM_OPERATION);
        operationElem.setAttribute(GraphPersistence.ATTR_ID, Integer.toString(opModel.getId()));
        operationElem.setAttribute(GraphPersistence.GM_ATTR_TYPE, opType.getName());
        if (opModel instanceof IRootOperationModel) {
            operationElem.setAttribute(GraphPersistence.GM_ATTR_ROOT, Boolean.toString(true));
        } else {
            operationElem.setAttribute(GraphPersistence.GM_ATTR_ROOT, Boolean.toString(false));
        }
        
        for (IInputParameter inputParam : inputParams) {
            if (inputParam.getType() == EInputParameterType.INNER) {
                Object value = inputParam.getValue();
                Element paramElem;
                if (value != null) {
                    paramElem = objectToXML(inputParam.getValue(), 
                                            inputParam.getName(), 
                                            doc);
                } else {
                    paramElem = nullObjectToXML(inputParam.getDataType(), 
                                                 inputParam.getName(), doc);
                }
                
                operationElem.appendChild(paramElem);
            }
        }
        
        return operationElem;
    }  
    
    private Element nullObjectToXML(Class returnType, String name, Document doc) {
        Element nullElem = doc.createElement(GraphPersistence.GM_ELEM_NULL_OBJECT);
        nullElem.setAttribute(GraphPersistence.GM_ATTR_NAME, name);
        nullElem.setAttribute(GraphPersistence.GM_ATTR_TYPE, returnType.getName());
        return nullElem;
    }
    
    private Element objectToXML(Object o, String name, Document doc) {
        Class objectType = o.getClass();
        
        Wrapper wrapper = WrappedObjects.getWrapper(objectType);
        if (wrapper != null) {
            wrapper.wrap(o);
            o = wrapper;
        }
        
        
        if (!Modifier.isPublic(objectType.getModifiers())) {
            String message = String.format(
                             messages.getString("exception.private_atribute_for_save",
                                                "The type '%s' must be defined as a 'public',"
                                                + "otherwise it can not again load from XML!"),
                             objectType.getCanonicalName());
            throw new IllegalArgumentException(message);
        }
        
        String primitiveName = GraphPersistence.getPrimitiveElementName(objectType);
        if (!primitiveName.equals("")) {
            // for primitive types (primitive types are defined in 
            // method: GraphPersistance.getPrimitiveElementName)
            Element primitiveElem = doc.createElement(GraphPersistence.GM_ELEM_PRIMITIVE);
            primitiveElem.setAttribute(GraphPersistence.GM_ATTR_NAME, name);
            
            Element valueElem = doc.createElement(primitiveName);
            valueElem.appendChild(doc.createTextNode(o.toString()));
            
            primitiveElem.appendChild(valueElem);
            return primitiveElem;
        } else if (Collection.class.isAssignableFrom(objectType)) {
            // for any collection type
            return collectionToXML((Collection) o, name, doc);
        } else if (Map.class.isAssignableFrom(objectType)) {
            // for any dictionary type
            return dictionaryToXML((Map) o, name, doc);
        } else if (objectType.isArray()) {
            // for arrays
            return arrayToXML(o, name, doc);
        } else if (objectType.isEnum()) {
            // for enumerated type
            return enumToXML((Enum) o, name, doc);
        }
        
        // for object
        Element objectElem = doc.createElement(GraphPersistence.GM_ELEM_OBJECT);
        objectElem.setAttribute(GraphPersistence.GM_ATTR_NAME, name);
        objectElem.setAttribute(GraphPersistence.GM_ATTR_TYPE, objectType.getName());
        
        ObjectProperties objectProperties = new ObjectProperties(o.getClass());
        
        for (String propertyName : objectProperties) {
            Object value = objectProperties.get(propertyName, o);
            
            if (value != null) {
                Element valueElem = objectToXML(value, propertyName, doc);
                objectElem.appendChild(valueElem);
            } else {
                Class returnType = objectProperties.getReadMethod(propertyName).getReturnType();
                objectElem.appendChild(nullObjectToXML(returnType, propertyName, doc));
            }
        }
        
        return objectElem;
    }
    
    private Element collectionToXML(Collection collect, String name, Document doc) {
        
        Element collectElem = doc.createElement(GraphPersistence.GM_ELEM_COLLECTION);
        collectElem.setAttribute(GraphPersistence.GM_ATTR_NAME, name);
        collectElem.setAttribute(GraphPersistence.GM_ATTR_TYPE, collect.getClass().getName());

        int idx = 0;
        for (Object o : collect) {
            Element itemElem = doc.createElement(GraphPersistence.GM_ELEM_ITEM);
            itemElem.setAttribute(GraphPersistence.GM_ATTR_INDEX, Integer.toString(idx));
            
            itemElem.appendChild(objectToXML(o, null, doc));
            collectElem.appendChild(itemElem);
            idx++;
        }
        return collectElem;
    }
    
    private Element dictionaryToXML(Map dictionary, String name, Document doc) {

        Element dictElem = doc.createElement(GraphPersistence.GM_ELEM_DICTIONARY);
        dictElem.setAttribute(GraphPersistence.GM_ATTR_NAME, name);
        dictElem.setAttribute(GraphPersistence.GM_ATTR_TYPE, dictionary.getClass().getName());
        
        Set keys = dictionary.keySet();
        
        for (Object key : keys) {
            
            Element keyElem = doc.createElement(GraphPersistence.GM_ELEM_KEY);
            keyElem.appendChild(objectToXML(key, null, doc));
            dictElem.appendChild(keyElem);
            
            Element valueElem = doc.createElement(GraphPersistence.GM_ELEM_VALUE);
            valueElem.appendChild(objectToXML(dictionary.get(key), null, doc));
            dictElem.appendChild(valueElem);
        }
        
        return dictElem;
    }
    
    private Element arrayToXML(Object array, String name, Document doc) {
        
        Element arrayElem = doc.createElement(GraphPersistence.GM_ELEM_ARRAY);
        arrayElem.setAttribute(GraphPersistence.GM_ATTR_NAME, name);
        arrayElem.setAttribute(GraphPersistence.GM_ATTR_TYPE, array.getClass().getComponentType().getName());
        int length = Array.getLength(array);
        arrayElem.setAttribute(GraphPersistence.GM_ATTR_SIZE, Integer.toString(length));
        
        for (int i = 0; i < length; i++) {
            Element itemElem = doc.createElement(GraphPersistence.GM_ELEM_ITEM);
            itemElem.setAttribute(GraphPersistence.GM_ATTR_INDEX, Integer.toString(i));
            itemElem.appendChild(objectToXML(Array.get(array, i), null, doc));
            arrayElem.appendChild(itemElem);
        }
        
        return arrayElem;
    }
    
    private Element enumToXML(Enum enumeration, String name, Document doc) {
    
        Element enumElem = doc.createElement(GraphPersistence.GM_ELEM_ENUM);
        enumElem.setAttribute(GraphPersistence.GM_ATTR_NAME, name);
        enumElem.setAttribute(GraphPersistence.GM_ATTR_TYPE, enumeration.getClass().getName());
        
        enumElem.appendChild(doc.createTextNode(enumeration.name()));
        
        return enumElem;
    }
    
    private Element edgeToXML(IEdge edge, Document doc) {
        
        Element edgeElem = doc.createElement(GraphPersistence.ELEM_EDGE);
        edgeElem.setAttribute(GraphPersistence.ATTR_ID, Integer.toString(edge.getId()));
        
        Element sourceElem = doc.createElement(GraphPersistence.GM_ELEM_SOURCE);
        sourceElem.setAttribute(GraphPersistence.GM_ATTR_OPERATION_ID, 
                                Integer.toString(edge.getSource().getParent().getId()));
        
        sourceElem.appendChild(doc.createTextNode(edge.getSource().getName()));
        edgeElem.appendChild(sourceElem);
        
        Element targetElem = doc.createElement(GraphPersistence.GM_ELEM_TARGET);
        targetElem.setAttribute(GraphPersistence.GM_ATTR_OPERATION_ID,
                                Integer.toString(edge.getTarget().getParent().getId()));
        
        targetElem.appendChild(doc.createTextNode(edge.getTarget().getName()));
        edgeElem.appendChild(targetElem);
        
        return edgeElem;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // saving view of graph
    
    private Element edgeToXML(Edge edge, Document doc) {
        Element edgeElem = doc.createElement(GraphPersistence.ELEM_EDGE);
        edgeElem.setAttribute(GraphPersistence.ATTR_ID, Integer.toString(edge.getModel().getId()));
        edgeElem.setAttribute(GraphPersistence.GV_ATTR_EDGE_COLOR, Integer.toString(edge.getColor()));
        
        List<IdPoint> points = edge.getPoints();
        // posledni bod se ulozi je ohraniceni vstupniho portu
        int size = points.size() - 1;
        for (int i = 0; i < size; i++) {
            IdPoint p = points.get(i);
            Element pointElem = doc.createElement(GraphPersistence.GV_ELEM_POINT);
            pointElem.setAttribute(GraphPersistence.ATTR_ID, Integer.toString(p.getId()));
            
            Element xLocElem = doc.createElement(GraphPersistence.GV_ELEM_X);
            xLocElem.appendChild(doc.createTextNode(Integer.toString(p.x)));
            pointElem.appendChild(xLocElem);
            
            Element yLocElem = doc.createElement(GraphPersistence.GV_ELEM_Y);
            yLocElem.appendChild(doc.createTextNode(Integer.toString(p.y)));
            pointElem.appendChild(yLocElem);

            edgeElem.appendChild(pointElem);
        }
        
        IdRectangle inputPortBounds = edge.getInputPortBounds();
        Element ipBoundsElem = doc.createElement(GraphPersistence.GV_ELEM_INPUT_PORT_BOUNDS);
        ipBoundsElem.setAttribute(GraphPersistence.ATTR_ID, 
                Integer.toString(inputPortBounds.getId()));
        
        Element xLocElem = doc.createElement(GraphPersistence.GV_ELEM_X);
        xLocElem.appendChild(doc.createTextNode(Integer.toString(inputPortBounds.x)));
        ipBoundsElem.appendChild(xLocElem);
        
        Element yLocElem = doc.createElement(GraphPersistence.GV_ELEM_Y);
        yLocElem.appendChild(doc.createTextNode(Integer.toString(inputPortBounds.y)));
        ipBoundsElem.appendChild(yLocElem);
        
        Element widthElem = doc.createElement(GraphPersistence.GV_ELEM_WIDTH);
        widthElem.appendChild(doc.createTextNode(Integer.toString(inputPortBounds.width)));
        ipBoundsElem.appendChild(widthElem);
        
        Element heightElem = doc.createElement(GraphPersistence.GV_ELEM_HEIGHT);
        heightElem.appendChild(doc.createTextNode(Integer.toString(inputPortBounds.height)));
        ipBoundsElem.appendChild(heightElem);
        
        edgeElem.appendChild(ipBoundsElem);
        
        return edgeElem;
    }
    
    private Element operationToXML(Operation op, Document doc) {
        
        Element operationElement = doc.createElement(GraphPersistence.ELEM_OPERATION);
        operationElement.setAttribute(GraphPersistence.ATTR_ID, 
                                      Integer.toString(op.getModel().getId()));
        
        Element positionElement = doc.createElement(GraphPersistence.GV_ELEM_POSITION);
        Point location = op.getLocation();
        
        Element xPosElement = doc.createElement(GraphPersistence.GV_ELEM_X);
        xPosElement.appendChild(doc.createTextNode(Integer.toString(location.x)));
        positionElement.appendChild(xPosElement);
        
        Element yPosElement = doc.createElement(GraphPersistence.GV_ELEM_Y);
        yPosElement.appendChild(doc.createTextNode(Integer.toString(location.y)));
        positionElement.appendChild(yPosElement);
        
        operationElement.appendChild(positionElement);
        return operationElement;
    }
    
    private Element graphToXML(GraphCanvas gCanvas, Document doc) {
        Element graphElem = doc.createElement(GraphPersistence.ELEM_GRAPH);

        Component[] graphComponents = gCanvas.getComponents();
        Element operationsElem = doc.createElement(GraphPersistence.ELEM_OPERATIONS);
        Element edgesElem = doc.createElement(GraphPersistence.ELEM_EDGES);
        for (Component comp : graphComponents) {
            
            if (comp instanceof Operation) {
                operationsElem.appendChild(operationToXML((Operation) comp, doc));
            } else if (comp instanceof Edge) {
                edgesElem.appendChild(edgeToXML((Edge) comp, doc));
            }
        }
        
        graphElem.appendChild(operationsElem);
        graphElem.appendChild(edgesElem);
        
        return graphElem;
    }
}
