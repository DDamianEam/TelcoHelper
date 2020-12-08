/*
 * Copyright (c) 2020, Damian Duda <damian.duda@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package info.danos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This is another variation on SPC conversion.
 * This time without the NB GUI Builder.
 *
 * // ISPC Country field
 * // TODO Date and version of SANC file
 * // TODO Date and version of ISPC file
 * // TODO Add source ref to ITU-T in About
 * // TODO SANC and ISPC files read as resources from jar
 * // TODO Wyszukiwanie nazwy SPC z bazy ITU-T
 * // TODO Wyszukiwanie danych kontaktowych dla SPC (źródła różne, CSV, URL, PDF)
 * // TODO Określić i zebrać źródła danych dla SPC (pliki, URL)
 * // TODO SANC Text przesunąć w lewo
 * // TODO Jaki opis SPC ze źródeł? Będzie wiele, może TextArea?
 * // TODO Info o peerach i adresach IP - CeS?
 * // DONE Hex output on max ANSI
 * // DONE SANC Text move lower, expand
 * // DONE Bin output on max ITU/ANSI
 * // DONE SANC output on max ITU/ANSI
 * // TODO SANC-country mapping from CSV
 * // TODO SPC 3-3-3 format output normalization to A-AAA-A
 * // TODO Logging level
 * // TODO SANC to Country mapping
 * // DONE ANSI 888 does not receive correct values greater than 16383 from DecInput
 * // TODO make labels, text fields private global
 * // TODO Swing invoke later, czy wtedy będzie można this w Listenerach?
 * 
 * @author Damian Duda damian.duda@gmail.com
 */
public class Main implements ActionListener, DocumentListener, KeyListener {
    
    static JPanel mainPanel;

    static SancClass sancCode;
            
    // Etykiety pól wejściowych
    // Decimal
    private static JLabel spcDecLbl;
    // ITU-T 3-8-3
    private static JLabel spc383Lbl;
    // ITU-T 7-7
    private static JLabel spc77Lbl;
    // Hexadecimal
    private static JLabel spcHexLbl;
    // ANSI 8-8-8
    private static JLabel spc888Lbl;
    // Binary
    private static JLabel spcBinLbl;
    // SANC
    private static JLabel spcSANCLbl;
    // SANC Description
    private static JLabel spcSANCDesc;


    
    //Pola wejściowe
        // Dec in
        static JTextField spcDecIn;
        // ITU-T 3-8-3 in
        static JTextField spc383In;
        // ITU-T 7-7 in
        static JTextField spc77In;
        // Hexadecimal
        static JTextField spcHexIn;
        // ANSI 8-8-8
        static JTextField spc888In;
        // Binary
        static JTextField spcBinIn;
        // SANC
        static JTextField spcSANCIn;
    // Pola wyjściowe
        // Decimal
        static JTextField spcDecOut;        
        // ITU-T 3-8-3
        static JTextField spc383Out;                
        // ITU-T 7-7 
        static JTextField spc77Out;        
        // Hexadecimal
        static JTextField spcHexOut;        
        // ANSI 8-8-8
        static JTextField spc888Out;        
        // Binary
        static JTextField spcBinOut;        
        // SANC
        static JTextField spcSancOut;
        static JTextPane spcSancOutText;
        
        // static JTextPane aaa;
        // static JTextField spcSancOutText;
        // SPC Info
        // private static JTextField spcInfo;
        
        //About button
        static JButton aboutButton;
        // Exit button
        static JButton exitButton;
        
        
        // Static class initializaton block
        // Setup the format of log entry to single line
        static {
            // Description: Schildt p. 575, ed. 5
            //System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
            // Syslog-like
            // "%1$tF %4$S %5$s%n";
            System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$S] %1$tF %1$tT %5$s%n");
        }
        /**
         * JUL documentation and examples
         * https://docs.oracle.com/en/java/javase/14/core/java-logging-overview.html#GUID-B83B652C-17EA-48D9-93D2-563AE1FF8EDA
         * Przykłady różnych sposobów konfiguracji:
         * https://www.logicbig.com/tutorials/core-java-tutorial/logging/loading-properties.html
         */
        
        // Log
        private static final Logger logger = Logger.getLogger(Main.class.getName());
        

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("Telco Helper");
        mainPanel = new JPanel(null);
        
        sancCode = new SancClass();
        
        mainFrame.setSize(465, 375);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mainFrame.add(mainPanel);
        mainPanel.setLayout(null);
                
  
        
        // System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");
        System.out.println("pr: " + System.getProperty("java.util.logging.SimpleFormatter.format"));
        
        logger.log(Level.INFO, "Application starting...");    

        
        // TODO finish initMainComponents
        initMainComponents();
        
        //Etykiety 
        // TODO move to init method
        // Decimal
//        JLabel spcDecLbl = new JLabel("Decimal");
//        spcDecLbl.setBounds(10, 20, 100, 25);
//        mainPanel.add(spcDecLbl);
//        // ITU-T 3-8-3
//        JLabel spc383Lbl = new JLabel("ITU-T 3-8-3");
//        spc383Lbl.setBounds(10, 50, 100, 25);
//        mainPanel.add(spc383Lbl);
//        // ITU-T 7-7
//        JLabel spc77Lbl = new JLabel("ITU-T 7-7");
//        spc77Lbl.setBounds(10, 80, 100, 25);
//        mainPanel.add(spc77Lbl);
//        // Hexadecimal
//        JLabel spcHexLbl = new JLabel("Hexadecimal");
//        spcHexLbl.setBounds(10, 110, 100, 25);
//        mainPanel.add(spcHexLbl);
//        // ANSI 8-8-8
//        JLabel spc888Lbl = new JLabel("ANSI 8-8-8");
//        spc888Lbl.setBounds(10, 140, 100, 25);
//        mainPanel.add(spc888Lbl);        
//        // Binary
//        JLabel spcBinLbl = new JLabel("Binary");
//        spcBinLbl.setBounds(10, 170, 100, 25);
//        mainPanel.add(spcBinLbl);
//        // SANC
//        JLabel spcSANCLbl = new JLabel("SANC");
//        spcSANCLbl.setBounds(10, 200, 100, 25);
//        mainPanel.add(spcSANCLbl);
        
        //Pola wejściowe
        // Dec in
        spcDecIn = new JTextField("0", 20);
        spcDecIn.setBounds(120, 20, 100, 25);
        mainPanel.add(spcDecIn);
        spcDecIn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                spcInKeyReleased("spcDecIn");
            }
        
        });
        
        // To jeżeli chcemy naciskać Przelicz lub Enter
//        spcDecIn.addKeyListener(new Main());
//        spcDecIn.setName("SPCDECIN");
//        spcDecIn.setActionCommand("DECINPUT");
//       
        
        
        // ITU-T 3-8-3 in
        spc383In = new JTextField("0", 20);
        spc383In.setBounds(120, 50, 100, 25);        
        mainPanel.add(spc383In);
        /* setActionCommand działa tylko po ENTER na polu,
            w docs.oracle.com napisali, że dla AWT jest TextListener
            w JTextField trzeba użyć DocumentListener
        
            Tu spróbujemy wywołać metodę z klasy zewnętrznej za pomocą 
            metody z klasy wewnętrznej.
        
        Tak jest jak metody są poza static main:
        
        spcBinInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                spcBinInputKeyReleased(evt);
            }
        
        */
        /**
         * KeyListener na razie bez adaptera, "po staremu"
         */
        spc383In.addKeyListener( new java.awt.event.KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                // nothing to do
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                // nothing to do
            }

            @Override
            public void keyReleased(KeyEvent e) {
                spcInKeyReleased("spc383In");
            }
        }
        );
        
        

        

        // TODO move these below to initializer




//        // Hexadecimal
//        spcHexIn = new JTextField("0", 20);
//        spcHexIn.setBounds(120, 110, 100, 25);
//        // TODO Hex calculation
//        spcHexIn.setEnabled(true);
//        mainPanel.add(spcHexIn);
//        spcHexIn.addKeyListener(new java.awt.event.KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                spcInKeyReleased("spcHexIn");
//            }
//        });
        
        
        
//        // ANSI 8-8-8
//        spc888In = new JTextField("0", 20);
//        spc888In.setBounds(120, 140, 100, 25);
//        // TODO 888 calculation
//        spc888In.setEnabled(true);
//        mainPanel.add(spc888In);
//        // tym razem KeyAdapter
//        spc888In.addKeyListener(new java.awt.event.KeyAdapter() {
//            
//            @Override
//            public void keyReleased(KeyEvent e){
//                spcInKeyReleased("spc888In");
//            }
//});
        
        
        
//        // Binary
//        spcBinIn = new JTextField("0", 20);
//        spcBinIn.setBounds(120, 170, 100, 25);
//        // TODO Binary calculation
//        spcBinIn.setEnabled(false);
//        mainPanel.add(spcBinIn);
//        // SANC
//        spcSANCIn = new JTextField("0", 20);
//        spcSANCIn.setBounds(120, 200, 100, 25);
//        // TODO SANC calculation
//        spcSANCIn.setEnabled(false);
//        mainPanel.add(spcSANCIn);
        
        
        mainFrame.setVisible(true);
    }

    private static void initMainComponents() {

        // Inicjalizacja etykiet (globalne)
        // Decimal
        spcDecLbl = new JLabel("Decimal");
        spcDecLbl.setBounds(10, 20, 100, 25);
        mainPanel.add(spcDecLbl);
        // ITU-T 3-8-3
        spc383Lbl = new JLabel("ITU-T 3-8-3");
        spc383Lbl.setBounds(10, 50, 100, 25);
        mainPanel.add(spc383Lbl);
        
        // ITU-T 7-7
        spc77Lbl = new JLabel("ITU-T 7-7");
        spc77Lbl.setBounds(10, 80, 100, 25);
        mainPanel.add(spc77Lbl);
        // Hexadecimal
        spcHexLbl = new JLabel("Hexadecimal");
        spcHexLbl.setBounds(10, 110, 100, 25);
        mainPanel.add(spcHexLbl);
        // ANSI 8-8-8
        spc888Lbl = new JLabel("ANSI 8-8-8");
        spc888Lbl.setBounds(10, 140, 100, 25);
        mainPanel.add(spc888Lbl);        
        // Binary
        spcBinLbl = new JLabel("Binary");
        spcBinLbl.setBounds(10, 170, 100, 25);
        mainPanel.add(spcBinLbl);
        // SANC
        spcSANCLbl = new JLabel("SANC");
        spcSANCLbl.setBounds(10, 200, 100, 25);
        mainPanel.add(spcSANCLbl);
        // SANC Description
        spcSANCDesc = new JLabel("SANC Description");
        spcSANCDesc.setBounds(10, 230, 100, 25);
        mainPanel.add(spcSANCDesc);
        

//Pola wejściowe
        
        
        
        
                // ITU-T 7-7 in
        spc77In = new JTextField("0", 20);
        spc77In.setBounds(120, 80, 100, 25);
        // FIXME Add 77 calculation
        spc77In.setEnabled(true);
        mainPanel.add(spc77In);
        spc77In.addKeyListener(new java.awt.event.KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyReleased(KeyEvent e) {
                spcInKeyReleased("spc77In");
            }
            
            
        });
        
        // Hexadecimal
        spcHexIn = new JTextField("0", 20);
        spcHexIn.setBounds(120, 110, 100, 25);
        // TODO Hex calculation
        spcHexIn.setEnabled(true);
        mainPanel.add(spcHexIn);
        spcHexIn.addKeyListener(new java.awt.event.KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyReleased(KeyEvent e) {
                spcInKeyReleased("spcHexIn");
            }
        });
        
        // ANSI 8-8-8
        spc888In = new JTextField("0", 20);
        spc888In.setBounds(120, 140, 100, 25);
        // TODO 888 calculation
        spc888In.setEnabled(true);
        mainPanel.add(spc888In);
        // tym razem KeyAdapter
        spc888In.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                spcInKeyReleased("spc888In");
            }
        });

        // Binary
        spcBinIn = new JTextField("0", 20);
        spcBinIn.setBounds(120, 170, 100, 25);
        // TODO Binary calculation
        spcBinIn.setEnabled(true);
        mainPanel.add(spcBinIn);
        spcBinIn.addKeyListener(new java.awt.event.KeyAdapter() {
            
            @Override
            public void keyReleased(KeyEvent e){
                spcInKeyReleased("spcBinIn");
            }
});
        
        
        
        // SANC
        spcSANCIn = new JTextField("0", 20);
        spcSANCIn.setBounds(120, 200, 100, 25);
        // TODO SANC calculation
        spcSANCIn.setEnabled(true);
        mainPanel.add(spcSANCIn);
        spcSANCIn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                spcInKeyReleased("spcSANCIn");
            }
        });

        
        // Pola wyjściowe
        // Decimal
        spcDecOut = new JTextField("0", 20);
        spcDecOut.setBounds(240, 20, 100, 25);
        spcDecOut.setEditable(false);
        mainPanel.add(spcDecOut);

        // ITU-T 3-8-3
        spc383Out = new JTextField("0", 20);
        spc383Out.setBounds(240, 50, 100, 25);
        spc383Out.setEditable(false);
        mainPanel.add(spc383Out);

        // ITU-T 7-7 
        spc77Out = new JTextField("0", 20);
        spc77Out.setBounds(240, 80, 100, 25);
        spc77Out.setEditable(false);
        mainPanel.add(spc77Out);
        
        // Hexadecimal
        spcHexOut = new JTextField("0", 20);
        spcHexOut.setBounds(240, 110, 100, 25);
        spcHexOut.setEditable(false);
        mainPanel.add(spcHexOut);
        
        // ANSI 8-8-8
        spc888Out = new JTextField("0", 20);
        spc888Out.setBounds(240, 140, 100, 25);
        spc888Out.setEditable(false);
        mainPanel.add(spc888Out);
        
        // Binary
        spcBinOut = new JTextField("0", 40);
        spcBinOut.setBounds(240, 170, 200, 25);
        spcBinOut.setEditable(false);
        mainPanel.add(spcBinOut);
        
        // SANC
        spcSancOut = new JTextField("0", 20);
        spcSancOut.setBounds(240, 200, 100, 25);
        spcSancOut.setEditable(false);
        mainPanel.add(spcSancOut);
        
        spcSancOutText = new JTextPane();
        
        spcSancOutText.setBounds(120, 230, 320, 50);
        spcSancOutText.setEditable(false);
        mainPanel.add(spcSancOutText);

        
        // About button
        aboutButton = new JButton("About");
        aboutButton.setBounds(180, 305, 80, 25);
        mainPanel.add(aboutButton);
        aboutButton.setActionCommand("ABOUT");
        aboutButton.addActionListener(new Main());
        
        // Exit button
        exitButton = new JButton("Exit");
        exitButton.setBounds(280, 305, 80, 25);
        mainPanel.add(exitButton);
        exitButton.setActionCommand("EXIT");
        exitButton.addActionListener(new Main());
    }
    
    /** 
     * Dodane automatycznie przez NB jako wymagane przez interfejs ActionListener.
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        
        System.out.println("Command: " + cmd);
        
        
        
        switch (cmd){
            case "EXIT":
                System.exit(0);
                break;
            case "ABOUT":
                JOptionPane.showMessageDialog(null,
                        "Telco Helper\nVersion 0.01\n(c) 2020 Damian Duda\n damian.duda@gmail.com",
                        "Telco Helper",
                        JOptionPane.PLAIN_MESSAGE);
                break;
            default: 
        }
    }

    /**
     * Metoda na potrzeby KeyListener
     * 
     * Ponieważ nie wykorzystuję generatora akcji z NB
     
     * @param inputName - name of input field which was updated
     */
    public static void spcInKeyReleased(String inputName){
        
        int spcValue = 0;
        
        // check which field updated
        switch (inputName){
            case "spcDecIn":
                // DONE tu teraz potrzebne są zmienne globalne
                try {
                    // tu obliczamy wartość int, bez walidacji
                    // walidacja dopiero przy konkretnych formatach
                    spcValue = Integer.parseInt(spcDecIn.getText());
                    // spcValue = Converter.decToDecStr(spcValue);
                } catch (NumberFormatException e){
                    spcValue = -1;
                }

                //DEBUG
                // System.out.println("spc: " + spcValue);
                
                break;
            case "spc383In":
                System.out.println("spc383In");
                // TODO convert 3-8-3 into Dec
                spcValue = Converter.Itu383ToDec(spc383In.getText());
                // DEBUG
                System.out.println("383:" + spc383In.getText() + "val: " + spcValue);
                
                break;
            case "spc77In":
                System.out.println("spc77In");
                spcValue = Converter.Itu77ToDec(spc77In.getText());
                // DEBUG
                System.out.println("77: " + spc77In.getText() + "val: " + spcValue);
                break;
            case "spcHexIn":
                spcValue = Converter.HexToDec(spcHexIn.getText());
                // DEBUG
                System.out.println("Hex: " + spcHexIn.getText() + "val: " + spcValue);
                break;
            case "spc888In":
                spcValue = Converter.ansi888ToDec(spc888In.getText());
                // DEBUG
                System.out.println("888: " + spc888In.getText() + "val: " + spcValue);
                break;
            case "spcBinIn":
                spcValue = Converter.binToDec(spcBinIn.getText());
                // DEBUG
                System.out.println("Bin: " + spcBinIn.getText() + "val: " + spcValue);
                break;
            case "spcSANCIn":
                // wprowadzenie SANC powinno wyzerować wszystkie pola Out
                // gdzie nie ma sensu obliczać na jego podstawie
                // Wykorzystujemy trik - doklejamy -0 do SANC
                // i dalej traktujemy jak SPC 3-8-3
                spcValue = Converter.Itu383ToDec(spcSANCIn.getText() + "-0");
                // DEBUG
                System.out.println("SANC In:" + spcSANCIn.getText() + ", val: " + spcValue);
                // TODO update output fields
            default:
                System.out.println("Unknown input field");
        }
        // update output pane
        
        spcUpdateOutput(spcValue);
        // DONE Potrzebne są pola wyjściowe jako zmienne globalne
        
        
    }
    
    /**
     * Updates all output GUI fields.
     * 
     * @param spcDecValue Decimal value of SPC
     */
    static void spcUpdateOutput(int spcDecValue){
     
        // Decimal
        spcDecOut.setText(Converter.decToDecStr(spcDecValue));
        
        // ITU-T 3-8-3
        spc383Out.setText(Converter.decToItu383(spcDecValue));
        
        // ITU-T 7-7 
        spc77Out.setText(Converter.decToItu77(spcDecValue));
        
        // Hexadecimal
        // DONE oddzielna metoda byłaby przydatna, żeby wyrzucić logikę stąd
        spcHexOut.setText(Converter.decToHexStr(spcDecValue));
        
        // ANSI 8-8-8
        spc888Out.setText(Converter.decToAnsi888(spcDecValue));
        
        // Binary
        spcBinOut.setText(Converter.decToBinStr(spcDecValue));
        
        // SANC
        spcSancOut.setText(Converter.decToSanc14Code(spcDecValue));
        // Geographical Area name
        spcSancOutText.setText(sancCode.getGeoAreaText(Converter.decToSanc14Code(spcDecValue)));
        
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {
        // do nothing
        //throw new UnsupportedOperationException("Not supported yet.");        
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        // do nothing
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent ke) {        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        String sourceName = ke.getSource().toString();
        
       
        System.out.println("Source: " + sourceName);
        // "spcDecIn".equals(ke)
    }

    @Override
    public void insertUpdate(DocumentEvent de) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
