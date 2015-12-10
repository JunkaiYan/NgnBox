<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">
<link rel="icon" href="../../favicon.ico">

<title>Box</title>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

<!-- jQuery library -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

<!-- Custom styles for this template -->
<link href="styles/login_style.css" rel="stylesheet">

<script src="js/script.js"></script>

<script type="text/javascript">

$(document).ready(function() {
	(function() {
		$("#submit").click(function() {
			var usr = document.getElementById('usr').value;
			var pwd = document.getElementById('pwd').value;
/* 			console.log("usr: " + usr);
			console.log("pwd: " + pwd); */
			var data = {
				"action": "login",
				"username": usr,
				"password": pwd
			};
			$.get(server, data, function (response) {
				if (response == "fail") 
					alert("Username or Password is wrong, try again!");
				else if (response == "success") {
					localStorage.username = usr;
					redirect();
				}
			})
			.fail(function(e) {
				alert("Fail, try again later!");
			});
		});
	})();
});

</script>

</head>





<body>

	<div class="container">
		<div class="header clearfix">
			<nav>
				<ul class="nav nav-pills pull-right">
					<li role="presentation" class="active"><a href="signup.jsp">Sign up</a></li>
				</ul>
			</nav>
			<h3 class="text-muted">Box</h3>
		</div>

		<div class="jumbotron">
			<h1>Welcome to Box!</h1>
			<p class="lead">An easy-to-use online file hosting platform.</p>
			<br>

			<div class="container">
			 <!-- action="http://localhost:8080/ngn/myServlet" method="post"  -->
				<form role="form" name="myform" id="myform">
					<div class="form-group">
						<input type="text" class="form-control" name="usr" id="usr" placeholder="Enter username">
					</div>
					<div class="form-group">
						<input type="password" class="form-control" name="pwd" id="pwd" placeholder="Enter password">
					</div>
					
				</form>
				<button  value=" Send" class="btn btn-lg btn-success" type="submit" id="submit">Sign in</button>
			</div>
		</div>

		<footer class="footer">
			<p>&copy; 2015 Junkai Yan, Yunge Ma, Yuxuan Liu</p>
		</footer>

	</div>
</body>
</html>