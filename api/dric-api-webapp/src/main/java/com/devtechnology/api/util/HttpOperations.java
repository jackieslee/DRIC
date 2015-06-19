package com.devtechnology.api.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * Utility methods for doing GET operations to external services
 * @author jbnimble
 *
 */
public class HttpOperations {
	Logger logger = Logger.getLogger(HttpOperations.class);
	
	public <V> V getMappedFromUlr(String url, Class<V> type) {
		V result = null;
		try {
			String httpResultStr = getFromUrl(url);
			result = new Gson().fromJson(httpResultStr, type);
		} catch(Exception e) {
			logger.error("Failed when performing a GET on url="+url+" and mapping result to class"+type.getName());
		}
		return result;
	}
	
	/**
	 * perform a GET on the given 'url' and return the result as a String
	 * @param url
	 * @return
	 */
	public String getFromUrl(String url) {
		String result = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(url);
			logger.info("Executing request " + httpget.getRequestLine());
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						throw new ClientProtocolException("Unexpected response status: " + status);
					}
				}
			};
			result = httpclient.execute(httpget, responseHandler);
		} catch (Exception e) {
			logger.error("Failed using url="+url, e);
		} finally {
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					logger.error("Failed to close httpclient using url="+url+" due to "+e.getMessage());
				}
			}
		}
		return result;
	}
}
