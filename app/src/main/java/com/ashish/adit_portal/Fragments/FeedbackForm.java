package com.ashish.adit_portal.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ashish.adit_portal.R;
import com.ashish.adit_portal.helper.FormResponse;
import com.ashish.adit_portal.theory_question_fragments.Question1;

public class FeedbackForm extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView= inflater.inflate(R.layout.fragment_feedback_form, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentTransaction ftransaction = getFragmentManager().beginTransaction();
                        Fragment fragment = new RecycleFragment();
                        ftransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ftransaction.replace(R.id.framelayout, fragment);
                        ftransaction.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        final Bundle arguments=getArguments();
        final FormResponse response=new FormResponse();
        arguments.putSerializable("object",response);
        final TextView enrollment_number=(TextView)rootView.findViewById(R.id.enrollment_text_feed);
        TextView subject_code=(TextView)rootView.findViewById(R.id.subject_code_text);
        TextView subject=(TextView) rootView.findViewById(R.id.Subject_text_feed);
        TextView faculty=(TextView)rootView.findViewById(R.id.Faculty_text_feed);
        enrollment_number.setText(arguments.getString("enrollment_number"));
        subject_code.setText(arguments.getString("subject_code"));
        subject.setText(arguments.getString("subject"));
        faculty.setText(arguments.getString("faculty"));
        Button start= (Button) rootView.findViewById(R.id.start_filling);
        final Button hide=(Button)rootView.findViewById(R.id.hide_button);
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text= (String) hide.getText();
                if(text.equals(getString(R.string.hide))){

                    hide.setText(R.string.show);
                    enrollment_number.setText("Hidden");
                }else{

                    hide.setText(R.string.hide);
                    enrollment_number.setText(arguments.getString("enrollment_number"));
                }
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ftransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = new Question1();
                fragment.setArguments(arguments);
                response.addresponse(0,R.id.hide_button,hide.getText().toString());
                ftransaction.replace(R.id.framelayout, fragment);
                ftransaction.commit();
            }
        });
        return rootView;
    }
}
