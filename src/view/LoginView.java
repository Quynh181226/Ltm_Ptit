package view;

import control.ClientControl;
import model.*;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private ClientControl control = new ClientControl();

    public LoginView() {
        super("Login");

        JPanel rowLog = new JPanel(new FlowLayout(FlowLayout.CENTER));
        txtUser = new JTextField(12);
        txtPass = new JPasswordField(12);
        btnLogin = new JButton("Login");
        setSize(260,148);

        rowLog.add(new JLabel("Username:"));
        rowLog.add(txtUser);
        rowLog.add(new JLabel("Password: "));
        rowLog.add(txtPass);
        rowLog.add(btnLogin);
        add(rowLog);

        btnLogin.addActionListener(e -> doLogin());

        // Hiển thị form
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // tự động kích thước theo component
        //pack();
        // đặt giữa màn hình
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void doLogin() {
        try {
            User u = new User(txtUser.getText(), new String(txtPass.getPassword()));

            if (!control.openConnection()) {
                showMessage("Cannot connect to server!!");
                return;
            }

            control.sendRequest("Login", u);
            Object resp = control.receiveResponse();

            if (resp == null) {
                showMessage("No response from server!!");
                control.closeConnection();
                return;
            }

            if (resp instanceof User) {
                showMessage("Login success!!");
                control.closeConnection();
                User loggedUser = (User) resp;
                new MainFrame(loggedUser).setVisible(true);
                dispose();
            } else if ("InvalidCredentials".equals(resp)) {
                showMessage("Invalid username or password!!");
                control.closeConnection();
            } else {
                showMessage("Unknown response: " + resp);
                control.closeConnection();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage());
            if (control != null) control.closeConnection();
        }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
    //    public static void main(String[] args) { new LoginView(); }
}