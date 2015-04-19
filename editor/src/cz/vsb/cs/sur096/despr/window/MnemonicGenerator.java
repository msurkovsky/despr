
package cz.vsb.cs.sur096.despr.window;

import java.util.List;

/**
 * Pomáhá s výběrem mnenmonic znaků pro lokalizované názvy.
 * 
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/03/05/20:28
 */
public final class MnemonicGenerator {
    
    /**
     * Vygeneruje mnemonic symbol pro dané jméno.
     * @param title jméno pro které má být vygenerován mnemonic symbol.
     * @param tabuMnemonics seznam zakázaných symbolu.
     * @return pokud byly ve jménu alespoň jeden nepožitý znak pak je použit
	 * jako mnemonic symbol, jinak je vrácen {@code null} a mnemonic symbol není
	 * nastaven.
     */
    public static Character getMnemonicChar(String title, List<Character> tabuMnemonics) {
        String smallTitle = title.toLowerCase();
        int len = smallTitle.length();
        for (int i = 0; i < len; i++) {
            Character ch = smallTitle.charAt(i);
            if (ch == ' ') continue;
            if (!tabuMnemonics.contains(ch)) {
				tabuMnemonics.add(ch);
                return ch;
            }
        }
        return null;
    }
}