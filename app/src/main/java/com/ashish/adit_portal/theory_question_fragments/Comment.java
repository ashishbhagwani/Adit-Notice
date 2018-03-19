package com.ashish.adit_portal.theory_question_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ashish.adit_portal.Fragments.FormSubmission;
import com.ashish.adit_portal.R;
import com.ashish.adit_portal.helper.FormResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class Comment extends Fragment {


    public Comment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_comment, container, false);
        final Bundle argument=getArguments();
        final FormResponse response=(FormResponse) argument.get("object");
        final EditText commentbox= (EditText) rootView.findViewById(R.id.commentbox);
        Button checkresponses= (Button) rootView.findViewById(R.id.checkresponsesbutton);
        Button previous= (Button) rootView.findViewById(R.id.previousbutton);
        if (response.getallresponses().get(20) != null) {
            commentbox.setText(response.getResponseText(20));
        }
        checkresponses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment=commentbox.getText().toString().trim();
                if(comment.isEmpty() || comment.length() == 0 || comment.equals("") || comment == null)
                {
                    response.addresponse(20,R.id.commentbox,"");
                    //EditText is empty
                }
                else
                {
                    response.addresponse(20,R.id.commentbox, comment);
                    //EditText is not empty
                }

                FragmentManager fm=getActivity().getSupportFragmentManager();
                //fm.popBackStack();
                FragmentTransaction ftransaction = fm.beginTransaction();
                Fragment fragment=new FormSubmission();
                fragment.setArguments(argument);
                ftransaction.replace(R.id.framelayout,fragment);
                ftransaction.commit();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm=getActivity().getSupportFragmentManager();
                FragmentTransaction ftransaction = fm.beginTransaction();
                Fragment fragment;
                if(argument.getString("type").equals("theory")) {
                    fragment = new Question19();
                }else{
                    fragment=new Question10();
                }
                fragment.setArguments(argument);
                ftransaction.replace(R.id.framelayout, fragment);
                ftransaction.commit();
            }
        });
        return rootView;
    }

}
