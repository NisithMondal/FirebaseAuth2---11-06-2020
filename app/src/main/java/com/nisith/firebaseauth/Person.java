package com.nisith.firebaseauth;

public class Person {

    private String name;
    private String state;
    private String country;
    private String phoneNumber;

    public Person(){

    }

    public Person(String name, String state, String country, String phoneNumber) {
        this.name = name;
        this.state = state;
        this.country = country;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String  getPhoneNumber() {
        return phoneNumber;
    }
}
