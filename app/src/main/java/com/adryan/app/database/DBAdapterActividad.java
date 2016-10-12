package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.adryan.app.Entidades.Actividad;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;

/**
 * Created by root on 20/03/14.
 */
public class DBAdapterActividad {
    // campos de la tabla
    public static final String ROW_CODIGO = "_id";
    public static final String ROW_DESCRIPCION = "descripcion";
    public static final String ROW_CENTROCOSTO = "centrocosto";
    public static final String ROW_COMPANIA = "compania";
    public static final String DATABASE_TABLE = "actividad";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterActividad(Context context){
        this.contexto = context;
        logger =  new LogFile(context);
    }

    public DBAdapterActividad open() throws SQLException {
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
    private ContentValues crearValores(Actividad act){
        ContentValues valores = new ContentValues();
        valores.put(ROW_CODIGO, act.getId());
        valores.put(ROW_DESCRIPCION, act.getDescripcion());
        valores.put(ROW_CENTROCOSTO, act.getCentroCosto());
        valores.put(ROW_COMPANIA, act.getCompania());
        return valores;
    }

    // eliminar todos las actividades
    public boolean deleteActividades(){
        boolean out = false;

        try {
            out = (db.delete(DATABASE_TABLE, null, null) > 0);
        } catch (Exception e) {
            logger.addRecordLog("deleteActividades(): " + e.toString());
            e.printStackTrace();
        }
        
        return out;
    }

    public boolean nuevaActividad(Actividad act){
        boolean out = false;
        ContentValues valoresIniciales = crearValores(act);

        try {
            out = (db.insert(DATABASE_TABLE, null, valoresIniciales) > 0);
        } catch (Exception e) {
            logger.addRecordLog("nuevaActividad(Actividad): " + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    // listar todos las actividades
    public Cursor listaActividades(){
        Cursor cDatos = null;
        try {
            cDatos = db.query(DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION
            }, null, null, null, null, null);
        }catch (Exception ex){
            logger.addRecordLog("listaActividades() :" + ex.toString());
        }
        return cDatos;
    }

    // datos una actividad por su codigo
    public Cursor datosActividad(String codigo){
        Cursor cDatos = null;
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION
            }, ROW_CODIGO + "='" + codigo + "'", null, null, null, null, null);
            if (cDatos != null) {
                cDatos.moveToFirst();
            }
        } catch (Exception ex){
            Log.e("Mensaje de error", ex.toString());
            logger.addRecordLog("datosActividad(String) :" + ex.toString());
        }
        return cDatos;
    }

    public Cursor getDatosFiltro(String cc, String cia){
        Cursor cDatos = null;
        String where = ROW_CENTROCOSTO + " = '" + cc + "' AND "
                + ROW_COMPANIA + " = '" + cia + "'";
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION
            }, where, null, null, null, null, null);
            if (cDatos != null) {
                cDatos.moveToFirst();
            }
        } catch (Exception ex){
            Log.e("Mensaje de error", ex.toString());
            logger.addRecordLog("getDatosFiltro(String, String) :" + ex.toString());
        }
        return cDatos;
    }

    public Cursor getDatosFiltro(String filtro, String cc, String cia){
        Cursor cDatos = null;
        String where = ROW_CODIGO + " in (" + filtro + ") AND " + ROW_CENTROCOSTO + " = '" + cc + "' AND "
                + ROW_COMPANIA + " = '" + cia + "'";
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION
            }, where, null, null, null, null, null);
            if (cDatos != null) {
                cDatos.moveToFirst();
            }
        } catch (Exception ex){
            Log.e("Mensaje de error", ex.toString());
            logger.addRecordLog("getDatosFiltro(String, String , String) :" + ex.toString());
        }
        return cDatos;
    }
}
