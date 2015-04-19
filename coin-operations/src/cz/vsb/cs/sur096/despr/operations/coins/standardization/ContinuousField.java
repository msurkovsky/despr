package cz.vsb.cs.sur096.despr.operations.coins.standardization;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Třída, která slouží pro hledání spojitých oblastí v obraze.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="maitlo:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2009/03/19/19:42
 */
public class ContinuousField
{
   // Konstanty pro metody
   /** Metoda křížem. */
   public final static int CROSS = 1;
   /** Metoda čtvercem */
   public final static int SQUARE = 2;


   // Konstanty pro spojitost
   /** Obrázek není nikde spojitý */
   public final static int NONE = 3;
   /** Obrazek je spojitý na ose x. */
   public final static int X_AXIS = 4;
//   /** Obrazek je spojitý na ose y. */
//   public final static int Y_AXIS = 5;

   /** Barva, pro kterou budu hledat spojité oblasti */
   private int color;

   /**
    * Metoda, která se použije při hledání:
    * <ul>
    *    <li>Pomocí <b>kříže</b> vhodna pro plochy.
    *       <strong><i>CROSS</i></strong></li>
    *    <li>Pomocí <b>čtverce</b> vhodná pro křivky.
    *       <strong><i>SQUARE</i></strong></li>
    * </ul>
    */
   private int method;

   /**
    * Spojitost, v obraze
    * <ul>
    *    <li>Žádná <strong><i>NONE</i></strong></li>
    *    <li>Přes x-ovou osu <strong><i>X_AXIS</i></strong></li>
    * </ul>
    */
   private int coherence;

   /**
    * Minimální počet pixelů v oblasti, aby se to dalo považovat
    * za oblast, jinak se to bere jako šum a výsledném obrázku se
    * nahradí pozadím.
    */
   private int minimumPixels;

   /**
    * Tolerance, velikost masky jakou se prohledává okolí. Pro 1 to znamená
    * že v okolí musí být nějaký "bílí" pixel se spojitý v oblast. U 2 znamená
    * že hledá "bílé" pixely ve vzdálenosti 2 {@literal =>} že může být mezi nimi 1 "černý".
    * U 3 ... .
    */
   private int envSize;

   /**
    * Pomocná tabulka ve které se budou počítat spojité oblasti.
    */
   private int[][] tmpTable;

   /** Vstupní obrázek */
   private BufferedImage in;

   /** Šířka vstupního obrázku */
   private int w;
   /** Výška vstupního obrázku */
   private int h;

   /** Vstupní obrázek */
   private BufferedImage out;

   /**
    * Tabulka pro ukládání informací o spojitých oblastech.
    */
   private Table table;

   /**
    * Konstruktor přebírající obrázek typu {@code BufferedImage}.
    * @param img vstupní obrázek
    * @param color barva, pro kterou mě zajímají spojité oblasti.
    * @param method metoda hledání spojitých oblastí:
    * <ul>
    *    <li>Pomocí <b>kříže</b> vhodná pro plochy.
    *       <strong><i>CROSS</i></strong></li>
    *    <li>Pomocí <b>čtverce</b> vhodná pro křivky.
    *       <strong><i>SQUARE</i></strong></li>
    * </ul>
    * @param coherence spojitost v obraze:
    * <ul>
    *    <li>Žádná <strong><i>NONE</i></strong></li>
    *    <li>Přes x-ovou osu <strong><i>X_AXIS</i></strong></li>
    * </ul>
    * @param minimumPixels minimální počet pixelů v oblasti, aby nebyla zahozena
    * @param envSize velikost zkoumaného okolí.
    */
   public ContinuousField( BufferedImage img, int color, int method,
           int coherence, int minimumPixels, int envSize )
   {
      this.in = img;
      this.w = img.getWidth();
      this.h = img.getHeight();
      out = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
      this.color = color;
      this.method = method;
      this.coherence = coherence;
      this.minimumPixels = minimumPixels;
      this.envSize = envSize;

      tmpTable = new int[ w ][ h ];

      // inicializace tabulky na nuly
      for ( int i = 0; i < w; i++ )
      {
         for ( int j = 0; j < h; j++ )
         {
            tmpTable[ i ][ j ] = 0;
         }
      }

      table = new Table();
   }
   /**
    * Konstruktor přebírající obrázek jako dvourozměrné pole.
    * @param img vstupní obrázek
    * @param color barva, pro kterou mě zajímají spojité oblasti.
    * @param method metoda hledání spojitých oblastí:
    * <ul>
    *    <li>Pomocí <b>kříže</b> vhodná pro plochy.
    *       <strong><i>CROSS</i></strong></li>
    *    <li>Pomocí <b>čtverce</b> vhodná pro křivky.
    *       <strong><i>SQUARE</i></strong></li>
    * </ul>
    * @param coherence spojitost v obraze:
    * <ul>
    *    <li>Žádná <strong><i>NONE</i></strong></li>
    *    <li>Přes x-ovou osu <strong><i>X_AXIS</i></strong></li>
    * </ul>
    * @param minimumPixels minimální počet pixelů v oblasti, aby nebyla zahozena
    * @param envSize velikost zkoumaného okolí.
    */
   public ContinuousField( int[][] img, int w, int h, int color, int method,
           int coherence, int minimumPixels, int envSize )
   {
      in = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
      for ( int i = 0; i < w; i++ )
      {
         for ( int j = 0; j < h; j++ )
         {
            if ( img[ i ][ j ] > 0 )
            {
               in.setRGB( i, j, Color.WHITE.getRGB() );
            }
            else
            {
               in.setRGB( i, j, Color.BLACK.getRGB() );
            }
         }
      }

      this.w = w;
      this.h = h;
      this.color = color;
      this.method = method;
      this.coherence = coherence;
      this.minimumPixels = minimumPixels;
      this.envSize = envSize;

      tmpTable = new int[ w ][ h ];

      // inicializace tabulky na nuly
      for ( int i = 0; i < w; i++ )
      {
         for ( int j = 0; j < h; j++ )
         {
            tmpTable[ i ][ j ] = 0;
         }
      }

      table = new Table();
   }
   
   /**
    * Konstruktor přebírající obrázek jako jednorozměrné pole.
    * @param img vstupní obrázek
    * @param color barva, pro kterou mě zajímají spojité oblasti.
    * @param method metoda hledání spojitých oblastí:
    * <ul>
    *    <li>Pomocí <b>kříže</b> vhodná pro plochy.
    *       <strong><i>CROSS</i></strong></li>
    *    <li>Pomocí <b>čtverce</b> vhodná pro křivky.
    *       <strong><i>SQUARE</i></strong></li>
    * </ul>
    * @param coherence spojitost v obraze:
    * <ul>
    *    <li>Žádná <strong><i>NONE</i></strong></li>
    *    <li>Přes x-ovou osu <strong><i>X_AXIS</i></strong></li>
    * </ul>
    * @param minimumPixels minimální počet pixelů v oblasti, aby nebyla zahozena
    * @param envSize velikost zkoumaného okolí.
    */
   public ContinuousField( int[] img, int w, int h, int color, int method,
           int coherence, int minimumPixels, int envSize )
   {
      in = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
      for ( int i = 0; i < w; i++ )
      {
         for ( int j = 0; j < h; j++ )
         {
            if ( img[ j * w + i ] > 0 )
            {
               in.setRGB( i, j, Color.WHITE.getRGB() );
            }
            else
            {
               in.setRGB( i, j, Color.BLACK.getRGB() );
            }
         }
      }

      this.w = w;
      this.h = h;
      this.color = color;
      this.method = method;
      this.coherence = coherence;
      this.minimumPixels = minimumPixels;
      this.envSize = envSize;

      tmpTable = new int[ w ][ h ];

      // inicializace tabulky na nuly
      for ( int i = 0; i < w; i++ )
      {
         for ( int j = 0; j < h; j++ )
         {
            tmpTable[ i ][ j ] = 0;
         }
      }

      table = new Table();
   }

   /**
    * Metoda hledající spojité oblasti pomocí kříže. Výsledky uloží do
    * pomocné tabulky {@code tmpTable}.
    */
   private void getCFCross()
   {
      // id nového pixelu, u nové oblasti se inkrementuje
      int idPixel = 0;

      for ( int j = h - 1; j >= 0; j-- )
      {
         for ( int i = 0; i < w; i++ )
         {
            int c = in.getRGB( i, j );

            // pokud se jedna o barvu, která mě zajímá začnu zjišťovat
            // její okolí
            if ( c == color )
            {
               // velikost oblasti zajmu v x-ove ose
               int loopI = 0;
               if ( ( i - envSize ) >= 0 )
               {
                  loopI = i - envSize;
               }

               // velikost oblasti zajmu v y-nove ose
               int loopJ = 0;
               if ( ( j + envSize ) < h )
               {
                  loopJ = j + envSize;
               }

               // hodnota prvního id, na které narazím
               int point = 0;

               // projdu sousedy vlevo od bodu
               for ( int x = i - 1; x >= loopI; x-- )
               {
                  if ( tmpTable[ x ][ j ] != 0 )
                  {
                     // pokud je bod nulový předám mu první id, na které narazím
                     if ( point == 0 )
                     {
                        point = tmpTable[ x ][ j ];
                     }
                     else
                     {
                        // aktualni id
                        int currentId = tmpTable[ x ][ j ];
                        table.setNewId( currentId, point );
                     }
                  }
               }

               // projdu sousedy dole od bodu
               for ( int y = j + 1; y <= loopJ; y++ )
               {
                  if ( tmpTable[ i ][ y ] != 0 )
                  {
                     // pokud je bod nulový, předám mu první nenulovou hodnotu,
                     // na kterou narazím
                     if ( point == 0 )
                     {
                        point = tmpTable[ i ][ y ];
                     }
                     else
                     {
                        // aktuální hodnota id
                        int currentId = tmpTable[ i ][ y ];
                        table.setNewId( currentId, point );
                     }
                  }
               }

               // pokud narazím na poslední sloupec a je požadována spojitost
               // přes osu x podívám se na začátek zda tam oblast nepokračuje.
               if ( coherence == X_AXIS && i == w - 1 )
               {
                  for ( int x = 0; x < envSize; x++ )
                  {
                     if ( tmpTable[ x ][ j ] != 0 )
                     {
                        if ( point == 0 )
                        {
                           point = tmpTable[ x ][ j ];
                        }
                        else
                        {
                           int currentId = tmpTable[ x ][ j ];
                           table.setNewId( currentId, point );
                        }
                     }
                  }
               }

               // pokud je bod stále nulový, znamená to, že v okolí zkoumaného
               // bodu není žádná dostupná oblast, proto zavedu oblast novou
               if ( point == 0 )
               {
                  tmpTable[ i ][ j ] = ++idPixel;
                  table.addId( idPixel );
               }
               // jinak přiřadím číslo oblasti, na kterou se odkazuje
               // proměnná point
               else
               {
                  tmpTable[ i ][ j ] = table.getNewId( point );
                  table.incrementFreq( point );
               }
            }
         }
      }

   }

   /**
    * Metoda hledající spojité oblasti pomocí čtverce. Výsledky uloží do
    * pomocné tabulky {@code tmpTable}.
    */
   private void getCFSquare()
   {
      int idPixel = 0;

      // tímto procházením zajistím projití od spodu nahoru
      // a zleva doprava
      for ( int j = h - 1; j >= 0; j-- )
      {
         for ( int i = 0; i < w; i++ )
         {
            int c = in.getRGB( i, j );

            // pokud má pixel barvu, která mě zajímá
            if ( c == color )
            {
               int loopI = 0;
               if ( ( i - envSize ) >= 0 )
               {
                  loopI = i - envSize;
               }

               int loopJ = 0;
               if ( ( j + envSize ) < h )
               {
                  loopJ = j + envSize;
               }

               int loopK = w - 1;
               if (  ( i + envSize ) < w )
               {
                  loopK = i + envSize;
               }


               int point = 0;

               // prohledávám oblast ve směru na souřadnici x vlevo od
               // aktuálního bod (smyčka loopI)
               for ( int x = i - 1; x >= loopI; x-- )
               {
                  if ( tmpTable[ x ][ j ] != 0 )
                  {
                     if ( point == 0 )
                     {
                        point = tmpTable[ x ][ j ];
                     }
                     else
                     {
                        int currentId = tmpTable[ x ][ j ];
                        table.setNewId( currentId, point );
                     }
                  }
               }

               // prohledávám oblasti pod aktuálním bodem (smyčky loopJ a loopK)
               for ( int y = j + 1; y <= loopJ; y++ )
               {
                  for ( int z = loopI; z <= loopK; z++ )
                  {
                     // pokud je požadovaná spojitost na ose x
                     // spojitost zkoumám pouze zde protože na ní mohou
                     // narazit pouze v dopředném směru
                     if ( coherence == X_AXIS && i == w - 1 )
                     {
                        int difference = envSize - ( loopK - i );
                        if ( difference > 0 )
                        {
                           for( int a = 0; a < difference; a++ )
                           {
                              for ( int b = y - 1; b < y + difference - 1; b++ )
                              {
                                 if ( b < h && tmpTable[ a ][ b ] != 0 )
                                 {
                                    if ( point == 0 )
                                    {
                                       point = tmpTable[ a ][ b ];
                                    }
                                    else
                                    {
                                       int currentId = tmpTable[ a ][ b ];
                                       table.setNewId( currentId, point );
                                    }
                                 }
                              }
                           }
                        }
                     } // end if ( coherence == X_AXIS );

                     if ( tmpTable[ z ][ y ] != 0 )
                     {
                        if ( point == 0 )
                        {
                           point = tmpTable[ z ][ y ];
                        }
                        else
                        {
                           int currentId = tmpTable[ z ][ y ];
                           table.setNewId( currentId, point );
                        }
                     }
                  }
               }

               // pokud nebyl ještě nastaven bod na nenulovou hodnotu
               // znamená to, že začíná nová oblast
               if ( point == 0 )
               {
                  tmpTable[ i ][ j ] = ++idPixel;
                  table.addId( idPixel );
               }
               else
               {
                  tmpTable[ i ][ j ] = table.getNewId( point );

                  // inkrementovat musím vždy už nové id, tzn. odkaz
                  table.incrementFreq( table.getNewId( point ) );
               }
            }
         }
      }
   }

   /**
    * Metoda hledající spojité oblasti.
    */
   public void searchContinuosField()
   {
      // výber metody
      switch( method )
      {
         case CROSS:
            getCFCross();
            break;

         case SQUARE:
            getCFSquare();
            break;

         default:
            new Exception( "Nedefinovaná metoda! " +
                    "Použíjte definované konstanty.\n" );
      }

      // na sčítání četností
      ArrayList<Integer> listId = table.getListId();
      int listIdSize = listId.size();
      for ( int i = 0; i < listIdSize; i++ )
      {
         // musím zvednout index, protože vnitřní uspořádání tabulky
         // počítá s číslováním od 1
         int idx = i + 1;

         // Pouze pokud jsou id a odkaz ruzné od sebe. Jinak bych přičetl
         // sám sobě to co už mám.
         if ( idx != table.getNewId( idx ) )
         {
            int oldFreq = table.getFrequency( idx );
            int currentFreq = table.getFrequency( table.getNewId( idx ) );

            table.setFrequncy( table.getNewId( idx ), oldFreq + currentFreq );
         }
      }

      // smazání duplicit
      listId = new ArrayList<Integer>( new HashSet<Integer>( listId ) );

      // zprahování oblastí (všechny které mají menší počet pixelů než je
      // zadán smažu)
      for ( int id : listId )
      {
         if ( table.getFrequency( table.getNewId( id ) ) <= minimumPixels )
         {
            table.setZeroId( table.getNewId( id ) );
         }
      }

      // přečíslování
      for ( int j = 0; j < h; j++ )
      {
         for ( int i = 0; i < w; i++ )
         {
            if ( tmpTable[ i ][ j ] != 0 )
            {
               tmpTable[ i ][ j ] = table.getNewId( tmpTable[ i ][ j ] );
            }
         }
      }
   }

  /**
   * Metoda vracející id největší oblasti.
   * @param max horní hranice, pro případ, že bych hledal další
   * největší oblasti.
   * @return id největší oblasti
   */
   public int getLargestArea( int max )
   {
      // Seznam s četnostmi jednotlivých oblastí
      ArrayList<Integer> freqList = table.getListFreq();

      int maxFreq = 0;
      int maxId = 0;
      int freqListSize = freqList.size();
      for ( int i = 0; i < freqListSize; i++ )
      {
         int freq = freqList.get( i );
         if (  maxFreq < max && maxFreq < freq )
         {
            maxFreq = freq;
            maxId = i + 1;
         }
      }
      return maxId;
   }

   /**
    * @return výstupní obrázek jako dvourozměrné pole, kde id {@literal >} 0 jsou
    * id jednotlivých spojitých oblastí.
    */
   public int[][] getOut()
   {
      return tmpTable;
   }

   /**
    * @return tabulku s informacemi o nalezených oblastech, pomocí ní
    * se pak hledá největší spojitá oblast v obrázku
    */
   public Table getTable()
   {
      return table;
   }
}
