package com.ashish.adit_portal.theory_question_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ashish.adit_portal.R;
import com.ashish.adit_portal.helper.FormResponse;

/**
 * Created by Ashish on 12/11/2016.
 */

public class Question8 extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView=inflater.inflate(R.layout.fragment_question1, container, false);
        final Bundle arguments=getArguments();
        final FormResponse response=(FormResponse) arguments.get("object");
        TextView question=(TextView)rootView.findViewById(R.id.Question);
        TextView counter=(TextView)rootView.findViewById(R.id.questioncounter);
        final RadioGroup options=(RadioGroup)rootView.findViewById(R.id.options);
        RadioButton option1=(RadioButton)rootView.findViewById(R.id.firstradioButton);
        RadioButton option2=(RadioButton)rootView.findViewById(R.id.secondradioButton);
        RadioButton option3=(RadioButton)rootView.findViewById(R.id.thirdradioButton);
        Button next=(Button) rootView.findViewById(R.id.next);
        final Button previous=(Button) rootView.findViewById(R.id.previous);
        if(arguments.get("type").toString().equals("theory")) {
            counter.setText("8 out of 19 Questions");
            question.setText(R.string.question8);
            option1.setText(R.string.Excellent);
            option2.setText(R.string.average);
            option3.setText(R.string.poor);

        }else{
            counter.setText("8 out of 10 Questions");
            question.setText(R.string.pquestion8);
            option1.setText(R.string.Excellent);
            option2.setText(R.string.average);
            option3.setText(R.string.poor);

        }
        if (response.getallresponses().get(8) != null) {
            options.check(response.getResponseId(8));
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(options.getCheckedRadioButtonId()==-1)){
                    FragmentTransaction ftransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new Question9();
                    fragment.setArguments(arguments);
                    int viewid=options.getCheckedRadioButtonId();
                    String selectedoption=((RadioButton)rootView.findViewById(viewid)).getText().toString();
                    response.addresponse(8, viewid, selectedoption);
                    ftransaction.replace(R.id.framelayout, fragment);
                    ftransaction.commit();
                } else{
                    Toast.makeText(getContext(), "You have not selected any option", Toast.LENGTH_SHORT).show();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fm=getActivity().getSupportFragmentManager();
                //fm.popBackStack();
                FragmentTransaction ftransaction = fm.beginTransaction();
                Fragment fragment = new Question7();
                fragment.setArguments(arguments);
                ftransaction.replace(R.id.framelayout, fragment);
                ftransaction.commit();
            }
        });
        return rootView;
    }

}
