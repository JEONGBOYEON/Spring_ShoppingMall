package com.codi.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
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
import com.codi.dto.ProductDTO;
import com.codi.util.MyUtil;

@Controller
public class CouponController {
	
	@Autowired
	@Qualifier("couponDAO")//Bean ��ü ���� 
	CouponDAO dao;
	
	@Autowired
	MyUtil myUtil;// Bean ��ü ����
	
	@RequestMapping(value = "couponA/couponAdminCreated_ok.action", method = RequestMethod.POST)
	public String couponAdminCreated_ok(HttpServletRequest req, HttpSession session,CouponDTO dto,int period) {

		Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.DATE, period);
	     
	    // Ư�� ������ ��¥�� ���� �̱�
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    String strDate = df.format(cal.getTime());
		
		int maxNum = dao.getMaxNum();
		
		dto.setCouponEndDate(strDate);
		dto.setCouponKey(maxNum+1);
		dao.insertData(dto);
		
		
		return "redirect:/couponA/couponAdminList.action";
	}	
	
	@RequestMapping(value = "couponA/couponAdminCreated.action", method = RequestMethod.GET)
	public String couponAdminCreated(HttpServletRequest req, HttpSession session) {
		
		return "admin/couponAdminCreate";
	}
	
	@RequestMapping(value = "couponA/couponAdminList.action", method = RequestMethod.GET)
	public String couponAdminList(HttpServletRequest req, HttpSession session) {
		
		String cp = req.getContextPath();

		String pageNum = req.getParameter("pageNum");

		int currentPage = 1;

		if (pageNum != null)
			currentPage = Integer.parseInt(pageNum);

		int dataCount = dao.getDataCount();

		int numPerPage = 5;
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);

		if (currentPage > totalPage)
			currentPage = totalPage;

		int start = (currentPage - 1) * numPerPage + 1;
		int end = currentPage * numPerPage;

		List<CouponDTO> lists = dao.getList(start, end);

		// ����¡�� ���� ���� �����ֱ�
		String listUrl = cp + "/couponA/couponAdminList.action";

		String pageIndexList = myUtil.pageIndexList(currentPage, totalPage, listUrl);

		System.out.println(lists);
		
		req.setAttribute("listUrl", listUrl);
		req.setAttribute("lists", lists);
		req.setAttribute("pageIndexList", pageIndexList);
		req.setAttribute("pageNum", pageNum);

		return "admin/couponAdminList";
	}
	
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
		
        //�����߱����̺��� insert
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
	public String myCouponList(HttpServletRequest req,HttpServletResponse response,HttpSession session,CouponDTO coupondto) throws IOException{
		
		List<CouponDTO> lists = dao.getList();

		req.setAttribute("lists", lists);
		
		return "coupon/couponMyList";
	}
}