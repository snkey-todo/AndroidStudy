package com.hxsj.noticedemo.widget;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

/**
 * 重新计算textview排版
 * **/
public class TVGlobalLayoutListener implements OnGlobalLayoutListener {

	private TextView textView;

	public TVGlobalLayoutListener(TextView textView) {
		this.textView = textView;
	}

	@Override
	public void onGlobalLayout() {
		textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		String newText = autoSplitText(textView);
		if (!TextUtils.isEmpty(newText)) {
			textView.setText(newText);
		}
	}

	private String autoSplitText(TextView tv) {
		String rawText = tv.getText().toString();
		if (TextUtils.isEmpty(rawText))
			return "";
		Paint tvPaint = tv.getPaint();
		int tvWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight();
		String[] rawTextLines = rawText.replaceAll("\r", "").split("\n");
		StringBuilder sbNewText = new StringBuilder();
		for (String rawTextLine : rawTextLines) {
			if (tvPaint.measureText(rawTextLine) <= tvWidth) {
				// 如果整行宽度在控件可用宽度之内，就不处理了
				sbNewText.append(rawTextLine);
			} else {
				// 如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
				float lineWidth = 0;
				for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
					char ch = rawTextLine.charAt(cnt);
					lineWidth += tvPaint.measureText(String.valueOf(ch));
					if (lineWidth <= tvWidth) {
						sbNewText.append(ch);
					} else {
						sbNewText.append("\n");
						lineWidth = 0;
						--cnt;
					}
				}
			}
			sbNewText.append("\n");
		}

		// 把结尾多余的\n去掉
		if (!rawText.endsWith("\n")) {
			sbNewText.deleteCharAt(sbNewText.length() - 1);
		}
		return sbNewText.toString();
	}
}