<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dropbox</title>
<link rel="stylesheet" href="styles/styles.css">

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

<!-- jQuery library -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

<script src="js/script.js"></script>

<script type="text/javascript">

	$(document)
			.ready(
					function() {
						(function() {
							$("#upload-button")
									.click(
											function() {
												$
														.ajax(
																{
																	type : "POST",
																	url : server,
																	data : new FormData(
																			document
																					.getElementById("myform")),
																	processData : false,
																	contentType : false,
																	dataType : 'text'
																})
														.done(
																function(data) {
																	if (data == "Existed") {
																		alert("File already exist!");
																	} else if (data == "error") {
																		alert("You need to select a file!");
																	} else {
																		insertRow(data);
																	}
																	document
																			.getElementById(
																					"myform")
																			.reset();
																	document
																			.getElementById("username").value = localStorage.username;

																})
														.fail(function(e) {
															alert("fail");
															//console.log(e);
														})
														.always(
																function() {
																	//console.log('always');
																});
											});

							// Get username from localStorage and fill in the form
							document.getElementById("username").value = localStorage.username;

							init();
							/* insertRow("aaaaa");
							insertRow("ff");
							insertRow("ddd"); */
							//deleteFile("testFile");
						})();
					});
</script>
</head>




<body>
	<nav class="navbar navbar-default">
		<div class="navbar-header">
			<a class="navbar-brand" href="#"> <span><img
					src="images/box.svg" /></span> Box
			</a>
		</div>
		<button type="button" style="float:right; margin-right:1cm;" onclick="signout()" class="btn btn-primary btn-sm navbar-btn">Sign out</button>
	</nav>

	<br>
	<br>
	<!-- <h1>Dropbox</h1> -->

	<!-- http://160.39.226.242:8080/test/myServlet -->

	<div id="dropbox">
		<button value=" Send" class="btn btn-xs btn-success" type="submit"
			id="upload-button">Upload</button>
		<div id="formdiv">
			<center>
				<form action="" style="" method="post" name="myform" id="myform"
					enctype="multipart/form-data">
					<div class="form-group">
						<input type="text" id="username" name="username"
							style="display: none" /> <input type="file" id="filename"
							name="filename">

					</div>
				</form>
			</center>
		</div>

		<br>

		<table id="filetable">
			<tr>
				<th>Name</th>
				<th>Modified</th>
				<th>Action</th>
			</tr>
		</table>

	</div>
</body>
</html>