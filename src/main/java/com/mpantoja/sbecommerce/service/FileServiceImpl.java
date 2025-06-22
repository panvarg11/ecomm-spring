package com.mpantoja.sbecommerce.service;

import com.mpantoja.sbecommerce.exceptions.APIException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        //get name of original file
        String originalFileName = file.getOriginalFilename();

        //generate a unique name to assign to file
        String randomUID= UUID.randomUUID().toString();
        if(originalFileName==null) throw new APIException("FileName cannot be null");
        String fileName = randomUID.concat(
                originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path+ File.separator+fileName;

        //if file exsists, then create
        File folder = new File(path);
        if(!folder.exists()) folder.mkdir();

        //upload to app
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

}
