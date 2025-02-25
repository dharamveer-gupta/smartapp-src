package com.youle.gamebox.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import butterknife.InjectView;
import com.viewpagerindicator.TabPageIndicator;
import com.youle.gamebox.ui.R;
import com.youle.gamebox.ui.adapter.AppFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/5/13.
 */
public class AppDetailFragment extends BaseFragment {

    @InjectView(R.id.indicator)
    TabPageIndicator indicator;
    @InjectView(R.id.appdetail_viewpage)
    ViewPager appdetail_viewpage;
    List<Fragment> fragmentList = null;
    private long id;
    private int resouce;
    private int tab  =0 ;
    public AppDetailFragment() {

    }

    public AppDetailFragment(long id, int resouce,int tab) {
        this.id = id;
        this.resouce = resouce;
        this.tab=tab ;
    }

    @Override
    protected int getViewId() {
        return R.layout.appdetail_fragment_layout;
    }

    @Override
    protected String getModelName() {
        return "游戏详情";
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (fragmentList == null) {
            loadData();
        }
    }


    private void loadData() {
        fragmentList = new ArrayList<Fragment>();
        AppDetailGamesFragment appDetailGamesFragment = new AppDetailGamesFragment(id, resouce);
        appDetailGamesFragment.setArguments(getBundle("详情"));
        fragmentList.add(appDetailGamesFragment);
        GameCommentFragment appDetailCommentsFragment = new GameCommentFragment(id,resouce);
        appDetailCommentsFragment.setArguments(getBundle("评论"));
        fragmentList.add(appDetailCommentsFragment);
        AllGiftFragment allGiftFragment = new AllGiftFragment();
        allGiftFragment.setGameId(id + "");
        allGiftFragment.setArguments(getBundle("礼包"));
        fragmentList.add(allGiftFragment);
        GonglueListFragment gonglueListFragment = new GonglueListFragment();
        gonglueListFragment.setArguments(getBundle("攻略"));
        gonglueListFragment.setGameId(id + "");
        fragmentList.add(gonglueListFragment);
        appdetail_viewpage.setAdapter(new AppFragmentPagerAdapter(getActivity().getSupportFragmentManager(), fragmentList));
        appdetail_viewpage.setCurrentItem(0);
        indicator.setViewPager(appdetail_viewpage);
        indicator.setOnPageChangeListener(new MyOnPageChangeListener());
        indicator.setCurrentItem(tab);
    }

    private Bundle getBundle(String tag) {
        Bundle bundle = new Bundle();
        bundle.putString("fragmentTag", tag);
        return bundle;
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            BaseFragment baseFragment = (BaseFragment) fragmentList.get(i);
            if (baseFragment != null) {

            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

}
