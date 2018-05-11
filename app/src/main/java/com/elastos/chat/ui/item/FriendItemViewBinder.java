package com.elastos.chat.ui.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elastos.chat.R;
import com.elastos.chat.activity.SendMessageActivity;

import org.elastos.carrier.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.drakeet.multitype.ItemViewBinder;

/**
 * @author rczhang on 2018/05/08.
 */
public class FriendItemViewBinder extends ItemViewBinder<FriendItemViewModel, FriendItemViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.item_friend, parent, false));

        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final FriendItemViewModel item) {
        holder.name.setText(item.getUserName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("", "userId=" + item.getUserId());
                SendMessageActivity.start(v.getContext(), item.getUserId());
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name) TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
