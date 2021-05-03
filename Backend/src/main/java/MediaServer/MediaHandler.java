package MediaServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;

import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.UUID;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class MediaHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    // query param used to download a file
    private static final String FILE_QUERY_PARAM = "file";
    private MinioInstance minio;
    private HttpPostRequestDecoder decoder;
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(true);

    private boolean readingChunks;

    private static final int THUMB_MAX_WIDTH = 100;
    private static final int THUMB_MAX_HEIGHT = 100;
    private JsonObject response = new JsonObject();

    public MediaHandler() {
                 minio=new MinioInstance();
                 System.out.println("ALO");
    }

//    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // get the URL
        System.out.println(" request received");
        URI uri = new URI(request.uri());
        String uriStr = uri.getPath();



        if (uriStr.contains("/file/download")) {
            serveFile(ctx, request); // user requested a file, serve it
        } else if (uriStr.contains("/file/upload")) {
            uploadFile(ctx, request); // user requested to upload file, handle request
        } else {
            // unknown request, send error message
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
        }

    }

    private void serveFile(ChannelHandlerContext ctx, FullHttpRequest request) {

        // decode the query string
        QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri());

        Map<String, List<String>> uriAttributes = decoderQuery.parameters();

        // get the requested file name
        String fileName = "";
        try {
            fileName = uriAttributes.get(FILE_QUERY_PARAM).get(0);
        } catch (Exception e) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST, FILE_QUERY_PARAM + " query param not found");
            return;
        }

        // start serving the requested file
        sendFile(ctx, fileName, request);
    }

    private void sendFile(ChannelHandlerContext ctx, String fileName, FullHttpRequest request) {
        byte[] fileByteArray = minio.downloadFile(fileName);
        File file=new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
      

        if (file.isDirectory() || file.isHidden() || !file.exists()) {
            sendError(ctx, NOT_FOUND);
            return;
        }

        if (!file.isFile()) {
            sendError(ctx, FORBIDDEN);
            return;
        }

        RandomAccessFile raf;

        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException fnfe) {
            sendError(ctx, NOT_FOUND);
            return;
        }

        long fileLength = 0;
        try {
            fileLength = raf.length();
        } catch (IOException ex) {
            Logger.getLogger(MediaHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        HttpUtil.setContentLength(response, fileLength);
        setContentTypeHeader(response, file);

        //setDateAndCacheHeaders(response, file);
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // Write the initial line and the header.
        ctx.write(response);

        // Write the content.
        ChannelFuture sendFileFuture;
        DefaultFileRegion defaultRegion = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
        sendFileFuture = ctx.write(defaultRegion);

        // Write the end marker
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        // Decide whether to close the connection or not.
        if (!HttpUtil.isKeepAlive(request)) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        mimeTypesMap.addMimeTypes("image png tif jpg jpeg bmp");
        mimeTypesMap.addMimeTypes("text/plain txt");
        mimeTypesMap.addMimeTypes("video/mp4 mp4");
        mimeTypesMap.addMimeTypes("application/pdf pdf");

        String mimeType = mimeTypesMap.getContentType(file);

        response.headers().set(CONTENT_TYPE, mimeType);
    }

    private void uploadFile(ChannelHandlerContext ctx, FullHttpRequest request) {

        // test comment
        try {
            System.out.println("In Upload");
            decoder = new HttpPostRequestDecoder(factory, request);
            //System.out.println("decoder created");
        } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
            e1.printStackTrace();
            sendError(ctx, HttpResponseStatus.BAD_REQUEST, "Failed to decode file data");
            return;
        }

        readingChunks = HttpHeaders.isTransferEncodingChunked(request);

        if (decoder != null) {
            if (request instanceof HttpContent) {

                // New chunk is received
                HttpContent chunk = (HttpContent) request;
                try {
                    decoder.offer(chunk);
                } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
                    e1.printStackTrace();
                    sendError(ctx, HttpResponseStatus.BAD_REQUEST, "Failed to decode file data");
                    return;
                }

                readHttpDataChunkByChunk(ctx);
                // example of reading only if at the end
                if (chunk instanceof LastHttpContent) {
                    readingChunks = false;
                    reset();
                }
            } else {
                sendError(ctx, HttpResponseStatus.BAD_REQUEST, "Not a http request");
            }
        } else {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST, "Failed to decode file data");
        }

    }

    private void sendOptionsRequestResponse(ChannelHandlerContext ctx) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendResponse(ChannelHandlerContext ctx, String responseString,
                              String contentType, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(responseString, CharsetUtil.UTF_8));

        response.headers().set(CONTENT_TYPE, contentType);
        response.headers().add("Access-Control-Allow-Origin", "*");

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendUploadedFileName(JSONObject fileName, ChannelHandlerContext ctx) {
        JSONObject jsonObj = new JSONObject();

        String msg = "Unexpected error occurred";
        String contentType = "application/json; charset=UTF-8";
        HttpResponseStatus status = HttpResponseStatus.OK;

        if (fileName != null) {
            msg = fileName.toString();
        } else {
            Logger.getLogger(MediaHandler.class.getName()).log(
                    Level.SEVERE, "uploaded file names are blank");
            status = HttpResponseStatus.BAD_REQUEST;
            contentType = "text/plain; charset=UTF-8";
        }

        sendResponse(ctx, msg, contentType, status);

    }

    private void reset() {
        //request = null;

        // destroy the decoder to release all resources
        decoder.destroy();
        decoder = null;
    }

    /**
     * Example of reading request by chunk and getting values from chunk to
     * chunk
     */
    private void readHttpDataChunkByChunk(ChannelHandlerContext ctx) {
        //decoder.isMultipart();
        if (decoder.isMultipart()) {
            try {
                while (decoder.hasNext()) {
                    //System.out.println("decoder has next");
                    InterfaceHttpData data = decoder.next();
                    if (data != null) {
                        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                            Attribute attribute = (Attribute) data;
                            try {
                                Logger.getLogger(MediaHandler.class.getName()).log(Level.INFO,"SOUIDAN");
                               response.add("alo",new JsonParser().parse(attribute.getString()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        writeHttpData(data, ctx);
                        data.release();
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } else {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST, "Not a multipart request");
        }

        //System.out.println("decoder has no next");
    }

    private void writeHttpData(InterfaceHttpData data, ChannelHandlerContext ctx) {
            try {
//                String value = "";
                FileUpload dataU   = (FileUpload)data;
                JsonObject final_response = new JsonObject();
                if(dataU.isCompleted()) {
                    String fileName = minio.uploadFile(dataU.get(), "image");
                    final_response = response.getAsJsonObject();
                    final_response.add("filename",new JsonParser().parse(fileName));
                }
//                sendUploadedFileName(json, ctx);
//                System.out.println(final_response.getAsString());
                ctx.fireChannelRead(final_response.getAsString()); //Send the response Json Command including the uploaded file aname to the next handler in the pipeline
            }
             catch(Exception e) {
                //responseContent.append("\tFile to be continued but should not!\r\n");
                sendError(ctx, HttpResponseStatus.BAD_REQUEST, "Unknown error occurred");
            }

    }



    private static boolean isImageExtension(String extension) {
        boolean isImageFile = false;
        String extensionInLowerCase = extension.toLowerCase();

        isImageFile |= extensionInLowerCase.equals(".jpg");
        isImageFile |= extensionInLowerCase.equals(".png");
        isImageFile |= extensionInLowerCase.equals(".jpeg");
        isImageFile |= extensionInLowerCase.equals(".gif");

        return isImageFile;

    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, String msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        sendError(ctx, status, "Failure: " + status.toString() + "\r\n");
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type) {

        BufferedImage resizedImage = new BufferedImage(THUMB_MAX_WIDTH, THUMB_MAX_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, THUMB_MAX_WIDTH, THUMB_MAX_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

}
