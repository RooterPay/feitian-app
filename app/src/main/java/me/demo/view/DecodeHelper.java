package me.demo.view;

import java.util.List;

import io.github.binaryfoo.RootDecoder;

public class DecodeHelper {

    public static String decodeTLV(String data) {
        return new RootDecoder().decode(data, "EMV", "constructed")
                .get(0).getDecodedData();
    }

    public static char convertHexBytesToAsciiString(String hex) {

        // Step 2: Convert hex string to integer (base 16)
        int decimalValue = Integer.parseInt(hex, 16);

        // Step 3: Format it as a hexadecimal value with 0x prefix
        String hexFormatted = String.format("0x%02X", decimalValue);

        // Step 4: Convert decimal to character
        char character = (char) decimalValue;

        // Step 5: Print each step
//        System.out.println("Input Hex String: " + hex);
//        System.out.println("Decimal Value: " + decimalValue);
//        System.out.println("Hex with Prefix: " + hexFormatted);
//        System.out.println("Corresponding Character: '" + character + "'");
//        System.out.println("-----------------------");
        return character;
    }

    public static String appendStrings(List<String> string) {
        StringBuilder sb = new StringBuilder();
        for (String txt : string) {
            sb.append(txt);
        }
        return sb.toString();
    }

}
