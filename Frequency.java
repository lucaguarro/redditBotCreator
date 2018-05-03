package redditBotCreator;

public class Frequency {
    private String english;
    private int milliseconds;
    public Frequency(String english, int milliseconds){
        this.english = english;
        this.milliseconds = milliseconds;
    }
    public String getEnglish(){
        return this.english;
    }
}
