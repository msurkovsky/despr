package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.controller.Executable;
import cz.vsb.cs.sur096.despr.controller.ProgressChangeListener;
import cz.vsb.cs.sur096.despr.model.IGraph;
import cz.vsb.cs.sur096.despr.structures.Pair;
import cz.vsb.cs.sur096.despr.utils.ID;
import cz.vsb.cs.sur096.despr.utils.persistenceGraph.GraphLoader;
import cz.vsb.cs.sur096.despr.view.Edge;
import cz.vsb.cs.sur096.despr.view.GraphCanvas;
import cz.vsb.cs.sur096.despr.view.Operation;
import cz.vsb.cs.sur096.despr.window.DesprIcons;
import cz.vsb.cs.sur096.despr.window.HeadWindow;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/**
 * Akce sloužící pro načtení grafu z uloženého souboru.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/02/20:27
 */
public class LoadGraphAction extends BasicAbstractAction {

    private HeadWindow hw;
    private GraphCanvas gCanvas;
    private JFileChooser fc;
    
    /**
     * Iniciuje akci.
     * @param hw odkaz na hlavní okno.
     * @param gCanvas odkaz na plátno grafu.
     */
    public LoadGraphAction(HeadWindow hw, GraphCanvas gCanvas) {
        
        super();
        this.hw = hw;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        putValue(SMALL_ICON, DesprIcons.getIcon(DesprIcons.LOAD_GRAPH_ICON, true));
        putValue(LARGE_ICON_KEY, DesprIcons.getIcon(DesprIcons.LOAD_GRAPH_ICON, false));
        this.gCanvas = gCanvas;
        fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory() || f.getName().endsWith("despr.zip")) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "Despr graph";
            }
        });
    }
    
    /**
     * Načte graf ze souboru.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        int returnVal = fc.showOpenDialog(gCanvas);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            Executable exec = new Executable() {

                @Override
                public void execute() throws Exception {
                    File inputFile = fc.getSelectedFile().getAbsoluteFile();
                    ID.resetIds();
                    IGraph model = GraphLoader.loadGraphModel(inputFile);
                    final Pair<List<Operation>, List<Edge>>  g = GraphLoader.loadGraphView(inputFile, model);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            gCanvas.loadComponents(g.getFirst(), g.getSecond());
                        }
                    });
                    hw.setLastOpenGraph(inputFile);
                }

                @Override
                public int getLengthOfExecute() {
                    return -1;
                }

                @Override
                public void addProgressChangeListener(ProgressChangeListener l) {
                }

                @Override
                public void removeProgressChangeListener(ProgressChangeListener l) {
                }
            };
            hw.showProgressBar(exec, HeadWindow.LOADING, null);
        }
    }
}
