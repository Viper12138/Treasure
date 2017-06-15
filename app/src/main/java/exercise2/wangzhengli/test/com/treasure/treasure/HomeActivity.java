package exercise2.wangzhengli.test.com.treasure.treasure;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import Util.ActivityUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import exercise2.wangzhengli.test.com.treasure.MainActivity;
import exercise2.wangzhengli.test.com.treasure.R;
import exercise2.wangzhengli.test.com.treasure.treasure.map.MapFragment;
import exercise2.wangzhengli.test.com.treasure.user.UserPrefs;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigationView)
    NavigationView mNavigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    private ImageView mIvIcon;
    private ActivityUtils mActivityUtils;
    private MapFragment mMapFragment;
    private FragmentManager mFragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        //toolbar
        setSupportActionBar(mToolbar);
        if(getSupportActionBar()!=null){
            //不显示默认的标题
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.syncState();//同步状态
        mDrawerLayout.addDrawerListener(toggle);
        //侧滑菜单item的选择事件
        mNavigationView.setNavigationItemSelectedListener(this);
        // 找到侧滑的头布局中的头像控件
        mIvIcon = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.iv_usericon);
        mIvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/6/12 跳转到个人页面
                Toast.makeText(HomeActivity.this, "头像", Toast.LENGTH_SHORT).show();
            }
        });
        mActivityUtils = new ActivityUtils(this);

        mFragmentManager = getSupportFragmentManager();
        mMapFragment = (MapFragment) mFragmentManager.findFragmentById(R.id.mapFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 头像信息的更新
        String photo = UserPrefs.getInstance().getPhoto();
        if (photo!=null){
            // 加载头像：采用picasso
            Picasso
                    .with(this)
                    .load(photo)
                    .into(mIvIcon);

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_hide:
                // 埋藏宝藏
                // 埋藏宝藏
                mMapFragment.changeUIMode(2);// 切换成埋藏宝藏的视图
                break;
            case R.id.menu_logout:
                // 退出
                mActivityUtils.startActivity(MainActivity.class);
                finish();
                UserPrefs.getInstance().clearUser();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
