package Backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDD {

    private static final String URL = "jdbc:mysql://localhost:3306/sistema_historias_medicas";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private Connection con; // Objeto de conexión

    // Método para establecer la conexión con la base de datos
    public Connection conexion() {
        try {
            // Cargar el controlador JDBC para MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer la conexión con la base de datos
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            
            // Imprimir un mensaje de éxito en la consola si la conexión se establece correctamente
            System.out.println("Conexión a la base de datos exitosa");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el controlador JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al establecer la conexión con la base de datos: " + e.getMessage());
        }
        
        return con; // Devolver el objeto de conexión
    }
    
    // Método para obtener la conexión establecida
    public Connection getConnection() {
        if (con == null) {
            con = conexion(); // Establecer la conexión si aún no está establecida
        }
        return con; // Devolver el objeto de conexión
    }
}
