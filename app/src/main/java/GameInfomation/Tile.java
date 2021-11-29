package GameInfomation;

import java.util.HashMap;

public class Tile {
    private char letter;
    private int score;
    private boolean isAlreadyOnBoard;

    public Tile(){
        this.isAlreadyOnBoard=false;
        this.score = 1;
        this.letter = ' ';
    }

    public int TotalScore(){
        int totalscore;
        totalscore = score * getValue(letter);
        return totalscore;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        if(letter == '*')
            initialTile();
        else
            this.letter = letter;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isAlreadyOnBoard() {
        return isAlreadyOnBoard;
    }

    public void setAlreadyOnBoard(boolean alreadyOnBoard) {
        isAlreadyOnBoard = alreadyOnBoard;
    }

    public void initialTile(){
        this.isAlreadyOnBoard=false;
        this.score = 1;
        this.letter = ' ';
    }

    private int getValue(char tileName){
        String s = Character.toString(tileName).toUpperCase();
        HashMap<String, Integer> tileValue = new HashMap<String,Integer>();
        tileValue.put("A",1);
        tileValue.put("B",3);
        tileValue.put("C",3);
        tileValue.put("D",2);
        tileValue.put("E",1);
        tileValue.put("F",4);
        tileValue.put("G",2);
        tileValue.put("H",4);
        tileValue.put("I",1);
        tileValue.put("J",8);
        tileValue.put("K",5);
        tileValue.put("L",1);
        tileValue.put("M",3);
        tileValue.put("N",1);
        tileValue.put("O",1);
        tileValue.put("P",3);
        tileValue.put("Q",10);
        tileValue.put("R",1);
        tileValue.put("S",1);
        tileValue.put("T",1);
        tileValue.put("U",1);
        tileValue.put("V",4);
        tileValue.put("W",4);
        tileValue.put("X",8);
        tileValue.put("Y",4);
        tileValue.put("Z",10);
        tileValue.put(" ",0);
        return tileValue.get(s);
    }
}
