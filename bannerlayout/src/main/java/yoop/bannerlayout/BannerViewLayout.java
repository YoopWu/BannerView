package yoop.bannerlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BannerViewLayout extends LinearLayout implements ViewPager.OnPageChangeListener{

    private static final String TAG = BannerViewLayout.class.getSimpleName();

    private LayoutInflater mInflater;

    private BannerViewPager mViewPager;

    private int mVpMarginLeft, mVpMarginRight, mVpPageMargin;
    private int mCount;
    private int mCurrentItem;
    private int mDelayTime = 2000;
    private int mImageCorner;
    private boolean isAutoPlay = true;
    private boolean isScroll = true;

    private List<String> mImageUrls;
    private List<View> mBannerViews;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnBannerListener mListener;
    private BannerPageAdapter mPageAdapter;
    private BannerViewLayoutScroller mScroller;

    private Handler mInnerHandler = InnerHandler.getInstance();

    public BannerViewLayout(Context context) {
        this(context, null);
    }

    public BannerViewLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BannerViewLayout);
        mVpMarginLeft = ta.getDimensionPixelSize(R.styleable.BannerViewLayout_bl_vp_margin_left, 0);
        mVpMarginRight = ta.getDimensionPixelSize(R.styleable.BannerViewLayout_bl_vp_margin_right, 0);
        mVpPageMargin = ta.getDimensionPixelSize(R.styleable.BannerViewLayout_bl_vp_page_margin, 0);
        ta.recycle();
        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.layout_banner_lay, this, true);
        mViewPager = view.findViewById(R.id.view_pager);
        mViewPager.setPageMargin(mVpPageMargin);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.leftMargin = mVpMarginLeft;
        layoutParams.rightMargin = mVpMarginRight;
        mViewPager.setLayoutParams(layoutParams);
        mImageUrls = new ArrayList<>();
        mBannerViews = new ArrayList<>();
        initViewPagerScroll();

    }

    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new BannerViewLayoutScroller(mViewPager.getContext());
            mField.set(mViewPager, mScroller);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public int getVpMarginLeft() {
        return mVpMarginLeft;
    }

    public void setVpMarginLeft(int vpMarginLeft) {
        mVpMarginLeft = vpMarginLeft;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.leftMargin = mVpMarginLeft;
        mViewPager.setLayoutParams(layoutParams);
    }

    public int getVpMarginRight() {
        return mVpMarginRight;
    }

    public void setVpMarginRight(int vpMarginRight) {
        mVpMarginRight = vpMarginRight;RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.rightMargin = mVpMarginRight;
        mViewPager.setLayoutParams(layoutParams);
    }

    public int getVpPageMargin() {
        return mVpPageMargin;
    }

    public void setVpPageMargin(int vpPageMargin) {
        mVpPageMargin = vpPageMargin;
        mViewPager.setPageMargin(mVpPageMargin);
    }

    public BannerViewLayout isAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
        return this;
    }

    public BannerViewLayout setDelayTime(int delayTime) {
        this.mDelayTime = delayTime;
        return this;
    }

    public BannerViewLayout setOffscreenPageLimit(int limit) {
        if (mViewPager != null) {
            mViewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    public BannerViewLayout setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        mViewPager.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }

    public BannerViewLayout setImageCorner(int imageCorner) {
        mImageCorner = imageCorner;
        return this;
    }

    public BannerViewLayout setBannerView(List<String> imageUrls) {
        this.mImageUrls = imageUrls;
        this.mCount = imageUrls.size();
        return this;
    }

    public BannerViewLayout start() {
        setImageView();
        setData();
        return this;
    }

    private void setImageView() {
        for (int i = 0; i <= mCount + 1; i++) {
            RoundedImageView imageView = new RoundedImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setCornerRadius(mImageCorner);
            if (i == 0) {
                Glide.with(getContext()).load(mImageUrls.get(mCount - 1)).into(imageView);
                mBannerViews.add(imageView);
            } else if (i == mCount + 1) {
                Glide.with(getContext()).load(mImageUrls.get(0)).into(imageView);
                mBannerViews.add(imageView);
            } else {
                Glide.with(getContext()).load(mImageUrls.get(i - 1)).into(imageView);
                mBannerViews.add(imageView);
            }
        }
    }

    private void setData() {
        mCurrentItem = 1;
        if (mPageAdapter == null) {
            mPageAdapter = new BannerPageAdapter();
            mViewPager.addOnPageChangeListener(this);
        }
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setFocusable(true);
        mViewPager.setCurrentItem(1);
        if (isScroll && mCount > 1) {
            mViewPager.setScrollable(true);
        } else {
            mViewPager.setScrollable(false);
        }
        if (isAutoPlay)
            startAutoPlay();
    }

    public void startAutoPlay() {
        mInnerHandler.removeCallbacks(task);
        mInnerHandler.postDelayed(task, mDelayTime);
    }

    public void stopAutoPlay() {
        mInnerHandler.removeCallbacks(task);
    }

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (mCount > 1 && isAutoPlay) {
                mCurrentItem = mCurrentItem % (mCount + 1) + 1;
                if (mCurrentItem == 1) {
                    mViewPager.setCurrentItem(mCurrentItem, false);
                    mInnerHandler.post(task);
                } else {
                    mViewPager.setCurrentItem(mCurrentItem);
                    mInnerHandler.postDelayed(task, mDelayTime);
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isAutoPlay) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startAutoPlay();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopAutoPlay();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(toRealPosition(position), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentItem = position;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(toRealPosition(position));
        }
        if (position == 0) position = mCount;
        if (position > mCount) position = 1;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
        switch (state) {
            case 0://No operation
                if (mCurrentItem == 0) {
                    mViewPager.setCurrentItem(mCount, false);
                } else if (mCurrentItem == mCount + 1) {
                    mViewPager.setCurrentItem(1, false);
                }
                break;
            case 1://start Sliding
                if (mCurrentItem == mCount + 1) {
                    mViewPager.setCurrentItem(1, false);
                } else if (mCurrentItem == 0) {
                    mViewPager.setCurrentItem(mCount, false);
                }
                break;
            case 2://end Sliding
                break;
        }
    }

    private class BannerPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mBannerViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(mBannerViews.get(position));
            View view = mBannerViews.get(position);
            if (mListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.OnBannerClick(toRealPosition(position));
                    }
                });
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    /**
     * 返回真实的位置
     *
     * @param position
     * @return 下标从0开始
     */
    public int toRealPosition(int position) {
        int realPosition = (position - 1) % mCount;
        if (realPosition < 0)
            realPosition += mCount;
        return realPosition;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public BannerViewLayout setOnBannerListener(OnBannerListener listener) {
        this.mListener = listener;
        return this;
    }

    public interface OnBannerListener {
        void OnBannerClick(int position);
    }

    private static class InnerHandler extends Handler {
        private static WeakReference<InnerHandler> sInstanceRef;

        private InnerHandler() {
            super();
        }

        public static Handler getInstance() {
            if (sInstanceRef != null && sInstanceRef.get() != null) {
                return sInstanceRef.get();
            }
            InnerHandler instance = new InnerHandler();
            sInstanceRef = new WeakReference<>(instance);
            return instance;
        }
    }

}
