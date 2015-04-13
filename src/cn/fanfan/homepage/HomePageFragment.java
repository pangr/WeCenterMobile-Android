package cn.fanfan.homepage;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import cn.fanfan.common.Config;
import cn.fanfan.common.NetworkState;
import cn.fanfan.main.MainActivity;
import cn.fanfan.main.R;
import cn.fanfan.widget.LoadMoreList;
import cn.fanfan.widget.LoadMoreList.OnLoadMoreListener;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class HomePageFragment extends Fragment implements
		SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {
	public static final String TAG = "HomePageFragment";
	// ҳ����getData�Ŀ�ѡ�����������ָ����ȡ�ڼ�ҳ�����ݡ�Ĭ��0���ӵ�0ҳ��ʼ��
	private int mPage = 0;
	// ������getData�Ŀ�ѡ����������ָ��һ�λ�ȡ���ݵ�������Ĭ��20��һ�λ�ȡ20������
	@SuppressWarnings("unused")
	private int mItem = 20;
	// ��ǰFragment�����е�Activity
	private MainActivity mActivity;
	// �ж�����״̬
	private NetworkState mNetState;

	private AsyncHttpClient mHttpClient;
	// ���ݼ���
	private List<HomePageItemModel> itemDataList = new ArrayList<HomePageItemModel>();

	private HomePageAdapter mAdapter;

	private LoadMoreList mListView;

	private SwipeRefreshLayout mSwipeLayout;

	private Bundle bundle;
	private int totalRow;
	// JSON����������
	private int actionCode;

	private int userUid = 1;
	private String userName = "Null";
	private String avatarUrl = "Null";

	private String itemTitle = "Null";
	private int itemTitleUid = 1;

	private String bestAnswer = "Null";
	private int bestAnswerUid = 1;
	private int agreeCount;

	private String action = "û�ж�̬";
	private int layoutType;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView = inflater.inflate(R.layout.fragment_homepage,
				container, false);
		mActivity = (MainActivity) getActivity();
		mNetState = new NetworkState();
		mListView = (LoadMoreList) fragmentView
				.findViewById(R.id.lvHomeListView);
		mAdapter = new HomePageAdapter(mActivity, R.layout.list_item_homepage,
				itemDataList);
		mListView.setAdapter(mAdapter);
		mListView.setOnLoadMoreListener(this);
		mSwipeLayout = (SwipeRefreshLayout) fragmentView
				.findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		// ����ˢ����ɫ��ʽ
		mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mSwipeLayout.setRefreshing(true);
		getData(mPage);
		return fragmentView;
	}

	private void getData(int page) {
		if (mNetState == null) {
			mNetState = new NetworkState();
		}
		if (mActivity == null) {
			mActivity = (MainActivity) getActivity();
		}
		if (mHttpClient == null) {
			mHttpClient = new AsyncHttpClient();
		}
		if (mNetState.isNetworkConnected(mActivity)) {
			// ����Http�������
			RequestParams params = new RequestParams();
			params.put("page", page);
			String url = Config.getValue("HomePageUrl");
			PersistentCookieStore mCookieStore = new PersistentCookieStore(
					mActivity);
			mHttpClient.setCookieStore(mCookieStore);
			// ����Http Get��������
			mHttpClient.get(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
					// TODO Auto-generated method stub
					// ����ʧ�ܺ���ʾ�û�
					Toast.makeText((MainActivity) getActivity(), "�޷���ȡ�������ԣ�",
							Toast.LENGTH_LONG).show();
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1,
						byte[] responseContent) {
					// TODO Auto-generated method stub
					parseData(responseContent);
					mSwipeLayout.setRefreshing(false);
				}
			});
		} else {
			Toast.makeText((MainActivity) getActivity(), "δ�������磡",
					Toast.LENGTH_LONG).show();
			mSwipeLayout.setRefreshing(false);
		}
	}

	private void parseData(byte[] responseContent) {
		// TODO Auto-generated method stub
		// ����ɹ����������
		layoutType = HomePageItemModel.LAYOUT_TYPE_SIMPLE;
		String data = new String(responseContent);
		try {
			// ����JSON��������
			JSONObject all = new JSONObject(data);
			JSONObject rsm = all.getJSONObject("rsm");
			totalRow = (rsm.getInt("total_rows"));
			// �������ɹ�totalRowΪ0ʱ˵���޸���������
			if (totalRow == 0) {
				// �Ѿ�����ȫ��������
				if (mPage == 0) {
					Toast.makeText(mActivity, "û�ж���Ŷ����ȥ��ע���˰ɣ�",
							Toast.LENGTH_LONG).show();
					MainActivity.mNavigationDrawerFragment.selectItem(1);
				} else {
					mPage = mPage - 1;
					Toast.makeText(mActivity, "û�и������ݣ�", Toast.LENGTH_LONG)
							.show();
					mListView.loadComplete();
				}
			}
			JSONArray rows = rsm.getJSONArray("rows");
			for (int i = 0; i < rows.length(); i++) {
				JSONObject rowsObject = rows.getJSONObject(i);
				// actionCode��ͬ��JSON������Ķ���ͬ���������
				actionCode = rowsObject.getInt("associate_action");
				// ��ȡuserInfo����
				JSONObject userInfoObject = rowsObject
						.getJSONObject("user_info");
				userUid = userInfoObject.getInt("uid");
				userName = userInfoObject.getString("user_name");

				if (!TextUtils.isEmpty(userInfoObject.getString("avatar_file"))) {
					avatarUrl = Config.getValue("AvatarPrefixUrl")
							+ userInfoObject.getString("avatar_file");
				} else {
					avatarUrl = "";
				}

				// ����actionCode��ͬ����ͬ���������ʣ�µ�JSON����
				switch (actionCode) {
				case 101:
					JSONObject questionInfoObject101 = rowsObject
							.getJSONObject("question_info");
					itemTitle = questionInfoObject101.getString(
							"question_content").trim();
					itemTitleUid = questionInfoObject101.getInt("question_id");
					action = "����������";
					layoutType = HomePageItemModel.LAYOUT_TYPE_SIMPLE;
					break;
				case 105:
					JSONObject questionInfoObject105 = rowsObject
							.getJSONObject("question_info");
					itemTitle = questionInfoObject105.getString(
							"question_content").trim();
					itemTitleUid = questionInfoObject105.getInt("question_id");
					action = "��ע������";
					layoutType = HomePageItemModel.LAYOUT_TYPE_SIMPLE;
					break;
				case 501:
					JSONObject articleInfoObject501 = rowsObject
							.getJSONObject("article_info");
					itemTitleUid = articleInfoObject501.getInt("id");
					itemTitle = articleInfoObject501.getString("title").trim();
					action = "����������";
					layoutType = HomePageItemModel.LAYOUT_TYPE_SIMPLE;
					break;
				case 502:
					JSONObject articleInfoObject502 = rowsObject
							.getJSONObject("article_info");
					itemTitleUid = articleInfoObject502.getInt("id");
					itemTitle = articleInfoObject502.getString("title").trim();
					action = "��ͬ������";
					layoutType = HomePageItemModel.LAYOUT_TYPE_SIMPLE;
					break;
				case 201:
					JSONObject answerInfoObject201 = rowsObject
							.getJSONObject("answer_info");
					bestAnswerUid = answerInfoObject201.getInt("answer_id");
					bestAnswer = answerInfoObject201
							.getString("answer_content").trim();
					agreeCount = answerInfoObject201.getInt("agree_count");
					JSONObject questionInfoObject201 = rowsObject
							.getJSONObject("question_info");
					itemTitle = questionInfoObject201.getString(
							"question_content").trim();
					itemTitleUid = questionInfoObject201.getInt("question_id");
					action = "�ش������";
					layoutType = HomePageItemModel.LAYOUT_TYPE_COMPLEX;
					break;
				case 204:
					JSONObject answerInfoObject204 = rowsObject
							.getJSONObject("answer_info");
					bestAnswerUid = answerInfoObject204.getInt("answer_id");
					bestAnswer = answerInfoObject204
							.getString("answer_content").trim();
					agreeCount = answerInfoObject204.getInt("agree_count");
					JSONObject questionInfoObject204 = rowsObject
							.getJSONObject("question_info");
					itemTitle = questionInfoObject204.getString(
							"question_content").trim();
					itemTitleUid = questionInfoObject204.getInt("question_id");
					action = "��ͬ�ûش�";
					layoutType = HomePageItemModel.LAYOUT_TYPE_COMPLEX;
					break;
				default:
					break;
				}
				// ���ص�ListItemModel
				HomePageItemModel item = new HomePageItemModel(layoutType,
						avatarUrl, userName, userUid, action, itemTitle,
						itemTitleUid, bestAnswer, bestAnswerUid, agreeCount);
				itemDataList.add(item);
				mAdapter.notifyDataSetChanged();
				mSwipeLayout.setRefreshing(false);
				mListView.loadComplete();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mSwipeLayout.setRefreshing(true);
			Log.i(TAG, "Json�����쳣");
		}

	}

	// ʵ��SwipeRefreshLayout�ӿڣ����ˢ�²���
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (!mListView.isLoading()) {
			itemDataList.clear();
			mPage = 0;
			getData(mPage);
		}
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		if (!mSwipeLayout.isRefreshing()) {
			mPage++;
			getData(mPage);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (bundle != null) {
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					"position"));
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHttpClient.cancelAllRequests(true);
	}
	
}
