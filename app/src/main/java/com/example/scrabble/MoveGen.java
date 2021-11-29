package com.example.scrabble;

import java.util.ArrayList;
import java.util.HashMap;

import MoveGeneration.Trie;

public class MoveGen {
    //find possible words on board with hand card
    // outputs => String

    public String[] findboardwords(ArrayList<String> listD, String input, char[][] BoardArray){
        char[][] BoardArraybuffer=BoardArray;
        Trie listDTrie = new Trie();
        listDTrie.addAll(listD);
        String[] chooseW=new String[5];
        chooseW[0]=" ";
        chooseW[1]="0";// 最後要傳出去的單字的資料 output value strow stcol orient
        chooseW[2]="0";
        chooseW[3]="0";
        chooseW[4]="0";
        ArrayList<String> wordlist = new ArrayList<String>();
        String[] newW = new String[]{" ", "0", "0"};
            //做每列的搜尋
        for(int row=0;row<BoardArraybuffer.length;row++){
            String Lock=""; //放場上每列字串的地方
            for(int col=0;col<BoardArraybuffer.length;col++){

                Lock=Lock+BoardArraybuffer[row][col];// 把那一行的字都加上去作為限制(Lock)

            }
            // lock is empty flag, if empty ,not call bienxi
            if(Lock.replace(" ", "").equals("")){
                continue;
            }

            ArrayList<String> bienWord=new ArrayList<>();
            bienWord=bienxi(listD,input,Lock);
            if(!bienWord.isEmpty()){
               // wordlist = crossVerify(listDTrie,bienWord,BoardArraybuffer,row,false);//最後傳回String[word,score,location]
                newW=Evaluate(bienWord);
            }
            if(!wordlist.isEmpty()){
               // newW=Evaluate(bienWord);
            }



            if(Integer.valueOf(newW[1])>Integer.valueOf(chooseW[1])){
                chooseW[0] = newW[0];
                chooseW[1] = newW[1];
                chooseW[2] = String.valueOf(row);
                chooseW[3] = newW[2];
                chooseW[4] = "0";
            }

        }
        newW = new String[]{" ", "0", "0"};
            //每行的搜尋
        for(int col=0;col<BoardArraybuffer.length;col++){
            String Lock=""; //放場上每行字串的地方
            for(int row=0;row<BoardArraybuffer.length;row++){

                Lock=Lock+BoardArraybuffer[row][col];// 把那一行的字都加上去作為限制(Lock)

            }
            // lock is empty flag, if empty ,not call bienxi
            if(Lock.replace(" ", "").equals("")){
                continue;
            }
            ArrayList<String> bienWord=bienxi(listD,input,Lock);
            if(!bienWord.isEmpty()){
//                wordlist = crossVerify(listDTrie,bienWord,BoardArraybuffer,col,true);//最後傳回String[word,score,location]
                newW=Evaluate(bienWord);
            }
            if(!wordlist.isEmpty()){
//                newW=Evaluate(bienWord);
            }

            if(Integer.valueOf(newW[1])>Integer.valueOf(chooseW[1])){
                chooseW[0] = newW[0];
                chooseW[1] = newW[1];
                chooseW[2] = newW[2];
                chooseW[3] = String.valueOf(col);
                chooseW[4] = "1";
            }
        }



        return chooseW;

    }

    public ArrayList<String> bienxi(ArrayList<String> listD,String inputs,String Lock) {
        ArrayList<String> matches = new ArrayList<String>();
        ArrayList<String> verymatches = new ArrayList<String>();

        // 代表是玩家拼完字要來看有沒有符合的
//        if (Lock.equals("")) {
//            if (listD.contains(inputs)) { // 代表listD裡面有這個字，回傳成功的訊息。
//                String[] successful = {"successful", "0", "0"};
//                return successful;
//            } else { // 代表拼錯，回傳失敗的訊息。
//                String[] fail = {"fail", "0", "0"};
//                return fail;
//            }
//        }

        String input = inputs.toLowerCase() + Lock.toLowerCase().replace(" ", "");
//         lock is empty flag
//        if(Lock.replace(" ","")==""){
//            String [] empty={" ","0","0"};
//            return null;
//        }

        // for each word in dict

        for (String word : listD) {

            // match flag
            Boolean nonMatch = true;
            // for each character of dict word
            for (char chW : word.toLowerCase().toCharArray()) {

                String w = Character.toString(chW);

                // if the count of chW in word is equal to its count in input,
                // then, they are match
                boolean a = ((word.length() - word.toLowerCase().replace(w, "").length()) >
                        input.length() - input.toLowerCase().replace(w, "").length());

                // 字典裡的字沒有在input裡 或 字數不符合
                if (!input.toLowerCase().contains(w) || a) {
                    nonMatch = false;
                    break;
                }
            }
            if (nonMatch && (!word.toLowerCase().equals(Lock.toLowerCase().replace(" ", "")))) {
                matches.add(word);
            }
        }


        //這邊開始matches就有所有可能了
        //現在要找出符合窗格的
        bienxiWithLock(inputs.toLowerCase(), matches, Lock.toLowerCase(), verymatches);
        return verymatches;
        //下面是把它顯示出來而已，不重要

//            String output = "";
//            //如果結果是空(沒有匹配的單字)的就返回空白
//            if(verymatches.isEmpty()){
//                output=" :0:0";
//            }
//            else{
//                output = verymatches.get(0); // verymatches是有delimiter的
//            }

//        for (int i = 0; i < verymatches.size(); i++) {
//            //Append all the values to a string
//            output += verymatches.get(i)+"\n";
//        }


//            final String[] part = output.split(":");
//            return part;


    }
    private void bienxiWithLock(String inputs,ArrayList<String> matches, String Lock, ArrayList<String> verymatches){
        //跑過所有可能字（存在matches裡）
        int lastValue=0;//前一個字的分數，如果之後的比之前的大，則取代，小則忽略。
        int arraylist_count=0;//看現在存到第幾個index了
        for (String word:matches){
            //從第0個位置套入，Lock會用substring方式篩掉比不到的
            for(int n=0;n<(Lock.length()-word.length()+1);n++){
                char chword[]=word.toLowerCase().toCharArray();//手牌➕場牌所有可能
                char chLock[]=Lock.substring(n,n+word.length()).toCharArray();//場上的牌
                char chLockcheck[]=Lock.toCharArray(); // for checking for speeding
                String scheck = inputs+Lock.substring(n,n+word.length()).replace(" ","");// 防止移花接木用

                //確定前後沒有其他字元（不是在最前，那前1就要空白;不是在最後，後1就要空白）
                if((n!=0&&chLockcheck[n-1]!=' ')||
                        (n!=Lock.length()-word.length() &&
                                chLockcheck[n+word.length()]!=' ')){
                    //如果沒符合就直接下一個
                    continue;
                }

                int count=0; // for checking
                int totalValue=0; // for calculate value
                for(int i=0;i<word.length();i++){
                    // Lock既有位置不等於0的情況下，若同位置字母不同，則棄之。
                    // 若所有字母皆未對到Lock，代表移花接木，棄之。
                    String w = Character.toString(chword[i]);
                    boolean a = (word.toLowerCase().length() - word.toLowerCase().replace(w, "").length()) >
                            (scheck.length() - scheck.replace(w, "").length());
                    if((chword[i]!=chLock[i]) && chLock[i]==' '){
                        count=count+1;
                    }
                    if(((chword[i]!=chLock[i]) && chLock[i]!=' ')||count==word.length()||a){
                        break;
                    }
                    //該單字的分數
                    totalValue = totalValue + getValue(chword[i]);
                    //比到最後一個字，也都通過考驗，就把單字列進考量內。
                    if(i==word.length()-1){
                        //只留分數最高的
//                        if (arraylist_count==0){
//                            //":"is delimiter到時候可以拆掉，順便就能知道第一個字要放在Lock的第幾個位置
//                            verymatches.add(word+":"+totalValue+":"+n);
//                            lastValue=totalValue;
//                            arraylist_count=arraylist_count+1;
//                        }
//                        else if(totalValue>lastValue){
//                            verymatches.remove(arraylist_count-1);
//                            //":"is delimiter到時候可以拆掉，順便就能知道第一個字要放在Lock的第幾個位置
//                            verymatches.add(word+":"+totalValue+":"+n);
//
//                            lastValue=totalValue;
//                        }
//                        else if(totalValue==lastValue){
//                            verymatches.add(word+":"+totalValue+":"+n);
//                            arraylist_count=arraylist_count+1;
//                        }

                        verymatches.add(word+":"+totalValue+":"+n); // 把verymatches的所有都回傳
                    }
                }
            }
        }
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
        return tileValue.get(s);
    }
    // 單純模擬放上去後cross有沒有符合
    private ArrayList<String> crossVerify(Trie listDTrie,ArrayList<String> wordlist,char[][]boardarray,int RC,boolean orient){
        //RC是看字在第幾行或列
        char[][] boardlast=boardarray;
        ArrayList<String> crosslist = new ArrayList<String>();//要回傳回去，有符合crossverify結果的人
        String word;//字
        int stloc;//字第一個放的地方
        boolean allOK;
        for (int i=0;i<wordlist.size();i++){
            word = wordlist.get(i).split(":")[0];
            stloc= Integer.valueOf(wordlist.get(i).split(":")[2]);
            if(orient){//下
                for(int j=0;j<word.length();j++){
                    int row = stloc+j;
                    boardarray[row][RC]=word.toCharArray()[j];//模擬把字放上去後，有沒有cross的情形，有的話要查字典有沒有那個字
                    StringBuilder wordtest= new StringBuilder();
                    if(RC==0&&boardarray[row][RC+1]==' ')continue;//如果在第一排，而且右邊一格是空白，代表不會有cross
                    if(RC==8&&boardarray[row][RC-1]==' ')continue;//如果在最末排，而且左邊一格是空白，代表不會有cross
                    if(RC!=0&&RC!=8&&boardarray[row][RC+1]==' '&&boardarray[row][RC-1]==' ')continue;//如果在第中間，而且左右邊一格都是空白，代表不會有cross
                    if(RC!=0){//先向左延伸，在這邊如果是最末排的，就不會向右延伸了
                        int k=1;
                        while(boardarray[row][RC-k]!=' '){
                            wordtest.insert(0, boardarray[row][RC - k]);
                            k++;
                        }
                    }
                    if(RC!=8){//再向左延伸
                        int k=1;
                        while(boardarray[row][RC+k]!=' '){
                            wordtest.append(boardarray[row][RC + k]);
                            k++;
                        }
                    }
                    if(!listDTrie.contains(wordtest.toString())) {
                        break;//結果有沒符合的就BREAK
                    }
                    if(j==word.length()&&boardlast!=boardarray){
                        crosslist.add(wordlist.get(i));
                    }
                }
            }
            if(!orient){//右
                for(int j=0;j<word.length();j++){
                    int col = stloc+j;
                    boardarray[RC][col]=word.toCharArray()[j];//模擬把字放上去後，有沒有cross的情形，有的話要查字典有沒有那個字
                    StringBuilder wordtest= new StringBuilder();
                    if(RC==0&&boardarray[RC+1][col]==' ')continue;//如果在第一排，而且右邊一格是空白，代表不會有cross
                    if(RC==8&&boardarray[RC-1][col]==' ')continue;//如果在最末排，而且左邊一格是空白，代表不會有cross
                    if(RC!=0&&RC!=8&&boardarray[RC+1][col]==' '&&boardarray[RC-1][col]==' ')continue;//如果在第中間，而且左右邊一格都是空白，代表不會有cross
                    if(RC!=0){//先向左延伸，在這邊如果是最末排的，就不會向右延伸了
                        int k=1;
                        while(boardarray[RC-k][col]!=' '){
                            wordtest.insert(0, boardarray[RC-k][col]);
                            k++;
                            if(RC-k<0)break;
                        }
                    }
                    if(RC!=8){//再向左延伸
                        int k=1;
                        while(boardarray[RC+k][col]!=' '){
                            wordtest.append(boardarray[RC+k][col]);
                            k++;
                            if(RC+k>8)break;
                        }
                    }
                    if(!listDTrie.contains(wordtest.toString())) {
                        break;//結果有沒符合的就BREAK
                    }
                    if(j==word.length()&&boardlast!=boardarray){
                        crosslist.add(wordlist.get(i));
                    }
                }
            }
        }
        return crosslist;
    }

    private String[] Evaluate(ArrayList<String> wordlist){
        int value;
        int index=0;
        int lastvalue=0;
        String[] word={" ","0","0"};
        for (int i=0;i<wordlist.size();i++){
            value=Integer.valueOf(wordlist.get(i).split(":")[2]);
            if(value>lastvalue)index=i;
        }
        word=wordlist.get(index).split(":");
        return word;
    }

}
