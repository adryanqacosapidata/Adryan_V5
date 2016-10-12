package com.adryan.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adryan.app.Entidades.Config;
import com.adryan.app.Entidades.Tareador;
import com.adryan.app.Entidades.Trabajador;
import com.adryan.app.comunes.VariablesGenerales;
import com.adryan.app.database.DBAdapterActividad;
import com.adryan.app.database.DBAdapterCentroCosto;
import com.adryan.app.database.DBAdapterConfig;
import com.adryan.app.database.DBAdapterDistribucion;
import com.adryan.app.database.DBAdapterLabor;
import com.adryan.app.database.DBAdapterSupervisor;
import com.adryan.app.database.DBAdapterTrabajador;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Configuracion extends ActionBarActivity {

    private DBAdapterSupervisor dbSup;
    private DBAdapterActividad dbAct;
    private DBAdapterCentroCosto dbCC;
    private DBAdapterLabor dbLabor;
    private DBAdapterTrabajador dbTrab;
    private DBAdapterConfig dbCfg;
    private DBAdapterDistribucion dbDist;
    private Cursor cursorAct;
    private Cursor cursorCC;
    private Cursor cursorLabor;

    private static String centroCosto = "";
    private static String actividad = "";
    private static String labor = "";
    
    private static final String CIA = VariablesGenerales.CODIGO_CIA;
    private static final String PERIODO = VariablesGenerales.PERIODO_ACTUAL;
    private static final String SUPERVISOR = VariablesGenerales.CODIGO_SUPERVISOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        try{
            dbSup = new DBAdapterSupervisor(this);
            dbAct = new DBAdapterActividad(this);
            dbCC = new DBAdapterCentroCosto(this);
            dbLabor = new DBAdapterLabor(this);
            dbTrab = new DBAdapterTrabajador(this);
            dbCfg = new DBAdapterConfig(this);
            dbDist = new DBAdapterDistribucion(this);
            dbSup.open();
            dbAct.open();
            dbCC.open();
            dbLabor.open();
            dbTrab.open();
            dbCfg.open();
            dbDist.open();
        } catch (SQLException ex){
            Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_LONG).show();
            dbSup.close();
            dbAct.close();
            dbCC.close();
            dbCfg.close();
            dbTrab.close();
            dbLabor.close();
            dbDist.close();
        }

        // componentes
        TextView lblIdDispositivo = (TextView) findViewById(R.id.txtIdDispositivo);
        TextView lblMatriculaSup = (TextView) findViewById(R.id.txtCfgMatSup);
        TextView lblSupervisor = (TextView) findViewById(R.id.txtCfgSupervisor);
        final TextView lblFechaActual = (TextView) findViewById(R.id.txtCfgPeriodo);
        final Spinner cbCentroCosto = (Spinner) findViewById(R.id.spinnerCC);
        final Spinner cbActividad = (Spinner) findViewById(R.id.spinnerActividad);
        final Spinner cbLabor = (Spinner) findViewById(R.id.spinnerLabor);
        Button btnAceptar = (Button) findViewById(R.id.btnAceptarCfg);
        Button btnCancelar = (Button) findViewById(R.id.btnCancelarCfg);

        lblIdDispositivo.setText(VariablesGenerales.ID_DISPOSITIVO);
        Calendar fecha = Calendar.getInstance();
        SimpleDateFormat strDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fechaActual = strDateFormat.format(fecha.getTime());
        lblFechaActual.setText(fechaActual);

        if (!VariablesGenerales.SW_CONFIGURADO){
            btnCancelar.setText("Salir");
        } else {
            btnCancelar.setText("Cancelar");
        }

        Trabajador trab = dbTrab.getTrabajador(SUPERVISOR, CIA);
        lblMatriculaSup.setText(trab.getMatricula());
        lblSupervisor.setText(trab.getNombre());

        String filtroCC = dbDist.getCC(SUPERVISOR, CIA, PERIODO);
        final String filtroAct = dbDist.getAct(SUPERVISOR, CIA, PERIODO);
        final String filtroLab = dbDist.getLabor(SUPERVISOR, CIA, PERIODO);

        cursorCC = dbCC.filtroCC(filtroCC, CIA);
        if (cursorCC != null) {
            SimpleCursorAdapter adtCC = new SimpleCursorAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    cursorCC,
                    new String[]{"descripcion"},
                    new int[]{android.R.id.text1});
            cbCentroCosto.setAdapter(adtCC);
        }

        cbCentroCosto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = (Cursor) cbCentroCosto.getItemAtPosition(i);
                if (c != null &&  c.moveToPosition(i)) {
                    centroCosto = c.getString(c.getColumnIndex("_id"));

                    cursorAct = dbAct.getDatosFiltro(filtroAct, centroCosto, CIA);
                    if (cursorAct != null) {
                        SimpleCursorAdapter adtAct = new SimpleCursorAdapter(getBaseContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                cursorAct,
                                new String[]{"descripcion"},
                                new int[]{android.R.id.text1});
                        cbActividad.setAdapter(adtAct);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cbActividad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = (Cursor) cbActividad.getItemAtPosition(i);
                if (c != null &&  c.moveToPosition(i)) {
                    actividad = c.getString(c.getColumnIndex("_id"));

                    cursorLabor = dbLabor.getDatosFiltro(filtroLab, centroCosto, actividad, CIA);
                    if (cursorLabor != null) {
                        SimpleCursorAdapter adtLabor = new SimpleCursorAdapter(getBaseContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                cursorLabor,
                                new String[]{"descripcion"},
                                new int[]{android.R.id.text1});
                        cbLabor.setAdapter(adtLabor);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cbLabor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = (Cursor) cbLabor.getItemAtPosition(i);
                if (c != null &&  c.moveToPosition(i)) {
                    labor = c.getString(c.getColumnIndex("_id"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config cfg = new Config();
                cfg.setActividad(actividad);
                cfg.setCentroCosto(centroCosto);
                cfg.setCompania(CIA);
                cfg.setFecha(lblFechaActual.getText().toString().trim());
                cfg.setSupervisor(SUPERVISOR);
                cfg.setLabor(labor);
                cfg.setTablet(VariablesGenerales.ID_DISPOSITIVO);
                cfg.setPeriodo(PERIODO);
                if (dbCfg.setConfig(cfg, dbCfg.isExist(cfg))) {
                    VariablesGenerales.SW_CONFIGURADO = true;
                    VariablesGenerales.SW_BD = true;
                    Toast.makeText(getBaseContext(), "Configuración completa.", Toast.LENGTH_SHORT).show();
                    dbSup.close();
                    dbAct.close();
                    dbCC.close();
                    dbCfg.close();
                    dbLabor.close();
                    dbDist.close();
                    Intent itPrincipal = new Intent();
                    itPrincipal.setClass(Configuracion.this, Principal.class);
                    startActivity(itPrincipal);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Ocurrio un error.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbSup.close();
                dbAct.close();
                dbCC.close();
                dbCfg.close();
                dbLabor.close();
                dbDist.close();
                if (!VariablesGenerales.SW_CONFIGURADO) {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                } else {
                    Intent itPrincipal = new Intent();
                    itPrincipal.setClass(Configuracion.this, Principal.class);
                    startActivity(itPrincipal);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}
