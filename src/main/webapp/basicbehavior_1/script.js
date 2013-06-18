


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

function Uav(){
    this.circle =  null;
    this.lat = 40.00;
    this.lon = -105.50;
	
    this.dlat = 0.0005;
    this.dlon = 0.0005;
			
}

function setupUav(){
    var circle = makeCircle(40,-105.50,300);
    circle.addTo(map);
    uav = new Uav();
    uav.setCircle(circle);
    uav.seek(target);
    setSeek(true);
}

function setupTargetMarker(){
    targetMarker = L.marker([target.lat, target.lon], {
        draggable:true
    } ).addTo(map);
    targetMarker.addEventListener('dragend',function(){
        target.lat = targetMarker.getLatLng().lat;
        target.lon = targetMarker.getLatLng().lng;
	
    });
}

Uav.prototype.nextLat = function() {
    //this.lat  = this.lat + this.dlat;
    return this.lat;
}

Uav.prototype.nextLon = function() {
    //this.lon  = this.lon + this.dlon;
    return this.lon;
}

Uav.prototype.setCircle = function(c){
    this.circle = c;
}

Uav.prototype.getCircle = function(){
    return this.circle;
}

Uav.prototype.seek = function(targetThing, isFlee){
    var bigDlat = targetThing.lat - this.lat;
    var bigDlon = targetThing.lon - this.lon;
	
    // a bit more north south?
    if(Math.floor(Math.random()*5) == 1){
        bigDlat += 0.05;
    }
	
	
    if(!isSeek()){
        bigDlat *= -1;
        bigDlon *= -1;		
    }
	
	
    var magnitude = Math.sqrt( Math.pow( bigDlat, 2) +  Math.pow( bigDlon, 2) );
	
    // yuck, kind of a random scale but makes faster when farther away, slow when close
	
    // gold!
    //this.dlat = ( bigDlat/  magnitude  ) * 1/500;
    //this.dlon = ( bigDlon/ magnitude  ) * 1/500;	
	
    this.dlat = ( bigDlat*1/10 ) ;
    this.dlon = ( bigDlon*1/10)  ;
	
    //this.dlat = this.dlat*(1/1000);
    //this.dlon = this.dlon*(1/1000);
	
    this.lat = this.lat  + this.dlat;
    this.lon = this.lon + this.dlon;
	
    $('#velocity').text(this.dlat + ' ' + this.dlon);
    $('#dlat').text(bigDlat + ' ' + bigDlon);
	
	
	
	
}

Uav.prototype.getVelocityMagnitude = function(){
    return Math.sqrt( Math.pow( this.dlat, 2), Math.pow( this.dlon, 2) );
}



	


function makeCircle(lat,lng, rad){

    var c = L.circle([lat, lng], rad, {
        color: '#37ABC8',
        fillColor: '#37ABC8',
        fillOpacity: 0.5
    });
	
    return c;
}

function moveCircle(){
    map.removeLayer(uav.getCircle());
    uav.setCircle(null);
    uav.seek(target);
    circle = makeCircle(uav.nextLat(),uav.nextLon(),300);
    circle.addTo(map);
    uav.setCircle(circle);
	
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
    bbox = data.bbox;
    
    if (theGon != null){
        theGon = L.polygon(bbox, {
            stroke:false
        });
        theGon.addTo(map);
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

function setupMap(){
    map = new L.Map('map', {
        center: new L.LatLng(40.34805555555265, -116.89675925930325),
        zoom: 11,
        layers: new L.TileLayer('http://{s}.tiles.mapbox.com/v3/deriggi.map-g7ztumv6/{z}/{x}/{y}.png')
    //https://tiles.mapbox.com/v3/deriggi.map-g7ztumv6/9/250/159.png
    });
    map.on('click', function(e){
   
        ltlng = e.latlng;
        lat = ltlng.lat;
        lng = ltlng.lng;
        direction = getDirection();
        if(direction == null){
            direction=  'high';
        }
        
        // use advancethings()
        $.get('/wisar/q/board/highflats/'+lng + '/' + lat + '/' + direction, function(data){
            showBoxNPointsPolygons(data);
        });
   
    });
}



