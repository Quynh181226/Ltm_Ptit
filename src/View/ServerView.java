package View;

import Control.ServerControl;

public class ServerView {
    public ServerView() {
        new ServerControl();
        showMessage("TCP server is running...");
    }

    public void showMessage(String msg)
    {
        System.out.println(msg);
    }
}
