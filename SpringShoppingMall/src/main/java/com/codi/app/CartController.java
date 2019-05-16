package com.codi.app;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codi.dao.CartDAO;
import com.codi.dao.ProductDetailDAO;
import com.codi.dto.CartDTO;
import com.codi.dto.MemberDTO;
import com.codi.util.MyUtil;

@Controller("cartController")
public class CartController {
	
	@Autowired
	@Qualifier("cartDAO")//Bean ��ü ���� 
	CartDAO dao;
	
	@Autowired
	@Qualifier("productDetailDAO")//Bean ��ü ���� 
	ProductDetailDAO productDetailDAO;

	@Autowired
	MyUtil myUtil;//Bean ��ü ����
	
    //��ٱ��� ����Ʈ
	@RequestMapping(value = "/cart/cartList.action", method = {RequestMethod.GET,RequestMethod.POST})
	public String cartList(CartDTO dto, HttpServletRequest request, HttpServletResponse response) {
		String cp = request.getContextPath();
		HttpSession session=request.getSession();
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		String userId = info.getUserId();

		List<CartDTO> lists = dao.getReadData(userId);
		int totalItemCount = dao.getTotalItemCount(userId);
		int totalItemPrice = dao.getTotalItemPrice(userId);
		int totalItemCountYes = dao.getTotalItemCountYes(userId);
		
		//��ǰ�� ��ǰ�ɼ� �о����
		Iterator<CartDTO> it = lists.iterator();
		while(it.hasNext()){
			CartDTO vo = (CartDTO)it.next();
			List<String> colorList = productDetailDAO.getColorList(vo.getSuperProduct());
			List<String> sizeList = productDetailDAO.getProductSizeList(vo.getSuperProduct());
			vo.setColorList(colorList);
			vo.setSizeList(sizeList);
		}		
		// �̹������ϰ��
		String imagePath = cp + "/upload/list";
		
		request.setAttribute("imagePath", imagePath);
		request.setAttribute("lists", lists);
		request.setAttribute("totalItemCount", totalItemCount);
		request.setAttribute("totalItemPrice", totalItemPrice);
		request.setAttribute("totalItemCountYes", totalItemCountYes);
		return "cart/cartList";
	}
	
	//��ٱ��� ������ ���
	@RequestMapping(value = "/cart/cartAdd_ok.action", method = {RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public boolean cartAddItem(CartDTO dto, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session=request.getSession();
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		String userId = info.getUserId();
		if(userId==null) {
			return false;
		} else {
			dto.setUserId(userId);
		}
		dto.setProductId(dao.getProductId(dto.getProductName(), dto.getProductSize(), dto.getColor()));

		//��ٱ��ϳ� ���ϻ�ǰ ��Ͽ��� �˻�		
		if(dao.searchBeforeProductId(dto)!=0) {
			//���ϻ�ǰ ������ ��ٱ��� �����߰�
			dao.updateCartItemAmountAdd(dto);
		}else {
			//��ٱ��� �Է�
			dao.insertCartItem(dto);
		}
		return true; 
	}
	
	//��ٱ��� ������ ����
	@RequestMapping(value = "/cart/deleteCartItem.action", method = {RequestMethod.GET,RequestMethod.POST})
	public String deleteCartItem(String productId, HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		String userId = info.getUserId();

		//��ٱ��� ����
		dao.deleteCartItem(productId, userId);		
		return "redirect:/cart/cartList.action";
	}
	
	//��ٱ��� �ֹ������� ����
	@RequestMapping(value = "/cart/amendToOrderSelect.action", method = {RequestMethod.GET,RequestMethod.POST})
	public String amendToOrderSelect(String productId, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		String userId = info.getUserId();

		//��ٱ��� �ֹ�üũ Ȯ��
		String beforeOrderSelect = dao.checkOrderSelect(productId, userId);
		//��ٱ��� �ֹ�üũ ���� 
		if(beforeOrderSelect.equals("yes")) {
			dao.amendOrderSelect(productId, userId, "no");
		}else{
			dao.amendOrderSelect(productId, userId, "yes");
		}
		return "redirect:/cart/cartList.action";
	}
	
	//��ٱ��� �ɼ� ����
	@RequestMapping(value = "/cart/amendProductOption.action", method = {RequestMethod.GET,RequestMethod.POST})
	public String amendProductOption(CartDTO dto, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		String userId = info.getUserId();
		dto.setUserId(userId);
		
		//���� ������ ������
		String beforeProductId= dto.getProductId();
		//�ű� ������ �Է¿�
		dto.setProductId(dao.getProductId(dto.getProductName(), dto.getProductSize(), dto.getColor()));
		
		//��ٱ��ϳ� ���ϻ�ǰ ��Ͽ��� �˻�		
		if(dao.searchBeforeProductId(dto)==0) {
			//��ٱ��� �ɼ� ���� �� �Է�
			dao.deleteCartItem(beforeProductId, userId);
			dao.insertCartItem(dto);
		}else {
			//��ٱ��� ��������
			dao.updateCartItemAmount(dto);
		}
		return "redirect:/cart/cartList.action";
	}
	
	//�ٷ��ֹ��ϱ�
	@RequestMapping(value = "/cart/cartAdd_directOrder.action", method = {RequestMethod.GET,RequestMethod.POST})
	public String cartAdd_directOrder(CartDTO dto, HttpServletRequest request, HttpServletResponse response) {
		
		//��ٱ��� ��ǰ�߰�
		cartAddItem(dto, request, response);
		//�����̷�Ʈ
		return "redirect:/order/orderList.action";
	}
	
}
