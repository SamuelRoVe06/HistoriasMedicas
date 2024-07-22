package sistema_historias_medicas;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;

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
                Object[] protesis = new Object[6];
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
            JOptionPane.showMessageDialog(null, "Error al consultar las prótesis dentales: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    public void guardar(String tipo, java.util.Date fechaColocacion, double costo, int HCId, int pacienteId) {
        if (tipo == null || tipo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El tipo de prótesis no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (fechaColocacion == null) {
            JOptionPane.showMessageDialog(null, "La fecha de colocación no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (costo < 0) {
            JOptionPane.showMessageDialog(null, "El costo no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (HCId <= 0) {
            JOptionPane.showMessageDialog(null, "El ID de la historia clínica debe ser positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pacienteId <= 0) {
            JOptionPane.showMessageDialog(null, "El ID del paciente debe ser positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaColocacionFormatted = sdf.format(fechaColocacion);

        String query = "INSERT INTO protesis_dentales (tipo_protesis, fecha_colocacion, costo, Historias_Clinicas_historia_clinica_id, Historias_Clinicas_Pacientes_paciente_id) VALUES ('"
                + tipo + "', '" + fechaColocacionFormatted + "', " + costo + ", " + HCId + ", " + pacienteId + ")";
        try {
            Statement st = cn.createStatement();
            st.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Prótesis dental guardada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la prótesis dental: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void modificar(int id, String tipo, java.util.Date fechaColocacion, double costo, int HCId, int pacienteId) {
        if (id <= 0) {
            JOptionPane.showMessageDialog(null, "El ID de la prótesis debe ser positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El tipo de prótesis no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (fechaColocacion == null) {
            JOptionPane.showMessageDialog(null, "La fecha de colocación no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (costo < 0) {
            JOptionPane.showMessageDialog(null, "El costo no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (HCId <= 0) {
            JOptionPane.showMessageDialog(null, "El ID de la historia clínica debe ser positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pacienteId <= 0) {
            JOptionPane.showMessageDialog(null, "El ID del paciente debe ser positivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       String fechaColocacionFormatted = sdf.format(fechaColocacion);

       String query = "UPDATE protesis_dentales SET tipo_protesis=?, fecha_colocacion=?, costo=?, Historias_Clinicas_historia_clinica_id=?, Historias_Clinicas_Pacientes_paciente_id=? WHERE protesis_id=?";
       try (PreparedStatement pst = cn.prepareStatement(query)) {
           pst.setString(1, tipo);
           pst.setString(2, fechaColocacionFormatted);
           pst.setDouble(3, costo);
           pst.setInt(4, HCId);
           pst.setInt(5, pacienteId);
           pst.setInt(6, id);
           pst.executeUpdate();
           JOptionPane.showMessageDialog(null, "Prótesis dental modificada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
       } catch (SQLException e) {
           JOptionPane.showMessageDialog(null, "Error al modificar la prótesis dental: ", "Error", JOptionPane.ERROR_MESSAGE);
       }
    }

    public void buscarProtesis(JTable tabla, String terminoBusqueda) {
        String query = "SELECT * FROM protesis_dentales WHERE tipo_protesis LIKE ?";
        try {
            PreparedStatement pst = cn.prepareStatement(query);
            pst.setString(1, "%" + terminoBusqueda + "%");
            ResultSet rs = pst.executeQuery();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] protesis = new Object[6];
                protesis[0] = rs.getInt("protesis_id");
                protesis[1] = rs.getString("tipo_protesis");
                protesis[2] = rs.getDate("fecha_colocacion");
                protesis[3] = rs.getBigDecimal("costo");
                protesis[4] = rs.getString("Historias_Clinicas_historia_clinica_id");
                protesis[5] = rs.getString("Historias_Clinicas_Pacientes_paciente_id");
                modelo.addRow(protesis);
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar las prótesis dentales: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void cargarDatosSeleccionados(JTable tabla, JTextField idField, JTextField tipoField, JTextField costoField, JTextField HCIdField, JTextField pacienteIdField, JDateChooser fechaColocacionChooser) {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            idField.setText(modelo.getValueAt(filaSeleccionada, 0).toString());
            tipoField.setText(modelo.getValueAt(filaSeleccionada, 1).toString());
            costoField.setText(modelo.getValueAt(filaSeleccionada, 3).toString());
            HCIdField.setText(modelo.getValueAt(filaSeleccionada, 4).toString());
            pacienteIdField.setText(modelo.getValueAt(filaSeleccionada, 5).toString());
            fechaColocacionChooser.setDate((java.util.Date) modelo.getValueAt(filaSeleccionada, 2));
        }
    }
}
