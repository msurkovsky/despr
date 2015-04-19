
package cz.vsb.cs.sur096.despr;

import cz.vsb.cs.sur096.despr.controller.GraphController;
import cz.vsb.cs.sur096.despr.controller.IGraphController;
import cz.vsb.cs.sur096.despr.controller.ProgressChangeListener;
import cz.vsb.cs.sur096.despr.controller.TimeLeft;
import cz.vsb.cs.sur096.despr.model.IGraph;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

/**
 * Prostředí pro spuštění grafu mimo grafické rozhraní.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com<a/>
 * @version 2012/04/17/18:43
 */
public class ConsoleRunner implements PropertyChangeListener {

    private LocalizeMessages messages;
    private IGraph model;
    private ConsoleTask cTask;
    private boolean end;
    
    /**
     * Iniciuje prostředí grafu pro konzoli.
     * @param model model grafu.
     */
    public ConsoleRunner(IGraph model) {
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        this.model = model;
        end = false;
        initTask();
    }
    
    private void initTask() {
        IGraphController controller = new GraphController(model);
        cTask = new ConsoleTask(controller);
        controller.addProgressChangeListener(cTask);
        cTask.addPropertyChangeListener(this);
    }
    
    /**
     * Reaguje na průběh zpracování grafu a stará se o vykreslení
     * konzolové verze progress baru.
     * @param evt událost vyvoláváající změnu.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            ProgressAndTime pt = (ProgressAndTime) evt.getNewValue();
            int progress = pt.getProgress(); 
            if (progress > 0) {
				
				int sizeOfProgressBar = progress / 2;
                if (progress < 100) {
                    System.out.printf("|%-50s| %4s   [%s]\r", 
                            getLine('=', sizeOfProgressBar) + ">", 
                            progress + "%",
                            pt.getTime());
                } else if (progress >= 100 && !end) {
                    System.out.printf("|%-50s| %4s\n\n", 
                            getLine('=', sizeOfProgressBar), 
                            progress + "%");
                    System.out.println(getLine('*', 100));
                    end = true; // uz nic nevypisuje, jen se ceka na par
                                // poslednich kousku ke zpracovani
                }
            }
        }
    }
    
    /**
     * Spustí graf v konzolovém režimu.
     */
    public void start() {
        System.out.printf("\n%s\n", messages.getString("title.welcome", "Welcome to the application: Desrp v1.0"));
        System.out.printf("%s\n", getLine('*', 100));
        System.out.printf("* %-97s*\n", messages.getString("title.graf_loaded", "You loaded this graph:"));
        System.out.printf("*%s*\n", getLine('-', 98));
        System.out.println(model.toString());
        System.out.printf("*%s*\n", getLine('-', 98));
 
        System.out.printf("%s\n", messages.getString("title.start_question", "Do you want to run this graph? [YES/no]"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if (br == null) {
            Despr.showError("No console", 
                    new Exception(messages.getString("exception.no_console", "No console!")),
                    Level.SEVERE, true);
        } else {
            try {
                String answer = br.readLine();
                String templateAnswer = messages.getString("question.positive.answer", "yes");
                if (answer.equals("") || answer.toLowerCase().equals(templateAnswer)) {
                    Thread t = new Thread(cTask);
                    t.start();
                }
            } catch (IOException ex) {
                Despr.showError("I/O excp", ex, Level.WARNING, false);
            } catch (Exception ex) {
                Despr.showError("General excpetion", ex, Level.WARNING, true);
            }
        }
   }
    
    
    private String getLine(char c, int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
    
    /**
     * Stará se o spouštění grafu v konzolli.
     */
    class ConsoleTask implements Runnable, ProgressChangeListener {

        private IGraphController cGraph;
        private int execLength;
        private double length;
        private TimeLeft timeLeft;
        private PropertyChangeSupport pcs;
        private ProgressAndTime progressAndTime;
        
        public ConsoleTask(IGraphController cGraph) {
            pcs = new PropertyChangeSupport(this);
            progressAndTime = new ProgressAndTime();
            this.cGraph = cGraph;
            execLength = cGraph.getLengthOfExecute();
            if (execLength > -1) {
                timeLeft = new TimeLeft((int) execLength);
                length = (double) execLength / 100;
            } else {
                Despr.showError("No finite root operation", new Exception(
                        messages.getString("exception.no_finite_root_op", "Don't exists any finite root operation!")), 
                        Level.SEVERE, false);
            }
        }
        
        @Override
        public void run() {
            try {
                cGraph.execute();
            } catch (Exception ex) {
                Despr.showError("Graph error", ex, Level.SEVERE, true);
            }
        }

        @Override
        public void progressChange(int progress, long oneCycleTime) {
            int realProgress;
            if (length == 0) {
                realProgress = 100;
            } else {
                realProgress = (int) (progress / length);
            }
            
            progressAndTime.setProgress(realProgress);
            timeLeft.addTime(oneCycleTime);
            progressAndTime.setTime(timeLeft.getTileLeft());
            pcs.firePropertyChange("progress", null, progressAndTime);
        }
        
        public void addPropertyChangeListener(PropertyChangeListener l) {
            pcs.addPropertyChangeListener(l);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener l) {
            pcs.removePropertyChangeListener(l);
        }
        
    }
    
    /**
     * Přepravka pro předaní času a prgoressu najednou.
     */
    class ProgressAndTime {
        private String time;
        private int progress;
        
        public ProgressAndTime() {
            time = "0";
            progress = 0;
        }
        
        public String getTime() {
            return time;
        }
        
        public void setTime(String time) {
            this.time = time;
        }
        
        public int getProgress() {
            return progress;
        }
        
        public void setProgress(int progress) {
            this.progress = progress;
        }
    }
}
