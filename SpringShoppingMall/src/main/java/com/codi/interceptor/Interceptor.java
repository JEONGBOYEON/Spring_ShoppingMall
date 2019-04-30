package com.codi.interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.codi.dto.MemberDTO;

public class Interceptor extends HandlerInterceptorAdapter{

	private static final Logger logger = LoggerFactory.getLogger(Interceptor.class);


	static final String[] EXCLUDE_URL_LIST = {//�α��� ������, ȸ������ ������ ���� ���ͼ��� ���� ���� ������
			"/login","/mem"
	};

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String reqUrl = request.getRequestURL().toString(); 


		/** �α���üũ ���� ����Ʈ */

		for( String target : EXCLUDE_URL_LIST ){

			if(reqUrl.indexOf(target)>-1){ //indexOf �̿� URL�ּҿ� �α���üũ ���� �ּҰ� ���ԵǾ� �ִ��� Ȯ��

				return true;

			}

		}

		HttpSession session = request.getSession();

		MemberDTO info = (MemberDTO)session.getAttribute("customInfo");

		if(info==null){

			logger.info(">> interceptor catch!!! userId is null.. ");

			session.invalidate();

			response.sendRedirect(request.getContextPath() + "/mem/login.action");

			return false;

		}

		return true;

	}

}