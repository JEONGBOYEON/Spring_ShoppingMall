package com.codi.app;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codi.dao.ProductDetailDAO;
import com.codi.dao.ReviewDAO;
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
	MyUtil myUtil;//Bean ��ü ����
	
	@RequestMapping(value = "/detail.action", method = {RequestMethod.GET,RequestMethod.POST})
	public String detail(HttpServletRequest request, HttpServletResponse response) {
		String cp = request.getContextPath();
		String superProduct = request.getParameter("superProduct");
		ProductDetailDTO dto = dao.getReadData(superProduct);
		List<String> colorList = dao.getColorList(dto.getSuperProduct());
		List<String> sizeList = dao.getProductSizeList(dto.getSuperProduct());
		
		// �̹������ϰ��
		String imagePath = cp + "/pds/productImageFile";
		request.setAttribute("imagePath", imagePath);
		//List<ProductDetailImageDTO> detailImagelists = dao.getDetailImageList("productName",productName);
		//List<String> optionList = dao.getOptionList(productName);
		
		//�������� / ����
		String pageNum = request.getParameter("pageNum");
		
		int currentPage = 1;
		
		if(pageNum!=null)
			currentPage = Integer.parseInt(pageNum);

		int dataCount_yes = reviewDAO.getProductDataCount(superProduct);
		
		if(dataCount_yes!=0){
			int numPerPage = 7;
			int totalPage = myUtil.getPageCount(numPerPage, dataCount_yes);

			if(currentPage>totalPage)
				currentPage = totalPage;

			int start = (currentPage-1)*numPerPage+1;
			int end = currentPage*numPerPage;

			List<ReviewDTO> lists = reviewDAO.productGetList(superProduct,start, end);

			Iterator<ReviewDTO> it = lists.iterator();

			//���� �հ�
			int totalReviewRate=0;

			while(it.hasNext()){
				ReviewDTO reviewDto = it.next();
				totalReviewRate += reviewDto.getRate();
			}

			//���� �� ���� ����
		 	int rate[] = {reviewDAO.getProductDataCountHeart(superProduct, 5),reviewDAO.getProductDataCountHeart(superProduct, 4),
		 			reviewDAO.getProductDataCountHeart(superProduct, 3),reviewDAO.getProductDataCountHeart(superProduct, 2),reviewDAO.getProductDataCountHeart(superProduct, 1)}; 
		 	
			//���� ��� 
			int avgReviewRate = totalReviewRate/dataCount_yes;
			String listUrl = cp + "detail.action";
			String pageIndexList = myUtil.pageIndexList(currentPage, totalPage, listUrl);
			String imagePath_review = "./upload/review";

			//������ ������
			request.setAttribute("lists", lists);
			request.setAttribute("pageIndexList", pageIndexList);
			request.setAttribute("imagePath_review", imagePath_review);
			request.setAttribute("avgReviewRate", avgReviewRate);
			request.setAttribute("rate", rate);
		}
		request.setAttribute("dataCount_yes", dataCount_yes);
		
		request.setAttribute("dto", dto);
		request.setAttribute("colorList", colorList);
		request.setAttribute("sizeList", sizeList);
		
		return "product/detail";
	}
	
}
