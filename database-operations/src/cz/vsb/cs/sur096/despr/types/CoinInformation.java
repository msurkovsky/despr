
package cz.vsb.cs.sur096.despr.types;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Struktura uchovávající informace o minci. Jedná se 
 * o přepis hodnot jednoho řádku z databáze.
 *
 * @author Martin Šurkovský, sur096 
	 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/01/07/19:21
 */
public class CoinInformation {

    private transient ResourceBundle messages;
    
    private int id;
    private String coinName;
    private String sourceImage;
    private float angle;
    private int suma;
    private float[] vector;
    private int idSimilarity;
    
    /**
     * Konstruktor inicializuje strukturu s informacemi o minci.
     * @param id id mince.
     * @param coinName jméno mince.
     * @param sourceImage cesta ke zdrojovému obrázku.
     * @param angle úhel s jakým byla mince natočena.
     * @param suma celková suma hodnot vektoru.
     * @param vector vektor reprezentující minci
     * @param vectorSize velikost vektoru.
     * @param idSimilarity id podobnosti.
     */
    public CoinInformation(int id, String coinName, String sourceImage,
            float angle, int suma, String vector, int vectorSize, int idSimilarity) {
        
        try {
            messages = ResourceBundle.getBundle(getClass().getCanonicalName());
        } catch (MissingResourceException ex) {
            messages = null;
        }
        
        this.id = id;
        this.coinName = coinName;
        this.sourceImage = sourceImage;
        this.angle = angle;
        this.suma = suma;
        this.vector = stringToVector(vector, vectorSize);
        this.idSimilarity = idSimilarity;
    }
    
    
    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Poskytne id mince.
     * @return id mince.
     */
    public int getId() {
        return id;
    }

    /**
     * Nastaví id mince.
     * @param id id mince.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Poskytne jméno mince.
     * @return jméno mince.
     */
    public String getCoinName() {
        return coinName;
    }

    /**
     * Nastaví jméno mince.
     * @param coinName jméno mince.
     */
    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    /**
     * Poskytne cestu ke zdrojovému obrázku.
     * @return cestu ke zdrojovému obrázku.
     */
    public String getSourceImage() {
        return sourceImage;
    }

    /**
     * Nastaví cestu ke zdrojovému obrázku.
     * @param sourceImage cesta ke zdrojovému obrázku.
     */
    public void setSourceImage(String sourceImage) {
        this.sourceImage = sourceImage;
    }
    
    /**
     * Poskytne úhel se kterým byla mince natočena.
     * @return úhel se kterým byla mince natočena.
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Nastaví úhel se kterým byla mince natočena.
     * @param angle úhel se kterým byla mince natočena.
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * Poskytne celkový součet hodnot vektoru.
     * @return celkový součet hodnot vektoru.
     */
    public int getSuma() {
        return suma;
    }

    /**
     * Nastaví celkový součet hodnot vektoru.
     * @param suma celkový součet hodnot vektoru.
     */
    public void setSuma(int suma) {
        this.suma = suma;
    }

    /**
     * Poskytne vektor reprezentující minci.
     * @return vektor reprezentující minci.
     */
    public float[] getVector() {
        return vector;
    }

    /**
     * Nastaví vektor reprezentující minci.
     * @param vector  vektor reprezentující minci.
     */
    public void setVector(float[] vector) {
        this.vector = vector;
    }

    /**
     * Poskytne id podobnosti.
     * @return id podobnosti.
     */
    public int getIdSimilarity() {
        return idSimilarity;
    }

    /**
     * 
     * Nastaví id podobnosti.
     * @param idSimilarity id podobnosti.
     */
    public void setIdSimilarity(int idSimilarity) {
        this.idSimilarity = idSimilarity;
    }
    
    //////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    
    private String getString(String key, String defaultMessage) {
        if (messages == null) {
            return defaultMessage;
        }
        
        try {
            return messages.getString(key);
        } catch (MissingResourceException ex) {
            return defaultMessage;
        }
    }
    
    private float[] stringToVector(String vector, int size) {
        
        float[] v = new float[size];
        
        StringTokenizer st = new StringTokenizer(vector, ",");
        int idx = 0;
        while(st.hasMoreTokens()) {
            if (idx < size) {
                v[idx] = Float.parseFloat(st.nextToken());
                idx++;
            } else {
                throw new IndexOutOfBoundsException(getString(
                        "exception.idx_out_of_range", 
                        "The variable 'idx' is greater than size of vector!"));
            }
        }
        
        if (idx != size) {
            throw new IndexOutOfBoundsException(getString(
                    "exception.too_few_components",
                    "The vector has fewer components than is size of the vector!"));
        }
        
        return v;
    }
}
