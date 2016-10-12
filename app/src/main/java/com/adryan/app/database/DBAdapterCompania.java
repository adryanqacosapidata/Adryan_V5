package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Compania;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by vquispe on 27/08/2014.
 */
public class DBAdapterCompania {
    // campos de la tabla
    public static final String ROW_ID = "_id";
    public static final String ROW_DESCRIPCION = "descripcion";
    public static final String DATABASE_TABLE = "compania";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterCompania(Context context){
        this.contexto = context;
        logger =  new LogFile(context);
    }

    public DBAdapterCompania open() throws SQLException {
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues crearValores(Compania cia){
        ContentValues content = new ContentValues();
        content.put(ROW_ID, cia.getId());
        content.put(ROW_DESCRIPCION, cia.getDescripcion());
        return content;
    }

    public boolean insertCia(Compania cia) {
        boolean out = false;
        try {
            out = (db.insert(DATABASE_TABLE, null, crearValores(cia)) > 0);
        } catch (Exception e) {
            logger.addRecordLog("insertCia(Compania): " + e.toString());
        }
        return out;
    }

    public boolean deleteCia(){
        boolean out = false;
        try {
            out = (db.delete(DATABASE_TABLE, null, null) > 0);
        } catch (Exception e) {
            logger.addRecordLog("deleteCia(): " + e.toString());
        }
        return out;
    }

    public Cursor getCompaniasLst(){
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_ID, ROW_DESCRIPCION
        }, null, null, null, null, null, null);
        if (cDatos != null) {
            cDatos.moveToFirst();
        }
        return cDatos;
    }

    public ArrayList<Compania> getCompanias(){
        ArrayList<Compania> lstcia = new ArrayList<Compania>();
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_ID, ROW_DESCRIPCION
        }, null, null, null, null, null, null);
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                Compania cia =  new Compania();
                cia.setId(cDatos.getString(0));
                cia.setDescripcion(cDatos.getString(1));
                lstcia.add(cia);
            }
        }
        return lstcia;
    }
}
