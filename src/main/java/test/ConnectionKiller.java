package test;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.net.NioChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConnectionKiller {
	@Autowired
	private ReflectionHelper reflectionHelper;

	public void killConnection(HttpServletResponse response) {
		NioChannel tomcatChannel = (NioChannel) reflectionHelper.getValueForPath(response, "response.coyoteResponse.outputBuffer.socket");
		try {
			tomcatChannel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
