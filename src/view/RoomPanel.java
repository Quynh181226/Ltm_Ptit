package view;

import control.ClientControl;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class RoomPanel extends JPanel {
    private JTextField txtId, txtName, txtDesc;
    private JButton btnAdd, btnUpdate, btnDelete, btnViewAssets, btnStats;
    private JTable tblRoom;
    private DefaultTableModel modelRoom;
    private ClientControl control = new ClientControl();

    public RoomPanel() {
        setLayout(new BorderLayout());

        // Input
        JPanel top = new JPanel(new FlowLayout());
        txtId = new JTextField(6);
        txtName = new JTextField(10);
        txtDesc = new JTextField(15);
        btnAdd = new JButton("Add");
        // --- SỬA Ở ĐÂY ---
        // btnUpdate = new JButton("Update");
        // btnDelete = new JButton("Delete");
        btnViewAssets = new JButton("View Assets");
        btnStats = new JButton("Statistics");

        top.add(new JLabel("ID:")); top.add(txtId);
        top.add(new JLabel("Name:")); top.add(txtName);
        top.add(new JLabel("Desc:")); top.add(txtDesc);
        top.add(btnAdd);
        // --- SỬA Ở ĐÂY ---
        // top.add(btnUpdate); top.add(btnDelete);
        top.add(btnViewAssets); top.add(btnStats);
        add(top, BorderLayout.NORTH);

        // Room table
        // --- SỬA Ở ĐÂY ---
        modelRoom = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Action"}, 0);
        tblRoom = new JTable(modelRoom);

//        setSize(1940,330);

        tblRoom.setRowHeight(40);

//        add(tblRoom, BorderLayout.CENTER);
        add(new JScrollPane(tblRoom), BorderLayout.CENTER);
        tblRoom.setBackground(new Color(245, 245, 245)); // Màu xám nhẹ

        // --- SỬA Ở ĐÂY ---
        // Tùy chỉnh cột Action
        TableColumn actionColumn = tblRoom.getColumnModel().getColumn(3); // Cột thứ 4 (index 3)
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox(), tblRoom, modelRoom));

        // Events
        btnAdd.addActionListener(e -> addRoom());
        // --- SỬA Ở ĐÂY ---
        // btnDelete.addActionListener(e -> deleteRoom());
        // btnUpdate.addActionListener(e -> updateRoom());
        btnViewAssets.addActionListener(e -> viewAssetsByRoom());
        btnStats.addActionListener(e -> showStats());

        loadRooms();
    }

    private void addRoom() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                showMessage("ID không được rỗng!");
                return;
            }
            if (txtName.getText().trim().isEmpty()) {
                showMessage("Name không được rỗng!");
                return;
            }

            if (!control.openConnection()) {
                showMessage("Server connect err!!");
                return;
            }

            Room r = new Room(txtId.getText().trim(), txtName.getText().trim(), txtDesc.getText().trim());
            control.sendRequest("ADD_ROOM", r);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res != null && "SUCCESS".equals(res)) {
                showMessage("Thêm phòng thành công!");
                clearFields();
                loadRooms();
            } else {
                showMessage("Thêm thất bại: " + (res != null ? res.toString() : "No response"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi: " + ex.getMessage());
        }
    }

    private void updateRoom() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                showMessage("ID không được rỗng!");
                return;
            }
            if (txtName.getText().trim().isEmpty()) {
                showMessage("Name không được rỗng!");
                return;
            }

            if (!control.openConnection()) {
                showMessage("Không thể kết nối server!");
                return;
            }

            Room r = new Room(txtId.getText().trim(), txtName.getText().trim(), txtDesc.getText().trim());
            control.sendRequest("UPDATE_ROOM", r);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res != null && "SUCCESS".equals(res)) {
                showMessage("Cập nhật phòng thành công!");
                loadRooms();
            } else {
                showMessage("Cập nhật thất bại: " + (res != null ? res.toString() : "Unknown error"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi: " + ex.getMessage());
        }
    }

    private void deleteRoom() {
        try {
            int row = tblRoom.getSelectedRow();
            if (row < 0) {
                showMessage("Vui lòng chọn một phòng!");
                return;
            }

            String roomId = modelRoom.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa phòng " + roomId + "?\n(Lưu ý: Phòng có tài sản sẽ không xóa được)",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            if (!control.openConnection()) {
                showMessage("Không thể kết nối server!");
                return;
            }

            control.sendRequest("DEL_ROOM", roomId);
            Object res = control.receiveResponse();
            control.closeConnection();

            String msg = res != null ? res.toString() : "No response";
            if ("CANNOT_DELETE".equals(msg)) {
                showMessage("Không thể xóa: phòng còn tài sản!");
            } else if ("SUCCESS".equals(msg)) {
                showMessage("Xóa phòng thành công!");
                loadRooms();
            } else {
                showMessage("Xóa thất bại: " + msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi: " + ex.getMessage());
        }
    }

    private void viewAssetsByRoom() {
        try {
            int row = tblRoom.getSelectedRow();
            if (row < 0) {
                showMessage("Vui lòng chọn một phòng!");
                return;
            }

            String roomId = modelRoom.getValueAt(row, 0).toString();

            if (!control.openConnection()) {
                showMessage("Không thể kết nối server!");
                return;
            }

            control.sendRequest("GET_ASSETS_BY_ROOM", roomId);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res instanceof List) {
                List<Asset> list = (List<Asset>) res;
                // Tạo popup dialog
                JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Assets in Room " + roomId, true);
                dialog.setLayout(new BorderLayout());
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(this);

                DefaultTableModel modelAssets = new DefaultTableModel(new String[]{"Asset ID", "Name", "Type", "Value"}, 0);
                JTable tblAssets = new JTable(modelAssets);
                for (Asset a : list) {
                    modelAssets.addRow(new Object[]{a.getId(), a.getName(), a.getType(), a.getValue()});
                }
                dialog.add(new JScrollPane(tblAssets), BorderLayout.CENTER);

                JButton btnClose = new JButton("Close");
                btnClose.addActionListener(e -> dialog.dispose());
                dialog.add(btnClose, BorderLayout.SOUTH);

                dialog.setVisible(true);
            } else {
                showMessage("Không tải được danh sách tài sản!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi tải tài sản: " + ex.getMessage());
        }
    }

    private void showStats() {
        try {
            if (!control.openConnection()) {
                showMessage("Không thể kết nối server!");
                return;
            }

            control.sendRequest("ROOM_ASSET_COUNT", null);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res instanceof List) {
                List<String[]> data = (List<String[]>) res;
                StringBuilder sb = new StringBuilder("THỐNG KÊ PHÒNG - SỐ TÀI SẢN:\n\n");
                for (String[] row : data) {
                    sb.append("ID: ").append(row[0])
                            .append(" | Name: ").append(row[1])
                            .append(" | Des: ").append(row[2]).append(" tài sản\n");
                }
                showMessage(sb.toString(), "Thống kê", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage("Không tải được thống kê!!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi thống kê: " + ex.getMessage());
        }
    }

    private void loadRooms() {
        try {
            if (!control.openConnection()) {
                showMessage("Không thể kết nối server!");
                return;
            }

            control.sendRequest("GET_ALL_ROOMS", null);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res instanceof List) {
                List<Room> list = (List<Room>) res;
                modelRoom.setRowCount(0);
                // --- SỬA Ở ĐÂY ---
                for (Room r : list) {
                    modelRoom.addRow(new Object[]{r.getId(), r.getName(), r.getDescription(), null});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi tải danh sách phòng: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtDesc.setText("");
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    private void showMessage(String msg, String title, int messageType) {
        JOptionPane.showMessageDialog(this, msg, title, messageType);
    }

    // --- SỬA Ở ĐÂY ---
    // Renderer cho cột Action
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnUpdate = new JButton("Update");
        private JButton btnDelete = new JButton("Delete");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            add(btnUpdate);
            add(btnDelete);

            setBackground(new Color(245, 245, 245));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor cho cột Action
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private JButton btnUpdate = new JButton("Update");
        private JButton btnDelete = new JButton("Delete");
        private JTable table;
        private DefaultTableModel model;
        private int row;

        public ButtonEditor(JCheckBox checkBox, JTable table, DefaultTableModel model) {
            this.table = table;
            this.model = model;
            panel.add(btnUpdate);
            panel.add(btnDelete);

            btnUpdate.addActionListener(e -> {
                row = table.getEditingRow();
                if (row >= 0) {
                    String id = (String) model.getValueAt(row, 0);
                    String name = (String) model.getValueAt(row, 1);
                    String desc = (String) model.getValueAt(row, 2);
                    updateRoomFromTable(id, name, desc);
                }
                fireEditingStopped();
            });

            btnDelete.addActionListener(e -> {
                row = table.getEditingRow();
                if (row >= 0) {
                    String id = (String) model.getValueAt(row, 0);
                    deleteRoomFromTable(id);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    // --- SỬA Ở ĐÂY ---
    private void updateRoomFromTable(String id, String name, String desc) {
        try {
            if (!control.openConnection()) {
                showMessage("Không thể kết nối server!");
                return;
            }
            Room r = new Room(id, name, desc);
            control.sendRequest("UPDATE_ROOM", r);
            Object res = control.receiveResponse();
            control.closeConnection();
            if (res != null && "SUCCESS".equals(res)) {
                showMessage("Cập nhật phòng thành công!");
                loadRooms();
            } else {
                showMessage("Cập nhật thất bại: " + (res != null ? res.toString() : "Unknown error"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi: " + ex.getMessage());
        }
    }

    // --- SỬA Ở ĐÂY ---
    private void deleteRoomFromTable(String id) {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa phòng " + id + "?\n(Lưu ý: Phòng có tài sản sẽ không xóa được)",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            if (!control.openConnection()) {
                showMessage("Không thể kết nối server!");
                return;
            }
            control.sendRequest("DEL_ROOM", id);
            Object res = control.receiveResponse();
            control.closeConnection();
            String msg = res != null ? res.toString() : "No response";
            if ("CANNOT_DELETE".equals(msg)) {
                showMessage("Không thể xóa: phòng còn tài sản!");
            } else if ("SUCCESS".equals(msg)) {
                showMessage("Xóa phòng thành công!");
                loadRooms();
            } else {
                showMessage("Xóa thất bại: " + msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Lỗi: " + ex.getMessage());
        }
    }
}