function $$(sel){
	return document.querySelector(sel)
}
function $$$(sel){
	return document.querySelector(sel + " td:nth-of-type(2)>div")
}

const socket = io('http://' + document.domain + ':' + location.port);
socket.on('connect', function() {
	$$$("#connected").className = 'ok'
});

socket.on('disconnect', function() {
	$$$("#connected").className = 'error'
});

const ROLE = "STATUS_PAGE"


function start_calibrate(){
	fetch("/start_calibrate_tobii")
	.then((e) => {
		console.log(e)
	})
}


$(function() {
	$('#toggle_start_stop').on('click', function() {
		console.log($(this).data('state'))
		var state = $('#toggle_start_stop').data('state')
		if (state == 'start') {
			fetch("/start_recording")
			.then((e) => {
				console.log(e)
				$('#toggle_start_stop').data('state', 'stop')
				socket.emit('status');
			})
		} else if (state == 'stop') {
			fetch("/stop_recording")
			.then((e) => {
				console.log(e)
				$('#toggle_start_stop').data('state', 'start')
				socket.emit('status');
			})
		}
	});
})


window.onload = function() {
	socket.emit('status');
	setInterval(()=>{
		socket.emit('status');
		socket.emit('get_topic_status');
	}, 2000)

	socket.on("status", (data) => {
		$$$("#clinician_draw").className = data["CLINICIAN"]? "ok": "error"
		$$$("#patient_draw").className = data["PATIENT"]? "ok": "error"
		if(data['is_recording']) {
			$('#toggle_start_stop').html('Stop recording').data('state', 'stop').removeClass('btn-success').addClass('btn-danger')
		} else {
			$('#toggle_start_stop').html('Start recording').data('state', 'start').addClass('btn-success').removeClass('btn-danger')
		}

	})

	socket.on("IphoneINFO", (data) => {
		var topic = data.topic
		var _topic = topic.split(".").join("_")


		if(!$$("#IphoneInfo #" + _topic)){
			// Create
			var tr = document.createElement('tr');
			tr.id = _topic;

			var td = document.createElement("td")
			tr.appendChild(td)
			td = document.createElement("td")
			tr.appendChild(td)
			td = document.createElement("td")
			tr.appendChild(td)
			td = document.createElement("td")
			tr.appendChild(td)
			td = document.createElement("td")
			tr.appendChild(td)
			td = document.createElement("td")
			tr.appendChild(td)
			td = document.createElement("td")
			tr.appendChild(td)
			// td = document.createElement("td")
			// tr.appendChild(td)	

			$$("#IphoneInfo").appendChild(tr)
		}

		var tds = $("#IphoneInfo #" + _topic+">td")
		if(tds.length <1)
			return 0
		tds[0].innerHTML = topic
		// {: false, : false, : 3936354304, : 59.960600992123055, : 32833536, flirBattery: -1,}

		var connectedColor = data.msg['isConnected'] ? 'success' : 'danger'
		var recordingColor = data.msg['isRecording'] ? 'success' : 'danger'

		// tds[1].innerHTML = '<span class="badge badge-' + connectedColor + '">' + data.msg['isConnected'] + '</span>'
		tds[1].innerHTML = '<span class="badge badge-' + recordingColor + '">' + data.msg['isRecording'] + '</span>'
		tds[2].innerHTML = Math.round(data.msg['cpuUsage'])
		tds[3].innerHTML = Math.round(data.msg['memoryUsageUsed']/data.msg['memoryUsageTotal']) * 100 + "% (" + (Math.round(data.msg['memoryUsageUsed']/1000000)/1000) + ")"
		tds[4].innerHTML = Math.round(data.msg['fps'])
		tds[5].innerHTML = data.msg['flirIsConnected']
		tds[6].innerHTML = Math.round(data.msg['flirBattery'])

	})


		// <tr id="watch">
		// 	<td>Smart watch</td>
		// 	<td><div></div></td>
		// 	<td></td>
		// </tr>

	socket.on("get_topic_status", (STATUS) => {
		for(var topic in STATUS){
			var _topic = topic.split(".").join("_")
			if(!$$("table#STATUS #" + _topic)){
				// Add row for that topic
				var tr = document.createElement('tr');
				tr.id = _topic;

				var td = document.createElement("td")
				td.innerHTML = topic
				tr.appendChild(td)

				td = document.createElement("td")
				td.appendChild( document.createElement("div"))
				tr.appendChild(td)


				// tr.appendChild( document.createElement("td"))

				$$('table#STATUS').appendChild(tr)

				continue

			}
			$$$("table#STATUS #" + _topic).className = STATUS[topic].connected? "ok": "error"
		}

	})

	function showVideo(div_id) {
		return function(data) {
			return document.getElementById(div_id).src = 'data:image/jpeg;base64, ' + btoa(String.fromCharCode.apply(null, new Uint8Array(data)));
		}

	}

	socket.on("video_clinician", showVideo('clinician'));
	socket.on("video_patient", showVideo('patient'));
	socket.on("overview_camera", showVideo('webcam_img'));
	socket.on("flir_video", showVideo('flir_video'));

}
