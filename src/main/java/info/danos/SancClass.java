/*
 * Copyright (c) 2020, Damian Duda <damian.duda@orange.com>
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class provides mapping of SANC Code to SANC textual area/country name.
 * 
 * The class requires a textual file (sanc.txt) with current mapping, available
 * in directory where the app jar is located.
 * 
 * TODO Dynamic iniitalization before class is used instead of static.
 * 
 * @author Damian Duda <damian.duda@orange.com>
 */
public class SancClass {
    
    private Hashtable<String, String> sancTable;
    
    
    SancClass(){
        
    BufferedReader inputDataCSV;
    
    // DONE Initialize SANC table from external CSV file
    
    this.sancTable = new Hashtable<String,String>();
    
        String lineReading;
        String regexSANC = "^[0-7]-[0-9]{1,3}";
        
        Pattern p = Pattern.compile(regexSANC, Pattern.CASE_INSENSITIVE);
        
        
        // Warning: Matches expect regexp matching entire string. This is not an 
        // usual string matching.
        
        try {
            inputDataCSV = new BufferedReader(new FileReader("sanc.txt"));                       
            
            // DEBUG
            /*
            if(m.find()){
                System.out.println("SANC regexp: " + regexSANC + " " + lineReading);
                } 
                else{
                    System.out.println("Not matched: " + regexSANC + " " + lineReading);
                }
            */
       
            
            while((lineReading = inputDataCSV.readLine()) != null){
                
                Matcher m = p.matcher(lineReading);
                String lineWords[];
                // This does not work as matches requires regexp matching whole line
//                if(Pattern.matches(regexSANC, lineReading)){
                if(m.find()){
                    System.out.println("SANC regexp: " + regexSANC + " " + lineReading);
                    // we would like two columns
                    lineWords = lineReading.split(",", 2);
                    
                    // skip not recognized pairs
                    if (lineWords.length < 2)
                        continue;
                    
                    System.out.println("lineWords: " + lineWords[0] + ":" + lineWords[1]);
                    
                    sancTable.put(lineWords[0], lineWords[1]);
                } 
                else{
                    System.out.println("Not matched: " + regexSANC + " " + lineReading);
                }
                    
                
                
            }
            
            inputDataCSV.close();
            
        }
        catch (IOException ioe){
            System.out.println("I/O Error: " + ioe.getMessage());
            System.exit(1);
        }
        
        
        
        
        // DEBUG
//        sancTable.put("2-035", "Germany (Federal Republic of)");
//        sancTable.put("2-120", "Poland (Republic of)");
        
    }
        
    
    /**
     * Method gives the text value of SANC's Geographical Area or Signalling Network.
     * 
     * @param sancCode SANC Code string A-BBB format
     * @return Geographical Area or Signalling Network name
     */
    public String getGeoAreaText(String sancCode){
        
        
        //TODO Normalize SANC code to A-BBB format
        System.out.println("getGeoAreaText in: " + sancCode);
        if(sancTable.containsKey(sancCode)){
            System.out.println("getGeoAreaText out: " + sancTable.get(sancCode));
            System.out.println("Key tst: " + sancTable.containsKey("7-251"));
            System.out.println("Key req: " + sancTable.containsKey(sancCode));
            return sancTable.get(sancCode);
        
        }
        else{
            System.out.println("getGeoAreaText out: <N>" );
            return "";
        }
    }
    
    
}
