var offset = 0;
var sync = false;
var CONNECTED = false;
var PAIRED = false;

const ROLE = "PATIENT"
var MODE = "PEN"


const socket = io('http://' + document.domain + ':' + location.port);
socket.on('connect', function() {
	CONNECTED = true;

	socket.emit('time_sync', {client_ping: Date.now()});

	setTimeout(()=>{
		socket.emit('time_sync', {client_ping: Date.now()});
	}, 200)

});

setInterval(()=>{
	if (!CONNECTED)
		return 0;
	socket.emit('heartbeat', {role: ROLE});
}, 500)

socket.on("paired_status", (data) => {
	if(data.paired == PAIRED)
		return 0
	PAIRED = data.paired
	if(PAIRED)
		$('div.status')[0].classList.add('paired')
	else
		$('div.status')[0].classList.remove('paired')

})


socket.on('time_sync', (data)=>{
	var d1 = data.client_ping_received - data.client_ping

	if(!offset){
		offset = d1
		return 0
	}
	offset = 0.5*offset + 0.5*d1
	$('div.status')[0].classList.add('connected')
	sync = true
})

socket.on('disconnect', function() {
	CONNECTED = false
	$('div.status')[0].classList.remove('connected')
	$('div.status')[0].classList.remove('paired')
});

socket.on('message', function(url) {
	window.location = url;
});


var pressure = 0
Pressure.set("#canvas", {
	change: function(force, event){
		pressure = force
	}
})


paper.install(window);

window.onload = function() {
	var DRAWING = {}

	function toggle(active){
		if(active == "ON")
			$("#cover")[0].style.display = "none"
		else
			$("#cover")[0].style.display = "block"
	}

	toggle("OFF")
	socket.on("control_action", function(data){
		if(data.action == "CALIBRATE_TOBII")
			window.location	= data.location
		if(data.from != "CLINICIAN" && data.from != "FURHAT")
			return 0
		if(data.action == "clear"){
			loadHash()
			return 0
		}
		if(data.action == "ON" || data.action == "OFF")
			return toggle(data.action)
		window.location.hash = "#" + data.action
	})

	let currentTopic = "#free"

	// Load image
	$('img#image_template')[0].src = imageUrl
	$('img#path_template')[0].src = pathImageUrl
	$('img#threeimages_template')[0].src = threeImagesUrl;
	$('img#cookie_template')[0].src = cookieImageUrl;
	$('img#reading_template')[0].src = textImageUrl;





	// Get a reference to the canvas object
	var canvas = document.getElementById('canvas');

	const H = canvas.offsetHeight
	const W = canvas.offsetWidth

	// Create an empty project and a view for the canvas:
	paper.setup(canvas);

	var size = new Size(W, H);

	view.viewSize = size

	var curPath = null;


	view.onMouseDrag = function (event) {
		curPath.add(event.point);
		curPath.smooth()
		socket.emit('draw_data', {
			time: Date.now() + offset,
			x: event.point.x,
			y: event.point.y,
			pressure: pressure,
			action: "drawing",
			mode: MODE,
			type: currentTopic,
		});
	}
	view.onMouseDown = function (event) {
		curPath = new Path();
		curPath.strokeColor = "red"
		curPath.strokeWidth = 5
		if(MODE == "ERASER"){
			curPath.blendMode = "destination-out"

			curPath.strokeWidth = 15
		}
		socket.emit('draw_data', {
			time: Date.now() + offset,
			x: event.point.x,
			y: event.point.y,
			pressure: pressure,
			action: "start_drawing",
			mode: MODE,
			type: currentTopic,
		});
	}
	view.onMouseUp = function (event) {
		// curPath.simplify()
		socket.emit('draw_data', {
			time: Date.now() + offset,
			x: event.point.x,
			y: event.point.y,
			pressure: pressure,
			action: "end_drawing",
			mode: MODE,
			type: currentTopic,
		});
	}


	$('#clear').click(loadHash)
	$('#pen').click((e)=>{
		MODE = "PEN"
		$("#pen")[0].style.background = "green"
		$("#eraser")[0].style.background = "red"
	})
	$('#eraser').click((e)=>{
		MODE = "ERASER"

		$("#eraser")[0].style.background = "green"
		$("#pen")[0].style.background = "red"
	})
	view.draw();

	function render(tempate, scale, xShift, yShift) {
		var image = new Raster(tempate)
		image.onLoad = function(){
			image.scale(scale)
			image.position = new Point(xShift+image.width*scale/2, yShift+image.height*scale/2)
		}
	}

	function loadHash() {
		// Load
		//	Export drawing
		if (currentTopic == '#free' || currentTopic == '#path' || currentTopic == '#image') {
			DRAWING[currentTopic] = canvas.toDataURL()
			socket.emit('drawing', {drawing: DRAWING});
		}

		currentTopic = (location.hash || currentTopic)
		project.clear()
		var DrawLayer = new Layer();
		var FixedLayer = new Layer();
		FixedLayer.activate()

		switch(currentTopic) {
			case '#free':
				$('#bottom_row').show()
			break
			case '#path':
				$('#bottom_row').show()
				render('path_template', 1, 40, 40)
			break
			case '#image':
				$('#bottom_row').show()
				render('image_template', 0.4, 0, 0)
			break
			case '#three_images':
				$('#bottom_row').hide()
				render('threeimages_template', 1.1, 0, 150)
			break
			case '#cookie':
				$('#bottom_row').hide()
				render('cookie_template', 0.6, 50, 0)
			break
			case '#reading':
				$('#bottom_row').hide()
				render('reading_template', 0.88, 0, 0)
			break
		}

		DrawLayer.activate()
	}


// create a dataUrl from the canvas
// var dataURL= canvas.toDataURL();

// // use jQuery to POST the dataUrl to you php server
// $.ajax({
//     type: "POST",
//     url: "upload.php",
//     data: {image: dataURL}
// }).done(function( respond ) {
//     // Done...report success or failure
//     // You will get back the temp file name
//     // or "Unable to save this image."
//     console.log(respond);
// });



	loadHash()
	window.onhashchange = loadHash;
}
