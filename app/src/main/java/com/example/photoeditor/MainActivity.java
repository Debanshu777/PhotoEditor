package com.example.photoeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

import com.example.photoeditor.Adapter.ViewPagerAdapter;
import com.example.photoeditor.Interface.EditImageFragmentListener;
import com.example.photoeditor.Interface.FilterListFragmentListener;
import com.example.photoeditor.Utils.BitmapUtils;
import com.google.android.material.tabs.TabLayout;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

public class MainActivity extends AppCompatActivity implements FilterListFragmentListener, EditImageFragmentListener {

    public static final String pictureName ="MyPic.jpg";
    public static final int PERMISSION_PIC_IMAGE=1000;

    ImageView image_preview;
    TabLayout tablayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;

    FilterListFragment filterListFragment;
    EditImageFragment editImageFragment;

    Bitmap originalBitmap,filterBitmap,finalBitmap;


    int brightnessFinal=0;
    float saturationFinal=1.0f;
    float contrastFinal=1.0f;

    //Load native filter image lib
    static{
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Photo Editor");

        //view
        image_preview=findViewById(R.id.image_preview);
        tablayout=findViewById(R.id.tabs);
        viewPager=findViewById(R.id.viewpager);
        coordinatorLayout=findViewById(R.id.coordinator);

        loadImage();
        setUpViewPager(viewPager);
        tablayout.setupWithViewPager(viewPager);

    }

    private void loadImage() {
        originalBitmap= BitmapUtils.getBitmapFromAssets(this,pictureName,300,300);
        filterBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        finalBitmap =originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        image_preview.setImageBitmap(originalBitmap);
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        filterListFragment=new FilterListFragment();
        filterListFragment.setListener(this);

        editImageFragment=new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filterListFragment,"Filters");
        adapter.addFragment(editImageFragment,"Edit");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal=brightness;
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        image_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal=saturation;
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        image_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onConstrantChanged(float constrant) {
        contrastFinal=constrant;
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constrant));
        image_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap=filterBitmap.copy(Bitmap.Config.ARGB_8888,true);

        Filter myfilter=new Filter();
        myfilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myfilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myfilter.addSubFilter(new ContrastSubFilter(contrastFinal));

        finalBitmap=myfilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetcontol();
        filterBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        image_preview.setImageBitmap(filter.processFilter(filterBitmap));
        finalBitmap=finalBitmap.copy(Bitmap.Config.ARGB_8888,true);

    }

    private void resetcontol() {
        if(editImageFragment!=null){
            editImageFragment.resetControl();
        }
        brightnessFinal=0;
        saturationFinal=1.0f;
        contrastFinal=1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
}
