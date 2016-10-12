package com.adryan.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adryan.app.Entidades.Actividad;
import com.adryan.app.Entidades.Asignacion;
import com.adryan.app.Entidades.CentroCosto;
import com.adryan.app.Entidades.Compania;
import com.adryan.app.Entidades.Cuadro;
import com.adryan.app.Entidades.Distribucion;
import com.adryan.app.Entidades.Labor;
import com.adryan.app.Entidades.Marcaciones;
import com.adryan.app.Entidades.Periodo;
import com.adryan.app.Entidades.Tareador;
import com.adryan.app.Entidades.Trabajador;
import com.adryan.app.comunes.CallSOAP;
import com.adryan.app.comunes.LogFile;
import com.adryan.app.comunes.VariablesGenerales;
import com.adryan.app.database.DBAdapterActividad;
import com.adryan.app.database.DBAdapterAsignacion;
import com.adryan.app.database.DBAdapterCentroCosto;
import com.adryan.app.database.DBAdapterCompania;
import com.adryan.app.database.DBAdapterCuadroTareo;
import com.adryan.app.database.DBAdapterDistribucion;
import com.adryan.app.database.DBAdapterLabor;
import com.adryan.app.database.DBAdapterMarcaciones;
import com.adryan.app.database.DBAdapterParametro;
import com.adryan.app.database.DBAdapterPeriodo;
import com.adryan.app.database.DBAdapterSupervisor;
import com.adryan.app.database.DBAdapterTrabajador;
import com.adryan.app.database.SqlDBConnection;
import com.adryan.app.mail.GMailSender;
import com.adryan.app.mail.Mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Sincronizacion extends Activity {

    private DBAdapterActividad dbAct;
    private DBAdapterLabor dbLabor;
    private DBAdapterTrabajador dbTrab;
    private DBAdapterCentroCosto dbCC;
    private DBAdapterSupervisor dbSup;
    private DBAdapterPeriodo dbPer;
    private DBAdapterMarcaciones dbMarca;
    private DBAdapterCompania dbCia;
    private DBAdapterAsignacion dbAsig;
    private DBAdapterDistribucion dbDist;
    private DBAdapterCuadroTareo dbCuadro;
    private DBAdapterParametro dbPara;
    private TextView lblLog;
    private WifiManager wifiMng;
    private ConnectivityManager cnMgn;
    private LogFile logger;
    SqlDBConnection sqlDB;
    Context context;
    CallSOAP soap;
    static boolean swTermino = true;
    static final String FILE_ADRYAN = "adryan";
    private String ARCHIVO = "";

    private Mail mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sincronizacion);

        context = this.getBaseContext();
        sqlDB = new SqlDBConnection(this.getBaseContext());
        logger = new LogFile(this.getBaseContext());
        soap = new CallSOAP(getBaseContext());

        try {
            dbAct = new DBAdapterActividad(this);
            dbLabor = new DBAdapterLabor(this);
            dbTrab = new DBAdapterTrabajador(this);
            dbCC = new DBAdapterCentroCosto(this);
            dbSup = new DBAdapterSupervisor(this);
            dbMarca = new DBAdapterMarcaciones(this);
            dbPer = new DBAdapterPeriodo(this);
            dbCia = new DBAdapterCompania(this);
            dbAsig = new DBAdapterAsignacion(this);
            dbDist = new DBAdapterDistribucion(this);
            dbCuadro = new DBAdapterCuadroTareo(this);
            dbPara = new DBAdapterParametro(this);

            dbAct.open();
            dbLabor.open();
            dbTrab.open();
            dbCC.open();
            dbSup.open();
            dbMarca.open();
            dbPer.open();
            dbCia.open();
            dbAsig.open();
            dbDist.open();
            dbCuadro.open();
            dbPara.open();
        } catch (SQLException ex){
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            dbAct.close();
            dbLabor.close();
            dbTrab.close();
            dbCC.close();
            dbSup.close();
            dbMarca.close();
            dbPer.close();
            dbCia.close();
            dbAsig.close();
            dbDist.close();
            dbCuadro.close();
            dbPara.close();
        }

        Button btnCancelar = (Button) findViewById(R.id.btnCancelarSinc);
        lblLog = (TextView) findViewById(R.id.lblLog);

        if (!VariablesGenerales.SW_BD) {
            btnCancelar.setText("Salir");
            //sincronizar(1);
            VariablesGenerales.SW_BD = true;
            Intent itIngreso = new Intent();
            itIngreso.setClass(Sincronizacion.this, Ingreso.class);
            startActivity(itIngreso);
            dbAct.close();
            dbLabor.close();
            dbTrab.close();
            dbCC.close();
            dbSup.close();
            dbMarca.close();
            dbPer.close();
            dbCia.close();
            dbAsig.close();
            dbDist.close();
            dbCuadro.close();
            finish();
        }

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swTermino) {
                    dbAct.close();
                    dbLabor.close();
                    dbTrab.close();
                    dbCC.close();
                    dbSup.close();
                    dbMarca.close();
                    dbPer.close();
                    dbCia.close();
                    dbAsig.close();
                    dbDist.close();
                    dbCuadro.close();
                    Intent itPrincipal = new Intent();
                    itPrincipal.setClass(Sincronizacion.this, Principal.class);
                    startActivity(itPrincipal);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Aún sigue en proceso de sincrinización", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnSyncIn = (Button) findViewById(R.id.btnSynIn);
        btnSyncIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sincronizar();
            }
        });

        Button btnSyncOut = (Button) findViewById(R.id.btnSyncWiFi);
        btnSyncOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lblLog.setText("");
                sincronizar();
            }
        });

        Button btnGenTXT = (Button) findViewById(R.id.btnGenTXT);
        btnGenTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                generateTXT();
                sendMail();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    private void sendMail() {
        String[] toArr = {dbPara.getValorParam("7")}; //{"vic88dark@gmail.com"}; // This is an array, you can add more emails, just separate them with a coma
        mail = new Mail(dbPara.getValorParam("5"),dbPara.getValorParam("6")); //new Mail("tareoadryan@gmail.com","v1ct0r201088");
        mail.setHost(dbPara.getValorParam("2")); //mail.setHost("smtp.gmail.com");
        mail.setPortSMTP(dbPara.getValorParam("3")); //mail.setPortSMTP("465");
        mail.setPortSSL(dbPara.getValorParam("4")); //mail.setPortSSL("465");
        mail.setTo(toArr); // load array to setTo function
        mail.setFrom(dbPara.getValorParam("5")); //mail.setFrom("tareoadryan@gmail.com"); // who is sending the email
        mail.setSubject(dbPara.getValorParam("8")); //mail.setSubject("Demo de envio");
        mail.setBody(dbPara.getValorParam("9")); //mail.setBody("Exito!!");

        try {
            File ruta_sd = Environment.getExternalStorageDirectory();
            String archivo = ruta_sd.getAbsolutePath() + "/" + ARCHIVO + ".txt";
            mail.addAttachment(archivo, (ARCHIVO + ".txt"));  // path to file you want to attach
            if(mail.send()) {
                // success
                Toast.makeText(Sincronizacion.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
            } else {
                // failure
                Toast.makeText(Sincronizacion.this, "Email was not sent.", Toast.LENGTH_LONG).show();
            }
        } catch(Exception e) {
            // some other problem
            e.printStackTrace();
            Toast.makeText(Sincronizacion.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
        }
    }

    private void generateTXT() {
        ArrayList<Marcaciones> lstMarcas = dbMarca.listaMarcasActuales();

        if (!lstMarcas.isEmpty()) {
            Calendar fecha = Calendar.getInstance();
            SimpleDateFormat strDateFormat = new SimpleDateFormat("yyyyMMdd");
            String fechaHoy = strDateFormat.format(fecha.getTime());
            ARCHIVO = "Marcaciones_" + VariablesGenerales.ID_DISPOSITIVO + "_" + fechaHoy;
            String contenido = "";

            try {
                File ruta_sd = Environment.getExternalStorageDirectory();
                File archivo = new File(ruta_sd.getAbsolutePath(), (ARCHIVO + ".txt"));

                lblLog.append("Archivo: " + archivo.getName() + "\n");

                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(archivo));

                for (Marcaciones marca : lstMarcas){
                    contenido += marca.getLabor() + "," + marca.getActividad() + "," + marca.getCentroCosto() + ",";
                    contenido += marca.getTrabajador() + "," + marca.getSupervisor() + ",";
                    contenido += marca.getCompania() + "," + marca.getFecha() + ",";
                    contenido += marca.getHora() + ",8," +  marca.getPeriodo() +"\n";
                }

                osw.write(contenido);
                osw.flush();
                osw.close();
                lblLog.append("Archivo creado... \n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkRed() {
        boolean sw = false;
        wifiMng = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        cnMgn = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);

        if (!wifiMng.isWifiEnabled()) {
            wifiMng.setWifiEnabled(true);
            Toast.makeText(getBaseContext(), "WiFi Activado.", Toast.LENGTH_SHORT).show();
        }

        if (cnMgn != null) {
            NetworkInfo info = cnMgn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    sw = true;
                } else {
                    Toast.makeText(getBaseContext(), "No conectado.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return sw;
    }

    private void sincronizar() {
        wifiMng = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        cnMgn = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);

        if (!wifiMng.isWifiEnabled()) {
            wifiMng.setWifiEnabled(true);
            Toast.makeText(getBaseContext(), "WiFi Activado.", Toast.LENGTH_SHORT).show();
        }

        if (cnMgn != null) {
            NetworkInfo info = cnMgn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    new AsyncBearings().execute();
                } else {
                    Toast.makeText(getBaseContext(), "No conectado.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void sinchronizeIn(){

        // Compania
        //if (dbCia.deleteCia()) {
        //    lblLog.append("Limpiando compañías... \n");
        //    new AsyncCompany().execute();
        //}

        // Trabajador
        //if (dbTrab.deleteTodosTrabajadores()) {
        //    lblLog.append("Limpiando trabajadores... \n");
        //    new AsyncEmployee().execute();
        //}

        // Centro de Costo
        //if (dbCC.deleteCentrosCostos()){
        //    lblLog.append("Limpiando centros de costo... \n");
        //    new AsyncCostCenter().execute();
        //}

        // Actividad
        //if (dbAct.deleteActividades()) {
        //    lblLog.append("Limpiando actividades... \n");
        //    new AsyncActivity().execute();
        //}

        // Labor
        //if (dbLabor.deleteLabores()) {
        //    lblLog.append("Limpiando labores... \n");
        //    new AsyncLabor().execute();
        //}

        // Periodo Actual
        //if (dbPer.deletePeriodos()) {
        //    lblLog.append("Limpiando periodos... \n");
        //    new AsyncCurrentPeriod().execute();
        //}

        // Tareador
        //if (dbSup.deleteSupervisores()) {
        //    lblLog.append("Limpiando tareadores... \n");
        //    new AsyncTareador().execute();
        //}

        // Asignaciones
        //if (dbAsig.deleteAsignacion()) {
        //    lblLog.append("Limpiando asignaciones... \n");
        //    new AsyncAllocation().execute();
        //}

        // Ditribucion
        //if (dbDist.deleteDistribucion()) {
        //    lblLog.append("Limpiando distribuciones... \n");
        //    new AsyncDistribution().execute();
        //}

    }

    private void sinchronizeOut() {
        // Marcaciones
        //new AsyncBearings().execute();

        // Cuadro de Tareo
        //new AsyncTareoBox().execute();

        // Horas trabajadas
        //new AsyncHoursWorked().execute();

        // Distribucion tareo
        //new AsyncTareoDistibution().execute();
    }

    public class AsyncTareoDistibution extends AsyncTask<Void, Void, String> {
        String msj = "";
        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Inicio de Distribución... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            //CallSOAP soap = new CallSOAP(getBaseContext());
            String valor = "";
            try {
                valor = soap.call("syncTareoDistibution");
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncTareoDistibution ->" + e.getMessage());
                msj = "Cancelado Por Perdida de RED... \n";
                cancel(true);
            }

            return valor;
        }

        @Override
        protected void onPostExecute(String str) {
            lblLog.append("Distribución de tareo sincronizadas... \n");
            if (!msj.equals("")) lblLog.append(msj);
            new AsyncCompany().execute();
            //super.onPostExecute(str);
        }
    }

    public class AsyncHoursWorked extends AsyncTask<Void, Void, String> {
        String msj = "";

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Inicio de Horas Trabajadas... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            //CallSOAP soap = new CallSOAP(getBaseContext());
            String valor = "";
            try {
                valor = soap.call("syncHoursWorded");
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncHoursWorked -> " + e.getMessage());
                msj = "Cancelado Por Perdida de RED... \n";
                cancel(true);
            }

            return valor;
        }

        @Override
        protected void onPostExecute(String str) {
            lblLog.append("Horas trabajadas sincronizadas... \n");
            if (!msj.equals("")) lblLog.append(msj);
            new AsyncTareoDistibution().execute();
            //super.onPostExecute(str);
        }
    }

    public class AsyncBearings extends AsyncTask<Void, Void, Integer> {
        int ct = 0;
        ArrayList<Marcaciones> lstMarcas = dbMarca.listaMarcasActuales();
        String[] names = {"ac","cc","co","ct","cia","fm","hm","ht","lb","pe"};
        String msj = "";

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Inicio de Marcaciones... \n");
            swTermino = false;
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                if (!lstMarcas.isEmpty()) {
                    for (Marcaciones marca : lstMarcas){
                        String[] values = {
                                marca.getActividad(), marca.getCentroCosto(), marca.getTrabajador(),
                                marca.getSupervisor(), marca.getCompania(), marca.getFecha(),
                                marca.getHora(), "8", marca.getLabor(), marca.getPeriodo()
                        };

                        if (soap.callIns("insBearing", names, values).trim().equalsIgnoreCase("true")) {
                            dbMarca.deleteByMarca(marca);
                            ct++;
                            //logger.addRecordLog("AsyncBearings -> " + ct);
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncBearings -> " + e.getMessage());
                msj = "Cancelado Por Perdida de RED... \n";
                cancel(true);
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Marcaciones enviadas(" + i.toString() + ")... \n");
            if (!msj.equals("")) lblLog.append(msj);
            new AsyncTareoBox().execute();
            //super.onPostExecute(integer);
        }
    }

    public class AsyncTareoBox extends AsyncTask<Void, Void, Integer> {
        int ct = 0;
        ArrayList<Cuadro> lstCuadro = dbCuadro.listaCuadroActual();
        String[] names = {"ac","cc","av","ct","cia","fm","np","ht","lb","pe","ob","os","re","un"};
        String msj = "";

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Inicio de Cuadro de Tareo... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                if (!lstCuadro.isEmpty()) {
                    for (Cuadro cuadro : lstCuadro){
                        String[] values = {
                                cuadro.getActividad(), cuadro.getCentroCosto(), cuadro.getAvance(),
                                cuadro.getSupervisor(), cuadro.getCompania(), cuadro.getFecha(),
                                cuadro.getNroPersonal(), cuadro.getHoras(), cuadro.getLabor(),
                                cuadro.getPeriodo(), cuadro.getObjetivo(), cuadro.getObservacion(),
                                cuadro.getRendimiento(), cuadro.getUnidMedida()
                        };

                        if (soap.callIns("insTareoBox", names, values) == "true") { ct++; }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncTareoBox -> " + e.getMessage());
                msj = "Cancelado Por Perdida de RED... \n";
                cancel(true);
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Cuadro de Tareo enviados(" + i + ")... \n");
            if (dbCuadro.deleteCuadro()) { lblLog.append("Cuadros de tareo eliminados..."); }
            if (!msj.equals("")) lblLog.append(msj);
            new AsyncHoursWorked().execute();
            //super.onPostExecute(integer);
        }
    }

    public class AsyncDistribution extends AsyncTask<Void, Void, Integer> {
        int ct = 0;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Sincronizando distribuciones... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Distribucion[] lstDist = null;
            try {
                if (checkRed()) {
                    lstDist = soap.getList("getAllDistributions", Distribucion[].class);

                    if (lstDist.length > 0) {
                        if (dbDist.deleteDistribucion()) {
                            for (Distribucion dist : lstDist) {
                                if (dbDist.insertDistribucion(dist)) ct++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncDistribution -> " + e.getMessage());
                return 0;
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Distribuciones sincronizadas(" + i + ")... \n");
            lblLog.append("Se culminó la sincronización");
            logger.addRecordLog("Distribuciones sincronizadas(" + i + ")...");
            swTermino = true;
            //super.onPostExecute(integer);
        }
    }

    public class AsyncAllocation extends AsyncTask<Void, Void, Integer> {
        int ct = 0;

        @Override
        protected void onPreExecute() {
            lblLog.append("Sincronizando asignaciones... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Asignacion[] lstAsig = null;
            try {
                if (checkRed()) {
                    lstAsig = soap.getList("getAllAllocations", Asignacion[].class);
                    if (lstAsig.length > 0) {
                        if (dbAsig.deleteAsignacion()) {
                            for (Asignacion asig : lstAsig) {
                                if (dbAsig.insertAsignacion(asig)) ct++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncAllocation -> " + e.getMessage());
                return 0;
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Asignaciones sincronizadas(" + i + ")... \n");
            logger.addRecordLog("Asignaciones sincronizadas(" + i + ")...");
            new AsyncDistribution().execute();
            //super.onPostExecute(integer);
        }
    }

    public class AsyncTareador extends AsyncTask<Void, Void, Integer> {
        int ct = 0;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Sincronizando tareadores... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Tareador[] lstTar = null;
            try {
                if (checkRed()) {
                    lstTar = soap.getList("getAllTareadores", Tareador[].class);

                    if (lstTar.length > 0) {
                        if (dbSup.deleteSupervisores()) {
                            for (Tareador tar : lstTar) {
                                if (dbSup.nuevoSupervisor(tar)) ct++;
                            }
                        }

                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncTareador -> " + e.getMessage());
                return 0;
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Tareadores sincronizados(" + i + ")... \n");
            logger.addRecordLog("Tareadores sincronizados(" + i + ")...");
            new AsyncAllocation().execute();
            //super.onPostExecute(integer);
        }
    }

    public class AsyncCurrentPeriod extends AsyncTask<Void, Void, Integer> {
        int ct = 0;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Sincronizando periodos... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Periodo[] lstPer = null;
            try {
                if (checkRed()) {
                    lstPer = soap.getList("getAllCurrentPeriod", Periodo[].class);

                    if (lstPer.length > 0) {
                        if (dbPer.deletePeriodos()) {
                            for (Periodo per : lstPer) {
                                if (dbPer.insertPeriodo(per)) ct++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncCurrentPeriod -> " + e.getMessage());
                return 0;
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Periodos sincronizados(" + i + ")... \n");
            logger.addRecordLog("Labores sincronizados(" + i + ")...");
            new AsyncTareador().execute();
            //super.onPostExecute(integer);
        }
    }

    public class AsyncLabor extends AsyncTask<Void, Void, Integer> {
        int ct = 0;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Sincronizando labores... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Labor[] lstLab = null;
            try {

                if (checkRed()) {
                    lstLab = soap.getList("getAllLabors", Labor[].class);

                    if (lstLab.length > 0) {
                        if (dbLabor.deleteLabores()) {
                            for (Labor lab : lstLab) {
                                if (dbLabor.nuevaLabor(lab)) ct++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncLabor -> " + e.getMessage());
                return 0;
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Labores sincronizados(" + i + ")... \n");
            logger.addRecordLog("Labores sincronizados(" + i + ")...");
            new AsyncCurrentPeriod().execute();
            //super.onPostExecute(integer);
        }
    }

    public class AsyncActivity extends AsyncTask<Void, Void, Integer> {
        int ct = 0;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Sincronizando actividades... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Actividad[] lstAct = null;
            try {
                if (checkRed()) {
                    lstAct = soap.getList("getAllActivities", Actividad[].class);

                    if (lstAct.length > 0) {
                        if (dbAct.deleteActividades()) {
                            for (Actividad act : lstAct) {
                                if (dbAct.nuevaActividad(act)) ct++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncActivity -> " + e.getMessage());
                return 0;
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Actividades sincronizadas(" + i + ")... \n");
            logger.addRecordLog("Actividades sincronizadas(" + i + ")...");
            new AsyncLabor().execute();
            //super.onPostExecute(integer);
        }
    }

    public class AsyncCostCenter extends AsyncTask<Void, Void, Integer> {
        int ct = 0;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            lblLog.append("Sincronizando centros de costo... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            CentroCosto[] lstCC = null;
            try {
                if (checkRed()) {
                    lstCC = soap.getList("getAllCostCenter", CentroCosto[].class);

                    if (lstCC.length > 0) {
                        if (dbCC.deleteCentrosCostos()){
                            for (CentroCosto cc : lstCC) {
                                if (dbCC.nuevoCentroCosto(cc)) ct++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncCostCenter -> " + e.getMessage());
                return 0;
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Centros de Costo sincronizados(" + i + ")... \n");
            logger.addRecordLog("Centros de Costo sincronizados(" + i + ")...");
            new AsyncActivity().execute();
            //super.onPostExecute(integer);
        }
    }

    public class AsyncEmployee extends AsyncTask<Void, Void, Integer> {
        int ct = 0;

        @Override
        protected void onPreExecute() {
            lblLog.append("Sincronizando trabajadores... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
            //super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Trabajador[] trabs = null;
            try {
                if (checkRed()) {
                    trabs = soap.getList("getAllEmployees", Trabajador[].class);

                    if (trabs.length > 0) {
                        if (dbTrab.deleteTodosTrabajadores()) {
                            for (Trabajador emp : trabs) {
                                if (dbTrab.nuevoTrabajador(emp)) ct++;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncEmployee -> " + e.getMessage());
                return 0;
            }

            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Trabajadores sincronizados(" + i + ")... \n");
            logger.addRecordLog("Trabajadores sincronizados(" + i + ")...");
            new AsyncCostCenter().execute();
            //super.onPostExecute(integer);
        }
    }

    public class AsyncCompany extends AsyncTask<Void, Void, Integer> {
        int ct = 0;

        @Override
        protected void onPreExecute() {
            lblLog.append("Sincronizando compañías... \n");
            if (!checkRed()) {
                lblLog.append("Se perdio la conexión... \n");
                swTermino = true;
                cancel(true);
            }
            //super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Compania[] cias;
            try {
                if (checkRed()) {
                    cias = soap.getList("getAllCompanies", Compania[].class);
                    if (cias.length > 0) {
                        if (dbCia.deleteCia()) {
                            for (Compania cia : cias) {
                                if (dbCia.insertCia(cia)) ct++;
                            }
                        }
                    }

                    if (cias.length > 0) {

                    }
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Ocurrio un Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                logger.addRecordLog("AsyncEmployee -> " + e.getMessage());
                return 0;
            }


            return ct;
        }

        @Override
        protected void onPostExecute(Integer i) {
            lblLog.append("Compañías sincronizadas(" + i + ")... \n");
            logger.addRecordLog("Compañías sincronizadas(" + i + ")...");
            new AsyncEmployee().execute();
            //super.onPostExecute(integer);
        }
    }

}