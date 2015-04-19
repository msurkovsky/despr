package cz.vsb.cs.sur096.despr.operations.database;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.EVectorType;
import cz.vsb.cs.sur096.despr.types.images.BinaryImage;

/**
 * Třída slouží k seskupení pixelu na obrázku do větších celků.
 * Např. řeknu že chci oblasti o velikosti {@literal 10x5px} a obrázek se 
 * do tolika oblastí rozdělí.
 *
 * @author Martin Šurkovský, SUR096 <martin.surkovsky@gmail.com>
 * @version 2011/12/14/20:22
 */
public class ApplyRectangularMask extends AbstractDatabaseOperation {
    
    /**
     * Konstruktor inicializuje operaci defaultními hodnotami.
     */
    public ApplyRectangularMask() {
        zoneWidth  = 1;
        zoneHeight = 1;
        typeVector = EVectorType.ABSOLUTE_VALUES;
    }
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private EVectorType typeVector;

    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2)
    /** Šířka oblasti */
    private Integer zoneWidth;

    @AInputParameter(value=EInputParameterType.INNER, enteringAs=3)
    /** Výška oblasti */
    private Integer zoneHeight;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=4)
    private BinaryImage inputImage;
    
    @AOutputParameter(enteringAs=1)
    private Float[] vector;
    
    @AOutputParameter(enteringAs=2)
    private Integer sum;
    
    /**
     * Metoda pro vytvoření oblastí, na barevném obrázku, nebo
     * obrázku v šedé škále. Vytvoří oblasti o požadované velikosti,
     * pokud by se však oblasti do obrázku nevešly bezezbytku, pak se
     * zbývající část dopočte jako samostatná oblast (tj. bude menši ),
     * jedná se o poslední sloupec a poslední řádek.
	 * @throws Exception pokud jsou vstupní parametry nekorektní.
     */
    @Override
    public void execute() throws Exception {

        checkTypeVector(typeVector);
        checkZoneWidth(zoneWidth);
        checkZoneHeight(zoneHeight);
        checkInputImage(inputImage);
        
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        
        if (zoneWidth > width || zoneHeight > height) {
            throw new RuntimeException(String.format("%s %s: 0 <= %d <= %d; %s: 0 <= %d <= %d",
                    getLocalizeMessage("exception.too_large_zone_sizes"),
                    getLocalizeMessage("zoneWidth"), zoneWidth, width,
                    getLocalizeMessage("zoneHeight"), zoneHeight, height));
        }
        
        int numberXZones = (int) Math.floor((double) width / zoneWidth);
        int numberYZones = (int) Math.floor((double) height / zoneHeight);

        int size = numberXZones * numberYZones;
        vector = new Float[size];
        
        // inicializace hodnot na nula.
        for (int i = 0; i < size; i++) {
            vector[i] = 0f;
        }

        // sečtení hodnot, jednotlivých barevných složek, v danné oblasti
        sum = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (inputImage.isWhite(x, y)) {
                    int m = x / zoneWidth;
                    int n = y / zoneHeight;
                    int position = n * numberXZones + m;
                    vector[position]++;
                    sum++;
                }
            }
        }

        if (typeVector.equals(EVectorType.RELATIVE_VALUES)) {
            // zrelativizovani
            int zoneSize = zoneWidth * zoneHeight;
            for (int i = 0; i < size; i++) {
                vector[i] = (vector[i] * 100) / zoneSize;
            }
        }
    }
    
    ////////////////////////////////////////////////////////////
    // Get and set methods

    /**
     * Poskytne typ výstupního vektoru.
     * @return 
     */
    public EVectorType getTypeVector() {
        return typeVector;
    }

    /**
     * Nastaví typ výstupního vektoru, tj. nastaví typ hodnot
	 * jaké má vektoru obsahovat (absolutní/relativní).
     * @param typeVector typ vektoru.
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setTypeVector(EVectorType typeVector) 
            throws NullPointerException {
        
        checkTypeVector(typeVector);
        this.typeVector = typeVector;
    }

    /**
     * Poskytne šířku shlukované oblasti.
     * @return šířka oblasti.
     */
    public Integer getZoneWidth() {
        return zoneWidth;
    }

    /**
     * Nastaví šířku oblasti do které se mají pixely seskupit.
     * @param zoneWidth šířka oblasti.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException  pokud je hodnota menší nebo rovna 
	 * nule.
     */
    public void setZoneWidth(Integer zoneWidth) 
            throws NullPointerException, IllegalArgumentException {
        
        checkZoneWidth(zoneWidth);
        this.zoneWidth = zoneWidth;
    }

    /**
     * Poskytne výšku shlukované oblasti.
     * @return výška oblasti.
     */
    public Integer getZoneHeight() {
        return zoneHeight;
    }

    /**
     * Nastaví výšku oblasti do které se mají pixely shluknout.
     * @param zoneHeight výška oblasti.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException  pokud je hodnota menší nebo rovna
	 * nule.
     */
    public void setZoneHeight(Integer zoneHeight) 
            throws NullPointerException, IllegalArgumentException {
        
        checkZoneHeight(zoneHeight);
        this.zoneHeight = zoneHeight;
    }

    /**
     * Poskytne vstupní obrázek.
     * @return vstupní obrázek.
     */
    public BinaryImage getInputImage() {
        return inputImage;
    }

    /**
	 * Nastaví vstupní obrázek.
     * @param inputImage vstupní obrázek.
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setInputImage(BinaryImage inputImage) 
            throws NullPointerException {
        
        checkInputImage(inputImage);
        this.inputImage = inputImage;
    }
    
    /**
     * Poskytne výstupní vektor.
     * @return nastaví výstupní vektor.
     */
    public Float[] getVector() {
        return vector;
    }

    /**
     * Nastaví výstupní vektor.
     * @param vector výstupní vektor.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setVector(Float[] vector) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    /**
     * Poskytne celkový součet všech hodnot výstupního vektoru.
     * @return součet hodnot výstupního vektoru.
     */
    public Integer getSum() {
        return sum;
    }

    /**
     * Nastaví součet výstupního vektoru.
     * @param sum součet výstupního vektoru.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setSum(Integer sum) 
           throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    private void checkTypeVector(EVectorType typeVector) 
            throws NullPointerException {
        
        if (typeVector == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_type_vector"));
        }
    }
    
    private void checkZoneWidth(Integer zoneWidth) 
            throws NullPointerException, IllegalArgumentException {
        
        if (zoneWidth == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_zone_width"));
        } else if (zoneWidth <= 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_zone_width"), 
                    zoneWidth));
        }
    }
    
    private void checkZoneHeight(Integer zoneHeight) 
            throws NullPointerException {
        
        if (zoneHeight == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_zone_height") +
                    " (ApplyRectangularMask.setZoneHeight)");
        } else if (zoneHeight <= 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_zone_height"), zoneHeight));
        }
    }
    
    private void checkInputImage(BinaryImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
}
