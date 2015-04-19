
package cz.vsb.cs.sur096.despr.view;

import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * Implementace rozhrani Trasferable, ktera zabaluje a umoznuje prenaset
 * zakladni (base) iplmentaci operace, dodanou uzivatelem. Nejedna se o mezivrstvu
 * ({@code OperationModel}).
 * 
 * @author Martin Surkovsky, sur096 <martin.surkovsky at gmail.com>
 * @version 2011/08/24/16:25
 */
public class TransferableOperations implements Transferable {
    
    /**
     * Definice {@code DataFlavor} pro pole typů {@code IOperation}.
     */
    public static final DataFlavor operationsLocalFlavor = new DataFlavor(
            String.format("%s;class=\"%s\"", 
                          DataFlavor.javaJVMLocalObjectMimeType,
                          IOperation[].class.getName()),
            "Local transferable operations");
    
    private DataFlavor[] flavors = {operationsLocalFlavor};
    
    private IOperation[] operations;
    
    /**
     * Iniciuje přenositelný objekt s polem operací.
     * @param operations seznam operací.
     */
    public TransferableOperations(IOperation[] operations) {
        this.operations = operations;
    }
    
    /**
     * Poskytne seznam podporovaných objektů.
     * @return pouze pole s typem {@code operationsLocalFlavo}.
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * Zjistí zda je daný typ podporován.
     * @param flavor typ {@code DataFlavor}.
     * @return {@code true} pokud bude daný typ nalezen
	 * v seznamu, jinak {@code false}.
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (int i = 0; i < flavors.length; i++) {
            if (flavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Poskytne původní zabalená data.
     * @param flavor typ dat který by měl být poskytnut.
     * @return zabalená data.
     * @throws UnsupportedFlavorException pokud dat není typ podporován.
     */
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (isDataFlavorSupported(flavor)) {
            return operations;
        }
        
        throw new UnsupportedFlavorException(flavor);
    }   
}
