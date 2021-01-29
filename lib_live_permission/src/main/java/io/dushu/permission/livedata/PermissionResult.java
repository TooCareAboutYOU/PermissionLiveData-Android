package io.dushu.permission.livedata;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

/**
 * @author zhangshuai
 * @date 2021/1/29 10:29
 * @description
 */
public class PermissionResult{

    /**
     * 全部同意
     */
    public static final int GRANT = 2021;
    /**
     * 拒绝且勾选了不再询问，permissions——被拒绝的权限
     */
    public static final int DENY = 2022;
    /**
     * 只是拒绝，没有勾选不再询问，permissions——被拒绝的权限
     */
    public static final int RATIONALE = 2023;

    @IntDef({GRANT, DENY, RATIONALE})
    @Retention(RetentionPolicy.SOURCE)
    @interface STATE {}

    private @STATE int mState;
    private List<String> arrayList;

    public PermissionResult(@STATE final int state) {
        mState = state;
    }

    public PermissionResult(@STATE final int state, final List<String> arrayList) {
        mState = state;
        this.arrayList = arrayList;
    }

    /**
     * 权限状态
     */
    @STATE
    public int getState() {
        return mState;
    }

    /**
     * 包含权限
     */
    public List<String> getPermissions() {
        return this.arrayList;
    }
}
