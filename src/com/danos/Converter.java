/*
 * Copyright (c) 2018, Damian Duda
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package com.danos;

import java.util.Formatter;
import java.util.Scanner;

/**
 * Klasa zawiera metody konwersji wartości SPC do różnych formatów.
 * 
 * Aktualnie obsługiwane konwersje:
 * - DEC -> 383 ITU-T
 * - DEC -> 77 ITU-T
 * - DEC -> 888 ANSI
 * - DEC -> Hex
 * 
 * Dla zachowania możliwości rozszerzenia do ANSI liczba bitów musi być 
 * nie mniejsza niż 24. Dlatego wykorzystano int.
 * 
 * TODO Unit tests
 * TODO Radio check list do wyboru formatu wejściowego + reakcja na PRZELICZ.
 * DONE Rozważyć usunięcie reakcji na ChangeValue z InputFields.
 * TODO Możliwość realizacji jako klasa statyczna, bez instancjalizacji u klienta.
 * 
 * @author Damian Duda, damian.duda@orange.com
 */
public class Converter {
    
// TODO move from global to appropriate function    
// mask3-mask2-mask1: 3-8-3
//    private final int mask1 = 0x7;
//    private final int mask2 = 0x7f8;
//    private final int mask3 = 0x3800;
    

    /**
     * The method converts decimal SPC into 3-8-3 ITU-T format.
     * 
     * It returns String data with textual SPC value.
     * In case of incorrect input, it returns empty string.
     * It was considered to return "X-X-X" string optionally,
     * but empty string is easier to evaluate in external programs.
     * 
     * How does it work?
     * First mask: 111 = 7;
     * Second mask: 111 1111 1000 = 7F8 hex, next shift is three times left
     * Third mask: 11 1000 0000 0000 = 3800 hex, asl 11
     * next shift left 8+3 times =11       
     * first = SPC & 0x7
     * second = (SPC & 0x7f8)>>3
     * third = (SPC & 0x3800)>>11;
     * 
     * @param spcode The decimal value of Signaling Point Code
     * @return String, ITU-T Signalling Point Code or empty string.
     */
    static String decToItu383(int spcode) {
        
    int maskRight = 0x7;
    int maskMiddle = 0x7f8;
    int maskLeft = 0x3800;

    // brak obsługi przypadku gdy liczba będzie za duża
    if(spcode < 0 || spcode >= 16384)
            // check empty instead of "0-0-0"
            // return "0-0-0";
            return "";
        // dodatkowe nawiasy bo konwertowało shifty na String
        
    return ((spcode & maskLeft)>>11) + "-"
            + ((spcode & maskMiddle)>>3) + "-"
            + (spcode & maskRight);
    }
    
    /**
     * Converts decimal SPC into 8-8-8 ANSI string value.
     * 
     * @param spcode Decimal value of SPC.
     * @return String SPC text value as ANSI 8-8-8 format.
     */
    static String decToAnsi888(int spcode){
        // mask3-mask2-mask1: 8-8-8
        int byte1 = 0xff;    
        int byte2 = 0xff00;
        int byte3 = 0xff0000;
        
        // Sprawdź zakres - max 255-255-255 
        if (spcode < 0 || spcode > 0xffffff)
            return "";
        
        return ((byte3 & spcode)>>16) + "-" + ((byte2 & spcode)>>8) + "-" + (byte1 & spcode);
    }
    
    /**
     * Returns 7-7 ITU-T SPC for passed decimal SPC.
     * 
     * Warning: 14-bit SPC is accepted only.
     * 
     * @param spCode
     * @return 
     */
    static String decToItu77(int spcode){
        
        // mask2-mask1: 7-7
        // 0111 1111
        int byte1 = 0x7f;
        // 11 1111 1000 0000
        int byte2 = 0x3f80;
        
        if (!isValidItuT14(spcode)) {
            return "";
        }

        return ((spcode & byte2)>>7) + "-" + (spcode & byte1);
        
    }
    
    /**
     * Dec to Dec - quite a nonsense, but used to validate Decimal SPC.
     * Instead of unecessary moving validation logic into application.
     *
     * @param spcode Integer value of SPC
     * @return SPC value if SPC is valid 24 bit number, -1 otherwise.
     */
    static String decToDecStr(int spcode){
        
        if (isValidDecimal(spcode))
            return Integer.toString(spcode);
        else
            return "";
        
    }
    
    /**
     * Mała optymalizacja, żeby nie wyświetlało ffffffff gdy SPC niepoprawny.
     * 
     * @param spcode Wartość numeryczna SCP
     * @return Ciąg tekstowy reprezentujący wartość hex, lub pusty, gy napotkano błąd.
     */
    static String decToHexStr(int spcode){
        
        if(isValidDecimal(spcode))
            return Integer.toHexString(spcode).toUpperCase();
        else 
            return "";
    }
    
    /**
     * Returns SANC code string for given SPC decimal value.
     * 
     * @param spcode Decimal value of SPC
     * @return Textual representation of SANC code, or empty string if out of range.
     */
    static String decToSanc14Code(int spcode) {

        String sanc;
        int geo=0;
        int area = 0;
        Formatter fmtStr = new Formatter();
        
        int kdBits = 0x7f8;
        int nlBits = 0x3800;

        // sanitize FIRST
        if (!isValidItuT14(spcode)) {
            return "";
        }

        // sanc = new SancClass((spcode & nlBits) >>> 11, (spcode & kdBits) >>> 3);
        geo = (spcode & nlBits) >>> 11;
        area = (spcode & kdBits) >>> 3;
        
        //sanc = ((spcode & nlBits) >>> 11) + "-" + ((spcode & kdBits) >>> 3);
        // String sancStr = decToItu383(spcode);

        fmtStr.format("%d-%03d", geo, area);
        return fmtStr.toString();
        // return geo + "-" + area;

    }
    
    
    /**
     * Returns SANC Geographical Area / Signalling Network name.
     * 
     * Reference: ITU-T
     * 
     * @param spcode Decimal value of SPC
     * @return Text value of Geo Area or Network, empty string if SPC out of range.
     */
    static String decToSanc14Text(int spcode){
        
        // Formatter fmtStr = new Formatter();
        
        String sancCode = decToSanc14Code(spcode);
        
        System.out.println("sancCode: ");
        
        // sancCode = fmtStr.format(sancCode, os)
        System.out.println("sancCode: ");
        
        // TODO przenieść do głównej aplikacji, żeby nie mieszać static i non-static
        // return SancClass.getGeoAreaText(sancCode);
       return "";
    }
    /**
     * Metoda konwertuje SPC w formacie 3-8-3 ITU-T na DEC.
     * 
     * TODO count the value
     *
     * @param SPC wartość punktu kodowego w formacie 3-8-3.
     * @return Valid decimal SPC or -1 if failed.
     */
    static int Itu383ToDec(String SPC) {

        // itu_conv validator = new itu_conv();
        Integer result = -1;

        if (!is_valid_383(SPC)) {
            return -1;
        }

        
        result = count_Itu_383(SPC);
        
        System.out.println("DEC SPC: " + result);

        return result;
    }

    /**
     * Converts 7-7 SPC format into Decimal SPC.
     * 
     * @param SPC String 7-7 SPC representation.
     * @return Int value of Decimal SPC.
     */
    static int Itu77ToDec(String SPC){
        
        int result = -1;
        
        if (!isValidItu77(SPC)){
            return -1;
        }
        
        result = count_Itu_77(SPC);
        
        System.out.println("DEC SPC: " + result);
        
        return result;
    }
    
    static int HexToDec(String spcStr){
        
        int spcCodeDecValue = -1; 

            try {
                // Yes, Java 5 allow automatic encapsulation
                spcCodeDecValue = Integer.parseInt(spcStr, 16);

            } catch (NumberFormatException e) {
                System.out.println("Error " + e.toString());
                spcCodeDecValue = -1;
            }        
        
        
//        if (spcStr.isEmpty()) {
//            // reset the Dec value if empty input, otherwise exception would leave SPC undefined
//            spcCodeDecValue = -1;
//
//        } else {
//            try {
//
//                // Yes, Java 5 allow automatic encapsulation
//                spcCodeDecValue = Integer.parseInt(spcStr, 16);
//
//            } catch (NumberFormatException e) {
//                System.out.println("Error " + e.toString());
//                // and do nothing
//                spcCodeDecValue = -1;
//            }
//        }
        
        return spcCodeDecValue;
        
    }
    
    /**
     * Counts the decimal value of 8-8-8 ANSI SPC representation.
     *
     * @param SPC String representation of SPC; "XXX-XXX-XXX".
     * @return Decimal value of SPC or -1 if any error occured.
     */
    static int ansi888ToDec(String SPC) {

        String[] numbers_tab = SPC.split("-");

        int spc_dec_tab[] = new int[3];

        Scanner sc;

        // First - are there three numbers?
        if (numbers_tab.length != 3) {
            // the count of numbers is not valid
            return -1;
        }

        // Second - are all elements valid integers?
        for (int i = 0; i < 3; i++) {
            sc = new Scanner(numbers_tab[i]);
            if (sc.hasNextInt()) {
                spc_dec_tab[i] = sc.nextInt();
            } else {
                // some element is not a number
                return -1;
            }
        }

        // Third - wether the number parts are in the appropriate range?
        if (spc_dec_tab[0] < 0 || spc_dec_tab[0] > 255) {
            return -1;
        }

        if (spc_dec_tab[1] < 0 || spc_dec_tab[1] > 255) {
            return -1;
        }

        if (spc_dec_tab[2] < 0 || spc_dec_tab[2] > 255) {
            return -1;
        }

        // All should be OK
        return (65536 * spc_dec_tab[0] + 256 * spc_dec_tab[1] + spc_dec_tab[2]);
    }
    
    
    /**
     * Validates the SPC in the 383 format.
     * 
     * TODO Refactor to modified split input type.
     * 
     * @param SPC
     * @param result
     * @return 
     */
    private static boolean is_valid_383(String SPC) {

        String[] numbers_tab = SPC.split("-");
        int spc_tab[] = new int[3];

        Scanner sc;

        // First - are there three numbers?
        if (numbers_tab.length != 3) {
            // the count of numbers is not valid
            return false;
        }

        // Second - are all elements valid integers?
        for (int i = 0; i < 3; i++) {
            sc = new Scanner(numbers_tab[i]);
            if (sc.hasNextInt()) {
                spc_tab[i] = sc.nextInt();
            } else {
                // some element is not a number
                return false;
            }

        }

        // Third - wether the number parts are in the appropriate range?
        
        if(spc_tab[0] < 0 || spc_tab[0] > 7)
            return false;
        
        if(spc_tab[1] < 0 || spc_tab[1] > 255)
            return false;
        
        if(spc_tab[2] < 0 || spc_tab[2] > 7)
            return false;
        
               
        return true;
    }
     
    
    
    /**
     * Counts decimal value of 383 SPC.
     * 
     * Note: The method does not validate input!
     * 
     * @param spcode Valid 383 SPC
     * @return Decimal SPC value
     */
    private static int count_Itu_383(String spcode){
        
        String[] numbers_tab = spcode.split("-");
        int spc_tab[] = new int[3];

        Scanner sc;

        for (int i = 0; i < 3; i++) {
            sc = new Scanner(numbers_tab[i]);
            if (sc.hasNextInt()) {
                spc_tab[i] = sc.nextInt();
            } else {
                // some element is not a number
                return -1;
            }

        }
       
        
        // All should be OK
        
        return (  2048 * spc_tab[0] + 8 * spc_tab[1] + spc_tab[2]);
        
    }
    
    /**
     * This method checks if SPC value is less than 24 bit max value.
     * 
     * This is needed regarding the ANSI SPC which are 24 bit values,
     * as ITU-T are only 14-bit values.
     * 
     * @param spcode
     * @return True if decimal value is valid (le 24bit).
     */
    private static boolean isValidDecimal(int spcode){
        
        return (spcode >= 0) & (spcode <= 0xffffff );
    }
    
    /** 
     * Checks if decimal value of SPC is in ITU-T 14-bit range.
     * 
     * @param spcode Integer value
     * @return true if SPC valid, false otherwise
     */
    private static boolean isValidItuT14(int spcode){
        
        // brak obsługi przypadku gdy liczba będzie za duża
        // 2^14 -1 = 16383
        
        // be aware of ! before cond expr
        return !(spcode < 0 || spcode > 16383);
        
    }
    
    /**
     * Check if SPC string has X-X format and 127 range.
     * @param SPC
     * @return 
     */
    private static boolean isValidItu77(String SPC){
        
        // extract SPC parts
        String[] numbers_tab = SPC.split("-");
        // array for result
        int spc_tab[] = new int[2];
        // for parsing number into int
        Scanner sc;
        
        // First - are there two numbers?
        if (numbers_tab.length != 2) {
            // the count of numbers is not valid
            return false;
        }
        
            // Second - are all elements valid integers?
        for (int i = 0; i < 2; i++) {
            sc = new Scanner(numbers_tab[i]);
            if (sc.hasNextInt()) {
                spc_tab[i] = sc.nextInt();
            } else {
                // some element is not a number
                return false;
            }
        }
        
        // Third - wether the number parts are in the appropriate range?
        
        if(spc_tab[0] < 0 || spc_tab[0] > 127)
            return false;
        
        if(spc_tab[1] < 0 || spc_tab[1] > 127)
            return false;
        
        return true;
    }
    
    
        private static int count_Itu_77(String spcode){
        
        String[] numbers_tab = spcode.split("-");
        int spc_tab[] = new int[2];

        Scanner sc;

        for (int i = 0; i < 2; i++) {
            sc = new Scanner(numbers_tab[i]);
            if (sc.hasNextInt()) {
                spc_tab[i] = sc.nextInt();
            } else {
                // some element is not a number
                return -1;
            }

        }
       
        
        // All should be OK
        
        return (  128 * spc_tab[0] + spc_tab[1]);
        
    }
        
        /**
         * Oblicza liczbową, dziesiętną reprezentację punktu sygnalizacyjnego.
         * 
         * @param spc Binarna-tekstowa reprezentacja punktu kodowego 01001... (14 bitów)
         * @return Wartość dziesiętna punktu kodowego lub -1 w przypadku błędu
         */
    static int binToDec(String spc) {

        int spcReturn = 0;

        // Ciąg pusty zwracamy jako 0d:
        if (spc.isEmpty())
            return -1;
        
        // Czy spc jest poprawną liczbą binarną?            
        if (spc.matches("[^01]")) {
            return -1;
        }

        // Czy spc ma 14 bitów lub mniej?
        if (spc.length() > 14) {
            return -1;
        }

        try {
            spcReturn = Integer.parseInt(spc, 2);
        } catch (NumberFormatException exc) {
            spcReturn = -1;
            // log exc
        }

        // log
        System.out.println("binToDec: " + spcReturn);
        return spcReturn;
    }
    
    static String decToBinStr(int spc) {

        if (isValidDecimal(spc)) {
            return Integer.toBinaryString(spc);
        } else {
            return "";
        }

    }

}

