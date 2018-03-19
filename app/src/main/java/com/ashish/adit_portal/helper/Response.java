package com.ashish.adit_portal.helper;

import java.io.Serializable;

public class Response implements Serializable{
    private int id;
    private String response;
    public Response(int id,String response){
        this.id=id;
        this.response=response;
    }

    public int getid() {
        return id;
    }

    public String getResponse() {
        return response;
    }
}
