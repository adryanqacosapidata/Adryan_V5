package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Asignacion;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by vquispe on 27/08/2014.
 */
public class DBAdapterAsignacion {
    // campos de la tabla
    private static final String ROW_MAC = "mac";
    private static final String ROW_TABLET = "tablet";
    private static final String ROW_SUPERVISOR = "supervisor";
    private static final String ROW_PERIODO = "periodo";
    private static final String ROW_COMPANIA = "compania";
    private static final String DATABASE_TABLE = "asignacion";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterAsignacion(Context context){
        this.contexto = context;
        logger =  new LogFile(context);
    }

    public DBAdapterAsignacion open() throws SQLException {
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues crearValores(Asignacion asi){
        ContentValues content = new ContentValues();
        content.put(ROW_MAC, asi.getMac());
        content.put(ROW_TABLET, asi.getTablet());
        content.put(ROW_SUPERVISOR, asi.getSupervisor());
        content.put(ROW_PERIODO, asi.getPeriodo());
        content.put(ROW_COMPANIA, asi.getCompania());
        return content;
    }

    public boolean insertAsignacion(Asignacion asi) {
        boolean out = false;

        try {
            out = (db.insert(DATABASE_TABLE, null, crearValores(asi)) > 0);
        } catch (Exception e) {
            logger.addRecordLog("insertAsignacion(Asignacion)" + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    public boolean deleteAsignacion(){
        boolean out = false;

        try {
            out = (db.delete(DATABASE_TABLE, null, null) > 0);
        } catch (Exception e) {
            logger.addRecordLog("deleteAsignacion()" + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    public Asignacion getAsignacionByMac(String mac, String cia, String periodo){
        Asignacion asig =  new Asignacion();
        String where = ROW_MAC + "='" + mac + "' AND " + ROW_COMPANIA + "='" + cia + "' AND " +
                ROW_PERIODO + "=" + periodo;
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_MAC, ROW_TABLET, ROW_SUPERVISOR, ROW_PERIODO, ROW_COMPANIA
        }, where, null, null, null, null, null);
        int ct = 0;
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                asig.setMac(cDatos.getString(0));
                asig.setTablet(cDatos.getString(1));
                asig.setSupervisor(cDatos.getString(2));
                asig.setPeriodo(cDatos.getString(3));
                asig.setCompania(cDatos.getString(4));
                ct++;
            }
        }
        asig.setCount(ct);
        return asig;
    }
}
