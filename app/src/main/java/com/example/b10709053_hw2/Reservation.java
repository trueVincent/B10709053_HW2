package com.example.b10709053_hw2;

public class Reservation {
    private int id;
    private String name;
    private String number;

    public Reservation(String name, String number){
        this(0, name, number);
    }

    public Reservation(int id, String name, String number){
        this.id=id;
        this.name=name;
        this.number=number;
    }

    public void setID(int id){
        this.id=id;
    }
    public int getID(){
        return id;
    }

    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }

    public void setNumber(String number){
        this.number=number;
    }
    public String getNumber(){
        return number;
    }
}
