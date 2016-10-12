package com.adryan.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.adryan.app.Entidades.Config;
import com.adryan.app.Entidades.Marcaciones;
import com.adryan.app.Entidades.Trabajador;
import com.adryan.app.comunes.LogFile;
import com.adryan.app.comunes.StringUtils;
import com.adryan.app.comunes.VariablesGenerales;
import com.adryan.app.database.DBAdapterConfig;
import com.adryan.app.database.DBAdapterMarcaciones;
import com.adryan.app.database.DBAdapterTrabajador;
import com.google.zxing.integration.android.IntentIntegrator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;


public class MarcaManual extends Activity {

    EditText txtHora, txtFecha, txtMatricula, txtTrabajador, txtFotocheck;
    private DBAdapterMarcaciones dbMarca;
    private DBAdapterTrabajador dbTrab;
    private DBAdapterConfig dbCfg;

    private Trabajador trab;
    private LogFile logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcamanual);

        logger = new LogFile(this.getBaseContext());

        try{
            dbMarca = new DBAdapterMarcaciones(this);
            dbTrab = new DBAdapterTrabajador(this);
            dbCfg = new DBAdapterConfig(this);
            dbMarca.open();
            dbTrab.open();
            dbCfg.open();
        } catch (SQLException ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            dbMarca.close();
            dbTrab.close();
            dbCfg.close();
        }

        Button btnAceptar = (Button) findViewById(R.id.btnAceptarEdit);
        Button btnCancelar = (Button) findViewById(R.id.btnCancelarEdit);
        txtFotocheck = (EditText) findViewById(R.id.txtFotocheckEdit);
        txtHora = (EditText) findViewById(R.id.txtHoraEdit);
        txtFecha = (EditText) findViewById(R.id.txtFechaEdit);
        txtMatricula = (EditText) findViewById(R.id.txtMatriculaEdit);
        txtTrabajador = (EditText) findViewById(R.id.txtTrabajadorEdit);

        txtFotocheck.setEnabled(false);
        txtMatricula.setEnabled(false);
        txtTrabajador.setEnabled(false);

        if (VariablesGenerales.ID_MARCA == ""){
            txtFotocheck.setEnabled(true);
        } else {
            Marcaciones marca = dbMarca.getMarca(VariablesGenerales.ID_MARCA);
            trab = dbTrab.getByCodigoUnico(marca.getTrabajador());
            txtFotocheck.setText(trab.getDocumento());
            txtTrabajador.setText(trab.getNombre());
            txtMatricula.setText(trab.getMatricula());
            txtFecha.setText(marca.getFecha());
            txtHora.setText(marca.getHora());
        }

        txtHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar horaActual = Calendar.getInstance();
                int hora = horaActual.get(Calendar.HOUR_OF_DAY);
                int minuto = horaActual.get(Calendar.MINUTE);
                TimePickerDialog tpDlg = new TimePickerDialog(MarcaManual.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        txtHora.setText("" + StringUtils.padLeft(("" + hour), 2, '0') + ":" + StringUtils.padLeft(("" + minute), 2, '0'));
                    }
                }, hora, minuto, true);
                tpDlg.setTitle("Seleccionar Hora");
                tpDlg.show();
            }
        });

        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar fechaActual = Calendar.getInstance();
                int anio = fechaActual.get(Calendar.YEAR);
                int mes = fechaActual.get(Calendar.MONTH);
                int dia = fechaActual.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpDlg = new DatePickerDialog(MarcaManual.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        txtFecha.setText(StringUtils.padLeft((""+day), 2, '0') + "/" + StringUtils.padLeft("" + (month + 1),2,'0') + "/" + year);
                    }
                }, anio, mes, dia);
                dpDlg.setTitle("Seleccionar fecha");
                dpDlg.show();
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fotocheck = txtFotocheck.getText().toString().trim();
                String matricula = txtMatricula.getText().toString().trim();
                String fecha = txtFecha.getText().toString().trim();
                String hora = txtHora.getText().toString().trim();
                boolean valor = false;

                Marcaciones marca = new Marcaciones();
                marca.setCentroCosto(VariablesGenerales.CENTRO_COSTO);
                marca.setActividad(VariablesGenerales.ACTIVIDAD);
                marca.setFecha(fecha);
                marca.setFlagManual("1");
                marca.setHora(hora);
                marca.setLabor(VariablesGenerales.LABOR);
                marca.setSupervisor(VariablesGenerales.CODIGO_SUPERVISOR);

                if (VariablesGenerales.UPD_MARCA) {
                    logger.addRecordLog("Inicio de edicion de marca.");
                    marca.setCompania(trab.getCompania());
                    marca.setTrabajador(trab.getCodigoUnico());
                    marca.setId((VariablesGenerales.ID_MARCA == "" ? 0 : Integer.parseInt(VariablesGenerales.ID_MARCA)));
                    if (!txtFecha.getText().toString().trim().isEmpty() && !txtHora.getText().toString().trim().isEmpty()){
                        valor = dbMarca.updateMarca(marca);
                    } else {
                        Toast.makeText(getBaseContext(), "Ingresar la Fecha y/o Hora.", Toast.LENGTH_SHORT).show();
                    }
                    VariablesGenerales.UPD_MARCA = false;
                } else {
                    logger.addRecordLog("Inicio de nueva marca.");
                    Config cfg = dbCfg.getCfgAct(Integer.parseInt(VariablesGenerales.PERIODO_ACTUAL));
                    marca.setCentroCosto(cfg.getCentroCosto());
                    marca.setSupervisor(cfg.getSupervisor());
                    marca.setActividad(cfg.getActividad());
                    marca.setLabor(cfg.getLabor());
                    marca.setFlagManual("1");

                    Trabajador trab = dbTrab.getByFotocheck(fotocheck, cfg.getCompania());

                    if (trab.getCount() > 0) {
                        marca.setCompania(trab.getCompania());
                        marca.setTrabajador(trab.getCodigoUnico());
                        txtTrabajador.setText(trab.getNombre());
                        txtMatricula.setText(trab.getMatricula());

                        try {
                            if (!txtFecha.getText().toString().trim().isEmpty() && !txtHora.getText().toString().trim().isEmpty()) {
                                if (!dbMarca.isExist(marca)) {
                                    valor = dbMarca.nuevaMarca(marca);
                                } else {
                                    Toast.makeText(getBaseContext(), "Ya existe una marcación rehistrada con los datos.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "Ingresar la Fecha y/o Hora.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            logger.addRecordLog("nuevaMarca: " + e.toString());
                            Log.e("nuevaMarca", e.toString());
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "No existe el trabajador.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (valor) {
                    Toast.makeText(getBaseContext(), "Se registro la marcacion manual.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Ocurrio un error al grabar la marcación.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbMarca.close();
                dbCfg.close();
                dbTrab.close();
                Intent itPrincipal = new Intent();
                itPrincipal.setClass(MarcaManual.this, Principal.class);
                startActivity(itPrincipal);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        dbMarca.close();
        dbTrab.close();
        dbCfg.close();
        finish();
    }
}
