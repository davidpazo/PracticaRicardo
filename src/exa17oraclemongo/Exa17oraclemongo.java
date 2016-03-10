

package exa17oraclemongo;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeMap;
import org.bson.Document;

public class Exa17oraclemongo {

    public static Connection conexion=null;
    Statement st;
    ResultSet rs;
    ArrayList <Pedido17> a_pedidos = new ArrayList<>();
    
     public static Connection getConexion() throws SQLException  {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost"; // tambien puede ser una ip como "192.168.1.14"
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;
        
           
            conexion = DriverManager.getConnection(ulrjdbc);
            return conexion;
        }

     
     public static void closeConexion() throws SQLException {
      conexion.close();
      }
    
     public void consulta() throws SQLException{
        String consulta = "select hr.* from produtos";
        st = getConexion().createStatement();
        st.execute(consulta);
        rs = st.getResultSet();
        
        while (rs.next()) {
            String cod = rs.getString("codidop");
            String nomep = rs.getString("nomep");
            int prezo = rs.getInt("prezo");
            int stock = rs.getInt("stock");
            System.out.println(cod + " " + nomep + " " + prezo + " " + stock);
        }
         
     }
     
    public void pedidoMongo(){
       MongoClient cli = new MongoClient("localhost", 27017);
       MongoDatabase base = cli.getDatabase("tenda");
       MongoCollection <Document> colection = base.getCollection("pedidos");
       FindIterable<Document> cursor = colection.find();
       MongoCursor<Document> iterator = cursor.iterator();
       while(iterator.hasNext()){
          Document obj = iterator.next();
           System.out.println(obj.toString());
           Pedido17 p = new Pedido17(obj.getString("codcli"), obj.getString("codpro"), obj.getDouble("cantidade"), obj.getString("data"));
           a_pedidos.add(p);
           
       }
    } 
     
   public void disminuir(String codpro) throws SQLException{
       for (Pedido17 obj: a_pedidos){
           if (obj.getCodpro().equalsIgnoreCase(codpro)){
               String consulta = "update produtos set stock= sock - " + obj.getCantidade();
               st = getConexion().createStatement();
               st.execute(consulta);
           }
       }
       
       
   }
     
 
     
     
     
     
     
     
     
     
     
     
     
    public static void main(String[] args) throws FileNotFoundException, SQLException {
        Exa17oraclemongo prueba = new Exa17oraclemongo();
        prueba.pedidoMongo();
        prueba.disminuir("p1");
        prueba.disminuir("p2");
        prueba.disminuir("p3");
        prueba.consulta();
        
    }

}