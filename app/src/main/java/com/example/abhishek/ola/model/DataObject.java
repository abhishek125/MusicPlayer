package com.example.abhishek.ola.model;

/**
 * Created by abhishek on 11/12/2017.
 */

import java.util.Date;

public class DataObject {
    private String songName;
    private String artist;
    private String url;
    private  String cover;
public DataObject(){

}

    public DataObject(String songName, String artist, String url,String cover) {
        this.songName = songName;
        this.artist = artist;
        this.url = url;
        this.cover=cover;

    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
