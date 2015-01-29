package net.yazeed44.groceryshopping.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.ActionMenuView;
import android.util.AttributeSet;

/**
 * Created by yazeed44 on 1/29/15.
 */
public class ShoppingCartActionView extends ActionMenuView {
    private int mCounter = 0;

    private Paint mCounterPaint;

    public ShoppingCartActionView(Context context) {
        super(context);
        mCounterPaint = getCounterPaint();
    }

    public ShoppingCartActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCounterPaint = getCounterPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(mCounter + "", getCounterX(), getCounterY(), mCounterPaint);
    }

    private float getCounterX() {
        return getLeft() + getWidth() - 50;
    }

    private float getCounterY() {
        return getTop() - 50;
    }

    private Paint getCounterPaint() {
        final Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(14);
        return paint;
    }

    public void updateCounter(final int newValue) {
        mCounter = newValue;
        invalidate();
    }

}
