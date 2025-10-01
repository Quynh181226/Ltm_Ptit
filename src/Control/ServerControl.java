package Control;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import Model.User;

public class ServerControl {
    private Connection con;
    private ServerSocket myServer;
    private int serverPort = 7777;
    public ServerControl() {
        //connect to database my SQL
        getDBConnection("laptrinhmang", "root","");
       
        //open server to accept connect from client
        openServer(serverPort);
        while (true)
        {
            // accept connect from client, receive request fron client and send response to client
            listenning();
        }
    }
    private void getDBConnection(String dbName, String username,String password)
    {
        String dbUrl = "jdbc:mysql://127.0.0.1:3306/" + dbName+"?serverTimezone=UTC";
        String dbClass = "com.mysql.cj.jdbc.Driver";
        try {
         Class.forName(dbClass);
         con = DriverManager.getConnection (dbUrl,username, password);
         if (con==null)  
             System.out.println("Ket noi khong thanh cong");
         else 
             System.out.println("Ket noi thanh cong");
             
         
         
        }
        catch(Exception e) {
         e.printStackTrace();
        } 
}
    private void openServer(int portNumber){
        try 
        {
            myServer = new ServerSocket(portNumber);
            if (myServer==null) 
                System.out.println("Khong mo dc");
            else 
                System.out.println("Mo cong thanh cong ");
        }
        catch(IOException e) {
                e.printStackTrace();
        }
}
    private void listenning(){
        System.out.println("GoiHam Listening");
        try {    
            Socket clientSocket = myServer.accept();
            System.out.println(clientSocket.getRemoteSocketAddress()+" connected\n");
            ObjectInputStream ois = new 
            ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream oos = new 
            ObjectOutputStream(clientSocket.getOutputStream());
            Object o = ois.readObject();
            if(o instanceof User){
                User user = (User)o;
                if(checkUser(user)){
                    oos.writeObject("ok");
                }
                else
                    oos.writeObject("false");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean checkUser(User user) throws Exception {
            String query = "Select * FROM users WHERE user_name ='" + user.getUserName() 
             + "' AND password ='" + user.getPassword() + "'";
            try {
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             if (rs.next()) {
              return true;
             }
            }catch(Exception e) {
            throw e;
            } 
            return false;
  }
    
    
        
}

