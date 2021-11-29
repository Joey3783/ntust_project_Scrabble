package GameActivity;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.R;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import Adapter.Menu_Data;
import Adapter.RecycAdapter_board;
import Adapter.RecycAdapter_historyword;
import Adapter.RecycAdapter_menu;
import Adapter.RecycAdapter_rack;
import Dictionaries.Dictionaries;
import GameInfomation.GameInfo;
import MoveGeneration.MoveGenerator;
import Spelling.SpellingChecker;

public class PlayActivity extends AppCompatActivity {

    /** bluetooth */
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int MESSAGE_READ=0;
    String writeBTpacket_bluetooth_message;

    /** board */

    TextView txt_player;
    TextView txt_comp;
    TextView txt_score;
    int readBTpacket_currentRow;      // 給readBTPacket用，用來找字的起始點
    int readBTpacket_currentCol;      // 給readBTPacket用，用來找字的起始點

    int readBTpacket_lastRow;      // 給readBTPacket用，用來找字的起始點
    int readBTpacket_lastCol;      // 給readBTPacket用，用來找字的起始點

    boolean readBTpacket_orien;

    MoveGenerator moveGenerator = new MoveGenerator();
    RecyclerView Boardrecycview;
    RecyclerView Handrecycview;
    RecyclerView Comprecycview;
    List<Integer> BoardGridList = new ArrayList<>(81);
    List<Integer> HandGridList = new ArrayList<>(7);
    List<Integer> CompGridList = new ArrayList<>(7);
    RecycAdapter_historyword historywordAdapter;
    FrameLayout frame_board;
    ConstraintLayout cons_button;

    LinearLayout llbutton;
    GameInfo gameInfo;
    Dictionaries dictionaries;
    SpellingChecker spellingChecker;
    final boolean Across = true;
    final boolean Down = false;

    final boolean round_player = true;
    final boolean round_comp = false;

    private String Level;
    private char tile_buffer=' ';

    private int currentWordScore=0;
    View view_hint;
    TextView txt_hintword;


    @SuppressWarnings("deprecation")
    @SuppressLint("HandlerLeak")
    Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg_type) {
            super.handleMessage(msg_type);

            if (msg_type.what == MESSAGE_READ) {

                byte[] readbuf = new byte[1024];
                System.arraycopy((byte[]) msg_type.obj,0,readbuf,0,msg_type.arg1);
//                        (byte[]) msg_type.obj;
                String string_recieved;
                // 把資料從ascii轉成string
                if (!(readbuf == null)) {
                    String Received_raw = ascii2String(readbuf);
                    String Received = Received_raw.replace("@","");


                    Log.d("oafjofkwpacket", ascii2String(readbuf));
                    string_recieved = Received.substring(0, 6);


                    if (string_recieved.toCharArray()[0] == '#') {
                        readBTPacket_func(string_recieved);
                    }


                }
            }
        }
    };
    ConnectedThread connectedThread;
    RecycAdapter_board Boardadapter_recyc;
    RecycAdapter_rack Handadapter_recyc;
    RecycAdapter_rack Compadapter_recyc;

    ImageButton btn_bag;
    Button btn_generate;
    Button btn_submit;
    boolean isHintMode = false;
    PopupWindow popupWindow_hint;
    public static PlayActivity instance;
    private RefWatcher refWatcher;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        instance = this;
        refWatcher = LeakCanary.install(getApplication());

        ProgressBar pb_loading = findViewById(R.id.pb_loading);
        setProgressMax(pb_loading,100);
        setProgressAnimate(pb_loading,100);
        ConstraintLayout cl_loading = findViewById(R.id.cl_loading);
        ImageView img_L = findViewById(R.id.loading_L);
        ImageView img_O = findViewById(R.id.loading_O);
        ImageView img_A = findViewById(R.id.loading_A);
        ImageView img_D = findViewById(R.id.loading_D);
        ImageView img_I = findViewById(R.id.loading_I);
        ImageView img_N = findViewById(R.id.loading_N);
        ImageView img_G = findViewById(R.id.loading_G);

        @SuppressLint("ResourceType") Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.animator.expand);
        img_L.startAnimation(animation);
        img_O.startAnimation(animation);
        img_A.startAnimation(animation);
        img_D.startAnimation(animation);
        img_I.startAnimation(animation);
        img_N.startAnimation(animation);
        img_G.startAnimation(animation);




        Intent intent = this.getIntent();
        Level = intent.getStringExtra("Level");
        String Game = intent.getStringExtra("Game");
        gameInfo = new GameInfo();
        if(!Game.equals("")){
            Log.d("startdod : ",Game);
            gameInfo.setGame(Game);
        }
        dictionaries = new Dictionaries();

        new Thread(()->{

            Log.d("oawkeorjj","br");
            ReadFile();
            Log.d("oawkeorjj","afrf");
            dictionaries.ReadFile();//載入資料
            runOnUiThread(()->{
                Log.d("oawkeorjj","all");
                cl_loading.setVisibility(View.GONE);
                btn_submit.setVisibility(View.VISIBLE);
                moveGenerator.setTrie(dictionaries.getGaddag(), dictionaries.getTrie());
                playerChange();
                spellingChecker = new SpellingChecker(this,dictionaries,Level);

            });
        }).start();


        view_hint  = LayoutInflater.from(this).inflate(R.layout.hint_view,null);
        popupWindow_hint = new PopupWindow(view_hint);
        txt_hintword = view_hint.findViewById(R.id.txt_hint);
        cons_button = findViewById(R.id.cons_button);

        btn_submit = findViewById(R.id.btn_Go); //按一下就會產生單字
        btn_generate = findViewById(R.id.btn_Generate); //按一下會逐步放在上去版子上
        frame_board = findViewById(R.id.frame_board);
        ImageButton btn_menu = findViewById(R.id.btn_Menu);
        ImageButton btn_pass = findViewById(R.id.btn_Pass);
        ImageButton btn_shuffle = findViewById(R.id.btn_Shuffle);
        btn_bag = findViewById(R.id.btn_Bag);
        llbutton = findViewById(R.id.LL_button);
        txt_player = findViewById(R.id.txt_player);
        txt_comp = findViewById(R.id.txt_comp);
        txt_score = findViewById(R.id.txt_score);
        txt_score.setVisibility(View.GONE); // hide



        Boardrecycview = findViewById(R.id.recyc_board);
        Handrecycview = findViewById(R.id.recyc_hand);
        Comprecycview = findViewById(R.id.recyc_comp);




        InitialBoard();


        Boardrecycview.setLayoutManager(new GridLayoutManager(this,9));
        Boardadapter_recyc = new RecycAdapter_board(this, BoardGridList);
        Boardrecycview.setAdapter(Boardadapter_recyc);

        Handrecycview.setLayoutManager(new GridLayoutManager(this,HandGridList.size()));
        Handadapter_recyc = new RecycAdapter_rack(this, HandGridList);
        Handrecycview.setAdapter(Handadapter_recyc);

        Comprecycview.setLayoutManager(new GridLayoutManager(this,CompGridList.size()));
        Compadapter_recyc = new RecycAdapter_rack(this, CompGridList);
        Comprecycview.setAdapter(Compadapter_recyc);

        Runtime.getRuntime().gc();




        btn_menu.setOnClickListener(v -> showMenu());

        // 按下之後代表確認要拼這個字
        btn_submit.setOnClickListener(v -> {
            Log.d("timeingtest","begin");
            // we have to check both of Across and Down situation, since to put a word on Scrabble,
            // the word should be fit in Both of Across and Down situation. And we don't know
            // what direction the player considers, so we need to check both and use the higher score one.
            if (gameInfo.getRound() != round_player) {
                return;
            }

            StringBuilder wordbuffer = new StringBuilder();       // wordbuffer for down
            int stRow;
            int stCol;
            int offset = 0;
            int currentRow_buffer = readBTpacket_currentRow;
            int currentCol_buffer = readBTpacket_currentCol;

            if(gameInfo.getPlayer_rack_buffer().size()==0){
                MisspelledView();
            }

            else if(gameInfo.getPlayer_rack_buffer().size()<2){
                int a_strow;
                int a_stcol;
                int a_score;
                StringBuilder a_word= new StringBuilder();
                int d_strow;
                int d_stcol;
                int d_score;
                StringBuilder d_word= new StringBuilder();

                // 先找across
                while (currentCol_buffer > -1 &&
                        gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer - offset).getLetter() != ' ') {
                    a_word.insert(0, gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer - offset).getLetter());
                    if (currentCol_buffer - offset - 1 < 0)
                        break;
                    offset++;
                }
                if (currentCol_buffer - offset == 0 && gameInfo.getBoard().getBoard().get(currentRow_buffer).get(0).getLetter() != ' ')
                    currentCol_buffer = 0;

                else
                    currentCol_buffer = currentCol_buffer - offset + 1;
                Log.d("across", currentCol_buffer + "," + offset);
                a_strow = currentRow_buffer;
                a_stcol = currentCol_buffer;
                // 再拼湊出整個字，向右拼
                currentRow_buffer = readBTpacket_currentRow;
                currentCol_buffer = readBTpacket_currentCol;
                offset = 1;
                while (currentCol_buffer+offset<gameInfo.getBoard().getBoard().size() && gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer + offset).getLetter() != ' ') {
                    a_word.append(gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer + offset).getLetter());
                    if (currentCol_buffer + offset + 1 >= gameInfo.getBoard().getBoard().size())
                        break;
                    offset++;
                }
                a_score = gameInfo.EvaluateWord(a_strow, a_stcol, a_word.toString(), Across);


                currentRow_buffer = readBTpacket_currentRow;
                currentCol_buffer = readBTpacket_currentCol;
                offset = 0;
                // Down
                // 先找到字的源頭，向上找
                while (currentRow_buffer > -1 &&
                        gameInfo.getBoard().getBoard().get(currentRow_buffer - offset).get(currentCol_buffer).getLetter() != ' ') {
                    d_word.insert(0, gameInfo.getBoard().getBoard().get(currentRow_buffer - offset).get(currentCol_buffer).getLetter());
                    if (currentRow_buffer - offset - 1 < 0)
                        break;
                    offset++;
                }
                if (currentRow_buffer - offset == 0 && gameInfo.getBoard().getBoard().get(0).get(currentCol_buffer).getLetter() != ' ')
                    currentRow_buffer = 0;

                else
                    currentRow_buffer = currentRow_buffer - offset + 1;
                Log.d("Row", currentRow_buffer + "," + offset);

                d_strow = currentRow_buffer;
                d_stcol = currentCol_buffer;
                // 再拼湊出整個字，向下拼
                currentRow_buffer = readBTpacket_currentRow;
                currentCol_buffer = readBTpacket_currentCol;
                offset = 1;

                while (currentRow_buffer+offset<gameInfo.getBoard().getBoard().size() && gameInfo.getBoard().getBoard().get(currentRow_buffer + offset).get(currentCol_buffer).getLetter() != ' ') {
                    d_word.append(gameInfo.getBoard().getBoard().get(currentRow_buffer + offset).get(currentCol_buffer).getLetter());
                    if (currentRow_buffer + offset + 1 >= gameInfo.getBoard().getBoard().size()) {
                        break;
                    }

                    offset++;
                }
                d_score = gameInfo.EvaluateWord(d_strow, d_stcol, d_word.toString(), Down);

                if(a_score>=d_score){
                    Log.d("fkasfkwf_a : ", a_score +"  d=  "+d_score+a_word);

                    checkSpell(a_word.toString(),Across,a_strow,a_stcol);
                }
                else {
                    Log.d("fkasfkwf_d : ", a_score +"  d=  "+d_score+d_word);
                    checkSpell(d_word.toString(),Down,d_strow,d_stcol);
                }


            }


            // Across
            // 先找到字的源頭，向左找

            else{
                if (readBTpacket_orien == Across) {
                    while (currentCol_buffer > -1 &&
                            gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer - offset).getLetter() != ' ') {
                        // 邊找初始位置，順便拼出完整字
                        wordbuffer.insert(0, gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer - offset).getLetter());

                        if (currentCol_buffer - offset - 1 < 0){
                            break;
                        }
                        offset++;
                    }
                    if (currentCol_buffer - offset == 0 && gameInfo.getBoard().getBoard().get(currentRow_buffer).get(0).getLetter() != ' ')
                        currentCol_buffer = 0;

                    else
                        currentCol_buffer = currentCol_buffer - offset + 1;
                    Log.d("across", currentCol_buffer + "," + offset);
                    stRow = currentRow_buffer;
                    stCol = currentCol_buffer;
                    // 再拼湊出整個字，向右拼
                    currentRow_buffer = readBTpacket_currentRow;
                    currentCol_buffer = readBTpacket_currentCol;
                    offset = 1;
                    while (currentCol_buffer+offset<gameInfo.getBoard().getBoard().size() && gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer + offset).getLetter() != ' ') {
                        wordbuffer.append(gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer + offset).getLetter());
                        if (currentCol_buffer + offset + 1 >= gameInfo.getBoard().getBoard().size())
                            break;
                        offset++;
                    }

                    checkSpell(wordbuffer.toString(), Across, stRow, stCol);
                } else if (readBTpacket_orien == Down) {

                    // Down
                    // 先找到字的源頭，向上找
                    while (currentRow_buffer > -1 &&
                            gameInfo.getBoard().getBoard().get(currentRow_buffer - offset).get(currentCol_buffer).getLetter() != ' ') {
                        // 邊找初始位置，順便拼出完整字
                        wordbuffer.insert(0, gameInfo.getBoard().getBoard().get(currentRow_buffer - offset).get(currentCol_buffer).getLetter());

                        if (currentRow_buffer - offset - 1 < 0)
                            break;
                        offset++;
                    }
                    if (currentRow_buffer - offset == 0 && gameInfo.getBoard().getBoard().get(0).get(currentCol_buffer).getLetter() != ' ')
                        currentRow_buffer = 0;

                    else
                        currentRow_buffer = currentRow_buffer - offset + 1;


                    stRow = currentRow_buffer;
                    stCol = currentCol_buffer;
                    // 再拼湊出整個字，向下拼
                    currentRow_buffer = readBTpacket_currentRow;
                    currentCol_buffer = readBTpacket_currentCol;
                    offset = 1;


                    while (currentRow_buffer+offset<gameInfo.getBoard().getBoard().size() && gameInfo.getBoard().getBoard().get(currentRow_buffer + offset).get(currentCol_buffer).getLetter() != ' ') {
                        wordbuffer.append(gameInfo.getBoard().getBoard().get(currentRow_buffer + offset).get(currentCol_buffer).getLetter());
                        if (currentRow_buffer + offset + 1 >= gameInfo.getBoard().getBoard().size())
                            break;

                        offset++;
                    }

                    checkSpell(wordbuffer.toString(), Down, stRow, stCol);
                }

            }
        });
        ArrayList<String> autogen = new ArrayList<>();


        autogen.add("#100P$");
        autogen.add("#101R$");
        autogen.add("#102E$");
        autogen.add("#103I$");
        autogen.add("#104S$");
        autogen.add("#105T$");
        autogen.add("#106U$");

        autogen.add("#110A$");
        autogen.add("#111E$");
        autogen.add("#112G$");
        autogen.add("#113Y$");
        autogen.add("#114H$");
        autogen.add("#115Q$");
        autogen.add("#116O$");

        autogen.add("#041U$");
        autogen.add("#042N$");
        autogen.add("#043E$");
        autogen.add("#044T$");

        autogen.add("#043I$");

        autogen.add("#110A$");
        autogen.add("#031P$");
        autogen.add("#110B$");
        autogen.add("#051R$");
        autogen.add("#102*$");
        autogen.add("#061E$");


        autogen.add("#032R$");
        autogen.add("#033I$");
        autogen.add("#034V$");
        autogen.add("#035A$");
        autogen.add("#036T$");
        autogen.add("#037E$");

        autogen.add("#100*$");
        autogen.add("#101*$");
        autogen.add("#102*$");
        autogen.add("#014S$");

        btn_generate.setOnClickListener(v -> {

            String add = autogen.get(0);
            readBTPacket_func(add);
            autogen.remove(0);

        });

        btn_bag.setOnClickListener(v -> {
            showHistoryWord();
//                    String add = autogen.get(0);
//                    readBTPacket_func(add);
//                    autogen.remove(0);

        }
        );

        btn_pass.setOnClickListener(v -> {
            if(gameInfo.getRound() == round_player){

                if(gameInfo.isFirstshot())
                    gameInfo.setFirstshot(false);

                playerChange();
                ComputerGo(Level);
            }
        });

        btn_shuffle.setOnClickListener(v -> {
            Collections.shuffle(HandGridList);
            Handrecycview.setAdapter(Handadapter_recyc);
            Handrecycview.setLayoutAnimation(HandRackAnimation());

        });

        connectBL();
        btn_generate.setVisibility(View.GONE);

    }
    private void setProgressMax(ProgressBar pb, int max) {
        pb.setMax(max * 100);
    }

    private void setProgressAnimate(ProgressBar pb, int progressTo)
    {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), progressTo * 100);
        animation.setDuration(10000);
//        animation.setAutoCancel(true);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }



    @Override
    protected void onPause() {
        super.onPause();
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("gameInfo",MODE_PRIVATE);
        sharedPreferences.edit()
                .putString("gameInfoJson",gameInfo.getGameInfoJson())
                .apply();

        try {
            connectedThread.mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish(); // finish playActivity
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        intent.putExtra("store_game",false);
//        intent.putExtra("cancel_game",false);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
        PlayActivity.instance.mustDie(this);
    }

    //     connect bluetooth device (only hc-05)
    private void connectBL(){
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice hc05=bluetoothAdapter.getRemoteDevice("98:D3:31:FD:5A:82");

        BluetoothSocket bluetoothSocket = null;

        do{
            try {
                bluetoothSocket = hc05.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }while (!Objects.requireNonNull(bluetoothSocket).isConnected());


        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();

    }

    private void ReadFile(){

        InputStream is = getResources().openRawResource(R.raw.scrabbledic);

        readInputStream(is,dictionaries.getListD());
        Log.d("aodkaofw",dictionaries.getListD().get(0));

        //sequenceInputStream 的話第二個檔案的第一列要空白，才會達到想要的效果
        InputStream is_c1 = getResources().openRawResource(R.raw.c1);
        readInputStream(is_c1,dictionaries.getListc1());

        InputStream is_b2 = getResources().openRawResource(R.raw.b2);
        readInputStream(is_b2,dictionaries.getListb2());

        InputStream is_b1 = getResources().openRawResource(R.raw.b1);
        readInputStream(is_b1,dictionaries.getListb1());

        InputStream is_a2 = getResources().openRawResource(R.raw.a2);
        readInputStream(is_a2,dictionaries.getLista2());

        InputStream is_a1 = getResources().openRawResource(R.raw.a1);
        readInputStream(is_a1,dictionaries.getLista1());

    }


    private void readInputStream(InputStream inputStream, ArrayList<String> list){
        String data;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        if(inputStream!=null){
            try{
                while ((data = reader.readLine())!=null){
                    list.add(data.toUpperCase());
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void ComputerGo(String Level){
        HashSet<String> wordlist;
        String word;


        wordlist = moveGenerator.getWords(gameInfo.getBoard().getBoard(), gameInfo.getRack_comp().getRackChar(),false,0,0,false);
        Log.d("asdqdqd",wordlist.toString());
        word = findTheWorthest(wordlist,Level);
        if(word.equals("isEmpty")) {//如果沒有結果的話，隨機取三張牌換牌
            if (gameInfo.getRemainTile() < 3) {
                playerChange();
            } else{
                ArrayList<Integer> list = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    list.add(i);
                }
                Collections.shuffle(list);

                writeBTpacket_bluetooth_message = "~~10" + list.get(0) + "-10" + list.get(1) + "-10" + list.get(2) + "$";//提供三個位置
                connectedThread.write(writeBTpacket_bluetooth_message.getBytes()); //藍芽傳值
                //螢幕要顯示換牌
                DrawView();

            }
        }
        else{
            String[] w = word.split(":");
            boolean dir;
            Log.d("sdadod",word);
            if (w[3].equals("A")) {
                dir = Across;
            } else {
                dir = Down;
            }
            readBTpacket_orien = dir;
            gameInfo.addGameWordlist(w[0]);
            setBoardArray(Integer.parseInt(w[1]), Integer.parseInt(w[2]), dir, w[0]);//把剛剛要放的單字放上去


        }




    }



    public void InitialBoard(){    // initialize board

        for(int row=0;row<gameInfo.getBoard().getBoard().size();row++){
            for (int col=0;col<gameInfo.getBoard().getBoard().size();col++){
                BoardGridList.add(0);
            }
        }
        for(int i=0;i<gameInfo.getRack_player().getRack().size();i++){
            HandGridList.add(0);
            CompGridList.add(0);
        }

        ScanComp();
        ScanBoard();
        ScanHand();
    }

    public void setBoardArray(int strow, int stcol, boolean orient, String word){
        char[] cword = word.toCharArray();
        writeBTpacket_bluetooth_message="";
        gameInfo.getComp_put_buffer().clear(); // 用來之後判斷玩家幫電腦放完牌了沒
        ArrayList<Character> rack_comp_char_buffer = new ArrayList<>(gameInfo.getRack_comp().getRackChar());
        for (int k=0;k<cword.length;k++){

            //right
            if(orient==Across){
//                gameInfo.getBoard().getBoard().get(strow).get(stcol+k).setLetter(cword[k]);
                if(!gameInfo.getBoard().getBoard().get(strow).get(stcol+k).isAlreadyOnBoard()) {


                    // 如果虛擬玩家要用空白牌出牌的話，就要找空白牌的位置
                    if(!rack_comp_char_buffer.contains(cword[k])) {
                        gameInfo.setComp_blank_buffer(cword[k]); // 存comp rack 空白牌要代表的字
                        gameInfo.getComp_put_buffer().add("#0" + strow + (stcol+k) + "-" + "$");
                        writeBTpacket_bluetooth_message += "##0" + strow + (stcol + k) + "-10" + gameInfo.getRack_comp().getRackChar().indexOf('-') + "$"; //#086-105$
                    }
                    else {
                        gameInfo.getComp_put_buffer().add("#0" + strow + (stcol+k) + cword[k] + "$");
                        writeBTpacket_bluetooth_message += "##0" + strow + (stcol + k) + "-10" + rack_comp_char_buffer.indexOf(cword[k]) + "$"; //#086-105$
                        rack_comp_char_buffer.set(rack_comp_char_buffer.indexOf(cword[k]),'-');
                    }

                }
            }
            //down
            else if(orient==Down){
//                gameInfo.getBoard().getBoard().get(strow+k).get(stcol).setLetter(cword[k]);
                if(!gameInfo.getBoard().getBoard().get(strow+k).get(stcol).isAlreadyOnBoard()) {

                    // 如果虛擬玩家要用空白牌出牌的話，就要找空白牌的位置
                    if(!rack_comp_char_buffer.contains(cword[k])) {
                        gameInfo.setComp_blank_buffer(cword[k]);
                        gameInfo.getComp_put_buffer().add("#0" + (strow+k) + stcol + "-" + "$");
                        writeBTpacket_bluetooth_message += "##0" + (strow + k) + stcol + "-10" + gameInfo.getRack_comp().getRackChar().indexOf('-') + "$"; //#086-105$
                    }
                    else {
                        gameInfo.getComp_put_buffer().add("#0" + (strow+k) + stcol + cword[k] + "$");
                        writeBTpacket_bluetooth_message += "##0" + (strow + k) + stcol + "-10" + rack_comp_char_buffer.indexOf(cword[k]) + "$"; //#086-105$
                        rack_comp_char_buffer.set(rack_comp_char_buffer.indexOf(cword[k]),'-');
                    }

                }
            }

        }
        gameInfo.updateAlreadyOnBoard();
        Log.d("timeingtest",writeBTpacket_bluetooth_message);
        connectedThread.write(writeBTpacket_bluetooth_message.getBytes()); //藍芽傳值

    }

    private void ScanBoard(){    //scan board
        for(int row=0;row<gameInfo.getBoard().getBoard().size();row++){
            for (int col=0;col<gameInfo.getBoard().getBoard().size();col++){
                char tile;
                tile = gameInfo.getBoard().getBoard().get(row).get(col).getLetter();

                putTile(row,col,tile);
            }
        }
    }

    private void putTile(int row, int col, char tile){
        int index = (row * 9) + col;
        if(tile==' '){
            BoardGridList.set(index,0);
        }
        else {
            BoardGridList.set(index, chooseAlphabet(tile));
        }
        Boardrecycview.setAdapter(Boardadapter_recyc);
    }


    private void ScanHand(){    //scan hand
        for(int i=0;i<gameInfo.getRack_player().getRack().size();i++){
            char tile;
            tile = gameInfo.getRack_player().getRack().get(i).getLetter();
            Handtile(i,tile);
        }
    }

    private void Handtile(int index, char tile){
        if(tile==' '){
            HandGridList.set(index,0);
        }
        else {
            HandGridList.set(index, chooseAlphabet(tile));
        }
        Handrecycview.setAdapter(Handadapter_recyc);
    }

    private void ScanComp(){    //scan Comp
        for(int i=0;i<gameInfo.getRack_comp().getRack().size();i++){
            char tile;
            tile = gameInfo.getRack_comp().getRack().get(i).getLetter();
            Comptile(i,tile);
        }
    }

    private void Comptile(int index, char tile){
        if(tile==' '){
            CompGridList.set(index,0);
        }
        else {
            CompGridList.set(index, chooseAlphabet(tile));
        }
        Comprecycview.setAdapter(Compadapter_recyc);
    }

    private int chooseAlphabet(char tile){
        switch(tile){
            case 'A':return R.drawable.ic_a;
            case 'B':return R.drawable.ic_b;
            case 'C':return R.drawable.ic_c;
            case 'D':return R.drawable.ic_d;
            case 'E':return R.drawable.ic_e;
            case 'F':return R.drawable.ic_f;
            case 'G':return R.drawable.ic_g;
            case 'H':return R.drawable.ic_h;
            case 'I':return R.drawable.ic_i;
            case 'J':return R.drawable.ic_j;
            case 'K':return R.drawable.ic_k;
            case 'L':return R.drawable.ic_l;
            case 'M':return R.drawable.ic_m;
            case 'N':return R.drawable.ic_n;
            case 'O':return R.drawable.ic_o;
            case 'P':return R.drawable.ic_p;
            case 'Q':return R.drawable.ic_q;
            case 'R':return R.drawable.ic_r;
            case 'S':return R.drawable.ic_s;
            case 'T':return R.drawable.ic_t;
            case 'U':return R.drawable.ic_u;
            case 'V':return R.drawable.ic_v;
            case 'W':return R.drawable.ic_w;
            case 'X':return R.drawable.ic_x;
            case 'Y':return R.drawable.ic_y;
            case 'Z':return R.drawable.ic_z;
            case '-':return R.drawable.ic_blank;
            default:return 0;
        }
    }



    private void readBTPacket_func(String btpacket){

        char[] btpacketc = btpacket.toCharArray();
        if((int) btpacketc[2] - '0'<0 || (int) btpacketc[3] - '0'<0 ||
                (int) btpacketc[2] - '0'>8 || (int) btpacketc[3] - '0'>8){
            return;
        }


        switch (btpacketc[1]) {
            case '0': // on board
                // 已經放在場上的就直接lock住，不會再動到了
                if(gameInfo.getBoard().getBoard().get((int) btpacketc[2] - '0').get((int) btpacketc[3] - '0').isAlreadyOnBoard()){
                    break;
                }

                // 如果是玩家回合，空白牌放到board上時要設定該牌
                if(btpacketc[4]=='-' && gameInfo.getRound()==round_player){
                    //問玩家是要放什麼牌
                    blankTileView(btpacketc);
                }
                else if(btpacketc[4]=='-' && gameInfo.getRound()==round_comp){
                    //放牌進陣列 然後顯示在螢幕上
                    gameInfo.getBoard().getBoard().get((int) btpacketc[2] - '0').get((int) btpacketc[3] - '0').setLetter(gameInfo.getComp_blank_buffer());
                    gameInfo.getBoard().getBoard().get((int) btpacketc[2] - '0').get((int) btpacketc[3] - '0').setScore(0);
                    ScanBoard();
                }
                else {
                    //放牌進陣列 然後顯示在螢幕上
                    gameInfo.getBoard().getBoard().get((int) btpacketc[2] - '0').get((int) btpacketc[3] - '0').setLetter(btpacketc[4]);
                    ScanBoard();

                    if(gameInfo.getRound()==round_player&& btpacketc[4]=='*') {


                        gameInfo.getPlayer_rack_buffer().remove(new Character(gameInfo.getBoard().getBoard().get((int) btpacketc[2] - '0').get((int) btpacketc[3] - '0').getLetter()));
                        boolean hasNewTileOnBoard = false;
                        for(int row=0;row<gameInfo.getBoard().getBoard().size();row++){
                            for(int col=0;col<gameInfo.getBoard().getBoard().size();col++){
                                if(gameInfo.getBoard().getBoard().get(row).get(col).getLetter()!=' '&&!gameInfo.getBoard().getBoard().get(row).get(col).isAlreadyOnBoard()) {
                                    Log.d("elfor,vl","gettout "+row+" "+col);
                                    readBTpacket_currentRow = row;
                                    readBTpacket_currentCol = col;
                                    hasNewTileOnBoard =true;
                                }

                            }
                        }
                        if(!hasNewTileOnBoard)
                            txt_score.setVisibility(View.GONE);

                        show_current_score();
                    }
                    if(gameInfo.getRound()==round_comp&& btpacketc[4]=='*') {

                        gameInfo.getPlayer_rack_buffer().remove(new Character(gameInfo.getBoard().getBoard().get((int) btpacketc[2] - '0').get((int) btpacketc[3] - '0').getLetter()));

                    }


                    // 玩家放牌的位置，只存最後位置，待會要以最後位置去向左/像上延伸，去找到字的起始點
                    if (gameInfo.getRound() == round_player) {

                        if(btpacketc[4]!='*'){
                            gameInfo.getPlayer_rack_buffer().add(btpacketc[4]);

                            readBTpacket_lastRow = readBTpacket_currentRow;
                            readBTpacket_lastCol = readBTpacket_currentCol;

                            readBTpacket_currentRow = (int) btpacketc[2] - '0';
                            readBTpacket_currentCol = (int) btpacketc[3] - '0';

                            // 如果一直傳重複點的位置來，就不更新方向判斷。
                            if(readBTpacket_lastRow==readBTpacket_currentRow && readBTpacket_currentCol==readBTpacket_lastCol && tile_buffer == btpacketc[4])
                                break;

                            tile_buffer = btpacketc[4];
                            Log.d("sdadowaf",gameInfo.getPlayer_rack_buffer().toString());


                            if(gameInfo.getPlayer_rack_buffer().size()==1){
                                show_current_score();
                            }
                            else if(readBTpacket_lastRow==readBTpacket_currentRow && readBTpacket_lastCol!=readBTpacket_currentCol){
                                readBTpacket_orien=Across;
                                show_current_score();
                            }

                            else if(readBTpacket_lastCol==readBTpacket_currentCol && readBTpacket_lastRow!=readBTpacket_currentRow) {
                                readBTpacket_orien = Down;
                                show_current_score();
                            }

                        }

                    }
                    else if(gameInfo.getRound() == round_comp){
                        if(btpacketc[4]!='*'){
                            gameInfo.getPlayer_rack_buffer().add(btpacketc[4]);

                            readBTpacket_lastRow = readBTpacket_currentRow;
                            readBTpacket_lastCol = readBTpacket_currentCol;

                            readBTpacket_currentRow = (int) btpacketc[2] - '0';
                            readBTpacket_currentCol = (int) btpacketc[3] - '0';
                            Log.d("sdadowaf",gameInfo.getPlayer_rack_buffer().toString());
                            if(gameInfo.getPlayer_rack_buffer().size()==1){
                                show_current_score();
                            }

                            else if(readBTpacket_lastRow==readBTpacket_currentRow && readBTpacket_lastCol!=readBTpacket_currentCol){
                                readBTpacket_orien=Across;
                                show_current_score();
                            }

                            else if(readBTpacket_lastCol==readBTpacket_currentCol && readBTpacket_lastRow!=readBTpacket_currentRow) {
                                readBTpacket_orien = Down;
                                show_current_score();
                            }

                        }
                    }
                    Log.d("playerrackbuffer", gameInfo.getPlayer_rack_buffer().toString());
                }

                if(gameInfo.getRound()==round_comp){
                    if(gameInfo.getComp_put_buffer().contains(btpacket)){
                        gameInfo.getBoard().getBoard().get((int) btpacketc[2] - '0').get((int) btpacketc[3] - '0').setAlreadyOnBoard(true);
                        gameInfo.getComp_put_buffer().remove(btpacket);
                    }

                    Log.d("comp_put:", gameInfo.getComp_put_buffer().toString());
                    if(gameInfo.getComp_put_buffer().isEmpty()){
                        gameInfo.getComp().addScore(currentWordScore);
                        gameInfo.getPlayer_rack_buffer().clear();
                        writeBTpacket_bluetooth_message = "^^";
                        connectedThread.write(writeBTpacket_bluetooth_message.getBytes());
                        playerChange();
                    }
                }

                break;

            case '1': // on hand
                switch (btpacketc[2]) {
                    case '0': // computer
                        writeBTpacket_bluetooth_message = "@"+btpacketc[1]+btpacketc[2]+btpacketc[3]+btpacketc[4]+"$";
                        gameInfo.getRack_comp().getRack().get((int) btpacketc[3] - '0').setLetter(btpacketc[4]);
                        ScanComp();
                        break;
                    case '1': // hand

                        if(isHintMode)
                            updateHintWords();

                        gameInfo.getRack_player().getRack().get((int) btpacketc[3] - '0').setLetter(btpacketc[4]);
                        ScanHand();

                        break;
                    default:
                        break;
                }
                break;
            default:
                break;


        }
        return;
    }

    public void mustDie(Object object) {
        if (refWatcher != null){
            refWatcher.watch(object);
        }
    }
    // bluetooth
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

//             Get the input and output streams, using temp objects because
//             member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) { }
            mmInStream=tmpIn;
            mmOutStream=tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    //解決了讀怪怪的問題！
//                    sleep(50);

                    bytes = mmInStream.available();

                    if (bytes>0){
                        // Read from the InputStream
                        bytes = mmInStream.read(buffer);
//                        txt_comp.setText(String.valueOf(bytes));
                        Log.d("oafjofkwpacket",String.valueOf(bytes));
                        // Send the obtained bytes to the UI Activity
                        // buffer.clone 也是要解決讀取的問題的
                        byte[] buffer_b = new byte[1024];  // buffer store for the stream
                        System.arraycopy(buffer,0,buffer_b,0,bytes);
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer_b)
                                .sendToTarget();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }}
        /* Call this from the main activity to send data to the remote device */

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException ignored) { }
        }

        /* Call this from the main activity to shutdown the connection */

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException ignored) { }
        }
    }

    /*將byte[] ASCII 轉為字串的方法
     */
    public static String ascii2String(byte[] in) {
        final StringBuilder stringBuilder = new StringBuilder(in.length);
        for (byte byteChar : in)
            stringBuilder.append(String.format("%02X ", byteChar));
        String output = null;
        try {
            output = new String(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return output;
    }

    private String findTheWorthest(HashSet<String> oriWordList, String Level){

        if(oriWordList.isEmpty()){
            return "isEmpty";
        }

        ArrayList<String> newWordList = dictionaries.LevelFilter(Level,oriWordList,true);

        if(newWordList.isEmpty()){
            return "isEmpty";
        }

        String WorthestWord="";
        int lastscore=0;

        for(String s:newWordList){
            Log.d("EVALUATERRR",s);
            boolean orient;

            if(s.split(":")[3].equals("A"))
                orient = Across;
            else
                orient =Down;

            int score = gameInfo.EvaluateWord(Integer.parseInt(s.split(":")[1]),Integer.parseInt(s.split(":")[2]),s.split(":")[0],orient);
            if(score>lastscore){
                WorthestWord = s;
                lastscore=score;
            }
        }
        return WorthestWord;
    }

    private void showHistoryWord(){

        View view = LayoutInflater.from(this).inflate(R.layout.history_word_view,null);
        PopupWindow popupWindow = new PopupWindow(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(this.getResources().getDisplayMetrics().heightPixels-llbutton.getHeight()-frame_board.getTop());
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY,0, frame_board.getTop());
        dimBehind(popupWindow);
        popupWindow.setOutsideTouchable(true);

        RecyclerView recyclerView = view.findViewById(R.id.recyc_historyWord);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));//分隔線
        historywordAdapter = new RecycAdapter_historyword(this,gameInfo.getGameWordlist());
        recyclerView.setAdapter(historywordAdapter);

    }



    @SuppressLint("ClickableViewAccessibility")
    private void showMenu(){


        View view = LayoutInflater.from(this).inflate(R.layout.menu_view,null);

        List<Menu_Data> data = new ArrayList<>();
        data.add(new Menu_Data("Cancel Game", R.drawable.ic_baseline_emoji_flags_24));
        data.add(new Menu_Data("End Game", R.drawable.ic_baseline_save_alt_24));
        PopupWindow popupWindow = new PopupWindow(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(700);
//        popupWindow.setHeight();
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY,0, this.getResources().getDisplayMetrics().heightPixels-llbutton.getHeight()-(700));

        dimBehind(popupWindow);

        RecyclerView recyclerView = view.findViewById(R.id.recyc_menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));//分隔線
        RecycAdapter_menu adapter = new RecycAdapter_menu(data, getApplication());
        recyclerView.setAdapter(adapter);

        ConstraintLayout cons_hint = view.findViewById(R.id.cons_hint);
        cons_hint.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            showHint();
            return false;
        });
    }

    private void showHint(){

        isHintMode = true;


        popupWindow_hint.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow_hint.setHeight(this.getResources().getDisplayMetrics().heightPixels-llbutton.getHeight()-frame_board.getTop());
        popupWindow_hint.showAtLocation(view_hint, Gravity.CENTER,0, 0);

        dimBehind(popupWindow_hint);

        updateHintWords();


    }

    private void updateHintWords(){

        // 取得所有可能，然後存著trie的方式
        HashSet<String> wordlist = moveGenerator.getWords(gameInfo.getBoard().getBoard(), gameInfo.getRack_player().getRackChar(),false,0,0,false);

        // 把現在放在手牌區的牌存下來
        HashSet<String> all = new HashSet<>();
        for(String s:wordlist){
//            wordtrie.add(s.split(":")[0]);
            all.add(s.split(":")[0]);
        }
        ArrayList<String> output= dictionaries.LevelFilter(Level,all,false);
        if(output.isEmpty())
            output.addAll(all);

        txt_hintword.setText(output.toString().replace("[","").replace("]","").replace(","," , "));

    }


    // popupwindow 之外背景變暗
    public static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
    }


    private void DrawView(){
        playerChange();

        View mview = getLayoutInflater().inflate(R.layout.dialog_comp_change,null);
        PopupWindow popupWindow = new PopupWindow(mview);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(mview, Gravity.CENTER,0, 0);
        dimBehind(popupWindow);
        Button btn_ok = mview.findViewById(R.id.cross_ok);
        btn_ok.setOnClickListener(v -> popupWindow.dismiss());

    }

    private void MisspelledView(){

        View mview = getLayoutInflater().inflate(R.layout.dialog_player_misspelled,null);
        PopupWindow popupWindow = new PopupWindow(mview);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(mview, Gravity.CENTER,0, 0);
        dimBehind(popupWindow);
        Button btn_ok = mview.findViewById(R.id.cross_ok);
        btn_ok.setOnClickListener(v -> popupWindow.dismiss());
    }

    @SuppressLint("SetTextI18n")
    private void BoardCheckView(HashSet<String> wrong_w) {

        View mview = getLayoutInflater().inflate(R.layout.dialog_player_cross_wrong, null);
        TextView textView = mview.findViewById(R.id.txt_wrong_cross);
        textView.setText("There are something wrong with the adjacent tiles !\n"+wrong_w.toString().
                replace("[","").replace("]","")+" cannot form a word.");
        PopupWindow popupWindow = new PopupWindow(mview);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(mview, Gravity.CENTER,0, 0);
        dimBehind(popupWindow);
        Button btn_ok = mview.findViewById(R.id.cross_ok);
        btn_ok.setOnClickListener(v -> popupWindow.dismiss());
    }

    private void GivePossiWordView(HashSet<String> possiword){

        View mview = getLayoutInflater().inflate(R.layout.dialog_player_possiword,null);
        TextView textView = mview.findViewById(R.id.txt_possi_word);
        textView.setText(possiword.toString().
                replace("[","").replace("]","").replace(","," , "));
        PopupWindow popupWindow = new PopupWindow(mview);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(mview, Gravity.CENTER,0, 0);
        dimBehind(popupWindow);
        Button btn_ok = mview.findViewById(R.id.possi_ok);
        btn_ok.setOnClickListener(v -> popupWindow.dismiss());
    }

    private void blankTileView(char[] btpacketc){

        View mview = getLayoutInflater().inflate(R.layout.dialog_tile_blank,null);
        EditText editText = mview.findViewById(R.id.edit_tileblank);
        editText.setFilters(new InputFilter[]
                {new InputFilter.AllCaps(),
                        new InputFilter.LengthFilter(1)}); // only uppercase


        PopupWindow popupWindow = new PopupWindow(mview);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);


        popupWindow.showAtLocation(mview, Gravity.CENTER,0, 0);
        dimBehind(popupWindow);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);


        Button btn_ok = mview.findViewById(R.id.blank_save);
        btn_ok.setOnClickListener(v -> {
            btpacketc[4] =  editText.getText().toString().toCharArray()[0];

            // 放牌進陣列 然後顯示在螢幕上
            gameInfo.getBoard().getBoard().get((int) btpacketc[2] - '0').get((int) btpacketc[3] - '0').setLetter(btpacketc[4]);
            gameInfo.getBoard().getBoard().get((int) btpacketc[2] - '0').get((int) btpacketc[3] - '0').setScore(0);
            ScanBoard();
            // 玩家放牌的位置，只存最後位置，待會要以最後位置去向左/像上延伸，去找到字的起始點

            gameInfo.getPlayer_rack_buffer().add(btpacketc[4]);
            readBTpacket_lastRow = readBTpacket_currentRow;
            readBTpacket_lastCol = readBTpacket_currentCol;

            readBTpacket_currentRow = (int) btpacketc[2] - '0';
            readBTpacket_currentCol = (int) btpacketc[3] - '0';

            if(gameInfo.getPlayer_rack_buffer().size()==1){
                show_current_score();
            }

            else if (readBTpacket_lastRow == readBTpacket_currentRow && readBTpacket_lastCol!=readBTpacket_currentCol) {
                readBTpacket_orien = Across;
                show_current_score();
            } else if (readBTpacket_lastCol == readBTpacket_currentCol && readBTpacket_lastRow != readBTpacket_currentRow) {
                readBTpacket_orien = Down;
                show_current_score();
            }
            popupWindow.dismiss();
        });

    }

    // 這個是給洗牌用的
    private LayoutAnimationController HandRackAnimation(){
        AnimationSet set = new AnimationSet(true);
        Animation animation;
        animation = new RotateAnimation(
                -20,0,50f,50f

        );
        animation.setDuration(50);
        set.addAnimation(animation);


        return new LayoutAnimationController(set, 0.5f);
    }

    private void playerChange(){
        if(!gameInfo.isFirstshot() && gameInfo.getComp_put_buffer().isEmpty())
            gameInfo.setRound(!gameInfo.getRound());
        if(gameInfo.getRound() == round_player) {
            txt_player.setTextColor(Color.rgb(0,0,0));
            txt_comp.setTextColor(Color.rgb(190,190,190));
        }

        else if(gameInfo.getRound() == round_comp) {
            txt_player.setTextColor(Color.rgb(190,190,190));
            txt_comp.setTextColor(Color.rgb(0,0,0));
        }
        txt_player.setText(String.valueOf(gameInfo.getPlayer().getScore()));
        txt_comp.setText(String.valueOf(gameInfo.getComp().getScore()));
    }

    private void dynamicShowScore(int index, int value){
        ViewTreeObserver observer = txt_score.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(() -> {
            float x=0;
            float y=0;
            if(Boardrecycview.getChildAt(index)!=null) {
                x = (Boardrecycview.getChildAt(index).getLeft()+
                        ((Boardrecycview.getChildAt(index).getRight() -
                                Boardrecycview.getChildAt(index).getLeft()) / 2)  );
                //- txt_score.getWidth() / 2

                if(index<71 && Boardrecycview.getChildAt(index+9)!=null){
                    y = (Boardrecycview.getChildAt(index).getTop()+
                            ((Boardrecycview.getChildAt(index+9).getBottom()-
                                    Boardrecycview.getChildAt(index).getTop())/2)
                    );
                    //-txt_score.getHeight()/2
                }
                else if(Boardrecycview.getChildAt(index-9)!=null){
                    y=Boardrecycview.getChildAt(index-9).getTop()+
                            ((Boardrecycview.getChildAt(index).getBottom()-
                                    Boardrecycview.getChildAt(index-9).getTop())/2)
                    ;
                    //-txt_score.getHeight()/2
                }
            }
            txt_score.setX(x);
            txt_score.setY(y);

        });
        txt_score.setText(String.valueOf(value));
        txt_score.setVisibility(View.VISIBLE);
    }

    private void checkSpell(String wordbuffer, boolean orien,int stRow,int stCol){
        Log.d("timeingtest","checkspell");
        // 如果是第一次拼字拼字，就直接看有沒有這個字
        if(gameInfo.isFirstshot()){
            HashSet<String> wordlist_gen = spellingChecker.SpellingCheck(wordbuffer,stRow,stCol,orien,gameInfo);
            Log.d("timeingtest","knowlist");
            if(wordlist_gen.contains("isCorrect")){ //成功拼字

                gameInfo.addGameWordlist(wordbuffer);

                gameInfo.updateAlreadyOnBoard();

                gameInfo.setFirstshot(false);
                ScanHand();
                gameInfo.getPlayer_rack_buffer().clear();
                txt_score.setVisibility(View.INVISIBLE);
                gameInfo.getPlayer().addScore(currentWordScore);
                playerChange();
                ComputerGo(Level);
            }

            else{ // 拼字失敗，這邊設定一定要把牌全部拿起來

                if(wordlist_gen.contains("isCross")){
                    wordlist_gen.remove("isCross");
                    BoardCheckView(wordlist_gen);
                }
                else {
                    GivePossiWordView(wordlist_gen);
                }
            }

        }
        else {
            //要用一個是否成功拼字，輸入是wordbuffer

            // 取得目前可拼的字

            HashSet<String> wordlist_gen = spellingChecker.SpellingCheck(wordbuffer,stRow,stCol,orien,gameInfo);
            Log.d("timeingtest","knowlist");
            if (wordlist_gen.contains("isCorrect")) { //成功拼字

                gameInfo.addGameWordlist(wordbuffer);

                gameInfo.updateAlreadyOnBoard();

                ScanHand();
                gameInfo.getPlayer_rack_buffer().clear();
                txt_score.setVisibility(View.INVISIBLE);
                gameInfo.getPlayer().addScore(currentWordScore);
                playerChange();
                ComputerGo(Level);
            } else { // 拼字失敗

                if(wordlist_gen.contains("isCross")){
                    wordlist_gen.remove("isCross");
                    BoardCheckView(wordlist_gen);
                }
                else {
                    GivePossiWordView(wordlist_gen);
                }
            }
        }
    }



    private void show_current_score(){
        if(gameInfo.getRound()==round_player && gameInfo.getPlayer_rack_buffer().isEmpty()){
            txt_score.setVisibility(View.GONE);
            return;
        }
        Log.d("showsss",String.valueOf(readBTpacket_currentRow)+readBTpacket_currentCol+readBTpacket_orien);
        StringBuilder wordbuffer  = new StringBuilder();// wordbuffer
        int currentRow_buffer = readBTpacket_currentRow;
        int currentCol_buffer = readBTpacket_currentCol;
        int strow;
        int stcol;
        currentWordScore = 0;
        int offset = 0;
        // 如果放到版子上的牌只有一張的話會不知道到底是幾分，也不知道方向，所以兩個都找取高的
        if(gameInfo.getPlayer_rack_buffer().size()<2){
            int a_strow;
            int a_stcol;
            int a_row_buffer;
            int a_col_buffer;
            int a_score;
            StringBuilder a_word= new StringBuilder();
            int d_strow;
            int d_stcol;
            int d_row_buffer;
            int d_col_buffer;
            int d_score;
            StringBuilder d_word= new StringBuilder();



            // 先找across
            while (currentCol_buffer > -1 &&
                    gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer - offset).getLetter() != ' ') {
                // 邊找初始位置，順便拼出完整字
                a_word.insert(0, gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer - offset).getLetter());

                if (currentCol_buffer - offset - 1 < 0)
                    break;
                offset++;
            }
            if (currentCol_buffer - offset == 0 && gameInfo.getBoard().getBoard().get(currentRow_buffer).get(0).getLetter() != ' ')
                currentCol_buffer = 0;

            else
                currentCol_buffer = currentCol_buffer - offset + 1;
            Log.d("across", currentCol_buffer + "," + offset);
            a_strow = currentRow_buffer;
            a_stcol = currentCol_buffer;
            // 再拼湊出整個字，向右拼
            currentRow_buffer = readBTpacket_currentRow;
            currentCol_buffer = readBTpacket_currentCol;
            offset = 1;
            while (currentCol_buffer+offset<gameInfo.getBoard().getBoard().size() && gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer + offset).getLetter() != ' ') {
                a_word.append(gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer + offset).getLetter());
                if (currentCol_buffer + offset + 1 >= gameInfo.getBoard().getBoard().size())
                    break;
                offset++;
            }
            a_row_buffer = currentRow_buffer;
            a_col_buffer = currentCol_buffer + offset - 1;
            a_score = gameInfo.EvaluateWord(a_strow, a_stcol, a_word.toString(), Across);


            currentRow_buffer = readBTpacket_currentRow;
            currentCol_buffer = readBTpacket_currentCol;
            offset = 0;
            // Down
            // 先找到字的源頭，向上找
            while (currentRow_buffer > -1 &&
                    gameInfo.getBoard().getBoard().get(currentRow_buffer - offset).get(currentCol_buffer).getLetter() != ' ') {
                // 邊找初始位置，順便拼出完整字
                d_word.insert(0, gameInfo.getBoard().getBoard().get(currentRow_buffer - offset).get(currentCol_buffer).getLetter());

                if (currentRow_buffer - offset - 1 < 0)
                    break;
                offset++;
            }
            if (currentRow_buffer - offset == 0 && gameInfo.getBoard().getBoard().get(0).get(currentCol_buffer).getLetter() != ' ')
                currentRow_buffer = 0;

            else
                currentRow_buffer = currentRow_buffer - offset + 1;
            Log.d("Row", currentRow_buffer + "," + offset);

            d_strow = currentRow_buffer;
            d_stcol = currentCol_buffer;
            // 再拼湊出整個字，向下拼
            currentRow_buffer = readBTpacket_currentRow;
            currentCol_buffer = readBTpacket_currentCol;
            offset = 1;

            while (currentRow_buffer+offset<gameInfo.getBoard().getBoard().size() && gameInfo.getBoard().getBoard().get(currentRow_buffer + offset).get(currentCol_buffer).getLetter() != ' ') {
                d_word.append(gameInfo.getBoard().getBoard().get(currentRow_buffer + offset).get(currentCol_buffer).getLetter());
                if (currentRow_buffer + offset + 1 >= gameInfo.getBoard().getBoard().size())
                    break;

                offset++;
            }
            d_row_buffer = currentRow_buffer + offset - 1;
            d_col_buffer = currentCol_buffer;
            d_score = gameInfo.EvaluateWord(d_strow, d_stcol, d_word.toString(), Down);

            if(a_score>=d_score){
                currentWordScore = a_score;
                currentRow_buffer = a_row_buffer;
                currentCol_buffer = a_col_buffer;
                wordbuffer = new StringBuilder(a_word.toString());
            }
            else {
                currentWordScore = d_score;
                currentRow_buffer = d_row_buffer;
                currentCol_buffer = d_col_buffer;
                wordbuffer = new StringBuilder(d_word.toString());
            }
        }




        // 要是出到場上的牌超過一張，就可以知道方向，就可以抓正確分數了
        // 在這邊的時候currentRow currentCol 會代表最左邊 / 最上面
        // Across
        // 先找到字的源頭，向左找
        else {
            if (readBTpacket_orien == Across) {
                while (currentCol_buffer > -1 &&
                        gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer - offset).getLetter() != ' ') {
                    // 邊找初始位置，順便拼出完整字
                    wordbuffer.insert(0, gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer - offset).getLetter());

                    if (currentCol_buffer - offset - 1 < 0){
                        break;
                    }
                    offset++;
                }
                if (currentCol_buffer - offset == 0 && gameInfo.getBoard().getBoard().get(currentRow_buffer).get(0).getLetter() != ' ')
                    currentCol_buffer = 0;

                else
                    currentCol_buffer = currentCol_buffer - offset + 1;
                Log.d("across", currentCol_buffer + "," + offset);
                strow = currentRow_buffer;
                stcol = currentCol_buffer;
                // 再拼湊出整個字，向右拼
                currentRow_buffer = readBTpacket_currentRow;
                currentCol_buffer = readBTpacket_currentCol;
                offset = 1;
                while (currentCol_buffer+offset<gameInfo.getBoard().getBoard().size() && gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer + offset).getLetter() != ' ') {
                    wordbuffer.append(gameInfo.getBoard().getBoard().get(currentRow_buffer).get(currentCol_buffer + offset).getLetter());
                    if (currentCol_buffer + offset + 1 >= gameInfo.getBoard().getBoard().size())
                        break;
                    offset++;
                }
                currentCol_buffer = currentCol_buffer + offset - 1;
                currentWordScore = gameInfo.EvaluateWord(strow, stcol, wordbuffer.toString(), Across);
            } else {
                // Down
                // 先找到字的源頭，向上找
                while (currentRow_buffer > -1 &&
                        gameInfo.getBoard().getBoard().get(currentRow_buffer - offset).get(currentCol_buffer).getLetter() != ' ') {
                    // 邊找初始位置，順便拼出完整字
                    wordbuffer.insert(0, gameInfo.getBoard().getBoard().get(currentRow_buffer - offset).get(currentCol_buffer).getLetter());

                    if (currentRow_buffer - offset - 1 < 0)
                        break;
                    offset++;
                }
                if (currentRow_buffer - offset == 0 && gameInfo.getBoard().getBoard().get(0).get(currentCol_buffer).getLetter() != ' ')
                    currentRow_buffer = 0;

                else
                    currentRow_buffer = currentRow_buffer - offset + 1;
                Log.d("Row", currentRow_buffer + "," + offset);

                strow = currentRow_buffer;
                stcol = currentCol_buffer;
                // 再拼湊出整個字，向下拼
                currentRow_buffer = readBTpacket_currentRow;
                currentCol_buffer = readBTpacket_currentCol;
                offset = 1;


                while (currentRow_buffer+offset<gameInfo.getBoard().getBoard().size() && gameInfo.getBoard().getBoard().get(currentRow_buffer + offset).get(currentCol_buffer).getLetter() != ' ') {
                    wordbuffer.append(gameInfo.getBoard().getBoard().get(currentRow_buffer + offset).get(currentCol_buffer).getLetter());
                    if (currentRow_buffer + offset + 1 >= gameInfo.getBoard().getBoard().size())
                        break;

                    offset++;
                }
                currentRow_buffer = currentRow_buffer + offset - 1;
                currentWordScore = gameInfo.EvaluateWord(strow, stcol, wordbuffer.toString(), Down);
            }
        }

        int index = 9*currentRow_buffer+currentCol_buffer;

        if(wordbuffer.length()>1)
            dynamicShowScore(index,currentWordScore);
        else
            txt_score.setVisibility(View.GONE);
    }





}