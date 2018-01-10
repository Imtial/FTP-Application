package Enan;
import FTP.NetworkUtil;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server implements Runnable {
    private Thread t;
    private ServerSocket serverSocket;
    private int timeOut = (int) Math.pow(10,5);
    private String path = "H:\\";
    private File file = new File(path);
    private OutputStream os = null;
    private InputStream is = null;

    private List<String> ID;

    Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(timeOut);

        // HardCoded User info file path, Fix later
        loadInfo("D:\\ID.txt");

        t = new Thread(this);
        t.start();
    }

    private void download() throws IOException {
        DataInputStream in = new DataInputStream(is);
        DataOutputStream out = new DataOutputStream(os);

        byte [] bytes = new byte[1024];
        int noOfBytes;

        out.writeUTF("Enter the file path : ");
        path = in.readUTF();
        while (!(new File(path)).exists()){
            out.writeBoolean(false);
            path = in.readUTF();
        }
        out.writeBoolean(true);
        file = new File(path);

        FileInputStream fin = new FileInputStream(file);
        while ((noOfBytes = fin.read(bytes)) != -1) {
            os.write(bytes, 0, noOfBytes);
        }

        fin.close();
        in.close();
        out.close();
    }
    private void upload() throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        DataInputStream in = new DataInputStream(is);


        byte [] bytes = new byte[1024];
        int noOfBytes;

        out.writeUTF("Enter the file path : ");
        path = in.readUTF();
        while (!(new File(path)).exists()) {
            out.writeBoolean(false);
            path = in.readUTF();
        }
        out.writeBoolean(true);
        file = new File(path);

        FileOutputStream fout = new FileOutputStream(file);
        while ((noOfBytes = is.read(bytes)) != -1) {
            fout.write(bytes,0, noOfBytes);
        }
        fout.close();
        out.close();
        in.close();
    }
    private void ls() throws IOException {
        DataOutputStream out = new DataOutputStream(os);
        DataInputStream in = new DataInputStream(is);

        out.writeUTF("Enter the path of the directory : ");
        path = in.readUTF();
        while (!(new File(path)).exists() || !(new File(path).isDirectory())) {
            out.writeBoolean(false);
            if(!(new File(path)).exists()) {
                out.writeUTF("Doesn't exist");
                path = in.readUTF();
            }
            else if (!(new File(path)).isDirectory()) {
                out.writeUTF("Not a directory");
                path = in.readUTF();
            }
        }
        out.writeBoolean(true);

        String[] lists = (new File(path)).list();
        ObjectOutputStream objs = new ObjectOutputStream(os);
        objs.writeObject(lists);
        objs.close();

        out.close();
        in.close();
    }

    private void loadInfo(String path)
    {
        ID = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while (true){
                line = reader.readLine();
                if(line == null) break;
                ID.add(line);
            }
            reader.close();
        }
        catch (IOException e){
            System.out.println("In loadInfo: " + e);
        }
    }
    private boolean validateUser(Socket socket)
    {
        String userPass = (String)new NetworkUtil(socket).read();
        for (String line: ID){
            if(line.equals(userPass))
                return true;
        }
        return false;
    }
    private List<String> subDir(String dir){
        File file = new File(dir);
        List<File> files = new ArrayList<>();
        if(file.isDirectory())
        Collections.addAll(files,file.listFiles());
        return files;
    }
    @Override
    public void run() {
        try {
            System.out.println("Waiting on port " + serverSocket.getLocalPort());
            Socket socket = serverSocket.accept();
            System.out.println("Connected to " + socket.getRemoteSocketAddress());

            //LOG IN
            boolean isValid = validateUser(socket);
            while(!isValid && socket.isConnected()){
                System.out.println("User not registered!");//If Client is closed, infinite loop   FIX NEEDED
            }
            if(isValid) System.out.println("Successful LOG IN.");

            os = socket.getOutputStream();
            is = socket.getInputStream();
            DataOutputStream out = new DataOutputStream(os);
            DataInputStream in = new DataInputStream(is);

            NetworkUtil connection = new NetworkUtil(socket);
            connection.write(path);
            connection.write();
            /*out.writeUTF("Thank you for connecting to " + serverSocket.getInetAddress());
            System.out.println(socket.getRemoteSocketAddress() + " : " + in.readUTF());

            String menu = "1. Download" + "\n" +
                          "2. Upload" + "\n" +
                          "3. List" + "\n";


            out.writeUTF(menu);
            int choice = Integer.parseInt(in.readUTF());
            switch (choice) {
                case 1:
                    download();
                    break;
                case 2:
                    upload();
                    break;
                case 3:
                    ls();
                    break;
                default:
                    break;
            }
            */
            in.close();
            out.close();
            is.close();
            os.close();
            socket.close();
        } catch (SocketTimeoutException e) {
            System.out.println("Server timed out!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
//        int port = Integer.parseInt(args[0]);
        int port = 9999;
        new Server(port);
    }
}