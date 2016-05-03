/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3dprinterudp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author debian
 */
public class Druckkopf {

  static String line;
  static String buffer;
  static BufferedReader fromPanel;
  static DataOutputStream toPanel;
  static Socket panelSocket;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        panelSocket = null;
        try {
            while (panelSocket == null) {
                try {
                    panelSocket = new Socket("localhost", 9998);
                }
                catch (IOException e) {
                    System.out.println(e.getMessage() + '\n');
                }
            }
            
            boolean exit = false;
            
            while (exit == false) {
                exit = warten();
            }
            panelSocket.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + '\n');
        }
    }
    
    public static boolean warten () {
        try {
        String buffer;
        BufferedReader fromPanel = new BufferedReader(new InputStreamReader(panelSocket.getInputStream()));
        DataOutputStream toPanel = new DataOutputStream(panelSocket.getOutputStream());
        buffer = fromPanel.readLine();
        
        /*
        **[0] X-Koordinat
        **[1] Y-Koordinate
        **[2] Z-Koordinate
        **[3] Farbe
        */
        String [] parameter = buffer.split(",");
        int x = Integer.parseInt(parameter[0]);
        int y = Integer.parseInt(parameter[1]);
        int z = Integer.parseInt(parameter[2]);
        String farbe = parameter[3];
        
        if (farbe.equals("exit")) {
            return true;
        }
        
        toPanel.writeBytes(String.valueOf(print(x, y, z, farbe)) + '\n');
        
        }
        catch (IOException e) {
            System.out.println(e.getMessage() + '\n');
        }
        return false;
    }
    public static int print(int x, int y, int z, String farbe) {
        try {
//            Zufallszahl fuer Probleme ... Wahrscheinlichkeit 1/10
            int random = (int)(Math.random()*10);
            if (random == 5) {
                return -1;
            }
        
            //1000ms warten, da druck nicht instant ist
            Thread.sleep(1000);
            System.out.println(farbe + " an " + x + "/" + y + "/" + z + " gedruckt");
            //Druck erfolgreich
            return 0;
        }
        catch (InterruptedException e) {
            System.out.println(e);
            return -2;
        }
    }
}