package cn.kavel.demo.javantwk.core;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by wjw_w on 2017/3/11.
 */
public class SpamCheck {
    private static final String BLACKHOLE = "sbl.spamhaus.org";

    public static void main(String[] args) throws UnknownHostException {
        for (String arg : args) {
            if (isSpammer(arg)) {
                System.out.println(arg + " is a known spammer.");
            } else {
                System.out.println(arg + " appears legitimate.");
            }
        }
    }

    private static boolean isSpammer(String arg) {
        try {
            InetAddress address = InetAddress.getByName(arg);
            byte[] quad = address.getAddress();
            String query = BLACKHOLE;
            for (byte octet : quad) {
                int unsignedByte = octet < 0 ? octet + 256 : octet;
                query = unsignedByte + "." + query;
            }
            InetAddress inetAddress = InetAddress.getByName(query);
            Log.v("ChkSpam", inetAddress.toString());
            return inetAddress.isLoopbackAddress(); //using isLoopbackAddress() to judge spammer in order to prevent DNS pollution.
            //return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static String getResult(String arg) throws IllegalArgumentException {
        if (arg.isEmpty())
            throw new IllegalArgumentException();
        if (SpamCheck.isSpammer(arg)) {
            return arg + " is a known spammer.";
        } else {
            return arg + " appears legitimate.";
        }
    }
}
