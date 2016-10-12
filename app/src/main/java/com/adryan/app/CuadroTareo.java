package com.adryan.app;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adryan.app.Entidades.Asignacion;
import com.adryan.app.Entidades.Cuadro;
import com.adryan.app.Entidades.Tareador;
import com.adryan.app.Entidades.Trabajador;
import com.adryan.app.R;
import com.adryan.app.comunes.VariablesGenerales;
import com.adryan.app.database.DBAdapterActividad;
import com.adryan.app.database.DBAdapterAsignacion;
import com.adryan.app.database.DBAdapterCentroCosto;
import com.adryan.app.database.DBAdapterCuadroTareo;
import com.adryan.app.database.DBAdapterDistribucion;
import com.adryan.app.database.DBAdapterLabor;
import com.adryan.app.database.DBAdapterSupervisor;
import com.adryan.app.database.DBAdapterTrabajador;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CuadroTareo extends ActionBarActivity {

    private static final String CIA = VariablesGenerales.CODIGO_CIA;
    private static final String PERIODO = VariablesGenerales.PERIODO_ACTUAL;
    private static final String SUPERVISOR = VariablesGenerales.CODIGO_SUPERVISOR;

    private DBAdapterTrabajador dbTrab;
    private DBAdapterDistribucion dbDist;
    private DBAdapterActividad dbAct;
    private DBAdapterCentroCosto dbCC;
    private DBAdapterLabor dbLabor;
    private DBAdapterCuadroTareo dbCuadro;

    private Cursor cursorAct;
    private Cursor cursorCC;
    private Cursor cursorLabor;

    private static String centroCosto = "";
    private static String actividad = "";
    private static String labor = "";
    private static String supervisor;
    private int totPersonas = 0;
    private double totHoras = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuadro_tareo);

        TextView lblMatricula = (TextView) findViewById(R.id.txtMatSupCuadro);
        TextView lblNombre = (TextView) findViewById(R.id.txtNomSupCuadro);
        TextView lblFecha = (TextView) findViewById(R.id.txtFechaCuadro);
        final Spinner cbCentroCosto = (Spinner) findViewById(R.id.spCuadroCC);
        final Spinner cbActividad = (Spinner) findViewById(R.id.spCuadroAct);
        final Spinner cbLabor = (Spinner) findViewById(R.id.spCuadroLab);
        final EditText txtObjetivo = (EditText) findViewById(R.id.txtObjetivo);
        final EditText txtUnidMedida = (EditText) findViewById(R.id.txtUnidMed);
        final EditText txtNroPersonas = (EditText) findViewById(R.id.txtNumPers);
        final EditText txtAvance = (EditText) findViewById(R.id.txtAvance);
        final EditText txtHoras = (EditText) findViewById(R.id.txtHoras);
        final EditText txtRendi = (EditText) findViewById(R.id.txtRend);
        final EditText txtObs = (EditText) findViewById(R.id.txtObs);


        try {
            dbTrab = new DBAdapterTrabajador(this);
            dbAct = new DBAdapterActividad(this);
            dbCC = new DBAdapterCentroCosto(this);
            dbLabor = new DBAdapterLabor(this);
            dbDist = new DBAdapterDistribucion(this);
            dbCuadro = new DBAdapterCuadroTareo(this);
            dbTrab.open();
            dbAct.open();
            dbCC.open();
            dbLabor.open();
            dbDist.open();
            dbCuadro.open();
        } catch (Exception e){
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
            dbTrab.close();
            dbAct.close();
            dbCC.close();
            dbLabor.close();
            dbDist.close();
            dbCuadro.close();
        }

        // info para los cbs
        supervisor = VariablesGenerales.CODIGO_SUPERVISOR;
        Trabajador sup = dbTrab.getByCodigoUnico(supervisor);
        lblMatricula.setText(sup.getMatricula());
        lblNombre.setText(sup.getNombre());

        Calendar fecha = Calendar.getInstance();
        SimpleDateFormat strDateFormat = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat strFormat103 = new SimpleDateFormat("dd/MM/yyyy");
        final String fechaActual = strDateFormat.format(fecha.getTime());
        final String fecha103 = strFormat103.format(fecha.getTime());

        lblFecha.setText(fecha103);

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
                    //totPersonas = dbCuadro.getTotMarcasFiltro(supervisor, centroCosto, actividad, labor, fecha103);
                    //totHoras = totPersonas * 8.0;
                    Cuadro cuadro = mostrarDatos(fechaActual, supervisor, centroCosto, actividad, labor);
                    if (cuadro.getCount() > 0){
                        txtAvance.setText(cuadro.getAvance());
                        txtObjetivo.setText(cuadro.getObjetivo());
                        txtNroPersonas.setText(cuadro.getNroPersonal());
                        txtUnidMedida.setText(cuadro.getUnidMedida());
                        txtHoras.setText(cuadro.getHoras());
                        txtRendi.setText(cuadro.getRendimiento());
                        txtObs.setText(cuadro.getObservacion());
                    } else {
                        txtAvance.setText("");
                        txtObjetivo.setText("");
                        txtUnidMedida.setText("");
                        txtRendi.setText("");
                        txtObs.setText("");
                        txtNroPersonas.setText("");
                        txtHoras.setText("");
                    }
                    //txtNroPersonas.setText(totPersonas + "");
                    //txtHoras.setText(totHoras + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btnGuardar = (Button) findViewById(R.id.btnSaveCuadro);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cuadro cuadro =  new Cuadro();
                cuadro.setSupervisor(supervisor);
                cuadro.setCompania(CIA);
                cuadro.setLabor(labor);
                cuadro.setActividad(actividad);
                cuadro.setAvance(txtAvance.getText().toString().trim());
                cuadro.setCentroCosto(centroCosto);
                cuadro.setFecha(fechaActual);
                cuadro.setHoras(txtHoras.getText().toString().trim());
                cuadro.setNroPersonal(txtNroPersonas.getText().toString().trim());
                cuadro.setObjetivo(txtObjetivo.getText().toString().trim());
                cuadro.setObservacion(txtObs.getText().toString().trim());
                cuadro.setRendimiento(txtRendi.getText().toString().trim());
                cuadro.setUnidMedida(txtUnidMedida.getText().toString().trim());
                cuadro.setPeriodo(PERIODO);

                if (dbCuadro.addUpdCuadro(cuadro)){
                    Toast.makeText(getBaseContext(), "Se grabaron los datos.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Error al guardar los datos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnSalir = (Button) findViewById(R.id.btnSalirCuadro);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbTrab.close();
                dbAct.close();
                dbCC.close();
                dbLabor.close();
                dbDist.close();
                dbCuadro.close();
                Intent itPrincipal = new Intent();
                itPrincipal.setClass(CuadroTareo.this, Principal.class);
                startActivity(itPrincipal);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private Cuadro mostrarDatos(String fechaActual, String supervisor, String centroCosto, String actividad, String labor) {
        return dbCuadro.getCuadro(fechaActual, supervisor, centroCosto, actividad, labor);
    }

    // captura datos
    private void getDatos (String sup, String cc, String act, String lab, String cia){

    }
}
