
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.window.HeadWindow;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Akce slouží pro uložení standardního výstupu do textového souboru.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/29/16:42
 */
public class OutputExportAction extends BasicAbstractAction {

    private JTextPane stdout;
    private HeadWindow hw;
    private File lastFile, currentDirectory;
    
    /**
     * Iniciace akce.
     * @param hw odkaz na hlavní okno.
     * @param stdout odkaz na textový panel se standardním výstupem.
     */
    public OutputExportAction(HeadWindow hw, JTextPane stdout) {
        this.hw = hw;
        this.stdout = stdout;
        currentDirectory = new File(System.getProperty("user.home"));
    }
    
    /**
     * Provede export obsahu standardního výstupu do souboru.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Document doc = stdout.getDocument();

        int result;
        if (lastFile != null) {
            result = ShowMessageUtil.showSaveQuestion();
        } else {
            result = JOptionPane.YES_OPTION;
        }
        if (result == JOptionPane.YES_OPTION) { // cancel option = save as
            JFileChooser fc = new JFileChooser(currentDirectory);
            fc.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    if (f.isDirectory() || f.getName().endsWith("despr.output")) {
                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public String getDescription() {
                    return "Despr output";
                }
            });
            if (fc.showSaveDialog(hw) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile().getAbsoluteFile();
                if (!selectedFile.getName().endsWith("despr.output")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".despr.output");
                    lastFile = selectedFile;
                }
                currentDirectory = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();

                save(doc, selectedFile);
                lastFile = selectedFile;
            }
        } else if (result == JOptionPane.CANCEL_OPTION) { // ok option = save
            save(doc, lastFile);
        }
    }
    
    private void save (Document doc, File f) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(doc.getText(0, doc.getLength()));
            bw.close();
        } catch (BadLocationException ex) {
            String title = messages.getString("title.bad_localiton_excp", "Bad location");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (FileNotFoundException ex) {
            String title = messages.getString("title.file_not_found_excp",
                    "File not found");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = messages.getString("title.io_excp", "I/O problem");
            String message = String.format("%s '%s'", ex.getMessage(),
                    f.getAbsolutePath());
            Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
        }
    }
}
