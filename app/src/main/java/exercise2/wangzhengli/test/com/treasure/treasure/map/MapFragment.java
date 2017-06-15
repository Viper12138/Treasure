package exercise2.wangzhengli.test.com.treasure.treasure.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import Util.ActivityUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import exercise2.wangzhengli.test.com.treasure.R;
import exercise2.wangzhengli.test.com.treasure.custom.TreasureView;
import exercise2.wangzhengli.test.com.treasure.treasure.Area;
import exercise2.wangzhengli.test.com.treasure.treasure.Treasure;
import exercise2.wangzhengli.test.com.treasure.treasure.TreasureRepo;
import exercise2.wangzhengli.test.com.treasure.treasure.detail.TreasureDetailActivity;

/**
 * Created by Administrator on 2017/6/12.
 */

public class MapFragment extends Fragment implements MapMvpView {
    private static final int LOCATION_REQUEST_CODE = 100;

    @BindView(R.id.center)
    Space center;
    @BindView(R.id.iv_located)
    ImageView ivLocated;
    @BindView(R.id.btn_HideHere)
    Button mBtnHideHere;
    @BindView(R.id.centerLayout)
    RelativeLayout mCenterLayout;
    @BindView(R.id.iv_scaleUp)
    ImageView ivScaleUp;
    @BindView(R.id.iv_scaleDown)
    ImageView ivScaleDown;
    @BindView(R.id.tv_located)
    TextView tvLocated;
    @BindView(R.id.tv_satellite)
    TextView mTvSatellite;
    @BindView(R.id.tv_compass)
    TextView tvCompass;
    @BindView(R.id.ll_locationBar)
    LinearLayout llLocationBar;
    @BindView(R.id.tv_currentLocation)
    TextView tvCurrentLocation;
    @BindView(R.id.iv_toTreasureInfo)
    ImageView ivToTreasureInfo;
    @BindView(R.id.et_treasureTitle)
    EditText etTreasureTitle;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.layout_bottom)
    FrameLayout mLayoutBottom;
    @BindView(R.id.map_frame)
    FrameLayout mMapFrame;
    @BindView(R.id.treasureView)
    TreasureView mTreasureView;
    @BindView(R.id.hide_treasure)
    RelativeLayout mHideTreasure;
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private LocationClient mLocationClient;
    private static LatLng mCurrentLocation;
    private String mCurrentAddr;
    private boolean isFirst = true;
    private LatLng mCurrentStatus;
    private MapPresenter mMapPresenter;
    private Marker mCurrentMarker;
    private ActivityUtils mActivityUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        //检测权限有没有成功
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //没有成功需要向用户申请
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mMapPresenter = new MapPresenter(this);
        mActivityUtils = new ActivityUtils(this);
        // 初始化地图
        initMapView();
        // 初始化定位相关
        initLocation();
    }

    private void initLocation() {
        // 前置：激活定位的图层
        mBaiduMap.setMyLocationEnabled(true);
        // 1. 第一步，初始化LocationClient类
        mLocationClient = new LocationClient(getContext().getApplicationContext());
        // 2. 第二步，配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setCoorType("bd09ll");// 设置坐标类型，默认gcj02，会有偏差
        option.setIsNeedAddress(true);// 需要地址信息
        // 设置参数给LocationClient
        mLocationClient.setLocOption(option);

        // 3. 第三步，实现BDLocationListener接口
        mLocationClient.registerLocationListener(mBDLocationListener);

        // 4. 第四步，开始定位
        mLocationClient.start();
    }

    // 定位监听
    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                mLocationClient.requestLocation();
                return;
            }
            // 直接拿到定位的经纬度信息
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            // 定位的位置和地址
            mCurrentLocation = new LatLng(latitude, longitude);
            mCurrentAddr = bdLocation.getAddrStr();
            Log.i("TAG", "定位的位置：" + mCurrentAddr + ",经纬度：" + latitude + "," + longitude);
            //地图上进行定位数据的设置
            MyLocationData locationData = new MyLocationData.Builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)//定位精度圈的大小
                    .build();
            mBaiduMap.setMyLocationData(locationData);
            // 移动到定位的地方：第一次的时候，自动移动到定位的位置
            if (isFirst) {
                // 自动移动
                moveToLocation();
                isFirst = false;
            }
        }
    };

    private void initMapView() {
/**
 * 1. 创建地图控件
 * 2. 在布局中添加地图的控件：FrameLayout
 * 3. 拿到地图的操作类
 */

        // 地图的状态
        MapStatus mapStatus = new MapStatus.Builder()
                .rotate(0)// 旋转的角度
                .zoom(15)// 默认是12，范围3-21
                .overlook(0)// 俯仰的角度
                .build();

        // 设置地图的信息
        BaiduMapOptions baiduMapOptions = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(true)// 是否显示指南针，默认显示
                .zoomGesturesEnabled(true)// 是否允许缩放手势
                .scaleControlEnabled(false)// 不显示比例尺
                .zoomControlsEnabled(false)// 不显示缩放的控件
                ;
        mMapView = new MapView(getContext(), baiduMapOptions);
        mMapFrame.addView(mMapView, 0);
        mBaiduMap = mMapView.getMap();
        // 设置地图状态的监听
        mBaiduMap.setOnMapStatusChangeListener(mStatusChangeListener);
        //设置覆盖物的点击监听
        mBaiduMap.setOnMarkerClickListener(mMarkerClickListener);

    }

    //设置覆盖物的点击监听
    private BaiduMap.OnMarkerClickListener mMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            // 对当前点击的做一个管理
            if (mCurrentMarker != null) {
                if (mCurrentMarker != marker) {
                    mCurrentMarker.setVisible(true);// 点击了其他的，把之前的显示出来
                }
            }
            mCurrentMarker = marker;
            // 点击展示InfoWindow，当前的覆盖物不可见
            mCurrentMarker.setVisible(false);
            // 1. 创建InfoWindow
            InfoWindow infoWindow = new InfoWindow(dot_expand, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {
                @Override// infoWindow的点击监听：infoWindow消失，Marker显示
                public void onInfoWindowClick() {
                    if (mCurrentMarker != null) {
                        mCurrentMarker.setVisible(true);
                    }
                    // 隐藏InfoWindow
                    mBaiduMap.hideInfoWindow();
                }
            });
            // 2. 地图上展示
            mBaiduMap.showInfoWindow(infoWindow);
            // 显示TreasureView
            int id = marker.getExtraInfo().getInt("id");
            mTreasureView.bindTreasure(TreasureRepo.getInstance().getTreasure(id));

            // 视图切换为宝藏选中的视图
            changeUIMode(UI_MODE_SELECT);
            return false;
        }
    };
    // 覆盖物图标
    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_dot);
    private BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);

    // 添加覆盖物的方法
    private void addMarker(LatLng latLng, int treasureId) {

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)// 覆盖物添加的位置
                .icon(dot)// 覆盖物的图标
                .anchor(0.5f, 0.5f)// 居中显示
                ;
        // 将宝藏id信息存储到覆盖物中
        Bundle bundle = new Bundle();
        bundle.putInt("id", treasureId);
        markerOptions.extraInfo(bundle);

        // 添加覆盖物
        mBaiduMap.addOverlay(markerOptions);
    }

    // 将定位的位置返回出去，供其它来使用
    public static LatLng getMyLocation() {
        return mCurrentLocation;
    }

    // 设置地图状态的监听
    private BaiduMap.OnMapStatusChangeListener mStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            // 拿到地图状态的位置
            LatLng target = mapStatus.target;

            // 地图的状态确实发生变化了
            if (target != MapFragment.this.mCurrentStatus) {

                // 根据当前地图的状态获取当前区域及区域内数据
                updateMapArea();

                // 当前的地图状态记录
                MapFragment.this.mCurrentStatus = target;
            }

        }

    };

    // 区域的确定及区域内数据的获取
    private void updateMapArea() {

        // 拿到地图的状态
        MapStatus mapStatus = mBaiduMap.getMapStatus();

        // 地图位置的经纬度
        double longitude = mapStatus.target.longitude;
        double latitude = mapStatus.target.latitude;

        // 根据当前经纬度的向上和向下取整确定区域
        Area area = new Area();
        area.setMinLng(Math.ceil(longitude));
        area.setMaxLat(Math.ceil(latitude));
        area.setMinLng(Math.floor(longitude));
        area.setMinLat(Math.floor(latitude));

        // TODO: 2017/6/13 根据当前的区域进行数据获取：拿到当前区域的宝藏信息
        // 根据当前的区域进行数据获取：拿到当前区域的宝藏信息
        mMapPresenter.getTreasure(area);
    }

    @OnClick(R.id.tv_located)
    public void moveToLocation() {
        // 更新的是地图的状态
        MapStatus mapStatus = new MapStatus.Builder()
                .target(mCurrentLocation)// 位置在定位处
                .rotate(0)
                .overlook(0)
                .zoom(19)
                .build();

        // 更新的状态
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);

        // 更新地图状态
        mBaiduMap.animateMapStatus(mapStatusUpdate);
    }

    // 卫星视图和普通视图的切换
    @OnClick(R.id.tv_satellite)
    public void switchMapType() {
        // 先拿到当前的地图类型
        int mapType = mBaiduMap.getMapType();
        // 切换类型
        mapType = (mapType == BaiduMap.MAP_TYPE_NORMAL) ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL;
        // 文字的变化
        String msg = (mapType == BaiduMap.MAP_TYPE_NORMAL) ? "卫星" : "普通";
        mTvSatellite.setText(msg);
        mBaiduMap.setMapType(mapType);
    }

    // 指南针
    @OnClick(R.id.tv_compass)
    public void switchCompass() {
        // 当前的地图指南针有没有显示
        boolean enabled = mBaiduMap.getUiSettings().isCompassEnabled();
        mBaiduMap.getUiSettings().setCompassEnabled(!enabled);
    }

    // 地图的缩放
    @OnClick({R.id.iv_scaleUp, R.id.iv_scaleDown})
    public void scallMap(View view) {
        switch (view.getId()) {
            case R.id.iv_scaleDown:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
            case R.id.iv_scaleUp:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
        }
    }
    // 宝藏信息卡片的点击事件
    @OnClick(R.id.treasureView)
    public void clickTreasureView(){
        // 跳转到宝藏详情页面，展示宝藏信息，将宝藏的信息传递过去
        int id = mCurrentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
        TreasureDetailActivity.open(getContext(),treasure);
    }
    // 处理权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                // 用户授权成功了
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 重新定位
                    mLocationClient.requestLocation();
                } else {
                    // 显示吐丝、提示框都可以
                }
                break;
        }
    }
    /**
     * 视图切换的方法：根据各种控件的显示和隐藏
     * 普通的视图
     * 宝藏选中的视图
     * 埋藏宝藏的视图
     *
     */
    private static final int UI_MODE_NORMAL = 0;// 普通视图
    private static final int UI_MODE_SELECT = 1;// 宝藏选中的视图
    private static final int UI_MODE_HIDE = 2;// 埋藏宝藏视图

    private static int mUIMode = UI_MODE_NORMAL;// 当前的视图

    public void changeUIMode(int uiMode) {
        if (mUIMode==uiMode) return;
        mUIMode = uiMode;
        switch(uiMode){
            // 切换为普通视图
            case UI_MODE_NORMAL:
                if (mCurrentMarker!=null){
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mLayoutBottom.setVisibility(View.GONE);
                mCenterLayout.setVisibility(View.GONE);
                break;
            // 切换到宝藏选中的视图
            case UI_MODE_SELECT:
                mLayoutBottom.setVisibility(View.VISIBLE);
                mTreasureView.setVisibility(View.VISIBLE);
                mHideTreasure.setVisibility(View.GONE);
                mCenterLayout.setVisibility(View.GONE);
                break;
            // 切换到埋藏视图
            case UI_MODE_HIDE:
                if (mCurrentMarker!=null){
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mCenterLayout.setVisibility(View.VISIBLE);
                mLayoutBottom.setVisibility(View.GONE);
                mBtnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLayoutBottom.setVisibility(View.VISIBLE);
                        mTreasureView.setVisibility(View.GONE);
                        mHideTreasure.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }
    }
    // ------------------------视图接口里面具体的视图方法---------------------------
    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void setTreasureData(List<Treasure> list) {
        // 再次进行请求，先将地图上的宝藏清除
        mBaiduMap.clear();// 清空地图上的所有的覆盖物

        for (Treasure treasure :
                list) {
            // 拿到每一个宝藏的数据，将宝藏的数据以添加地图覆盖物的形式显示在地图上
            LatLng latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());
            addMarker(latLng, treasure.getId());
        }
    }
}
