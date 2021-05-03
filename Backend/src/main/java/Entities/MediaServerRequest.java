package Entities;


import org.json.JSONObject;

import java.io.*;

public class MediaServerRequest implements Serializable {
    private byte[] file;
    private String request;
    private String filename;

    public MediaServerRequest(byte[] file, String request, String filename) {
        this.file = file;
        this.request = request;
        this.filename = filename;
    }

    public MediaServerRequest() {
    }

    public MediaServerRequest(byte[] file) {
        this.file = file;
    }

    public MediaServerRequest(String request) {
        this.request = request;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public JSONObject getRequest() {
        return new JSONObject(request);
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public MediaServerRequest(byte[] file, String request) {
        this.file = file;
        this.request = request;
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
    public static MediaServerRequest getObject(byte[] objectBytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(objectBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object object = in.readObject();
            return (MediaServerRequest) object;

        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException ce){
            return null;
        }
        catch(ClassCastException cce){
            return null;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

