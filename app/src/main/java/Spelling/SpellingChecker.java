package Spelling;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import Dictionaries.Dictionaries;
import GameInfomation.GameInfo;
import GameInfomation.Tile;

public class SpellingChecker {

    private HashSet<String> word_possi;
    private Activity activity;
    private Metaphone3 metaphone3;
    private Dictionaries dictionary;
    private String Level;
    final boolean Across = true;
    final boolean Down = false;


    public SpellingChecker(){
        metaphone3 = new Metaphone3();
        word_possi = new HashSet<>();
    }

    public SpellingChecker(Activity activity, Dictionaries dictionary, String Level){
        this();


        this.activity = activity;
        this.dictionary = dictionary;
        this.Level = Level;

    }

    public HashSet<String> SpellingCheck(String word_input, int strow, int stcol, boolean orient, GameInfo gameInfo) {
        // 要是trie有這個字，就顯示 please check the cross side
        Log.d("aoskdwoad",String.valueOf(strow)+" "+stcol+String.valueOf(orient));
        if (dictionary.getTrie().contains(word_input)) {

            HashSet<String> wrong_w = new HashSet<>();
            wrong_w.add("isCross");

            wrong_w.addAll(getSpecCrossSetCheck(gameInfo.getBoard().getBoard(),strow,stcol,orient,word_input));

            if(wrong_w.size()<2){
                wrong_w.add("isCorrect");
            }

            return wrong_w;

        } else {

            Log.d("time_test_before","start_before_version");
//            WordCorrection_before(word_input);
//            Log.d("time_test_before","size = "+ word_possi.size());

            word_possi.clear();

            Log.d("time_test_after","start_after_version");
            WordCorrection(word_input);
            Log.d("time_test_after","size = "+ word_possi.size());

            return  reorderbyLevel();
        }

    }

    private HashSet<String> reorderbyLevel(){
        // hashset add 時不會考慮順序，所以如果跟order有關的，就要改用linkedHashset!
        HashSet<String> newlist = new LinkedHashSet<>();

        Log.d("waeaeewe__",word_possi.toString());
        for(String s:word_possi){
            if(dictionary.SingleWordLevelCheck(s,Level)){
                newlist.add(s);
                Log.d("waeaeewe_",s.toString());
            }
        }
        Log.d("waeaeewe___",newlist.toString());
        newlist.addAll(word_possi);

        Log.d("waeaeewe__",word_possi.toString());
        return newlist;
    }

    public ArrayList<String> getSpecCrossSetCheck(ArrayList<ArrayList<Tile>> BoardArray, int specRow, int specCol, boolean orien, String word){
        ArrayList<String> wrong_w = new ArrayList<>();
        if(orien == Across){
            for(int i=0;i<word.length();i++){

                int currentRow_buffer = specRow;
                int currentCol_buffer = specCol+i;
                int offset = 0;
                String wordbuffer = "";
                // Down
                // 先找到字的源頭，向上找
                while (currentRow_buffer > -1 &&
                        BoardArray.get(currentRow_buffer - offset).get(currentCol_buffer).getLetter() != ' ') {
                    wordbuffer = BoardArray.get(currentRow_buffer - offset).get(currentCol_buffer).getLetter() + wordbuffer;
                    Log.d("aoskdwoad++",wordbuffer);

                    if (currentRow_buffer - offset - 1 < 0)
                        break;
                    offset++;
                }
                if (currentRow_buffer - offset == 0 && BoardArray.get(currentRow_buffer - offset).get(currentCol_buffer).getLetter() != ' ')
                    currentRow_buffer = currentRow_buffer - offset;

                else
                    currentRow_buffer = currentRow_buffer - offset + 1;
                Log.d("Row", String.valueOf(currentRow_buffer) + "," + offset);


                // 再拼湊出整個字，向下拼
                currentRow_buffer = specRow;
                offset = 1;

                while (currentRow_buffer+offset<BoardArray.size() && BoardArray.get(currentRow_buffer + offset).get(currentCol_buffer).getLetter()!= ' ') {
                    wordbuffer = wordbuffer + BoardArray.get(currentRow_buffer + offset).get(currentCol_buffer).getLetter();
                    Log.d("aoskdwoad--",wordbuffer);
                    if (currentRow_buffer + offset + 1 >= BoardArray.size())
                        break;

                    offset++;
                }
                Log.d("aoskdwoad",wordbuffer);
                if(!dictionary.getTrie().contains(wordbuffer)&&wordbuffer.length()>1){
                    wrong_w.add(wordbuffer);
                }
            }
        }else {
            for(int i=0;i<word.length();i++){
                char c = word.charAt(i);
                String wordbuffer="";
                int currentCol_buffer = specCol;
                int currentRow_buffer = specRow+i;
                int offset=0;
                while (currentCol_buffer > -1 &&
                        BoardArray.get(currentRow_buffer).get(currentCol_buffer - offset).getLetter() != ' ') {
                    wordbuffer = BoardArray.get(currentRow_buffer).get(currentCol_buffer - offset).getLetter() + wordbuffer;
                    if (currentCol_buffer - offset - 1 < 0)
                        break;
                    offset++;
                }
                if (currentCol_buffer - offset == 0 && BoardArray.get(currentRow_buffer).get(0).getLetter() != ' ')
                    currentCol_buffer = currentCol_buffer - offset;

                else
                    currentCol_buffer = currentCol_buffer - offset + 1;
                Log.d("across", String.valueOf(currentCol_buffer) + "," + offset);

                // 再拼湊出整個字，向右拼
                currentCol_buffer = specCol;
                offset = 1;
                while (currentCol_buffer+offset<BoardArray.size() && BoardArray.get(currentRow_buffer).get(currentCol_buffer + offset).getLetter() != ' ') {
                    wordbuffer = wordbuffer + BoardArray.get(currentRow_buffer).get(currentCol_buffer + offset).getLetter();
                    if (currentCol_buffer + offset + 1 >= BoardArray.size())
                        break;
                    offset++;
                }
                Log.d("aoskdwoad",wordbuffer);
                if(!dictionary.getTrie().contains(wordbuffer)&&wordbuffer.length()>1){
                    wrong_w.add(wordbuffer);
                }



            }
        }
        return wrong_w;
    }

    private void WordCorrection(String word_input) {

        metaphone3.SetWord(word_input);
        metaphone3.Encode();
        String meta_input = metaphone3.GetMetaph();

        for(String s:dictionary.getListD()){
            int edit_distance = levenshtein_edit_distance(word_input.toCharArray(),s.toCharArray());
            if(edit_distance>getThreshold(word_input)){
                continue;
            }
            metaphone3.SetWord(s);
            metaphone3.Encode();
            String meta_s = metaphone3.GetMetaph();
            // 如果發音一樣，就近一步看edit distance
            if(meta_input.equals(meta_s) &&
                    edit_distance<=getThreshold(word_input)){
                word_possi.add(s);
            }
            // 如果發音不同，就純看edit distance
            else if(levenshtein_edit_distance(meta_input.toCharArray(),meta_s.toCharArray())==1){
                if(edit_distance==1){
                    word_possi.add(s);
                }
            }
        }
    }

    private void WordCorrection_before(String word_input) {

        metaphone3.SetWord(word_input);
        metaphone3.Encode();
        String meta_input = metaphone3.GetMetaph();

        for(String s:dictionary.getListD()){

            metaphone3.SetWord(s);
            metaphone3.Encode();
            String meta_s = metaphone3.GetMetaph();
            // 如果發音一樣，就近一步看edit distance
            if(meta_input.equals(meta_s)){
                word_possi.add(s);
            }

        }
    }

    private int getThreshold(String input){
        if(input.length()<=3){
            return 1;
        }
        else if(input.length()==4){
            return 2;
        }
        else
            return 3;
    }

    private int getMin(int x, int y, int z){
        if(x<=y && x<=z)
            return x;
        else return Math.min(y, z);
    }

    // 只使用當行陣列與前一行陣列
    public int levenshtein_edit_distance(char[] cor_word, char[] wro_word){

        int[] v_last = new int[wro_word.length+1];
        int[] v_current = new int[wro_word.length+1];

        // initialize the vector
        for(int i=0;i<v_last.length;i++){
            v_last[i]=i;
        }

        for(int row=1;row<cor_word.length+1;row++){

            v_current[0] = row;

            for(int col=1;col<wro_word.length+1;col++){

                if(cor_word[row-1] == wro_word[col-1])
                    v_current[col] = v_last[col - 1];

                else
                    v_current[col] = getMin(v_current[col-1],v_last[col-1],v_last[col])+1;

            }
            System.arraycopy(v_current, 0, v_last, 0, v_current.length);
        }
        return v_current[wro_word.length];
    }


}
