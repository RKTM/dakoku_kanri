/*
* A tabbed application, consisting of multiple stacks of windows associated with tabs in a tab group.
* A starting point for tab-based application with multiple top-level windows.
* Requires Titanium Mobile SDK 1.8.0+.
*
* In app.js, we generally take care of a few things:
* - Bootstrap the application with any data we need
* - Check for dependencies like device type, platform version or network connection
* - Require and open our top-level UI component
*
*/

//bootstrap and check dependencies
if(Ti.version < 1.8) {
	alert('Sorry - this application template requires Titanium Mobile SDK 1.8 or later');
} else if(Ti.Platform.osname === 'mobileweb') {
	alert('Mobile web is not yet supported by this template');
} else {
	var window = Ti.UI.createWindow({
		title : 'dakoku',
		backgroundColor : 'white'
	});

	var employee_cd = Ti.UI.createTextField({
		color : '#336699',
		top : 10,

		height : 35,
		width : 250,
		borderStyle : Ti.UI.INPUT_BORDERSTYLE_ROUNDED
	});
	window.add(employee_cd);

	var button = Ti.UI.createButton({
		height : 44,
		width : 200,
		top : 70,
		title : 'work start',
	});
	window.add(button);

	button.addEventListener('click', function() {

		var url = "http://10.0.2.2:9000/dakoku/"

		// var dakokuUtil = require('dakoku/dakoku');

		// prepare the conncetion.
		// var dakokuClient = dakokuUtil.getHttpClient();
		var dakokuClient = getHttpClient();

		dakokuClient.open("POST", url);

		var param = {
			employee_cd : employee_cd.value,
			dummy		: ""
		}

		//send the request.
		dakokuClient.send(param);

	});

	window.open();
}

function getHttpClient() {

		if(Ti.Network.online = false) {
			return;
		}

		//HTTPClientオブジェクトの生成
		return Ti.Network.createHTTPClient({
			// function called when the response data is available
			onload : function(e) {
				Ti.API.info("Received text: " + this.responseText);
				alert('success');
			},
			// function called when an error occurs, including a timeout
			onerror : function(e) {
				Ti.API.debug(e.error);
				alert('error');
			},
			timeout : 5000  /* in milliseconds */
		});
	}