package com.web.poker.common.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
/*import org.apache.http.conn.ClientConnectionManager;*/
/*import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;*/
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
/*import org.apache.http.conn.ssl.SSLSocketFactory;*/
import org.apache.http.entity.StringEntity;
/*import org.apache.http.impl.client.DefaultHttpClient;*/
import org.apache.http.impl.client.HttpClients;
/*import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;*/
import org.apache.http.message.BasicNameValuePair;
/*import org.apache.http.params.CoreConnectionPNames;*/
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Title: HttpRequestUtil.java
 * @Package: com.util.http
 * @Description:HTTP请求实现类
 * @Comment:
 * @author: 
 * @CreateDate: 
 */
@Service
public class HttpRequestUtil {
	private final static Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

	/**
	 * @Title: doPost
	 * @Description:模拟post请求
	 * @author:
	 * @Create:
	 * @Modify:
	 * @param idCode用于识别post请求是否包含包体
	 *            （HttpEntity），即采用流的形式传递，若包含，idCode请传10000，
	 *            包体字符串key值请传postEntityString， 此时，若参数url带有字符"?"，即带有get请求方式的参数时，
	 *            参数dataMap中的参数只有postEntityString有效
	 *            ，反之，不带有问号，其他参数方法会自动组装成get请求的形式
	 * @return:
	 * @throws URISyntaxException
	 */
	public Map<String, String> doPost(String idCode, String url, String charset, Map<String, String> dataMap)
			throws Exception {
		String contents = String.format("请求URL :%s，请求参数: %s。", url, dataMap);
		logger.info(contents);
		logger.info("字符编码为：" + charset);
		Map<String, String> resultMap = new HashMap<String, String>();
		String result = "";
		String header = url.substring(0, 5);
		HttpClient httpclient = null;
		if (header.equalsIgnoreCase("https")) {
			httpclient = getHttpsClient();
		}else{
			httpclient = HttpClients.createDefault();
		}
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(10000).build();//设置请求和传输超时时间

		//HttpClient httpclient = new DefaultHttpClient();
		// http请求和https请求判断
		//httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
		// url空格及其他特殊字符转换
		// url = url.replaceAll("\\?", "%3F");
		// url = url.replaceAll("&", "%26");
		url = url.replaceAll(" ", "%20");
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);
		// 组织参数
		try {
			// 当传输的方式为流时
			if (idCode != null && !"".equals(idCode) && idCode.equalsIgnoreCase("10000")) {
				HttpEntity entity1 = new StringEntity(dataMap.get("postEntityString"), charset);
				httpPost.setEntity(entity1);
				dataMap.remove("postEntityString");
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				if (dataMap != null && !dataMap.isEmpty() && url.indexOf("?") == -1) { // 若url中不包含"?"
					for (String key : dataMap.keySet()) {
						logger.info(key + ":" + dataMap.get(key));
						params.add(new BasicNameValuePair(key, dataMap.get(key)));
					}
					String str = EntityUtils.toString(new UrlEncodedFormEntity(params, charset));
					// System.out.println("url组装" + gets.getURI().toString() +
					// "?" + str);
					httpPost.setURI(new URI(httpPost.getURI().toString() + "?" + str));
				}
			} else {

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				if (dataMap != null && !dataMap.isEmpty()) {
					for (String key : dataMap.keySet()) {
						logger.info(key + ":" + dataMap.get(key));
						params.add(new BasicNameValuePair(key, dataMap.get(key).trim()));// 处理参数的前后空格
					}
				}
				httpPost.setEntity(new UrlEncodedFormEntity(params, charset));
			}

			HttpResponse response = httpclient.execute(httpPost);
			// 获取返回的StatusLine
			StatusLine StatusLine = response.getStatusLine();
			//logger.info("响应状态：" + StatusLine);
			// resultMap.put("ProtocolVersion",
			// response.getProtocolVersion().toString());// 协议版本
			resultMap.put("StatusCode", String.valueOf(StatusLine.getStatusCode()));// 返回状态码
			resultMap.put("ReasonPhrase", StatusLine.getReasonPhrase());// 返回原因短语
			HttpEntity entity = response.getEntity();
			//result = EntityUtils.toString(entity, charset);
			result = convertStreamToString(entity.getContent());
			resultMap.put("respResult", result);
			//logger.info("响应结果： " + result);
			EntityUtils.consume(entity);
		} catch (UnsupportedEncodingException e) {
			throw e;
		} catch (ClientProtocolException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			httpPost.releaseConnection();
		}
		return resultMap;
	}

	/**
	 * @Title: doGet
	 * @Description:模拟get请求
	 * @author:
	 * @Create:
	 * @Modify:
	 * @param:
	 * @return:
	 */
	public static Map<String, String> doGet(String url, String charset, Map<String, String> dataMap) throws Exception {
		String contents = String.format("请求URL :%s，请求参数: %s。", url, dataMap);
		logger.info(contents);
		Map<String, String> resultMap = new HashMap<String, String>();
		String result = "";
		HttpClient httpclient = null;
		// http请求和https请求判断
		String header = url.substring(0, 5);
		if (header.equalsIgnoreCase("https")) {
			httpclient = getHttpsClient();
		}else{
			httpclient = HttpClients.createDefault();
		}
		//httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
		 RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();//设置请求和传输超时时间
		// url空格及其他特殊字符转换，get请求不需要对?及&符号进行转换
		url = url.replaceAll(" ", "%20");
		HttpGet gets = new HttpGet(url);
		gets.setConfig(requestConfig);
		// 组织参数
		try {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (dataMap != null && !dataMap.isEmpty()) {
				for (String key : dataMap.keySet()) {
					logger.info(key + ":" + dataMap.get(key));
					params.add(new BasicNameValuePair(key, dataMap.get(key).trim()));
				}
			}

			String str = EntityUtils.toString(new UrlEncodedFormEntity(params, charset));

			// System.out.println("url组装" + gets.getURI().toString() + "?" +
			// str);
			if (gets.getURI().toString().indexOf("?") == -1) {
				gets.setURI(new URI(gets.getURI().toString() + "?" + str));
			} else {
				gets.setURI(new URI(gets.getURI().toString() + "&" + str));
			}
			System.out.println(gets.getURI());
			// 发送请求
			HttpResponse response = httpclient.execute(gets);
			// 获取返回的StatusLine
			StatusLine StatusLine = response.getStatusLine();
			//logger.info("响应状态：" + StatusLine);
			// resultMap.put("ProtocolVersion",
			// response.getProtocolVersion().toString());// 协议版本
			resultMap.put("StatusCode", String.valueOf(StatusLine.getStatusCode()));// 返回状态码
			resultMap.put("ReasonPhrase", StatusLine.getReasonPhrase());// 返回原因短语
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, charset);
			resultMap.put("respResult", result);
			//logger.info("响应结果： " + result);
			EntityUtils.consume(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw e;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			gets.releaseConnection();
		}
		return resultMap;
	}

	/**
	 * @Title: getHttpsClient
	 * @Description:https请求配置（一）
	 * @author:
	 * @Create:
	 * @Modify:
	 * @param:
	 * @return:
	 */
	public static HttpClient getHttpsClient() {
		
		 try {  
	            SSLContext ctx = SSLContext.getInstance("TLS");  
	            X509TrustManager tm = new X509TrustManager() {  
	                public X509Certificate[] getAcceptedIssuers() {  
	                    return null;  
	                }  
	  
	                public void checkClientTrusted(X509Certificate[] arg0,  
	                        String arg1) throws CertificateException {  
	                }  
	  
	                public void checkServerTrusted(X509Certificate[] arg0,  
	                        String arg1) throws CertificateException {  
	                }  
	            };  
	            ctx.init(null, new TrustManager[] { tm }, null);  
	            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(  
	                    ctx, NoopHostnameVerifier.INSTANCE);  
	            HttpClient httpclient = HttpClients.custom()  
	                    .setSSLSocketFactory(ssf).build();  
	            return httpclient;  
	        } catch (Exception e) {  
	            return HttpClients.createDefault();  
	        }  
		
		/*try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			HttpClient client = HttpClients.createDefault();
			ctx.init(null, new TrustManager[] { tm }, null);
			// SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER为必须，否则报错
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			// 设置要使用的端口，默认是443
			sr.register(new Scheme("https", 443, ssf));
			return client;
		} catch (Exception ex) {
			return null;
		}*/
	}

	/**
	 * @Title: wrapClient
	 * @Description:https请求配置（二），留待备用
	 * @author:
	 * @Create:
	 * @Modify:
	 * @param:
	 * @return:
	 */
	/*public static HttpClient wrapClient(HttpClient base) {
		try {
			// TLS是SSL的继承者
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", 443, ssf));
			ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
			return new DefaultHttpClient(mgr, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}*/

	/**
	 * @Title: getDocument
	 * @Description:将文件流转化为Document
	 * @author:
	 * @Create:
	 * @Modify:
	 * @param:
	 * @return:
	 */
	public Document getDocument(InputStream inputStream) {
		Document tDocument = null;
		SAXReader tSAXReader = new SAXReader();
		BufferedInputStream tBufferedInputStream = new BufferedInputStream(inputStream);
		try {
			tDocument = tSAXReader.read(tBufferedInputStream);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return tDocument;
	}

	static String doMD5Sign(Map<String, String> signMap, String key) throws Exception {
		return MD5Utils.getMD5String(
				SortUtil.createLinkString(signMap, true, false, "") + "&" + MD5Utils.getMD5String(key).toLowerCase());
	}
	
	public static String convertStreamToString(InputStream is) {      
        StringBuilder sb1 = new StringBuilder();      
        byte[] bytes = new byte[4096];    
        int size = 0;    
          
        try {      
            while ((size = is.read(bytes)) > 0) {    
                String str = new String(bytes, 0, size, "UTF-8");    
                sb1.append(str);    
            }    
        } catch (IOException e) {      
            e.printStackTrace();      
        } finally {      
            try {      
                is.close();      
            } catch (IOException e) {      
               e.printStackTrace();      
            }      
        }      
        return sb1.toString();      
    }
	
	
	
	public static void main(String []args){
		
		HttpRequestUtil httpRequestUtil = new HttpRequestUtil();
		Map<String, String> data = new HashMap<String, String>();

		data.put("merchantId", "00000000000030");
		data.put("requestId", "C20171129140643");
		data.put("bizType", "0422");
		data.put("versionId", "1.0.0");
		data.put("orderId", "O20171129377066_311");
		data.put("signType", "MD5");
		data.put("signature", "397b3e61c99c841d0fd2d40a51e07fd1");
		Map<String, String> retHtml;
		try {
			retHtml = httpRequestUtil.doPost(null, "https://hlw-old.52bill.com/gateway/FormTrans.dor", "utf-8", data);
			String pram = retHtml.get("respResult");
			System.out.println(pram);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
