package com.example.liz.virtualcit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Login extends ActionBarActivity {
    private String userType;
    private String department;
    private String course;
    private String semester;
    Spinner semesterSpinner;
    Spinner courseSpinner;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userAlert(this);//calls alert when login is created

    }

    public void launchHomePage()//creates home page intent
    {
        Intent i = new Intent(this, HomePage.class);
        i.putExtra("user", userType);
        i.putExtra("department", department);
        i.putExtra("course", course);
        i.putExtra("semester", semester);

        if (userType.compareToIgnoreCase("Guest") == 0) {
            startActivity(i);
        } else {
            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void userAlert(Login login)//alert method
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Alert");
        alert.setMessage("Are you a Student or a Guest?");

        alert.setPositiveButton("Guest", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                userType = "Guest";
                launchHomePage();
            }
        });

        alert.setNegativeButton("Student", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                userType = "Student";
                setSpinnerAdapters();
                dialogInterface.dismiss();//student accesses the login choice for entering data

            }
        });

        alert.show();
    }


    public void setSpinnerAdapters() {
        String[] departmentChoice = {"Computing"};
        String[] courseChoice = {"CO.DCOM1 - A", "CO.DCOM1 - B", "CO.DCOM2-A", "CO.DCOM2-B", "CO.DCOM3", "CO.DCOM4", "CO.DCOM5", "CO.DCOM6"};
        String[] semesterChoice = {"Semester 1", "Semester 2"};
        Spinner departmentSpinner = (Spinner) findViewById(R.id.depSpinner);
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        semesterSpinner = (Spinner) findViewById(R.id.semesterSpinner);
        Button submit = (Button) findViewById(R.id.button);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, departmentChoice);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courseChoice);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, semesterChoice);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        departmentSpinner.setAdapter(adapter1);
        courseSpinner.setAdapter(adapter2);

        semesterSpinner.setAdapter(adapter3);

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                course = courseSpinner.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                semester = semesterSpinner.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        department = departmentSpinner.getSelectedItem().toString();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomePage();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
