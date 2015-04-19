
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import javax.swing.JOptionPane;

/**
 * Utilitka která se zeptá na to zda má byt soubor uložen do stávajícího 
 * souboru nebo do nového souboru.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/06/01:02
 */
public final class ShowMessageUtil {
    
    /**
     * Zobrazí dialog s otázkou zda se má soubor uložit, do stávajícího
	 * nebo do nového souboru.
     * @return {@code JOptionPane.YES_OPTIO} pro možnost uložit jako,
	 * {@code JOptionPane.NO_OPTION} pro možnost zrušení a 
	 * {@code JOptionPane.CANCEL_OPTION} pro uložit do stávajícího souboru.
     */
    public static int showSaveQuestion() {
        return util.pShowSaveQuestion();
    }
    
    /**
     * Zobrazí dialog s otázkou zda se má restartovat aplikace.
     */
    public static void showRestartInfo() {
        util.pShowRestartInfo();
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome promene a metody
    
    private static ShowMessageUtil util;
    static {
        util = new ShowMessageUtil();
    }
    private transient LocalizeMessages messages;
    
    private ShowMessageUtil() {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }

    private int pShowSaveQuestion() {
        Object[] options = new Object[3];
        options[0] = messages.getString("button.saveas.title", "Save as");
        options[1] = messages.getString("button.cancel.title", "Cancel");
        options[2] = messages.getString("button.save.title", "Save");
        String message = messages.getString("dialog.question.save_or_save_as",
                "Do you want to save to the last file or as a new file?");
        String title = messages.getString("dialog.title.save_or_save_as",
                "Save or Save as");
        int result = JOptionPane.showOptionDialog(Despr.getHeadWindow(), 
                message, title, JOptionPane.YES_NO_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
        
        return result;
    }
    
    private void pShowRestartInfo() {
        String message = messages.getString("dialog.question.restart", "The changes take effect after restart application.");
        String title = messages.getString("dialog.title.restart_app", "Restart application");
        JOptionPane.showMessageDialog(Despr.getHeadWindow(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}