package com.android.launcher3.t9;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.android.launcher3.LauncherModel;
import com.android.launcher3.R;
import com.android.launcher3.t9.blurImage.BlurBehind;

/**
 *t9 search 整合
 * tos launcher
 */
public class MainActivity extends Activity {
    private static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mContext = getContext();
        AppInfoHelper.getInstance().setBaseAllAppInfos(LauncherModel.allAddAppItems);
        BlurBehind.getInstance()//在你需要添加模糊或者透明的背景中只需要设置这几行简单的代码就可以了
                .withAlpha(99)
                .withFilterColor(Color.parseColor("#030a09"))
                .setBackground(this);
    }
    public static Context getContext() {
        return mContext;
    }

}
