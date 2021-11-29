package com.example.scrabble;

import org.json.JSONArray;

public class WordData {

    private boolean Player;          // 看是誰拼出這個字的
    private String Name;            // 字
    private String Part_of_Speech;  // 詞性
    private JSONArray Definition;      // 意思

    public boolean getPlayer() {
        return Player;
    }

    public void setPlayer(boolean player) {
        Player = player;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPart_of_Speech() {
        return Part_of_Speech;
    }

    public void setPart_of_Speech(String part_of_Speech) {
        Part_of_Speech = part_of_Speech;
    }

    public JSONArray getDefinition() {
        return Definition;
    }

    public void setDefinition(JSONArray definition) {
        Definition = definition;
    }

    public WordData(){

    }


}
