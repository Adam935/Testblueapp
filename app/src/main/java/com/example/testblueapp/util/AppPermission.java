package com.example.testblueapp.util;
import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.testblueapp.MainActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AppPermission {
    private static final int REQUEST_BT_PERMISSION = 101;
    public static final int REQUEST_LOCATION_PERMISSION = 102;
    public static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 103;
    public static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 104;
    private static final int REQUEST_STORAGE_CODE_PERMISSION = 200;
    private static final int REQUEST_CHECK_SETTINGS_LOCATION = 1000;
    private static final int REQUEST_BACKGROUND_LOCATION_PERMISSION = 2000;
    private final Activity mActivity;
    private boolean isRequestingPermission = false;

    public AppPermission(Activity activity) {
        this.mActivity = activity;
    }

    /***************
     Storage Permissions
     ***************/
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean checkStoragePermission() {
        int result = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermission() {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_CODE_PERMISSION);
    }

    /***************
     Runtime Permissions
     ***************/

    public boolean hasRequiredRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return checkBluetoothScanPermission() &&
                    checkBluetoothConnectPermission() && checkBackgroundLocationPermission();
        } else {
            return checkLocationPermission();
        }
    }

    public void requestRelevantRuntimePermissions() {
        if (hasRequiredRuntimePermissions() || isRequestingPermission) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            isRequestingPermission = true;
            requestLocationPermission();
            isRequestingPermission = false;
        } else {
            isRequestingPermission = true;
            requestBluetoothScanPermission();
            requestBackgroundLocationPermission();
            isRequestingPermission = false;

        }
    }


    /***************
     Bluetooth Permissions
     ***************/
    public boolean checkBluetoothPermission() {
        return mActivity.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestBluetoothPermission() {
        mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BT_PERMISSION);
    }

    /****************
     Location Permissions
     ****************/

    public void requestLocationSettings() {
        // Créez une demande de localisation
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Créez une demande de paramètres de localisation
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // Vérifiez si les paramètres de localisation actuels sont satisfaits
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(mActivity)
                .checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
                // Les paramètres de localisation sont satisfaits. L'application peut démarrer la localisation.
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Les paramètres de localisation ne sont pas satisfaits, mais cela peut être corrigé
                        // en montrant à l'utilisateur une boîte de dialogue.
                        try {
                            // Affichez la boîte de dialogue en appelant startResolutionForResult(),
                            // et vérifiez le résultat dans onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(mActivity,
                                    REQUEST_CHECK_SETTINGS_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignorez l'erreur.
                        } catch (ClassCastException e) {
                            // Ignorez, ne devrait jamais se produire.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Les paramètres de localisation ne sont pas satisfaits. Cependant, nous n'avons aucune façon de les résoudre.
                        // Aucune boîte de dialogue ne sera affichée.
                        break;
                }
            }
        });
    }
    public boolean checkLocationPermission() {
        // we always request permissions if the call is made, without checking the SDK version
        return mActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission() {
        // We always request permissions if the call is made, without checking the SDK version
        mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }

    /***************************
     Background Location Permission
     (Android 11 and above)
     ***************************/
    public boolean checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return mActivity.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_BACKGROUND_LOCATION_PERMISSION);
        }
    }

    /***************************
     Bluetooth Connect Permission
     (Android 12 and above)
     ***************************/
    public boolean checkBluetoothConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return mActivity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void requestBluetoothConnectPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT_PERMISSION);
        }
    }

    /***********************
     Bluetooth Scan Permission
     (Android 12 and above)
     ***********************/
    public boolean checkBluetoothScanPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return mActivity.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void requestBluetoothScanPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mActivity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN_PERMISSION);
        }
    }

    // Handle permission request responses

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        isRequestingPermission = false;
        if (grantResults.length > 0) {
            handlePermissionResult(requestCode, permissions[0], grantResults[0]);
        } else {
            Log.e("AppPermission", "No grantResults received.");
        }
    }

    private void handlePermissionResult(int requestCode, String permission, int grantResult) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mActivity, permission + " permission was granted", Toast.LENGTH_SHORT).show();
            if (requestCode == REQUEST_BT_PERMISSION) {
                ((MainActivity)mActivity).startBleScan(); // Start BLE scan after permission is granted
            }
        } else {
            Toast.makeText(mActivity, permission + " permission was denied", Toast.LENGTH_SHORT).show();
        }
    }
        }

