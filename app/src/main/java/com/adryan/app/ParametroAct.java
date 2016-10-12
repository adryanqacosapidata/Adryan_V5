package com.adryan.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adryan.app.database.DBAdapterParametro;

import java.sql.SQLException;

/**
 * Created by vquispe on 19/10/2015.
 */
public class ParametroAct extends Activity {
    DBAdapterParametro dbPar;

    EditText txtServerAPI;
    EditText txtServerSMTP;
    EditText txtPortSMTP;
    EditText txtPortSSL;
    EditText txtMailFrom;
    EditText txtPwdFrom;
    EditText txtMailTo;
    EditText txtSubject;
    EditText txtBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametro);

        try {
            dbPar = new DBAdapterParametro(this);
            dbPar.open();
        }  catch (SQLException ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
            dbPar.close();
        }

        txtServerAPI = (EditText) findViewById(R.id.txtIpServer);
        txtServerSMTP = (EditText) findViewById(R.id.txtSmtpServer);
        txtPortSMTP = (EditText) findViewById(R.id.txtPortSMTP);
        txtPortSSL = (EditText) findViewById(R.id.txtPortSSL);
        txtMailFrom = (EditText) findViewById(R.id.txtMailFrom);
        txtPwdFrom = (EditText) findViewById(R.id.txtPwdMailFrom);
        txtMailTo = (EditText) findViewById(R.id.txtMailTo);
        txtSubject = (EditText) findViewById(R.id.txtSubjectMail);
        txtBody = (EditText) findViewById(R.id.txtBodyMail);
        Button btnGrabar = (Button) findViewById(R.id.btnAceptarPar);
        Button btnCancelar = (Button) findViewById(R.id.btnCancelarPar);

        txtServerAPI.setText(dbPar.getValorParam("1"));
        txtServerSMTP.setText(dbPar.getValorParam("2"));
        txtPortSMTP.setText(dbPar.getValorParam("3"));
        txtPortSSL.setText(dbPar.getValorParam("4"));
        txtMailFrom.setText(dbPar.getValorParam("5"));
        txtPwdFrom.setText(dbPar.getValorParam("6"));
        txtMailTo.setText(dbPar.getValorParam("7"));
        txtSubject.setText(dbPar.getValorParam("8"));
        txtBody.setText(dbPar.getValorParam("9"));

        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbPar.updParametro("1", txtServerAPI.getText().toString().trim()) &&
                        dbPar.updParametro("2",txtServerSMTP.getText().toString().trim()) &&
                        dbPar.updParametro("3",txtPortSMTP.getText().toString().trim()) &&
                        dbPar.updParametro("4",txtPortSSL.getText().toString().trim()) &&
                        dbPar.updParametro("5",txtMailFrom.getText().toString().trim()) &&
                        dbPar.updParametro("6",txtPwdFrom.getText().toString().trim()) &&
                        dbPar.updParametro("7",txtMailTo.getText().toString().trim()) &&
                        dbPar.updParametro("8",txtSubject.getText().toString().trim()) &&
                        dbPar.updParametro("9",txtBody.getText().toString().trim())
                ) {
                    Toast.makeText(ParametroAct.this, "Parámetros guardados....", Toast.LENGTH_SHORT).show();
                    Intent itPrincipal = new Intent();
                    itPrincipal.setClass(ParametroAct.this, Principal.class);
                    startActivity(itPrincipal);
                    finish();
                } else {
                    Toast.makeText(ParametroAct.this, "Error al guardar los datos....", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlgSalir = new AlertDialog.Builder(ParametroAct.this);
                dlgSalir.setMessage("¿Desea salir de la aplicación?")
                        .setTitle("Salir")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                dbPar.close();
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
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
