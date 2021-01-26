package cn.wps.moffice.demo.util;

import android.content.Context;
import android.os.RemoteException;


import cn.wps.moffice.demo.R;
import cn.wps.moffice.service.OfficeService;
import cn.wps.moffice.service.app.App;


public class PremiumUtils {

    public static void doPremium(int id, Context context, OfficeService mService, String docPath) {
        try {
            if (mService == null || mService.getApps() == null) {
                return;
            }
            App app = mService.getApps().waitForApp();
            if (app != null)
                if (id == R.id.pic2doc) {
                    app.pic2doc(docPath);
                } else if (id == R.id.pic2ET) {
                    app.pic2ET(docPath);
                } else if (id == R.id.pic2PPT) {
                    app.pic2PPT(docPath);
                } else if (id == R.id.pic2PDF) {
                    app.pic2PDF(docPath);
                } else if (id == R.id.openCameraOcr) {
                    app.openCameraOcr();
                } else if (id == R.id.pdfkit2doc) {
                    app.pdfkit2doc(docPath);
                } else if (id == R.id.pdfkitOcr2Text) {
                    app.pdfkitOcr2Text(docPath);
                } else if (id == R.id.pdfkitSign) {
                    app.pdfkitSign(docPath);
                } else if (id == R.id.pdfkitFileSizeReduce) {
                    app.pdfkitFileSizeReduce(docPath);
                } else if (id == R.id.pdfkitMerge) {
                    app.pdfkitMerge(docPath);
                } else if (id == R.id.pdfkitExtract) {
                    app.pdfkitExtract(docPath);
                } else if (id == R.id.extractFile) {
                    app.extractFile(docPath);
                } else if (id == R.id.mergeFile) {
                    app.mergeFile(docPath);
                } else if (id == R.id.pptPlayRecord) {
                    app.pptPlayRecord(docPath);
                } else if (id == R.id.documentBatchSlim) {
                    app.documentBatchSlim();
                } else if (id == R.id.openCameraDoc) {
                    app.openCameraDoc();
                } else if (id == R.id.openCameraPPT) {
                    app.openCameraPPT();
                } else if (id == R.id.pdfkitAnotation) {
                    app.pdfkitAnotation(docPath);
                } else if (id == R.id.pdfkitDocument2pdf) {
                    app.pdfkitDocument2pdf(docPath);
                } else if (id == R.id.goShareplay) {
                    app.goShareplay();
                }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public static void doAThread(int id, Context context, OfficeService mService, String docPath) {
        new PremiumThread(id, context, mService, docPath).start();
    }


    static class PremiumThread extends Thread {

        private int id;
        private Context context;
        private OfficeService service;
        private String docPath;

        public PremiumThread(int id, Context context, OfficeService service, String docPath) {
            this.id = id;
            this.context = context;
            this.service = service;
            this.docPath = docPath;
        }

        @Override
        public void run() {
            doPremium(id, context, service, docPath);
        }
    }
}
