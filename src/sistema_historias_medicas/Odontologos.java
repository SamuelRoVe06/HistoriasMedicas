package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Odontologos {
    private Connection cn;

    public Odontologos(Connection conexion) {
        this.cn = conexion;
    }

    public void consultar(JTable Tabla) {
        String query = "SELECT * FROM odontologos";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] odontologo = new Object[11]; // Aumentamos en uno el tamaño del arreglo
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
                odontologo[10] = rs.getString("estado"); // Agregamos el estado del odontólogo
                modelo.addRow(odontologo);
            }
            Tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al consultar los odontólogos: " + e.getMessage());
        }
    }

    public void cargarDatosSeleccionados(JTable Tabla, JTextField idField, JTextField nombreField, JTextField apellidoField, JTextField numeroColegiaturaField, JTextField especialidadField, JTextArea horarioAtencionField, JTextField consultorioAsignadoField, JTextField telefonoField, JTextField direccionField, JTextField emailField, JComboBox<String> estadoOdontologoField) {
        int filaSeleccionada = Tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
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
            estadoOdontologoField.setSelectedItem(modelo.getValueAt(filaSeleccionada, 10).toString()); // Seteamos el estado del odontólogo en el JComboBox
        }
    }

    public void guardar(String nombre, String apellido, String numeroColegiatura, String especialidad, String horarioAtencion, int consultorioAsignado, String telefono, String direccion, String email, String estadoOdontologo) {
        String query = "INSERT INTO odontologos (nombre, apellido, numero_colegiatura, especialidad, horario_atencion, consultorio_asignado, telefono, direccion, email, estado) VALUES ('" + nombre + "', '" + apellido + "', '" + numeroColegiatura + "', '" + especialidad + "', '" + horarioAtencion + "', " + consultorioAsignado + ", '" + telefono + "', '" + direccion + "', '" + email + "', '" + estadoOdontologo + "')";
        try {
            Statement st = cn.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error al guardar el odontólogo: " + e.getMessage());
        }
    }

    public void modificar(int id, String nombre, String apellido, String numeroColegiatura, String especialidad, String horarioAtencion, int consultorioAsignado, String telefono, String direccion, String email, String estadoOdontologo) {
        String query = "UPDATE odontologos SET nombre='" + nombre + "', apellido='" + apellido + "', numero_colegiatura='" + numeroColegiatura + "', especialidad='" + especialidad + "', horario_atencion='" + horarioAtencion + "', consultorio_asignado=" + consultorioAsignado + ", telefono='" + telefono + "', direccion='" + direccion + "', email='" + email + "', estado='" + estadoOdontologo + "' WHERE odontologos_id=" + id;
        try {
            Statement st = cn.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error al modificar el odontólogo: " + e.getMessage());
        }
    }
}
