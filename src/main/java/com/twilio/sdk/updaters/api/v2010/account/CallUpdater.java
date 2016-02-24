package com.twilio.sdk.updaters.api.v2010.account;

import com.twilio.sdk.clients.TwilioRestClient;
import com.twilio.sdk.converters.Promoter;
import com.twilio.sdk.exceptions.ApiConnectionException;
import com.twilio.sdk.exceptions.ApiException;
import com.twilio.sdk.http.HttpMethod;
import com.twilio.sdk.http.Request;
import com.twilio.sdk.http.Response;
import com.twilio.sdk.resources.RestException;
import com.twilio.sdk.resources.api.v2010.account.Call;
import com.twilio.sdk.updaters.Updater;

import java.net.URI;

public class CallUpdater extends Updater<Call> {
    private final String accountSid;
    private final String sid;
    private URI url;
    private HttpMethod method;
    private Call.Status status;
    private URI fallbackUrl;
    private HttpMethod fallbackMethod;
    private URI statusCallback;
    private HttpMethod statusCallbackMethod;

    /**
     * Construct a new CallUpdater.
     * 
     * @param accountSid The account_sid
     * @param sid Call Sid that uniquely identifies the Call to update
     */
    public CallUpdater(final String accountSid, final String sid) {
        this.accountSid = accountSid;
        this.sid = sid;
    }

    /**
     * A valid URL that returns TwiML. Twilio will immediately redirect the call to
     * the new TwiML upon execution..
     * 
     * @param url URL that returns TwiML
     * @return this
     */
    public CallUpdater setUrl(final URI url) {
        this.url = url;
        return this;
    }

    /**
     * A valid URL that returns TwiML. Twilio will immediately redirect the call to
     * the new TwiML upon execution..
     * 
     * @param url URL that returns TwiML
     * @return this
     */
    public CallUpdater setUrl(final String url) {
        return setUrl(Promoter.uriFromString(url));
    }

    /**
     * The HTTP method Twilio should use when requesting the URL. Defaults to
     * `POST`..
     * 
     * @param method HTTP method to use to fetch TwiML
     * @return this
     */
    public CallUpdater setMethod(final HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * Either `canceled` or `completed`. Specifying `canceled` will attempt to
     * hangup calls that are queued or ringing but not affect calls already in
     * progress. Specifying `completed` will attempt to hang up a call even if it's
     * already in progress..
     * 
     * @param status Status to update the Call with
     * @return this
     */
    public CallUpdater setStatus(final Call.Status status) {
        this.status = status;
        return this;
    }

    /**
     * A URL that Twilio will request if an error occurs requesting or executing the
     * TwiML at `Url`..
     * 
     * @param fallbackUrl Fallback URL in case of error
     * @return this
     */
    public CallUpdater setFallbackUrl(final URI fallbackUrl) {
        this.fallbackUrl = fallbackUrl;
        return this;
    }

    /**
     * A URL that Twilio will request if an error occurs requesting or executing the
     * TwiML at `Url`..
     * 
     * @param fallbackUrl Fallback URL in case of error
     * @return this
     */
    public CallUpdater setFallbackUrl(final String fallbackUrl) {
        return setFallbackUrl(Promoter.uriFromString(fallbackUrl));
    }

    /**
     * The HTTP method that Twilio should use to request the `FallbackUrl`. Must be
     * either `GET` or `POST`. Defaults to `POST`..
     * 
     * @param fallbackMethod HTTP Method to use with FallbackUrl
     * @return this
     */
    public CallUpdater setFallbackMethod(final HttpMethod fallbackMethod) {
        this.fallbackMethod = fallbackMethod;
        return this;
    }

    /**
     * A URL that Twilio will request when the call ends to notify your app..
     * 
     * @param statusCallback Status Callback URL
     * @return this
     */
    public CallUpdater setStatusCallback(final URI statusCallback) {
        this.statusCallback = statusCallback;
        return this;
    }

    /**
     * A URL that Twilio will request when the call ends to notify your app..
     * 
     * @param statusCallback Status Callback URL
     * @return this
     */
    public CallUpdater setStatusCallback(final String statusCallback) {
        return setStatusCallback(Promoter.uriFromString(statusCallback));
    }

    /**
     * The HTTP method that Twilio should use to request the `StatusCallback`.
     * Defaults to `POST`..
     * 
     * @param statusCallbackMethod HTTP Method to use with StatusCallback
     * @return this
     */
    public CallUpdater setStatusCallbackMethod(final HttpMethod statusCallbackMethod) {
        this.statusCallbackMethod = statusCallbackMethod;
        return this;
    }

    /**
     * Make the request to the Twilio API to perform the update.
     * 
     * @param client TwilioRestClient with which to make the request
     * @return Updated Call
     */
    @Override
    public Call execute(final TwilioRestClient client) {
        Request request = new Request(
            HttpMethod.POST,
            TwilioRestClient.Domains.API,
            "/2010-04-01/Accounts/" + this.accountSid + "/Calls/" + this.sid + ".json",
            client.getAccountSid()
        );
        
        addPostParams(request);
        Response response = client.request(request);
        
        if (response == null) {
            throw new ApiConnectionException("Call update failed: Unable to connect to server");
        } else if (response.getStatusCode() != TwilioRestClient.HTTP_STATUS_CODE_OK) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
        
            throw new ApiException(
                restException.getMessage(),
                restException.getCode(),
                restException.getMoreInfo(),
                restException.getStatus(),
                null
            );
        }
        
        return Call.fromJson(response.getStream(), client.getObjectMapper());
    }

    /**
     * Add the requested post parameters to the Request.
     * 
     * @param request Request to add post params to
     */
    private void addPostParams(final Request request) {
        if (url != null) {
            request.addPostParam("Url", url.toString());
        }
        
        if (method != null) {
            request.addPostParam("Method", method.toString());
        }
        
        if (status != null) {
            request.addPostParam("Status", status.toString());
        }
        
        if (fallbackUrl != null) {
            request.addPostParam("FallbackUrl", fallbackUrl.toString());
        }
        
        if (fallbackMethod != null) {
            request.addPostParam("FallbackMethod", fallbackMethod.toString());
        }
        
        if (statusCallback != null) {
            request.addPostParam("StatusCallback", statusCallback.toString());
        }
        
        if (statusCallbackMethod != null) {
            request.addPostParam("StatusCallbackMethod", statusCallbackMethod.toString());
        }
    }
}