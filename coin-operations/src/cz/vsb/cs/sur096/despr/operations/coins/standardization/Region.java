
package cz.vsb.cs.sur096.despr.operations.coins.standardization;

/**
 * Struktura uchovávající informace o oblasti, která se používá
 * pří hledání nejdelší spojité oblasti v histogramu, podle které
 * se bude normovat natočení.
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/02/16:06
 */
class Region {
    
	/** index kde oblast začíná */
    int start;
	/** délka oblasti */
    int length;
	/** typ oblasti */
    int type;
	/** plocha kterou zábírá */
    int space;
    
    /**
     * Konstruktor iniciující oblast.
     * @param start počáteční index.
     * @param length délka oblasti.
     * @param type typ oblasti.
     * @param space plocha oblasti.
     */
    Region(int start, int length, int type, int space) {
        this.start = start;
        this.length = length;
        this.type = type;
        this.space = space;
    }
}
