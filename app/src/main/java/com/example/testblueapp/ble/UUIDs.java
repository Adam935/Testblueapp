package com.example.testblueapp.ble;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class UUIDs {
    // UUID des caractéristiques
    public static final UUID BATTERY_LEVEL_CHAR = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    public static final UUID BATTERY_STATUS_CHAR = UUID.fromString("00002bea-0000-1000-8000-00805f9b34fb");
    public static final UUID TESTNAME_SPECIAL_ONE = UUID.fromString("00005678-0000-1000-8000-00805f9b34fb");

    public static LinkedList<UUID> initializeCharacteristicQueue() {
        LinkedList<UUID> readQueue = new LinkedList<>();
        readQueue.add(BATTERY_LEVEL_CHAR);
        readQueue.add(BATTERY_STATUS_CHAR);
        readQueue.add(TESTNAME_SPECIAL_ONE);
        return readQueue;
    }


    // Table de correspondance entre les UUID et les noms de caractéristiques
    public static final HashMap<UUID, String> characteristicNames = new HashMap<>();

    static {
        // Ajouter les UUID et les noms de caractéristiques à la table de correspondance
        characteristicNames.put(BATTERY_LEVEL_CHAR, "Battery Health Information");
        characteristicNames.put(BATTERY_STATUS_CHAR, "Battery Health Status");
        characteristicNames.put(TESTNAME_SPECIAL_ONE, "Test Name Special One");
    }
}
