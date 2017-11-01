package com.futureagent.lib.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * @author skywalker
 * @date 16-8-23
 * @description
 * @Email: yuhai833@126.com
 */
public abstract class BaseFragment extends Fragment {

    //布局文件id
    protected int mResId = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mResId = getLayoutResId();
        View view = inflater.inflate(mResId, container, false);

        ButterKnife.bind(this, view);
        initView(view);

        return view;
    }


    /**
     * 返回布局文件资源引用id
     *
     * @return
     */
    protected abstract int getLayoutResId();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public abstract void initView(View view);

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
