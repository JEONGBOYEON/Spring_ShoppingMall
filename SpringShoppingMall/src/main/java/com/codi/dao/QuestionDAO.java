package com.codi.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codi.dto.CommunityDTO;
import com.codi.dto.MemberDTO;
import com.codi.dto.ProductDTO;
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

	//list select �Ѹ��� User
	public List<QuestionDTO> getListsUser(int start, int end,String loginUserId){

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("start", start);
		params.put("end", end);
		params.put("userId", loginUserId);

		List<QuestionDTO> lists = sessionTemplate.selectList("questionMapper.getListsUser",params);
		return lists;

	}

	//list select �Ѹ��� User
	public List<QuestionDTO> getListsUserReply(int start, int end,String loginUserId){

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("start", start);
		params.put("end", end);
		params.put("userId", loginUserId);

		List<QuestionDTO> lists = sessionTemplate.selectList("questionMapper.getListsUserReply",params);
		return lists;

	}

	//�Ѹ��� User ���� count
	public int countOneUser(String loginUserId){

		int count = 0;

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("userId", loginUserId);

		count=sessionTemplate.selectOne("questionMapper.countOneUser",params);

		return count;
	}

	//�Ѹ��� User ���� count(���� �亯��)
	public int countOneUserReply(String loginUserId){

		int count = 0;

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("userId", loginUserId);

		count=sessionTemplate.selectOne("questionMapper.countOneUserReply",params);

		return count;
	}

	//�ȷο� : ���� �ȷο� �� ��� userId = myFreindId�� myId��
	public int follower(String userId){
		int result = 0;

		result = sessionTemplate.selectOne("mypageMapper.follower",userId);

		return result;
	}

	//�ȷ��� : ���� �ȷο� �� ��� userId = myId�� myFreind�ΰ�
	public int following(String userId){
		int result = 0;

		result = sessionTemplate.selectOne("mypageMapper.following",userId);

		return result;
	}

	//list order�� �°� ����
	public List<QuestionDTO> getListOrder(int start, int end,String order){

		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("start", start);
		params.put("end",end);
		params.put("order",order);

		List<QuestionDTO> lists = 
				sessionTemplate.selectList("questionMapper.getListsOrder",params);

		return lists;
	}

	public List<QuestionDTO> getListNotyet(int start, int end,String order){

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("start", start);
		params.put("end", end);
		params.put("order",order);

		List<QuestionDTO> lists = sessionTemplate.selectList("questionMapper.getListNotyet",params);
		return lists;

	}

	//�亯�޸������� ���� ������
	public int countNotyet(){

		int count = 0;

		count=sessionTemplate.selectOne("questionMapper.countNotyet");

		return count;

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

	//�� ���� 
	public void updateData(QuestionDTO dto){
		sessionTemplate.update("questionMapper.updateData",dto);	
	}

	//�� ���� 
	public void deleteData(QuestionDTO dto){	
		sessionTemplate.update("questionMapper.deleteData",dto);
	}
	
	//qNum ����
	public QuestionDTO getDtoQnum(int qNum){
		QuestionDTO dto = sessionTemplate.selectOne("questionMapper.getDtoQnum",qNum);
		
		return dto;
	}

	//�ۼ��� �ȷο� ���� Ȯ��
	public int followCheck(String followerId, String followingId) {
		HashMap<String, Object> hMap = new HashMap<String, Object>();
		hMap.put("MyId", followerId);
		hMap.put("MyFriendId", followingId);	

		return sessionTemplate.selectOne("questionMapper.followCheck", hMap);	
	}

	//follow
	public void insertFollow(String myId,String myFriendId){

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("myId", myId);
		params.put("myFriendId",myFriendId);

		sessionTemplate.insert("questionMapper.insertFollow",params);

	}

	//follow
	public void deleteFollow(String myId,String myFriendId){

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("myId", myId);
		params.put("myFriendId",myFriendId);

		sessionTemplate.insert("questionMapper.deleteFollow",params);

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
