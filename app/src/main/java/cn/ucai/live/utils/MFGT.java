package cn.ucai.live.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.easeui.domain.User;

import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.ui.activity.MainActivity;


/**
 * Created by Administrator on 2017/3/16.
 */

public class MFGT {
    public static void finish(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void startActivity(Activity activity, Class cls) {
        activity.startActivity(new Intent(activity, cls));
        activity.overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
    }

    public static void gotoMain(Activity activity) {
        startActivity(activity, MainActivity.class);
    }

    private static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
