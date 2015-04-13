package cn.fanfan.detail.question;

import java.util.ArrayList;

import cn.fanfan.common.ImageGet;
import cn.fanfan.main.R;
import cn.fanfan.topic.imageload.FileUtils;
import cn.fanfan.topic.imageload.ImageDownLoader;
import cn.fanfan.topic.imageload.ImageDownLoader.onImageLoaderListener;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class AnswerList extends Activity {

	private boolean isFirstEnter;
	private int mFirstVisibleItem;
	private int mVisibleItemCount;
	private ArrayList<AnswerItem> arrayList;
	private ListView answerlist;
	private ImageDownLoader downLoader;
	private CommentAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.found_list);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(null);
		actionBar.setTitle("�ش��б�");
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.show();
		answerlist = (ListView) findViewById(R.id.content);
		arrayList = QuestionDetailActivity.getlist();
		downLoader = new ImageDownLoader(this);
		isFirstEnter = true;
		answerlist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
					showImage(mFirstVisibleItem, mVisibleItemCount);
				} else {
					cancleTask();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				mFirstVisibleItem = firstVisibleItem;
				mVisibleItemCount = visibleItemCount;
				// ���������Ϊ�״ν����������������
				if (isFirstEnter && visibleItemCount > 0) {
					showImage(mFirstVisibleItem, mVisibleItemCount);
					isFirstEnter = false;
				}
			}
		});
		if (arrayList != null && arrayList.size() != 0) {
			adapter = new CommentAdapter(arrayList, this);
			answerlist.setAdapter(adapter);
		} else {
		}

	}

	private void showImage(int firstVisibleItem, int visibleItemCount) {
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
			final ArrayList<String> urls = arrayList.get(i).getUrls();
			GridView gridView = (GridView) answerlist.findViewWithTag(arrayList
					.get(i).getAnswer_id() + "gridview");
			if (urls != null && urls.size() > 0) {
				for (int j = 0; j < urls.size(); j++) {
					String url = urls.get(j);
					final ImageView imageView = (ImageView) gridView
							.findViewWithTag(url);
					final String path = FileUtils.SAVE_PATH + "/"
							+ url.replaceAll("[^\\w]", "");
					final ImageGet imageGet = new ImageGet();

					downLoader.getBitmap(url, new onImageLoaderListener() {

						@Override
						public void onImageLoader(Bitmap bitmap, String url) {
							// TODO Auto-generated method stub

							imageView.setImageBitmap(imageGet.getBitmap(path));
						}
					});

				}
			} else {
				gridView.setVisibility(View.GONE);
			}
		}
	}

	public void cancleTask() {
		downLoader.cacelTask();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
