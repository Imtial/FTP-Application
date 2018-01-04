package FTP;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainWindowController {

    @FXML
    private TextField host;

    @FXML
    private TextField userName;

    @FXML
    private TextField userPass;

    @FXML
    private TextField port;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private Button uploadButton;

    @FXML
    private Button downloadButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button clearMessageButton;

    @FXML
    private Button clearLogButton;

    @FXML
    private Tab messageTab;

    @FXML
    private TextArea msgTabText;

    @FXML
    private Tab qeuedTab;

    @FXML
    private Tab successfulTab;

    @FXML
    private Tab failedTab;

    @FXML
    private TextField localDir;

    @FXML
    private TextField serverDir;

    @FXML
    private Button localBackButton;

    @FXML
    private Button localForwardButton;

    @FXML
    private Button serverBackButton;

    @FXML
    private Button serverForwardButton;

    @FXML
    private ListView<String> localFileList;

    @FXML
    private ListView<String> serverFileList;

    private Main main;

    @FXML
    void connectCalled(ActionEvent event) {
        String hostAddress = host.getText();
        String user = userName.getText();
        String password = userPass.getText();
        int portNo = Integer.parseInt(port.getText());
        Client client = new Client(hostAddress,portNo,user,password);
    }
    void init(){
        localDir.setEditable(false);
        localDir.setFocusTraversable(false);

        serverDir.setEditable(false);
        serverDir.setFocusTraversable(false);

        localFileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        serverFileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public List<File> getFiles(String fileLocation){
        List<File> files = new ArrayList<>();
        File file = new File(fileLocation);
        Collections.addAll(files,file.listFiles());
        return files;
    }
    public void setLocalFileList(List<File> fileList)
    {
        localDir.setText(fileList.get(0).getParent());
        localFileList.getItems().clear();
        for(File f : fileList)
        {
            localFileList.getItems().add(f.getName());
        }
    }

    public void setServerFileList(List<File> fileList)
    {
        serverDir.setText(fileList.get(0).getParent());
        serverFileList.getItems().clear();
        for(File f : fileList)
        {
            serverFileList.getItems().add(f.getName());
        }
    }

    @FXML
    void lFileListClicked(MouseEvent click) {
        if(click.getClickCount() == 2)
        {
            String dir = localDir.getText() + "\\" +
                    localFileList.getSelectionModel().getSelectedItem();
            File f = new File(dir);
            if(f.isDirectory())
                setLocalFileList(getFiles(dir));
        }
    }

    @FXML
    void sFileListClicked(MouseEvent click) {
        if(click.getClickCount() == 2)
        {
            String dir = serverDir.getText() + "\\" +
                    serverFileList.getSelectionModel().getSelectedItem();

            File f = new File(dir);

            if(f.isDirectory())
                setServerFileList(getFiles(dir));
        }
    }

    public void setMain(Main main){
        this.main = main;
    }
}
