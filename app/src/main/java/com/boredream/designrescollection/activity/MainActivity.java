package com.boredream.designrescollection.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.boredream.bdcodehelper.fragment.FragmentController;
import com.boredream.designrescollection.R;
import com.boredream.designrescollection.base.BaseActivity;
import com.boredream.designrescollection.fragment.HomeFragment;
import com.boredream.designrescollection.fragment.UserFragment;
import com.boredream.designrescollection.utils.UpdateUtils;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup mRadioGroup;
    private RadioButton mHomePageButton;
    private FragmentController mFragmentController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        setCouldDoubleBackExit(true);

        mRadioGroup = (RadioGroup) findViewById(R.id.rg_bottom_tab);
        mHomePageButton = (RadioButton) findViewById(R.id.tab_home);

        mRadioGroup.setOnCheckedChangeListener(this);
    }

    private void initData() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new UserFragment());

        mFragmentController = new FragmentController(this, R.id.fl_content, fragments);

        // 默认Fragment
        mHomePageButton.setChecked(true);
        mFragmentController.showFragment(0);
        UpdateUtils.checkUpdate(this, false);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.tab_home:
                mFragmentController.showFragment(0);
                break;
            case R.id.tab_me:
                mFragmentController.showFragment(1);
                break;
        }
    }

}
