package com.codi.app;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.codi.dao.ProductDetailDAO;
import com.codi.dto.ProductDetailDTO;
import com.codi.util.MyUtil;

@Controller
public class ProductDetailController {

	@Autowired
	@Qualifier("productDetailDAO")//Bean ��ü ���� 
	ProductDetailDAO dao;
	
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
		
		request.setAttribute("dto", dto);
		request.setAttribute("colorList", colorList);
		request.setAttribute("sizeList", sizeList);
		
		return "product/detail";
	}
	
}
