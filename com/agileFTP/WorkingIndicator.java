package com.agileFTP;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

import java.awt.print.Printable;
import java.io.OutputStream;
import java.io.PrintStream;

public class WorkingIndicator extends Thread {
    private FTPClient ftp;
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
    private String [] miniScenes =
            {
                    "-", "\\", "|", "/"

            };
    private volatile Thread indicator;
    private int percentage = 0;
    private int index = 0;

    public WorkingIndicator(){}

    public WorkingIndicator(FTPClient ftp, long fileSize) {
        this.ftp = ftp;
        if(fileSize == 0) working();
        CopyStreamAdapter streamListener = new CopyStreamAdapter() {
            @Override
            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                percentage = (int)(totalBytesTransferred * 100 / fileSize);
            }
        };
        this.ftp.setCopyStreamListener(streamListener);
    }

    public void run() {
        if(ftp != null) {
            percentage();
        } else {
            working();
        }
    }

    public void percentage() {
        Thread thisThread = Thread.currentThread();
        indicator = Thread.currentThread();
        while(indicator == thisThread) {
            index %= miniScenes.length;
            System.out.print("Percentage: " + percentage + '%' + "   " + miniScenes[index++] + "   " + '\r');
            try {
                Thread.sleep(300);
            } catch(InterruptedException e) {
                return;
            }
        }
    }

    //functionally changed to be a test for a bad file
    //working() gets called if file size is 0 or if ftp is null
    public void working() {
        System.out.println("Not a valid file to download.");
    }


    public void terminate() {
        indicator = null;
        Thread.currentThread().interrupt();
    }
}
