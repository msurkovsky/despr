
package cz.vsb.cs.sur096.despr;

import cz.vsb.cs.sur096.despr.model.IGraph;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.utils.persistenceGraph.GraphLoader;
import cz.vsb.cs.sur096.despr.window.HeadWindow;
import java.awt.EventQueue;
import java.awt.SplashScreen;
import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Spouštecí třída celé aplikace.
 * 
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/08/31/18:14
 */
public class Run {
    
    private static LocalizeMessages messages;
    
    public static void main(String[] args) throws InterruptedException {

        messages = Despr.loadLocalizeMessages(Run.class, null, false);
        
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                String message = messages.getString("exception.unknown_excp", 
                        "It was caught some fail or error!\n"
                        + "More information you can find in the log file.");
                String title = messages.getString("title.unknown_excp", "Top level exception");
                if (e.getMessage() == null || e.getMessage().equals("")) {
                    Despr.showError(title, new Exception(message, e), Level.WARNING, true);
                } else {
                    Despr.showError(title, e, Level.WARNING, true);
                }
            }
        });
        
        Locale.setDefault((Locale) Despr.getProperty(Despr.SET_LOCALE));
        
        int argsCount = args.length;
        if (argsCount == 0) {
            final SplashScreen splash = SplashScreen.getSplashScreen();
            Thread.sleep(500);  // sekundu pocka at splash screen jen neproblikne
            
            graphicsMode();
            
            if (splash != null) {
                splash.close();
            }
        } else if (argsCount == 1) {
            consoleMode(args[0]);
        } else {
            Despr.setMode(Despr.CONSOLE_MODE);
            String message = messages.getString("exception.illegal_count_of_arguments",
                    "Unsupported count of input arguments!");
            throw new RuntimeException(message);
        }
    }
    
    private static void graphicsMode() throws InterruptedException {
        Despr.setMode(Despr.GRAPHICS_MODE);

        try {
            // Set System L&F
            String lookAndFeelName = (String) Despr.getProperty(Despr.STYLE);
            LookAndFeelInfo[] lookAndFeelsInfo = UIManager.getInstalledLookAndFeels();
            boolean ok = false;
            for (LookAndFeelInfo laf : lookAndFeelsInfo) {
                if (laf.getClassName().equals(lookAndFeelName)) {
                    ok = true;
                    break;
                }
            }
            if (ok) {
                UIManager.setLookAndFeel(lookAndFeelName);
            } else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                Despr.setProperty(Despr.STYLE, UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } 
        catch (UnsupportedLookAndFeelException e) {
            String title = messages.getString("title.unsupported_look_and_feel_excp",
                    "Unsupported look and feel");
            Despr.showError(title, e, Level.WARNING, true);
        }
        catch (ClassNotFoundException e) {
            String title = messages.getString("title.class_not_found_excp", "Class not found");
            Despr.showError(title, e, Level.WARNING, true);
        }
        catch (InstantiationException e) {
            String title = messages.getString("title.instantiation_excp", "Problem with instantiation");
            Despr.showError(title, e, Level.WARNING, true);
        }
        catch (IllegalAccessException e) {
            String title = messages.getString("title.illegal_access_excp", "Illegal access");
            Despr.showError(title, e, Level.WARNING, true);
        }
        
        Runnable runner = new Runnable() {

            @Override
            public void run() {
                HeadWindow headWindow = new HeadWindow();
                headWindow.setVisible(true);

                // tyto vlastnosti je treba nastavovat az po vykresleni okna
                headWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
                headWindow.setSplitPaneDividerPosition(0.3);
                headWindow.setSplitPane2DividerPostion(0.9);

                Despr.setHeadWindow(headWindow);
            }
        };
        EventQueue.invokeLater(runner);
    }
    
    private static void consoleMode(String graphPath) {
        Despr.setMode(Despr.CONSOLE_MODE);
        File f = new File(graphPath);
        if (f.exists() && f.getAbsolutePath().endsWith(".despr.zip")) {
            IGraph model = GraphLoader.loadGraphModel(f);
            ConsoleRunner cr = new ConsoleRunner(model);
            cr.start();
        } else {
            // error
            Despr.showError("Bad file", 
                    new Exception(messages.getString("exception.bad_file", 
                    "File doesn't exist or it's in unsupported format.")), 
                    Level.WARNING, false);
        }
        
    }
}