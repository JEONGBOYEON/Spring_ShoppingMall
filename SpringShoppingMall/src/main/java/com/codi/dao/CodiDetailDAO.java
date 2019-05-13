package com.codi.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codi.dto.CommunityDTO;
import com.codi.dto.MemberDTO;
import com.codi.dto.ProductDetailDTO;
import com.codi.dto.ReplyDTO;

@Component("codiDetailDAO")
public class CodiDetailDAO {
	@Autowired
	private SqlSessionTemplate sessionTemplate;

	//Ŀ�´�Ƽ �Խñ� ��ȸ
	public CommunityDTO getReadCodiData(int iNum){
		return sessionTemplate.selectOne("codiDetailMapper.getOneInstar", iNum);	
	}
	
	//��ȸ�� ����
	public void updateHitCount(int iNum) {
		sessionTemplate.update("codiDetailMapper.updateHitCount", iNum);
	}
	
	//����id �Խñ� ��ȸ
	public List<CommunityDTO> getReadCodiData(String userId, int iNum){
		HashMap<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("userId", userId);
		hMap.put("iNum", iNum);
		return sessionTemplate.selectList("codiDetailMapper.getReadCodiLists", hMap);	
	}
	
	//����id �Խñ� ���� ��ȸ
	public int getReadCodiDataCount(String userId, int iNum){
		HashMap<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("userId", userId);
		hMap.put("iNum", iNum);
		return sessionTemplate.selectOne("codiDetailMapper.getReadCodiDataCount", hMap);	
	}
	
	//������ǰid ��ǰ���� ��ȸ
	public ProductDetailDTO getCodiProductItem(String productId){
		return sessionTemplate.selectOne("codiDetailMapper.getCodiProductItem", productId);	
	}
	
	//���߻�ǰ ���ƿ� ��ȸ
	public List<String> storeHeartList(String userId){
		return sessionTemplate.selectList("codiDetailMapper.storeHeartList", userId);	
	}
	
	//���ƿ� ��� ī��Ʈ
	public int getCodiHeartCount(int iNum){
		return sessionTemplate.selectOne("codiDetailMapper.getCodiHeartCount", iNum);	
	}
	
	//�α��� �� ���ƿ� üũ
	public int getMyCodiHeart(String userId,int iNum){
		HashMap<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("userId", userId);
		hMap.put("iNum", iNum);
		return sessionTemplate.selectOne("codiDetailMapper.myCodiHeart", hMap);	
	}
	
	//�ۼ��� ���� ��ȸ
	public MemberDTO getUserInfo(String userId){
		MemberDTO dto = sessionTemplate.selectOne("instarMapper.getUserInfo",userId);
		return dto;
	}
	
	//����ִ�ī��Ʈ
	public int getReplyMaxNum(int iNum) {
		return sessionTemplate.selectOne("codiDetailMapper.getReplyMaxNum", iNum);	
	}
	
	//����Է�
	public void insertReplyData(ReplyDTO dto) {
		sessionTemplate.insert("codiDetailMapper.insertReplyData", dto);	
	}
	
	//��ۻ���
	public void deleteReplyData(int replyNum) {
		sessionTemplate.delete("codiDetailMapper.deleteReplyData", replyNum);	
	}
	
	//�ڵ�Խù� ���ī��Ʈ
	public int getReplyDataCount(int iNum) {
		return sessionTemplate.selectOne("codiDetailMapper.getReplyDataCount", iNum);	
	}
	
	//�����ȸ
	public List<ReplyDTO> getReplyListData(int iNum, int start, int end) {
		HashMap<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("iNum", iNum);
		hMap.put("start", start);
		hMap.put("end", end);
		return sessionTemplate.selectList("codiDetailMapper.getReplyListData", hMap);	
	}
	
	//�ۼ��� �ȷο� ���� Ȯ��
	public int followCheck(String followerId, String followingId) {
		HashMap<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("followerId", followerId);
		hMap.put("followingId", followingId);	
		return sessionTemplate.selectOne("codiDetailMapper.followCheck", hMap);	
	}
}
