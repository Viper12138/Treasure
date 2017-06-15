package exercise2.wangzhengli.test.com.treasure.treasure.map;

import java.util.List;

import exercise2.wangzhengli.test.com.treasure.treasure.Treasure;

/**
 * Created by Administrator on 2017/6/14.
 */

public interface MapMvpView {
    void showMessage(String msg);// 显示信息
    void setTreasureData(List<Treasure> list);// 设置数据
}
