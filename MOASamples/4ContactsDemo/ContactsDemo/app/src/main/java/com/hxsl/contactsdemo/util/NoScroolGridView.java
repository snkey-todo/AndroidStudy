package com.hxsl.contactsdemo.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class NoScroolGridView extends GridView {

	public NoScroolGridView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public NoScroolGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScroolGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.GridView#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}

}
