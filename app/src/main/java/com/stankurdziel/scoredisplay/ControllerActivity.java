package com.stankurdziel.scoredisplay;

import android.app.Activity;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class ControllerActivity extends Activity {

    private static final int PORT = 33333;
    private NsdManager.RegistrationListener registrationListener;
    private String serviceName;
    private SocketServerThread serverThread;
    private TextView ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller);
        findViewById(R.id.connect_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerService();
            }
        });
        findViewById(R.id.start_server_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServer();
            }
        });
        ipAddress = (TextView) findViewById(R.id.ip_address);
    }

    private void startServer() {
        if (serverThread == null) {
            serverThread = new SocketServerThread();
            serverThread.start();
        }
    }

    private class SocketServerThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT);

                final String address = getIpAddress();
                ControllerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ipAddress.setText(address);
                    }
                });

                while (true) {
                    Socket socket = serverSocket.accept();
                    count++;
                    SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getIpAddress() {
        String ip = "unknown";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ip;
    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int connectionNum;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            connectionNum = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                BufferedReader in = new BufferedReader(new InputStreamReader(hostThreadSocket.getInputStream()));

                while (true) {
                    printStream.println("Ready for request: ");

                    String request = "";
                    while (request.isEmpty()) {
                        request = in.readLine();
                    }

                    final Intent intent = new Intent("incoming-request");
                    intent.putExtra("request", request);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    Thread.sleep(100);
                    printStream.println(ScoreApplication.getInstance(ControllerActivity.this).getScore());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerService() {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("ScoreDisplay");
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(5555);
    }

    public void initializeRegistrationListener() {
        registrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                serviceName = NsdServiceInfo.getServiceName();

                Log.d("SEK", "service registered: " + serviceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
            }
        };
    }
}
