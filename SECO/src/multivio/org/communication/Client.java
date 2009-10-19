package multivio.org.communication;

import java.io.IOException;
import java.io.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation
 */
public class Client extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private String packagePath = "multivio.org.";

	/**
	 * Default constructor.
	 */
	public Client() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("doGet");
		this.dispatchRequest(req, resp);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		System.out.println("doPost");
		this.dispatchRequest(req, resp);
	}
	
	private void dispatchRequest (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String list = req.getParameter("cl");
			String action = req.getParameter("act");
			Class c = Class.forName(packagePath + list);
			Method m = c.getMethod(action, new Class[] {
  								   HttpServletRequest.class, HttpServletResponse.class });
			Object[] parameters = new Object[] { req, resp };
			Object res = m.invoke(c.newInstance(), parameters);
			PrintWriter writer = resp.getWriter();
			writer.print(res.toString());
			writer.flush();
  		} catch (ClassNotFoundException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (SecurityException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (NoSuchMethodException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (IllegalArgumentException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (IllegalAccessException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (InvocationTargetException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (InstantiationException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
	}
}
