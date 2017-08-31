package com.deadmc.wizardstepview_example;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.deadmc.wizardstepview.WizardStepView;

public class MainActivity extends AppCompatActivity {

    WizardStepView wizardStepView;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        viewPager = findViewById(R.id.viewPager);
        wizardStepView = findViewById(R.id.wizardStepView);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        wizardStepView.setViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        @Override
        public Fragment getItem(int position) {
            return TestFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
