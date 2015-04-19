/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vsb.cs.sur096.despr.operations.coins.continuesfield;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.operations.coins.AbstractCoinOperation;
import cz.vsb.cs.sur096.despr.operations.coins.standardization.Table;
import cz.vsb.cs.sur096.despr.types.ECoherence;
import cz.vsb.cs.sur096.despr.types.EMethod;
import cz.vsb.cs.sur096.despr.types.images.BinaryImage;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/04/16/08:31
 */
public class FindBiggestRegion extends AbstractCoinOperation implements Displayable{

    private int[][] tmpTable;
    private Table table;
    
    public FindBiggestRegion() {
        method = EMethod.SQUARE;
        coherence = ECoherence.NONE;
        minimumPixels = 15;
        envSize = 3;
    }
    
    @AInputParameter(EInputParameterType.INNER)
    private EMethod method;
    
    @AInputParameter(EInputParameterType.INNER)
    private ECoherence coherence;
    
    @AInputParameter(EInputParameterType.INNER)
    private Integer minimumPixels;
    
    @AInputParameter(EInputParameterType.INNER)
    private Integer envSize;
    
    @AInputParameter(EInputParameterType.OUTER)
    private BinaryImage input;
    
    @AOutputParameter
    private BinaryImage output;
    
    @Override
    public void execute() throws Exception {
        
        checkCoherence(coherence);
        checkMethod(method);
        checkInput(input);
        checkEnvSize(envSize);
        checkMinimumPixels(minimumPixels);
        
        
        int w = input.getWidth();
        int h = input.getHeight();
        
        tmpTable = new int[w][h];

        // inicializace tabulky na nuly
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++)  {
                tmpTable[i][j] = 0;
            }
        }

        table = new Table();
        
        
        // výber metody
        switch(method)
        {
            case CROSS:
                getCFCross(input, w, h);
                break;

            case SQUARE:
                getCFSquare(input, w, h);
                break;

            default:
                throw new Exception(
                        getLocalizeMessage("exception.undefined_method"));
        }

        // na sčítání četností
        ArrayList<Integer> listId = table.getListId();
        int listIdSize = listId.size();
        for (int i = 0; i < listIdSize; i++) {
            // musím zvednout index, protože vnitřní uspořádání tabulky
            // počítá s číslováním od 1
            int idx = i + 1;

            // Pouze pokud jsou id a odkaz ruzné od sebe. Jinak bych přičetl
            // sám sobě to co už mám.
            if (idx != table.getNewId(idx)) {
                int oldFreq = table.getFrequency(idx);
                int currentFreq = table.getFrequency(table.getNewId(idx));

                table.setFrequncy(table.getNewId(idx), oldFreq + currentFreq);
            }
        }

        // smazání duplicit
        listId = new ArrayList<Integer>(new HashSet<Integer>(listId));

        // zprahování oblastí (všechny které mají menší počet pixelů než je
        // zadán smažu)
        for (int id : listId) {
            if (table.getFrequency(table.getNewId(id)) <= minimumPixels) {
                table.setZeroId(table.getNewId(id));
            }
        }
        
        // přečíslování
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                if (tmpTable[i][j] != 0) {
                tmpTable[i][j] = table.getNewId(tmpTable[i][j]);
                }
            }
        }
        
        // Seznam s četnostmi jednotlivých oblastí
        ArrayList<Integer> freqList = table.getListFreq();

        int max = Integer.MAX_VALUE;
        int maxFreq = 0;
        int maxId = 0;
        int freqListSize = freqList.size();
        for (int i = 0; i < freqListSize; i++) {
            int freq = freqList.get(i);
            if (maxFreq < max && maxFreq < freq) {
                maxFreq = freq;
                maxId = i + 1;
            }
        }
        
        output = new BinaryImage(w, h);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++ ) {
                if (tmpTable[i][j] == maxId) {
                    output.setWhite(i, j);
                } else {
                    output.setBlack(i, j);
                }
            }
        }
    }
    
    @Override
    public Image getThumbnail() {
        return output;
    }

    ////////////////////////////////////////////////////////////
    
    public ECoherence getCoherence() { 
        return coherence;
    }

    public void setCoherence(ECoherence coherence) 
            throws NullPointerException {
        
        checkCoherence(coherence);
        this.coherence = coherence;
    }

    public EMethod getMethod() {
        return method;
    }

    public void setMethod(EMethod method) 
            throws NullPointerException {
        
        checkMethod(method);
        this.method = method;
    }

    public BinaryImage getInput() {
        return input;
    }

    public void setInput(BinaryImage input) 
            throws NullPointerException {
        
        checkInput(input);
        this.input = input;
    }

    public Integer getEnvSize() {
        return envSize;
    }

    public void setEnvSize(Integer envSize) 
            throws NullPointerException, IllegalArgumentException {
        
        checkEnvSize(envSize);
        this.envSize = envSize;
    }

    public Integer getMinimumPixels() {
        return minimumPixels;
    }

    public void setMinimumPixels(Integer minimumPixels) 
            throws NullPointerException, IllegalArgumentException {
        
        checkMinimumPixels(minimumPixels);
        this.minimumPixels = minimumPixels;
    }

    public BinaryImage getOutput() {
        return output;
    }

    @Deprecated
    public void setOutput(BinaryImage output) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    
    private void checkCoherence(ECoherence coherence) 
            throws NullPointerException {
        
        if (coherence == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_coherence"));
        }
    }
    
    private void checkMethod(EMethod method) 
            throws NullPointerException {
        if (method == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_method"));
        }
    }
    
    private void checkInput(BinaryImage input) 
            throws NullPointerException {
     
        if (input == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_input"));
        }
    }
    
    private void checkEnvSize(Integer envSize) 
            throws NullPointerException, IllegalArgumentException {
        
        if (envSize == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_env_size"));
        } else if (envSize < 0) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.negative_env_size"));
        }
    }
    
    private void checkMinimumPixels(Integer minimumPixels) 
            throws NullPointerException, IllegalArgumentException {
        
        if (minimumPixels == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_minimum_pixels"));
        } else if (minimumPixels < 0) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.negative_miminum_pixels"));
        }
    }
    ////////////////////////////////////////////////////////////
    
    /**
    * Metoda hledající spojité oblasti pomocí kříže. Výsledky uloží do
    * pomocné tabulky {@code tmpTable}.
    */
   private void getCFCross(BinaryImage img, int w, int h)
   {
      // id nového pixelu, u nové oblasti se inkrementuje
      int idPixel = 0;

      for (int j = h - 1; j >= 0; j--) {
         for (int i = 0; i < w; i++) {

            // pokud se jedna o barvu, která mě zajímá začnu zjišťovat
            // její okolí
            if (img.isWhite(i, j)) {
               // velikost oblasti zajmu v x-ove ose
               int loopI = 0;
               if ((i - envSize) >= 0) {
                  loopI = i - envSize;
               }

               // velikost oblasti zajmu v y-nove ose
               int loopJ = 0;
               if ((j + envSize) < h) {
                  loopJ = j + envSize;
               }

               // hodnota prvního id, na které narazím
               int point = 0;

               // projdu sousedy vlevo od bodu
               for (int x = i - 1; x >= loopI; x--) {
                  if (tmpTable[x][j] != 0) {
                     // pokud je bod nulový předám mu první id, na které narazím
                     if (point == 0) {
                        point = tmpTable[x][j];
                     } else {
                        // aktualni id
                        int currentId = tmpTable[x][j];
                        table.setNewId(currentId, point);
                     }
                  }
               }

               // projdu sousedy dole od bodu
               for (int y = j + 1; y <= loopJ; y++) {
                  if (tmpTable[i][y] != 0) {
                     // pokud je bod nulový, předám mu první nenulovou hodnotu,
                     // na kterou narazím
                     if (point == 0) {
                        point = tmpTable[i][y];
                     } else {
                        // aktuální hodnota id
                        int currentId = tmpTable[i][y];
                        table.setNewId(currentId, point);
                     }
                  }
               }

               // pokud narazím na poslední sloupec a je požadována spojitost
               // přes osu x podívám se na začátek zda tam oblast nepokračuje.
               if (coherence == ECoherence.X_AXIS && i == w - 1) {
                  for ( int x = 0; x < envSize; x++ ) {
                     if (tmpTable[x][j] != 0) {
                        if ( point == 0 ) {
                           point = tmpTable[x][j];
                        } else {
                           int currentId = tmpTable[x][j];
                           table.setNewId(currentId, point);
                        }
                     }
                  }
               }

               // pokud je bod stále nulový, znamená to, že v okolí zkoumaného
               // bodu není žádná dostupná oblast, proto zavedu oblast novou
               if (point == 0) {
                  tmpTable[i][j] = ++idPixel;
                  table.addId(idPixel);
               }
               // jinak přiřadím číslo oblasti, na kterou se odkazuje
               // proměnná point
               else {
                  tmpTable[i][j] = table.getNewId(point);
                  table.incrementFreq(point);
               }
            }
         }
      }
   }

   /**
    * Metoda hledající spojité oblasti pomocí čtverce. Výsledky uloží do
    * pomocné tabulky {@code tmpTable}.
    */
   private void getCFSquare(BinaryImage img, int w, int h) {
      int idPixel = 0;

      // tímto procházením zajistím projití od spodu nahoru
      // a zleva doprava
      for (int j = h - 1; j >= 0; j--) {
         for ( int i = 0; i < w; i++ ) {
            // pokud má pixel barvu, která mě zajímá
            if (img.isWhite(i, j)) {
               int loopI = 0;
               if ((i - envSize) >= 0) {
                  loopI = i - envSize;
               }

               int loopJ = 0;
               if ((j + envSize) < h) {
                  loopJ = j + envSize;
               }

               int loopK = w - 1;
               if ((i + envSize ) < w) {
                  loopK = i + envSize;
               }

               int point = 0;

               // prohledávám oblast ve směru na souřadnici x vlevo od
               // aktuálního bod (smyčka loopI)
               for (int x = i - 1; x >= loopI; x--) {
                  if (tmpTable[x][j] != 0) {
                     if (point == 0) {
                        point = tmpTable[x][j];
                     } else {
                        int currentId = tmpTable[x][j];
                        table.setNewId(currentId, point);
                     }
                  }
               }

               // prohledávám oblasti pod aktuálním bodem (smyčky loopJ a loopK)
               for (int y = j + 1; y <= loopJ; y++) {
                  for (int z = loopI; z <= loopK; z++) {
                     // pokud je požadovaná spojitost na ose x
                     // spojitost zkoumám pouze zde protože na ní mohou
                     // narazit pouze v dopředném směru
                     if (coherence == ECoherence.X_AXIS && i == w - 1) {
                        int difference = envSize - (loopK - i);
                        if (difference > 0) {
                           for(int a = 0; a < difference; a++) {
                              for (int b = y - 1; b < y + difference - 1; b++) {
                                 if (b < h && tmpTable[a][b] != 0) {
                                    if (point == 0) {
                                       point = tmpTable[a][b];
                                    } else {
                                       int currentId = tmpTable[a][b];
                                       table.setNewId(currentId, point);
                                    }
                                 }
                              }
                           }
                        }
                     } // end if ( coherence == X_AXIS );

                     if (tmpTable[ z ][ y ] != 0) {
                        if (point == 0) {
                           point = tmpTable[z][y];
                        } else {
                           int currentId = tmpTable[z][y];
                           table.setNewId(currentId, point);
                        }
                     }
                  }
               }

               // pokud nebyl ještě nastaven bod na nenulovou hodnotu
               // znamená to, že začíná nová oblast
               if (point == 0) {
                  tmpTable[i][j] = ++idPixel;
                  table.addId(idPixel);
               } else {
                  tmpTable[i][j] = table.getNewId(point);
                  // inkrementovat musím vždy už nové id, tzn. odkaz
                  table.incrementFreq(table.getNewId(point));
               }
            }
         }
      }
   }
}
