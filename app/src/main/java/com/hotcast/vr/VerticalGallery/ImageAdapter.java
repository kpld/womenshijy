package com.hotcast.vr.VerticalGallery;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hotcast.vr.pageview.GalleryItemView;

import java.util.List;


public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	List<GalleryItemView> views;
	public ImageAdapter(Context c, List<GalleryItemView> views) {
		mContext = c;
		this.views = views;
	}

	public int getCount() {
		return views.size();
	}

	public Object getItem(int position) {
		return views.get(position);
	}

	public long getItemId(int position) {
		return position ;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		return views.get(position);
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

	public ImageView createReflectedImages(Context mContext, int imageId) {

		Bitmap originalImage = BitmapFactory.decodeResource(
				mContext.getResources(), imageId);

		final int reflectionGap = 4;
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(originalImage, 0, 0, null);

		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff,
				TileMode.MIRROR);

		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		ImageView imageView = new ImageView(mContext);
		imageView.setImageBitmap(bitmapWithReflection);

		return imageView;
	}
}