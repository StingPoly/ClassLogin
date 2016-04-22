package com.app.classlogin;

import java.security.MessageDigest;


public class Myencode {
    StringBuffer a = new StringBuffer();
    
    Myencode() {
    }
    
    public String encode(String s) {
        int i = s.length();
        int j = 0;
        do
        {
            if(j >= i)
                return a.toString();
            System.out.print(s.charAt(j));
            System.out.print((new StringBuilder("-")).append(-32 + s.charAt(j)).toString());
            String s4;
            if(Integer.toBinaryString(-32 + s.charAt(j)).toString().length() == 6)
            {
                System.out.print((new StringBuilder("-0")).append(Integer.toBinaryString(-32 + s.charAt(j))).toString());
                String s8 = (new StringBuilder("0")).append(Integer.toBinaryString(-32 + s.charAt(j))).toString();
                String s9 = (new StringBuilder()).append(s8.charAt(0)).append(s8.charAt(1)).toString();
                String s10 = (new StringBuilder()).append(s8.charAt(6)).append(s8.charAt(5)).append(s8.charAt(4)).append(s8.charAt(3)).append(s8.charAt(2)).toString();
                int i1 = 32 + Integer.parseInt((new StringBuilder(String.valueOf(s9))).append(s10).toString(), 2);
                System.out.println((new StringBuilder("-")).append(s9).append(s10).append("-").append(i1).append("/").append((char)i1).toString());
                s4 = (new StringBuilder()).append((char)i1).toString();
            } else
            if(Integer.toBinaryString(-32 + s.charAt(j)).toString().length() == 5)
            {
                System.out.print((new StringBuilder("-00")).append(Integer.toBinaryString(-32 + s.charAt(j))).toString());
                String s5 = (new StringBuilder("00")).append(Integer.toBinaryString(-32 + s.charAt(j))).toString();
                String s6 = (new StringBuilder()).append(s5.charAt(0)).append(s5.charAt(1)).toString();
                String s7 = (new StringBuilder()).append(s5.charAt(6)).append(s5.charAt(5)).append(s5.charAt(4)).append(s5.charAt(3)).append(s5.charAt(2)).toString();
                int l = 32 + Integer.parseInt((new StringBuilder(String.valueOf(s6))).append(s7).toString(), 2);
                System.out.println((new StringBuilder("-")).append(s6).append(s7).append("-").append(l).append("/").append((char)l).toString());
                s4 = (new StringBuilder()).append((char)l).toString();
            } else
            {
                System.out.print((new StringBuilder("-")).append(Integer.toBinaryString(-32 + s.charAt(j))).toString());
                String s1 = (new StringBuilder()).append(Integer.toBinaryString(-32 + s.charAt(j))).toString();
                String s2 = (new StringBuilder()).append(s1.charAt(0)).append(s1.charAt(1)).toString();
                String s3 = (new StringBuilder()).append(s1.charAt(6)).append(s1.charAt(5)).append(s1.charAt(4)).append(s1.charAt(3)).append(s1.charAt(2)).toString();
                int k = 32 + Integer.parseInt((new StringBuilder(String.valueOf(s2))).append(s3).toString(), 2);
                System.out.println((new StringBuilder("-")).append(s2).append(s3).append("-").append(k).append("/").append((char)k).toString());
                s4 = (new StringBuilder()).append((char)k).toString();
            }
            if(s4.equals("!"))
                s4 = "%21";
            else
            if(s4.equals("#"))
                s4 = "%23";
            else
            if(s4.equals("%"))
                s4 = "%25";
            else
            if(s4.equals(")"))
                s4 = "%29";
            else
            if(s4.equals("-"))
                s4 = "%2D";
            else
            if(s4.equals("="))
                s4 = "%3D";
            else
            if(s4.equals("{"))
                s4 = "%7B";
            else
            if(s4.equals("|"))
                s4 = "%7C";
            else
            if(s4.equals("}"))
                s4 = "%7D";
            else
            if(s4.equals("~"))
                s4 = "%7E";
            else
            if(s4.equals("["))
                s4 = "%5B";
            else
            if(s4.equals("\\"))
                s4 = "%5C";
            else
            if(s4.equals("]"))
                s4 = "%5D";
            else
            if(s4.equals("^"))
                s4 = "%5E";
            else
            if(s4.equals("\""))
                s4 = "%22";
            else
            if(s4.equals("$"))
                s4 = "%24";
            else
            if(s4.equals("&"))
                s4 = "%26";
            else
            if(s4.equals("'"))
                s4 = "%27";
            else
            if(s4.equals("("))
                s4 = "%28";
            else
            if(s4.equals("*"))
                s4 = "%2A";
            else
            if(s4.equals("+"))
                s4 = "%2B";
            else
            if(s4.equals(","))
                s4 = "%2C";
            else
            if(s4.equals("."))
                s4 = "%2E";
            else
            if(s4.equals("/"))
                s4 = "%2F";
            else
            if(s4.equals(":"))
                s4 = "%3A";
            else
            if(s4.equals(";"))
                s4 = "%3B";
            else
            if(s4.equals("<"))
                s4 = "%3C";
            else
            if(s4.equals(">"))
                s4 = "%3E";
            else
            if(s4.equals("?"))
                s4 = "%3F";
            else
            if(s4.equals("@"))
                s4 = "%40";
            else
            if(s4.equals("_"))
                s4 = "%5F";
            else
            if(s4.equals("`"))
                s4 = "%60";
            else
            if(s4.equals(" "))
                s4 = "%20";
            a.append(s4);
            j++;
        } while(true);
    }
    
    public String md5(String txt, String time) {
    	String tmp = "";
        String num1 = txt;
        String num2 = time;
        try {
            long num3 = (Long.parseLong(num1) + Long.parseLong(num2)) * Long.parseLong(num2);
            String out = String.valueOf(num3);
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5hash = new byte[32];
            md.update(out.getBytes("iso-8859-1"), 0x0, out.length());
            md5hash = md.digest();
            
            StringBuffer hexString = new StringBuffer();  
            int i = 0;
            for (i=0; i < md5hash.length; i++)  
                hexString.append(Integer.toHexString(0xFF & md5hash[i]));  
            
            //tmp = convertToHex(md.digest()).toString().substring(0, 5);
            //tmp = md5hash.toString();
            
            return hexString.toString().substring(0, 5);
            //return tmp;
        } catch(Exception e) {
            e.getMessage();
        }
        return tmp;
    }
    
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        int i;
        if((i = 0x0) >= data.length) {
            i = i + 0x1;
            return String.valueOf(data.length);
        }
        int halfbyte = data[i] & 0xf;
        int two_halfs = 2*halfbyte;// int two_halfs = two_halfs;
        if(two_halfs >= data[i]) {
            if((halfbyte >= 0) && (halfbyte <= 0x9)) {
                buf.append((char)(halfbyte + 0x30));
            } else {
                buf.append((char)((halfbyte - 0xa) + 0x61));
            } 
        }
        halfbyte = data[i] & 0xf;
        data[i] = 0x1;
        return buf.toString();
    }
}



