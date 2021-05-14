package Entities;

import org.json.JSONObject;

import java.io.*;

public class MediaServerResponse implements Serializable{
    private byte[] file;
    private String responseJson;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MediaServerResponse(byte[] file, String responseJson, String fileName)  {
        this.file = file;
        this.responseJson = responseJson;
        this.fileName = fileName;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public JSONObject getResponseJson() {
        return new JSONObject(responseJson);
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public MediaServerResponse() {
    }

    public MediaServerResponse(byte[] file, String responseJson) {
        this.file = file;
        this.responseJson = responseJson;
    }
    public byte[] getByteArray(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            byte[] objectBytes = bos.toByteArray();
            return objectBytes;
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try {
                bos.close();
            } catch (IOException ex) {
//                ex.printStackTrace();
            }
        }
        return null ;
    }
    public static MediaServerResponse getObject(byte[] objectBytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(objectBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object object = in.readObject();
            return (MediaServerResponse) object;

        }
        catch (Exception e) {

        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }
}
