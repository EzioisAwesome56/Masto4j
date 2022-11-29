package com.github.ezioisawesome56.masto4j;

import com.github.ezioisawesome56.masto4j.exceptions.MastodonAPIException;
import com.github.ezioisawesome56.masto4j.jsonObjects.*;
import com.github.ezioisawesome56.masto4j.jsonObjects.subobjects.Account;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Mastodon {

    // static variables
    private static final String redirect_urli = "urn:ietf:wg:oauth:2.0:oob";
    private String instanceurl;
    private String appname;
    private boolean writescope = false;
    private boolean readsceope = true;
    private final Gson g = new GsonBuilder().setPrettyPrinting().create();
    // application id shit goes here
    private String clientid;
    private String clientsecret;
    private String access_token;


    public void setWritescope(boolean in){
        this.writescope = in;
    }

    public void setReadsceope(boolean in){
        this.readsceope = in;
    }

    public void setClientid(String id){
        this.clientid = id;
    }
    public void setClientsecret(String secret){
        this.clientsecret = secret;
    }
    public void setAccess_token(String token){
        this.access_token = token;
    }


    public Mastodon(String url, String appname){
        this.instanceurl = url;
        this.appname = appname;
    }

    public Mastodon(String url, String appname, String clientid, String secret){
        this.instanceurl = url;
        this.appname = appname;
        this.clientid = clientid;
        this.clientsecret = secret;
    }

    public Mastodon(String url, String appname, String clientid, String secret, String access_token){
        this.instanceurl = url;
        this.appname = appname;
        this.clientid = clientid;
        this.clientsecret = secret;
        this.access_token = access_token;
    }

    /**
     * used to register your application with an instance
     * @return object with required credentials inside
     * @throws IOException
     * @throws MastodonAPIException
     */
    public ApplicationCreateResponse registerApplication() throws IOException, MastodonAPIException {
        String baseurl = "/api/v1/apps";
        // create the post data
        List<NameValuePair> params = new ArrayList<>();
        // client name
        params.add(new BasicNameValuePair("client_name", appname));
        // some magic bullshit
        params.add(new BasicNameValuePair("redirect_uris", redirect_urli));
        // add it to params
        params.add(new BasicNameValuePair("scopes", buildScope()));
        // do the request
        String page = httpDoPost(params, baseurl, null);
        // create mastodon api response object thing
        return g.fromJson(page, ApplicationCreateResponse.class);
    }

    /**
     * function to help reduce on copied pastted code over and over and over
     */
    private String httpDoPost(List<NameValuePair> params, String url, HashMap<String, String> headers) throws IOException, MastodonAPIException{
        // create a basic http client
        CloseableHttpClient client = HttpClients.createDefault();
        // make a new post objectt
        HttpPost post = new HttpPost(this.instanceurl + url);
        // do we have headers?
        if (headers != null){
            // add headers as required
            for (Map.Entry<String, String> header : headers.entrySet()){
                post.addHeader(header.getKey(), header.getValue());
            }
        }
        // set the entity
        post.setEntity(new UrlEncodedFormEntity(params));
        // do the request
        CloseableHttpResponse response = client.execute(post);
        // first convert what was returned tto a string
        String returned_page = EntityUtils.toString(response.getEntity(), "UTF-8");
        // check the status code
        if (response.getStatusLine().getStatusCode() != 200) {
            // aw shit, there was an error
            MastodonError error = g.fromJson(returned_page, MastodonError.class);
            // close everything before we yeet
            int statuscode = response.getStatusLine().getStatusCode();
            client.close();
            response.close();
            throw new MastodonAPIException(statuscode, error);
        }
        // otherwise close and return the string
        client.close();
        response.close();
        return returned_page;
    }
    private String httpDoGet(String url, HashMap<String, String> headers) throws MastodonAPIException, IOException {
        // make the client
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(this.instanceurl + url);
        // do we have headers?
        if (headers != null){
            // add the headers
            for (Map.Entry<String, String> header : headers.entrySet()){
                get.addHeader(header.getKey(), header.getValue());
            }
        }
        // do the request
        CloseableHttpResponse response = client.execute(get);
        String returned_page = EntityUtils.toString(response.getEntity(), "UTF-8");
        int statuscode = response.getStatusLine().getStatusCode();
        // did we have an error?
        if (statuscode != 200){
            // dah shit, we did have an error
            client.close();
            response.close();
            MastodonError e = g.fromJson(returned_page, MastodonError.class);
            throw new MastodonAPIException(statuscode, e);
        }
        // otherwise, just return the string
        client.close();
        response.close();
        return returned_page;
    }

    /**
     * gets all notifications for the account the access token belongs too
     * @return notifaction response object
     * @throws IOException
     * @throws MastodonAPIException
     */
    public NotificationResponse GetAllNotifications() throws IOException, MastodonAPIException {
        String baseurl = "/api/v1/notifications";
        // do we have an access token?
        if (this.access_token.isEmpty()){
            throw new NullPointerException("Error: no Access Token!");
        }
        // do the requestt
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + this.access_token);
        String content = httpDoGet(baseurl, headers);
        // do just a little bit of processing to the string
        String processed = "{ fak: " + content + "}";
        return g.fromJson(processed, NotificationResponse.class);
    }

    /**
     * posts a status to instance, plaintext only
     * @param text text to post
     * @return status response object
     * @throws IOException
     * @throws MastodonAPIException
     */
    public StatusResponse postBasicTextStatus(String text) throws IOException, MastodonAPIException {
        // do we have an access token?
        if (this.access_token.isEmpty()){
            throw new NullPointerException("Error: no access token provided!");
        }
        if (text.isEmpty() || text == null){
            throw new NullPointerException("Error: post text is invalid");
        }
        String baseurl = "/api/v1/statuses";
        // make form datta
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("status", text));
        // do request
        HashMap<String, String> why = new HashMap<>();
        why.put("Authorization", "Bearer " + this.access_token);
        String returnedcont = httpDoPost(params, baseurl, why);
        return g.fromJson(returnedcont, StatusResponse.class);
    }

    /**
     * Posts a status containing a media attachment that you uploaded previously
     * @param text text to post with status
     * @param mediaid id of the media you previously uploaded
     * @return object form of the API response
     * @throws IOException
     * @throws MastodonAPIException
     */
    public StatusResponse postStatusWithMedia(String text, String mediaid) throws IOException, MastodonAPIException {
        // do we have an access token?
        if (this.access_token.isEmpty()){
            throw new NullPointerException("Error: no access token!");
        }
        String baseurl = "/api/v1/statuses";
        // make form data
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("status", text));
        // add the media id
        params.add(new BasicNameValuePair("media_ids[]", mediaid));
        // do the request
        HashMap<String, String> headerinfo = new HashMap<>();
        headerinfo.put("Authorization", "Bearer " + this.access_token);
        String returned_cont = httpDoPost(params, baseurl, headerinfo);
        // parse and return response
        return g.fromJson(returned_cont, StatusResponse.class);
    }

    /**
     * uploads an image to the selected instance
     * @param input byte[] for source image
     * @param desc image description
     * @param filename filename to upload as
     * @return object form of api response
     * @throws NullPointerException
     * @throws IOException
     * @throws MastodonAPIException
     */
    public MediaUploadResponse UploadImage(byte[] input, String desc, String filename) throws NullPointerException, IOException, MastodonAPIException {
        /**
         * cant really refacttor this because its a special case. oops
         */
        // do we have an access token first?
        if (this.access_token.isEmpty()){
            throw new NullPointerException("Error: no access token!");
        }
        // setup the client
        String baseurl = "/api/v1/media";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(this.instanceurl + baseurl);
        // setup the headers correctly
        post.addHeader("Authorization", "Bearer " + this.access_token);
        // create form data
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("description", desc);
        builder.addBinaryBody("file", input, ContentType.APPLICATION_OCTET_STREAM, filename);
        // build the entity
        HttpEntity ent = builder.build();
        post.setEntity(ent);
        // get the response
        CloseableHttpResponse response = client.execute(post);
        String beans = EntityUtils.toString(response.getEntity());
        int statuscode = response.getStatusLine().getStatusCode();
        if (statuscode != 200){
            // create error object
            MastodonError e = g.fromJson(beans, MastodonError.class);
            throw new MastodonAPIException(statuscode, e);
        }
        // create the mediauploadresponse
        MediaUploadResponse mur = g.fromJson(beans, MediaUploadResponse.class);
        client.close();
        response.close();
        return mur;
    }
    public MediaUploadResponse UploadImage(byte[] in, String filename) throws MastodonAPIException, IOException {
        return UploadImage(in, "Uploaded via Masto4j", filename);
    }

    /**
     * obtains a user token using username and password
     * @param username
     * @param password
     * @return response object with access token inside
     * @throws NullPointerException
     * @throws IOException
     * @throws MastodonAPIException
     */
    public TokenResponse ObtainTokenViaUserPass(String username, String password) throws NullPointerException, IOException, MastodonAPIException {
        if (username.isEmpty()){
            throw new NullPointerException("Username is empty!");
        }
        if (password.isEmpty()){
            throw new NullPointerException("Password is empty!");
        }
        if (this.clientsecret.isEmpty() || this.clientid.isEmpty()){
            throw new NullPointerException("Error: missing client id/secret!");
        }
        // create form data
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("client_id", this.clientid));
        params.add(new BasicNameValuePair("client_secret", this.clientsecret));
        params.add(new BasicNameValuePair("redirect_uri", redirect_urli));
        // user information goes here
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        // scope shit
        params.add(new BasicNameValuePair("scope", buildScope()));
        // do the request
        String returned_cont = httpDoPost(params, "/oauth/token", null);
        // parse and return object
        return g.fromJson(returned_cont, TokenResponse.class);
    }

    /**
     * gets an account via an ID
     * @param id account id in the database
     * @return accountt objectt
     * @throws NullPointerException no access token provided!
     * @throws MastodonAPIException mastodon fucking exploded
     * @throws IOException http client went haha boom
     */
    public Account GetAccountWithAuthorization(String id) throws NullPointerException, MastodonAPIException, IOException {
        // check to make sure we even have an access token
        if (this.access_token == null){
            throw new NullPointerException("Error: no access token has been provided!");
        }
        // firstt we need to setup the headers ig
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + this.access_token);
        String cont = httpDoGet("/api/v1/accounts/" + id, headers);
        // TODO: handle account suspension shit
        return g.fromJson(cont, Account.class);
    }

    private String buildScope(){
        StringBuilder b = new StringBuilder();
        if (this.writescope) b.append("write ");
        if (this.readsceope) b.append("read ");
        return b.toString();
    }
}
