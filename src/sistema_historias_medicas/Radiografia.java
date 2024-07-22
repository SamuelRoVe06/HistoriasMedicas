package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
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
                Object[] radiografia = new Object[6];
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
            System.err.println("Error al consultar las radiografías: ");
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

    private boolean validarCampos(String tipoRadiografia, String descripcion, int HCId, int pacienteId) {
        if (tipoRadiografia == null || tipoRadiografia.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El tipo de radiografía no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "La descripción no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (HCId <= 0) {
            JOptionPane.showMessageDialog(null, "El ID de la historia clínica debe ser positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (pacienteId <= 0) {
            JOptionPane.showMessageDialog(null, "El ID del paciente debe ser positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean verificarExistenciaHistoriaClinicaPaciente(int HCId, int pacienteId) {
        String query = "SELECT COUNT(*) FROM Historias_Clinicas " +
                       "WHERE historia_clinica_id = ? AND EXISTS (" +
                       "SELECT 1 FROM Pacientes " +
                       "WHERE paciente_id = ? AND paciente_id IN (" +
                       "SELECT paciente_id FROM Historias_Clinicas " +
                       "WHERE historia_clinica_id = ?))";

        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setInt(1, HCId);
            pst.setInt(2, pacienteId);
            pst.setInt(3, HCId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar la existencia de la historia clínica y el paciente: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public void guardar(java.sql.Date fechaRadiografia, String tipoRadiografia, String descripcion, int HCId, int pacienteId) {
        if (!validarCampos(tipoRadiografia, descripcion, HCId, pacienteId)) {
            return;
        }
        if (!verificarExistenciaHistoriaClinicaPaciente(HCId, pacienteId)) {
            JOptionPane.showMessageDialog(null, "La historia clínica con ID " + HCId + " y el paciente con ID " + pacienteId + " no existen en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO radiografias (fecha_radiografia, tipo_radiografia, descripcion, Historias_Clinicas_historia_clinica_id, Historias_Clinicas_Pacientes_paciente_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setDate(1, fechaRadiografia);
            pst.setString(2, tipoRadiografia);
            pst.setString(3, descripcion);
            pst.setInt(4, HCId);
            pst.setInt(5, pacienteId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Radiografía guardada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la radiografía: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modificar(int id, java.sql.Date fechaRadiografia, String tipoRadiografia, String descripcion, int HCId, int pacienteId) {
        if (id <= 0) {
            JOptionPane.showMessageDialog(null, "El ID de la radiografía debe ser positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!validarCampos(tipoRadiografia, descripcion, HCId, pacienteId)) {
            return;
        }
        if (!verificarExistenciaHistoriaClinicaPaciente(HCId, pacienteId)) {
            JOptionPane.showMessageDialog(null, "La historia clínica y el paciente no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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
                JOptionPane.showMessageDialog(null, "Radiografía modificada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo modificar la radiografía.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar la radiografía: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
        public void buscarRadiografias(JTable tabla, String terminoBusqueda) {
        String query = "SELECT * FROM radiografias WHERE descripcion LIKE ?";
        try {
            PreparedStatement pst = cn.prepareStatement(query);
            pst.setString(1, "%" + terminoBusqueda + "%");
            ResultSet rs = pst.executeQuery();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] radiografia = new Object[6];
                radiografia[0] = rs.getInt("radiografia_id");
                radiografia[1] = rs.getDate("fecha_radiografia");
                radiografia[2] = rs.getString("tipo_radiografia");
                radiografia[3] = rs.getString("descripcion");
                radiografia[4] = rs.getInt("Historias_Clinicas_historia_clinica_id");
                radiografia[5] = rs.getInt("Historias_Clinicas_Pacientes_paciente_id");
                modelo.addRow(radiografia);
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar las radiografías: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}