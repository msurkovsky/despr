package cz.vsb.cs.sur096.despr.model.operation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotace slouží pro označení vstupních parametrů uživatelem definované
 * operace. K takto označeným parametrům je následně třeba definovat veřejné 
 * přístupové metody {@code get/set} ve stejném formátu jako JavaBeans. 
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/07/03/18:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AInputParameter {
    /**
     * Počáteční nastavení typu vstupního parametrů. Vstupní parametry
     * je možné rozdělit do dvou typů:
     * <ol>
     *  <li><b>INNER</b> tzv. vnitřní. Jedna se o parametry které jsou nastaveny
     * na začátku operace a pro celý průběh zpracování operace jsou neměnné. Také
     * je možné je chápat jako konstanty, které jsou neměnné po celý průběh 
     * zpracovávání.?</li>
     * <li><b>OUTER</b> tzv. vnější. Jedná se o parametry, které jejichž hodnoty
     * jsou zjišťovány v průběhu výpočtu. Jsou to vstupní porty operace, pomocí
     * kterých jsou posilány data z jiných operací. Tyto parametry je možné chápat
     * jako hodnoty, které jsou dynamicky zjišťovaný až průběhu zpracování. Typicky
     * si operace mezi sebou předávají obrázky.</li>
     * </ol>
     * Většinu vstupních parametru je možné při definici procesu přepínat, podle
     * potřeby.
     * 
     * @return vrací typ parametru {@code INNER/OUTER}.
     */
    EInputParameterType value();
    
    /**
     * Indikuje zda zamčená možnost přepínat typ vstupního parametru ({@code INNER/OUTER}). 
     * Výchozí hodnota přepínaní typů nezakazuje, umožňuje to vetší flexibilitu
     * použití jedné a té samé operace. Ovšem vyskytnou se případy kdy autor modulu
     * chce u některých parametru tuto možnost zakázat. Typicky se jedna o
     * vstupní parametry operací definovaných pomocí rozhraní 
     * {@code IRootOperation}. Kde je naopak žádoucí, aby vstupní parametry
     * těchto operací byly pouze vnitřní {@code INNER}.
     * 
     * @return {@code true} pokud je možnost přepínaní parametrů zakázána,
     * jinak {@code false}.
     */
    boolean lock() default false;
    
    /**
     * Určuje pořadí argumentů v jakém budou prezentovaný v aplikaci.
     * Pokud je hodnota dvou parametru stejná porovnávají se lexograficky.
     * Výchozí hodnota 100 znamená že u argumentů, které nebudou mít explicitně
     * zadané pořadí se budou porovnávat lexograficky podle jména. Je předpoklad,
	 * že jedna operace nebude obsahovat 100 vstupních parametrů. Již více jak
	 * deset vstupních parametrů je moc. Mohou se najít ojedinělé případy kdy je
	 * nutné použít větší množství vstupních parametrů, ale obecně je to spíš znak
	 * toho že je operací možné rozdělit na více jednodušších operací.
     * 
     * @return pořadí argumentu.
     */
    int enteringAs() default 100;
}
