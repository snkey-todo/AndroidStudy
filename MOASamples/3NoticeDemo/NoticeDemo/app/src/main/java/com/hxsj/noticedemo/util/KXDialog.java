package com.hxsj.noticedemo.util;

import java.util.List;
import java.util.Map;

import com.hxsj.noticedemo.R;
import com.hxsj.noticedemo.log.Logger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class KXDialog extends Dialog {
	public static final int WIDTH = 338 * 3 / 4;
	public static final int WRAP_CONTENT = WindowManager.LayoutParams.WRAP_CONTENT;
	private Logger logger = Logger.getLogger();
	private LinearLayout _contentLayout;
	private Button _positiveButton;
	private Button _negativeButton;
	private Button _middleButton;
	private ImageView icon;
	private TextView title;
	private Context cxt = null;
	private LayoutInflater inflater = null;
	private LinearLayout _bottomLay = null;
	private LinearLayout dialogTitleLayout = null;
	private DisplayMetrics dm = null;
	private Window window;
	private int width;
	private LinearLayout _belowLayout = null;
	private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	private Activity mParentActivity;

	public KXDialog(Context context) {
		super(context, R.style.myDialogTheme2);
		init(context, WRAP_CONTENT, WRAP_CONTENT);
	}

	public KXDialog(Context context, int width, int height) {
		super(context, R.style.myDialogTheme2);
		init(context, width, height);
	}

	private void init(Context cxt, int width, int height) {
		mParentActivity = (Activity) cxt;
		window = getWindow();
		window.setFlags(WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED, WindowManager.LayoutParams.SCREEN_ORIENTATION_CHANGED);
		window.setContentView(R.layout.d_dialog_layout);
		this.cxt = cxt;
		dm = new DisplayMetrics();

		window.getWindowManager().getDefaultDisplay().getMetrics(dm);
		WindowManager.LayoutParams wlp = window.getAttributes();
		if (width != WRAP_CONTENT) {
			this.width = width;
		} else {
			this.width = (int) (WIDTH * dm.density);
		}
		wlp.width = width;
		if (height != WRAP_CONTENT) {
			wlp.height = height;
		} else {
			wlp.height = WRAP_CONTENT;
		}
		window.setAttributes(wlp);

		inflater = LayoutInflater.from(cxt);
		icon = (ImageView) findViewById(R.id.icon);
		title = (TextView) findViewById(R.id.dialog_title);
		_contentLayout = (LinearLayout) findViewById(R.id.dialog_content_main);
		_contentLayout.setLayoutParams(params);
		_bottomLay = (LinearLayout) findViewById(R.id.below);
		_bottomLay.setLayoutParams(params);
		dialogTitleLayout = (LinearLayout) findViewById(R.id.dialog_title_layout);
		dialogTitleLayout.setLayoutParams(params);
		_positiveButton = (Button) findViewById(R.id.dialog_positive_button);
		_negativeButton = (Button) findViewById(R.id.dialog_negative_button);
		_middleButton = (Button) findViewById(R.id.dialog_middle_button);
		_belowLayout = (LinearLayout) findViewById(R.id.below);
	}

	/**
	 * 在dialog的大小
	 * 
	 * @param view
	 */
	public KXDialog setView(View view) {
		LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
		setView(view, lpp);
		return this;
	}

	/**
	 * 在dialog的大小
	 * 
	 * @param view
	 */
	public KXDialog setView(View view, LinearLayout.LayoutParams lp) {
		_contentLayout.setVisibility(View.VISIBLE);
		_contentLayout.removeAllViews();
		_contentLayout.addView(view, lp);
		return this;
	}

	/**
	 * @param width
	 * @param height
	 * @return
	 */
	public KXDialog setDialogSize(int width, int height) {
		WindowManager.LayoutParams lp = window.getAttributes();
		float density = dm.density;
		lp.width = (int) (width * density); // 宽度
		if (height != 0) {
			lp.height = (int) (height * density); // 高度
		}
		window.setAttributes(lp);
		return this;
	}

	/**
	 * 设置标题
	 */
	public KXDialog setTitles(CharSequence str) {
		if (!TextUtils.isEmpty(str)) {
			title.setText(str);
			title.setVisibility(View.VISIBLE);
			dialogTitleLayout.setVisibility(View.VISIBLE);
			dialogTitleLayout.setLayoutParams(params);
		}
		return this;
	}

	/**
	 * 设置标题
	 */
	public KXDialog setTitles(CharSequence str, float textSize) {
		if (!TextUtils.isEmpty(str)) {
			title.setText(str);
			title.setTextSize(textSize);
			title.setVisibility(View.VISIBLE);
			dialogTitleLayout.setVisibility(View.VISIBLE);
			dialogTitleLayout.setLayoutParams(params);
		}
		return this;
	}

	/**
	 * 设置标题
	 */
	public KXDialog setTitles(int resId) {
		String str = cxt.getString(resId);
		if (!TextUtils.isEmpty(str)) {
			title.setText(str);
			title.setVisibility(View.VISIBLE);
			dialogTitleLayout.setVisibility(View.VISIBLE);
			dialogTitleLayout.setLayoutParams(params);
		}
		return this;
	}

	public LinearLayout getContentLayout() {
		return _contentLayout;
	}

	/**
	 * 设置图标
	 */
	public KXDialog setIcon() {
		icon.setVisibility(View.VISIBLE);
		title.setGravity(Gravity.CENTER_VERTICAL);
		title.setGravity(Gravity.LEFT);
		return this;
	}

	/**
	 * 设置图标
	 */
	public KXDialog setIcon(int resId) {
		icon.setVisibility(View.VISIBLE);
		icon.setImageResource(resId);
		if (resId > 0) {
			title.setGravity(Gravity.CENTER_VERTICAL);
			title.setGravity(Gravity.LEFT);
		}
		return this;
	}

	/**
	 * 设置图标
	 */
	public KXDialog setIcon(Bitmap bitmap) {
		icon.setVisibility(View.VISIBLE);
		icon.setImageBitmap(bitmap);
		if (bitmap != null) {
			title.setGravity(Gravity.CENTER_VERTICAL);
			title.setGravity(Gravity.LEFT);
		}
		return this;
	}

	/**
	 * @param resource
	 *            布局文件
	 * @param textViewResourceId
	 *            控件id
	 * @param objects
	 *            值列表
	 * @param listener
	 *            回调接口
	 * @param position
	 *            选中位置 -1代表不选中
	 * @return
	 */
	public KXDialog setAdapter(int resource, int textViewResourceId, List<Object> objects, AdapterView.OnItemClickListener listener, int position) {
		ListView list = (ListView) inflater.inflate(R.layout.listview_layout, null);
		list.setAdapter(new ArrayAdapter<Object>(cxt, resource, textViewResourceId, objects));
		list.setOnItemClickListener(listener);
		if (position != -1)
			list.setSelection(position);
		setView(list);
		return this;
	}

	/**
	 * @param adapter
	 * @param listener
	 * @param position
	 * @return
	 */
	public KXDialog setAdapter(BaseAdapter adapter, AdapterView.OnItemClickListener listener, int position) {
		ListView list = (ListView) inflater.inflate(R.layout.listview_layout, null);
		list.setAdapter(adapter);
		list.setOnItemClickListener(listener);
		if (position != -1)
			list.setSelection(position);
		setView(list);
		return this;
	}

	/**
	 * @param cxt
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 * @param listener
	 * @param position
	 * @return
	 */
	public KXDialog setAdapter(Context cxt, List<Map<String, Object>> data, int resource, String[] from, int[] to, AdapterView.OnItemClickListener listener, int position) {
		ListView list = (ListView) inflater.inflate(R.layout.listview_layout, null);
		list.setAdapter(new SimpleAdapter(cxt, data, resource, from, to));
		list.setOnItemClickListener(listener);
		if (position != -1)
			list.setSelection(position);
		setView(list);
		return this;
	}

	/**
	 * @param resource
	 *            布局文件
	 * @param textViewResourceId
	 *            控件id
	 * @param objects
	 *            值列表
	 * @param listener
	 *            回调接口
	 * @param position
	 *            选中位置 -1代表不选中
	 * @return
	 */
	public KXDialog setAdapter(int resource, int textViewResourceId, String[] objects, AdapterView.OnItemClickListener listener, int position) {
		ListView list = (ListView) inflater.inflate(R.layout.listview_layout, null);
		list.setAdapter(new ArrayAdapter<String>(cxt, resource, textViewResourceId, objects));
		list.setOnItemClickListener(listener);
		if (position != -1)
			list.setSelection(position);
		setView(list);
		return this;
	}

	/**
	 * @param items
	 * @param position
	 * @param listener
	 * @return
	 */
	public KXDialog setSingleChoiceItems(SpannableString[] items, int position, AdapterView.OnItemClickListener listener) {
		ListView list = (ListView) inflater.inflate(R.layout.listview_layout, null);
		list.setAdapter(new ArrayAdapter<SpannableString>(cxt, R.layout.listview_singlechoice_layout, items));
		list.setOnItemClickListener(listener);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setItemChecked(position, true);
		list.setSelection(position);
		list.setItemsCanFocus(false);
		setView(list);
		return this;
	}

	/**
	 * @param items
	 * @param position
	 * @param listener
	 * @return
	 */
	public KXDialog setSingleChoiceItems(String[] items, int position, AdapterView.OnItemClickListener listener) {
		ListView list = (ListView) inflater.inflate(R.layout.listview_layout, null);
		list.setAdapter(new ArrayAdapter<String>(cxt, R.layout.listview_singlechoice_layout, items));
		list.setOnItemClickListener(listener);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setItemChecked(position, true);
		list.setSelection(position);
		list.setItemsCanFocus(false);
		setView(list);
		return this;
	}

	/**
	 * @param items
	 * @return
	 */
	public KXDialog setItems(String[] items, AdapterView.OnItemClickListener listener) {
		ListView list = (ListView) inflater.inflate(R.layout.listview_layout, null);
		list.setAdapter(new ArrayAdapter<String>(cxt, R.layout.simple_list_item_1, items));
		list.setOnItemClickListener(listener);
		setView(list);
		return this;
	}

	/**
	 * @param arrayId
	 * @param listener
	 * @return
	 */
	public KXDialog setItems(int arrayId, AdapterView.OnItemClickListener listener) {
		String[] array = cxt.getResources().getStringArray(arrayId);
		return setItems(array, listener);
	}

	/**
	 * @param str
	 * @return
	 */
	public KXDialog setMessage(CharSequence str) {
		TextView text = (TextView) inflater.inflate(R.layout.d_textview, null);
		text.setText(str);
		setView(text);
		return this;
	}

	/**
	 * @param resid
	 * @param gravity
	 * @return
	 */
	public KXDialog setMessage(int resid, int gravity) {
		TextView text = (TextView) inflater.inflate(R.layout.d_textview, null);
		text.setText(resid);
		text.setGravity(gravity);
		setView(text);
		return this;
	}

	/**
	 * @param str
	 * @param textsize
	 * @param textcolor
	 * @return
	 */
	public KXDialog setMessage(CharSequence str, float textsize, int textcolor) {
		TextView text = (TextView) inflater.inflate(R.layout.d_textview, null);
		text.setText(str);
		if (textsize != 0)
			text.setTextSize(textsize);
		if (textcolor != 0)
			text.setTextColor(textcolor);
		setView(text);
		return this;
	}

	/**
	 * 设置左边按钮文本、文本颜色、背景及点击事件
	 * 
	 * @param message
	 * @param textColor
	 *            0:默认
	 * @param textSize
	 *            0:默认
	 * @param backgroundResource
	 *            0：默认
	 * @param listerner
	 * @return
	 */
	public KXDialog setPositiveButton(CharSequence message, int textColor, float textSize, int backgroundResource, Button.OnClickListener listerner) {
		_positiveButton.setText(message);
		if (0 != textColor) {
			_positiveButton.setTextColor(textColor);
		}
		if (0 != textSize) {
			_positiveButton.setTextSize(textSize);
		}
		if (0 != backgroundResource) {
			_positiveButton.setBackgroundResource(backgroundResource);
		}
		setPositiveButton(listerner);
		return this;
	}

	/**
	 * 设置左边按钮文本、背景及点击事件
	 * 
	 * @param message
	 * @param backgroundResource
	 * @param listerner
	 * @return
	 */
	public KXDialog setPositiveButton(CharSequence message, int backgroundResource, Button.OnClickListener listerner) {
		_positiveButton.setText(message);
		_positiveButton.setBackgroundResource(backgroundResource);
		setPositiveButton(listerner);
		return this;
	}

	/**
	 * 设置左边按钮文本及点击事件
	 * 
	 * @param message
	 * @param listerner
	 * @return
	 */
	public KXDialog setPositiveButton(CharSequence message, Button.OnClickListener listerner) {
		_positiveButton.setText(message);
		setPositiveButton(listerner);
		return this;
	}

	/**
	 * 设置左边按钮文本及点击事件
	 * 
	 * @param resId
	 * @param listerner
	 * @return
	 */
	public KXDialog setPositiveButton(int resId, Button.OnClickListener listerner) {
		_positiveButton.setText(resId);
		setPositiveButton(listerner);
		return this;
	}

	/**
	 * 设置左边按钮点击事件
	 * 
	 * @param listerner
	 * @return
	 */
	public KXDialog setPositiveButton(Button.OnClickListener listerner) {
		// findViewById(R.id.leftSpacer).setVisibility(View.VISIBLE);
		// findViewById(R.id.rightSpacer).setVisibility(View.VISIBLE);
		_positiveButton.setVisibility(View.VISIBLE);
		_belowLayout.setVisibility(View.VISIBLE);
		_positiveButton.setOnClickListener(listerner);
		return this;
	}

	/**
	 * 设置右边按钮文本、文本颜色、背景及点击事件
	 * 
	 * @param message
	 * @param textColor
	 *            0:默认
	 * @param textSize
	 *            0:默认
	 * @param backgroundResource
	 *            0:默认
	 * @param listerner
	 * @return
	 */
	public KXDialog setNegativeButton(CharSequence message, int textColor, float textSize, int backgroundResource, Button.OnClickListener listerner) {
		_negativeButton.setText(message);
		if (0 != textColor) {
			_negativeButton.setTextColor(textColor);
		}
		if (0 != textSize) {
			_negativeButton.setTextSize(textColor);
		}
		if (0 != backgroundResource) {
			_negativeButton.setBackgroundResource(backgroundResource);
		}
		setNegativeButton(listerner);
		return this;
	}

	/**
	 * 设置右边按钮文本、背景及点击事件
	 * 
	 * @param message
	 * @param backgroundResource
	 *            0:默认
	 * @param listerner
	 * @return
	 */
	public KXDialog setNegativeButton(CharSequence message, int backgroundResource, Button.OnClickListener listerner) {
		_negativeButton.setText(message);
		if (0 != backgroundResource) {
			_negativeButton.setBackgroundResource(backgroundResource);
		}
		setNegativeButton(listerner);
		return this;
	}

	/**
	 * 设置右边按钮文本、背景及点击事件
	 * 
	 * @param message
	 * @param listerner
	 * @return
	 */
	public KXDialog setNegativeButton(CharSequence message, Button.OnClickListener listerner) {
		_negativeButton.setText(message);
		setNegativeButton(listerner);
		return this;
	}

	/**
	 * 设置右边按钮文本及点击事件
	 * 
	 * @param resId
	 * @param listerner
	 * @return
	 */
	public KXDialog setNegativeButton(int resId, Button.OnClickListener listerner) {
		_negativeButton.setText(resId);
		setNegativeButton(listerner);
		return this;
	}

	/**
	 * 设置右边按钮点击事件
	 * 
	 * @param listerner
	 * @return
	 */
	public KXDialog setNegativeButton(Button.OnClickListener listerner) {
		// findViewById(R.id.leftSpacer).setVisibility(View.GONE);
		// findViewById(R.id.rightSpacer).setVisibility(View.GONE);
		findViewById(R.id.vertical_line_imageview).setVisibility(View.VISIBLE);
		_negativeButton.setVisibility(View.VISIBLE);
		_belowLayout.setVisibility(View.VISIBLE);
		_negativeButton.setOnClickListener(listerner);
		return this;
	}

	/**
	 * 设置中间按钮文本及点击事件
	 * 
	 * @param message
	 * @param listerner
	 * @return
	 */
	public KXDialog setMiddleButton(CharSequence message, Button.OnClickListener listerner) {
		this._middleButton.setVisibility(View.VISIBLE);

		findViewById(R.id.below).setVisibility(View.VISIBLE);
		this._middleButton.setText(message);
		this._middleButton.setOnClickListener(listerner);
		return this;
	}

	/**
	 * 设置中间按钮文本及点击事件
	 * 
	 * @param resId
	 * @param listerner
	 * @return
	 */
	public KXDialog setMiddleButton(int resId, Button.OnClickListener listerner) {
		this._middleButton.setVisibility(View.VISIBLE);
		findViewById(R.id.below).setVisibility(View.VISIBLE);
		// findViewById(R.id.leftSpacer).setVisibility(View.VISIBLE);
		// findViewById(R.id.rightSpacer).setVisibility(View.VISIBLE);
		this._middleButton.setText(resId);
		this._middleButton.setOnClickListener(listerner);
		return this;
	}

	/**
	 * 物理键返回调用接口
	 * 
	 * @param listener
	 * @return
	 */
	public KXDialog setOnCMCCDialogCancelListener(OnCancelListener listener) {
		setOnCancelListener(listener);
		return this;
	}

	/**
	 * 展现动画dialog
	 */
	public void showAnimation() {
		_contentLayout.setGravity(Gravity.CENTER);
		final AnimationDrawable drawable = (AnimationDrawable) _contentLayout.getBackground();

		if (drawable != null) {
			drawable.stop();
			// drawable.start();
			// 低版本系统不支持直接drawable.start(),故改用以下调用方法
			_contentLayout.post(new Runnable() {
				@Override
				public void run() {
					drawable.start();
				}
			});
		}
		show();
	}

	@Override
	public void dismiss() {
		if (mParentActivity != null && !mParentActivity.isFinishing()) {
			super.dismiss();// 调用超类对应方法
		}
	}

}