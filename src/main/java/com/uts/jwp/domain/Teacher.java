package com.uts.jwp.domain;

public class Teacher {
    private String nip;
    private String fullName;
    private String email;
    private String phoneNumber;

    public Teacher(){
    }

    // NIP
    public String getNip(){
        return nip;
    }
    public void setNip(String nip){
        this.nip = nip;
    }

    // Full Name
    public String getFullName(){
        return fullName;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    // Email
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    // Phone Number
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
}
