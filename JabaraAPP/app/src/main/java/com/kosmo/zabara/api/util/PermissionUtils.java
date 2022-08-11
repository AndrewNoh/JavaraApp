package com.kosmo.zabara.api.util;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.Vector;

public class PermissionUtils {

    //권한요청시 각 권한을 구분하기 위한 요청코드값(식별자)
    public static final int MY_PERMISSIONS =1000;

    private Context context;
    private AppCompatActivity activity;
    //앱에서 필요한 권한들
    private String[] requestPermissions;

    //허용이 안된 권한들을 저장할 컬렉션
    private List<String> deniedPermissions = new Vector<>();

    public PermissionUtils(Context context, AppCompatActivity activity, String[] requestPermissions) {
        this.context = context;
        this.activity = activity;
        this.requestPermissions = requestPermissions;
    }

    //허용되지 않은 권한 있는지 체크
    public boolean checkPermission() {
        deniedPermissions.clear();
        for(String permission:requestPermissions){
            int checkPermission= ActivityCompat.checkSelfPermission(context,permission);
            //권한이 없는 경우 deniedPermissions에 저장
            if(checkPermission== PackageManager.PERMISSION_DENIED)
                deniedPermissions.add(permission);
        }
        if(!deniedPermissions.isEmpty()){
            return false;
        }
        return true;
    }

    //사용자에게 권한 허용 요청
    public void requestPermissions(){

        ActivityCompat.requestPermissions(activity,deniedPermissions.toArray(new String[deniedPermissions.size()]), MY_PERMISSIONS);
    }

    //요청한 권한에 대한 결과값 처리
    public boolean requestPermissionsResult(int requestCode,
                                              @NonNull String[] permissions,
                                              @NonNull int[] grantResults){

        if(requestCode == MY_PERMISSIONS && (grantResults.length >0)) {
            deniedPermissions.clear();
            for(int i=0; i< grantResults.length; i++){
                //grantResults 가
                // 0이면 사용자가 허용
                //-1이면 사용자가 거부
                //permissions중에 하나라도 허용하지 않는 권한이 있다면 false를 리턴
                if(grantResults[i] == -1){
                    deniedPermissions.add(permissions[i]);
                }
            }
            if(!deniedPermissions.isEmpty()){
                return false;
            }

        }
        return true;
    }

}
