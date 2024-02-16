//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.coralpay.config;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class MySimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
    private final HostnameVerifier verifier;

    public MySimpleClientHttpRequestFactory(HostnameVerifier verifier) {
        this.verifier = verifier;
    }

    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection)connection).setHostnameVerifier(this.verifier);

            try {
                ((HttpsURLConnection)connection).setSSLSocketFactory(this.trustSelfSignedSSL().getSocketFactory());
            } catch (KeyStoreException var4) {
                var4.printStackTrace();
            } catch (NoSuchAlgorithmException var5) {
                var5.printStackTrace();
            } catch (KeyManagementException var6) {
                var6.printStackTrace();
            }
        }

        super.prepareConnection(connection, httpMethod);
    }

    public SSLContext trustSelfSignedSSL() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = (chain, authType) -> {
            return true;
        };
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((KeyStore)null, acceptingTrustStrategy).build();
        return sslContext;
    }
}
