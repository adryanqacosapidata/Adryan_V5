package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Distribucion;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;

/**
 * Created by vquispe on 27/08/2014.
 */
public class DBAdapterDistribucion {
    // campos de la tabla
    private static final String ROW_COMPANIA = "compania";
    private static final String ROW_PERIODO = "periodo";
    private static final String ROW_SUPERVISOR = "supervisor";
    private static final String ROW_CENTROCOSTO = "centrocosto";
    private static final String ROW_ACTIVIDAD = "actividad";
    private static final String ROW_LABOR = "labor";
    private static final String ROW_FECHA = "fecha";
    private static final String DATABASE_TABLE = "distribucion";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterDistribucion(Context context){
        this.contexto = context;
        logger = new LogFile(context);
    }

    public DBAdapterDistribucion open() throws SQLException {
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues crearValores(Distribucion dist){
        ContentValues content = new ContentValues();
        content.put(ROW_SUPERVISOR, dist.getSupervisor().trim());
        content.put(ROW_CENTROCOSTO, dist.getCentroCosto().trim());
        content.put(ROW_ACTIVIDAD, dist.getActividad().trim());
        content.put(ROW_LABOR, dist.getLabor().trim());
        content.put(ROW_PERIODO, dist.getPeriodo().trim());
        content.put(ROW_COMPANIA, dist.getCompania().trim());
        content.put(ROW_FECHA, dist.getFecha().trim());
        return content;
    }

    public boolean insertDistribucion(Distribucion dist) {
        boolean out = false;

        try {
            out = (db.insert(DATABASE_TABLE, null, crearValores(dist)) > 0);
        } catch (Exception e) {
            logger.addRecordLog("insertDistribucion(Distribucion): " + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    public boolean deleteDistribucion(){
        boolean out = false;

        try {
            out = (db.delete(DATABASE_TABLE, null, null) > 0);
        } catch (Exception e) {
            logger.addRecordLog("deleteDistribucion(): " + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    public Distribucion getDistriucion(String sup, String cia, String periodo){
        Distribucion dist =  new Distribucion();
        String where = ROW_SUPERVISOR + "='" + sup + "' AND " + ROW_COMPANIA + "='" + cia + "' AND " +
                ROW_PERIODO + "=" + periodo;
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_SUPERVISOR, ROW_CENTROCOSTO, ROW_ACTIVIDAD, ROW_LABOR, ROW_PERIODO, ROW_COMPANIA
        }, where, null, null, null, null, null);
        int ct = 0;
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                dist.setSupervisor(cDatos.getString(0));
                dist.setCentroCosto(cDatos.getString(1));
                dist.setActividad(cDatos.getString(2));
                dist.setLabor(cDatos.getString(3));
                dist.setPeriodo(cDatos.getString(4));
                dist.setCompania(cDatos.getString(5));
                ct++;
            }
            dist.setCount(ct);
        }
        return dist;
    }

    public String getCC(String sup, String cia, String periodo){
        String result = "";
        String where = "trim(" + ROW_SUPERVISOR + ")=trim('" + sup + "') AND trim(" + ROW_COMPANIA + ")=trim('" + cia + "') AND trim(" +
                ROW_PERIODO + ")=trim('" + periodo + "')";
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{ ROW_CENTROCOSTO },where ,null,null,null,null,null);
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                result += "'" + cDatos.getString(0) + "',";
            }
        }
        return ((result.length() > 0) ? result.substring(0,result.length() - 1) : "");
    }

    public String getAct(String sup, String cia, String periodo){
        String result = "";
        String where = ROW_SUPERVISOR + "='" + sup + "' AND " + ROW_COMPANIA + "='" + cia + "' AND " +
                ROW_PERIODO + "=" + periodo;
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{ ROW_ACTIVIDAD },where ,null,null,null,null,null);
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                result += "'" + cDatos.getString(0) + "',";
            }
        }
        return ((result.length() > 0) ? result.substring(0,result.length() - 1) : "");
    }

    public String getLabor(String sup, String cia, String periodo){
        String result = "";
        String where = ROW_SUPERVISOR + "='" + sup + "' AND " + ROW_COMPANIA + "='" + cia + "' AND " +
                ROW_PERIODO + "=" + periodo;
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{ ROW_LABOR },where ,null,null,null,null,null);
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                result += "'" + cDatos.getString(0) + "',";
            }
        }
        return ((result.length() > 0) ? result.substring(0,result.length() - 1) : "");
    }
}
