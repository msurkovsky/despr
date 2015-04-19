package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.IParameters;
import cz.vsb.cs.sur096.despr.model.IInputParameter;
import cz.vsb.cs.sur096.despr.model.IOperationModel;
import cz.vsb.cs.sur096.despr.model.IOutputParameter;
import cz.vsb.cs.sur096.despr.utils.DrawIcon;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import cz.vsb.cs.sur096.despr.window.DesprIcons;
import cz.vsb.cs.sur096.despr.window.actions.DeleteComponentFromGraphAction;
import cz.vsb.cs.sur096.despr.window.actions.ExecuteOperationAction;
import cz.vsb.cs.sur096.despr.window.actions.ShowThumbnailAction;
import java.awt.*;
import java.beans.ExceptionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import javax.swing.*;

/**
 * Grafická komponenta reprezentující operaci.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/22/08:09 
 */
public class Operation
            extends JPanel
            implements Movable, Selectable, ExceptionListener {
    
    /** Seznam posluchačů změny vlastosti. */
    private PropertyChangeSupport pcs;
    
    /** Model operace */
    private IOperationModel model;
    
    /** Panely se vstupními a výstupními porty.*/
    private InputPortsPanel pnlInputPorts;
    private OutputPortsPanel pnlOutputPorts;
    
    /** Tlčítka na operaci.*/
    private JButton btnThumbs;
    private JButton btnPlay;
    
    /** Příznak toho zda je operace vybrána.*/
    private boolean selected;

    /**
     * Iniciuje operace na základě modelu.
     * @param model 
     */
    public Operation(IOperationModel model) {
        this.model = model;
        addMouseListener(new DeleteComponentFromGraphAction());
        init();
    }
    
    /** Barva pozadí operace. */
    private Color bgColor;
    /** Barvy pro vytvoření gradientu, který přechází přes operaci. */
    private Color gradientColor1;
    private Color gradientColor2;
    
    /**
     * inicializuje operaci
     */
    private void init() {
        bgColor = new Color(100, 100, 100);
        gradientColor1 = new Color(255, 255, 255, 250);
        gradientColor2 = new Color(255, 255, 255, 150);
        
        model.addExceptionListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new OperationRoundBorder(Color.BLACK, 2));
        setBackground(new Color(0,0,0,0));
        
        pnlInputPorts = new InputPortsPanel();
        IParameters<IInputParameter> ips = model.getInputParameters();
        
        for (IInputParameter ip : ips) {
            pnlInputPorts.addInputParameter(ip);
        }
        add(pnlInputPorts);
        
        // panel s informacemi ve stredu operace
        JPanel pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 15, 2, 0);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        URL urlIcon = getModel().getOperation().getClass().getResource(getModel().getName() + ".png");
        Icon icon = DrawIcon.loadIcon(urlIcon, 32, 32);
        JLabel lblIcon = new JLabel(icon);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        pnlInfo.add(lblIcon, gbc);
        
        // cast se jmenem
        JPanel pnlName = new JPanel();
        pnlName.setOpaque(false);
        pnlName.setLayout(new BoxLayout(pnlName, BoxLayout.Y_AXIS));
        Font fontName = new Font(Font.SANS_SERIF, Font.BOLD, 10);
        
        String name = model.getDisplayName();
        String[] lines = name.split("\\n");
        for (int i = 0; i < lines.length; i++) {
            JLabel lblName = new JLabel(lines[i]);
            lblName.setFont(fontName);
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlName.add(lblName);
        }
        
        JLabel lblId = new JLabel("@" + model.getId());
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        Font fontId = new Font(Font.SANS_SERIF, Font.PLAIN, 8);
        lblId.setFont(fontId);
        pnlName.add(lblId);
        
        gbc.gridx = 1;
        pnlInfo.add(pnlName, gbc);
        
        // tlacitka
        gbc.insets.right = 3;
        gbc.insets.bottom = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        int y = 0;
        if (model.getOperation() instanceof Displayable) {
            btnThumbs = new JButton(DesprIcons.getIcon(
                    DesprIcons.THUMBNAIL_ICON, true));
            btnThumbs.setFont(fontName);
            btnThumbs.addActionListener(new ShowThumbnailAction(model));
            gbc.gridheight = 1;
            gbc.gridx = 2;
            gbc.gridy = y;
            y += 1;
            pnlInfo.add(btnThumbs, gbc);
        }    
        
        Action executeOperation = new ExecuteOperationAction(model);
        executeOperation.putValue(AbstractAction.NAME, null);
        executeOperation.putValue(AbstractAction.LARGE_ICON_KEY, 
                DesprIcons.getIcon(DesprIcons.OPERATION_PLAY_ICON, true));
        btnPlay = new JButton(executeOperation);

        gbc.insets.top = 0;
        gbc.insets.bottom = 2;
        gbc.gridx = 2;
        gbc.gridy = y;
        pnlInfo.add(btnPlay, gbc);
        
        add(pnlInfo);
        
        // vystupni porty
        pnlOutputPorts = new OutputPortsPanel();
        IParameters<IOutputParameter> ops = model.getOutputParameters();
        for (IOutputParameter op : ops) {
            pnlOutputPorts.addOutputParameter(op);
        }
        pnlInputPorts.addMessageListener(pnlOutputPorts);
        
        add(pnlOutputPorts);
        
        String descr = model.getDescription();
        if (descr != null && !descr.equals("")) {
            setToolTipText(model.getDescription());
        }
    }
    
    /**
     * Překreslí okolí okolo operace. Je to nutné díky tomu,
	 * že operace obsahuje průhlednou část pod kterou musí
	 * být korektně překreslené komponenty.
     */
    public void repaintNearArea() {
        
        revalidate(); // je mozne ze se zmenily vlastnosti portu, je treba
                      // revalidovat obsah komponenty
        
        // take je mozna zmena velikosti komponenty takze se prepocte
        setSize(getPreferredSize());
        
        Rectangle bounds = getBounds();      
        // diky pruhlednosti okolo portu se musi preklreslit i platno,
        // ale prekresluji jen oblast okolo operace, je zbytecne prekreslovat
        // cele platno
        // aby prekresleni fungovalo je treba o neco zvetsit hranice +-1.
        Component parent = getParent();
        if (parent != null) {

            parent.repaint(bounds.x - 1, bounds.y - 1, 
                                bounds.width + 1, bounds.height + 1);
            
            // prekresleni vsech hran, tak aby vizualne vse sedelo. Bez tohoto
            // jsou pri prekreslovani bile ramecky okolo operaci, ktere 
            // zakryvaji casti sipek.
            Component[] comps = ((Container) parent).getComponents();
            for (Component comp : comps) {
                if (comp instanceof Edge) {
                    comp.repaint();
                }
            }
        }
    }
    
    /**
     * Vykreslí komponentu.
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {

        // zjisti se a nastavi nove rozmery operace
        Dimension prefSize = getPreferredSize();
        int w = prefSize.width;
        int h = prefSize.height;
        
        int ipPnlHeight = pnlInputPorts.getPreferredSize().height;
        int opPnlHeight = pnlOutputPorts.getPreferredSize().height;
        int halfIpHeight = ipPnlHeight / 2;
        int halfOpHeight = opPnlHeight / 2;
        
        h = h - halfIpHeight - halfOpHeight;
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        int arcSize = Math.min(w, h);
        arcSize /= 4;
        g2.setColor(bgColor);
        g2.fillRoundRect(1, halfIpHeight + 1, w-2, h-2, arcSize, arcSize);
        
        GradientPaint gp = new GradientPaint(w/2, 0, gradientColor1, 
                                             w/2, h, gradientColor2);
        g2.setPaint(gp);
        g2.fillRoundRect(1, halfIpHeight + 1, w-2, h-2, arcSize, arcSize);
    }
    
    /**
     * Povolí či zakáže tlačítka na operaci.
     * @param enabled mají být tlačítka na operaci povolena?
     */
    public void setEnableButtons(boolean enabled) {
        btnPlay.setEnabled(enabled);
        if (btnThumbs != null) {
            btnThumbs.setEnabled(enabled);
        }
    }
    
    /**
     * Reaguje na chybu vyvolanou v operaci. Pokud se tak stane
	 * nastaví operaci jako vybranou. A tím zdůrazní o kterou v které
	 * operaci nastal problém.
     * @param e chyba kterou mohla operace při svém zpracování vyvolat.
     */
    @Override
    public void exceptionThrown(Exception e) {
        // Pokud v modelu nastala nejaka chyba oznacim operaci jako vybranou
        // tim dam uzivateli vedet ve ktere operaci chyba nastala.
        setSelected(true);
    }
    
    /**
     * Zjistí zda je operace vybrána či nikoliv.
     * @return {@code true} pokud je operace vybrána, jinak {@code false}
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * Nastaví operaci příznak určující zda je vybrána či nikoliv.
     * @param selected je operace vybrána?
     */
    @Override
    public void setSelected(boolean selected) {
        boolean oldSelected = this.selected;
        if (oldSelected != selected) {
            this.selected = selected;
            if (selected) { 
                setBorder(new OperationRoundBorder(Despr.SELECT_OBJECT_COLOR, 2));
            } else {
                setBorder(new OperationRoundBorder(Color.BLACK, 2));
            }
            repaintNearArea();
            pcs.firePropertyChange("selected", oldSelected, selected);
        }
    }
    
    /**
     * Nataví pozici operace na plátně.
     * @param p pozice levého horního rohu operace.
     */
    @Override
    public void setLocation(Point p) {
        Point oldLocation = getLocation();
        super.setLocation(p);
        
        if (!oldLocation.equals(p)) {
            pnlInputPorts.changePosition();
            pnlOutputPorts.changePosition();
        }
    }
    
    /**
     * Poskytne model operace.
     * @return model operace.
     */
    public IOperationModel getModel() {
        return model;
    }
    
    /**
     * Poskytne panel se vstupními porty.
     * @return panel se vstupními porty.
     */
    // tyhle dve metody vyuziva kresleni oramovani RoundBorder
    public int getInputPortsPnlHeight() {
        return pnlInputPorts.getPreferredSize().height;
    }
    
    /**
     * Poskytne panel se výstupními porty.
     * @return panel se výstupními porty.
     */
    public int getOutputPortsPnlHeight() {
        return pnlOutputPorts.getPreferredSize().height;
    }
    
    /**
     * Najde vstupní port operace na základě modelu.
     * @param target model vstupního parametru.
     * @return pokud existuje vrátí odkaz na model vstupního portu,
	 * jinak vrátí {@code null}
     */
    public Port findInputPort(IInputParameter target) {
        return pnlInputPorts.findPort(target);
    }
    
    /**
     * Najde výstupní port operace na základě modelu.
     * @param source model výstupní parametru.
     * @return pokud existuje vrátí odkaz na výstupní port,
	 * jinak vrátí {@code null}.
     */
    public Port findOutputPort(IOutputParameter source) {
        return pnlOutputPorts.findPort(source);
    }
    
    /**
     * Přidá posluchače na změnu stavu.
     * @param l posluchač změny stavu.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (pcs == null) {
            pcs = new PropertyChangeSupport(this);
        }
        pcs.addPropertyChangeListener(l);
    }
    
    /**
     * Smaže posluchače na změnu stavu.
     * @param l posluchač změny stavu.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (pcs != null) {
            pcs.removePropertyChangeListener(l);
        }
    }
}
