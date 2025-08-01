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

public class SignupFormController {
    public AnchorPane context;
    public TextField txtFirstName;
    public PasswordField txtPassword;
    public TextField txtEmail;
    public TextField txtLastName;

    public void signUpOnAction(ActionEvent actionEvent) throws IOException {
        String email = txtEmail.getText().toLowerCase();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String password = new PasswordManager().encrypt(txtPassword.getText().trim());

        User createUser = new User(firstName,lastName,email,password);
        try{
            boolean isSaved = signup(createUser);
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION, "Welcome").show();
                setUi("LoginForm");
            }else{
                new Alert(Alert.AlertType.WARNING, "Try again!").show();
            }
        } catch (SQLException | ClassNotFoundException e1) {
            new Alert(Alert.AlertType.ERROR, e1.toString()).show();
        }

    }

    public void alreadyHaveAnAccountOnAction(ActionEvent actionEvent) throws IOException {
        setUi("LoginForm");
    }
    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }

    //===========================

    private boolean signup(User user ) throws ClassNotFoundException, SQLException {

        //Create a connection
        Connection connection = DbConnection.getInstance().getConnection();
        System.out.println(connection);
        // Using a sql statment
        //write a sql
//        String sql = "INSERT INTO User VALUES ('" + user.getEmail()  + "','" + user.getFirstName() + "','" + user.getLastName() + "','" + user.getPassword() + "')";
//
//        //create statement
//        Statement statement = connection.createStatement();
//        //set sql into statement
//        return statement.executeUpdate(sql)>0; // INSERT, UPDATE, DELETE

        // using a sql prepared statement
        String sql = "INSERT INTO User VALUES (?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,user.getEmail());
        statement.setString(2,user.getFirstName());
        statement.setString(3,user.getLastName());
        statement.setString(4,user.getPassword());

        // Execute statement return boolean
        return statement.executeUpdate()>0;
    }
}


