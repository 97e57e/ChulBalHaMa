package com.example.leeseungchan.chulbalhama.Activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.leeseungchan.chulbalhama.Service.HamaService;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.UI.habit.HabitListFragment;
import com.example.leeseungchan.chulbalhama.UI.personal_info.PersonalInfoFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private FragmentManager fragmentManager;
    private HabitListFragment habitListFragment;
    private PersonalInfoFragment personalInfoFragment;
    private FragmentTransaction transaction;
    public SharedPreferences prefs;
    public static boolean mainCreated=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainCreated=true;

        /* 위치 권한 받는 코드 */
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }

        DBHelper dbHelper = new DBHelper(this);
        prefs = getSharedPreferences("Pref", MODE_PRIVATE);
//        dbHelper.setDays();
//        dbHelper.setUser();

        setMain();

        checkFirstRun();
    }

    private void setMain(){
        // set up toolbar on top
        Toolbar toolbarMain = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbarMain);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // drawable
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbarMain, R.string.openDrawerNavigation,R.string.closeDrawerNavigation);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        habitListFragment = new HabitListFragment();
        personalInfoFragment = new PersonalInfoFragment();

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, habitListFragment).commitAllowingStateLoss();

        setTitle(R.string.app_name);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_main) {
            if(!habitListFragment.isAdded()){
                replaceFragment(habitListFragment, "habit_list");
                setTitle(R.string.app_name);
            }
        } else if (id == R.id.nav_info) {
            if(!personalInfoFragment.isAdded()){
                replaceFragment(personalInfoFragment, "personal_info");
                setTitle(R.string.title_personal);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment, String tag){
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment, tag)
                .commitNowAllowingStateLoss();
    }

    @Override
    public void setTitle(int id){
        TextView title = (TextView)findViewById(R.id.toolbar_title);
        title.setText(id);
    }

    public void checkFirstRun(){
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        DBHelper dbHelper = new DBHelper(this);
        if(isFirstRun)
        {
            dbHelper.setDays();
            dbHelper.setUser();
            Intent newIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(newIntent);

            prefs.edit().putBoolean("isFirstRun",false).apply();
            //처음만 true 그다음부터는 false 바꾸는 동작
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission","승인!");
            Intent intent = new Intent(MainActivity.this, HamaService.class);

            startService(intent);
        } else {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            localBuilder.setTitle("권한 설정")
                    .setMessage("위치 권한을 허용하지 않으면 하마 서비스 이용이 불가합니다.")
                    .setPositiveButton("권한 설정하러 가기", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt){
                            try {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                startActivity(intent);
                            }
                        }})
                    .setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            Toast.makeText(getApplication(),"키키",Toast.LENGTH_SHORT).show();
                        }})
                    .create()
                    .show();
        }
        return;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainCreated=false;
    }

}
