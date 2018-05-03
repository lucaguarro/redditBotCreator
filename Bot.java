package redditBotCreator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.util.ArrayList;

public class Bot {
    private SimpleStringProperty name;
    private Frequency frequency;

    private ObservableList<String> subreddits = FXCollections.observableArrayList();
    private ObservableList<String> words = FXCollections.observableArrayList();
    //private ArrayList<String> subreddits = new ArrayList<String>();
    //private ArrayList<String> words = new ArrayList<String>();
    private boolean isOn;
    public Bot(String name){
        this.name = new SimpleStringProperty(name);
        this.setIsOn(false);
    }
    public Frequency getFrequency(){
        return frequency;
    }
    public void setFrequency(Frequency frequency){
        this.frequency = frequency;
    }
    public ObservableList<String> getSubreddits(){
        return subreddits;
    }
    public void setSubreddits(ObservableList<String> subreddits){
        this.subreddits = subreddits;
    }
    public ObservableList<String> getWords(){
        return words;
    }
    public void setWords(ObservableList<String> words){
        this.words = words;
    }
    public String getName(){
        return name.get();
    }
    public void setName(String name){
        this.name.setValue(name);
    }
    public Boolean isOn(){
        return this.isOn;
    }
    public void setIsOn(boolean newValue){
        this.isOn = newValue;
    }
}