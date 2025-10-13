/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Frontend;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author admin
 */
public class PortResultCellRender extends DefaultTableCellRenderer{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String state = table.getValueAt(row, 2).toString().toLowerCase(); // Cột 2 là "STATE"

        if (state.equals("open")) {
            c.setBackground(new Color(200, 255, 200)); // xanh lá nhạt
        } else if (state.contains("filtered")) {
            c.setBackground(new Color(200, 230, 255)); // xanh biển nhạt
        } else if (state.contains("closed")) {
            c.setBackground(new Color(240, 240, 240)); // xám nhạt
        } else {
            c.setBackground(Color.WHITE); // mặc định
        }

        return c;
    }
}
