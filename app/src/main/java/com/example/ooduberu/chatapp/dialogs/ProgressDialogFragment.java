package com.example.ooduberu.chatapp.dialogs;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.example.ooduberu.chatapp.R;

import static android.view.Gravity.CENTER;

public class ProgressDialogFragment extends DialogFragment {
    private String msgId;

    public static ProgressDialogFragment newInstance(String msg) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("msgId", msg);
        fragment.setArguments(args);
        return fragment;
    }

    // Empty constructor required for DialogFragment
    public ProgressDialogFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msgId = getArguments().getString("msgId");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_progress, container, false);
        TextView message = view.findViewById(R.id.progress_message);
        message.setText(msgId);
        this.setCancelable(false);
        return view;
    }

//    @Override
//    public void onCancel(DialogInterface dialog) {
//        getActivity().finish();
//        super.onCancel(dialog);
//    }

    @Override
    public void onResume() {
        //onResumeFragments();
        Window window = getDialog().getWindow();
        if (window != null) {
            Point point = new Point();
            window.getWindowManager().getDefaultDisplay().getSize(point);
            // int height = point.x;
            // int width = point.y;

            // DisplayMetrics dm = new DisplayMetrics();
            // window.getWindowManager().getDefaultDisplay().getMetrics(dm);
            // int height = (int) (dm.heightPixels * 0.7);
            // int width = (int) (dm.widthPixels * 0.8);

            //window.setLayout(width, height);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setGravity(CENTER);
        }
        super.onResume();
    }
}
