package com.fhh.phone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CALL_PHONE = 0;
    private final int PERMISSION_REQUEST_SEND_SMS = 1;
    private final int PERMISSION_REQUEST_READ_CONTACTS = 2;
    private final int PERMISSION_REQUEST_READ_CALL_LOG = 3;
    private boolean couldCall;
    private boolean couldSendSMS;
    private boolean couldReadContacts;
    private boolean couldReadCallLog;

    private List<Call> callLogs = new ArrayList<>();

    private MyClickListener myClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(PERMISSION_REQUEST_READ_CALL_LOG);
        checkPermission(PERMISSION_REQUEST_READ_CONTACTS);

        if (couldReadContacts && couldReadCallLog) {
            readCallData();
            createPage();
        } else {
            checkPermission(PERMISSION_REQUEST_READ_CALL_LOG);
            checkPermission(PERMISSION_REQUEST_READ_CONTACTS);
            Toast.makeText(MainActivity.this, "请授予权限,否则无法正常工作", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    public void readCallData() {
        Cursor cursor = null;

        Uri uri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                CallLog.Calls.CACHED_NAME,//姓名0
                CallLog.Calls.NUMBER, // 号码1
                CallLog.Calls.DATE,   // 日期2
                CallLog.Calls.TYPE,   // 类型：来电、去电、未接3
                CallLog.Calls.DURATION,//通话时长4
                CallLog.Calls.CACHED_PHOTO_URI//头像5
        };
        try {
            cursor = getContentResolver().query(uri, projection, null, null, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                if (name == null) {
                    name = "无";
                }
                String number = cursor.getString(1);
                String date = new SimpleDateFormat("yyyy年MM月dd日").format(cursor.getLong(2));
                String time = new SimpleDateFormat("HH:mm").format(cursor.getLong(2));
                int type = cursor.getInt(3);
                String temp = cursor.getString(4);
                String duration;
                if (type == Call.CALL_IN) {
                    duration = "呼入" + temp + "s";
                } else if (type == Call.CALL_OUT) {
                    duration = "呼出" + temp + "s";
                } else {
                    duration = "未接通";
                }
                String photo = cursor.getString(5);
                Uri photoUri = null;
                if (photo != null) {
                    photoUri = Uri.parse(photo);
                }
                Call oneCall = new Call(name, number, photoUri, date, time, type, duration);
                callLogs.add(oneCall);
            }
            Collections.reverse(callLogs);//倒序,把最近的电话放到最前面
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void createPage() {
        ViewPager pager = findViewById(R.id.viewpager_calls);
        TabLayout tabLayout = findViewById(R.id.tab_calls);

        List<Call> callIn = new ArrayList<>();
        List<Call> callOut = new ArrayList<>();
        List<Call> callMissed = new ArrayList<>();

        List<CallRecordsFragment> fragments = new ArrayList<>();
        CallRecordsFragment callInFragment = new CallRecordsFragment();
        CallRecordsFragment callOutFragment = new CallRecordsFragment();
        CallRecordsFragment callMissedFragment = new CallRecordsFragment();

        for (Call call : callLogs) {
            switch (call.getType()) {
                case Call.CALL_IN:
                    callIn.add(call);
                    break;
                case Call.CALL_OUT:
                    callOut.add(call);
                    break;
                case Call.CALL_MISSED:
                    callMissed.add(call);
                    break;
                default:
                    break;
            }
        }
        setMyClickListener();

        callInFragment.setMyCallList(callIn);
        callOutFragment.setMyCallList(callOut);
        callMissedFragment.setMyCallList(callMissed);
        callInFragment.setMyClickListener(myClickListener);
        callOutFragment.setMyClickListener(myClickListener);
        callMissedFragment.setMyClickListener(myClickListener);

        fragments.add(callInFragment);
        fragments.add(callOutFragment);
        fragments.add(callMissedFragment);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(pager);

    }


    public void askForPermission(int requestCode) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE:
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
                break;
            case PERMISSION_REQUEST_SEND_SMS:
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                break;
            case PERMISSION_REQUEST_READ_CONTACTS:
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
                break;
            case PERMISSION_REQUEST_READ_CALL_LOG:
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, PERMISSION_REQUEST_READ_CALL_LOG);
                break;
            default:
                break;
        }
    }

    public void checkPermission(int requestCode) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    couldCall = true;
                } else {
                    askForPermission(PERMISSION_REQUEST_CALL_PHONE);
                }
                break;
            case PERMISSION_REQUEST_SEND_SMS:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    couldSendSMS = true;
                } else {
                    askForPermission(PERMISSION_REQUEST_SEND_SMS);
                }
                break;
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    couldReadContacts = true;
                } else {
                    askForPermission(PERMISSION_REQUEST_READ_CONTACTS);
                }
                break;
            case PERMISSION_REQUEST_READ_CALL_LOG:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    couldReadCallLog = true;
                } else {
                    askForPermission(PERMISSION_REQUEST_READ_CALL_LOG);
                }
                break;
            default:
                break;
        }
    }

    private void setMyClickListener() {
        myClickListener = new MyClickListener() {
            @Override
            public void onClick(String name, String theNumber) {
                final String number = theNumber;
                AlertDialog.Builder chooseDialog = new AlertDialog.Builder(MainActivity.this);
                chooseDialog.setTitle("请选择：");
                if (!(name.equals("无"))) {
                    chooseDialog.setMessage("向" + name + "(" + number + ")");
                }
                chooseDialog.setMessage("向" + number);
                chooseDialog.setCancelable(true);
                chooseDialog.setPositiveButton("拨打电话", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermission(PERMISSION_REQUEST_CALL_PHONE);
                        if (couldCall) {
                            makeCall(number);
                        } else {
                            Toast.makeText(MainActivity.this, "无法拨打电话，请提供权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                chooseDialog.setNegativeButton("发送短信", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermission(PERMISSION_REQUEST_SEND_SMS);
                        if (couldSendSMS) {
                            sendSMS(number);
                        } else {
                            Toast.makeText(MainActivity.this, "无法发送短信，请提供权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                chooseDialog.show();
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    couldCall = true;
                } else {
                    couldCall = false;
                    Toast.makeText(this, "你拒绝了授权拨打电话", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    couldSendSMS = true;
                } else {
                    couldSendSMS = false;
                    Toast.makeText(this, "你拒绝了授权发送短信", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    couldReadContacts = true;
                } else {
                    couldReadContacts = false;
                    Toast.makeText(this, "你拒绝了读取联系人权限,软件无法正常工作.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case PERMISSION_REQUEST_READ_CALL_LOG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    couldReadCallLog = true;
                } else {
                    couldReadCallLog = false;
                    Toast.makeText(this, "你拒绝了读取通话记录权限,软件无法正常工作.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void makeCall(String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendSMS(String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("smsto:" + number));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
