<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Prova Web App</title>
</head>
<body>
<h1>A malicious web app</h1>
<br>
<form action="MyNewServlet" method="post">
	<div class="container">
    	<label><b>Username</b></label>
    	<input type="text" placeholder="Enter Username" name="uname" required>

    	<label><b>Password</b></label>
    	<input type="password" placeholder="Enter Password" name="psw" required>

    	<button type="submit">Login</button>
    	<!--  <input type="checkbox" checked="checked"> Remember me -->
 	 </div>
	<!--<input type="submit" value="Send"/> -->
</form>
</body>
</html>