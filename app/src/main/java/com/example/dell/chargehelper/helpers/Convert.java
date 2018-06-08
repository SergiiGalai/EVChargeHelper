package com.example.dell.chargehelper.helpers;

import java.util.List;

public class Convert {
    public static String[] toArray(List<String> values){
        return values.toArray(new String[values.size()]);
    }
}
