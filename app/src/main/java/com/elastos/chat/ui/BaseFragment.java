package com.elastos.chat.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment基类
 *
 * @author rczhang on 2018/05/08.
 */
public class BaseFragment extends Fragment {
    private static final boolean LOG_LIFECYCLE = false; // 是否打印生命周期，用于调试

    private boolean isActivityCreated = false; // 页面是否已初始化
    private boolean isStarted = false; // 页面是否启动过

    public boolean isStarted() {
        return isStarted;
    }

    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        log("setUserVisibleHint: " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
        if (isActivityCreated) {
            if (isVisibleToUser) {
                onPageStart();
                if (!isStarted) {
                    isStarted = true;
                    onPageFirstStart();
                }
            } else {
                onPageEnd();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isActivityCreated = true;
        if (getUserVisibleHint()) {
            if (!isStarted) {
                isStarted = true;
                onPageFirstStart();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy");
    }

    @Override
    public void onStart() {
        super.onStart();
        log("onStart");
        if (getUserVisibleHint()) {
            onPageStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        log("onStop");
        if (getUserVisibleHint()) {
            onPageEnd();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        log("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        log("onPause");
    }

    /**
     * 页面展示到前台
     * todo 目前onPageStart与onPageEnd不能保证完全的一一对应，需要进一步测试与优化
     */
    public void onPageStart() {
        log("onPageStart");
    }

    /**
     * 页面从前台离开
     */
    public void onPageEnd() {
        log("onPageEnd");
    }

    /**
     * 当页面首次可见时调用。调用时页面控件已经完成初始化
     * 用于ViewPager下的页面懒加载，在一个生命周期内只会调用一次
     */
    public void onPageFirstStart() {
        log("onPageFirstStart");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        log("onCreateOptionsMenu");
    }

    private void log(String message) {
        if (LOG_LIFECYCLE) {
            Log.d(this.getClass().getSimpleName(), message);
        }
    }
}
