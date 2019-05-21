<%@page import="com.codi.dto.MemberDTO"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@include file="../layout/top.jsp"  %>

<style>
.container2 {
    margin-right: auto;
    margin-left: auto;
    box-sizing: border-box;
    width: 1186px;
    max-width: 100%;
    box-sizing: border-box;
    min-height: 600px;
    padding-top: 60px;
    text-align: center;
}

.card__action__btn, .card__action__link {
    -webkit-box-flex: 1;
    -moz-flex: 1;
    -ms-flex: 1;
    flex: 1;
    border: none;
    height: 56px;
    font-size: 12px;
    padding: 0;
    background: transparent;
    color: #424242;
    position: relative;
}

.icon--common-action {
    background-image: url(/resources/image/1410959343.PNG);
    background-size: 400px auto;
}


.text_overflow {
  overflow: hidden; 
  text-overflow: ellipsis;
  height: 45px;
}


</style>

<script type="text/javascript">


$(function(){
	
	
	$(".goodButton").click(function(){


		var iNum = $(this).attr('value');
        

		var chk='${sessionScope.customInfo.userId}';
		
		if(chk==""){
        	alert("로그인이 필요합니다.");
        	return;
        }    
		
        $.ajax({
            async: true,
            type : 'POST',
            data : iNum,
            url : "../codiGood.action",
            dataType : "json",
            contentType: "application/json; charset=UTF-8",
            success : function(data) {
            	if(data.temp > 0) {
            		$(".goodDiv" + iNum).html('<img src="../resources/image/heart1.PNG" style="height: 25px;"/> ' + data.count);
                } else {
            		$(".goodDiv" + iNum).html('<img src="../resources/image/heart2.PNG" style="height: 25px;"/> ' + data.count);
                }
                
            },
            error : function(error) {
            	alert(error);
            }
        });
			
	});
	
	
	$(".followButton").click(function(){


		var chk='${sessionScope.customInfo.userId}';
		
		if(chk==""){
        	alert("로그인이 필요합니다.");
        	return;
        }    
		
		var dtoId = $(this).attr('value');
        
        $.ajax({
            async: true,
            type : 'POST',
            data : dtoId,
            url : "../follow.action",
            dataType : "json",
            contentType: "application/json; charset=UTF-8",
            success : function(data) {
            	$(".followDiv" + dtoId).html(data.str);
            },
            error : function(jqXHR, exception) {
            	alert(error);
            }
        });
			
	});
	
});



</script>

<div class="container2">

<c:set var="i" value="0" />

<c:forEach var="dto" items="${lists }">

<c:if test="${i==0 }">
	<div style="display: block;" >
	<c:set var="j" value="0" />
</c:if>

<div style="width: 280px; height: 530px; display: inline-block;  vertical-align: top;">
<div style="position: relative;  text-align: left;">
	<a href="/users/2324110">
		<img style="border:1px solid #F6F6F6; width: 36px; height: 36px; border-radius: 18px; display: inline-block;" src="../upload/profile/${dto.mImage }"/>
	</a>
	<p style="margin-left:10px; margin-bottom:5px; display: inline-block; vertical-align:bottom;">
		<a href="#">
			<strong>${dto.userId }</strong>
		</a>
		
		<strong>&nbsp;・&nbsp;</strong>
		<button class="followButton" value="${dto.userId}"style="color: #8080FF; padding: 0; font-weight: 550; padding-bottom: 4px;" type="button">
			<div class="followDiv${dto.userId}">
				<c:set var="k" value="0" />
				<c:forEach var="follow" items="${follow }">
					<c:if test="${dto.userId eq follow}">
						팔로우 취소
						<c:set var="k" value="1" />
					</c:if>
				</c:forEach>
				<c:if test="${k==0 }">
					팔로우
				</c:if>
			</div>
		</button>
		
		<a href="/users/820291"><br>
			<small>${dto.mMessage }</small>
		</a>
	</p>
	
	<div style="width: 260px; position: relative;" >
		<a href="<%=cp%>/pr/codiDetailList.action?iNum=${dto.iNum}">
			<img style="border:1px solid #C2C2C2; width: 256px; border-bottom: none; height: 236px; margin-top:5px; display: inline-block;" src="../upload/makecodi/${dto.iImage }.png"/>
		</a>
	</div>

	<div style="border:1px solid #C2C2C2;  width: 256px;" >
		<div style="margin: 10px 5px 10px 5px; line-height: 20px;">
			<strong>${dto.userId }</strong>&nbsp;&nbsp; <span style="color: #8080FF">${dto.iHashTag}</span>&nbsp;${dto.iContent}
		</div> 
	</div>
	
	<div style="border:1px solid #C2C2C2;  width: 256px; border-top:none;  "  >
		<div style="margin: 0px 5px 10px 5px; line-height: 20px; padding-top: 10px;">
			<c:choose>
				<c:when test="${empty dto.replydto }">
					<a href="<%=cp%>/pr/codiDetailList.action?iNum=${dto.iNum}" style="color:#C2C2C2;">
						댓글 달기..
					</a>
				</c:when>
				<c:otherwise>
					<div style="margin-bottom: 5px;">
						<a href="<%=cp%>/pr/codiDetailList.action?iNum=${dto.iNum}">
							댓글 ${dto.replyCount }개 모두 보기
						</a>
					</div>
					<c:set var="cnt" value="0" />
					<c:forEach var="replydto" items="${dto.replydto }">
							<c:if test="${cnt!=2}">
								<div style="vertical-align:middle; ">
									<img style="border:1px solid #F6F6F6; width: 20px; height: 20px; border-radius: 18px; display: inline-block;" src="../upload/profile/${dto.mImage }"/>
									<strong>${replydto.userId }</strong>
									<div style=" display:inline-block; vertical-align:middle; width:150px; overflow:hidden; white-space:nowrap; text-overflow:ellipsis">&nbsp;&nbsp; ${replydto.content}</div>
									<c:set var="cnt" value="${cnt+1 }" />
								</div>	
							</c:if>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div> 
	</div>
	
	<div style="display: flex; border:1px solid #C2C2C2; width: 256px; border-top :none;">
		<button class="goodButton" value="${dto.iNum}" style="height: 40px;">
			<div  style="display: inline-block; margin-left: 5px; ">
			받은 좋아요 수 ${dto.heartCount }
			</div>
			<div class="goodDiv${dto.iNum}" style="margin-left: 120px; display: inline-block;">
				<c:set var="k" value="0" />
				<c:forEach var="good" items="${good }">
					<c:if test="${dto.iNum eq good}">
						<img src="../resources/image/heart2.PNG" style="height: 25px;"/>
						<c:set var="k" value="1" />
					</c:if>
				</c:forEach>
				<c:if test="${k==0 }">
					<img src="../resources/image/heart1.PNG" style="height: 25px;"/>
				</c:if>
			</div>
		</button>
	</div>
	
	
</div>
</div>
<!-- end 하나 이미지 -->

<c:set var="j" value="${j+1 }" /> 
<c:set var="i" value="1" />

<c:if test="${j==4 }">
	</div>
	<c:set var="j" value="0" />
	<c:set var="i" value="0" />
</c:if>

</c:forEach>

<c:if test="${j!=4 and j!=0}">
	<c:forEach begin="${j }" end="3" step="1">
		<div style="width: 280px; display: inline-block; "></div>
		<c:set var="j" value="${j+1 }" />
	</c:forEach>
	</div>
</c:if>

<div style="height:100px;">
	<c:if test="${dataCount!=0 }">
		<font style="font-size: 20px">${pageIndexList}</font>
	</c:if> 
	<c:if test="${dataCount==0 }">
		등록된 게시물이 없습니다.
	</c:if>
</div>

<%@include file="../layout/footer.jsp"  %>