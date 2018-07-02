package com.hxsj.telephone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TimerTextView extends TextView implements Runnable {
	private long mday, mhour, mmin, msecond;// 天，小时，分钟，秒
	private boolean run = false; // 是否启动了

	public TimerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean isRun() {
		return run;
	}

	public void beginRun() {
		this.run = true;
		run();
	}

	public void stopRun() {
		this.run = false;
	}

	private void computeTime() {
		msecond++;
		if (msecond > 59) {
			mmin++;
			msecond = 0;
			if (mmin > 59) {
				mhour++;
				mmin = 0;
				if (mhour > 23) {
					mday++;
					mhour = 0;
				}
			}
		}
	}

	@Override
	public void run() {
		if (run) {
			computeTime();
			String strTime = "";
			if (mday == 0) {
				if (mhour == 0) {
					if (mmin == 0) {
						strTime ="0:"+ msecond ;
					} else {
						strTime = mmin + ":" + msecond ;
					}
				} else {
					strTime = mhour + ":" + mmin + ":" + msecond ;
				}
			} else {
				strTime = mday + ":" + mhour + ":" + mmin + ":" + msecond + "";
			}
			// String strTime= mday +"天:"+ mhour+"小时:"+ mmin+"分钟:"+msecond+"秒";
			this.setText("通话时长:"+strTime);
			postDelayed(this, 1000);
		} else {
			removeCallbacks(this);
		}

	}

}
