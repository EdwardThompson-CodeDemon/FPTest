package sparta.realm.fptest;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.digitalpersona.uareu.Compression;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.digitalpersona.uareu.dpfj.CompressionImpl;
import com.gemalto.wsq.WSQEncoder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import sparta.realm.Activities.SpartaAppCompactFingerPrintActivity;
import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.fptest.Storage.StorageManager;
import sparta.realm.spartautils.biometrics.DataMatcher;
import sparta.realm.spartautils.biometrics.fp.Globals;
import sparta.realm.spartautils.bluetooth.bt_probe;
import sparta.realm.spartautils.matching_interface;
import sparta.realm.spartautils.svars;

import static sparta.realm.spartautils.biometrics.fp.fp_handler_stf_usb_8_inch.main_fmd_format;

public class MainActivity extends SpartaAppCompactFingerPrintActivity implements bt_probe.data_interface {

    boolean reg_mode = true;
    EditText data_source1, data_source2;
    Button rm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        svars.set_current_device(Realm.context, svars.DEVICE.UAREU.ordinal());
        rm = findViewById(R.id.mode);
        data_source1 = findViewById(R.id.data1);
        data_source2 = findViewById(R.id.data2);


        data_source1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageManager.selectFile(Environment.getExternalStorageDirectory().toString(), new StorageManager.FileBrowserInterface() {
                    @Override
                    public void onFleSelected(String filePath) {
                        data_source1.setText(filePath);
                    }

                    @Override
                    public void onCanceled() {

                    }
                }, false);
            }
        });
        data_source2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageManager.selectFile(Environment.getExternalStorageDirectory().toString(), new StorageManager.FileBrowserInterface() {
                    @Override
                    public void onFleSelected(String filePath) {
                        data_source2.setText(filePath);
                    }

                    @Override
                    public void onCanceled() {

                    }
                }, false);
            }
        });


        rm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                reg_mode=!reg_mode;
                rm.setText(reg_mode ? "Registering" : "Verifying ...");
//                matchWsq("");
//                rm.setText( "Matching wsq ...");
//                match_wsq(data_source1.getText().toString(),data_source2.getText().toString());
            }
        });
        findViewById(R.id.match_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bitmap bitmap = BitmapFactory.decodeFile(data_source1.getText().toString());
                Bitmap bitmap2 = BitmapFactory.decodeFile(data_source2.getText().toString());
////                match_image(overlayBitmap(bitmap));
//                convertWSQ((bitmap));
//                convertWSQ(overlayBitmap(bitmap));
                matchImages(overlayBitmap(bitmap), overlayBitmap(bitmap2));

            }
        });
        findViewById(R.id.match_wsq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rm.setText("Matching WSQs ...");
                matchWsq(data_source1.getText().toString(), data_source2.getText().toString());

            }
        });
        findViewById(R.id.match_iso).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rm.setText("Matching ISOs ...");
                matchISO(readFile(data_source1.getText().toString()), readFile(data_source2.getText().toString()));

            }
        });
        findViewById(R.id.match_wsq_iso).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rm.setText("Matching WSQ vs ISOs ...");
                matchWSQISO((data_source1.getText().toString()), readFile(data_source2.getText().toString()));

            }
        });
    }

    public String ImageToIso(Bitmap bmp) {
        byte[] wsq = new WSQEncoder(bmp).encode();
        Log.e("Converted wsq1", "" + wsqData.length);
        CompressionImpl cmp = new CompressionImpl();
        try {
            cmp.Start();
            cmp.SetWsqBitrate(90, 0);
            Compression.RawImage rawCompress = cmp.ExpandRaw(wsq, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST);
            cmp.Finish();
            Fmd fmd = UareUGlobal.GetEngine().CreateFmd(rawCompress.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);

            String ISOTemplate1 = Base64.encodeToString(fmd.getData(), Base64.DEFAULT);

            return ISOTemplate1;
        } catch (Exception ex) {
            Log.e("WSQ ERROR=>", "" + ex);
            try {
                cmp.Finish();
            } catch (Exception exx) {

            }
        }

        return null;
    }

    public String WsqToIso(byte[] wsq) {
        CompressionImpl cmp = new CompressionImpl();
        try {
            cmp.Start();
            cmp.SetWsqBitrate(90, 0);
            Compression.RawImage rawCompress = cmp.ExpandRaw(wsq, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST);
            cmp.Finish();
            Fmd fmd = UareUGlobal.GetEngine().CreateFmd(rawCompress.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);

            String ISOTemplate1 = Base64.encodeToString(fmd.getData(), Base64.DEFAULT);

            return ISOTemplate1;
        } catch (Exception ex) {
            Log.e("WSQ ERROR=>", "" + ex);
            try {
                cmp.Finish();
            } catch (Exception exx) {

            }
        }

        return null;
    }

    void setupMatching() {

        int limit = 200;

        int offset = 0;
        String limit_stt = "LIMIT " + limit + " OFFSET " + offset;
        Cursor c = DatabaseManager.database.query("SELECT sid,data FROM member_data_table " + limit_stt);
        int result_count = c.getCount();
        int i = 0;
        int fp_count = 0;
        while (result_count >= limit) {
            LinkedHashMap<String, Fmd> fpId_fmd = new LinkedHashMap<>();
            if (c.moveToFirst()) {
                do {
                    fpId_fmd.put(c.getString(0), dm.base_64_tofmd(c.getString(1)));

                } while (c.moveToNext());

            }

            fp_count += fpId_fmd.size();
            result_count = c.getCount();
            f_main_map.put(i, fpId_fmd);
            i++;
            limit_stt = "LIMIT " + limit + " OFFSET " + i * limit;
            c = DatabaseManager.database.query("SELECT sid,data FROM member_data_table " + limit_stt);
        }
        c.close();
    }

    String readFile(String file_path) {
        File file = new File(file_path);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        return text.toString();
    }

    HashMap<Integer, LinkedHashMap<String, Fmd>> f_main_map = new HashMap<>();
    public static String logTag = "FPTEST";
    matching_interface mint = new matching_interface() {
        @Override
        public void on_match_complete(boolean match_found, String mils) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Realm.context, "Match complete " + match_found, Toast.LENGTH_LONG).show();

                }
            });


        }

        @Override
        public void on_match_found(String employee_id, String data_index, String match_time, int v_type, int verrification_mode) {

        }

        @Override
        public void on_finger_match_found(String fp_id, int score, String match_time) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Realm.context, "Match found:" + fp_id + " in mills: " + match_time, Toast.LENGTH_LONG).show();
                }
            });
            Log.e(logTag, "Match found:" + fp_id + " in mills: " + match_time);

        }

        @Override
        public void on_match_progress_changed(int progress) {

        }

        @Override
        public void on_match_faild_reason_found(int reason, String employee_id) {

        }
    };
    DataMatcher dm = new DataMatcher();

    private Bitmap overlayBitmap(Bitmap sourceBMP) {


        Bitmap tempBitmap = Bitmap.createBitmap(256, 360, Bitmap.Config.RGB_565);

        Canvas tempCanvas = new Canvas(tempBitmap);
// tempCanvas.dr
        tempCanvas.drawColor(Color.WHITE);
        tempCanvas.drawBitmap(sourceBMP, 0, 0, new Paint());
        return tempBitmap;
    }

    @Override
    public void on_result_obtained(String capt_result) {
        super.on_result_obtained(capt_result);
        Log.e("Inserted =>", "Res : " + capt_result);
//    try{
//        if (new DataMatcher().base_64_tofmd("NDY0ZDUyMDAyMDMyMzAwMDAwMDAwMWIwMDAwMDAxMDAwMTY4MDBjNTAwYzUwMTAwMDAwMDQ3NDMw\n" +
//                "    MGE5MDEzNGY5MTQwMGQxMDEwMzgzMTQwMGVhMDBlZTBhMTQwMGJiMDAxN2MwMTQwMDk2MDAxZjQw\n" +
//                "    MTQwMGFlMDAyMGJiMTQwMGJiMDAyMDNiMTQwMDdhMDAyMjQ0MTQwMGEyMDAyMjNiMTQwMDE4MDAy\n" +
//                "    M2M0MTQwMDMyMDAyNDQ1MTQwMDZmMDAyNDRlMTQwMDg5MDAyYjM4MTQwMDM1MDAzNGNhMTQwMGFi\n" +
//                "    MDAzNTMxMTQwMGUxMDA0MjJlMTQwMDFiMDA1MTUzMTQwMGQ4MDA1MWE3MTQwMDQyMDA1NmRhMTQw\n" +
//                "    MDYwMDA1Nzc0MTQwMDVmMDA1ODczMTQwMDdhMDA1ZDg5MTQwMDE4MDA2MzU5MTQwMDQzMDA2OTYz\n" +
//                "    MTQwMGM4MDA2YTFiMTQwMDY0MDA2YmZhMTQwMGE4MDA3MzE2MTQwMGFiMDA3NDk2MTQwMGMxMDA4\n" +
//                "    NjEwMTQwMGYyMDA4ODFkMTQwMGE3MDA4YThiMTQwMDg5MDA4YjA2MTQwMGM5MDA4YjhlMTQwMGQ0\n" +
//                "    MDA4YjE1MTQwMDkzMDA5MDA3MTQwMGRhMDA5Njk0MTQwMGU5MDA5Njk4MTQwMGE4MDA5YTA5MTQw\n" +
//                "    MGUxMDBhMjE2MTQwMGQ4MDBhODkyMTQwMDhhMDBhYzAxMTQwMGU4MDBhYzE0MTQwMDUzMDBiMzcz\n" +
//                "    MTQwMDRlMDBiNWYzMTQwMGFkMDBiNTA0MTQwMDI1MDBiODZlMTQwMDM0MDBjMDczMTQwMDJhMDBj\n" +
//                "    MWYyMTQwMDU3MDBjNGY1MTQwMDIyMDBjYmVkMTQwMDhmMDBkNWZjMTQwMDZhMDBkZGY1MTQwMGUy\n" +
//                "    MDBlMjA5MTQwMDk0MDBlMzdiMTQwMDNiMDBlY2YxMTQwMDZmMDBlZDc1MTQwMDFjMDBlZWVjMTQw\n" +
//                "    MGMyMDAxMTQyMTQwMDg0MDBmMmZhMTQwMGUxMDBmMjg3MTQwMDk1MDEwMjdiMTQwMDg3MDAxNGM4\n" +
//                "    MTQwMGM2MDEwNDAyMTQwMDU3MDExOGYyMTQwMDM5MDExYmVhMTQwMDMwMDEyNTY3MTQwMDk1MDAx\n" +
//                "    NGM1MTQwMDAw") == null) {
//
//            Log.e(logTag, "SDK Template is invalid");
//        } else {
//            // fph_8_inch.match_global(capt_result, "464D520020323000000000CC00000200020000C500C5010000002F1C0049009BFE00005700B5F900003B00C5FC00006B00E8D900003100D99200008100EFED00003400E6950000B20067F000008A00F1DE0000B600CFE200004200410400007100FBC800004E00FBB500001500DC3300003300FA9E00003D0100BE00004900308400007E002BFD00001100EAB400008A002C7E00003F010A370000830024750000900026F5000036010EB000009200177E0000AF001FF60000AD000FF00000B700137000000600020006000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001D32000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
//            Log.e(logTag,"Match result:" + fph_8_inch.match_global(capt_result, "NDY0ZDUyMDAyMDMyMzAwMDAwMDAwMWIwMDAwMDAxMDAwMTY4MDBjNTAwYzUwMTAwMDAwMDQ3NDMw\n" +
//                    "    MGE5MDEzNGY5MTQwMGQxMDEwMzgzMTQwMGVhMDBlZTBhMTQwMGJiMDAxN2MwMTQwMDk2MDAxZjQw\n" +
//                    "    MTQwMGFlMDAyMGJiMTQwMGJiMDAyMDNiMTQwMDdhMDAyMjQ0MTQwMGEyMDAyMjNiMTQwMDE4MDAy\n" +
//                    "    M2M0MTQwMDMyMDAyNDQ1MTQwMDZmMDAyNDRlMTQwMDg5MDAyYjM4MTQwMDM1MDAzNGNhMTQwMGFi\n" +
//                    "    MDAzNTMxMTQwMGUxMDA0MjJlMTQwMDFiMDA1MTUzMTQwMGQ4MDA1MWE3MTQwMDQyMDA1NmRhMTQw\n" +
//                    "    MDYwMDA1Nzc0MTQwMDVmMDA1ODczMTQwMDdhMDA1ZDg5MTQwMDE4MDA2MzU5MTQwMDQzMDA2OTYz\n" +
//                    "    MTQwMGM4MDA2YTFiMTQwMDY0MDA2YmZhMTQwMGE4MDA3MzE2MTQwMGFiMDA3NDk2MTQwMGMxMDA4\n" +
//                    "    NjEwMTQwMGYyMDA4ODFkMTQwMGE3MDA4YThiMTQwMDg5MDA4YjA2MTQwMGM5MDA4YjhlMTQwMGQ0\n" +
//                    "    MDA4YjE1MTQwMDkzMDA5MDA3MTQwMGRhMDA5Njk0MTQwMGU5MDA5Njk4MTQwMGE4MDA5YTA5MTQw\n" +
//                    "    MGUxMDBhMjE2MTQwMGQ4MDBhODkyMTQwMDhhMDBhYzAxMTQwMGU4MDBhYzE0MTQwMDUzMDBiMzcz\n" +
//                    "    MTQwMDRlMDBiNWYzMTQwMGFkMDBiNTA0MTQwMDI1MDBiODZlMTQwMDM0MDBjMDczMTQwMDJhMDBj\n" +
//                    "    MWYyMTQwMDU3MDBjNGY1MTQwMDIyMDBjYmVkMTQwMDhmMDBkNWZjMTQwMDZhMDBkZGY1MTQwMGUy\n" +
//                    "    MDBlMjA5MTQwMDk0MDBlMzdiMTQwMDNiMDBlY2YxMTQwMDZmMDBlZDc1MTQwMDFjMDBlZWVjMTQw\n" +
//                    "    MGMyMDAxMTQyMTQwMDg0MDBmMmZhMTQwMGUxMDBmMjg3MTQwMDk1MDEwMjdiMTQwMDg3MDAxNGM4\n" +
//                    "    MTQwMGM2MDEwNDAyMTQwMDU3MDExOGYyMTQwMDM5MDExYmVhMTQwMDMwMDEyNTY3MTQwMDk1MDAx\n" +
//                    "    NGM1MTQwMDAw"));
//
//        }
//    }catch (Exception ex){
//        Log.e(logTag, "Conversion Error Invalid Template");
//
//    }
        if (reg_mode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ContentValues cv = new ContentValues();
                    cv.put("data", capt_result);
                    cv.put("data_index", "" + 1);
                    cv.put("transaction_no", svars.device_specific_transaction_no(Realm.context));
                    cv.put("data_type", "" + svars.data_type_indexes.fingerprints);
                    cv.put("sid", "" + svars.device_specific_transaction_no(Realm.context));

                    cv.put("sync_status", "p");
                    cv.put("user_id", svars.user_id(act));

                    //  sd.database.insert("member_data_table", null, cv);
                    long insrt = DatabaseManager.database.insert("member_data_table", null, cv);
                    Log.e("Inserted =>", "fingerprint : " + insrt);
                    Toast.makeText(Realm.context, "Saved ISO Fp: " + insrt, Toast.LENGTH_LONG).show();


                }
            });
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (LinkedHashMap<String, Fmd> dmmms : f_main_map.values()) {

                        dm.load_match_uareu(Base64.decode(capt_result, 0), mint, dmmms);
                        Toast.makeText(Realm.context, "Matching ...", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
    }


    @Override
    public void on_result_image_obtained(Bitmap capt_result_img) {
        super.on_result_image_obtained(capt_result_img);
        if (reg_mode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Realm.context, "Saved Image: " + save_fp(capt_result_img), Toast.LENGTH_LONG).show();
                    Log.e("IMG", "" + capt_result_img.getWidth() + " X " + capt_result_img.getHeight());
                }
            });
        }
    }

    //0724047705
    @Override
    public void on_result_wsq_obtained(byte[] wsq) {
        super.on_result_wsq_obtained(wsq);
        String wsq_name = save_wsq(wsq);
        matchWsq(wsq_name);
        if (reg_mode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Realm.context, "Saved WSQ: " + wsq_name, Toast.LENGTH_LONG).show();
                    Log.e("WSQ GEN  =>", "WSQ : " + Base64.encodeToString(wsq, Base64.DEFAULT));
                }
            });
        }

    }

    //Realm:: 208 X 288
//    IMG: 256 X 360
    void matchWsq(String wsq_path) {
        try {

            CompressionImpl cmp = new CompressionImpl();
            cmp.Start();
            cmp.SetWsqBitrate(90, 0);
//            File file = new File(svars.current_app_config(Realm.context).file_path_employee_data, "IDC_DAT1622727317951WSQ_IDC");
//            File file = new File(svars.current_app_config(Realm.context).file_path_employee_data, "IDC_DAT1622744407947WSQ_IDC");
            File file = new File(Environment.getExternalStorageDirectory().toString(), "20220506161519.wsq");
//            File file2 = new File(Environment.getExternalStorageDirectory().toString(), "20220506161519.wsq");
            File file2 = new File(svars.current_app_config(Realm.context).file_path_employee_data, wsq_path);

//            Fid fid=new Fid();
//            Fid rawCompresss = cmp.ExpandFid(fid, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            //  Log.e("WSQ match =>",""+UareUGlobal.GetEngine().Compare(rawCompresss, 0,rawCompresss, 0)<101);
            Compression.RawImage rawCompress = cmp.ExpandRaw(org.apache.commons.io.FileUtils.readFileToByteArray(file), Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            Compression.RawImage rawCompress2 = cmp.ExpandRaw(org.apache.commons.io.FileUtils.readFileToByteArray(file2), Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            cmp.Finish();
            /*    Fmd ffm = UareUGlobal.GetImporter().ImportFmd(rawCompress.data, main_fmd_format,main_fmd_format);  */
            Fmd fmd = UareUGlobal.GetEngine().CreateFmd(rawCompress.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);
            Fmd fmd2 = UareUGlobal.GetEngine().CreateFmd(rawCompress2.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);

            Log.e("WSQ match =>", "" + UareUGlobal.GetEngine().Compare(fmd, 0, fmd2, 0));
            Bitmap bmp = Globals.GetBitmapFromRaw(rawCompress.data, 256, 360);

            String ISOTemplate1 = Base64.encodeToString(fmd.getData(), Base64.DEFAULT);
            String ISOTemplate2 = Base64.encodeToString(fmd2.getData(), Base64.DEFAULT);
            Log.e("ISO match =>", "" + fph_8_inch.match_global(ISOTemplate1, ISOTemplate2));

            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) findViewById(R.id.fp_img)).setImageBitmap(bmp);

                }
            });
        } catch (Exception ex) {
            Log.e("WSQ ERROR=>", "" + ex);
        }
    }

    void matchWsq(byte[] wsq, byte[] wsq2) {
        CompressionImpl cmp = new CompressionImpl();
        try {

            cmp.Start();
            cmp.SetWsqBitrate(90, 0);

            Compression.RawImage rawCompress = cmp.ExpandRaw(wsq, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            Compression.RawImage rawCompress2 = cmp.ExpandRaw(wsq2, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            cmp.Finish();
            /*    Fmd ffm = UareUGlobal.GetImporter().ImportFmd(rawCompress.data, main_fmd_format,main_fmd_format);  */
            Fmd fmd = UareUGlobal.GetEngine().CreateFmd(rawCompress.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);
            Fmd fmd2 = UareUGlobal.GetEngine().CreateFmd(rawCompress2.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);


            int match_diff = UareUGlobal.GetEngine().Compare(fmd, 0, fmd2, 0);
            Log.e("WSQ match =>", "" + match_diff);
            rm.setText("WSQ MATCH:Difference:: " + match_diff + (match_diff > 100 ? "  Failed " : "  Ok"));
            Log.e("WSQ match =>", "" + match_diff);
            Bitmap bmp = Globals.GetBitmapFromRaw(rawCompress2.data, 256, 360);

            String ISOTemplate1 = Base64.encodeToString(fmd.getData(), Base64.DEFAULT);
            String ISOTemplate2 = Base64.encodeToString(fmd2.getData(), Base64.DEFAULT);

            Log.e("ISO match =>", "" + ISOTemplate1);
            Log.e("ISO match =>", "" + ISOTemplate2);
//            Log.e("ISO match =>",""+fph_8_inch.match_global(ISOTemplate1,ISOTemplate2));
            Fmd ffm = UareUGlobal.GetImporter().ImportFmd(Base64.decode(ISOTemplate1, 0), main_fmd_format, main_fmd_format);
            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(ISOTemplate2, 0), main_fmd_format, main_fmd_format);


            //  Log.e("FFM =>","");

            int mm_score = UareUGlobal.GetEngine().Compare(ffm, 0, ffm2, 0);
            Log.e("BASE64 Result=>", "" + mm_score);
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) findViewById(R.id.fp_img)).setImageBitmap(bmp);

                }
            });
        } catch (Exception ex) {
            Log.e("WSQ ERROR=>", "" + ex);
            try {
                cmp.Finish();
            } catch (Exception exx) {

            }
        }
    }

    void matchWsq(byte[] wsq) {
        try {

            CompressionImpl cmp = new CompressionImpl();
            cmp.Start();
            cmp.SetWsqBitrate(90, 0);
//            File file = new File(svars.current_app_config(Realm.context).file_path_employee_data, "IDC_DAT1622727317951WSQ_IDC");
//            File file = new File(svars.current_app_config(Realm.context).file_path_employee_data, "IDC_DAT1622744407947WSQ_IDC");
            File file = new File(Environment.getExternalStorageDirectory().toString(), "20220506161519.wsq");
//            File file2 = new File(Environment.getExternalStorageDirectory().toString(), "20220506161519.wsq");
//            File file2 = new File(svars.current_app_config(Realm.context).file_path_employee_data, wsq_path);

//            Fid fid=new Fid();
//            Fid rawCompresss = cmp.ExpandFid(fid, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            //  Log.e("WSQ match =>",""+UareUGlobal.GetEngine().Compare(rawCompresss, 0,rawCompresss, 0)<101);
            Compression.RawImage rawCompress = cmp.ExpandRaw(org.apache.commons.io.FileUtils.readFileToByteArray(file), Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            Compression.RawImage rawCompress2 = cmp.ExpandRaw(wsq, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            cmp.Finish();
            /*    Fmd ffm = UareUGlobal.GetImporter().ImportFmd(rawCompress.data, main_fmd_format,main_fmd_format);  */
            Fmd fmd = UareUGlobal.GetEngine().CreateFmd(rawCompress.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);
            Fmd fmd2 = UareUGlobal.GetEngine().CreateFmd(rawCompress2.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);

            Log.e("WSQ match =>", "" + UareUGlobal.GetEngine().Compare(fmd, 0, fmd2, 0));
            Bitmap bmp = Globals.GetBitmapFromRaw(rawCompress2.data, 256, 360);

            String ISOTemplate1 = Base64.encodeToString(fmd.getData(), Base64.DEFAULT);
            String ISOTemplate2 = Base64.encodeToString(fmd2.getData(), Base64.DEFAULT);

            Log.e("ISO match =>", "" + ISOTemplate1);
            Log.e("ISO match =>", "" + ISOTemplate2);
//            Log.e("ISO match =>",""+fph_8_inch.match_global(ISOTemplate1,ISOTemplate2));
            Fmd ffm = UareUGlobal.GetImporter().ImportFmd(Base64.decode(ISOTemplate1, 0), main_fmd_format, main_fmd_format);
            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(ISOTemplate2, 0), main_fmd_format, main_fmd_format);


            //  Log.e("FFM =>","");

            int mm_score = UareUGlobal.GetEngine().Compare(ffm, 0, ffm2, 0);
            Log.e("BASE64 Result=>", "" + mm_score);
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) findViewById(R.id.fp_img)).setImageBitmap(bmp);

                }
            });
        } catch (Exception ex) {
            Log.e("WSQ ERROR=>", "" + ex);
        }
    }

    void match_image(Bitmap bmp) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) findViewById(R.id.fp_img)).setImageBitmap(bmp);

            }
        });
        CompressionImpl cmp = new CompressionImpl();
        try {
            cmp.Start();

            cmp.SetWsqBitrate(90, 0);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Log.e("WSQ BMP =>", bmp.getWidth() + " x " + bmp.getHeight());


            //captureResult is a parameter passed into the Async capture listener\callback and contains the captured fingerprint image data (Fid).
            //    byte[] rawCompress = cmp.CompressRaw(ISOFid.getViews()[0].getWidth(), ISOFid.getViews()[0].getHeight(), 500, 8, ISOFid.getViews()[0].getImageData(), Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */ File.WriteAllBytes("WSQfromRaw.wsq",rawCompress);
            byte[] rawCompress = cmp.CompressRaw(byteArray, bmp.getWidth(), bmp.getHeight(), 500, 8, Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            cmp.Finish();
            Log.e("WSQ GEN  =>", "WSQ successfully compressed ");
            String wsq = save_wsq(rawCompress);
            matchWsq(wsq);

        } catch (UareUException e) {
            e.printStackTrace();
            Log.e("WSQ ERROR  =>", "" + e.toString());

        }
    }

    void convertWSQ(Bitmap bmp) {


        byte[] wsqData = new WSQEncoder(bmp).encode();
        Log.e("Converted wsq", "" + wsqData.length);
        matchWsq(wsqData);
    }

    void matchImages(Bitmap bmp1, Bitmap bmp2) {


        byte[] wsqData = new WSQEncoder(bmp1).encode();
        Log.e("Converted wsq1", "" + wsqData.length);
        byte[] wsqData2 = new WSQEncoder(bmp2).encode();
        Log.e("Converted wsq2", "" + wsqData2.length);
        matchWsq(wsqData, wsqData2);
    }

    String save_fp(Bitmap fpb) {
        String img_name = "IDC_FP" + System.currentTimeMillis() + "JPG_IDC.JPG";
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOutputStream = null;
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/IDCAPTURE_BUKINA/");
        // if (!file.exists()) {
        file.mkdirs();
        //  }
        file = new File(Environment.getExternalStorageDirectory().toString() + "/IDCAPTURE_BUKINA/", img_name);

        try {
            fOutputStream = new FileOutputStream(file);

            fpb.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);

            fOutputStream.flush();
            fOutputStream.close();

//        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        } catch (IOException e) {
            e.printStackTrace();

            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        }
        return img_name;
    }

    String save_wsq(byte[] file_bytes) {
        //  byte[] file_bytes= Base64.decode(base64_bytes,0);

        String img_name = "IDC_DAT" + System.currentTimeMillis() + "WSQ_IDC";

        File file = new File(svars.current_app_config(Realm.context).file_path_employee_data);
        if (!file.exists()) {
            Log.e("Creating data dir=>", "" + String.valueOf(file.mkdirs()));
        }

        file = new File(file, img_name);

        try {
            FileOutputStream fOutputStream = new FileOutputStream(file);
            fOutputStream.write(file_bytes);
            fOutputStream.flush();
            fOutputStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        } catch (IOException e) {
            e.printStackTrace();

            //   Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return "--------------";
        }
        return img_name;
    }


    void matchWsq(String wsq_path, String wsq_path2) {
        CompressionImpl cmp = new CompressionImpl();
        try {

            cmp.Start();
            cmp.SetWsqBitrate(90, 0);
            File file = new File(wsq_path);
            File file2 = new File(wsq_path2);

            Compression.RawImage rawCompress = cmp.ExpandRaw(org.apache.commons.io.FileUtils.readFileToByteArray(file), Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            Compression.RawImage rawCompress2 = cmp.ExpandRaw(org.apache.commons.io.FileUtils.readFileToByteArray(file2), Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */
            cmp.Finish();
            /*    Fmd ffm = UareUGlobal.GetImporter().ImportFmd(rawCompress.data, main_fmd_format,main_fmd_format);  */
            Fmd fmd = UareUGlobal.GetEngine().CreateFmd(rawCompress.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);
            Fmd fmd2 = UareUGlobal.GetEngine().CreateFmd(rawCompress2.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);

            int match_diff = UareUGlobal.GetEngine().Compare(fmd, 0, fmd2, 0);
            Log.e("WSQ match =>", "" + match_diff);
            rm.setText("WSQ MATCH:Difference:: " + match_diff + (match_diff > 100 ? "  Failed " : "  Ok"));

            Bitmap bmp = Globals.GetBitmapFromRaw(rawCompress.data, 256, 360);

            String ISOTemplate1 = Base64.encodeToString(fmd.getData(), Base64.DEFAULT);
            String ISOTemplate2 = Base64.encodeToString(fmd2.getData(), Base64.DEFAULT);
            Log.e("ISO match =>", "" + fph_8_inch.match_global(ISOTemplate1, ISOTemplate2));

            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ImageView) findViewById(R.id.fp_img)).setImageBitmap(bmp);

                }
            });
        } catch (Exception ex) {
            Log.e("WSQ ERROR=>", "" + ex);//0769998985
            try {
                cmp.Finish();
            } catch (Exception exx) {

            }
        }
    }


    public int matchISO(String fp1, String fp2) {

        try {
            //String b64 =Base64.encodeToString(m_engine.CreateFmd(cap_result.image,main_fmd_format).getData(),0);
            //Log.e("BASE64 =>",""+b64);
            Fmd ffm = UareUGlobal.GetImporter().ImportFmd(Base64.decode(fp1, 0), main_fmd_format, main_fmd_format);
            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(fp2, 0), main_fmd_format, main_fmd_format);


            //  Log.e("FFM =>","");

            int mm_score = UareUGlobal.GetEngine().Compare(ffm, 0, ffm2, 0);
            Log.e("BASE64 Result=>", "" + mm_score);

            return mm_score;

        } catch (UareUException e) {
            Log.e("BASE64 Error=>", "" + e.toString());
            e.printStackTrace();
            return -1;
        }
    }

    public void matchWSQISO(String wsq_path, String iso_base64) {
        CompressionImpl cmp = new CompressionImpl();

        try {


            cmp.Start();

            cmp.SetWsqBitrate(90, 0);
            File file = new File(wsq_path);


            Compression.RawImage rawCompress = cmp.ExpandRaw(org.apache.commons.io.FileUtils.readFileToByteArray(file), Compression.CompressionAlgorithm.COMPRESSION_WSQ_NIST); /* Creates valid WSQ file */

            cmp.Finish();


            Fmd fmd = UareUGlobal.GetEngine().CreateFmd(rawCompress.data, 256, 360, 500, 0, 3407615, Fmd.Format.ISO_19794_2_2005);

            Fmd ffm2 = UareUGlobal.GetImporter().ImportFmd(Base64.decode(iso_base64, 0), main_fmd_format, main_fmd_format);

            int match_diff = UareUGlobal.GetEngine().Compare(fmd, 0, ffm2, 0);
            Log.e("WSQ match =>", "" + match_diff);
            rm.setText("WSQ MATCH:Difference:: " + match_diff + (match_diff > 100 ? "  Failed " : "  Ok"));


        } catch (UareUException | IOException e) {
            Log.e("BASE64 Error=>", "" + e.toString());
            e.printStackTrace();
            try {
                cmp.Finish();
            } catch (Exception exx) {

            }


        }
    }


    @Override
    public void on_data_received(BluetoothDevice device, String data) {

    }

    @Override
    public void on_device_connection_failed(BluetoothDevice device) {

    }

    @Override
    public void on_data_sent(BluetoothDevice device, String data) {

    }

    @Override
    public void on_data_sent(BluetoothDevice device, byte[] data) {

    }

    @Override
    public void on_data_parsed(BluetoothDevice device, String data) {

    }

    @Override
    public void on_device_connection_changed(boolean connected, BluetoothDevice device) {

    }

    @Override
    public void on_device_reonnected(BluetoothDevice device) {

    }

    @Override
    public void on_device_error(BluetoothDevice device, String error) {

    }
}