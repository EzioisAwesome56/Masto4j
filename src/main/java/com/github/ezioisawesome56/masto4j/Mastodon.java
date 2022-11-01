package com.github.ezioisawesome56.masto4j;

import com.github.ezioisawesome56.masto4j.exceptions.MastodonAPIException;
import com.github.ezioisawesome56.masto4j.jsonObjects.ApplicationCreateResponse;
import com.github.ezioisawesome56.masto4j.jsonObjects.MastodonError;
import com.github.ezioisawesome56.masto4j.jsonObjects.StatusResponse;
import com.github.ezioisawesome56.masto4j.jsonObjects.TokenResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * used to register your application with an instance
     * @return object with required credentials inside
     * @throws IOException
     * @throws MastodonAPIException
     */
    public ApplicationCreateResponse registerApplication() throws IOException, MastodonAPIException {
        String baseurl = "/api/v1/apps";
        // setup the httpclient
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost psot = new HttpPost(this.instanceurl + baseurl);
        // create the post data
        List<NameValuePair> params = new ArrayList<>();
        // client name
        params.add(new BasicNameValuePair("client_name", appname));
        // some magic bullshit
        params.add(new BasicNameValuePair("redirect_uris", redirect_urli));
        // add it to params
        params.add(new BasicNameValuePair("scopes", buildScope()));
        // do the request
        psot.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = client.execute(psot);
        // get the response as a string
        String beans = EntityUtils.toString(response.getEntity(), "UTF-8");
        // check the error code
        if (response.getStatusLine().getStatusCode() != 200){
            MastodonError error = g.fromJson(beans, MastodonError.class);
            throw new MastodonAPIException(response.getStatusLine().getStatusCode(), error);
        }
        // create mastodon api response object thing
        ApplicationCreateResponse acr = g.fromJson(beans, ApplicationCreateResponse.class);
        response.close();
        client.close();
        return acr;
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
        // create new http clientt
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(this.instanceurl + baseurl);
        // set the required header
        post.addHeader("Authorization", "Bearer " + access_token);
        // create the post form data
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("status", text));
        post.setEntity(new UrlEncodedFormEntity(params));
        // do request
        CloseableHttpResponse response = client.execute(post);
        String beans = EntityUtils.toString(response.getEntity());
        int statuscode = response.getStatusLine().getStatusCode();
        if (statuscode != 200){
            MastodonError error = g.fromJson(beans, MastodonError.class);
            throw new MastodonAPIException(statuscode, error);
        }
        // if we are here, should be good to go
        StatusResponse s = g.fromJson(beans, StatusResponse.class);
        client.close();
        response.close();
        return s;
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
        // spin up another httpclient
        CloseableHttpClient client = HttpClients.createDefault();
        // make new params box
        HttpPost post = new HttpPost(this.instanceurl + "/oauth/token");
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
        post.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = client.execute(post);
        String beans = EntityUtils.toString(response.getEntity(), "UTF-8");
        // check to makle sure its ok
        if (response.getStatusLine().getStatusCode() != 200){
            MastodonError e = g.fromJson(beans, MastodonError.class);
            throw new MastodonAPIException(response.getStatusLine().getStatusCode(), e);
        }
        // create and return object
        TokenResponse t = g.fromJson(beans, TokenResponse.class);
        client.close();
        response.close();
        return t;
    }

    private String buildScope(){
        StringBuilder b = new StringBuilder();
        if (this.writescope) b.append("write ");
        if (this.readsceope) b.append("read ");
        return b.toString();
    }
}
