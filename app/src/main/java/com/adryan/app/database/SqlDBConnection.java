package com.adryan.app.database;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.adryan.app.comunes.LogFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by vquispe on 26/08/2014.
 */
public class SqlDBConnection {

    private Connection connection = null;
    private final static String databaseName = "ADRYAN_ECOSAC";
    private final static String userName = "sa";
    //private final static String password =  "R3cr1d14r/*.-2015";
    private final static String password =  "cosapisoft";
    //private final static String url= "jdbc:jtds:sqlserver://10.10.50.245:1433/" + databaseName + ";instance=MSSQLSERVER";
    private final static String url= "jdbc:jtds:sqlserver://128.1.4.120:1433/" + databaseName + ";instance=MSSQLSERVER";

    LogFile logger;

    public SqlDBConnection(Context context) {
        logger =  new LogFile(context);
    }

    private Connection getConnection(){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(url, userName, password);
            logger.addRecordLog("Conectado a: " + url + " - user: " + userName + " - pwd: " + password);
        } catch (SQLException se) {
            Log.e("SQLException getConnection", se.toString());
            logger.addRecordLog("SQLException getConnection : " + se.toString());
        } catch (ClassNotFoundException e) {
            Log.e("ClassNotFoundException getConnection", e.toString());
            logger.addRecordLog("ClassNotFoundException getConnection : " + e.toString());
        } catch (Exception e) {
            Log.e("Exception getConnection", e.toString());
            logger.addRecordLog("SQLException getConnection : " + e.toString());
        }
        return connection;
    }

    private void closeConnection(){
        try {
            connection.close();
            logger.addRecordLog("Cerrando conexion.");
        } catch (Exception e){
            Log.e("Close Connection: ", e.toString());
            logger.addRecordLog("Close Connection: " + e.toString());
        }
    }

    public boolean ExecSQL(String strSql) {
        try {
            connection = this.getConnection();
            if (connection != null) {
                Statement select = connection.createStatement();
                select.executeQuery(strSql);
                select.close();
                closeConnection();
                return true;
            }
            else
                return false;
        } catch (Exception e) {
            Log.e("ExecSQL: ", e.toString());
            logger.addRecordLog("ExecSQL: " + e.toString());
            return false;
        }
    }

    public Object[][] ExecSQLRS(String strSql) {
        ResultSet result = null;
        try {
            connection = this.getConnection();
            if (connection != null) {
                Statement select = connection.createStatement();
                result = select.executeQuery(strSql);

                if (result != null) {
                    ResultSetMetaData rsMetaData = result.getMetaData();
                    int columnCount = rsMetaData.getColumnCount();
                    ArrayList result2 = new ArrayList();
                    Object[] header = new Object[columnCount];

                    for (int i = 1; i <= columnCount; ++i) {
                        Object label = rsMetaData.getColumnLabel(i);
                        header[i - 1] = label;
                    }

                    while (result.next()) {
                        Object[] str = new Object[columnCount];
                        for (int i = 1; i <= columnCount; ++i) {
                            Object obj = result.getObject(i);
                            str[i - 1] = obj;
                        }
                        result2.add(str);
                    }

                    int resultLength = result2.size();
                    Object[][] finalResult = new Object[resultLength + 1][columnCount];
                    finalResult[0] = header;

                    int posRes = 1;
                    for (int i = 0; i < resultLength; i++) {
                        Object[] row = (Object[]) result2.get(i);
                        finalResult[posRes] = row;
                        posRes++;
                    }
                    select.close();
                    result.close();
                    closeConnection();
                    return finalResult;
                } else { return null; }
            } else { return null; }
        } catch (SQLException se) {
            Log.e("SQLException ExecSQLRS",se.toString());
            logger.addRecordLog("SQLException ExecSQLRS: " + se.toString());
            closeConnection();
            return null;
        } catch (Exception e) {
            Log.e("Exception ExecSQLRS",e.toString());
            logger.addRecordLog("Exception ExecSQLRS: " + e.toString());
            closeConnection();
            return null;
        }
    }
}
