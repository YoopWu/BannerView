package app.wu.yoop.bannerproject;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import yoop.bannerlayout.BannerViewLayout;
import yoop.bannerlayout.PageIndicator;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        BannerViewLayout.OnBannerListener {

    BannerViewLayout mBanner;
    PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBanner = findViewById(R.id.banner_view);
        mIndicator = findViewById(R.id.indicator);
        mBanner.setOnPageChangeListener(this);
        mBanner.setOnBannerListener(this);
        List<String> url = new ArrayList<>();
        url.add("http://pic69.nipic.com/file/20150608/9252150_134415115986_2.jpg");
        url.add("http://pic1.win4000.com/wallpaper/9/5450ae2fdef8a.jpg");
        url.add("http://pic32.nipic.com/20130823/13339320_183302468194_2.jpg");
        url.add("http://pic75.nipic.com/file/20150821/9448607_145742365000_2.jpg");
        mBanner.setBannerView(url).setImageCorner(18).start();
        mIndicator.setNumPages(url.size());
        mIndicator.setCurrentPage(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBanner.startAutoPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBanner.stopAutoPlay();
    }

    @Override
    public void onPageScrolled(final int i, final float v, final int i1) {

    }

    @Override
    public void onPageSelected(final int position) {
        if (mIndicator != null) mIndicator.setCurrentPage(position);
    }

    @Override
    public void onPageScrollStateChanged(final int i) {

    }

    @Override
    public void OnBannerClick(final int position) {
        Toast.makeText(this, "current position is " + position, Toast.LENGTH_SHORT).show();
    }
}
