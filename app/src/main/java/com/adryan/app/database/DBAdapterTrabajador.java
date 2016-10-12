package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Trabajador;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by VFQS on 20/03/14.
 */
public class DBAdapterTrabajador {
    // campos de la base de datos
    public static final String ROW_CODIGOUNICO = "codigounico";
    public static final String ROW_MATRICULA = "matricula";
    public static final String ROW_NOMBRES = "nombres";
    public static final String ROW_FOTOCHECK = "fotocheck";
    public static final String ROW_COMPANIA = "compania";
    public static final String ROW_DOCUMENTO = "documento";
    public static final String DATABASE_TABLE = "trabajador";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterTrabajador(Context context){
        this.contexto = context;
        logger = new LogFile(context);
    }

    public DBAdapterTrabajador open() throws SQLException{
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues insertValues(Trabajador trab){
        ContentValues valores = new ContentValues();
        valores.put(ROW_CODIGOUNICO, trab.getCodigoUnico());
        valores.put(ROW_MATRICULA, trab.getMatricula());
        valores.put(ROW_FOTOCHECK, trab.getFotocheck());
        valores.put(ROW_NOMBRES, trab.getNombre());
        valores.put(ROW_COMPANIA, trab.getCompania());
        valores.put(ROW_DOCUMENTO, trab.getDocumento());
        return valores;
    }

    public boolean nuevoTrabajador(Trabajador trab){
       boolean out = false;
        try {
            ContentValues valores = insertValues(trab);
            out = (db.insert(DATABASE_TABLE, null, valores) > 0);
        } catch (Exception e) {
            logger.addRecordLog("nuevoTrabajador(Trabajador): " + e.toString());
            e.printStackTrace();
        }
        return out;
    }

    public boolean deleteTodosTrabajadores(){
        boolean out = false;
        try {
            out = (db.delete(DATABASE_TABLE, null, null) > 0);
        } catch (Exception e) {
            logger.addRecordLog("deleteTodosTrabajadores(): " + e.toString());
            e.printStackTrace();
        }
        return out;
    }

    public Trabajador getByFotocheckOrDocumento(String fotocheck, String documento) {
        Trabajador trab = new Trabajador();
        String where = ROW_FOTOCHECK + "='" + fotocheck + "' OR " + ROW_DOCUMENTO + "='" + documento + "'";
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_CODIGOUNICO, ROW_MATRICULA, ROW_NOMBRES, ROW_FOTOCHECK, ROW_COMPANIA, ROW_DOCUMENTO
        }, where, null, null, null, null, null);

        if (cDatos != null) {
            int  i = 0;
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                trab.setCodigoUnico(cDatos.getString(0));
                trab.setMatricula(cDatos.getString(1));
                trab.setNombre(cDatos.getString(2));
                trab.setFotocheck(cDatos.getString(3));
                trab.setCompania(cDatos.getString(4));
                trab.setDocumento(cDatos.getString(5));
                i++;
            }
            trab.setCount(i);
        }
        return trab;
    }

    public Trabajador getByFotocheck(String fotocheck, String cia) {
        Trabajador trab = new Trabajador();
        String where = " (trim(" + ROW_FOTOCHECK + ")=trim('" + fotocheck + "') OR trim(" +
                ROW_DOCUMENTO + ")=trim('" + fotocheck + "')) AND trim(" + ROW_COMPANIA +") = trim('"+ cia + "') ";
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_CODIGOUNICO, ROW_MATRICULA, ROW_NOMBRES, ROW_DOCUMENTO, ROW_COMPANIA
        }, where, null, null, null, null, null);
        //logger.addRecordLog("SQLWHERE->getByFotocheck(): " + where);
        if (cDatos != null) {
            int  i = 0;
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                trab.setCodigoUnico(cDatos.getString(0));
                trab.setMatricula(cDatos.getString(1));
                trab.setNombre(cDatos.getString(2));
                trab.setDocumento(cDatos.getString(3));
                trab.setCompania(cDatos.getString(4));
                i++;
            }
            trab.setCount(i);
        }
        return trab;
    }

    public ArrayList<Trabajador> getByMatriculaFotocheck(String matricula, String fotocheck) {
        ArrayList<Trabajador> lstTarb = new ArrayList<Trabajador>();
        Trabajador trab = new Trabajador();
        String where = "trim(" + ROW_MATRICULA + ")='" + matricula + "' AND trim(" + ROW_FOTOCHECK + ")='" + fotocheck + "'";
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_CODIGOUNICO, ROW_MATRICULA, ROW_NOMBRES, ROW_FOTOCHECK, ROW_COMPANIA
        }, where, null, null, null, null, null);
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                trab.setCodigoUnico(cDatos.getString(0));
                trab.setMatricula(cDatos.getString(1));
                trab.setNombre(cDatos.getString(2));
                trab.setDocumento(cDatos.getString(3));
                trab.setCompania(cDatos.getString(4));
                lstTarb.add(trab);
            }
        }
        return lstTarb;
    }

    public Trabajador getByCodigoUnico(String codigo) {
        Trabajador trab = new Trabajador();
        String where = "trim(" + ROW_CODIGOUNICO + ")='" + codigo + "'";
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_CODIGOUNICO, ROW_MATRICULA, ROW_NOMBRES, ROW_FOTOCHECK, ROW_COMPANIA
        }, where, null, null, null, null, null);
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                trab.setCodigoUnico(cDatos.getString(0));
                trab.setMatricula(cDatos.getString(1));
                trab.setNombre(cDatos.getString(2));
                trab.setDocumento(cDatos.getString(3));
                trab.setCompania(cDatos.getString(4));
            }
        }
        return trab;
    }

    public Trabajador getTrabajador(String codigo, String cia) {
        Trabajador trab = new Trabajador();
        String where = "trim(" + ROW_CODIGOUNICO + ")='" + codigo + "' And trim("+ ROW_COMPANIA + ")='" + cia + "'";
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_CODIGOUNICO, ROW_MATRICULA, ROW_NOMBRES, ROW_FOTOCHECK, ROW_COMPANIA
        }, where, null, null, null, null, null);
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                trab.setCodigoUnico(cDatos.getString(0));
                trab.setMatricula(cDatos.getString(1));
                trab.setNombre(cDatos.getString(2));
                trab.setDocumento(cDatos.getString(3));
                trab.setCompania(cDatos.getString(4));
            }
        }
        return trab;
    }
}
