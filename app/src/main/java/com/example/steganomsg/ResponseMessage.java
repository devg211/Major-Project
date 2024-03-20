package com.example.steganomsg;



public class ResponseMessage {
    private String sender;
    private String receiver;
    private String msg;
    private String key;
    private String first_name;
    private String last_name;
    private String mobile;
    private String email;
    private String gender;
    private String profile_pic;
    private String msg_id;
    private String number;
    private String isEncypted;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public ResponseMessage(String sender, String receiver, String msg, String key, String msg_id,String isEncypted,String image) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.key = key;
        this.msg_id = msg_id;
        this.image = image;
        this.isEncypted = isEncypted;
    }

    public ResponseMessage(String msg, String first_name, String profile_pic, String number,String isEncypted) {
        this.msg = msg;
        this.first_name = first_name;
        this.profile_pic = profile_pic;
        this.number = number;
        this.isEncypted = isEncypted;

    }

    public String getIsEncypted() {
        return isEncypted;
    }

    public void setIsEncypted(String isEncypted) {
        this.isEncypted = isEncypted;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
