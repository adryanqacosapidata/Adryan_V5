package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.adryan.app.Entidades.Tareador;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;

/**
 * Created by VFQS on 20/03/14.
 */
public class DBAdapterSupervisor {
    // campos de la base de datos
    public static final String ROW_CODIGOUNICO = "codigounico";
    public static final String ROW_COMPANIA = "compania";
    public static final String ROW_MATRICULA = "matricula";
    public static final String ROW_NOMBRES = "nombres";
    public static final String ROW_DOCUMENTO = "documento";
    public static final String ROW_USUARIO = "usuario";
    public static final String DATABASE_TABLE = "supervisor";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterSupervisor(Context context){
        this.contexto = context;
        logger =  new LogFile(context);
    }

    public DBAdapterSupervisor open() throws SQLException {
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
    private ContentValues crearValores(Tareador sup){
        ContentValues valores = new ContentValues();
        valores.put(ROW_CODIGOUNICO, sup.getCodigoUnico());
        valores.put(ROW_COMPANIA, sup.getCompania());
        valores.put(ROW_MATRICULA, sup.getMatricula());
        valores.put(ROW_NOMBRES, sup.getNombre());
        valores.put(ROW_DOCUMENTO, sup.getDocumento());
        valores.put(ROW_USUARIO, sup.getUsuario());
        return valores;
    }

    // insertar un nuevo supervisor
    public boolean nuevoSupervisor(Tareador sup){
        boolean out = false;
        try {
            ContentValues valoresIniciales = crearValores(sup);
            out = (db.insert(DATABASE_TABLE, null, valoresIniciales) > 0);
        } catch (Exception e) {
            logger.addRecordLog("nuevoSupervisor(Tareador): " + e.toString());
            e.printStackTrace();
        }
        return out;
    }

    public boolean deleteSupervisores() {
        boolean out = false;

        try {
            out = (db.delete(DATABASE_TABLE, null, null) >= 0);
        } catch (Exception e) {
            logger.addRecordLog("deleteSupervisores()" + e.toString());
            e.printStackTrace();
        }

        return out;
    }

    public Cursor getSupConfig(String idTablet, String periodo, String cc, String act, String lab){
        Cursor cDatos = null;
        String sql = "select distinct b.codigounico, b.matricula, b.nombres, a.centrocosto, c.descripcion," +
                " a.actividad ,d.descripcion, a.labor, e.descripcion, f.descripcion as 'periodo'," +
                " g.descripcion as 'compania', h.mac, h.tablet from config a" +
                " inner join supervisor b on a.supervisor = b.codigounico and a.compania = b.compania" +
                " inner join centro_costo c on a.centrocosto = c._id and a.compania = c.compania" +
                " inner join actividad d on a.actividad = d._id and a.compania = d.compania" +
                " inner join labor e on a.labor = e._id and a.compania = e.compania" +
                " inner join periodo f on a.periodo = f._id and a.compania = f.compania" +
                " inner join compania g on a.compania = g._id" +
                " inner join asignacion h on a.supervisor = h.supervisor and a.periodo = h.periodo" +
                " and a.compania = h.compania" +
                " where a.tablet='" + idTablet + "' and a.periodo='" + periodo +"' and a.centrocosto='" +
                cc + "' and a.actividad='" + act + "' and labor='" + lab + "';";
        cDatos = db.rawQuery(sql,null);
        try {
            if (cDatos != null) {
                cDatos.moveToFirst();
            }
        } catch (Exception ex){
            Log.e("prueba : ", ex.getMessage());
            logger.addRecordLog("getSupConfig() :" + ex.toString());
        }
        return cDatos;
    }

    public Tareador getDatos(String codigo){
        Tareador tar = new Tareador();
        String where = ROW_CODIGOUNICO+ "='" + codigo + "'";
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_CODIGOUNICO, ROW_MATRICULA, ROW_NOMBRES, ROW_DOCUMENTO,
                ROW_COMPANIA
        },where ,null,null,null,null,null);
        int ct = 0;
        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                tar.setCodigoUnico(cDatos.getString(0));
                tar.setMatricula(cDatos.getString(1));
                tar.setNombre(cDatos.getString(2));
                tar.setDocumento(cDatos.getString(3));
                tar.setCompania(cDatos.getString(4));
                ct++;
            }
            tar.setCount(ct);
        }
        return tar;
    }

}
