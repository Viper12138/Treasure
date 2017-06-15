package exercise2.wangzhengli.test.com.treasure.treasure.detail;

/**
 * Created by Administrator on 2017/6/15.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import exercise2.wangzhengli.test.com.treasure.R;
import exercise2.wangzhengli.test.com.treasure.treasure.Treasure;


// 宝藏详情页
public class TreasureDetailActivity extends AppCompatActivity {

    private static final String KEY_TREASURE = "key_treasure";

    /**
     * 对外提供一个跳转的方法：规范一下传递的数据
     */
    public static void open(Context context, Treasure treasure){
        Intent intent = new Intent(context,TreasureDetailActivity.class);
        intent.putExtra(KEY_TREASURE,treasure);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_detail);

        // 拿到传递的数据
        Treasure treasure = (Treasure) getIntent().getSerializableExtra(KEY_TREASURE);

        // 处理视图

    }
}