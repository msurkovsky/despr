package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.exceptions.IncompatibleDataTypeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgeException;
import cz.vsb.cs.sur096.despr.exceptions.ParameterUsedException;
import cz.vsb.cs.sur096.despr.model.*;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.model.operation.IRootOperation;
import cz.vsb.cs.sur096.despr.structures.IdPoint;
import cz.vsb.cs.sur096.despr.structures.IdRectangle;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.window.actions.DeleteComponentFromGraphAction;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.*;

/**
 * Grafická reprezentace grafu. Plátno uchovává komponenty operací,
 * hran a zprostředkovává komunikaci s uživatelem.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/24/15:40
 */
public class GraphCanvas extends JLayeredPane implements Scrollable {
    
    private transient LocalizeMessages messages;
    
	/** Minimální rozměry plátna */
    private final int MIN_WIDTH = 1920;
    private final int MIN_HEIGH = 1280;
    
	/**
	 * Plátno využívá tři vrstvy a v každé z nich 
	 * se nachází jeden druh komponent, tak aby se vzájemně
	 * nevhodně nepřekreslovali.
	 */
    private final int EDGE_LAYER = 1;
    private final int POINT_VIEW_LAYER = 2;
    private final int OPERATION_LAYER = 3;
    
    /** Skoky při skrolování */
    private int maxUnitIncrement;

	/** Model grafu. */
    private IGraph model;
	/** Odkaz na v správce vybraných objektů. */
    private SelectedObjects selectedObjects;

	/** Informace o právě kreslené hraně. */
    private DrawingEdge drawingEdge;

    /** Posluchač který zajišťuje vykreslování hrany v průběhu kreslení. */
    private DrawingEdgeToCanvasListner drawingEdgeListener;
    
	/**
	 * Iniciuje prázdný graf.
	 * @param selectedObjects odkaz na správce vybraných objektů.
	 */
    public GraphCanvas(SelectedObjects selectedObjects) {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        model = new DefaultGraph();
        this.selectedObjects = selectedObjects;
        drawingEdge = new DrawingEdge();
        drawingEdgeListener = new DrawingEdgeToCanvasListner();
        maxUnitIncrement = 10;
        
        setLayout(null);
        setPreferredSize(new Dimension(MIN_WIDTH, MIN_HEIGH));
        setOpaque(true);
        setBackground(Color.WHITE);
        setTransferHandler(new CanvasTransferHandler());
        addMouseListener(new CanvasMouseListener());
        
        setAutoscrolls(true);
        CanvasMovingListener canvasMovingLst = new CanvasMovingListener();
        addMouseListener(canvasMovingLst);
        addMouseMotionListener(canvasMovingLst);
    }
    
    /**
     * Iniciuje graf s již načtenými komponentami ze souboru.
     * @param selectedObjects správce vybraných objektů.
     * @param operations seznam operací.
     * @param edges seznam hran.
     */
    public GraphCanvas(SelectedObjects selectedObjects, 
            List<Operation> operations, List<Edge> edges) {
        
        this(selectedObjects);
        loadComponents(operations, edges);
    }
    
    /**
     * Načte do grafu seznam hran a operací.
     * @param operations seznam operací.
     * @param edges seznam hran.
     */
    public final void loadComponents(List<Operation> operations, List<Edge> edges) {
        
        clearCanvas();
        
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = 0, maxY = 0;
        Rectangle bounds;
        for (Operation op : operations) {
            model.addOperation(op.getModel());
            addComponentToCanvas(op, OPERATION_LAYER);
            bounds = op.getBounds();
            if (bounds.x < minX) {
                minX = bounds.x;
            }
            if (bounds.y < minY) {
                minY = bounds.y;
            }
            
            int tmpX = bounds.x + bounds.width;
            if (tmpX > maxX) {
                maxX = tmpX + 10;
            }
            
            int tmpY = bounds.y + bounds.height;
            if (tmpY > maxY) {
                maxY = tmpY + 10;
            }
        }
        
        for (Edge edge : edges) {
            IEdge edgeModel = edge.getModel();
            IOutputParameter source = edgeModel.getSource();
            IInputParameter target = edgeModel.getTarget();
            int sourceOperationId = source.getParent().getId();
            int targetOperationId = target.getParent().getId();
            
            Port sourcePort = findOperation(sourceOperationId).findOutputPort(
                    source);
            Port targetPort = findOperation(targetOperationId).findInputPort(
                    target);
            
            // pred pridavanim hran, je treba uvolnit vsechny porty operaci
            sourcePort.getModel().setUsed(false);
            targetPort.getModel().setUsed(false);
            
            // je treba znovu vytvorit prvni a posledni port, kvuli mozne
            // zmene id zdrojeoveho a ciloveho portu.
            List<IdPoint> edgePoints = edge.getPoints();
            IdPoint pZero = edgePoints.get(0);
            edgePoints.remove(0);
            int lastIdx = edgePoints.size() - 1;
            IdPoint pLast = edgePoints.get(lastIdx);
            edgePoints.remove(lastIdx);
            edgePoints.add(0, new IdPoint(pZero, sourcePort.getId()));
            edgePoints.add(new IdPoint(pLast, targetPort.getId()));
            
            sourcePort.addPropertyChangeListener(edge);
            targetPort.addPropertyChangeListener(edge);
            
            try {
                // pridani hrany do modelu
                model.addEdge(edgeModel);
                // pridani kompoenenty na platno
                addComponentToCanvas(edge, EDGE_LAYER);
            } catch (IncorrectEdgeException ex) {
                String title = messages.getString("title.incorrect_edge_excp", 
                                                  "Incorrect Edge");
                Despr.showError(title, ex, Level.WARNING, false);
            } catch (ParameterUsedException ex) {
                String title = messages.getString("title.parameter_used_excp",
                                                  "Parameter is used");
                Despr.showError(title, ex, Level.WARNING, false);
            } catch (IncompatibleDataTypeException ex) {
                String title = messages.getString("title.incompatible_data_types_excp",
                                                  "Incompatible data types");
                Despr.showError(title, ex, Level.WARNING, false);
            }
            
            bounds = edge.getBounds();
            if (bounds.x < minX) {
                minX = bounds.x;
            }
            
            if (bounds.y < minY) {
                minY = bounds.y;
            }
            
            int tmpX = bounds.x + bounds.width;
            if (tmpX > maxX) {
                maxX = tmpX + 10;
            }
            
            int tmpY = bounds.y + bounds.height;
            if (tmpY > maxY) {
                maxY = tmpY + 10;
            }
        }
        
        setPreferredSize(new Dimension((maxX > MIN_WIDTH ? maxX : MIN_WIDTH), 
                                        (maxY > MIN_HEIGH ? maxY : MIN_HEIGH)));
        
        Container parent = getParent();
        if (parent != null && parent instanceof JViewport) {
            JViewport viewPort = (JViewport) parent;
            viewPort.setViewPosition(new Point(minX, minY));
        }
    }
    
    /**
     * Přidá komponentu na platno.
     * @param comp komponenta, která má být přidána.
     */
    public void addComponentToCanvas(Component comp) {
        addComponentToCanvas(comp, null);
    }
    
    /**
     * Přidá komponentu na platno, do specifické vrstvy.
     * @param comp komponenta, která má být přidána.
     * @param depth vrstva na kterou má být přidána.
     */
    public void addComponentToCanvas(Component comp, Integer depth) {
        
        if (comp instanceof Movable) {
            MovableHandler movableHandler = new MovableHandler();
            Movable movable = (Movable) comp;
            movable.addMouseListener(movableHandler);
            movable.addMouseMotionListener(movableHandler);
        }
        
        if (comp instanceof Selectable) {
            Selectable selectable = (Selectable) comp;
            selectable.addPropertyChangeListener(selectedObjects);
            if (!(comp instanceof Edge)) {
                selectable.addMouseListener(new SelectableHandler());
            }
        }
        
        if (depth != null) {
            add(comp, depth);
        } else {
            add(comp);
        }
    }
    
    /**
     * Smaže komponentu z plátna.
     * @param comp komponenta která má být smazána.
     */
    public void removeComponentFromCanvas(Component comp) {
        
        if (comp instanceof Movable) {
            Movable movable = (Movable) comp;
            MouseListener[] mouseListeners = movable.getMouseListeners();
            for (MouseListener ml : mouseListeners) {
                movable.removeMouseListener(ml);
            }
            
            MouseMotionListener[] mouseMotionListeners = movable.getMouseMotionListeners();
            for (MouseMotionListener mml : mouseMotionListeners) {
                movable.removeMouseMotionListener(mml);
            }
        }
        
        // vezme se ohraniceni komponenty
        Rectangle bounds = comp.getBounds();
        // zvetsi o pixel
        bounds = new Rectangle(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1);
        remove(comp);
        revalidate();
        // po smazani se jeste prekresli okoli kde byla komponenta umistena
        repaint(bounds);
    }
    
    /**
     * Najde operaci na plátně podle ID.
     * @param id ID operace.
     * @return pokud taková operace na plátně existuje pak ji vrátí,
	 * jinak vrací {@code null}.
     */
    public Operation findOperation(int id) {
        Component[] comps = getComponents();
        for (Component comp : comps) {
            if (comp instanceof Operation) {
                
                Operation op = (Operation) comp;
                if (op.getModel().getId() == id) {
                    return op;
                }
            }
        }
        return null;
    }
    
    /**
     * Najde hranu na plátně podle ID.
     * @param id ID hrany.
     * @return pokud taková hrana na plátně existuje, pak ji poskytne,
	 * jinak vrátí {@code null}.
     */
    public Edge findEdges(int id) {
        Component[] comps = getComponents();
        for (Component comp : comps) {
            if (comp instanceof Edge) {
                Edge edge = (Edge) comp;
                if (edge.getModel().getId() == id) {
                    return edge;
                }
            }
        }
        return null;
    }
    
    /**
     * Metoda pro kreslení hrany.
     * @param evt událost myši která ji vyvolala.
     * @param loc pozice kde má být vložen bod.
     */
    public void drawEdge(MouseEvent evt, Point loc) {
        if (drawingEdge.isDrawing) {
            Object o = evt.getSource();
            if (o instanceof GraphCanvas) {
                IdPoint idPoint = new IdPoint(loc);
                drawingEdge.edgePoints.add(idPoint);
            } else if (o instanceof Port) {
                Port port = (Port) o;
                IParameter param = port.getModel();
                if (param instanceof IInputParameter) {
                    
                    try {
                        IEdge edgeModel = model.addEdge(
                            (IOutputParameter) drawingEdge.source.getModel(),
                            (IInputParameter) param);
                    
                        // misto posledniho bodu se posila cele ohraniceni
                        // vstupniho portu
                        IdRectangle inputPortBounds = new IdRectangle(
                                port.getLocation(), port.getPreferredSize(), 
                                port.getId());
                        drawingEdge.isDrawing = false;

                        drawingEdge.target = (Port) o;

                        Edge e = new Edge(edgeModel, drawingEdge.edgePoints,
                                inputPortBounds);

                        drawingEdge.source.addPropertyChangeListener(e);
                        drawingEdge.target.addPropertyChangeListener(e);
                        addComponentToCanvas(e, EDGE_LAYER); 
                        
                    } catch (IncorrectEdgeException ex) {
                        String title = messages.getString("title.incorrect_edge_excp", 
                                                        "Incorrect Edge");
                        Despr.showError(title, ex, Level.WARNING, false);
                    } catch (ParameterUsedException ex) {
                        String title = messages.getString("title.parameter_used_excp",
                                                        "Parameter is used");
                        Despr.showError(title, ex, Level.WARNING, false);
                    } catch (IncompatibleDataTypeException ex) {
                        String title = messages.getString("title.incompatible_data_types_excp",
                                                        "Incompatible data types");
                        Despr.showError(title, ex, Level.WARNING, false);
                    } finally {
                        drawingEdge.cancelDrawingEdge();
                        removeMouseMotionListener(drawingEdgeListener);
                        repaint();
                    }
                }
            }
        } else {
            Object o = evt.getSource();
            if (o instanceof Port) {
                Port p = (Port) o;
                IParameter parameter = p.getModel();
                if (parameter instanceof IOutputParameter) {
                    drawingEdge.isDrawing = true;
                     // pridani posluchace pri kresleni hrany
                    addMouseMotionListener(drawingEdgeListener);
                    IdPoint idPoint = new IdPoint(loc, p.getId());
                    drawingEdge.edgePoints.add(idPoint);
                    drawingEdge.source = p;
                }
            }
        }
    }

    /**
     * Poskytne model grafu.
     * @return model grafu.
     */
    public IGraph getModel() {
        return model;
    }
    
    /**
     * Poskytne správce vybratelných objektů.
     * @return správce vybratelných objektů.
     */
    public SelectedObjects getSelectedObjects() {
        return selectedObjects;
    }
    
    /**
     * Nastaví správce vybratelných objektů.
     * @param selectedObjects  správce vybratelných objektů.
     */
    public void setSelectedObjects(SelectedObjects selectedObjects) {
        this.selectedObjects = selectedObjects;
    }
    
    /**
     * Vymaže všechny komponenty z plátna.
     */
    public void clearCanvas() {
        removeAll();
        revalidate();
        repaint();
        
        List<IOperationModel> ops = model.getOperations();
        
        while(!ops.isEmpty()) {
            model.removeOperation(ops.get(0));
        }
    }
    
    /**
     * Při kreslení hrany vykresluje přímo do plátna linku, znázorňující
	 * kreslenou hranu.
     * @param g grafický kontext.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (drawingEdge.isDrawing) { // kresleni hrany
            Graphics2D g2 = (Graphics2D) g;
			int len = drawingEdge.getCountEdgePoints() - 1;
			for (int i = 0; i < len; i++) {
				Point p1 = drawingEdge.edgePoints.get(i);
				Point p2 = drawingEdge.edgePoints.get(i+1);
				g2.drawLine(p1.x, p1.y, p2.x, p2.y);
			}
			Point lastEdgePoint = drawingEdge.edgePoints.get(len);
			g2.drawLine(lastEdgePoint.x, lastEdgePoint.y, 
                        drawingEdge.drawingPoint.x, drawingEdge.drawingPoint.y);
		}
    }
       
    /**
     * Poskytne minimální rozměry plátna.
     * @return minimální rozměry plátna.
     */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(MIN_WIDTH, MIN_HEIGH);
    }

    ////////////////////////////////////////////////////////////
    // Implementace rozhrani Scrollable
    
    /**
     * Poskytne velikost zobrazitelného pole pro skrolování.
     * @return preffered size.
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * Poskytne velikost jedné jednotky pro pohyb s plátnem.
     * @param visibleRect viditelná oblast.
     * @param orientation orientace scroll baru.
     * @param direction směr.
     * @return číslo reprezentující velikost posunu.
     */
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            //Get the current position.
        int currentPosition;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                            (currentPosition / maxUnitIncrement)
                            * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                    * maxUnitIncrement
                    - currentPosition;
        }
    }

    /**
     * Poskytne velikost skoku při skrolování po blocích.
     * @param visibleRect viditelná oblast.
     * @param orientation orientace scroll baru.
     * @param direction směr pohybu.
     * @return číslo reprezentující velikost posunu.
     */
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    /**
     * @return {@code false}.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    /**
     * @return {@code false}.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
	/**
	 * Implementace reakcí myší na ovládání plátna.
	 */
    private class CanvasMouseListener extends  MouseAdapter {

        private Edge selectedEdge;
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (drawingEdge.isDrawing) {
                int modifiers = e.getModifiers();
                if (modifiers == MouseEvent.BUTTON1_MASK) {
                    drawEdge(e, e.getPoint());
                } else if (modifiers == MouseEvent.BUTTON3_MASK) {
                    // pri stisknuti praveho tlacitka mysi se kreslena cara vymaze
                    drawingEdge.isDrawing = false;
                    drawingEdge.cancelDrawingEdge();
                    repaint(); //kreslena hrana nebyla dokoncena je tedy nutne
                               //platno prekresli
                }
            } else {
                // vyber hrany je zde i kvuli moznosti pridavat body
                // zaroven totiz provede kontrolu zda je bod na hrane
                // pokud ne automaticky ji smaze
                List<Edge> edges = new ArrayList<Edge>(50);
                Component[] canvasComponents = GraphCanvas.this.getComponents();
                for (Component comp : canvasComponents) {
                    if (comp instanceof Edge) {
                        edges.add((Edge) comp);
                    }
                }

                Edge select = EdgeUtils.setSelected(edges, e.getPoint());
                if (select == null) {
                    selectedObjects.setSelectObjects();
                    selectedEdge = null;
                } else {
                    if (select.isFirstSelect()) {
                        List<PointView> pvs = select.getPointsView();
                        for (PointView pv : pvs) {
                            GraphCanvas.this.addComponentToCanvas(pv, POINT_VIEW_LAYER);
                        }
                        select.setFirstSelectToFalse();
                    }
                    selectedEdge = select;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!drawingEdge.isDrawing && e.getModifiers() == MouseEvent.BUTTON3_MASK) {
                maybeShowPopup(e, selectedEdge);
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e, selectedEdge);
        }
        
        private void maybeShowPopup(final MouseEvent e, final Edge selectedEdge) {
            if (e.isPopupTrigger()) {
                JPopupMenu deleteMenu = new JPopupMenu();
                deleteMenu.setLocation(e.getLocationOnScreen());
                
                JMenuItem addPoint = new JMenuItem(
                        messages.getString("popup.edge.add_point", "Add Point"));
                addPoint.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        Point point = new Point(e.getX(), e.getY());
                        IdPoint idPoint = new IdPoint(point);
                        PointView newPoint = selectedEdge.addNewPoint(idPoint);
                        if (newPoint != null) {
                            GraphCanvas.this.addComponentToCanvas(
                                    newPoint, POINT_VIEW_LAYER);
                        }
                    }
                });
                
                JMenuItem deleteComponent = new JMenuItem(
                        messages.getString("popup.edge.delete", "Delete"));
                deleteComponent.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (selectedEdge != null) {
                            DeleteComponentFromGraphAction.deleteComponent(selectedEdge);
                        }
                    }
                });

                if (selectedEdge != null) {
                    deleteMenu.add(addPoint);
                    deleteMenu.add(deleteComponent);
                    deleteMenu.show(e.getComponent(), e.getX(), e.getY());
                    
                }
            }
        }
    }
    
    /**
     * Stará se o znázornění kreslené hrany.
     */
    private class DrawingEdgeToCanvasListner extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            drawingEdge.drawingPoint = e.getPoint();
            repaint();
        }
    }
    
    /**
     * Umožňuje táhnout plátnem při držení tlačítka.
     */
    private class CanvasMovingListener extends MouseAdapter implements MouseMotionListener {
        
        @Override
        public void mouseReleased(MouseEvent e) {
            Object o = e.getSource();
            if (o instanceof JComponent) {
                JComponent comp = (JComponent) o;
                comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            Object o = e.getSource();
            if (o instanceof GraphCanvas) {
                GraphCanvas gCanvas = (GraphCanvas) o;
                gCanvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                Rectangle rec = new Rectangle(e.getX(), e.getY(), 1, 1);
                gCanvas.scrollRectToVisible(rec);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }
    
    /**
     * Umožňuje přidávat operace na plátno pomocí funkce Drag and Drop.
     */
    private class CanvasTransferHandler extends TransferHandler {
        
        @Override
        public boolean canImport(TransferSupport supp) {
            if (supp.isDataFlavorSupported(TransferableOperations.operationsLocalFlavor)) {
                return true;
            } else {
                return false;
            }
        }
        
        @Override
        public boolean importData(TransferSupport supp) {
            if (!canImport(supp)) {
                return false;
            }
            
            if (supp.getComponent() instanceof GraphCanvas) {
                
                GraphCanvas gCanvas = (GraphCanvas) supp.getComponent();    
                
                Transferable t = supp.getTransferable();
                try {
                    IOperation[] baseOps = (IOperation[]) t.getTransferData(
                            TransferableOperations.operationsLocalFlavor);
                    for (IOperation baseOp : baseOps) {
                        Operation op;
                        if (baseOp instanceof IRootOperation) {
                            IRootOperationModel rootOp = new RootOperationModel((IRootOperation) baseOp);
                            op = new Operation(rootOp);
                        } else {
                            IOperationModel opModel = new OperationModel(baseOp);
                            op = new Operation(opModel);
                        }

                        IGraph model = gCanvas.getModel();
                        op.getModel();
                        model.addOperation(op.getModel());

                        op.setSize(op.getPreferredSize());

                        Point dropLocation = supp.getDropLocation().getDropPoint();
                        Dimension opSize = op.getPreferredSize();
                        Point newDropLocation = new Point(dropLocation.x - opSize.width / 2, dropLocation.y - opSize.height / 2);

                        op.setLocation(newDropLocation);
                        gCanvas.addComponentToCanvas(op, OPERATION_LAYER);
                    }
                    gCanvas.revalidate();
                    return true;
                    
                } catch (UnsupportedFlavorException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            
            return false;
        }
        
    }
    
    /**
     * Struktura uchovávající informace o kreslené hraně.
     */
    private class DrawingEdge {
        
        boolean isDrawing;
        Point drawingPoint;
        List<IdPoint> edgePoints;
        Port source;
        Port target;
        
        DrawingEdge() {
            edgePoints = new ArrayList<IdPoint>(10);
        }
        
        void cancelDrawingEdge() {
            isDrawing = false;
            if (edgePoints != null) {
                while (!edgePoints.isEmpty()) {
                    edgePoints.remove(0);
                }
            }
        }
        
        int getCountEdgePoints() {
            return edgePoints.size();
        }
    }
}
