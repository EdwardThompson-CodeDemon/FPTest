package sparta.realm.fptest.Storage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import sparta.realm.fptest.R;


public class FileBrowserAdapter extends RecyclerView.Adapter<FileBrowserAdapter.view>  {


    Context cntxt;
    public File [] order_items;
    public onItemClickListener listener;

    public interface onItemClickListener {

        void onItemClick(File mem, View view);
    }


    public FileBrowserAdapter( File[] order_items) {

        this.order_items = order_items;



    }

   public void setupListener(onItemClickListener listener) {

        this.order_items = order_items;
        this.listener = listener;


    }

    @NonNull
    @Override
    public view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        cntxt=parent.getContext();
        View view = LayoutInflater.from(cntxt).inflate(R.layout.item_file, parent, false);

        return new view(view);
    }

    @Override
    public void onBindViewHolder(@NonNull view holder, int position) {
        File obj = order_items[position];
        holder.name.setText(obj.getName());
        if(obj.isFile()){
            holder.icon.setImageDrawable(cntxt.getDrawable(R.drawable.ic_twotone_insert_drive_file_24));

        }else {
            holder.icon.setImageDrawable(cntxt.getDrawable(R.drawable.ic_baseline_folder_24));


        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(obj,holder.itemView);

            }
        });


    }


    @Override
    public int getItemCount() {
        return order_items==null?0:order_items.length;
    }

    public class view extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name,mod_time;
        public String sid;
        public ImageView icon;



        view(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            mod_time = itemView.findViewById(R.id.mod_time);
            icon = itemView.findViewById(R.id.icon);


        }

        @Override
        public void onClick(View view) {

        }
    }
}
