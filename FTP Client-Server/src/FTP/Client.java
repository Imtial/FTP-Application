package FTP;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import sun.net.util.IPAddressUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Client implements Runnable{
    String address;
    int port;
    String username;
    String password;
    public NetworkUtil network;
    private String serverPath;
    private List<String> serverFiles;
    @Override
    public void run(){

    }

    public Client(String address, int port, String username, String password){
        try{
            if(IPAddressUtil.isIPv4LiteralAddress(address) || IPAddressUtil.isIPv6LiteralAddress(address))
            {
                this.address = address;
            }
            else
            {
                String ip [] = InetAddress.getByName(address).toString().split("/");
                this.address = ip[ip.length - 1];
            }
        }catch (UnknownHostException e){
            System.out.println("In setAddress() : " + e);
        }
        this.port = port;
        this.username = username;
        this.password = password;

        connectClient();
    }
    public void setAddress(String address){
        try{
            if(IPAddressUtil.isIPv4LiteralAddress(address) || IPAddressUtil.isIPv6LiteralAddress(address))
            {
                this.address = address;
            }
            else
            {
                String ip [] = InetAddress.getByName(address).toString().split("/");
                this.address = ip[ip.length - 1];
            }
        }catch (UnknownHostException e){
            System.out.println("In setAddress() : " + e);
        }
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerPath() {
        return serverPath;
    }

    public List<String> getServerFiles() {
        return serverFiles;
    }

    public void setServerPath() {
        Object ob = network.read();
        if(ob instanceof String)
        {
            this.serverPath = (String) ob;
        }
        else
        {
            //Send server a msg to resend the path
        }
    }

    public void setServerFiles(List<String> serverFiles) {
        this.serverFiles = serverFiles;
    }

    private void connectClient(){
        network = new NetworkUtil(address,port);

        //For testing purpose
        if(network.socket.isConnected())
        {
            System.out.println("Connected");
        }

        String userPassAppended = username + "," + password;
        network.write(userPassAppended);//Send Info for login

        //boolean valid = network
    }
    public void sendCommand(String cmd){
        network.write(cmd);
    }

    public File download(String filePath) throws IOException{
        DataOutputStream sendPath = new DataOutputStream(network.socket.getOutputStream());
        sendPath.writeUTF(filePath);
        ByteInputStream inputStream = null;
        inputStream = (ByteInputStream)network.socket.getInputStream();
        FileOutputStream outputStream = new FileOutputStream(MainWindowController.selectedFileName);

        int read = -1;
        byte [] buffer = new byte[4096];
        while((read = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer,0,read);
            //System.out.println(file.length()/1024 + "KB");

        }
        inputStream.close();
        outputStream.close();
        System.out.println("Download completed");
        return new File(MainWindowController.selectedFileName);
    }

    public void upload(String filePath) throws IOException{
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        OutputStream outputStream = network.socket.getOutputStream();

        int read = -1;
        byte [] buffer = new byte[4096];
        while((read = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer,0,read);

        }
        inputStream.close();
        outputStream.close();
    }

    private boolean isCommandSent(){
        Boolean flag = false;
        flag =(Boolean) network.read();
        return flag;
    }
}
