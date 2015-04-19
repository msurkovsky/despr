
package cz.vsb.cs.sur096.despr.operations.specialfunctions;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.images.BinaryImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Image;

/**
 * Operace pro vizualizaci vektoru ve formou histogramu.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/15/09:48
 */
public class HistogramVisualizer extends AbstractSpecialOperation implements Displayable {
    
    /**
     * Konstruktor inicializuje operaci s defaultními hodnotami.
     */
    public HistogramVisualizer() {
        height = 300;
        columnWidth = 1;
        relative = true;
    }
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private Integer columnWidth;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2)
    private Integer height;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=3)
    private Boolean relative;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=4)
    private Number[] vector;
    
    @AOutputParameter
    private BinaryImage outputImage;

    /**
     * Na základě vstupního vektoru vizualizuje histogram.
     * @throws Exception pokud jsou vstupní parametry nekorektní.
     */
    @Override
    public void execute() throws Exception {
        
        checkColumnWidth(columnWidth);
        checkHeight(height);
        checkRelative(relative);
        checkVector(vector);
        
        double maxValue = 0;
        int vectorLength = vector.length;
        for (int i = 0; i < vectorLength; i++) {
            Number a = vector[i];
            
            if (a.doubleValue() > maxValue) {
                maxValue = a.doubleValue();
            }
        }
        
        int width = vectorLength * columnWidth;
        outputImage = new BinaryImage(width, height);
        for (int x = 0; x < width; x++) {
            int i = x / columnWidth;
            int max;
            if (relative) {
                max = (int) ((vector[i].doubleValue() * height) / maxValue);
            } else {
                max = (int) vector[i].doubleValue();
                if (max >= height) {
                    max = height -1;
                }
            }
            for (int y = 0; y < height; y++) {
                if (y < (height - max)) {
                    outputImage.setBlack(x, y);
                } else {
                    outputImage.setWhite(x, y);
                }
            }
        }
    }
    
    /**
     * Jako náhled je použit vizualizovaný histogram.
     * @return vizualizovaný histogram.
     */
    @Override
    public Image getThumbnail() {
        return outputImage;
    }

    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Poskytne šířku jednoho sloupce histogramu.
     * @return šířka jednoho sloupce histogramu.
     */
    public Integer getColumnWidth() {
        return columnWidth;
    }

    /**
     * Nastaví šířku jednoho sloupce histogramu.
     * @param columnWidth šířka jednoho sloupce
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException  pokud je hodnota záporná.
     */
    public void setColumnWidth(Integer columnWidth) 
            throws NullPointerException, IllegalArgumentException {
        
        checkColumnWidth(columnWidth);
        this.columnWidth = columnWidth;
    }

    /**
     * Poskytne výšku histogramu.
     * @return výška histogramu.
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * 
     * Nastaví výšku histogramu.
     * @param height výška histogramu.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException pokud je hodnota záporná.
     */
    public void setHeight(Integer height) 
            throws NullPointerException, IllegalArgumentException {
        
        checkHeight(height);
        this.height = height;
    }

    /**
     * Poskytne hodnotu příznaku relative.
     * @return {@code true} pokud se má histogram přizpůsobit výšce, jinak 
     * {@code false}.
     */
    public Boolean getRelative() {
        return relative;
    }

    /**
     * Nastaví zda se má histogram vykreslit relativně k výšce.
     * @param relative {@code true} pokud se má histogram přizpůsobit výšce,
     * pokud ne pak {@code false}
     * @throws NullPointerException  pokud je hodnota prázdná.
     */
    public void setRelative(Boolean relative) throws NullPointerException {
        checkRelative(relative);
        this.relative = relative;
    }
    
    /**
     * Poskytne vstupní vektor.
     * @return vstupní vektor.
     */
    public Number[] getVector() {
        return vector;
    }
    
    /**
     * Nastaví vstupní vektor.
     * @param vector vstupní vektor.
     * @throws NullPointerException pokud je vektor {@code null} nebo
	 * jakákoli jeho hodnota.
     * @throws IllegalArgumentException pokud je jakákoli hodnota vektoru
	 * záporná.
     */
    public void setVector(Number[] vector) 
            throws NullPointerException, IllegalArgumentException {
        
        checkVector(vector);
        this.vector = vector;
    }

    /**
     * Poskytne vizuální reprezentaci vektoru.
	 * Histogram je nakreslen bílou barvou s černým pozadím.
     * @return vizuální reprezentaci histogramu.
     */
    public BinaryImage getOutputImage() {
        return outputImage;
    }

    /**
     * Nastaví vizuální reprezentaci histogramu.
     * @param outputImage vizuální reprezentace histogramu.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setOutputImage(BinaryImage outputImage) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomecne metody
    
    private void checkColumnWidth(Integer columnWidth) 
            throws NullPointerException, IllegalArgumentException {
        
        if (columnWidth == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_column_width"));

        } else if (columnWidth < 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_column_width"), 
                    columnWidth));
        }
    }

    private void checkHeight(Integer height) 
            throws NullPointerException, IllegalArgumentException {
        
        if (height == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_height"));
        } else if (height < 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_height"), height));
        }
    }
    
    private void checkRelative(Boolean relative) throws NullPointerException {
        
        if (relative == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_relative"));
        }
    }
    
    private void checkVector(Number[] vector) 
        throws NullPointerException, IllegalArgumentException {
        
        if (vector == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_vector"));
        } else {
            int size = vector.length;
            for (int i = 0; i < size; i++) {
                Number v = vector[i];
                if (v == null) {
                    throw new NullPointerException(String.format("%s 'index=%d'",
                            getLocalizeMessage("exception.null_vector_item"),
                            i));
                } else if (v.doubleValue() < 0) {
                    throw new IllegalArgumentException(String.format("%s 'v[%d] = %f'", 
                            getLocalizeMessage("exception.negative_vector_item"),
                            i, v.doubleValue()));
                }
            }
        }
    }
    
}
