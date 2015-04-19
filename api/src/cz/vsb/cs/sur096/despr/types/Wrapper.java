
package cz.vsb.cs.sur096.despr.types;

/**
 * Rozhraní pomocí něhož je možné definovat, tzv. obal pro určité typy. 
 * Aplikace jej vyžívá pro načítaní a ukládání hodnot typů, které nesplňují
 * to, že pro každý parametr, který není statický nebo skrytý ({@code transient}),
 * je  pro něj definovaná veřejná přístupová metoda. Všechny typy které 
 * jsou použity jako vstupní (vnitřní - {@code INNER}) parametry operací 
 * musí tuto podmínku splňovat. Pokud NE, je třeba definovat wrapper, 
 * který je schopný z parametru, který má být uložen a zvonu načten 
 * vytvořit požadovaný objekt. V operaci je pak možné takovéto "nekorektní" typy
 * používat bez obav, že by jejich hodnota nemohla být uložena.
 * 
 * Typickým příkladem muže být objekt typu {@code java.io.File},
 * který uchovává adresu souboru, který reprezentuje. To je to nejdůležitější
 * informace na základě které je možné objekt typu {@code File} znovu vytvořit.
 * Je tak definován parametr {@code path} a jeho přístupové metody.
 * Implementované metody jsou následně schopny na základě této informace 
 * vytvořit nový objekt původního typu {@code java.io.File}.
 * <br>
 * Příklad
 * <pre>
 * <code>
 * public class FileWrapper implements Wrapper<File> {
 *   
 *   private String path;
 *   
 *   public FileWrapper() { }
 *   
 *   public void setPath(String path) {
 *       this.path = path;
 *   }
 *   
 *   public String getPath() {
 *       return path;
 *   }
 *   
 *   &#064;Override
 *   public void wrap(File f) {
 *       this.path = f.getAbsolutePath();
 *   }
 *   
 *   &#064;Override
 *   public File unwrap() {
 *       return new File(path);
 *   }
 *
	 *   &#064;Override
 *   public Class<File> getWrapType() {
 *       return File.class;
 *   }
 * }
 * </code>
 * </pre>
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/10/30/19:44
 */
public interface Wrapper<T> {
    
    /**
     * Metoda která z objektu vytáhne důležité informace, na jejichž základě
     * bude možné objekt znovu vytvořit.
     * @param object objekty který má být uložen.
     */
    public void wrap(T object);
    
    /**
     * Metoda která z uložených hodnot vytvoří původní objekt.
     * @return instanci původního objektu.
     */
    public T unwrap();
    
    /**
     * Metoda která vrátí datový typ původního objektu.
     * @return původní typ zabaleného objektu.
     */
    public Class<T> getWrapType();
}
