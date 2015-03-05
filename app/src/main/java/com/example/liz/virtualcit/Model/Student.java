package com.example.liz.virtualcit.Model;

/**
 * Created by Liz on 26/02/2015.
 */
public class Student {
    private String department;
    private String studentClass;


    public Student(String course, String dep) {
        setDepartment(dep);
        setStudentClass(course);
    }


    private String getDepartment() {
        return department;
    }

    private void setDepartment(String dep) {
        this.department = dep;
    }

    private String getStudentClass() {
        return studentClass;
    }

    private void setStudentClass(String course) {
        this.studentClass = course;
    }


}