package com.linkitsoft.beepvending.Activities.Dispense;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.View;

import com.linkitsoft.beepvending.Activities.MainActivity;
import com.linkitsoft.beepvending.databinding.ActivityTestDispenseBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Constants.AppConstants;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class TestDispense extends AppCompatActivity {
    ActivityTestDispenseBinding binding;

    Thread mThread;
    SerialPort serialPort;
    String devPath;
    int baudrate;
    int no = 0;
    byte[] ackBytes = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x42, 0x00, 0x43};
    private ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
    Queue<byte[]> queue = new LinkedList<byte[]>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private List<countObj> arr_count ;

    private Handler mHandler;
    private boolean checkPosAll = false;
    private int count1 = 0;

    private  int posall = -1;
    private int queueNow = 0;

    private int mInterval = 3000;
    private String TAG = "Dispense", LDispense = "";
    private int product_hdhInt =-1;
    private SweetAlertDialog sdthankyou;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestDispenseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        devPath = AppConstants.devPath;
        baudrate = AppConstants.baudRate;
        arr_count = new ArrayList<>();

        clickListener();

    }


    private void clickListener() {
        binding.btnDispense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                    int hdh = Integer.parseInt(((EditText) findViewById(R.id.hdh)).getText().toString());
//                    int hdh = Integer.parseInt(binding.etLane.getText().toString().trim());
//                    short[] hdhbyte = HexDataHelper.Int2Short16_2(hdh);
//
//                    //货道号补齐两字节
//                    if (hdhbyte.length == 1) {
//                        short temp = hdhbyte[0];
//                        hdhbyte = new short[2];
//                        hdhbyte[0] = 0;
//                        hdhbyte[1] = temp;
//                    }
//
//
//                    byte[] data = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte[0], (byte) hdhbyte[1], 0x00};
//                    data[data.length - 1] = (byte) HexDataHelper.computerXor(data, 0, data.length - 1);
//                    writeCmd(data);



                    countObj obj = new countObj();
                   //ye position number set honay wali bus
                    obj.setproduct(binding.etLane.getText().toString());
                    obj.setcount(0);
                    obj.setposition(0);
                    arr_count.add(obj);

                    setUpForDispense();

       

                } catch (
                        Exception e) {

                }
            }
        });

        binding.btnConnectPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serialPort == null) {
                    bindSerialPort();
                } else {
                    try {
                        serialPort.close();
                        serialPort = null;
                    } catch (
                            Exception e) {
                        Log.d("SerialPortException:: ", "" + e);
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                if (count1 < 2) {
                    if (!checkPosAll) {
                        posall = 0;
                    } else {
//                        yaha kuch change huga
                        posall = arr_count.get(queueNow).getcount();
                    }
// yaha kuch change huga -- idr kuch change ni hoga
                    int hdhInt = Integer.parseInt(arr_count.get(queueNow).getproduct());
                    System.out.println("loggings-check-sent-product-" + String.valueOf(hdhInt));
                    short[] hdhbyte = HexDataHelper.Int2Short16_2(hdhInt);
                    if (hdhbyte.length == 1) {
                        short temp = hdhbyte[0];
                        hdhbyte = new short[2];
                        hdhbyte[0] = 0;
                        hdhbyte[1] = temp;
                    }
                    byte[] data = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte[0], (byte) hdhbyte[1], 0x00};
                    data[data.length - 1] = (byte) HexDataHelper.computerXor(data, 0, data.length - 1);
                    queue.add(data);
                    //queueNow = arr_count.get(queueNow).getcount();
                    //handler.removeCallbacksAndMessages(null);
                    count1++;
                } else {
                    //handler11.removeCallbacksAndMessages(null);
                    stopRepeatingTask();
                }
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void setUpForDispense() {

        String text = "";
        handler.post(new RunableEx(text) {
            public void run() {
                runQueue();
            }
        });

    }

    private void runQueue() {
        mHandler = new Handler();
        checkPosAll = false;
        startRepeatingTask();
    }

    private void startRepeatingTask() {
        count1 = 0;
        mStatusChecker.run();
    }

    //*****************************************************Binding Serial Port**************************************************************
    private void bindSerialPort() {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File serialFile = new File(devPath);
                    if (!serialFile.exists() || baudrate == -1) {
                        return;
                    }

                    try {
                        serialPort = new SerialPort(serialFile, baudrate, 0);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onSerialPortConnectStateChanged(true);
                            }
                        });
                        readSerialPortData();
                    } catch (
                            Exception e) {
                        e.printStackTrace();
                    }
                } catch (
                        Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (null != serialPort) {
                        try {
                            serialPort.close();
                            serialPort = null;
                        } catch (
                                Exception e) {
                        }
                    }

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSerialPortConnectStateChanged(false);
                    }
                });
            }
        });
        mThread.start();
    }

    private void readSerialPortData() {
        while (true) {
            try {
                if (null == serialPort) {
                    Thread.sleep(1000);
                    continue;
                }
                int available = serialPort.getInputStream().available();
                if (0 == available) {
                    Thread.sleep(10);
                    continue;
                }

                byte[] data = readBytes(serialPort.getInputStream(), available);
                mBuffer.write(data);
                while (true) {
                    byte[] bytes = mBuffer.toByteArray();
                    int start = 0;
                    int cmdCount = 0;
                    boolean shuldBreak = false;
                    for (; start <= bytes.length - 5; start++) {
                        if ((short) (bytes[start] & 0xff) == 0xFA && (short) (bytes[start + 1] & 0xff) == 0xFB) {
                            try {
                                int len = bytes[start + 3];
                                byte[] cmd = new byte[len + 5];
                                System.arraycopy(bytes, start, cmd, 0, cmd.length);
                                cmdCount++;
                                proccessCmd(cmd);


                                //计算还有多少剩余字节要解析，没有的跳出等待接收新的字节，有则继续处理
                                int remain = bytes.length - start - cmd.length;
                                if (0 == remain) {
                                    shuldBreak = true;
                                    mBuffer.reset();
                                    break;
                                }
                                byte[] buffer2 = new byte[remain];
                                System.arraycopy(bytes, start + cmd.length, buffer2, 0, buffer2.length);
                                mBuffer.reset();
                                mBuffer.write(buffer2);
                            } catch (
                                    Exception e) {
                                shuldBreak = true;
                                //因数据包不全，导致越界异常，直接跳出即可
                            }
                            break;
                        }
                    }
                    if (0 == cmdCount || shuldBreak) {
                        break;
                    }

                }

            } catch (
                    Exception e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] readBytes(InputStream stream, int length) throws IOException {
        byte[] buffer = new byte[length];

        int total = 0;

        while (total < length) {
            int count = stream.read(buffer, total, length - total);
            if (count == -1) {
                break;
            }
            total += count;
        }

        if (total != length) {
            throw new IOException(String.format("Read wrong number of bytes. Got: %s, Expected: %s.", total, length));
        }

        return buffer;
    }

    public void proccessCmd(byte[] cmd) {

        if (LDispense.equalsIgnoreCase("true")) {
            Log.i(TAG, "loggings-check-response-" + HexDataHelper.hex2String(cmd));
        }
        System.out.println("loggings-check-response-" + HexDataHelper.hex2String(cmd));

        if (0x41 == (short) (cmd[2] & 0xff)) {
            //收到POLL包

            if (queue.size() == 0) {
                writeCmd(ackBytes);
            } else {
                writeCmd(queue.poll());
            }
        } else if (0x42 == (short) (cmd[2] & 0xff)) {
            // 收到ACK
            Log.d("TAG:", "ACK Received");
//            handler11.removeCallbacksAndMessages(null);

            stopRepeatingTask();
            //writeCmd(ackBytes);
        } else if (0x04 == (short) (cmd[2] & 0xff)) {
            stopRepeatingTask();
            writeCmd(ackBytes);
            //update status here
            if (0x01 == (short) (cmd[5] & 0xff)) {
                //dispensing
//                handler3.removeCallbacksAndMessages(null);

                int product = Integer.parseInt(String.format("%02X", cmd[7]), 16);
                if (LDispense.equalsIgnoreCase("true")) {
                    Log.i(TAG, "loggings-check-dispensing-product-" + String.valueOf(product));
                }

            } else if (0x02 == (short) (cmd[5] & 0xff)) {
                //dispensed
                queueNow = queueNow + 1;

                product_hdhInt = -1;
                for (int i = 0; i < arr_count.size(); i++) {
                    if (posall == arr_count.get(i).getcount()) {
                        arr_count.get(i).setposition(1);
                        break;
                    }
                }

                if (queueNow == arr_count.size()) {
                    //quit
                } else {
                    //callquanQueue(queueNow);
                    checkPosAll = true;
                    //mHandler = new Handler();
                    startRepeatingTask();
                }

                int product = Integer.parseInt(String.format("%02X", cmd[7]), 16);
                if (LDispense.equalsIgnoreCase("true")) {
                    Log.i(TAG, "loggings-check-dispensed-product-" + String.valueOf(product));
                }
                int mstatus = Integer.parseInt(String.format("%02X", cmd[5]), 16);
                updateStatus(product, "1", mstatus);
            } else {
                //dispensed with no dropsensor
                queueNow = queueNow + 1;

                int product = Integer.parseInt(String.format("%02X", cmd[7]), 16);
                product_hdhInt = -1;

//                Ajay Work:
                for(int i=0;i<arr_count.size();i++) {
                    if(posall==arr_count.get(i).getcount()) {
                        arr_count.get(i).setposition(1);
                        break;
                    }
                }

                if(queueNow==arr_count.size()){
                    //quit
                }else {
                    //callquanQueue(queueNow);
                    checkPosAll=true;
                    //mHandler = new Handler();
                    startRepeatingTask();
                }

                int mstatus = Integer.parseInt(String.format("%02X", cmd[5]), 16);
                if(LDispense.equalsIgnoreCase("true")) {
                    Log.i(TAG, "loggings-check-dispensed-product-" + String.valueOf(product));
                }
                updateStatus(product, "2",mstatus);

            }
        }
    }

    private void updateStatus(int product, String s, int mstatus) {

        String productcode = String.valueOf(product);
        boolean checkhere = false;

        boolean check = false;
        for (int j = 0; j < arr_count.size(); j++) {
            System.out.println("loggings-count-" + arr_count.get(j).getposition() + "-product-" + arr_count.get(j).getproduct());
            if (arr_count.get(j).getproduct().equalsIgnoreCase(productcode) && arr_count.get(j).getposition() != 1) {
                check = true;
            }
        }

        if (!check) {
            System.out.println("loggings-countmodel"+product+ "pos" + s + "status" + mstatus);
        }

        String text = "";
        if (checkhere) {
            handler.post(new RunableEx(text) {
                public void run() {
                    // one by one updating the prducts dispense here
//                    adapter.update(cartListModels);

                    boolean checkc = false;
                    for (int i = 0; i < arr_count.size(); i++) {
                        if (arr_count.get(i).getposition() == 0) {
                            checkc = true;
                            break;
                        }
                    }
                    if (!checkc) {
                        serialPort.close();
                        if (LDispense.equalsIgnoreCase("true")) {
                            Log.i(TAG, "Serial port closed");
                        }
                        serialPort = null;

                        Handler handler13 = new Handler();
                        handler13.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler13.removeCallbacksAndMessages(null);
                                Intent intent = new Intent(TestDispense.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                if (LDispense.equalsIgnoreCase("true")) {
                                    Log.i(TAG, "Dispensing Activity Closed");
                                }
                            }
                        }, 10000);
                        if (LDispense.equalsIgnoreCase("true")) {
                            Log.i(TAG, "End Dispensing Activity");
                        }


                        //all products dispensed


//                        updatetransactiondb(allstatuses);

                        sdthankyou = new SweetAlertDialog(TestDispense.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Thank you.")
                                .setContentText("Your order is dispensed please get ready to pickup.");
                        sdthankyou.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                handler13.removeCallbacksAndMessages(null);
                                sdthankyou.dismissWithAnimation();
                                Intent intent = new Intent(TestDispense.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                if (LDispense.equalsIgnoreCase("true")) {
                                    Log.i(TAG, "Dispensing Activity Closed");
                                }
                            }
                        });
                        sdthankyou.show();

                    }
                }
            });
        }

    }

    public void writeCmd(byte[] cmd) {
        try {
            serialPort.getOutputStream().write(cmd);
            serialPort.getOutputStream().flush();
            addText(">> " + HexDataHelper.hex2String(cmd));
        } catch (
                Exception e) {

        }
    }

    public void addText(String text) {
        handler.post(new RunableEx(text) {
            public void run() {
                Log.d("Cmd logs: ", "" + text);
            }
        });

    }

    public void onSerialPortConnectStateChanged(boolean connected) {
        if (connected) {
            binding.etSerialPortStatus.setText("Serial Port :" + connected);
        } else {
            binding.etSerialPortStatus.setText("Serial Port :" + connected);
        }
    }
    //*****************************************************Binding Serial Port**************************************************************

    //    *********************************************************Dispense*************************************************************
    public int getNextNo() {
        no++;
        if (no >= 255) {
            no = 0;
        }
        return no;
    }
//    *********************************************************Dispense*************************************************************

}