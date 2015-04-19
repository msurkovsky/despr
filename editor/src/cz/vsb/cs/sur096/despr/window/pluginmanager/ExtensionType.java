
package cz.vsb.cs.sur096.despr.window.pluginmanager;

/**
 * Struktura uchovávající informace o konkrétním typovém rozšíření.
 * Pamatuje si zda již bylo použito a kolikrát bylo použito.
 * Navíc je možné je porovnávat kvůli řazeni.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/19/13:02
 */
public class ExtensionType implements Comparable<ExtensionType> {
    
    /** Typ konkrétního rozšíření. */
    private Class type;
    /** Počet použití. */
    private int countOfUse;
    
    /**
     * Iniciuje typové rozšíření.
     * @param type typ který je typovým rozšířením.
     */
    public ExtensionType(Class type) {
        this(type, 0);
    }
    
    /**
     * Iniciuje typové rozšíření s konkrétním počtem použití.
     * @param type typ který je typovým rozšířením.
     * @param countOfUse počet použití (používá se při načítaní seznamu
	 * ze souboru).
     */
    public ExtensionType(Class type, int countOfUse) {
        this.type = type;
        this.countOfUse = countOfUse;
    }
    
    /**
     * Nastaví zda již byl typ použit. Vnitřně si pamatuje
	 * i počet použití, takže když se pak nastaví 10x used = true,
	 * pak se musí i 10x nastavit used = false aby byl typ považován nepoužitý.
     * @param used je typ použitý?
     */
    public void setUsed(boolean used) {
        if (used) {
            countOfUse++;
        } else {
            if (countOfUse > 0) {
                countOfUse--;
            } else {
                throw new RuntimeException("Unexpected using. "
                        + "Extension type is not used. Why do you set 'false'?");
            }
        }
    }
    
    /**
     * Zjistí zda je typ použitý.
     * @return {@code true} pokud je počet použití větší jak nula,
	 * pokud je roven nule {@code false}.
     */
    public boolean isUsed() {
        return (countOfUse > 0 ? true : false);
    }
    
    /**
     * Poskytne typ uloženého rozšíření.
     * @return typ uloženého rozšíření.
     */
    public Class getType() {
        return type;
    }
    
    /**
     * Poskytne počet použití.
     * @return počet použití.
     */
    public int getCountOfUse() {
        return countOfUse;
    }

    /**
     * Porovná dvě typové rozšíření. Přednost mají ty které jsou nepoužité
	 * pak se v každé skupině řadí abecedně.
     * @param o jiné typové rozšíření k porovnání.
     * @return 1 pokud je první použitý a druhý ne, -1 v opačném případě a
	 * pokud jsou stejné porovnají se lexograficky.
     */
    @Override
    public int compareTo(ExtensionType o) {
        if (isUsed() && !o.isUsed()) {
            return 1;
        } else if (!isUsed() && o.isUsed()) {
            return -1;
        } else {
            Class oType = o.getType();
            return type.getCanonicalName().compareTo(oType.getCanonicalName());
        }
    }
}