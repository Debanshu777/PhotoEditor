package com.example.photoeditor;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.photoeditor.Interface.EditImageFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private EditImageFragmentListener listener;
    SeekBar seekbar_brightness, seekbar_constrant, seekbar_saturation;
    LinearLayout all, brightness, saturation, contrast, brightness_scale, contrast_scale, saturation_scale, options;
    LinearLayout brush_edit;
    Button cancel_button, done_button;
    View bottomSheetLayout;

    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_edit_image, container, false);
        init(itemView);
        brightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setVisibility(View.GONE);
                brightness_scale.setVisibility(View.VISIBLE);
                options.setVisibility(View.VISIBLE);

            }
        });
        contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setVisibility(View.GONE);
                contrast_scale.setVisibility(View.VISIBLE);
                options.setVisibility(View.VISIBLE);

            }
        });
        saturation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setVisibility(View.GONE);
                saturation_scale.setVisibility(View.VISIBLE);
                options.setVisibility(View.VISIBLE);

            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setVisibility(View.VISIBLE);
                options.setVisibility(View.GONE);
                saturation_scale.setVisibility(View.GONE);
                brightness_scale.setVisibility(View.GONE);
                contrast_scale.setVisibility(View.GONE);
                resetControl();
            }
        });
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all.setVisibility(View.VISIBLE);
                options.setVisibility(View.GONE);
                saturation_scale.setVisibility(View.GONE);
                brightness_scale.setVisibility(View.GONE);
                contrast_scale.setVisibility(View.GONE);
            }
        });

        seekbar_brightness.setMax(200);
        seekbar_brightness.setProgress(100);

        seekbar_constrant.setMax(200);
        seekbar_constrant.setProgress(100);

        seekbar_saturation.setMax(200);
        seekbar_saturation.setProgress(100);

        seekbar_brightness.setOnSeekBarChangeListener(this);
        seekbar_constrant.setOnSeekBarChangeListener(this);
        seekbar_saturation.setOnSeekBarChangeListener(this);

        return itemView;
    }

    private void init(View itemView) {
        seekbar_brightness = itemView.findViewById(R.id.seekbar_brightness);
        seekbar_constrant = itemView.findViewById(R.id.seekbar_contrast);
        seekbar_saturation = itemView.findViewById(R.id.seekbar_saturation);

        brightness = itemView.findViewById(R.id.brignthess);
        contrast = itemView.findViewById(R.id.contrast);
        saturation = itemView.findViewById(R.id.saturation);
        all = itemView.findViewById(R.id.all);

        brightness_scale = itemView.findViewById(R.id.brightness_scale);
        contrast_scale = itemView.findViewById(R.id.contrast_scale);
        saturation_scale = itemView.findViewById(R.id.saturation_scale);
        brush_edit = itemView.findViewById(R.id.brush_edit);

        cancel_button = itemView.findViewById(R.id.cancel_btn);
        done_button = itemView.findViewById(R.id.done_button);
        options = itemView.findViewById(R.id.options);


    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
        if (listener != null) {
            if (seekBar.getId() == R.id.seekbar_brightness) {
                listener.onBrightnessChanged(progress - 100);
            } else if (seekBar.getId() == R.id.seekbar_contrast) {
                float value = .01f * progress;
                listener.onConstrantChanged(value);
            } else if (seekBar.getId() == R.id.seekbar_saturation) {
                float value = .01f * progress;
                listener.onSaturationChanged(value);
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (listener != null) {
            listener.onEditStarted();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null) {
            listener.onEditCompleted();
        }
    }

    public void resetControl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                seekbar_brightness.setProgress(100);
                seekbar_constrant.setProgress(100);
                seekbar_saturation.setProgress(100);
                onProgressChanged(seekbar_brightness, 100, true);
                onProgressChanged(seekbar_constrant, 100, true);
                onProgressChanged(seekbar_saturation, 100, true);
            }
        }).start();

    }
}
