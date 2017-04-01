package interfaces;

import javax.swing.*;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;

public class GUI implements Runnable{

    private JFrame Windows;
    private JTextArea outputAreaSender;
    private JTextArea outputAreaReceiverMC;
    private JTextArea outputAreaReceiverMDB;
    private JTextArea outputAreaReceiverMDR;
    private JScrollPane scrollPaneSender;
    private JScrollPane scrollPaneReceiverMC;
    private JScrollPane scrollPaneReceiverMDB;
    private JScrollPane scrollPaneReceiverMDR;

    public GUI() {
    	
        this.outputAreaSender = new JTextArea(12, 100);
        this.outputAreaSender.setEditable(false);
        
        this.outputAreaReceiverMC = new JTextArea(12, 100);
        this.outputAreaReceiverMC.setEditable(false);
        
        this.outputAreaReceiverMDB = new JTextArea(12, 100);
        this.outputAreaReceiverMDB.setEditable(false);
        
        this.outputAreaReceiverMDR = new JTextArea(12, 100);
        this.outputAreaReceiverMDR.setEditable(false);
    }

    @Override
    public void run() {
        Windows = new JFrame();
        Windows.setAlwaysOnTop(false);
		Windows.setIconImage(Toolkit.getDefaultToolkit().getImage("..\\Upload-to_Cloud-512.png"));
		Windows.setResizable(false);
		Windows.setTitle("SDIS - Distributed Backup Service"); 
		Windows.setBackground(Color.WHITE);
		Windows.setBounds(0, 0, 1125, 900);
		Windows.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Windows.setLayout(null);
		
        scrollPaneSender = new JScrollPane(outputAreaSender);
        JPanel outputPanelSender = new JPanel(new FlowLayout());
        outputPanelSender.setBounds(0, 0, 1120, 200);
        scrollPaneSender.setBorder(BorderFactory.createTitledBorder("Sender"));
        outputPanelSender.add(scrollPaneSender);
        
        scrollPaneReceiverMC = new JScrollPane(outputAreaReceiverMC);
        JPanel outputPanelReceiverMC = new JPanel(new FlowLayout());
        outputPanelReceiverMC.setBounds(0, 220, 1120, 200);
        scrollPaneReceiverMC.setBorder(BorderFactory.createTitledBorder("MC"));
        outputPanelReceiverMC.add(scrollPaneReceiverMC);
        
        scrollPaneReceiverMDB = new JScrollPane(outputAreaReceiverMDB);
        JPanel outputPanelReceiverMDB = new JPanel(new FlowLayout());
        outputPanelReceiverMDB.setBounds(0, 440, 1120, 200);
        scrollPaneReceiverMDB.setBorder(BorderFactory.createTitledBorder("MDB"));
        outputPanelReceiverMDB.add(scrollPaneReceiverMDB);
        
        scrollPaneReceiverMDR = new JScrollPane(outputAreaReceiverMDR);
        JPanel outputPanelReceiverMDR = new JPanel(new FlowLayout());
        outputPanelReceiverMDR.setBounds(0, 660, 1120, 200);
        scrollPaneReceiverMDR.setBorder(BorderFactory.createTitledBorder("MDR"));
        outputPanelReceiverMDR.add(scrollPaneReceiverMDR);

        Windows.add(outputPanelSender);
        Windows.add(outputPanelReceiverMC);
        Windows.add(outputPanelReceiverMDB);
        Windows.add(outputPanelReceiverMDR);
        Windows.setVisible(true);
    }

    private void updateTextSender(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outputAreaSender.append(text);
            }
        });
    }
    
    private void updateTextReceiverMC(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outputAreaReceiverMC.append(text);
            }
        });
    }
    
    private void updateTextReceiverMB(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outputAreaReceiverMDB.append(text);
            }
        });
    }
    
    private void updateTextReceiverMR(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outputAreaReceiverMDR.append(text);
            }
        });
    }

    public void printlnSendChannel(String msg) {
        updateTextSender(msg + "\n");
    }
    
    public void printlnReceiverMC(String msg) {
        updateTextReceiverMC(msg + "\n");
    }
    
    public void printlnReceiverMDB(String msg) {
        updateTextReceiverMB(msg + "\n");
    }
    
    public void printlnReceiverMDR(String msg) {
        updateTextReceiverMR(msg + "\n");
    }
}
