package com.example.leeseungchan.chulbalhama.UI.habit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.leeseungchan.chulbalhama.Activities.HabitInfoActivity;
import com.example.leeseungchan.chulbalhama.Activities.LocationInfoActivity;
import com.example.leeseungchan.chulbalhama.Activities.MainActivity;
import com.example.leeseungchan.chulbalhama.Adpater.PrepareAdapter;
import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.DayDialog;
import com.example.leeseungchan.chulbalhama.DayTimeDialog;
import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.UI.components.CustomChangeDeleteItem;
import com.example.leeseungchan.chulbalhama.UI.components.CustomSevenDayInfo;
import com.example.leeseungchan.chulbalhama.VO.HabitsVO;
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;

public class HabitChangeFragment extends Fragment {
    
    public Bundle bundle = new Bundle();
    
    private HabitsVO habit;
    private DBHelper dbHelper;
    private ArrayList<String> prepare = new ArrayList<>();
    private ArrayList<Boolean> days = new ArrayList<>();
    private CustomSevenDayInfo customSevenDayInfo;
    private RecyclerView.Adapter prepareAdapter;
    private EditText prepareHintView;
    private View view;
    
    public static HabitChangeFragment newInstance(Bundle bundle){
        HabitChangeFragment v = new HabitChangeFragment();
        v.bundle = bundle;
        return v;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_habit, container, false);
        this.view = v;
        dbHelper = DBHelper.getInstance(getContext());
        habit = (HabitsVO) bundle.getSerializable("habit");
        
        
        /* habit name */
        setDestInfoChangeDeleteItem(v, R.id.name_setting);
    
        /* habit due */
        setDestInfoChangeDeleteItem(v, R.id.due);
    
        /* prepare list*/
        LinearLayout layoutPrepare = v.findViewById(R.id.prepare_setting);
        prepareHintView = layoutPrepare.findViewById(R.id.input_for_selection);
        TextView guideText = layoutPrepare.findViewById(R.id.guide_for_selection);
        guideText.setText(R.string.guide_ask_prepare);
    
        RecyclerView prepares = layoutPrepare.findViewById(R.id.list);
        RecyclerView.LayoutManager layoutManager =
            new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        prepares.setLayoutManager(layoutManager);
    
        prepareAdapter = new PrepareAdapter(prepare, 0, habit.getId());
        prepares.setAdapter(prepareAdapter);
    
        setPrepare(habit.getPrepare());
    
        Button prepareInputButton = layoutPrepare.findViewById(R.id.button_for_selection);
        prepareInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prepareHintView.getText().toString().length() < 1){
                    prepareHintView.setHint("1자 이상 입력해주세요");
                    prepareHintView.setHintTextColor(Color.RED);
                }else {
                    prepare.add(prepareHintView.getText().toString());
                    updatePrepare(getContext());
                    prepareHintView.setText("");
                    prepareHintView.setHint("여기에 입력해 주세요.");
                    prepareHintView.setHintTextColor(Color.GRAY);
                    prepareAdapter.notifyDataSetChanged();
                }
            }
        });
    
    
        /* habit days */
        customSevenDayInfo = new CustomSevenDayInfo(v.findViewById(R.id.add_habit_day));
        customSevenDayInfo.setPlaceData();
        customSevenDayInfo.showSelectedDay(habit.getId());
        
        setDestInfoChangeDeleteItem(v, R.id.day_setting);
    
        return v;
    }
    private void setDestInfoChangeDeleteItem(View v, int id){
        LinearLayout layout = v.findViewById(id);
        CustomChangeDeleteItem item = new CustomChangeDeleteItem(layout);
        // set title text view
        item.setTitle(getTitle(id));
        // set description textView invisible
        item.setVisibility(item.DESCRIPTION, View.INVISIBLE);
        // set change button text & setOnClickListener
        item.setChange(getResources().getString(R.string.button_setting));
        setChangeBtnClickListener(item.getChange(), layout);
        // set delete button gone
        item.setVisibility(item.DELETE_BTN, View.GONE);
    }
    
    private String getTitle(int id){
        String title = null;
        switch (id){
            case R.id.name_setting:
                title = habit.getHabitName();
                break;
            case R.id.due:
                title = habit.getDue() + "일";
                break;
            case R.id.prepare_setting:
                title = getResources().getString(R.string.guide_ask_prepare);
                break;
            case R.id.day_setting:
                title = getResources().getString(R.string.guide_date);
                break;
        }
        return title;
    }
    
    private void setChangeBtnClickListener(View target, final View root){
        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (root.getId()){
                    case R.id.name_setting:
                        setNameDialog( v, "이름 바꾸기", habit.getId(), "habit_name");
                        break;
                    case R.id.due:
                        setDue();
                        break;
                    case R.id.prepare_setting:
                        setPrepareOperation();
                        break;
                    case R.id.day_setting:
                        DayDialog customDialog = new DayDialog(getActivity());
                        customDialog.callFunction(days, customSevenDayInfo, habit.getId());
                        break;
                }
            }
        });
    }
    
    private void setDue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_target, null);
        
        final EditText input = inflatedView.findViewById(R.id.input);
        
        builder.setView(inflatedView)
            .setPositiveButton(R.string.button_change, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    int newDue = Integer.parseInt(input.getText().toString());
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String sql = "update habits set due="+ newDue + " where _id="+habit.getId();
                    db.execSQL(sql);
                    db.close();
                    habit.setDue(newDue);
                    setDestInfoChangeDeleteItem(view, R.id.due);
    
                    refresh();
                }
            })
            .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        
        builder.create();
        builder.show();
    }
    
    
    public void setNameDialog(View v, String title, final int id, final String attr){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(title);
        View viewInflated =
            LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_edit_text,
                (ViewGroup) getView(),
                false
            );
        
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);
        
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String newName = input.getText().toString();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String sql = "update habits set "+ attr +"=\""+ newName + "\" where _id=" + id;
                db.execSQL(sql);
                db.close();
                ((HabitInfoActivity)getActivity()).setTitle(newName);
    
                habit.setHabitName(newName);
                setDestInfoChangeDeleteItem(view, R.id.name_setting);
                refresh();
            }
        });
        
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        
        builder.show();
    }
    
    
    private void setPrepareOperation(){
        if(prepareHintView.getText().toString().length() < 1){
            prepareHintView.setHint("1자 이상 입력해주세요");
            prepareHintView.setHintTextColor(Color.RED);
        }else {
            prepare.add(prepareHintView.getText().toString());
            updatePrepare(getContext());
            prepareHintView.setText("");
            prepareHintView.setHint("여기에 입력해 주세요.");
            prepareHintView.setHintTextColor(Color.GRAY);
            prepareAdapter.notifyDataSetChanged();
        }
    }
    
    private void setPrepare(String prepare){
        if(prepare == null)
            return;
        String[] prepares = prepare.split(",\n\t ",0);
        
        this.prepare.clear();
        
        for(int i=0; i<prepares.length; i++){
            if(prepares[i] != "")
                this.prepare.add(prepares[i]);
        }
    }
    
    private void updatePrepare(Context context){
        StringBuffer newPrepare = new StringBuffer();
        
        for(int i = 0; i < prepare.size(); i++){
            newPrepare.append(prepare.get(i) + ",");
        }
        updateHabitPrepareDB(newPrepare.toString(), habit.getId(), context);
    }
    
    private void updateHabitPrepareDB(String prepare, int id,Context context){
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "update habits set prepare = ? where _id = ? ";
        db.execSQL(sql, new Object[]{prepare, id});
        db.close();
    }
  
    private void refresh(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }
}
