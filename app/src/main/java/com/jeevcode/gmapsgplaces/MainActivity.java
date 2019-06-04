package com.jeevcode.gmapsgplaces;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";
    private static final int ERROR_DIALOGUE_REQUEST=9001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isServicesOK())
        {

            init();
        }
    }

    private void init(){

        Button btnMap=(Button)findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);

            }
        });



    }

    public boolean isServicesOK(){
        Log.d(TAG,"isServiceok:checking google services version!!");
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available== ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG,"isServicesOK: google play services is working");
            return true;

        }
        //else if the error is resolvable....
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServiceOK:an error occured but it is resolvable!!");
            Dialog dialogue=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOGUE_REQUEST);
            dialogue.show();
        }
        else {
            Toast.makeText(this,"We cant make map requests!",Toast.LENGTH_SHORT).show();
        }
            return false;

    }
}
