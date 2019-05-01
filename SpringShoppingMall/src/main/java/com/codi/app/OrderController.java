package com.codi.app;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codi.dao.OrderDAO;
import com.codi.dto.DestinationDTO;
import com.codi.dto.OrderDTO;
import com.codi.dto.OrderListDTO;
import com.codi.dto.ReviewDTO;
import com.codi.util.MyUtil;

@Controller("orderController")
public class OrderController {
	
	@Autowired
	@Qualifier("orderDAO")
	OrderDAO dao;
	
	@Autowired
	MyUtil myUtil;
	
	@RequestMapping(value = "/orderList.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String list(OrderDTO orderDTO, OrderListDTO orderListDTO, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//����� ����
		DestinationDTO destDto = dao.getOrderDest("aaa");
		//��� ����� ����
		List<DestinationDTO> destAllList = dao.getAllDest("aaa");
		//�ֹ� ����Ʈ
		List<OrderListDTO> orderList = dao.getOrderList("aaa");
		//�ֹ� ����
		int totalOrderCount = dao.getOrderCount("aaa");
		//��� ����Ʈ ���� ��������
		int memberPoint = dao.getMemberPoint("aaa");
		
		//�� �ֹ� �հ� / �� �ֹ� ����
		int totalPrice=0;
		int totalAmount=0;
		Iterator<OrderListDTO> orderLists = orderList.iterator();
		while(orderLists.hasNext()){
			OrderListDTO dto = orderLists.next();
			totalPrice += dto.getPrice()*dto.getAmount();
			totalAmount += dto.getAmount();
		}
		
		/*
		//��ǥ���� �����ϱ�---------------------------------------------------------------------------
		List<MyCouponDTO> lists = dao2.couponGetLists(info.getUserId());
		


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
	        	dao2.couponInsertM(dto.getCouponKey(),info.getUserId());
	        }
        }
		
		//��� ������ ���� ���� ��������
		List<CouponDTO> couponList = dao.getUserCoupon(info.getUserId(), totalPrice);
		*/
		
		String imagePath = "./upload/list";
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
		request.setAttribute("imagePath", "./upload/list");
				
		return "order/reception";
	}
	
	@RequestMapping(value = "/payReq.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String payReq(OrderDTO orderDTO, OrderListDTO orderListDTO, DestinationDTO destinationDTO,
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws Exception {
	
		String userName = request.getParameter("destName");
		
		//�ſ�ī�� / ������ ����
		String order_payment = request.getParameter("order_payment");
		
		//���� �ݾ�
		String totalOrderPrice = request.getParameter("totalPriceDelivery");
		
		//���� ����Ʈ
		String totalPoint = request.getParameter("totalPoint");
		
		//����� �̸���
		String userEmail = destinationDTO.getUserEmail();
		
		//����ID
		String storeID = "tdacomst7";
		
		//�ֹ� ����Ʈ
		String orderProdudct="";
		List<OrderListDTO> orderList = dao.getOrderList("aaa");
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
		request.setAttribute("userName", userName);
		request.setAttribute("orderProdudct", orderProdudct);
		request.setAttribute("totalOrderPrice", totalOrderPrice);
		request.setAttribute("userEmail", userEmail);
		request.setAttribute("storeID", storeID);
		request.setAttribute("currentTime", currentTime);
		request.setAttribute("hashData", hashData);
		request.setAttribute("destAddr", destAddr);
		request.setAttribute("destZip", destinationDTO.getZip());
		request.setAttribute("destAddr1", destinationDTO.getAddr1());
		request.setAttribute("destAddr2", destinationDTO.getAddr2());
		request.setAttribute("destAddrKey", destinationDTO.getAddrKey());
		request.setAttribute("totalPoint", totalPoint);
		
		if(order_payment.equals("without_bankbook")) {
			
			redirectAttributes.addAttribute("mode","without_bankbook");
			redirectAttributes.addAttribute("userName", userName);
			redirectAttributes.addAttribute("destZip", destinationDTO.getZip());
			redirectAttributes.addAttribute("destAddr1",destinationDTO.getAddr1());
			redirectAttributes.addAttribute("destAddr2",destinationDTO.getAddr2());
			redirectAttributes.addAttribute("destAddrKey",destinationDTO.getAddrKey());
			redirectAttributes.addAttribute("orderNum",orderNum);
			redirectAttributes.addAttribute("userEmail",userEmail);
			redirectAttributes.addAttribute("totalOrderPrice",totalOrderPrice);

			return "redirect:/orderComplete.action";
		}
		
		
		return "order/payreq";
		
	}
	
	@RequestMapping(value = "/without_bankbook_paymentYes.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String without_bankbook_paymentYes(OrderDTO orderDTO, OrderListDTO orderListDTO, DestinationDTO destinationDTO, ReviewDTO reviewDTO,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		
		return "redirect:/list.action";
	}
	
	@RequestMapping(value = "/orderComplete.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String orderComplate(OrderDTO orderDTO, OrderListDTO orderListDTO, DestinationDTO destinationDTO, ReviewDTO reviewDTO,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String mode = request.getParameter("mode");
		
		//�����
		orderDTO.setZip(request.getParameter("destZip"));
		orderDTO.setAddr1(request.getParameter("destAddr1"));
		orderDTO.setAddr2(request.getParameter("destAddr2"));
		orderDTO.setAddrKey(request.getParameter("destAddrKey"));
		
		
		String orderNum,userEmail,userName="";
		int totalPrice = 0;
		//������ �Ա��� ���
		if(mode=="without_bankbook" && mode.equals("without_bankbook")) {
			orderNum = (request.getParameter("orderNum"));
			userEmail = request.getParameter("userEmail");
			userName = request.getParameter("userName");
			totalPrice = Integer.parseInt(request.getParameter("totalOrderPrice"));
			
		} else {
			orderNum = (request.getParameter("LGD_OID"));
			userEmail = request.getParameter("LGD_BUYEREMAIL");
			userName = request.getParameter("LGD_BUYER");
			totalPrice = Integer.parseInt(request.getParameter("LGD_AMOUNT"));
			
		}
		
		orderDTO.setOrderNum(orderNum);
		orderDTO.seteMail(userEmail);
		
		//�����
		orderDTO.setUserId("aaa");
		reviewDTO.setUserId("aaa");
				
		//�ֹ� ����Ʈ
		List<OrderListDTO> orderList = dao.getOrderList("aaa");
		Iterator<OrderListDTO> orderLists = orderList.iterator();
		int totalAmount=0;
		
		while(orderLists.hasNext()){
		
			//�ֹ����� �Է�
			OrderListDTO dto = orderLists.next();
			
			orderDTO.setProductId(dto.getProductId());
			orderDTO.setAmount(dto.getAmount());
			orderDTO.setPrice(dto.getPrice());
			
			if(mode.equals("without_bankbook")) {
				orderDTO.setPayment("no");
			}
			else {
				orderDTO.setPayment("yes");
				
				//���䵥���� �Է�
				reviewDTO.setProductId(dto.getProductId());
				dao.insertReview(reviewDTO);
			}
			
			dao.insertOrderDataProduct(orderDTO);
			
			//��ٱ��� ������ ����
			dao.deleteCartProduct("aaa", dto.getProductId());
			
			//�� ���� �� ����
			totalAmount += dto.getAmount();

		}
		
		//�⺻ ����
		String orderDate ="";
		String orderDest="";
		
		List<OrderDTO> orderCompleteList = dao.getCompleteOrder("aaa",orderNum);
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
		request.setAttribute("imagePath", "./upload/list");
		
		if(mode.equals("without_bankbook")) {
			return "order/without_bankbook";
		}
		
		//����� ����Ʈ ����
		dao.updateMemberPoint("aaa", (int)Float.parseFloat(request.getParameter("totalPoint")));
		return "order/orderComplete";
		
	}
	
	@RequestMapping(value = "/myOrderLists.action", method = {RequestMethod.GET, RequestMethod.POST})
	public String myOrderLists(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
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
		
		int dataCount = dao.getNumUserOrderLists("aaa", num, searchPeriod);
		int numPerPage = 7;
		
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);
		
		if(currentPage>totalPage)
			currentPage = totalPage;
		
		int start = (currentPage-1)*numPerPage+1;
		int end = currentPage*numPerPage;
		
		userOrderlist = dao.getUserOrderLists("aaa", numPerPage, searchPeriod, start, end);
		
		String listUrl = cp + "/myOrderLists.action";
		
		String pageIndexList = myUtil.myOrderPageIndexList(currentPage, totalPage, listUrl,period);
		
		request.setAttribute("userOrderlist", userOrderlist);
		request.setAttribute("pageIndexList", pageIndexList);
		request.setAttribute("period", period);
		request.setAttribute("imagePath", "./upload/list");
		
		return "order/myOrderList";
	}
	
}