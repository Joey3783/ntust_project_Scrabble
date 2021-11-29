package MoveGeneration;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class SquareData {

    private char _letter;
    private boolean _isAnchor;

    private HashSet<Character> _acrossSet; // 給across時用，也就是across時遇到這個node只能用這裡面的字母
    private HashSet<Character> _downSet;   // 給down時用，也就是down時遇到這個node只能用這裡面的字母

    public SquareData() {
        this._letter = ' ';
        this._isAnchor = false;
        this._acrossSet = new HashSet<>();
        this._downSet = new HashSet<>();
    }

    public char getletter() {
        return this._letter;
    }

    public void setletter(char _letter) {
        this._letter = _letter;
    }

    public boolean getisAnchor() {
        return _isAnchor;
    }

    public void setisAnchor(boolean _isAnchor) {
        this._isAnchor = _isAnchor;
    }

    public HashSet<Character> get_acrossSet() {
        return _acrossSet;
    }

    public HashSet<Character> get_downSet() {
        return _downSet;
    }

    public void addAcrossSet(char letter) {
        _acrossSet.add(letter);
    }

    public void addDownSet(char letter) {
        _downSet.add(letter);
    }

    public void set_acrossSet(HashSet<Character> acrossSet){
        _acrossSet = acrossSet;
    }
    public void set_downSet(HashSet<Character> downSet){
        _downSet = downSet;
    }


    public void initialAcrossSet(){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for(int i=0;i<alphabet.length();i++){
            _acrossSet.add(alphabet.charAt(i));
        }
    }

    public void initialDownSet(){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for(int i=0;i<alphabet.length();i++){
            _downSet.add(alphabet.charAt(i));
        }
    }

    public void clearAcrossSet(){
        _acrossSet.clear();
    }

    public void clearDownSet(){
        _downSet.clear();
    }




}
