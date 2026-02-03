package com.lwf.implatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lwf.implatform.entity.FileInfo;
import com.lwf.implatform.vo.UploadImageVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService extends IService<FileInfo> {

    String uploadFile(MultipartFile file);

    UploadImageVO uploadImage(MultipartFile file, Boolean isPermanent);

}
