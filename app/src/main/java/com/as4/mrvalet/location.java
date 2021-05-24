package com.as4.mrvalet;

import org.json.JSONArray;

public class location {

    private String Id ;
    private String Name ;
    private JSONArray Points;

    public void item_object()
    {

        Id="";
        Name="";

    }



    public void setName(String name)
    {
        Name = name;
    }
    public String getName( )
    {
        return Name ;
    }

    public void setId(String id)
    {
        Id = id;
    }
    public String getId( )
    {
        return Id ;
    }

    public void setPoints(JSONArray points)
    {
        Points = points;
    }
    public JSONArray getPoints( )
    {
        return Points ;
    }

}
