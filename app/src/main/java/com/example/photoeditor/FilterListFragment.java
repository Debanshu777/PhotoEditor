package com.example.photoeditor;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoeditor.Adapter.ThumbnailAdapter;
import com.example.photoeditor.Interface.FilterListFragmentListener;
import com.example.photoeditor.Utils.BitmapUtils;
import com.example.photoeditor.Utils.SpacesItemDecoration;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilterListFragment extends Fragment implements FilterListFragmentListener {
    RecyclerView recyclerView;
    ThumbnailAdapter adapter;
    List<ThumbnailItem> thumbnailItems;

    FilterListFragmentListener listener;

    public void setListener(FilterListFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView= inflater.inflate(R.layout.fragment_filter_list, container, false);

        thumbnailItems=new ArrayList<>();
        adapter=new ThumbnailAdapter(thumbnailItems,this,getActivity());

        recyclerView=itemView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int space=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,0,getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(adapter);

        displayedThumbnail(null);


        return itemView;
    }

    public void displayedThumbnail(final Bitmap bitmap) {
        Runnable r =new Runnable() {
            @Override
            public void run() {
                Bitmap thumbImg;
                if (bitmap ==null){
                    thumbImg= BitmapUtils.getBitmapFromAssets(Objects.requireNonNull(getActivity()),MainActivity.pictureName,100,100);
                }
                else {
                    thumbImg=Bitmap.createScaledBitmap(bitmap,100,100,false);
                }
                if (thumbImg==null)
                    return;
                ThumbnailsManager.clearThumbs();
                thumbnailItems.clear();

                //add normal bitmap
                ThumbnailItem thumbnailItem=new ThumbnailItem();
                thumbnailItem.image=thumbImg;
                thumbnailItem.filterName="Original";
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters= FilterPack.getFilterPack(Objects.requireNonNull(getActivity()));
                for(Filter filter:filters){
                    ThumbnailItem tI=new ThumbnailItem();
                    tI.image=thumbImg;
                    tI.filter=filter;
                    tI.filterName=filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }
                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        };
        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {
        if (listener!=null){
            listener.onFilterSelected(filter);
        }

    }
}
