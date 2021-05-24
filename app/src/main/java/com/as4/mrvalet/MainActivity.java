package com.as4.mrvalet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.nfc.NfcAdapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends view_main
{
    public String LocatioID ;
    List<location> LocatioList;

    public String pointID = "0";
    List<String> PointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        final EditText empT = (EditText) findViewById(R.id.empid);
        empT.setText(session.EMPNum);
        try {
            this.fillList();
        } catch (JSONException e) {
            e.printStackTrace();

        }


        Spinner spLocation = (Spinner) findViewById(R.id.location);
        spLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {     public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {

            LocatioID = LocatioList.get(pos).getId();
            session.locationSE =  LocatioID;
            try {
                fillPoint(LocatioList.get(pos).getPoints()) ;
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
            public void onNothingSelected(AdapterView<?> parent) {     } });

        Spinner spPoint = (Spinner) findViewById(R.id.Point);
        spPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {     public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {

            pointID = PointList.get(pos);

            session.pointSe =  pointID;

        }
            public void onNothingSelected(AdapterView<?> parent) {     } });
    }

    private void fillList() throws JSONException {



        LocatioList = new ArrayList<location>();
        String parameter= "?type=GetLocation";
        String path = session.url+parameter;
        JSONArray jArr =  getDataArray(path);


        Spinner spinner = (Spinner) findViewById(R.id.location);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        int Pos =  0 ;
        for (int i=0; i < jArr.length(); i++) {

            JSONObject obj = jArr.getJSONObject(i);
            JSONArray points = obj.getJSONArray("points") ;

            location temp = new location() ;
            temp.setName( obj.getString("LOCATION_NAME"));
            temp.setId(obj.getString("LOCATION_PMK_ID"));
            temp.setPoints(obj.getJSONArray("points"));
            LocatioList.add(temp) ;
if(session.locationSE.equals(obj.getString("LOCATION_PMK_ID")))
{
    Pos= i ;
}
            adapter.add(  obj.getString("LOCATION_NAME") );
        }

spinner.setSelection(Pos);

    }



    private void fillPoint(JSONArray jArr )  throws JSONException  {



        PointList = new ArrayList<String>();

        Spinner spinner = (Spinner) findViewById(R.id.Point );

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        adapter.add( "select");
        PointList.add("0") ;
        int Pos =  0 ;

        for (int i=0; i < jArr.length(); i++) {

            JSONObject obj = jArr.getJSONObject(i);

            PointList.add(obj.getString("POINT_PMK_ID")) ;

            adapter.add(  obj.getString("POINT_NAME") );
            if(session.pointSe.equals(obj.getString("POINT_PMK_ID")))
            {
                Pos= i ;
            }
        }
        spinner.setSelection(Pos+1);


    }

    public void addTransaction(View v)
    {



        final EditText empT = (EditText) findViewById(R.id.empid);

        if (!Validation.hasText(empT)) {
            Toast.makeText(this, "Insert Employee Number", Toast.LENGTH_LONG).show();

            return;
        }
        session.EMPNum =  empT.getText().toString();
        if(!pointID.equals("0"))
        {
            Intent goTo = new Intent(this, view_add_transaction.class);
            goTo.putExtra("point", pointID);
            goTo.putExtra("empid", empT.getText().toString());
            startActivity(goTo);
        }
        else
        {
            Toast.makeText(this, "Select Point", Toast.LENGTH_LONG).show();
        }


    }






}
