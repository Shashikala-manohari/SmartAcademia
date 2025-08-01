package com.project.smartacademia.controller;

import com.project.smartacademia.db.DbConnection;
import com.project.smartacademia.model.User;
import com.project.smartacademia.util.security.PasswordManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginFormController {
    public AnchorPane context;
    public TextField txtEmail;
    public PasswordField txtPassword;

    public void forgotPasswordOnAction(ActionEvent actionEvent) throws IOException {
        setUi("ForgotPasswordForm");
    }

    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        String email = txtEmail.getText().toLowerCase();
        String password = txtPassword.getText().trim();

        try{
            User selectedUser = login(email);
            if (null!=selectedUser){
                if (new PasswordManager().checkPassword(password, selectedUser.getPassword())) {
                    setUi("DashboardForm");
                } else {
                    new Alert(Alert.AlertType.ERROR,
                            "Wrong Password!").show();
                }
            }else{
                new Alert(Alert.AlertType.WARNING,
                        String.format("user not found (%s)", email)).show();
            }
        }catch (ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.ERROR, e.toString()).show();
        }
       /* for (User user : Database.userTable){
            if (user.getEmail().equals(email)){
               if (user.getPassword().equals(password)){
                   System.out.println(user.toString());
                   return;
               }else{
                   new Alert(Alert.AlertType.ERROR,
                          "Wrong Password!").show();
                   return;
               }
            }
        }
        new Alert(Alert.AlertType.WARNING,
                String.format("user not found (%s)",email)).show();*/
    }

    public void createAnAccountOnAction(ActionEvent actionEvent) throws IOException {
        setUi("SignupForm");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }

    private User login(String email) throws ClassNotFoundException, SQLException {
         //Create a connection
        Connection connection = DbConnection.getInstance().getConnection();
        // using SQL Statements
//        String sql = "SELECT * FROM user WHERE email='"+email+"'";
//        //create statement
//        Statement statement = connection.createStatement();

        //Using SQL prepared Statement
        String sql = "SELECT * FROM user WHERE email=?";
        System.out.println(sql);
        System.out.println(connection);

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, email);
        ResultSet resultSet = statement.executeQuery(); // SELECT
        if (resultSet.next()) {
            User user = new User(
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString(4));
            System.out.println(user);
            return user;
        }
        return null;
    }
}
