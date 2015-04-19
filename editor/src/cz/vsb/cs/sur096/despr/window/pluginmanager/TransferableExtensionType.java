
package cz.vsb.cs.sur096.despr.window.pluginmanager;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Definuje přenositelný objekt pro typ {@code ExtensionType}.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gamil.com">martin.surkovsky at gamil.com</a>
 * @version 2012/02/18/18:01
 */
public class TransferableExtensionType implements Transferable {

    /** Definice {@code DataFlavor} pro typp {@code ExtensionType}. */
    public static final DataFlavor typeDataFlavor;
    static {
        typeDataFlavor = new DataFlavor(
                String.format("%s;class=\"%s\"", 
                              DataFlavor.javaJVMLocalObjectMimeType,
                              ExtensionType.class.getName()),
                "Local transferable class");
    }
    
    private DataFlavor[] flavors;
    private ExtensionType transferType;
    
    /**
     * Iniciuje přenositelný objekt.
     * @param transferType objekt který má být přenesen.
     */
    public TransferableExtensionType(ExtensionType transferType) {
        this.transferType = transferType;
        flavors = new DataFlavor[] {typeDataFlavor};
    }
    
    /**
     * Poskytne seznam podporovaných {@code DataFlavor} objektů.
     * @return seznam podporovaných {@code DataFlavor} objektů.
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * Zjistí zda je daný {@code DataFlavor} podporován.
     * @param flavor {@code DataFlavor} pro který je vznesen dotaz.
     * @return {@code true} pokud je daný flavor v seznamu podporovaných,
	 * jinak {@code false}.
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Poskytne přenášená data.  
     * @param flavor {@code DatatFlavor} pro který mají být
	 * poskytnuty data.
     * @return přenášená data..
     * @throws UnsupportedFlavorException pokud dany flavor není podporován.
     */
    @Override
    public Object getTransferData(DataFlavor flavor) 
            throws UnsupportedFlavorException, IOException {
        
        if (isDataFlavorSupported(flavor)) {
            return transferType;
        }
        
        throw new UnsupportedFlavorException(flavor);
    }
}