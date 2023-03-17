package com.linkitsoft.beepvending.Activities.Dispense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.serialport.SerialPort;
import android.util.Log;
import android.view.View;

import com.linkitsoft.beepvending.databinding.ActivityTestDispenseBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import Constants.AppConstants;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestDispenseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        devPath = AppConstants.devPath;
        baudrate = AppConstants.baudRate;

        clickListener();

    }


    private void clickListener() {
        binding.btnDispense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                    int hdh = Integer.parseInt(((EditText) findViewById(R.id.hdh)).getText().toString());
                    int hdh = Integer.parseInt(binding.etLane.getText().toString().trim());
                    short[] hdhbyte = HexDataHelper.Int2Short16_2(hdh);

                    //货道号补齐两字节
                    if (hdhbyte.length == 1) {
                        short temp = hdhbyte[0];
                        hdhbyte = new short[2];
                        hdhbyte[0] = 0;
                        hdhbyte[1] = temp;
                    }


                    byte[] data = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte[0], (byte) hdhbyte[1], 0x00};
                    data[data.length - 1] = (byte) HexDataHelper.computerXor(data, 0, data.length - 1);
                    writeCmd(data);

       

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
        addText("<<" + HexDataHelper.hex2String(cmd));

        if (0x41 == (short) (cmd[2] & 0xff)) {
            //收到POLL包

            if (queue.size() == 0) {
                writeCmd(ackBytes);
            } else {
                writeCmd(queue.poll());
            }
        } else if (0x42 == (short) (cmd[2] & 0xff)) {
            // 收到ACK
        } else {
            writeCmd(ackBytes);
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