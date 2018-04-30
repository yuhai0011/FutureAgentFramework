package com.futureagent.lib.view.recyclerview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futureagent.lib.R;
import com.futureagent.lib.utils.ListUtils;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseSuperRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> implements
        RecyclerView.OnChildAttachStateChangeListener, SuperRecyclerView.SuperRecyclerItemClickInterface {

    // 显示类型
    public static final int TYPE_PROGRESS_BAR = -1000;
    private WeakReference<SuperRecyclerView> superRecyclerView;
    // 数据
    private List<T> dataList;
    // load more的必备参数
    private boolean loading;

    // 接口
    private OnLoadMore onLoadMore;

    private Handler handler;

    public BaseSuperRecyclerViewAdapter(@NonNull final SuperRecyclerView superRecyclerView) {

        handler = new Handler(Looper.getMainLooper());

        this.superRecyclerView = new WeakReference<SuperRecyclerView>(superRecyclerView);
        superRecyclerView.getRecyclerView().addOnChildAttachStateChangeListener(this);

        if (superRecyclerView.getRecyclerView().getLayoutManager() instanceof LinearLayoutManager &&
                superRecyclerView.isLoadMore()) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)
                    superRecyclerView.getRecyclerView().getLayoutManager();

            superRecyclerView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    // 滑动获取更多
                    if (
                            !superRecyclerView.isRefreshing() &&
                                    !loading &&
                                    lastVisibleItem == totalItemCount - 1 &&
                                    getOnLoadMore() != null &&
                                    !isClickToLoadMore() &&
                                    dy > 0
                            ) {

                        // 如果最后一个item已经是菊花了，且还没有转，则必须手动转
                        if (getDataList().size() > 0 && getDataList().get(getDataList().size() - 1) == null) {
                            View view = superRecyclerView.getRecyclerView().getLayoutManager().findViewByPosition(getDataList().size() - 1);
                            if (view == null) {
                                return;
                            }

                            final ProgressWheel progressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);

                            if (progressWheel.getVisibility() != View.VISIBLE) {
                                return;
                            }
                        }

                        loading = true;
                        getOnLoadMore().loadMore();
                    }
                }
            });
        }
    }

    // get & set
    public OnLoadMore getOnLoadMore() {
        return onLoadMore;
    }

    public void setOnLoadMore(OnLoadMore onLoadMore) {
        this.onLoadMore = onLoadMore;
    }

    public List<T> getDataList() {
        if (dataList == null) {
            dataList = new ArrayList<T>();
        }
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public WeakReference<SuperRecyclerView> getSuperRecyclerView() {
        return superRecyclerView;
    }

    public void setSuperRecyclerView(WeakReference<SuperRecyclerView> superRecyclerView) {
        this.superRecyclerView = superRecyclerView;
    }

    private boolean isLoadMore() {
        return superRecyclerView.get().isLoadMore();
    }

    /**
     * layout结束后的处理
     */
    public void handleLayoutComplete() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                // add
                if (isClickToLoadMore()) {
                    if (getDataList().size() == 1 && getDataList().get(0) == null) {
                        hideLoadMore();
                    } else {
                        showLoadMore();
                    }
                }

                // handle
                if (isLoading() || getDataList().size() == 0 || getDataList().get(getDataList().size() - 1) != null) {
                    return;
                }

                View view = superRecyclerView.get().getRecyclerView().getLayoutManager().findViewByPosition(getDataList().size() - 1);

                if (view == null) {
                    return;
                }

                final ProgressWheel progressWheel = view.findViewById(R.id.progressWheel);
                final TextView textViewLoadMore = view.findViewById(R.id.textView_click_load_more);

                if (progressWheel == null || textViewLoadMore == null) {
                    return;
                }

                if (isClickToLoadMore()) {
                    progressWheel.setVisibility(View.INVISIBLE);
                    textViewLoadMore.setVisibility(View.VISIBLE);

                    textViewLoadMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onLoadMore != null && superRecyclerView != null && superRecyclerView.get() != null && !superRecyclerView.get().isRefreshing()) {

                                progressWheel.setVisibility(View.VISIBLE);
                                textViewLoadMore.setVisibility(View.INVISIBLE);

                                loading = true;
                                onLoadMore.loadMore();
                            }
                        }
                    });
                } else {
                    progressWheel.setVisibility(View.VISIBLE);
                    textViewLoadMore.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 在最后一行，显示默认progressbar，加载之前调用
     */
    public void showLoadMore() {
        if (getDataList().size() == 0) {
            return;
        }

        if (getDataList().get(getDataList().size() - 1) == null) {
            return;
        }

        if (!isLoadMore()) {
            return;
        }

        getDataList().add(null);
        notifyItemInserted(getDataList().size() - 1);
    }

    /**
     * 隐藏默认progressbar，加载完毕后调用
     */
    public void hideLoadMore() {

        if (getDataList().size() == 0) {
            return;
        }

        if (getDataList().get(getDataList().size() - 1) == null) {
            getDataList().remove(getDataList().size() - 1);
            notifyItemRemoved(getDataList().size());
        }

        setLoaded();
    }

    /**
     * 数据刷新（默认自动刷新数据）
     *
     * @param dataList
     */
    public void dataRefresh(List<T> dataList) {
        dataRefresh(dataList, true);
    }

    /**
     * 数据刷新(下拉刷新时调用)
     *
     * @param dataList
     * @param refresh
     */
    public void dataRefresh(List<T> dataList, boolean refresh) {
        this.dataList = dataList;
        if (this.dataList != null) {
            if (isLoading()) {
                this.dataList.add(null);
            }
        }
        if (refresh) {
            notifyDataSetChanged();
        }
    }

    /**
     * 数据加载更多（获取更多的时候调用）
     *
     * @param dataList
     */
    public void dataMore(List<T> dataList) {
        if (!ListUtils.isEmpty(dataList)) {
            for (T entity : dataList) {
                getDataList().add(entity);
                notifyItemInserted(getDataList().size());
            }
        }
    }

    /**
     * 完成加载更多
     */
    private void setLoaded() {
        loading = false;
    }

    /**
     * 是否正在加载更多
     *
     * @return
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * 是否是点击加载更多
     *
     * @return
     */
    protected boolean isClickToLoadMore() {
        try {
            return
                    (
                            ((LinearLayoutManager) getSuperRecyclerView().get().getRecyclerView().getLayoutManager())
                                    .findFirstCompletelyVisibleItemPosition() <= 0
                    );

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return getDataList().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getDataList().get(position) == null) {
            return TYPE_PROGRESS_BAR;
        } else {
            return getMyItemViewType(position);
        }
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PROGRESS_BAR) {
            // theme
            ((ProgressBarHolder) holder).textViewLoadMore.setTextColor(getContext().getResources().getColor(R.color.theme_color));
            ((ProgressBarHolder) holder).textViewLoadMore.setVisibility(View.INVISIBLE);
            ((ProgressBarHolder) holder).progressWheel.setVisibility(View.VISIBLE);
        } else {
            onBindMyViewHolder(holder, position);
        }
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PROGRESS_BAR) {
            return new ProgressBarHolder(LayoutInflater.from(superRecyclerView.get().getContext()).inflate(R.layout.item_progressbar, parent, false));
        } else {
            return onCreateMyViewHolder(parent, viewType);
        }
    }

    // 必须要重写的
    public abstract int getMyItemViewType(int position);

    public abstract BaseRecyclerViewHolder onCreateMyViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindMyViewHolder(BaseRecyclerViewHolder holder, int position);

    protected Context getContext() {
        return superRecyclerView.get().getContext();
    }

    @Override
    public void onChildViewAttachedToWindow(final View view) {

        // 添加OnClick 事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getSuperRecyclerView() != null && getSuperRecyclerView().get() != null) {
                    onItemClick(v, getSuperRecyclerView().get().getRecyclerView().getChildViewHolder(v).getAdapterPosition());
                }
            }
        });
    }

    @Override
    public void onChildViewDetachedFromWindow(View view) {
    }

    @Override
    public void onItemClick(View view, int position) {
        if (getSuperRecyclerView() != null && getSuperRecyclerView().get() != null && getSuperRecyclerView().get().getSuperRecyclerItemClickInterface() != null) {
            getSuperRecyclerView().get().getSuperRecyclerItemClickInterface().onItemClick(view, position);
        }
    }

    // 获取更多 接口
    public interface OnLoadMore {
        void loadMore();
    }

    // 加载更多的progressBar
    public static class ProgressBarHolder extends BaseRecyclerViewHolder {
        ProgressWheel progressWheel;
        TextView textViewLoadMore;

        public ProgressBarHolder(View itemView) {
            super(itemView);

            progressWheel = itemView.findViewById(R.id.progressWheel);
            textViewLoadMore = itemView.findViewById(R.id.textView_click_load_more);
        }
    }

}
