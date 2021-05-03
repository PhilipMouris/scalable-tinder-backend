package MediaServer;

import Config.Config;
import com.arangodb.ArangoDB;
import io.minio.*;
import io.minio.errors.*;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

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
                   return name;
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
    public String uploadFile(byte[] data,String fileType){
        UUID uuid = UUID.randomUUID();
        String name = uuid.toString()+".png";
        try {
            String contentType="image/png";
            if(fileType.equals("video")){
                contentType="video/mp4";
            }

            ObjectWriteResponse objectWriteResponse = minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioBucketName).object(name).stream(
                            new ByteArrayInputStream(data), -1, 10485760)
                            .contentType(contentType)
                            .build());
            return name;
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

    public byte [] downloadFile(String name){

        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioBucketName)
                            .object(name)
                            .build());
            return stream.readAllBytes();
        }
            // Read data from stream

     catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        MinioInstance minio=new MinioInstance();
        File file=new File("/home/vm/Desktop/Scalable/video.mp4");
        byte [] byteArray= new byte[(int) file.length()];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(byteArray, 0, byteArray.length);
        buf.close();
        String fileName=minio.uploadFile(byteArray,"video");
        System.out.println(fileName);
        byte[] downloadedFile= minio.downloadFile(fileName);
        File fileToWrite=new File("/home/vm/Desktop/Scalable/thumbsu.mp4");
        FileOutputStream fos = new FileOutputStream(fileToWrite); 
        try  {
            fos.write(downloadedFile);
            //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            fos.close();
        }
        
    }
}