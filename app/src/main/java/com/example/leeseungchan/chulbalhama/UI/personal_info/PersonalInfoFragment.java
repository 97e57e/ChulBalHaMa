package com.example.leeseungchan.chulbalhama.UI.personal_info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.leeseungchan.chulbalhama.Adpater.DestinationAdapter;
import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.Activities.LocationInfoActivity;
import com.example.leeseungchan.chulbalhama.UI.components.CustomChangeDeleteItem;
import com.example.leeseungchan.chulbalhama.VO.DestinationsVO;
import com.example.leeseungchan.chulbalhama.VO.UserVO;

import java.util.ArrayList;

public class PersonalInfoFragment extends Fragment{

    private ArrayList<DestinationsVO> destinations = new ArrayList<>();
    private DBHelper dbHelper;
    private UserVO userVO;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState) {
        View v = inflater.inflate(R.layout.fragment_personal_info, container, false);
        dbHelper = DBHelper.getInstance(getContext());
        
        userVO = getUserVO();
        view = v;
        
        /* name */
        setPersonalInfoChangeDeleteItem(v, R.id.info_name);


        /* start point */
        setPersonalInfoChangeDeleteItem(v, R.id.info_start);
        

        /* destination */
        LinearLayout destination = v.findViewById(R.id.info_destination);
        Button endPointChangeBtn = destination.findViewById(R.id.button_for_selection);
        endPointChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LocationInfoActivity.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }
        });

        TextView endPointName = destination.findViewById(R.id.guide_for_selection);
        endPointName.setText(R.string.setting_destination);

        EditText endPointDesc = destination.findViewById(R.id.input_for_selection);
        endPointDesc.setVisibility(View.INVISIBLE);
        destination.removeView(endPointDesc);

        RecyclerView destinationRecycler = destination.findViewById(R.id.list);
        RecyclerView.LayoutManager layoutManager =
            new LinearLayoutManager(
                getContext(),
                RecyclerView.VERTICAL,
                false
            );
        destinationRecycler.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter;
        mAdapter = new DestinationAdapter(destinations);
        destinationRecycler.setAdapter(mAdapter);

        retrieve();

        return v;
    }

    public void retrieve(){
        destinations.clear();
        DBHelper dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select destination_name, _id from destinations";
        Cursor c = db.rawQuery(sql, null);
        while(c.moveToNext()){
            String name = c.getString(0);
            int id = c.getInt(1);

            DestinationsVO h = new DestinationsVO();
            h.setDestinationName(name);
            h.setId(id);
            destinations.add(h);
        }
    }
    
    private void setPersonalInfoChangeDeleteItem(View v, int id){
        LinearLayout layout = v.findViewById(id);
        CustomChangeDeleteItem item= new CustomChangeDeleteItem(layout);
        // set title text
        item.setTitle(getPersonalInfoTitle(id));
        // set desc invisible
        item.setVisibility(item.DESCRIPTION, View.INVISIBLE);
        // set change button text & onClickListener
        item.setChange(getResources().getString(R.string.button_change));
        setOnClickListener(item.getChange(), layout);
        // set delete button Gone
        item.setVisibility(item.DELETE_BTN, View.GONE);
    }
    
    private String getPersonalInfoTitle(int id){
        String title = null;
        switch (id){
            case R.id.info_name:
                title = userVO.getName();
                break;
            case R.id.info_start:
                title = userVO.getStartingName();
        }
        return title;
    }
    
    private void setOnClickListener(View target, final View root){
        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (root.getId()){
                    case R.id.info_name:
                        setNameDialog();
                        break;
                    case R.id.info_start:
                        Intent intent = new Intent(getContext(), LocationInfoActivity.class);
                        intent.putExtra("type", 0);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void setNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("이름 바꾸기");
        View viewInflated =
                LayoutInflater.from(getContext())
                        .inflate(R.layout.dialog_edit_text, (ViewGroup) getView(), false);

        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String newName = input.getText().toString();
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                db.execSQL("update user set name=\""+ newName + "\" where _id=1");
                db.close();
    
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
    
    private UserVO getUserVO(){
        dbHelper = DBHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
    
        Cursor cursor =  db.rawQuery("select * from user where _id=1", null);
        cursor.moveToNext();
        
        UserVO userVO = new UserVO(
            cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3)
        );
        db.close();
        return userVO;
    }
    
    private void refresh(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }
}
