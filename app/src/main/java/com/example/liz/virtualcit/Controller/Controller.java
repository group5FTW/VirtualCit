package com.example.liz.virtualcit.Controller;

import com.example.liz.virtualcit.Model.MenuObject;

import java.util.ArrayList;

public class Controller {
    private static Controller instance;
    private ArrayList<MenuObject> menuArray = new ArrayList();

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }

        return instance;
    }

    public ArrayList loadOptions() {
        MenuObject mo = new MenuObject("MyCit", "http://www.mycit.ie");
        menuArray.add(mo);
        mo = new MenuObject("BlackBoard", "http://citbb.blackboard.com");
        menuArray.add(mo);
        mo = new MenuObject("Access Student Drive", "http://webvpn.cit.ie");
        menuArray.add(mo);
        mo = new MenuObject("CIT TimeTables", "http://timetables.cit.ie");
        menuArray.add(mo);
        mo = new MenuObject("Student Email", "https://mail.google.com/mail/u/1/#inbox");
        menuArray.add(mo);

        return menuArray;

    }


    public ArrayList getMenu() {
        menuArray = loadOptions();
        return menuArray;
    }


}
