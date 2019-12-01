package com.example.leeseungchan.chulbalhama.UI.habit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.leeseungchan.chulbalhama.Activities.AddHabitActivity;
import com.example.leeseungchan.chulbalhama.Adpater.HabitAdapter;
import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.VO.HabitsVO;

import java.util.ArrayList;


public class HabitListFragment extends Fragment {
    public static Context mContext;
    ArrayList <HabitsVO> habits = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_habit_list, container, false);
        
        // set up recycler view
        setRecyclerView(v);

        // set add button
        Button habitAddButton = v.findViewById(R.id.add);
        habitAddButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                startActivity(intent);
    
            }
        });

        mContext = getContext();

        return v;
    }

    private void retrieve(){
        habits.clear();

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select * from habits";
        Cursor c = db.rawQuery(sql, null);
        while(c.moveToNext()){
            int id = c.getInt(0);
            String name = c.getString(1);
            String prepare = c.getString(2);
            int due = c.getInt(3);

            HabitsVO h = new HabitsVO(id, name, due, prepare, 0);
            habits.add(h);
        }
    }
    
    private void setRecyclerView(View v){
        RecyclerView recyclerView;
        recyclerView = (RecyclerView) v.findViewById(R.id.list);
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    
        RecyclerView.Adapter mAdapter;
        mAdapter = new HabitAdapter(habits);
        recyclerView.setAdapter(mAdapter);
    
        retrieve();
    }
    
}
