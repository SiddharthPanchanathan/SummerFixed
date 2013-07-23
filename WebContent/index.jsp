<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    <%@ page import="com.hm.newage.services.newFile1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="dataForm.asp">
Country: <input type="text" name="Country" value="Singapore"><br>
Level: <input type="text" name="Level" value="Secondary 1"><br>
Stream: <input type="text" name="Stream" value="Express"><br>
</form>
<%
out.println(newFile1.getHTMLString());
%>

</body>
</html>