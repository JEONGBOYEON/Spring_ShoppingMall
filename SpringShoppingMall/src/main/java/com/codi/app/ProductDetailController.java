package com.codi.app;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.codi.dao.ProductDetailDAO;
import com.codi.dao.ReviewDAO;
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
	MyUtil myUtil;//Bean ��ü ����
	
	@RequestMapping(value = "/pr/detail.action", method = {RequestMethod.GET,RequestMethod.POST})
	public String detail(String superProduct, HttpServletRequest request, HttpServletResponse response) {
		String cp = request.getContextPath();
		ProductDetailDTO dto = dao.getReadData(superProduct);
		List<String> colorList = dao.getColorList(dto.getSuperProduct());
		List<String> sizeList = dao.getProductSizeList(dto.getSuperProduct());
		
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
	public String detailReview(HttpServletRequest request, HttpServletResponse response) {
		
		String cp = request.getContextPath();
		String superProduct = request.getParameter("superProduct");
		ProductDetailDTO dto = dao.getReadData(superProduct);
		
		String order = request.getParameter("order");	
		
		if(order==null)
			order="recent";
		
		String orderBy;
		if(order.equals("recent"))
			orderBy = "reviewDate desc";
		else if(order.equals("worst"))
			orderBy = "rate";
		else
			orderBy = "rate desc";
		
		// �̹������ϰ��
		String imagePath = cp + "/pds/productImageFile";
		request.setAttribute("imagePath", imagePath);
		
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
			
			List<ReviewDTO> lists = reviewDAO.productGetList(superProduct, start, end,orderBy);
			

			//���� �� ���� ����
		 	int rate[] = {reviewDAO.getProductDataCountHeart(superProduct, 5),reviewDAO.getProductDataCountHeart(superProduct, 4),
		 			reviewDAO.getProductDataCountHeart(superProduct, 3),reviewDAO.getProductDataCountHeart(superProduct, 2),reviewDAO.getProductDataCountHeart(superProduct, 1)}; 
		 	
			//���� ��� 
		 	float avgReviewRate = reviewDAO.productGetList_heart(superProduct);
		 	
			String pageIndexList = myUtil.reviewPageIndexList(currentPage, totalPage,order);
			String imagePath_review = "./upload/review";

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
		
		request.setAttribute("dto", dto);
		
		return "product/productReview";
	}
	
	@RequestMapping(value = "/productAdminUpdate.action", method = RequestMethod.GET)
	public String productadminUpdate(String productId, HttpServletRequest request) {
		
		ProductDetailDTO dto = dao.getUpdateData(productId);

		request.setAttribute("dto", dto);
		return "admin/productAdminUpdate";
	}
	
	@RequestMapping(value = "/productAdminUpdate_ok.action", method = { RequestMethod.POST, RequestMethod.GET })
	public String productAdminUpdate_ok(ProductDTO dto, ProductDetailDTO detailDTO, MultipartHttpServletRequest request, String str)
			throws Exception {
		// 1. ����Ʈ ������ ������ �ø��� �۾�
		// Spring3.0���� ��ΰ� �ٲ� : WEB-INF�� files��� ������ �����ؼ� �����ض�
		String path = request.getSession().getServletContext().getRealPath("/upload/list");
		String detailImagePath = request.getSession().getServletContext().getRealPath("/upload/productDetail");

		// �̸����� file�� �޾ƿ�
		MultipartFile file = request.getFile("productListImage");

		// ������ �����Ѵٸ� ���ε� �ϱ�
		if (file != null && file.getSize() > 0) {

			try {

				FileOutputStream fos = new FileOutputStream(path + "/" + file.getOriginalFilename());

				InputStream is = file.getInputStream();

				byte[] buffer = new byte[512];

				while (true) {

					int data = is.read(buffer, 0, buffer.length);

					if (data == -1) {
						break;
					}

					fos.write(buffer, 0, data);
				}

				is.close();
				fos.close();

			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}

		// 2. DB�� �ֱ�
		if(file != null && file.getSize() > 0) {
			//������� ������ �ִ� ���
			dto.setOriginalName(file.getOriginalFilename());
			dto.setSaveFileName(file.getOriginalFilename());
		}else {
			//������� ������ ���� ��� ���������� �״�� ���
			ProductDetailDTO beforedto = dao.getReadData(dto.getProductId());
			dto.setOriginalName(beforedto.getOriginalName());
			dto.setSaveFileName(beforedto.getSaveFileName());
		}
		
		//product���̺� ����
		dao.updateData(dto);
		
		// ��ǰ ���̹��� ���
		// ���ε��� ���̹��� ���� ���� ����
		// ���̹����� ÷�������� 3������ ����
		for (int i=1; i<=3; i++) {		
			String productDetailImage = "productDetailImage"+i;
			//�̸����� file�� �޾ƿ�
			MultipartFile detailFile = request.getFile(productDetailImage);
			//���Ͼ��ε�
			if(detailFile!=null && detailFile.getSize()>0) {
				
				try {
					FileOutputStream fos = new FileOutputStream(detailImagePath + "/" + detailFile.getOriginalFilename());
					InputStream is = detailFile.getInputStream();
					byte[] buffer = new byte[512];
					
					while(true) {
						int data = is.read(buffer,0,buffer.length);
						if(data==-1) {
							break;
						}
						fos.write(buffer,0,data);
					}
					is.close();
					fos.close();
					
				} catch (Exception e) {
					System.out.println(e.toString());
				}
				
				//DB�ݿ�
				detailDTO.setOriginalName(detailFile.getOriginalFilename());
				detailDTO.setSaveFileName(detailFile.getOriginalFilename());
				
				//������ǰ���Է½�
				if(detailDTO.getSuperProduct()==null) {
					//���ϻ�ǰ�� ��ǰ��ȸ
					String superProduct = dao.searchSuperProduct(detailDTO.getProductName());
					//��� ���� ��� �ڽ��� �ֻ�����ǰ
					if(superProduct==null) {
						detailDTO.setSuperProduct(detailDTO.getProductId());
					} else {
						detailDTO.setSuperProduct(superProduct);
					}
				}
				String detailNum = detailDTO.getProductId()+"-"+i;
				detailDTO.setDetailNum(detailNum);
				dao.insertData(detailDTO);
			}
		}
		return "redirect:/productAdminList.action";
	}

	
}
