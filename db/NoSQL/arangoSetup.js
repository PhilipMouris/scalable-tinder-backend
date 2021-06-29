db = require('arangojs')();
db.createDatabase('tinderDB').then(()=> console.log("db created"),err=>console.log("Failed to create db"))
db.useDatabase('tinderDB')
collection = db.collection("users");
collection.create().then(
  () => console.log('Collection created'),
  err => console.error('Failed to create collection:', err)
);
collection = db.collection("notifications");
collection.create().then(
  () => console.log('Collection created'),
  err => console.error('Failed to create collection:', err)
);
collection = db.collection("profileViews");
collection.create().then(
  () => console.log('Collection created'),
  err => console.error('Failed to create collection:', err)
);
collection = db.collection("chats");
collection.create().then(
  () => console.log('Collection created'),
  err => console.error('Failed to create collection:', err)
);
