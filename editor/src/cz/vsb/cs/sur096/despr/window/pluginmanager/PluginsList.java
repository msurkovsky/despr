package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.DesprClassLoader;
import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.events.MessageSupport;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.utils.ExtensionsOfTypes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.*;

/**
 * Komponenta pro práci s celými rozšiřujícími balíky. Komponenta 
 * zobrazí seznam jmen importovaných balíků a umožní je smazat.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/15/18:25
 */
public class PluginsList extends JList {
    
    private transient LocalizeMessages messages;
    private DefaultListModel unusedOperationsList;
    private ExtensionsOfTypes typesListModel;
    private AvailableTypesListModel availableTypesList;
    private MessageSupport messageSupport;
    
    /**
     * Iniciuje seznam rozšíření.
     * @param unusedOperationsList seznam nepoužitých operací.
     * @param availableTypesList seznam dostupných typu.
     */
    public PluginsList(DefaultListModel unusedOperationsList, ExtensionsOfTypes typesListModel,
            AvailableTypesListModel availableTypesList) {
        
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        this.unusedOperationsList = unusedOperationsList;
        this.typesListModel = typesListModel;
        this.availableTypesList = availableTypesList;
        messageSupport = new MessageSupport(this);
        addMouseListener(new MenuItemAction());
    }
    
    /**
     * Přidá posluchače zajímajícího se o zasílané zprávy z komponenty.
     * @param l posluchač.
     */
    public void addMessageListener(MessageListener l) {
        messageSupport.addMessageListener(l);
    }
    
    /**
     * Smaže posluchače zajímajícího se o zasílané zprávy z komponenty.
     * @param l posluchač.
     */
    public void removeMessageListener(MessageListener l) {
        messageSupport.removeMessageListener(l);
    }
    
    /**
     * Pošlu registrovaným posluchačům zprávu.
     * @param message zpráva pro registrované posluchače.
     */
    public void sendMessage(String message) {
        messageSupport.sendMessage(message);
    }
    
    /**
     * Definuje akci vyvolávající popup menu na vybrané položce.
     */
    private class MenuItemAction extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showPopup(e);
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                JList list = (JList) e.getSource();
                int index = list.locationToIndex(e.getPoint());
                list.setSelectedIndex(index);
                JPopupMenu menu = new JPopupMenu();
                JMenuItem removeItem = new JMenuItem(messages.getString(
                        "popup.menu.title.remove", "Remove"));
                removeItem.addActionListener(new RemoveAction(index, list.getModel()));
                menu.add(removeItem);
                
                menu.show(list, e.getX(), e.getY());
            }
        }
        
        /**
         * Definuje akci pro smazání plug-inu. Pro to aby mohl být plugin
		 * korektně smazán všechny v něm definované operace se musí nacházet 
		 * v seznamu nepoužitých operací a všechny v něm definovaná typová 
		 * rozšíření nastavena jako nepoužitá. 
         */
        class RemoveAction implements ActionListener {
            
            int index;
            DefaultListModel listModel;
            
            /**
             * Iniciuje akci pro smazání rozšířujícího balíku.
             * @param index index vybrané položky.
             * @param listModel model seznamu s pluginy.
             */
            public RemoveAction(int index, ListModel listModel) {
                this.index = index;
                this.listModel = (DefaultListModel) listModel;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                File plugin = (File) listModel.get(index);
                Extension extension = PluginReader.loadExtension(plugin);
                
                // kontrola zda jsou vsechny z daneho baliku v seznamu nepouzitych
                // operaci, pak je mozne je snadno smazat
                List<Class> operations = extension.getOperations();
                List unusedOperations = new ArrayList();
                Enumeration unusedOperationsEnum = unusedOperationsList.elements();
                while (unusedOperationsEnum.hasMoreElements()) {
                    unusedOperations.add(unusedOperationsEnum.nextElement());
                }
                
                int operationsSize = operations.size();
                Object[] deleteOperations = new Object[operationsSize];
                List<Class> notFoundOperations = new ArrayList<Class>();
                
                for (int i = 0; i < operationsSize; i++) {
                    Class opCls = operations.get(i);
                    Object o = findObject(opCls, unusedOperations);
                    if (o != null) {
                        deleteOperations[i] = o;
                    } else {
                        notFoundOperations.add(opCls);
                    }
                }
                                
                List<Class> types = new ArrayList<Class>();
                types.addAll(extension.getWrappers());
                types.addAll(extension.getCopiers());
                types.addAll(extension.getParameterCellRenderers());
                types.addAll(extension.getParameterCellEditors());
                
                int typesSize = types.size();
                ExtensionType[] deleteTypes = new ExtensionType[typesSize];
                List<ExtensionType> usedTypes = new ArrayList<ExtensionType>();
                for (int i = 0; i < typesSize; i++) {
                    Class cls = types.get(i);
                    ExtensionType type = availableTypesList.getType(cls);
                    if (type != null) {
                        if (!type.isUsed()) {
                            deleteTypes[i] = type;
                        } else {
                            usedTypes.add(type);
                        }
                    }
                }
                
                List<Class> otherTypes = extension.getOtherTypes();
                List<Class> connectedTypes = typesListModel.getTypes();
                List<Class> usedConnectedTypes = new ArrayList<Class>();
                for (Class cls : otherTypes) {
                    Class connectedType = (Class) findObject(cls, connectedTypes);
                    if (connectedType != null) {
                        usedConnectedTypes.add(connectedType);
                    }
                }
                
                // pokud jsou v baliku operace, ktere nejsou v seznamu
                // nepouzitych operaci, pak neni mozne modul smazat a jsou
                // vypsany jsmena trid, jejich opereace nebyly nalezeny
                StringBuilder opSpec = new StringBuilder();
                if(!notFoundOperations.isEmpty()) {
                    for (Class cls : notFoundOperations) {
                        opSpec.append(
                                String.format(" - %s: %s.class\n",
                                plugin.getName(), cls.getCanonicalName().replace('.', '/')));
                    }
                }
                
                StringBuilder typesSpec = new StringBuilder();
                if (!usedTypes.isEmpty()) {
                    for (ExtensionType type : usedTypes) {
                        typesSpec.append(String.format(" - %s: %s.class\n",
                                plugin.getName(), 
                                type.getType().getCanonicalName().replace('.', '/')));
                    }
                }
                
                StringBuilder connectedTypesSpec = new StringBuilder();
                if (!usedConnectedTypes.isEmpty()) {
                    for (Class cls : usedConnectedTypes) {
                        connectedTypesSpec.append(String.format(" - %s: %s.class\n",
                                plugin.getName(), cls.getCanonicalName().replace('.', '/')));
                    }
                }
                
                boolean existsUsedOperation = !notFoundOperations.isEmpty();
                boolean existsUsedTypes = !usedTypes.isEmpty();
                boolean existsConnectedTypes = !usedConnectedTypes.isEmpty();
                
                String errMsg = messages.getString("exception.some_used_title", "Still used items!"); // still used items
                if (existsUsedOperation) {
                    errMsg = String.format("\n%s\n %s:\n%s", errMsg, 
                            messages.getString("exception.used_operations", "Operations"),
                            opSpec.toString());
                }
                
                if (existsUsedTypes) {
                    errMsg = String.format("\n%s\n %s:\n%s", errMsg, 
                            messages.getString("exception.used_types", "Types"),
                            typesSpec.toString());
                }
                
                if (existsConnectedTypes) {
                    errMsg = String.format("\n%s\n %s:\n%s", errMsg, 
                            messages.getString("exception.used_connected_types", 
                                               "Connected types"),
                            connectedTypesSpec.toString());
                }
                
                if (existsUsedOperation || existsUsedTypes || existsConnectedTypes) {
                    JOptionPane.showMessageDialog(null, errMsg, messages.getString(
                            "title.plugin_cannot_be_delete", "Plug-in can not be delete"), 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                    
                // je mozne operace smazat
                for (Object o : deleteOperations) {
                    unusedOperationsList.removeElement(o);
                }
                
                for (ExtensionType type : deleteTypes) {
                    availableTypesList.removeElement(type);
                }
                
                // nakonec je odstranen cely balicek
                if (!plugin.delete()) {
                    String title = String.format(messages.getString(
                            "title.plugin_deleting_problem", "Plug-in deleting problem"));
                    String message = String.format(messages.getString(
                            "exception.plugin_was_not_deleted",
                            "Plug-in '%s' was not deleted!"), plugin.getName());
                    Despr.showInfo(title, message);
                }
                listModel.remove(index);
                DesprClassLoader.reloadPlugins();
                messageSupport.sendMessage("plugins_has_changed");
            }
            
            private Object findObject(Class opCls, List operations) {
                for (Object o : operations) {
                    Class cls;
                    if (o instanceof Class) {
                        cls = (Class) o;
                    } else {
                        cls = o.getClass();
                    }
                    
                    if (equalClasses(cls, opCls)) {
                        return o;
                    }
                }
                return null;
            }
            
            // pokud se tridy porovnavaji jako cls1.equals(cls2) tak to na 
            // stejne tridy nevrati vzdy true, coz je divne. Proto se porovnavaji
            // tridy podle canonickych jmen!
            private boolean equalClasses(Class cls1, Class cls2) {
                String s1 = cls1.getCanonicalName();
                String s2 = cls2.getCanonicalName();
                return s1.equals(s2);
            }
        }
    }
}
