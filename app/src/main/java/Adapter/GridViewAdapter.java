package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.scrabble.R;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private final List<Integer> imageId;

    public GridViewAdapter(Context context, List<Integer> imageId) {
        this.context = context;
        this.imageId = imageId;
    }

    @Override
    public int getCount() {
        return imageId.size();
    }

    @Override
    public Object getItem(int position) {
        return imageId.get(position);
    }

    public List<Integer> getItems(){
        return imageId;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        // Context 動態放入mainActivity
        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            grid = new View(context);
            // 將grid_single 動態載入(image)
            grid = inflater.inflate(R.layout.grid_single, null);
            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
            imageView.setImageResource(imageId.get(position));
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}