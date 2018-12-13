package com.example.ooduberu.chatapp.activities;

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

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private int totalSteps = 4;
    private MaterialButton btnSkip, btnNext, btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeStatusBar();
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        btnBack = findViewById(R.id.back);
        viewPager = findViewById(R.id.view_pager);
        TabLayout dots = findViewById(R.id.dots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);


        dots.setupWithViewPager(viewPager, true);
        Pager adapter = new Pager(getSupportFragmentManager(),4);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setOffscreenPageLimit(0);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextPage = viewPager.getCurrentItem() + 1;
                if (nextPage < totalSteps) {
                    viewPager.setCurrentItem(nextPage, true);
                } else {
                    launchHomeScreen();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastPage = viewPager.getCurrentItem() - 1;
                if(lastPage > -1){
                    viewPager.setCurrentItem(lastPage, true);
                }
            }
        });


    }


    private void launchHomeScreen(){

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

}
