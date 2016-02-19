package com.nero.videoshuffle.util;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by nlang on 2/19/2016.
 */
public class MyProxy extends Thread {
    final String TAG = this.getClass().getSimpleName();
    SocketChannel socketChannel;
    public final static int PORT = 60001;


    @Override
    public void run() {

        try {

            // socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 60001));
            ServerSocket server = new ServerSocket(PORT);
            Socket socket = server.accept();
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            while (inputStream.read(buffer) > 0) {
                String request = new String(buffer);
                if (request.startsWith(GET)) {
                    OutputStream outputStream = socket.getOutputStream();
                    get(request, outputStream);
                    outputStream.close();
                }
                Log.i(TAG, request);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final String GET = "GET";

    private boolean get(String request, OutputStream outputStream) {
        boolean result = false;
        int startIndex = request.indexOf(GET) + GET.length();
        int endIndex = request.indexOf("\r\n");
        String host = request.substring(startIndex, endIndex).trim().split(" ")[0];
        Log.i(TAG, host);
        try {
            URL url = new URL(host);
            URLConnection con = url.openConnection();
            InputStream inputStream = con.getInputStream();
            byte[] buffer = new byte[1024];
            while (inputStream.read(buffer) > 0) {
                outputStream.write(buffer);
                Arrays.fill(buffer, (byte) 0);
            }
            outputStream.flush();
            inputStream.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
