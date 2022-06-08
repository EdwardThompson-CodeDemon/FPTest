package sparta.realm.fptest;


import com.realm.annotations.DynamicClass;
import com.realm.annotations.DynamicProperty;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.db_class_;

import java.io.Serializable;


@DynamicClass(table_name ="member_data_table")

@SyncDescription(service_id = "2",service_name = "Images",service_type = SyncDescription.service_type.Download,download_link = "/Api_v1.0/Camera/GetImagesToCamera",use_download_filter = true,chunk_size = 10,is_ok_position = "JO:isOkay",download_array_position = "JO:result")//uipa

@SyncDescription(service_id = "3",service_name = "Member images",service_type = SyncDescription.service_type.Upload,upload_link = "/Api_v1.0/Camera/AddCamImageTwo",table_filters = {"data_status IS NULL "," data_format NOT NULL"},chunk_size = 1)


public class member_data extends db_class_ implements Serializable {///Api_v1.0/Camera/AddCamImageTwo


//    @DynamicProperty(json_key = "employee_details_id")//uipa
    @DynamicProperty(json_key = "member_id",column_name = "memid",indexed_column = true)//dole
    public String member_id="";



    @DynamicProperty(json_key = "biometric_type_id")
   public String data_type="";

    @DynamicProperty(json_key = "biometric_type_details_id")
    public String data_index="";

    @DynamicProperty(column_name = "transaction_no",json_key = "transaction_no")
    public String transaction_no="";

    @DynamicProperty(json_key = "biometric_string")
//    @DynamicProperty(json_key = "imgBase64",storage_mode = "path")
    public String data="";

    @DynamicProperty(json_key = "biometric_type_id")
 public String data_format="";


    @DynamicProperty(json_key = "data_storage_mode")
 public String data_storage_mode="";




    @DynamicProperty(json_key = "optimization_status")
 public String optimization_status="";

    @DynamicProperty(column_name = "camera_sync_status")
    public String camera_sync_status="";

   @DynamicProperty(json_key = "url_path")
    public String url="";


    @DynamicProperty(column_name = "download_status",column_default_value = "1")
    public String download_status="";

  @DynamicProperty(json_key = "face_token")
    public String face_token="";








    public member_data()
    {

//        sync_service_description sd=new sync_service_description();
//        sd.service_name= "Member fingerprints";
//        sd.table_filters=new String[]{"data_type='"+svars.data_type_indexes.fingerprints+"'"};
//        sd.upload_link= svars.Fingerprint_uploading_link;
//        sd.download_link= svars.Fingerprint_downloading_link;
//        sd.servic_type= sync_service_description.service_type.Download_Upload;
//        sd.chunk_size= svars.fingerprints_request_limit;
//        sd.use_download_filter= true;
//        sd.table_name= table_name;
//
//
//        sync_service_description sd2=new sync_service_description();
//        sd2.service_name= "Member images";
//        sd2.table_filters=new String[]{"data_type='"+svars.data_type_indexes.photo+"'"};
//        sd2.upload_link= svars.Image_uploading_link;
//        sd2.download_link= svars.Image_downloading_link;
//        sd2.servic_type= sync_service_description.service_type.Download_Upload;
//        sd2.chunk_size= svars.images_request_limit;
//        sd2.use_download_filter= true;
//        sd2.table_name= table_name;
//
//        sync_service_description sd3=new sync_service_description();
//        sd3.service_name= "Member face_images";
//        sd3.table_filters=new String[]{"data_index='"+svars.image_indexes.camera_photo+"'"};
//        sd3.upload_link= svars.Camera_Image_uploading_link;
//        sd3.download_link= svars.Camera_Image_downloading_link;
//        sd3.servic_type= sync_service_description.service_type.Download_Upload;
//        sd3.chunk_size= svars.images_request_limit;
//        sd3.use_download_filter= true;
//        sd3.table_name= table_name;
//
//
//
//        ssds = new sync_service_description[]{sd,sd2,sd3};

    }

}
