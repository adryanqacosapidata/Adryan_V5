package com.adryan.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adryan.app.Entidades.Trabajador;
import com.adryan.app.Entidades.Usuario;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by VFQS on 20/03/14.
 */
public class DBAdapterUsuario {
    // campos de la base de datos
    public static final String ROW_ID = "_id";
    public static final String ROW_USUARIO = "usuario";
    public static final String ROW_CODIGOUNICO = "codigounico";
    public static final String ROW_COMPANIA = "compania";
    public static final String DATABASE_TABLE = "usuarios";

    private Context contexto;
    private SQLiteDatabase db;
    //private DatabaseHelper dbHelper;
    private Database dbHelper;

    public DBAdapterUsuario(Context context){
        this.contexto = context;
    }

    public DBAdapterUsuario open() throws SQLException{
        //dbHelper = new DatabaseHelper(contexto);
        //db = dbHelper.getWritableDatabase();
        dbHelper = new Database(contexto);
        db = dbHelper.getDataBase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    private ContentValues insertValues(Usuario usu){
        ContentValues valores = new ContentValues();
        valores.put(ROW_CODIGOUNICO, usu.getCodigoUnico());
        valores.put(ROW_USUARIO, usu.getUsuario());
        return valores;
    }

    public boolean nuevoUsuario(Usuario usu){
       ContentValues valores = insertValues(usu);
        return db.insert(DATABASE_TABLE, null, valores) > 0;
    }

    public boolean deleteTodosUsuarios(){
        return db.delete(DATABASE_TABLE,null ,null ) >= 0;
    }

    public String getByUsuarioDocuemento(String user, String document, String cia) {
        String codigoUnico = "";
        String sql = "Select t.codigounico From supervisor u Inner Join Trabajador t " +
                "On u.codigounico = t.codigounico And u.compania = t.compania Where Trim(t.documento) = Trim('" + document + "') And " +
                "Upper(Trim(u.usuario)) = Upper(Trim('" + user + "')) And Trim(u.compania) = Trim('" + cia + "');";
        Cursor cDatos = db.rawQuery(sql, null);
        int  i = 0;

        if (cDatos != null) {
            for(cDatos.moveToFirst(); !cDatos.isAfterLast(); cDatos.moveToNext()){
                codigoUnico = cDatos.getString(0);
            }
        }
        return codigoUnico;
    }

}
