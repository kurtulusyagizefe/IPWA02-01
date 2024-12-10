import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class testo {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/emissiondb";
        String user = "root";
        String password = "";
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM emissions LIMIT 10");
            
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Country: " + rs.getString("country"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
