package Control;
import Model.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientControl {
    private Socket mySocket;
    private String serverHost = "localhost";
    private int serverPort = 7777;
    
    public ClientControl(){
    }
    
    public Socket openConnection(){
    try {
                mySocket = new Socket(serverHost, serverPort);     
                if (mySocket==null) System.out.println("Khong the mo ket noi");
                else System.out.println("Mo ket noi thanh cong");
            } catch (Exception ex) {
            ex.printStackTrace();
                return null;
            }
    return mySocket;
    }
    
    public boolean sendData(User user){
        try {
                    ObjectOutputStream oos = 
        new ObjectOutputStream(mySocket.getOutputStream());
        oos.writeObject(user);
                } catch (Exception ex) {
                ex.printStackTrace();
                    return false;
                }
        return true;
    }
    
    public String receiveData(){
        String result = null;
        try {
        ObjectInputStream ois = 
        new ObjectInputStream(mySocket.getInputStream());
        Object o = ois.readObject();
        if(o instanceof String){
        result = (String)o;
        }
                } catch (Exception ex) {
                 ex.printStackTrace();     return null;
                }
        return result;
    }
    
    public boolean closeConnection(){
        try {
         mySocket.close();
                } catch (Exception ex) {
                 ex.printStackTrace();        return false;
                }
        return true;
    }
}
