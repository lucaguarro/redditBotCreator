package redditBotCreator;

import javafx.beans.property.SimpleStringProperty;


import java.util.ArrayList;

public class Bot {
    private SimpleStringProperty name;
    private int frequency;
    private ArrayList<String> subreddits;
    private ArrayList<String> words;
    private boolean isOn;
    public Bot(String name){
        this.name = new SimpleStringProperty(name);
        this.turnOff();
    }
    public int getFrequency(){ return frequency; }
    public void setFrequency(int frequency){ this.frequency = frequency; }
    public ArrayList<String> getSubreddits(){ return subreddits; }
    public void setSubreddits(ArrayList<String> subreddits){ this.subreddits = subreddits; }
    public ArrayList<String> getWords(){ return words; }
    public void setWords(ArrayList<String> words){ this.words = words; }
    public String getName(){ return name.get(); }
    public void setName(String name){ this.name.setValue(name); }
    public Boolean isOn(){ return this.isOn; }
    public void turnOff(){ this.isOn = false; }
    public void turnOn(){ this.isOn = true; }
}
