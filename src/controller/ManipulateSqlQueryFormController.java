package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManipulateSqlQueryFormController {


    public TextArea txtOutput;
    public TextArea txtCommand;
    public Label lblCurrentSchema;
    private Process mysql;


    public void initData(String host, String port, String userName, String password){
//        try {
//            mysql = new ProcessBuilder("mysql",
//                    "-h", host,
//                    "--port", port,
//                    "-u", userName,
//                    "-p").start();
//
//            mysql.getOutputStream().write(password.getBytes());
//            mysql.getOutputStream().flush();
//
//            txtCommand.getScene().getWindow().setOnCloseRequest(event -> {
//                if (mysql.isAlive()){
//                    mysql.destroy();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR, "Failed to establish the connection for some reason").show();
//            if (mysql.isAlive()){
//                mysql.destroyForcibly();
//            }
//        }


        ProcessBuilder mysqlBuilder = new ProcessBuilder("mysql",
                "-h", host,
                "-u", userName,
                "--port", port,
                "-p",
                "-n",           // <- This is important (https://dev.mysql.com/doc/refman/8.0/en/mysql-command-options.html#option_mysql_unbuffered)
                "-L",           // <- This is important (https://dev.mysql.com/doc/refman/8.0/en/mysql-command-options.html#option_mysql_skip-line-numbers)
                "-f",           // <- This is important (https://dev.mysql.com/doc/refman/8.0/en/mysql-command-options.html#option_mysql_force)
                "-v",           // <- This is important (https://dev.mysql.com/doc/refman/8.0/en/mysql-command-options.html#option_mysql_verbose)
                "-v",
                "-v");
        try {

            mysqlBuilder.redirectErrorStream(true);
            this.mysql = mysqlBuilder.start();

            processInputStream(mysql.getInputStream());

            this.mysql.getOutputStream().write((password + "\n").getBytes());
            this.mysql.getOutputStream().flush();

            txtCommand.getScene().getWindow().setOnCloseRequest(event -> {
                if (this.mysql.isAlive()) {
                    this.mysql.destroy();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to establish the connection for some reason").show();
            if (mysql.isAlive()) {
                mysql.destroyForcibly();
            }
            Platform.exit();
        }









    }



    public void btnExecute_OnAction(ActionEvent actionEvent) {
//        String statement = txtCommand.getText();
//        if (!txtCommand.getText().endsWith(";")){
//            statement += ";";
//        }
//        try {
//            System.out.println(mysql.isAlive());
//            mysql.getOutputStream().write(statement.getBytes());
//            mysql.getOutputStream().flush();
//
//            InputStream is = mysql.getErrorStream();
//            byte[] buffer = new byte[1024];
//            System.out.println(is.read(buffer));
//            txtOutput.setText(new String(buffer));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String statement = txtCommand.getText();

        if (!txtCommand.getText().endsWith(";")) {
            statement += ";";
        }

        try {
            txtOutput.clear();

            if (statement.equalsIgnoreCase("exit;")){
                Platform.exit();
                return;
            }

            this.mysql.getOutputStream().write((statement + "\n").getBytes());
            this.mysql.getOutputStream().flush();
            txtCommand.selectAll();

            Pattern pattern = Pattern.compile(".*[;]?((?i)(use)) (?<db>[A-Za-z0-9-_]+);.*");
            Matcher matcher = pattern.matcher(statement);
            if (matcher.matches()){
                lblCurrentSchema.setText("SCHEMA: " + matcher.group("db"));
                txtOutput.setText("Database changed");
                lblCurrentSchema.setTextFill(Paint.valueOf("blue"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void processInputStream(InputStream is) {

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    byte[] buffer = new byte[1024];
                    int read = is.read(buffer);

                    if (read == -1) {
                        break;
                    }

                    String output = new String(buffer, 0, read);
                    Platform.runLater(() -> {
                        txtOutput.appendText(output);

                        /* A little hack */
                        if (txtOutput.getText().contentEquals("Enter password: ")){
                            txtOutput.clear();
                            txtOutput.setText("Welcome to DEP-8 MySQL Client Shell\n" +
                                    "----------------------------------------\n\n" +
                                    "Please enter your command above to proceed.\nThank you! \uD83D\uDE09" +
                                    "\n\nCopyright © 2022 DEP8 IJSE. All Rights Reserved.\n");
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.setDaemon(true);
        thread.start();

    }



}
