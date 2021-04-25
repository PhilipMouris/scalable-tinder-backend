db = require('arangojs')();
userSchema=require("./userSchema.json")
db.createDatabase('tinderDB10').then(()=> console.log("db created"),err=>console.log("Failed to create db"))
db.useDatabase('tinderDB10')
collection = db.collection("user", { "schema": userSchema });
collection.create({"schema":userSchema}).then(
  () => console.log('Collection created'),
  err => console.error('Failed to create collection:', err)
);
