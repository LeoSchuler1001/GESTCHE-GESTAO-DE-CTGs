package dao;

public class CredenciaisBancoDados {
    //ATRIBUTOS 
    private static final String URL = "jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:6543/postgres?prepareThreshold=0";
    private static final String USER = "postgres.rqolgvzsbbbdxwxdcwzg";
    private static final String PASSWORD = "Tentenovamente1001@";
    
    //GETERS
    public static String getUrl() {
        return URL;
    }
    public static String getUser() {
        return USER;
    }
    public static String getPassword() {
        return PASSWORD;
    }
}