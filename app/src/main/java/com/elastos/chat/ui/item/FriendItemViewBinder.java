package com.elastos.chat.ui.item;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

    private OnItemLongClickListener onItemLongClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, FriendItemViewModel item);
    }

    public FriendItemViewBinder(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.item_friend, parent, false));
        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull final FriendItemViewModel item) {
        holder.name.setText(item.getUserName());
        holder.connectStatus.setText(item.getConnectStatus());
        if (TextUtils.isEmpty(item.getMessage())) {
            holder.message.setVisibility(View.GONE);
        } else {
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(item.getMessage());
        }
        holder.dot.setVisibility(TextUtils.isEmpty(item.getMessage()) ? View.GONE : View.VISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("", "userId=" + item.getUserId());
                SendMessageActivity.start(v.getContext(), item.getUserId());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClickListener.onItemLongClick(v,item);
                return true;
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name) TextView name;
        @BindView(R.id.connect_status) TextView connectStatus;
        @BindView(R.id.message) TextView message;
        @BindView(R.id.dot) View dot;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
