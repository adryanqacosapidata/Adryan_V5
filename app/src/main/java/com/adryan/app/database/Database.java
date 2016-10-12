package com.adryan.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by vquispe on 04/11/2014.
 */
public class Database extends SQLiteOpenHelper {
    //Ruta por defecto de las bases de datos en el sistema Android
    private static String DB_PATH = "/";
    private static String DB_NAME = "Adryan.db";
    private static int DATABASE_VERSION = 1;
    private final Context myContext;

    /**
     * Constructor
     * Toma referencia hacia el contexto de la aplicación que lo invoca para poder acceder a los 'assets' y 'resources' de la aplicación.
     * Crea un objeto DBOpenHelper que nos permitirá controlar la apertura de la base de datos.
     * @param context
     */
    public Database(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    /**
     * Crea una base de datos vacía en el sistema y la reescribe con nuestro
     * fichero de base de datos.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (!dbExist) {
            // Llamando a este método se crea la base de datos vacía en la ruta
            // por defecto del sistema de nuestra aplicación
            // por lo que podremos sobreescribirla con
            // nuestra base de datos.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copiando Base de Datos");
            }
        }

    }

    /**
     * Comprueba si la base de datos existe para evitar copiar siempre el
     * fichero cada vez que se abra la aplicación.
     *
     * @return true si existe, false si no existe
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {

            String myPath = myContext.getExternalFilesDir(null).getAbsolutePath() + DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException e) {
            // si llegamos aqui es porque la base de datos no existe todavía.
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copia nuestra base de datos desde la carpeta assets a la recién creada
     * base de datos en la carpeta de sistema, desde dónde podremos acceder a
     * ella. Esto se hace con bytestream.
     * */
    private void copyDataBase() throws IOException {

        // Abrimos el fichero de base de datos como entrada
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Ruta a la base de datos vacía recién creada
        String outFileName = myContext.getExternalFilesDir(null).getAbsolutePath() + DB_PATH + DB_NAME;

        // Abrimos la base de datos vacía como salida
        OutputStream myOutput = new FileOutputStream(outFileName);

        // Transferimos los bytes desde el fichero de entrada al de salida
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Liberamos los streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    /**
     * Inicia el proceso de copia del fichero de base de datos, o crea una base
     * de datos vacía en su lugar
     * */
    public SQLiteDatabase getDataBase() {
        // Abre la base de datos
        try {
            createDataBase();
        } catch (IOException e) {
            throw new Error("Ha sido imposible crear la Base de Datos");
        }

        String myPath = myContext.getExternalFilesDir(null).getAbsolutePath() + DB_PATH + DB_NAME;
        return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS |
                SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
