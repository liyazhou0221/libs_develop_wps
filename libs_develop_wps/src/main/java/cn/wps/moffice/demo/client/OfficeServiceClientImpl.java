package cn.wps.moffice.demo.client;
import android.os.RemoteException;

import cn.wps.moffice.client.OfficeAuthorization;
import cn.wps.moffice.client.OfficeEventListener;
import cn.wps.moffice.client.OfficeServiceClient;
import cn.wps.moffice.demo.service.BaseService;

public class OfficeServiceClientImpl extends OfficeServiceClient.Stub {

    protected MOfficeClientService service = null;

    protected OfficeAuthorizationImpl authorization = null;
    protected OfficeEventListenerImpl eventListener = null;

    public OfficeServiceClientImpl(MOfficeClientService service) {
        this.service = service;
        authorization = new OfficeAuthorizationImpl(service);
        eventListener = new OfficeEventListenerImpl(service);
    }

    @Override
    public OfficeAuthorization getAuthorization() throws RemoteException {
        return authorization;
    }

    @Override
    public OfficeEventListener getOfficeEventListener() throws RemoteException {
        return eventListener;
    }
}
