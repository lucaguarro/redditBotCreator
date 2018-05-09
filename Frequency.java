package redditBotCreator;

/**
 * This class represents a Frequency object so that the frequency in which the bot must run has the english and the milliseconds
 */
public class Frequency {
    private String english;
    private long milliseconds;
    public Frequency(String english, int milliseconds){
        this.english = english;
        this.milliseconds = milliseconds;
    }
    public String getEnglish(){
        return this.english;
    }
    public long getMilliseconds(){
        return this.milliseconds;
    }
}
