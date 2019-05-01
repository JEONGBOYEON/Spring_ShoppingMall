package com.codi.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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

import com.codi.dao.ProductDAO;
import com.codi.dao.ReviewDAO;
import com.codi.dto.MemberDTO;
import com.codi.dto.ProductDTO;
import com.codi.util.MyUtil;

@Controller
public class ProductController {

	@Autowired
	@Qualifier("productDAO")
	ProductDAO dao;
	
	@Autowired
	@Qualifier("reviewDAO")
	ReviewDAO reviewDAO;

	@Autowired
	MyUtil myUtil;// Bean ��ü ����

	@RequestMapping(value = "/commuMain.action", method = RequestMethod.GET)
	public String commuMain(HttpServletRequest req) {
		return "commuMain";
	}

	@RequestMapping(value = "/storeMain.action", method = RequestMethod.GET)
	public String storeMain(HttpServletRequest req) {
		return "storeMain";
	}

	@RequestMapping(value = "/listNew.action", method = RequestMethod.GET)
	public String listNew(HttpServletRequest req, HttpSession session) throws IOException {
		
		
		 MemberDTO info = (MemberDTO) session.getAttribute("customInfo"); 
		 
		 List<String> good = null;
		 
		 System.out.println(info);
		 
		 if(info!=null) {
			 good = dao.storeHeartList(info.getUserId());
			 System.out.println(good);
		 }
		 

		String order = req.getParameter("order");
		if (order != null) {
			order = URLDecoder.decode(req.getParameter("order"), "UTF-8");
		}

		String cp = req.getContextPath();

		String pageNum = req.getParameter("pageNum");

		int currentPage = 1;

		if (pageNum != null)
			currentPage = Integer.parseInt(pageNum);

		int dataCount = dao.getDataCount();

		int numPerPage = 3;
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);

		if (currentPage > totalPage)
			currentPage = totalPage;

		int start = (currentPage - 1) * numPerPage + 1;
		int end = currentPage * numPerPage;

		List<ProductDTO> lists;

		if (order != null && !order.equals("")) {
			lists = dao.getListOrder(start, end, order);
		} else {
			lists = dao.getList(start, end);
		}
		
		
		//������ dto�� reviewCount �� reviewRate �߰�
		
		ListIterator<ProductDTO> it = lists.listIterator();
		
		while(it.hasNext()){
			ProductDTO vo = (ProductDTO)it.next();
			
			//dao.getReviewCount(vo.productId)
			int reviewCount =  reviewDAO.getProductDataCount(vo.getSuperProduct());
			//dao.getReviewRate(vo.productId)
			float avgReviewRate = reviewDAO.productGetList_heart(vo.getSuperProduct());
			
			vo.setReviewCount(reviewCount);
			vo.setReviewRate(avgReviewRate);
		}
		
		

		// �̹������� ��� �����ֱ�
		String imagePath = req.getSession().getServletContext().getRealPath("/upload");

		// String imagePath =
		// req.getSession().getServletContext().getRealPath("/WEB-INF/files");

		// ����¡�� ���� ���� �����ֱ�
		String listUrl = cp + "/listNew.action";

		String pageIndexList = myUtil.listPageIndexList(currentPage, totalPage, listUrl, order);

		req.setAttribute("listUrl", listUrl);
		req.setAttribute("imagePath", imagePath);
		req.setAttribute("lists", lists);
		req.setAttribute("good", good);
		req.setAttribute("pageIndexList", pageIndexList);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("pageNum", pageNum);

		return "list/listNew";
	}

	@RequestMapping(value = "/listCategory.action", method = RequestMethod.GET)
	public String listCategory(HttpServletRequest req,HttpSession session) throws IOException {

		MemberDTO info = (MemberDTO) session.getAttribute("customInfo"); 

		List<String> good = null;

		System.out.println(info);

		if(info!=null) {
			good = dao.storeHeartList(info.getUserId());
			System.out.println(good);
		}
		
		String order = req.getParameter("order");
		if (order != null) {
			order = URLDecoder.decode(req.getParameter("order"), "UTF-8");
		}

		String productCategory = req.getParameter("productCategory");
		if (productCategory != null) {
			productCategory = URLDecoder.decode(req.getParameter("productCategory"), "UTF-8");
		}

		String cp = req.getContextPath();

		String pageNum = req.getParameter("pageNum");

		int currentPage = 1;

		if (pageNum != null)
			currentPage = Integer.parseInt(pageNum);

		int dataCount = dao.getDataCountCategory(productCategory);

		int numPerPage = 3;
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);

		if (currentPage > totalPage)
			currentPage = totalPage;

		int start = (currentPage - 1) * numPerPage + 1;
		int end = currentPage * numPerPage;

		List<ProductDTO> lists;

		if (order != null && !order.equals("")) {
			lists = dao.getListsCategoryOrder(start, end, productCategory, order);
		} else {
			lists = dao.getListsCategory(start, end, productCategory);
		}

		// �̹������� ��� �����ֱ�
		String imagePath = req.getSession().getServletContext().getRealPath("/upload");

		// String imagePath =
		// req.getSession().getServletContext().getRealPath("/WEB-INF/files");

		// ����¡�� ���� ���� �����ֱ�
		String listUrl = cp + "/listCategory.action?productCategory=" + productCategory;

		String pageIndexList = myUtil.listPageIndexList(currentPage, totalPage, listUrl, order);

		if (productCategory != null) {
			productCategory = URLEncoder.encode(productCategory, "UTF-8");
		}

		req.setAttribute("good", good);
		req.setAttribute("listUrl", listUrl);
		req.setAttribute("productCategory", productCategory);
		req.setAttribute("imagePath", imagePath);
		req.setAttribute("lists", lists);
		req.setAttribute("pageIndexList", pageIndexList);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("pageNum", pageNum);

		return "list/listCategory";
	}

	@RequestMapping(value = "/listBest.action", method = RequestMethod.GET)
	public String listBest(HttpServletRequest req,HttpSession session) throws IOException {
		
		MemberDTO info = (MemberDTO) session.getAttribute("customInfo"); 

		List<String> good = null;

		System.out.println(info);

		if(info!=null) {
			good = dao.storeHeartList(info.getUserId());
			System.out.println(good);
		}
		
		String cp = req.getContextPath();

		String pageNum = req.getParameter("pageNum");

		int currentPage = 1;

		if (pageNum != null)
			currentPage = Integer.parseInt(pageNum);

		int dataCount = dao.getDataCount();

		int numPerPage = 3;
		int totalPage = myUtil.getPageCount(numPerPage, dataCount);

		if (currentPage > totalPage)
			currentPage = totalPage;

		int start = (currentPage - 1) * numPerPage + 1;
		int end = currentPage * numPerPage;

		List<ProductDTO> lists;

		lists = dao.getListOrder(start, end, "amount desc");

		// �̹������� ��� �����ֱ�
		String imagePath = req.getSession().getServletContext().getRealPath("/upload");

		// String imagePath =
		// req.getSession().getServletContext().getRealPath("/WEB-INF/files");

		// ����¡�� ���� ���� �����ֱ�
		String listUrl = cp + "/listBest	.action";

		String pageIndexList = myUtil.listPageIndexList(currentPage, totalPage, listUrl, "amount desc");

		req.setAttribute("good", good);
		req.setAttribute("listUrl", listUrl);
		req.setAttribute("imagePath", imagePath);
		req.setAttribute("lists", lists);
		req.setAttribute("pageIndexList", pageIndexList);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("totalPage", totalPage);
		req.setAttribute("pageNum", pageNum);

		return "list/listBest";
	}

	@RequestMapping(value = "/productAdminCreate.action", method = RequestMethod.GET)
	public String productadminCreate(HttpServletRequest req) {

		return "admin/productAdminCreate";
	}

	@RequestMapping(value = "/productAdminCreate_ok.action", method = { RequestMethod.POST, RequestMethod.GET })
	public String productAdminCreate_ok(ProductDTO dto, MultipartHttpServletRequest request, String str)
			throws Exception {

		// 1. ����Ʈ ������ ������ �ø��� �۾�

		// Spring3.0���� ��ΰ� �ٲ� : WEB-INF�� files��� ������ �����ؼ� �����ض�
		String path = request.getSession().getServletContext().getRealPath("/upload/list");
		// D:\sts-bundle\work\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\SpringShoppingMall\

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
		dto.setOriginalName(file.getOriginalFilename());
		dto.setSaveFileName(file.getOriginalFilename());

		dao.insertData(dto);

		return "redirect:/productAdminList.action";

	}

	@RequestMapping(value = "/productAdminList.action", method = RequestMethod.GET)
	public String productAdminList(HttpServletRequest req) {

		List<ProductDTO> lists = dao.getAdminLists();

		req.setAttribute("lists", lists);

		return "admin/productAdminList";
	}

	@RequestMapping(value = "/productAdminDelete.action", method = RequestMethod.GET)
	public String productAdminDelete(HttpServletRequest req) {

		String productId = req.getParameter("productId");
		String originalName = req.getParameter("originalName");

		String path = req.getSession().getServletContext().getRealPath("/upload/list/" + originalName);

		// DB���� ����
		dao.productAdminDelete(productId);

		// ������ ���� ����
		try {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return "redirect:/productAdminList.action";
	}

	@RequestMapping(value = "/good.action", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Map<Object, Object> good(@RequestBody String superProduct, HttpSession session) {

		MemberDTO info = (MemberDTO) session.getAttribute("customInfo");

		// if storeHeart���̺��� userId�� superProduct�� ������ ������ �Է� : ���̺��� �Է��ϰ� cnt = 0

		// ���� Ŭ���� ��ǰ�� ���� �α����� ���̵� ���ƿ� ���� ��ǰ�̸� 1 �ƴϸ� 0
		int result = dao.storeHeartCount(superProduct, info.getUserId());
		int count = 0;

		if (result == 0) {
			dao.insertHeart(superProduct, info.getUserId());
			count = 0;
		} else {
			dao.deleteHeart(superProduct, info.getUserId());
			count = 1;
		}

		Map<Object, Object> map = new HashMap<Object, Object>();

		map.put("cnt", count);

		return map;
	}

}
