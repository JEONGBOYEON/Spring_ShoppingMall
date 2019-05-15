package com.codi.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codi.dto.CommunityDTO;
import com.codi.dto.MemberDTO;
import com.codi.dto.QreplyDTO;
import com.codi.dto.QuestionDTO;
import com.codi.dto.ReplyDTO;

@Component("questionDAO")
public class QuestionDAO {
	
	@Autowired
	private SqlSessionTemplate sessionTemplate;
	
	public void setSessionTemplate(SqlSessionTemplate sessionTemplate) throws Exception{
		this.sessionTemplate = sessionTemplate;
	}
	
	//qnum������
	public int getMaxNum(){
			
		int maxNum = 0;
			
		maxNum=sessionTemplate.selectOne("questionMapper.maxNum");
		
		return maxNum;
		
	}
	
	//question ���̺� insert
	public void insertQuestion(QuestionDTO dto){

		sessionTemplate.insert("questionMapper.insertQuestion",dto);
	}
	
	//�Խù� ���� ������
	public int getDataCount(){
			
		int count = 0;
			
		count=sessionTemplate.selectOne("questionMapper.getDataCount");
		
		return count;
		
	}
	
	//list select
	public List<QuestionDTO> getLists(int start, int end){
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("start", start);
		params.put("end", end);
		
		List<QuestionDTO> lists = sessionTemplate.selectList("questionMapper.getLists",params);
		return lists;
		
	}
	
	//ȸ�������� ����
	public MemberDTO getUserInfo(String userId){
		MemberDTO dto = sessionTemplate.selectOne("questionMapper.getUserInfo",userId);
		return dto;
	}
	
	//��ȸ�� ����
	public void updateHitCount(int qNum) {
		sessionTemplate.update("questionMapper.updateHitCount", qNum);
	}

	//�Խñ� ��ȸ
	public QuestionDTO getReadOne(int qNum){
		return sessionTemplate.selectOne("questionMapper.getOne", qNum);	
	}
	
	//���
	//����ִ�ī��Ʈ
	public int getReplyMaxNum(int qNum) {
		return sessionTemplate.selectOne("questionMapper.getReplyMaxNum", qNum);	
	}
	
	//����Է�
	public void insertReplyData(QreplyDTO dto) {
		sessionTemplate.insert("questionMapper.insertReplyData", dto);	
	}
	
	//��ۻ���
	public void deleteReplyData(int replyNum) {
		sessionTemplate.delete("questionMapper.deleteReplyData", replyNum);	
	}
	
	//�ڵ�Խù� ���ī��Ʈ
	public int getReplyDataCount(int qNum) {
		return sessionTemplate.selectOne("questionMapper.getReplyDataCount", qNum);	
	}
	
	//�����ȸ
	public List<QreplyDTO> getReplyListData(int qNum, int start, int end) {
		HashMap<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("qNum", qNum);
		hMap.put("start", start);
		hMap.put("end", end);
		return sessionTemplate.selectList("questionMapper.getReplyListData", hMap);	
	}
	

}
