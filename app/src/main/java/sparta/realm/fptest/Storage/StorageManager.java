package sparta.realm.fptest.Storage;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import sparta.realm.Realm;
import sparta.realm.fptest.R;


public class StorageManager {

    public interface FileBrowserInterface
    {
        void onFleSelected(String filePath);
        void onCanceled();
    }

    public static void selectFile(String base_folder_path,FileBrowserInterface listener,boolean folder)
    {
        final File base_folder=new File(base_folder_path);
        if(!base_folder.exists()){
            base_folder.mkdirs();
            return;
        }
        if(base_folder.isFile()){
            //I expect a folder
            return;
        }
        assert !base_folder.isFile() ;
        final View aldv = LayoutInflater.from(Realm.context).inflate(R.layout.dialog_file_browser, null);
        final AlertDialog ald = new AlertDialog.Builder(Realm.context)
                .setView(aldv)
                .create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {


            ald.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        } else {
            ald.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);

        }
        ald.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ald.show();
        aldv.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ald.dismiss();
            }
        });

        RecyclerView rv=aldv.findViewById(R.id.fileList);
        final File[] selected_path = {base_folder};
        TextView selected_path_txt=aldv.findViewById(R.id.selected_path_txt);
        FileBrowserAdapter  fileBrowserAdapter= new FileBrowserAdapter(base_folder.listFiles());

        FileBrowserAdapter.onItemClickListener onItemClickListener=new FileBrowserAdapter.onItemClickListener() {
            @Override
            public void onItemClick(File mem, View view) {
                selected_path[0] = mem;
                selected_path_txt.setText(selected_path[0].getAbsolutePath());

                fileBrowserAdapter.order_items = mem.listFiles();
                fileBrowserAdapter.notifyDataSetChanged();

//                if(folder&&!mem.isFile()){
//
//
//
//                }else

                if(!folder&&mem.isFile()){
                    listener.onFleSelected(selected_path[0].getAbsolutePath());
                    ald.dismiss();
                }
            }
        };
        fileBrowserAdapter.setupListener(onItemClickListener);
        rv.setLayoutManager(new LinearLayoutManager(Realm.context));
        rv.setAdapter(fileBrowserAdapter);
        aldv.findViewById(R.id.select).setOnClickListener(v -> {
            listener.onFleSelected(selected_path[0].getAbsolutePath());
            ald.dismiss();
        });

        aldv.findViewById(R.id.home_folder).setOnClickListener(v -> {
            try{
                selected_path[0] = base_folder;
                selected_path_txt.setText(selected_path[0].getAbsolutePath());

                fileBrowserAdapter.order_items = base_folder.listFiles();
                fileBrowserAdapter.notifyDataSetChanged();
            }catch (Exception ex){

            }
        });

        aldv.findViewById(R.id.parent_folder).setOnClickListener(v -> {
            try{
                selected_path[0] = selected_path[0].getParentFile();
                selected_path_txt.setText(selected_path[0].getAbsolutePath());
                fileBrowserAdapter.order_items = selected_path[0].listFiles();
                fileBrowserAdapter.notifyDataSetChanged();
            }catch (Exception ex){

            }
        });

    }
}
