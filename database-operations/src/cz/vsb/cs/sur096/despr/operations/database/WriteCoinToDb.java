
package cz.vsb.cs.sur096.despr.operations.database;

import com.mysql.jdbc.MysqlErrorNumbers;
import com.mysql.jdbc.SQLError;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.AOutputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.types.CoinInformation;
import cz.vsb.cs.sur096.despr.utils.ID;
import java.sql.*;

/**
 * Operace slouží pro zápis vektoru reprezentujícího minci do databáze.
 * Operace je platformně závislá. To znamená, že potřebuje ke své funkci
 * mít na instalovaný MySQL server a v něm na instalované dvě rozšířující 
 * funkce (compute_distance {@literal &} compare_vectors} jejich zdrojové 
 * kódy a podrobný na jejich instalaci se nachází v adresáři {@code c_src}
 * který je distribuován s balíčkem ve kterém se tato operace nachází.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/08/11:47
 */
public class WriteCoinToDb extends BasicDatabaseParameters {
    
    private Connection connection;
    private int similarityIdGenerator;
    private boolean wasSetLastIDSimilarity;
    
    /**
     * Konstruktor inicializuje operaci s defaultními hodnotami.
     */
    public WriteCoinToDb() {
        updateExist = true;
        similarityIdGenerator = ID.createNewIDGenerator();
        wasSetLastIDSimilarity = false;
    }
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=6)
    private Boolean updateExist;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=7)
    private Double angle;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=8)
    private Float[] vector;

    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=9)
    private Integer sum;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=10)
    private String coinName;
    
    @AInputParameter(value=EInputParameterType.OUTER, enteringAs=11)
    private String sourceImgPath;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=12)
    private CoinInformation similarCoin;
    
    @AOutputParameter
    private Integer idSimilarity;
    
    /**
     * Zapíše vektor reprezentující minci do databáze.
     * @throws Exception pokud jsou vstupní parametry nekorektní
	 * nebo selže komunikace s databází.
     */
    @Override
    public void execute() throws Exception {
        
        checkHost(host);
        checkDatabase(database);
        checkUser(user);
        checkPassword(password);
        checkTableName(tableName);
        
        checkUpdateExist(updateExist);
        checkCoinName(coinName);
        checkSourceImgPath(sourceImgPath);
        checkAngle(angle);
        checkVector(vector);
        checkSum(sum);
        
        try {
            connection = DriverManager.
                    getConnection( "jdbc:mysql://" + host + "/" + database +
                                     "?user=" + user +
                                     "&password=" + password );
            
            if (!wasSetLastIDSimilarity) {
                try {
                    String sqlQuery = "SELECT " + CoinsDatabaseConstants.ID_SIMILARITY + 
                            " from " + tableName + 
                            " ORDER BY " + CoinsDatabaseConstants.ID_SIMILARITY + " DESC" + 
                            " LIMIT 1";
                    Statement st = connection.createStatement();
                    boolean hasResult = st.execute(sqlQuery);
                    if (hasResult) {
                        ResultSet rs = st.getResultSet();
                        rs.next(); // toto je velmi dulezite, bez toho nefunguje!!!
                        int lastIDSimilarity = rs.getInt(CoinsDatabaseConstants.ID_SIMILARITY);
                        ID.addId(lastIDSimilarity, similarityIdGenerator);
                    }
                } catch (SQLException ex) {
                    // do nothing, table probably does not exists. And return NULL
                    // thats indicate: coin was not found.
                }
                wasSetLastIDSimilarity = true;
            }
            
            String vectorAsString = vectorToString(vector);
            String keys = CoinsDatabaseConstants.COIN_NAME + ", " + 
                    CoinsDatabaseConstants.SOURCE_IMAGE + ", " +
                    CoinsDatabaseConstants.ANGLE + ", " + 
                    CoinsDatabaseConstants.SUM + ", " +
                    CoinsDatabaseConstants.VECTOR + ", " + 
                    CoinsDatabaseConstants.VECTOR_SIZE + ", " + 
                    CoinsDatabaseConstants.ID_SIMILARITY;
            
            idSimilarity = similarCoin == null ? ID.getNextID(similarityIdGenerator) : similarCoin.getIdSimilarity();
            String values = "\"" + coinName + "\"," + 
                            "\"" + sourceImgPath + "\"," +
                            angle + "," +
                            sum + "," +
                            "\"" + vectorAsString + "\"," + 
                            vector.length + "," + 
                            + idSimilarity;
            
            String sqlQuery = "INSERT INTO " + tableName + "(" + keys + ") VALUES(" + values + ");";
            Statement st = connection.createStatement();
            boolean hasResult = st.execute(sqlQuery);
            if (hasResult) {
                ResultSet rs = st.getResultSet();
                rs.last();
                rs.close();
            }
//             System.out.println(String.format("%s '%s'",
//                     getLocalizeMessage("message.add_entry"),
//                     coinName));
            connection.close();
        } catch (CommunicationsException ex) {
            
            String sqlState = ex.getSQLState();
            // unknown server
            if (sqlState.equals(SQLError.SQL_STATE_COMMUNICATION_LINK_FAILURE)) {
                String msg = String.format("%s %s", 
                        completeErrMsg(ex.getErrorCode(), sqlState, 
                        getLocalizeMessage("exception.unknown_server_name")),
                        host);
                
                throw new RuntimeException(msg);
            } else {
                throw new RuntimeException(completeErrMsg(ex));
            }
        } catch (SQLException ex) {
            int errorCode = ex.getErrorCode();
            switch (errorCode) {
                case MysqlErrorNumbers.ER_BAD_DB_ERROR: // unknown database name
//                    System.out.println(String.format("%s '%s'. %s",
//                            getLocalizeMessage("message.uknown_database_name"), 
//                            database,
//                            getLocalizeMessage("message.database_will_be_create")));
//                    System.out.println("Unknown databse name '" + database + "'." + 
//                            " It will be creating.");
//                    System.out.println(String.format("%s '%s' ...", 
//                            getLocalizeMessage("message.creating_database"),
//                            database));
                    createDatabse();
//                    System.out.println(String.format("%s '%s'",
//                            getLocalizeMessage("message.creting_databse_done"),
//                            database));
                    execute();
                    break;
                case MysqlErrorNumbers.ER_NO_SUCH_TABLE: // unknow table name
//                    System.out.println(String.format("%s '%s'. %s",
//                            getLocalizeMessage("message.uknown_table_name"),
//                            tableName,
//                            getLocalizeMessage("message.table_will_be_create")));
//                    System.out.println("Unknown table name '" + tableName + "'. It will be creating.");
//                    System.out.println(String.format("%s '%s' ...",
//                            getLocalizeMessage("message.creating_table"), tableName));
                    createTable();
//                    System.out.println(String.format("%s '%s'",
//                            getLocalizeMessage("message.creating_table_done"), 
//                            tableName));
                    execute();
                    break;
                case MysqlErrorNumbers.ER_DUP_ENTRY:
                    if (updateExist) {
                        updateEntry();
//                        System.out.println(String.format("%s '%s'",
//                                getLocalizeMessage("message.update_entry"),
//                                coinName));
                    } else {
//                        System.out.println(String.format("%s '%s'",
//                                getLocalizeMessage("message.entry_exists"),
//                                coinName));
                    }
                    break;
                case MysqlErrorNumbers.ER_ACCESS_DENIED_ERROR: // bad password or user name
                default:
                    throw new RuntimeException(completeErrMsg(ex));
            }
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Get nad Set method

    /**
     * Zjistí zda mají být existující hodnoty v databází aktualizovány,
	 * či nikoli.
     * @return mají být hodnoty v databázi aktualizovány?
     */
    public Boolean isUpdateExist() {
        return updateExist;
    }

    /**
     * Nastaví to, zda mají být hodnoty v databázi aktualizovány nebo ne.
     * @param updateExist mají být hodnoty v databázi aktualizovány?
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setUpdateExist(Boolean updateExist) 
            throws NullPointerException {
        
        checkUpdateExist(updateExist);
        this.updateExist = updateExist;
    }

    /**
     * Poskytne jméno mince.
     * @return jméno mince.
     */
    public String getCoinName() {
        return coinName;
    }

    /**
     * Nastaví jméno mince.
     * @param coinName jméno mince.
     * @throws NullPointerException pokud je hodnota {@code null}.
     * @throws IllegalArgumentException pokud je hodnota prázdná.
     */
    public void setCoinName(String coinName) 
            throws NullPointerException, IllegalArgumentException {
        
        checkCoinName(coinName);
        this.coinName = coinName;
    }

    /**
     * Poskytne cestu ke zdrojovému obrázku.
     * @return cesta ke zdrojovému obrázku.
     */
    public String getSourceImgPath() {
        return sourceImgPath;
    }

    /**
     * Nastaví cestu ke zdrojovému obrázku.
     * @param sourceImgPath cesta ke zdrojovému obrázku.
     * @throws NullPointerException pokud je hodnota {@code null}.
     * @throws IllegalArgumentException pokud je hodnota prázdná.
     */
    public void setSourceImgPath(String sourceImgPath) 
            throws NullPointerException, IllegalArgumentException {
        
        checkSourceImgPath(sourceImgPath);
        this.sourceImgPath = sourceImgPath;
    }

    /**
     * Poskytne úhel, s jakým byla mince natočena.
     * @return normující úhel natočení mince.
     */
    public Double getAngle() {
        return angle;
    }
    
    /**
     * Nastaví úhel natočení mince.
     * @param angle úhel natočení mince.
     * @throws NullPointerException pokud je hodnota prázdná.
     */
    public void setAngle(Double angle) 
            throws NullPointerException {
        
        checkAngle(angle);
        this.angle = angle;
    }

    /**
     * Poskytne vektor reprezentující ukládanou minci.
     * @return vektor reprezentující ukládanou minci.
     */
    public Float[] getVector() {
        return vector;
    }
    
    /**
     * Nastaví vektor reprezentující ukládanou minci.
     * @param vector
     * @throws NullPointerException pokud je vektor {@code null} nebo
	 * jakákoli jeho prvek.
     * @throws IllegalArgumentException pokud je jakákoliv hodnota vektoru 
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
     * @param sum
     * @throws NullPointerException
     * @throws IllegalArgumentException 
     */
    public void setSum(Integer sum) 
            throws NullPointerException, IllegalArgumentException {
        
        checkSum(sum);
        this.sum = sum;
    }

    /**
     * Poskytne informace o nalezené podobné minci.
     * @return informace o nejpodobnější minci.
     */
    public CoinInformation getSimilarCoin() {
        return similarCoin;
    }

    /**
     * Nastaví informace o nejpodobnější minci. Pokud taková mince
	 * neexistuje, může být předána hodnota {@code null}.
     * @param similarCoin informace o nejpodobnější minci.
     */
    public void setSimilarCoin(CoinInformation similarCoin) {
        this.similarCoin = similarCoin;
    }

    /**
     * Vrátí id podobnosti s jakou byla mince uložena v databázi.
     * @return id podobnosti.
     */
    public Integer getIdSimilarity() {
        return idSimilarity;
    }

    /**
     * Nastaví id podobnosti.
     * @param idSimilarity id podobnosti.
     * @throws UnsupportedOperationException vždy.
     * @deprecated metoda je definována pouze formálně.
     */
    @Deprecated
    public void setIdSimilarity(Integer idSimilarity) 
            throws UnsupportedOperationException {
        
        throw new UnsupportedOperationException(
                getLocalizeMessage("exception.output_parameter_set_method"));
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    private void checkUpdateExist(Boolean updateExist) 
            throws NullPointerException {
        
        if (updateExist == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_update_exist"));
        }
    }
    
    private void checkCoinName(String coinName) 
            throws NullPointerException, IllegalArgumentException {
        
        if (coinName == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_coin_name"));
        } else if (coinName.equals("")) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.empty_coin_name"));
        }
    }
    
    private void checkSourceImgPath(String sourceImgPath) 
            throws NullPointerException, IllegalArgumentException {
        
        if (sourceImgPath == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_source_img_path"));
        } else if (sourceImgPath.equals("")) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.empty_source_img_path"));
        }
    }
    
    private void checkAngle(Double angle) 
            throws NullPointerException {
        
        if (angle == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_angle"));
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
                Float v = vector[i];
                if (v == null) {
                    throw new NullPointerException(String.format("%s 'index=%d'", 
                            getLocalizeMessage("exception.null_vector_item"), i));
                } else if (v < 0) {
                    throw new IllegalArgumentException(String.format("%s 'v[%d]=%f'",
                            getLocalizeMessage("exception.negative_vector_item"),
                            i, v));
                }
            }
        }
    }
    
    private void checkSum(Integer sum) 
            throws NullPointerException, IllegalArgumentException {
        
        if (sum == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_sum"));
        } else if (sum < 0) {
            throw new IllegalArgumentException(String.format("%s '%d'",
                    getLocalizeMessage("exception.negative_sum"), sum));
        }
    }
    
    @Deprecated
    private void checkSimilarCoin(CoinInformation similarCoin) {
        // throw notfing, it could be NULL. If is not find (FindSimlarCoin),
        // it will return null.
    }
    
    ////////////////////////////////////////////////////////////
    
    private void createDatabse() {
        try {
            Connection c = DriverManager.getConnection( "jdbc:mysql://" +
                 host + "?user=" + user + "&password=" + password );

            Statement st = c.createStatement();
            st.execute( "CREATE DATABASE " + database + ";" );
        } catch (SQLException ex) {
            throw new RuntimeException(completeErrMsg(ex));
        }
    }
    
    private void createTable() {
        try {
            String sqlCreateTable = "CREATE TABLE " + tableName + " (" +
                    CoinsDatabaseConstants.ID + " INT NOT NULL AUTO_INCREMENT, " +
                    CoinsDatabaseConstants.COIN_NAME + " VARCHAR(15) NOT NULL, " + 
                    CoinsDatabaseConstants.SOURCE_IMAGE + " VARCHAR(300) NOT NULL, " +
                    CoinsDatabaseConstants.ANGLE + " FLOAT NOT NULL, " +
                    CoinsDatabaseConstants.SUM + " INT NOT NULL, " +
                    CoinsDatabaseConstants.VECTOR + " VARCHAR(1000) NOT NULL, " + 
                    CoinsDatabaseConstants.VECTOR_SIZE + " SMALLINT NOT NULL, " +
                    CoinsDatabaseConstants.ID_SIMILARITY + " SMALLINT NOT NULL, " +
                    "PRIMARY KEY (" + CoinsDatabaseConstants.ID + ")"
                    + ");";
            
            Statement st = connection.createStatement();
            st.execute(sqlCreateTable);
        } catch (SQLException ex) {
            switch (ex.getErrorCode()) {
                case MysqlErrorNumbers.ER_CANT_CREATE_TABLE:
                    break;
                default:
                    throw new RuntimeException(completeErrMsg(ex));
            }
        }
    }
    
    private void updateEntry() {
        try {
            String sqlUpdate = "UPDATE " + tableName + " SET " + 
                    CoinsDatabaseConstants.ANGLE + "=" + angle + ", " +
                    CoinsDatabaseConstants.SUM + "=" + sum + ", " +
                    CoinsDatabaseConstants.VECTOR + "=\"" + vectorToString(vector) + "\", " +
                    CoinsDatabaseConstants.VECTOR_SIZE + "=" + vector.length + ", " +
                    " WHERE " + CoinsDatabaseConstants.COIN_NAME + "=\"" + coinName + "\";";
            Statement st = connection.createStatement();
            st.execute(sqlUpdate);
        } catch (SQLException ex) {
            throw new RuntimeException(completeErrMsg(ex));
        }
    }
    
    private String completeErrMsg(SQLException e) {
        return completeErrMsg(e.getErrorCode(), e.getSQLState(), e.getMessage());
    }
    
    private String completeErrMsg(int errCode, String sqlState, String message) {
            return "SQL ERROR " + errCode + " (" + sqlState + "): " + message;
    }
    
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
