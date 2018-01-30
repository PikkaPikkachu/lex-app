package com.example.prakritibansal.minerva_cards.Bot;



import com.amazonaws.services.lexrts.model.Button;

import java.util.List;

/**
 * Created by prakritibansal on 12/23/17.
 */

public class Message {
    private String message;
    private String from;
    private String timeStamp;
    private String subTitle;
    private String imageUrl;
    private List<Button> buttonList;

    public Message(final String message, final String from, final String timeStamp) {
        this.message = message;
        this.from = from;
        this.timeStamp = timeStamp;

    }
    public Message(final String message, final String from, final String subTitle, final String url, final List<Button> buttonList ){
        this.message= message;
        this.subTitle = subTitle;
        this.from = from;
        this.imageUrl = url;
        this.buttonList = buttonList;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    //Card Features

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String cardName) {
        this.subTitle = subTitle;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public List<Button> getBtnList(){
        return buttonList;
    }


}
