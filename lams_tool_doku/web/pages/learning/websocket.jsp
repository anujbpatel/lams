<script type="text/javascript">
	var dokuWebsocketInitTime = Date.now(),
		dokuWebsocket = new WebSocket('<lams:WebAppURL />'.replace('http', 'ws') 
					+ 'learningWebsocket?toolContentID=' + ${sessionMap.toolContentID}),
		dokuWebsocketPingTimeout = null,
		dokuWebsocketPingFunc = null;
	
	dokuWebsocket.onclose = function(){
		// react only on abnormal close
		if (e.code === 1006 &&
			Date.now() - dokuWebsocketInitTime > 1000) {
			location.reload();
		}
	};
	
	dokuWebsocketPingFunc = function(skipPing){
		if (dokuWebsocket.readyState == dokuWebsocket.CLOSING 
				|| dokuWebsocket.readyState == dokuWebsocket.CLOSED){
			return;
		}
		
		// check and ping every 3 minutes
		dokuWebsocketPingTimeout = setTimeout(dokuWebsocketPingFunc, 3*60*1000);
		// initial set up does not send ping
		if (!skipPing) {
			dokuWebsocket.send("ping");
		}
	};
	
	// set up timer for the first time
	dokuWebsocketPingFunc(true);
	
	// run when the server pushes new reports and vote statistics
	dokuWebsocket.onmessage = function(e) {
		// create JSON object
		var input = JSON.parse(e.data);
		
		if (input.pageRefresh) {
			location.reload();
			return;
		}
		
		// reset ping timer
		clearTimeout(dokuWebsocketPingTimeout);
		dokuWebsocketPingFunc(true);
		
		// Monitor has added one more minute to the time limit. All learners will need
    	// to add +1 minute to their countdown counters.
		if (input.addTime) {
			//reload page in order to allow editing the pad again
			if (!$('#countdown').length) {
				location.reload();
			}
			
	    	var times = $("#countdown").countdown('getTimes'),
	    		secondsLeft = times[4]*3600 + times[5]*60 + times[6] + input.addTime*60;
	    	$('#countdown').countdown('option', "until", '+' + secondsLeft + 'S');
			
			return;
		}
	};
	
	
	$(document).ready(function(){
		// command websocket stuff for refreshing
		// trigger is an unique ID of page and action that command websocket code in Page.tag recognises
		commandWebsocketHookTrigger = 'gallery-walk-refresh-${sessionMap.toolContentID}';
		// if the trigger is recognised, the following action occurs
		commandWebsocketHook = function() {
			location.reload();
		};
	});
</script>