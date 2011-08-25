package com.cgi.open.easyshare.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cgi.open.ServicesMapper;
import com.cgi.open.easyshare.DuplicateAppointmentException;
import com.cgi.open.easyshare.DuplicateResourceException;
import com.cgi.open.easyshare.EasyShareServices;
import com.cgi.open.easyshare.SessionNotFoundException;

/**
 * Servlet implementation class AddResource
 */
public class AddResource extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddResource() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			MyHttpServletRequest myRequest=new MyHttpServletRequest(request);
			ServiceResponse sr = new ServiceResponse();
			sr.initServiceResponse(myRequest, "AddResource");
			Integer sessionId = Integer.valueOf(myRequest.getParameter(REQUEST_PARAMETERS.SESSION_ID));
			String resourceName = myRequest.getParameter(REQUEST_PARAMETERS.RESOURCE_NAME);
			String resourceUrl = myRequest.getParameter(REQUEST_PARAMETERS.RESOURCE_URL);
			EasyShareServices easyshare = ServicesMapper.getEasyShareServicesProxyInstance();
			Integer resourceId=null;
			try {
				resourceId= easyshare.addResource(sessionId,resourceName,resourceUrl);
				sr.setCode("SUCCESS");
				sr.setMessage("SUCCESS");
				sr.setData(resourceId);
			} catch (SessionNotFoundException e) {
				sr.setCode("FAILURE");
				sr.setMessage(e.getMessage());
			} catch (DuplicateResourceException e) {
				sr.setCode("FAILURE");
				sr.setMessage(e.getMessage());
			}
			RenderResponse r=new RenderResponse();
			r.render(response, sr);
		}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
