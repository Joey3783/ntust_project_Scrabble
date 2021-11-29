package MoveGeneration;


import java.util.ArrayList;

public class GADDAG {
    private static final char separator = '>';
    private final Trie.Node root;//根節點
    private int size;

    // 搜字是從本字開始往左搜，
    // 搜到separator代表要跳回本字，
    // 開始往右搜。存取方式 e.g., CARE => C>ARE, AC>RE, RAC>E, ERAC.

    public GADDAG(){
        this.root=new Trie.Node();
        this.size=0;
    }

    public void addGaddagWordList(ArrayList<String> list){
        if(list.size()==0)return;
        for(int i=0;i<list.size();i++){
            String word = list.get(i);
            if(!word.equals("")&&word.length()<11){
                this.addGaddagWord(word);
            }
        }
    }

    public void addGaddagWord(String word){
        if(word.length()==0){return;}

        String prefix;

        for(int i=1;i<word.length();i++){
            String prefix_buffer = word.substring(0,i);     // 取separator前面的字串
                                                            // substring的第二個數字不會出現在word裡！
            prefix = reverse(prefix_buffer);// 前面的字串反轉
            String suffix=word.substring(i);
            add(prefix+separator+suffix);  // 呼叫Trie的add函式
        }

        prefix = reverse(word);
        add(prefix);                                  // 存最後一個(ERAC)
    }
    //冒泡排序
    private static String reverse(String word){
        char[] c = word.toCharArray();
        int n = c.length-1;
        int halflength = n/2;
        for(int i=0;i<=halflength;i++){
            char temp    = c[i];
            c[i]         = c[n-i];
            c[n-i]       = temp;
        }
        return String.valueOf(c);
    }

    public int getSize(){
        return size;
    }
    public Trie.Node getRoot(){
        return root;
    }
    //新增一組單字
    public void addAll(ArrayList<String> list){
        for(int i=0;i<list.size();i++){
            String word = list.get(i);
            if(word.length()<11){
                //特別設定成只存9以內的單字，
                // 因為每個字幾乎都會加一個separator，
                // 所以這邊限制是10個字
                this.add(word);
            }
        }
    }
    //新增一個單字
    public void add(String word){
        Trie.Node current = this.root; //根
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
        Trie.Node current = root;
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
        Trie.Node current = root;
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
