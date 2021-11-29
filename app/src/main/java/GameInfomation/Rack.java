package GameInfomation;

import java.util.ArrayList;

public class Rack {

    private ArrayList<Tile> rack;
    private final int size = 7;

    public Rack() {

        rack = new ArrayList<>(size);

        for(int i=0;i<size;i++){
            rack.add(new Tile());
        }

    }

    public ArrayList<Tile> getRack() {
        return rack;
    }

    public ArrayList<Character> getRackChar(){
        ArrayList<Character> c = new ArrayList<>();
        for(int index=0; index<rack.size(); index++){
            c.add(rack.get(index).getLetter());
        }
        return c;
    }

    public ArrayList<Character> getRackChar_json(){
        ArrayList<Character> c = new ArrayList<>();
        for(int index=0; index<rack.size(); index++){
            if(rack.get(index).getLetter()==' ')
            {
                c.add('*');
            }
            else {
                c.add(rack.get(index).getLetter());
            }
        }
        return c;
    }

}
