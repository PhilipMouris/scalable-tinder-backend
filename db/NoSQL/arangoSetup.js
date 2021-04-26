db = require('arangojs')();
userSchema=require("./userSchema.json")
notificationSchema=require("./notificationSchema.json")
profileViewSchema=require("./profileViewSchema.json")
chatSchema=require("./chatSchema.json")
db.createDatabase('tinderDB').then(()=> console.log("db created"),err=>console.log("Failed to create db"))
db.useDatabase('tinderDB')
collection = db.collection("users", { "schema": userSchema });
collection.create({"schema":userSchema}).then(
  () => console.log('Collection created'),
  err => console.error('Failed to create collection:', err)
);
collection = db.collection("notifications", { "schema": notificationSchema });
collection.create({"schema":notificationSchema}).then(
  () => console.log('Collection created'),
  err => console.error('Failed to create collection:', err)
);
collection = db.collection("profileViews", { "schema": profileViewSchema });
collection.create({"schema":profileViewSchema}).then(
  () => console.log('Collection created'),
  err => console.error('Failed to create collection:', err)
);
collection = db.collection("chats", { "schema": chatSchema });
collection.create({"schema":chatSchema}).then(
  () => console.log('Collection created'),
  err => console.error('Failed to create collection:', err)
);
