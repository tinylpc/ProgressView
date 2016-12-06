package com.tiny.progressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.tiny.waveview.R;

/**
 * WaveView.java
 * 类的描述信息
 *
 * @author tiny
 * @version 2016/12/05 11:41
 */
public class DrawableProgressView extends View {

	private Bitmap srcBitmap;
	private int bitmapWidth;
	private int bitmapHeight;
	private Paint bitmapPaint;
	private PorterDuffXfermode mXfermode;
	private Rect mDynamicRect;
	private int maxProgress;
	private int progressColor;
	private int srcId;
	private int orientation;
	private int measuredHeight;
	private int measuredWidth;

	private Path p = new Path();

	public DrawableProgressView(Context context) {

		this(context, null);
	}

	public DrawableProgressView(Context context, AttributeSet attrs) {

		this(context, attrs, 0);
	}

	public DrawableProgressView(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DrawableProgressView);
		maxProgress = array.getInt(R.styleable.DrawableProgressView_maxProgress, 100);
		progressColor = array.getColor(R.styleable.DrawableProgressView_progressColor, Color.GREEN);
		srcId = array.getResourceId(R.styleable.DrawableProgressView_src, -1);
		orientation = array.getInt(R.styleable.DrawableProgressView_orientation, 0);
		array.recycle();

		if (srcId == -1) {
			throw new IllegalArgumentException("must set src argument");
		}
		try {
			srcBitmap = ((BitmapDrawable) getResources().getDrawable(srcId)).getBitmap();
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("do not support .9 picture");
		}
		mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
		bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bitmapPaint.setStrokeWidth(3);
		bitmapPaint.setFilterBitmap(true);
		bitmapPaint.setDither(true);
		bitmapPaint.setColor(progressColor);
		bitmapPaint.setStyle(Paint.Style.FILL);

		bitmapWidth = srcBitmap.getWidth();
		bitmapHeight = srcBitmap.getHeight();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		measuredHeight = measureHeight(heightMeasureSpec);
		measuredWidth = measureWidth(widthMeasureSpec);

		// 按照xml中配置的长宽缩放图片
		srcBitmap = matrixBitmap(srcBitmap, measuredWidth, measuredHeight);
		// 设置图片大小即为整个布局大小
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	private int measureHeight(int measureSpec) {

		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// Default size if no limits are specified.

		int result = bitmapHeight;
		if (specMode == MeasureSpec.AT_MOST) {

			// Calculate the ideal size of your
			// control within this maximum size.
			// If your control fills the available
			// space return the outer bound.

			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {

			// If your control can fit within these bounds return that value.
			result = specSize;
		}

		return result;
	}

	private int measureWidth(int measureSpec) {

		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		int result = bitmapWidth;
		if (specMode == MeasureSpec.AT_MOST) {
			// Calculate the ideal size of your control
			// within this maximum size.
			// If your control fills the available space
			// return the outer bound.
			result = specSize;
		}

		else if (specMode == MeasureSpec.EXACTLY) {

			result = specSize;
		}

		return result;
	}

	public static Bitmap matrixBitmap(Bitmap bitmap, int w, int h) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		float scaleWidth = ((float) w) / width;
		float scaleHeight = ((float) h) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		// 存为新图层
		int saveLayerCount = canvas.saveLayer(0, 0, measuredWidth, measuredHeight, bitmapPaint, Canvas.ALL_SAVE_FLAG);
		canvas.drawBitmap(srcBitmap, 0, 0, bitmapPaint);
		bitmapPaint.setXfermode(mXfermode);
		canvas.drawRect(mDynamicRect, bitmapPaint);
		bitmapPaint.setXfermode(null);
		canvas.restoreToCount(saveLayerCount);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);
		// 设置初始状态没有进度
		mDynamicRect = new Rect(0, 0, 0, 0);
	}

	public void setProgress(int progress) {

		if (progress > maxProgress) {
			progress = maxProgress;
		}
		// 从左往右
		if (orientation == 0) {
			mDynamicRect = new Rect(0, 0, measuredWidth * progress / maxProgress, measuredHeight);
			postInvalidate();
		} else {
			// 从下往上
			mDynamicRect = new Rect(0, measuredHeight - measuredHeight * progress / maxProgress, measuredWidth,
					measuredHeight);
			postInvalidate();
		}
	}
}
