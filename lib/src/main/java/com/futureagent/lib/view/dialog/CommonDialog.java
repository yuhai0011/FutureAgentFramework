package com.futureagent.lib.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futureagent.lib.R;
import com.futureagent.lib.utils.PixelUtil;

/**
 * Created by skywalker on 16/4/3.
 * Email: skywalker@thecover.cn
 * Description: common android dialog for the cover.cn
 */
public class CommonDialog extends Dialog {
    private Context mContext;
    private boolean mShowTitle;

    private LinearLayout mLayoutButtons;
    private Button mBtnCancel;
    private Button mBtnConfirm;

    private LinearLayout mLayoutDefault;

    private LinearLayout mLayoutNoTitle;
    private TextView mContentNoTitle;

    private LinearLayout mLayoutWithTitle;
    private TextView mContentTitle;
    private TextView mContentValue;

    private LinearLayout mLayoutUser;

    public CommonDialog(Context context, boolean showTitle) {
        super(context, R.style.MyTheme_CustomDialog);
        setContentView(R.layout.dialog_common);
        mContext = context;
        mShowTitle = showTitle;

        initView();
    }

    private void initView() {
        mLayoutDefault = (LinearLayout) findViewById(R.id.layout_background);
        mLayoutUser = (LinearLayout) findViewById(R.id.layout_user);

        mLayoutButtons = (LinearLayout) findViewById(R.id.dialog_button);
        mBtnCancel = (Button) findViewById(R.id.button_cancel);
        mBtnConfirm = (Button) findViewById(R.id.button_confirm);

        mLayoutNoTitle = (LinearLayout) findViewById(R.id.layout_no_title);
        mContentNoTitle = (TextView) findViewById(R.id.content_no_title);

        mLayoutWithTitle = (LinearLayout) findViewById(R.id.layout_with_title);
        mContentTitle = (TextView) findViewById(R.id.content_title);
        mContentValue = (TextView) findViewById(R.id.content_value);

        if (mShowTitle) {
            mLayoutNoTitle.setVisibility(View.GONE);
            mLayoutWithTitle.setVisibility(View.VISIBLE);
        } else {
            mLayoutNoTitle.setVisibility(View.VISIBLE);
            mLayoutWithTitle.setVisibility(View.GONE);
        }
        showColorByModel();
    }

    public View setUserLayout(int layoutID) {
        mLayoutDefault.setVisibility(View.GONE);
        mLayoutUser.setVisibility(View.VISIBLE);
        ViewStub contentStub = (ViewStub) findViewById(R.id.content_stub);
        if (contentStub != null) {
            contentStub.setLayoutResource(layoutID);
            return contentStub.inflate();
        } else {
            return findViewById(R.id.content_stub_holder);
        }
    }

    private void showColorByModel() {
        mLayoutDefault.setBackgroundResource(R.drawable.dialog_bg_day);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.dialog_divider_day);
        mLayoutDefault.setDividerDrawable(drawable);
        mBtnCancel.setBackgroundResource(R.drawable.dialog_button_bg_day);
        mBtnConfirm.setBackgroundResource(R.drawable.dialog_button_bg_day);
        mLayoutButtons.setDividerDrawable(drawable);
    }

    public void setTitle(int resId) {
        setTitle(mContext.getString(resId));
    }

    public void setTitle(String title) {
        if (!mShowTitle) {
            return;
        }
        mContentTitle.setText(title);
    }

    public void setContent(int resId) {
        setContent(mContext.getString(resId));
    }

    public void setContent(String content) {
        TextView contentView;
        if (mShowTitle) {
            contentView = mContentValue;
        } else {
            contentView = mContentNoTitle;
        }
        contentView.setText(content);
        setContentGravity(contentView, content);
    }

    /**
     * 根据显示内容多少设置文本对其方式
     *
     * @param contentView
     * @param content
     */
    private void setContentGravity(TextView contentView, String content) {
        float textSize = PixelUtil.dp2px(mContext, mContext.getResources().getDimension(R.dimen.XL));
        float dialog_width = PixelUtil.dp2px(mContext, mContext.getResources().getDimension(R.dimen.dialog_width));
        float edge_width = PixelUtil.dp2px(mContext, mContext.getResources().getDimension(R.dimen.dialog_content_margin_edge));
        float devWidth = dialog_width - 2 * edge_width;
        int oneLineHeight = calcTextViewHeight("cover", textSize, (int) devWidth);
        int allLineHeight = calcTextViewHeight(content, textSize, (int) devWidth);
        if (oneLineHeight > 0 && allLineHeight % oneLineHeight > 1) {
            // 多行时垂直居中，靠左
            contentView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        } else {
            // 单行时居中
            contentView.setGravity(Gravity.CENTER);
        }
    }

    /**
     * 计算文本显示的高度
     *
     * @param text
     * @param textSize
     * @param devWidth
     * @return
     */
    private int calcTextViewHeight(String text, float textSize, int devWidth) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(devWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    public void setOkBtn(String btnText, View.OnClickListener clickListener) {
        if (clickListener == null) {
            mBtnConfirm.setOnClickListener(new CloseListener());
        } else {
            mBtnConfirm.setOnClickListener(new ExternalListener(clickListener));
        }
        if (!TextUtils.isEmpty(btnText)) {
            mBtnConfirm.setText(btnText);
        }
    }

    public void setOkBtn(View.OnClickListener clickListener) {
        setOkBtn(null, clickListener);
    }

    public void setCancelBtn(String btnText, View.OnClickListener clickListener) {
        if (clickListener == null) {
            mBtnCancel.setOnClickListener(new CloseListener());
        } else {
            mBtnCancel.setOnClickListener(new ExternalListener(clickListener));
        }
        if (!TextUtils.isEmpty(btnText)) {
            mBtnCancel.setText(btnText);
        }
    }

    public void setCancelBtn(View.OnClickListener clickListener) {
        setCancelBtn(null, clickListener);
    }

    public void hideOkBtn() {
        mBtnConfirm.setVisibility(View.GONE);
    }

    /**
     * 封装按钮点击事件，在dialog内部处理dismiss()事件
     * 外部只需要关注自己的业务
     */
    private class CloseListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            cancel();
        }
    }

    private class ExternalListener implements View.OnClickListener {
        private View.OnClickListener mListener;

        public ExternalListener(View.OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            CommonDialog.this.dismiss();
            mListener.onClick(v);
        }
    }
}
