package com.example.liz.virtualcit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class Login extends HomePage {
    private String userType;
    private String department;
    private String course;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userAlert();//calls alert when login is created

    }

    public void launchHomePage()//creates home page intent
    {
        Intent i = new Intent(this, HomePage.class);
        i.putExtra("user", userType);
        i.putExtra("department", department);
        i.putExtra("course", course);
        startActivity(i);
    }

    public void userAlert()//alert method
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Alert");
        alert.setMessage("Are you a Student or a Guest?");

        alert.setPositiveButton("Guest", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                userType = "Guest";
                launchHomePage();//launches straight into homePage if guest
            }
        });

        alert.setNegativeButton("Student", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                userType = "Student";
                dialogInterface.dismiss();//student accesses the login choice for entering data
            }
        });

        alert.show();
    }


    public void departmentChoice()//name can change i know..
    {
        //populate listView from database table
    }

}
