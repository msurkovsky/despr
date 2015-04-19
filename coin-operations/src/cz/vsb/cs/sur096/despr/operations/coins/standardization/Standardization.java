package cz.vsb.cs.sur096.despr.operations.coins.standardization;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.coins.AbstractCoinOperation;
import cz.vsb.cs.sur096.despr.types.images.BinaryImage;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Operace která vypočte úhel, podle kterého je mince natočena.
 * Algoritmus převzatý od Petra Kapara. Byl přepsán z php do javy.
 *
 * @author Martin Šurkovský, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2010/02/23/21:24, 2011/12/05/17:36 aktualizován pro Despra.
 */
public class Standardization extends AbstractCoinOperation {
 
    /**
     * Konstruktor, který inicializuje operaci defaultními hodnotami.
     */
    public Standardization() {
        // default values:
        pointThreshold = 85;
        regionThreshold = 10;
    }
    
    @AInputParameter(EInputParameterType.INNER)
    private Integer pointThreshold;
    
    @AInputParameter(EInputParameterType.INNER)
    private Integer regionThreshold;
    
    @AInputParameter(EInputParameterType.OUTER)
    private BinaryImage inputImage;
    
    @AOutputParameter
    private Double angle;
    
    @AOutputParameter
    private Integer[] histogram;
    
    /**
     * Vypočte úhel podle kterého bude natočena mince.
     * @throws Exception pokud jsou vstupní parametry nekorektní.
     */
    @Override
    public void execute() throws Exception {
        checkPointThreshold(pointThreshold);
        checkRegionThreshold(regionThreshold);
        checkInputImage(inputImage);
        
        process();
    }
    
    ////////////////////////////////////////////////////////////
    // Get a set metody

    /**
     * Poskytne práh ve kterém je jsou hledány spojité oblasti, 
	 * tj. procento kolik bodů z histogramu má zůstat pod touto
	 * hranicí. 
	 * @return hranice rozdělení histogramu.
     */
    public Integer getPointThreshold() {
        return pointThreshold;
    }

    /**
     * Nastaví hranici rozdělení histogramu.
     * @param pointThreshold procentuální hranice rozdělení oblasti.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException pokud je hodnota záporná. 
     */
    public void setPointThreshold(Integer pointThreshold) 
            throws NullPointerException, IllegalArgumentException {
        
        checkPointThreshold(pointThreshold);
        this.pointThreshold = pointThreshold;
    }

    /**
     * Poskytne práh oblasti, tj. minimální délka oblasti.
     * @return práh oblasti.
     */
    public Integer getRegionThreshold() {
        return regionThreshold;
    }

    /**
     * Nastaví práh oblasti.
     * @param regionThreshold minimální délka oblasti.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException pokud je hodnota záporná.
     */
    public void setRegionThreshold(Integer regionThreshold) 
            throws NullPointerException, IllegalArgumentException {
        
        checkRegionThreshold(regionThreshold);
        this.regionThreshold = regionThreshold;
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
     * @throws NullPointerException  pokud je hodnota prázdná.
     */
    public void setInputImage(BinaryImage inputImage) 
            throws NullPointerException {
        
        checkInputImage(inputImage);
        this.inputImage = inputImage;
    }

    /**
     * Poskytne úhle natočení ve stupních.
     * @return úhel natočení.
     */
    public Double getAngle() {
        return angle;
    }

    /**
     * Nastaví úhel natočení.
     * @param angle úhel natočení.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setAngle(Double angle) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    /**
     * Poskytne rozšířený histogram z kterého se určoval úhel natočení.
     * @return rozšířený histogram.
     */
    public Integer[] getHistogram() {
        return histogram;
    }

    /**
     * Nastaví rozšířený histogram.
     * @param histogram rozšířený histogram.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setHistogram(Integer[] histogram) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Sourkrome pomocne metody
    
    private void checkPointThreshold(Integer pointThreshold) 
            throws NullPointerException, IllegalArgumentException {
        
        if (pointThreshold == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_point_threshold"));
        } else if (pointThreshold < 0 || pointThreshold > 100) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.point_threshold_out_of_range"));
		}
    }
    
    private void checkRegionThreshold(Integer regionThreshold)
            throws NullPointerException, IllegalArgumentException {
        
        if (regionThreshold == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_region_threshold"));
        } else if (regionThreshold < 0) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.negative_region_threshold"));
        }
    }
    
    private void checkInputImage(BinaryImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
    
    ////////////////////////////////////////////////////////////
    /**
     * Metoda, která vrátí největší prvek v poli
     *
     * @param numbers pole s čísly
     * @return největší číslo v poli
     */
    private int getMaxValue(int[] numbers) {
        int maxValue = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > maxValue) {
                maxValue = numbers[i];
            }
        }
        return maxValue;
    }
    
    /**
     * Metoda, která převede barvu v ARGB do číselného formátu pro další zpracování
     *
     * @param a alfa kanál
     * @param r hodnota red
     * @param g hodnota green
     * @param b hodnota blue
     * @return výsledná barva
     */
    private int makeARGB(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }
    
    /**
     * Metoda, která vypočítá vlastní rozšířený histogram
     *
     * @param image vstupní obrázek v polárních souřadnicích a detekovanými hranami + prahování
     * @param sirka šířka pásu
     * @param sirkaPolarek šířka polárek
     * @return obrázek s rozšířeným histogramem
     * @throws IOException
     */
    private BufferedImage getExtendedHistogram(BufferedImage image, 
            int stripWidth, int polarWidth) {
        
        int width = image.getWidth();
        int height = image.getHeight();

        int[] histTemp = new int[width];

        //Pro kazdy sloupecek
        for (int x = 0; x < width; x++) {
            histTemp[x] = 0;
            for (int x1 = x; x1 < (x + stripWidth); x1++) {
                for (int y1 = (height - 1); y1 >= 0; y1--) {

                    int x1r;
                    if (x1 > polarWidth - 1) {
                        x1r = Math.abs(width - x1);
                    } else {
                        x1r = x1;
                    }
                    
                    int color = image.getRGB(x1r, y1);
                    int r = (color >>> 16) & 0xFF;
                    if (r == 255) {
                        histTemp[ x ]++;
                    }
                }
            }
        }

        histogram = new Integer[width];
        for (int i = 0; i < width; i++) {
            histogram[i] = histTemp[i];
        }
        
        //kresleni histogramu
        int maximum = getMaxValue(histTemp);
        BufferedImage imageHist = new BufferedImage(width, maximum, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y1 = (maximum - 1); y1 > (maximum - histTemp[x] - 1); y1--) {
                imageHist.setRGB(x, y1, makeARGB(255, 200, 200, 200));
            }
        }
        return imageHist;
    }

    public void process()
    {
        int spaceThreshold;
        int polarWidth = inputImage.getWidth();
        
        BufferedImage image1 = getExtendedHistogram(inputImage, 20, polarWidth);
        int width = image1.getWidth();
        int height = image1.getHeight();

        //-----------[ START: Výpočet počtu bodů v obraze ]-------------
        int count = 0;
        int maximum = height - 1;

        for (int x = 0; x < width; x++) {
            for (int y = (height - 1); y >= 0; y--) {
                int color = image1.getRGB(x, y);
                int r = (color >>> 16) & 0xFF;
                if (r == 200) {
                    count++;
                    if (y < maximum) {
                        maximum = y;
                    }
                }
            }
        }
        //-----------[ KONEC: Výpočet počtu bodů v obraze ]-------------

        spaceThreshold = count / 175; //Dynamické nastavení minimální plochy, která se odfiltruje

        //-----------[ START: Výpočet Y souřednice hranice ]------------
        int limit = (int) (((double) pointThreshold / 100) * count);
        int count_1 = 0;
        int yMedian_1 = 0;

        for (int y = (height - 1); y >= 0; y--)  {
            for (int x = 0; x < width; x++) {
                int color = image1.getRGB(x, y);
                int r = (color >>> 16) & 0xFF;
                if (r == 200) {
                    count_1++;
                }
            }

            if (count_1 > limit)  {
                yMedian_1 = y;
                break;
            }
        }
        //-----------[ KONEC: Výpočet Y souřednice hranice ]------------

        
        //-----------[ START: Výpočet mezních bodů na Y hranici ]-------
        int coordinate = yMedian_1;
        int previous = 0;
        boolean first = true;

        ArrayList<Region> borderTest = new ArrayList<Region>();
        ArrayList<Region> border_1_Test = new ArrayList<Region>();
        
        boolean firstFull = false;
        
        for (int x = 0; x < width; x++)  {
            int color = image1.getRGB(x, coordinate);
            int r = (color >>> 16) & 0xFF;
            if (r == 200)  {
                if (first)  {
                    first = false;
                    previous = 200;
                    borderTest.add(new Region(x, 0, 0, 0));
                    firstFull = true;
                } else {
                    if (previous == 0) {
                        borderTest.add(new Region(x, 0, 0, 0));
                        previous = 200;
                    }
                }
            } else {
                if (first) {
                    first = false;
                    previous = 0;
                    borderTest.add(new Region(x, 0, 0, 0));
                    firstFull = false;
                } else {
                    if (previous == 200) {
                        borderTest.add(new Region(x, 0, 0, 0));
                        previous = 0;
                    }
                }
            }
        }
        //-----------[ KONEC: Výpočet mezních bodů na Y hranici ]-------

        
        //-----------[ START: Doplnění typu a délek k jednotlivým oblastem ]-------
        for (int k = 0; k < borderTest.size(); k++)
        {
            int length;
            int type;
            Region currentRegion = borderTest.get(k);

            if (k < borderTest.size() - 1) {
                Region nextRegion = borderTest.get(k + 1);
                length = nextRegion.start - currentRegion.start;
            } else {
                length = polarWidth - currentRegion.start;
            }

            if (k % 2 != 0) {
                if (firstFull) {
                    type = 0;
                } else {
                    type = 1;
                }
            } else {
                if (firstFull) {
                    type = 1;
                } else {
                    type = 0;
                }
            }

            int space = 0;
            if (type == 1) {
                for ( int x = currentRegion.start; x < currentRegion.start + length; x++) {
                    for (int y = 0; y < coordinate; y++) {
                        int color = image1.getRGB(x, y);
                        int r = (color >>> 16) & 0xFF;
                        if (r == 200) {
                            space++;
                        }
                    }
                }
            }
            else {
                for (int x = currentRegion.start; x < currentRegion.start + length; x++) {
                    for (int y = coordinate; y < height; y++) {
                        int color = image1.getRGB(x, y);
                        int r = (color >>> 16 ) & 0xFF;
                        if (r != 200) {
                            space++;
                        }
                    }
                }
            }

            border_1_Test.add(new Region(currentRegion.start, length, type, space));
        }
        //-----------[ KONEC: Doplnění typu a délek k jednotlivým oblastem ]-------

        
        //-----------[ START: Ošetření stejného typu na začátku a konci ]-------
        Region firstRegion = border_1_Test.get(0);
        Region lastRegion = border_1_Test.get(border_1_Test.size() - 1);

        if (firstRegion.type == lastRegion.type && border_1_Test.size() != 1) {
            border_1_Test.remove(0);

            border_1_Test.add(0, new Region(firstRegion.start - lastRegion.length,
                    firstRegion.length + lastRegion.length,
                    firstRegion.type,
                    firstRegion.space + lastRegion.space));

            border_1_Test.remove(border_1_Test.size() - 1);
        }
        //-----------[ KONEC: Ošetření stejného typu na začátku a konci ]-------

        
        //-----------[ START: Počet obastí ]-------
        int regionCount = 0;
        Iterator iter = border_1_Test.iterator();
        while (iter.hasNext()) {
            Region temp = (Region) iter.next();
            if (temp.length <= regionThreshold) {
                regionCount++;
            }
        }
        //-----------[ KONEC: Počet obastí ]-------

        
        //-----------[ START: Smazání menších oblastí, než je limit ]-------
        // Limit sirky Region
        for (int c = 1; c <= regionCount; c++) {
            int before;
            int next;

            for (int k = 0; k < border_1_Test.size(); k++) {
                if (border_1_Test.get(k).length < regionThreshold) {
                    if ((k - 1) < 0) {
                        before = border_1_Test.size() - 1;
                    } else {
                        before = k - 1;
                    } if ((k + 1) > (border_1_Test.size() - 1)) {
                        next = 0;
                    } else {
                        next = k + 1;
                    }

                    border_1_Test.set(before,
                            new Region(border_1_Test.get(before).start,
                            border_1_Test.get(before).length + border_1_Test.get(next).length + border_1_Test.get(k).length,
                            border_1_Test.get(before).type,
                            border_1_Test.get(before).space + border_1_Test.get(next).space + border_1_Test.get(k).space));

                    border_1_Test.remove(k);

                    if (k == border_1_Test.size()) {
                        k = 0;
                    }

                    if (border_1_Test.size() != 1) {
                        border_1_Test.remove(k);
                    }
                    break;
                }
            }
        }

        //Limit maximalni plochy

        for (int c = 1; c <= regionCount; c++) {
            int before;
            int next;

            for (int k = 0; k < border_1_Test.size(); k++) {
                if (border_1_Test.get(k).space < spaceThreshold) {
                    if ((k - 1) < 0) {
                        before = border_1_Test.size() - 1;
                    } else {
                        before = k - 1;
                    }
                    
                    if ((k + 1) > (border_1_Test.size() - 1)) {
                        next = 0;
                    } else {
                        next = k + 1;
                    }

                    border_1_Test.set(before,
                            new Region(border_1_Test.get(before).start,
                            border_1_Test.get(before).length + border_1_Test.get(next).length + border_1_Test.get(k).length,
                            border_1_Test.get(before).type,
                            border_1_Test.get(before).space + border_1_Test.get(next).space + border_1_Test.get(k).space));

                    border_1_Test.remove(k);

                    if (k == border_1_Test.size()) {
                        k = 0;
                    }

                    if (border_1_Test.size() != 1) {
                        border_1_Test.remove(k);
                    }

                    break;
                }
            }
        }
        //-----------[ KONEC: Smazání menších oblastí, než je limit ]-------


        //-----------[ START: Ošetření stejného typu na začátku a konci ]-------
        firstRegion = border_1_Test.get(0);
        lastRegion = border_1_Test.get(border_1_Test.size() - 1);

        if (firstRegion.type == lastRegion.type) {
            border_1_Test.remove(0);
            border_1_Test.add(0,
                    new Region(firstRegion.start - lastRegion.length,
                    firstRegion.length + lastRegion.length,
                    firstRegion.type,
                    firstRegion.space + lastRegion.space));

            border_1_Test.remove(border_1_Test.size() - 1);
        }
        //-----------[ KONEC: Ošetření stejného typu na začátku a konci ]-------

        
        //-----------[ START: Nalezení největší Region + ukládání úhlů ]-------
        //Nalezení největší oblasti
        int biggest = 0;
        angle = 0.0;
        iter = border_1_Test.iterator();

        while (iter.hasNext()) {
            Region temp = (Region) iter.next();
            if (temp.length > biggest && temp.type == 1) {
                biggest = temp.length;
                angle = new Double(temp.start);
            }
        }

        if (angle < 0) {
            angle += polarWidth;
        }

        angle = (angle * 360) / polarWidth;
        
        border_1_Test.clear();
        borderTest.clear();
        //-----------[ KONEC: Nalezení největší oblasti + ukládání úhlů ]-------
    }
}
