package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Config;
import com.adryan.app.Entidades.Tareador;
import com.adryan.app.comunes.LogFile;

import java.sql.SQLException;

/**
 * Created by vquispe on 20/08/2014.
 */
public class DBAdapterConfig {
    // campos de la tabla
    private static final String ROW_SUPERVISOR = "supervisor";
    private static final String ROW_CENTROCOSTO = "centrocosto";
    private static final String ROW_ACTIVIDAD = "actividad";
    private static final String ROW_LABOR = "labor";
    private static final String ROW_PERIODO = "periodo";
    private static final String ROW_COMPANIA = "compania";
    private static final String ROW_TABLET = "tablet";
    private static final String ROW_ESTADO = "estado";
    private static final String ROW_FECHA = "fecha";
    private static final String DATABASE_TABLE = "config";

    private static final String ACTIVO = "1";
    private static final String INACTIVO = "0";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    LogFile logger;

    public DBAdapterConfig(Context context){
        this.contexto = context;
        logger =  new LogFile(context);
    }

    public DBAdapterConfig open() throws SQLException {
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues crearValores(Config cfg){
        ContentValues content = new ContentValues();
        content.put(ROW_SUPERVISOR, cfg.getSupervisor());
        content.put(ROW_CENTROCOSTO, cfg.getCentroCosto());
        content.put(ROW_ACTIVIDAD, cfg.getActividad());
        content.put(ROW_LABOR, cfg.getLabor());
        content.put(ROW_PERIODO, cfg.getPeriodo());
        content.put(ROW_TABLET, cfg.getTablet());
        content.put(ROW_ESTADO, ACTIVO);
        content.put(ROW_COMPANIA, cfg.getCompania());
        content.put(ROW_FECHA, cfg.getFecha());
        return content;
    }

    private boolean insertConfig(Config cfg) {
        return (db.insert(DATABASE_TABLE, null, crearValores(cfg)) > 0);
    }

    private boolean setActConfig(Config cfg){
        ContentValues content = new ContentValues();
        content.put(ROW_ESTADO, ACTIVO);
        String where = ROW_ACTIVIDAD + "='" + cfg.getActividad() + "' AND " + ROW_COMPANIA + "='" +
                cfg.getCompania() + "' AND " + ROW_FECHA + "='" + cfg.getFecha() + "' AND " +
                ROW_LABOR + "='" + cfg.getLabor() + "' AND " + ROW_CENTROCOSTO + "='" + cfg.getCentroCosto() +
                "' AND " + ROW_SUPERVISOR + "='" + cfg.getSupervisor() + "' AND " + ROW_TABLET +
                "='" + cfg.getTablet() + "'";
        return (db.update(DATABASE_TABLE, content, where, null) > 0);
    }

    private boolean setDesactConfig(){
        ContentValues content = new ContentValues();
        content.put(ROW_ESTADO, INACTIVO);
        return (db.update(DATABASE_TABLE, content, null, null) > 0);
    }

    public boolean deleteConfig(){
        return (db.delete(DATABASE_TABLE, null, null) > 0);
    }

    public Config getCfgAct(int periodo){
        Config cfg =  new Config();
        String where = "trim(" + ROW_ESTADO + ")='1' And trim(" + ROW_PERIODO + ")='" + periodo + "'" ;
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_SUPERVISOR, ROW_CENTROCOSTO, ROW_ACTIVIDAD, ROW_LABOR, ROW_FECHA,
                ROW_COMPANIA, ROW_PERIODO
        }, where, null, null, null, null, null);
        int ct = 0;
        if (cDatos != null){
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                cfg.setSupervisor(cDatos.getString(0).trim());
                cfg.setCentroCosto(cDatos.getString(1).trim());
                cfg.setActividad(cDatos.getString(2).trim());
                cfg.setLabor(cDatos.getString(3).trim());
                cfg.setFecha(cDatos.getString(4).trim());
                cfg.setCompania(cDatos.getString(5).trim());
                cfg.setPeriodo(cDatos.getString(6).trim());
                ct++;
            }
            cfg.setCount(ct);
        }
        return cfg;
    }

    public Cursor getConfigAct() {
        Cursor cDatos = null;
        String sql = "Select cfg.tablet As 'Tablet', cfg.supervisor As 'CodSupervisor', " +
                "t.matricula As 'MatSupervisor', t.nombres As 'NomSupervisor', " +
                "cc.descripcion As 'CentroCosto', act.descripcion As 'Actividad', " +
                "l.descripcion As 'Labor', cfg.compania, cfg.fecha From config cfg " +
                "Inner Join trabajador t On cfg.supervisor = t.codigounico " +
                "Inner Join centro_costo cc On cfg.centrocosto = cc._id " +
                "Inner Join actividad act On cfg.actividad = act._id " +
                "Inner Join labor l On cfg.labor = l._id " +
                "Where trim(cfg.estado) = '1';";
        cDatos = db.rawQuery(sql, null);
        if (cDatos != null) { cDatos.moveToFirst(); }
        return cDatos;
    }

    public boolean isExist(Config cfg){
        String where = "trim(" + ROW_ACTIVIDAD + ")=trim('" + cfg.getActividad() + "') AND trim(" +
                ROW_COMPANIA + ")=trim('" + cfg.getCompania() + "') AND trim(" + ROW_FECHA +
                ")=trim('" + cfg.getFecha() + "') AND trim(" + ROW_LABOR + ")=trim('" + cfg.getLabor() +
                "') AND trim(" + ROW_CENTROCOSTO + ")=trim('" + cfg.getCentroCosto() +
                "') AND trim(" + ROW_SUPERVISOR + ")=trim('" + cfg.getSupervisor()+ "') AND trim(" +
                ROW_TABLET + ")=trim('" + cfg.getTablet() + "') AND trim(" + ROW_PERIODO + ")=trim('" +
                ROW_PERIODO +"')";
        Cursor cDatos = db.query(true, DATABASE_TABLE, new String[]{
                ROW_SUPERVISOR, ROW_CENTROCOSTO, ROW_ACTIVIDAD, ROW_LABOR, ROW_FECHA,
                ROW_COMPANIA
        }, where, null, null, null, null, null);
        int ct = 0;
        if (cDatos != null){
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                ct++;
            }
        }
        return (ct > 0);
    }

    public boolean setConfig(Config cfg, boolean existeAnt){
        boolean sw = deleteConfig();
        if (existeAnt) {
            sw = setActConfig(cfg);
        } else {
            sw = insertConfig(cfg);
        }
        return sw;
    }

}
