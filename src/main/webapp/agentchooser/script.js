


var map;
var REQUEST_SIZE = 10;

//=====================
// bbox 
//=====================
var setGameBBox;
var displayBBox;
var removeBBox;
(function setupBBoxAccess(){
    var nwlon, nwlat, selon, selat;
    var bbox;
    setGameBBox = function(nwlon, nwlat, selon, selat){
        this.nwlon = nwlon;
        this.nwlat = nwlat;
        this.selon = selon;
        this.selat = selat;

    }

    displayBBox = function(theMap){
        removeBBox();
        bounds = [[this.nwlat, this.nwlon], [this.selat, this.selon]];
        bbox = L.rectangle(bounds, {
            color:'#3498DB', 
            weight:0
        }).addTo(theMap)
    //        theMap.fitBounds(bounds);
    }

    removeBBox = function(){
        if(bbox && bbox != null){
            map.removeLayer(bbox);
            bbox = null;    
        }
        
    }

})();



SimId = (function setupSimId(){
    var simId;
    
    return{
        getSimId : function(){
            return simId;
        },
        setSimId : function(someId){
            simId = someId;
        }
    }
    
})();



//=====================
// retrieve the gameboard metadata
//=====================
function fetchSimId(){
    
    $.post('/wisar/q/agent/simid', function(data){
            
            if(data && data.length>0){
                SimId.setSimId(data[0]);
            }
            
        });
}

//=====================
// retrieve the gameboard metadata
//=====================
function getBoard(){
    
    $.post('/wisar/q/board', function(data){
        var nwlon = data.nwlon;
        var nwlat = data.nwlat;
        
        var selon = data.selon;
        var selat = data.selat;

        setGameBBox(nwlon, nwlat, selon, selat);
        
        
        var maxlat = data.maxlat;
        var maxlon = data.maxlon;
        
        var minlat = data.minlat;
        var minlon = data.minlon;
        
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


//=====================
// convert points into leaflet latlngs
//=====================
function makeLatLngs(coords){
    var linePoints = [];
    for( var coord in coords){
        linePoints.push(new L.LatLng(coords[coord][0],coords[coord][1])); 
    }
    return linePoints;
}

//=====================
// axis ordering fuck!
//=====================
function flip(lonLat){
    return [lonLat[1],lonLat[0]];
}

//=====================
// axis ordering again!
//=====================
// function flip(loc){
//     return [loc[1],loc[0]];
//}



//=====================
// display crazy viewshed polygons
//=====================
function showViewshed(viewshedPolygons){
    var tempPolygon;
    var multipolygon = [];
    for(polygonIndex in viewshedPolygons){
        // the polygon
        tempPolygon = viewshedPolygons[polygonIndex]
        var ltlngs = [];
        for(var vertexIndex in tempPolygon){
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

ResponseQueue = (function setupQueue(){
    var queue = [];
    var refillRequested = false;
    
    return {
        hasRefillRequested: function(){
            return refillRequested
        },
        setRefillRequested : function(requested){
            refillRequested = requested;
        },
        
        isEmpty : function(){
            return queue.length == 0;
        },
        clear : function(){
            queue.length = 0;
        }, 
        
        size : function(){
            return queue.length;
        },
        
        pushResponse : function (data){
            queue.push(data);
        },
        popResponse : function (){
            if(queue.length == 0){
                return null;
            }
            var returnObject =  queue[0];
            queue.splice(0, 1);
            
            return returnObject;
            
        }
    }
    
})();

// draw agentloc every 100 until quit flag is raised
function animateResponse(){
    if(!isRunning()){
        return;
    }
    
    // if
    if( !ResponseQueue.hasRefillRequested() && ResponseQueue.size() < REQUEST_SIZE*AgentCounter.getCount()){
        // call wander again if low on data
        
        refill();
        ResponseQueue.setRefillRequested(true);
        
    }
    drawAgentLocation();
    window.setTimeout(animateResponse,400)
}

//=====================
// display agent loc
//=====================
function drawAgentLocation() {
    var data= ResponseQueue.popResponse();
    $('#queuesize').text('Queue size is: ' + ResponseQueue.size() );
    
    if(data == null){
        return;
    }
    
    for(var agentIndex in data){
        var tempLastPosition = getLastLocation(data[agentIndex].id);
        if(tempLastPosition){

            // draw line to this location
            var line = [new L.LatLng(tempLastPosition[1],tempLastPosition[0]), new L.LatLng(data[agentIndex].location[1], data[agentIndex].location[0])];
            var leafletLine = L.polyline(line,{
                color:'#0068CD'
            }).addTo(map);
            pushLine(data[agentIndex].id,leafletLine);

        }else{
            L.circle(
                flip(data[agentIndex].location), 
                8, 
                {
                    weight:1
                }).addTo(map);
        }
        setLastLocation(data[agentIndex].id, data[agentIndex].location);
        
        if(data[agentIndex].nameTag == 'uav' && data[agentIndex].foundOthers == true){
            showFindings( data[agentIndex] );
        }
        
    }
    
}


function showFindings(findingsData){
    var timestep = findingsData.timestep;
    
    $('#findings').show();
    $('#timestepfound').text(timestep);
}


function refill(){
    var statusbar = $('#statusbar');
    
    $(statusbar).text("requesting data...")
    $.post('/wisar/q/agent/wander/'+REQUEST_SIZE, function(data){
        $(statusbar).text("data received");
        for(dataIndex in data){
            ResponseQueue.pushResponse([data[dataIndex]]);
        }
        ResponseQueue.setRefillRequested(false);

    });
    
}
//=====================
// repeating call to move agent
//=====================
function wander(){
    var statusbar = $('#statusbar');
    $(statusbar).text("requesting data...")
    $.post('/wisar/q/agent/wander/' + (REQUEST_SIZE*3), function(data){
        $(statusbar).text("data received");
        for(dataIndex in data){
            ResponseQueue.pushResponse([data[dataIndex]]);
        }
        animateResponse();

    });
    
}

//=====================
// run all agents until one is found
//=====================
function runsim(){
    
    $.post('/wisar/q/agent/runsim/', function(data){
        
        drawAgentLocation(data);
        

    });
    
}


//=====================
// maintain running state
//=====================
var isRunning;
var setRunning;
var setTimeoutKey;
var stopSim;
var resumeSim;
(function setupIsRunning(){
    var running = false;
    var timeoutKey;
    isRunning = function(){
        return running;
    }

    setRunning = function(r){
        running = r;
        
    }

    setTimeoutKey = function(tok){
        timeoutKey = tok;
    }

    stopSim = function(){

        window.clearTimeout(timeoutKey);
        setRunning(false);
        $('#statusbar').text("sim paused");

    }

    

})();


//=====================
// retireve the viewshed for a location
//=====================
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



//=====================
// remove tail of agent path
//=====================
var getLastLocation
var setLastLocation
var pushAgentMarker
var pushLine
var removeAllLines;
var removeAgentMarkers;

(function setupGetLastLocs(){
    
    var lastLocs = {}
    var lastLines = {}
    var agentMarkers = [];
    
    pushLine = function(agentId, line){
        if(!lastLines[agentId]){
            lastLines[agentId] = [];
        }
        lastLines[agentId].push(line);
        if(lastLines[agentId].length > 5){
            for(var i = 0; i < lastLines[agentId].length - 20; i++){
                map.removeLayer(lastLines[agentId][i]);
            }

            var howMuchGreater = lastLines[agentId] - 20; 
            var x=0;
            while(x < howMuchGreater){
                lastLines[agentId].splice(0,1);
            }

        }
    }

    pushAgentMarker = function(agentMarker){
        agentMarkers.push(agentMarker);
    }
    removeAgentMarkers = function(){
        for( var markerIndex in agentMarkers){
            map.removeLayer(agentMarkers[markerIndex]);
        }
    }
    
    removeAllLines = function(){
        for(var agentId in lastLines){
            for(var line in lastLines[agentId]){
                map.removeLayer(lastLines[agentId][line]);
            }
        }
    }

    getLastLocation = function(agentId){
        return lastLocs[agentId]
    }

    setLastLocation = function(agentId,loc){
        lastLocs[agentId] = loc
    }

})();

//=====================
// handle behaviour selection
//=====================
var getSelectedBehaviourItem;
var setSelectedBehaviourItem;
(function setupSelectBehaviourItem(){
    
    var selectedBehaviour;
    getSelectedBehaviourItem = function(){
        return selectedBehaviour;
    }

    setSelectedBehaviourItem = function(theBehaviour){
        if(theBehaviour == null){
            selectedBehaviour = null;
            return;
        }

        selectedBehaviour = theBehaviour;
        setAgentBehaviour(selectedBehaviour.text())        
    }

})();


//=====================
// handle agne type selection
//=====================
var getSelectedTypeItem;
var setSelectedTypeItem;
(function setupSelectBehaviourItem(){
    
    var selectedTypeItem;
    getSelectedTypeItem = function(){
        return selectedTypeItem;
    }

    setSelectedTypeItem = function(theAgentType){
        if(theAgentType == null){
            selectedTypeItem = null;
            return;
        }

        selectedTypeItem = theAgentType;
        setAgentType(selectedTypeItem.text())
    // if uav type set velocity to 4 otherwise 2
    }

})();


//=====================
// update style of selected behaviour
//=====================
var selectBehaviorLink
(function setupBehaviorSelectionLink(){
    var selectedLink = null;
    selectBehaviorLink = function(someLink){
        if(selectedLink != null){
            $(selectedLink).css('text-decoration', 'none');
        }
        selectedLink = someLink;
        $(selectedLink).css('text-decoration', 'underline');
    }
})();


//=====================
// get maptiles and setup event handlers
//=====================
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


//=====================
// fetch behaviours
//=====================
function getThemBehaviours(){
    var ul = makeList();
    
    $.getJSON('/wisar/q/behaviour/list', function(data){
        
        for(var dataIndex in data ){
            $(ul).append(makeListItem(data[dataIndex].name, 'behaviouritem', getSelectedBehaviourItem, setSelectedBehaviourItem));
        }
        $('#page_2').append(ul);
    });
}

//=====================
// why am i doing this dynamically?
//=====================
function makeAgentTypes(){
    var ul = makeList();

    $(ul).append(makeListItem('UAV_AGENT', 'behaviouritem', getSelectedTypeItem, setSelectedTypeItem));
    $(ul).append(makeListItem('LOST_PERSON_AGENT', 'behaviouritem', getSelectedTypeItem, setSelectedTypeItem));

    $('#page_1').append(ul);
}


//=====================
// make the parent list ul item
//=====================
function makeList(theStyle){
    if(!theStyle){
        theStyle = 'behaviourlist'
    }
    var ul = document.createElement('ul');
    $(ul).addClass(theStyle);
    return ul;
}


//=====================
// make the item and handle clicks
//=====================
function makeListItem(data, theStyle, getter, setter){
    if(!theStyle){
        theStyle = 'behaviouritem'
    }
    
    var li = $(document.createElement('li')).addClass(theStyle).append(data);
    giveItemClickyBehaviour(li, getter, setter);

    return li;
}


//=====================
// handle clicks of list items
//=====================
function giveItemClickyBehaviour(listItem, getter, setter){
    
    $(listItem).click(function(){

        var previous = getter();
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
            setter($(this));
            
        }
        else{
            $(this).css('background-color','#34495E');
            $(this).attr('lebronned', '');   
        }

    });
}

function setToDefaultStyle(listItem){
    $(listItem).css('background-color','#34495E');
    $(listItem).attr('lebronned', '');   
}

function iterateThroughListItems(ul, doToThem){
    var items = $(ul).children();
    for( var itemIndex in items){
        doToThem(items[itemIndex])
    }
}



var handleMapClick;
var setAction;
(function setupMapClickHandler(){
    
    var actionHolder = {};

    doNothingAction = function(e) {}
    setLocationAction = function(e) {

        var ltlng = e.latlng;
        var lat = ltlng.lat;
        var lng = ltlng.lng;
        var someMarker = L.marker([lat, lng], {
            draggable:true
        } ).addTo(map);

        setAgentMarker(someMarker);
        setAction('nothing')

    }

    setAction = function(someKey){
        handleMapClick = actionHolder[someKey];
    }

    actionHolder['nothing'] = doNothingAction;
    actionHolder['set'] = setLocationAction;
    handleMapClick = doNothingAction;

})();


var setAgentBehaviour
var setAgentMarker
var setAgentType
var getConstructedAgent
var resetAgentCreator
var isAgentComplete;
(function setupAgentCreator(){
    var behaviour = null;
    var agentType = null;
    var agentMarker = null;

    isAgentComplete = function(){
        
        if(
            behaviour != null && 
            agentType != null &&
            agentMarker != null
            ){
            return true;
        }
        return false;

    }

    setAgentBehaviour = function(b){
        behaviour = b;
    }

    setAgentMarker = function(someMarker){
        agentMarker = someMarker;
        pushAgentMarker(someMarker);
    }

    setAgentType = function(t){
        agentType = t;
    }

    getConstructedAgent = function(){
        var myLat, myLng;

        if(agentMarker != null){
            var latlng = agentMarker.getLatLng();
            myLat = latlng.lat;
            myLng = latlng.lng;
            

        }
        return new VectorAgent(behaviour, myLng, myLat, agentType);
    }

    resetAgentCreator = function(){
        behaviour = null;
        agentMarker = null;
        agentType = null;
    }

})();


function VectorAgent(behaviour, startLon, startLat, agentType){
    this.behaviour = behaviour;
    this.lon = startLon;
    this.lat = startLat;
    this.agentType = agentType;
}



//=====================
// keep track of page number for agent dialog
//=====================
var getPageNumber;
var resetPageNumber;
var incrementPageNumber;
var decrementPageNumber;
(function setupPageNumberMachine(){
    var pageNumber = 0;
    
    resetPageNumber = function(){
        pageNumber = 0;
    }

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
    page0.push(function() {
        $('#agentchooser').hide('slide', {
            direction:'right'
        }, 200);
        resetAgentCreatorDialog();
    });
    page0.push(function() {
        incrementPageNumber();
        $('#agentdesigntitle').text('Select a Beahaviour')
        $('#page_1').hide('slide',{
            direction:'left'
        }, 200, function(){
            $('#page_2').show('slide', {
                direction:'right'
            }, 200)
        });
    }
    );


    // page 1 handlers
    var page1 = [];
    page1.push(function() {
        decrementPageNumber();
        $('#agentdesigntitle').text('Agent Design');
        $('#page_2').hide('slide',{
            direction:'right'
        }, 200, function(){
            $('#page_1').show('slide', {
                direction:'left'
            }, 200)
        });
        setAction('nothing');
    }
    );

    page1.push(function() {
        incrementPageNumber();
        displayBBox(map);
        $('#agentdesigntitle').text('Choose Start Point');
        $('#agentchooser').animate({
            height:'100px'
        }, 200);
        $('#designcontainer').hide();
        $('#nextpage').text('done');

        setAction('set');
    }                
    );

    // page 2 handlers
    var page2 = [];
    page2.push(function() {
        decrementPageNumber();
        $('#agentdesigntitle').text('Select Behaviour');
        $('#agentchooser').animate({
            height:'420px'
        }, 200);
        $('#designcontainer').show();
        $('#nextpage').text('next');
        setAction('nothing');
    }
    );                                
    page2.push(function() {

        // check if agent has everything
        if(isAgentComplete()){
            agent = getConstructedAgent();
            
            // have completed agent so send to server
            createAgent(agent);    

            // reset agent property holder
            resetAgentCreatorDialog();

            $('#agentchooser').hide('slide', {
                direction:'right'
            }, 200);
            // $('#addagentsection').hide();
            $('#runagents').fadeIn('fast');

            removeBBox();
            

        }    
    }

    );

    // push all
    nextHandlers.push(page0);
    nextHandlers.push(page1);
    nextHandlers.push(page2);
    
})();


function resetAgentCreatorDialog(){
    resetAgentCreator();
    setToDefaultStyle(getSelectedTypeItem());
    setToDefaultStyle(getSelectedBehaviourItem());
    setSelectedTypeItem(null);
    setSelectedBehaviourItem(null);
}
function createAgent(agent) {
    
    $.post('/wisar/q/agent/createagent/' + agent.lon + '/' + agent.lat, {
        agenttype : agent.agentType, 
        behaviour : agent.behaviour, 
        simid : SimId.getSimId()
    }, function(data){
        drawAgentLocation(data);
        AgentCounter.increment();
    });
}

AgentCounter = (function setupAgentCounter(){
    var counter = 0;
    
    return {
        getCount : function(){
            return counter;  
        },
        increment: function(){
            counter++;
        },
        clear: function(){
            counter = 0;
        }
    }
})();


function clearAgents() {
    stopSim();
    AgentCounter.clear();
    ResponseQueue.clear();
    
    $.post('/wisar/q/agent/clearagents/' , function(data){
        $('#statusbar').text(data);
        
        
    });
}



