package com.re.paas.api.fusion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface Part {

    /**
     * Gets the content of this part as an <tt>InputStream</tt>
     * 
     * @return The content of this part as an <tt>InputStream</tt>
     * @throws IOException If an error occurs in retrieving the content as an <tt>InputStream</tt>
     */
    public InputStream getInputStream() throws IOException;

    /**
     * Gets the content type of this part.
     *
     * @return The content type of this part.
     */
    public String getContentType();

    /**
     * Gets the name of this part
     *
     * @return The name of this part as a <tt>String</tt>
     */
    public String getName();

    public String getSubmittedFileName();

    public long getSize();

    public void delete() throws IOException;

    public String getHeader(String name);

    public Collection<String> getHeaders(String name);

    public Collection<String> getHeaderNames();

}
