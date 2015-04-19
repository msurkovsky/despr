package cz.vsb.cs.sur096.despr.operations.coins.standardization;

import java.util.ArrayList;


/**
 * Struktura sloužící pro uchování hodnot id oblastí při detekci 
 * spojitých oblastí v obraze.
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class Table {
   /**
    * Seznam odkazu na nové id-čka. Pozice - 1 určuje id a hodnota odkaz
    * na nove id.
    */
   private ArrayList<Integer> listId;

   /**
    * Seznam cetnosti (velikostí) jednotlivych oblasti, zase pozice - 1
    * určuje id oblasti a hondonta na pozici je počet pixelu dané barvy
    * v oblasti.
    */
   private ArrayList<Integer> listFrequency;

   /**
    * Konstruktor inicializující tabulky
    */
   public Table()
   {
      listId = new ArrayList<Integer>();
      listFrequency = new ArrayList<Integer>();
   }

   /**
    * Přidá nové id do seznamu.
    * @param id nové id, které chci přidat do seznamu
    */
   public void addId( int id )
   {
      listId.add( id );
      // při zavedení nové oblasti nastavím i čítač četnosti pixelů
      // pro danou oblast
      listFrequency.add( 1 );
   }

   /**
    * Nastaví nové id.
    * @param id staré id
    * @param newId nové id
    */
   public void setNewId( int id, int newId )
   {
      int previousId = listId.get( id - 1 );
      
      // kdyby se náhodou nové id už odkazovalo na jiné
      int nextId = listId.get( newId - 1 );

      // všem co se odkazovali na staré id nastavím taky nové
      int listIdSize = listId.size();
      for ( int i = 0; i < listIdSize; i++ )
      {
         if ( listId.get( i ) == previousId )
         {
            listId.set( i, nextId );
         }
      }
   }

   /**
    * Nastaví požadované id na nulu, slouží při odmazáváni malých oblastí.
    * @param id id oblasti
    */
   public void setZeroId( int id )
   {
      int previosId = listId.get( id - 1 );

      int listIdSize = listId.size();
      for ( int i = 0; i < listIdSize; i++ )
      {
         if ( listId.get( i ) == previosId )
         {
            listId.set( i, 0 );
         }
      }
   }

   /**
    * Získá odkaz na nové id.
    * @param id id oblasti
    * @return nové id, na které se oblast odkazuje
    */
   public int getNewId( int id )
   {
      return listId.get( id - 1 );
   }
   
   /**
    * Při zvýšení počtu pixelů v oblasti o jeden.
    * @param id id oblasti.
    */
   public void incrementFreq( int id )
   {
      int currentFreq = listFrequency.get( id - 1 );
      listFrequency.set( id - 1, currentFreq + 1 );
   }

   /**
    * Vrací četnost daných pixelu v oblasti.
    * @param id id oblasti.
    * @return četnost daných pixelů v oblasti.
    */
   public int getFrequency( int id )
   {
      return listFrequency.get( id - 1 );
   }

   public void setFrequncy( int id, int frequency )
   {
      listFrequency.set( id - 1, frequency );
   }
   
   public ArrayList<Integer> getListId()
   {
      return listId;
   }

   public ArrayList<Integer> getListFreq()
   {
      return listFrequency;
   }
}
