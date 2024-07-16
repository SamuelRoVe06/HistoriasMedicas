
package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;

public class Paciente {

    private Connection cn;

    public Paciente(Connection conexion) {
        this.cn = conexion;
    }

    public void consultar(JTable tabla) {
        String query = "SELECT * FROM Pacientes";
        try {
            PreparedStatement pst = cn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] paciente = new Object[11];
                paciente[0] = rs.getInt("paciente_id");
                paciente[1] = rs.getString("nombre");
                paciente[2] = rs.getString("apellido");
                paciente[3] = rs.getDate("fecha_nacimiento");
                paciente[4] = rs.getString("genero");
                paciente[5] = rs.getString("direccion");
                paciente[6] = rs.getString("telefono");
                paciente[7] = rs.getString("correo_electronico");
                paciente[8] = rs.getString("alergias");
                paciente[9] = rs.getString("enfermedades_preexistentes");
                paciente[10] = rs.getString("seguro_dental");
                modelo.addRow(paciente);
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al consultar los pacientes: " + e.getMessage());
        }
    }

    public void guardarPaciente(String nombre, String apellido, String fechaNacimiento, String genero, String direccion, String telefono, String correo, String alergias, String enfermedades, String seguroDental) throws SQLException {
        String query = "INSERT INTO Pacientes (nombre, apellido, fecha_nacimiento, genero, direccion, telefono, correo_electronico, alergias, enfermedades_preexistentes, seguro_dental) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, fechaNacimiento);
            pst.setString(4, genero);
            pst.setString(5, direccion);
            pst.setString(6, telefono);
            pst.setString(7, correo);
            pst.setString(8, alergias);
            pst.setString(9, enfermedades);
            pst.setString(10, seguroDental);
            pst.executeUpdate();
        }
    }
    
    public void modificarPaciente(int id, String nombre, String apellido, String fechaNacimiento, String genero, String direccion, String telefono, String correoElectronico, String alergias, String enfermedadesPreexistentes, String seguroDental) throws SQLException {
        String query = "UPDATE Pacientes SET nombre = ?, apellido = ?, fecha_nacimiento = ?, genero = ?, direccion = ?, telefono = ?, correo_electronico = ?, alergias = ?, enfermedades_preexistentes = ?, seguro_dental = ? WHERE id = ?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, fechaNacimiento);
            pst.setString(4, genero);
            pst.setString(5, direccion);
            pst.setString(6, telefono);
            pst.setString(7, correoElectronico);
            pst.setString(8, alergias);
            pst.setString(9, enfermedadesPreexistentes);
            pst.setString(10, seguroDental);
            pst.setInt(11, id);
            pst.executeUpdate();
        }
    }
}