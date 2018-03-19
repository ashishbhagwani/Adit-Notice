package com.ashish.adit_portal.helper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ashish on 12/12/2016.
 */

public class FormResponse implements Serializable{
     private ArrayList<Response> responses;
     public FormResponse(){
         responses=new ArrayList<>();
         for(int i=0;i<=20;i++){
             responses.add(i,null);
         }
     }
     public void addresponse(int index,int id,String response){
        Response newresponse=new Response(id,response);
        responses.remove(index);
        responses.add(index,newresponse);
     }
     public int getResponseId(int index){
        return responses.get(index).getid();
    }
     public String getResponseText(int index){
        return responses.get(index).getResponse();
     }
     public  ArrayList<Response> getallresponses(){
        return responses;
    }
}
