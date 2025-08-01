package com.project.smartacademia.controller;

import com.project.smartacademia.db.DbConnection;
import com.project.smartacademia.model.Student;
import com.project.smartacademia.view.tm.StudentTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class StudentFormController {
    public AnchorPane context;
    public TextField txtId;
    public TextField txtName;
    public DatePicker txtDob;
    public TextField txtAddress;
    public TableView<StudentTm> tblStudents;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colDob;
    public TableColumn colAddress;
    public TableColumn colOption;
    public Button btn;
    public TextField txtSearch;

    String searchText = "";

    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        setStudentId();
        setTableData(searchText);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText = newValue;
            setTableData(searchText);
        });

        tblStudents.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null != newValue) {
                        setData(newValue);
                    }
                });
    }

    private void setData(StudentTm tm) {
        txtId.setText(tm.getId());
        txtName.setText(tm.getFullName());
        txtAddress.setText(tm.getAddress());
        txtDob.setValue(LocalDate.parse(tm.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        btn.setText("Update Student");
    }

    private void setTableData(String searchText) {
        ObservableList<StudentTm> obList = FXCollections.observableArrayList();
        try{
            for (Student st : searchStudents(searchText)
            ) {
                    Button btn = new Button("Delete");
                    StudentTm tm = new StudentTm(
                            st.getStudentId(),
                            st.getFullName(),
                            new SimpleDateFormat("yyyy-MM-dd").format(st.getDateOfBirth()),
                            st.getAddress(),
                            btn
                    );

                    btn.setOnAction(e -> {
                        Alert alert = new Alert(
                                Alert.AlertType.CONFIRMATION,
                                "Are you sure?",
                                ButtonType.YES, ButtonType.NO
                        );
                        Optional<ButtonType> buttonType = alert.showAndWait();
                        if (buttonType.get().equals(ButtonType.YES)) {

                            try {
                                if(deleteStudent(st.getStudentId())){
                                    new Alert(Alert.AlertType.INFORMATION, "Deleted!").show();
                                    setTableData(searchText);
                                    setStudentId();
                                }else{
                                    new Alert(Alert.AlertType.WARNING, "Try again!").show();
                                }
                            } catch (ClassNotFoundException | SQLException ex) {
                                new Alert(Alert.AlertType.ERROR, e.toString()).show();
                            }
                        }
                    });

                    obList.add(tm);
            }
            tblStudents.setItems(obList);
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    private void setStudentId() {

        try {
            String lastId = getLastId();
            if (lastId != null) {
                String splitData[] = lastId.split("-");
                String lastIdIntegerNumberAsAString = splitData[1];
                int lastIntegerIdAsInt = Integer.parseInt(lastIdIntegerNumberAsAString);
                lastIntegerIdAsInt++;
                String generatedStudentId = "S-" + lastIntegerIdAsInt;
                txtId.setText(generatedStudentId);
            } else {
                txtId.setText("S-1");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveOnAction(ActionEvent actionEvent) {
        Student student = new Student(
                txtId.getText(),
                txtName.getText(),
                Date.from(txtDob.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                txtAddress.getText()
        );

        if (btn.getText().equalsIgnoreCase("Save Student")) {

            try {
                if (saveStudent(student)) {
                    //Database.studentTable.add(student);
                    setStudentId();
                    clear();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION, "Student saved!").show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try again!").show();
                }
            } catch (ClassNotFoundException | SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.toString()).show();
            }

        } else {
            try {
                if (updateStudent(student)) {
                    clear();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION, "Student Updated!").show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try again!").show();
                }
            } catch (ClassNotFoundException | SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.toString()).show();
            }
        }
    }

    private void clear() {
        txtDob.setValue(null);
        //txtName.setText("");
        txtName.clear();
        txtAddress.clear();
    }

    public void newStudentOnAction(ActionEvent actionEvent) {
        clear();
        setStudentId();
        btn.setText("Save Student");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/" + location + ".fxml"))));
        stage.centerOnScreen();
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashboardForm");
    }

    private boolean saveStudent(Student student) throws ClassNotFoundException, SQLException {

        //Create a connection
        Connection connection = DbConnection.getInstance().getConnection();
        System.out.println(connection);
        String sql = "INSERT INTO student VALUES (?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, student.getStudentId());
        preparedStatement.setString(2, student.getFullName());
        preparedStatement.setObject(3, student.getDateOfBirth());
        preparedStatement.setString(4, student.getAddress());

        return preparedStatement.executeUpdate() > 0;
    }

    private String getLastId() throws ClassNotFoundException, SQLException {

        //Create a connection
        Connection connection =  DbConnection.getInstance().getConnection();
        System.out.println(connection);
        String sql = "SELECT student_id FROM student ORDER BY CAST(SUBSTRING(student_id,3) AS UNSIGNED) DESC LIMIT 1";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString(1);
        }
        return null;
    }

    private List<Student> searchStudents(String text) throws ClassNotFoundException, SQLException {
        text = "%" + text + "%";

        Connection connection =  DbConnection.getInstance().getConnection();
        System.out.println(connection);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM student WHERE full_name LIKE ? OR address LIKE ?");
        preparedStatement.setString(1, text);
        preparedStatement.setString(2, text);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Student> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(
                    new Student(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getDate(3),
                            resultSet.getString(4)
                    )
            );

        }
        return list;
    }

    private boolean deleteStudent(String id) throws ClassNotFoundException, SQLException {

        Connection connection =  DbConnection.getInstance().getConnection();
        System.out.println(connection);
        String sql = "DELETE  FROM student WHERE student_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, id);
        return preparedStatement.executeUpdate()>0;

    }

    private boolean updateStudent(Student student) throws ClassNotFoundException, SQLException {

        //Create a connection
        Connection connection = DbConnection.getInstance().getConnection();
        System.out.println(connection);
        String sql = "UPDATE student SET full_name =?, dob=?, address=? WHERE student_id =?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);


        preparedStatement.setString(1, student.getFullName());
        preparedStatement.setObject(2, student.getDateOfBirth());
        preparedStatement.setString(3, student.getAddress());
        preparedStatement.setString(4, student.getStudentId());

        return preparedStatement.executeUpdate() > 0;
    }
}
