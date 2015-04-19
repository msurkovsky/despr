
package cz.vsb.cs.sur096.despr.operations.coins;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.ECoherence;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.*;

/**
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class GaussianFilter extends AbstractCoinOperation implements Displayable {

    private final int KERNEL_SIZE = 7;
    
    private boolean wasInit;
    private double[] kernel;
    
    public GaussianFilter() {
        scale = 1.0f;
        sigma = 1.0f;
        coherence = ECoherence.X_AXIS;
        wasInit = false;
    }
    
    @AInputParameter(EInputParameterType.INNER)
    private Float sigma;
    
    @AInputParameter(EInputParameterType.INNER)
    private Float scale;
    
    @AInputParameter(EInputParameterType.INNER)
    private ECoherence coherence;
    
    @AInputParameter(EInputParameterType.OUTER)
    private Integer[] inputVector;
    
    @AOutputParameter
    private Integer[] outputVector;
    
    @Override
    public void execute() throws Exception {
        
        checkSigma(sigma);
        checkScale(scale);
        checkCoherence(coherence);
        checkInputVector(inputVector);
        
        if (!wasInit) {
            init();
            wasInit = true;
        }
        
        int length = inputVector.length;
        outputVector = new Integer[length];
        boolean flagOverflow = false;
        int kHalfSize = kernel.length / 2;
        for (int i = 0; i < length; i++) {
            double sum = 0.0;
            for (int k = -kHalfSize; k <= kHalfSize; k++) {
                int x = i + k;
                if (x < 0) {
                    x = length + k; // k < 0
                    flagOverflow = true;
                } else if (x >= length) {
                    x = k;
                    flagOverflow = true;
                }
                
                if (!flagOverflow || coherence == ECoherence.X_AXIS) {
                    sum += inputVector[x] * kernel[kHalfSize + k];
                }
                flagOverflow = false;
            }
            outputVector[i] = (int) (sum + 0.5);
        }
    }

    @Override
    public Image getThumbnail() {
        
        double gx1 = (double) 1 / (Math.sqrt(2*Math.PI) * sigma);
        
        int width = 400;
        int height = (int) (gx1 * width * scale);
        int height2 = height + 2;
        
        ColorImage img = new ColorImage(width, height2);
        
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        int halfWidth = width / 2;
        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(1.5f));
        for (int x = 0; x < halfWidth-1; x++) {
            int x2 = x + 1;
            
            double i = (double) x / 10;
            double gx2 = (i * i) / (2 * sigma * sigma);
            double j = gx1 * Math.pow(Math.E, -gx2) * scale;
            
            double i2 = (double) x2 / 10;
            double gx22 = (i2 * i2) / (2 * sigma * sigma);
            double j2 = gx1 * Math.pow(Math.E, - gx22) * scale;
            
            int y = (int) (j * width);
            int y2 = (int) (j2 * width);
            
            g2d.drawLine(halfWidth + x, height - y, halfWidth + x2, height - y2);
            g2d.drawLine(halfWidth - x, height - y, halfWidth - x2, height - y2);
        }
        
        return img;
    }

    ////////////////////////////////////////////////////////////
    
    public Float getSigma() {
        return sigma;
    }

    public void setSigma(Float sigma)
            throws NullPointerException, IllegalArgumentException {
        
        checkSigma(sigma);
        
        wasInit = false;
        this.sigma = sigma;
    }

    public Float getScale() {
        return scale;
    }

    public void setScale(Float scale) 
            throws NullPointerException, IllegalArgumentException {
        
        checkScale(scale);
        
        wasInit = false;
        this.scale = scale;
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
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }

    ////////////////////////////////////////////////////////////
    
    private void init() {
        kernel = new double[KERNEL_SIZE];
        int kHalfSize = KERNEL_SIZE / 2; // also is the same as a center point
        
        double[] tmpK = new double[kHalfSize + 1];
        double gx1 = (double) 1 / (Math.sqrt(2*Math.PI) * sigma);
        
        for (int x = 0; x <= kHalfSize; x++) {
            double gx2 = (double) (x * x) / (2 * sigma * sigma);
            tmpK[x] = gx1 * (Math.pow(Math.E, -gx2));
        }
        
        kernel[kHalfSize] = tmpK[0];
        for (int i = 1; i <= kHalfSize; i++) {
            kernel[kHalfSize - i] = kernel[kHalfSize + i] = tmpK[i] * scale;
        }
    }
    
    ////////////////////////////////////////////////////////////
    
    private void checkSigma(Float sigma) 
            throws NullPointerException, IllegalArgumentException {
        
        if (sigma == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_sigma"));
        } else if (sigma < 0.01) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.sigma_out_of_range"));
        }
    }
    
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