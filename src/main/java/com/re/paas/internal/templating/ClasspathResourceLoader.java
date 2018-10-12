package com.re.paas.internal.templating;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.ExtProperties;

import com.re.paas.api.utils.ClassUtils;

public class ClasspathResourceLoader extends ResourceLoader {

	@Override
	public void init(ExtProperties configuration) {
	}

	@Override
	public Reader getResourceReader(String name, String encoding) throws ResourceNotFoundException {

        Reader result = null;

        if (StringUtils.isEmpty(name))
        {
            throw new ResourceNotFoundException ("No template name provided");
        }

        /**
         * look for resource in thread classloader first (e.g. WEB-INF\lib in
         * a servlet container) then fall back to the system classloader.
         */

        InputStream rawStream = null;
        try
        {
            rawStream = ClassUtils.getResourceAsStream( getClass(), name );
            
            if (rawStream != null)
            {
                result = buildReader(rawStream, encoding);
            }
        }
        catch( Exception fnfe )
        {
            if (rawStream != null)
            {
                try
                {
                    rawStream.close();
                }
                catch (IOException ioe) {}
            }
            throw new ResourceNotFoundException("ClasspathResourceLoader problem with template: " + name, fnfe );
        }

        if (result == null)
        {
            String msg = "ClasspathResourceLoader Error: cannot find resource " + name;

            throw new ResourceNotFoundException( msg );
        }

        return result;
		
	}

	@Override
	public boolean isSourceModified(Resource resource) {
		return false;
	}

	@Override
	public long getLastModified(Resource resource) {
		return 0;
	}

}
