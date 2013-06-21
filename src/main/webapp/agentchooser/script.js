


var map;


var setGameBBox;
var displayBBox;
(function setupBBoxAccess(){
    var nwlon, nwlat, selon, selat;

    setGameBBox = function(nwlon, nwlat, selon, selat){
        this.nwlon = nwlon;
        this.nwlat = nwlat;
        this.selon = selon;
        this.selat = selat;

    }

    displayBBox = function(theMap){
        bounds = [[this.nwlat, this.nwlon], [this.selat, this.selon]];
        L.rectangle(bounds, {color:'#3498DB', weight:0}).addTo(theMap)
        theMap.fitBounds(bounds);
    }



})();

function getBoard(){
    
    $.post('/wisar/q/board', function(data){
        nwlon = data.nwlon;
        nwlat = data.nwlat;
        
        selon = data.selon;
        selat = data.selat;

        setGameBBox(nwlon, nwlat, selon, selat);
        
        
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
   
       handleMapClick(e);
   
    });

    fullscreenControl = new L.Control.Fullscreen();
    fullscreenControl.addTo(map);

}

function getThemBehaviours(){
    var ul = makeList();

    $.getJSON('/wisar/q/behaviour/list', function(data){
        
        for(var dataIndex in data ){
            $(ul).append(makeListItem(data[dataIndex].name));
        }


        $('#page_2').append(ul);

    });
}

function makeAgentTypes(){
    var ul = makeList();

    $(ul).append(makeListItem('UAV AGENT'));
    $(ul).append(makeListItem('LOST PERSON AGENT'));

    $('#page_1').append(ul);
    
}

function makeList(theStyle){
    if(!theStyle){
        theStyle = 'behaviourlist'
    }
    var ul = document.createElement('ul');
    $(ul).addClass(theStyle);
    return ul;
}





function makeListItem(data, theStyle){
    if(!theStyle){
        theStyle = 'behaviouritem'
    }
    
    var li = $(document.createElement('li')).addClass(theStyle).append(data);
    giveItemClickyBehaviour(li);

    return li;
}

function giveItemClickyBehaviour(listItem){
    
    $(listItem).click(function(){

        var previous = getSelectedBehaviourItem();
        if(previous){
            $(previous).attr('lebronned','');
            $(previous).css('background-color','#34495E');
            if(previous.is($(this))){
                return;
            }    
        }

        var theChosen = $(this).attr('lebronned');
        if(theChosen != 'yes'){ 
            
            $(this).css('background-color','#1ABC9C');
            $(this).attr('lebronned', 'yes');
            setSelectedBehaviourItem($(this));
            
        }
        else{
            $(this).css('background-color','#34495E');
            $(this).attr('lebronned', '');   
        }

    });
}

var handleMapClick;
var setAction;
(function setupMapClickHandler(){
    
    var isCreateAgentMode = false;
    var isCreateAgentMode = false;

    var actionHolder = {};

    doNothingAction = function(e){}

    createAgentAction = function(e){
        ltlng = e.latlng;
        lat = ltlng.lat;
        lng = ltlng.lng;
   
        $.post('/wisar/q/agent/createagent/'+lng + '/' + lat, function(data){
            drawAgentLocation(data);
        });    
    }

    setLocationAction = function(e) {
        ltlng = e.latlng;
        lat = ltlng.lat;
        lng = ltlng.lng;
        setAgentStartLoc(lng, lat);
    }

    setAction = function(someKey){
        handleMapClick = actionHolder[someKey];
    }

    actionHolder['nothing'] = doNothingAction;
    actionHolder['create'] = createAgentAction;
    actionHolder['set'] = setLocationAction;

    handleMapClick = doNothingAction;

})();


var setAgentBehaviour
var setAgentStartLoc
var setAgentstartVelocity
var getConstructedAgent
var resetAgentCreator
(function setupAgentCreator(){
    var behaviour;
    var startLon;
    var startLat;
    var velocity;

    setAgentBehaviour = function(b){
        this.behaviour =b;
    }

    setAgentStartLoc = function(lon, lat){
        this.startLon = lon;
        this.startLat = lat;
    }

    setAgentVelocity = function(v){
        this.velocity = v;
    }

    getConstructedAgent = function(){
        return new Agent(this.behaviour, this.startLon, this.startLat, this.velocity);
    }

    resetAgentCreator = function(){
        this.behaviour = null;
        this.startLon = null;
        this.startLat = null;
        this.velocity = null;
    }

})();


function VectorAgent(behaviour, startLon, startLat, velocity){
    this.behaviour = behaviour;
    this.startLon = startLon;
    this.startLat = startLat;
    this.velocity = velocity;
}





var getPageNumber;
var incrementPageNumber;
var decrementPageNumber;
(function setupPageNumberMachine(){
    var pageNumber = 0;
    incrementPageNumber = function(){
        pageNumber++;
    }

    decrementPageNumber = function(){
        if(pageNumber == 0){
            return;
        }
        pageNumber--;
    }

    getPageNumber = function(){
        return pageNumber;
    }

})();


var getPageHandler;
(function setupNextHandlers(){
    
    var nextHandlers = [];    
    getPageHandler = function(handlerIndex){
        return nextHandlers[handlerIndex];
    }

    // 2D array where prev is element 0 and next is element 1 
    // page 0 handlers
    var page0 = [];
    page0.push(function() {});
    page0.push(function() {
                        incrementPageNumber();
                        $('#agentdesigntitle').text('Select a Beahaviour')
                        $('#page_1').hide('slide',{direction:'left'}, 200, function(){
                            $('#page_2').show('slide', {direction:'right'}, 200)
                        });
                    }
    );


    // page 1 handlers
    page1 = [];
    page1.push(function() {
                        decrementPageNumber();
                        $('#agentdesigntitle').text('Agent Design');
                        $('#page_2').hide('slide',{direction:'right'}, 200, function(){
                            $('#page_1').show('slide', {direction:'left'}, 200)
                        });
                        setAction('nothing');
                    }
    );

    page1.push(function() {
                        incrementPageNumber();
                        displayBBox(map);
                        $('#agentdesigntitle').text('Choose Start Point');
                        $('#agentchooser').animate({height:'100px'}, 200);
                        $('#designcontainer').hide();
                        setAction('create');
                    }                
    );

    // page 2 handlers
    page2 = [];
    page2.push(function() {
                        decrementPageNumber();
                        $('#agentdesigntitle').text('Select Behaviour');
                        $('#agentchooser').animate({height:'420px'}, 200);
                        $('#designcontainer').show();
                        setAction('nothing');
                    }
    );                                
    page2.push(function() {
                        
                    }                
    );

    // push all
    nextHandlers.push(page0);
    nextHandlers.push(page1);
    nextHandlers.push(page2);
    
})();







