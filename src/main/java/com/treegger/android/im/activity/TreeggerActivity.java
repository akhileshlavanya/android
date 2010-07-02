package com.treegger.android.im.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.treegger.android.im.service.TreeggerService;

public abstract class TreeggerActivity extends Activity {

    protected TreeggerService treeggerService = null;

    private ServiceConnection onService = new ServiceConnection()
    {
        public void onServiceConnected( ComponentName className, IBinder rawBinder )
        {
            treeggerService = ( (TreeggerService.LocalBinder) rawBinder ).getService();
            onTreeggerService();
        }

        public void onServiceDisconnected( ComponentName className )
        {
            treeggerService = null;
        }
    };
    
    public void onTreeggerService()
    {
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        public void onReceive( Context context, Intent intent )
        {
            if( TreeggerService.TREEGGER_BROADCAST_ACTION.equals( intent.getAction() ) )
            {
                int messageType = intent.getIntExtra( TreeggerService.EXTRA_MESSAGE_TYPE, -1 );
                onMessageType( messageType );
            }
        }
    };
    
    public void onMessageType( int messageType )
    {
        switch( messageType )
        {
            case TreeggerService.MESSAGE_TYPE_CONNECTING:
                showDialog(TreeggerService.MESSAGE_TYPE_CONNECTING);
                break;
            case TreeggerService.MESSAGE_TYPE_CONNECTING_FINISHED:
                removeDialog( TreeggerService.MESSAGE_TYPE_CONNECTING);
                break;
            case TreeggerService.MESSAGE_TYPE_AUTHENTICATING:
                showDialog(TreeggerService.MESSAGE_TYPE_AUTHENTICATING);
                break;
            case TreeggerService.MESSAGE_TYPE_AUTHENTICATING_FINISHED:
                removeDialog( TreeggerService.MESSAGE_TYPE_AUTHENTICATING);
                break;
        }
    }
    
   
    @Override
    public void onStart()
    {
        super.onStart();
    }
    
    
    @Override
    public void onResume()
    {
        super.onResume();
         
        registerReceiver( receiver, new IntentFilter( TreeggerService.TREEGGER_BROADCAST_ACTION ) );
    }

    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver( receiver );
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        bindService( new Intent( this, TreeggerService.class ), onService, BIND_AUTO_CREATE );
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unbindService( onService );
    }

    
    
    private ProgressDialog buildDialog( String title, String message )
    {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        return dialog;
        
    }

    @Override
    protected Dialog onCreateDialog( int dialogType ) 
    {
        switch( dialogType )
        {
            case TreeggerService.MESSAGE_TYPE_CONNECTING:
                return buildDialog( "Connecting", "Please wait...");
            case TreeggerService.MESSAGE_TYPE_AUTHENTICATING:
                return buildDialog( "Authentication", "Please wait...");
        }
        return null;
    }
    
    
}
