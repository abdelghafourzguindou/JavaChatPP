/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javachat;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 *
 * @author zGuindouOS
 */

public class client {

    public static void main(String[] args) throws UnknownHostException, IOException {

        Socket sock = new Socket("localhost", 8001);
        //try {

            //BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            //PrintWriter pw    = new PrintWriter(sock.getOutputStream());
            
            InputStream is          = sock.getInputStream(); 
            InputStreamReader ipsr  = new InputStreamReader(is);
            BufferedReader br       = new BufferedReader(ipsr);
            OutputStream os         = sock.getOutputStream();
            PrintWriter pw          = new PrintWriter(os,true);
           
            Thread Recepter = new Thread(new Runnable() {
                
                String msg;

                //@Override
                public void run() {
                    while (true) {
                        //do {
                            try {
                                msg = br.readLine();
                                if (msg != null) System.out.println(msg);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        //} while (msg != null);
                    }
                }
            });

            Thread Sender = new Thread(new Runnable() {
                //@Override
                public void run() {
                    while (true) {
                    	//System.out.print("You : ");
                        Scanner sc = new Scanner(System.in);
                        pw.println(sc.nextLine());
                    }
                }
            });

            Recepter.start();
            Sender.start();
        //} finally {
          //  sock.close();
        //}
    }

}
