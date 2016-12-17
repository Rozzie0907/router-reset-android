package com.froz3narcher.router_reset;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    private BluetoothAdapter mBTAdapter;
    private Button enableButton;
    private Button resetButton;

    private boolean connected = false;

    ConnectThread mConnectThread = null;
    Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableButton = (Button) findViewById(R.id.button1);
        resetButton = (Button) findViewById(R.id.button2);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBTAdapter != null)
        {
            if (!mBTAdapter.isEnabled())
            {
                enableButton.setText(getText(R.string.buttonEnableText));
            } else
            {
                enableButton.setText(getText(R.string.buttonConnectText));
            }
        } else
        {
            // without Bluetooth, this app won't work
            enableButton.setEnabled(false);
            resetButton.setEnabled(false);
        }

        enableButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!mBTAdapter.isEnabled())
                {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
                }
                else if (!connected)
                {
                    Intent connectIntent = new Intent(MainActivity.this, select_bluetooth.class);
                    startActivityForResult(connectIntent, Constants.REQUEST_CONNECT);
                }
                else if (connected)
                {
                    mConnectThread.cancel();
                    mConnectThread = null;
                    connected = false;
                    enableButton.setText(getText(R.string.buttonConnectText));
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if ((connected) && (mConnectThread != null))
                {
                    if (mConnectThread.isConnected())
                    {
                        // send a "1" to the bluetooth device
                        String message = "1";
                        mConnectThread.sendData(message.getBytes());
                    }
                    else
                    {
                        connected = false;
                        enableButton.setText(getText(R.string.buttonConnectText));
                    }
                }
            }
        });


        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                byte[] writeBuf = (byte[]) msg.obj;
                int begin = (int) msg.arg1;
                int end = (int) msg.arg2;
                switch (msg.what)
                {
                    case Constants.MESSAGE_READ:
                        String writeMessage = new String(writeBuf);
                        writeMessage = writeMessage.substring(begin, end);
                        TextView display = (TextView) findViewById(R.id.statusView);
                        display.setText(writeMessage);
                        break;
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case Constants.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK)
                {
                    if (mBTAdapter.isEnabled())
                    {
                        enableButton.setText(getText(R.string.buttonConnectText));
                    }
                }
                break;

            case Constants.REQUEST_CONNECT:
                if (resultCode == Activity.RESULT_OK)
                {
                    connected = true;

                    enableButton.setText(getText(R.string.buttonDisconnectText));

                    String message = data.getStringExtra(Constants.DEVICE_RESULT);

                    // Get the last 17 characters, which are the MAC Address of the chosen
                    // Bluetooth device
                    String address =
                            message.substring(message.length() - Constants.MAC_ADDRESS_SIZE);

                    mConnectThread = new ConnectThread(address, mHandler);
                    mConnectThread.start();

                    TextView display = (TextView) findViewById(R.id.statusView);
                    display.setText(R.string.statusConnected);
                }
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mConnectThread != null)
        {
            mConnectThread.cancel();
        }
        super.onDestroy();
    }
}