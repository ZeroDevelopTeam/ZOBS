package com.zero.logic.controller;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.UploadFileUtil;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 文件上传控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/12
 */
@RestController
@RequestMapping("upload")
public class UploadFileController {

    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    @ApiOperation(value = "文件上传",notes = "文件上传")
    public String singleFileUpload(@RequestParam("file")MultipartFile file) throws JSONException {
        try {
            String msg = UploadFileUtil.singleFileUpload(file);
            if("文件上传成功".equals(msg)){
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS,msg);
            }else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,msg);
            }
        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL,"文件上传失败");
        }
    }
}
