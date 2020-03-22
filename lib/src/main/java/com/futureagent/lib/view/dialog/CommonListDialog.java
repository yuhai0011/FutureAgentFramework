package com.futureagent.lib.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.futureagent.lib.R;

/**
 * Created by skywalker on 16/4/5.
 * Email: skywalker@thecover.cn
 * Description: android list dialog for the cover.cn
 */
public class CommonListDialog extends Dialog {
    private Context mContext;

    private LinearLayout mLayoutAll;
    private TextView mContentTitle;
    private ListView mContentList;
    private ListItemAdapter mListItemAdapter;

    public CommonListDialog(Context context) {
        super(context, R.style.MyTheme_CustomDialog);
        setContentView(R.layout.dialog_list);
        mContext = context;

        initView();
    }

    private void initView() {
        mLayoutAll = (LinearLayout) findViewById(R.id.layout_background);

        mContentTitle = (TextView) findViewById(R.id.dialog_title);
        mContentList = (ListView) findViewById(R.id.list_items);

        showColorByModel();
    }

    /**
     * 根据白天、晚上模式修改背景、字体颜色
     */
    private void showColorByModel() {
        mLayoutAll.setBackgroundResource(R.drawable.dialog_bg_day);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.dialog_divider_day);
        mLayoutAll.setDividerDrawable(drawable);
        mContentList.setDivider(drawable);
    }

    public void setTitle(int resId) {
        setTitle(mContext.getString(resId));
    }

    public void setTitle(String title) {
        mContentTitle.setText(title);
    }

    public void setContent(int arrayResId, final OnClickListener onClickListener) {
        CharSequence[] items = mContext.getResources().getTextArray(arrayResId);

        mListItemAdapter = new ListItemAdapter(mContext, items);
        mContentList.setAdapter(mListItemAdapter);
        mContentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CommonListDialog.this.dismiss();
                onClickListener.onClick(null, position);
            }
        });
    }

    private static class ListItemAdapter extends BaseAdapter {

        private Context mContext;
        private CharSequence[] listItemValues;
        private LayoutInflater inflater;

        public ListItemAdapter(Context context, CharSequence[] listItems) {
            this.mContext = context;
            this.listItemValues = listItems;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listItemValues.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //自定义视图
            ListItem listItem;
            if (convertView == null) {
                listItem = new ListItem();
                convertView = inflater.inflate(R.layout.dialog_list_item, null);
                listItem.item = (TextView) convertView.findViewById(R.id.item_text);
                convertView.setTag(listItem);
            } else {
                listItem = (ListItem) convertView.getTag();
            }
            listItem.item.setText(listItemValues[position]);

            listItem.item.setBackgroundResource(R.drawable.dialog_button_bg_day);
            return convertView;
        }
    }

    private static class ListItem {
        public TextView item;
    }
}
