package gr.extract.hivelive.hiveUtilities;


import android.util.Log;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

@JsonObject
public class User {

    @JsonField(name = "token")
    String token;
    @JsonField(name = "username")
    String username;
    @JsonField(name = "password")
    String password;
    @JsonField(name = "email")
    String email;
    @JsonField(name = "groupid")
    int groupid;
    @JsonField(name = "userid")
    String userid;
    @JsonField(name = "fullname")
    String fullname;
    @JsonField(name = "userphoto")
    String userphoto;
    @JsonField(name = "sex")
    String sex;
    @JsonField(name = "country")
    String country;
    @JsonField(name = "city")
    String city;
    @JsonField(name = "education")
    String education;
    @JsonField(name = "occupation")
    String occupation;
    @JsonField(name = "birthdate")
    String birthdate;
    @JsonField(name = "address")
    String address;
    @JsonField(name = "zipcode")
    String zipcode;
    @JsonField(name = "phoneNo")
    String phoneNo;
    @JsonField(name = "mphoneNo")
    String mphoneNo;
    @JsonField(name = "active")
    private int active;
    @JsonField(name = "registerdate")
    private Date registerdate;

    public User(){
        this.userid="";
        this.username="";
        this.password="";
        this.email="";
        this.fullname="";
        this.userphoto="";
        this.sex="";
        this.birthdate="";
        this.address="";
        this.zipcode="";
        this.phoneNo="";
        this.mphoneNo="";
        this.token="";
        this.groupid=0;
        this.city="";
        this.country="";
        this.education="";
        this.occupation="";
        this.registerdate=new Date();

    }

    public Date getRegisterdate() {
        return registerdate;
    }

    public int getActive() {
        return active;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getEducation() {
        return education;
    }

    public int getGroupid() {
        return groupid;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getEmail() {
        return email;
    }

    public String getUserid() {
        return userid;
    }

    public String getFullname() {
        return fullname;
    }

    public String getMphoneNo() {
        return mphoneNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getSex() {
        return sex;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getUserphoto() {
        return userphoto;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setUserphoto(String userphoto) {
        this.userphoto = ""+userphoto;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setMphoneNo(String mphoneNo) {
        this.mphoneNo = mphoneNo;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setRegisterdate(Date registerdate) {
        this.registerdate = registerdate;
    }


    public void setActive(int active) {
        this.active = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void printUser(){
        Log.d("User", "username --> "+ this.username);
        Log.d("User", "full name --> "+ this.fullname);
        Log.d("User", "email --> "+ this.email);
        Log.d("User", "phone --> "+ this.phoneNo);
        Log.d("User", "mobile --> "+ this.mphoneNo);
        Log.d("User", "city --> "+ this.city);
        Log.d("User", "education --> "+ this.education);
        Log.d("User", "occupation --> "+ this.occupation);
        Log.d("User", "birthday --> "+ this.birthdate);
        Log.d("User", "sex --> "+ this.sex);
        Log.d("User", "address --> "+ this.address);
        Log.d("User", "zip code --> "+ this.zipcode);
    }
}
