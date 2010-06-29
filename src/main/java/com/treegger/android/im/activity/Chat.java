package com.treegger.android.im.activity;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.treegger.android.im.R;
import com.treegger.android.im.service.TreeggerService;

public class Chat
    extends TreeggerActivity
{

    public static final String TAG = "Chat";

    public static final String EXTRA_ROSTER_JID = "rosterJID";
    
    private String jid;
    
    private ArrayAdapter<String> chatMessageAdapter;
    
    
    @Override
    public void onMessageType( int messageType )
    {
        if ( messageType == TreeggerService.MESSAGE_TYPE_TEXTMESSAGE_UPDATE )
        {
            updateChatMessage();
        }
    }

    
    
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        Log.v( TAG, "Activity State: onCreate()" );
        super.onCreate( savedInstanceState );

        setContentView( R.layout.chat );

        jid = getIntent().getStringExtra( EXTRA_ROSTER_JID );
        
    }

    @Override
    public void onResume()
    {
        Log.v( TAG, "Activity State: onResume()" );
        super.onResume();
        if( treeggerService != null ) treeggerService.setVisibleChat( jid );
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if( treeggerService != null ) treeggerService.setVisibleChat( null );        
    }

    @Override
    public void onTreeggerService()
    {
        super.onTreeggerService();

        treeggerService.setVisibleChat( jid );
        updateChatMessage();
        
        Button button = (Button) findViewById( R.id.button_send );
        button.setOnClickListener( new OnClickListener()
        {
            public void onClick( View v )
            {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById( R.id.input_chat );
                String message = view.getText().toString();
                sendMessage( message );
                view.clearComposingText();
                view.setText( null );
            }
        } );
    }

    private void updateChatMessage()
    {
        ListView chatList = (ListView) findViewById( R.id.chat_list );
        List<String> textMessages = treeggerService.getTextMessageList( jid );
        treeggerService.markHasReadMessageFrom( jid );
        if( textMessages != null )
        {
            chatMessageAdapter = new ArrayAdapter<String>( this, R.layout.chatmessage, textMessages );
            chatList.setAdapter( chatMessageAdapter );
        }
    }
    
    private void sendMessage( String message )
    {
        // Check that there's actually something to send
        if ( message.length() > 0 )
        {
            treeggerService.sendTextMessage( jid, message );

        }
    }
}
