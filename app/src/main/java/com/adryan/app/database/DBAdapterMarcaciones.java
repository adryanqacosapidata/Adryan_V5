package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.adryan.app.Entidades.Marcaciones;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by VFQS on 04/04/14.
 */
public class DBAdapterMarcaciones {
    //campos de la tabla
    public static final String ROW_ID = "_id";
    public static final String ROW_SUPERVISOR = "supervisor";
    public static final String ROW_PERIODO = "periodo";
    public static final String ROW_CENTROCOSTO = "centrocosto";
    public static final String ROW_ACTIVIDAD = "actividad";
    public static final String ROW_LABOR = "labor";
    public static final String ROW_TRABAJADOR = "trabajador";
    public static final String ROW_COMPANIA = "compania";
    public static final String ROW_FECHA = "fecha";
    public static final String ROW_HORA = "hora";
    public static final String ROW_FLAG = "flagmanual";
    public static final String DATABASE_TABLE = "marcaciones";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterMarcaciones(Context context) {
        this.contexto = context;
        logger = new LogFile(context);
    }

    public DBAdapterMarcaciones open() throws SQLException {
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues valuesNuevaMarca(Marcaciones marca){
        ContentValues valores = new ContentValues();
        valores.put(ROW_CENTROCOSTO, marca.getCentroCosto().trim());
        valores.put(ROW_ACTIVIDAD, marca.getActividad().trim());
        valores.put(ROW_LABOR, marca.getLabor().trim());
        valores.put(ROW_TRABAJADOR, marca.getTrabajador().trim());
        valores.put(ROW_SUPERVISOR, marca.getSupervisor().trim());
        valores.put(ROW_COMPANIA, marca.getCompania().trim());
        valores.put(ROW_FECHA, marca.getFecha().trim());
        valores.put(ROW_HORA, marca.getHora().trim());
        valores.put(ROW_FLAG, marca.getFlagManual().trim());
        valores.put(ROW_PERIODO, marca.getPeriodo().trim());
        return valores;
    }

    public boolean nuevaMarca(Marcaciones marca) {
        boolean valor =  false;
        ContentValues valores = valuesNuevaMarca(marca);
        try {
            valor = (db.insert(DATABASE_TABLE, null, valores) > 0);
        } catch (Exception e) {
            logger.addRecordLog("NuevaMarca(Marcaciones): " + e.toString());
            e.printStackTrace();
        }
        return valor;
    }

    private ContentValues vUpdMarca(Marcaciones marca){
        ContentValues values = new ContentValues();
        values.put(ROW_FECHA, marca.getFecha());
        values.put(ROW_HORA, marca.getHora());
        values.put(ROW_FLAG, marca.getFlagManual());
        return values;
    }

    public boolean updateMarca(Marcaciones marca){
        String where = "";
        return (db.update(DATABASE_TABLE, vUpdMarca(marca), where, null) > 0);
    }

    public boolean deleteMarcas() {
        return db.delete(DATABASE_TABLE, null, null) >= 0;
    }

    public boolean deleteByMarca(Marcaciones marca) {
        boolean swValor = false;

        try {
            String where = ROW_CENTROCOSTO + "='" + marca.getCentroCosto().trim() + "' And " +
                    ROW_ACTIVIDAD + "='" + marca.getActividad().trim() + "' And " +
                    ROW_LABOR + "='" + marca.getLabor().trim() + "' And " +
                    ROW_TRABAJADOR + "='" + marca.getTrabajador().trim() + "' And " +
                    ROW_SUPERVISOR + "='" + marca.getSupervisor().trim() + "' And " +
                    ROW_COMPANIA + "='" + marca.getCompania().trim() + "' And " +
                    ROW_FECHA + "='" + marca.getFecha().trim() + "' And " +
                    ROW_HORA + "='" + marca.getHora().trim() + "' And " +
                    ROW_FLAG + "='" + marca.getFlagManual().trim() + "' And " +
                    ROW_PERIODO + "='" + marca.getPeriodo().trim() + "' ";

            swValor = (db.delete(DATABASE_TABLE, where, null) > 0);
        } catch (Exception e){
            logger.addRecordLog("deleteByMarca->" + e.toString());
            e.printStackTrace();
        }

        return  swValor;
    }

    public ArrayList<Marcaciones> listaMarcasActuales() {
        ArrayList<Marcaciones> lstMarcas = new ArrayList<Marcaciones>();
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_SUPERVISOR, ROW_PERIODO, ROW_CENTROCOSTO, ROW_ACTIVIDAD, ROW_LABOR,
                ROW_TRABAJADOR, ROW_COMPANIA, ROW_FECHA, ROW_HORA
        }, null, null, null, null, null, null);

        if (cDatos != null){
            for (cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                Marcaciones marca =  new Marcaciones();
                marca.setSupervisor(cDatos.getString(0).trim());
                marca.setPeriodo(cDatos.getString(1).trim());
                marca.setCentroCosto(cDatos.getString(2).trim());
                marca.setActividad(cDatos.getString(3).trim());
                marca.setLabor(cDatos.getString(4).trim());
                marca.setTrabajador(cDatos.getString(5).trim());
                marca.setCompania(cDatos.getString(6).trim());
                marca.setFecha(cDatos.getString(7).trim());
                marca.setHora(cDatos.getString(8).trim());
                lstMarcas.add(marca);
            }
        }

        return  lstMarcas;
    }

    public Cursor listaMarcaciones() throws SQLException {
        Cursor cDatos = null;
        String sql = "SELECT DISTINCT s.codigounico as 'supervisor', cc._id as 'centro_costo', " +
                "a._id as 'actividad', l._id as 'labor', t.codigounico as 'trabajador', " +
                "m.fecha as 'fecha', m.hora as 'hora'" +
                " FROM marcaciones m" +
                " JOIN trabajador t ON m.trabajador = t.codigounico" +
                " JOIN supervisor s ON m.supervisor = s.codigounico" +
                " JOIN centro_costo cc ON s.centrocosto = cc._id" +
                " JOIN actividad a ON s.actividad = a._id" +
                " JOIN labor l ON s.labor = l._id";
        cDatos = db.rawQuery(sql, null);
        if (cDatos != null) {
            cDatos.moveToFirst();
        }
        return cDatos;
    }

    public Cursor listaDetMarcaciones(String flag) throws SQLException {
        Cursor cDatos = null;
        String sql = "SELECT DISTINCT m._id as '_id', t.nombres as 'trabajador', s.nombres as 'supervisor'," +
                " l.descripcion as 'labor', (m.fecha || ' ' || m.hora) as 'fecha', t.matricula as 'matricula'," +
                " a.descripcion as 'actividad', t.codigounico, t.fotocheck, cc.descripcion as 'centrocosto'" +
                " FROM marcaciones m" +
                " JOIN trabajador t ON m.trabajador = t.codigounico" +
                " JOIN trabajador s ON m.supervisor = s.codigounico" +
                " LEFT JOIN centro_costo cc ON m.centrocosto = cc._id" +
                " LEFT JOIN actividad a ON m.actividad = a._id" +
                " LEFT JOIN labor l ON m.labor = l._id " +
                " WHERE trim(m.fecha) = trim(strftime('%d/%m/%Y', 'now'));";
        cDatos = db.rawQuery(sql, null);
        if (cDatos != null) {
            cDatos.moveToFirst();
        }
        return cDatos;
    }

    public Marcaciones getMarca(String id){
        Marcaciones marca = new Marcaciones();
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_ID, ROW_ACTIVIDAD, ROW_CENTROCOSTO, ROW_COMPANIA, ROW_FECHA, ROW_FLAG,
                ROW_HORA, ROW_LABOR, ROW_SUPERVISOR, ROW_TRABAJADOR, ROW_PERIODO
        }, ROW_ID + "=" + id, null, null, null, null, null);
        int ct = 0;
        if (cDatos != null){
            for (cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                marca.setId(cDatos.getInt(0));
                marca.setActividad(cDatos.getString(1).trim());
                marca.setCentroCosto(cDatos.getString(2).trim());
                marca.setCompania(cDatos.getString(3).trim());
                marca.setFecha(cDatos.getString(4).trim());
                marca.setFlagManual(cDatos.getString(5).trim());
                marca.setHora(cDatos.getString(6).trim());
                marca.setLabor(cDatos.getString(7).trim());
                marca.setSupervisor(cDatos.getString(8).trim());
                marca.setTrabajador(cDatos.getString(9).trim());
                marca.setPeriodo(cDatos.getString(10).trim());
                ct++;
            }
            marca.setCount(ct);
        }
        return marca;
    }

    public boolean isExist(Marcaciones marca){
        boolean sw = false;
        String sql = "select _id from marcaciones where trim(fecha)='" + marca.getFecha().trim() + "'" +
                " and trim(supervisor)='" + marca.getSupervisor().trim() + "' and trim(centrocosto)='" +
                marca.getCentroCosto().trim() + "' and trim(actividad)='" + marca.getActividad().trim() + "'" +
                " and trim(labor)='" + marca.getLabor().trim() + "' and trim(trabajador) = '" +
                marca.getTrabajador().trim() + "' and trim(compania) ='" + marca.getCompania().trim() + "'";
        Cursor cDatos = db.rawQuery(sql, null);
        int ct = 0;
        if (cDatos != null){
            for (cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                ct++;
            }
            if (ct > 0) sw = true;
        }
        return sw;
    }

}