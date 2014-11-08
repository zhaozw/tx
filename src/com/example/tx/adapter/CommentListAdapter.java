package com.example.tx.adapter;
import java.util.List;

import com.example.tx.dto.Comment;
import com.example.tx.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class CommentListAdapter extends BaseAdapter {
	protected static final String TAG = "CommentListAdapter";

	private Context context;
	public List<Comment> msgs;

	public CommentListAdapter(Context context, List<Comment> messages) {
		super();
		this.context = context;
		this.msgs = messages;

	}

	@Override
	public int getCount() {
		return msgs.size();
	}

	@Override
	public Object getItem(int position) {
		return msgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null
				|| (holder = (ViewHolder) convertView.getTag()).flag != index) {
			holder = new ViewHolder();
			holder.flag = index;
			
			// make list item view
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_comment, null);
			
			Comment msg = msgs.get(index);
			
			TextView tv_comment_nickname = (TextView) convertView.findViewById(R.id.tv_comment_nickname);
			TextView tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
			tv_comment_nickname.setText(msg.maker.userName+"：");
			tv_comment.setText(msg.content);
			
			convertView.setTag(holder);

		}

		return convertView;
	}  

	static class ViewHolder {
		
		int flag = -1;
	}

}
