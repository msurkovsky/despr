
package cz.vsb.cs.sur096.despr.operations.database;

import com.mysql.jdbc.MysqlErrorNumbers;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.CoinInformation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Operace která vyhledá podobnou minci v databázi. Operace je platformě
 * závislá, v tom smyslu, že potřebuje pro svou činnost databázi
 * (konkrétně MySQL server) a na serveru mít na instalované dvě 
 * pomocné metody (compute_distance {@literal &} compare_vectors)
 * jejich zdrojové kódy je možné nalézt v adresáři {@code c_src}, který
 * je distribuován s tímto balíkem. V něm se nachází také návod jak
 * funkce zkompilovat a na instalovat.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/17/18:12
 */
public class FindSimilarCoins extends BasicDatabaseParameters {
    
    /**
     * Konstruktor inicializuje operaci defaultními hodnotami.
     */
    public FindSimilarCoins() {
        totalThreshold = 2000;
        threshold = 200;
        limit = 10;
    }
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=6)
    private Integer totalThreshold;
        
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=7)
    private Integer threshold;
    
    @AInputParameter(value= EInputParameterType.INNER, enteringAs=8)
    private Integer limit;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=9)
    private Float[] vector;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=10)
    private Integer sum;
    
    @AOutputParameter
    private CoinInformation[] result;
    
    /**
     * Pokusí se nalézt podobnou minci v databázi.
     * @throws Exception pokud jsou nekorektní vstupní parametry,
	 * nebo nějak selže komunikace s databází.
     */
    @Override
    public void execute() throws Exception {
        
        checkHost(host);
        checkDatabase(database);
        checkUser(user);
        checkPassword(password);
        checkTableName(tableName);
        
        checkTotalThreshold(totalThreshold);
        checkThreshold(threshold);
        checkLimit(limit);
        checkVector(vector);
        checkSum(sum);
        
        try {
            result = null;
            Connection connection = DriverManager.
                    getConnection( "jdbc:mysql://" + host + "/" + database +
                                     "?user=" + user +
                                     "&password=" + password );
            
            String strVector = vectorToString(vector);
            String searchQuery = "SELECT *, " + 
                    "abs(" + (CoinsDatabaseConstants.SUM + "-" + sum) + ") as total_tolerance, " +
                    "compute_distance(\"" + strVector + "\", " + 
                                      CoinsDatabaseConstants.VECTOR + ", " + 
                                      CoinsDatabaseConstants.VECTOR_SIZE + ", " + 
                                      "\",\", 1) as distance " +
                    " FROM " + tableName +
                    " WHERE abs(" + CoinsDatabaseConstants.SUM + "-" + sum + ") < " + totalThreshold + 
                    " AND compare_vectors(\"" + strVector + "\", " + 
                                          CoinsDatabaseConstants.VECTOR + ", " + 
                                          CoinsDatabaseConstants.VECTOR_SIZE + ", " +
                                          "\",\", " + threshold + ") = 1" + 
                    " ORDER BY distance, total_tolerance" + 
                    " LIMIT 0, " + limit + ";";
            
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(searchQuery);
            List<CoinInformation> coins = new ArrayList<CoinInformation>(limit);
            
            while (rs.next()) {
                coins.add(new CoinInformation(
                        rs.getInt(CoinsDatabaseConstants.ID),
                        rs.getString(CoinsDatabaseConstants.COIN_NAME),
                        rs.getString(CoinsDatabaseConstants.SOURCE_IMAGE),
                        rs.getFloat(CoinsDatabaseConstants.ANGLE),
                        rs.getInt(CoinsDatabaseConstants.SUM),
                        rs.getString(CoinsDatabaseConstants.VECTOR),
                        rs.getInt(CoinsDatabaseConstants.VECTOR_SIZE),
                        rs.getInt(CoinsDatabaseConstants.ID_SIMILARITY)));
            }
            
            int foundCoinsSize = coins.size();
            result = new CoinInformation[foundCoinsSize];
            for (int i = 0; i < foundCoinsSize; i++) {
                result[i] = coins.get(i);
            }
            
        } catch (SQLException ex) {
            if (ex.getErrorCode() == MysqlErrorNumbers.ER_NO_SUCH_TABLE) {
                // do nothing
                // table does not exist => database can not find similar coin
                // and returns null;
            } else {
                throw ex;
            }
        }
    }
    
    ////////////////////////////////////////////////////////////
    // Get a set metody

    /**
     * Poskytne absolutní práh chyby.
     * @return absolutní práh.
     */
    public Integer getTotalThreshold() {
        return totalThreshold;
    }

    /**
     * Nastaví absolutní práh, tj. práh který kontroluje zda je
	 * v rozmezí +- práh vzdálenost obou vektorů. 
     * @param totalThreshold absolutní práh.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException pokud je hodnota záporná.
     */
    public void setTotalThreshold(Integer totalThreshold) 
            throws NullPointerException, IllegalArgumentException {
        
        checkTotalThreshold(totalThreshold);
        this.totalThreshold = totalThreshold;
    }
    
    /**
     * Poskytne práh pro jednotlivé hodnoty vektoru.
     * @return práh pro jednotlivé hodnoty vektoru.
     */
    public Integer getThreshold() {
        return threshold;
    }

    /**
     * Nastaví práh pro jednotlivé hodnoty vektoru, tj.
	 * práh který se používá pro porovnání vzdálenosti
	 * mezi hodnotami dvou vektorů na stejné pozici.
     * @param threshold práh.
     * @throws NullPointerException pokud je hodnota prázdna.
     * @throws IllegalArgumentException pokud je hodnota záporná.
     */
    public void setThreshold(Integer threshold) 
            throws NullPointerException, IllegalArgumentException {
        
        checkThreshold(threshold);
        this.threshold = threshold;
    }

    /**
     * Metoda pokytne horní limit, pro SQL dotaz.
     * @return horní limit SQL dotazu.
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Nastaví horní limit pro SQL dotaz. Specifikuje tak maximální počet 
     * vracených výsledků z databáze.
     * @param limit maximání počet vracených výsledků z databáze.
     * @throws NullPointerException pokud je hodnota rovna {@null}.
     * @throws IllegalArgumentException Pokud je hodnota menší než 1.
     */
    public void setLimit(Integer limit) 
            throws NullPointerException, IllegalArgumentException {
        
        checkLimit(limit);
        this.limit = limit;
    }
    
    /**
     * Poskytne vstupní vektor reprezentující hledanou minci.
     * @return vstupní vektor.
     */
    public Float[] getVector() {
        return vector;
    }

    /**
     * Nastaví vstupní vektor reprezentující hledanou minci.
     * @param vector vstupní vektor.
     * @throws NullPointerException pokud je vektor {@code null} nebo
	 * jakákoli jeho prvek.
     * @throws IllegalArgumentException pokud je jakákoliv hodnota vektoru 
	 * záporná
     */
    public void setVector(Float[] vector) 
            throws NullPointerException, IllegalArgumentException {
        
        checkVector(vector);
        this.vector = vector;
    }

    /**
     * Poskytne celkový součet hodnot vektoru.
     * @return celkový součet hodnot vektoru.
     */
    public Integer getSum() {
        return sum;
    }

    /**
     * Nastaví celkový součet hodnot vektoru.
     * @param sum celkový součet hodnot vektoru.
     * @throws NullPointerException pokud je hodnota prázdná.
     * @throws IllegalArgumentException pokud je hodnota záporná.
     */
    public void setSum(Integer sum) 
            throws NullPointerException, IllegalArgumentException {
        
        checkSum(sum);
        this.sum = sum;
    }

    /**
     * Poskytne informace o nalezené minci.
     * @return pokud nalezne podobnou minci vrátí informace o ni,
	 * jinka {@code null}.
     */
    public CoinInformation[] getResult() {
        return result;
    }

    /**
     * Nastaví informace o nalezené minci.
     * @param result informace o minci.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setResult(CoinInformation[] result) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    private void checkTotalThreshold(Integer totalThreshold) 
            throws NullPointerException, IllegalArgumentException {
        
        if (totalThreshold == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_total_threshold"));
        } else if (totalThreshold < 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_total_threshold"),
                    totalThreshold));
        }
    }
    
    private void checkThreshold(Integer threshold) 
            throws NullPointerException, IllegalArgumentException {
        
        if (threshold == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_threshold"));
        } else if (threshold < 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_threshold"),
                    threshold));
        }
    }
    
    private void checkLimit(Integer limit) 
            throws NullPointerException, IllegalArgumentException {
        
        if (limit == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_limit"));
        } else if (limit < 1) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.nonpositive_limit"),
                    limit));
        }
        
    }
    
    private void checkVector(Float[] vector) 
            throws NullPointerException, IllegalArgumentException {
        
        if (vector == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_vector"));
        } else {
            int size = vector.length;
            for (int i = 0; i < size; i++) {
                Float a = vector[i];
                if (a == null) {
                    throw new NullPointerException(String.format("%s 'index=%d'",
                            getLocalizeMessage("exception.null_vector_item"),
                            i));
                } else if (a < 0) {
                    throw new IllegalArgumentException(String.format("%s 'v[%d]=%f'", 
                            getLocalizeMessage("exception.negative_vector_item"),
                            i, a));
                }
            }
        }
    }
    
    private void checkSum(Integer suma) 
            throws NullPointerException, IllegalArgumentException {
        
        if (suma == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_suma"));
        } else if (suma < 0) {
            throw new IllegalArgumentException(String.format("%s '%f'",
                    getLocalizeMessage("exception.negative_suma"), suma));
        }
    }
    
    ////////////////////////////////////////////////////////////
    
    private String vectorToString(Float[] vector) {
        String ret = "";
        
        int length = vector.length;
        for (int i = 0; i < length; i++) {
            String separator = (i < length -1) ? "," : "";
            ret += vector[i] + separator;
        }
        
        return ret;
    }
}
