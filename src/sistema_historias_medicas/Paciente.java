package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

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
            JOptionPane.showMessageDialog(null, "Error al consultar los pacientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarDatos(String nombre, String apellido, String fechaNacimiento, String genero, String direccion, String telefono, String correo) {
        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(null, "El nombre es obligatorio y debe contener solo letras.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (apellido == null || apellido.trim().isEmpty() || !apellido.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(null, "El apellido es obligatorio y debe contener solo letras.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (fechaNacimiento == null || fechaNacimiento.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "La fecha de nacimiento debe ser rellenada.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (genero == null || genero.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El género es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "La dirección es obligatoria.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (telefono == null || telefono.trim().isEmpty() || telefono.length() < 7) {
            JOptionPane.showMessageDialog(null, "El teléfono debe tener al menos 7 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (correo == null || correo.trim().isEmpty() || !correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(null, "El correo electrónico no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void guardarPaciente(String nombre, String apellido, String fechaNacimiento, String genero, String direccion, String telefono, String correo, String alergias, String enfermedades, String seguroDental) {
        if (!validarDatos(nombre, apellido, fechaNacimiento, genero, direccion, telefono, correo)) {
            return;
        }

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
            JOptionPane.showMessageDialog(null, "Paciente guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el paciente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modificarPaciente(int id, String nombre, String apellido, String fechaNacimiento, String genero, String direccion, String telefono, String correoElectronico, String alergias, String enfermedadesPreexistentes, String seguroDental) {
        if (!validarDatos(nombre, apellido, fechaNacimiento, genero, direccion, telefono, correoElectronico)) {
            return;
        }

        String query = "UPDATE Pacientes SET nombre = ?, apellido = ?, fecha_nacimiento = ?, genero = ?, direccion = ?, telefono = ?, correo_electronico = ?, alergias = ?, enfermedades_preexistentes = ?, seguro_dental = ? WHERE paciente_id = ?";
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
            JOptionPane.showMessageDialog(null, "Paciente modificado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar el paciente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
     public void buscarPacientes(JTable tabla, String criterio) {
        String query = "SELECT * FROM Pacientes WHERE nombre LIKE ? OR apellido LIKE ? OR telefono LIKE ?";
        try {
            PreparedStatement pst = cn.prepareStatement(query);
            String parametro = "%" + criterio + "%";
            pst.setString(1, parametro);
            pst.setString(2, parametro);
            pst.setString(3, parametro);
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
            JOptionPane.showMessageDialog(null, "Error al buscar los pacientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
