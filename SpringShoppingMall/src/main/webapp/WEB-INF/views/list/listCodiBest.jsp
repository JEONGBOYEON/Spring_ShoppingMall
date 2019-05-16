<%@page import="com.codi.dto.MemberDTO"%>
<%@page import="java.net.URLEncoder"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@include file="../layout/storeNav.jsp"  %>


<script type="text/javascript">


$(function(){
	
	
	$(".goodButton").click(function(){


		var superProduct = $(this).attr('value');


        var info = '<%=(MemberDTO)session.getAttribute("customInfo")%>';
        
        if(info=="" || info==null){
        	alert("ㅅㅂ로그인이 필요합니다.");
           	alert(id);
        	return;
        }
        
        
        
        $.ajax({
            async: true,
            type : 'POST',
            data : superProduct,
            url : "../storeGood.action",
            dataType : "json",
            contentType: "application/json; charset=UTF-8",
            success : function(data) {
            	if(data.cnt > 0) {
            		$(".goodDiv" + superProduct).html('♡');
                } else {
            		$(".goodDiv" + superProduct).html('♥');
                }
                
            },
            error : function(error) {
            	alert("로그인이 필요합니다.");
            }
        });
			
	});
	
});



</script>

<style>
#order{
    color: #757575;
    width:150px;
    border: solid 1px #ededed;
    background-color: #fafafa;
    cursor: pointer;
    padding: 0 4px 0 8px;
    height: 30px;
    font-size: 14px;
    line-height: 1;
    border-radius: 0;
}
</style>

<div id="ap_container" class="ap_container">

	<div class="page_title_area">
		<div class="page_title">
			<h1 class="h_title page">CODIBEST</h1>
			<p class="text font_lg"></p>
		</div>
	</div>
	
	
	<div class="ap_contents prd_list">
		<div class="prd_category"  style="border-top: 2px solid #000000">
		

		<div class="item_list column2">
			<div class="panel notice etu_find_store_none no_result"
				style="display: none;">
				<i class="ico"></i>
				<p class="text font_lg">
					<span class="tit_1">상품이 존재하지 않습니다.</span>
				</p>
			</div>
			
			<table style="width: 1200">
				<c:set var="i" value="0" />
				
				<%int cnt = 0; int flag = 1;%>
				
				<!-- list 출력 -->
				<c:forEach var="dto" items="${lists }">
				
					<c:if test="${i==0 }">
						<tr height="30px"></tr>
						<tr>
						<c:set var="j" value="0" />
					</c:if>
					
					<td align="center" style="border: 1px solid #ededed; padding: 5px;" >
						<table>
							<tr>
								<td style="position: relative;">
									<%
										if(flag<4){
									%>
									<div style="position: absolute;">
										<img src="../resources/image/commuImage/gold-medal.png" width="50px" height="55px">
									</div>
									<div style="position: absolute; left: 21px; top: 25px; font-size: 17px; font-weight: bolder; color: #000000">
										<%=flag %>
									</div>
									<% }%>
									<a href="<%=cp%>/pr/detail.action?superProduct=${dto.superProduct}">
										<img style="max-width: 250px;" src="../upload/list/${dto.originalName}" />
									</a>
								</td>
							</tr>
							<tr>
								<td>
									<a href="${dto.storeUrl }" >
									<p align="left" style="font-size: 11pt; margin-bottom: 10px; margin-top: 2px;">${dto.storeName }</p>
									</a>
								</td>
							</tr>
							<tr height="16px">
								<td>
									<p align="left" style="font-size: 13pt; margin-bottom: 10px;">${dto.productName }</p>
								</td>
							</tr>
							<tr height="25px">
								<td>
									<p align="left" style="border-top: 1px solid #ededed; padding-top:5px; width:250px; font-size: 17pt; margin-bottom: 10px; color: black;">${dto.price}원</p>
								</td>
							</tr>
							<tr>
								<td>
									<p align="left" style="margin: 0px 10px 10px 0px; height: 20px">
									<span style="font-size: 14pt;  color: #8080FF" >★</span>
									<span>평점 ${dto.reviewRate}</span>
									<span>&nbsp;&nbsp;&nbsp;리뷰&nbsp;${dto.reviewCount}</span>
									
									<span style="font-size: 14pt;  color: #8080FF; margin-left: 98px;">
										<input type="hidden" id="superProduct" value="${dto.superProduct}" >
										<button class="goodButton" value="${dto.superProduct}">
											<div class="goodDiv${dto.superProduct}">
												<c:set var="k" value="0" />
												<c:forEach var="good" items="${good }">
													<c:if test="${dto.superProduct eq good}">
														♥
														<c:set var="k" value="1" />
													</c:if>
												</c:forEach>
												<c:if test="${k==0 }">
													♡
												</c:if>
											</div>
										</button>
									</span>
									</p>
								</td>
							</tr>
						</table>
						
						<c:set var="j" value="${j+1 }" /> 
						<c:set var="i" value="1" />
					</td>
					<c:if test="${j==4 }">
						</tr>
						<c:set var="j" value="0" />
						<c:set var="i" value="0" />
					</c:if>
					
					<%cnt++; flag++;%>
						
					<%
						if (cnt == 4) {
							out.print("</dl>");
							cnt = 0;
						}
					%>
				</c:forEach>

				<c:if test="${j!=4 }">
					<c:forEach begin="${j }" end="3" step="1">
						<td width="400"></td>
						<c:set var="j" value="${j+1 }" />
					</c:forEach>
					</tr>
				</c:if>

				<tr height="100">
					<td align="center" colspan="4">
						<c:if test="${dataCount!=0 }">
							<font style="font-size: 20px">${pageIndexList}</font>
						</c:if> 
						<c:if test="${dataCount==0 }">
							등록된 게시물이 없습니다.
						</c:if>
					</td>
				</tr>
			</table>
		</div>
	</div>


</div>


<%@include file="../layout/footer.jsp"  %>