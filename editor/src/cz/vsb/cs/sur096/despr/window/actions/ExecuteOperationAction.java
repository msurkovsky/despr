package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.controller.Executable;
import cz.vsb.cs.sur096.despr.controller.ProgressChangeListener;
import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.events.MessageSupport;
import cz.vsb.cs.sur096.despr.model.IOperationModel;
import cz.vsb.cs.sur096.despr.window.DesprIcons;
import cz.vsb.cs.sur096.despr.window.HeadWindow;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

/**
 * Akce slouží pro spuštění zpracování jedné konkrétní operace.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/05/10:38
 */
public class ExecuteOperationAction extends BasicAbstractAction {

    /** Odkaz na model operace. */
    private IOperationModel opModel;
    
    /** Seznam posluchačů kteří se zajímají o zaslanou zprávu.*/
    private MessageSupport messageListeners;
    
    /**
     * Iniciuje akci.
     * @param opModel model operace. 
     */
    public ExecuteOperationAction(IOperationModel opModel) {
        this.opModel = opModel;
        messageListeners = new MessageSupport(this);
        
        putValue(LARGE_ICON_KEY, DesprIcons.getIcon(DesprIcons.PLAY_ICON, true));
    }
    
    /**
     *  Spustí zpracování operace, po skončení informuje posluchače.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        ExecuteOperation execOp = new ExecuteOperation(opModel);
        Despr.getHeadWindow().showProgressBar(execOp, HeadWindow.RUNNING, messageListeners);
    }
    
    /**
     * Přidá posluchače zajímajícího se o zasílané zprávy.
     * @param l posluchač.
     */
    public void addMessageListener(MessageListener l) {
        messageListeners.addMessageListener(l);
    }
    
    /**
     * Smaže posluchače zajímajícího se o zasílané zprávy.
     * @param l posluchač.
     */
    public void removeChangeListener(MessageListener l) {
        messageListeners.removeMessageListener(l);
    }
    
    
	/**
	 * Zabalením provedení operace do rozhraní {@code Executable}
	 * je možné jej předat jako úkol do hlavního okna které vizualizuje
	 * průběh pomocí progress baru.
	 */
    private class ExecuteOperation implements Executable {

        private IOperationModel opModel;
        public ExecuteOperation(IOperationModel opModel) {
            this.opModel = opModel;
        }
        
		/**
		 * Spustí zpracování operace.
		 */
        @Override
        public void execute() throws Exception {
            try {
               opModel.call();
            } catch (Exception ex) {
                Despr.showError(messages.getString("exception.title"), ex, Level.WARNING, true);
            }
        }

		/**
		 * Obecně leze říct jak dlouho bude operace trvat,
		 * proto se požije indeterminate progress bar který znázorní pouze 
		 * že se něco děje.
		 */
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
    }
}
