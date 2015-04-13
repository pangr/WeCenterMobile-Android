package cn.fanfan.widget;

import cn.fanfan.main.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class LoadMoreList extends ListView implements OnScrollListener {
	private View footer; // �ײ����ظ�����ͼ
	private int mTotalItemCount;// ��������
	private int mLastVisibleItem;// ���һ���ɼ���item��
	private int mVisibleItemCount;// �ɼ�item������
	private boolean mIsLoading;// ���ڼ��أ�
	private OnLoadMoreListener mLoadMoreListener;// ���ظ���ص��ӿ�

	public LoadMoreList(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public LoadMoreList(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public LoadMoreList(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		this.mLastVisibleItem = firstVisibleItem + visibleItemCount;
		this.mTotalItemCount = totalItemCount;
		this.mVisibleItemCount = visibleItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		Log.d("LoadMoreList onScrollStateChanged:", "mTotalItemCount:"
				+ mTotalItemCount + ";" + "mLastVisibleItem:"
				+ mLastVisibleItem + "mVisibleItemCount:" + mVisibleItemCount);
		if (mTotalItemCount == mLastVisibleItem
				&& scrollState == SCROLL_STATE_IDLE
				&& (mVisibleItemCount != mTotalItemCount)) {
			if (!mIsLoading) {
				mIsLoading = true;
				footer.findViewById(R.id.ll_loadlist_footer).setVisibility(
						View.VISIBLE);
				// ���ظ���
				mLoadMoreListener.onLoad();
			}
		}
	}

	/**
	 * ��ӵײ�������ʾ���ֵ�listview
	 * 
	 * @param context
	 */
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.widget_loadlist_footer, null);
		footer.findViewById(R.id.ll_loadlist_footer).setVisibility(View.GONE);
		this.addFooterView(footer);
		this.setOnScrollListener(this);
	}

	/**
	 * �������
	 */
	public void loadComplete() {
		mIsLoading = false;
		footer.findViewById(R.id.ll_loadlist_footer).setVisibility(View.GONE);
	}

	public Boolean isLoading() {
		return mIsLoading;
	}

	// ���ü���
	public void setOnLoadMoreListener(OnLoadMoreListener mLoadMoreListener) {
		this.mLoadMoreListener = mLoadMoreListener;
	}

	// ���ظ������ݵĻص��ӿ�
	public interface OnLoadMoreListener {
		public void onLoad();
	}

}
