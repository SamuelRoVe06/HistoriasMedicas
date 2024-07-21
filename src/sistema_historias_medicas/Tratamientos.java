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
import java.text.SimpleDateFormat;

public class Tratamientos {
    private Connection cn;

    public Tratamientos(Connection conexion) {
        this.cn = conexion;
    }

    public void consultar(JTable Tabla) {
        String query = "SELECT * FROM tratamientos";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
            modelo.setRowCount(0); // Limpia la tabla antes de agregar nuevas filas

            while (rs.next()) {
                Object[] tratamiento = new Object[8];
                tratamiento[0] = rs.getInt("tratamiento_id");
                tratamiento[1] = rs.getString("descripcion");
                tratamiento[2] = rs.getDouble("costo");
                tratamiento[3] = rs.getDate("fecha_inicio");
                tratamiento[4] = rs.getDate("fecha_fin");
                tratamiento[5] = rs.getString("procedimientos");
                tratamiento[6] = rs.getInt("Historias_Clinicas_historia_clinica_id");
                tratamiento[7] = rs.getInt("Historias_Clinicas_Pacientes_paciente_id");
                modelo.addRow(tratamiento);
            }
            Tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al consultar los tratamientos: " + e.getMessage());
        }
    }

    public void cargarDatosSeleccionados(JTable Tabla, JTextField idField, JTextArea descripcionField, JTextField costoField, JDateChooser fechaInicioChooser, JDateChooser fechaFinChooser, JTextArea procedimientosField, JTextField HCIdField, JTextField pacienteIdField) {
        int filaSeleccionada = Tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) Tabla.getModel();
            Object idValue = modelo.getValueAt(filaSeleccionada, 0);
            Object descripcionValue = modelo.getValueAt(filaSeleccionada, 1);
            Object costoValue = modelo.getValueAt(filaSeleccionada, 2);
            Object fechaInicioValue = modelo.getValueAt(filaSeleccionada, 3);
            Object fechaFinValue = modelo.getValueAt(filaSeleccionada, 4);
            Object procedimientosValue = modelo.getValueAt(filaSeleccionada, 5);
            Object HCIdValue = modelo.getValueAt(filaSeleccionada, 6);
            Object pacienteIdValue = modelo.getValueAt(filaSeleccionada, 7);

            if (idValue != null) {
                idField.setText(idValue.toString());
            }
            if (descripcionValue != null) {
                descripcionField.setText(descripcionValue.toString());
            }
            if (costoValue != null) {
                costoField.setText(costoValue.toString());
            }
            if (fechaInicioValue != null && fechaInicioValue instanceof java.util.Date) {
                fechaInicioChooser.setDate((java.util.Date) fechaInicioValue);
            }
            if (fechaFinValue != null && fechaFinValue instanceof java.util.Date) {
                fechaFinChooser.setDate((java.util.Date) fechaFinValue);
            }
            if (procedimientosValue != null) {
                procedimientosField.setText(procedimientosValue.toString());
            }
            if (HCIdValue != null) {
                HCIdField.setText(HCIdValue.toString());
            }
            if (pacienteIdValue != null) {
                pacienteIdField.setText(pacienteIdValue.toString());
            }
        }
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
            JOptionPane.showMessageDialog(null, "Error al verificar la existencia de la historia clínica y el paciente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public void guardar(String descripcion, double costo, java.util.Date fechaInicio, java.util.Date fechaFin, String procedimientos, int HCId, int pacienteId) {
        if (!validarCampos(descripcion, costo, fechaInicio, fechaFin, procedimientos, HCId, pacienteId)) {
            return;
        }
        if (!verificarExistenciaHistoriaClinicaPaciente(HCId, pacienteId)) {
            JOptionPane.showMessageDialog(null, "La historia clínica con ID " + HCId + " y el paciente con ID " + pacienteId + " no existen en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicioFormatted = sdf.format(fechaInicio);
        String fechaFinFormatted = sdf.format(fechaFin);

        String query = "INSERT INTO tratamientos (descripcion, costo, fecha_inicio, fecha_fin, procedimientos, Historias_Clinicas_historia_clinica_id, Historias_Clinicas_Pacientes_paciente_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, descripcion);
            pst.setDouble(2, costo);
            pst.setString(3, fechaInicioFormatted);
            pst.setString(4, fechaFinFormatted);
            pst.setString(5, procedimientos);
            pst.setInt(6, HCId);
            pst.setInt(7, pacienteId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tratamiento guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el tratamiento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modificar(int id, String descripcion, double costo, java.util.Date fechaInicio, java.util.Date fechaFin, String procedimientos, int HCId, int pacienteId) {
        if (id <= 0) {
            JOptionPane.showMessageDialog(null, "El ID del tratamiento debe ser positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!validarCampos(descripcion, costo, fechaInicio, fechaFin, procedimientos, HCId, pacienteId)) {
            return;
        }
        if (!verificarExistenciaHistoriaClinicaPaciente(HCId, pacienteId)) {
            JOptionPane.showMessageDialog(null, "La historia clínica y el paciente no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicioFormatted = sdf.format(fechaInicio);
        String fechaFinFormatted = sdf.format(fechaFin);

        String query = "UPDATE tratamientos SET descripcion=?, costo=?, fecha_inicio=?, fecha_fin=?, procedimientos=?, Historias_Clinicas_historia_clinica_id=?, Historias_Clinicas_Pacientes_paciente_id=? WHERE tratamiento_id=?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, descripcion);
            pst.setDouble(2, costo);
            pst.setString(3, fechaInicioFormatted);
            pst.setString(4, fechaFinFormatted);
            pst.setString(5, procedimientos);
            pst.setInt(6, HCId);
            pst.setInt(7, pacienteId);
            pst.setInt(8, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tratamiento modificado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar el tratamiento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void buscarTratamientos(JTable tabla, String terminoBusqueda) {
        String query = "SELECT * FROM tratamientos WHERE descripcion LIKE ?";
        try {
            PreparedStatement pst = cn.prepareStatement(query);
            pst.setString(1, "%" + terminoBusqueda + "%");
            ResultSet rs = pst.executeQuery();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0); // Limpia la tabla antes de agregar nuevas filas

            while (rs.next()) {
                Object[] tratamiento = new Object[8];
                tratamiento[0] = rs.getInt("tratamiento_id");
                tratamiento[1] = rs.getString("descripcion");
                tratamiento[2] = rs.getDouble("costo");
                tratamiento[3] = rs.getDate("fecha_inicio");
                tratamiento[4] = rs.getDate("fecha_fin");
                tratamiento[5] = rs.getString("procedimientos");
                tratamiento[6] = rs.getInt("Historias_Clinicas_historia_clinica_id");
                tratamiento[7] = rs.getInt("Historias_Clinicas_Pacientes_paciente_id");
                modelo.addRow(tratamiento);
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar tratamientos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos(String descripcion, double costo, java.util.Date fechaInicio, java.util.Date fechaFin, String procedimientos, int HCId, int pacienteId) {
        if (descripcion.trim().isEmpty() || costo <= 0 || fechaInicio == null || fechaFin == null || procedimientos.trim().isEmpty() || HCId <= 0 || pacienteId <= 0) {
            JOptionPane.showMessageDialog(null, "Todos los campos deben estar completos y válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
