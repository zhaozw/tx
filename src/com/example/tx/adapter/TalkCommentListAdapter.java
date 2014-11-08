package com.example.tx.adapter;

import java.util.List;

import com.example.tx.R;
import com.example.tx.dto.TalkComment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TalkCommentListAdapter extends BaseAdapter {

	private Context context;
	public List<TalkComment> msgs;
	
	public TalkCommentListAdapter(Context context, List<TalkComment> messages) {
		super();
		this.context = context;
		this.msgs = messages;

	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgs.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return msgs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null
				|| (holder = (ViewHolder) convertView.getTag()).flag != index) {
			holder = new ViewHolder();
			holder.flag = index;
			
			// make list item view
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_comment, null);
			convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			
			TalkComment msg = msgs.get(index);
			
			TextView tv_comment_nickname = (TextView) convertView.findViewById(R.id.tv_comment_nickname);
			TextView tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
			tv_comment_nickname.setText(msg.sender.userName+"：");
			tv_comment.setText(msg.content);
			
			convertView.setTag(holder);

		}

		return convertView;
	}
	
	static class ViewHolder {
		
		int flag = -1;
	}

}
