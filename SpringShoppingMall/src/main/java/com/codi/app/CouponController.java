package com.codi.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codi.dao.CouponDAO;
import com.codi.dto.CouponDTO;
import com.codi.dto.IssueDTO;
import com.codi.dto.MemberDTO;
import com.codi.dto.MyCouponDTO;
import com.codi.dto.ProductDTO;
import com.codi.util.MyUtil;

@Controller
public class CouponController {
	
	@Autowired
	@Qualifier("couponDAO")//Bean ��ü ���� 
	CouponDAO dao;
	
	@Autowired
	MyUtil myUtil;// Bean ��ü ����
			
	@RequestMapping(value = "couponA/couponAllList.action", method = RequestMethod.GET)
	public String couponAllList(HttpServletRequest req) {
		
		List<CouponDTO> lists = dao.getList();

		req.setAttribute("lists", lists);
		
		return "coupon/couponAllList";
	}
	
	@RequestMapping(value = "/coupon/couponIssue_ok.action", method = RequestMethod.POST)
	public String couponIssue_ok(HttpServletRequest req,HttpServletResponse response,HttpSession session,CouponDTO coupondto) throws IOException{
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo"); 
		
		//alterâ�� ����ֱ� ���� ����
		response.setCharacterEncoding("utf-8"); 
		PrintWriter writer = response.getWriter();
		
		//��������Ʈ �����ֱ�
		List<CouponDTO> lists2 = dao.getList();
		req.setAttribute("lists", lists2);
		
		//��ް˻�
		if(!coupondto.getCouponGrade().equals(info.getUserGrade())){
			writer.println("<script type='text/javascript'>");
			writer.println("alert('����� �ٸ��ϴ�.');");
			writer.println("history.back();");
			writer.println("</script>");
			writer.flush();

			return "coupon/couponAllList";
		}
		
		//�̸����������� �˻�
		List<IssueDTO> lists = dao.couponGetLists();
		Iterator<IssueDTO> it = lists.iterator();
		
        while (it.hasNext()){
			IssueDTO dto = it.next();

			if(dto.getUserId().equals(info.getUserId())&&dto.getCouponKey()==coupondto.getCouponKey()){
				writer.println("<script type='text/javascript'>");
				writer.println("alert('�̹� �߱޹��� �����Դϴ�');");
				writer.println("history.back();");
				writer.println("</script>");
				writer.flush();
				
				return "coupon/couponAllList";
			}
        }
		
        //�����߱����̺� insert
		IssueDTO issuedto = new IssueDTO();
		
		issuedto.setCouponKey(coupondto.getCouponKey());
		issuedto.setUserId(info.getUserId());

		dao.couponInsertData(issuedto);
		
		writer.println("<script type='text/javascript'>");
		writer.println("alert('�����߱��� �Ϸ�Ǿ����ϴ�.');");
		writer.println("history.back();");
		writer.println("</script>");
		writer.flush();
		
		return "coupon/couponAllList";
	}
	
	@RequestMapping(value = "/coupon/couponMyList.action", method = RequestMethod.GET)
	public String couponMyList(HttpServletRequest req,HttpServletResponse response,HttpSession session,CouponDTO coupondto) throws IOException{
		
		List<CouponDTO> lists = dao.getList();

		req.setAttribute("lists", lists);
		
		return "coupon/couponMyList";
	}
	
	@RequestMapping(value = "/coupon/myCouponList.action", method = RequestMethod.GET)
	public String myCouponList(HttpServletRequest req,HttpServletResponse response,HttpSession session){
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo"); 
		
		List<MyCouponDTO> lists = dao.couponGetList(info.getUserId());
		
		req.setAttribute("lists", lists);
		
		return "coupon/myCouponList";
	}
	
	@RequestMapping(value = "/coupon/myUsedCouponList.action", method = RequestMethod.GET)
	public String myUsedCouponList(HttpServletRequest req,HttpServletResponse response,HttpSession session){
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo"); 
		
		List<MyCouponDTO> lists = dao.couponGetList(info.getUserId());
		
		//��¥��
		SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		//���糯¥
	    String strDate = dateFormat.format(date);
	    Date date1 = null;
	    
	    //��¥Ȯ���ؼ� �������� �ƴ��� �־��ֱ�(�����̸� used�� 'M'�ֱ�)
	    //date1�� ���⳯¥
	    //date2�� ���糯¥
	    //���⳯¥�� ���糯¥���� �����̸� true = ���� ���Ⱑ �ȵ�
		Iterator<MyCouponDTO> it = lists.iterator();
		
        while (it.hasNext()){

        	MyCouponDTO dto = it.next();

	        try {
				date1 = dateFormat.parse(dto.getCouponEndDate());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        Date date2 = null;
			try {
				date2 = dateFormat.parse(strDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        boolean re = date1.after(date2);
	        
	        if(re!=true){
	        	dao.couponInsertM(dto.getCouponKey(),info.getUserId());
	        }
        }
		

		List<MyCouponDTO> lists2 = dao.couponGetList(info.getUserId());
		
		req.setAttribute("lists", lists2);
		
		return "coupon/myUsedCouponList";
	}
}
