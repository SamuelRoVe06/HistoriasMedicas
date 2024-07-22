package sistema_historias_medicas;

import com.toedter.calendar.JDateChooser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

public class Citas {
    private Connection cn;

    public Citas(Connection conexion) {
        this.cn = conexion;
    }

    public void consultar(JTable tabla) {
        String query = "SELECT * FROM citas";
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] cita = new Object[6];
                cita[0] = rs.getInt("cita_id");
                cita[1] = rs.getTimestamp("fecha_hora");
                cita[2] = rs.getString("motivo_consulta");
                cita[3] = rs.getString("estado_cita");
                cita[4] = rs.getInt("Pacientes_paciente_id");
                cita[5] = rs.getInt("Odontologos_odontologos_id");

                modelo.addRow(cita);
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al consultar las citas: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarDatosSeleccionados(JTable tabla, JTextField idField, JDateChooser fechaHoraField, JTextArea motivoConsultaField, JComboBox estadoCitaField, JTextField pacienteIdField, JTextField odontologoIdField) {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            idField.setText(modelo.getValueAt(filaSeleccionada, 0).toString());
            fechaHoraField.setDate((Date) modelo.getValueAt(filaSeleccionada, 1));
            motivoConsultaField.setText((String) modelo.getValueAt(filaSeleccionada, 2));
            estadoCitaField.setSelectedItem((String) modelo.getValueAt(filaSeleccionada, 3));
            pacienteIdField.setText(modelo.getValueAt(filaSeleccionada, 4).toString());
            odontologoIdField.setText(modelo.getValueAt(filaSeleccionada, 5).toString());
        }
    }

    public void guardar(Date fechaHora, String motivoConsulta, String estadoCita, int pacienteId, int odontologoId) {
        // Validar campos obligatorios
        if (!validarCampos(fechaHora, motivoConsulta, estadoCita, pacienteId, odontologoId)) {
            return;
        }

        // Validar cita solo si es una nueva cita (no se especifica un ID de cita en este caso)
        if (!validarCita(0, fechaHora, estadoCita, pacienteId, odontologoId)) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHoraFormatted = sdf.format(fechaHora);

        String query = "INSERT INTO citas (fecha_hora, motivo_consulta, estado_cita, Pacientes_paciente_id, Odontologos_odontologos_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, fechaHoraFormatted);
            pst.setString(2, motivoConsulta);
            pst.setString(3, estadoCita);
            pst.setInt(4, pacienteId);
            pst.setInt(5, odontologoId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Cita guardada correctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la cita: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void buscarCitas(JTable tabla, String criterio) {
        String query = "SELECT * FROM citas WHERE motivo_consulta LIKE ? OR estado_cita LIKE ?";
        try {
            PreparedStatement pst = cn.prepareStatement(query);
            String parametro = "%" + criterio + "%";
            pst.setString(1, parametro);
            pst.setString(2, parametro);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            modelo.setRowCount(0);
            while (rs.next()) {
                Object[] cita = new Object[6];
                cita[0] = rs.getInt("cita_id");
                cita[1] = rs.getTimestamp("fecha_hora");
                cita[2] = rs.getString("motivo_consulta");
                cita[3] = rs.getString("estado_cita");
                cita[4] = rs.getInt("Pacientes_paciente_id");
                cita[5] = rs.getInt("Odontologos_odontologos_id");

                modelo.addRow(cita);
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar las citas: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void modificar(int citaId, Date fechaHora, String motivoConsulta, String estadoCita, int pacienteId, int odontologoId) {
        if (!validarCampos(fechaHora, motivoConsulta, estadoCita, pacienteId, odontologoId) || !validarCita(citaId, fechaHora, estadoCita, pacienteId, odontologoId)) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHoraFormatted = sdf.format(fechaHora);

        String query = "UPDATE citas SET fecha_hora=?, motivo_consulta=?, estado_cita=?, Pacientes_paciente_id=?, Odontologos_odontologos_id=? WHERE cita_id=?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setString(1, fechaHoraFormatted);
            pst.setString(2, motivoConsulta);
            pst.setString(3, estadoCita);
            pst.setInt(4, pacienteId);
            pst.setInt(5, odontologoId);
            pst.setInt(6, citaId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Cita modificada correctamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar la cita: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos(Date fechaHora, String motivoConsulta, String estadoCita, int pacienteId, int odontologoId) {
        if (fechaHora == null || motivoConsulta.isEmpty() || estadoCita.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Error: Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!existePaciente(pacienteId)) {
            JOptionPane.showMessageDialog(null, "Error: El ID del paciente no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!existeOdontologo(odontologoId)) {
            JOptionPane.showMessageDialog(null, "Error: El ID del odontólogo no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean validarCita(int citaId, Date fechaHora, String estadoCita, int pacienteId, int odontologoId) {
    // Verificar citas pendientes solo si se está estableciendo el estado como 'Pendiente'
    if (estadoCita.equals("Pendiente")) {
        if (tieneCitaPendiente(pacienteId, citaId)) {
            JOptionPane.showMessageDialog(null, "Error: El paciente ya tiene una cita pendiente.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Verificar si el odontólogo ya tiene una cita en el mismo horario
    if (citaOdontologoEnHorario(fechaHora, odontologoId, citaId)) {
        JOptionPane.showMessageDialog(null, "Error: El odontólogo ya tiene una cita en el mismo horario.", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    return true;
}


    private boolean tieneCitaPendiente(int pacienteId, int citaId) {
    String query = "SELECT COUNT(*) FROM citas WHERE Pacientes_paciente_id = ? AND estado_cita = 'Pendiente' AND cita_id != ?";
    try (PreparedStatement pst = cn.prepareStatement(query)) {
        pst.setInt(1, pacienteId);
        pst.setInt(2, citaId);
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al verificar citas pendientes: ", "Error", JOptionPane.ERROR_MESSAGE);
    }
    return false;
}

    private boolean citaOdontologoEnHorario(Date fechaHora, int odontologoId, int citaId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHoraFormatted = sdf.format(fechaHora);

        String query = "SELECT COUNT(*) FROM citas WHERE Odontologos_odontologos_id = ? AND fecha_hora = ? AND cita_id != ?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setInt(1, odontologoId);
            pst.setString(2, fechaHoraFormatted);
            pst.setInt(3, citaId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar citas del odontólogo: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private boolean existePaciente(int pacienteId) {
        String query = "SELECT COUNT(*) FROM pacientes WHERE paciente_id = ?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setInt(1, pacienteId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar existencia del paciente: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private boolean existeOdontologo(int odontologoId) {
        String query = "SELECT COUNT(*) FROM odontologos WHERE odontologos_id = ?";
        try (PreparedStatement pst = cn.prepareStatement(query)) {
            pst.setInt(1, odontologoId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar existencia del odontólogo: ", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
