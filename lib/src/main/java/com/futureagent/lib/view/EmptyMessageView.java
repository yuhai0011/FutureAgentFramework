package com.futureagent.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.futureagent.lib.R;


/**
 * Created by skywalker on 16/07/28.
 * 显示空消息的view
 */
public class EmptyMessageView extends LinearLayout {
    private View emptyView;
    private ImageView mEmptyIcon;
    private TextView mTextViewHint;
    private TextView mTextViewTitle;
    private Button mBtnRetry;


    private int emptyImageIconId;
    private int emptyHintID;
    private boolean emptyViewHasRetryBtn;
    private boolean emptyViewHasTitle;
    private int retryBtnTextID;

    // construct
    public EmptyMessageView(Context context) {
        super(context);
        initView();
    }

    public EmptyMessageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initAttr(attrs);
        initView();
    }

    public EmptyMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        initView();
    }

    /**
     * view 初始化
     */
    private void initView() {
        emptyView = LayoutInflater.from(getContext()).inflate(R.layout.empty_list_layout, this, true);
        mEmptyIcon = (ImageView) emptyView.findViewById(R.id.imageView_icon);
        mTextViewHint = (TextView) emptyView.findViewById(R.id.hint);
        mBtnRetry = (Button) emptyView.findViewById(R.id.button_retry);
        mTextViewTitle = (TextView) emptyView.findViewById(R.id.title);

        mTextViewHint.setText(emptyHintID > 0 ? getContext().getString(emptyHintID) : "");
        mBtnRetry.setVisibility(emptyViewHasRetryBtn ? VISIBLE : GONE);
        mBtnRetry.setText(retryBtnTextID > 0 ? getContext().getString(retryBtnTextID) : "");
        if (emptyImageIconId > 0) {
            mEmptyIcon.setImageResource(emptyImageIconId);
        }
        mTextViewTitle.setVisibility(emptyViewHasTitle ? VISIBLE : GONE);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EmptyMessageView);

        try {
            emptyImageIconId = a.getResourceId(R.styleable.EmptyMessageView_imageIcon, R.drawable.ic_refresh_black);
            emptyHintID = a.getResourceId(R.styleable.EmptyMessageView_emptyInfoHint, -1);
            emptyViewHasRetryBtn = a.getBoolean(R.styleable.EmptyMessageView_viewHasRetryBtn, false);
            emptyViewHasTitle = a.getBoolean(R.styleable.EmptyMessageView_viewHasTitle, false);
            retryBtnTextID = a.getResourceId(R.styleable.EmptyMessageView_retryBtnHint, R.string.text_retry);
        } finally {
            a.recycle();
        }
    }

    public void setEmptyHint(String hint) {
        if (mTextViewHint == null) {
            return;
        }

        if (TextUtils.isEmpty(hint)) {
            hint = getContext().getString(R.string.http_server_fail);
        } else if (hint.startsWith("<!DOCTYPE html>")) {
            hint = getContext().getString(R.string.http_server_die);
        }

        mTextViewHint.setText(hint);
    }

    public void setEmptyViewTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            emptyViewHasTitle = false;
            mTextViewTitle.setVisibility(GONE);
        } else {
            emptyViewHasTitle = true;
            mTextViewTitle.setVisibility(VISIBLE);
            mTextViewTitle.setText(title);
        }
    }

    public void setRetryButtonText(String buttonText) {
        if (mBtnRetry == null) {
            return;
        }
        mBtnRetry.setText(buttonText);
    }

    public void setRetryButtonListener(OnClickListener listener) {
        if (mBtnRetry == null) {
            return;
        }
        mBtnRetry.setOnClickListener(listener);
    }

    public void setRetryButtonVisible(int visible) {
        if (mBtnRetry == null) {
            return;
        }
        mBtnRetry.setVisibility(visible);
    }

    public void setRefreshImage(int resId) {
        if (mEmptyIcon == null) {
            return;
        }
        mEmptyIcon.setImageResource(resId);
    }

    public void setEmptyViewHasRetryBtn(boolean emptyViewHasRetryBtn) {
        this.emptyViewHasRetryBtn = emptyViewHasRetryBtn;
        mBtnRetry.setVisibility(emptyViewHasRetryBtn ? VISIBLE : GONE);
    }

    public void setRetryBtnText(int retryBtnText) {
        retryBtnTextID = retryBtnText;
        mBtnRetry.setText(retryBtnTextID);
    }

    public void setEmptyImageIcon(int emptyImageIcon) {
        emptyImageIconId = emptyImageIcon;
        mEmptyIcon.setImageResource(emptyImageIconId);
    }
}

