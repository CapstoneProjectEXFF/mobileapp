package service;

import android.app.ProgressDialog;
import android.content.Context;

public class UserService {

    public static ProgressDialog setUpProcessDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Đang Xử Lí...");
        progressDialog.setTitle("Xin Chờ");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }

}
