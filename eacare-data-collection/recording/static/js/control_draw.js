var CONNECTED = false;
var currentTopic = "#blank"
const ROLE = "CLINICIAN"


const socket = io('http://' + document.domain + ':' + location.port);
socket.on('connect', function() {
	CONNECTED = true;
	$('div.status')[0].classList.add('connected')
});


setInterval(()=>{
	if (!CONNECTED)
		return 0;
	socket.emit('heartbeat', {role: ROLE});

}, 500)

socket.on('drawing', (data)=> {
	// console.log(data.drawing)
	for(var id in data.drawing){
		var imgObj = $(id + '_img')
		// console.log(id, imgObj, imgObj.length)
		if (imgObj.length) {
			imgObj[0].src = data.drawing[id]
		}
	}
})


socket.on("paired_status", (data) => {
	if(data.is_recording) {
		$('#stop_recording').removeClass('bg-dark bg-light text-dark').addClass('bg-danger text-light')
	} else {
		$('#stop_recording').addClass('bg-dark text-light').removeClass('bg-danger text-dark')
	}

	if(data.paired){
		$('div.status').addClass('paired')
	} else {
		$('div.status').removeClass('paired')
	}

})

socket.on('disconnect', function() {
	CONNECTED = false
	$('div.status').removeClass('connected')
	$('div.status').removeClass('paired')
});

socket.on('message', function(url) {
	window.location = url;
});

function displayNothing() {
	document.getElementById('clinician_images').style.display = 'none';
	document.getElementById('reading_q1').style.display = 'none';
	document.getElementById('reading_q2').style.display = 'none';
	return true;
}

function displayImages() {
	document.getElementById('clinician_images').style.display = 'block';
	document.getElementById('reading_q1').style.display = 'none';
	document.getElementById('reading_q2').style.display = 'none';
	return true;
}

function displayReadingQ1() {
	document.getElementById('clinician_images').style.display = 'none';
	document.getElementById('reading_q1').style.display = 'block';
	document.getElementById('reading_q2').style.display = 'none';
	return true;
}

function displayReadingQ2() {
	document.getElementById('clinician_images').style.display = 'none';
	document.getElementById('reading_q1').style.display = 'none';
	document.getElementById('reading_q2').style.display = 'block';
	return true;
}

function finish() {
	if (!confirm('Vill du avsluta inspelningen?')) return
	fetch("/stop_recording")
	.then((e) => {
		console.log(e)
	})
	displayImages()
}


window.onload = function() {


	socket.emit("broadcast_action", {role: ROLE, action: currentTopic.substring(1)})

	$('#clear').click(function(){
		socket.emit("broadcast_action", {role: ROLE, action: "clear"})
	})

	function loadHash() {
		currentTopic = (location.hash || currentTopic)
		console.log(currentTopic)
		if(currentTopic == '#blank' || currentTopic == '#reading_q1') {
			socket.emit("broadcast_action", {role: ROLE, action: "OFF"})
		} else {
			socket.emit("broadcast_action", {role: ROLE, action: "ON"})

			if (currentTopic == '#reading_q2') {
				socket.emit("broadcast_action", {role: ROLE, action: 'reading'})
			} else {
				socket.emit("broadcast_action", {role: ROLE, action: currentTopic.substring(1)})
			}
		}
		if($("a.bg-dark").length) {
			$("a.bg-dark").addClass('bg-light text-dark')
			$("a.bg-dark").removeClass('bg-dark text-light')
		}

		$("a").removeClass('bg-dark text-light').addClass('bg-light text-dark')
		$("a[href='"+currentTopic+"']").removeClass('bg-light text-dark').addClass('bg-dark text-light')
	}


	loadHash()
	window.onhashchange = loadHash;
}
