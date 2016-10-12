package com.adryan.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.adryan.app.Entidades.Periodo;
import com.adryan.app.comunes.StringUtils;
import com.adryan.app.comunes.VariablesGenerales;
import com.adryan.app.database.DBAdapterCompania;
import com.adryan.app.database.DBAdapterParametro;
import com.adryan.app.database.DBAdapterPeriodo;
import com.adryan.app.database.DBAdapterUsuario;
import com.adryan.app.util.SystemUiHider;

import java.sql.SQLException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Ingreso extends Activity {

    public static String ID_DEVICE = "";
    //private DBAdapterSupervisor dbSup;
    private DBAdapterPeriodo dbPer;
    //private DBAdapterConfig dbCfg;
    private DBAdapterCompania dbCia;
    private DBAdapterUsuario dbUser;
    private DBAdapterParametro dbPar;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ingreso);

        //final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        final Spinner cbCia = (Spinner) findViewById(R.id.spCia);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.show();

        // logica VFQS
        ID_DEVICE = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        //TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //ID_DEVICE = telephonyManager.getDeviceId();
        VariablesGenerales.ID_DISPOSITIVO = ID_DEVICE;
        VariablesGenerales.CODIGO_CIA = "";

        try{
            //dbSup = new DBAdapterSupervisor(this);
            dbPer = new DBAdapterPeriodo(this);
            //dbCfg = new DBAdapterConfig(this);
            dbCia = new DBAdapterCompania(this);
            dbUser = new DBAdapterUsuario(this);
            dbPar = new DBAdapterParametro(this);
            //dbSup.open();
            dbPer.open();
            //dbCfg.open();
            dbCia.open();
            dbUser.open();
            dbPar.open();
        } catch (SQLException ex){
            Toast.makeText(getBaseContext(), ex.toString(),Toast.LENGTH_SHORT).show();
            //dbSup.close();
            //dbCfg.close();
            dbPer.close();
            dbCia.close();
            dbUser.close();
            dbPar.close();
            finish();
        }

        Cursor cCia = dbCia.getCompaniasLst();
        if (cCia != null) {
            SimpleCursorAdapter adtCC = new SimpleCursorAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    cCia,
                    new String[]{"descripcion"},
                    new int[]{android.R.id.text1});
            cbCia.setAdapter(adtCC);
        }

        cbCia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) cbCia.getItemAtPosition(position);
                if (c != null &&  c.moveToPosition(position)) {
                    VariablesGenerales.CODIGO_CIA = c.getString(c.getColumnIndex("_id"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (cbCia.getCount() > 0) {
                    Cursor c = (Cursor) cbCia.getItemAtPosition(0);
                    if (c != null &&  c.moveToPosition(0)) {
                        VariablesGenerales.CODIGO_CIA = c.getString(c.getColumnIndex("_id"));
                    }
                }
            }
        });

        if (cbCia.getCount() > 0) {
            Cursor c = (Cursor) cbCia.getItemAtPosition(0);
            if (c != null &&  c.moveToPosition(0)) {
                VariablesGenerales.CODIGO_CIA = c.getString(c.getColumnIndex("_id"));
            }
        }

        final EditText txtServer = (EditText) findViewById(R.id.edtServer);
        final EditText txtUser = (EditText) findViewById(R.id.edtUser);
        final EditText txtPwd = (EditText) findViewById(R.id.edtPassword);

        txtServer.setText(dbPar.getValorParam("1"));

        Button btnIngresar = (Button) findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(txtServer.getText().toString())) {
                    txtServer.requestFocus();
                    Toast.makeText(getBaseContext(),"IP servidor obligatorio.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtils.isEmpty(txtUser.getText().toString())) {
                    txtUser.requestFocus();
                    Toast.makeText(getBaseContext(),"Nombre de usuario obligatorio.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (StringUtils.isEmpty(txtPwd.getText().toString())) {
                    txtPwd.requestFocus();
                    Toast.makeText(getBaseContext(), "Contraseña obligatoria.", Toast.LENGTH_SHORT).show();
                    return;
                }

                VariablesGenerales.CODIGO_SUPERVISOR = dbUser.getByUsuarioDocuemento(
                        txtUser.getText().toString(), txtPwd.getText().toString(),
                        VariablesGenerales.CODIGO_CIA);

                if (VariablesGenerales.CODIGO_SUPERVISOR == "") {
                    txtUser.setText("");
                    txtPwd.setText("");
                    txtUser.requestFocus();
                    Toast.makeText(getBaseContext(),"Usuario y/o contraseña incorrectos.", Toast.LENGTH_SHORT).show();
                    VariablesGenerales.NRO_INTENTTOS++;
                } else {
                    VariablesGenerales.SW_CONFIGURADO = false;
                    Intent itConfigurar = new Intent();
                    //itConfigurar.setClass(Ingreso.this, Configuracion.class);
                    itConfigurar.setClass(Ingreso.this, ParametroAct.class);
                    startActivity(itConfigurar);
                    Periodo periodo =  dbPer.getPeriodoActual(VariablesGenerales.CODIGO_CIA);
                    VariablesGenerales.PERIODO_ACTUAL = periodo.getId();
                    VariablesGenerales.DES_PERIODO_ACTUAL = periodo.getDescripcion();
                    //dbSup.close();
                    //dbCfg.close();

                    dbPar.updParametro("1",txtServer.getText().toString().trim());
                    VariablesGenerales.SERVER = dbPar.getValorParam("1");

                    dbPer.close();
                    dbCia.close();
                    dbUser.close();
                    dbPar.close();
                    finish();
                }

                if (VariablesGenerales.NRO_INTENTTOS >= 3) {
                    Toast.makeText(getBaseContext(),"Usuario bloqueado. Se mostrará la pantalla de sincronización", Toast.LENGTH_SHORT).show();
                    Intent itSincronizar = new Intent();
                    itSincronizar.setClass(Ingreso.this, Sincronizacion.class);
                    startActivity(itSincronizar);
                    VariablesGenerales.SW_BD = false;
                    VariablesGenerales.NRO_INTENTTOS = 0;
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder dlgSalir = new AlertDialog.Builder(Ingreso.this);
        dlgSalir.setMessage("¿Desea salir de la aplicación?")
                .setTitle("Salir")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        //dbSup.close();
                        //dbCfg.close();
                        dbPer.close();
                        dbCia.close();
                        dbUser.close();
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
}
