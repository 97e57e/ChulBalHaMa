package com.example.leeseungchan.chulbalhama.UI.location_info;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.Activities.LocationInfoActivity;
import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.UI.components.CustomChangeDeleteItem;
import com.example.leeseungchan.chulbalhama.UI.map.MapAddFragment;
import com.example.leeseungchan.chulbalhama.VO.LocationVO;

import java.util.ArrayList;

public class StartingPointInfoFragment extends Fragment {
    private Bundle bundle;
    private LocationVO locationVO;

    public static StartingPointInfoFragment newInstance(Bundle bundle){
        StartingPointInfoFragment v = new StartingPointInfoFragment();
        v.bundle = bundle;
        return v;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_starting_info, container, false);

        bundle.putInt("type", 0);
        locationVO = (LocationVO) bundle.getSerializable("locationVO");
        setEditTextText((EditText) v.findViewById(R.id.starting_name), locationVO);

        /* starting setting view */
        setStartPointChangeDeleteItem(v, R.id.starting_setting);


        /* starting store */
        Button startStoreBtn = v.findViewById(R.id.store_starting);
        setButtonClickEvent(startStoreBtn);
        return v;
    }
    private void setEditTextText(EditText editText, LocationVO locationVO){
        String name = locationVO.getName();
        if(name != null) {
            editText.setText(name);
        }
        setNameListener(editText, locationVO);
    }
    
    private void setNameListener(EditText edit, final LocationVO locationVO){
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable arg0) {
                locationVO.setName(arg0.toString());
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
    }
    private void setStartPointChangeDeleteItem(View v, int id){
        LinearLayout layout = v.findViewById(id);
        CustomChangeDeleteItem item = new CustomChangeDeleteItem(layout);
        
        // starting TextView guide text
        setAddress(item, bundle.getString("address"));
        
        // set add button
        item.setChange(getResources().getString(R.string.button_setting));
        setButtonClickEvent(item.getChange());
        item.setVisibility(item.DELETE_BTN, View.GONE);
    }
    private void setAddress(CustomChangeDeleteItem item, String address){
        if(address == null)
            item.setTitle(getResources().getString(R.string.guide_address));
        else
            item.setTitle(address);
    }
    
    private void setButtonClickEvent(Button btn){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.store_starting){
                    if(checkEvertThingInserted()) {
                        updateStartPoint();
                        getActivity().finish();
                    }
                    
                } else {
                    fragmentChange(MapAddFragment.newInstance(bundle), R.id.nav_host_fragment);
                }
            }
        });
    }
    
    private void fragmentChange(Fragment fragment, int hostId){
        // start transaction
        FragmentTransaction transaction =
            getActivity().getSupportFragmentManager().beginTransaction();
        
        // if the fragment doesn't add to activity, make new one.
        if (!fragment.isAdded()) {
            transaction.replace(hostId, fragment).commitNowAllowingStateLoss();
        }
    }
    
    private void updateStartPoint(){
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "update user set starting_name=\""+locationVO.getName() +
            "\", starting_coordinate=\""+ locationVO.getCoordinate() + "\" where _id=1";
        db.execSQL(sql);
        db.close();
        getActivity().finish();
    }
    
    private boolean checkEvertThingInserted(){
        if(!isDestNameEmpty() && !isMapUnselected()){
            return true;
        }
        return false;
    }
    
    private boolean isDestNameEmpty(){
        if(locationVO.getName() == null || locationVO.getName().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "이름을 입력해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    
    private boolean isMapUnselected(){
        if(locationVO.getCoordinate() == null || locationVO.getCoordinate().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "장소를 설정해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}