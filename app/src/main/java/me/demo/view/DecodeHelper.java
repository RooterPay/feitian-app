package me.demo.view;

import io.github.binaryfoo.RootDecoder;

public class DecodeHelper {

    public static String decode(String data) {
        return new RootDecoder().decode(data, "EMV", "constructed")
                .get(0).getDecodedData();
    }
}
