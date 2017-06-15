package exercise2.wangzhengli.test.com.treasure.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import exercise2.wangzhengli.test.com.treasure.R;
import exercise2.wangzhengli.test.com.treasure.treasure.Treasure;
import exercise2.wangzhengli.test.com.treasure.treasure.map.MapFragment;

/**
 * Created by Administrator on 2017/6/15.
 */

public class TreasureView extends RelativeLayout {
    @BindView(R.id.tv_treasureTitle)
    TextView mTvTreasureTitle;
    @BindView(R.id.tv_distance)
    TextView mTvDistance;
    @BindView(R.id.tv_treasureLocation)
    TextView mTvTreasureLocation;

    // 代码中使用：new Treasure()
    public TreasureView(Context context) {
        super(context);
        init();
    }

    // 布局中使用
    public TreasureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // 布局中使用，设置了style样式
    public TreasureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
// 视图的填充及绑定
        LayoutInflater.from(getContext()).inflate(R.layout.view_treasure, this, true);
        ButterKnife.bind(this);
    }
    // 对外提供一个方法:根据宝藏信息，填充视图里面的数据
    public void bindTreasure(@NonNull Treasure treasure){

        // 标题
        mTvTreasureTitle.setText(treasure.getTitle());
        // 地址
        mTvTreasureLocation.setText(treasure.getLocation());

        // 距离：宝藏距离我们有多远
        double distance = 0.00d;// 距离

        // 宝藏的位置
        LatLng latLng = new LatLng(treasure.getLatitude(),treasure.getLongitude());

        // 定位的位置
        LatLng myLocation = MapFragment.getMyLocation();
        if (myLocation==null){
            distance = 0.00d;
        }

        // 利用百度地图提供的计算工具
        distance = DistanceUtil.getDistance(latLng,myLocation);

        // 规范一下小数的样式：0.00
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String text = decimalFormat.format(distance / 1000) + "km";
        mTvDistance.setText(text);
    }
}
