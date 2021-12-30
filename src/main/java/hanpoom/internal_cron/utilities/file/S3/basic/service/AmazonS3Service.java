package hanpoom.internal_cron.utilities.file.S3.basic.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AmazonS3Service {

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${amazon.s3.bucket-name}")
    public String bucket;  // S3 버킷 이름

    public String upload(MultipartFile multipartFile, String dirName, String userFileName) throws IOException {
        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));

        return upload(uploadFile, dirName, userFileName);
    }

    public String upload(byte[] bytefile, String dirName, String userFileName) throws IOException {
        File uploadFile = createFile(bytefile, userFileName)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: byte[] -> File create fail"));

        return upload(uploadFile, dirName, userFileName);
    }

    // S3로 파일 업로드하기
    private String upload(File uploadFile, String dirName, String userFileName) {
        System.out.println(uploadFile);
        String fileName = dirName + "/" + userFileName;   // S3에 저장된 파일 이름
        String uploadImageUrl = uploadS3(uploadFile, fileName); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }


    public String uploadByteToS3(byte[] bytefile, String dirName, String userFileName) throws IOException {
        File uploadFile = createFile(bytefile, userFileName)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: byte[] -> File create fail"));
        System.out.println(uploadFile);
        String fileName = dirName + "/" + userFileName;   // S3에 저장된 파일 이름
        return uploadS3(uploadFile, fileName);
    }

    // S3로 업로드
    private String uploadS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 저장된 파일 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            System.out.println("File delete success");
            return;
        }
        System.out.println("File delete fail");
    }

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    // 로컬에 파일 생성 하기
    private Optional<File> createFile(byte[] bytefile, String userFileName) throws IOException {
        File createFile = new File(System.getProperty("user.dir") + "/" + userFileName);
        if (createFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(createFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(bytefile);
            }
            return Optional.of(createFile);
        }

        return Optional.empty();
    }

    public ResponseEntity<byte[]> getObject(String storedFileName) throws IOException {
        System.out.println(bucket);
        System.out.println(storedFileName);
        S3Object o = amazonS3Client.getObject(new GetObjectRequest(bucket, storedFileName));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        String[] subString = storedFileName.split("/");
        String fileName = subString[subString.length - 1];
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        ResponseEntity<byte[]> bytesObject = new ResponseEntity<byte[]>(bytes, httpHeaders, HttpStatus.OK);
        return bytesObject;

    }

    public String getObjectBase64(String storedFileName) throws IOException {
        String objectBase64 = null;

        S3Object o = amazonS3Client.getObject(new GetObjectRequest(bucket, storedFileName));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        objectBase64 = Base64.getEncoder().encodeToString(bytes);

        return objectBase64;
    }

}
