package com.example.testblueapp.database;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.example.testblueapp.util.AppPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// ENREGISTE LES DONNÉES DANS UN FICHIER TEXTE DANS LE DOSSIER INTERNE DE L'APPLICATION
// EXAMPLE /storage/emulated/0/Android/data/com.example.testblueapp/files/Easyclaim/data_for_easyclaim.txt
public class DataRecorder {
    private final Activity activity;
    private final AppPermission appPermission; // Adding AppPermission instance

    public DataRecorder(Activity activity) {
        this.activity = activity;
        this.appPermission = new AppPermission(activity); // Initializing AppPermission
        createDirectory();
    }
    private String getDirectoryPath() {
        // Utilisez getExternalFilesDir pour supporter Android 10+ en respectant la politique de stockage
        return this.activity.getExternalFilesDir(null) + "/Easyclaim";
    }
    public void createDirectory() {
        if (appPermission.isExternalStorageWritable()) {
            if (appPermission.checkStoragePermission()) {
                File directory = new File(getDirectoryPath());
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        Log.d("DataRecorder", "Directory created successfully. init");
                    } else {
                        Log.e("DataRecorder", "Failed to create directory init");
                    }
                }
            } else {
                appPermission.requestStoragePermission();
            }
        } else {
            Log.e("DataRecorder", "External storage not writable.");
        }
    }

    public void saveDataToFile(String fileName, String data) {
        createDirectory();

        File file = new File(getDirectoryPath(), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(data.getBytes());
            fos.write("\n".getBytes());
            fos.close();

            Log.i("DataRecorder", "Data saved to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearData(String fileName) {
        File file = new File(getDirectoryPath(), fileName);

        if (file.exists()) {
            file.delete();
            Log.i("DataRecorder", "Data file deleted.");
        }
    }
}





/*
 public class DataRecorder {
 private Context context;

 public DataRecorder(Context context) {
 this.context = context;
 }
 // Méthode pour enregistrer les données dans un fichier texte
 public static void saveDataToFile(String data) {
 // Vérifier si le stockage externe est disponible en écriture
 String state = Environment.getExternalStorageState();
 if (Environment.MEDIA_MOUNTED.equals(state)) {
 // Répertoire où le fichier sera enregistré
 File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
 if (!dir.exists()) {
 dir.mkdirs(); // Créer le répertoire s'il n'existe pas
 }

 // Nom du fichier
 File file = new File(dir, "data_for_esayclaim.txt");

 try {
 // Vérifier si le fichier existe déjà
 if (!file.exists()) {
 // Créer le fichier s'il n'existe pas
 file.createNewFile();
 }

 // Ouvrir un flux de sortie vers le fichier
 FileOutputStream fos = new FileOutputStream(file, true);
 fos.write(data.getBytes());
 fos.write("\n".getBytes()); // Ajouter un saut de ligne pour séparer les données
 fos.close(); // Fermer le flux de sortie

 // Afficher un message de succès
 Log.i("DataRecorder", "Data saved to file: " + file.getAbsolutePath());
 } catch (IOException e) {
 e.printStackTrace();
 }

 } else {
 Log.e("DataRecorder", "External storage not available for writing.");
 }
 }


 // Méthode pour effacer les données enregistrées
 public void clearData() {
 // Vérifier si le stockage externe est disponible en écriture
 String state = Environment.getExternalStorageState();
 if (Environment.MEDIA_MOUNTED.equals(state)) {
 // Répertoire où le fichier est enregistré
 File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

 // Nom du fichier
 File file = new File(dir, "data_for_esayclaim.txt");
 if (file.exists()) {
 file.delete(); // Supprimer le fichier
 Log.i("DataRecorder", "Data file deleted.");
 }
 } else {
 Log.e("DataRecorder", "External storage not available for writing.");
 }
 }

 }


 */