package com.vint.pdf2ppt.ui;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String args[]) throws FileNotFoundException, IOException {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UserInterface window = new UserInterface();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
