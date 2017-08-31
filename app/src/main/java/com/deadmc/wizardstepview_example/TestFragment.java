package com.deadmc.wizardstepview_example;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestFragment extends Fragment {

    private static final String POSITION = "position";
    private int position = 0;


    public TestFragment() {
    }

    public static TestFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()) {
            position = bundle.getInt(POSITION, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_test, container, false);
        TextView testTextView = view.findViewById(R.id.testTextView);
        testTextView.setText("Fragment #"+position);
        return view;
    }
}
