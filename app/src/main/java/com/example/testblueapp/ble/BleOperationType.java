package com.example.testblueapp.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.UUID;

/** Classe abstraite scellée représentant un type d'opération BLE */
public abstract class BleOperationType {
    public abstract BluetoothDevice getDevice();
}

/** Se connecter à [device] et effectuer une découverte de service */
class Connect extends BleOperationType {
    private final BluetoothDevice device;
    private final Context context;

    public Connect(BluetoothDevice device, Context context) {
        this.device = device;
        this.context = context;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }
}

/** Se déconnecter de [device] et libérer toutes les ressources de connexion */
class Disconnect extends BleOperationType {
    private final BluetoothDevice device;

    public Disconnect(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }
}

/** Écrire [payload] comme valeur d'une caractéristique représentée par [characteristicUuid] */
class CharacteristicWrite extends BleOperationType {
    private final BluetoothDevice device;
    private final UUID characteristicUuid;
    private final int writeType;
    private final byte[] payload;

    public CharacteristicWrite(BluetoothDevice device, UUID characteristicUuid, int writeType, byte[] payload) {
        this.device = device;
        this.characteristicUuid = characteristicUuid;
        this.writeType = writeType;
        this.payload = payload;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }
}

/** Lire la valeur d'une caractéristique représentée par [characteristicUuid] */
class CharacteristicRead extends BleOperationType {
    private final BluetoothDevice device;
    private final UUID characteristicUuid;

    public CharacteristicRead(BluetoothDevice device, UUID characteristicUuid) {
        this.device = device;
        this.characteristicUuid = characteristicUuid;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }
}

/** Écrire [payload] comme valeur d'un descripteur représenté par [descriptorUuid] */
class DescriptorWrite extends BleOperationType {
    private final BluetoothDevice device;
    private final UUID descriptorUuid;
    private final byte[] payload;

    public DescriptorWrite(BluetoothDevice device, UUID descriptorUuid, byte[] payload) {
        this.device = device;
        this.descriptorUuid = descriptorUuid;
        this.payload = payload;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }
}

/** Lire la valeur d'un descripteur représenté par [descriptorUuid] */
class DescriptorRead extends BleOperationType {
    private final BluetoothDevice device;
    private final UUID descriptorUuid;

    public DescriptorRead(BluetoothDevice device, UUID descriptorUuid) {
        this.device = device;
        this.descriptorUuid = descriptorUuid;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }
}

/** Activer les notifications/indications sur une caractéristique représentée par [characteristicUuid] */
class EnableNotifications extends BleOperationType {
    private final BluetoothDevice device;
    private final UUID characteristicUuid;

    public EnableNotifications(BluetoothDevice device, UUID characteristicUuid) {
        this.device = device;
        this.characteristicUuid = characteristicUuid;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }
}

/** Désactiver les notifications/indications sur une caractéristique représentée par [characteristicUuid] */
class DisableNotifications extends BleOperationType {
    private final BluetoothDevice device;
    private final UUID characteristicUuid;

    public DisableNotifications(BluetoothDevice device, UUID characteristicUuid) {
        this.device = device;
        this.characteristicUuid = characteristicUuid;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }
}

/** Demande pour un MTU de [mtu] */
class MtuRequest extends BleOperationType {
    private final BluetoothDevice device;
    private final int mtu;

    public MtuRequest(BluetoothDevice device, int mtu) {
        this.device = device;
        this.mtu = mtu;
    }

    @Override
    public BluetoothDevice getDevice() {
        return device;
    }
}
