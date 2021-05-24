package com.as4.mrvalet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class view_transaction  extends view_main
{
        public String LocatioID ;
        List<location> LocatioList;

        public String pointID = "0";
        List<String> PointList;

        @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.view_transaction);
        super.onCreate(savedInstanceState);



            try {
                this.fillList2();
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
        private void fillList2() throws JSONException {



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

        public void addTransaction(View v)throws JSONException
    {

        this.fillList();

    }

    public void fillList() throws JSONException {


        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.list);
        View cell;
        TextView text;
        mainLayout.removeAllViews();
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        String formattedDate = df.format(c.getTime());
        String parameter= "?type=GetTRANSACTION&Date="+formattedDate+"&point="+pointID;
        String path = session.url + parameter.replace(" ", "%20");
        Log.e( "parameter2 : ", " " + path);
        JSONArray jArr  =  getDataArray(path);
        boolean isAccept= false ;
        String CardData = "" ;
        for (int i=0; i < jArr.length(); i++) {

            cell = getLayoutInflater().inflate(R.layout.trans_row, null);


            JSONObject obj = jArr.getJSONObject(i);

            CardData  = "********************************\n"
                      ;
           CardData  +=    " Date         : "+obj.getString("TRANSACTION_DATE")+" \n"+
                           " Employee     : "+obj.getString("TRANSACTION_EMP")+" \n"+
                           " Card UID     : "+obj.getString("TRANSACTION_SERIALNO")+" \n"+
                           " Card Number  : "+obj.getString("TRANSACTION_CARDNO")+" \n"+
                           " Plate Number : "+obj.getString("TRANSACTION_PLATENO")+" \n"+
                           " Ticket Number: "+obj.getString("TRANSACTION_TICKETNO")+" \n"+
                           " Location     : "+obj.getString("LOCATION_NAME")+"\n"+
                           " Point        : "+obj.getString("POINT_NAME")+" \n"


           ;


            TextView EName = (TextView) cell.findViewById(R.id.textView);
            EName.setText(CardData);
            mainLayout.addView(cell);


        }


    }

}
