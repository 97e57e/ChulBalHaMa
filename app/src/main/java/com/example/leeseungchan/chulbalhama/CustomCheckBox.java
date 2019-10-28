package com.example.leeseungchan.chulbalhama;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class CustomCheckBox extends ConstraintLayout {
    ConstraintLayout custom_radio;
    CheckBox custom_radio_btn;

    public CustomCheckBox(@NonNull Context context, @Nullable AttributeSet attrs){
        super(context, attrs);

        inflateViews(context, attrs);
    }

    void inflateViews(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_checkbox, this);

        custom_radio_btn = findViewById(R.id.custom_radio_btn);
        custom_radio = findViewById(R.id.custom_radio);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setText(@NonNull String text){
        custom_radio_btn.setText(text);
    }

    public void setTextColor(int color){
        custom_radio_btn.setTextColor(color);
    }

}
