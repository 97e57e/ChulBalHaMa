/**
 * @todo implement drop down
 */
package com.example.leeseungchan.chulbalhama.UI.location_info;

import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.DayTimeDialog;
import com.example.leeseungchan.chulbalhama.Activities.LocationInfoActivity;
import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.UI.components.CustomChangeDeleteItem;
import com.example.leeseungchan.chulbalhama.UI.components.CustomSevenDayInfo;
import com.example.leeseungchan.chulbalhama.UI.map.MapAddFragment;
import com.example.leeseungchan.chulbalhama.VO.DestinationsVO;
import com.example.leeseungchan.chulbalhama.VO.LocationVO;
import com.google.android.material.shape.CutCornerTreatment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DestinationInfoFragment extends Fragment{

    private ArrayList<Boolean> days = new ArrayList<>();
    private ArrayList<Integer> time = new ArrayList<>();
    private LocationVO locationVO;
    private CustomSevenDayInfo sevenDayInfo;
    private ArrayList<String> dayOfWeekTime;
    
    Bundle bundle = new Bundle();

    public static DestinationInfoFragment newInstance(Bundle bundle){
        DestinationInfoFragment v = new DestinationInfoFragment();
        v.bundle = bundle;
        return v;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_destination_info, container, false);

        bundle.putInt("type", 1);
        locationVO = (LocationVO) bundle.getSerializable("locationVO");
        dayOfWeekTime = bundle.getStringArrayList("dayOfWeekTime");
    
        /* EditText to get name*/
        setEditTextText((EditText) v.findViewById(R.id.destination_name),locationVO);
        
        /* destination coordination view */
        setDestInfoChangeDeleteItem(v, R.id.destination_setting);


        /* time setting view */
        setDestInfoChangeDeleteItem(v, R.id.destination_time_duration);
        

        /* start time view */
        // set input layout
        setDestInfoChangeDeleteItem(v, R.id.destination_time_start);
        // set 2d array for day and time
        setDayOfWeekArray(v);
        
        
        /* destination store */
        insertDestination(v);

        return v;
    }
    
    public void setEditTextText(EditText editText, LocationVO locationVO){
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
    
    private void setDestInfoChangeDeleteItem(View v, int id){
        LinearLayout layout = v.findViewById(id);
        CustomChangeDeleteItem item = new CustomChangeDeleteItem(layout);
        // set title text view
        setTitle(id, item);
        // set description textView invisible
        item.setVisibility(item.DESCRIPTION, View.INVISIBLE);
        // set change button text & setOnClickListener
        item.setChange(getResources().getString(R.string.button_setting));
        setDestInfoClickListener(item.getChange(), layout);
        // set delete button gone
        item.setVisibility(item.DELETE_BTN, View.GONE);
    }
    
    private void setTitle(int id, CustomChangeDeleteItem item){
        switch (id){
            case R.id.destination_setting:
                setAddress(item, bundle.getString("address"));
                break;
            case R.id.destination_time_duration:
                setTime(item, locationVO);
                break;
            case R.id.destination_time_start:
                item.setVisibility(item.TITLE, View.GONE);
        }
    }
    
    private void setAddress(CustomChangeDeleteItem item, String address){
        if(address == null)
            item.setTitle(getResources().getString(R.string.guide_address));
        else
            item.setTitle(address);
    }
    private void setTime(CustomChangeDeleteItem time, LocationVO locationVO){
        int time_hour = locationVO.getTimeHour();
        int time_minute = locationVO.getTimeMin();
        
        if(time_hour > 0 && time_minute > 0){
            time.setTitle(time_hour + ":" + time_minute);
        }else{
            time.setTitle(getResources().getString(R.string.guide_when_time));
        }
    }
    
    private void insertDestination(View v){
        Button destinationStoreBtn = v.findViewById(R.id.store_destination);
        storeAndUpdateAndFinish(destinationStoreBtn);
    }
    
    private void setDestInfoClickListener(View target, final View root){
        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (root.getId()){
                    case R.id.destination_setting:
                        callMapFragment();
                        break;
                    case R.id.destination_time_duration:
                        showTimeDialog(locationVO);
                        break;
                    case R.id.destination_time_start:
                        DayTimeDialog dayTimeDialog = new DayTimeDialog(getContext());
                        dayTimeDialog.callFunction(days, time, sevenDayInfo);
                        break;
                }
            }
        });
    }
    
    private void showTimeDialog(final LocationVO locationVO){
        TimePickerDialog dialog = new TimePickerDialog(
            getContext(),
            android.R.style.Theme_Holo_Light_Dialog,
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    locationVO.setTime(hourOfDay, minute);
                }
            }, 0,0,true);
        
        
        dialog.show();
    }
    
    private void callMapFragment(){
        FragmentTransaction transaction =
            getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fg;
        fg = MapAddFragment.newInstance(bundle);
        if (!fg.isAdded()) {
            transaction.replace(R.id.nav_host_fragment, fg)
                .commitNowAllowingStateLoss();
        }
    }
    
    private void setDayOfWeekArray(View v){
        LinearLayout dayInput = v.findViewById(R.id.day_input);
        sevenDayInfo = new CustomSevenDayInfo(dayInput);
        ArrayList<String> isTempData = dayOfWeekTime;
        if(isTempData != null){
            sevenDayInfo.setSomeTimeRow(isTempData);
        }
    }
    
    private void storeAndUpdateAndFinish(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEvertThingInserted()) {
                    // insert destination data to sqlite db
                    insertDest(locationVO.getCoordinate(), locationVO.getTime(), locationVO.getName());
    
                    // update dayOfWeek table.
                    updateDayOfWeek();
    
                    getActivity().finish();
                }
            }
        });
    }
    
    private void insertDest(String coordinate, String time, String name){
        DBHelper dbHelper =  DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // destination
        String sql = "insert into destinations (coordinate, time, destination_name) values(?,?,?)";
       
        db.execSQL(sql, new Object[]{coordinate,time,name});
        db.close();
    }
    
    private void updateDayOfWeek(){
        ArrayList<String> times = new ArrayList<>();
        sevenDayInfo.getResultTimeDataInput(times);
        sevenDayInfo.updateTimeToDays(days,times);
    }
    
    private boolean checkEvertThingInserted(){
        if(!isDestNameEmpty() && !isMapUnselected() && !isTimeEmpty() && !isDayEmpty()){
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
    
    private boolean isTimeEmpty(){
        if(locationVO.getTime() == null || locationVO.getTime().length() == 0){
            Toast.makeText(getActivity().getApplicationContext(), "소요시간을 입력해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
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
    
    private boolean isDayEmpty(){
        if(days.isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "요일과 시간을 설정해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}