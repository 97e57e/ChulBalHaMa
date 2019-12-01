package com.example.leeseungchan.chulbalhama.UI.map;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.UI.location_info.DestinationInfoFragment;
import com.example.leeseungchan.chulbalhama.UI.location_info.StartingPointInfoFragment;
import com.example.leeseungchan.chulbalhama.VO.LocationVO;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class MapAddFragment extends Fragment implements View.OnClickListener{

    private Context context;
    String tmpLat, tmpLon;
    double saveLat, saveLon;
    Bundle bundle;

    /* TMap 세팅*/
    TMapData tMapData = new TMapData();
    TMapView tMapView;
    TMapMarkerItem markerItem = new TMapMarkerItem();
    Button register_btn;

    /*기타 컴포넌트*/
    EditText search_box; //주소 검색창

    public static MapAddFragment newInstance(Bundle bundle){
        MapAddFragment v = new MapAddFragment();
        v.bundle = bundle;
        return v;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saveInstanceState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map_select, container,false);
        context = container.getContext();

        Button registerLocation = rootView.findViewById(R.id.register_btn);
        registerLocation.setOnClickListener(this);

        /* 지도 세팅 */
        RelativeLayout relativeLayoutTmap = (RelativeLayout) rootView.findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(getActivity());
        tMapView.setSKTMapApiKey("887bc5c0-3f62-4cc7-b1b9-957f10d3dfc6");
        tMapView.setHttpsMode(true);
        relativeLayoutTmap.addView( tMapView );
        tMapView.setCenterPoint( 126.985302, 37.570841 );
        markerItem.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정

        /* 검색 로직 */
        search_box = (EditText) rootView.findViewById(R.id.editText);
        search_box.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER){

                    if ( search_box.getText().toString().length() == 0 ) { // 검색창에 빈 스트링 입력 했을 경우
                        Toast.makeText(context, "검색어를 입력 해 주세요", Toast.LENGTH_LONG).show();

                    } else {
                        // 검색 결과 반환 callBack 메소드
                        tMapData.findAllPOI(search_box.getText().toString(), new TMapData.FindAllPOIListenerCallback() {
                            @Override
                            public void onFindAllPOI(ArrayList poiItem) {
                                for(int i = 0; i < poiItem.size(); i++) {
                                    TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
                                    Log.d("POI Name: ", item.getPOIName().toString() + ", " +
                                            "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
                                            "Point: " + item.getPOIPoint().toString());
                                }
                                if(poiItem.size()==0){
                                    Toast.makeText(context, "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
                                } else {
                                    TMapPOIItem item = (TMapPOIItem) poiItem.get(0);
                                    markerItem.setTMapPoint( item.getPOIPoint() ); // 마커의 좌표 지정
                                    tMapView.addMarkerItem("markerItem1", markerItem); // 지도에 마커 추가
                                    tmpLat = item.getPOIPoint().toString().split(" ")[1];
                                    tmpLon = item.getPOIPoint().toString().split(" ")[3];
                                    tMapView.setCenterPoint( Double.parseDouble(tmpLon), Double.parseDouble(tmpLat) ); // 지도 이동
                                    saveLat = Double.parseDouble(tmpLat);
                                    saveLon = Double.parseDouble(tmpLon);
                                }
                                Log.d("finish Tag", "검색 끝");
                            }
                        });
                    }
                    return true;
                }
                return false;
            }
        });

        /* MAP 터치 이벤트*/
        tMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            double pressed_X;
            double pressed_Y;
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                Log.d("x" , Float.toString(pointF.x));
                Log.d("y" , Float.toString(pointF.y));
                Log.d("x", tMapPoint.toString());
                tmpLat = tMapPoint.toString().split(" ")[1];
                tmpLon = tMapPoint.toString().split(" ")[3];
                pressed_X = Double.parseDouble(tmpLon);
                pressed_Y = Double.parseDouble(tmpLat);
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                Log.d("x" , Float.toString(pointF.x));
                Log.d("y" , Float.toString(pointF.y));
                Log.d("x", tMapPoint.toString());
                tmpLat = tMapPoint.toString().split(" ")[1];
                tmpLon = tMapPoint.toString().split(" ")[3];
                String result = "위도 : " + tmpLat + "경도 : " + tmpLon;
                if(Double.parseDouble(tmpLon) == pressed_X && Double.parseDouble(tmpLat) == pressed_Y){
                    Log.d("마커!","마커!");
                    markerItem.setTMapPoint( tMapPoint ); // 마커의 좌표 지정
                    saveLon = pressed_X;
                    saveLat = pressed_Y;
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fg;
        bundle.putString("address",search_box.getText().toString());

        LocationVO temp = (LocationVO) bundle.getSerializable("locationVO");
        temp.setLongitude(saveLon);
        temp.setLatitude(saveLat);
        if(view.getId() == R.id.register_btn){

            switch (bundle.getInt("type")) {
            case 0:
                fg = StartingPointInfoFragment.newInstance(bundle);
                if (!fg.isAdded()) {
                    transaction.replace(R.id.nav_host_fragment, fg)
                            .commitNowAllowingStateLoss();
                }
                break;
            case 1:
                fg = DestinationInfoFragment.newInstance(bundle);
                if (!fg.isAdded()) {
                    transaction.replace(R.id.nav_host_fragment, fg)
                            .commitNowAllowingStateLoss();
                }
                break;
            }

        }
    }

    private void setMap(FrameLayout map){
        //@todo implement map
    }
}
