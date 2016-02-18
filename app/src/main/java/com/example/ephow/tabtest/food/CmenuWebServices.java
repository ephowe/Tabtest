package com.example.ephow.tabtest.food;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by 1 on 2015/8/18.
 */

public class CmenuWebServices implements BaseCmenu, BaseCmenu.RPC_Methods {

    private final String SERVICES;
    // 调用的webservice命令空间
    public static final String PACE = "http://tempuri.org/";

    private String mServer_url = null;

    private final HttpTransportSE httpSE;
    private final SoapSerializationEnvelope envelope;

    public CmenuWebServices(CmenuJson cj) {
        SERVICES = cj.getValue("webservice","relativepath") +
                cj.getValue("webservice","name");
        mServer_url = "http://" + cj.getValue("webservice", "addr") + ":" +
                cj.getValue("webservice", "port") + SERVICES;
        // 创建SoapSerializationEnvelope对象并传入SOAP协议的版本号
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // 创建HttpTransportSE传输对象 传入webservice服务器地址
        httpSE = new HttpTransportSE(mServer_url);
        httpSE.debug = true;
        httpSE.setUrl(mServer_url);
        // 设置与.NET提供的Web service保持有良好的兼容性
        envelope.dotNet = true;
    }

    @Override
    public String invoke_RPC(SoapObject rpc) {
        //获取方法名
        final String Methond = rpc.getName();
        //导入待发送的SOAP
        envelope.bodyOut = rpc;
        FutureTask future = new FutureTask(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String rtn = null;
                        //线程类可以考虑做成一个CMENU通用接口类,负责HTTP连接
                        // 调用HttpTransportSE对象的call方法来调用 webserice ,需要加连接时间限制比如10秒连不上算超时
                        httpSE.call(PACE + Methond, envelope);     //负责HTTP连接
                        if (envelope.getResponse() != null) {
                            // 获取服务器响应返回的SOAP消息,并返回解析信息
                            //SoapObject rtn = (SoapObject) soapserial.bodyIn;
                            //获取返回方法
                            //SoapObject result = (SoapObject) rtn.getProperty("pLoginResult");
                            SoapObject result = (SoapObject) ( (SoapObject) envelope.bodyIn ).getProperty(Methond + "Result");
                            //获取返回值
                            rtn = result.getProperty(0).toString();
                            //if (rtn.equals("0") || rtn.equals("1"))
                            rtn = result.getProperty(1).toString();
                        }
                        return rtn;
                    }
                });
        new Thread(future).start();
        try {
            return (String)future.get( 10000, TimeUnit.MILLISECONDS );
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            future.cancel(true);
        } catch (ExecutionException e) {
            future.cancel(true);
        } catch (TimeoutException e) {
            future.cancel(true);
            return "超时无应答.";
        }
        return "调用异常.";
    }











}
