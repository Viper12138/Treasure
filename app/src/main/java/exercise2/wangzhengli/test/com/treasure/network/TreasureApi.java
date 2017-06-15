package exercise2.wangzhengli.test.com.treasure.network;

import java.util.List;

import exercise2.wangzhengli.test.com.treasure.treasure.Area;
import exercise2.wangzhengli.test.com.treasure.treasure.Treasure;
import exercise2.wangzhengli.test.com.treasure.user.User;
import exercise2.wangzhengli.test.com.treasure.user.login.LoginResult;
import exercise2.wangzhengli.test.com.treasure.user.register.RegisterResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface TreasureApi {
    // 登录的请求
    @POST("/Handler/UserHandler.ashx?action=login")
    Call<LoginResult> login(@Body User user);

    // 注册的请求
    @POST("/Handler/UserHandler.ashx?action=register")
    Call<RegisterResult> register(@Body User user);
   @POST("/Handler/TreasureHandler.ashx?action=show")
   Call<List<Treasure>> getTreasureInArea(@Body Area area);

}
