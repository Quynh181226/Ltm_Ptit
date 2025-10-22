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

public class AssetPanel extends JPanel {
    private JTextField txtId, txtName, txtType, txtRoom, txtValue, txtSearchRoom, txtMinValue, txtMaxValue;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearchRoom, btnSearchValue;
    private JTable table;
    private DefaultTableModel model;
    private ClientControl control = new ClientControl();

    public AssetPanel() {
        setLayout(new BorderLayout());

        // --- TOP INPUT ---
        JPanel top = new JPanel(new FlowLayout());

        txtId = new JTextField(6);
        txtName = new JTextField(10);
        txtType = new JTextField(8);
        txtRoom = new JTextField(6);
        txtValue = new JTextField(6);
        txtSearchRoom = new JTextField(6);
        txtMinValue = new JTextField(5);
        txtMaxValue = new JTextField(5);

        btnAdd = new JButton("Add");
        // --- SỬA Ở ĐÂY ---
        // btnUpdate = new JButton("Update");
        // btnDelete = new JButton("Delete");
        btnSearchRoom = new JButton("Search by Room");
        btnSearchValue = new JButton("Search by Value");

        top.add(new JLabel("Room ID:"));
        top.add(txtSearchRoom);
        top.add(btnSearchRoom);

        top.add(new JLabel("Value from:"));
        top.add(txtMinValue);
        top.add(new JLabel("to"));
        top.add(txtMaxValue);
        top.add(btnSearchValue);

        top.add(new JLabel("ID")); top.add(txtId);
        top.add(new JLabel("Name")); top.add(txtName);
        top.add(new JLabel("Type")); top.add(txtType);
        top.add(new JLabel("RoomId")); top.add(txtRoom);
        top.add(new JLabel("Value")); top.add(txtValue);
        top.add(btnAdd);
        // --- SỬA Ở ĐÂY ---
        // top.add(btnUpdate); top.add(btnDelete);

//        setSize(940,330);

        add(top, BorderLayout.NORTH);

        // --- TABLE ---
        // --- SỬA Ở ĐÂY ---
        model = new DefaultTableModel(new String[]{"ID", "Name", "Type", "Room", "Value", "Action"}, 0);
        table = new JTable(model);

        table.setRowHeight(40);

        add(new JScrollPane(table), BorderLayout.CENTER);

        table.setBackground(new Color(245, 245, 245)); // Màu xám nhẹ

//        add(table, BorderLayout.CENTER);
        // --- SỬA Ở ĐÂY ---
        // Tùy chỉnh cột Action
        TableColumn actionColumn = table.getColumnModel().getColumn(5); // Cột thứ 6 (index 5)
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox(), table, model));

        // --- EVENTS ---
        btnAdd.addActionListener(e -> addAsset());
        // --- SỬA Ở ĐÂY ---
        // btnUpdate.addActionListener(e -> updateAsset());
        // btnDelete.addActionListener(e -> deleteAsset());
        btnSearchRoom.addActionListener(e -> searchByRoom());
        btnSearchValue.addActionListener(e -> searchByValue());

//        table.getSelectionModel().addListSelectionListener(e -> fillFields());

        loadAssets();
    }

//    private void fillFields() {
//        int row = table.getSelectedRow();
//        if (row >= 0) {
//            txtId.setText(model.getValueAt(row, 0).toString());
//            txtName.setText(model.getValueAt(row, 1).toString());
//            txtType.setText(model.getValueAt(row, 2).toString());
//            txtRoom.setText(model.getValueAt(row, 3).toString());
//            txtValue.setText(model.getValueAt(row, 4).toString());
//        }
//    }

    private void addAsset() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                showMessage("ID cannot be empty!!");
                return;
            }
            if (txtName.getText().trim().isEmpty()) {
                showMessage("Name cannot be empty!!");
                return;
            }

            double value;
            try {
                value = Double.parseDouble(txtValue.getText().trim());
                if (value < 0) {
                    showMessage("Value must be >= 0!!");
                    return;
                }
            } catch (NumberFormatException e) {
                showMessage("Value must be a valid number!!");
                return;
            }

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            Asset a = new Asset(txtId.getText().trim(), txtName.getText().trim(),
                    txtType.getText().trim(), txtRoom.getText().trim(), value);
            control.sendRequest("AddAsset", a);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res != null && "Success".equals(res)) {
                showMessage("Asset added success!!");
                clearFields();
                loadAssets();
            } else {
                showMessage("Failed to add asset: " + (res != null ? res.toString() : "No response"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }

    private void updateAsset() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                showMessage("ID cannot be empty!!");
                return;
            }
            if (txtName.getText().trim().isEmpty()) {
                showMessage("Name cannot be empty!!");
                return;
            }

            double value;
            try {
                value = Double.parseDouble(txtValue.getText().trim());
                if (value < 0) {
                    showMessage("Value must be >= 0!!");
                    return;
                }
            } catch (NumberFormatException e) {
                showMessage("Value must be a valid number!!");
                return;
            }

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            Asset a = new Asset(txtId.getText().trim(), txtName.getText().trim(),
                    txtType.getText().trim(), txtRoom.getText().trim(), value);
            control.sendRequest("UpdateAsset", a);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res != null && "Success".equals(res)) {
                showMessage("Asset updated success!!");
                loadAssets();
            } else {
                showMessage("Failed to update asset: " + (res != null ? res.toString() : "No response"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }

    private void deleteAsset() {
        try {
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                showMessage("Please select an asset to delete!!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete asset " + id + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            control.sendRequest("DeleteAsset", id);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res != null && "Success".equals(res)) {
                showMessage("Asset deleted success!!");
                clearFields();
                loadAssets();
            } else {
                showMessage("Failed to delete asset: " + (res != null ? res.toString() : "No response"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }

    private void loadAssets() {
        try {
            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            control.sendRequest("GetAllAssets", null);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res instanceof List) {
                List<Asset> list = (List<Asset>) res;
                updateTable(list);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error loading assets: " + ex.getMessage());
        }
    }

    private void searchByRoom() {
        try {
            String roomId = txtSearchRoom.getText().trim();
            if (roomId.isEmpty()) {
                showMessage("Please enter Room ID!");
                return;
            }

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            control.sendRequest("SearchAssetByRoom", roomId);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res instanceof List) {
                List<Asset> list = (List<Asset>) res;
                updateTable(list);
            } else {
                showMessage("No assets found in room " + roomId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Search error: " + ex.getMessage());
        }
    }

    private void searchByValue() {
        try {
            double min, max;
            try {
                min = Double.parseDouble(txtMinValue.getText().trim());
                max = Double.parseDouble(txtMaxValue.getText().trim());
                if (min > max) {
                    showMessage("Min must be <= Max!!");
                    return;
                }
            } catch (NumberFormatException e) {
                showMessage("Min/Max must be valid numbers!!");
                return;
            }

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            double[] range = {min, max};
            control.sendRequest("SearchAssetByValue", range);
            Object res = control.receiveResponse();
            control.closeConnection();

            if (res instanceof List) {
                List<Asset> list = (List<Asset>) res;
                updateTable(list);
            } else {
                showMessage("No assets found in the value range!!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Search error: " + ex.getMessage());
        }
    }

    private void updateTable(List<Asset> list) {
        model.setRowCount(0);
        for (Asset a : list) {
            model.addRow(new Object[]{a.getId(), a.getName(), a.getType(), a.getRoomId(), a.getValue(), null});
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtType.setText("");
        txtRoom.setText("");
        txtValue.setText("");
        txtSearchRoom.setText("");
        txtMinValue.setText("");
        txtMaxValue.setText("");
    }

    private void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
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
                    String type = (String) model.getValueAt(row, 2);
                    String room = (String) model.getValueAt(row, 3);
                    double value = Double.parseDouble(model.getValueAt(row, 4).toString());
                    updateAssetFromTable(id, name, type, room, value);
                }
                fireEditingStopped();
            });

            btnDelete.addActionListener(e -> {
                row = table.getEditingRow();
                if (row >= 0) {
                    String id = (String) model.getValueAt(row, 0);
                    deleteAssetFromTable(id);
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

    private void updateAssetFromTable(String id, String name, String type, String room, double value) {
        try {
            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }
            Asset a = new Asset(id, name, type, room, value);
            control.sendRequest("UpdateAsset", a);
            Object res = control.receiveResponse();
            control.closeConnection();
            if (res != null && "Success".equals(res)) {
                showMessage("Asset updated success!!");
                loadAssets();
            } else {
                showMessage("Failed to update asset: " + (res != null ? res.toString() : "No response"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }

    private void deleteAssetFromTable(String id) {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete asset " + id + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }
            control.sendRequest("DeleteAsset", id);
            Object res = control.receiveResponse();
            control.closeConnection();
            if (res != null && "Success".equals(res)) {
                showMessage("Asset deleted success!!");
                loadAssets();
            } else {
                showMessage("Failed to delete asset: " + (res != null ? res.toString() : "No response"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
        }
    }
}