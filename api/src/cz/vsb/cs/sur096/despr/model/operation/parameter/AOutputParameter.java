
package cz.vsb.cs.sur096.despr.model.operation.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotace slouží pro označení výstupních parametrů uživatelsky definovaných 
 * operací. K takto označeným parametrům je třeba napsat veřejné přístupové
 * metody ({@code get/set}), konvence JavaBeans. Nicméně aplikace využívá pouze
 * {@code get} metod. {@code Set} metody je třeba definovat pouze 
 * formálně kvůli jednotnosti zpracovávání parametrů. Je tedy nutné definovat
 * pouze hlavičku metody a tělo může být prázdné, ovšem je lepší aby metoda
 * vyhodila výjimku {@code UnsupportedOperationException}.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version  2011/07/03/18:41
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AOutputParameter {
    /**
     * Jméno vstupního parametru, na kterém výstupní parametr závisí. 
     * Touto závislostí je myšlen datový typ. Parametry totiž respektují 
     * dědičnost typů. Tzn. do parametru typy {@code Object} muže být
     * prakticky poslán jakýkoliv datový typ, ovšem operace by jej měla umět
     * zpracovat. Pokud to umí je v některých případech žádoucí, aby se
     * takto zadaný typ projevil i na některém z výstupních portů. 
	 * Jiným příkladem může být operace, která provádí změnu velikosti obrázku. 
	 * Operace má definován vstup jako barevný obrázek a stejně tak výstup. 
	 * Nyní je do takovéto operace předán černobílí obrázek. V tu chvíli je
	 * jasné že na výstupu bude také černobílý obrázek a že je ve grafické
	 * nadstavbě vidět že typ byl konkretizován zajistí toto označení.
	 * Nicméně autor komponenty se musí postarat o to aby v metodě {@code
	 * IOperation.execute()} byl správně přetypován výstupní parametr.
     * <br> 
     * Příklad:
	 * <pre>
	 * <code>
     *  &#064;AInputParameter(EInputParameterType.OUTER)
     *  ColorImage inputImg;
     *  
     *  &#064;AOutputParameter(depends"inputImg")
     *  ColorImage outputImg;
	 * </code>
	 * </pre>
     * 
     * @return jméno vázaného parametru.
     */
    String depends() default "";
    
    /**
     * Pořadí výstupního parametru. V zadaném pořadí budou seřazeny výstupní
     * porty ve vizualizaci operace. Pokud hodnota není zadána, jsou porty 
     * seřazeny lexograficky podle jména.
     * 
     * @return pořadí portu.
     */
    int enteringAs() default 100;
}
