package self.zhangkang.android.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created on 2019/4/12
 * Title:
 * Description: 和{@link android.support.v4.view.ViewPager}和{@link android.support.v4.app.FragmentStatePagerAdapter}
 * 配合使用更好的处理onResume和onPause方法
 *
 * @author Android-张康
 * update 2019/4/12
 */
public class BaseStatePageFragment extends Fragment {
    private View mRootView;
    private boolean isOnCreateView;
    private boolean mIsVisibleToUser;
    private boolean isFirstLoadData = true;
    private boolean mIsOnResume;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isOnCreateView = true;
        if (null == mRootView) {
            mRootView = onCreateViewReal(inflater, container, savedInstanceState);
        }
        return mRootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //用来记录当前对用户是否可见
        mIsVisibleToUser = isVisibleToUser;
        //使用FragmentPagerAdapter时系统会自动调用该方法，但是此时无法确定页面是否创建，如果
        //当前isOnCreateView==false，说明当前页面还没创建完成，不做处理
        if (!isOnCreateView) {
            return;
        }
        //判断当前对用户是否可见，如果可见，开始加载数据，不可见停止加载数据
        if (isVisibleToUser) {
            //对用户可见，将是否第一次加载数据标志位设位false
            isFirstLoadData = false;
            mIsOnResume = true;
            onResumeReal();
        } else {
            mIsOnResume = false;
            onPauseReal();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //如果当前对用户可见，但时是否是第一次加载数据的标志位为true，说明没有开始加载过数据，开始加载数据
        //并将是否第一次加载数据标志位设位false
        if (mIsVisibleToUser && isFirstLoadData) {
            isFirstLoadData = false;
            onResumeReal();
        } else if (mIsVisibleToUser && !mIsOnResume) {
            onResumeReal();
        }
        mIsOnResume = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        //当前对用户可见 且处于可交互状态时
        if (mIsVisibleToUser && mIsOnResume) {
            onPauseReal();
        }
        mIsOnResume = false;
    }

    @Override
    public void onDestroyView() {
        if (null != mRootView) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        super.onDestroyView();
        isOnCreateView = false;
    }

    /**
     * 真正的初始化方法，减少View的重复创建
     *
     * @param inflater           inflater
     * @param container          放置Fragment的布局
     * @param savedInstanceState 恢复Fragment的
     * @return View
     */
    protected View onCreateViewReal(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    /**
     * 真正的onResume方法
     */
    protected void onResumeReal() {

    }

    /**
     * 真正的onPause
     */
    protected void onPauseReal() {

    }


}
