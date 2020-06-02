package com.ds.phonoexplorer;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HelpActivityImageAdapter extends PagerAdapter {
	Context context;
	private int[] GalImages = new int[] {
			R.drawable.firstandlast,
			R.drawable.searchoption,
			R.drawable.applicationsearch,
			R.drawable.contactsearch,
			R.drawable.imagesearch,
			R.drawable.musicsearch,
			R.drawable.videosearch,
			R.drawable.optionmenu,
			R.drawable.settings,
			R.drawable.aboutus,R.drawable.firstandlast
	};
	
	HelpActivityImageAdapter(Context context){
		this.context=context;
	}
	@Override
	public int getCount() {
		return GalImages.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(context);
//		int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
//		imageView.setPadding(padding, padding, padding, padding);
		try {
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setImageResource(GalImages[position]);
			((ViewPager) container).addView(imageView, 0);
			return imageView;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return imageView;

	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}
}
