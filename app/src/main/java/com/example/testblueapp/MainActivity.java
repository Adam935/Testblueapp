package com.example.testblueapp;


import com.example.testblueapp.database.HexToAsciiConverter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import com.example.testblueapp.ble.BleExtensions;
import com.example.testblueapp.ble.UUIDs;
import com.example.testblueapp.database.DataRecorder;


public class MainActivity extends AppCompatActivity {


    DataRecorder dataRecorder = new DataRecorder(MainActivity.this);
    private static final int ENABLE_BLUETOOTH_REQUEST_CODE = 1;
    private static final int RUNTIME_PERMISSION_REQUEST_CODE = 2;



    private ScanSettings scanSettings;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private Button scan_button;
    private RecyclerView scan_results_recycler_view;
    private ScanResultAdapter scanResultAdapter;
    private List<ScanResult> scanResults = new ArrayList<>();
    // Déclarez une HashMap pour stocker les adresses MAC des périphériques déjà découverts
    private HashMap<String, ScanResult> discoveredDevices = new HashMap<>();

    int transport = BluetoothDevice.TRANSPORT_LE; // Utilisation du transport BLE
    int phy = BluetoothDevice.PHY_LE_1M; // Utilisation du PHY LE à 1M

    private BluetoothGatt bluetoothGatt;

    // Créer une file d'attente pour les caractéristiques à lire
    //LinkedList<UUID> readQueue = new LinkedList<>();
    LinkedList<UUID> readQueue = UUIDs.initializeCharacteristicQueue();
    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Créer le répertoire "Easyclaim"
        //dataRecorder.createEasyclaimDirectory();

        // Récupérer le bouton de numérisation
        scan_button = findViewById(R.id.scan_button);
        scan_results_recycler_view = findViewById(R.id.scan_results_recycler_view);




        scan_button.setOnClickListener(v -> {
            if (isScanning) {
                stopBleScan();
            } else {
                startBleScan();
            }
        });


        // Initialisation de l'adaptateur
        setupAdapter();
        setupRecyclerView();

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (bluetoothAdapter != null) {
            bleScanner = getBleScanner();
        }

        scanSettings = createScanSettings();

    }

    // Dans la méthode setupAdapter() de votre MainActivity
    private void setupAdapter() {
        scanResultAdapter = new ScanResultAdapter(scanResults, new ScanResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ScanResult result) {
                // Utilisateur a tapé sur un résultat de scan
                if (isScanning) {
                    stopBleScan();
                }
                BluetoothDevice device = result.getDevice();
                Log.w("ScanResultAdapter", "Connecting to " + device.getAddress());
                // Connecter à GATT Server hébergé par ce périphérique
                BluetoothGatt bluetoothGatt = device.connectGatt(MainActivity.this, false, gattCallback, transport, phy);

                // Appeler printGattTable

                printGattTable(bluetoothGatt);
            }
        });
    }
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        scan_results_recycler_view.setLayoutManager(layoutManager);
        scan_results_recycler_view.setAdapter(scanResultAdapter);
        scan_results_recycler_view.setNestedScrollingEnabled(false);

        RecyclerView.ItemAnimator itemAnimator = scan_results_recycler_view.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
    }


    private void startBleScan() {
        // Vider la liste des périphériques découverts
        discoveredDevices.clear();
        scanResults.clear();

        Log.d("errora", "Starting BLE scan...");
        if (!hasRequiredRuntimePermissions()) {
            requestRelevantRuntimePermissions();
        } else {
            runOnUiThread(() -> {
                scanResults.clear();
                if (scanResultAdapter != null) {
                    scanResultAdapter.notifyDataSetChanged();
                }
                Log.d("errora", "test thread ble scan");
            });
            // Check if Bluetooth is enabled
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
                return;
            }
            // Check if device supports BLE
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Log.d("errora", "BLE not supported");
                Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
                return;
            }
            // Check if BluetoothAdapter and BluetoothLeScanner are initialized
            if (bleScanner == null) {
                Log.d("errora", "Initializing BluetoothLeScanner...");
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                if (bluetoothManager != null) {
                    bluetoothAdapter = bluetoothManager.getAdapter();
                    if (bluetoothAdapter != null) {
                        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
                    }
                }
                Log.d("errora", "BluetoothLeScanner initialized.");
            }
            // Check if BluetoothLeScanner is not null before starting the scan
            if (bleScanner != null) {
                Log.d("errora", "Starting BLE scan... after check");
                bleScanner.startScan(null, scanSettings, scanCallback);
                isScanning = true;
                setIsScanning(true);
            } else {
                Log.e("MainActivity", "Unable to initialize BluetoothLeScanner.");
            }
        }
    }


    private void stopBleScan() {
        Log.d("errora", "Stopping BLE scan...");
        bleScanner.stopScan(scanCallback);
        setIsScanning(false);
    }

    private void setIsScanning(boolean value) {
        isScanning = value;
        runOnUiThread(() -> scan_button.setText(value ? "Stop Scan" : "Start Scan"));
    }


    private boolean hasPermission(String permissionType) {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasRequiredRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                    hasPermission(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void requestRelevantRuntimePermissions() {
        if (hasRequiredRuntimePermissions()) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            requestLocationPermission();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestBluetoothPermissions();
        }
    }

    private void requestLocationPermission() {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("Location permission required")
                .setMessage("Starting from Android M (6.0), the system requires apps to be granted location access in order to scan for BLE devices.")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RUNTIME_PERMISSION_REQUEST_CODE))
                .show());
    }

    private void requestBluetoothPermissions() {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("Bluetooth permissions required")
                .setMessage("Starting from Android 12, the system requires apps to be granted Bluetooth access in order to scan for and connect to BLE devices.")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, RUNTIME_PERMISSION_REQUEST_CODE))
                .show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RUNTIME_PERMISSION_REQUEST_CODE) {
            boolean containsPermanentDenial = false;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED &&
                        !ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    containsPermanentDenial = true;
                    break;
                }
            }
            boolean containsDenial = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    containsDenial = true;
                    break;
                }
            }
            boolean allGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (containsPermanentDenial) {
                // TODO: Handle permanent denial (e.g., show AlertDialog with justification)
                // Note: The user will need to navigate to App Settings and manually grant
                // permissions that were permanently denied
            } else if (containsDenial) {
                requestRelevantRuntimePermissions();
            } else if (allGranted && hasRequiredRuntimePermissions()) {
                startBleScan();
            } else {
                // Unexpected scenario encountered when handling permissions
                recreate();
            }
        }
    }

    private ScanSettings createScanSettings() {
        return new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
    }

    private BluetoothLeScanner getBleScanner() {
        return bluetoothAdapter.getBluetoothLeScanner();
    }



    /**
     * Espace Callback
     */

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result != null && result.getDevice() != null) {
                BluetoothDevice device = result.getDevice();
                String deviceAddress = device.getAddress();
                int indexQuery = -1;
                for (int i = 0; i < scanResults.size(); i++) {
                    if (scanResults.get(i) != null && scanResults.get(i).getDevice() != null) {
                        if (scanResults.get(i).getDevice().getAddress().equals(deviceAddress)) {
                            indexQuery = i;
                            break;
                        }
                    } else {
                        Log.e("MainActivity", "ScanResult ou BluetoothDevice est null à l'index " + i);
                    }
                }
                if (indexQuery != -1) {
                    // Un résultat de scan existe déjà avec la même adresse
                    scanResults.set(indexQuery, result);
                    if (scanResultAdapter != null) {
                        scanResultAdapter.notifyItemChanged(indexQuery);
                    }
                } else {
                    // Vérifiez si le périphérique a déjà été découvert
                    if (!discoveredDevices.containsKey(deviceAddress)) {
                        // Le périphérique n'a pas encore été découvert, ajoutez-le à la liste des résultats
                        discoveredDevices.put(deviceAddress, result);
                        scanResults.add(result);
                        if (scanResultAdapter != null) {
                            scanResultAdapter.notifyItemInserted(scanResults.size() - 1);
                        }
                    } else {
                        // Le périphérique a déjà été découvert, ne faites rien
                    }
                }
            } else {
                Log.e("MainActivity", "ScanResult ou BluetoothDevice est null.");
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("ScanCallback", "onScanFailed: code " + errorCode);
        }
    };



    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String deviceAddress = gatt.getDevice().getAddress();

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully connected to " + deviceAddress);
                    // Clear the data recorder
                    dataRecorder.clearData();

                    // Discover services
                    bluetoothGatt = gatt;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            bluetoothGatt.discoverServices();
                        }
                    });
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully disconnected from " + deviceAddress);
                    gatt.close();
                }
            } else {
                Log.w("BluetoothGattCallback", "Error " + status + " encountered for " + deviceAddress + "! Disconnecting...");
                gatt.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.w("BluetoothGattCallback", "Discovered " + services.size() + " services for " + gatt.getDevice().getAddress());
            printGattTable(gatt); // See implementation just above this section
            // Consider connection setup as complete here

            // Appeler la méthode readNextCharacteristic pour lire la première caractéristique et lancez la lecture des autres caractéristiques.
            gatt.readCharacteristic(BleExtensions.findCharacteristic(gatt, UUIDs.BATTERY_LEVEL_CHAR));
        }
        @Override
        // Utiliser la table de correspondance dans la fonction onCharacteristicRead
        // pour afficher les valeurs de caractéristiques lues
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String uuid = characteristic.getUuid().toString();
            String characteristicName = UUIDs.characteristicNames.get(characteristic.getUuid()); // Utilisez directement la table de correspondance

            if (characteristicName != null) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    byte[] value = characteristic.getValue();
                    if (value != null && value.length > 0) {
                        StringBuilder stringBuilder = new StringBuilder(value.length * 2);
                        for (byte byteChar : value) {
                            stringBuilder.append(String.format("%02X", byteChar));
                        }
                        String hexValue = stringBuilder.toString();
                        Log.i("BluetoothGattCallback", "Read characteristic " + characteristicName + ":\n" + hexValue);

                        // Convertir la valeur hexadécimale en ASCII
                        String asciiValue = HexToAsciiConverter.hexToAscii(hexValue);

                        // Récupérer les données de la caractéristique
                        String data = characteristicName +", Valeur : " + asciiValue;
                        // Appeler la méthode pour enregistrer les données dans un fichier
                        dataRecorder.saveDataToFile(data);

                    } else {
                        Log.e("BluetoothGattCallback", "Empty characteristic value for " + characteristicName + "!");
                    }
                } else if (status == BluetoothGatt.GATT_READ_NOT_PERMITTED) {
                    Log.e("BluetoothGattCallback", "Read not permitted for " + characteristicName + "!");
                } else {
                    Log.e("BluetoothGattCallback", "Characteristic read failed for " + characteristicName + ", error: " + status);
                }
            } else {
                Log.e("BluetoothGattCallback", "Unknown characteristic with UUID: " + uuid);
            }

            // After reading a characteristic, remove it from the queue and read the next one
            readQueue.poll();
            readNextCharacteristic(gatt);
        }
        private void readNextCharacteristic(BluetoothGatt gatt) {
            if (!readQueue.isEmpty()) {
                UUID charUuid = readQueue.peek();
                BluetoothGattCharacteristic characteristic = BleExtensions.findCharacteristic(gatt, charUuid);
                if (characteristic != null && BleExtensions.isReadable(characteristic)) {
                    gatt.readCharacteristic(characteristic);
                } else {
                    readQueue.poll(); // Remove the characteristic from the queue
                    readNextCharacteristic(gatt); // Try to read the next characteristic
                }
            }
        }

    };

    private void printGattTable(BluetoothGatt gatt) {
        if (gatt == null || gatt.getServices() == null || gatt.getServices().isEmpty()) {
            Log.i("printGattTable", "No service and characteristic available, call discoverServices() first?");
            return;
        }

        for (BluetoothGattService service : gatt.getServices()) {
            StringBuilder characteristicsTable = new StringBuilder();
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                characteristicsTable.append("|--").append(characteristic.getUuid().toString()).append("\n");
            }
            Log.i("printGattTable", "\nService " + service.getUuid() + "\nCharacteristics:\n" + characteristicsTable.toString());
        }
    }

}
