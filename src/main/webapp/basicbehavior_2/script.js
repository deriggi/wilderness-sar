


var map;
var uav;
var target = {

    lat : 40.34805555555265,
    lon : -116.89675925930325 
	
//boulder
//lat : 40.0360,
//lon : -105.2315
	
//lat : 39.88,
//lon : -109.04
}
var targetMarker;

var getBox;
var setBox;
(function setUpAccess(){
    this.box = null;
    getBox = function(){
        return this.box;
    }
    setBox = function(theBox){
        this.box = theBox;
    }
})();

var isSeek;
var setSeek;
(function isSeekOrFlee(){
    this.seek = true;
    isSeek = function(){
        return this.seek;
    }
	
    setSeek = function(yesOrNo){
        this.seek = yesOrNo;
    }
	
})();




var setNextMove;
var getNextMove;
(function setUpNextMove(){
    this.nextMove = null;
    getNextMove = function(){
        return this.nextMove;
    }
    setNextMove = function(nm){
        this.nextMove = nm;
    }
    
})();

var setDirection, getDirection;
(function setupSetDirection(){
    this.direction = null;
    getDirection = function(){
        return this.direction;
    }
    setDirection = function(nd){
        this.direction= nd;
    }
})();


function setupTargetMarker(){
    targetMarker = L.marker([target.lat, target.lon], {
        draggable:true
    } ).addTo(map);
    targetMarker.addEventListener('dragend',function(){
        target.lat = targetMarker.getLatLng().lat;
        target.lon = targetMarker.getLatLng().lng;
	
    });
}



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

function getMaxHood(){
    
    $.get('/wisar/q/board/maxhood', function(data){
        showBoxNPoints(data);
    });
    
}


function showBoxNPoints(data){
    
    bbox = data.bbox;
    theGon = L.polygon(bbox);
    theGon.addTo(map);
    
    if( getBox() != null){
        map.removeLayer(getBox())
    }
    
    setBox(theGon);
    ridge = data.ridgePoints;
        
    for ( ridgePoint in ridge){
        L.marker(ridge[ridgePoint]).addTo(map);
    }
    
}

function makeLatLngs(coords){
    linePoints = [];
    for( coord in coords){
        linePoints.push(new L.LatLng(coords[coord][0],coords[coord][1])); 
    }
    return linePoints;
}

function showBoxNPointsPolygons(data){
    if(data.bbox){
        bbox = data.bbox;
    
        if (theGon != null){
            theGon = L.polygon(bbox, {
                stroke:false
            });
            theGon.addTo(map);
        }
    }
    if( getBox() != null){
        map.removeLayer(getBox())
    }
    
    if(data.forceVector != null){
        forceVector = data.forceVector;
        latlngs = makeLatLngs(forceVector);
        L.polyline(latlngs, {
            color:'red', 
            weight:1
        }).addTo(map);
    }
    
    if (data.nextMove != null){
        L.circle(data.nextMove, 5, {
            weight:1
        }).addTo(map);
        setNextMove(data.nextMove)
    }
    
    setBox(theGon);
    if(data.ridgePoints && data.ridgePoints != null){
        ridge = data.ridgePoints;
        cellWidth = 0.0000926
        count = 0;
        for ( ridgePoint in ridge){
            weeBox = [];
            boundsSw = ridge[ridgePoint];
            boundsNe = [];
            lat = ridge[ridgePoint][0];
            lon = ridge[ridgePoint][1];
        
            boundsNe[0] = lat + cellWidth;
            boundsNe[1] = lon + cellWidth;
        
            weeBox.push(boundsSw);
            weeBox.push(boundsNe);
        
            map.addLayer(L.rectangle(weeBox, {
                stroke:false, 
                fillColor:'#f90000', 
                opacity:1
            }));
        
        }    
    }
    
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

var runAgent
(function setupRunAgent(){
    runAgent = function(startLon, startLat, startDirection){
        $.get('/wisar/q/board/agentrun/'+startLon + '/' + startLat + '/' + startDirection, function(hoods){
            setHoods(hoods);
            play();
        //            for(hoodIndex in hoods){
        //                showBoxNPointsPolygons(hoods[hoodIndex]);
        //            }
        });
    }
})();

var setHoods
var getNextHood
var getHood
var play;
(function setupHoodsHolder(){
    this.intervalId = 0;
    this.index = 0;
    this.hoods = null;
    setHoods = function(someHoods){
        this.hoods = someHoods;
        this.index = 0;
    }
    getHood = function(i){
        return this.hoods[i];
    }
    
    getNextHood = function(){
        return this.hoods[this.index++]
    }
    
    hasNext = function(){
        var keepGoing = this.index < this.hoods.length;
        if(!keepGoing){
            clearInterval(this.intervalId);
            this.hoods = []
            this.hoods = null;
        }
    }
    
    play = function(){
        this.intervalId =  window.setInterval(showOne,200);
    }
    
    showOne = function(){
        showBoxNPointsPolygons(getNextHood());
        hasNext();
    }
    
})();


var advanceThings
(function setupAdavanceThings(){
    // get next point, then advance this shit
    advanceThings = function(direction){
        //        alert('sending ' + '/wisar/q/board/highflats/'+nexty[1] + '/' + nexty[0] + '/' + direction);
        var nexty = getNextMove();
        $.get('/wisar/q/board/highflats/'+nexty[1] + '/' + nexty[0] + '/' + direction, function(data){
            showBoxNPointsPolygons(data);
        });
    }
})();

var findMostWalkable
(function setupFindMostWalkable(){
    // get next point, then advance this shit
    findMostWalkable = function(){
        //        alert('sending ' + '/wisar/q/board/highflats/'+nexty[1] + '/' + nexty[0] + '/' + direction);
        var nexty = getNextMove();
        $.get('/wisar/q/board/testfor/'+nexty[1] + '/' + nexty[0], function(data){
            showBoxNPointsPolygons(data);
        });
    }
})();

var radialSearch
(function setupRadialSearch(){
    
    radialSearch = function(){
        var nexty = getNextMove();
        $.get('/wisar/q/board/radialsearch/'+nexty[1] + '/' + nexty[0], function(data){
            showBoxNPointsPolygons(data);
        });
    }
})();

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



var getAction;
var setAction;
(function setupGetAction(){
    this.action = null;
    getAction = function(){
        return this.getAction;
    }
    setAction = function(actionFunction){
        setAction = actionFunction;
    }
})();

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
        direction = getDirection();
        if(direction == null){
            direction=  'high';
        }
        
        //** use advancethings()
        $.get('/wisar/q/board/highflats/'+lng + '/' + lat + '/' + direction, function(data){
            showBoxNPointsPolygons(data)
        });
   
    });
}



