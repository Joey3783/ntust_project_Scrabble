package GameInfomation;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class GameInfo {

    private Player player;
    private Player comp;
    private Board board;
    private Rack rack_player;
    private Rack rack_comp;
    private boolean round;
    private ArrayList<String> gameWordlist;
    private boolean firstshot;
    private final int remainTile; // the tiles remaining in bag and racks

    private HashSet<String> comp_put_buffer; // 之後用來判斷玩家是否幫虛擬玩家放好牌的依據
    private char comp_blank_buffer; // 把虛擬玩家的空白牌要變成的樣子先存起來
    private ArrayList<Character> player_rack_buffer;//每次玩家拼好之後，就從場上這邊當作hand rack去找是否可以

    final boolean round_player = true;
    final boolean round_comp = false;

    final boolean Across = true;
    final boolean Down = false;

    public GameInfo(){
        player = new Player();
        comp = new Player();
        comp.setName("CHRISTY");
        round = round_player;
        gameWordlist = new ArrayList<>();
        board = new Board();
        rack_player = new Rack();
        rack_comp = new Rack();
        firstshot = true;
        remainTile = 100;
        comp_put_buffer = new HashSet<>();
        player_rack_buffer = new ArrayList<>();
    }

    public Player getPlayer() {
        return player;
    }

    public Player getComp() {
        return comp;
    }

    public boolean getRound() {
        return round;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    public ArrayList<String> getGameWordlist() {
        return gameWordlist;
    }

    public Board getBoard() {
        return board;
    }

    public Rack getRack_player() {
        return rack_player;
    }

    public Rack getRack_comp() {
        return rack_comp;
    }

    public void setGameWordlist(ArrayList<String> gameWordlist) {
        this.gameWordlist = gameWordlist;
    }

    public void addGameWordlist(String word){
        this.gameWordlist.add(word);
    }

    public boolean isFirstshot() {
        return firstshot;
    }

    public void setFirstshot(boolean firstshot) {
        this.firstshot = firstshot;
    }

    public HashSet<String> getComp_put_buffer() {
        return comp_put_buffer;
    }


    public char getComp_blank_buffer() {
        return comp_blank_buffer;
    }

    public void setComp_blank_buffer(char comp_blank_buffer) {
        this.comp_blank_buffer = comp_blank_buffer;
    }

    public ArrayList<Character> getPlayer_rack_buffer() {
        return player_rack_buffer;
    }


    public int getRemainTile() {
        // 最後要取得的數量是只看bag的數目
        int TileHasPutOnBoard=0;
        for(int row=0;row<board.getBoard().size();row++){
            for(int col=0;col<board.getBoard().size();col++){
                if(board.getBoard().get(row).get(col).getLetter()!=' '){
                    TileHasPutOnBoard++;
                }

            }
        }
        for(int index=0;index<rack_player.getRackChar().size();index++){
            if(rack_player.getRackChar().get(index)!=' '){
                TileHasPutOnBoard++;
            }
            if(rack_comp.getRackChar().get(index)!=' '){
                TileHasPutOnBoard++;
            }
        }
        return remainTile-TileHasPutOnBoard;
    }

    public String getGameInfoJson(){

        ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
        HashMap<String,String> GameInfo = new HashMap<>();
        GameInfo.put("PlayerScore",String.valueOf(player.getScore()));
        GameInfo.put("CompScore",String.valueOf(comp.getScore()));
        if(round==round_player){
            GameInfo.put("Round","Player");
        }else {
            GameInfo.put("Round","Comp");
        }
        GameInfo.put("PlayerRack",rack_player.getRackChar_json().toString());
        GameInfo.put("CompRack",rack_comp.getRackChar_json().toString());
        GameInfo.put("Board_letter",board.getBoardChar().toString());
        GameInfo.put("Board_Score",board.getBoardScore().toString());
        GameInfo.put("WordList",gameWordlist.toString());

        String comp_put="";
        for(String s:comp_put_buffer){
            comp_put += s+":";
        }
        GameInfo.put("Comp_put_buffer",comp_put);
        GameInfo.put("Comp_blank_buffer",String.valueOf(comp_blank_buffer));

        String player_rack="";
        for(char c:player_rack_buffer){
            player_rack += c+":";
        }
        GameInfo.put("Player_rack_buffer",player_rack);

        GameInfo.put("IsFirshot",String.valueOf(isFirstshot()));


        arrayList.add(GameInfo);
        String gson = new Gson().toJson(arrayList);

        return gson;
    }

    public void setGame(String gameJson){

        try {
            JSONArray jsonArray = new JSONArray(gameJson);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            String PlayerScore = jsonObject.getString("PlayerScore");
            String CompScore = jsonObject.getString("CompScore");
            String Round = jsonObject.getString("Round");
            String PlayerRack = jsonObject.getString("PlayerRack");
            String CompRack = jsonObject.getString("CompRack");
            String Board_letter = jsonObject.getString("Board_letter");
            String Board_Score = jsonObject.getString("Board_Score");
            String WordList = jsonObject.getString("WordList");
            String Comp_put_buffer = jsonObject.getString("Comp_put_buffer");
            String Comp_blank_buffer = jsonObject.getString("Comp_blank_buffer");
            String Player_rack_buffer = jsonObject.getString("Player_rack_buffer");
            String IsFirtshot = jsonObject.getString("IsFirshot");

            player.setScore(Integer.valueOf(PlayerScore));
            comp.setScore(Integer.valueOf(CompScore));
            if(Round.equals("Player")){
                round = round_player;
            }
            else {
                round = round_comp;
            }
            for(int index=0;index<rack_player.getRack().size();index++){
                char c = PlayerRack.split(",")[index].charAt(1);
                if(c=='*')
                    rack_player.getRack().get(index).setLetter(' ');
                else
                    rack_player.getRack().get(index).setLetter(c);
            }
            for(int index=0;index<rack_comp.getRack().size();index++){
                char c = CompRack.split(",")[index].charAt(1);
                if(c=='*')
                    rack_comp.getRack().get(index).setLetter(' ');
                else
                    rack_comp.getRack().get(index).setLetter(c);
            }
            for(int row=0;row<board.getBoard().size();row++){
                for(int col=0;col<board.getBoard().size();col++) {
                    char c = Board_letter.split(",")[9 * row + col].charAt(1);
                    if (c == '*') {
                        board.getBoard().get(row).get(col).setLetter(' ');
                    } else {
                        board.getBoard().get(row).get(col).setLetter(c);
                        board.getBoard().get(row).get(col).isAlreadyOnBoard();
                    }
                }
            }
            for(int row=0;row<board.getBoard().size();row++){
                for(int col=0;col<board.getBoard().size();col++) {
                    char c = Board_Score.split(",")[9 * row + col].charAt(1);

                        board.getBoard().get(row).get(col).setScore(Integer.valueOf(c)-'0');

                }
            }
            String[] wordlist_buffer = WordList.replace("[","").replace("]","").replace(" ","").split(",");
            gameWordlist.addAll(Arrays.asList(wordlist_buffer));


            String[] comp_put=Comp_put_buffer.split(":");
            Collections.addAll(comp_put_buffer, comp_put);

            comp_blank_buffer = Comp_blank_buffer.charAt(0);

            if(IsFirtshot.equals("true"))
                setFirstshot(true);
            else
                setFirstshot(false);

            if(Player_rack_buffer.equals(""))
                return;
            String[] player_rack_buf = Player_rack_buffer.split(":");
            for (String s : player_rack_buf) {
                player_rack_buffer.add(s.toCharArray()[0]);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateAlreadyOnBoard(){
        for(int row=0;row<board.getBoard().size();row++){
            for(int col=0;col<board.getBoard().size();col++){
                if(board.getBoard().get(row).get(col).getLetter()!=' ')
                    board.getBoard().get(row).get(col).setAlreadyOnBoard(true);
            }
        }
    }

    public int EvaluateWord(int strow, int stcol, String word, boolean orient){
        int totalScore = 0;

        for(int index=0;index<word.length();index++){
            char tile = word.charAt(index);
            if(orient==Across) {
                totalScore += getValue(tile) * board.getBoard().get(strow).get(stcol+index).getScore();
            }

            else if(orient==Down) {
                Log.d("EVALUATERRT",word+String.valueOf(strow)+"  "+stcol+"  "+totalScore+"  "+board.getBoard().get(strow+index).get(stcol).getScore());
                totalScore += getValue(tile) * board.getBoard().get(strow+index).get(stcol).getScore();
                Log.d("EVALUATERRT_af",word+String.valueOf(strow)+"  "+stcol+"  "+totalScore+"  "+board.getBoard().get(strow+index).get(stcol).getScore());

            }

        }

        return totalScore;
    }

    private int getValue(char tileName){
        Log.d("getvalue ",String.valueOf(tileName));
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
        return tileValue.get(s);
    }
}
