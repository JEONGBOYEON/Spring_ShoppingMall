package com.codi.app;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.codi.dao.CouponDAO;
import com.codi.dao.ProductDetailDAO;
import com.codi.dao.ReviewDAO;
import com.codi.dto.CouponDTO;
import com.codi.dto.MemberDTO;
import com.codi.dto.MyCouponDTO;
import com.codi.dto.ProductDTO;
import com.codi.dto.ProductDetailDTO;
import com.codi.dto.ReviewDTO;
import com.codi.util.MyUtil;

@Controller
public class ProductDetailController {

	@Autowired
	@Qualifier("productDetailDAO")//Bean ��ü ���� 
	ProductDetailDAO dao;
	
	@Autowired
	@Qualifier("reviewDAO")
	ReviewDAO reviewDAO;
	
	@Autowired
	@Qualifier("couponDAO")
	CouponDAO couponDAO;
	
	@Autowired
	MyUtil myUtil;//Bean ��ü ����
	
	@RequestMapping(value = "/pr/detail.action", method = {RequestMethod.GET,RequestMethod.POST})
	public String detail(String superProduct, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		String cp = request.getContextPath();
		ProductDetailDTO dto = dao.getReadData(superProduct);
		List<String> colorList = dao.getColorList(dto.getSuperProduct());
		List<String> sizeList = dao.getProductSizeList(dto.getSuperProduct());

		List<CouponDTO> couponlists = couponDAO.getList();
		

		// �̹������ϰ��
		String imagePath = cp + "/upload/list";
		request.setAttribute("imagePath", imagePath);
		String detailImagePath = cp + "/upload/productDetail";
		request.setAttribute("detailImagePath", detailImagePath);
		
		
		List<ProductDetailDTO> detailImagelists = dao.getDetailImageList(superProduct);
		
		String order = request.getParameter("order");	
		
		if(order==null)
			order="recent";		

		int dataCount_yes = reviewDAO.getProductDataCount(superProduct);
		
		request.setAttribute("couponlists", couponlists);
		request.setAttribute("dataCount_yes", dataCount_yes);
		request.setAttribute("detailImagelists", detailImagelists);
		request.setAttribute("dto", dto);
		request.setAttribute("order", order);
		request.setAttribute("superProduct", superProduct);
		request.setAttribute("colorList", colorList);
		request.setAttribute("sizeList", sizeList);
		
		return "product/detail";
	}
	
	@RequestMapping(value = "/pr/detailReview.action", method = {RequestMethod.GET,RequestMethod.POST})
	public String detailReview(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");
		
		String cp = request.getContextPath();
		String superProduct = request.getParameter("superProduct");
		ProductDetailDTO dto = dao.getReadData(superProduct);
		
		String order = request.getParameter("order");	
		
		String mode = request.getParameter("mode");
		String reviewNum = request.getParameter("reviewNum");
		if(reviewNum!=null && !reviewNum.equals("")) {
			if(info!=null && !info.equals("")) {
				if(mode=="good" || mode.equals("good")) {
					int result = reviewDAO.reviewGoodCount(reviewNum, info.getUserId());
				
					if (result == 0) {
						reviewDAO.insertReviewGood(reviewNum, info.getUserId());
					} else {
						reviewDAO.deleteReviewGood(reviewNum, info.getUserId());
					}
				}
				else {
					int result = reviewDAO.reviewReportCount(reviewNum, info.getUserId());					
					if (result == 0) {
						reviewDAO.insertReviewReport(reviewNum, info.getUserId(),request.getParameter("checkedValue"));
					} else {
						reviewDAO.deleteReviewReport(reviewNum, info.getUserId());
					}
				}
			}			
		}
		
		if(order==null)
			order="recent";
		
		String orderBy;
		if(order.equals("recent"))
			orderBy = "reviewDate desc";
		else if(order.equals("worst"))
			orderBy = "rate";
		else
			orderBy = "rate desc";
		
		List<String> good = null;
		if(info!=null) {
			good = reviewDAO.reviewGoodList(info.getUserId());
		}
		
		List<String> report = null;
		if(info!=null) {
			report = reviewDAO.reviewReportList(info.getUserId());
		}
		
		// �̹������ϰ��
		String imagePath = cp + "/pds/productImageFile";
		request.setAttribute("imagePath", imagePath);
		
		//�������� / ����
		String pageNum = request.getParameter("pageNum");
		
		int currentPage = 1;
		
		if(pageNum!=null && !pageNum.equals(""))
			currentPage = Integer.parseInt(pageNum);

		int dataCount_yes = reviewDAO.getProductDataCount(superProduct);
		
		if(dataCount_yes!=0){
			
			int numPerPage = 7;
			int totalPage = myUtil.getPageCount(numPerPage, dataCount_yes);

			if(currentPage>totalPage)
				currentPage = totalPage;

			int start = (currentPage-1)*numPerPage+1;
			int end = currentPage*numPerPage;
			
			List<ReviewDTO> lists = reviewDAO.productGetList(superProduct, start, end,orderBy);
			Iterator<ReviewDTO> it = lists.iterator();
			while(it.hasNext()) {
				ReviewDTO reviewdto = it.next();
				reviewdto.setGoodCount(reviewDAO.reviewAllCount(reviewdto.getReviewNum()));
				reviewdto.setContent(reviewdto.getContent().replaceAll("\n", "<br/>"));
			}
			

			//���� �� ���� ����
		 	int rate[] = {reviewDAO.getProductDataCountHeart(superProduct, 5),reviewDAO.getProductDataCountHeart(superProduct, 4),
		 			reviewDAO.getProductDataCountHeart(superProduct, 3),reviewDAO.getProductDataCountHeart(superProduct, 2),reviewDAO.getProductDataCountHeart(superProduct, 1)}; 
		 	
			//���� ��� 
		 	float avgReviewRate = (float) (Math.round(reviewDAO.productGetList_heart(superProduct)*10)/10.0);
		 	
			String pageIndexList = myUtil.reviewPageIndexList(currentPage, totalPage,order);
			String imagePath_review = "../upload/review";

			//������ ������
			request.setAttribute("lists", lists);
			request.setAttribute("order", order);
			request.setAttribute("pageNum", pageNum);	
			request.setAttribute("pageIndexList", pageIndexList);
			request.setAttribute("imagePath_review", imagePath_review);
			request.setAttribute("avgReviewRate", avgReviewRate);
			request.setAttribute("rate", rate);
		}
		
		request.setAttribute("dataCount_yes", dataCount_yes);
		request.setAttribute("good", good);
		request.setAttribute("report", report);
		request.setAttribute("dto", dto);
		
		return "product/productReview";
	}
	
}
