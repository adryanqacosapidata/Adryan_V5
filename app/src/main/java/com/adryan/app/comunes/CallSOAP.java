package com.adryan.app.comunes;

/**
 * Created by vquispe on 12/06/2015.
 */

import android.content.Context;

import com.google.gson.GsonBuilder;

import org.apache.http.NameValuePair;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class CallSOAP {
    public final String SOAP_ACTION = "http://tempuri.org/";
    public  final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
    //public  final String SOAP_ADDRESS = "http://10.10.50.245/AdryanAPI/Adryan.asmx";
    public  final String SOAP_ADDRESS = "http://" + VariablesGenerales.SERVER + "/AdryanAPI/Adryan.asmx";

    LogFile logger;

    public CallSOAP(Context context) {
        logger = new LogFile(context);
    }

    public String call(String method)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,method);
        //PropertyInfo pi=new PropertyInfo();
        //request.addProperty(pi);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response = null;
        try
        {
            httpTransport.call((SOAP_ACTION + method), envelope);
            //SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
            response = envelope.getResponse();
            //response = soapPrimitive.toString();
            //logger.addRecordLog("call->Response-> " + response.toString());
        }
        catch (Exception exception)
        {
            response = exception.toString();
            logger.addRecordLog("call->Exception-> " + response.toString());
        }
        return response.toString();
    }

    public <T> T getList(String methName, final Class<T> objectClass) {
        String resultString = call(methName);
        return new GsonBuilder().create().fromJson(resultString, objectClass);
    }

    public String callIns(String method, String[] names, String[] values)
    {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,method);
        PropertyInfo pi;
        for (int i = 0; i < names.length; i++) {
            pi = new PropertyInfo();
            pi.setNamespace(WSDL_TARGET_NAMESPACE);
            pi.setName(names[i].toString().trim());
            pi.setValue(values[i].toString().trim());
            pi.setType(String.class);
            request.addProperty(pi);
            //request.addProperty(names[i].toString().trim(),values[i].toString().trim());
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        Object response = null;
        try
        {
            httpTransport.debug = true;
            httpTransport.call((SOAP_ACTION + method), envelope);
            //SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
            response = envelope.getResponse();
            //response = soapPrimitive.toString();
            logger.addRecordLog("callIns->Response->" + method + "->" + response.toString());
        }
        catch (Exception exception)
        {
            response = exception.toString();
            logger.addRecordLog("callIns->Exception->" + method + "->" + response.toString());
        }
        return response.toString();
    }

}
