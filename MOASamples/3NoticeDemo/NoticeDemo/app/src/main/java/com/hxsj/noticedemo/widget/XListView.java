/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.hxsj.noticedemo.widget;


import com.hxsj.noticedemo.R;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;


public class XListView extends ListView implements OnScrollListener {

	private float mLastY = -1; // 记录按下点的Y坐标
	private Scroller mScroller; // 用来回滚操作
	private OnScrollListener mScrollListener; // 滑动回滚事件监听
	private IXListViewListener mListViewListener;//上拉，下拉监听触发器
	private XListViewHeader mHeaderView;// -- 头部布局

	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private TextView mHeaderTv;
	private int mHeaderViewHeight; // 头部Header的高度
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // 是否能够刷新.

	private XListViewFooter mFooterView;// -- 底部footer view
	private boolean mEnablePullLoad;//是否可以加载更多
	private boolean mPullLoading;//是否正在加载
	private boolean mIsFooterReady = false;//是否footer准备状态

	private int mTotalItemCount;// Item数目统计，判断Listview是否在底部
	private int mScrollBack;// 记录是从header还是footer返回
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // 滑动时常
	private final static int PULL_LOAD_MORE_DELTA = 50; // 加载更多的距离.
	private final static float OFFSET_RADIO = 1.8f; // 滑动比例

	public XListViewFooter getFooterView() {
		return mFooterView;
	}

	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}
	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}
	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}
	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// 监听滑动事件，
		super.setOnScrollListener(this);
		// 初始化头部
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);
		mHeaderTv = (TextView) mHeaderView.findViewById(R.id.xlistview_header_tv);
		addHeaderView(mHeaderView);
		// 初始化底部
		mFooterView = new XListViewFooter(context);
		// 初始化顶部高度
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mHeaderViewHeight = mHeaderViewContent.getHeight();
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// 确保footer最后添加并且只添加一次
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * 下拉刷新开关，true允许下拉刷新，false禁止.
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 上拉加载更多开关，true允许，false禁止
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// 上拉跟点击底部都可以出发加载事件
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * 刷新停止，重置顶部布局
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	/**
	 * 加载完成，重置底部布局
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	/**
	 * 设置最后刷新时间
	 * @param time
	 */
	public void setRefreshTime(String time) {
		if (TextUtils.isEmpty(time)) {
			mHeaderTimeView.setVisibility(View.GONE);
		} else {
			mHeaderTimeView.setVisibility(View.VISIBLE);
			mHeaderTimeView.setText(time);
		}
	}
	
	public void setRefreshing(boolean refresh){
		if (refresh) {
			mListViewListener.onRefresh();
		} else {

		}
	}
	
	public boolean isRefreshing(){
		return mPullRefreshing;
	}

	/**
	 * 设置最后更新时间
	 * @param tv
	 */
	public void setHeaderTv(String tv) {
		if (TextUtils.isEmpty(tv)) {
			mHeaderTv.setVisibility(View.GONE);
		} else {
			mHeaderTv.setVisibility(View.VISIBLE);
			mHeaderTv.setText(tv);
		}
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * 重置顶部高度
	 */
	private void resetHeaderHeight() {
		//当前的可见高度
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // 当前不可见
			return;
		//正在刷新或者顶部没有显示完全，返回
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // 默认会回滚到header的位置
		// 如果正在刷新，则回滚到header的高度
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
		// 触发 computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				// 高度足够触发加载更多
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
			if (mLastY == -1) {
			mLastY = ev.getRawY();//记录按下的坐标
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY; //计算移动距离
			mLastY = ev.getRawY();

			if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// 是第一项并且标题已经显示或者是在下拉
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// 最后一项，正在加载更多或者正在加载更多
				if (mEnablePullLoad)
					updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			mLastY = -1; // 重置
			if (getFirstVisiblePosition() == 0) {
				// 请求刷新
				if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// 请求加载更多
				if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA && !mPullLoading) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// 设置滑动监听事件
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}
	/**
	 * 可以通过ListView.OnScrollListener或者这个接口，监听header/footer回滚
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}
	/**
	 * 该接口实现刷新/加载更多事件
	 */
	public interface IXListViewListener {
		public void onRefresh();
		public void onLoadMore();
	}
}
