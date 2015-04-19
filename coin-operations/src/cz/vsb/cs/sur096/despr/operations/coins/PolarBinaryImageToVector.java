
package cz.vsb.cs.sur096.despr.operations.coins;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.EReduceMethod;
import cz.vsb.cs.sur096.despr.types.images.BinaryImage;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Třída sluhkne binarni obrazek v polarních souřadních na vektor čísel,
 * podle jedné z předdefinovaných metod.
 * 
 * @author Martin Šurkovský, sur096
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class PolarBinaryImageToVector extends AbstractCoinOperation implements Displayable {

    public PolarBinaryImageToVector() {
        reduceMethod = EReduceMethod.HISTOGRAM;
    }

    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2)
    private EReduceMethod reduceMethod;
    
    @AInputParameter(value= EInputParameterType.OUTER, enteringAs=3)
    private BinaryImage input;
    
    @AOutputParameter
    private Integer[] outVector;
    
    @Override
    public void execute() throws Exception {
        checkInput(input);
        checkReduceMethod(reduceMethod);
        
        switch(reduceMethod) {
            case HISTOGRAM:
                outVector = computeHistogram(input);
                break;
            case AVERAGE:
                outVector = computeAVG(input);
                break;
            case MEDIAN:
                outVector = computeMedian(input);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
    
    @Override
    public Image getThumbnail() {
        int w = input.getWidth();
        int h = input.getHeight();
        ColorImage thumbnail = new ColorImage(w, h);
        
        for (int x = 0; x < w - 1; x++) {
            for (int y = 0; y < h; y++) {
                thumbnail.setRGB(x, y, input.getRGB(x, y));
            }
            
            int k = outVector[x];
            int l = outVector[x+1];
            
            int min = Math.min(k, l);
            int max = Math.max(k, l);
            for (int y = min; y <= max; y++) {
                int m = h - y;
                thumbnail.setRGB(x, m, Color.RED.getRGB());
            }
        }
        
        return thumbnail;
    }

    public EReduceMethod getReduceMethod() {
        return reduceMethod;
    }

    public void setReduceMethod(EReduceMethod reduceMethod) 
            throws NullPointerException {
        
        checkReduceMethod(reduceMethod);
        this.reduceMethod = reduceMethod;
    }

    public BinaryImage getInput() {
        return input;
    }

    public void setInput(BinaryImage input) 
            throws NullPointerException {
        
        checkInput(input);
        this.input = input;
    }

    public Integer[] getOutVector() {
        return outVector;
    }

    public void setOutVector(Integer[] outVector) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    
    private void checkReduceMethod(EReduceMethod reduceMethod) 
            throws NullPointerException {
    
        if (reduceMethod == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_reduce_method"));
        }
    }
    
    private void checkInput(BinaryImage input) throws NullPointerException {
        
        if (input == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input"));
        }
    }
    
    ////////////////////////////////////////////////////////////
    
    private Integer[] computeHistogram(BinaryImage img) {
        
        int width = img.getWidth();
        int height = img.getHeight();

        Integer[] out = new Integer[width];
        for (int i = 0; i < width; i++) {
            out[i] = 0;
        }
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (img.isWhite(x, y)) {
                    out[x]++;
                }
            }
        }
        
        return out;
    }
    
    private Integer[] computeAVG(BinaryImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        
        Integer[] out = new Integer[width];
        for (int i = 0; i < width; i++) {
            out[i] = 0;
        }
        
        for (int x = 0; x < width; x++) {
            int count = 0;
            int sum = 0;
            for (int y = 0; y < height; y++) {
                if (img.isWhite(x, y)) {
                    sum += y;
                    count++;
                }
            }
            out[x] = sum / count;
        }
        
        return out;
    }
    
    private Integer[] computeMedian(BinaryImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        
        Integer[] out = new Integer[width];
        for (int i = 0; i < width; i++) {
            out[i] = 0;
        }

        List<Integer> yCoordinates = new ArrayList<Integer>();
        for (int x = 0; x < width; x++) {
            yCoordinates.clear();
            for (int y = 0; y < height; y++) {
                if (img.isWhite(x, y)) {
                    yCoordinates.add(y);
                }
            }
            Collections.sort(yCoordinates);
            out[x] = yCoordinates.get(yCoordinates.size() / 2);
        }
        return out;
    }
}