
package com.re.paas.api.cryto;

/**
 * Time Stamping Authority (TSA) Client [RFC 3161].
 */
public class TSAClient
{

    private final String url;
    private final String username;
    private final String password;
    private final String digest;

    
    /**
     *
     * @param url the URL of the TSA service
     * @param username user name of TSA
     * @param password password of TSA
     * @param digest the message digest to use
     */
    public TSAClient(String url, String username, String password, String digest)
    {
        this.url = url;
        this.username = username;
        this.password = password;
        this.digest = digest;
    }
    
    public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDigest() {
		return digest;
	}

}