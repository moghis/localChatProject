package com.moghis;


import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Semaphore;

class PersonProcess extends Thread {

    private JTextArea output;
    private JTextField input;
    private String ENTER = "Enter";
    private String personText = "";
    private static StringBuffer buffer = new StringBuffer();
    private volatile boolean running = true;

    private Semaphore sem;
    private String name;
    PersonProcess(Semaphore sem, String name)
    {
        super(name);
        this.sem = sem;
        this.name = name;
    }

    void createUI() {
        JFrame frame = new JFrame("chat room : " + name);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        output = new JTextArea(30, 50);
        output.setWrapStyleWord(true);
        output.setEditable(false);
        JButton enterButton = new JButton("Send");
        enterButton.setActionCommand(ENTER);
        ButtonListener buttonListener = new ButtonListener();
        enterButton.addActionListener(buttonListener);
        input = new JTextField(20);
        input.setActionCommand(ENTER);
        input.addActionListener(buttonListener);
        DefaultCaret caret = (DefaultCaret) output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scroller = new JScrollPane(output);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scroller);
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        inputpanel.add(input);
        inputpanel.add(enterButton);
        panel.add(inputpanel);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.addWindowListener(new closeListener());
        input.requestFocus();
    }

    public class ButtonListener implements ActionListener {
        public void actionPerformed(final ActionEvent ev)
        {
            if (!input.getText().trim().equals(""))
            {
                String cmd = ev.getActionCommand();
                if (ENTER.equals(cmd)) {
                    personText += name +" : " + input.getText()+"\n";
                }
            }
            input.setText("");
            input.requestFocus();
        }
    }

    public class closeListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            try {
                sem.acquire();
                output.setText(buffer.toString());
                String left = String.format("%80s left\n",name);
                output.append(left);
                buffer.append(left);
                sem.release();
                running = false;
                join();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            sem.acquire();
            output.setText(buffer.toString());
            String join = String.format("%78s joined\n",name);
            output.append(join);
            buffer.append(join);
            sem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(running){
            try {
                sem.acquire();
                output.setText(buffer.toString());
                output.append(personText);
                buffer.append(personText);
                personText = "";
                Thread.sleep(10);
                sem.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }

}