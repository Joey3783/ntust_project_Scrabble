package GameInfomation;

public class Player {
    private String Name;
    private int Score;

    public Player() {
        Score = 0;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public int getScore() {
        return Score;
    }

    public void addScore(int add){
        Score += add;
    }

    public void setScore(int score){
        this.Score = score;
    }
}
