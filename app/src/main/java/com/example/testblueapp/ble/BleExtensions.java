package com.example.testblueapp.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import java.util.Locale;
import java.util.UUID;

public class BleExtensions {

    /** UUID du descripteur de configuration caractéristique client (0x2902). */
    public static final String CCC_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805F9B34FB";

    // BluetoothGatt

    public static void printGattTable(BluetoothGatt gatt) {
        if (gatt.getServices().isEmpty()) {
            Log.i("BLEUtils", "No service and characteristic available, call discoverServices() first?");
            return;
        }
        for (android.bluetooth.BluetoothGattService service : gatt.getServices()) {
            StringBuilder characteristicsTable = new StringBuilder();
            for (BluetoothGattCharacteristic charac : service.getCharacteristics()) {
                String description = charac.getUuid() + ": " + printProperties(charac);
                if (!charac.getDescriptors().isEmpty()) {
                    for (BluetoothGattDescriptor descriptor : charac.getDescriptors()) {
                        description += "\n" + descriptor.getUuid() + ": " + printProperties(descriptor);
                    }
                }
                characteristicsTable.append("|--").append(description).append("\n");
            }
            Log.i("BLEUtils", "Service " + service.getUuid() + "\nCharacteristics:\n" + characteristicsTable.toString());
        }
    }

    public static BluetoothGattCharacteristic findCharacteristic(BluetoothGatt gatt, UUID uuid) {
        for (android.bluetooth.BluetoothGattService service : gatt.getServices()) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (characteristic.getUuid().equals(uuid)) {
                    return characteristic;
                }
            }
        }
        return null;
    }

    public static BluetoothGattDescriptor findDescriptor(BluetoothGatt gatt, UUID uuid) {
        for (android.bluetooth.BluetoothGattService service : gatt.getServices()) {
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    if (descriptor.getUuid().equals(uuid)) {
                        return descriptor;
                    }
                }
            }
        }
        return null;
    }

    // BluetoothGattCharacteristic

    public static String printProperties(BluetoothGattCharacteristic characteristic) {
        StringBuilder properties = new StringBuilder();
        if (isReadable(characteristic)) properties.append("READABLE ");
        if (isWritable(characteristic)) properties.append("WRITABLE ");
        if (isWritableWithoutResponse(characteristic)) properties.append("WRITABLE WITHOUT RESPONSE ");
        if (isIndicatable(characteristic)) properties.append("INDICATABLE ");
        if (isNotifiable(characteristic)) properties.append("NOTIFIABLE ");
        //if (isEmpty(characteristic)) properties.append("EMPTY ");
        return properties.toString();
    }

    public static boolean isReadable(BluetoothGattCharacteristic characteristic) {
        return containsProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_READ);
    }

    public static boolean isWritable(BluetoothGattCharacteristic characteristic) {
        return containsProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE);
    }

    public static boolean isWritableWithoutResponse(BluetoothGattCharacteristic characteristic) {
        return containsProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);
    }

    public static boolean isIndicatable(BluetoothGattCharacteristic characteristic) {
        return containsProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_INDICATE);
    }

    public static boolean isNotifiable(BluetoothGattCharacteristic characteristic) {
        return containsProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY);
    }

    public static boolean isEmpty(BluetoothGattCharacteristic characteristic) {
        return characteristic.getValue() == null || characteristic.getValue().length == 0;
    }

    public static boolean containsProperty(BluetoothGattCharacteristic characteristic, int property) {
        return (characteristic.getProperties() & property) != 0;
    }

    // BluetoothGattDescriptor

    public static String printProperties(BluetoothGattDescriptor descriptor) {
        StringBuilder properties = new StringBuilder();
        if (isReadable(descriptor)) properties.append("READABLE ");
        if (isWritable(descriptor)) properties.append("WRITABLE ");
        //if (isEmpty(descriptor)) properties.append("EMPTY ");
        return properties.toString();
    }

    public static boolean isReadable(BluetoothGattDescriptor descriptor) {
        return containsPermission(descriptor, BluetoothGattDescriptor.PERMISSION_READ);
    }

    public static boolean isWritable(BluetoothGattDescriptor descriptor) {
        return containsPermission(descriptor, BluetoothGattDescriptor.PERMISSION_WRITE);
    }

    public static boolean isEmpty(BluetoothGattDescriptor descriptor) {
        return descriptor.getValue() == null || descriptor.getValue().length == 0;
    }

    public static boolean containsPermission(BluetoothGattDescriptor descriptor, int permission) {
        return (descriptor.getPermissions() & permission) != 0;
    }

    /**
     * Fonction d'extension de commodité qui retourne true si ce {@link BluetoothGattDescriptor}
     * est un descripteur de configuration de caractéristique client.
     */
    public static boolean isCccd(BluetoothGattDescriptor descriptor) {
        return descriptor.getUuid().toString().toUpperCase(Locale.US).equals(CCC_DESCRIPTOR_UUID.toUpperCase(Locale.US));
    }


}
