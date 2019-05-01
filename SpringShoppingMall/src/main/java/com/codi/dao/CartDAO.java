package com.codi.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codi.dto.CartDTO;

@Component("cartDAO")
public class CartDAO {
	
	@Autowired
	private SqlSessionTemplate sessionTemplate;

	//��ٱ��ϸ���Ʈ
	public List<CartDTO> getReadData(String userId){
		return sessionTemplate.selectList("cartMapper.getReadData", userId);	
	}
	
	//��ٱ��Ͼ����۰���
	public int getTotalItemCount(String userId){
		return sessionTemplate.selectOne("cartMapper.getTotalItemCount", userId);	
	}
	
	//��ٱ����Ѿ�
	public int getTotalItemPrice(String userId){
		return sessionTemplate.selectOne("cartMapper.getTotalItemPrice", userId);	
	}
	
	//��ٱ��� �ֹ�����
	public int getTotalItemCountYes(String userId){
		return sessionTemplate.selectOne("cartMapper.getTotalItemCountYes", userId);	
	}
	
	//productIdã��
	public String getProductId(String productName, String productSize, String color){
		HashMap<String, String> hMap = new HashMap<String, String>();
		hMap.put("productName", productName);
		hMap.put("productSize", productSize);
		hMap.put("color", color);
		return sessionTemplate.selectOne("cartMapper.getProductId", hMap);	
	}
	
	//��ٱ��ϳ� ������ǰ���翩�� Ȯ��
	public int searchBeforeProductId(CartDTO dto){
		return sessionTemplate.selectOne("cartMapper.searchBeforeItem", dto);	
	}
	
	//��ٱ��� ��ǰ�߰�
	public void insertCartItem(CartDTO dto){
		sessionTemplate.insert("cartMapper.insertCartItem", dto);
	}
	
	//��ٱ��� �����߰�
	public void updateCartItemAmountAdd(CartDTO dto){
		sessionTemplate.update("cartMapper.updateCartItemAddAmount", dto);
	}
	
	//��ٱ��� ��ǰ����
	public void deleteCartItem(String productId, String userId) {
		HashMap<String, String> hMap = new HashMap<String, String>();
		hMap.put("productId", productId);
		hMap.put("userId", userId);
		sessionTemplate.delete("cartMapper.deleteCart", hMap);
	}
	
	//��ٱ��� �ֹ����� Ȯ��
	public String checkOrderSelect(String productId, String userId) {
		HashMap<String, String> hMap = new HashMap<String, String>();
		hMap.put("productId", productId);
		hMap.put("userId", userId);
		return sessionTemplate.selectOne("cartMapper.getOrderSelectData", hMap);
	}
	
	//��ٱ��� �ֹ����� ����
	public void amendOrderSelect(String productId, String userId, String orderSelect) {
		HashMap<String, String> hMap = new HashMap<String, String>();
		hMap.put("productId", productId);
		hMap.put("userId", userId);
		hMap.put("orderSelect", orderSelect);
		sessionTemplate.update("cartMapper.updateOrderSelectData", hMap);
	}
	
	//��ٱ��� ������/�÷� �ɼ� ���� �Է�
	public void updateCartItemSizeOrColor(CartDTO dto){	
		sessionTemplate.update("cartMapper.updateCartItemSizeOrColor", dto);
	}
	
	//��ٱ��� ��������
	public void updateCartItemAmount(CartDTO dto){
		sessionTemplate.update("cartMapper.updateCartItemAmount", dto);
	}
}
