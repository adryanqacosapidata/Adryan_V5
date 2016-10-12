package com.adryan.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adryan.app.Entidades.Config;
import com.adryan.app.Entidades.Marcaciones;
import com.adryan.app.Entidades.Trabajador;
import com.adryan.app.R;
import com.adryan.app.comunes.LogFile;
import com.adryan.app.comunes.StringUtils;
import com.adryan.app.comunes.VariablesGenerales;
import com.adryan.app.database.DBAdapterConfig;
import com.adryan.app.database.DBAdapterMarcaciones;
import com.adryan.app.database.DBAdapterTrabajador;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class LectorUsb extends Activity implements Runnable {

    private final String ACTION_USB_PERMISSION = "com.adryan.app.USB_PERMISSION";
    private static final String TAG = "USB_PERMISSION";

    UsbManager mUsbManager;
    UsbAccessory mAccessory;
    ParcelFileDescriptor mFileDescriptor;
    PendingIntent mPermissionIntent;

    FileInputStream mInputStream;
    FileOutputStream mOutputStream;

    boolean mPermissionRequestPending = false;

    EditText txtDemo;
    TextView lblLog, lblCentroCosto, lblActividad, lblLabor;
    Button btnProcesar, btnConfig, btnCancel;

    DBAdapterTrabajador dbTrab;
    DBAdapterMarcaciones dbMarca;
    DBAdapterConfig dbCfg;
    LogFile logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logger = new LogFile(getBaseContext());
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        mAccessory = (UsbAccessory) getIntent().getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
        mUsbManager.requestPermission(mAccessory, mPermissionIntent);

        setContentView(R.layout.activity_lector_usb);
        txtDemo = (EditText) findViewById(R.id.etMuestra);
        lblLog = (TextView) findViewById(R.id.tvLogMarca);
        lblCentroCosto = (TextView) findViewById(R.id.txtLecCC);
        lblActividad = (TextView) findViewById(R.id.txtLecAct);
        lblLabor = (TextView) findViewById(R.id.txtLecLab);
        btnProcesar = (Button) findViewById(R.id.btnLecProccesar);
        btnConfig = (Button) findViewById(R.id.btnLecCfg);
        btnCancel = (Button) findViewById(R.id.btnLecCancel);
        //txtDemo.setText("74152364\n45349488");

        try{
            dbTrab = new DBAdapterTrabajador(this);
            dbCfg = new DBAdapterConfig(this);
            dbMarca = new DBAdapterMarcaciones(this);
            dbTrab.open();
            dbCfg.open();
            dbMarca.open();
        } catch (SQLException ex){
            Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_LONG).show();
            dbTrab.close();
            dbCfg.close();
            dbMarca.close();
        }

        Cursor cCfg = dbCfg.getConfigAct();
        if (cCfg.getCount() > 0) {
            lblCentroCosto.setText(cCfg.getString(4));
            lblActividad.setText(cCfg.getString(5));
            lblLabor.setText(cCfg.getString(6));
        }

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //txtDemo.setText("42630429\n45349488\n048021");
                //logger.addRecordLog("LectorUsb->Datos : " + txtDemo.getText());
                if (txtDemo.getText().toString().length() > 0) {
                    char rep = (char)10;
                    String datos = txtDemo.getText().toString().trim().replace(rep,',');
                    //Toast.makeText(getBaseContext(), datos, Toast.LENGTH_SHORT).show();
                    logger.addRecordLog("LectorUsb->Datos : " + datos);
                    String arrCodigos[] = datos.split(",");
                    String codigo;
                    String msj = "";
                    String fechaActual;
                    String horaActual;
                    Calendar fecha = Calendar.getInstance();
                    SimpleDateFormat strDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    boolean sw = false;
                    Config cfg = dbCfg.getCfgAct(Integer.parseInt(VariablesGenerales.PERIODO_ACTUAL));

                    for(int i = 0; i < arrCodigos.length; i++) {
                        codigo = arrCodigos[i].toString().trim();
                        //Toast.makeText(getBaseContext(), codigo, Toast.LENGTH_SHORT).show();
                        //logger.addRecordLog("LectorUsb->capturado : " + codigo);
                        Trabajador trab = dbTrab.getByFotocheck(codigo, cfg.getCompania().trim());

                        if (trab.getCount() > 0) {
                            //logger.addRecordLog("LectorUsb->trabajador encontrado : " + codigo);
                            fechaActual = strDateFormat.format(fecha.getTime()).substring(0, 10);
                            horaActual = strDateFormat.format(fecha.getTime()).substring(11, 16);
                            Marcaciones marca = new Marcaciones();

                            marca.setTrabajador(trab.getCodigoUnico());
                            marca.setHora(horaActual);
                            marca.setCompania(trab.getCompania());
                            marca.setFecha(fechaActual.trim());
                            marca.setCentroCosto(cfg.getCentroCosto().trim());
                            marca.setSupervisor(cfg.getSupervisor().trim());
                            marca.setActividad(cfg.getActividad().trim());
                            marca.setLabor(cfg.getLabor().trim());
                            marca.setCompania(cfg.getCompania().trim());
                            marca.setPeriodo(cfg.getPeriodo().trim());
                            marca.setFlagManual("0");

                            try {
                                if (dbMarca.isExist(marca)) {
                                    msj = "(Ya registra marca) Trabajador : " + trab.getMatricula() + " - " + trab.getNombre();
                                } else {
                                    sw = dbMarca.nuevaMarca(marca);
                                }
                            } catch (Exception e) {
                                msj = e.getMessage();
                            }

                            if (sw) {
                                msj = "(" + codigo + ") Marca registrada -> Trabajador : " + trab.getMatricula() + " - " + trab.getNombre();
                            } else {
                                if (msj.trim() == "") msj = "Ocurrio un problema al insertar la marcación.";
                            }

                        } else {
                            msj = "(" + codigo + ") Trabajador no registrado";
                        }

                        lblLog.append(msj + "\n");
                        //logger.addRecordLog("LectorUsb->Procesado : " + msj);
                    }

                    txtDemo.setText("");
                } else {
                    Toast.makeText(getBaseContext(), "No hay datos para procesar.", Toast.LENGTH_SHORT).show();
                    //logger.addRecordLog("LectorUsb : No hay datos para procesar.");
                    txtDemo.setText("");
                }
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itConfig = new Intent();
                itConfig.setClass(LectorUsb.this, Configuracion.class);
                startActivity(itConfig);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAccessory();
                Intent itPrincipal = new Intent();
                itPrincipal.setClass(LectorUsb.this, Principal.class);
                startActivity(itPrincipal);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //closeAccessory();
        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Intent intent = getIntent();
        if (mInputStream != null && mOutputStream != null) {
            return;
        }

        //UsbAccessory[] accessories = mUsbManager.getAccessoryList();
        //UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        UsbAccessory accessory = mAccessory;
        if (accessory != null) {
            if (mUsbManager.hasPermission(accessory)) {
                openAccessory(accessory);
            } else {
                synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        mUsbManager.requestPermission(accessory, mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                }
            }
        } else {
            Log.d(TAG, "mAccessory is null");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeAccessory();
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    //Toast.makeText(getBaseContext(), "accessory: " + accessory, Toast.LENGTH_SHORT).show();
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(accessory != null){
                            if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
                                //Toast.makeText(getBaseContext(), "ACTION_USB_ACCESSORY_ATTACHED", Toast.LENGTH_SHORT).show();
                                openAccessory(accessory);
                            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                                //Toast.makeText(getBaseContext(), "ACTION_USB_ACCESSORY_ATTACHED", Toast.LENGTH_SHORT).show();
                                closeAccessory();
                            }
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "permission denied for accessory " + accessory, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private void closeAccessory() {
        //enableControls(false);
        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }
        } catch (IOException e) {
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }
    }

    private void openAccessory(UsbAccessory accessory) {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);
            Thread thread = new Thread(null, this, "LectorUsb");
            thread.start();
            //Toast.makeText(getBaseContext(), "accessory open", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "accessory open fail", Toast.LENGTH_SHORT).show();
        }
    }

    public void run() {
    }

}
