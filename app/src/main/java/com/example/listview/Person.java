package com.example.listview;

public class Person {

    private String name;
    private String sex;
    private String birthDay;


    public Person(String name, String sex, String birthDay) {
        this.name = name;
        this.sex = sex;
        this.birthDay = birthDay;
    }


    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getBirthDay() {
        return birthDay;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    @Override
    public String toString(){
        return String.format("[ %s, %s, %s, %s]",getBirthDay(),getName(),getSex(),getClass());
    }
}
