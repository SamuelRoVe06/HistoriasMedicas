package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

public class Protesis_Dentales {
    private Connection cn;

    public Protesis_Dentales(Connection conexion) {
        this.cn = conexion;
    }

    public void consultar(JTable Tabla) {
        String query = "SELECT * FROM protesis_dentales";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] protesis = new Object[6]; // Aumentamos en uno el tamaño del arreglo
                protesis[0] = rs.getInt("protesis_id");
                protesis[1] = rs.getString("tipo_protesis");
                protesis[2] = rs.getDate("fecha_colocacion");
                protesis[3] = rs.getDouble("costo");
                protesis[4] = rs.getInt("Historias_Clinicas_historia_clinica_id");
                protesis[5] = rs.getInt("Historias_Clinicas_Pacientes_paciente_id");
                modelo.addRow(protesis);
            }
            Tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al consultar las prótesis dentales: " + e.getMessage());
        }
    }

    public void cargarDatosSeleccionados(JTable Tabla, JTextField idField, JTextField tipoField, JTextField costoField, JTextField HCIdField, JTextField pacienteIdField, JDateChooser fechaColocacionChooser) {
        int filaSeleccionada = Tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
            idField.setText(modelo.getValueAt(filaSeleccionada, 0).toString());
            tipoField.setText(modelo.getValueAt(filaSeleccionada, 1).toString());
            fechaColocacionChooser.setDate((java.util.Date) modelo.getValueAt(filaSeleccionada, 2));
            costoField.setText(modelo.getValueAt(filaSeleccionada, 3).toString());
            HCIdField.setText(modelo.getValueAt(filaSeleccionada, 4).toString());
            pacienteIdField.setText(modelo.getValueAt(filaSeleccionada, 5).toString());
        }
    }

    public void guardar(String tipo, String fechaColocacion, double costo, int HCId, int pacienteId) {
        String query = "INSERT INTO protesis_dentales (tipo_protesis, fecha_colocacion, costo, Historias_Clinicas_historia_clinica_id, Historias_Clinicas_Pacientes_paciente_id) VALUES ('"
                + tipo + "', '" + fechaColocacion + "', " + costo + ", " + HCId + ", " + pacienteId + ")";
        try {
            Statement st = cn.createStatement();
            st.executeUpdate(query);
            // Mostrar mensaje de éxito
            System.out.println("Protesis dental guardada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al guardar la prótesis dental: " + e.getMessage());
        }
    }

    public void modificar(int id, String tipo, String fechaColocacion, double costo, int HCId, int pacienteId) {
        String query = "UPDATE protesis_dentales SET tipo_protesis='" + tipo + "', fecha_colocacion='" + fechaColocacion + "', costo=" + costo
                + ", Historias_Clinicas_historia_clinica_id=" + HCId + ", Historias_Clinicas_Pacientes_paciente_id=" + pacienteId
                + " WHERE protesis_id=" + id;
        try {
            Statement st = cn.createStatement();
            st.executeUpdate(query);
            // Mostrar mensaje de éxito
            System.out.println("Protesis dental modificada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al modificar la prótesis dental: " + e.getMessage());
        }
    }
}