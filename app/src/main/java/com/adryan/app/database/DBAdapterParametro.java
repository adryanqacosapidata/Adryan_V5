package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Parametro;
import com.adryan.app.Entidades.Periodo;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;

/**
 * Created by vquispe on 20/08/2014.
 */
public class DBAdapterParametro {
    // campos de la tabla
    public static final String ROW_ID = "_id";
    public static final String ROW_NOMBRE = "nombre";
    public static final String ROW_VALOR = "valor";
    public static final String DATABASE_TABLE = "parametro";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterParametro(Context context){
        this.contexto = context;
        logger = new LogFile(context);
    }

    public DBAdapterParametro open() throws SQLException {
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues crearValores(String valor) {
        ContentValues content = new ContentValues();
        content.put(ROW_VALOR, valor);
        return content;
    }

    public boolean updParametro(String id, String valor) {
        return db.update(DATABASE_TABLE, crearValores(valor),("_id='" + id.trim() + "' "),null) > 0;
    }

    public String getValorParam(String id) {
        String valor = "";
        Cursor cDatos;
        try {
            cDatos = db.query(true, DATABASE_TABLE, new String[]{
                    ROW_ID, ROW_NOMBRE, ROW_VALOR
            }, "_id=" + id, null, null, null, null, null);
            if (cDatos != null) {
                for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                    valor = cDatos.getString(2);
                }
            } else {
                cDatos = null;
            }
        } catch (Exception ex){
            logger.addRecordLog("getValorParam() :" + ex.toString());
        }
        return valor;
    }

}
