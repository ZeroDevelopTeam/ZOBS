package com.zero.logic.util;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 文件上传工具类
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
            //获取已存在的文件名
            List<String> list = getExistsFile(UploadFileConfigUtil.uploadFilePath);
            for (String oldFileName:list){
                if(oldFileName.equals(file.getOriginalFilename())){
                    System.out.print("上传的文件："+file.getOriginalFilename()+"已经存在目录"+UploadFileConfigUtil.uploadFilePath);
                }
            }
            //获取文件、保存文件
            byte[] bytes = file.getBytes();
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(UploadFileConfigUtil.uploadFilePath+"/"+file.getOriginalFilename())));
            out.write(file.getBytes());
            out.flush();
            out.close();

            /*获取文件、保存文件的第二只中写法
            Path path = Paths.get(UPLOAD_FOLDER+file.getOriginalFilename());//保存文件路径
            Files.write(path,bytes);//文件写入
             */
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
        if(UploadFileConfigUtil.uploadFileSize == null){//如果用户不设置则不作限制
            return true;
        }
        long fileSize = Integer.parseInt(UploadFileConfigUtil.uploadFileSize)*1024*1024;//单位为：字节
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
}
