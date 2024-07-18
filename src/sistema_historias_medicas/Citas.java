package sistema_historias_medicas;

import com.toedter.calendar.JDateChooser;
import java.sql.Connection;
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
                cita[0] = rs.getInt("cita_id");                   // cita_id
                cita[1] = rs.getTimestamp("fecha_hora");          // fecha_hora
                cita[2] = rs.getString("motivo_consulta");        // motivo_consulta
                cita[3] = rs.getString("estado_cita");            // estado_cita
                cita[4] = rs.getInt("Pacientes_paciente_id");     // Pacientes_paciente_id
                cita[5] = rs.getInt("Odontologos_odontologos_id"); // Odontologos_odontologos_id

                modelo.addRow(cita);
            }
            tabla.setModel(modelo);
        } catch (SQLException e) {
            System.err.println("Error al consultar las citas: " + e.getMessage());
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHoraFormatted = sdf.format(fechaHora);

        String query = "INSERT INTO citas (fecha_hora, motivo_consulta, estado_cita, Pacientes_paciente_id, Odontologos_odontologos_id) " +
                       "VALUES ('" + fechaHoraFormatted + "', '" + motivoConsulta + "', '" + estadoCita + "', " + pacienteId + ", " + odontologoId + ")";
        try {
            Statement st = cn.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error al guardar la cita: " + e.getMessage());
        }
    }

    public void modificar(int citaId, Date fechaHora, String motivoConsulta, String estadoCita, int pacienteId, int odontologoId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHoraFormatted = sdf.format(fechaHora);

        String query = "UPDATE citas SET fecha_hora='" + fechaHoraFormatted + "', motivo_consulta='" + motivoConsulta + "', estado_cita='" + estadoCita + "', " +
                       "Pacientes_paciente_id=" + pacienteId + ", Odontologos_odontologos_id=" + odontologoId + " WHERE cita_id=" + citaId;
        try {
            Statement st = cn.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            System.err.println("Error al modificar la cita: " + e.getMessage());
        }
    }
}
