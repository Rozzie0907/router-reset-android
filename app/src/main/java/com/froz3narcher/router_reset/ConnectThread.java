package com.froz3narcher.router_reset;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by froz3narcher on 12/17/16.
 */

public class ConnectThread extends Thread
{
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mBTAdapter;
    private ConnectedThread mConnectedThread;
    private final Handler messageHandler;

    public ConnectThread(String address, Handler msgHandler)
    {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        messageHandler = msgHandler;
        mDevice = mBTAdapter.getRemoteDevice(address);

        BluetoothSocket tmp = null;

        try
        {
            tmp = mDevice.createRfcommSocketToServiceRecord(Constants.thisUUID);
            mBTAdapter.cancelDiscovery();
        } catch (IOException e)
        {
        }
        mSocket = tmp;
    }

    public void run()
    {
        try
        {
            mSocket.connect();
        } catch (IOException connectException)
        {
            try
            {
                mSocket.close();
            } catch (IOException closeException)
            {
            }
            return;
        }

        mConnectedThread = new ConnectedThread(mSocket, messageHandler);
        mConnectedThread.start();
    }

    public void cancel()
    {
        mConnectedThread.cancel();
        try
        {
            mSocket.close();
        } catch (IOException e)
        {
        }
    }

    public void sendData(byte[] data)
    {
        mConnectedThread.write(data);
    }

    public boolean isConnected()
    {
        if (mConnectedThread != null)
        {
            return mConnectedThread.isConnected();
        }
        else
        {
            return false;
        }
    }
}
