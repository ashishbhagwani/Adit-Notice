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
 * A simple {@link Fragment} subclass.
 */
public class Question1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView=inflater.inflate(R.layout.fragment_question1, container, false);
        final Bundle arguments=getArguments();
        final FormResponse response=(FormResponse) arguments.get("object");
        final RadioGroup options=(RadioGroup)rootView.findViewById(R.id.options);
        TextView question=(TextView)rootView.findViewById(R.id.Question);
        TextView counter=(TextView)rootView.findViewById(R.id.questioncounter);
        RadioButton option1=(RadioButton)rootView.findViewById(R.id.firstradioButton);
        RadioButton option2=(RadioButton)rootView.findViewById(R.id.secondradioButton);
        RadioButton option3=(RadioButton)rootView.findViewById(R.id.thirdradioButton);
        Button next=(Button) rootView.findViewById(R.id.next);
        Button previous=(Button) rootView.findViewById(R.id.previous);

        previous.setVisibility(View.INVISIBLE);
        //Toast.makeText(getContext(),arguments.get("type").toString(), Toast.LENGTH_SHORT).show();
        //Log.e("inQuestion1",arguments.get("type").toString());
        if(arguments.get("type").toString().equals("theory")){
            counter.setText("1 out of 19 Questions");
            question.setText(R.string.question1);
            option1.setText(R.string.very_clearly);
            option2.setText(R.string.reasonably);
            option3.setText(R.string.poorly);
            if(response.getallresponses().get(1)!=null){
                options.check(response.getResponseId(1));
            }
        }else{
            counter.setText("1 out of 10 Questions");
            question.setText(R.string.pquestion1);
            option1.setText(R.string.always);
            option2.setText(R.string.often);
            option3.setText(R.string.rarley);
            if(response.getallresponses().get(1)!=null){
                options.check(response.getResponseId(1));
            }
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(options.getCheckedRadioButtonId()==-1)){
                    FragmentTransaction ftransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment fragment = new Question2();
                    fragment.setArguments(arguments);
                    int viewid=options.getCheckedRadioButtonId();
                    String selectedoption=((RadioButton)rootView.findViewById(viewid)).getText().toString();
                    response.addresponse(1, viewid, selectedoption);
                    ftransaction.replace(R.id.framelayout, fragment);
                    ftransaction.commit();
                } else{
                    Toast.makeText(getContext(), "You have not selected any option", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }
}
