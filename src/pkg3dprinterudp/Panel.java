/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dprinterudp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;


/**
 *
 * @author debian
 */
public class Panel {
    static int ID = -1;
    static int gruenMat = 0;
    static int rotMat = 0;
    static int gelbMat = 0;
    static int gruenAuftrag = 0;
    static int rotAuftrag = 0;
    static int gelbAuftrag = 0;
    static Vector<Plan> Auftraege = new Vector<>();
    static Plan akt;
    static long startzeit;
    static Vector<Long> zeiten;
    static long ping;
    static Vector<Long> pings;
    static DruckkopfThread druckkopf;
    static MaterialThread material;
    
    public static void main(String args[]) throws Exception {
        material = new MaterialThread(9999, "localhost");
        material.start();
        druckkopf = new DruckkopfThread(9998, "localhost");
        druckkopf.start();
        
        zeiten = new Vector<> ();
        startzeit = 0;
        pings = new Vector<> ();
        ping = 0;
        
        setID();
        populatePlaene();
        
        while (waehlenPlan()) {
            countFarben();
            print();
            
            System.out.println("Laufzeit Befehle:");
            for (int tmp = 0; tmp < zeiten.size(); tmp++) {
                System.out.println("[" + tmp + "] " + zeiten.elementAt(tmp));
            }
            System.out.println("Ping Druckkopf:");
            for (int tmp = 0; tmp < pings.size(); tmp++) {
                System.out.println("[" + tmp + "] " + pings.elementAt(tmp));
            }
            zeiten.clear();
            pings.clear();
        }
        druckkopf.exit();
//        druckkopf.stop();
        material.exit();
//        material.stop();
    }
    
    public static void populatePlaene() {
        Auftraege.clear();
        Auftraege.add (new Plan ("schiefe Pyramide"));
        Auftraege.get(0).addBefehl (new Befehl (1, 0 ,1 , "gruen"));
        Auftraege.get(0).addBefehl (new Befehl (1, 0 ,2 , "gruen"));
        Auftraege.get(0).addBefehl (new Befehl (1, 0 ,3 , "gruen"));
        Auftraege.get(0).addBefehl (new Befehl (2, 0 ,1 , "gruen"));
        Auftraege.get(0).addBefehl (new Befehl (2, 0 ,2 , "gruen"));
        Auftraege.get(0).addBefehl (new Befehl (2, 0 ,3 , "gruen"));
        Auftraege.get(0).addBefehl (new Befehl (3, 0 ,1 , "gruen"));
        Auftraege.get(0).addBefehl (new Befehl (3, 0 ,2 , "gruen"));
        Auftraege.get(0).addBefehl (new Befehl (3, 0 ,3 , "gruen"));
        Auftraege.get(0).addBefehl (new Befehl (1, 1 ,1 , "gelb"));
        Auftraege.get(0).addBefehl (new Befehl (1, 1 ,2 , "gelb"));
        Auftraege.get(0).addBefehl (new Befehl (2, 1 ,1 , "gelb"));
        Auftraege.get(0).addBefehl (new Befehl (2, 1 ,2 , "gelb"));
        Auftraege.get(0).addBefehl (new Befehl (1, 2 ,1 , "rot"));
        
        Auftraege.add(new Plan ("Ecke"));
        Auftraege.get(1).addBefehl (new Befehl (1, 0, 1, "rot"));
        Auftraege.get(1).addBefehl (new Befehl (1, 0, 2, "gelb"));
        Auftraege.get(1).addBefehl (new Befehl (1, 0, 3, "gruen"));
        Auftraege.get(1).addBefehl (new Befehl (2, 0, 1, "gelb"));
        Auftraege.get(1).addBefehl (new Befehl (3, 0, 1, "gruen"));
        
        Auftraege.add(new Plan ("Schachbrett"));
        Auftraege.get(2).addBefehl (new Befehl (1, 0 ,1 , "gruen"));
        Auftraege.get(2).addBefehl (new Befehl (1, 0 ,2 , "rot"));
        Auftraege.get(2).addBefehl (new Befehl (1, 0 ,3 , "gruen"));
        Auftraege.get(2).addBefehl (new Befehl (2, 0 ,1 , "rot"));
        Auftraege.get(2).addBefehl (new Befehl (2, 0 ,2 , "gruen"));
        Auftraege.get(2).addBefehl (new Befehl (2, 0 ,3 , "rot"));
        Auftraege.get(2).addBefehl (new Befehl (3, 0 ,1 , "gruen"));
        Auftraege.get(2).addBefehl (new Befehl (3, 0 ,2 , "rot"));
        Auftraege.get(2).addBefehl (new Befehl (3, 0 ,3 , "gruen"));
    }
    
    public static void setID() {
        try {
            System.out.println("Bitte geben Sie die ID des Druckers ein.");
            BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            String s = reader.readLine();
            ID = Integer.parseInt(s);
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + '\n');
        }
    }
    
    public static void infoAnzeige() {
        String auftrag = "";
        if (akt != null) {
            auftrag = akt.getName();
        }
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("Gruen:\t" + gruenMat + " Einheiten\t\tDrucker-ID: " + ID);
        System.out.println("Rot:\t" + rotMat + " Einheiten\t\tAuftrag:" + auftrag);
        System.out.println("Gelb:\t" + gelbMat + " Einheiten\t\t");
    }
    
    public static boolean waehlenPlan () {
        int index = -1;
        System.out.println("Bitte waehlen Sie einen Plan.");
        for (int tmp = 0; tmp < Auftraege.size(); tmp++) {
            System.out.println("[" + tmp + "] " + Auftraege.get(tmp).getName());
        }
        System.out.println("[exit] Beenden");
        try {
            BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            while (index < 0 || index > Auftraege.size()) {
                String s = reader.readLine();
                if (s.equals("exit")) {
                    return false;
                }
                index = Integer.parseInt(s);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + '\n');
        }
        
        akt = Auftraege.get(index);
        return true;
    }
    
    public static void userError (String error, String loesung) {
        try {
            System.out.println("\n\tERROR:\t" + error);
            System.out.println("\t" + loesung);
            
            BufferedReader reader = new BufferedReader (new InputStreamReader (System.in));
            String input = reader.readLine();
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + '\n');
        }
    }
    
    public static void print () {
        String rueckMat = material.checkMat();
        while (!(rueckMat.equals("OK"))) {
            userError("Die Patrone " + rueckMat + " reicht fuer den Druckauftrag nicht aus.", "Druecken Sie ENTER um einen Patronenwechsel zu simulieren");
            material.sendRefill(rueckMat);
            rueckMat = material.checkMat();
        }
        for (int tmp = 0; tmp < akt.getSize(); tmp++) {
            startzeit = System.currentTimeMillis();
            if (!(sendPrint(akt.getBefehl(tmp)))) {
                //Fehler beim Druck, daher muss der Befehl wiederholt werden
                tmp--;
            }
            zeiten.add(System.currentTimeMillis() - startzeit);
        }
        akt = null;
        infoAnzeige();
    }
    
    public static boolean sendPrint (Befehl tmp) {
        String befehl = tmp.getX() + "," + tmp.getY() + "," + tmp.getZ() + "," + tmp.getFarbe() + ',';
        int rueckPrint = druckkopf.sendBefehl(befehl);
        
        switch (rueckPrint) {
            case 0:
//                System.out.println("erfolgreich gedruckt");
                break;
            case -1:
                userError("Der Druckkopf ist verschmutzt.", "Druecken Sie ENTER um eine Reinigung zu simulieren.");
                return false;
            default:
                break;
        }
        material.getMat();
        infoAnzeige();
        return true;
    }
    
    public static void countFarben() {
        gruenAuftrag = 0;
        rotAuftrag = 0;
        gelbAuftrag = 0;
        
        for (int tmp = 0; tmp < akt.getSize(); tmp++) {
            if (akt.getBefehl(tmp).getFarbe().equals("gruen")) {
                gruenAuftrag++;
            }
            if (akt.getBefehl(tmp).getFarbe().equals("rot")) {
                rotAuftrag++;
            }
            if (akt.getBefehl(tmp).getFarbe().equals("gelb")) {
                gelbAuftrag++;
            }
        }
    }
    
    private static class DruckkopfThread extends Thread {
        DatagramSocket printSocket;
        DatagramSocket serverSocket;
        int port = 0;
        InetAddress IPAddress;
        byte[] sendData;
        byte[] receiveData;
        String buffer;
        
        public DruckkopfThread (int _port, String _IPAddress) {
            try {
                this.IPAddress = InetAddress.getByName(_IPAddress);
                this.port = _port;
                this.serverSocket = new DatagramSocket(9997);
                printSocket = new DatagramSocket();
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
            }
        }
        
        public int sendBefehl (String Befehl) {
            try {
                receiveData = new byte[1024];
                sendData = new byte[1024];
                
                ping = System.currentTimeMillis();
                buffer = ping + "," + Befehl;
                sendData = buffer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                printSocket.send(sendPacket);
                
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String fromMaterial = new String( receivePacket.getData());
                String [] parameter = fromMaterial.split(",");
                
//                System.out.println("RECEIVED: ");
//                for (int tmp = 0; tmp < parameter.length; tmp++) {
//                    System.out.println("[" + tmp + "]" + parameter[tmp]);
//                }
                
                if (parameter[1].equals("success")) {
                    return 0;
                }
                else { //parameter[1].equals("error")
                    return -1;
                }
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
                return -2;
            }
        }
        
        public void exit () {
            try {
                sendData = new byte[1024];
                
                buffer = System.currentTimeMillis() + "," + "exit" + ",";
                sendData = buffer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                printSocket.send(sendPacket);
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
            }
        }
    }
    
    private static class MaterialThread extends Thread {
        DatagramSocket printSocket;
        int port = 0;
        InetAddress IPAddress;
        byte[] sendData;
        byte[] receiveData;
        String buffer;
        
        public MaterialThread (int _port, String _IPAddress) {
            try {
                this.IPAddress = InetAddress.getByName(_IPAddress);
                this.port = _port;
                printSocket = new DatagramSocket();
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
            }
        }
        
        public int sendRefill (String farbe) {
            try {
                sendData = new byte[1024];
                
                buffer = System.currentTimeMillis() + "," + "refill" + "," + farbe + ",";
                sendData = buffer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                printSocket.send(sendPacket);
                
                /*
                Antwort einarbeiten
                
                String antwort = fromMat.readLine();
                if (antwort.equals("erfolgreich")) {
                    return 0;
                }
                */
                return 1;
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
                return -1;
            }
        }
        
        public String checkMat () {
            try {
                sendData = new byte[1024];
                receiveData = new byte[1024];
                
                buffer = System.currentTimeMillis() + "," + "check" + ",";
                sendData = buffer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                printSocket.send(sendPacket);
                
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                printSocket.receive(receivePacket);
                String fromMaterial = new String( receivePacket.getData());
                String [] parameter = fromMaterial.split(",");
                
//                System.out.println("RECEIVED: ");
//                for (int tmp = 0; tmp < parameter.length; tmp++) {
//                    System.out.println("[" + tmp + "]" + parameter[tmp]);
//                }
                
                String fuellen = "OK";
                /*
                **[0] CurrentTimeMillis
                **[1] gruen
                **[2] rot
                **[3] gelb
                */
                gruenMat = Integer.parseInt(parameter[1]);
                rotMat = Integer.parseInt(parameter[2]);
                gelbMat = Integer.parseInt(parameter[3]);
                
                if (gruenMat < gruenAuftrag) {
                    fuellen = "gruen";
                }
                else if (rotMat < rotAuftrag) {
                    fuellen = "rot";
                }
                else if (gelbMat < gelbAuftrag) {
                    fuellen = "gelb";
                }
                return fuellen;
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
                return "error";
            }
        }
        
        public void getMat () {
            try {
                sendData = new byte[1024];
                receiveData = new byte[1024];
                
                buffer = System.currentTimeMillis() + "," + "check" + ",";
                sendData = buffer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                printSocket.send(sendPacket);
                
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                printSocket.receive(receivePacket);
                String fromMaterial = new String( receivePacket.getData());
                String [] parameters = fromMaterial.split(",");
                /*
                **[0] CurrentTimeMillis
                **[1] gruen
                **[2] rot
                **[3] gelb
                */
                gruenMat = Integer.parseInt(parameters[1]);
                rotMat = Integer.parseInt(parameters[2]);
                gelbMat = Integer.parseInt(parameters[3]);
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
            }
        }
        
        public void exit () {
            try {
                sendData = new byte[1024];
                
                buffer = System.currentTimeMillis() + "," + "exit" + ",";
                sendData = buffer.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                printSocket.send(sendPacket);
            }
            catch (IOException e) {
                System.out.println(e.getMessage() + '\n');
            }
        }
    }
}