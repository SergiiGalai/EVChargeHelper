package com.example.dell.chargehelper.charge;

import android.support.test.runner.AndroidJUnit4;

import com.example.dell.chargehelper.helpers.Convert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ChargeValuesProviderTest {

    @Test
    public void result_contains_amperage_when_amperage_was_not_in_the_generated_list(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(20);
        String[] expected = {"6", "8", "10", "12", "14", "16", "20", "32", "64"};
        assertArrayEquals(expected, Convert.toArray(amperage));
    }


    @Test
    public void result_contains_amperage_when_amperage_in_the_generated_list(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(16);
        String[] expected = {"6", "8", "10", "12", "14", "16", "32", "64"};
        assertArrayEquals(expected, Convert.toArray(amperage));
    }
}
