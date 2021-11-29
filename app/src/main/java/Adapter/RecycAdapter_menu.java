package Adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import GameActivity.MainActivity;
import GameActivity.PlayActivity;

public class RecycAdapter_menu extends RecyclerView.Adapter<RecycAdapter_menu.View_Holder> {
    private List<Menu_Data> list = Collections.emptyList();
    private Context context;


    public RecycAdapter_menu(List<Menu_Data> data, Application application) {
        this.list = data;
        this.context = application;
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView imageView;
        private View mView;

        View_Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.menu_item_name);
            imageView = (ImageView) itemView.findViewById(R.id.menu_item_image);
            mView = itemView;
        }
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_itemview, parent, false);
        View_Holder holder = new View_Holder(v);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(list.get(position).getName());
        holder.imageView.setImageResource(list.get(position).getImageId());
        holder.mView.setOnClickListener((v)->{
            Intent intent = new Intent();
            switch (list.get(position).getName()){

                case "Cancel Game":
                    intent = new Intent();
                    intent.setClass(context, MainActivity.class);
                    intent.putExtra("store_game",false);
                    intent.putExtra("cancel_game",true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
                case "End Game":
                    Date currentTime = Calendar.getInstance().getTime();
                    String current_time = currentTime.toString().substring(4,16);
                    intent.setClass(context, MainActivity.class);
                    intent.putExtra("store_game",true);
                    intent.putExtra("cancel_game",false);
                    intent.putExtra("current_time",current_time);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}
