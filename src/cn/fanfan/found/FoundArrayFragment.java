package cn.fanfan.found;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import bean.Article;
import bean.FoundItem;
import bean.Question;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cn.fanfan.common.Config;
import cn.fanfan.main.R;
import cn.fanfan.widget.LoadMoreList;
import cn.fanfan.widget.LoadMoreList.OnLoadMoreListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FoundArrayFragment extends Fragment implements OnLoadMoreListener {
	// ViewPager��λ��
	private int mPosition;
	// �������������ͣ�hot��unresponsive��new��
	private String mType;
	private String mCommend;
	// �����б�
	private LoadMoreList mListView;

	private List<FoundItem> mItems;

	// ���������ʲô����Ҳû�³���
	private FoundAdapter adapter;
	private int mPage = 1;

	/**
	 * Create a new instance of CountingFragment
	 */
	static Fragment newInstance(int position) {
		FoundArrayFragment f = new FoundArrayFragment();
		Bundle args = new Bundle();
		args.putInt("position", position);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.found_list, container, false);
		mListView = (LoadMoreList) rootView.findViewById(R.id.content);
		mItems = new ArrayList<FoundItem>();
		adapter = new FoundAdapter(mItems, getActivity());
		mListView.setAdapter(adapter);
		mListView.setOnLoadMoreListener(this);
		getData(mPage);
		return rootView;
	}

	private void getData(int page) {
		RequestParams params = new RequestParams();
		String url = Config.getValue("FoundList");
		AsyncHttpClient client = new AsyncHttpClient();
		switch (mPosition) {
		case 0:
			mType = "new";
			break;
		case 1:
			mType = "hot";
			break;
		case 2:
			mType = "unresponsive";
			break;
		default:
			return;
		}
		mCommend = "0";
		params.put("page", page);
		params.put("sort_type", mType);
		params.put("is_recommend", mCommend);
		client.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] result) {
				// TODO Auto-generated method stub
				String string = new String(result);
				Log.d("FoundArrayFragment : " + mType, string);
				parseData(result);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "�޷���ȡ���ݣ������ԣ�", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	/* �������� */
	private void parseData(byte[] data) {
		// ����json�ֶκ���
		// rsm (�ɹ�ʱ�������ص����ݣ�ʧ��ʱ���ֶ�Ϊnull)
		// errno (1:�ɹ� -1:ʧ��)
		// err (�ɹ�ʱ���ֶ�Ϊnull��ʧ��ʱ���ֶΰ�������ԭ�򣬿�ԭ�����)
		try {
			JSONObject all = new JSONObject(new String(data));
			if (all.getInt("errno") == -1) {
				Toast.makeText(getActivity(), all.getString("err"),
						Toast.LENGTH_SHORT).show();
			} else {
				JSONObject rsm = all.getJSONObject("rsm");
				JSONArray rows = rsm.getJSONArray("rows");
				for (int i = 0; i < rows.length(); i++) {
					JSONObject jsonObject = rows.getJSONObject(i);
					String type = jsonObject.getString("post_type");
					if (type.equals("article")) {
						Article article = new Gson().fromJson(
								jsonObject.toString(), Article.class);
						mItems.add(article);
					} else {
						if (!jsonObject.get("answer_users").equals(0)) {
							jsonObject.put("answer_users", 1);
						}
						Question question = new Gson().fromJson(
								jsonObject.toString(), Question.class);
						mItems.add(question);
					}
				}
				adapter.notifyDataSetChanged();
				mListView.loadComplete();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Found:mType:" + mType, "parse error!");
		} finally {
			mListView.loadComplete();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mPosition = bundle.getInt("position");
	}

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		getData(mPage++);
	}
}
