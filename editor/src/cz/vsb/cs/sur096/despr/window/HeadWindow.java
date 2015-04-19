package cz.vsb.cs.sur096.despr.window;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.controller.Executable;
import cz.vsb.cs.sur096.despr.controller.GraphController;
import cz.vsb.cs.sur096.despr.controller.IGraphController;
import cz.vsb.cs.sur096.despr.events.MessageSupport;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.view.GraphCanvas;
import cz.vsb.cs.sur096.despr.view.Operation;
import cz.vsb.cs.sur096.despr.view.SelectedObjects;
import cz.vsb.cs.sur096.despr.view.operationstree.*;
import cz.vsb.cs.sur096.despr.window.actions.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Definice hlavního okna. Komponenta se stará o vykreslení celé aplikace.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/24/11:54
 */
public class HeadWindow extends JFrame {

    /**
	 * Stav aplikace ve kterém se definuje nový graf 
	 * (struktura procesu pro zpracování).
	 */
    public static final int PAINTING = 1;
    /** Stav ve kterém se prování zpracování grafu. */
    public static final int RUNNING = 2;
    /** Stav ke kterém se provádí náčítání */
    public static final int LOADING = 3;
    
    /** Seznam lokalizačních zpráv. */
    private transient LocalizeMessages messages;
    /** Aktuální stav aplikace. */
    private int state;
    /** Odkaz na správce vybratelných objektů.*/
    private SelectedObjects selectedObjects;
    /** Odkaz na plátno grafu.*/
    private GraphCanvas gCanvas;
    /** Odkaz na objekt umožňující zpracování grafu.*/
    private IGraphController gController;
    /** 
	 * Odkaz na panel se záložkami obsahujícími standardní výstup a 
	 * nastavení vstupních parametrů vybrané operace.
	 */
    private InputOutputPanel stdoutAndPropertiesEdtior;
    /** Okdaz na strom operací.*/
    private OperationsTree operationsTree;
    /** Odkaz na komponentu se status barem.*/
    private StatusBar statusBar;
    
    /** 
	 * Dva panely panely rozdělující hlavní okno do třech částí:
	 * první dělí okno vertikálně kde vlevo je strom operací
	 * a vpravo druhý panel dělící druhou část okna horizontálně, 
	 * kde nahoře se nachází plátno grafu a vespod {@code InputOutputPanel}.
	 */
    private JSplitPane splitPane, splitPane2;

    
    /** Akce pro vytvoření, načtení, uložení grafu a zavření aplikace. */
    private Action newGraph, saveGraph, loadGraph, close;
    /** Akce pro spuštění, zastavení, verifikaci a resetování grafu.*/
    private Action executeGraph, stopExecuting, verifyGraph, resetGraph;
    /** Akce pro vytisknutí grafu do textového režimu a ořez plátna. */
	private Action printGraph, cutGraph;
    /** Akce pro smazání a export obsahu standardního výstupu. */
    private Action outputClear, outputExport;
    /** Akce pro zobrazení komponent nastavení aplikace.*/
    private Action showPluginManager, showTypesStructure;
    /** Seznam akcí pro změnu podporováných jazyků.*/
    private Action[] languageChangeActions;
    /** Seznam akcí pro změnu podporovaných stylů.*/
    private Action[] styleChangeActions;
    
    /**
     * Iniciace hlavního okna s výchozím nastavením stavu
	 * na definici grafu.
     */
    public HeadWindow() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        state = PAINTING;
        selectedObjects = new SelectedObjects();
        gCanvas = new GraphCanvas(selectedObjects);
        gController = new GraphController(gCanvas.getModel());
        setTitle("Despr");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800,600));
        init();
        setDesprState(PAINTING);
        addWindowListener(new HeadWindowClosingAction());
        setIconImage(DesprIcons.getImage(DesprIcons.APP_ICON, false));
    }
    
    /**
     * Nastaví stav aplikace jsou dvě možná nastavení:
	 * <ol>
	 *  <li>Běžící, zpracovávající graf.</li>
	 *  <li>Kreslící, tvořící graf modulů.</li>
	 * </ol>
	 * Podle stavu se povolí čí zakáže použitelnost definovaných akcí.
     * @param state změněný stav.
     */
    public final void setDesprState(int state) {
        if (state == RUNNING) {
            statusBar.setText(messages.getString("state.title.running", "Running"));
            newGraph.setEnabled(false);
            saveGraph.setEnabled(false);
            loadGraph.setEnabled(false);
            
            executeGraph.setEnabled(false);
            stopExecuting.setEnabled(true);
            verifyGraph.setEnabled(false);
            cutGraph.setEnabled(false);
            printGraph.setEnabled(false);
            
            outputClear.setEnabled(false);
            outputExport.setEnabled(false);
            
            showPluginManager.setEnabled(false);
            showTypesStructure.setEnabled(false);

            for (Action action : languageChangeActions) {
                action.setEnabled(false);
            }
            
            for (Action action : styleChangeActions) {
                action.setEnabled(false);
            }
            
            selectedObjects.setSelectObjects();
            stdoutAndPropertiesEdtior.setEditablePropertiesTable(false);
            setOperationButtonsEnabled(false);
        } else if (state == PAINTING) {
            statusBar.setText(messages.getString("state.title.painting", "Painting"));
            statusBar.cancelProgressBar();
            newGraph.setEnabled(true);
            saveGraph.setEnabled(true);
            cutGraph.setEnabled(true);
            loadGraph.setEnabled(true);
            
            executeGraph.setEnabled(true);
            stopExecuting.setEnabled(false);
            verifyGraph.setEnabled(true);
            printGraph.setEnabled(true);
            
            outputClear.setEnabled(true);
            outputExport.setEnabled(true);
            
            showPluginManager.setEnabled(true);
            showTypesStructure.setEnabled(true);
            
            for (Action action : languageChangeActions) {
                action.setEnabled(true);
            }
            
            for (Action action : styleChangeActions) {
                action.setEnabled(true);
            }
            
            stdoutAndPropertiesEdtior.setEditablePropertiesTable(true);
            setOperationButtonsEnabled(true);
        } else if (state == LOADING) {
            statusBar.setText(messages.getString("state.title.loading", "Loading"));
            newGraph.setEnabled(false);
            saveGraph.setEnabled(false);
            loadGraph.setEnabled(false);
            
            executeGraph.setEnabled(false);
            stopExecuting.setEnabled(false);
            verifyGraph.setEnabled(false);
            cutGraph.setEnabled(false);
            printGraph.setEnabled(false);
            
            outputClear.setEnabled(false);
            outputExport.setEnabled(false);
            
            showPluginManager.setEnabled(false);
            showTypesStructure.setEnabled(false);

            for (Action action : languageChangeActions) {
                action.setEnabled(false);
            }
            
            for (Action action : styleChangeActions) {
                action.setEnabled(false);
            }
            
            selectedObjects.setSelectObjects();
            stdoutAndPropertiesEdtior.setEditablePropertiesTable(false);
            setOperationButtonsEnabled(false);
        }
        this.state = state;
    }
    
    /**
     * Poskytne aktuální stav aplikace.
     * @return aktuální stav aplikace, buď {@code PAINTING} nebo {@code RUNNING}.
     */
    public int getDesprState() {
        return state;
    }
    
    public void setLastOpenGraph(File lastGraph) {
        if (lastGraph != null) {
            String fileName = lastGraph.getName();
            fileName = fileName.replace(".despr.zip", "");
            setTitle(String.format("Despr - %s", fileName));
            Despr.setLastOpenGraph(lastGraph);
        } else {
            setTitle(String.format("Despr"));
            Despr.setLastOpenGraph(lastGraph);
        }
    }
    
    /**
     * Poskytne odkaz na plátno grafu.
     * @return odkaz na plátno grafu.
     */
    public GraphCanvas getGraphCanvas() {
        return gCanvas;
    }
    
    /**
     * Poskytne odkaz na objekt, který umí zpracovat graf.
     * @return odkaz na objekt, který umí zpracovat graf.
     */
    public IGraphController getGraphController() {
        return gController;
    }

     /**
     * Poskytne odkaz na strom operací.
     * @return odkaz na strom operací.
     */
    public OperationsTree getOperationsTree() {
        return operationsTree;
    }
    
   /**
     * Spustí zpracování grafu.
     */
    public void runGraph() {
        showProgressBar(gController, RUNNING, null);
    }
    
    /**
	 * Spustí "spustitelnou" akci tak že se promítne průběh
	 * zpracování do status baru.
     * @param executable "spustitelný" modul.
     */
    public void showProgressBar(Executable executable, int state, MessageSupport msgSupport) {
        setDesprState(state);
        statusBar.setProgressBar(executable, msgSupport);
    }
    
    
    
    /**
     * Iniciuje všechny komponenty a akce definované v okně.
     */
    private void init() {
        List<Character> tabuMnemonics = new ArrayList<Character>(5);
        newGraph = new NewGraphAction(this, gCanvas);
        loadGraph = new LoadGraphAction(this, gCanvas);
        saveGraph = new SaveGraphAction(this, gCanvas);
        close = new CloseAction(this);
        
        executeGraph = new ExecuteGraphAction(this);
        stopExecuting = new StopExecutingAction(this);
        verifyGraph = new VerifyGraphAction(gController);
        printGraph = new PrintGraphAction(gCanvas);
        cutGraph = new CutGraphAction(gCanvas);
        resetGraph = new ResetGraphAction(gCanvas);
        
        JPanel rootPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 1, 1, 1);
        
        JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPnl.add(createActionPanel());
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        rootPanel.add(topPnl, gbc);
        
        JSplitPane middlePnl = getMiddlePanel();
        middlePnl.setOneTouchExpandable(true);
        middlePnl.setContinuousLayout(true);
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        rootPanel.add(middlePnl, gbc);
        
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5, 1, 0, 1);
        rootPanel.add(sep, gbc);
        
        statusBar = new StatusBar(this, messages.getString("state.title.painting", "Painting"));
        gbc.gridy = 3;
        gbc.insets = new Insets(1, 1, 1, 1);
        rootPanel.add(statusBar, gbc);
        
        showPluginManager = new ShowPluginManagerAction(this, operationsTree);
        showTypesStructure = new ShowPortTypesStructureAction(this);
        
        JMenuBar menu = new JMenuBar();
        String projectTitle = messages.getString("menu.title.project", "Project");
        Character projectMnemonic = MnemonicGenerator.getMnemonicChar(projectTitle, tabuMnemonics);
        menu.add(createJMenu(projectTitle, projectMnemonic, newGraph, loadGraph, saveGraph, close));
        
        String runTitle = messages.getString("menu.title.run", "Run");
        Character runMnemonic = MnemonicGenerator.getMnemonicChar(runTitle, tabuMnemonics);
        menu.add(createJMenu(runTitle, runMnemonic, executeGraph, stopExecuting, verifyGraph, resetGraph));
        
        String canvasTilte = messages.getString("menu.title.canvas", "Canvas");
        Character canvasMnemonic = MnemonicGenerator.getMnemonicChar(canvasTilte, tabuMnemonics);
        menu.add(createJMenu(canvasTilte, canvasMnemonic, cutGraph, printGraph));
        
        String toolsTitle = messages.getString("menu.title.tools", "Tools");
        Character toolsMnemonic = MnemonicGenerator.getMnemonicChar(toolsTitle, tabuMnemonics);
        menu.add(createJMenu(toolsTitle, toolsMnemonic, showPluginManager, showTypesStructure));
        
        String styleTitle = messages.getString("menu.title.style", "Style");
        Character styleMnenmonic = MnemonicGenerator.getMnemonicChar(styleTitle, tabuMnemonics);
        menu.add(createChooseTemplateMenu(styleTitle, styleMnenmonic));
        
        String languageTitle = messages.getString("menu.title.choose_language", "Language");
        Character languageMnemonic = MnemonicGenerator.getMnemonicChar(languageTitle, tabuMnemonics);
        menu.add(createChoseLanguageMenu(languageTitle, languageMnemonic));
        
        setJMenuBar(menu);
        add(rootPanel);
    }
    
    /**
     * Vytvoří menu s akčními tlačítky.
     * @return panel s akčními tlačítky.
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createQuickButtons(newGraph, loadGraph, saveGraph), gbc);
        
        gbc.gridx = 1;
        panel.add(createQuickButtons(executeGraph, stopExecuting), gbc);
        
        gbc.gridx = 2;
        panel.add(createQuickButtons(printGraph, cutGraph), gbc);
        
        // slider pro nastaveni barvy hrany
        JToolBar sliderBar = new JToolBar();
        sliderBar.setFloatable(false);
        sliderBar.addSeparator();
        sliderBar.setMargin(new Insets(2, 1, 1, 1));
        EdgeColorSlider edgeColorSlider = new EdgeColorSlider();
        selectedObjects.addSelectedObjectsChangeListener(edgeColorSlider);
        sliderBar.add(edgeColorSlider);
        
        gbc.gridx = 3;
        panel.add(sliderBar, gbc);
        
        return panel;
    }
    
    /**
     * Vytvoří menu pro výběr jazyka.
     * @param title jméno menu.
     * @param mnemonc pomocný znak
     * @return menu pro výběr jazyka.
     */
    private JMenu createChoseLanguageMenu(String title, Character mnemonc) {
        JMenu menu = new JMenu(title);
        if (mnemonc != null) {
            menu.setMnemonic(mnemonc);
        }
        Locale[] supportLocales = (Locale[]) Despr.getProperty(Despr.SUPPORT_LOCALES);
        Arrays.sort(supportLocales, new Comparator<Locale>() {

            @Override
            public int compare(Locale o1, Locale o2) {
                return o1.getDisplayLanguage().compareTo(o2.getDisplayLanguage());
            }
        });
        
        ButtonGroup group = new ButtonGroup();
        int size = supportLocales.length;
        languageChangeActions = new Action[size];
        for (int i = 0; i < size; i++) {
            Locale loc = supportLocales[i];
            JRadioButtonMenuItem rb = new JRadioButtonMenuItem(loc.getDisplayLanguage());
            if (loc.equals(Locale.getDefault())) {
                rb.setSelected(true);
            }
            
            Action action = new ChangeLanguageAction(loc);
            languageChangeActions[i] = action;
            rb.addActionListener(action);
            group.add(rb);
            menu.add(rb);
        }
        
        return menu;
    }
    
    /**
     * Vytvoří menu pro výběr vzhledu.
     * @param title jméno menu.
     * @param mnemonic pomocný znak.
     * @return menu pro výběr jazyka.
     */
    private JMenu createChooseTemplateMenu(String title, Character mnemonic) {
        JMenu menu = new JMenu(title);
        if (mnemonic != null) {
            menu.setMnemonic(mnemonic);
        }
        ButtonGroup group = new ButtonGroup();
        LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
        int size = lookAndFeels.length;
        styleChangeActions = new Action[size];
        for (int i = 0; i < size; i++) {
            LookAndFeelInfo laf = lookAndFeels[i];
            JRadioButtonMenuItem rb = new JRadioButtonMenuItem(laf.getName());
            if (UIManager.getLookAndFeel().getClass().getName().equals(laf.getClassName())) {
                rb.setSelected(true);
            }
            Action action = new ChangeStyleAction(laf.getClassName());
            styleChangeActions[i] = action;
            rb.addActionListener(action);
            group.add(rb);
            menu.add(rb);
        }
        return menu;
    }
    
    /**
     * Vytvoří střední (hlavní) část okna.
     */
    private JSplitPane getMiddlePanel() {
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // add tree of operations
        Category rootCategory = OperationsReader.operationsTree();
        operationsTree = new OperationsTree(new OperationsTreeModel(rootCategory));
        operationsTree.setCellRenderer(new OperationsTreeRenderer());
        splitPane.add(new JScrollPane(operationsTree));
        
        // add graph canvas
        splitPane.add(getGraphCanvasPanel());
        return splitPane;
    }

    /**
     * Nastaví pozici vertikální dělící hrany. 
     * @param pos pozice vertikální dělící hrany.
     */
    public void setSplitPaneDividerPosition(double pos) {
        splitPane.setDividerLocation(pos);
    }

    /**
     * Vytvoří pravou část hlavního okna.
     * @return komponentu s plátnem grafu a vstupně/výstupním panelem.
     */
    private JTabbedPane getGraphCanvasPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane2.setOneTouchExpandable(true);
        splitPane2.setContinuousLayout(true);
        
        tabbedPane.addTab(messages.getString("tab.title.graph", "Graph"), splitPane2);
        JScrollPane scrollGraphCanvas = new JScrollPane(gCanvas);
//        scrollGraphCanvas.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
        splitPane2.add(scrollGraphCanvas);
//        splitPane2.add(new JScrollPane(gCanvas));
        
        JTextPane stdout = DesprOut.getStdoutView();
        outputClear = new OutputClearAction(stdout);
        outputExport = new OutputExportAction(this, stdout);
        stdoutAndPropertiesEdtior = new InputOutputPanel(outputClear, outputExport, stdout);
        selectedObjects.addSelectedObjectsChangeListener(stdoutAndPropertiesEdtior);
        splitPane2.add(stdoutAndPropertiesEdtior);
        
        return tabbedPane;
    }
    
    /**
     * Nastaví pozici horizontální dělící linky mezi plátnem grafu a 
	 * vstupně/výstupním panelem.
     * @param pos pozice horizontální dělicí hrany.
     */
    public void setSplitPane2DividerPostion(double pos) {
        splitPane2.setDividerLocation(pos);
    }
    
    /**
     * Metoda vytvoří {@code JToolBar} s definovanými akcemi.
     * @param actions seznam akcí které mají být obsaženy 
	 * v {@code JToolBaru}.
     * @return vygenerovaný {@code JToolBar}.
     */
    private JToolBar createQuickButtons(Action... actions) {
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.addSeparator();
        JButton btn;
        for (Action a : actions) {
            btn = new JButton(a);
            toolBar.add(btn);
        }
        return toolBar;
    }
    
    /**
     * Vygeneruje položku v menu.
     * @param name Jméno položky.
     * @param mnemonic znak určující písmeno z názvu které 
	 * zpřístupní položku přes ALT.
     * @param actions seznam akcí které budou v menu obsaženy.
     * @return vygenerovanou položku do menu.
     */
    private JMenu createJMenu(String name, Character mnemonic, Action... actions) {
        JMenu menu = new JMenu(name);
        if (mnemonic != null) {
            menu.setMnemonic(mnemonic);
        }
        JMenuItem menuItem;
        List<Character> localTabuMnenomincs = new ArrayList<Character>();
        for (Action a : actions) {
            menuItem = new JMenuItem(a);
            Character actionMnemonic = MnemonicGenerator.getMnemonicChar(
                    (String) a.getValue(AbstractAction.NAME), localTabuMnenomincs);
            if (actionMnemonic != null) {
                menuItem.setMnemonic(actionMnemonic);
            }
            menu.add(menuItem);
        }
        return menu;
    }
    
    /**
     * Povolí čí zakáže použití tlačítek na operaci.
     * @param enabled mají být tlačítka na operacích povolena?
     */
    private void setOperationButtonsEnabled(boolean enabled) {
        Component[] graphComponets = gCanvas.getComponents();
        for (Component comp : graphComponets) {
            if (comp instanceof Operation) {
                Operation op = (Operation) comp;
                op.setEnableButtons(enabled);
            }
        }
    }
}