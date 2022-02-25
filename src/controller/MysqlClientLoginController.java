package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class MysqlClientLoginController {
    public TextField txtHost;
    public TextField txtPort;
    public TextField txtUserName;
    public TextField txtPassword;
    public Button btnConnect;
    public Button btnExit;

    public void initialize(){

    }



    public void btnConnect_OnAction(ActionEvent actionEvent) {

        //validation
        if (txtHost.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Host can't be empty").show();
            txtHost.requestFocus();
            txtHost.selectAll();
            return;
        }else if (!txtPort.getText().matches("\\d+")){
            new Alert(Alert.AlertType.ERROR, "Invalid port").show();
            txtPort.requestFocus();
            txtPort.selectAll();
            return;
        }else if (txtUserName.getText().trim().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Username can't be empty").show();
            txtUserName.requestFocus();
            txtUserName.selectAll();
            return;
        }

        String command = String.format("mysql -h %s -u %s -p%s --port %s -e exit",
                txtHost.getText(),
                txtUserName.getText(),
                txtPassword.getText(),
                txtPort.getText());

        try {
            Process mysql = Runtime.getRuntime().exec(command);
            System.out.println(mysql.waitFor());
            int exitCode = mysql.waitFor();
            if (exitCode !=0){
                new Alert(Alert.AlertType.ERROR, "Can't establish the connection, try again").show();
                txtUserName.requestFocus();
                txtUserName.selectAll();
            }else{
                System.out.println("done");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void btnExit_OnAction(ActionEvent actionEvent) {System.exit(0);}
}
