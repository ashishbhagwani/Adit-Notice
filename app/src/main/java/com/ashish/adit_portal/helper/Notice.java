package com.ashish.adit_portal.helper;

import android.view.View;

/**
 * Created by Ashish on 9/29/2016.
 */

public class Notice {

   public Notice(){
        visibilitydownload= View.VISIBLE;
        visibilityprogress=View.INVISIBLE;
    }
    private String Url;

    private String Title;
    private int visibilitydownload;
    private int visibilityprogress;
    public void setTitle(String title) {
        this.Title = title;
    }
    public void setVisibilitydownload(int visibilitydownload){
        this.visibilitydownload=visibilitydownload;
    }
    public void setVisibilityprogress(int visibilityprogress){
        this.visibilityprogress=visibilityprogress;
    }
    public void setUrl(String url) {
        this.Url = url;
    }
    public int getvisibilitydownload(){
        return visibilitydownload;
    }
    public int getVisibilityprogress(){
        return visibilityprogress;
    }
    public String getUrl(){
        return Url;
    }
    public String getTitle(){
        return Title;
    }
}
