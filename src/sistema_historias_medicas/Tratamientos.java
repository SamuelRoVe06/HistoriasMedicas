package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

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
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] tratamiento = new Object[8]; // Corregido el tamaño del arreglo
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

            // Verificar si los valores son null antes de convertir a String o asignar
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

    public void guardar(String descripcion, double costo, String fechaInicio, String fechaFin, String procedimientos, int HCId, int pacienteId) {
        String query = "INSERT INTO tratamientos (descripcion, costo, fecha_inicio, fecha_fin, procedimientos, Historias_Clinicas_historia_clinica_id, Historias_Clinicas_Pacientes_paciente_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, descripcion);
            pst.setDouble(2, costo);
            pst.setString(3, fechaInicio);
            pst.setString(4, fechaFin);
            pst.setString(5, procedimientos);
            pst.setInt(6, HCId);
            pst.setInt(7, pacienteId);
            pst.executeUpdate();
            System.out.println("Tratamiento guardado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al guardar el tratamiento: " + e.getMessage());
        }
    }

    public void modificar(int id, String descripcion, double costo, String fechaInicio, String fechaFin, String procedimientos, int HCId, int pacienteId) {
        String query = "UPDATE tratamientos SET descripcion=?, costo=?, fecha_inicio=?, fecha_fin=?, procedimientos=?, Historias_Clinicas_historia_clinica_id=?, Historias_Clinicas_Pacientes_paciente_id=? WHERE tratamiento_id=?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, descripcion);
            pst.setDouble(2, costo);
            pst.setString(3, fechaInicio);
            pst.setString(4, fechaFin);
            pst.setString(5, procedimientos);
            pst.setInt(6, HCId);
            pst.setInt(7, pacienteId);
            pst.setInt(8, id);
            pst.executeUpdate();
            System.out.println("Tratamiento modificado correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al modificar el tratamiento: " + e.getMessage());
        }
    }
}
