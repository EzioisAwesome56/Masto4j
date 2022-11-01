# Masto4J
A java library for interacting with Mastodon's API. Written using apache http client v4
## usage
```java
// create a new objectt with base url and appname
Mastodon m = new Mastodon("http://example", "beans-app");
// set scope options (read scope is on by default)
m.setWritescope(true);
// register your app with the instance
ApplicationCreateResponse a = m.registerApplication();
a.getClient_id() // client id
a.getClient_secret() // secret id
// provide the client with these details        
m.setClientid(a.getClient_id());
m.setClientsecret(a.getClient_secret());
// get user access token
TokenResponse t = m.ObtainTokenViaUserPass("user", "coolpass");
t.getAccess_token() // access token for authentiation
// set the token in the client
m.setAccess_token(t.getAccess_token());
// make a simple text post
StatusResponse s = m.postBasicTextStatus("hi");
```

better documentation will come at some point