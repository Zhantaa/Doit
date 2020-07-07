package com.example.upc.Doit.Bean;


import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class Todos extends BmobObject{
    private String title;
    private String desc;
    private String date;
    private String time;
    private long remindTime,remindTimeNoDay;
    private int id,isAlerted,isRepeat,imgId;
    private BmobDate bmobDate;
    private User user;
    private String dbObjectId;

    public Todos(){}


    public void setId(int id){
        this.id = id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setTime(String time){
        this.time = time;
    }

    public void setisAlerted(int hasAlerted){
        this.isAlerted = hasAlerted;
    }

    public void setRemindTime(long remindTime){
        this.remindTime = remindTime;
    }

    public void setRemindTimeNoDay(long remindTimeNoDay){
        this.remindTimeNoDay = remindTimeNoDay;
    }

    public void setIsRepeat(int isRepeat){
        this.isRepeat = isRepeat;
    }

    public void setImgId(int imgId){
        this.imgId = imgId;
    }

    public void setBmobDate(BmobDate bmobDate){
        this.bmobDate = bmobDate;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void setDbObjectId(String dbObjectId){
        this.dbObjectId = dbObjectId;
    }



    public int getId(){
        return id;
    }

    public String getDesc() {
        return desc;
    }


    public String getTitle() {
        return title;
    }

    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public int getisAlerted(){
        return isAlerted;
    }

    public long getRemindTime(){
        return remindTime;
    }

    public long getRemindTimeNoDay(){
        return remindTimeNoDay;
    }

    public int getIsRepeat(){
        return isRepeat;
    }

    public int getImgId(){
        return imgId;
    }

    public BmobDate getBmobDate() {
        return bmobDate;
    }

    public User getUser() {
        return user;
    }

    public String getDbObjectId(){
        return dbObjectId;
    }
}