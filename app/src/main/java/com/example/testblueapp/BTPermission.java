package com.example.testblueapp;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

public class BTPermission {
    private static final int REQUEST_BT_PERMISSION = 101;
    public static final int REQUEST_LOCATION_PERMISSION = 102;
    public static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 103;
    public static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 104;
    private Activity mActivity;

    public BTPermission(Activity activity) {
        this.mActivity = activity;
    }

    // Check Bluetooth scan permission
    public boolean checkBluetoothScanPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            int permissionCheck = mActivity.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN);
            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
    // Request Bluetooth scan permission
    public void requestBluetoothScanPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN_PERMISSION);
        }
    }

    // Vérifier les autorisations Bluetooth
    public boolean checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = mActivity.checkSelfPermission(Manifest.permission.BLUETOOTH);
            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // Vérifier les autorisations de localisation
    public boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int coarsePermissionCheck = mActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int finePermissionCheck = mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            return coarsePermissionCheck == PackageManager.PERMISSION_GRANTED &&
                    finePermissionCheck == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // Request Bluetooth connect permission
    public void requestBluetoothConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
        }
    }

    // Check Bluetooth connect permission
    public boolean checkBluetoothConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            int permissionCheck = mActivity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT);
            return permissionCheck == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // Demander les autorisations Bluetooth
    public void requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BT_PERMISSION);
        }
    }

    // Demander les autorisations de localisation
    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    // Gérer la réponse de demande d'autorisation
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_BT_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Bluetooth accordée
                } else {
                    // Permission Bluetooth refusée
                    Toast.makeText(mActivity, "Permission Bluetooth refusée", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission de localisation accordée
                } else {
                    // Permission de localisation refusée
                    Toast.makeText(mActivity, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_BLUETOOTH_CONNECT_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth connect permission was granted
                } else {
                    // Bluetooth connect permission was denied
                    Toast.makeText(mActivity, "Bluetooth connect permission was denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_BLUETOOTH_SCAN_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth scan permission was granted
                } else {
                    // Bluetooth scan permission was denied
                    Toast.makeText(mActivity, "Bluetooth scan permission was denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}







/// pour le BLE
/*
        private void startScan() {
        Log.d("ConnectionDeviceActivity", "startScan: Attempting to start scan...");
        if (btPermission.checkBluetoothPermission()) {
            Log.d("ConnectionDeviceActivity", "startScan: Bluetooth permissions granted.");
            if (!btPermission.checkLocationPermission()) {
                Log.d("ConnectionDeviceActivity", "startScan: Location permissions not granted. Requesting permissions...");
                btPermission.requestLocationPermission();
            } else {
                Log.d("ConnectionDeviceActivity", "startScan: Location permissions granted.");
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    Log.d("ConnectionDeviceActivity", "startScan: Bluetooth is enabled. Starting BLE scan...");
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                        Log.d("ConnectionDeviceActivity", "startScan: Cancelling ongoing discovery...");
                    }
                    scannedDevicesAdapter.clear();
                    scannedDevicesList.clear();

                    // Get BluetoothLeScanner
                    BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

                    // Start BLE scan
                    bluetoothLeScanner.startScan(new ScanCallback() {
                        @Override
                        public void onScanResult(int callbackType, ScanResult result) {
                            super.onScanResult(callbackType, result);
                            if (btPermission.checkBluetoothPermission()) {
                                Log.d("ConnectionDeviceActivity", "onScanResult: Bluetooth permissions granted.");
                                BluetoothDevice device = result.getDevice();
                                Log.d("ConnectionDeviceActivity", "Device found: " + device.getName() + " - " + device.getAddress());
                                if (!scannedDevicesList.contains(device)) {
                                    scannedDevicesList.add(device);
                                    scannedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                                    scannedDevicesAdapter.notifyDataSetChanged(); // Notify the adapter of changes
                                }
                            } else {
                                btPermission.requestBluetoothPermission();
                            }
                        }

                        @Override
                        public void onScanFailed(int errorCode) {
                            super.onScanFailed(errorCode);
                            Log.d("ConnectionDeviceActivity", "BLE Scan Failed with error code: " + errorCode);
                        }
                    });

                    Log.d("ConnectionDeviceActivity", "startScan: BLE Scan started");

                    // Display a Toast to inform that the discovery has started
                    Toast.makeText(getApplicationContext(), "BLE Scan started", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ConnectionDeviceActivity", "startScan: Bluetooth is not enabled or supported.");
                    // Bluetooth is not enabled or supported
                    // Handle this case if needed
                }
            }
        } else {
            Log.d("ConnectionDeviceActivity", "startScan: Bluetooth permissions not granted. Requesting permissions...");
            btPermission.requestBluetoothPermission();
        }
    }
 */