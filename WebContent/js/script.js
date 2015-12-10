var server = "http://ngnbox-env.elasticbeanstalk.com/S3Servlet";
var fileList = [];
var timeList = [];

//var fileList = ["a.txt","b.txt","d.txt"];
//var timeList = ["12/12","11/11","1/1"];

function insertRow(name) {
	var currentdate = new Date();
	var minutes = currentdate.getMinutes();
	//alert(minutes.length);
	if (minutes.length == 1) {minutes = "0"+minutes;}
	var seconds = currentdate.getSeconds();
	if (seconds.length == 1) {seconds = "0"+seconds;}
	
    var datetime = currentdate.toISOString().split('T')[0] + " "  
                + currentdate.getHours() + ":"  
                + minutes + ":"
                + seconds;
	
	var table = document.getElementById("filetable");
    var row = table.insertRow(1);
    row.id = name + "row";
    var cell0 = row.insertCell(0);
    var cell1 = row.insertCell(1);
    var cell2 = row.insertCell(2);
    
    cell0.onclick = function () {
    	//Cell click event handler
//    	console.log("download "+name);
    	download(name);
    };
    cell0.innerHTML = "<a href=\"#\">"+name+"</a>"; // onclick event handler
    
    cell1.innerHTML = datetime;
    var deleteButton = document.createElement("input");
    deleteButton.setAttribute("type", "submit");
    deleteButton.setAttribute("class", "btn btn-xs btn-danger");
    deleteButton.setAttribute("id", name);
    deleteButton.setAttribute("value", "Delete");
    deleteButton.onclick = function() {
    	var tmp = this.id;
    	deleteFile(tmp);
    };
    cell2.appendChild(deleteButton);
}

function dummy(id) {
	alert(id);
}

function deleteFile(fileName) {
	var json = {
			"action": "delete",
			"username": localStorage.username,
			"file": fileName
	};
	$.get(server, json, function(response) {
		if (response == "success") {
			deleteRow(fileName);
		} else {
			alert("delete failed, try again later.");
		}
//		console.log("delete success");
	})
	.fail(function() {
		alert("delete fail");
	});
}

function deleteRow(buttonId) {
	var rowId = buttonId + "row";
	var row = document.getElementById(rowId);
	row.parentNode.removeChild(row);
//	console.log("row with id = "+rowId+" deleted");
}

function download(fileName) {
	var json = {
			"action": "download",
			"username": localStorage.username,
			"file": fileName
	};
	$.get(server, json, function(response) {
//		console.log(response);
		window.location = response; // Jump to the url to download file
	})
	.fail(function(e) {
		alert(e + "\n Download failed");
	});
}

function getDataFromServer() {
//	console.log("in getDatFromServer()");
	
}

function init() {
	if (localStorage.username == null) {
		alert("log in first!");
		window.location = "login.jsp";
		return;
	}
		
	var usrJson = {
			"action": "listfiles",
			"username": localStorage.username
	};
	$.get(server, usrJson, function (response) {
		if (response != "null") {
//			console.log(response);
			// Initialize page content
			var s = response.split("\n");
//			console.log(s.length);
			for (var i = 0; i < s.length; i ++) {
				var obj = JSON.parse(s[i]);
				fileList.push(obj.file);
				timeList.push(obj.time);
//				console.log(obj.file);
//				console.log(obj.time);
			}
			
//			console.log("init begin");
//			console.log("fileList size: "+fileList.length);
//			console.log(localStorage.username);
			var table = document.getElementById("filetable");
			for (var i = 0; i < fileList.length; i++) {
//				console.log("inside loop!");
				var row = table.insertRow(1);
				row.id = fileList[i] + "row";
				var cell0 = row.insertCell(0);
				
				cell0.id = "cell"+fileList[i];
				cell0.onclick = function () {
			    	//Cell click event handler
					var file = this.id.substring(4);
//			    	console.log("download "+file);
			    	download(file);
			    };
			    cell0.innerHTML = "<a href=\"#\">"+fileList[i]+"</a>"; // onclick event handler
			    
				var cell1 = row.insertCell(1);
				var cell2 = row.insertCell(2);
				cell1.innerHTML = timeList[i]; // modified time
				
				var button = document.createElement("input");
			    button.setAttribute("type", "submit");
			    button.setAttribute("class", "btn btn-xs btn-danger");
			    button.setAttribute("id", fileList[i]);
			    button.setAttribute("value", "Delete");
			    
			    button.onclick = function() {
			    	var tmp = this.id;
			    	deleteFile(tmp);
			    };
			    cell2.appendChild(button);
			}
//			localStorage.clear(); // clear localStorage
//			console.log("init success");
		}
	})
	.fail(function() {
		alert("Fetching data from server failed, try again later");
	});
	
}

function signout() {
//	console.log("signing out");
	localStorage.clear(); // TO Verify
	window.location = "login.jsp";
}

function redirect() {
//	console.log('redirect');
    window.location="dropbox.jsp";
}