package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class HistoriasClinicas {
    private Connection cn;

    public HistoriasClinicas(Connection conexion) {
        this.cn = conexion;
    }

    public void consultarHC(JTable Tabla) {
        String query = "SELECT * FROM historias_clinicas";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] historiaClinica = new Object[4];
                historiaClinica[0] = rs.getInt("historia_clinica_id");
                historiaClinica[1] = rs.getString("resumen_consultas");
                historiaClinica[2] = rs.getString("diagnostico");
                historiaClinica[3] = rs.getInt("Pacientes_paciente_id");
                modelo.addRow(historiaClinica);
            }
            Tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al consultar las historias clínicas: " + e.getMessage());
        }
    }

    public void cargarDatosSeleccionados(JTable Tabla, JTextField idField, JTextArea resumenConsultasField, JTextArea diagnosticoField, JTextField pacienteIdField) {
        int filaSeleccionada = Tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
            idField.setText(modelo.getValueAt(filaSeleccionada, 0).toString());
            resumenConsultasField.setText(modelo.getValueAt(filaSeleccionada, 1).toString());
            diagnosticoField.setText(modelo.getValueAt(filaSeleccionada, 2).toString());
            pacienteIdField.setText(modelo.getValueAt(filaSeleccionada, 3).toString());
        }
    }

    public void guardar(String resumenConsultas, String diagnostico, int pacienteId) {
        String query = "INSERT INTO historias_clinicas (resumen_consultas, diagnostico, Pacientes_paciente_id) VALUES ('" + resumenConsultas + "', '" + diagnostico + "', " + pacienteId + ")";
        try {
            Statement st = cn.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error al guardar la historia clínica: " + e.getMessage());
        }
    }

    public void modificar(int id, String resumenConsultas, String diagnostico, int pacienteId) {
        String query = "UPDATE historias_clinicas SET resumen_consultas='" + resumenConsultas + "', diagnostico='" + diagnostico + "', Pacientes_paciente_id=" + pacienteId + " WHERE historia_clinica_id=" + id;
        try {
            Statement st = cn.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error al modificar la historia clínica: " + e.getMessage());
        }
    }
}
