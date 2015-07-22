package com.felicity.mummyding.felicitychatrobot;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;


public class FelicityActivity extends Activity implements AMapLocationListener,View.OnClickListener {

    ImageButton start_btn;
    private LocationManagerProxy mLocationManagerProxy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_felicity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        start_btn = (ImageButton) findViewById(R.id.start_btn);
        start_btn.setOnClickListener(this);
         getLocation();

    }

    /**
     * 初始化定位
     */
    public void getLocation() {

        mLocationManagerProxy = LocationManagerProxy.getInstance(this);

        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, -1, 15, this);

        mLocationManagerProxy.setGpsEnable(false);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
            //获取位置信息
            VALUES.Latitude = amapLocation.getLatitude();
            VALUES.Longitude = amapLocation.getLongitude();
            Bundle locBundle = amapLocation.getExtras();
            VALUES.CITY = amapLocation.getCity().substring(0,amapLocation.getCity().length()-1);

            if (locBundle != null) {
                VALUES.LOCATION = locBundle.getString("desc");
            }
       }
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_btn:
                Intent intent = new Intent(this,ChatActivity.class);
                startActivity(intent);
                break;
        }
    }
}
