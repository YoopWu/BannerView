package yoop.bannerlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PageIndicator extends View {

    private static final int INDICATOR_COLOR = 0x4CFE5858;
    private static final int INDICATOR_COLOR_HL = 0xFFFE5858;

    private int mIndicatorWidth, mIndicatorHeight, mSelectedIndicatorWidth;
    private int dotSpace;
    private int mRadius;
    private int mCenterY;

    private int mDotColor = INDICATOR_COLOR;
    private int mDotColorSelected = INDICATOR_COLOR_HL;

    private int mNumPages;
    private int mCurrentPage;

    private Paint mPaint;
    private Paint mPaintShadow;
    private Paint mPaintCurrent;

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator);
        mDotColor = ta.getColor(R.styleable.PageIndicator_dotColor, INDICATOR_COLOR);
        mDotColorSelected = ta.getColor(R.styleable.PageIndicator_dotColorSelected, INDICATOR_COLOR_HL);
        mIndicatorHeight = ta.getDimensionPixelSize(R.styleable.PageIndicator_dotHeight, 30);
        mRadius = mIndicatorHeight / 2;
        mCenterY = mIndicatorHeight / 2;
        dotSpace = ta.getDimensionPixelSize(R.styleable.PageIndicator_dotSpace, 30);
        mIndicatorWidth = ta.getDimensionPixelSize(R.styleable.PageIndicator_dotWidth, 30);
        mSelectedIndicatorWidth = ta.getDimensionPixelSize(R.styleable.PageIndicator_dotSelectedWidth, 30);
        ta.recycle();
    }

    public void setNumPages(int numPages) {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mDotColor);
        }
        if (mPaintCurrent == null) {
            mPaintCurrent = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaintCurrent.setColor(mDotColorSelected);
        }
        if (mPaintShadow == null) {
            mPaintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaintShadow.setColor(0xff888888);
            mPaintShadow.setStyle(Paint.Style.STROKE);
        }
        if (mNumPages != numPages) {
            mNumPages = numPages;
            requestLayout();
        }
    }

    public void setCurrentPage(int currentPage) {
        if (mCurrentPage == currentPage) {
            return;
        }
        mCurrentPage = currentPage;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (mNumPages - 1) * (mIndicatorWidth + dotSpace) + mSelectedIndicatorWidth;
        setMeasuredDimension(width, mIndicatorHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mNumPages == 0) {
            return;
        }
        for (int i = 0; i < mNumPages; i++) {
            int x = 0;
            if (i > 0) {
                if (mCurrentPage < i) {
                    x = mSelectedIndicatorWidth + (i - 1) * mIndicatorWidth + i * dotSpace;
                } else {
                    x = i * mIndicatorWidth + i * dotSpace;
                }
            }
            if (i == mCurrentPage) {
                RectF rectF = new RectF(x,
                        mCenterY - mRadius,
                        (float) (x + mSelectedIndicatorWidth),
                        mCenterY + mRadius);
                canvas.drawRoundRect(rectF, 10, 10, mPaintCurrent);
            } else {
                RectF rectF = new RectF(x,
                        mCenterY - mRadius,
                        (float) (x + mIndicatorWidth),
                        mCenterY + mRadius);
                canvas.drawRoundRect(rectF, 10, 10, mPaint);
            }
        }
    }
}
