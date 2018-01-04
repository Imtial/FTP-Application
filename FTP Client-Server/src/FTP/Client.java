package FTP;

import sun.net.util.IPAddressUtil;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client implements Runnable{
    String address;
    int port;
    String username;
    String password;
    private NetworkUtil network;
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

    private void connectClient(){
        network = new NetworkUtil(address,port);
        String userPassAppended = username + "," + password;
        network.write(userPassAppended);

        //boolean valid = network
    }
    public void sendCommand(String cmd){
        network.write(cmd);
    }
    public File download(String filePath){
        while(!isCommandSent()){
            network.write(filePath);
        }
        Object recieved = network.read();
        if(recieved instanceof File){
            return (File)recieved;
        }
        else{
            System.out.println("File Download failed.");
            return null;
        }
    }

    public void upload(String filePath){
        File file = new File(filePath);
        network.write(file);
    }
    private boolean isCommandSent(){
        Boolean flag = false;
        flag =(Boolean) network.read();
        return flag;
    }
}
