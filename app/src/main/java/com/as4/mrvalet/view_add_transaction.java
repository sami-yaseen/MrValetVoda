package com.as4.mrvalet;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener ;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class view_add_transaction extends view_main
{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_CAPTURE_F = 2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };
    ImageView mImageView  ;
    LinearLayout cardLayout ;
    LinearLayout compleatLayout ;
    LinearLayout detailsTLayout ;

    String SERIALNO = "";
    public String ISVIP = "0" ;
    RadioGroup vip ;
    String formattedDate ="";
    public String Image = "" ;
    public String Image2 = "" ;
    String Pointv ="";
    String empid ="";
    int Page=1 ;

    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    ProgressDialog dialog2 = null;
    String upLoadServerUri = null;
    /**********  File Path *************/
    String uploadFilePath = "";
    String uploadFileName = "";
    ImageView imageview ;
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.view_add_transaction);
        super.onCreate(savedInstanceState);

        verifyStoragePermissions(this) ;
        mImageView = (ImageView) findViewById(R.id.ticketImge);
        compleatLayout = (LinearLayout) findViewById(R.id.comp);
        cardLayout = (LinearLayout) findViewById(R.id.cardChecK);
        detailsTLayout = (LinearLayout) findViewById(R.id.detailsT);

        vip = (RadioGroup) findViewById(R.id.vip);
        upLoadServerUri = session.UploadToServer;
        Bundle extras = getIntent().getExtras();
        Pointv =(String)( extras != null ? extras.getString("point"): 0);
        empid =(String)( extras != null ? extras.getString("empid"): 0);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            ((TextView)findViewById(R.id.serla)).setText(
                    "UID: " +
                            ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
            SERIALNO = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID))  ;
        }
    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
    public void checkCard(View v) throws JSONException {

        final EditText card = (EditText) findViewById(R.id.card);
        if (SERIALNO.equals("")) {
            Toast.makeText(this, "Tag You Card First ", Toast.LENGTH_LONG).show();

            return;
        }
        if (!Validation.hasText(card)) {
            Toast.makeText(this, "Insert Card Number", Toast.LENGTH_LONG).show();

            return;
        }
        dialog = ProgressDialog.show(view_add_transaction.this, "", "Checking  Data...", true);

        detailsTLayout.setVisibility(View.GONE);
        cardLayout.setVisibility(View.VISIBLE);
        compleatLayout.setVisibility(View.GONE);
        Button result = (Button) findViewById(R.id.result);
        result.setText("Error");
        result.setBackgroundColor(Color.rgb(251,205,54));




        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        formattedDate = df.format(c.getTime());



        String parameter= "?type=checKcard&CARDNO="+card.getText().toString()+"&Date="+formattedDate+"&SERIALNO="+SERIALNO;
        String path = session.url + parameter.replace(" ", "%20");
        JSONArray jArr  =  getDataArray(path);
        boolean isAccept= false ;
        String CardData = "" ;
        String MSG = "Error" ;
        for (int i=0; i < jArr.length(); i++) {



            JSONObject obj = jArr.getJSONObject(i);
            JSONArray jArrD = obj.getJSONArray("details") ;
            isAccept=obj.getString("isTrue").equals("True") ;
            MSG =obj.getString("msg") ;
            CardData  =
                    " Number of Park : "+obj.getString("num")+" \n" ;
            if(jArrD.length() > 0 ) {
                JSONObject details = jArrD.getJSONObject(0);
                CardData  +=
                        " last Visit \n"+
                                " Date         : "+details.getString("TRANSACTION_DATE")+" \n"+
                                " Plate Number : "+details.getString("TRANSACTION_PLATENO")+" \n"+
                                " Ticket Number: "+details.getString("TRANSACTION_TICKETNO")+" \n"+
                                " Location     : "+details.getString("LOCATION_NAME")+" \n"+
                                " Point        : "+details.getString("POINT_NAME")+" \n"

                ;
            }



        }

        result.setText(MSG);
        dialog.dismiss();
        detailsTLayout.setVisibility(View.VISIBLE);
        cardLayout.setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(CardData);
        if(isAccept) {


            result.setBackgroundColor(Color.GREEN);

            compleatLayout.setVisibility(View.VISIBLE);
        }
    }
    public void addTransaction(View v)throws JSONException
    {

        if(vip.getCheckedRadioButtonId()==R.id.radioButton2)
            ISVIP = "1" ;

        final EditText card = (EditText) findViewById(R.id.card);
        final EditText plate = (EditText) findViewById(R.id.plate);
        final EditText ticket = (EditText) findViewById(R.id.ticket);
        if (Image.equals("")) {
            Toast.makeText(this, "Insert Back Image", Toast.LENGTH_LONG).show();

            return;
        }
        if (Image2.equals("")) {
            Toast.makeText(this, "Insert Front Image", Toast.LENGTH_LONG).show();

            return;
        }
        if (!Validation.hasText(card)) {
            Toast.makeText(this, "Insert Card Number", Toast.LENGTH_LONG).show();

            return;
        }
        if (!Validation.hasText(plate)) {
            Toast.makeText(this, "Insert Plate Number", Toast.LENGTH_LONG).show();

            return;
        }
  /*  if (!Validation.hasText(ticket)) {
        Toast.makeText(this, "Insert Ticket Number", Toast.LENGTH_LONG).show();

        return;
    }*/
        dialog = ProgressDialog.show(view_add_transaction.this, "", "Saving  Data...", true);
        compleatLayout.setVisibility(View.GONE);

        String parameter= "?type=TransactionSave&CARDNO="+card.getText().toString()+"&Date="+formattedDate+"&SERIALNO="+SERIALNO
                +"&Point="+Pointv
                +"&PLATENO="+plate.getText().toString()
                +"&TICKETNO="+ticket.getText().toString()
                +"&IMAGE="+Image +"&IMAGE2="+Image2
                +"&ISVIP="+ISVIP +"&empid="+empid;
        String path = session.url + parameter.replace(" ", "%20");
        JSONArray jArr  =  getDataArray(path);
        String message = "Error";
        String Id = "" ;
        for (int i=0; i < jArr.length(); i++) {




            JSONObject obj = jArr.getJSONObject(i);
            Id = obj.getString("id");
            message = obj.getString("message");
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        dialog.dismiss();
        if(Id.equals("1"))
        {
            Intent goTo = new Intent(this, MainActivity.class);

            startActivity(goTo);

        }
        else
            compleatLayout.setVisibility(View.VISIBLE);



    }
    public void addImage(View v)
    {
        dispatchTakePictureIntent();
    }
    public void addImage_f(View v)
    {
        dispatchTakePictureIntent_f();
    }
    private void dispatchTakePictureIntent_f() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_F);
            }
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        uploadFilePath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            galleryAddPic() ;
            setPic(1) ;
            Image =uploadFilePath + "" + uploadFileName;
            uploadeImage();
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE_F && resultCode == RESULT_OK) {
            galleryAddPic() ;
            setPic(2) ;
            Image2 =uploadFilePath + "" + uploadFileName;
            uploadeImage();
        }
    }
    public void uploadeImage() {
        // EditText imageEdit =(EditText)findViewById(R.id.image);
        //  imageEdit.setText(uploadFilePath + "" + uploadFileName);
        dialog = ProgressDialog.show(view_add_transaction.this, "", "Uploading file...", true);

        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //   messageText.setText("uploading started.....");
                    }
                });

                uploadFile(uploadFilePath + "" + uploadFileName);

            }
        }).start();

    }
    private void setPic(int indexV) {
        // Get the dimensions of the View
        int targetW = 500;
        int targetH = 850;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uploadFilePath + "" + uploadFileName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(uploadFilePath + "" + uploadFileName, bmOptions);
        mImageView = (ImageView) findViewById(R.id.ticketImge);
        if(indexV==2)
            mImageView = (ImageView) findViewById(R.id.ticketImge2) ;

        mImageView.setImageBitmap(bitmap);
    }
    private Bitmap getBitmap() {
        // Get the dimensions of the View
        int targetW = 500;
        int targetH = 850;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uploadFilePath + "" + uploadFileName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(uploadFilePath + "" + uploadFileName, bmOptions);
        return bitmap;
    }
    public int uploadFile(String sourceFileUri) {

        File sourceFile = new File(sourceFileUri);
        try {
            sourceFile =   getCompressed(this, uploadFilePath + "" + uploadFileName) ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"+uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    // messageText.setText("Source File not exist :"
                    //  +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        }
        else
        {

/// new code
            ApiInterface apiService;
            apiService = ApiClient.getClient().create(ApiInterface.class);


            File file = sourceFile;

            //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("uploaded_file", sourceFileUri, mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("multipart/form-data"), sourceFileUri);

            try {
                final Call<JsonObject> call = apiService.uploadImage2(fileToUpload, filename );

                call.enqueue(new APICallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject model) {
                        if (!call.isCanceled()) {
                        }
                        serverResponseCode =  200;
                        Log.d(TAG, "Filename " + model.toString());
                        runOnUiThread(new Runnable() {
                            public void run() {

                                String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                        +" http://www.androidexample.com/media/uploads/"
                                        +uploadFileName;

                                File fdelete = new File(uploadFilePath);
                                if (fdelete.exists()) {
                                    if (fdelete.delete()) {
                                        System.out.println("file Deleted :" + uploadFilePath);
                                    } else {
                                        System.out.println("file not Deleted :" + uploadFilePath);
                                    }
                                }
                                // messageText.setText(msg);
                                Toast.makeText( view_add_transaction.this, "File Upload Complete.",Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                        serverResponseCode =  code;
                        if (!call.isCanceled())
                            Log.d(TAG,msg);
                        dialog.dismiss();
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        Log.d(TAG, "Filename " + t.toString());
                        if (!call.isCanceled())
                            Log.d(TAG,"Something went wrong please try again later ");
                        dialog.dismiss();
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            /// End new code
            return serverResponseCode;

        } // End else block
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(uploadFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    public static File getCompressed(Context context, String path) throws IOException {

        if(context == null)
            throw new NullPointerException("Context must not be null.");
        //getting device external cache directory, might not be available on some devices,
        // so our code fall back to internal storage cache directory, which is always available but in smaller quantity
        File cacheDir = context.getExternalCacheDir();
        if(cacheDir == null)
            //fall back
            cacheDir = context.getCacheDir();

        String rootDir = cacheDir.getAbsolutePath() + "/ImageCompressor";
        File root = new File(rootDir);

        //Create ImageCompressor folder if it doesnt already exists.
        if(!root.exists())
            root.mkdirs();

        //decode and resize the original bitmap from @param path.
        Bitmap bitmap = decodeImageFromFiles(path, /* your desired width*/300, /*your desired height*/ 300);

        //create placeholder for the compressed image file
        File compressed = new File(root, SDF.format(new Date()) + ".jpg" /*Your desired format*/);

        //convert the decoded bitmap to stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        /*compress bitmap into byteArrayOutputStream
            Bitmap.compress(Format, Quality, OutputStream)
            Where Quality ranges from 1 - 100.
         */
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        /*
        Right now, we have our bitmap inside byteArrayOutputStream Object, all we need next is to write it to the compressed file we created earlier,
        java.io.FileOutputStream can help us do just That!
         */
        FileOutputStream fileOutputStream = new FileOutputStream(compressed);
        fileOutputStream.write(byteArrayOutputStream.toByteArray());
        fileOutputStream.flush();

        fileOutputStream.close();

        //File written, return to the caller. Done!
        return compressed;
    }
    public static Bitmap decodeImageFromFiles(String path, int width, int height) {
        BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
        scaleOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, scaleOptions);
        int scale = 1;
        while (scaleOptions.outWidth / scale / 2 >= width
                && scaleOptions.outHeight / scale / 2 >= height) {
            scale *= 2;
        }
        // decode with the sample size
        BitmapFactory.Options outOptions = new BitmapFactory.Options();
        outOptions.inSampleSize = scale;
        return BitmapFactory.decodeFile(path, outOptions);
    }
    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}
