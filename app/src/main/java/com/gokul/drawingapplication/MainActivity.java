package com.gokul.drawingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;

import java.io.OutputStream;

import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity {

    private DrawView paint;
    private ImageButton save, color, stroke, undo;
    private RangeSlider rangeSlider;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paint=(DrawView)findViewById(R.id.draw_view);
        rangeSlider=(RangeSlider)findViewById(R.id.rangebar);
        undo=(ImageButton)findViewById(R.id.btn_undo);
        save=(ImageButton)findViewById(R.id.btn_save);
        color=(ImageButton)findViewById(R.id.btn_color);
        stroke=(ImageButton)findViewById(R.id.btn_stroke);
        pb = (ProgressBar) findViewById(R.id.pbLoading);


        undo.setOnClickListener(view -> paint.undo());

        save.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            //getting the bitmap from DrawView class
            Bitmap bmp=paint.save();
            //opening a OutputStream to write into the file
            OutputStream imageOutStream;

            ContentValues cv=new ContentValues();
            //name of the file
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.png");
            //type of the file
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            //location of the file to be saved
            cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            //ge the Uri of the file which is to be v=created in the storage
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            try {
                //open the output stream with the above uri
                imageOutStream = getContentResolver().openOutputStream(uri);
                //this method writes the files in storage
                bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
                //close the output stream after use
                imageOutStream.close();
                Toast.makeText(getApplicationContext(), "Image save", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pb.setVisibility(View.GONE);

        });

        color.setOnClickListener(view -> {

            final ColorPicker colorPicker=new ColorPicker(MainActivity.this);
            colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                @Override
                public void setOnFastChooseColorListener(int position, int color) {
                    //get the integer value of color selected from the dialog box and
                    // set it as the stroke color
                    paint.setColor(color);

                }

                @Override
                public void onCancel() {

                    colorPicker.dismissDialog();
                }
            })
                    //set the number of color columns you want  to show in dialog.
                    .setColumns(5)
                    //set a default color selected in the dialog
                    .setDefaultColorButton(Color.parseColor("#000000"))
                    .show();
        });

        stroke.setOnClickListener(view -> {
            if(rangeSlider.getVisibility()==View.VISIBLE)
                rangeSlider.setVisibility(View.GONE);
            else
                rangeSlider.setVisibility(View.VISIBLE);
        });

        //set the range of the RangeSlider
        rangeSlider.setValueFrom(0.0f);
        rangeSlider.setValueTo(100.0f);
        //adding a OnChangeListener which will change the stroke width
        //as soon as the user slides the slider
        rangeSlider.addOnChangeListener((slider, value, fromUser) -> paint.setStrokeWidth((int) value));

        //pass the height and width of the custom view to the init method of the DrawView object
        ViewTreeObserver vto = paint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = paint.getMeasuredWidth();
                int height = paint.getMeasuredHeight();
                paint.init(height, width);
            }
        });
    }


}