package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.CentroCosto;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;

/**
 * Created by root on 20/03/14.
 */
public class DBAdapterCentroCosto {
    // campos de la tabla
    public static final String ROW_CODIGO = "_id";
    public static final String ROW_DESCRIPCION = "descripcion";
    public static final String ROW_COMPANIA = "compania";
    public static final String DATABASE_TABLE = "centro_costo";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterCentroCosto(Context context){
        this.contexto = context;
        logger =  new LogFile(context);
    }

    public DBAdapterCentroCosto open() throws SQLException{
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
    private ContentValues crearValores(CentroCosto cc){
        ContentValues valores = new ContentValues();
        valores.put(ROW_CODIGO, cc.getId());
        valores.put(ROW_DESCRIPCION, cc.getDescripcion());
        valores.put(ROW_COMPANIA, cc.getCompania());
        return valores;
    }

    // eliminar todos los centro de costos
    public boolean deleteCentrosCostos(){
        boolean out = false;

        try {
            out = (db.delete(DATABASE_TABLE, null, null) > 0);
        } catch (Exception e) {
            logger.addRecordLog("deleteCentrosCostos(): " + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    public boolean nuevoCentroCosto(CentroCosto cc){
        boolean out = false;
        ContentValues valoresIniciales = crearValores(cc);

        try {
            out = (db.insert(DATABASE_TABLE, null, valoresIniciales) > 0);
        } catch (Exception e) {
            logger.addRecordLog("nuevoCentroCosto(CentroCosto): " + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    // listar todos los centro de Costo
    public Cursor listaCentrosCosto(){
        Cursor cDatos = null;
        try {
            cDatos = db.query(DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION, ROW_COMPANIA
            }, null, null, null, null, null);
        } catch (Exception ex){
            logger.addRecordLog("listaCentrosCosto() :" + ex.toString());
            ex.printStackTrace();
        }
        return cDatos;
    }

    // datos un supervisor por su codigo
    public Cursor datosCentroCosto(String codigo){
        Cursor cDatos = null;
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION
            }, ROW_CODIGO + "='" + codigo + "'", null, null, null, null, null);
            if (cDatos != null) {
                cDatos.moveToFirst();
            }
        } catch (Exception ex){
            logger.addRecordLog("datosCentroCosto(String) :" + ex.toString());
        }
        return cDatos;
    }

    public Cursor filtroCC(String cia){
        Cursor cDatos = null;
        String where = ROW_COMPANIA + " = '" + cia + "'";
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION
            }, where, null, null, null, null, null);
            if (cDatos != null) {
                cDatos.moveToFirst();
            }
        } catch (Exception ex){
            logger.addRecordLog("filtroCC(String) :" + ex.toString());
        }
        return cDatos;
    }

    public Cursor filtroCC(String filtro, String cia){
        Cursor cDatos = null;
        String where = ROW_CODIGO + " in (" + filtro + ") AND " + ROW_COMPANIA + " = '" + cia + "'";
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_CODIGO, ROW_DESCRIPCION
            }, where, null, null, null, null, null);
            if (cDatos != null) {
                cDatos.moveToFirst();
            }
        } catch (Exception ex){
            logger.addRecordLog("filtroCC(String, String) :" + ex.toString());
        }
        return cDatos;
    }
}
