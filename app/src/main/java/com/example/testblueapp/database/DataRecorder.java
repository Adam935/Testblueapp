package com.example.testblueapp.database;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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






/*
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DataRecorder {

    private Context context;

    public DataRecorder(Context context) {
        this.context = context;
    }

    public void createEasyclaimDirectory() {
        File directory = new File(context.getFilesDir(), "Easyclaim");

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Log.d("DataRecorder", "Directory created successfully");
            } else {
                Log.e("DataRecorder", "Failed to create directory");
            }
        } else {
            Log.d("DataRecorder", "Directory already exists");
        }
    }

    // Méthode pour enregistrer les données dans un fichier texte
    public void saveDataToFile(String data) {
        // Vérifier si le répertoire "Easyclaim" existe
        createEasyclaimDirectory();

        // Répertoire où le fichier sera enregistré
        File dir = new File(context.getFilesDir(), "Easyclaim");

        // Nom du fichier
        File file = new File(dir, "data.txt");

        try {
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
    }

    // Méthode pour effacer les données enregistrées
    public void clearData() {
        // Répertoire où le fichier est enregistré
        File dir = new File(context.getFilesDir(), "Easyclaim");

        // Nom du fichier
        File file = new File(dir, "data.txt");
        if (file.exists()) {
            file.delete(); // Supprimer le fichier
            Log.i("DataRecorder", "Data file deleted.");
        }
    }
}
*/

