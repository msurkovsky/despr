
package cz.vsb.cs.sur096.despr;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Implementace class loaderu, který umí spolupracovat s jar balíky. Obsahuje
 * metodu k tomu aby mohli být přidávany rozšíření za běhu aplikace.
 * 
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/12/15:36
 */
public class DesprClassLoader {
    
    private static ClassLoader classLoader; 
    static {
        classLoader = createClassLoader();
    }

    /**
     * Poskytne odkaz na aktuální class loader.
     * @return odkaz na aktuální class loaderů
     */
    public static ClassLoader getClassLoader() {
        return classLoader;
    }
    
    /**
     * Přidá rozšíření do class loaderu.
     * @param plugin rozšíření které má být přidáno.
     * @throws IllegalArgumentException pokud by plugin nebyl jar balikem.
     */
    public static void addPlugin(File plugin) throws IllegalArgumentException {
        ((PlugableClassLoader) classLoader).addPackage(plugin);
    }
    
    public static void reloadPlugins() {
        classLoader = createClassLoader();
    }
    
    private DesprClassLoader() { }
    
    private static ClassLoader createClassLoader() {
        File extensions = new File("extensions");
        if (extensions.exists() && extensions.isDirectory()) {
            File[] plugins = extensions.listFiles(new JarFileFilter());
            int pluginsCount = plugins.length;
            URL[] urls = new URL[pluginsCount];
            try {
                for (int i = 0; i < pluginsCount; i++) {
                    File plugin = plugins[i];
                        URL pluginURL = plugin.toURI().toURL();
                        urls[i] = pluginURL;
                }
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
            return new PlugableClassLoader(urls);
        } else {
            return new PlugableClassLoader();
        }
    }
    
    private static class JarFileFilter implements FileFilter{

        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName().toLowerCase();
            if (name.endsWith(".jar")) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    /**
     * Rozšíření URL class loaderu který umožňuje přidat odkaz na soubor
     * s JAR balíkem.
     */
    private static class PlugableClassLoader extends URLClassLoader {
        
        /**
         * Iniciuje URL class loader s prázdným seznamem URL seznamem.
         */
        public PlugableClassLoader() {
            super(new URL[] {});
        }
        
        /**
         * Iniciuje classl loader se seznamem adres.
         * @param urls seznam adres.
         */
        public PlugableClassLoader(URL[] urls) {
            super(urls);
        }
        
        /**
         * Přidá balík do URL cest class loaderu.
         * @param f soubor reprezentující jar balík.
         * @throws IllegalArgumentException pokud by se nejednalo o jar balík.
         */
        public void addPackage(File f) throws IllegalArgumentException {
            if (f.exists() && f.getName().endsWith(".jar")) {
                try {
                    URL url = f.toURI().toURL();
                    addURL(url);
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                throw new IllegalArgumentException("Plugin must be a jar package!");
            }
        }
    }
}