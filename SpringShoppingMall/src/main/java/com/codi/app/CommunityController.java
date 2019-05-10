package com.codi.app;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codi.dao.CommunityDAO;
import com.codi.dto.CommunityDTO;
import com.codi.dto.MemberDTO;
import com.codi.dto.ProductDTO;
import com.codi.util.MyUtil;


@Controller
public class CommunityController {
	
	@Autowired
	@Qualifier("communityDAO")
	CommunityDAO dao;
	
	@Autowired
	MyUtil myUtil;
	
	@RequestMapping(value = "pr/commuList.action", method = RequestMethod.GET)
	public String commuMain(HttpServletRequest req,HttpSession session) {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo"); 

		List<String> good = null;

		if(info!=null) {
			good = dao.myCodiHeartList(info.getUserId());
		}
		
		String cp = req.getContextPath();

		String pageNum = req.getParameter("pageNum");

		int currentPage = 1;

		if (pageNum != null)
			currentPage = Integer.parseInt(pageNum);

		int dataCount = dao.getDataCount();

		int numPerPage = 8;
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);

		if (currentPage > totalPage)
			currentPage = totalPage;

		int start = (currentPage - 1) * numPerPage + 1;
		int end = currentPage * numPerPage;

		List<CommunityDTO> lists = dao.getLists(start, end);		
		
		ListIterator<CommunityDTO> it = lists.listIterator();
		
		while(it.hasNext()){
			CommunityDTO vo = (CommunityDTO)it.next();
			
			int heartCount =  dao.heartCount(vo.getiNum());
			
			vo.setHeartCount(heartCount);
		}
		
		//������ dto�� reviewCount �� reviewRate �߰�
		

		// ����¡�� ���� ���� �����ֱ�
		String listUrl = cp + "/pr/commuList.action";

		String pageIndexList = myUtil.pageIndexList(currentPage, totalPage, listUrl);

		req.setAttribute("good", good);
		req.setAttribute("listUrl", listUrl);
		req.setAttribute("lists", lists);
		req.setAttribute("pageIndexList", pageIndexList);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("pageNum", pageNum);

		System.out.println(lists);
		
		return "/community/commuList";
	}
	
	@RequestMapping(value = "/codiGood.action", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Map<Object, Object> good(@RequestBody int iNum, HttpSession session) {

		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");

		// if storeHeart���̺��� userId�� superProduct�� ������ ������ �Է� : ���̺��� �Է��ϰ� cnt = 0

		// ���� Ŭ���� ��ǰ�� ���� �α����� ���̵� ���ƿ� ���� ��ǰ�̸� 1 �ƴϸ� 0
		int result = dao.myCodiHeart(iNum, info.getUserId());
		int temp = 0;
		int count = 0;

		if (result == 0) {
			dao.insertHeart(iNum, info.getUserId());
			count = dao.heartCount(iNum);
			temp = 0;
		} else {
			dao.deleteHeart(iNum, info.getUserId());
			count = dao.heartCount(iNum);
			temp = 1;
		}

		Map<Object, Object> map = new HashMap<Object, Object>();

		map.put("temp", temp);
		map.put("count", count);

		return map;
	}
	
	@RequestMapping(value = "pr/commuMain1.action", method = { RequestMethod.GET, RequestMethod.POST })
	public String commuHome(HttpServletRequest request,HttpSession session) {
		
		return "community/commuHome";
		
	}
}
