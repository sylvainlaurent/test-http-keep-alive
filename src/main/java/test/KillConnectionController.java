package test;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class KillConnectionController {
	@Autowired
	private ConnectionKiller killer;

	private AtomicInteger nbRequests = new AtomicInteger();

	@RequestMapping(value = "/*", produces = "text/plain", method = { GET, POST })
	@ResponseBody
	public String ping(HttpServletRequest request, HttpServletResponse response) {

		int cnt = nbRequests.incrementAndGet();
		if (cnt % 4 == 0) {
			killer.killConnection(response);
		}

		return "OK, " + request.getMethod() + " request #" + cnt;
	}

}
