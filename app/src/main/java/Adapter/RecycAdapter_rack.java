package Adapter;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.icu.util.ULocale;
import android.os.Build;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.R;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RecycAdapter_rack extends RecyclerView.Adapter<RecycAdapter_rack.ViewHolder>{
    private List<Integer> imageId;
    private LayoutInflater mInflater;


    // data is passed into the constructor
    public RecycAdapter_rack(Context context, List<Integer> imageId) {
        this.mInflater = LayoutInflater.from(context);
        this.imageId = imageId;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_rack, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(imageId.get(position));
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return imageId.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnDragListener {
        ImageView imageView;



        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_image);
//            itemView.setOnTouchListener(this::onTouch);
//            itemView.setOnDragListener(this::onDrag);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {





            // Get view object tag value.
            String image = String.valueOf(getImageId().get(getAdapterPosition()));
            // Create clip data.
            ClipData clipData = ClipData.newPlainText("image", image);

            // Create drag shadow builder object.
            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
        /* Invoke view object's startDrag method to start the drag action.
           clipData : to be dragged data.
           dragShadowBuilder : the shadow of the dragged view.
        */
            v.startDrag(clipData, dragShadowBuilder, v, 0);
            // Hide the view object because we are dragging it.
            v.setVisibility(View.INVISIBLE);

            // 拿起來得時候先把原本的換成空白
            getImageId().set(getAdapterPosition(),0);
            notifyItemChanged(getAdapterPosition());
            Log.d("ofakfowkffi",String.valueOf(getImageId().toString()));
//


            return true;
        }



        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:

                    // drag has started, return true to tell that you're listening to the drag
                    return true;


                case DragEvent.ACTION_DRAG_LOCATION:

                    Log.d("aodkwwaodko-l",String.valueOf(getLayoutPosition()));
                    Log.d("aodkwwaodko-a",String.valueOf(getAdapterPosition()));

                    // 如果移動到有牌的位置上空，就要把排往左/往右移出一個空位
                    if(getImageId().get(getLayoutPosition())!=0){
                        Log.d("aodkwwaodko___","getin");
                        int offset = 0;
                        while(true){
                            Log.d("aodkwwaodko___","getwhile");
                            // 如果是右邊有空，就把右邊的都往右移
                            if(getLayoutPosition()+offset<getImageId().size() && (getImageId().get(getLayoutPosition()+offset)==0)){
                                for (int i = getLayoutPosition()+offset; i > getLayoutPosition(); i--) {
                                    Log.d("aodkwwaodko___","getleft");

                                    notifyItemChanged(i);
                                    notifyItemChanged(i-1);
                                    Collections.swap(getImageId(), i, i - 1);
                                    notifyItemChanged(i);
                                    notifyItemChanged(i-1);
//                                    notifyItemChanged(i);
                                }
//                                notifyItemChanged(getLayoutPosition()+offset);
//                                notifyItemMoved(getLayoutPosition(),getLayoutPosition()+offset);
                                break;
                            }

                            else if(getLayoutPosition()-offset>=0 && getImageId().get(getLayoutPosition()-offset)==0){

                                for (int i = getLayoutPosition()-offset; i < getLayoutPosition(); i++) {
                                    Log.d("aodkwwaodko___","getright");

                                    notifyItemChanged(i+1);
                                    notifyItemChanged(i);
                                    Collections.swap(getImageId(), i, i + 1);
//                                    notifyItemChanged(i);
                                    notifyItemChanged(i+1);
                                    notifyItemChanged(i);
                                }
//                                notifyItemChanged(getLayoutPosition()-offset);
//                                notifyItemMoved(getLayoutPosition(),getLayoutPosition()-offset);
                                break;
                            }
                            offset++;
                        }

                    }
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData clipData = event.getClipData();
                    int itemCount = clipData.getItemCount();
                    if(itemCount > 0){
                        ClipData.Item item = clipData.getItemAt(0);
                        View srcView = (View)event.getLocalState();


//                        owner.getChildAt(Integer.valueOf(item.toString()));
//                        owner.removeView(srcView);

//                        Log.d("ofakfowkff",String.valueOf(getAdapterPosition()));
//                        Log.d("ofakfowkff",String.valueOf(clipData));
                        if(getImageId().get(getLayoutPosition())==0) {
                            getImageId().set(getAdapterPosition(), Integer.valueOf(item.getText().toString()));
                            notifyDataSetChanged();
                        }
                        else {
                            if(getImageId().get(getLayoutPosition()+1)==0) {
                                getImageId().set(getAdapterPosition()+1, Integer.valueOf(item.getText().toString()));
                                notifyDataSetChanged();
                            }
                            else {
                                getImageId().set(getAdapterPosition()-1, Integer.valueOf(item.getText().toString()));
                                notifyDataSetChanged();
                            }
                        }

                        Log.d("ofakfowkffi",String.valueOf(Integer.valueOf(item.getText().toString())));
                        Log.d("ofakfowkffi",String.valueOf(getImageId().toString()));

//                        LinearLayout tar = (LinearLayout) v;
//                        tar.removeAllViewsInLayout();
//                        tar.addView(srcView);
                        srcView.setVisibility(View.VISIBLE);

                    }
                    return true;

                default:
                    return false;

            }
        }
    }

    public List<Integer> getImageId() {
        return imageId;
    }

    public void updateImageId(List<Integer> imageId){
        this.imageId = imageId;
    }


}
