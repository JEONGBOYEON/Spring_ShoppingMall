package com.codi.app;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codi.dao.CouponDAO;
import com.codi.dao.OrderDAO;
import com.codi.dto.AdminPaymentDTO;
import com.codi.dto.CouponDTO;
import com.codi.dto.DestinationDTO;
import com.codi.dto.MemberDTO;
import com.codi.dto.MyCouponDTO;
import com.codi.dto.OrderDTO;
import com.codi.dto.OrderListDTO;
import com.codi.dto.ProductDetailDTO;
import com.codi.dto.ReviewDTO;
import com.codi.util.MyUtil;

@Controller("orderController")
public class OrderController {
	
	@Autowired
	@Qualifier("orderDAO")
	OrderDAO dao;

	@Autowired
	@Qualifier("couponDAO")//Bean ��ü ���� 
	CouponDAO couponDAO;
	
	@Autowired
	MyUtil myUtil;
	
	@RequestMapping(value = "/order/orderList.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String list(OrderDTO orderDTO, OrderListDTO orderListDTO, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		//����� ����
		DestinationDTO destDto = dao.getOrderDest(info.getUserId());
		//��� ����� ����
		List<DestinationDTO> destAllList = dao.getAllDest(info.getUserId());
		//�ֹ� ����Ʈ
		List<OrderListDTO> orderList = dao.getOrderList(info.getUserId());
		//�ֹ� ����
		int totalOrderCount = dao.getOrderCount(info.getUserId());
		//��� ����Ʈ ���� ��������
		int memberPoint = dao.getMemberPoint(info.getUserId());
		
		//�� �ֹ� �հ� / �� �ֹ� ����
		int totalPrice=0;
		int totalAmount=0;
		Iterator<OrderListDTO> orderLists = orderList.iterator();
		while(orderLists.hasNext()){
			OrderListDTO dto = orderLists.next();
			totalPrice += dto.getPrice()*dto.getAmount();
			totalAmount += dto.getAmount();
		}
		
		
		//��ǥ���� �����ϱ�---------------------------------------------------------------------------
		List<MyCouponDTO> lists = couponDAO.couponGetList(info.getUserId());
		
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
	        	couponDAO.couponInsertM(dto.getCouponKey(),info.getUserId());
	        }
        }
		
		List<MyCouponDTO> couponLists = couponDAO.canUseCoupon(info.getUserId());
		
		String imagePath = "../upload/list";
		int deliveryFee = 2500;
		request.setAttribute("destDto", destDto);
		request.setAttribute("destAllList", destAllList);
		request.setAttribute("orderList", orderList);
		request.setAttribute("totalOrderCount", totalOrderCount);
		request.setAttribute("totalPrice", totalPrice);
		request.setAttribute("totalAmount", totalAmount);
		request.setAttribute("memberPoint", memberPoint);
		request.setAttribute("imagePath", imagePath);
		request.setAttribute("deliveryFee", deliveryFee);
		request.setAttribute("couponLists", couponLists);
				
		return "order/reception";
	}
	
	@RequestMapping(value = "/order/payReq.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String payReq(OrderDTO orderDTO, OrderListDTO orderListDTO, DestinationDTO destinationDTO,
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, HttpSession session) throws Exception {
	
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		String userName = request.getParameter("destName");
		
		//��� ����Ʈ
		int discount = Integer.parseInt(request.getParameter("discount"));
		
		//�ſ�ī�� / ������ ����
		String order_payment = request.getParameter("order_payment");
		
		//���� �ݾ�
		String totalOrderPrice = request.getParameter("totalPriceDelivery");
		
		//���� ����Ʈ
		String totalPoint = request.getParameter("totalPoint");
		
		//����� �̸���
		String userEmail = destinationDTO.getUserEmail();
		
		//����� �޴���ȭ
		String destPhone = destinationDTO.getDestPhone();
		destPhone = destPhone.substring(0,3) + destPhone.substring(4,8) + destPhone.substring(9);
		
		//����ID
		String storeID = "tdacomst7";
		
		//�ֹ� ����Ʈ
		String orderProdudct="";
		List<OrderListDTO> orderList = dao.getOrderList(info.getUserId());
		Iterator<OrderListDTO> orderLists = orderList.iterator();
		int orderSize = orderList.size();
		if(orderLists.hasNext()){
			OrderListDTO dto = orderLists.next();
			if(orderSize==1) {
				orderProdudct = dto.getProductName();
			}
			else {
				orderProdudct =  dto.getProductName() + "�� " + orderSize + "��";
			}
		}
		
		//���� �ð�
		long curr = System.currentTimeMillis();  // �Ǵ� System.nanoTime();
		String currentTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date(curr));
				
		//�ֹ���ȣ
		//���� �ֹ��� �ֹ��Ǽ�
		int todayOrderCount = dao.getMaxNum()+1;
		String  orderNum;
		if(todayOrderCount<10)
			orderNum = currentTime + "00" + todayOrderCount;
		else if(todayOrderCount>=10 && todayOrderCount<100)
			orderNum = currentTime + "0" + todayOrderCount;
		else
			orderNum = currentTime + todayOrderCount;
		
		//�ؽõ�����
		Random rnd =new Random();

		StringBuffer hashData =new StringBuffer();
		
		hashData.append(new SimpleDateFormat("yyyyMMdd").format(new Date(curr)));
		for(int i=0;i<12;i++){
		    if(rnd.nextBoolean()){
		    	hashData.append((char)((int)(rnd.nextInt(26))+97));
		    }else{
		    	hashData.append((rnd.nextInt(10)));
		    }
		}
		
		//����� �ּ�
		String destAddr = destinationDTO.getDestAddr();
		request.setAttribute("orderNum", orderNum);
		request.setAttribute("userName", URLEncoder.encode(userName,"UTF-8"));
		request.setAttribute("orderProdudct", orderProdudct);
		request.setAttribute("totalOrderPrice", totalOrderPrice);
		request.setAttribute("userEmail", userEmail);
		request.setAttribute("destPhone", destPhone);
		request.setAttribute("storeID", storeID);		
		request.setAttribute("currentTime", currentTime);
		request.setAttribute("hashData", hashData);
		request.setAttribute("destAddr", destAddr);
		request.setAttribute("destZip", destinationDTO.getZip());
		request.setAttribute("destAddr1", URLEncoder.encode(destinationDTO.getAddr1(),"UTF-8"));
		request.setAttribute("destAddr2", URLEncoder.encode(destinationDTO.getAddr2(),"UTF-8"));
		request.setAttribute("destAddrKey", destinationDTO.getAddrKey());
		request.setAttribute("totalPoint", totalPoint);
		request.setAttribute("discount", discount);
		
		//������� ����
		couponDAO.usedCounpon(Integer.parseInt(request.getParameter("useCouponKey")),info.getUserId());
		
		if(order_payment.equals("without_bankbook")) {
			
			redirectAttributes.addAttribute("mode","without_bankbook");
			redirectAttributes.addAttribute("userName", userName);
			redirectAttributes.addAttribute("zip", destinationDTO.getZip());
			redirectAttributes.addAttribute("addr1",destinationDTO.getAddr1());
			redirectAttributes.addAttribute("addr2",destinationDTO.getAddr2());
			redirectAttributes.addAttribute("addrKey",destinationDTO.getAddrKey());
			redirectAttributes.addAttribute("orderNum",orderNum);
			redirectAttributes.addAttribute("eMail",userEmail);
			redirectAttributes.addAttribute("destPhone",destPhone);
			redirectAttributes.addAttribute("totalPrice",totalOrderPrice);
			redirectAttributes.addAttribute("totalPoint",totalPoint);
			redirectAttributes.addAttribute("discount",discount);

			return "redirect:/order/orderComplete.action";
		}
		
		
		return "order/payreq";
		
	}
	
	@RequestMapping(value = "/order/backOrder.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String backOrder(OrderDTO dto, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		dto.setAddr1(URLEncoder.encode(dto.getAddr1(),"UTF-8"));
		dto.setAddr2(URLEncoder.encode(dto.getAddr2(),"UTF-8"));
		dto.setUserName(URLEncoder.encode(dto.getUserName(),"UTF-8"));
		request.setAttribute("dto",dto);
		return "order/backOrder";
	}
	
	@RequestMapping(value = "/order/orderComplete.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String orderComplate(OrderDTO orderDTO, OrderListDTO orderListDTO, DestinationDTO destinationDTO, ReviewDTO reviewDTO,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		String mode = request.getParameter("mode");
		
		//����
		String minus = request.getParameter("discountAll");
		int discount=0;
		if(minus!=null && !minus.equals("")) {
			discount = Integer.parseInt(request.getParameter("discountAll"));
		}
		
		//�����
		orderDTO.setAddr1(URLDecoder.decode(orderDTO.getAddr1(),"UTF-8"));
		orderDTO.setAddr2(URLDecoder.decode(orderDTO.getAddr2(),"UTF-8"));

		String userName = URLDecoder.decode(request.getParameter("userName"),"UTF-8");
		int totalPrice = Integer.parseInt(request.getParameter("totalPrice"));
		float totalPoint = Float.parseFloat(request.getParameter("totalPoint"));
		
		//�����
		orderDTO.setUserId(info.getUserId());
		reviewDTO.setUserId(info.getUserId());
				
		//�ֹ� ����Ʈ
		List<OrderListDTO> orderList = dao.getOrderList(info.getUserId());
		Iterator<OrderListDTO> orderLists = orderList.iterator();
		int totalAmount=0;		
		while(orderLists.hasNext()){
			
			//�ֹ����� �Է�
			OrderListDTO dto = orderLists.next();
			
			orderDTO.setProductId(dto.getProductId());
			orderDTO.setAmount(dto.getAmount());
			orderDTO.setPrice(dto.getPrice());
			
			if(mode=="without_bankbook" || mode.equals("without_bankbook")) {
				orderDTO.setPayment("no");
			}
			else {
				orderDTO.setPayment("yes");
				
				//���䵥���� �Է�
				reviewDTO.setProductId(dto.getProductId());
				dao.insertReview(reviewDTO);
			}
			
			dao.insertOrderDataProduct(orderDTO);
			dao.insertOrderPayment(orderDTO.getOrderNum(),info.getUserId(),totalPrice,discount);
			dao.updateProductAcount(dto.getAmount(), dto.getSuperProduct());
			
			//��ٱ��� ������ ����
			dao.deleteCartProduct(info.getUserId(), dto.getProductId());
			
			//�� ���� �� ����
			totalAmount += dto.getAmount();
		}
		
		//�⺻ ����
		String orderDate ="";
		String orderDest="";
		
		List<OrderDTO> orderCompleteList = dao.getCompleteOrder(info.getUserId(),orderDTO.getOrderNum());
		Iterator<OrderDTO> orderCompleteLists = orderCompleteList.iterator();
		if(orderCompleteLists.hasNext()){
			OrderDTO dto = orderCompleteLists.next();
			orderDate = dto.getOrderDate();
			orderDest = "[" + dto.getZip() + "]" + dto.getAddr1() + " " + dto.getAddr2();
		}
		
		request.setAttribute("orderList", orderList);
		request.setAttribute("userName", userName);
		request.setAttribute("orderDest", orderDest);
		request.setAttribute("orderCompleteList", orderCompleteList);
		request.setAttribute("orderDate", orderDate);
		request.setAttribute("totalPrice", totalPrice);
		request.setAttribute("totalAmount", totalAmount);
		request.setAttribute("discount", discount);
		request.setAttribute("imagePath", "../upload/list");
		
		//����� ����Ʈ ����
		dao.updateMemberPointUse(info.getUserId(), discount);
		if(mode.equals("without_bankbook")) {
			return "order/without_bankbook";
		}
		
		//����� ����Ʈ ����
		dao.updateMemberPoint(info.getUserId(),(int)totalPoint);
		
		int gradePoint = dao.gradePoint(info.getUserId());
		String userGrade = "SILVER";
		if(gradePoint<500000) 
			userGrade = "SILVER";
		else if (gradePoint>=500000 && gradePoint<1000000) {
			userGrade = "GOLD";
		}
		else if(gradePoint>=1000000)
			userGrade = "VIP";
			
		dao.updateGrade(info.getUserId(), userGrade);
		return "order/orderComplete";
		
	}
	
	@RequestMapping(value = "/order/myOrderLists.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String myOrderLists(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		String period = request.getParameter("period");
		
		if(period==null || period.equals("")) {
			period = "3month";
		}
		//����� �ֹ�����Ʈ �Ⱓ�� ��������		
		
		List<OrderDTO> userOrderlist;
		int num=3;
		String searchPeriod = "month";
		if(period=="week" || period.equals("week")) {
			num=7; searchPeriod="day";
			
		}else if(period=="month" || period.equals("month")) {
			num=1; searchPeriod="month";
			
		}else if(period=="3month" || period.equals("3month")) {
			num=3; searchPeriod="month";
			
		}else if(period=="6month" || period.equals("6month")) {
			num=6; searchPeriod="month";
			
		}else {
			num=1; searchPeriod="year";
			
		}
		
		//����¡
		String cp = request.getContextPath();
		
		String pageNum = request.getParameter("pageNum");
		
		int currentPage = 1;
		
		if(pageNum!=null)
			currentPage = Integer.parseInt(pageNum);
		
		int dataCount = dao.getNumUserOrderLists(info.getUserId(), num, searchPeriod);
		int numPerPage = 7;
		
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);
		
		if(currentPage>totalPage)
			currentPage = totalPage;
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		userOrderlist = dao.getUserOrderLists(info.getUserId(), num, searchPeriod, start, end);
		List<String> userOrderNum = dao.getUserOrderNum(info.getUserId(), num, searchPeriod, start, end);
		
		List<String> orderDateYMD = new ArrayList<String>();
		String date;
		Iterator<String> orderDate = userOrderNum.iterator();
		while(orderDate.hasNext()) {
			date = orderDate.next();
			orderDateYMD.add(date.substring(0,4) + "-" + date.substring(4,6) + "-" + date.substring(6,8));
		}
		
		String listUrl = cp + "/order/myOrderLists.action";
		
		String pageIndexList = myUtil.myOrderPageIndexList(currentPage, totalPage, listUrl,period);
		
		request.setAttribute("userOrderlist", userOrderlist);
		request.setAttribute("userOrderNum", userOrderNum);
		request.setAttribute("pageIndexList", pageIndexList);
		request.setAttribute("orderDateYMD", orderDateYMD);
		request.setAttribute("period", period);
		request.setAttribute("imagePath", "../upload/list");
		
		return "order/myOrderList";
	}
	
}
