<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script
  src="https://code.jquery.com/jquery-3.6.0.js"
  integrity="sha256-H+K7U5CnXl1h5ywQfKtSj8PCmoN9aaq30gDh27Xc0jk="
  crossorigin="anonymous"></script>
</head>
<body>

<h3>${map.title }</h3>
<h3><fmt:formatDate value="${map.date }" type="date" /></h3>

<div>${map.content }</div>
	
	<c:if test="${'no' ne map.contentType}">
		<c:if test="${'img' eq map.contentType}">
			<img src="/file/board/${map.storedName}" style="width:300px; height:300px;" />
		</c:if>
		
		<c:if test="${'video' eq map.contentType}">
			<video src="/file/board/${map.storedName}" style="width:550px; height:480px;" autoplay="autoplay"></video>
		</c:if>
		<br>
		<br>
		<button type="button" id="file_down">파일 다운로드</button>
	</c:if>
	<c:if test="${'no' eq map.contentType }">
		[파일없음]
	</c:if>
</body>

<script>

// 파일 다운로드
$('#file_down').unbind("click").click(function(e) {	
	document.location.href = "<c:url value='/board/fileDown'/>?fileName=" + '${map.originName}' + '&saveName=' + '${map.storedName}' ;
	e.preventDefault();
});

</script>
</html>