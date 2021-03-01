/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pvehiculos;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.persistence.EntityManagerFactory;
import org.bson.Document;

/**
 *
 * @author oracle
 */
public class Pvehiculos {
    private static EntityManagerFactory em;
    private static Connection conxOrcl;
    private static MongoClient mongoC;
    private static MongoDatabase mongoDB;
    /**
     * @param args the command line arguments
     */
    
    public static void getSQLConnection() throws SQLException{
        String usuario = "hr";
        String password = "hr";
        String host = "localhost"; 
        String puerto = "1521";
        String sid = "orcl";
        String url = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;
        
           
            conxOrcl = DriverManager.getConnection(url);
    }
    
    public static void connectMongoClient(){
        mongoC = new MongoClient("localhost");
    }
    
    public static void connectMongoDB(String name){
        mongoDB = mongoC.getDatabase(name);
    }
    
    public static MongoCollection<Document> getCollection(String name){
        return mongoDB.getCollection(name);
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
