package com.adryan.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.adryan.app.comunes.LogFile;
import com.adryan.app.comunes.VariablesGenerales;
import com.adryan.app.database.DBAdapterMarcaciones;
import com.adryan.app.database.DBAdapterTrabajador;

import java.sql.SQLException;


public class DetalleMarcas extends Activity {

    private DBAdapterMarcaciones dbMrca;
    private Cursor cursorMarca;
    private String codigoUnico, idMarca;

    private LogFile logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_marcas);

        logger = new LogFile(this.getBaseContext());

        Button btnCancelar = (Button) findViewById(R.id.btnCancelarDato);
        Button btnNuevo = (Button) findViewById(R.id.btnNuevoDato);
        Button btnEditar = (Button) findViewById(R.id.btnEditDato);
        GridView gdDatos = (GridView) findViewById(R.id.gdvDatos);

        VariablesGenerales.ID_MARCA = "";
        VariablesGenerales.COD_TRABAJADOR = "";
        VariablesGenerales.UPD_MARCA = false;
        codigoUnico = "";
        idMarca = "";

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idMarca != "" && codigoUnico != "") {
                    logger.addRecordLog("Editar marcacion: " + idMarca + " - " + codigoUnico);
                    VariablesGenerales.ID_MARCA = idMarca;
                    VariablesGenerales.COD_TRABAJADOR = codigoUnico;
                    idMarca = "";
                    codigoUnico = "";
                    VariablesGenerales.UPD_MARCA = true;
                    Intent itNuevo = new Intent();
                    itNuevo.setClass(DetalleMarcas.this, MarcaManual.class);
                    startActivity(itNuevo);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Seleccionar para editar información", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logger.addRecordLog("Nueva marcacion.");
                VariablesGenerales.ID_MARCA = "";
                VariablesGenerales.COD_TRABAJADOR = "";
                VariablesGenerales.UPD_MARCA = false;
                Intent itNuevo = new Intent();
                itNuevo.setClass(DetalleMarcas.this, MarcaManual.class);
                startActivity(itNuevo);
                finish();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try{
            dbMrca = new DBAdapterMarcaciones(this);
            dbMrca.open();
        } catch (SQLException ex) {
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG);
        }

        try {
            cursorMarca = dbMrca.listaDetMarcaciones("1");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (cursorMarca.getCount() > 0) {
            try {
                String[] cols = new String[]{"matricula", "trabajador", "centrocosto", "actividad", "labor", "fecha", "_id", "codigounico"};
                int[] names = new int[]{R.id.txtMatriculaDet, R.id.txtTrabajadorDet, R.id.txtCentroCostoDet, R.id.txtActividadDet,
                        R.id.txtLaborDet, R.id.txtHoraMarcaDet, R.id.txtIdMarca, R.id.txtCodigoUnicoDet};
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.datatable_layout, cursorMarca, cols, names);
                gdDatos.setAdapter(adapter);
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

        gdDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                codigoUnico = ((TextView) view.findViewById(R.id.txtCodigoUnicoDet)).getText().toString();
                idMarca = ((TextView) view.findViewById(R.id.txtIdMarca)).getText().toString();
                //Toast.makeText(getBaseContext(), codigoUnico, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
