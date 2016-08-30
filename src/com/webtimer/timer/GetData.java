package com.webtimer.timer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webtimer.timer.CountdownTimer;


@WebServlet("/getData")
public class GetData extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

    public GetData() {
    	super();
    }

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * Returns a simpel text-string of the current countdown.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/text;charset=UTF-8");
		response.getWriter().append(String.valueOf(CountdownTimer.getCountdown()/1000));
		response.getWriter().append("_").append(String.valueOf(CountdownTimer.getInterval()/1000));
		response.getWriter().append("_").append(String.valueOf(CountdownTimer.getInterval2()/1000));
	}

}