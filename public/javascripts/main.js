// The leaflet instance
var map = L.map('map').setView([52.5, 13.4], 15)

var tileLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map)
map.addControl(L.control.zoom({ position: 'bottomright' }))

var base = {
	'Tiles': tileLayer
}

var overlays = {}
L.control.layers(base, overlays).addTo(map);