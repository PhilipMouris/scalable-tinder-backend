package Entities;

//import com.arangodb.velocypack.annotations.SerializedName;

public class RequestBody {
    private String method;
    private String command;


//
//    @SerializedName("post_object")
//    private PostDBObject post_object;
//
//    @SerializedName("post_id")
//    private String post_id;
//
//    @SerializedName("category_id")
//    private String category_id;
//
//    @SerializedName("comment_id")
//    private String comment_id;
//
//
//    @SerializedName("comment_object")
//    private CommentDBObject comment_object;
//
//    @SerializedName("category_object")
//    private CategoryDBObject category_object;
//
//    @SerializedName("board_object")
//    private BoardDBObject board_object;
//
//    @SerializedName("imageUrl")
//    private String imageUrl;
//
//    @SerializedName("board_id")
//    private String board_id;
//
//    @SerializedName("user_id")
//    private String user_id;
//
//    @SerializedName("tag_name")
//    private String tag_name;
//
//    @SerializedName("skip")
//    private int skip;
//
//    @SerializedName("limit")
//    private int limit;
//
//    @SerializedName("tag_object")
//    private TagDBObject tag_object;
//
//    @SerializedName("server_object")
//    private ServerDBObject server_object;
//
//    @SerializedName("server_id")
//    private String server_id;
//
//
//    public ServerDBObject getServer_object() {
//        return server_object;
//    }
//
//    public void setServer_object(ServerDBObject server_object) {
//        this.server_object = server_object;
//    }
//
//    public String getServer_id() {
//        return server_id;
//    }
//
//    public Message(String method, ServerDBObject server_object) {
//        this.method = method;
//        this.server_object = server_object;
//    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
//
//    public CategoryDBObject getCategory_object() {
//        return category_object;
//    }
//
//    public void setCategory_object(CategoryDBObject category_object) {
//        this.category_object = category_object;
//    }
//
//    public String getCategory_id() {
//        return category_id;
//    }
//
//    public void setCategory_id(String category_id) {
//        this.category_id = category_id;
//    }
//
//    public String getPost_id() {
//        return post_id;
//    }
//
//    public void setPost_id(String post_id) {
//        this.post_id = post_id;
//    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
//
//    public PostDBObject getPost_object() {
//        return post_object;
//    }
//
//    public void setPost_object(PostDBObject post_object) {
//        this.post_object = post_object;
//    }
//
//    public TagDBObject getTag_object() {
//        return tag_object;
//    }
//
//    public void setTag_object(TagDBObject tag_object) {
//        this.tag_object = tag_object;
//    }
//
//    public String getUser_id() {
//        return user_id;
//    }
//
//    public void setUser_id(String user_id) {
//        this.user_id = user_id;
//    }
//
//    public BoardDBObject getBoard_object() {
//        return board_object;
//    }
//
//    public void setBoard_object(BoardDBObject board_object) {
//        this.board_object = board_object;
//    }
//
//    public String getBoard_id() {
//        return board_id;
//    }
//
//    public void setBoard_id(String board_id) {
//        this.board_id = board_id;
//    }
//
//    public String getTag_name() {
//        return tag_name;
//    }
//
//    public void setTag_name(String tag_name) {
//        this.tag_name = tag_name;
//    }
//
//    public int getSkip() {
//        return skip;
//    }
//
//    public void setSkip(int skip) {
//        this.skip = skip;
//    }
//
//    public int getLimit() {
//        return limit;
//    }
//
//    public void setLimit(int limit) {
//        this.limit = limit;
//    }
//
//    public String getComment_id() {
//        return comment_id;
//    }
//
//    public void setComment_id(String comment_id) {
//        this.comment_id = comment_id;
//    }
//
//    public CommentDBObject getComment_object() {
//        return comment_object;
//    }
//
//    public void setComment_object(CommentDBObject comment_object) {
//        this.comment_object = comment_object;
//    }
//
//
//    @com.google.gson.annotations.SerializedName("payload")
//    private User payload;
//    private String otherUserId;
//    private String pinId;
//    private String boardId;
//    private String hashtagId;
//    private String categoryId;
//
//    public String getCategoryId() {
//        return categoryId;
//    }
//
//    public void setCategoryId(String categoryId) {
//        this.categoryId = categoryId;
//    }
//
//    public String getHashtagId() {
//        return hashtagId;
//    }
//
//    public void setHashtagId(String hashtagId) {
//        this.hashtagId = hashtagId;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public void setServer_id(String server_id) {
//        this.server_id = server_id;
//    }
//
//    public String getPhotoId() {
//        return photoId;
//    }
//
//    public void setPhotoId(String photoId) {
//        this.photoId = photoId;
//    }
//    private String photoId;
//
//    public String getOtherUserId() {
//        return otherUserId;
//    }
//
//    public void setOtherUserId(String otherUserId) {
//        this.otherUserId = otherUserId;
//    }
//
//    public String getPinId() {
//        return pinId;
//    }
//
//    public void setPinId(String pinId) {
//        this.pinId = pinId;
//    }
//
//    public String getBoardId() {
//        return boardId;
//    }
//
//    public void setBoardId(String boardId) {
//        this.boardId = boardId;
//    }
//    // private String userId;
//
//    public User getPayload() {
//        return payload;
//    }
//
//    public void setPayload(User payload) {
//        this.payload = payload;
//    }
//
//    public Message() {
//    }
//
//    public Message(String method, PostDBObject post_object) {
//        this.method = method;
//        this.post_object = post_object;
//    }
}
