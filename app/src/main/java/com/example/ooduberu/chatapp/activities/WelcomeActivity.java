package com.example.ooduberu.chatapp.activities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ooduberu.chatapp.R;
import com.example.ooduberu.chatapp.utility.AppPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WelcomeActivity extends AppCompatActivity {
    Unbinder unbinder;

    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.dots) TabLayout dots;
    @BindView(R.id.btn_skip) MaterialButton btnSkip;
    @BindView(R.id.btn_next) MaterialButton btnNext;
    @BindView(R.id.back) MaterialButton  btnBack;

    private int totalSteps = 4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeStatusBar();
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        unbinder = ButterKnife.bind(this);

        dots.setupWithViewPager(viewPager, true);
        Pager adapter = new Pager(getSupportFragmentManager(),totalSteps);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setOffscreenPageLimit(0);

    }

    @OnClick(R.id.btn_skip)
    public void skipOnBoarding(){
        launchHomeScreen();
    }

    @OnClick(R.id.btn_next)
    public void nextPage(){
        int nextPage = viewPager.getCurrentItem() + 1;
        if (nextPage < totalSteps) {
            viewPager.setCurrentItem(nextPage, true);
        } else {
            launchHomeScreen();
        }
    }

    @OnClick(R.id.back)
    public void previousPage(){
        int lastPage = viewPager.getCurrentItem() - 1;
        if(lastPage > -1){
            viewPager.setCurrentItem(lastPage, true);
        }
    }


    private void launchHomeScreen(){
        AppPreference.setIsFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        finish();
    }


    public void removeStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }



    private ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            if(position == 0){
                btnNext.setText("Next");
                btnSkip.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.INVISIBLE);
                Step1.play();
            }else if(position == 1){
                btnNext.setText("Next");
                btnSkip.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                Step2.play();
            }else if(position == 2){
                btnNext.setText("Next");
                btnSkip.setVisibility(View.VISIBLE);
                Step3.play();
            }else {
                btnNext.setText("Got it!");
                btnSkip.setVisibility(View.GONE);
                Step4.play();
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    public class Pager extends FragmentPagerAdapter {
        private int tabCount;

        public Pager(FragmentManager fm, int tabCount) {
            super(fm);
            this.tabCount= tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return Step1.newInstance();
                case 1: return Step2.newInstance();
                case 2: return Step3.newInstance();
                case 3: return Step4.newInstance();
                default:return null;
            }
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }

    public static class Step1 extends Fragment {
        private static LottieAnimationView  animationView;

        public static void play(){
            animationView.playAnimation();
        }

        public static Step1 newInstance() {
            return  new Step1();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_slider_one, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            animationView = view.findViewById(R.id.animation);
        }
    }

    public static class Step2 extends Fragment {
        private static LottieAnimationView  animationView;

        public static void play(){
            animationView.playAnimation();
        }

        public static Step2 newInstance() {
            return  new Step2();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_slider_two, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            animationView = view.findViewById(R.id.animation);
        }
    }

    public static class Step3 extends Fragment {

        private static LottieAnimationView  animationView;

        public static void play(){
            animationView.playAnimation();
        }

        public static Step3 newInstance() {
            return  new Step3();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_slider_three, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            animationView = view.findViewById(R.id.animation);
        }
    }

    public static class Step4 extends Fragment {
        private static LottieAnimationView animationView;

        public static void play(){
            animationView.playAnimation();
        }

        public static Step4 newInstance() {
            return new Step4();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_slider_four, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            animationView = view.findViewById(R.id.animation);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
