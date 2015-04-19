
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.DesprClassLoader;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.types.Copier;
import cz.vsb.cs.sur096.despr.types.Wrapper;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

/**
 * Nástroj sloužící pro načtení rozšíření z JAR balíku.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/16/08:22
 */
public final class PluginReader {

    /**
     * Načte a vytvoří s JAR balíku rozšíření se kterým umí spolupracovat
	 * aplikace.
	 * @return rozšíření se kterým umí spolupracovat aplikace.
     */
    public static Extension loadExtension(File jarFile) {
        return pluginReader.pLoadExtension(jarFile);
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome promene a metody
    
    private static PluginReader pluginReader;
    static {
        pluginReader = new PluginReader();
    }
    
    private transient LocalizeMessages messages;
    
    private PluginReader() { 
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
    }
    
    private Extension pLoadExtension(File jarFile) {
        Extension ext = new Extension();
        
        try {
            URL url = jarFile.toURI().toURL();
            URL u = new URL("jar", "", url + "!/");

            // these two lines can throw IOExceptio
            JarURLConnection uc = (JarURLConnection) u.openConnection();
            JarFile jarfile = uc.getJarFile();

            Enumeration<JarEntry> entries = jarfile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                // Vyberou sobory trid
                if (name.endsWith(".class")) {
                    // odstrani se pripona
                    String className = name.replaceAll("\\.class$", "");
                    // slash se nahradi teckou, tak className reprezuntuje
                    // korektni pojmenovani tridy v jave.
                    className = className.replace('/', '.');

                    try {
                        // Trdia se nacte
                        Class c = Class.forName(className, true, DesprClassLoader.getClassLoader());
                        if (Modifier.isAbstract(c.getModifiers())) {
                            continue;
                        }
                        // vyberou se zajimave objekty, takove
                        // ktere se zaitegruji do aplikace
                        if (IOperation.class.isAssignableFrom(c)) {
                            ext.add(IOperation.class, c);
                        } else if (Copier.class.isAssignableFrom(c)) {
                            ext.add(Copier.class, c);
                        } else if (Wrapper.class.isAssignableFrom(c)) {
                            ext.add(Wrapper.class, c);
                        } else if (ParameterCellRenderer.class.isAssignableFrom(c)) {
                            ext.add(ParameterCellRenderer.class, c);
                        } else if (ParameterCellEditor.class.isAssignableFrom(c)) {
                            ext.add(ParameterCellEditor.class, c);
                        } else {
                            ext.addAnotherType(c);
                        }
                        
                    } catch (ClassNotFoundException ex) {
                        // toto by nemelo nastat
                        String title = messages.getString("title.class_not_found_excp",
                                "Class not found");
                        Despr.showError(title, ex, Level.WARNING, true);
                    }
                }
            }
        } catch (MalformedURLException ex) {
            String title = messages.getString("title.malformed_url_excp",
                    "Malformed URL");
            Despr.showError(title, ex, Level.WARNING, true);
        } catch (IOException ex) {
            String title = messages.getString("title.io_excp", "I/O problem");
            Despr.showError(title, ex, Level.WARNING, true);
        }
        
        return ext;
    }
}
