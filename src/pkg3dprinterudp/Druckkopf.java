/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dprinterudp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author debian
 */
public class Druckkopf {
    public static void main(String args[]) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9998);
        byte[] receiveData;
        byte[] sendData;
        boolean exit = false;
        String buffer;
        InetAddress IPAddress;
        int port;
        
        System.out.println("Druckkopf gestartet ...");
        
        while(exit == false) {
            receiveData = new byte[1024];
            sendData = new byte[1024];
            
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            
            buffer = new String( receivePacket.getData());
            String [] parameter = buffer.split(",");
            /*
            [0] CurrentTimeMillis
            [1] X-Koordinate / exit
            [2] Y-Koordinate
            [3] Z-Koordinate
            [4] Farbe
            */
//            System.out.println("RECEIVED: ");
//            for (int tmp = 0; tmp < parameter.length; tmp++) {
//                System.out.println("[" + tmp + "]" + parameter[tmp]);
//            }
            
            if (parameter[1].equals("exit")) {
                exit = true;
            }
            else {
                long ping = System.currentTimeMillis() - Long.parseLong(parameter[0]);
                System.out.println("Panel -> Druckkopf: " + ping + "ms");
                
                int random = (int)(Math.random()*10);
                //verstopfter Druckkopf
                if (random == 5) {
                    System.out.println("error");
                    IPAddress = InetAddress.getByName("localhost");
                    port = 9997;
                    buffer = System.currentTimeMillis() + "," + "error" + ",";
                    sendData = buffer.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
//                    System.out.println("send: " + buffer + "\n");
                }
                else {
                    Thread.sleep(1000);
                    System.out.println("success");
                    IPAddress = InetAddress.getByName("localhost");
                    port = 9999;
                    buffer = System.currentTimeMillis() + "," + "decrease" + "," + parameter[4] + ",";
                    sendData = buffer.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                    serverSocket.send(sendPacket);
//                    System.out.println("send: " + buffer + "\n");
                }
            }
        }
    }
}