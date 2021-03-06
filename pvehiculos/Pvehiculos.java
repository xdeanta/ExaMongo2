/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pvehiculos;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.bson.Document;

/**
 *
 * @author oracle
 */
public class Pvehiculos {
    private static EntityManagerFactory emf;
    private static EntityManager em;
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
    
    public static void getObjects(){
        emf = Persistence.createEntityManagerFactory("examen_files/vehicli.odb");
        em = emf.createEntityManager();
    }
    
    public static List<Vehiculos> getVehiculos(){
        TypedQuery<Vehiculos> q1 = em.createQuery("SELECT v from Vehiculos v", Vehiculos.class);
        List<Vehiculos> res = q1.getResultList();
        return res;
    }
    
    public static List<Clientes> getClientes(){
        TypedQuery<Clientes> q2 = em.createQuery("SELECT c from Clientes c", Clientes.class);
        List<Clientes> res = q2.getResultList();
        return res;
    }
    
    public static ArrayList<Ventas> getVentas(){
        MongoCollection<Document> docs = getCollection("vendas");
        FindIterable<Document> it = docs.find();
        MongoCursor<Document> cur = it.iterator();
        ArrayList<Ventas> ventas = new ArrayList<>();
        while(cur.hasNext()){
            Document doc = cur.next();
            Ventas v = new Ventas(doc.getDouble("_id"),doc.getString("dni"),doc.getString("codveh"));
            ventas.add(v);
        }
        return ventas;
    }
    
    public static void filterQueries(List<Vehiculos> veh, List<Clientes> cli, ArrayList<Ventas> v){
        ArrayList<Vehiculos> temp = new ArrayList<>();
        ArrayList<Clientes> temp2 = new ArrayList<>();
        for(int i = 0; i < v.size(); i++){
            for(int j = 0; j<veh.size(); j++){
                if(v.get(i).getCodveh().equals(veh.get(j).getCodveh())){
                    //System.out.println("Anadiendo: " + veh.get(j));
                    temp.add(veh.get(j));
                }
            }
        }
        for(int i = 0; i < v.size(); i++){
            for(int j = 0; j<cli.size(); j++){
                if(v.get(i).getDni().equals(cli.get(j).getDni())){
                    //System.out.println("Anadiendo: " + cli.get(j));
                    temp2.add(cli.get(j));
                }
            }
        }
        veh.clear();
        cli.clear();
        veh.addAll(temp);
        cli.addAll(temp2);
        /*veh=(List<Vehiculos>)temp.clone();
        cli=(List<Clientes>)temp2.clone();*/
        /*System.out.println(temp);
        System.out.println(temp2);*/
    }
    
    public static void saveInfo(List<Vehiculos> veh, List<Clientes> cli, ArrayList<Ventas> v){
        int ncompras=0;
        int preciofinal=0;
        try{
            PreparedStatement pst = conxOrcl.prepareStatement("Insert into finalveh values(?,?,?,tipo_vehf(?,?))");
            for(int i = 0; i<v.size(); i++){
                pst.setInt(1, v.get(i).getId());
                pst.setString(2, v.get(i).getDni());
                for(int j = 0; j < cli.size(); j++){
                    if(v.get(i).getDni().equals(cli.get(j).getDni())){
                        pst.setString(3, cli.get(j).getNomec());
                        ncompras=cli.get(j).getNcompras();
                        for(int k = 0; k < veh.size(); k++){
                            if(v.get(i).getCodveh().equals(veh.get(k).getCodveh())){
                              pst.setString(4, veh.get(k).getNomveh());
                              if(ncompras > 0){
                                  preciofinal=veh.get(k).getPrezoorixe()-((2019-veh.get(k).getAnomatricula())*500)-500;
                              }else{
                                  preciofinal=veh.get(k).getPrezoorixe()-((2019-veh.get(k).getAnomatricula())*500);
                              }
                              pst.setInt(5, preciofinal);
                            }
                        }
                        
                    }
                
                }
               if(pst.executeUpdate() > 0){
                    System.out.println("Ok");
                }
            }
        
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        getObjects();
        try{
            getSQLConnection();
        }catch(SQLException e){
            e.printStackTrace();
        }
        connectMongoClient();
        connectMongoDB("test");
        MongoCollection<Document> docs;
        List<Vehiculos> vehiculos;
        List<Clientes> clientes;
        docs=getCollection("vendas");
        vehiculos=getVehiculos();
        clientes=getClientes();
        ArrayList<Ventas> vs = getVentas();
        /*System.out.println("Ventas:");
        for(Ventas v : vs){
            System.out.println(v);
        }*/
        filterQueries(vehiculos,clientes,vs);
        saveInfo(vehiculos,clientes,vs);
        /*System.out.println(vehiculos);
        System.out.println(clientes);*/
        em.close();
        emf.close();
        mongoC.close();
        try{
            conxOrcl.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
}
