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
    
    static int gruen = 10;
    static int rot = 10;
    static int gelb = 10;
    
    public static void main(String args[]) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9999);
        byte[] receiveData;
        byte[] sendData;
        boolean exit = false;
        String buffer;
        InetAddress IPAddress;
        int port;
        
        System.out.println("Materiallager gestartet ...");
        
        while(exit == false) {
            receiveData = new byte[1024];
            sendData = new byte[1024];
            
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            
            buffer = new String( receivePacket.getData());
            String [] parameter = buffer.split(",");
            /*
            [0] CurrentTimeMillis
            [1] Befehl
            [2] Farbe
            */
//            System.out.println("RECEIVED: ");
//            for (int tmp = 0; tmp < parameter.length; tmp++) {
//                System.out.println("[" + tmp + "]" + parameter[tmp]);
//            }
            
            if (parameter[1].equals("exit")) {
                exit = true;
            }
            else if (parameter[1].equals("refill")) {
                long ping = System.currentTimeMillis() - Long.parseLong(parameter[0]);
                System.out.println("Panel -> Lager: " + ping + "ms");
//                IPAddress = receivePacket.getAddress();
//                port = receivePacket.getPort();
                
                if (parameter[2].equals("gruen")) {
                    gruen = 10;
                }
                else if (parameter[2].equals("rot")) {
                    rot = 10;
                }
                else if (parameter[2].equals("gelb")) {
                    gelb = 10;
                }
                
                /*
                Antwort benoetigt?
                */
//                buffer = System.currentTimeMillis() + "," + "refilled" + "," + parameter[2] + ",";
//                sendData = buffer.getBytes();
//                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
//                serverSocket.send(sendPacket);
            }
            else if (parameter[1].equals("decrease")) {
                long ping = System.currentTimeMillis() - Long.parseLong(parameter[0]);
                System.out.println("Druckkopf -> Lager: " + ping + "ms");
                IPAddress = InetAddress.getByName("localhost");
                port = 9997;
                String tmp = "";
                
                if (parameter[2].equals("gruen")) {
                    gruen --;
                    if (gruen == 0) {
                        tmp = "empty" + "," + "gruen";
                    }
                    else {
                        tmp = "success";
                    }
                }
                else if (parameter[2].equals("rot")) {
                    rot --;
                    if (rot == 0) {
                        tmp = "empty" + "," + "rot";
                    }
                    else {
                        tmp = "success";
                    }
                }
                else if (parameter[2].equals("gelb")) {
                    gelb --;
                    if (gelb == 0) {
                        tmp = "empty" + "," + "gelb";
                    }
                    else {
                        tmp = "success";
                    }
                }
                
                buffer = System.currentTimeMillis() + "," + tmp + ",";
                sendData = buffer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
//                System.out.println("send: " + buffer);
            }
            else if (parameter[1].equals("check")) {
                long ping = System.currentTimeMillis() - Long.parseLong(parameter[0]);
                System.out.println("Panel -> Lager: " + ping + "ms");
                IPAddress = receivePacket.getAddress();
                port = receivePacket.getPort();
                buffer = System.currentTimeMillis() + "," + gruen + "," + rot + "," + gelb + ",";
                sendData = buffer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
//                System.out.println("send: " + buffer);
            }
        }
    }
}

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