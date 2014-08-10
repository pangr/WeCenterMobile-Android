package cn.fanfan.homepage;

import java.util.List;

import cn.fanfan.common.smartGetImage;
import cn.fanfan.main.R;
import cn.fanfan.userinfo.UserInfoActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomePageAdapter extends ArrayAdapter<HomePageItemModel> {
	private int resourceId;
	private Context context;

	public HomePageAdapter(Context context, int itemViewResourceId,
			List<HomePageItemModel> objects) {
		super(context, itemViewResourceId, objects);
		this.context = context;
		resourceId = itemViewResourceId;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final HomePageItemModel itemModel = getItem(position);
		// 优化ListView性能
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.complexLayout = (LinearLayout) view
					.findViewById(R.id.llHomeListItemContent);
			if (itemModel.getLayoutType() == HomePageItemModel.LAYOUT_TYPE_SIMPLE) {
				viewHolder.complexLayout.setVisibility(View.GONE);
			} else {
				viewHolder.complexLayout.setVisibility(View.VISIBLE);
			}
			viewHolder.avatar = (FrameLayout) view.findViewById(R.id.flAvatar);
			viewHolder.avatarImage = (ImageView) view
					.findViewById(R.id.ivHomeListItemAvatar);
			viewHolder.userName = (TextView) view
					.findViewById(R.id.tvHomeListItemName);
			viewHolder.action = (TextView) view
					.findViewById(R.id.tvHomeListIteAction);
			viewHolder.itemTitle = (TextView) view
					.findViewById(R.id.tvHomeListItemTitle);
			viewHolder.bestAnswer = (TextView) view
					.findViewById(R.id.tvHomeListItemBestAnswer);
			viewHolder.agreeCount = (TextView) view
					.findViewById(R.id.tvHomeListItemAgreeCount);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
			if (itemModel.getLayoutType() == HomePageItemModel.LAYOUT_TYPE_SIMPLE) {
				viewHolder.complexLayout.setVisibility(View.GONE);
			} else {
				viewHolder.complexLayout.setVisibility(View.VISIBLE);
			}
		}
		viewHolder.agreeCount.setText(itemModel.getAgreeCount() + " ");
		viewHolder.action.setText(itemModel.getAction());
		viewHolder.userName.setText(itemModel.getUserName());
		viewHolder.itemTitle.setText(itemModel.getItemTitle());
		viewHolder.bestAnswer.setText(itemModel.getBestAnswer());
		smartGetImage getImage = new smartGetImage(context,
				itemModel.getAvatarUrl(), viewHolder.avatarImage);
		// 对头像的监听，点击头像跳转到相对应的用户信息详情页
		viewHolder.avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UserInfoActivity.actionStar(context,
						Integer.toString(itemModel.getUserUid()));
			}
		});
		return view;
	}

	class ViewHolder {
		LinearLayout complexLayout;
		FrameLayout avatar;
		ImageView avatarImage;
		TextView userName;
		TextView action;
		TextView itemTitle;
		TextView bestAnswer;
		TextView agreeCount;
	}
}
