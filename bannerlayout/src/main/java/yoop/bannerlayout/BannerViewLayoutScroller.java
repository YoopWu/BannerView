package yoop.bannerlayout;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class BannerViewLayoutScroller extends Scroller {

    private int mDuration = 800;

    public BannerViewLayoutScroller(Context context) {
        super(context);
    }

    public BannerViewLayoutScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public BannerViewLayoutScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setDuration(int time) {
        mDuration = time;
    }

}
