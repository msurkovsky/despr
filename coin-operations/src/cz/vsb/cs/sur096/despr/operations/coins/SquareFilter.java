package cz.vsb.cs.sur096.despr.operations.coins;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.ECoherence;

/**
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class SquareFilter extends AbstractCoinOperation {

    public SquareFilter() {
        scale = 1.0f;
        kernelWidth = 10;
        coherence = ECoherence.X_AXIS;
    }
    
    @AInputParameter(EInputParameterType.INNER)
    private Float scale;
    
    @AInputParameter(EInputParameterType.INNER)
    private Integer kernelWidth;
    
    @AInputParameter(EInputParameterType.INNER)
    private ECoherence coherence;
    
    @AInputParameter(EInputParameterType.OUTER)
    private Integer[] inputVector;
    
    @AOutputParameter
    private Integer[] outputVector;
            
    @Override
    public void execute() throws Exception {
        
        checkKernelWidth(kernelWidth);
        checkScale(scale);
        checkCoherence(coherence);
        checkInputVector(inputVector);
        
        int length = inputVector.length;
        if (kernelWidth > length / 2) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.kernel_width_out_of_range"));
        }
        
        outputVector = new Integer[length];
        boolean flagOverflow = false;
        for (int i = 0; i < length; i++) {
            double sum = 0;
            for (int k = i; k < (i + kernelWidth); k++) {
                
                int x;
                if (k >= length) {
                    x = Math.abs(length - k);
                    flagOverflow = true;
                } else {
                    x = k;
                }
                
                if (!flagOverflow || coherence == ECoherence.X_AXIS) {
                    sum += inputVector[x] * scale;
                }
                flagOverflow = false;
            }
            outputVector[i] = (int) (sum + 0.5);
        }
    }

    public Float getScale() {
        return scale;
    }

    public void setScale(Float scale) 
            throws NullPointerException, IllegalArgumentException {
        
        checkScale(scale);
        this.scale = scale;
    }

    public Integer getKernelWidth() {
        return kernelWidth;
    }

    public void setKernelWidth(Integer kernelWidth) 
            throws NullPointerException, IllegalArgumentException {
        
        checkKernelWidth(kernelWidth);
        this.kernelWidth = kernelWidth;
    }

    public ECoherence getCoherence() {
        return coherence;
    }

    public void setCoherence(ECoherence coherence) 
            throws NullPointerException {
        
        checkCoherence(coherence);
        this.coherence = coherence;
    }
    
    public Integer[] getInputVector() {
        return inputVector;
    }

    public void setInputVector(Integer[] inputVector) 
            throws NullPointerException {
        
        checkInputVector(inputVector);
        this.inputVector = inputVector;
    }

    public Integer[] getOutputVector() {
        return outputVector;
    }

    @Deprecated
    public void setOutputVector(Integer[] outputVector) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException();
    }
    
    ////////////////////////////////////////////////////////////
    
    private void checkScale(Float scale) 
            throws NullPointerException, IllegalArgumentException {
        
        if (scale == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_scale"));
        } else if (scale < 0) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.negative_scale"));
        }
    }
    
    private void checkKernelWidth(Integer kernelWidth) 
            throws NullPointerException, IllegalArgumentException {
        
        if (kernelWidth == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_kernel_width"));
        } else if (kernelWidth <= 0) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.negative_kernel_width"));
        }
    }
    
    private void checkCoherence(ECoherence coherence) 
            throws NullPointerException {
        
        if (coherence == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_coherence"));
        }
    }
    
    private void checkInputVector(Integer[] inputVector) 
            throws NullPointerException {
        
        if (inputVector == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_vector"));
        }
        
        for (Integer v : inputVector) {
            if (v == null) {
                throw new NullPointerException(
                        getLocalizeMessage("exception.null_item_in_input_vector"));
            }
        }
    }
}