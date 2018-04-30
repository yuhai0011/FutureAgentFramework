package com.futureagent.lib.view.recyclerview;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.futureagent.lib.R;
import com.futureagent.lib.view.EmptyMessageView;

/**
 * @author skywalker on 2018/4/30.
 * Email: skywalker@thecover.co
 * Description:
 */
public class SuperRecyclerView extends RelativeLayout {

    private SwipeRefreshLayout swipeRefreshLayout;
    private NoAutoScrollRecyclerView recyclerView;
    private EmptyMessageView emptyView;
    private View view;
    private LinearLayout boxHint;
    private TextView textViewHint;

    private boolean isLoadMore;
    private boolean isRefresh;
    private int colorRes;

    private int emptyImageIconId;
    private int emptyHintID;
    private boolean emptyViewHasRetryBtn;
    private int retryBtnTextID;

    private SuperRecyclerInterface superRecyclerInterface;
    private SuperRecyclerEventBlockInterface superRecyclerEventBlockInterface;
    private SuperRecyclerItemClickInterface superRecyclerItemClickInterface;

    private BaseSuperRecyclerViewAdapter adapter;

    private boolean isEventBlock = false; // 是否拦截touch event

    // recyclerView的itemDecoration
    private RecyclerView.ItemDecoration itemDecoration;

    // get & set

    public RecyclerView.ItemDecoration getItemDecoration() {
        return itemDecoration;
    }

    public void setItemDecoration(RecyclerView.ItemDecoration itemDecoration) {

        if (this.itemDecoration != null) {
            getRecyclerView().removeItemDecoration(this.itemDecoration);
        }

        this.itemDecoration = itemDecoration;

        if (this.itemDecoration != null) {
            getRecyclerView().addItemDecoration(this.itemDecoration);
        }
    }

    public boolean isEventBlock() {
        return isEventBlock;
    }

    public void setEventBlock(boolean eventBlock) {
        isEventBlock = eventBlock;
    }

    public BaseSuperRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public boolean isLoadMore() {
        return isLoadMore;
    }

    public void setLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    public SuperRecyclerInterface getSuperRecyclerInterface() {
        return superRecyclerInterface;
    }

    public void setOnSuperRecyclerInterface(SuperRecyclerInterface superRecyclerInterface) {

        if (superRecyclerInterface == null) {
            return;
        }

        this.superRecyclerInterface = superRecyclerInterface;

        // 上拉刷新
        if (this.superRecyclerInterface != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (getSuperRecyclerInterface() != null) {
                        getSuperRecyclerInterface().onRefresh();
                    }
                }
            });
        }

        // 加载更多
        if (this.adapter != null) {
            adapter.setOnLoadMore(new BaseSuperRecyclerViewAdapter.OnLoadMore() {
                @Override
                public void loadMore() {
                    if (getSuperRecyclerInterface() != null) {
                        getSuperRecyclerInterface().onLoadMore();
                    }
                }
            });
        }
    }

    public SuperRecyclerEventBlockInterface getSuperRecyclerEventBlockInterface() {
        return superRecyclerEventBlockInterface;
    }

    public void setOnSuperRecyclerEventBlockInterface(SuperRecyclerEventBlockInterface
                                                              superRecyclerEventBlockInterface) {
        this.superRecyclerEventBlockInterface = superRecyclerEventBlockInterface;
    }

    public SuperRecyclerItemClickInterface getSuperRecyclerItemClickInterface() {
        return superRecyclerItemClickInterface;
    }

    public void setOnSuperRecyclerItemClickInterface(SuperRecyclerItemClickInterface superRecyclerItemClickInterface) {
        this.superRecyclerItemClickInterface = superRecyclerItemClickInterface;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    // construct
    public SuperRecyclerView(Context context) {
        super(context);

        initView();
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttr(attrs);
        initView();
    }

    public SuperRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttr(attrs);
        initView();
    }

    /**
     * view 初始化
     */
    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.super_recycler_view, this, true);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        emptyView = view.findViewById(R.id.empty_view);
        boxHint = view.findViewById(R.id.box_hint);
        textViewHint = view.findViewById(R.id.textView_hint);

        // 配置swipeRefreshLayout
        setSwipeRefreshLayout();

        // 配置recyclerView
        setRecyclerView();

        // 配置emptyView
        setEmptyView();
    }

    /**
     * 属性初始化
     *
     * @param attrs
     */
    private void initAttr(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SuperRecyclerView);

        try {
            isRefresh = a.getBoolean(R.styleable.SuperRecyclerView_isRefresh, true);
            isLoadMore = a.getBoolean(R.styleable.SuperRecyclerView_isLoadMore, true);
            colorRes = a.getResourceId(R.styleable.SuperRecyclerView_swipeColor, R.color.theme_color);
            emptyImageIconId = a.getResourceId(R.styleable.SuperRecyclerView_emptyImageIcon, R.drawable.ic_refresh_black);
            emptyHintID = a.getResourceId(R.styleable.SuperRecyclerView_emptyHint, -1);
            emptyViewHasRetryBtn = a.getBoolean(R.styleable.SuperRecyclerView_emptyViewHasRetryBtn, false);
            retryBtnTextID = a.getResourceId(R.styleable.SuperRecyclerView_emptyRetryBtnHint, R.string.text_retry);
        } finally {
            a.recycle();
        }
    }

    /**
     * 配置swipeRefreshLayout
     */
    private void setSwipeRefreshLayout() {

        // 下拉刷新
        if (!isRefresh) {
            swipeRefreshLayout.setEnabled(false);
        } else {
            swipeRefreshLayout.setEnabled(true);
        }

        // 颜色
        swipeRefreshLayout.setColorSchemeResources(colorRes);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.theme_color);
    }

    /**
     * 设置recyclerView adapter
     *
     * @param adapter
     */
    public void setAdapter(BaseSuperRecyclerViewAdapter adapter) {
        this.adapter = adapter;
        getRecyclerView().setAdapter(adapter);

        if (adapter instanceof SuperRecyclerEventBlockInterface) {
            setOnSuperRecyclerEventBlockInterface((SuperRecyclerEventBlockInterface) adapter);
        }
    }

    /**
     * 配置recyclerView
     */
    private void setRecyclerView() {
        getRecyclerView().setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getContext()) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
//                    if (BuildConfig.DEBUG) {
//                        throw e;
//                    }
                    e.printStackTrace();
                }

                if (adapter != null) {
                    adapter.handleLayoutComplete();
                }
            }
        });
    }

    /**
     * 配置emptyView
     */
    private void setEmptyView() {
        emptyView.setEmptyHint(emptyHintID >= 0 ? getContext().getString(emptyHintID) : "");
        emptyView.setRefreshImage(emptyImageIconId);
        emptyView.setRetryButtonVisible(emptyViewHasRetryBtn ? VISIBLE : GONE);
        emptyView.setRetryButtonText(getContext().getString(retryBtnTextID));

        emptyView.setRetryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSuperRecyclerInterface() != null) {
                    getSuperRecyclerInterface().onRefresh();
                }
            }
        });

        hideEmptyView();
    }

    /**
     * 设置按钮监听
     *
     * @param listener
     */
    public void setRetryButtonListener(View.OnClickListener listener) {
        emptyView.setRetryButtonListener(listener);
    }

    public void setEmptyHint(int emptyHintID) {
        this.emptyHintID = emptyHintID;
        emptyView.setEmptyHint(getContext().getString(emptyHintID));
    }

    public void setEmptyHint(String hint) {
        emptyView.setEmptyHint(hint);
    }

    public void setEmptyImageIcon(int emptyImageIconId) {
        this.emptyImageIconId = emptyImageIconId;
        emptyView.setRefreshImage(emptyImageIconId);
    }

    public void setEmptyViewHasRetryBtn(boolean emptyViewHasRetryBtn) {
        this.emptyViewHasRetryBtn = emptyViewHasRetryBtn;
        emptyView.setRetryButtonVisible(emptyViewHasRetryBtn ? VISIBLE : GONE);
    }

    public void setRetryBtnText(int retryBtnTextID) {
        this.retryBtnTextID = retryBtnTextID;
        emptyView.setRetryButtonText(getContext().getString(retryBtnTextID));
    }

    public void setRetryBtnText(String retryBtnText) {
        emptyView.setRetryButtonText(retryBtnText);
    }

    public void setEmptyViewTitle(String title) {
        emptyView.setEmptyViewTitle(title);
    }

    /**
     * 显示占位layout
     */
    public void showEmptyView() {
        if (emptyView != null &&
                (getAdapter().getItemCount() <= 0 ||
                        (getAdapter().getItemCount() == 1 && getAdapter().getDataList().get(0) == null))) {
            emptyView.setVisibility(VISIBLE);
            if (emptyHintID > 0) {
                emptyView.setEmptyHint(getContext().getString(emptyHintID));
            }
            emptyView.setRefreshImage(emptyImageIconId);
            emptyView.setRetryButtonVisible(emptyViewHasRetryBtn ? VISIBLE : GONE);
        }
    }

    /**
     * 显示占位layout
     *
     * @param hint 自定义显示文字
     */
    public void showEmptyView(String hint) {
        if (emptyView != null &&
                (getAdapter().getItemCount() <= 0 ||
                        (getAdapter().getItemCount() == 1 && getAdapter().getDataList().get(0) == null))) {
            emptyView.setVisibility(VISIBLE);
            emptyView.setEmptyHint(hint);
            emptyView.setRefreshImage(emptyImageIconId);
            emptyView.setRetryButtonVisible(emptyViewHasRetryBtn ? VISIBLE : GONE);
        }
    }

    /**
     * 显示占位layout
     *
     * @param hint  自定义显示文字
     * @param resID 自定义图片 ID
     */
    public void showEmptyView(String hint, int resID) {
        if (emptyView != null &&
                (getAdapter().getItemCount() <= 0 ||
                        (getAdapter().getItemCount() == 1 && getAdapter().getDataList().get(0) == null))) {
            emptyView.setVisibility(VISIBLE);
            emptyView.setEmptyHint(hint);
            emptyView.setRefreshImage(resID);
            emptyView.setRetryButtonVisible(emptyViewHasRetryBtn ? VISIBLE : GONE);
        }
    }

    /**
     * 显示错误页面,可刷新
     *
     * @param hint 错误信息
     */
    public void showErrorEmptyView(String hint) {
        if (emptyView != null &&
                (getAdapter().getItemCount() <= 0 ||
                        (getAdapter().getItemCount() == 1 && getAdapter().getDataList().get(0) == null))) {
            emptyView.setVisibility(VISIBLE);
            emptyView.setRetryButtonVisible(VISIBLE);
            emptyView.setEmptyHint(hint);
            emptyView.setRefreshImage(R.drawable.ic_refresh_black);
        }
    }

    /**
     * 显示网络错误页面,可刷新
     */
    public void showNetErrorEmptyView() {
        if (emptyView != null &&
                (getAdapter().getItemCount() <= 0 ||
                        (getAdapter().getItemCount() == 1 && getAdapter().getDataList().get(0) == null))) {
            emptyView.setVisibility(VISIBLE);
            emptyView.setRetryButtonVisible(VISIBLE);
            emptyView.setEmptyHint(getContext().getString(R.string.text_net_work_error));
            emptyView.setRefreshImage(R.mipmap.ic_no_connected);
        }
    }

    /**
     * 隐藏占位layout
     */
    public void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
    }

    /**
     * 在最后一行，显示默认progressbar，加载之前调用
     */
    public void showLoadMore() {
        if (getAdapter() != null) {
            getAdapter().showLoadMore();
        }
    }

    /**
     * 隐藏默认progressbar，加载完毕后调用
     */
    public void hideLoadMore() {
        if (getAdapter() != null) {
            getAdapter().hideLoadMore();
        }
    }

    /**
     * 是否在转菊花
     *
     * @return
     */
    public boolean isRefreshing() {
        return getSwipeRefreshLayout().isRefreshing();
    }

    /**
     * 设置菊花动画是否显示
     *
     * @param value
     */
    public void setRefreshing(boolean value) {
        getSwipeRefreshLayout().setRefreshing(value);
    }

    /**
     * 下拉刷新
     * 自动获取更多
     * 占位页面重新加载
     */
    public interface SuperRecyclerInterface {
        void onRefresh();

        void onLoadMore();

        void onEmpty();
    }

    /**
     * event被拦截时，onTouchDown 时调用
     */
    public interface SuperRecyclerEventBlockInterface {
        void onEventBlockTouch(MotionEvent event);
    }

    /**
     * item被点击时调用
     */
    public interface SuperRecyclerItemClickInterface {
        void onItemClick(View view, int position);
    }

    /**
     * 是否正在加载更多
     *
     * @return
     */
    public boolean isLoadingMore() {
        if (getAdapter() != null) {
            return getAdapter().isLoading();
        } else {
            return false;
        }
    }

    /**
     * 更新条数提示
     *
     * @param count
     */
    private void showRefreshHint(int count) {
        if (count > 0) {
            textViewHint.setText(getContext().getString(R.string.update_news_size, count));

            // 显示动画
            if (boxHint.getAnimation() != null && !boxHint.getAnimation().hasEnded()) {
                boxHint.getAnimation().cancel();
                boxHint.setVisibility(GONE);
            }

            boxHint.setAlpha(1);
            boxHint.setVisibility(VISIBLE);
            boxHint.animate().alpha(0).setDuration(300).setStartDelay(1500).setListener(new Animator.AnimatorListener
                    () {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    boxHint.setVisibility(GONE);
                    boxHint.clearAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    boxHint.setVisibility(GONE);
                    boxHint.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }
    }

    /**
     * smooth scroll
     *
     * @param position
     */
    public void smoothScrollToPosition(int position) {
        recyclerView.smoothScrollToPosition(position);
    }

    /**
     * sudden scroll
     *
     * @param position
     */
    public void scrollToPosition(int position) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            ((LinearLayoutManager) (recyclerView.getLayoutManager())).scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isEventBlock()) {
            if (getSuperRecyclerEventBlockInterface() != null) {
                getSuperRecyclerEventBlockInterface().onEventBlockTouch(ev);
            }
            return true;
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }
}
