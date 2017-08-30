package com.ifox.platform.file.rest;

import com.ifox.platform.common.rest.BaseController;
import com.ifox.platform.common.rest.response.BaseResponse;
import com.ifox.platform.file.enums.EnumFile;
import com.ifox.platform.file.service.FileService;
import com.ifox.platform.utility.common.UUIDUtil;
import com.ifox.platform.utility.datetime.DateTimeUtil;
import com.ifox.platform.utility.jwt.JWTUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import static com.ifox.platform.common.constant.RestStatusConstant.*;

/**
 * 文件服务控制器
 */
@Controller("fileController")
@RequestMapping(value = "/file", headers = {"api-version=1.0", "Authorization"})
@Api(tags = "文件服务")
public class FileController extends BaseController {

    @Autowired
    private Environment env;

    @Autowired
    private FileService fileService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiOperation("单文件上传")
    @ApiResponses({ @ApiResponse(code = 401, message = "没有文件操作权限"),
                    @ApiResponse(code = 484, message = "不支持的文件类型"),
                    @ApiResponse(code = 485, message = "不支持的服务名称")})
    public @ResponseBody BaseResponse upload(@RequestParam("file") MultipartFile file, @RequestParam String serviceName, @RequestParam EnumFile.FileType fileType, HttpServletResponse response) {
        String uuid = UUIDUtil.randomUUID();
        logger.info("单文件上传 serviceName:{}, fileType:{}, uuid:{}", serviceName, fileType, uuid);

        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        boolean validation = fileService.validateFileType(fileType, ext.substring(1));
        if (!validation) {
            logger.info("不支持的文件类型 uuid:{}", uuid);
            response.setStatus(NOT_SUPPORT_FILE_TYPE);
            return notSupportFileTypeBaseResponse();
        }

        String[] serviceNameArray = env.getProperty("file-service.service-name").split(",");
        if (!Arrays.asList(serviceNameArray).contains(serviceName)) {
            logger.info("不支持的服务名称 uuid:{}", uuid);
            response.setStatus(NOT_SUPPORT_SERVICE_NAME);
            return notSupportServiceNameBaseResponse();
        }

        String absolutePath = env.getProperty("file-service.save.path");
        String fileName = uuid + ext;
        String currentDateAsString = DateTimeUtil.getCurrentDateAsString(null);
        String relativePath = serviceName + File.separator+ currentDateAsString + File.separator + fileName;
        String finalPathString = absolutePath + relativePath;
        File finalPath = new File(finalPathString);
        File parentFile = finalPath.getParentFile();
        if (!parentFile.exists())
            parentFile.mkdir();

        if (!parentFile.canWrite()) {
            logger.info("没有文件操作权限 finalPathString:{}, uuid:{}", finalPathString, uuid);
            response.setStatus(UNAUTHORIZED);
            return unauthorizedBaseResponse("没有文件操作权限");
        }

        try {
            file.transferTo(finalPath);
        } catch (IOException e) {
            logger.warn("保存文件异常 finalPathString:{}, uuid:{}", finalPathString, uuid);
            response.setStatus(SERVER_EXCEPTION);
            return serverExceptionBaseResponse();
        }

        logger.info("上传成功 finalPathString:{}, uuid:{}", finalPathString, uuid);
        return successBaseResponse(relativePath);
    }

    @ApiOperation("文件获取")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResponseEntity<byte[]> get(@RequestParam EnumFile.FileType fileType, @RequestParam String path) throws IOException {
        String absolutePath = env.getProperty("file-service.save.path");
        File file=new File(absolutePath + path);

        HttpHeaders headers = new HttpHeaders();
//        String fileName=new String("你好.xlsx".getBytes("UTF-8"),"iso-8859-1");//为了解决中文名称乱码问题
//        headers.setContentDispositionFormData("attachment", fileName);
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        String ext = path.substring(path.lastIndexOf(".")).substring(1);

        switch (fileType) {
            case PICTURE:
                if ("JPG".equalsIgnoreCase(ext) || "JPEG".equalsIgnoreCase(ext)) {
                    headers.setContentType(MediaType.IMAGE_JPEG);
                } else if ("PNG".equalsIgnoreCase(ext)) {
                    headers.setContentType(MediaType.IMAGE_PNG);
                } else if ("GIF".equalsIgnoreCase(ext)) {
                    headers.setContentType(MediaType.IMAGE_GIF);
                }
        }



        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
            headers, HttpStatus.OK);
    }


}