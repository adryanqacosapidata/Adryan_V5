package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Labor;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;

/**
 * Created by root on 21/03/14.
 */
public class DBAdapterLabor {
    // campos de la tabla
    public static final String ROW_CODIGO = "_id";
    public static final String ROW_DESCRIPCION = "descripcion";
    public static final String ROW_ACTIVIDAD = "actividad";
    public static final String ROW_CENTROCOSTO = "centrocosto";
    public static final String ROW_COMPANIA = "compania";
    public static final String DATABASE_TABLE = "labor";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterLabor(Context context){
        this.contexto = context;
        logger =  new LogFile(context);
    }

    public DBAdapterLabor open() throws SQLException {
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    // método para el llenado de los campos y sus valores
    private ContentValues crearValores(Labor labor){
        ContentValues valores = new ContentValues();
        valores.put(ROW_CODIGO, labor.getId());
        valores.put(ROW_DESCRIPCION, labor.getDescripcion());
        valores.put(ROW_ACTIVIDAD, labor.getActividad());
        valores.put(ROW_CENTROCOSTO, labor.getCentrocosto());
        valores.put(ROW_COMPANIA, labor.getCompania());
        return valores;
    }

    public boolean nuevaLabor(Labor labor){
        boolean out = false;
        ContentValues valoresIniciales = crearValores(labor);

        try {
            out = (db.insert(DATABASE_TABLE, null, valoresIniciales) > 0);
        } catch (Exception e) {
            logger.addRecordLog("nuevaLabor(Labor): " + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    public boolean deleteLabores() {
        boolean out = false;

        try {
            out = (db.delete(DATABASE_TABLE, null, null) > 0);
        }catch (Exception e) {
            logger.addRecordLog("deleteLabores(): " + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    // listar todos las labores
    public Cursor listaLabores(){
        Cursor cDatos = null;
        try {
            cDatos = db.query(DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION, ROW_ACTIVIDAD, ROW_CENTROCOSTO, ROW_COMPANIA
            }, null, null, null, null, null);
        } catch (Exception ex){
            logger.addRecordLog("listaLabores() :" + ex.toString());
        }
        return cDatos;
    }

    // datos una labor por su codigo
    public Cursor datosLabor(String codigo){
        Cursor cDatos = null;
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION, ROW_ACTIVIDAD, ROW_CENTROCOSTO, ROW_COMPANIA
            }, ROW_CODIGO + "='" + codigo + "'", null, null, null, null, null);
            if (cDatos != null) {
                cDatos.moveToFirst();
            } else {
                cDatos = null;
            }
        } catch (Exception ex){
            logger.addRecordLog("datosLabor(String) :" + ex.toString());
        }
        return cDatos;
    }

    public Cursor getDatosFiltro(String cc, String act, String cia){
        Cursor cDatos = null;
        String where = ROW_CENTROCOSTO + " = '" + cc + "' AND "
                + ROW_ACTIVIDAD + " = '" + act + "' AND " + ROW_COMPANIA + " = '" + cia + "'";
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION, ROW_ACTIVIDAD, ROW_CENTROCOSTO, ROW_COMPANIA
            }, where, null, null, null, null, null);
            if (cDatos != null) {
                cDatos.moveToFirst();
            } else {
                cDatos = null;
            }
        } catch (Exception ex){
            logger.addRecordLog("getDatosFiltro(String, String, String) :" + ex.toString());
        }
        return cDatos;
    }

    public Cursor getDatosFiltro(String filtro, String cc, String act, String cia){
        Cursor cDatos = null;
        String where = ROW_CODIGO + " in (" + filtro + ") AND " + ROW_CENTROCOSTO + " = '" + cc + "' AND "
                + ROW_ACTIVIDAD + " = '" + act + "' AND " + ROW_COMPANIA + " = '" + cia + "'";
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION, ROW_ACTIVIDAD, ROW_CENTROCOSTO, ROW_COMPANIA
            }, where, null, null, null, null, null);
            if (cDatos != null) {
                cDatos.moveToFirst();
            } else {
                cDatos = null;
            }
        } catch (Exception ex){
            logger.addRecordLog("getDatosFiltro(String, String, String, String) :" + ex.toString());
        }
        return cDatos;
    }
}
