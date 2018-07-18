package abhishek.redvelvet.com.perfect_call_manager.animation;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;

import abhishek.redvelvet.com.perfect_call_manager.R;

/**
 * Created by abhishek on 17/7/18.
 */

public class SlideUtil {

    static Context context;
    static View view;
    public SlideUtil(Context context,View view){
        this.context = context;
        this.view = view;
    }

    public static void slideInFromLeft(){
        runAnimation(R.anim.slide_from_left);
    }

    public static void slideOutToLeft(){
        runAnimation(R.anim.slide_to_left);
    }

    public static void slideInFromRight(){
        runAnimation(R.anim.slide_from_right);
    }

    public static void slideOutToRight(){
        runAnimation(R.anim.slide_to_right);
    }

    private static void runAnimation(int animationId){
        view.startAnimation(AnimationUtils.loadAnimation(context,animationId));
    }
}
