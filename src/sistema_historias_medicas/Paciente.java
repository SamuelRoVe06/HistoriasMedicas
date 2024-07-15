
package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Clase que representa un paciente y proporciona métodos para interactuar con la base de datos.
 */
public class Paciente {
    private Connection cn; // Conexión a la base de datos

    /**
     * Constructor de la clase Paciente.
     * @param conexion La conexión a la base de datos.
     */
    public Paciente(Connection conexion) {
        this.cn = conexion;
    }

    public void consultar(JTable tabla) {
        String query = "SELECT * FROM Pacientes"; // Consulta SQL para obtener todos los pacientes
        try {
            Statement st = cn.createStatement(); // Crear una declaración para ejecutar la consulta SQL
            ResultSet rs = st.executeQuery(query); // Ejecutar la consulta y obtener el resultado
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel(); // Crear un modelo de tabla para almacenar los datos de los pacientes
            modelo.setRowCount(0); // Limpiar la tabla antes de agregar los nuevos datos
            // Recorrer el resultado de la consulta y agregar cada paciente a la tabla
            while (rs.next()) {
                Object[] paciente = new Object[10]; // Crear un arreglo para almacenar los datos de cada paciente
                paciente[0] = rs.getInt("paciente_id"); // Obtener el ID del paciente
                paciente[1] = rs.getString("nombre"); // Obtener el nombre del paciente
                paciente[2] = rs.getString("apellido"); // Obtener el apellido del paciente
                paciente[3] = rs.getDate("fecha_nacimiento"); // Obtener la fecha de nacimiento del paciente
                paciente[4] = rs.getString("genero"); // Obtener el género del paciente
                paciente[5] = rs.getString("direccion"); // Obtener la dirección del paciente
                paciente[6] = rs.getString("telefono"); // Obtener el teléfono del paciente
                paciente[7] = rs.getString("correo_electronico"); // Obtener el correo electrónico del paciente
                paciente[8] = rs.getString("alergias"); // Obtener las alergias del paciente
                paciente[9] = rs.getString("enfermedades_preexistentes"); // Obtener las enfermedades preexistentes del paciente
                modelo.addRow(paciente); // Agregar el paciente al modelo de la tabla
            }
            tabla.setModel(modelo); // Establecer el modelo de la tabla con los nuevos datos
        } catch (SQLException e) {
            // Manejar cualquier excepción que pueda ocurrir durante la consulta
            System.err.println("Error al consultar los pacientes: " + e.getMessage());
        }
    }
}
