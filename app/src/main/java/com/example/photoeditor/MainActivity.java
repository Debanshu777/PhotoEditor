package com.example.photoeditor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.photoeditor.Adapter.ViewPagerAdapter;
import com.example.photoeditor.Interface.EditImageFragmentListener;
import com.example.photoeditor.Interface.FilterListFragmentListener;
import com.example.photoeditor.Utils.BitmapUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MainActivity extends AppCompatActivity implements FilterListFragmentListener, EditImageFragmentListener {

    public static final String pictureName ="MyPic2.jpg";
    public static final int PERMISSION_PIC_IMAGE=1000;

    PhotoEditorView photoeditorview;
    PhotoEditor photoEditor;
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Photo Editor");

        //view
        photoeditorview=findViewById(R.id.image_preview);
        photoEditor=new PhotoEditor.Builder(this,photoeditorview).setPinchTextScalable(true).build();
        tablayout=findViewById(R.id.tabs);
        viewPager=findViewById(R.id.viewpager);
        coordinatorLayout=findViewById(R.id.coordinator);

        loadImage();
        setUpViewPager(viewPager);
        tablayout.setupWithViewPager(viewPager);

    }

    private void loadImage() {
        originalBitmap= BitmapUtils.getBitmapFromAssets(this,pictureName,300,300);
        assert originalBitmap != null;
        filterBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        finalBitmap =originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        photoeditorview.getSource().setImageBitmap(originalBitmap);
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
        photoeditorview.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));

    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal=saturation;
        Filter myFilter=new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        photoeditorview.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
    }

    @Override
    public void onConstrantChanged(float constrant) {
        contrastFinal=constrant;
        Filter myFilter=new Filter();
        //myFilter.addSubFilter(new St);
        myFilter.addSubFilter(new ContrastSubFilter(constrant));
        photoeditorview.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888,true)));
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
        filterBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
        photoeditorview.getSource().setImageBitmap(filter.processFilter(filterBitmap));
        finalBitmap=filterBitmap.copy(Bitmap.Config.ARGB_8888,true);

    }

    private void resetContol() {
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_open){
            openImageFromGallery();
            return true;
        }
        if(id== R.id.action_save){
            saveImageToGallery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImageToGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {
                                    try {
                                        final String path=BitmapUtils.insertImage(getContentResolver()
                                                ,saveBitmap,System.currentTimeMillis()+"_profile.jpg",
                                                null);
                                        if(!TextUtils.isEmpty(path)){
                                            Snackbar snackbar=Snackbar.make(coordinatorLayout,
                                                    "Image saved to gallery!",
                                                    Snackbar.LENGTH_LONG).setAction("Open",
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            openImage(path);
                                                        }
                                                    });
                                            snackbar.show();
                                        }
                                        else {
                                            Snackbar snackbar=Snackbar.make(coordinatorLayout,
                                                    "Unable to save Image",
                                                    Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openImage(String path) {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path),"image/");
        startActivity(intent);
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Intent intent =new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent,PERMISSION_PIC_IMAGE);

                        }
                        else{
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PERMISSION_PIC_IMAGE) {
            assert data != null;
            Bitmap bitmap = BitmapUtils.getBitmapFromGallary(this, data.getData(), 800, 800);
            //clear bitmap memory
            originalBitmap.recycle();
            finalBitmap.recycle();
            filterBitmap.recycle();

            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            filterBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            photoeditorview.getSource().setImageBitmap(originalBitmap);
            bitmap.recycle();
            // render selected image thumbnail
            filterListFragment.displayedThumbnail(originalBitmap);

        }
    }
}
