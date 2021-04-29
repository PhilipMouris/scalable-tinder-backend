package MediaServer;

import Config.Config;
import com.arangodb.ArangoDB;
import io.minio.*;
import io.minio.errors.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioInstance {
    private Config conf = Config.getInstance();
    private String minioSecretKey = conf.getMinioSecretKey();
    private String minioAccessKey=conf.getMinioAccessKey();
    private String minioPort=conf.getMinioPort();
    private String minioBucketName=conf.getMinioBucketName();
    private MinioClient minioClient;

    public MinioInstance(){

            try {
                // Create a minioClient with the MinIO server playground, its access key and secret key.
                 minioClient =
                        MinioClient.builder()
                                .endpoint("http://localhost:"+minioPort)
                                .credentials(minioAccessKey,minioSecretKey )
                                .build();

                // Make ' bucket if not exist.
                boolean found =
                        minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucketName).build());
                if (!found) {
                    // Make a new bucket called 'asiatrip'.
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucketName).build());
                } else {
                    System.out.println("Bucket  already exists.");
                }

            } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
                System.out.println("Error occurred: " + e);
                System.out.println("HTTP trace: " );
            }
        }
           public String uploadFile(String path,String name){
               try {
                   ObjectWriteResponse res=minioClient.uploadObject(
                            UploadObjectArgs.builder()
                                    .bucket(minioBucketName)
                                    .object(name)
                                    .filename(path)
                                    .build());
                   return res.etag();
               } catch (ErrorResponseException e) {
                   e.printStackTrace();
               } catch (InsufficientDataException e) {
                   e.printStackTrace();
               } catch (InternalException e) {
                   e.printStackTrace();
               } catch (InvalidKeyException e) {
                   e.printStackTrace();
               } catch (InvalidResponseException e) {
                   e.printStackTrace();
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (NoSuchAlgorithmException e) {
                   e.printStackTrace();
               } catch (ServerException e) {
                   e.printStackTrace();
               } catch (XmlParserException e) {
                   e.printStackTrace();
               }
               return null;
           }
    public String uploadFile(byte[] data,String name){
        try {


            ObjectWriteResponse objectWriteResponse = minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioBucketName).object(name).stream(
                            new ByteArrayInputStream(data), -1, 10485760)
                            .contentType("binary/octet-stream")
                            .build());
            return objectWriteResponse.etag();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        
    }
}