


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



function showViewshed(viewshedPolygons){
    var tempPolygon;
    var multipolygon = [];
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
    
}


// create agents will make  a js holder to keep track of previous point so that linees can be drawn
function flip(loc){
    return [loc[1],loc[0]];
}

function drawAgentLocation(data){
    for(agentIndex in data){
        var tempLastPosition = getLastLocation(data[agentIndex].id);
        if(tempLastPosition){
            // draw line to this location
            var line = [new L.LatLng(tempLastPosition[1],tempLastPosition[0]), new L.LatLng(data[agentIndex].location[1], data[agentIndex].location[0])];
            L.polyline(line,{color:'#0068CD'}).addTo(map)
        }else{
            L.circle(
                flip(data[agentIndex].location), 
                8, 
                {weight:1}).addTo(map);
        }
        setLastLocation(data[agentIndex].id, data[agentIndex].location);
    }
}

    
function isKeepRunning(){
    return true;
}



function wander(){
    //        alert('sending ' + '/wisar/q/board/highflats/'+nexty[1] + '/' + nexty[0] + '/' + direction);
     
    $.post('/wisar/q/agent/wander/', function(data){
        drawAgentLocation(data);
        if(isKeepRunning()){
            window.setTimeout(wander, 200);
        }
        
    });
    
}


var doViewshed
(function setupViewshed(){
    
    doViewshed = function(){
        var nexty = getNextMove();
        $.get('/wisar/q/board/viewshed/'+nexty[1] + '/' + nexty[0], function(data){
            //            showBoxNPointsPolygons(data);
            showViewshed(data);
        });
    }
})();

var getLastLocation
var setLastLocation
(function setupGetLastLocs(){
    
    lastLocs = {}
    
    getLastLocation = function(agentId){
        return lastLocs[agentId]
    }
    
    setLastLocation = function(agentId,loc){
        lastLocs[agentId] = loc
    }
    
})();

var getSelectedBehaviourItem;
var setSelectedBehaviourItem;

(function setupSelectBehaviourItem(){
    
    var selectedBehaviour;
    getSelectedBehaviourItem = function(){
        return selectedBehaviour;
    }

    var setSelectedBehaviour;
    setSelectedBehaviourItem = function(theBehaviour){
        selectedBehaviour = theBehaviour;
    }

})();



var getAgent
var addAgent
(function setupAgentHolder(){
    allAgents = [];
    addAgent = function(id,location){
        allAgents[id] = {'id' : id, 'location':location};
    }
    
    getAgent = function(id){
        return allAgents[id];
    }
    
    getAllAgents = function(){
        return allAgents;
    }
    
})



var selectBehaviorLink
(function setupBehaviorSelectionLink(){
    this.selectedLink = null;
    selectBehaviorLink = function(someLink){
        if(this.selectedLink != null){
            $(this.selectedLink).css('text-decoration', 'none');
        }
        this.selectedLink = someLink;
        $(this.selectedLink).css('text-decoration', 'underline');
    }
})();

function setupMap(){
    
    map = new L.Map('map', {
        center: new L.LatLng(40.34805555555265, -116.89675925930325),
        zoom: 11,
//        layers: new L.TileLayer('http://{s}.tiles.mapbox.com/v3/deriggi.map-yu0p8myf/{z}/{x}/{y}.png')
            layers: new L.TileLayer('http://{s}.tiles.mapbox.com/v3/johnderiggi.map-pcso5skt/{z}/{x}/{y}.png')
    });
    map.on('click', function(e){
   
        ltlng = e.latlng;
        lat = ltlng.lat;
        lng = ltlng.lng;
       
        
        $.post('/wisar/q/agent/createagent/'+lng + '/' + lat , function(data){
            drawAgentLocation(data);
        });
   
    });
}





