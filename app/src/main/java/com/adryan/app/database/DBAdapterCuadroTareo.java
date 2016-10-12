package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Cuadro;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by vquispe on 29/08/2014.
 */
public class DBAdapterCuadroTareo {
    // campos de la tabla
    private static final String ROW_FECHA = "fecha";
    private static final String ROW_SUPERVISOR = "supervisor";
    private static final String ROW_CENTROCOSTO = "centrocosto";
    private static final String ROW_ACTIVIDAD = "actividad";
    private static final String ROW_LABOR = "labor";
    private static final String ROW_OBJETIVO = "objetivo";
    private static final String ROW_UNIDMED = "unidmed";
    private static final String ROW_NROPERS = "nropers";
    private static final String ROW_AVANCE = "avance";
    private static final String ROW_HORAS = "horas";
    private static final String ROW_RENDIMIENTO = "rendimiento";
    private static final String ROW_OBSERVACION = "observacion";
    private static final String ROW_COMPANIA = "compania";
    private static final String ROW_PERIODO = "periodo";
    private static final String DATABASE_TABLE = "cuadro";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    public DBAdapterCuadroTareo(Context context){
        this.contexto = context;
    }

    public DBAdapterCuadroTareo open() throws SQLException {
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues setValores (Cuadro cTareo){
        ContentValues valores =  new ContentValues();
        valores.put(ROW_FECHA, cTareo.getFecha().trim());
        valores.put(ROW_SUPERVISOR, cTareo.getSupervisor().trim());
        valores.put(ROW_CENTROCOSTO, cTareo.getCentroCosto().trim());
        valores.put(ROW_ACTIVIDAD, cTareo.getActividad().trim());
        valores.put(ROW_LABOR, cTareo.getLabor().trim());
        valores.put(ROW_OBJETIVO, cTareo.getObjetivo().trim());
        valores.put(ROW_UNIDMED, cTareo.getUnidMedida().trim());
        valores.put(ROW_NROPERS, cTareo.getNroPersonal().trim());
        valores.put(ROW_AVANCE, cTareo.getAvance().trim());
        valores.put(ROW_HORAS, cTareo.getHoras().trim());
        valores.put(ROW_RENDIMIENTO, cTareo.getRendimiento().trim());
        valores.put(ROW_OBSERVACION, cTareo.getObservacion().trim());
        valores.put(ROW_COMPANIA, cTareo.getCompania().trim());
        valores.put(ROW_PERIODO, cTareo.getPeriodo().trim());
        return valores;
    }

    public boolean addUpdCuadro(Cuadro cTareo){
        boolean sw;
        if (isExist(cTareo)){
            String where = "trim(" + ROW_FECHA + ")='" + cTareo.getFecha() + "' And" +
                    " trim(" + ROW_SUPERVISOR + ")='" + cTareo.getSupervisor() + "' And trim(" + ROW_CENTROCOSTO + ")='" +
                    cTareo.getCentroCosto() + "' And trim(" + ROW_ACTIVIDAD + ")='" + cTareo.getActividad() + "' And" +
                    " trim(" + ROW_LABOR + ")='" + cTareo.getLabor() + "'";
            sw = (db.update(DATABASE_TABLE, setValores(cTareo), where, null) > 0);
        } else {
            sw = (db.insert(DATABASE_TABLE, null, setValores(cTareo)) > 0);
        }
        return sw;
    }

    private boolean isExist(Cuadro cTareo){
        boolean sw = false;
        String sql = "Select fecha, supervisor From " + DATABASE_TABLE + " Where trim(" + ROW_FECHA + ")='" + cTareo.getFecha() + "' And" +
                " trim(" + ROW_SUPERVISOR + ")='" + cTareo.getSupervisor() + "' And trim(" + ROW_CENTROCOSTO + ")='" +
                cTareo.getCentroCosto() + "' And trim(" + ROW_ACTIVIDAD + ")='" + cTareo.getActividad() + "' And" +
                " trim(" + ROW_LABOR + ")='" + cTareo.getLabor() + "'";
        Cursor cDatos = db.rawQuery(sql, null);
        int ct = 0;
        if (cDatos != null){
            for (cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                ct++;
            }
        }
        if (ct > 0 ) sw = true;
        return  sw;
    }

    public boolean deleteCuadro() {
        return (db.delete(DATABASE_TABLE, null, null) > 0);
    }

    public Cuadro getCuadro(String fechaActual, String supervisor, String centroCosto, String actividad, String labor){
        Cuadro cuadro = new Cuadro();
        String sql = "select distinct a.objetivo, a.unidmed, a.avance," +
                " a.horas, a.rendimiento, a.observacion, a.nropers from cuadro a " +
                " where trim(a.fecha)=trim('" + fechaActual + "') And" +
                " trim(a.supervisor)=trim('" + supervisor + "') And trim(a.centrocosto)=trim('" +
                centroCosto + "') And trim(a.actividad)=trim('" + actividad + "') And" +
                " trim(a.labor)=trim('" + labor + "')";
        Cursor cDatos = db.rawQuery(sql,null);
        int ct = 0;
        if (cDatos != null){
            for (cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                cuadro.setObjetivo(cDatos.getString(0));
                cuadro.setUnidMedida(cDatos.getString(1));
                //cuadro.setNroPersonal(cDatos.getString(6));
                cuadro.setNroPersonal(getTotMarcasFiltro(supervisor, centroCosto, actividad, labor, fechaActual) + "");
                cuadro.setAvance(cDatos.getString(2));
                cuadro.setHoras(cDatos.getString(3));
                cuadro.setRendimiento(cDatos.getString(4));
                cuadro.setObservacion(cDatos.getString(5));
                ct++;
            }
            cuadro.setCount(ct);
        }
        return cuadro;
    }

    public int getTotMarcasFiltro(String sup, String cc, String act, String lab, String fec){
        int valor = 0;
        String sql = "Select _id From marcaciones Where trim(fecha) = trim('" + fec + "')" +
                " and trim(supervisor) = trim('" + sup + "')" +
                " and trim(centrocosto) = trim('" + cc + "')" +
                " and trim(actividad) = trim('" + act + "')" +
                " and trim(labor) = trim('" + lab + "')";
        Cursor cDatos = db.rawQuery(sql, null);
        if (cDatos != null){
            for (cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                valor++;
            }
        }
        return valor;
    }

    public ArrayList<Cuadro> listaCuadroActual() {
        ArrayList<Cuadro> lstCuadro = new ArrayList<Cuadro>();
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
            ROW_COMPANIA, ROW_PERIODO, ROW_SUPERVISOR, ROW_CENTROCOSTO, ROW_ACTIVIDAD,
            ROW_LABOR, ROW_FECHA, ROW_OBJETIVO, ROW_UNIDMED, ROW_NROPERS, ROW_AVANCE,
            ROW_HORAS, ROW_RENDIMIENTO, ROW_OBSERVACION
        }, null, null, null, null, null, null);

        if (cDatos != null) {
            for (cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                Cuadro cuadro =  new Cuadro();
                cuadro.setCompania(cDatos.getString(0).trim());
                cuadro.setPeriodo(cDatos.getString(1).trim());
                cuadro.setSupervisor(cDatos.getString(2).trim());
                cuadro.setCentroCosto(cDatos.getString(3).trim());
                cuadro.setActividad(cDatos.getString(4).trim());
                cuadro.setLabor(cDatos.getString(5).trim());
                cuadro.setFecha(cDatos.getString(6).trim());
                cuadro.setObjetivo(cDatos.getString(7).trim());
                cuadro.setUnidMedida(cDatos.getString(8).trim());
                cuadro.setNroPersonal(cDatos.getString(9).trim());
                cuadro.setAvance(cDatos.getString(10).trim());
                cuadro.setHoras(cDatos.getString(11).trim());
                cuadro.setRendimiento(cDatos.getString(12).trim());
                cuadro.setObservacion(cDatos.getString(13).trim());
                lstCuadro.add(cuadro);
            }
        }

        return lstCuadro;
    }
}