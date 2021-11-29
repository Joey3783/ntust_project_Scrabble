package GameActivity;

import android.annotation.SuppressLint;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.scrabble.R;
import com.squareup.leakcanary.LeakCanary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import Adapter.RecycAdapter_historyword_with_date;
import RoomDataBase.DataBase;
import RoomDataBase.HistoryWords;
import RoomDataBase.PlayerInfo;
import Spelling.Metaphone3;
import Spelling.SpellingChecker;

public class MainActivity extends AppCompatActivity {

    TextView afterdic;
    TextView txt_main_playerName;
    TextView txt_main_Level;
    DataBase myDatabase;
    private String intent_playerName;
    private String intent_Level;
    private boolean havePlayer = false;
    ArrayList<String> listD = new ArrayList<>();
    View view_his;
    PopupWindow popupWindow_his;
    int duration_button_rotate = 100;
    LinearLayout ll_buttom;
    LinearLayout ll_continue;
    ConstraintLayout CL_toolbar;
    Button btn_StNewGame;
    Button btn_ResumeGame;
    ImageButton btn_main_editLevel;
    ImageButton btn_main_historywords;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(getApplication());
        afterdic = findViewById(R.id.afterdic);
        CL_toolbar = findViewById(R.id.CL_toolbar);
        ll_buttom = findViewById(R.id.LL_buttom);
        ll_continue = findViewById(R.id.LL_continue);
        TextView txt_last_score = findViewById(R.id.txt_last_score);
        TextView txt_turn = findViewById(R.id.txt_turn);
        txt_main_playerName = findViewById(R.id.txt_main_playerName);
        txt_main_Level = findViewById(R.id.txt_main_Level);
        btn_ResumeGame = findViewById(R.id.btn_ResumeGame);
        Button btn = findViewById(R.id.btn_Create);
        btn_StNewGame = findViewById(R.id.btn_StNewGame);
        btn_main_editLevel = findViewById(R.id.btn_main_editLevel);
        btn_main_historywords = findViewById(R.id.btn_main_historywords);
        view_his = LayoutInflater.from(this).inflate(R.layout.history_word_date_view,null);
        popupWindow_his = new PopupWindow(view_his);


        readFile();


        new Thread(() -> {
            List<PlayerInfo> list = DataBase.getInstance(getApplicationContext()).getDataUao().displayAll();
            if (!list.isEmpty()) {
                havePlayer = true;
                intent_playerName = list.get(0).getPlayerName();
                intent_Level = list.get(0).getLevel();
            }
            runOnUiThread(this::popUpEditText);
        }).start();

        Intent intent = this.getIntent();
        boolean store_game = intent.getBooleanExtra("store_game", false);
        boolean cancel_game = intent.getBooleanExtra("cancel_game", false);
        String game_date_time = intent.getStringExtra("current_time");

        String json_sharedPreferences = getSharedPreferences("gameInfo", MODE_PRIVATE).getString("gameInfoJson", "");

        Log.d("oasdkao",String.valueOf(store_game));
        Log.d("oasdkao",String.valueOf(json_sharedPreferences));
        if (!store_game && !json_sharedPreferences.equals("") && !cancel_game) {
            ll_continue.setVisibility(View.VISIBLE);
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(json_sharedPreferences);

                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String Round = jsonObject.getString("Round");
                String PlayerScore = jsonObject.getString("PlayerScore");
                String CompScore = jsonObject.getString("CompScore");


                if(Round.equals("Player")){
                    txt_turn.setText("Your Turn");
                }
                else {
                    txt_turn.setText("Opponent's Turn");
                }
                txt_last_score.setText(PlayerScore+" - "+CompScore);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            ll_continue.setVisibility(View.GONE);
        }



        if(store_game){
            String Wordlist="";
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(json_sharedPreferences);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                Wordlist = jsonObject.getString("WordList");
                Log.d("woraormwwof",Wordlist);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            HistoryWords historyWords = new HistoryWords(game_date_time,Wordlist,1);

            new Thread(() -> DataBase.getInstance(getApplicationContext()).getDataUao().saveHistoryWord(historyWords)).start();
        }


        myDatabase = Room.databaseBuilder(this, DataBase.class, DataBase.DB_NAME).build();

        popupWindow_his.setOnDismissListener(() -> btn_main_historywords.animate().rotation(0).setDuration(duration_button_rotate));


        btn_ResumeGame.setOnClickListener(v -> {
            Intent intent12 = new Intent();
            intent12.setClass(MainActivity.this, PlayActivity.class);
            intent12.putExtra("Name", intent_playerName);
            intent12.putExtra("Level", intent_Level);
            intent12.putExtra("Game",json_sharedPreferences);
            startActivity(intent12);
        });

        btn.setOnClickListener(v -> {



        });

        btn_main_editLevel.setOnClickListener(v -> editLevel());

        btn_main_historywords.setOnClickListener(v -> {
            btn_main_historywords.animate().rotation(-90).setDuration(duration_button_rotate);
            showHistoryWord();
        });

//        /**=======================================================================================*/
//        /*  進入遊戲  */
        btn_StNewGame.setOnClickListener(v -> {

            if(!havePlayer)
                return;
            Intent intent1 = new Intent();
            intent1.setClass(MainActivity.this, PlayActivity.class);
            intent1.putExtra("Name", intent_playerName);
            intent1.putExtra("Level", intent_Level);
            intent1.putExtra("Game","");
            startActivity(intent1);
        });
//
//        /**=======================================================================================*/

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }


    /*  for spinner */
    private final AdapterView.OnItemSelectedListener spnOnItemSelected
            = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    private void popUpEditText() {
        if(havePlayer){
            txt_main_playerName.setText(intent_playerName);
            txt_main_Level.setText(intent_Level);
            return;
        }

        View mview = getLayoutInflater().inflate(R.layout.dialog_player_setting,null);
        /*  spinner for Level setting  */
        EditText edit_PlayerName = mview.findViewById(R.id.edit_playerName);
        edit_PlayerName.setFilters(new InputFilter[]{new InputFilter.AllCaps()}); // only uppercase
        Spinner spinner_level = mview.findViewById(R.id.spinner_playerLevel);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, //對應的context
                        R.array.Level,                      //資料選項內容
                        R.layout.spinner_level);//預設spinner未展開時的View
        adapter.setDropDownViewResource(R.layout.spinner_list_item);//展開時的選項清單樣式
        spinner_level.setAdapter(adapter);//將設定好的ArrayAdapter to spinner
        spinner_level.setSelection(0, false);
        spinner_level.setOnItemSelectedListener(spnOnItemSelected);


        PopupWindow popupWindow = new PopupWindow(mview);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        new Handler().post(()->{
            popupWindow.showAtLocation(mview, Gravity.CENTER,0, 0);
            dimBehind(popupWindow);
        });

        Button btn_ok = mview.findViewById(R.id.set_save);

        btn_ok.setOnClickListener(v -> {
            String playername = edit_PlayerName.getText().toString();
            String level = spinner_level.getSelectedItem().toString();

            intent_playerName = playername;
            intent_Level = level;

            new Thread(() -> {

                if (playername.length() == 0) return;//如果沒填名子，則不執行下面的程序
                PlayerInfo playerInfo = new PlayerInfo(playername, level);
                DataBase.getInstance(getApplicationContext()).getDataUao().savePlayerInfo(playerInfo);

                runOnUiThread(()->{
                    txt_main_playerName.setText(intent_playerName);
                    txt_main_Level.setText(intent_Level);
                    popupWindow.dismiss();
                    havePlayer = true;
                });
            }).start();
        });






    }

    private void editLevel(){


        View mview = getLayoutInflater().inflate(R.layout.dialog_player_setting,null);
        /*  spinner for Level setting  */
        EditText edit_PlayerName = mview.findViewById(R.id.edit_playerName);
        edit_PlayerName.setFilters(new InputFilter[]{new InputFilter.AllCaps()}); // only uppercase


        /*  spinner for Level setting  */
        Spinner spinner_level = mview.findViewById(R.id.spinner_playerLevel);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, //對應的context
                        R.array.Level,                      //資料選項內容
                        R.layout.spinner_level

                        );//預設spinner未展開時的View android.R.layout.simple_spinner_item

        adapter.setDropDownViewResource(R.layout.spinner_list_item);//展開時的選項清單樣式
        spinner_level.setAdapter(adapter);//將設定好的ArrayAdapter to spinner


        int current_position;
        switch (String.valueOf(txt_main_Level.getText())){
            case "C1":current_position=0;break;
            case "B2":current_position=1;break;
            case "B1":current_position=2;break;
            case "A2":current_position=3;break;
            case "A1":current_position=4;break;
            default:  current_position=0;break;

        }
        spinner_level.setSelection(current_position, false);
        spinner_level.setOnItemSelectedListener(spnOnItemSelected);
        Button btn_save = mview.findViewById(R.id.set_save);


        PopupWindow popupWindow = new PopupWindow(mview);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);


        new Handler().post(()->{

            popupWindow.showAtLocation(mview, Gravity.CENTER,0, 0);
            dimBehind(popupWindow);
            popupWindow.setOutsideTouchable(true);

        });


        btn_save.setOnClickListener(v -> {

            String level = spinner_level.getSelectedItem().toString();
            intent_Level = level;
            String playername = edit_PlayerName.getText().toString();
            intent_playerName = playername;

            new Thread(() -> {

                if(havePlayer)
                    DataBase.getInstance(getApplicationContext()).getDataUao().updateinfo(1,playername,level);

                else {
                    PlayerInfo playerInfo = new PlayerInfo(playername, level);
                    DataBase.getInstance(getApplicationContext()).getDataUao().savePlayerInfo(playerInfo);
                }

                runOnUiThread(()->{
                    txt_main_playerName.setText(intent_playerName);
                    txt_main_Level.setText(intent_Level);
                    popupWindow.dismiss();
                    havePlayer = true;
                });
            }).start();
        });


    }



    private void showHistoryWord(){
        new Thread(()->{
            List<HistoryWords> list = DataBase.getInstance(getApplicationContext()).getDataUao().getHistoryWordsById(1);
            runOnUiThread(()->{
                ArrayList<String> dateList = new ArrayList<>();
                HashMap<String,ArrayList<String>> dateList_historywords = new HashMap<>();
                for(int index=0;index<list.size();index++){
                    String date = list.get(index).getDate();
                    dateList.add(date);
                    String[] historywords_buffer = list.get(index).getVocabulary().replace("[","").replace("]","").replace(" ","").split(",");
                    Log.d("mawoamf",historywords_buffer.toString());
                    ArrayList<String> historywords = new ArrayList<>(Arrays.asList(historywords_buffer));
                    dateList_historywords.put(date,historywords);
                }


//                afterdic.setText(list.get(1).getVocabulary());

                popupWindow_his.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow_his.setHeight(1200);
                popupWindow_his.showAtLocation(view_his, Gravity.CENTER,0,0);
                popupWindow_his.setOutsideTouchable(true);
                dimBehind(popupWindow_his);

//                popupWindow.setAnimationStyle(R.style.popup_window_animation);

                RecyclerView recyclerView = view_his.findViewById(R.id.recyc_historyWord_date);
                LinearLayoutManager layoutManager = new LinearLayoutManager(view_his.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(view_his.getContext(), DividerItemDecoration.VERTICAL));//分隔線
                RecycAdapter_historyword_with_date recycAdapter_historyword_with_date = new RecycAdapter_historyword_with_date(this,dateList,dateList_historywords);
                recyclerView.setAdapter(recycAdapter_historyword_with_date);
            });
        }).start();
    }

    // popupwindow 之外背景變暗
    public static void dimBehind(PopupWindow popupWindow) {

        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;

        wm.updateViewLayout(container, p);
    }

    private void readFile()  {
        InputStream is = this.getResources().openRawResource(R.raw.scrabbledic);
        readInputStream(is,listD);


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




    /**
     * Release memory when the UI becomes hidden or when system resources become low.
     * @param level the memory-related event that was raised.
     */
    public void onTrimMemory(int level) {

        // Determine which lifecycle or system event was raised.
        switch (level) {

            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:

                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:

                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

                break;

            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }



}






