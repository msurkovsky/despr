
package cz.vsb.cs.sur096.despr.operations.database;

import cz.vsb.cs.sur096.despr.model.operation.parameter.AInputParameter;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;

/**
 * Abstraktní třída poskytuje základní parametry nutné pro 
 * přípojení do databáze a jejich přístupové metody.
 *
 * @author Martin Šurkovský, sur096 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2011/12/17/18:14
 */
public abstract class BasicDatabaseParameters extends AbstractDatabaseOperation {
    
    /**
     * Konstruktor inicializuje defaultní hodnoty.
     */
    public BasicDatabaseParameters() {
        host = "localhost";
        database = "coins";
        user = "root";
        password = "root";
        tableName = "some_table";
    }
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=1)
    protected String host;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=2)
    protected String database;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=3)
    protected String user;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=4)
    protected String password;
    
    @AInputParameter(value=EInputParameterType.INNER, enteringAs=5)
    protected String tableName;

    ////////////////////////////////////////////////////////////
    // Get a set metody
    
    /**
     * Poskytne adresu serveru.
     * @return adresa serveru.
     */
    public String getHost() {
        return host;
    }

    /**
     * Nastaví adresu serveru.
     * @param host adresa serveru.
     * @throws NullPointerException pokud je hodnota {@code null}.
     * @throws IllegalArgumentException pokud je hodnota prázdná.
     */
    public void setHost(String host) 
            throws NullPointerException, IllegalArgumentException {
        
        checkHost(host);
        this.host = host;
    }

    /**
     * Poskytne jméno databáze.
     * @return jméno databáze.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Nastaví jméno databáze. 
     * @param database jméno databáze.
     * @throws NullPointerException pokud je hodnota {@code null}.
     * @throws IllegalArgumentException pokud je hodnota prázdná.
     */
    public void setDatabase(String database) 
            throws NullPointerException, IllegalArgumentException {
        
        checkDatabase(database);
        this.database = database;
    }

    /**
     * Poskytne uživatelské jméno.
     * @return  uživatelské jméno
     */
    public String getUser() {
        return user;
    }

    /**
     * Nastaví uživatelské jméno.
     * @param user uživatelské jméno.
     * @throws NullPointerException pokud je hodnota {@code null}.
     * @throws IllegalArgumentException pokud je hodnota prázdná.
     */
    public void setUser(String user) 
            throws NullPointerException, IllegalArgumentException {
        
        checkUser(user);
        this.user = user;
    }

    /**
     * Poskytne heslo pro připojení k databázi.
     * @return heslo pro připojení k databázi.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Nastaví heslo pro připojení k databázi.
     * @param password heslo k databázi.
     * @throws NullPointerException pokud je hodnota {@code null}.
     */
    public void setPassword(String password) throws NullPointerException {
        checkPassword(password);
        this.password = password;
    }

    /**
     * Poskytne jméno tabulky, se kterou se má pracovat.
     * @return jméno tabulky.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Nastaví jméno tabulky se kterou se má pracovat.
     * @param tableName jméno tabulky.
     * @throws NullPointerException pokud je hodnota {@code null}.
     * @throws IllegalArgumentException pokud je hodnota prázdná.
     */
    public void setTableName(String tableName) 
            throws NullPointerException, IllegalArgumentException {
        
        checkTableName(tableName);
        this.tableName = tableName;
    }
    
    ////////////////////////////////////////////////////////////
    // Kontrolni meotdy
    
    protected void checkHost(String host) 
            throws NullPointerException, IllegalArgumentException {
        
        if (host == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_host"));
        } else if (host.equals("")) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.empty_host"));
        }
    }
    
    protected void checkDatabase(String database) 
            throws NullPointerException, IllegalArgumentException {
        
        if (database == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_database"));
        } else if (database.equals("")) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.empty_database"));
        }
    }
    
    protected void checkUser(String user) 
            throws NullPointerException, IllegalArgumentException {
        
        if (user == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_user"));
        } else if (user.equals("")) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.empty_user"));
        }
    }
    
    protected void checkPassword(String password)
            throws NullPointerException {
        
        if (password == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_password"));
        }
    }
    
    protected void checkTableName(String tableName) 
            throws NullPointerException, IllegalArgumentException {
        
        if (tableName == null) {
            throw new NullPointerException(
                    getLocalizeMessage("exception.null_tale_name"));
        } else if (tableName.equals("")) {
            throw new IllegalArgumentException(
                    getLocalizeMessage("exception.empty_table_name"));
        }
    }
}
