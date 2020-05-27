package self.zhangkang.android.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarContainer;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

/**
 * Created on 2018/5/17
 * Title: 基类Activity 只提供标题相关的操作
 * Description:
 *
 * @author 张康
 * update 2018/5/17
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    protected TextView mTitleTextView;
    protected CharSequence mTitleText;
    private Toolbar mToolbar;
    private View mCustomTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initTitleView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initTitleView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initTitleView();
    }


    @Override
    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleText = title;
        if (null != mTitleTextView) {
            mTitleTextView.setText(title);
        }
    }

    @Override
    public Intent getIntent() {
        Intent intent = super.getIntent();
        return null == intent ? new Intent() : intent;
    }

    @Override
    public void onBackPressed() {
        if (onBackClick()) {
            return;
        }
        super.onBackPressed();
    }

    /**
     * 返回键点击事件，点击标题和物理返回键都会调用改方法
     *
     * @return 返回false 表示没有消费该返回事件，会继续走系统的返回逻辑，返回true表示消费了返回事件，不会继续走系统的返回逻辑
     */
    public boolean onBackClick() {
        return false;
    }


    /**
     * 设置自定义标题
     *
     * @param customTitleView 自定义标题
     */
    protected void setCustomTitleView(View customTitleView) {
        //如果当前标题已经初始化，设置自定义标题，如果当前标题没有初始化，记录下
        //自定义标题，在初始化的时候设置上
        if (null != mToolbar) {
            initCustomTitle(mToolbar, customTitleView);
            mCustomTitleView = null;
        } else {
            mCustomTitleView = customTitleView;
        }
    }

    /**
     * 初始化标题
     */
    private void initTitleView() {
        ActionBar supportActionBar = getSupportActionBar();
        if (null == supportActionBar) {
            return;
        }
        supportActionBar.setDisplayShowTitleEnabled(false);
        //用来隐藏Toolbar的阴影效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActionBarContainer actionBarContainer = findViewById(R.id.action_bar_container);
            actionBarContainer.setElevation(0);
        }
        Toolbar toolbar = findViewById(R.id.action_bar);
        //如果自定义标题为空，初始化通用的标题，否则初始话自定义标题
        if (null == mCustomTitleView) {
            initCommonTitle(toolbar);
        } else {
            initCustomTitle(toolbar, mCustomTitleView);
        }
        setTitle(getTitle());
        mToolbar = toolbar;
    }

    /**
     * 初始化自定义标题
     *
     * @param toolbar     当前Toolbar
     * @param customTitle 自定义标题View
     */
    private void initCustomTitle(Toolbar toolbar, View customTitle) {
        //移除掉所有的子View
        toolbar.removeAllViews();
        //设置左右两边的边距为0
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetsAbsolute(0, 0);
        ViewGroup.LayoutParams layoutParams = customTitle.getLayoutParams();
        //没有设置LayoutParams 创建一个占满父控件的LayoutParams
        if (null == layoutParams) {
            layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        } else {
            if (!(layoutParams instanceof Toolbar.LayoutParams)) {
                layoutParams = new Toolbar.LayoutParams(layoutParams);
            }
        }
        customTitle.setLayoutParams(layoutParams);
        toolbar.setNavigationIcon(null);
        toolbar.addView(customTitle);
    }

    /**
     * 初始化通用标题 只有左边的返回按钮和中间的标题
     *
     * @param toolbar 当前Toolbar
     */
    private void initCommonTitle(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.base_icon_left_back);
        //如果当前的TextView为null，则创建一个
        if (null == mTitleTextView) {
            mTitleTextView = createTitleTextView();
        } else {
            ViewParent parent = mTitleTextView.getParent();
            if (null != parent) {
                ((ViewGroup) parent).removeView(mTitleTextView);
            }
        }
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        mTitleTextView.setLayoutParams(layoutParams);
        toolbar.addView(mTitleTextView);
        if (!TextUtils.isEmpty(mTitleText)) {
            mTitleTextView.setText(mTitleText);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private TextView createTitleTextView() {
        TextView textView = new TextView(this);
        //设置显示为1行
        textView.setSingleLine();
        //设置最多显示多少个字
        textView.setEms(10);
        //设置省略号在尾部
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        textView.setTextSize(18);
        return textView;
    }

}
