package slawomir.qstra.ble.bluetooth.bluetooth;

import java.nio.charset.StandardCharsets;

public class BluetoothUtils {

    public static String bytesToHex(byte[] bytes) {
       return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] hexStringToByteArray(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
