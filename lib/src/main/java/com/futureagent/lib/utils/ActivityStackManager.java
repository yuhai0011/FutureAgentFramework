package com.futureagent.lib.utils;


import com.futureagent.lib.view.BaseActivity;

import java.util.Stack;

/**
 * @author skywalker
 * @date 15/12/18
 * @description
 * @Email: yuhai833@126.com
 */
public class ActivityStackManager {
    private Stack<BaseActivity> activityStack;
    private static ActivityStackManager activityStackManager;

    /**
     * 单例 ActivityStackManager
     */
    public static final ActivityStackManager getInstance() {
        if (activityStackManager == null) {
            activityStackManager = new ActivityStackManager();
        }
        return activityStackManager;
    }

    /**
     * 单例 activityStack
     */
    public Stack<BaseActivity> getActivityStack() {
        if (activityStack == null) {
            activityStack = new Stack<BaseActivity>();
        }
        return activityStack;
    }

    /**
     * 入栈
     */
    public void pushToStack(BaseActivity baseActivity) {
        getActivityStack().push(baseActivity);
    }

    /**
     * 出栈
     */
    public void popFromStack(BaseActivity baseActivity) {
        getActivityStack().remove(baseActivity);
    }

    /**
     * 获取栈顶activity
     */
    public BaseActivity getTopActivity() {
        return getActivityStack().peek();
    }

    /**
     * 判断该activity是否已入栈
     */
    public boolean hasActivity(Class<? extends BaseActivity> activityClass) {
        for (BaseActivity baseActivity : getActivityStack()) {
            if (baseActivity.getClass() == activityClass) {
                return true;
            }
        }
        return false;
    }

    /**
     * 清除所有activity
     */
    public void clearActivityWhenExitApplication() {
        for (BaseActivity baseActivity : getActivityStack()) {
            if (baseActivity != null) {
                baseActivity.finish();
                popFromStack(baseActivity);
            }
        }
    }

    /**
     * recreate栈内所有activity
     */
    public void recreateActivityInStack() {
        for (BaseActivity baseActivity : getActivityStack()) {
            if (baseActivity != null) {
                baseActivity.recreate();
            }
        }
    }
}
