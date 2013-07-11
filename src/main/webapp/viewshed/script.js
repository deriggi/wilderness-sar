


var map;



function getBoard(){
    
    $.post('/wisar/q/board', function(data){
        nwlon = data.nwlon;
        nwlat = data.nwlat;
        
        selon = data.selon;
        selat = data.selat;
        
        maxlat = data.maxlat;
        maxlon = data.maxlon;
        
        minlat = data.minlat;
        minlon = data.minlon;
        
        L.marker([nwlat, nwlon], {
            draggable:false
        } ).addTo(map);
        L.marker([selat, selon], {
            draggable:false
        } ).addTo(map);
        L.marker([maxlat, maxlon], {
            draggable:false
        } ).addTo(map);
        L.marker([minlat, minlon], {
            draggable:false
        } ).addTo(map);
        
    });
    
}





function makeLatLngs(coords){
    linePoints = [];
    for( coord in coords){
        linePoints.push(new L.LatLng(coords[coord][0],coords[coord][1])); 
    }
    return linePoints;
}

function flip(lonLat){
    return [lonLat[1],lonLat[0]];
}

function drawAgentLocation(data){
    for(agentIndex in data)
        if (data[agentIndex].location != null){
            L.circle(flip(data[agentIndex].location), 8, {
                weight:1
            }).addTo(map);
        }
}


ViewshedContainer = (function setupPushViewshed(){
    var sheds = [];
    
    return{

        pushShed : function(someShed){
            sheds.push(someShed)
        },

        clearSheds : function(){
            for(var shedIndex in sheds){
                map.removeLayer(sheds[shedIndex]);
            }
            sheds.length = 0;
        }
    }

})();


function showViewshed(viewshedPolygons){
    var tempPolygon;
    var multipolygon = [];
    
    ViewshedContainer.clearSheds();

    for(polygonIndex in viewshedPolygons){
        // the polygon
        tempPolygon = viewshedPolygons[polygonIndex]
        var ltlngs = [];
        for(vertexIndex in tempPolygon){
            // create latLngs
            lon = tempPolygon[vertexIndex][0];
            lat = tempPolygon[vertexIndex][1];
            ltlngs.push(new L.LatLng(lat,lon));
        }
        //        ltlngs.pop();
        multipolygon.push(ltlngs);
    // have ltlngs now
    }
    
    visibleGon = new L.MultiPolygon(multipolygon, {
        stroke:false
    });
    
    map.addLayer(visibleGon);

    ViewshedContainer.pushShed(visibleGon);
    
}









var doViewshed
(function setupViewshed(){
    
    doViewshed = function(lon, lat){
        
        $.get('/wisar/q/board/viewshed/'+lon + '/' + lat, function(data){
            
            showViewshed(data);
        });
    }
})();



function setupMap(){
    alert('called') ;   
    map = new L.Map('map', {
        center: new L.LatLng(40.34805555555265, -116.89675925930325),
        zoom: 15,
        //        layers: new L.TileLayer('http://{s}.tiles.mapbox.com/v3/deriggi.map-yu0p8myf/{z}/{x}/{y}.png')
        layers: new L.TileLayer('http://{s}.tiles.mapbox.com/v3/johnderiggi.map-pcso5skt/{z}/{x}/{y}.png'),
        minZoom: 14
    });
    
    alert('have map');

    map.on('click', function(e){
   
        ltlng = e.latlng;
        lat = ltlng.lat;
        lng = ltlng.lng;
           
        doViewshed(lng, lat);
   
    });
    alert('done');
}



