package MoveGeneration;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import GameInfomation.Tile;

public class MoveGenerator {

    private GADDAG gaddag;
    private Trie trie;
    private ArrayList<ArrayList<SquareData>> Board;
    private ArrayList<ArrayList<SquareData>> BoardTranspose;
    private int anchorRow;
    private int anchorCol;
    private char tileOnCurrentSquare;
    private HashSet<String> GaddagWords;
    private final boolean Across=true;
    private final boolean Down  =false;
    private boolean hasAnchor = false;

    public void setTrie(GADDAG gaddag1, Trie trie1){
        this.gaddag = gaddag1;
        this.trie   = trie1;
    }


    public HashSet<String> getWords(ArrayList<ArrayList<Tile>> BoardArray, ArrayList<Character> Rack, boolean specify, int specRow, int specCol, boolean orien){

        GaddagWords = new HashSet<>();
        initialBoard(BoardArray);
        updateCrossSet();

        if(!hasAnchor){
            this.anchorRow = 4;
            this.anchorCol = 4;
            Generate(anchorCol,Rack, gaddag.getRoot(), Across,"");
        }
        // 如果是從指定位置開始生成，就進入這個
        else if(specify && orien==Across){
            this.anchorRow = specRow;
            this.anchorCol = specCol;
            Generate(anchorCol, Rack, gaddag.getRoot(), Across, "");
        }
        // 一般情況進入這個

        else {
            for (int row = 0; row < Board.size(); row++) {
                // 先跑across
                for (int col = 0; col < Board.size(); col++) {


                    if (!specify&&Board.get(row).get(col).getisAnchor()
                            && ((col > 0 && Board.get(row).get(col - 1).getletter() != ' ') ||
                            (col < 8 && Board.get(row).get(col + 1).getletter() != ' '))) { //從有字的那格開始生成字
                        this.anchorRow = row;
                        this.anchorCol = col;
//                        Log.d("movegenerator_t","a_anchor:"+String.valueOf(anchorRow)+anchorCol);
                        //GaddagWords.add(String.valueOf(row)+col+">");
                        Generate(anchorCol, Rack, gaddag.getRoot(), Across, "");
                    }
                }
            }
        }
        Transpose();

        if(!hasAnchor){
            this.anchorRow = 4;
            this.anchorCol = 4;
            Generate(anchorCol,Rack, gaddag.getRoot(), Down,"");
        }
        else // 如果是從指定位置開始生成，就進入這個
            if(specify && orien==Down){
                this.anchorRow = specCol;
                this.anchorCol = specRow;
                Generate(anchorCol, Rack, gaddag.getRoot(), Down, "");
            }
            else {
                Log.d("movegenerator_yy: ",String.valueOf(Board.get(8).get(2).getletter()));
                Log.d("movegenerator_yT: ",String.valueOf(Board.get(2).get(8).getletter()));

                for (int row = 0; row < Board.size(); row++) {
                    // 再跑down
                    for (int col = 0; col < Board.size(); col++) {

                        if (!specify&&Board.get(row).get(col).getisAnchor()
                                && ((col > 0 && Board.get(row).get(col - 1).getletter() != ' ') ||
                                (col < 8 && Board.get(row).get(col + 1).getletter() != ' '))) { //從有字的那格開始生成字
                            this.anchorRow = row;
                            this.anchorCol = col;
                            Generate(anchorCol, Rack, gaddag.getRoot(), Down, "");
                        }
                    }
                }
            }
        Log.d("gdfoasdjoasd_a ",Board.get(3).get(8).get_acrossSet().toString());
        Log.d("gdfoasdjoasd_b ",Board.get(3).get(8).get_downSet().toString());

        Log.d("gdfoasdjoasd_c ",Board.get(3).get(5).get_acrossSet().toString());
        Log.d("gdfoasdjoasd_d ",Board.get(3).get(5).get_downSet().toString());
        Log.d("gdfoasdjoasd_i ",String.valueOf(Board.get(3).get(6).getisAnchor()));
        Log.d("gdfoasdjoasd_j ",String.valueOf(Board.get(6).get(3).getisAnchor()));
        Transpose();
        Log.d("gdfoasdjoasd_e ",Board.get(8).get(3).get_acrossSet().toString());
        Log.d("gdfoasdjoasd_f ",Board.get(5).get(3).get_downSet().toString());


        Log.d("gdfoasdjoasd_g ",Board.get(1).get(3).get_acrossSet().toString());
        Log.d("gdfoasdjoasd_h ",Board.get(1).get(3).get_downSet().toString());
        Log.d("gdfoasdjoasd_k ",String.valueOf(Board.get(3).get(6).getisAnchor()));
        Log.d("gdfoasdjoasd_l ",String.valueOf(Board.get(6).get(3).getisAnchor()));
        return GaddagWords;

    }

    private void initialBoard(ArrayList<ArrayList<Tile>> BoardArray){
        Board = new ArrayList<>(BoardArray.size());
        BoardTranspose = new ArrayList<>(BoardArray.size());
        for(int i=0;i<BoardArray.size();i++){
            Board.add(new ArrayList<>(BoardArray.size()));
            BoardTranspose.add(new ArrayList<>(BoardArray.size()));
        }
        for(int row=0;row<BoardArray.size();row++){
            for(int col=0;col<BoardArray.size();col++) {
                Board.get(row).add(col, new SquareData());
                BoardTranspose.get(row).add(col, new SquareData());
                Board.get(row).get(col).setletter(BoardArray.get(row).get(col).getLetter());
            }
        }


        // if there are no anchor, it means the board is first shot,
        // so we set the middlest point to be an anchor.
        for(int row=0;row<BoardArray.size();row++){
            for(int col=0;col<BoardArray.size();col++){
                Board.get(row).get(col).clearAcrossSet();
                Board.get(row).get(col).clearDownSet();
                if (BoardArray.get(row).get(col).getLetter() != ' ') {
                    hasAnchor = true;
                    if (row > 0 && BoardArray.get(row-1).get(col).getLetter() == ' ')
                        Board.get(row - 1).get(col).setisAnchor(true);//設up是anchor

                    if (col > 0 && BoardArray.get(row).get(col-1).getLetter() == ' ')
                        Board.get(row).get(col - 1).setisAnchor(true);//設left是anchor

                    if (row < BoardArray.size()-1 && BoardArray.get(row+1).get(col).getLetter() == ' ')
                        Board.get(row + 1).get(col).setisAnchor(true);//設down是anchor

                    if (col < BoardArray.size()-1 && BoardArray.get(row).get(col+1).getLetter() == ' ')
                        Board.get(row).get(col + 1).setisAnchor(true);//設right是anchor
                }
            }
        }
        if(!hasAnchor){
            Board.get(4).get(4).setisAnchor(true);
        }
    }

    // 負責生牌
    private void Generate(int currentCol, ArrayList<Character> rack, Trie.Node arc, boolean dir,String word){
        // 取得目前位置上的牌
        tileOnCurrentSquare=Board.get(anchorRow).get(currentCol).getletter();
        // 如果已經有牌在上面
        if(tileOnCurrentSquare!=' '){
            // 更新Node，並繼續
            Trie.Node NewArc = arc.getNode(tileOnCurrentSquare);
            GoOn(currentCol, tileOnCurrentSquare, rack, NewArc, arc, dir,word);
        }
        // 如果沒有牌，而且手牌(rack)仍還有牌可以使用
        else if(!rack.isEmpty()){
            // 找到目前位置的Cross-Set
            Set<Character> crossSet;
            if(dir==Across)
                crossSet = Board.get(anchorRow).get(currentCol).get_acrossSet();
            else
                crossSet = Board.get(anchorRow).get(currentCol).get_downSet();

            // 取得手牌內所有的牌作使用
            for(int i=0;i<rack.size();i++){
                // 取得手牌內的某張牌(tile)作使用
                char tile=rack.get(i);
                // 刪除手牌內的tile，作為新手牌(newRack)
                ArrayList<Character> newRack=new ArrayList<>(rack);
                newRack.remove(i);
                // tile不是空白牌(空白牌以'-'作標記)
                if(tile!='-') {
                    // 目前位置是Anchor，考慮Cross-Set
                    if (Board.get(anchorRow).get(currentCol).getisAnchor()){
                        // 若Cross-Set存在'*'，則為空集合
                        if (crossSet.contains(tile)&&!crossSet.contains('*')) {
                            tileOnCurrentSquare = tile;
                            Trie.Node NewArc = arc.getNode(tileOnCurrentSquare);
                            GoOn(currentCol, tileOnCurrentSquare, newRack, NewArc, arc, dir, word);
                        }
                    }
                    // 目前位置是非Anchor，不考慮Cross-Set
                    else {
                        tileOnCurrentSquare = tile;
                        Trie.Node NewArc = arc.getNode(tileOnCurrentSquare);
                        GoOn(currentCol, tileOnCurrentSquare, newRack, NewArc, arc, dir, word);
                    }
                }
                // tile是空白牌
                else{
                    // 目前位置是Anchor，考慮Cross-Set
                    if (Board.get(anchorRow).get(currentCol).getisAnchor()) {
                        for (char blanktile:crossSet){
                            // 若Cross-Set存在'*'，則為空集合
                            if (!crossSet.contains('*')) {
                                tileOnCurrentSquare=blanktile;
                                Trie.Node NewArc = arc.getNode(tileOnCurrentSquare);
                                GoOn(currentCol, blanktile, newRack, NewArc, arc, dir,word);
                            }
                        }
                    }
                    // 目前位置非Anchor，不考慮Cross-Set
                    else {
                        for (char blanktile:arc.getNext().keySet()){
                            tileOnCurrentSquare = blanktile;
                            Trie.Node NewArc = arc.getNode(tileOnCurrentSquare);
                            GoOn(currentCol, blanktile, newRack, NewArc, arc, dir,word);

                        }
                    }
                }
            }
        }
    }

    // 負責放牌
    private void GoOn(int currentcol, char letter, ArrayList<Character> rack, Trie.Node NewArc, Trie.Node OldArc,boolean dir,String word){

        //
        if(currentcol<=anchorCol){
            word = letter + word;
            if(OldArc.getNode(tileOnCurrentSquare)!=null) {
                if (OldArc.getNode(tileOnCurrentSquare).isEnd() && (currentcol == 0 || Board.get(anchorRow).get(currentcol - 1).getletter() == ' ') &&
                        (anchorCol<8&&Board.get(anchorRow).get(anchorCol + 1).getletter() == ' ')) { // 如果延伸到結束點，代表這個字可以用，納入考量！
                    if (dir == Across)
                        GaddagWords.add(word.replace(" ","") + ":" + anchorRow + ":" + currentcol + ":" + "A");
                    else if(dir == Down)
                        GaddagWords.add(word.replace(" ","") + ":" + currentcol + ":" + anchorRow + ":" + "D");
                }
            }

            if(NewArc!=null){ //如果接下去還有東西，就繼續讀
                if(currentcol>0){ //如果不是在0的位置，代表還能繼續延伸
                    Generate(currentcol-1,rack,NewArc,dir,word);
                }
                char separator = '>';
                NewArc = NewArc.getNode(separator);
                if (NewArc!=null && (currentcol == 0 || Board.get(anchorRow).get(currentcol - 1).getletter() == ' ') &&
                        anchorCol < Board.size()-1) {
                    Generate(anchorCol + 1, rack, NewArc, dir, word);
                }
            }
        }
        else if(currentcol>anchorCol){ //向右(下)延伸
            word = word + letter;
            if(dir==Across) {
                if(OldArc.getNode(tileOnCurrentSquare)!=null) {
                    if (OldArc.getNode(tileOnCurrentSquare).isEnd() && (currentcol == Board.size()-1 || Board.get(anchorRow).get(currentcol + 1).getletter() == ' ')) { // 如果延伸到結束點，代表這個字可以用，納入考量！
                        GaddagWords.add(word.replace(" ","") + ":" + anchorRow + ":" + (currentcol - word.length() + 1) + ":" + "A");
                    }
                }
                if (NewArc!=null && currentcol < Board.size()-1) {
                    Generate(currentcol + 1,  rack, NewArc, dir,word);
                }
            }
            else if(dir==Down) {
                if(OldArc.getNode(tileOnCurrentSquare)!=null) {
                    if (OldArc.getNode(tileOnCurrentSquare).isEnd() && (currentcol == Board.size()-1 || Board.get(anchorRow).get(currentcol + 1).getletter() == ' ')) { // 如果延伸到結束點，代表這個字可以用，納入考量！
                        GaddagWords.add(word.replace(" ","") + ":" + (currentcol - word.length() + 1) + ":" + anchorRow + ":" + "D");
                    }
                }
                if (NewArc != null && currentcol < Board.size()-1) {
                    Generate(currentcol + 1, rack, NewArc, dir,word);
                }
            }
        }
    }


    private void updateCrossSet(){

        for(int row=0;row<Board.size();row++){
            int skipCol=0;
            // 先找downset 用的
            for(int col=0;col<Board.size();col++){
                if(col<skipCol)continue;
                if(Board.get(row).get(col).getletter()!=' '){ //
//                    Log.d("movegenerator_t","upc:in "+String.valueOf(row)+col);

                    updateDownSetRight(row,col, trie.getRoot());
//                    Log.d("movegenerator_t","upc:has_d_r");
                    int offset=1;
                    while (col+offset<8&&Board.get(row).get(col+offset).getletter()!=' '){
                        offset++;
//                        Log.d("movegenerator_t","upc:has_d_r_of : "+offset);
                    }

                    if(col==8){
                        offset=0;
                    }
                    if(Board.get(row).get(col+offset).getletter()!=' '){ //用在最後一個字在最右底的時候
//                        Log.d("movegenerator_t","upc:has_d_l_1_in "+ String.valueOf(row) +col+" , "+offset);
                        updateDownSetLeft(row,col+offset, gaddag.getRoot());
//                        Log.d("movegenerator_t","upc:has_d_l_1");
                    }
                    else if(col>0&&Board.get(row).get(col+offset).getletter()==' '){ //通常時候用，第八格是空白
//                        Log.d("movegenerator_t","upc:has_d_l_2_in "+ offset);
                        updateDownSetLeft(row,col+offset-1, gaddag.getRoot());
//                        Log.d("movegenerator_t","upc:has_d_l_2");
                    }
                    skipCol = col+offset;

                }

            }
        }
        Transpose();
        Log.d("movegenerator_t","gfhfj ");

        for(int row=0;row<Board.size();row++){
            int skipCol=0;
            for(int col=0;col<Board.size();col++){
                if(col<skipCol)continue;
                if(Board.get(row).get(col).getletter()!=' '){
                    Log.d("movegenerator_t","upc:in_2 "+String.valueOf(row)+col);
                    updateAcrossSetDown(row,col, trie.getRoot());
//                    Log.d("movegenerator_t","upc:has_a_d");
                    int offset=1;
                    while (col+offset<8&&Board.get(row).get(col+offset).getletter()!=' '){
                        offset++;
                    }
                    if(col==8){
                        offset=0;
                    }
                    if(Board.get(row).get(col+offset).getletter()!=' '){ //用在最後一個字在最右底的時候
                        updateAcrossSetUp(row,col+offset, gaddag.getRoot());
//                        Log.d("movegenerator_t","upc:has_a_u1");
                    }
                    else if(col>0&&Board.get(row).get(col+offset).getletter()==' '){ //通常時候用
                        updateAcrossSetUp(row,col+offset-1, gaddag.getRoot());
//                        Log.d("movegenerator_t","upc:has_a_u2");
                    }
                    skipCol = col+offset;

                }

            }
        }
        Transpose();

        for(int row=0;row<Board.size();row++){
            for(int col=0;col<Board.size();col++) {
                if(Board.get(row).get(col).get_acrossSet().isEmpty())
                    Board.get(row).get(col).initialAcrossSet();
                if(Board.get(row).get(col).get_downSet().isEmpty())
                    Board.get(row).get(col).initialDownSet();
            }
        }
    }

    public void Transpose(){
        for(int row=0;row<Board.size();row++){
            for(int col=0;col<Board.size();col++){

                BoardTranspose.get(col).get(row).setletter(Board.get(row).get(col).getletter());
                BoardTranspose.get(col).get(row).setisAnchor(Board.get(row).get(col).getisAnchor());
                BoardTranspose.get(col).get(row).set_acrossSet(Board.get(row).get(col).get_acrossSet());
                BoardTranspose.get(col).get(row).set_downSet(Board.get(row).get(col).get_downSet());

            }
        }

        for(int row=0;row<Board.size();row++){
            for(int col=0;col<Board.size();col++){

                Board.get(row).get(col).setletter(BoardTranspose.get(row).get(col).getletter());
                Board.get(row).get(col).setisAnchor(BoardTranspose.get(row).get(col).getisAnchor());
                Board.get(row).get(col).set_acrossSet(BoardTranspose.get(row).get(col).get_acrossSet());
                Board.get(row).get(col).set_downSet(BoardTranspose.get(row).get(col).get_downSet());

            }
        }
    }

    private void updateDownSetRight(int row, int col, Trie.Node arc){
        Log.d("asdafgegee",String.valueOf(row)+col);
        if(col<8){
            int offset=1;

            //往右找到底（col=8 或是 找到有空白的）（特例是空白剛好在兩個單字中間）
            Trie.Node NextArc = arc.getNode(Board.get(row).get(col).getletter());
            while(col+offset<8&&Board.get(row).get(col+offset).getletter()!=' '){
                if(NextArc!=null) {
                    NextArc = NextArc.getNode(Board.get(row).get(col + offset).getletter());

                }
                offset++;
            }

            // 如果剛好在第八排 或是 再右邊一格也是空白，放字進去
            if((col+offset<9)&&(col+offset==8 || Board.get(row).get(col+offset+1).getletter()==' ')){
                for(char c:NextArc.getNext().keySet()){
                    if(NextArc.getNode(c).isEnd())
                        Board.get(row).get(col+offset).addDownSet(c);

                }
                // 如果發現crossSet是空的時候，存一個＊當作記號
                if(Board.get(row).get(col+offset).get_downSet().isEmpty()){
                    Board.get(row).get(col+offset).addDownSet('*');
                }

            }
            // 如果再右邊一格又有字
            else if((col+offset<9)&&Board.get(row).get(col+offset+1).getletter()!=' '){
                for(char c:NextArc.getNext().keySet()){
                    Trie.Node NewArc = NextArc.getNode(c);
                    int offset_1 = offset+1;
                    boolean correct = true;
                    while((col+offset_1<9)&&Board.get(row).get(col+offset_1).getletter()!=' '){
                        if(NewArc!=null) {
                            NewArc = NewArc.getNode(Board.get(row).get(col + offset_1).getletter());

                        }
                        else {
                            correct = false;
                        }
                        offset_1++;
                    }

                    if(NewArc == null)
                        correct=false;

                    if(correct) { //應該要是new arc 是end 不是next
                        if (NewArc.isEnd())
                            Board.get(row).get(col + offset).addDownSet(c);
                    }
                }
                // 如果發現crossSet是空的時候，存一個＊當作記號
                if(Board.get(row).get(col+offset).get_downSet().isEmpty()){
                    Board.get(row).get(col+offset).addDownSet('*');
                }
            }

        }
    }

    private void updateDownSetLeft(int row, int col, Trie.Node arc){

        if(col>0&&col<9){
            int offset=1;

            //往左找到底（col=0 或是 找到有空白的）（特例是空白剛好在兩個單字中間）
            Trie.Node NextArc = arc.getNode(Board.get(row).get(col).getletter());
            while(col-offset>=0&&Board.get(row).get(col-offset).getletter()!=' '){
                if(NextArc!=null) {
                    NextArc = NextArc.getNode(Board.get(row).get(col - offset).getletter());
                }
                offset++;
            }

            // 如果剛好在第0排 或是 再左邊一格也是空白，放字進去
            if((col-offset>-1)&&(col-offset==0 || Board.get(row).get(col-offset-1).getletter()==' ')){
//                Log.d("updateDownSetLeft : ","easy put");
                for(char c:NextArc.getNext().keySet()){
                    if(NextArc.getNode(c).isEnd())
                        Board.get(row).get(col-offset).addDownSet(c);
                }
                // 如果發現crossSet是空的時候，存一個＊當作記號
                if(Board.get(row).get(col-offset).get_downSet().isEmpty()){
                    Board.get(row).get(col-offset).addDownSet('*');
                }
            }
            // 如果再右邊一格又有字
            else if((col-offset>-1)&&Board.get(row).get(col-offset-1).getletter()!=' '){
//                Log.d("updateDownSetLeft : ","hard put");
                for(char c:NextArc.getNext().keySet()){
                    Trie.Node NewArc = NextArc.getNode(c);
                    int offset_1 = offset+1;
                    boolean correct =true;
//                    Log.d("updateDownSetLeft : ","hard put_ be wh "+String.valueOf(row)+col+String.valueOf(offset));
                    while((col-offset_1>-1)&&Board.get(row).get(col-offset_1).getletter()!=' '){
                        if(NewArc!=null) {
                            NewArc = NewArc.getNode(Board.get(row).get(col - offset_1).getletter());
//                            Log.d("updateDownSetLeft : ","hard put_ ing wh "+offset_1);

                        }
                        else{
                            correct = false;
                        }
                        offset_1++;
//                        Log.d("updateDownSetLeft : ","hard put_ ing wh degbug");
                    }
//                    Log.d("updateDownSetLeft : ","hard put_ af wh");

                    if(NewArc == null)
                        correct=false;

                    if(correct) { //應該要是new arc 是end 不是next
                        if (NewArc.isEnd())
                            Board.get(row).get(col - offset).addDownSet(c);
                    }
                }
                // 如果發現crossSet是空的時候，存一個＊當作記號
                if(Board.get(row).get(col-offset).get_downSet().isEmpty()){
                    Board.get(row).get(col-offset).addDownSet('*');
                }
            }

        }
    }



    private void updateAcrossSetDown(int row, int col, Trie.Node arc){

        if(row==3&&col==1){
            Log.d("oasjdaof","get in");
        }
        if(col<Board.size()-1){
            int offset=1;

            //往右找到底（col=8 或是 找到有空白的）（特例是空白剛好在兩個單字中間）
            Trie.Node NextArc = arc.getNode(Board.get(row).get(col).getletter());
            while(col+offset<8&&Board.get(row).get(col+offset).getletter()!=' '){
                if(NextArc!=null) {
                    NextArc = NextArc.getNode(Board.get(row).get(col + offset).getletter());

                }
                offset++;
            }
            if(row==3&&col==1){
                Log.d("oasjdaof",String.valueOf(offset));
            }

            // 如果剛好在第八排 或是 再右邊一格也是空白，放字進去
            if((col+offset<9)&&(col+offset==8 || Board.get(row).get(col+offset+1).getletter()==' ')){
                if(row==3&&col==1){
                    Log.d("oasjdaof","first");
                }
                if(NextArc!=null) {
                    for (char c : NextArc.getNext().keySet()) {
                        if (NextArc.getNode(c).isEnd())
                            Board.get(row).get(col + offset).addAcrossSet(c);
                    }
                    // 如果發現crossSet是空的時候，存一個＊當作記號
                    if(Board.get(row).get(col+offset).get_acrossSet().isEmpty()){
                        Board.get(row).get(col+offset).addAcrossSet('*');
                    }
                }
            }
            // 如果再右邊一格又有字
            else if((col+offset<9)&&Board.get(row).get(col+offset+1).getletter()!=' '){
                if(row==3&&col==1){
                    Log.d("oasjdaof","second");
                }
                for(char c:NextArc.getNext().keySet()){
                    Trie.Node NewArc = NextArc.getNode(c);
                    int offset_1 = offset+1;
                    boolean correct = true;

                    while((col+offset_1<9)&&Board.get(row).get(col+offset_1).getletter()!=' '){
                        Log.d("oasjdaof",String.valueOf(Board.get(row).get(col+offset_1).getletter()));
                        if(NewArc!=null) {
                            Log.d("oasjdaod",String.valueOf(Board.get(row).get(col+offset_1).getletter()));

                            NewArc = NewArc.getNode(Board.get(row).get(col + offset_1).getletter());

                        }
                        else {
                            correct = false;
                        }
                        offset_1++;
                    }

                    if(NewArc == null)
                        correct = false;

                    if(correct) { //應該要是new arc 是end 不是next
                        if (NewArc.isEnd())
                            Board.get(row).get(col + offset).addAcrossSet(c);
                    }
                    if(row==3&&col==1&&correct){
                        if(NewArc.isEnd())
                            Log.d("oasjdaof_isend ",String.valueOf(c));
                    }
                }
                // 如果發現crossSet是空的時候，存一個＊當作記號
                if(Board.get(row).get(col+offset).get_acrossSet().isEmpty()){
                    if(row==6&&col==4){
                        Log.d("oasjdaof_isend ","none");
                    }
                    Board.get(row).get(col+offset).addAcrossSet('*');
                }
            }

        }
    }

    private void updateAcrossSetUp(int row, int col, Trie.Node arc){

        if(col>0&&col<9){
            int offset=1;

            //往左找到底（col=0 或是 找到有空白的）（特例是空白剛好在兩個單字中間）
            Trie.Node NextArc = arc.getNode(Board.get(row).get(col).getletter());
            while(col-offset>=0&&Board.get(row).get(col-offset).getletter()!=' '){
                if(NextArc!=null) {
                    NextArc = NextArc.getNode(Board.get(row).get(col - offset).getletter());

                }
                offset++;
            }

            // 如果剛好在第0排 或是 再左邊一格也是空白，放字進去
            if((col-offset>-1)&&(col-offset==0 || Board.get(row).get(col-offset-1).getletter()==' ')) {
                if (NextArc != null){
                    for (char c : NextArc.getNext().keySet()) {
                        if (NextArc.getNode(c).isEnd())
                            Board.get(row).get(col - offset).addAcrossSet(c);
                    }
                    // 如果發現crossSet是空的時候，存一個＊當作記號
                    if(Board.get(row).get(col-offset).get_acrossSet().isEmpty()){
                        Board.get(row).get(col-offset).addAcrossSet('*');
                    }
                }
            }
            // 如果再左邊一格又有字
            else if((col-offset>-1)&&Board.get(row).get(col-offset-1).getletter()!=' '){
                for(char c:NextArc.getNext().keySet()){
                    Trie.Node NewArc = NextArc.getNode(c);
                    int offset_1 = offset+1;
                    boolean correct = true;

                    while((col-offset_1>-1)&&Board.get(row).get(col-offset_1).getletter()!=' '){
                        if(NewArc!=null) {
                            NewArc = NewArc.getNode(Board.get(row).get(col - offset_1).getletter());

                        }
                        else {
                            correct = false;
                        }
                        offset_1++;
                    }
                    if(NewArc == null)
                        correct = false;

                    if(correct) { //應該要是new arc 是end 不是next
                        if (NewArc.isEnd())
                            Board.get(row).get(col - offset).addAcrossSet(c);
                    }
                }
                // 如果發現crossSet是空的時候，存一個＊當作記號
                if(Board.get(row).get(col-offset).get_acrossSet().isEmpty()){
                    Board.get(row).get(col-offset).addAcrossSet('*');
                }
            }

        }
    }



}
