
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.utils.persistenceGraph.GraphPersistence;
import cz.vsb.cs.sur096.despr.utils.persistenceGraph.GraphSaver;
import cz.vsb.cs.sur096.despr.view.GraphCanvas;
import cz.vsb.cs.sur096.despr.window.DesprIcons;
import cz.vsb.cs.sur096.despr.window.HeadWindow;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

/**
 * Akce slouží pro uložení grafu do souboru.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/03/19:35
 */
public class SaveGraphAction extends BasicAbstractAction {

    private HeadWindow hw;
    private GraphCanvas gCanvas;
    private File currentDirectory;
    private File lastFile;
    
    /** 
     * Iniciuje akci.
     * @param gCanvas plátno grafu.
     */
    public SaveGraphAction(HeadWindow hw, GraphCanvas gCanvas) {
        
        super();
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        putValue(SMALL_ICON, DesprIcons.getIcon(DesprIcons.SAVE_GRAPH_ICON, true));
        putValue(LARGE_ICON_KEY, DesprIcons.getIcon(DesprIcons.SAVE_GRAPH_ICON, false));
        
        this.hw = hw;
        this.gCanvas = gCanvas;
        currentDirectory = new File(System.getProperty("user.home"));
    }
    
    /**
     * Uloží graf do souboru.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (lastFile == null) {
            lastFile = Despr.getLastOpenGraph();
        }
        
        int result;
        if (lastFile != null) {
            result = ShowMessageUtil.showSaveQuestion();
        } else {
            result = JOptionPane.YES_OPTION; // save new
        }
        
        if (result == JOptionPane.YES_OPTION) { // cancel option = save as
            JFileChooser fc = new JFileChooser(currentDirectory);
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
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile().getAbsoluteFile();
                if (!selectedFile.getName().endsWith("despr.zip")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".despr.zip");
                    lastFile = selectedFile;
                }
                currentDirectory = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();

                save(selectedFile);
                lastFile = selectedFile;
                hw.setLastOpenGraph(lastFile);
            }
        } else if (result == JOptionPane.CANCEL_OPTION) { // ok option = save
            save(lastFile);
        }
    }
    
    private void save(File f) {
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(f);
            ZipOutputStream zfo = new ZipOutputStream(fo);

            ByteArrayOutputStream modelStream  = GraphSaver.saveGraphModel(gCanvas.getModel());
            byte[] modelContent = modelStream.toByteArray();
            ZipEntry ze = new ZipEntry(GraphPersistence.MODEL_FILE);
            zfo.putNextEntry(ze);
            zfo.write(modelContent);
            zfo.closeEntry();
            modelStream.close();

            ByteArrayOutputStream viewStream = GraphSaver.saveGraphView(gCanvas);
            byte[] viewContent = ((ByteArrayOutputStream) viewStream).toByteArray();
            ze = new ZipEntry(GraphPersistence.VIEW_FILE);
            zfo.putNextEntry(ze);
            zfo.write(viewContent);
            zfo.closeEntry();
            viewStream.close();

            zfo.close();
            
            String message = messages.getString("dialog.message.graph_save_into", "Graph saved into file");
            JOptionPane.showMessageDialog(Despr.getHeadWindow(), String.format("%s '%s'.", message, f.getAbsoluteFile()));
            } catch (FileNotFoundException ex) {
                String title = messages.getString("title.file_not_found_excp",
                        "File not found");
                Despr.showError(title, ex, Level.WARNING, true);
            } catch (IOException ex) {
                String title = messages.getString("title.io_excp", "I/O problem");
                String message = String.format("%s '%s'", ex.getMessage(),
                        f.getAbsolutePath());
                Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
            } finally {
            try {
                if (fo != null) {
                    fo.close();
                }
            } catch (IOException ex) {
                String title = messages.getString("title.io_excp", "I/O problem");
                String message = String.format("%s '%s'", ex.getMessage(),
                        f.getAbsolutePath());
                Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
            }
        }
    }
}
