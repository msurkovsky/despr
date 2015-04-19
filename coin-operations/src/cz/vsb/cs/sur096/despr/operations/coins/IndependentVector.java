
package cz.vsb.cs.sur096.despr.operations.coins;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.images.BinaryImage;
import cz.vsb.cs.sur096.despr.types.images.ColorImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Martin Šurkvoský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class IndependentVector extends AbstractCoinOperation implements Displayable {
    
    private int[][] hashImg;
    private int max;
    
    public IndependentVector() {
        maskWidth = 10;
        maskHeight = 63;
    }
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private Integer maskWidth;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2)
    private Integer maskHeight;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=3)
    private BinaryImage inputImage;
    
    @AOutputParameter(enteringAs=1)
    private Float[] outputVector;
    
    @AOutputParameter(enteringAs=2)
    private Integer sum;

    @Override
    public void execute() throws Exception {
        
        checkInputImage(inputImage);
        checkMaskWidth(maskWidth);
        checkMaskHeight(maskHeight);
        
        
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        int width2 = (width % 2 == 0) ? width / 2 + 1 : width / 2;
        int height2 = 2 * height;
        
        if (maskWidth > width2) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.too_big_mask_width"));
        }
        
        if (maskHeight > height2) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.too_big_mask_height"));
        }
        
        //////////
        
        ArrayList<Point> whitePixels = new ArrayList<Point>();

        for( int x = 0; x < width; x++ )
        {
            for( int y = 0; y < height; y++ )
            {
                if (inputImage.isWhite(x, y))
                {
                    whitePixels.add( new Point( x, y ) );
                }
            }
        }

        max = 0;
        hashImg = new int[width2][height2];
        for (int x = 0; x < width2; x++) {
            for (int y = 0; y < height2; y++) {
                hashImg[x][y] = 0;
            }
        }
        
        // wihtePixelsLenght
        int wpl = whitePixels.size();
        // aktualni pocatek
        for (int i = 0; i < wpl; i++) {
            // Beginninig Coordinates systems
            Point currentBCS = whitePixels.get( i );
            
            // aktualni bod
            for (int j = 0; j < wpl; j++) {
                Point currentPixel = whitePixels.get( j );

                int x = currentPixel.x - currentBCS.x;
                if (x < 0) {
                    x = width + x;
                }

                // increment value in hash of image
                if (x < width2) { // co je nad polovinou uz je symtericky
                    
                    int y = currentPixel.y + currentBCS.y;
                    
                    hashImg[x][y]++;
                    max = hashImg[x][y] > max ? hashImg[x][y] : max; // odstraneni hledani maxima
                }
            }
        }
        
        int ovWidth = width2 / maskWidth;
        int ovHeight = height2 / maskHeight;
        int ovSize = ovWidth * ovHeight;
        outputVector = new Float[ovSize];
        for (int i = 0; i < ovSize; i++) {
            outputVector[i] = 0.0f;
        }
        
        int max2 = 0;
        for (int x = 0; x < width2; x++) {
            for (int y = 0; y < height2; y++) {
                
                int c = (int) ((double) (hashImg[x][y] * 100)) / max;
                int m = x / maskWidth;
                int n = y / maskHeight;
                int i = n * ovWidth + m;
                if (i < ovSize) {
                    outputVector[i] += c;
                    if (max2 < outputVector[i]) {
                        max2 = outputVector[i].intValue();
                    }
                }
            }
        }
        
        sum = 0;
        for (int i = 0; i < ovSize; i++) {
            Float v = outputVector[i];
            int inRange = (int) (((double) v * 1000) / max2);
            sum += inRange;
            outputVector[i] = new Float(inRange);
        }
    }

    @Override
    public Image getThumbnail() 
            throws RuntimeException {
         
        if (hashImg != null) {
            int width = inputImage.getWidth();
            int height = inputImage.getHeight();
            int width2 = (width % 2 == 0) ? width / 2 + 1 : width / 2;
            int height2 = 2 * height;
            ColorImage vizualize = new ColorImage(width2, height2);

            // vyobrazeni hash hodnoty
            for (int i = 0; i < width2; i++) {
                for (int j = 0; j < height2; j++) {
                    float c = (float) hashImg[i][j] / max;
                    if (c != 0) {
                        vizualize.setRGB(i, j, Color.HSBtoRGB(1.0F - c, 1F, 1F));
                    } else {
                        vizualize.setRGB(i, j, Color.BLACK.getRGB());
                    }
                }
            }
            return vizualize;
        } else {
            throw new RuntimeException("exception.null_hash_img");
        }
    }
    
    //////////////////////////////////////////////////////////////////////
    
    public Integer getMaskWidth() {
        return maskWidth;
    }

    public void setMaskWidth(Integer maskWidth)
            throws NullPointerException, IllegalArgumentException {
        
        checkMaskWidth(maskWidth);
        this.maskWidth = maskWidth;
    }

    public Integer getMaskHeight() {
        return maskHeight;
    }

    public void setMaskHeight(Integer maskHeight) 
            throws NullPointerException, IllegalArgumentException {
        
        checkMaskHeight(maskHeight);
        this.maskHeight = maskHeight;
    }

    public BinaryImage getInputImage() {
        return inputImage;
    }

    public void setInputImage(BinaryImage inputImage) 
            throws NullPointerException {
        
        checkInputImage(inputImage);
        this.inputImage = inputImage;
    }

    public Float[] getOutputVector() {
        return outputVector;
    }

    @Deprecated
    public void setOutputVector(Float[] outputVector) 
            throws UnsupportedOperationException {
        
          throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }

    public Integer getSum() {
        return sum;
    }

    @Deprecated
    public void setSum(Integer sum) 
            throws UnsupportedOperationException {
        
          throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    private void checkMaskWidth(Integer maskWidth) 
            throws NullPointerException, IllegalArgumentException {
        
        if (maskWidth == null) {
            throw new NullPointerException("exception.null_mask_width");
        } else if (maskWidth <= 0) {
            throw new IllegalArgumentException("exception.mask_width_is_not_positive_int");
        }
    }
    
    private void checkMaskHeight(Integer maskHeight) 
            throws NullPointerException, IllegalArgumentException {
        
        if (maskHeight == null) {
            throw new NullPointerException("exception.null_mask_height");
        } else if (maskHeight <= 0) {
            throw new IllegalArgumentException("exception.mask_height_is_not_positive_int");
        }
    }
    
    private void checkInputImage(BinaryImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException("exception.null_input_image");
        }
    }
}
