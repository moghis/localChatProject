package com.moghis;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {
        Semaphore sem = new Semaphore(1,true);
        Scanner inputName = new Scanner(System.in);
        String name;
        while(true) {

            System.out.println("write your name :");
            name = inputName.nextLine();

            if (name.equals("0"))
                break;

            PersonProcess process = new PersonProcess(sem,name);
            process.createUI();
            process.start();
        }
    }
}
