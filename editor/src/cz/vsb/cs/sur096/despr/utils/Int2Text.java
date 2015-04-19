
package cz.vsb.cs.sur096.despr.utils;

/**
 * Slouží k převodu kladného celého čísla na řetězec.
 *
 * @author Martin Šurkvoský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/11/07/19:26
 */
public final class Int2Text {

	/** 26 znaků anglické abecedy. */
    private static int MAX = 26;
	/** ANSII hodnota malého 'a'. */
    private static int ANSII_SMALL_A = 97;
    
	/** Zabrání vytvoření instance. */
    private Int2Text() {};
    
    /**
     * Převede celé kladné číslo na řetězec.
     * @param n celé kladné číslo, které má být převedeno na řetězec.
     * @return řetězec reprezentující číslo, čísla od 1 do 26 jsou
	 * kódovány na jednotlivá písmena abecedy. Číslo 27 na 'aa', 28 = 'ab', ...
     */
    public static String getText(int n) {
        
        if (n == 0) {
            return "";
        } else if (n <= MAX) {
            return "" + (char) (ANSII_SMALL_A - 1 + n) ;
        } else {
            double a = (double) n / MAX;
            // 'b' is result of integer division
            int b = (int) a;
            // 'c' is remaider of integer divison
            int c = (int) (((a - b) * MAX) + 0.5);
            
            // if 'a' = 'b' and 'b' > 0 is the last symbol of the sequence:
            // e.g. 26 = z; 27 = aa; 28 = ab; 51 = ay; and 52 must be 'az'!
            // but 52 / MAX = 2, and 2 = b and is not 'az'; 
            // to correct value '2' is transformed as a '1 + MAX'. 
            // And now 1 = a; MAX (26) = z; complete result is 'az'!.
            return (c == 0 && b > 0) ? 
                    (getText(b-1) + getText(MAX)) : (getText(b) + getText(c));
        }
    }
}