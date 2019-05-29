package com.codi.app;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codi.dao.CodiDetailDAO;
import com.codi.dao.CommunityDAO;
import com.codi.dao.ProductDetailDAO;
import com.codi.dto.CartDTO;
import com.codi.dto.CodiHashTagDTO;
import com.codi.dto.CommunityDTO;
import com.codi.dto.MemberDTO;
import com.codi.dto.ProductDetailDTO;
import com.codi.dto.ReplyDTO;
import com.codi.util.MyUtil;

@Controller
public class CodiDetailController {

	@Autowired
	@Qualifier("productDetailDAO")
	ProductDetailDAO productDetailDAO;
	
	@Autowired
	@Qualifier("codiDetailDAO")
	CodiDetailDAO dao;
	
	@Autowired
	MyUtil myUtil;
	
	@RequestMapping(value = "pr/codiDetailList.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String detailList(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		int iNum = Integer.parseInt(request.getParameter("iNum"));
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		MemberDTO loginUserInfo = null;	
		List<String> good = null;
		int follow = 0;
		
		//�ڵ� �Խñ� ��ȸ�� ����
		dao.updateHitCount(iNum);
		
		//�ڵ� �Խñ� detail ���� ��������
		CommunityDTO dto = dao.getReadCodiData(iNum);
		
		//�α��� ����� 
		if(info!=null) {
			String loginUserId = info.getUserId();
			loginUserInfo = dao.getUserInfo(loginUserId);
			dto.setHeartCount(dao.getMyCodiHeart(loginUserId, iNum));
			good = dao.storeHeartList(info.getUserId());
			follow = dao.followCheck(loginUserId,dto.getUserId());
		}
		
		//�Խù� �ۼ��� ������ ����
		MemberDTO userInfo = dao.getUserInfo(dto.getUserId());
		
		//�ڵ� ���ƿ� ���� ī��Ʈ
		int codiHeartCount = dao.getCodiHeartCount(iNum);
		
		//�����ۼ��� ����Ʈ
		List<CommunityDTO> codiLists = dao.getReadCodiData(dto.getUserId(), iNum);
		
		//�����ۼ��� ����
		int codiListsDataCount = dao.getReadCodiDataCount(dto.getUserId(), iNum);

		//�ڵ� ���� ��ǰid
		String usedProductId = dto.getProductId();
		String[] arrProductId = usedProductId.split(",");
		List<ProductDetailDTO> usedProductLists = new ArrayList<ProductDetailDTO>();
		
		for(String str: arrProductId) {
			ProductDetailDTO vo = null;
			if(str!="" && !str.equals(null)) {
				vo = dao.getCodiProductItem(str);
				usedProductLists.add(vo);
				
			}
		}
		
		request.setAttribute("dto", dto);
		request.setAttribute("good", good);
		request.setAttribute("follow", follow);
		request.setAttribute("userInfo",userInfo);
		request.setAttribute("loginUserInfo",loginUserInfo);
		request.setAttribute("codiLists", codiLists);
		request.setAttribute("codiListsDataCount", codiListsDataCount);
		request.setAttribute("usedProductLists", usedProductLists);
		request.setAttribute("codiHeartCount",codiHeartCount);
		
		return "codiDetail/codiProductDetail";
	}
	
	//��� ����
	@RequestMapping(value = "pr/replyCreated.action", method = {RequestMethod.GET, RequestMethod.POST}, produces="application/json; charset=utf8")
	@ResponseBody
	public ResponseEntity<Object>  replyCreated(ReplyDTO dto, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		String userId = info.getUserId();

		int maxNum = dao.getReplyMaxNum(dto.getiNum());
		//��۹�ȣ
		dto.setReplyNum(maxNum+1);
		//�ۼ���id
		dto.setUserId(userId);
		//��� �Է�
		dao.insertReplyData(dto);
		
		return replyLists(request,response,session);
	}
	
	//��� ����
	@RequestMapping(value = "pr/replyIdCheck.action", method = {RequestMethod.GET, RequestMethod.POST}, produces="application/json; charset=utf8")
	@ResponseBody
	public boolean replyIdCheck(HttpServletRequest request,HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		int replyNum = Integer.parseInt(request.getParameter("replyNum"));
		
		if(info==null)
			return false;
		
		if(dao.deleteReplyDataCheck(replyNum, info.getUserId())>0) {
			return true;
		}
		return false;
	}
	
	//��� ���� ���̵� üũ
	@RequestMapping(value = "pr/replyDeleted.action", method = {RequestMethod.GET, RequestMethod.POST}, produces="application/json; charset=utf8")
	@ResponseBody
	public ResponseEntity<Object> replyDeleted(HttpServletRequest request,HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		int replyNum = Integer.parseInt(request.getParameter("replyNum"));
		dao.deleteReplyData(replyNum);
		return replyLists(request,response,session);

	}
	
	//��� ��ȸ
	@RequestMapping(value = "pr/replyList.action", method = {RequestMethod.GET, RequestMethod.POST}, produces="application/json; charset=utf8")
	@ResponseBody
	public ResponseEntity<Object> replyLists(HttpServletRequest request ,HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		HttpHeaders responseHeaders = new HttpHeaders();
		ArrayList<HashMap<String,Object>> hmlist = new ArrayList<HashMap<String,Object>>();
		
		//��� ������
		int numPerPage = 5;
		int totalPage = 0;
		int totalReplyDataCount =0;
		String pageNum = request.getParameter("pageNum");//���������� �Ѿ��
		int iNum = Integer.parseInt(request.getParameter("iNum"));
		
		int currentPage = 1;
		if(pageNum!=null&&pageNum!=""){
			currentPage = Integer.parseInt(pageNum);
		}else{
			pageNum = "1";
		}
		totalReplyDataCount = dao.getReplyDataCount(iNum);
		
		if(totalReplyDataCount!=0){
			totalPage = myUtil.getPageCount(numPerPage, totalReplyDataCount);
		}

		//���� ����Ǿ� ���������� ���������
		if(currentPage>totalPage)
			currentPage = totalPage;

		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		List<ReplyDTO> replyLists = dao.getReplyListData(iNum, start, end);
        
		if(replyLists.size() > 0){
			
			for(int i=0; i<replyLists.size() ; i++){
				HashMap<String,Object> hm = new HashMap<String,Object>();
                hm.put("replyNum", replyLists.get(i).getReplyNum());
                hm.put("userId", replyLists.get(i).getUserId());
                hm.put("content", replyLists.get(i).getContent());
                hm.put("iNum", replyLists.get(i).getiNum());
                hm.put("replyDate", replyLists.get(i).getReplyDate());
                hm.put("mImage", replyLists.get(i).getmImage());
                hmlist.add(hm);
            }
		}
		
		//����¡ó���� �ڹٽ�ũ��Ʈ�� ����
		String pageIndexList = myUtil.pageIndexList(currentPage, totalPage);

		//����¡ ����
		HashMap<String,Object> pageHashMap = new HashMap<String,Object>();
		pageHashMap.put("pageIndexList", pageIndexList);
		pageHashMap.put("totalReplyDataCount", totalReplyDataCount);
		pageHashMap.put("pageNum", pageNum);
		hmlist.add(pageHashMap);
		
		JSONArray json = new JSONArray(hmlist); 
		/*
		for(int i=0; i<json.length(); i++) {
			System.out.println(json.get(i));
		}
		*/
        return new ResponseEntity<Object>(json.toString(), responseHeaders, HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "pr/codiHashTagList.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String codiHashTaglList(String iHashtag, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		//System.out.println(iHashtag);
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		List<String> good = null;
		List<String> hashTagLists = dao.getPopularHashTagLists();
				
		String pageNum = request.getParameter("pageNum");
		
		//�˻� �ؽ��±� ���� ��� ���� ���� ���� �ؽ��±� ���
		if(iHashtag==null) {
			iHashtag= hashTagLists.get(0);
		}else {
			//������
			iHashtag = URLDecoder.decode(iHashtag,"UTF-8");			
		}
		
		//����� �α׿½� ���ƿ� ����Ʈ ��������
		if(info!=null) {
			good = dao.codiHeartList(info.getUserId());
		}
		
		//�ڵ� �ؽ��±� ����Ʈ
		int numPerPage = 8;
		int totalPage = 0;
		int totalCodiHashTagDataCount =0;
		
		int currentPage = 1;
		if(pageNum!=null&&pageNum!=""){
			currentPage = Integer.parseInt(pageNum);
		}else{
			pageNum = "1";
		}
		
		//���� �ؽ��±� �ڵ� ī��Ʈ
		totalCodiHashTagDataCount = dao.getCodiHashTagDataCount(iHashtag);
		
		if(totalCodiHashTagDataCount!=0){
			totalPage = myUtil.getPageCount(numPerPage, totalCodiHashTagDataCount);
		}

		//���� ����Ǿ� ���������� ���������
		if(currentPage>totalPage)
			currentPage = totalPage;

		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		//���� �ؽ��±� �ڵ� ����Ʈ
		List<CodiHashTagDTO> codiHashTagLists = dao.getCodiHashTagLists(iHashtag, start, end);
		
		String cp = session.getServletContext().getContextPath();
		
		//������
		iHashtag = URLEncoder.encode(iHashtag,"UTF-8");
		
		String listUrl = cp +"/pr/codiHashTagList.action?iHashtag="+iHashtag;
		
		//����¡ó���� �ڹٽ�ũ��Ʈ�� ����
		String pageIndexList = myUtil.pageIndexList(currentPage, totalPage, listUrl);
				
		//�ڵ� ���� ��ǰ ��� ���� iterator���
		Iterator<CodiHashTagDTO> it = codiHashTagLists.iterator();
		
		while(it.hasNext()){
			
			CodiHashTagDTO vo = (CodiHashTagDTO)it.next();
			//�ڵ� ���� ��ǰid
			String usedProductId = vo.getProductId();
			String[] arrProductId = usedProductId.split(",");
			List<ProductDetailDTO> usedProductLists = new ArrayList<ProductDetailDTO>();
			
			//���� ��ǰ ����� 3���� ����
			for(int i=0; i<3; i++) {
				ProductDetailDTO usedItemDTO = null;
				String str = arrProductId[i];
				if(str!="" && !str.equals(null)) {
					usedItemDTO = dao.getCodiProductItem(str);
					usedProductLists.add(usedItemDTO);	
				}
			}
			vo.setItemLists(usedProductLists);
		}	
		
		request.setAttribute("good", good);
		request.setAttribute("hashTagLists",hashTagLists);
		request.setAttribute("codiHashTagLists", codiHashTagLists);
		request.setAttribute("pageIndexList", pageIndexList);
		request.setAttribute("pageNum", pageNum);
		request.setAttribute("totalCodiHashTagDataCount", totalCodiHashTagDataCount);
		return "codiDetail/codiHashtagList";
	}
		
}
