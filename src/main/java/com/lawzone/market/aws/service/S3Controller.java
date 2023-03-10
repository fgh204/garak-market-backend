package com.lawzone.market.aws.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.lawzone.market.user.controller.UserInfoController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class S3Controller {
	@Value("${cloud.aws.s3.bucket}")
    private String bucket;

	@Value("${cloud.aws.s3.dir}")
    private String dir;

    private final AmazonS3 amazonS3;

    public String upload(MultipartFile multipartFile, String s3FileName, String imgCfcd) throws IOException {
        //String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
    	String s3uploadImagePath = ""; 
    	
    	if("03".equals(imgCfcd)){
    		s3uploadImagePath = "/profileImages";
    	} else if("01".equals(imgCfcd) || "02".equals(imgCfcd)){
    		s3uploadImagePath = "/productImage";
    	} else if("04".equals(imgCfcd)){
    		s3uploadImagePath = "/productReviewImages";
    	}
    	
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getInputStream().available());

        amazonS3.putObject(bucket + s3uploadImagePath, s3FileName, multipartFile.getInputStream(), objMeta);
        return amazonS3.getUrl(bucket + s3uploadImagePath, s3FileName).toString();
    }
    
    public String uploadInputStream(InputStream inputStream, String s3FileName, String imgCfcd) throws IOException {
        //String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
    	String s3uploadImagePath = ""; 
    	
    	if("03".equals(imgCfcd)){
    		s3uploadImagePath = "/profileImages";
    	} else if("01".equals(imgCfcd) || "02".equals(imgCfcd)){
    		s3uploadImagePath = "/productImage";
    	} else if("04".equals(imgCfcd)){
    		s3uploadImagePath = "/productReviewImages";
    	}
    	
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(inputStream.available());

        amazonS3.putObject(bucket + s3uploadImagePath, s3FileName, inputStream, objMeta);
        return amazonS3.getUrl(bucket + s3uploadImagePath, s3FileName).toString();
    }
    
    public void delete(String s3FileName) throws IOException {
    	String _s3FileName = s3FileName.substring(s3FileName.lastIndexOf("/") + 1);
    	
    	log.info("_s3FileName === " + _s3FileName);
    	
        amazonS3.deleteObject(bucket, _s3FileName);
    }
    
    public void copy(String orgFileName, String copyFileName) throws IOException {
    	log.info("orgFileName === " + orgFileName);
    	log.info("copyFileName === " + copyFileName);
    	
        amazonS3.copyObject(bucket, orgFileName, bucket, copyFileName);
    }
}
