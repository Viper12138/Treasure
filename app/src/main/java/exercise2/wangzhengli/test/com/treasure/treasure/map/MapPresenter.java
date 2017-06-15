package exercise2.wangzhengli.test.com.treasure.treasure.map;

import java.util.List;

import exercise2.wangzhengli.test.com.treasure.network.NetClient;
import exercise2.wangzhengli.test.com.treasure.treasure.Area;
import exercise2.wangzhengli.test.com.treasure.treasure.Treasure;
import exercise2.wangzhengli.test.com.treasure.treasure.TreasureRepo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/6/14.
 */

public class MapPresenter  {
    private Area mArea;
    private MapMvpView mMapMvpView;


    public MapPresenter(MapMvpView mMapMvpView) {
        this.mMapMvpView = mMapMvpView;
    }
    //获取宝藏数据
    public void getTreasure(Area area){
        //当前的区域没有缓存
        if(TreasureRepo.getInstance().isCached(area)){
        return;
        }
        mArea =area;
        Call<List<Treasure>> listCall= NetClient.getInstance().getTreasureApi().getTreasureInArea(area);
        listCall.enqueue(mListCallback);
    }
    private Callback<List<Treasure>> mListCallback=new Callback<List<Treasure>>() {
        @Override
        public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {
            if (response.isSuccessful()){
                // 拿到响应体的数据
                List<Treasure> treasureList = response.body();

                if(treasureList==null){
                    // 弹出一个吐司说明
                    mMapMvpView.showMessage("未知的错误");
                    return;
                }

                // 做一个缓存：缓存请求的宝藏和区域
                TreasureRepo.getInstance().addTreasure(treasureList);
                TreasureRepo.getInstance().cache(mArea);

                // 拿到数据：给MapFragment返回数据，在地图上进行展示
                mMapMvpView.setTreasureData(treasureList);
            }
        }

        @Override
        public void onFailure(Call<List<Treasure>> call, Throwable t) {
            // 弹出吐司
            mMapMvpView.showMessage("请求失败："+t.getMessage());
        }
    };
}
