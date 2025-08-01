package com.project.smartacademia.db;

import com.project.smartacademia.model.Program;
import com.project.smartacademia.model.Student;
import com.project.smartacademia.model.Teacher;
import com.project.smartacademia.model.User;
import com.project.smartacademia.util.security.PasswordManager;

import java.util.ArrayList;

public class Database {
    public static ArrayList<User> userTable
            = new ArrayList();
    public static ArrayList<Student> studentTable
            = new ArrayList();
    public static ArrayList<Teacher> teacherTable
            = new ArrayList();
    public static ArrayList<Program> programTable
            = new ArrayList();

    static {
        userTable.add(
                new User("Hasika","sandaruwan",
                        "h@gmail.com",
                        new PasswordManager().encrypt("1234"))
        );
    }

}
