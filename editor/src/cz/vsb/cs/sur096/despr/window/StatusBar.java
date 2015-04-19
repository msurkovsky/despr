
package cz.vsb.cs.sur096.despr.window;

import cz.vsb.cs.sur096.despr.controller.Executable;
import cz.vsb.cs.sur096.despr.controller.ProgressChangeListener;
import cz.vsb.cs.sur096.despr.controller.TimeLeft;
import cz.vsb.cs.sur096.despr.events.MessageSupport;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * Panel se status barem indikující progress průběhu zpracování 
 * {@code Executable} komponent.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/28/12:44
 */
public class StatusBar extends JPanel implements PropertyChangeListener {
    
    private JLabel status;
    private JLabel txtTimeLeft;
    private JProgressBar progressBar;
    private Executable executable;
    private Task task;
    private HeadWindow hw;
    private GridBagConstraints gbc;
    
    /**
     * Iniciuje panel s progress barem.
     * @param hw odkaz na hlavní panel.
     * @param defaultStatus jméno defaultního statutu.
     */
    public StatusBar(HeadWindow hw, String defaultStatus) {
        this.hw = hw;
        setLayout(new GridBagLayout());
        setMinimumSize(new Dimension(0, 30));
        gbc = new GridBagConstraints();
        status = new JLabel(defaultStatus);
        
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(status, gbc);
    }
    
    /**
     * Nastaví progres bar pro danou spustitelnou komponentu.
     * @param executable spustitelná komponenta.
     */
    public void setProgressBar(Executable executable, MessageSupport msgSupport) {
        this.executable = executable;
        task = new Task(executable, msgSupport, hw);
        task.addPropertyChangeListener(this);
        executable.addProgressChangeListener(task);
        
        txtTimeLeft = new JLabel();
        gbc.gridx = 1;
        gbc.weightx = 0.0;
        add(txtTimeLeft, gbc);
        
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(250, 18));
        progressBar.setStringPainted(true);
        if (executable.getLengthOfExecute() == -1) {
            progressBar.setIndeterminate(true);
            progressBar.setStringPainted(false);
        }
        gbc.gridx = 2;
        add(progressBar, gbc);
        revalidate();
        
        task.execute();
    }

    /**
     * Reaguje na změnu prgoressu a změnu času zbývajícího času
	 * ke zpracování.
     * @param evt událost.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } else if (evt.getPropertyName().equals("time_left")) {
            txtTimeLeft.setText(evt.getNewValue().toString());
        }
    }
    
    /**
     * Nastaví jméno statutu ve kterém se aplikace nachází.
     * @param text jméno statutu.
     */
    public void setText(String text) {
        status.setText(text);
        revalidate();
    }
    
    /**
     * Zruší progress bar.
     */
    public void cancelProgressBar() {
        if (progressBar != null && txtTimeLeft != null) {
            remove(progressBar);
            remove(txtTimeLeft);
            revalidate();
            repaint();
            executable.removeProgressChangeListener(task);
        }
    }
    
    /**
     * Implementace úkolu který komunikuje s progress barem a poskytuje informace o 
     * stavu zpracování.
     */
    class Task extends SwingWorker<Void, Void> implements ProgressChangeListener {

        private HeadWindow hw;

        /** Spustitelná komponenta. */
        private Executable executable;
        /** Zbývající čas ke zpracování. */
        private TimeLeft timeLeft;
        /** Celková počet zpracovávaných cyklů. */
        private int executeLength;
        private double length;

        private MessageSupport msgSupport;

        /**
        * Iniciuje úkol se spustitelnou komponentou.
        * @param executable spustitelná komponenta.
        */
        public Task(Executable executable, MessageSupport msgSupport, HeadWindow hw) {
            this.hw = hw;
            this.msgSupport = msgSupport;
            this.executable = executable;
            executeLength = executable.getLengthOfExecute();
            if (executeLength > -1) {
                timeLeft = new TimeLeft(executeLength);
                length = (double) executeLength / 100; // length convert to range 0 to 100;
            }
        }

        public Task(Executable executable) {
            this(executable, null, null);
        }

        /**
        * Spustí komponentu na pozadí.
        * @return {@code null}.
        * @throws Exception pokud nastane chyba v průběhu zpracování komponenty.
        */
        @Override
        protected Void doInBackground() throws Exception {
            executable.execute();
            return null;
        }

        /**
        * Reaguje na ukončení tasku.
        */
        @Override
        public void done() {
            if (hw != null) {
                hw.setDesprState(HeadWindow.PAINTING);
            }

            if (msgSupport != null) {
                msgSupport.sendMessage("done");
            }
        }

        /**
        * Reaguje na změnu progressu spuštěného úkolu.
        * @param progress jak daleko je spuštěna komponenta ve zpracování.
        * @param oneCycleTime jak dlouho trvala poslední iterace.
        */
        @Override
        public void progressChange(int progress, long oneCycleTime) {
            int realProgress;
            if (length == 0) {
                realProgress = 100;
            } else {
                realProgress = (int) (progress / length);
            }

            if (realProgress <= 100) {
                setProgress(realProgress);
            } else {
                setProgress(100);
            }

            timeLeft.addTime(oneCycleTime);
            getPropertyChangeSupport().firePropertyChange(
                    "time_left", null, timeLeft.getTileLeft());
        }
    }
}