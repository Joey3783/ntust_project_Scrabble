package Adapter;

import android.view.View;

import java.util.List;

public class PassObject {
    private View view;
    private int imgId;
    private List<Integer> imgList;

    PassObject(View v, int i, List<Integer> l){
        view = v;
        imgId = i;
        imgList = l;
    }

    public View getView() {
        return view;
    }

    public int getImgId() {
        return imgId;
    }

    public List<Integer> getImgList() {
        return imgList;
    }
}
