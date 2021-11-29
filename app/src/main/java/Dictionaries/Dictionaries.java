package Dictionaries;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;

import MoveGeneration.GADDAG;
import MoveGeneration.Trie;

public class Dictionaries {

    private final ArrayList<String> listD;
    private final ArrayList<String> lista1;
    private final ArrayList<String> lista2;
    private final ArrayList<String> listb1;
    private final ArrayList<String> listb2;
    private final ArrayList<String> listc1;
    private final Trie trie;
    private final GADDAG gaddag;

    public Dictionaries(){
        this.gaddag = new GADDAG();
        this.trie = new Trie();
        this.listD = new ArrayList<>();
        this.lista1 = new ArrayList<>();
        this.lista2 = new ArrayList<>();
        this.listb1 = new ArrayList<>();
        this.listb2 = new ArrayList<>();
        this.listc1 = new ArrayList<>();
    }


    public void ReadFile(){

        trie.addAll(listD);
        Log.d("oawkeorjj","aftrd");

        gaddag.addGaddagWordList(listD);
        Log.d("oawkeorjj","afld");


    }


    public ArrayList<String> getListD() {
        return listD;
    }

    public Trie getTrie() {
        return trie;
    }

    public GADDAG getGaddag() {
        return gaddag;
    }

    public ArrayList<String> getLista1() {
        return lista1;
    }

    public ArrayList<String> getLista2() {
        return lista2;
    }

    public ArrayList<String> getListb1() {
        return listb1;
    }

    public ArrayList<String> getListb2() {
        return listb2;
    }

    public ArrayList<String> getListc1() {
        return listc1;
    }

    public ArrayList<String> LevelFilter(String level, HashSet<String> oriWordList, boolean isMoveGenerator) {
        ArrayList<String> newWordList = new ArrayList<>();
        //  如果是從movegenerator來的list，那word會在split後的第0個位置。
        if(isMoveGenerator) {
            for (int i = 0; i < oriWordList.size(); i++) {
                String s = (String) oriWordList.toArray()[i];
                switch (level) {
                    case "C1":
                    case "B2":
                        if (listc1.contains(s.split(":")[0])) newWordList.add(s + ":" + "C1");

                    case "B1":
                        if (listb2.contains(s.split(":")[0])) newWordList.add(s + ":" + "B2");

                    case "A2":
                        if (listb1.contains(s.split(":")[0])) newWordList.add(s + ":" + "B1");

                    case "A1":
                        if (lista2.contains(s.split(":")[0])) newWordList.add(s + ":" + "A2");
                        if (lista1.contains(s.split(":")[0])) newWordList.add(s + ":" + "A1");
                        break;

                    default://
                }
            }
        }

        else {
            for (int i = 0; i < oriWordList.size(); i++) {
                String s = (String) oriWordList.toArray()[i];
                switch (level) {
                    case "C1":
                    case "B2":
                        if (listc1.contains(s.split(":")[0])) newWordList.add(s);

                    case "B1":
                        if (listb2.contains(s.split(":")[0])) newWordList.add(s);

                    case "A2":
                        if (listb1.contains(s.split(":")[0])) newWordList.add(s);

                    case "A1":
                        if (lista2.contains(s)) newWordList.add(s);
                        if (lista1.contains(s)) newWordList.add(s);
                        break;

                    default://
                }
            }
        }

        return newWordList;
    }

    public boolean SingleWordLevelCheck(String word,String level){
        ArrayList<String> leveldic = getLevelList(level);

        return leveldic.contains(word);
    }

    public ArrayList<String> getLevelList(String level){
        ArrayList<String> mix = new ArrayList<>();

        switch (level) {
            case "C1":
            case "B2":
                mix.addAll(listc1);
            case "B1":
                mix.addAll(listb2);
            case "A2":
                mix.addAll(listb1);
            case "A1":
                mix.addAll(lista2);
                mix.addAll(lista1);
                break;

            default://
        }

        return mix;
    }

}
