package Adapter;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scrabble.R;

import java.util.List;

public class RecycAdapter_board extends RecyclerView.Adapter<RecycAdapter_board.ViewHolder>{

    private List<Integer> imageId;
    private LayoutInflater mInflater;


    // data is passed into the constructor
    public RecycAdapter_board(Context context, List<Integer> imageId) {
        this.mInflater = LayoutInflater.from(context);
        this.imageId = imageId;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_single, parent, false);
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

            return true;
        }


        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:

                    // drag has started, return true to tell that you're listening to the drag
                    return true;
                case DragEvent.ACTION_DROP:
                    ClipData clipData = event.getClipData();

                    int itemCount = clipData.getItemCount();
                    if(itemCount > 0){
                        ClipData.Item item = clipData.getItemAt(0);
                        View srcView = (View)event.getLocalState();

                        ViewGroup owner = (ViewGroup) srcView.getParent();

//                        owner.getChildAt(Integer.valueOf(item.toString()));
//                        owner.removeView(srcView);

                        Log.d("ofakfowkff",String.valueOf(srcView.getParent()));

                        Log.d("ofakfowkff",String.valueOf(owner));
//                        Log.d("ofakfowkff",String.valueOf(clipData));
                        if(getImageId().get(getAdapterPosition())==0){
                            getImageId().set(getAdapterPosition(),Integer.valueOf(item.getText().toString()));

                        }else {
                            int row = getAdapterPosition()/9;
                            int col = getAdapterPosition()%9;
                            if(col>0 && getImageId().get((row)*9+col-1)==0){
                                getImageId().set((row)*9+col-1,Integer.valueOf(item.getText().toString()));

                            }else if(col<8 && getImageId().get((row)*9+col+1)==0){
                                getImageId().set((row)*9+col+1,Integer.valueOf(item.getText().toString()));
                            }else if(row>0 && getImageId().get((row-1)*9+col)==0){
                                getImageId().set((row-1)*9+col,Integer.valueOf(item.getText().toString()));

                            }else if(row<8 && getImageId().get((row+1)*9+col)==0){
                                getImageId().set((row+1)*9+col,Integer.valueOf(item.getText().toString()));
                            }
                        }
                        notifyDataSetChanged();


                        Log.d("ofakfowkffi",String.valueOf(Integer.valueOf(item.getText().toString())));
                        Log.d("ofakfowkffi",String.valueOf(getImageId().toString()));

//                        LinearLayout tar = (LinearLayout) v;
//                        tar.removeAllViewsInLayout();
//                        tar.addView(srcView);
                        srcView.setVisibility(View.VISIBLE);

                    }
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
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
