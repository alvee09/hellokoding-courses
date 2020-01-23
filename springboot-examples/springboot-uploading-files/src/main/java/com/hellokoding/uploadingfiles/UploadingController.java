package com.hellokoding.uploadingfiles;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;

//import org.apache.log4j.Logger;

@Controller
public class UploadingController {
    //public static final String uploadingDir = System.getProperty("user.dir") + "/uploadingDir/";
    public static final String uploadingDir = "D:/Upload/uploadingDir/";
    //private Logger logger = Logger.getLogger(this.getClass());
    @RequestMapping("/")
    public String uploading(Model model) {
        File file = new File(uploadingDir);
        model.addAttribute("files", file.listFiles());
        return "uploading";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String uploadingPost(@RequestParam("uploadingFiles") MultipartFile[] uploadingFiles, @RequestParam("uploadingInvoices") MultipartFile[] uploadingFiles2) throws IOException {
        for (MultipartFile uploadedFile : uploadingFiles) {
            System.out.println(uploadedFile.getOriginalFilename());
            File file = new File(uploadingDir + uploadedFile.getOriginalFilename());
            uploadedFile.transferTo(file);
//conversion code
            /*File convFile = new File(uploadedFile.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(uploadedFile.getBytes());
            fos.close();
            byte[] bytes = loadFile(convFile);
            byte[] encoded = Base64.encodeBase64(bytes);
            String encodedString = new String(encoded);*/
            //if (convFile.delete())
            //    logger.info("Invoice file Deleted and sent for parsing");

            String encodedString = null;
            try {
                encodedString = convert(uploadedFile);
            } catch (Exception e) {
                System.out.println("Converting Error");
            }

            String parserId = "brxwstsgvcyk";
            String documentInfo = docparserFileUpload(encodedString, parserId);
            System.out.println(documentInfo);
            /*String url = "https://api.docparser.com/v1/document/upload/" + parserId;
            // String url="https://api.docparser.com/v1/parsers";
            System.out.println("upload url:" + url);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.ALL));
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            httpHeaders.set("api_key", "9cfd72aca8439ff3f367c74d33731ecc4763d2b6");
            body.set("file_content", encodedString);
            HttpEntity<?> entity = new HttpEntity<Object>(body, httpHeaders);
            ResponseEntity<String> responseEntity;
            do {
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            } while (responseEntity.getBody() == null);
            String documentInfo = responseEntity.getBody();
            System.out.println(documentInfo);
        }*/
        }
            for (MultipartFile uploadedFile : uploadingFiles2) {
                System.out.println(uploadedFile.getOriginalFilename());
                File file = new File(uploadingDir + uploadedFile.getOriginalFilename());
                uploadedFile.transferTo(file);
            }
            return "redirect:/";
    }
        public String docparserFileUpload(String file, String parserId) {
            // TODO Auto-generated method stub
            String url = "https://api.docparser.com/v1/document/upload/" + parserId;
            // String url="https://api.docparser.com/v1/parsers";
            System.out.println("upload url:" + url);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAccept(Collections.singletonList(MediaType.ALL));
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            httpHeaders.set("api_key", "9cfd72aca8439ff3f367c74d33731ecc4763d2b6");
            body.set("file_content", file);
            HttpEntity<?> entity = new HttpEntity<Object>(body, httpHeaders);
            ResponseEntity<String> responseEntity;
            do {
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            } while (responseEntity.getBody() == null);
            String documentInfo = responseEntity.getBody();
            return documentInfo;
        }
    public String convert(MultipartFile file) throws Exception {
        // TODO Auto-generated method stub
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        byte[] bytes = loadFile(convFile);
        byte[] encoded = Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);
        if (convFile.delete())
            System.out.println("Invoice file Deleted and sent for parsing");
        return encodedString;
    }
    private byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

}
