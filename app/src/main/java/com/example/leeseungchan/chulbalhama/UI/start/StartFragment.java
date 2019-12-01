package com.example.leeseungchan.chulbalhama.UI.start;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leeseungchan.chulbalhama.Activities.LocationInfoActivity;
import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.VO.LocationVO;

public class StartFragment extends Fragment {
    private EditText userName;
    private LocationVO startPoint;
    private Button starting;
    private Button dest;
    private LocationVO endPoint;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_first_main, container, false);
    
        userName = v.findViewById(R.id.user_name);
        starting = v.findViewById(R.id.button_for_start);
        starting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPoint = new LocationVO();
                startLocation(0, startPoint);
            }
        });
        dest = v.findViewById(R.id.button_for_dest);
        dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endPoint = new LocationVO();
                startLocation(1, endPoint);
            }
        });
        Button store = v.findViewById(R.id.store);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEvertThingInserted()) {
                    updateUsername();
                }
            }
        });

        return v;
    }
    private void startLocation(int type, LocationVO locationVO){
        Intent intent = new Intent(getContext(), LocationInfoActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("locationVO", locationVO);
        startActivity(intent);
    }
    private void updateUsername(){
        String newName = userName.getText().toString();
        DBHelper dbHelper = DBHelper.getInstance();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("update user set name=\""+ newName + "\" where _id=1");
        db.close();
        getActivity().finish();
    }
    
    
    private boolean checkEvertThingInserted(){
        if(!isNameEmpty() && !isStartEmpty() && !isEndEmpty()){
            return true;
        }
        return false;
    }
    
    private boolean isNameEmpty(){
        if(userName.getText().toString().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "이름을 입력해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    
    private boolean isStartEmpty(){
        if(startPoint == null || startPoint.getName().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "출발지를 입력해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    private boolean isEndEmpty(){
        if(endPoint == null || startPoint.getName().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "도착지를 입력해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
