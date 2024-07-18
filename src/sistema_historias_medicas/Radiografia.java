package sistema_historias_medicas;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

public class Radiografia {
    private Connection cn;

    public Radiografia(Connection conexion) {
        this.cn = conexion;
    }

    public void consultar(JTable Tabla) {
        String query = "SELECT * FROM radiografias";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] radiografia = new Object[6]; // Aumentamos en uno el tamaño del arreglo
                radiografia[0] = rs.getInt("radiografia_id");
                radiografia[1] = rs.getDate("fecha_radiografia");
                radiografia[2] = rs.getString("tipo_radiografia");
                radiografia[3] = rs.getString("descripcion");
                radiografia[4] = rs.getInt("Historias_Clinicas_historia_clinica_id");
                radiografia[5] = rs.getInt("Historias_Clinicas_Pacientes_paciente_id");
                modelo.addRow(radiografia);
            }
            Tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al consultar las radiografías: " + e.getMessage());
        }
    }

    public void cargarDatosSeleccionados(JTable Tabla, JTextField idField, JDateChooser fechaField, JTextField tipoField, JTextArea descripcionField, JTextField HCIdField, JTextField pacienteIdField) {
        int filaSeleccionada = Tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
            Object idValue = modelo.getValueAt(filaSeleccionada, 0);
            Object fechaValue = modelo.getValueAt(filaSeleccionada, 1);
            Object tipoValue = modelo.getValueAt(filaSeleccionada, 2);
            Object descripcionValue = modelo.getValueAt(filaSeleccionada, 3);
            Object HCIdValue = modelo.getValueAt(filaSeleccionada, 4);
            Object pacienteIdValue = modelo.getValueAt(filaSeleccionada, 5);

            // Verificar si los valores son null antes de convertir a String o asignar
            if (idValue != null) {
                idField.setText(idValue.toString());
            }
            if (fechaValue != null && fechaValue instanceof java.util.Date) {
                fechaField.setDate((java.util.Date) fechaValue);
            }
            if (tipoValue != null) {
                tipoField.setText(tipoValue.toString());
            }
            if (descripcionValue != null) {
                descripcionField.setText(descripcionValue.toString());
            }
            if (HCIdValue != null) {
                HCIdField.setText(HCIdValue.toString());
            }
            if (pacienteIdValue != null) {
                pacienteIdField.setText(pacienteIdValue.toString());
            }
        }
    }

    public void guardar(java.sql.Date fechaRadiografia, String tipoRadiografia, String descripcion, int HCId, int pacienteId) {
        String query = "INSERT INTO radiografias (fecha_radiografia, tipo_radiografia, descripcion, Historias_Clinicas_historia_clinica_id, Historias_Clinicas_Pacientes_paciente_id) VALUES (?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setDate(1, fechaRadiografia);
            pst.setString(2, tipoRadiografia);
            pst.setString(3, descripcion);
            pst.setInt(4, HCId);
            pst.setInt(5, pacienteId);
            pst.executeUpdate();
            System.out.println("Radiografía guardada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al guardar la radiografía: " + e.getMessage());
        }
    }

public void modificar(int id, java.sql.Date fechaRadiografia, String tipoRadiografia, String descripcion, int HCId, int pacienteId) {
    String query = "UPDATE radiografias SET fecha_radiografia=?, tipo_radiografia=?, descripcion=?, Historias_Clinicas_historia_clinica_id=?, Historias_Clinicas_Pacientes_paciente_id=? WHERE radiografia_id=?";
    try (PreparedStatement pst = cn.prepareStatement(query)) {
        pst.setDate(1, fechaRadiografia);
        pst.setString(2, tipoRadiografia);
        pst.setString(3, descripcion);
        pst.setInt(4, HCId);
        pst.setInt(5, pacienteId);
        pst.setInt(6, id);
        
        int rowsUpdated = pst.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Radiografía modificada correctamente.");
        } else {
            System.out.println("No se pudo modificar la radiografía.");
        }
    } catch (SQLException e) {
        System.err.println("Error al modificar la radiografía: " + e.getMessage());
    }
}

}
