package com.codi.app;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codi.dao.DestinationDAO;
import com.codi.dto.DestinationDTO;
import com.codi.dto.MemberDTO;
import com.codi.util.MyUtil;

@Controller("destinationController")
public class DestinationController {
	
	@Autowired
	@Qualifier("destinationDAO")//Bean ��ü ���� 
	DestinationDAO dao;
	
	@Autowired()
	MyUtil myUtil;//Bean ��ü ����
	
	@RequestMapping(value = "/destlist.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String list(DestinationDTO dto, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		int dataCount = dao.getDataCount(info.getUserId());
		List<DestinationDTO> lists = dao.getList(info.getUserId());
		
		Iterator<DestinationDTO> it = lists.iterator();

		int i = 0;
		while(it.hasNext()){
			dto = it.next();
			dto.setDestNickname2(URLDecoder.decode(dto.getDestNickname(),"UTF-8"));
			i++;
		}
		
		Iterator<DestinationDTO> it2 = lists.iterator();
		
		i=0;
		while(it2.hasNext()){
			dto = it2.next();
			dto.setDestNickname(URLEncoder.encode(dto.getDestNickname(),"UTF-8")); 
			i++;
		}
		
		request.setAttribute("lists", lists);
		request.setAttribute("dataCount", dataCount);
		request.setAttribute("message", request.getParameter("message"));
		
		return "destination/destinationList";
	}
	
	@RequestMapping(value = "/destwrited.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String writed(DestinationDTO dto, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, HttpSession session) throws Exception {

		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		if(dto.getDestNickname()!=null){
			dto.setDestNickname(URLDecoder.decode(dto.getDestNickname(),"UTF-8"));
		}

		int totalDataCount = dao.getDataCount(info.getUserId());
		
		if(totalDataCount==5){
			
			redirectAttributes.addAttribute("message","������� �ִ� 5�������� ��� �����մϴ�.");
			
			return "redirect:/destlist.action";
		}
		
		List<DestinationDTO> lists = dao.selectDestNickname(info.getUserId(), " ");
		Iterator<DestinationDTO> it = lists.iterator();
		String[] destNicknameList = new String[lists.size()];
		
		int i = 0;
		while(it.hasNext()){
			dto = it.next();
			destNicknameList[i] = dto.getDestNickname();
			i++;
		}
		
		request.setAttribute("dto", dto);
		request.setAttribute("destNicknameList", destNicknameList);
		request.setAttribute("totalDataCount", totalDataCount);
		
		return "destination/destinationWrited";
		
	}
	
	@RequestMapping(value = "/destwrited_ok.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String writed_ok(DestinationDTO dto, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		dto.setUserId(info.getUserId());
		dto.setAddrKey("no");	
		
		dao.insertData(dto);
		
		return "redirect:/destlist.action";
		
	}
	
	@RequestMapping(value = "/changeAddrkey_ok.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String changeAddrkey_ok(DestinationDTO dto, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		String userId = info.getUserId();
		
		dao.changeAddrkeyNo(userId);
		dao.changeAddrkeyYes(userId, dto.getDestNickname());
		
		return "redirect:/destlist.action";
		
	}
	
		
	@RequestMapping(value = "/destupdated.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String updated(DestinationDTO dto, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
	
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		DestinationDTO updateDTO = (DestinationDTO)dao.getReadData(info.getUserId(), dto.getDestNickname());

		List<DestinationDTO> lists = dao.selectDestNickname(info.getUserId(), dto.getDestNickname());
		Iterator<DestinationDTO> it = lists.iterator();
		String[] destNicknameList = new String[lists.size()];
		
		int i = 0;
		while(it.hasNext()){
			DestinationDTO destNicknameListDto = it.next();
			destNicknameList[i] = destNicknameListDto.getDestNickname();
			i++;
		}
		
		request.setAttribute("dto", updateDTO);
		request.setAttribute("destNicknameList", destNicknameList);
		request.setAttribute("ex_destNickname", dto.getDestNickname());
		
		return "destination/destinationUpdated";
	}
	
	@RequestMapping(value = "/destupdated_ok.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String updated_ok(DestinationDTO dto, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		dto.setEx_destNickname(request.getParameter("ex_destNickname"));
		dto.setUserId(info.getUserId());		
		dao.updateData(dto);
		
		return "redirect:/destlist.action";
		
	}
	
	@RequestMapping(value = "/destdeleted.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String destdeleted(DestinationDTO dto, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		dao.deleteData(info.getUserId(), dto.getDestNickname());
		
		return "redirect:/destlist.action";
		
	}

}
