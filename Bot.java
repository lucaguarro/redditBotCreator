package redditBotCreator;

import java.util.ArrayList;

public class Bot {
    String name;
    private int frequency;
    private ArrayList<String> subreddits;
    private ArrayList<String> words;
    public Bot(String name){
        this.name = name;
    }
    public int getFrequency(){ return frequency; }
    public void setFrequency(int frequency){ this.frequency = frequency; }
    public ArrayList<String> getSubreddits(){ return subreddits; }
    public void setSubreddits(ArrayList<String> subreddits){ this.subreddits = subreddits; }
    public ArrayList<String> getWords(){ return words; }
    public void setWords(ArrayList<String> words){ this.words = words; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
}
