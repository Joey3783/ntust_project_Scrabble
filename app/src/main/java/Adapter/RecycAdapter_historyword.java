package Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.R;
import com.example.scrabble.WordData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static GameActivity.PlayActivity.dimBehind;


public class RecycAdapter_historyword extends RecyclerView.Adapter<RecycAdapter_historyword.ViewHolder>{
    private ArrayList<String> wordList;
    private HashMap<String,WordData> wordDataList;
    private Activity activity;
    private View view_;

    public RecycAdapter_historyword(Activity activity, ArrayList<String> wordList) {
        this.wordList = wordList;
        this.wordDataList = new HashMap<>();
        this.activity = activity;
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txt_hisword);
            view = itemView;
        }

        public View getView() {
            return view;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_word_itemview,parent,false);
        view_ = new ViewHolder(view).getView();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(wordList.get(position));
        holder.view.setOnClickListener((v)->{
            if(wordDataList.containsKey(wordList.get(position))) {
                his_word_View(wordDataList.get(wordList.get(position)));
            }
            else {
                getWordMean(wordList.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }


    public View getView_() {
        return view_;
    }



    private void his_word_View(WordData wordData){



        View mview = activity.getLayoutInflater().inflate(R.layout.dialog_history_word_moreinfo,null);
        TextView title = mview.findViewById(R.id.moreinfo_title);
        TextView p_of_speech = mview.findViewById(R.id.txt_hisword_part_of_speech);
        TextView meaning = mview.findViewById(R.id.txt_hisword_meaning);
        Button btn_ok = mview.findViewById(R.id.morinfo_ok);
        PopupWindow popupWindow = new PopupWindow(mview);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(mview, Gravity.CENTER,0, 0);
        dimBehind(popupWindow);
        popupWindow.setOutsideTouchable(true);

        title.setText(wordData.getName());
        p_of_speech.setText(wordData.getPart_of_Speech());
        String def="";
        for(int i=0;i<wordData.getDefinition().length();i++){
            try {
                def = def + wordData.getDefinition().get(i)+"\n\n";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        meaning.setText(def);

       btn_ok.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               popupWindow.dismiss();
           }
       });
    }

    private void getWordMean(String word){

        String catchData="https://dictionaryapi.com/api/v3/references/learners/json/"+word.toLowerCase()+"?key=cfb57e42-44bb-449d-aa59-1a61d2ca31f0";
        ProgressDialog dialog = ProgressDialog.show(activity,"讀取中","請稍候",true);

        new Thread(()->{
            try {
                URL url = new URL(catchData);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String line = in.readLine();
                StringBuffer json = new StringBuffer();
                while (line!=null){
                    json.append(line);
                    line = in.readLine();
                }

                JSONArray jsonArray = new JSONArray(String.valueOf(json));
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                JSONArray shortdefarray = jsonObject.getJSONArray("shortdef");

                String Part_of_Speech = jsonObject.getString("fl");

                JSONArray id = jsonObject.getJSONArray("shortdef");

                String id2 = id.getString(0);

                WordData wordData = new WordData();
                wordData.setName(word);
                wordData.setPart_of_Speech(Part_of_Speech);
                wordData.setDefinition(shortdefarray);
                wordDataList.put(word,wordData);

                activity.runOnUiThread(() -> {
                    his_word_View(wordDataList.get(word));
                    dialog.dismiss();
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();




    }
}