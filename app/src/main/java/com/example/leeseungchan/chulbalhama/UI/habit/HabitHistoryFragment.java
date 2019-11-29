package com.example.leeseungchan.chulbalhama.UI.habit;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.leeseungchan.chulbalhama.Adpater.SRBAIAdapter;
import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.VO.HabitsVO;
import com.example.leeseungchan.chulbalhama.VO.SrbaiVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HabitHistoryFragment extends Fragment {
    
    public Bundle bundle;
    private HabitsVO habit;
    private DBHelper dbHelper;
    private View v;
    private ArrayList<SrbaiVO> srbaiVOS = new ArrayList<>();
    
    public static HabitHistoryFragment newInstance(Bundle bundle){
        HabitHistoryFragment v = new HabitHistoryFragment();
        v.bundle = bundle;
        return v;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history_habit, container, false);
        this.v = v;
        dbHelper = DBHelper.getInstance(getContext());
        habit = (HabitsVO) bundle.getSerializable("habit");

        /* srbai history */
        RecyclerView recyclerView = v.findViewById(R.id.history_list);
        RecyclerView.LayoutManager layoutManager=
            new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        SRBAIAdapter adapter = new SRBAIAdapter(srbaiVOS);
        recyclerView.setAdapter(adapter);
        retrieveSrbai(habit.getId());
        
        /* SRABI questions */
        final LinearLayout srbaiQ = v.findViewById(R.id.srbai_questions);
    
        final CheckBox showAdditional = v.findViewById(R.id.show_additional);
        boolean todayComplete = isTodayComplete(lastSrbaiVO(srbaiVOS));
    
        if(todayComplete) {
            showAdditional.setChecked(true);
            showAdditional.setEnabled(!todayComplete);
            srbaiQ.setVisibility(View.GONE);
        }
        showAdditional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    srbaiQ.setVisibility(View.VISIBLE);
                }else{
                    srbaiQ.setVisibility(View.GONE);
                }
            }
        });
        /* SRBAI input */
        LinearLayout srbaiFirst = srbaiQ.findViewById(R.id.srbai_fir);
        final RadioGroup firstQ = srbaiFirst.findViewById(R.id.radio_result);
        LinearLayout srbaiSecond = srbaiQ.findViewById(R.id.srbai_sec);
        final RadioGroup secondQ = srbaiSecond.findViewById(R.id.radio_result);
        LinearLayout srbaiThird = srbaiQ.findViewById(R.id.srbai_thr);
        final RadioGroup thirdQ = srbaiThird.findViewById(R.id.radio_result);
        LinearLayout srbaiFourth = srbaiQ.findViewById(R.id.srbai_fou);
        final RadioGroup fourthQ = srbaiFourth.findViewById(R.id.radio_result);
        
        /* store SRBAI data */
        final Button storeSRBAI = srbaiQ.findViewById(R.id.store_srbai);
        storeSRBAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                srbaiQ.setVisibility(View.GONE);
                SrbaiVO data = new SrbaiVO(getDate(),
                    getScore(new RadioGroup[]{firstQ,secondQ,thirdQ,fourthQ}),
                    habit.getId());
                storeSRBAI(data);
                refresh();
            }
        });
    
        return v;
    }
    
    private void storeSRBAI(SrbaiVO result){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String sql = "insert into srbai(day, score, habit_id) values(?,?,?)";
        db.execSQL(sql, new Object[]{result.getDay(), result.getScore(), result.getHabitId()});
        
        db.close();
    }
    
    private int getScore(RadioGroup[] groups){
        int score = 0;
        
        for(int i = 0; i < 4; i++){
            RadioButton button = v.findViewById(groups[i].getCheckedRadioButtonId());
            score +=  Integer.parseInt(button.getText().toString());
        }
        return score;
    }
    
    private String getDate(){
        String today;
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat date = new SimpleDateFormat("MM월 dd일",Locale.getDefault());
        today = date.format(currentTime);
        return today;
    }
    
    public void retrieveSrbai(int habitId){
        srbaiVOS.clear();
        
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select day, score from srbai where habit_id=?";
        Cursor c = db.rawQuery(sql, new String[]{Integer.toString(habitId)});
        while(c.moveToNext()){
            String day = c.getString(0);
            int score = c.getInt(1);
            SrbaiVO srbaiVO = new SrbaiVO(day, score, habitId);
            srbaiVOS.add(srbaiVO);
        }
    }
    
    private boolean isTodayComplete( SrbaiVO today){
        if(today == null)
            return false;
        
        if(today.getDay().equals(getDate()))
            return true;
        else
            return false;
    }
    private void refresh(){
        FragmentTransaction transaction =
            getActivity().getSupportFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }
    
    private SrbaiVO lastSrbaiVO(ArrayList<SrbaiVO> srbaivos){
        if(srbaivos == null || srbaivos.size() == 0){
            return null;
        }
        return srbaivos.get(srbaivos.size()-1);
    }

}
