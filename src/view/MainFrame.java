package view;

import model.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User user;
    public MainFrame(User user) {
        super("Management"); this.user = user;
        setSize(1300,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Assets", new AssetPanel());
        tabs.addTab("Rooms", new RoomPanel());

        add(tabs, BorderLayout.CENTER);
    }
}