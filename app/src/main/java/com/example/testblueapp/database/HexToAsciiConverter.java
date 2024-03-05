package com.example.testblueapp.database;

public class HexToAsciiConverter {


    public static String toHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        //StringBuilder stringBuilder = new StringBuilder();
        for (byte byteChar : bytes) {
            stringBuilder.append(String.format("%02X", byteChar));
        }
        return stringBuilder.toString();
    }
    public static String hexToAscii(String hexString) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

}
