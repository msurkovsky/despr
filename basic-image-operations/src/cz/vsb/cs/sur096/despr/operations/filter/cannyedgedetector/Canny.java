
package cz.vsb.cs.sur096.despr.operations.filter.cannyedgedetector;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.AbstractImageOperation;
import cz.vsb.cs.sur096.despr.types.images.GrayscaleImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Operace sloužící pro detekci hran. Pro to je využit cannyho hranový detektor.
 * @author Martin Šurkovký, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/09/02/21:13, 2011/12/26/17:00 přidáno orámování ve velikosti
 * masky okolo obrázku {@literal =>} nedetekuje pak okrajové hrany, které mohou být nežádoucí.
 * Po dokončení je obraz opět oříznut.
 */
public class Canny extends AbstractImageOperation implements Displayable {

    private int[] src_1d;
    private int d_w;
    private int d_h;
    private int[] dest_1d;
    
    private int orig_w;
    private int orig_h;
    
    /**
     * Inicializuje operací a nastaví výchozí hodnoty.
	 * <ul>
	 *   <li><b>size</b> 3</li>
	 *   <li><b>theta</b> 0.45</li>
	 *   <li><b>lowthres</b> 6</li>
	 *   <li><b>highthres</b> 25</li>
	 *   <li><b>scale</b> 1.0</li>
	 *   <li><b>offset</b> 0</li>
	 *   <li><b>completeSides</b> false</li>
	 *   <li><b>extendedBackgroundColor</b> Color.WHITE Color.WHITE</li>
	 * </ul>
     */
    public Canny() {
        size = 3;
        theta = 0.45f;
        lowthresh = 6;
        highthresh = 25;
        scale = 1.0f;
        offset = 0;
        completeSides = false;
        extendedBackgroundColor = Color.WHITE;
    }

    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    private Integer size;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2)
    private Float theta;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=3)
    private Integer lowthresh;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=4)
    private Integer highthresh;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=5)
    private Float scale;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=6)
    private Integer offset;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=7)
    private Boolean completeSides;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=8)
    private Color extendedBackgroundColor;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=9)
    private GrayscaleImage inputImage;
    
    @AOutputParameter
    private GrayscaleImage outputImage;
    
    /**
     * Aplikace Cannyho hranového detektoru. Před samotnou detekcí je
	 * obrázek rozšířen o okraj, velikosti masky (parametr {@code size})
	 * s barvou (parametr {@code extendedBackgroundColor}). Po zpracování
	 * je okraj oříznut. Děje se to kvůli toho, aby byl korektně projit
	 * celý obrázek.
     * @throws Exception výjimka je vyvolána v případě že některý
	 * z argumentů metody neprošel kontrolou.
     */
    @Override
    public void execute() throws Exception {
        
        checkSize(size);
        checkTheta(theta);
        checkLowtrhersh(lowthresh);
        checkHighthresh(highthresh);
        checkScale(scale);
        checkOffset(offset);
        checkCopleteSides(completeSides);
        checkExtendedBackgroundColor(extendedBackgroundColor);
        
        // prevedeni vstupniho obrazku na pole, se kterym spolupracuje
        // tato trida (src_1d);
        orig_w = inputImage.getWidth();
        orig_h = inputImage.getHeight();
        d_w = orig_w + 2 * size;
        d_h = orig_h + 2 * size;
        
        
        int img_size = d_w * d_h;
        src_1d = new int[d_w * d_h];
        
        // rozsireni obrazku o okraj, kvuli velikosti masky
        for (int i = 0; i < img_size; i++) {
            src_1d[i] = extendedBackgroundColor.getRGB();
        }
        
        // prekopirovani
        for (int x = 0; x < orig_w; x++) {
            for (int y = 0; y < orig_h; y++) {
                int m = x + size;
                int n = y + size;
                src_1d[n * d_w + m] = inputImage.getRGB(x, y);
            }
        }
        
        if (completeSides) {
            // doplnit sloupec z konce na zacatek
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < d_h - size; y++) {
                    // ze zacatku na konec
                    int m = x + size;
                    int n = y + size;
                    int mTo = d_w - size + x;
                    src_1d[n * d_w + mTo] = src_1d[n * d_w + m];

                    // z konce na zacatek
                    m = d_w - size - x - 1;
                    mTo = size - x - 1;
                    src_1d[n * d_w + mTo] = src_1d[n * d_w + m];
                }
            }
        }
        
        apply_canny();
        
        outputImage = new GrayscaleImage(orig_w, orig_h);

        // oriznuti o okraj 
        for (int x = size; x < d_w - size; x++) {
            for (int y = size; y < d_h - size; y++) {
                int m = x - size;
                int n = y - size;
                outputImage.setRGB(m, n, dest_1d[y * d_w + x]);
            }
        }
    }
    
	/**
	 * Pro náhled je použít výstupní obrázek.
	 * @return výstupní obrázek.
	 */
    @Override
    public Image getThumbnail() {
        return outputImage;
    }
    
    ////////////////////////////////////////////////////////////
    // Get a set metody

    /**
     * Poskytne velikost masky.
     * @return velikost masky.
     */
    public Integer getSize() {
        return size;
    }
    
    /**
     * Nastavuje velikost masky ta může být pouze liché číslo.
     * @param size velikost masky.
     * @throws NullPointerException pokud je parametr {@code size == null}.
     * @throws IllegalArgumentException pokud je velikost záporná nebo sudá.
     */
    public void setSize(Integer size) 
            throws NullPointerException, IllegalArgumentException {
        
        checkSize(size);
        this.size = size;
    }

    /**
     * Vrátí nastavený úhel.
	 * @see sector(theta):int.
     * @return nastavený úhel.
     */
    public Float getTheta() {
        return theta;
    }

    /**
     * Nastaví úhel v radiánech, který ovlivňuje výběr sektoru ve kde se 
	 * hrana nachází.
     * @param theta
     * @throws NullPointerException theta nesmí být prázdný ({@code null}).
     * @throws IllegalArgumentException pokud theta není v rozsahu 0 až Pi vč.
     */
    public void setTheta(Float theta) 
            throws NullPointerException, IllegalArgumentException {
        
        checkTheta(theta);
        this.theta = theta;
    }

    /**
     * Poskytne spodní hranici prahu.
     * @return spodní hranici prahu.
     */
    public Integer getLowthresh() {
        return lowthresh;
    }

    /**
     * Nastaví spodní hranici prahu.
     * @param lowthresh spodní hranice prahu.
     * @throws NullPointerException hranice nesmí být {@code null}.
     * @throws IllegalArgumentException pokud není hranice v rozsahu 0 až 255 vč.
     */
    public void setLowthresh(Integer lowthresh) 
            throws NullPointerException, IllegalArgumentException {
        
        checkLowtrhersh(lowthresh);
        this.lowthresh = lowthresh;
    }

    /**
     * Poskytne horní hranici prahu.
     * @return horní hranici prahu.
     */
    public Integer getHighthresh() {
        return highthresh;
    }

    /**
     * Nastaví horní hranici prahu.
     * @param highthresh horní hranice prahu.
     * @throws NullPointerException hranice nesmí být {@code null}.
     * @throws IllegalArgumentException pokud není hranice v rozsahu 0 až 255 vč.
     */
    public void setHighthresh(Integer highthresh) 
            throws NullPointerException, IllegalArgumentException {
        
        checkHighthresh(highthresh);
        this.highthresh = highthresh;
    }

    /**
     * Poskytne hodnotu scale.
     * @return vrátí hodnotu ovlivňující sílu hrany.
     */
    public Float getScale() {
        return scale;
    }

    /**
     * Nastaví hodnotu ovlivňující sílu hrany. Hodnota 1 je bez zesílení,
	 * co je nad hranu zesílí (je více viditelná), pod naopak zeslabí.
     * @param scale zesílení čí zeslabení hrany.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException pokud je hodnota záporná.
     */
    public void setScale(Float scale) 
            throws NullPointerException, IllegalArgumentException {
        
        checkScale(scale);
        this.scale = scale;
    }

    /**
     * Poskytne hodnotu která je přičtena k celému obrázku.
     * @return hodnotu, která je přičtena k celému obrázku.
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Nastaví hodnotu, která je přičtena k celému obrázku.
     * @param offset hodnota která je přičtena k celému obrázku.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException  pokud není v rozsahu 0 až 255 vč.
     */
    public void setOffset(Integer offset) 
            throws NullPointerException, IllegalArgumentException {
        
        checkOffset(offset);
        this.offset = offset;
    }

    /**
     * Zjistí zda má jí být doplněny konce obrázku.
     * @return {@code true} pokud mají být konce doplněny, 
	 * jinak {@code false}.
     */
    public Boolean isCompleteSides() {
        return completeSides;
    }

    /**
     * Nastaví informaci o tom zda mají být doplněny strany obrázků.
	 * Je vhodné ji využít v případě kdy je zpracováván obraz mince
	 * po polární transformaci. Je tak doplněn kousek z konce na začátek
	 * a opačně čímž jsou korektně na detekovány hrany i na okraji obrázku.
     * @param completeSides 
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setCompleteSides(Boolean completeSides) 
            throws NullPointerException {
        
        checkCopleteSides(completeSides);
        this.completeSides = completeSides;
    }
    
    /**
     * Vrátí barvu rozšířeného pozadí.
     * @return barvu rozšířeného pozadí.
     */
    public Color getExtendedBackgroundColor() {
        return extendedBackgroundColor;
    }
    
    /**
     * Nastaví barvu rozšířeného pozadí. Ta je využita na počátku kdy 
	 * je obrázek rozšířen o velikost masky. Je vhodné aby měla barva 
	 * shodnou barvu s pozadím, tak nebudou na detekovány falešné hrany.
     * @param extendedBackgroundColor barva rozšířeného pozadí.
     * @throws NullPointerException pokud je hodnota barvy prázdná.
     */
    public void setExtendedBackgroundColor(Color extendedBackgroundColor) 
            throws NullPointerException {
        
        checkExtendedBackgroundColor(extendedBackgroundColor);
        this.extendedBackgroundColor = extendedBackgroundColor;
    }

    /**
     * Poskytne vstupní obrázek.
     * @return vstupní obrázek.
     */
    public GrayscaleImage getInputImage() {
        return inputImage;
    }

    /**
     * Nastaví vstupní obrázek.
     * @param inputImage vstupní obrázek.
     * @throws NullPointerException pokud je vstupní obrázek prázdný.
     */
    public void setInputImage(GrayscaleImage inputImage) 
            throws NullPointerException {
        
        checkInputImage(inputImage);
        this.inputImage = inputImage;
    }

    /**
     * Poskytne výstupní obrázek.
     * @return výstupní obrázek.
     */
    public GrayscaleImage getOutputImage() {
        return outputImage;
    }
    
    /**
     * Nenastaví nic, pouze vyhodí výjimku.
     * @param outputImage výstupní obrázek.
     * @throws UnsupportedOperationException Je vyhozena vždy.
	 * @deprecated  u výstupních parametrů jsou set metody definovány pouze
	 * formálně kvůli stejnému přístupu jak ke vstupním tak
	 * výstupním parametrům. Nic méně tyto metody nejsou v aplikaci
	 * nikdy volány.
     */
    @Deprecated
    public void setOutputImage(GrayscaleImage outputImage) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }

    ////////////////////////////////////////////////////////////
    // soukrome pomocne metody
    
    private void checkSize(Integer size) 
            throws NullPointerException, IllegalArgumentException {
        
        if (size == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_size"));
        } else if (size % 2 == 0) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.even_size"));
        }
    }
    
    private void checkTheta(Float theta) 
            throws NullPointerException, IllegalArgumentException {
        if (theta == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_theta"));
        } else if (theta < 0 || theta > Math.PI) {
            throw new IllegalArgumentException(String.format("%s '%.3f'", 
                    getLocalizeMessage("exception.theta_is_out_of_range"), theta));
        }
    }
    
    private void checkLowtrhersh(Integer lowthresh) 
            throws NullPointerException, IllegalArgumentException {
        
        if (lowthresh == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_lowthresh"));
        } else if (lowthresh < 0 || lowthresh > 255) {
            throw new IllegalArgumentException(String.format("%s '%d'", 
                    getLocalizeMessage("exception.lowthresh_out_of_range"),
                    lowthresh));
        }
    }
    
    private void checkHighthresh(Integer highthresh) 
            throws NullPointerException, IllegalArgumentException {
        
        if (highthresh == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_highthresh"));
        } else if (highthresh < 0 || highthresh > 255) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.highthresh_out_of_range"),
                    highthresh));
        }
    }
    
    private void checkScale(Float scale) 
            throws NullPointerException, IllegalArgumentException {
        
        if (scale == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_scale"));
        } else if (scale < 0) {
            throw new IllegalArgumentException(String.format("%s '%.3f'", 
                    getLocalizeMessage("exception.negative_scale"), scale));
        }
    }
    
    private void checkOffset(Integer offset) 
            throws NullPointerException, IllegalArgumentException {
        
        if (offset == null) {
            throw new NullPointerException (
                    getLocalizeMessage("exception.null_offset"));
        } if (offset < 0 || offset > 255) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.offset_is_out_of_range"),
                    offset));
        }
    }
    
    private void checkCopleteSides(Boolean completeSides) 
            throws NullPointerException {
        
        if (completeSides == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_complete_sides"));
        }
    }
    
    private void checkExtendedBackgroundColor(Color extendedBackgroundColor) 
            throws NullPointerException {
        
        if (extendedBackgroundColor == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_extended_background_color"));
        }
    }
    
    private void checkInputImage(GrayscaleImage inputImage) 
            throws NullPointerException {
        
        if (inputImage == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input_image"));
        }
    }
    
    ////////////////////////////////////////////////////////////
    // Vnitrni implemnetace algoritmu 
    
    //Tim's Canny Edge Detection Algorithm
   //Based on algorithm in Machine Vision (pg 169)
   /*
   a) assume the image is grey level (hence RR=GG=BB)
   b) use value &0x000000ff to get the BB value
   c) gaussian smooth image
   d) work out gradient magnitude
   e) apply nonmaxima suppression
   f) threshold and detect edges
   */

   private void apply_canny()
   {
      //Setup local variables
      dest_1d = new int[d_w*d_h];
      int [] tmp_1d = new int[d_w*d_h];
      int [][] tmp_2d = new int[d_w][d_h];
      int [][] p_2d = new int[d_w][d_h];
      int [][] q_2d = new int[d_w][d_h];
      int [][] r_2d = new int[ d_w ][ d_h ];
      int [][] s_2d = new int[ d_w ][ d_h ];
      int [][] m_2d = new int[d_w][d_h];
      double [][] theta_2d = new double[d_w][d_h];
      int [][] nms = new int[d_w][d_h];
      int [][] delta = new int[d_w][d_h];
      int [][] tracked = new int[d_w][d_h];
      int result;
      int tmp = 0;
      int [] tmp2_1d;


      //Set up the output array
      for(int i = 0; i < dest_1d.length; i++)
      {
         //dest_1d[i] = 0xff000000;
         dest_1d[i] = 0xff000000;
      }

      //Smooth the initial image
      tmp_1d = GaussianSmooth.smooth_image(src_1d, d_w, d_h, size, theta);

      //Mask off so that we work with values between 0 and 255
      for(int i = 0; i < tmp_1d.length; i++)
      {
         tmp_1d[i] = tmp_1d[i] & 0x000000ff;
      }

      //Convert 1_d to 2_d for ease of processing in next stages
      for(int i = 0; i < d_w; i++)
      {
         for(int j = 0; j < d_h; j++)
         {
            tmp_2d[i][j] = tmp_1d[i+(j*d_w)];
         }
      }

      //Apply the gradient detection
      for(int i = 0; i < (d_w-1); i++)
      {
         for(int j = 0; j < (d_h-1); j++)
         {
            p_2d[i][j] = (tmp_2d[i][j+1]-tmp_2d[i][j]+
               tmp_2d[i+1][j+1]-tmp_2d[i+1][j])/2;

            q_2d[i][j] = (tmp_2d[i][j]-tmp_2d[i+1][j]+
               tmp_2d[i][j+1]-tmp_2d[i+1][j+1])/2;

            m_2d[i][j] = (int)Math.sqrt(Math.pow((double)p_2d[i][j],2)+
               Math.pow((double)q_2d[i][j],2));
            
            theta_2d[i][j] = Math.atan2((double)(q_2d[i][j]),(double)(p_2d[i][j]));
         }
      }

      //Resize image 
      d_w--;
      d_h--;

      //Apply the nonmaxima suppression

      //First calculate which sector each line appears in

      for(int i = 0; i < d_w; i++)
      {
         for(int j = 0; j < d_h; j++)
         {
            delta[i][j] = sector(theta_2d[i][j]);
         }
      }


      //Then apply non maximal suppression
      for(int i = 0; i < (d_w-1); i++){ nms[i][0] = 0; nms[i][d_h-1] = 0; }
      for(int j = 0; j < (d_h-1); j++){ nms[0][j] = 0; nms[d_w-1][j] = 0; }
      for(int i = 1; i < (d_w-1); i++)
      {
         for(int j = 1; j < (d_h-1); j++)
         {
            nms[i][j] = suppress(m_2d, delta[i][j], i, j,lowthresh);
         }
      }

      //Resize again!
      d_w = d_w - 2;
      d_h = d_h - 2;

      //Track the image
      tracked = apply_track(nms, d_w, d_h, lowthresh, highthresh);

      //Calculate the output array
      for(int i = 0; i < d_w; i++)
      {
         for(int j = 0; j < d_h; j++)
         {
            result = tracked[i][j];
            result = (int) (result * scale);
            result = result + offset;
            
            if(result > 255){result = 255;}
            
            if(result < 0){result = 0;}
            
            dest_1d[(i+(j*(d_w+3)))] = 0xff000000 | 
               result << 16 | result << 8 | result;
         }
      }

      //Change the sizes back
      d_w = d_w + 3;
      d_h = d_h + 3;
   }

   //Function to check which sector the line is in (see Machine Vision pg 171)
   private int sector(double theta)
   {

      //Converting into degrees from radians, and moving to lie between 0 and 360
      theta = Math.toDegrees(theta);
      theta = theta + 270 ; 
      theta = theta % 360;


      if((theta >= 337.5) || (theta < 22.5) || ((theta >= 157.5) && (theta < 202.5)))
      {
         return 0;
      }
      
      if(((theta >= 22.5) && (theta < 67.5)) || ((theta >=202.5) && (theta < 247.5)))
      {
         return 1;
      }
      
      if(((theta >= 67.5) && (theta < 112.5)) || ((theta >=247.5) && (theta < 292.5)))
      {
         return 2;
      }
      
      if(((theta >= 112.5) && (theta < 157.5)) || ((theta >= 292.5) && (theta < 337.5)))
      {
         return 3;
      }
      
      return 0;
   }

   // Function to apply non maxima suppression to the image array
   private int suppress(int[][] m_2d, int sector, int i, int j, int lowthresh)
   {

      int tmp = m_2d[i][j];
      if (tmp < lowthresh) return 0;

      //if (318 < i && i < 322 && 113 < j && j < 117)System.out.println("ij("+i+","+j+") sector: "+sector+" neigh: "+m_2d[i-1][j-1]+" "+m_2d[i-1][j]+" "+m_2d[i-1][j+1]+" "+m_2d[i][j-1]+" "+m_2d[i][j]+" "+m_2d[i][j+1]+" "+m_2d[i+1][j-1]+" "+m_2d[i+1][j]+" "+m_2d[i+1][j+1]);

      if(sector == 0)
      {
         if((m_2d[i+1][j] >= tmp) || (m_2d[i-1][j] > tmp))
         {
            return 0;
         }
         else 
         {
            return tmp;
         }
      }
      
      if(sector == 1)
      {
         if((m_2d[i+1][j+1] >= tmp) || (m_2d[i-1][j-1] > tmp))
         {
            return 0;
         }
         else 
         {
            return tmp;
         }
      }
      if(sector == 2)
      {
         if((m_2d[i][j+1] >= tmp) || (m_2d[i][j-1] > tmp))
         {
            return 0;
         }
         else 
         {
            return tmp;
         }
      }
      
      if(sector == 3)
      {
         if((m_2d[i+1][j-1] >= tmp) || (m_2d[i-1][j+1] > tmp))
         {
            return 0;
         }
         else 
         {
            return tmp;
         }
      }
      
      System.out.println("Canny - Unidentified sector "+sector+" at ij: "+i+" "+j);
      
      return 0;
   }

   /*The function apply_track is used to track the image for suitable lines. It
   *does this by first finding points above the highthreshold. When it finds
   *such a point it then finds surrounding point which are above the low threshold
   *and tracks along them, continually finding points above the low threshold. This
   *is done until the tracker explores all paths from the original point. It then
   *finds the next starting point and starts tracking again.
   */  

   private int [][] apply_track(int [][] input, int width, int height, 
   int lowthresh,int highthresh) 
   {

      d_w = width;
      d_h = height;

      int [][] marked = new int[d_w][d_h];
      int [][] tracked = new int[d_w][d_h];

      Stack to_track = new Stack();

      //Initialise the marked array
      for(int i = 0; i < d_w; i++)
      {
         for(int j = 0; j < d_h; j++)
         {
            marked[i][j] = 0;
         }
      }

      //Now find all the starting points for the tracking
      for(int i = 0; i < d_w; i++)
      {
         for(int j = 0; j < d_h; j++)
         {
            //If the point is unmarked and above high threshold then track
            if((input[i][j] > highthresh) && (marked[i][j] == 0))
            {
               marked = track(input, marked, to_track, lowthresh, i, j);
            }
         }
      }

      //Now clear all the pixels in the input which are unmarked
      for(int i = 0; i < d_w; i++)
      {
         for(int j = 0; j < d_h; j++)
         {
            if(marked[i][j] == 0)
            {
               tracked[i][j] = 0;
            }
            else 
            {
               tracked[i][j] = input[i][j];
            }
         }
      }
      return tracked;
   }

   /*The function track is called once a starting point for tracking has been
   *found. When this happens, this function follows all possible paths above
   *the threshold by placing unsearched paths on the stack. Each time a path 
   *is looked at it's pixels are marked. This continues until the stack is
   *empty, at which point the new array of marked paths is returned.
   */

   private int [][] track(int [][] input, int [][] marked, Stack to_track, 
   int thresh, int i, int j)
   {

      //empty represents when the stack is empty
      boolean empty = false;
      int a;
      int b;
      //Create a point to represent where to start the tracking from
      Point current = new Point(i,j);

      //Push the initial point onto the stack
      to_track.push(current);
      while(!empty)
      {
         try
         {

            //Take the top pixel from the stack
            current = (Point)to_track.pop();
            //Find it's co-ordinates
            a = current.x;
            b = current.y;
            //Now check neighbourhood and add to stack anything above thresh
            //Only done if pixel is currently unmarked
            if(marked[a][b] == 0)
            {

               //Try and track from each neighbouring point
               if(a > 0 && b > 0)
               {
                  if(input[a-1][b-1] > thresh)
                  {
                     current = new Point((a-1), (b-1));
                     to_track. push(current);
                  }
               }

               if(b > 0)
               {
                  if(input[a][b-1] > thresh)
                  {
                     current = new Point((a), (b-1));
                     to_track. push(current);
                  }
               }

               if(a < (d_w-1) && b > 0)
               {
                  if(input[a+1][b-1] > thresh)
                  {
                     current = new Point((a+1), (b-1));
                     to_track. push(current);
                  }
               }

               if(a > 0)
               {
                  if(input[a-1][b] > thresh)
                  {
                     current = new Point((a-1), (b));
                     to_track. push(current);
                  }
               }

               if(a < (d_w-1))
               {
                  if(input[a+1][b] > thresh)
                  {
                     current = new Point((a+1), (b));
                     to_track. push(current);
                  }
               }

               if(a > 0 && b < (d_h-1))
               {
                  if(input[a-1][b+1] > thresh)
                  {
                     current = new Point((a-1), (b+1));
                     to_track. push(current);
                  }
               }

               if( b < (d_h-1))
               {
                  if(input[a][b+1] > thresh)
                  {
                     current = new Point((a), (b+1));
                     to_track. push(current);
                  }
               }

               if(a < (d_w-1) && b < (d_h-1))
               {
                  if(input[a+1][b+1] > thresh)
                  {
                     current = new Point((a+1), (b+1));
                     to_track. push(current);
                  }
               }

               //Mark this pixel as having been tracked from
               marked[a][b] = 1;
            }
         } 

         //If stack empty, then set the empty flag to true
         catch(EmptyStackException e)
         {
            empty = true;
         }
      }
      return marked;
   }
}
