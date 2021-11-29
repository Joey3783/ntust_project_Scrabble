package MoveGeneration;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Trie {

    private final Node root;//根節點
    private int size;

    public static class Node{
        private boolean End; // 判斷是否為字的結尾
        private TreeMap<Character, Node> next;

        public Node(boolean End){   // 存完之後的trie，裡面的每個node不是指向下一個，就是代表結束
            this.End = End;         // 字的結束
            next = new TreeMap<>(); // 指向下一個node<Key,Value>
        }
        public Node(){
            this(false);
        }

        public Node getNode(char letter) {
            if(next.containsKey(letter))
                return next.get(letter);
            else
                return null;
        }

        public boolean isEnd() {
            return End;
        }

        public void setEnd(boolean end) {
            End = end;
        }

        public Map<Character, Node> getNext() {
            return next;
        }
        public void addNext(char c){
            this.next.put(c, new Node());
        }

    }

    public Trie(){
        root = new Node();
        size =0;
    }
    // 單字的數量
    public int getSize(){
        return size;
    }
    public Node getRoot(){
        return root;
    }
    //新增一組單字
    public void addAll(ArrayList<String> list){
        for(int i=0;i<list.size();i++){
            String word = list.get(i);
            if(word.length()<10){
                //特別設定成只存9以內的單字，
                // 因為每個字幾乎都會加一個separator，
                // 所以這邊限制是10個字
                this.add(word);
            }
        }
    }
    //新增一個單字
    public void add(String word){
        Node current = this.root; //根
        for(int i=0;i<word.length();i++){
            char c=word.charAt(i);
            if(current.getNext().isEmpty()||current.getNext().get(c)==null){
                //沒有與之對應的子節點，建立一個子節點，比如在根節點下面，只有存b c，就創一個a的子節點
                //current.getNext().put(c, new Node());
                current.addNext(c);
            }
            current = current.getNext().get(c);
        }

        assert current != null;
        if(!current.isEnd()){
            // 到這已經把單字都新增完了，所以要標示該節點是單字的結尾
            current.setEnd(true);
            size++; // 代表trie又多了一個字詞
        }
    }
    // 查詢單字是否存在於trie
    public boolean contains(String word){
        Node current = root;
        for(int i=0;i<word.length();i++){
            char c = word.charAt(i);
            assert current != null;
            if(current.getNext().get(c)==null){
                return false;
            }
            current = current.getNext().get(c);
        }
        // 查到最後一個字母，並且他是該單字的結尾，才可以確定單字存在trie當中
        assert current != null;
        return current.isEnd();
    }
    //查詢是否在trie中有單字以prefix為字首
    public boolean hasPrefix(String prefix){
        Node current = root;
        for(int i=0;i<prefix.length();i++){
            char c = prefix.charAt(i);
            assert current != null;
            if(current.getNext().get(c)==null){
                return false;
            }
            current = current.getNext().get(c);
        }
        return true;
    }
}
