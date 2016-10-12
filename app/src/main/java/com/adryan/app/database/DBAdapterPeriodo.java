package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Periodo;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;

/**
 * Created by vquispe on 20/08/2014.
 */
public class DBAdapterPeriodo {
    // campos de la tabla
    public static final String ROW_ID = "_id";
    public static final String ROW_DESCRIPCION = "descripcion";
    public static final String ROW_COMPANIA = "compania";
    public static final String DATABASE_TABLE = "periodo";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterPeriodo(Context context){
        this.contexto = context;
        logger = new LogFile(context);
    }

    public DBAdapterPeriodo open() throws SQLException {
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues crearValores(Periodo per){
        ContentValues content = new ContentValues();
        content.put(ROW_ID, per.getId());
        content.put(ROW_DESCRIPCION, per.getDescripcion());
        content.put(ROW_COMPANIA, per.getCompania());
        return content;
    }

    public boolean insertPeriodo(Periodo per) {
        boolean out = false;

        try {
            out = (db.insert(DATABASE_TABLE, null, crearValores(per)) > 0);
        } catch (Exception e) {
            logger.addRecordLog("insertPeriodo(Periodo): " + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    public boolean deletePeriodos(){
        boolean out = false;

        try {
            out = (db.delete(DATABASE_TABLE, null, null) > 0);
        } catch (Exception e) {
            logger.addRecordLog("deletePeriodos(): " + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    public Periodo getPeriodoActual(String cia){
        Periodo per =  new Periodo();
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_ID, ROW_DESCRIPCION, ROW_COMPANIA
        }, ROW_COMPANIA + "='" + cia + "'", null, null, null, null, null);
        int ct = 0;
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                per.setId(cDatos.getString(0));
                per.setDescripcion(cDatos.getString(1));
                per.setCompania(cDatos.getString(2));
                ct++;
            }
            per.setCount(ct);
        }
        return per;
    }
}
