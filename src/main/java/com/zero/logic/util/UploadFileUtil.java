package com.zero.logic.util;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
public class UploadFileUtil {

    /**
     * 单个文件上传
     * @param file
     * @return msg
     */
    public static  String singleFileUpload(MultipartFile file){
        if(file.isEmpty()){
            //redirectAttributes.addFlashAttribute("message","请选择一个文件上传！");
            return"请选择上传文件";
        }
        if(!scopeFileSize(file)){//上传文件大小限制
            return "文件太大";
        }
        if(".exe".equals(getFileType(file.getOriginalFilename()))){
            return "不能上传应用程序文件";
        }
        try {
            //获取文件、保存文件
            byte[] bytes = file.getBytes();
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(UploadFilePathUtil.uploadFilePath+"/"+file.getOriginalFilename())));
            out.write(bytes);
            out.flush();
            out.close();

        }catch (IOException e){
            e.printStackTrace();
            return "文件上传失败";
        }
        return "文件上传成功";
    }

    /**
     * 设置文件大小
     * @param file
     * @return
     */
    public static boolean scopeFileSize(MultipartFile file){
        if(UploadFilePathUtil.uploadFileSize == null){//如果用户不设置则不作限制
            return true;
        }
        long fileSize = Integer.parseInt(UploadFilePathUtil.uploadFileSize)*1024*1024;//单位为：字节
        if(file.getSize()>0 && file.getSize()<fileSize){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 获取文件名后缀
     * @param fileName
     * @return
     */
    public static String getFileType(String fileName){
        if(!"".equals(fileName) &&fileName!=null &&fileName.indexOf(".")>=0){
            return fileName.substring(fileName.lastIndexOf("."),fileName.length());
        }
        return "";
    }

    /**
     * 获取已经存在的文件列表
     * @param filePath
     * @return
     */
    public static List<String> getExistsFile(String filePath){
        File[] files = new File(filePath).listFiles();
        List<String> fileNameList = new ArrayList<>();
        for (File file:files){
            fileNameList.add(file.getName());
        }
        return  fileNameList;
    }




    /**
     * 多个文件上传
     * @param files
     * @return
     */
    public static String moreFileUpload(MultipartFile[] files){
        if(files!=null && files.length>=1){
            BufferedOutputStream bs = null;
            MultipartFile file = null;
            for(int i = 0;i<files.length;i++){
                file = files[i];

                if(file.isEmpty()){
                    return "第"+i+"个文件为空上传失败";
                }
                if(!file.isEmpty()){
                    try{
                        if(!scopeFileSize(file)){//上传文件大小限制
                            return  "第"+i+"个文件:"+file.getOriginalFilename()+"超过文件上传大小限制";
                        }
                        byte[] bytes = file.getBytes();
                        bs = new BufferedOutputStream(new FileOutputStream(new File(UploadFilePathUtil.uploadFilePath+"/"+file.getOriginalFilename())));
                        bs.write(bytes);
                        bs.flush();
                        bs.close();
                    }catch(IOException e){
                        e.printStackTrace();
                        return "第"+i+"个文件:"+file.getOriginalFilename()+"上传失败";
                    }
                }
            }
        }
        return  "文件上传成功";
    }
}
