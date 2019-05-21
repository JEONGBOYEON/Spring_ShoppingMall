package com.codi.app;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.codi.dao.QuestionDAO;
import com.codi.dto.CommunityDTO;
import com.codi.dto.MemberDTO;
import com.codi.dto.ProductDTO;
import com.codi.dto.QuestionDTO;
import com.codi.dto.QreplyDTO;
import com.codi.util.MyUtil;

@Controller("questionController")
public class QuestionController {

	@Autowired
	@Qualifier("questionDAO")
	QuestionDAO dao;

	@Autowired
	MyUtil myUtil;// Bean ��ü ����

	//QnA�� ����Ʈ
	@RequestMapping(value = "/qna/questionMain.action", method = {RequestMethod.POST, RequestMethod.GET})
	public String questionMain(HttpServletRequest request,HttpSession session) throws IOException{

		//MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		String cp = request.getContextPath();
		String pageNum = request.getParameter("pageNum");
		//�亯�� ��ٸ��� �Խñ�
		String status=request.getParameter("status");
		
		//�ֽż�,�α�� �޾ƿͼ� selected�� ����
		String orderStd=request.getParameter("orderStd");

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

		List<QuestionDTO> lists=dao.getLists(start, end);
		
		//������ dto�� reviewCount �� reviewRate �߰�
		ListIterator<QuestionDTO> it = lists.listIterator();

		while(it.hasNext()){

			QuestionDTO vo = (QuestionDTO)it.next();
			
			//�ؽ��±� �߶� �����ֱ�
			String hashStr = vo.getqHashTag();
			ArrayList<String> hash=new ArrayList<String>();
			
			if(hashStr!=null) {
				String[] array = hashStr.split("#");
				
				for(String temp:array) {
					hash.add(temp);
				}
				
				hash.remove(0);//ù��°�� �����̶� ����
			
				vo.setqHash(hash);
				//System.out.println(vo.getqHash());
			
			}
			
		}
		

		// ����¡�� ���� ���� �����ֱ�
		String listUrl = cp + "/qna/questionMain.action";
		
		String order = request.getParameter("order");
		if (order != null) {
			order = URLDecoder.decode(order, "UTF-8");
		}else {
			order="qNum desc";
		}
		
		String pageIndexList=null;
		String notyet="";
		
		if(status==null) {
			lists = dao.getListOrder(start, end, order);
		}
		else {
			totalPage=myUtil.getPageCount(numPerPage, dao.countNotyet());
			lists = dao.getListNotyet(start, end, order);
			listUrl = cp + "/qna/questionMain.action";
			notyet="status=notyet";
		}
		
		pageIndexList = myUtil.listPageIndexList(currentPage, totalPage, listUrl, order);

		request.setAttribute("listUrl", listUrl);
		request.setAttribute("notyet", notyet);
		request.setAttribute("orderStd", orderStd);
		request.setAttribute("imagePath", "../upload/qna");
		request.setAttribute("memberPath", "../upload/profile");
		request.setAttribute("lists", lists);

		request.setAttribute("pageIndexList", pageIndexList);
		request.setAttribute("dataCount", dataCount);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("pageNum", pageNum);

		return "question/questionMain";
	}
	
	

	//�� �ۼ��ϱ�
	@RequestMapping(value = "/ques/questionCreated.action", method = {RequestMethod.POST, RequestMethod.GET})
	public String questionCreated(QuestionDTO dto,HttpServletRequest request) throws IOException{
		
		if(dto==null||dto.getMode()==null||dto.getMode().equals("")){
			
			request.setAttribute("mode", "created");
			
		}else {
			request.setAttribute("mode", "updated");
			request.setAttribute("dto",dto);
		}
		
		return "question/questionCreated";

		
	}

	@RequestMapping(value = "/ques/questionCreated_ok.action", method = {RequestMethod.POST, RequestMethod.GET})
	public String questionCreated_ok(QuestionDTO dto,MultipartHttpServletRequest request, HttpSession session) throws IOException{

		MemberDTO info = (MemberDTO)session.getAttribute("customInfo"); 
		String userId = info.getUserId();

		int qNum=dao.getMaxNum();

		dto.setqNum(qNum+1);
		dto.setUserId(userId);

		Date date = new Date();
		SimpleDateFormat today = new SimpleDateFormat("yyyymmdd24hhmmss");
		String time = today.format(date);

		String path = request.getSession().getServletContext().getRealPath("/upload/qna");

		MultipartFile file = request.getFile("qnaUpload");

		//������ �����Ѵٸ� ���ε� �ϱ�
		if(file!=null && file.getSize()>0) {

			try {

				String saveName = time + file.getOriginalFilename();

				File saveFile = new File(path,saveName); 
				file.transferTo(saveFile);

				dto.setOriginalName(saveName);
				dto.setSaveFileName(saveName);

			} catch (Exception e) {
				System.out.println(e.toString());
			}

		}
		else {
			dto.setOriginalName("");
			dto.setSaveFileName("");

		}

		dao.insertQuestion(dto);

		return "redirect:/qna/questionMain.action";
	}

	//Article �� �����ֱ�
	@RequestMapping(value = "/qna/questionAticle.action", method = {RequestMethod.POST, RequestMethod.GET})
	public String questionArticle(HttpServletRequest request,HttpSession session) throws IOException{

		int qNum = Integer.parseInt(request.getParameter("qNum"));
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		MemberDTO loginUserInfo = null;	
		
		//�α��� ����� 
		if(info!=null) {
			String loginUserId = info.getUserId();
			loginUserInfo = dao.getUserInfo(loginUserId);
		}
		
		//�ڵ� �Խñ� ��ȸ�� ����
		dao.updateHitCount(qNum);

		//�ڵ� �Խñ� detail ���� ��������
		QuestionDTO dto = dao.getReadOne(qNum);
		
		//�ؽ��±� �߶� �����ֱ�
		String hashStr = dto.getqHashTag();
		ArrayList<String> hash=new ArrayList<String>();
		
		if(hashStr!=null) {
			String[] array = hashStr.split("#");
			
			for(String temp:array) {
				hash.add(temp);
			}
			
			hash.remove(0);//ù��°�� �����̶� ����
		}
		/*
		System.out.println(hash.size());
		for(int i=0;i<hash.size();i++) {
			System.out.println(hash);
		}
		*/
		
		int cnt=0;
		//followüũ
		if(info!=null) {
			cnt=dao.followCheck(info.getUserId(),dto.getUserId());
		}
		
		request.setAttribute("cnt", cnt);
		request.setAttribute("dto", dto);
		request.setAttribute("hashArray", hash);
		request.setAttribute("imagePath", "../upload/qna");
		request.setAttribute("memberPath", "../upload/profile");
		request.setAttribute("loginUserInfo",loginUserInfo);

		return "question/questionArticle";
	}
	

	@RequestMapping(value = "/qna/follow.ajax", method = {RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
    public Map<Object, Object> follow(HttpServletRequest request,HttpSession session,String userid) throws IOException{
        
        int count=0;
        String str="";
        Map<Object, Object> map = new HashMap<Object, Object>();
 
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
        count =dao.followCheck(info.getUserId(),userid);
        
        if(count==0) {
    		dao.insertFollow(info.getUserId(), userid);
    		str="�ȷ��� ���";
        }
        else {
        	dao.deleteFollow(info.getUserId(), userid);
        	str="�ȷο�";
        }
        
        map.put("str", str);

        
        return map;
    }
	
	@RequestMapping(value = "/qna/updated.action", method = {RequestMethod.POST, RequestMethod.GET})
	public String updated(QuestionDTO dto,HttpServletRequest request) throws IOException{
		
		int qNum=Integer.parseInt(request.getParameter("qNum"));
		//����â�� ��
		if(dto==null||dto.getMode()==null||dto.getMode().equals("")){
		
			dto= dao.getReadOne(qNum);
			
			if(dto==null)
				return "read-error";
			
			//request.setAttribute("pageNum", pageNum);
			request.setAttribute("mode", "updated");
			request.setAttribute("dto",dto);
			
			return "question/questionCreated";
		}
		
		//������ ���� update
		dao.updateData(dto);
		
		//request.setAttribute("qNum", dto.getqNum());
		
		return "redirect:/qna/questionAticle.action?qNum="+dto.getqNum();
	}
	
	@RequestMapping(value = "/qna/deleted.action", method = {RequestMethod.POST, RequestMethod.GET})
	public String deleted(QuestionDTO dto,HttpServletRequest request) throws IOException{
		
		dao.deleteData(dto);
		
		return "redirect:/qna/questionMain.action";
	}


	//Comment
	
	//��� ����
	@RequestMapping(value = "/qna/replyCreated.action", method = {RequestMethod.GET, RequestMethod.POST}, produces="application/json; charset=utf8")
	@ResponseBody
	public ResponseEntity<Object>  replyCreated(QreplyDTO dto, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		String userId = info.getUserId();

		int maxNum = dao.getReplyMaxNum(dto.getqNum());
		//��۹�ȣ
		dto.setReplyNum(maxNum+1);
		//�ۼ���id
		dto.setUserId(userId);
		//��� �Է�
		dao.insertReplyData(dto);

		return replyLists(request,response,session);
	}

	//��� ����
	@RequestMapping(value = "/qna/replyDeleted.action", method = {RequestMethod.GET, RequestMethod.POST}, produces="application/json; charset=utf8")
	@ResponseBody
	public ResponseEntity<Object> replyDeleted(HttpServletRequest request,HttpServletResponse response, HttpSession session) throws Exception {

		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		int replyNum = Integer.parseInt(request.getParameter("replyNum"));
		dao.deleteReplyData(replyNum);
		return replyLists(request,response,session);

	}

	//��� ��ȸ
	@RequestMapping(value = "/qna/replyList.action", method = {RequestMethod.GET, RequestMethod.POST}, produces="application/json; charset=utf8")
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
		int qNum = Integer.parseInt(request.getParameter("qNum"));

		int currentPage = 1;
		if(pageNum!=null&&pageNum!=""){
			currentPage = Integer.parseInt(pageNum);
		}else{
			pageNum = "1";
		}
		totalReplyDataCount = dao.getReplyDataCount(qNum);

		if(totalReplyDataCount!=0){
			totalPage = myUtil.getPageCount(numPerPage, totalReplyDataCount);
		}

		//���� ����Ǿ� ���������� ���������
		if(currentPage>totalPage)
			currentPage = totalPage;

		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;

		List<QreplyDTO> replyLists = dao.getReplyListData(qNum, start, end);

		if(replyLists.size() > 0){

			for(int i=0; i<replyLists.size() ; i++){
				HashMap<String,Object> hm = new HashMap<String,Object>();
				hm.put("replyNum", replyLists.get(i).getReplyNum());
				hm.put("userId", replyLists.get(i).getUserId());
				hm.put("content", replyLists.get(i).getContent());
				hm.put("qNum", replyLists.get(i).getqNum());
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
	
	//QnA ����������
	@RequestMapping(value = "/myPage/questionMypage.action", method = {RequestMethod.POST, RequestMethod.GET})
	public String QnaMypage(QuestionDTO dto,HttpServletRequest request,HttpSession session) throws IOException{

		String cp = request.getContextPath();
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		MemberDTO loginUserInfo = null;	
		
		dto.setUserId(info.getUserId());
		String status = request.getParameter("status");
		String on=request.getParameter("on");
		
		//�α��� ����� 
		if(info!=null) {
			String loginUserId = info.getUserId();
			loginUserInfo = dao.getUserInfo(loginUserId);
		}
		int currentPage = 1;
		String pageNum = request.getParameter("pageNum");
		
		if (pageNum != null)
			currentPage = Integer.parseInt(pageNum);

		
		int dataCount = 0;
		
		if(status==null) {
			dataCount=dao.countOneUser(info.getUserId());
			
		}else {
			dataCount=dao.countOneUserReply(info.getUserId());
		}
		
		int numPerPage = 3;
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);

		if (currentPage > totalPage)
			currentPage = totalPage;

		int start = (currentPage - 1) * numPerPage + 1;
		int end = currentPage * numPerPage;
		
		List<QuestionDTO> lists=null;
		
		// ����¡�� ���� ���� �����ֱ�
		String listUrl ="";
		
		if(status==null) {
			lists=dao.getListsUser(start, end, info.getUserId());
			listUrl= cp + "/myPage/questionMypage.action?on=me";
			dataCount=dao.countOneUser(info.getUserId());
			
		}else {
			lists=dao.getListsUserReply(start, end, info.getUserId());
			listUrl= cp + "/myPage/questionMypage.action?status=reply&on=other";
			dataCount=dao.countOneUserReply(info.getUserId());
		}
		
		String pageIndexList=null;
		
		pageIndexList = myUtil.pageIndexList(currentPage, totalPage,listUrl);
		
		//���� �ȷ���� ���
		int follower = dao.follower(info.getUserId());
		//���� �ȷο��� ���
		int following = dao.following(info.getUserId());

		request.setAttribute("on", on);
		request.setAttribute("follower", follower);
		request.setAttribute("following", following);
		request.setAttribute("dto", dto);
		request.setAttribute("lists", lists);
		request.setAttribute("dataCount", dataCount);
		request.setAttribute("loginUserInfo", loginUserInfo);
		request.setAttribute("imagePath", "../upload/qna");
		request.setAttribute("memberPath", "../upload/profile");
		request.setAttribute("pageIndexList", pageIndexList);
		
		return "question/questionMypage";
	}
	
	
}