package sistema_historias_medicas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;

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
            JOptionPane.showMessageDialog(null, "Error al consultar las historias clínicas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void buscarHistoriasClinicas(JTable tabla, String criterio) {
           String query = "SELECT * FROM historias_clinicas WHERE resumen_consultas LIKE ? OR diagnostico LIKE ?";
           try {
               PreparedStatement pst = cn.prepareStatement(query);
               String parametro = "%" + criterio + "%";
               pst.setString(1, parametro);
               pst.setString(2, parametro);
               ResultSet rs = pst.executeQuery();
               DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
               modelo.setRowCount(0);
               while (rs.next()) {
                   Object[] historiaClinica = new Object[4];
                   historiaClinica[0] = rs.getInt("historia_clinica_id");
                   historiaClinica[1] = rs.getString("resumen_consultas");
                   historiaClinica[2] = rs.getString("diagnostico");
                   historiaClinica[3] = rs.getInt("Pacientes_paciente_id");
                   modelo.addRow(historiaClinica);
               }
               tabla.setModel(modelo);
           } catch (SQLException e) {
               JOptionPane.showMessageDialog(null, "Error al buscar las historias clínicas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        if (!validarHistorial(pacienteId)) {
            return;
        }

        String query = "INSERT INTO historias_clinicas (resumen_consultas, diagnostico, Pacientes_paciente_id) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, resumenConsultas);
            pst.setString(2, diagnostico);
            pst.setInt(3, pacienteId);
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la historia clínica: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modificar(int id, String resumenConsultas, String diagnostico, int pacienteId) {
        if (!validarHistorial(pacienteId)) {
            return;
        }

        String query = "UPDATE historias_clinicas SET resumen_consultas=?, diagnostico=?, Pacientes_paciente_id=? WHERE historia_clinica_id=?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, resumenConsultas);
            pst.setString(2, diagnostico);
            pst.setInt(3, pacienteId);
            pst.setInt(4, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar la historia clínica: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarHistorial(int pacienteId) {
        if (!pacienteExiste(pacienteId)) {
            JOptionPane.showMessageDialog(null, "Error: El ID del paciente no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validar que no haya más historias clínicas que pacientes
        int numPacientes = contarPacientes();
        int numHistorias = contarHistorias();
        if (numHistorias >= numPacientes) {
            JOptionPane.showMessageDialog(null, "Error: No se pueden registrar más historias clínicas que el número de pacientes.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validar que no haya más de una historia clínica para el mismo paciente
        if (existeHistoria(pacienteId)) {
            JOptionPane.showMessageDialog(null, "Error: Ya existe una historia clínica para el paciente con ID " + pacienteId, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean pacienteExiste(int pacienteId) {
        String query = "SELECT COUNT(*) FROM pacientes WHERE paciente_id = ?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setInt(1, pacienteId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar la existencia del paciente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private int contarPacientes() {
        String query = "SELECT COUNT(*) FROM pacientes";
        try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al contar los pacientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    private int contarHistorias() {
        String query = "SELECT COUNT(*) FROM historias_clinicas";
        try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al contar las historias clínicas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    private boolean existeHistoria(int pacienteId) {
        String query = "SELECT COUNT(*) FROM historias_clinicas WHERE Pacientes_paciente_id = ?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setInt(1, pacienteId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar la existencia de una historia clínica: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
