package Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class RecycAdapter_historyword_with_date extends RecyclerView.Adapter<RecycAdapter_historyword_with_date.ViewHolder>{
    private ArrayList<String> dateList; // date time
    private HashMap<String,ArrayList<String>> dateList_historywords = new HashMap<>(); // click date get wordlist
    private Activity activity;
    private View view_;

    public RecycAdapter_historyword_with_date(Activity activity, ArrayList<String> dateList, HashMap<String,ArrayList<String>> dateList_historywords) {
        this.dateList = dateList;
        this.dateList_historywords = dateList_historywords;
        this.activity = activity;
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_date;
        RecyclerView recyc_historyword_date_item;
        ImageButton btn_expand;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_date);
            recyc_historyword_date_item = itemView.findViewById(R.id.recyc_historyWord_date_item);
            btn_expand = itemView.findViewById(R.id.btn_expand);
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
                .inflate(R.layout.history_word_date_itemview,parent,false);
        view_ = new ViewHolder(view).getView();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_date.setText(dateList.get(position));
        String date = dateList.get(position);
        RecycAdapter_historyword recycAdapter_historyword = new RecycAdapter_historyword(activity,dateList_historywords.get(date));
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        holder.recyc_historyword_date_item.setLayoutManager(layoutManager);
        holder.recyc_historyword_date_item.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));//分隔線
        holder.recyc_historyword_date_item.setAdapter(recycAdapter_historyword);

        holder.getView().setOnClickListener((view)->{
            if(holder.recyc_historyword_date_item.getVisibility()==View.GONE) {

                holder.recyc_historyword_date_item.setAlpha(0f);
                holder.recyc_historyword_date_item.setVisibility(View.VISIBLE);
                holder.btn_expand.animate().rotation(90).setDuration(100);
                holder.recyc_historyword_date_item.animate().alpha(1f).setDuration(200);

            }
            else {
                holder.recyc_historyword_date_item.setVisibility(View.GONE);
                holder.btn_expand.animate().rotation(-90).setDuration(100);
            }
        });
        holder.btn_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.recyc_historyword_date_item.getVisibility()==View.GONE) {

                    holder.recyc_historyword_date_item.setAlpha(0f);
                    holder.recyc_historyword_date_item.setVisibility(View.VISIBLE);
                    holder.btn_expand.animate().rotation(90).setDuration(100);
                    holder.recyc_historyword_date_item.animate().alpha(1f).setDuration(200);

                }
                else {
                    holder.recyc_historyword_date_item.setVisibility(View.GONE);
                    holder.btn_expand.animate().rotation(-90).setDuration(100);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }


    public View getView_() {
        return view_;
    }



}
