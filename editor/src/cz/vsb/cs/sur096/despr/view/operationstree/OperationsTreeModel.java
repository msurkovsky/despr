
package cz.vsb.cs.sur096.despr.view.operationstree;

import javax.swing.tree.DefaultTreeModel;

/**
 * Implementace modelu stromu operací. V tomto případě se spíše jedná pouze o 
 * typovou definici. Model dědí z {@code DefaultTreeModel} a veškerou praci
 * nechává na něm.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/20/16:41
 */
public class OperationsTreeModel extends DefaultTreeModel {
    
    /**
     * Iniciuje model stromu.
     * @param root  model stromu.
     */
    public OperationsTreeModel(Category root) {
        super(root);
    }
    
    /**
     * Poskytne kořenovou operaci.
     * @return kořenovou operaci.
     */
    @Override
    public Category getRoot() {
        return (Category) super.getRoot();
    }
}
