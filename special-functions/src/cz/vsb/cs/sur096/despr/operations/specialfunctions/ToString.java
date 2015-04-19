
package cz.vsb.cs.sur096.despr.operations.specialfunctions;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;

/**
 * Operace slouží ke stejné funkci jako metoda {@code toString} v jave.
 * Vezme jakýkoliv objekt zavolá na něj metodu {@code toString} a výsledek
 * vrátí.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/07/18:19
 */
public class ToString extends AbstractSpecialOperation {
    
    @AInputParameter(EInputParameterType.OUTER)
    private Object input;
    
    @AOutputParameter
    private String output;
    
    /**
     * Zavolá na objekt metodu {@code toString}.
     * @throws Exception jedině pokud by vstupní objekt byl
	 * nulový nebo pokud by objekt metoda {@code toString} 
	 * daného objektu vyhodila nějakou výjimku.
     */
    @Override
    public void execute() throws Exception {
        
        checkInput(input);
        output = input.toString();
    }

    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Poskytne vstupní objekt.
     * @return vstupní objekt.
     */
    public Object getInput() {
        return input;
    }
    
    /**
     * Nastaví vstupní objekt.
     * @param input vstupní objekt.
     * @throws NullPointerException pokud je vstupní objekt
	 * prázdný.
     */
    public void setInput(Object input) 
            throws NullPointerException {
        
        checkInput(input);
        this.input = input;
    }
    
    /**
     * Poskytne textovou reprezentaci objektu.
     * @return textová reprezentace objektu.
     */
    public String getOutput() {
        return output;
    }
    
    /**
     * Nastaví textovou reprezentaci objektu.
     * @param output textová reprezentace objektu.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setOutput(String output) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    private void checkInput(Object input) 
            throws NullPointerException {
        
        if (input == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input"));
        }
    }
}
