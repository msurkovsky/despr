
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import cz.vsb.cs.sur096.despr.types.Copier;
import cz.vsb.cs.sur096.despr.types.Wrapper;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellEditor;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParameterCellRenderer;

/**
 * Definuje strukturu pro svázání konkrétního typu s konkrétními rozšířeními.
 * Každý typ lze svázat s definovanými typovými rozšířeními, které jsou:
 * <ul>
 *  <li>{@code Wrapper}</li>
 *  <li>{@code Copier}</li>
 *  <li>{@code ParameterCellRenderer}</li>
 *  <li>{@code ParameterCellEditor}</li>
 * </ul>
 *
 * Pokud je k jakémukoliv typy připojeno ať již jedno nebo všechny druhy 
 * rozšíření aplikace s nimi pak dále spolupracuje.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/17/17:32
 */
public class ExtensionsForType {
    
	/** Typ pro který se rozšíření vztahují.*/
    private Class type;
    
    private Class<? extends Wrapper> wrapper;
    private Class<? extends Copier> copier;
    private Class<? extends ParameterCellRenderer> renderer;
    private Class<? extends ParameterCellEditor> editor;
    
    /**
     * Iniciuje strukturu pro daný typ.
     * @param type typ ke kterému se rozšíření vztahují.
     */
    public ExtensionsForType(Class type) {
        this.type = type;
    }

    /**
     * Poskytne typ ke kterému se rozšíření vztahují.
     * @return typ ke kterému se rozšíření vztahují.
     */
    public Class getType() {
        return type;
    }

    /**
     * Poskytne wrapper k danému typu.
     * @return wrapper k danému typu.
     */
    public Class<? extends Wrapper> getWrapper() {
        return wrapper;
    }

    /**
     * Připojí wrapper k danému typu.
     * @param wrapper wrapper který má být připojen.
     */
    public void setWrapper(Class<? extends Wrapper> wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * Poskytne typ který umí zkopírovat hodnotu vázaného typu.
     * @return "kopírku" k danému typu.
     */
    public Class<? extends Copier> getCopier() {
        return copier;
    }

    /**
     * Nastaví "kopírku" k danému typu.
     * @param copier typ objektu který umí zkopírovat hodnotu
	 * vázaného typu.
     */
    public void setCopier(Class<? extends Copier> copier) {
        this.copier = copier;
    }

    /**
     * Poskytne renderer hodnoty vázaného typu.
     * @return renderer hodnoty vázaného typu.
     */
    public Class<? extends ParameterCellRenderer> getRenderer() {
        return renderer;
    }

    /**
     * Nastaví renderer hodnoty k danému typu.
     * @param renderer renderer hodnoty daného typu.
     */
    public void setRenderer(Class<? extends ParameterCellRenderer> renderer) {
        this.renderer = renderer;
    }

    /**
     * Poskytne editor hodnoty vázaného typu.
     * @return editor hodnoty vázaného typu.
     */
    public Class<? extends ParameterCellEditor> getEditor() {
        return editor;
    }

    /**
     * Nastaví editor hodnoty vázaného typu.
     * @param editor editor hodnoty vázaného typu.
     */
    public void setEditor(Class<? extends ParameterCellEditor> editor) {
        this.editor = editor;
    }
    
    /**
     * Poskytne textovou reprezentaci struktury.
     * @return textová reprezentace struktury.
     */
    @Override
    public String toString() {
        String typeName = type == null ? "" : type.getCanonicalName();
        String wrapperName = wrapper == null ? "" : wrapper.getCanonicalName();
        String copierName = copier == null ? "" : copier.getCanonicalName();
        String rendererName = renderer == null ? "" : renderer.getCanonicalName();
        String editorName = editor == null ? "" : editor.getCanonicalName();
        return String.format(
                "%s <-- (%s, %s, %s, %s)" + 
                typeName, wrapperName, copierName, rendererName, editorName);
    }
}