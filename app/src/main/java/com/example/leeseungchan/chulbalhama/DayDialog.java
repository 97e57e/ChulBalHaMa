package com.example.leeseungchan.chulbalhama;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.leeseungchan.chulbalhama.UI.components.CustomDayCheckBox;
import com.example.leeseungchan.chulbalhama.UI.components.CustomSevenDayInfo;

import java.util.ArrayList;


public class DayDialog {
    private Context context;

    public DayDialog(Context context) {
        this.context = context;
    }

    public void callFunction(final ArrayList<Boolean> result,
                             final CustomSevenDayInfo customSevenDayInfo,
                             final int habitId) {

        final Dialog dlg = new Dialog(context);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dlg.setContentView(R.layout.dialog_day);

        dlg.show();

        final CustomDayCheckBox dayCheckBox =
                new CustomDayCheckBox(dlg.findViewById(R.id.custom_days_checkbox));
        dayCheckBox.setBoxes();
        dayCheckBox.showSelectedBoxes(habitId);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayCheckBox.getResult(result);
                customSevenDayInfo.pickDay(result);
                customSevenDayInfo.updateDayHabit(result, habitId);
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
    }
}
