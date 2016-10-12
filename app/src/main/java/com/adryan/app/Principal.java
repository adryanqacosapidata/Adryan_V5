package com.adryan.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adryan.app.Entidades.Config;
import com.adryan.app.comunes.VariablesGenerales;
import com.adryan.app.database.DBAdapterActividad;
import com.adryan.app.database.DBAdapterCentroCosto;
import com.adryan.app.database.DBAdapterConfig;
import com.adryan.app.database.DBAdapterLabor;
import com.adryan.app.database.DBAdapterPeriodo;
import com.adryan.app.database.DBAdapterSupervisor;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Principal extends Activity {

    private DBAdapterConfig dbConfig;
    private Cursor cursorCfg;
    //COMMIT
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        try{
            dbConfig = new DBAdapterConfig(this);
            dbConfig.open();
        } catch (SQLException ex){
            Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }

        TextView lblIdTablet = (TextView) findViewById(R.id.txtIdDispositivo);
        TextView lblFecha = (TextView) findViewById(R.id.txtFecha);
        TextView lblMatricula = (TextView) findViewById(R.id.txtMatSup);
        TextView lblNombres = (TextView) findViewById(R.id.txtSupervisor);
        TextView lblCentroCosto = (TextView) findViewById(R.id.txtCC);
        TextView lblLabor = (TextView) findViewById(R.id.txtLabor);
        TextView lblActividad = (TextView) findViewById(R.id.txtActividad);

        Calendar fecha = Calendar.getInstance();
        SimpleDateFormat strDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String fechaActual = strDateFormat.format(fecha.getTime());
        lblFecha.setText(fechaActual);

        Button btnConfig = (Button) findViewById(R.id.btnConfiguration);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itConfig = new Intent();
                itConfig.setClass(Principal.this, Configuracion.class);
                startActivity(itConfig);
                finish();
            }
        });

        Button btnLectura = (Button) findViewById(R.id.btnLectura);
        btnLectura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itLectura = new Intent();
                itLectura.setClass(Principal.this, LectorUsb.class);
                startActivity(itLectura);
                finish();
            }
        });

        Button btnManual = (Button) findViewById(R.id.btnIngManual);
        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itManual = new Intent();
                itManual.setClass(Principal.this, DetalleMarcas.class);
                startActivity(itManual);
                finish();
            }
        });

        Button btnSincronizacion = (Button) findViewById(R.id.btnSincronizacion);
        btnSincronizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itSync = new Intent();
                itSync.setClass(Principal.this, Sincronizacion.class);
                startActivity(itSync);
                finish();
            }
        });

        Button btnCuadro = (Button) findViewById(R.id.btnCuadro);
        btnCuadro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itCuadro = new Intent();
                itCuadro.setClass(Principal.this, CuadroTareo.class);
                startActivity(itCuadro);
                finish();
            }
        });

        Button btnSalir = (Button) findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlgSalir = new AlertDialog.Builder(Principal.this);
                dlgSalir.setMessage("¿Desea salir de la aplicación?")
                        .setTitle("Salir")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                //finish();
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                dlgSalir.create();
                dlgSalir.show();
            }
        });

        cursorCfg = dbConfig.getConfigAct();
        if (cursorCfg.getCount() > 0){
            lblIdTablet.setText(cursorCfg.getString(0));
            lblMatricula.setText(cursorCfg.getString(2));
            lblNombres.setText(cursorCfg.getString(3));
            lblActividad.setText(cursorCfg.getString(5));
            lblCentroCosto.setText(cursorCfg.getString(4));
            lblLabor.setText(cursorCfg.getString(6));
            VariablesGenerales.CODIGO_SUPERVISOR = cursorCfg.getString(1);
            VariablesGenerales.SW_CONFIGURADO = true;
            VariablesGenerales.SW_BD = true;
        } else {
            btnLectura.setEnabled(false);
        }

        dbConfig.close();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }

}
