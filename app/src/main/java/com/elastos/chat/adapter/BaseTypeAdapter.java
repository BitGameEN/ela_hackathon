package com.elastos.chat.adapter;

import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author rczhang on 2018/05/08.
 */
public class BaseTypeAdapter extends MultiTypeAdapter {

    private Items dataItems = new Items();

    public BaseTypeAdapter() {
        setItems(dataItems);
    }

    public void setData(List<Object> data) {
        dataItems.clear();
        dataItems.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<Object> data) {
        int start = getItemCount();
        dataItems.addAll(data);
        notifyItemRangeInserted(start, dataItems.size());
    }

}
