
package cz.vsb.cs.sur096.despr.window.actions;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.DesprClassLoader;
import cz.vsb.cs.sur096.despr.gui.components.FilesSelector;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.window.pluginmanager.*;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.logging.Level;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * Akce importuje balík s rozšířeními do aplikace.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/15/13:49
 */
public class ImportPackageAction extends BasicAbstractAction implements PropertyChangeListener {

    private Window window;
    private File jarPackage;
    private PluginsList pluginsList;
    private DefaultListModel pluginListModel, unusedOperationsListModel;
    private AvailableTypesListModel typesListModel;
    private FilesSelector filesSelector;
    
    /**
     * Iniciuje akci.
     * @param window okno ve kterém byla akce vyvolána.
     * @param pluginsList odkaz na seznam pluginů.
     * @param unusedOperationsListModel odkaz na seznam nepoužitých operací.
     * @param typesListModel odkaz na seznam připojených typů.
     */
    public ImportPackageAction(Window window, PluginsList pluginsList,
            DefaultListModel unusedOperationsListModel, 
            AvailableTypesListModel typesListModel) {
        
        this.window = window;
        this.pluginsList = pluginsList;
        this.pluginListModel = (DefaultListModel) pluginsList.getModel();
        this.unusedOperationsListModel = unusedOperationsListModel;
        this.typesListModel = typesListModel;
    }
    
    /**
     * Provede import balíku do aplikace.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (jarPackage != null && pluginListModel != null) {
            String importQuestionMessage = messages.getString(
                    "dialog.message.import_question", "Do you want to import '%s'?");
            String importQuestionTitle = messages.getString(
                    "dialog.title.import_question", "Import package?");
            int result = JOptionPane.showConfirmDialog(window, 
                                String.format(importQuestionMessage, jarPackage.getName()), 
                                importQuestionTitle, JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // check importing jar dependences
                    URL url = jarPackage.toURI().toURL();
                    URL u = new URL("jar", "", url + "!/");
                    
                    JarURLConnection uc = (JarURLConnection) u.openConnection();
                    Attributes attr = uc.getMainAttributes();
                    if (attr != null) {
                        String classPath = attr.getValue(Attributes.Name.CLASS_PATH);
                        StringTokenizer tokenizer = new StringTokenizer(classPath, " ");
                        List<String> dependsPaths = new ArrayList<String>(5);
                        while (tokenizer.hasMoreTokens()) {
                            String path = tokenizer.nextToken();
                            File dependsPackage = new File(path);
                            if (!dependsPackage.exists()) {
                                dependsPaths.add(path);
                            }
                        }
                        
                        if (!dependsPaths.isEmpty()) {
                            StringBuilder dependsPackages = new StringBuilder();
                            for (String dependsPackage : dependsPaths) {
                                dependsPackages.append(String.format(" - %s\n", 
                                                       dependsPackage));
                            }
                            String dependsPackagesMessage = messages.getString(
                                    "dialog.message.depends_packages",
                                    "Package '%s' can not be import"
                                    + " because it depends on following packages:\n%s"
                                    + "You have to copy these packages into directory"
                                    + " '%s' as the first.");
                            String dependsPackagesTitle = messages.getString(
                                    "dialog.title.depends_packages", "Dependent packages");
                            JOptionPane.showMessageDialog(window,
                                    String.format(dependsPackagesMessage,
                                    jarPackage.getName(), dependsPackages, 
                                    new File("lib").getAbsolutePath()), 
                                    dependsPackagesTitle, JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                } catch (MalformedURLException ex) {
                    String title = messages.getString("title.malformed_url_excp",
                            "Malformed URL");
                    Despr.showError(title,ex, Level.WARNING, true);
                } catch (IOException ex) {
                    String title = messages.getString("title.io_excp", "I/O problem");
                    String message = String.format("%s '%s'", ex.getMessage(),
                            jarPackage.getAbsolutePath());
                    Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
                }
                
                // import
                File extensionsDir = new File("extensions");
                if (!extensionsDir.exists() || !extensionsDir.isDirectory()) {
                    if (!extensionsDir.mkdir()) {
                        String message = messages.getString("exception.it_cannot_create_directory",
                                "The '%s' directory does not exists and it can not be created\n. "
                                + "Please you try create the directory manually and add plug-in again.");
                        throw new RuntimeException(String.format(message, extensionsDir.getAbsolutePath()));
                    }
                }
                
                File destFile = new File(extensionsDir.getName() + 
                        File.separator + jarPackage.getName());

                // pokud se knihnovna nenachazi v baliku s ostatnimi rozsirenimi
                // prekopiruje se.
                if (destFile.exists()) {
                    String message = messages.getString(
                            "dialog.message.package_with_the_same_name",
                            "Package with the same name '%s' exists! It will not be added.");
                    String title = messages.getString(
                            "dialog.title.package_with_the_same_name",
                            "Package already exists!");
                    
                    JOptionPane.showMessageDialog(window, 
                            String.format(message, destFile.getName()), 
                            title, JOptionPane.WARNING_MESSAGE);
                    return;
                }

                FileChannel source = null, destination = null;
                try {
                    source = new FileInputStream(jarPackage).getChannel();
                    destination = new FileOutputStream(destFile).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    pluginListModel.addElement(destFile);
                    pluginsList.sendMessage("plugins_has_changed");
                } catch (FileNotFoundException ex) {
                    String title = messages.getString("title.file_not_found_excp",
                            "File not found");
                    Despr.showError(title, ex, Level.WARNING, true);
                } catch (IOException ex) {
                    String title = messages.getString("title.io_excp", "I/O problem");
                    String message = String.format("%s '%s'", ex.getMessage(),
                            jarPackage.getAbsolutePath());
                    Despr.showError(title, new IOException(message, ex), Level.WARNING, true);
                } finally {
                    try {
                        if (source != null) {
                            source.close();
                        }

                        if (destination != null) {
                            destination.close();
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                
                DesprClassLoader.addPlugin(destFile);
                Extension ext = PluginReader.loadExtension(destFile);
                List<Class> operations = ext.getOperations();
                StringBuilder addedOperations = new StringBuilder();
                
                for (Class opClass : operations) {
                    
                    try {
                        IOperation op = (IOperation) opClass.newInstance();
                        unusedOperationsListModel.addElement(op);
                        String opName = op.getLocalizeMessage("name");
                        if (opName == null) {
                            opName = op.getClass().getSimpleName();
                        }
                        opName = opName.replaceAll("\\n", " ");
                        addedOperations.append(String.format("- %s\n", opName));
                    } catch (InstantiationException ex) {
                        String title = messages.getString("title.instantiation_excp",
                                "Problem with create new instance");
                        Despr.showError(title, ex, Level.WARNING, true);
                    } catch (IllegalAccessException ex) {
                        String title = messages.getString("title.illegal_access",
                                "Illegal access");
                        Despr.showError(title, ex, Level.WARNING, true);
                    }
                }
                
                List<Class> typesExtensions = new ArrayList<Class>();
                typesExtensions.addAll(ext.getWrappers());
                typesExtensions.addAll(ext.getCopiers());
                typesExtensions.addAll(ext.getParameterCellRenderers());
                typesExtensions.addAll(ext.getParameterCellEditors());
                StringBuilder addedTypes = new StringBuilder();
                for (Class typeExtension : typesExtensions) {
                    ExtensionType type = new ExtensionType(typeExtension);
                    typesListModel.addElement(type);
                    addedTypes.append(String.format(" - %s\n", 
                            type.getType().getCanonicalName()));
                }
                
                String finalMessage = String.format(messages.getString(
                        "dialog.message.import_done", 
                        "Package '%s' was imported into extensions."),
                        jarPackage.getName());
                if (!addedOperations.toString().equals("")) {
                    finalMessage += String.format("\n %s: \n%s", messages.getString(
                            "dialog.message.added_operations", "Added operations"),
                            addedOperations.toString());
                }
                
                if (!addedTypes.toString().equals("")) {
                    finalMessage += String.format("\n %s: \n%s", messages.getString(
                            "dialog.message.added_types_exctensions", 
                            "Added extensions of types"), 
                            addedTypes.toString());
                }
                        
                JOptionPane.showMessageDialog(window, finalMessage,
                        messages.getString("dialog.title.import_done", "Import done"),
                        JOptionPane.INFORMATION_MESSAGE);
                
                // delete from input
                jarPackage = null;
                setEnabled(false);
                if (filesSelector != null) {
                    filesSelector.setFile(null);
                }
            }
        }
    }

    /**
     * Reaguje na změnu vybraného souboru.
     * @param evt 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("lastOpenFile")) {
            jarPackage = (File) evt.getNewValue();
            setEnabled(true);
            filesSelector = (FilesSelector) evt.getSource();
        }
    }
}