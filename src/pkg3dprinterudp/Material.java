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
public class Material {
    public static void main(String args[]) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9877);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        boolean exit = false;
        while(exit == false) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String fromPanel = new String( receivePacket.getData());
            System.out.println("RECEIVED: " + fromPanel);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String toPanel = fromPanel.toUpperCase();
            sendData = toPanel.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }
}

//    static int gruen = 10;
//    static int rot = 10;
//    static int gelb = 10;
//    
//    static Socket panelSocket = null;
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        try {
//            while (panelSocket == null) {
//                try {
//                    panelSocket = new Socket("localhost", 9999);
//                }
//                catch (IOException e) {
//                    System.out.println(e.getMessage() + '\n');
//                }
//            }
//            
//            boolean exit = false;
//            while (exit == false) {
//                exit = arbeiten();
//            }
//            panelSocket.close();
//        }
//        catch (IOException e) {
//            System.out.println(e.getMessage() + '\n');
//        }
//    }
//    
//    public static boolean arbeiten () {
//        try {
//            BufferedReader fromPanel = new BufferedReader(new InputStreamReader(panelSocket.getInputStream()));
//            DataOutputStream toPanel = new DataOutputStream(panelSocket.getOutputStream());
//            String buffer;
//            String send = "";
//            buffer = fromPanel.readLine();
////            System.out.println("Empfangen: " + buffer);
//            
//            if (buffer.equals("exit")) {
//                return true;
//            }
//            else if (buffer.equals("check")) {
//                send = gruen + "," + rot + "," + gelb;
//            }
//            else if (buffer.equals("gruen")) {
//                gruen--;
//                if (gruen == 0) {
//                    send = "leer";
//                }
//                else {
//                    send = "erfolgreich";
//                }
//            }
//            else if (buffer.equals("rot")) {
//                rot--;
//                if (rot == 0) {
//                    send = "leer";
//                }
//                else {
//                    send = "erfolgreich";
//                }
//            }
//            else if (buffer.equals("gelb")) {
//                gelb--;
//                if (gelb == 0) {
//                    send = "leer";
//                }
//                else {
//                    send = "erfolgreich";
//                }
//            }
//            else if (buffer.equals("gruen auffuellen")) {
//                gruen = 10;
//                send = "erfolgreich";
//            }
//            else if (buffer.equals("rot auffuellen")) {
//                rot = 10;
//                send = "erfolgreich";
//            }
//            else if (buffer.equals("gelb auffuellen")) {
//                gelb = 10;
//                send = "erfolgreich";
//            }
//            toPanel.writeBytes(send + '\n');
//            System.out.println("gruen:" + gruen + " rot: " + rot + " gelb: " + gelb);
//            
//            return false;
//        }
//        catch (IOException e) {
//            System.out.println(e.getMessage() + '\n');
//            return true;
//        }
//    }
//}