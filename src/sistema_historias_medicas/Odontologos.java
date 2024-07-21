package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

public class Odontologos {
    private Connection cn;

    public Odontologos(Connection conexion) {
        this.cn = conexion;
    }

    public void consultar(JTable tabla) {
        String query = "SELECT * FROM odontologos";
        try {
            PreparedStatement pst = cn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] odontologo = new Object[11];
                odontologo[0] = rs.getInt("odontologos_id");
                odontologo[1] = rs.getString("nombre");
                odontologo[2] = rs.getString("apellido");
                odontologo[3] = rs.getString("numero_colegiatura");
                odontologo[4] = rs.getString("especialidad");
                odontologo[5] = rs.getString("horario_atencion");
                odontologo[6] = rs.getInt("consultorio_asignado");
                odontologo[7] = rs.getString("telefono");
                odontologo[8] = rs.getString("direccion");
                odontologo[9] = rs.getString("email");
                odontologo[10] = rs.getString("estado");
                modelo.addRow(odontologo);
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al consultar los odontólogos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarDatosSeleccionados(JTable tabla, JTextField idField, JTextField nombreField, JTextField apellidoField, JTextField numeroColegiaturaField, JTextField especialidadField, JTextArea horarioAtencionField, JTextField consultorioAsignadoField, JTextField telefonoField, JTextField direccionField, JTextField emailField, JComboBox<String> estadoOdontologoField) {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            idField.setText(modelo.getValueAt(filaSeleccionada, 0).toString());
            nombreField.setText(modelo.getValueAt(filaSeleccionada, 1).toString());
            apellidoField.setText(modelo.getValueAt(filaSeleccionada, 2).toString());
            numeroColegiaturaField.setText(modelo.getValueAt(filaSeleccionada, 3).toString());
            especialidadField.setText(modelo.getValueAt(filaSeleccionada, 4).toString());
            horarioAtencionField.setText(modelo.getValueAt(filaSeleccionada, 5).toString());
            consultorioAsignadoField.setText(modelo.getValueAt(filaSeleccionada, 6).toString());
            telefonoField.setText(modelo.getValueAt(filaSeleccionada, 7).toString());
            direccionField.setText(modelo.getValueAt(filaSeleccionada, 8).toString());
            emailField.setText(modelo.getValueAt(filaSeleccionada, 9).toString());
            estadoOdontologoField.setSelectedItem(modelo.getValueAt(filaSeleccionada, 10).toString());
        }
    }

    private boolean validarDatos(String nombre, String apellido, String numeroColegiatura, String especialidad, String horarioAtencion, String consultorioAsignadoStr, String telefono, String direccion, String email, String estadoOdontologo) {
        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(null, "El nombre es obligatorio y debe contener solo letras.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (apellido == null || apellido.trim().isEmpty() || !apellido.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(null, "El apellido es obligatorio y debe contener solo letras.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (numeroColegiatura == null || numeroColegiatura.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El número de colegiatura es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (especialidad == null || especialidad.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "La especialidad es obligatoria.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (horarioAtencion == null || horarioAtencion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El horario de atención es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (consultorioAsignadoStr == null || consultorioAsignadoStr.trim().isEmpty() || !consultorioAsignadoStr.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "El consultorio asignado debe ser un número positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(consultorioAsignadoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El consultorio asignado debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (telefono == null || telefono.trim().isEmpty() || telefono.length() < 7) {
            JOptionPane.showMessageDialog(null, "El teléfono debe tener al menos 7 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "La dirección es obligatoria.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(null, "El correo electrónico no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (estadoOdontologo == null || (!estadoOdontologo.equals("A") && !estadoOdontologo.equals("I"))) {
            JOptionPane.showMessageDialog(null, "El estado del odontólogo debe ser 'A' (Activo) o 'I' (Inactivo).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void guardar(String nombre, String apellido, String numeroColegiatura, String especialidad, String horarioAtencion, String consultorioAsignadoStr, String telefono, String direccion, String email, String estadoOdontologo) {
        if (!validarDatos(nombre, apellido, numeroColegiatura, especialidad, horarioAtencion, consultorioAsignadoStr, telefono, direccion, email, estadoOdontologo)) {
            return;
        }

        int consultorioAsignado = Integer.parseInt(consultorioAsignadoStr);
        String query = "INSERT INTO odontologos (nombre, apellido, numero_colegiatura, especialidad, horario_atencion, consultorio_asignado, telefono, direccion, email, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, numeroColegiatura);
            pst.setString(4, especialidad);
            pst.setString(5, horarioAtencion);
            pst.setInt(6, consultorioAsignado);
            pst.setString(7, telefono);
            pst.setString(8, direccion);
            pst.setString(9, email);
            pst.setString(10, estadoOdontologo);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Odontólogo guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el odontólogo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

        public void buscarOdontologos(JTable tabla, String terminoBusqueda) {
        String query = "SELECT * FROM odontologos WHERE nombre LIKE ? OR apellido LIKE ? OR numero_colegiatura LIKE ?";
        try {
            PreparedStatement pst = cn.prepareStatement(query);
            pst.setString(1, "%" + terminoBusqueda + "%");
            pst.setString(2, "%" + terminoBusqueda + "%");
            pst.setString(3, "%" + terminoBusqueda + "%");
            ResultSet rs = pst.executeQuery();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] odontologo = new Object[11];
                odontologo[0] = rs.getInt("odontologos_id");
                odontologo[1] = rs.getString("nombre");
                odontologo[2] = rs.getString("apellido");
                odontologo[3] = rs.getString("numero_colegiatura");
                odontologo[4] = rs.getString("especialidad");
                odontologo[5] = rs.getString("horario_atencion");
                odontologo[6] = rs.getInt("consultorio_asignado");
                odontologo[7] = rs.getString("telefono");
                odontologo[8] = rs.getString("direccion");
                odontologo[9] = rs.getString("email");
                odontologo[10] = rs.getString("estado");
                modelo.addRow(odontologo);
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar los odontólogos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modificar(int id, String nombre, String apellido, String numeroColegiatura, String especialidad, String horarioAtencion, String consultorioAsignadoStr, String telefono, String direccion, String email, String estadoOdontologo) {
        if (!validarDatos(nombre, apellido, numeroColegiatura, especialidad, horarioAtencion, consultorioAsignadoStr, telefono, direccion, email, estadoOdontologo)) {
            return;
        }

        int consultorioAsignado = Integer.parseInt(consultorioAsignadoStr);
        String query = "UPDATE odontologos SET nombre = ?, apellido = ?, numero_colegiatura = ?, especialidad = ?, horario_atencion = ?, consultorio_asignado = ?, telefono = ?, direccion = ?, email = ?, estado = ? WHERE odontologos_id = ?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, nombre);
            pst.setString(2, apellido);
            pst.setString(3, numeroColegiatura);
            pst.setString(4, especialidad);
            pst.setString(5, horarioAtencion);
            pst.setInt(6, consultorioAsignado);
            pst.setString(7, telefono);
            pst.setString(8, direccion);
            pst.setString(9, email);
            pst.setString(10, estadoOdontologo);
            pst.setInt(11, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Odontólogo modificado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar el odontólogo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}