package com.agileFTP;

import java.awt.print.Printable;
import java.io.OutputStream;
import java.io.PrintStream;

public class WorkingIndicator extends Thread {
    private String [] scenes =
            {
                    "          Working          ",
                    "         =Working=         ",
                    "        = Working =        ",
                    "       =  Working  =       ",
                    "      =   Working   =      ",
                    "     =    Working    =     ",
                    "    =     Working     =    ",
                    "   =      Working      =   ",
                    "  =       Working       =  ",
                    " =        Working        = ",
                    "=         Working         ="
            };
    private volatile Thread indicator;
    private int index = 0;

    public void run() {
        Thread thisThread = Thread.currentThread();
        indicator = Thread.currentThread();
        while(indicator == thisThread) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e){
                System.out.println("Done!");
            }
            index %= scenes.length;
            System.out.print(scenes[index++] + '\r');
        }
    }

    public void terminate() {
        indicator = null;
        Thread.currentThread().interrupt();
    }
}
