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
        // btnUpdate = new JButton("Update");
        // btnDelete = new JButton("Delete");
        btnViewAssets = new JButton("View Assets");
        btnStats = new JButton("Statistics");

        top.add(new JLabel("ID:")); top.add(txtId);
        top.add(new JLabel("Name:")); top.add(txtName);
        top.add(new JLabel("Desc:")); top.add(txtDesc);
        top.add(btnAdd);
        // top.add(btnUpdate); top.add(btnDelete);
        top.add(btnViewAssets); top.add(btnStats);
        add(top, BorderLayout.NORTH);

        // Room table
        modelRoom = new DefaultTableModel(new String[]{"ID", "Name", "Description", "Action"}, 0);
        tblRoom = new JTable(modelRoom);

//        setSize(1940,330);

        tblRoom.setRowHeight(40);

//        add(tblRoom, BorderLayout.CENTER);
        add(new JScrollPane(tblRoom), BorderLayout.CENTER);
        tblRoom.setBackground(new Color(245, 245, 245));

        // Tùy chỉnh cột Action
        TableColumn actionColumn = tblRoom.getColumnModel().getColumn(3);
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
                showMessage("ID cannot be empty!!");
                return;
            }
            if (txtName.getText().trim().isEmpty()) {
                showMessage("Name cannot be empty!!");
                return;
            }

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            Room r = new Room(txtId.getText().trim(), txtName.getText().trim(), txtDesc.getText().trim());
            control.sendRequest("AddRoom", r);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res != null && "Success".equals(res)) {
                showMessage("Room added success!!");
                clearFields();
                loadRooms();
            } else {
                showMessage("Failed to add room: " + (res != null ? res.toString() : "No response"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }

    private void updateRoom() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                showMessage("ID cannot be empty!!");
                return;
            }
            if (txtName.getText().trim().isEmpty()) {
                showMessage("Name cannot be empty!!");
                return;
            }

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            Room r = new Room(txtId.getText().trim(), txtName.getText().trim(), txtDesc.getText().trim());
            control.sendRequest("UpdateRoom", r);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res != null && "Success".equals(res)) {
                showMessage("Room updated success!!");
                loadRooms();
            } else {
                showMessage("Failed to update room: " + (res != null ? res.toString() : "Unknown error"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }

    private void deleteRoom() {
        try {
            int row = tblRoom.getSelectedRow();
            if (row < 0) {
                showMessage("Please select a room!!");
                return;
            }

            String roomId = modelRoom.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete room " + roomId + "?\n(Note: Rooms with assets cannot be deleted)",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            control.sendRequest("DeleteRoom", roomId);
            Object res = control.receiveResponse();
            control.closeConnection();

            String msg = res != null ? res.toString() : "No response";
            if ("CannotDelete".equals(msg)) {
                showMessage("Cannot delete: room contains assets!!");
            } else if ("Success".equals(msg)) {
                showMessage("Room deleted success!!");
                loadRooms();
            } else {
                showMessage("Failed to delete room: " + msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }

    private void viewAssetsByRoom() {
        try {
            int row = tblRoom.getSelectedRow();
            if (row < 0) {
                showMessage("Please select a room!!");
                return;
            }

            String roomId = modelRoom.getValueAt(row, 0).toString();

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            control.sendRequest("GetAssetsByRoom", roomId);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res instanceof List) {
                List<Asset> list = (List<Asset>) res;
                // Create popup dialog
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
                showMessage("Failed to load asset list!!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error loading assets: " + ex.getMessage());
        }
    }

    private void showStats() {
        try {
            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            control.sendRequest("RoomAssetCount", null);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res instanceof List) {
                List<String[]> data = (List<String[]>) res;
                StringBuilder sb = new StringBuilder("ROOM - ASSET COUNT STATISTICS:\n\n");
                for (String[] row : data) {
                    sb.append("ID: ").append(row[0])
                            .append(" | Name: ").append(row[1])
                            .append(" | Des: ").append(row[2]).append(" assets\n");
                }
                showMessage(sb.toString(), "Statistics", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage("Failed to load statistics!!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error loading statistics: " + ex.getMessage());
        }
    }

    private void loadRooms() {
        try {
            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            control.sendRequest("GetAllRooms", null);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res instanceof List) {
                List<Room> list = (List<Room>) res;
                modelRoom.setRowCount(0);
                for (Room r : list) {
                    modelRoom.addRow(new Object[]{r.getId(), r.getName(), r.getDescription(), null});
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error loading room list: " + ex.getMessage());
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

    private void updateRoomFromTable(String id, String name, String desc) {
        try {
            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }
            Room r = new Room(id, name, desc);
            control.sendRequest("UpdateRoom", r);
            Object res = control.receiveResponse();
            control.closeConnection();
            if (res != null && "Success".equals(res)) {
                showMessage("Room updated success!!");
                loadRooms();
            } else {
                showMessage("Failed to update room: " + (res != null ? res.toString() : "Unknown error"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }

    private void deleteRoomFromTable(String id) {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete room " + id + "?\n(Note: Rooms with assets cannot be deleted)",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }
            control.sendRequest("DeleteRoom", id);
            Object res = control.receiveResponse();
            control.closeConnection();
            String msg = res != null ? res.toString() : "No response";
            if ("CannotDelete".equals(msg)) {
                showMessage("Cannot delete: room contains assets!!");
            } else if ("Success".equals(msg)) {
                showMessage("Room deleted success!!");
                loadRooms();
            } else {
                showMessage("Failed to delete room: " + msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }
}