package com.cgi.open.easyshare.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.cgi.open.ServicesMapper;
import com.cgi.open.Utilities.ServiceInvoker;
import com.cgi.open.easyshare.DuplicateSessionException;
import com.cgi.open.easyshare.EasyShareServices;
import com.cgi.open.easyshare.InvalidServiceInvocationException;
import com.cgi.open.easyshare.SessionNotFoundException;
import com.cgi.open.easyshare.UserTypeNotValidException;
import com.cgi.open.easyshare.model.UserDetails;
import com.cgi.open.userconcerns.SeparationOfUserConcerns;
import com.cgi.open.userconcerns.model.ServiceDef;
import com.cgi.open.userconcerns.model.ServiceGroupDef;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Servlet Filter implementation class UserAccessFilter
 */
public class UserAccessFilter implements Filter {

	private FilterConfig filterConfig;
	EasyShareServices easy;
	SeparationOfUserConcerns sp;

	public UserAccessFilter() {

	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String service;
		ServiceDef sd;
		String userEmail;
		Integer sessionId;
		
			
		HttpServletRequest req = (HttpServletRequest) request;
		MyHttpServletRequest myRequest=new MyHttpServletRequest(req);
		
		ServiceResponse sr = new ServiceResponse();
		RenderResponse r=new RenderResponse();
		if(myRequest.getParameter(REQUEST_PARAMETERS.SESSION_ID)!=null){
			sessionId = Integer.valueOf(myRequest.getParameter(REQUEST_PARAMETERS.SESSION_ID));
		}
		else{
			sessionId=null;
		}
		service=req.getServletPath();
		service=service.substring(1,service.length());
		sr.initServiceResponse(myRequest, service);
		ServiceGroupDef group=loadXml();
		userEmail=getUser(req);
		userEmail=userEmail+"@cgi.com";		
		ServiceInvoker.setUserDetails(new UserDetails(userEmail));
		try {
			if(group!=null){
				sd=loadServiceDef(group, service);
				if(sd!=null){
					if(userEmail!=null){
						if (sp.isServReqValid(sd,userEmail, sessionId)) {
							chain.doFilter(request, response);
						}
						else {
							throw new InvalidServiceInvocationException();
						}
					}else{
						sr.setCode("FAILURE");
						sr.setMessage("User email is required");
						r.render(response,sr);
					}
				}else{
					sr.setCode("FAILURE");
					sr.setMessage("Service not available!!!");
					r.render(response,sr);
				}
			}else{
				sr.setCode("FAILURE");
				sr.setMessage("service definitions not defined");
				r.render(response,sr);
			}
		} catch (InvalidServiceInvocationException e) {
			sr.setCode("FAILURE");
			sr.setMessage("Invalid service invocation");
			r.render(response,sr);

		} catch (UserTypeNotValidException e) {
			sr.setCode("FAILURE");
			sr.setMessage("User Type is not valid");
			r.render(response,sr);
		} catch (SessionNotFoundException e) {
			sr.setCode("FAILURE");
			sr.setMessage("The Session requested is not available");
			r.render(response,sr);
		}
		// 
		// 
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.filterConfig = fConfig;
		easy = ServicesMapper.getEasyShareServicesProxyInstance();
		sp = ServicesMapper.getSeparationOfUserConcernsProxyInstance();

	}

	public ServiceGroupDef loadXml() {
		String file = "D:/sanjana/Projects/OpenCGI/UserConcerns.xml";
		ServiceGroupDef g = null;
		XStream x = new XStream(new DomDriver());
		x.alias("ServiceDefinitionGroup", ServiceGroupDef.class);
		x.alias("ServiceDefinition", ServiceDef.class);
		x.addImplicitCollection(ServiceGroupDef.class, "serviceDefinitions");
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			g = (ServiceGroupDef) x.fromXML(fis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return g;

	}

	public ServiceDef loadServiceDef(ServiceGroupDef group, String service) {
		ServiceDef sd = null;
		if (group != null) {
			for (ServiceDef d : group.getServiceDefinitions()) {
				if (d.getServiceName().equals(service)) {
					sd = d;
					break;
				}
			}

		} 
		return sd;
	}
	
	public String getUser(HttpServletRequest req){
		String userEmail=null;
		Cookie[] cookies = req.getCookies();
		for(Cookie thisCookie : cookies) {
			String cookieName = thisCookie.getName();
			if(cookieName.equalsIgnoreCase("es")) {
				userEmail = thisCookie.getValue();
				break;
			}
		}
		return userEmail;		
		
	}

}
