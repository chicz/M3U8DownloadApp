package com.example.mym3u8down.utils;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

/**
 * author  yangc
 * date 2017/11/26
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated: 动画帮助类
 */
public class AnimUtils {

    /**
     * Sets out anim.
     *
     * @param view the view
     * @param ab   the ab
     * @return the out anim
     */
    public static ViewPropertyAnimatorCompat setOutAnim(View view, boolean ab, long duration, float alp) {
        return ViewCompat.animate(view).translationY(ab ? view.getHeight() : -view.getHeight())
                .setDuration(duration)
                .alpha(alp);
    }

    /**
     * Sets in anim.
     *
     * @param view the view
     * @return the in anim
     */
    public static ViewPropertyAnimatorCompat setInAnim(View view, long duration, float alp) {
        return ViewCompat.animate(view).translationY(0)
                .setDuration(duration)
                .alpha(alp);

    }

    /**
     * Sets out anim x.
     *
     * @param view the view
     * @param ab   the ab
     * @return the out anim x
     */
    public static ViewPropertyAnimatorCompat setOutAnimX(View view, boolean ab) {
        return ViewCompat.animate(view).translationX(ab ? view.getWidth() : -view.getWidth())
                .setDuration(500)
                .alpha(0.1f);
    }

    /**
     * Sets in anim x.
     *
     * @param view the view
     * @return the in anim x
     */
    public static ViewPropertyAnimatorCompat setInAnimX(View view) {
        return ViewCompat.animate(view).translationX(0)
                .setDuration(500)
                .alpha(1f);
    }

    /**
     * 动画回调接口
     */
    public interface AnimatorListener {
        /**
         * Show.
         *
         * @param isIn the is in
         */
        void show(boolean isIn);
    }

    /**
     * 进度更新回调接口
     */
    public interface UpdateProgressListener {
        /**
         * Update progress.
         *
         * @param position         the position
         * @param bufferedPosition the buffered position
         * @param duration         the duration
         */
        void updateProgress(long position, long bufferedPosition, long duration);
    }

}
