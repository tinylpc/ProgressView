package com.tiny.progressview;

import com.tiny.waveview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * CircularProgressView.java
 * 类的描述信息
 *
 * @author tiny
 * @version 2016/12/06 14:48
 */
public class CircularProgressView extends View {

	private Paint paint;
	private RectF rect;
	private PorterDuffXfermode mXfermode;

	private int backgroundColor;
	private int progressColor;
	private int radius;
	private int maxProgress;
	private int start;

	private int angel;

	public CircularProgressView(Context context) {

		this(context, null);
	}

	public CircularProgressView(Context context, AttributeSet attrs) {

		this(context, attrs, 0);
	}

	public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView);
		backgroundColor = ta.getColor(R.styleable.CircularProgressView_backgroundColor, Color.GRAY);
		progressColor = ta.getColor(R.styleable.CircularProgressView_cpv_progressColor, Color.GREEN);
		radius = ta.getDimensionPixelSize(R.styleable.CircularProgressView_radius, 100);
		maxProgress = ta.getInt(R.styleable.CircularProgressView_cpv_maxProgress, 100);
		start = ta.getInt(R.styleable.CircularProgressView_start, 270);
		ta.recycle();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);

		mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
		rect = new RectF(0, 0, 0, 0);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		int saveLayerCount = canvas.saveLayer(0, 0, radius * 2, radius * 2, paint, Canvas.ALL_SAVE_FLAG);
		paint.setColor(backgroundColor);
		canvas.drawCircle(radius, radius, radius, paint);
		paint.setXfermode(mXfermode);

		paint.setColor(progressColor);

		canvas.drawArc(rect, start, angel, true, paint);

		paint.setXfermode(null);
		canvas.restoreToCount(saveLayerCount);
	}

	public void setProgress(int progress) {

		if (progress > maxProgress) {
			progress = maxProgress;
		}
		angel = (int) (progress / (float) maxProgress * 360);
		rect = new RectF(0, 0, radius * 2, radius * 2);
		postInvalidate();
	}
}
