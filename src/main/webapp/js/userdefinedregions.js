/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


var poly, map;
var markers = [];
var dataMarkers = [];
var path = new google.maps.MVCArray;
var cacheKey = null;
var inRunningState = false;
var tid = null;


[
  {
    "stylers": [
      { "saturation": -100 }
    ]
  },{
  }
]

function initialize() {
    var greyStyle= [
  {
    "stylers": [
      { "saturation": -100 }
    ]
  },{
    "featureType": "administrative.country",
    "elementType": "labels.text",
    "stylers": [
      { "saturation": -92 },
      { "invert_lightness": true },
      { "visibility": "simplified" }
    ]
  }
]

//    var greyStyle = [
//    {
//        featureType: "administrative.province",
//        stylers: [
//        {
//            visibility: "on"
//        },
//        {
//            saturation: -48
//        },
//        {
//            gamma: 0.63
//        },
//        {
//            lightness: 81
//        }
//        ]
//    },{
//        featureType: "poi.park",
//        stylers: [
//        {
//            gamma: 0.55
//        },
//        {
//            visibility: "on"
//        },
//        {
//            lightness: 85
//        },
//        {
//            saturation: -80
//        }
//        ]
//    },{
//        featureType: "water",
//        stylers: [
//        {
//            gamma: 1.16
//        },
//        {
//            saturation: -76
//        },
//        {
//            lightness: 22
//        }
//        ]
//    },{
//        featureType: "road",
//        stylers: [
//        {
//            visibility: "on"
//        },
//        {
//            saturation: -87
//        },
//        {
//            lightness: 60
//        }
//        ]
//    },{
//        featureType: "poi.government",
//        stylers: [
//        {
//            lightness: 55
//        },
//        {
//            saturation: 50
//        }
//        ]
//    },{
//        featureType: "administrative.country",
//        stylers: [
//        {
//            saturation: -57
//        },
//        {
//            lightness: 58
//        }
//        ]
//    },{
//        featureType: "administrative.locality",
//        elementType: "labels",
//        stylers: [
//        {
//            visibility: "on"
//        },
//        {
//            hue: "#1900ff"
//        },
//        {
//            saturation: -53
//        },
//        {
//            lightness: 73
//        }
//        ]
//    },{
//        featureType: "water",
//        elementType: "labels",
//        stylers: [
//        {
//            lightness: 32
//        }
//        ]
//    }
//    ];
                
    var greyType= new google.maps.StyledMapType(greyStyle,
    {
        name: "Grey Style"
    });
            
    var uluru = new google.maps.LatLng(41.1, -107);

    map = new google.maps.Map(document.getElementById("testmap"), {
        zoom: 5,
        center: uluru,
        streetViewControl : false,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });
    
    map.mapTypes.set('greyStyle', greyType);
    map.setMapTypeId('greyStyle');
                
                
    poly = new google.maps.Polygon({
        strokeWeight: 3,
        fillColor: '#5555FF'
    });
    poly.setMap(map);
    poly.setPaths(new google.maps.MVCArray([path]));

    google.maps.event.addListener(map, 'click', addPoint);
}
// set interval

function checkPercentComplete() {
    makePercentCompleteRequest(cacheKey);
}
function abortTimer() { // to be called when you want to stop the timer
    clearInterval(tid);
}

function clearMarkers(){
    
    for (var i in markers){
        markers[i].setMap(null)
    }
    
    for (var i in dataMarkers){
        dataMarkers[i].setMap(null)
    }
    
    path.clear()
    markers = []
    dataMarkers = []
}
// call get

function goToStart(){
    
    $('#buttonwrapper').hide('slow');
    $('#resetbutton').hide('slow');
    $('#arrow').fadeIn('slow')
    $('#instructions').text('Click on the map to draw a region');
}
function displaySubmitState(){
    
  //$('#formwrapper').fadeIn('slow');
    $('#buttonwrapper').fadeIn('slow');
    $('#resetbutton').fadeIn('slow');
    $('#arrow').hide()
    $('#instructions').text('Looks good, submit your job');
}

function addPoint(event) {
    path.insertAt(path.length, event.latLng);
    
    var marker = new google.maps.Marker({
        position: event.latLng,
        map: map,
        draggable: true
    });
    markers.push(marker);
    
    if(markers.length == 3){
        displaySubmitState()
        
    }
    marker.setTitle("#" + path.length);

    google.maps.event.addListener(marker, 'click', function() {
        marker.setMap(null);
        for (var i = 0, I = markers.length; i < I && markers[i] != marker; ++i);
        markers.splice(i, 1);
        path.removeAt(i);
        
    }
    );

    google.maps.event.addListener(marker, 'dragend', function() {
        for (var i = 0, I = markers.length; i < I && markers[i] != marker; ++i);
        path.setAt(i, marker.getPosition());
    //        $('#pointlog').clear();
        
    }
    );

//    for(var p = 0; p < path.length; p++){
//        $('#pointlog').append(path.getAt(p).lng() + ' ' + path.getAt(p).lat())
//
//    }
}

function createRequestString(){
    var pathArray = path.getArray();
    var coordsString= '';

    for(var i in pathArray){
        coordsString += pathArray[i].lng() + ',' + pathArray[i].lat();
        if(i < pathArray.length - 1){
            coordsString += '|';
        }
    }
    coordsString+= '|' + pathArray[0].lng() + ',' + pathArray[0].lat();
    return coordsString;
}

function showMarkerIntersections(climateResult){
    var centroids = climateResult.coordinates;

    for (var i in centroids){
        var lon = centroids[i][0]
        var lat = centroids[i][1]
        
        var ltlng = new google.maps.LatLng(lat,lon);
        
        dataMarkers.push(new google.maps.Marker({
            position : ltlng,
            map : map,
            title : 'roid'
        }));
    }
    
}

function makeIntersectionRequest(){
    if(inRunningState){
        alert('already running yall');
        return;
    }
    $('#buttonwrapper').fadeOut('slow',function(){
        $('#arrow').hide();
        $('#formwrapper').hide();
        $('#instructions').text('Your job is running...');
    });
    
    var boundary = createRequestString();
    $.post('/rasterintersect/q/userdefined/',{
        'pointslist' : boundary
    },
    
    function(data){
        showMarkerIntersections(data);
        $('#instructions').text('et voila!')
           
    });
//            
//            
//            cacheKey =   data.cachekey;
//            inRunningState = true;
//        
//        $('#progressblock').fadeIn('slow', function(){
//            $('#emailblock').fadeIn('slow');
//        });
        
       
//tid =  setInterval(checkPercentComplete, 5000);
       
//    });
}

function makePercentCompleteRequest(cacheKey){
    //    alert('making precent copmete request with key: '  +cacheKey);
    var dummy = {
        'x':new Date().getTime()
    }
    
    if(cacheKey != null){
        $.get('/climateweb/rest/projectstatus/percentcomplete/' + cacheKey,dummy,function(data){
            //            alert('response is ' + data);
            $('#statustrail').text( data )
            $('#progressbar').css( 'width', data+'%' )
            $('#progressblock').css( 'display', 'block' )
            
            if(data >= 100){
                // done so get the file written location
                abortTimer();
                makeFinalRestingPlaceRequest(cacheKey);
                inRunningState = false;
            }
        });
    }
}

function makeFinalRestingPlaceRequest(cacheKey){
    if(cacheKey != null){
        
        var dummy = {
            'x':new Date().getTime()
        }

        $.get('/climateweb/rest/projectstatus/restingplace/' + cacheKey,dummy,function(data){
            // turn this into a link
            var fullPath = data;
            //fullPath = "/"+fullPath.substring(data.indexOf('climateweb'));
            $('#downloadlink').attr('href',fullPath).text('Output File');
        });
    }
}

function setUserEmail(cacheKey, useremail){
    
    
    $('#emailblock').fadeOut('slow');
    
    if(cacheKey != null){
        var requestObject = {
            'cachekey':cacheKey,
            'email' : useremail
        }
        $.post('/climateweb/rest/projectstatus/setemail/',requestObject ,function(data){
            // TODO add confirmation to user here for a few seconds
            }, 'json');
    }
}


// ================================================================================

// on ready

// ================================================================================


$(document).ready(function(){

    $('#showdata').click(function(){
        makeIntersectionRequest();
    });
    
    $('#resetbutton').click(function(){
        clearMarkers(); 
        goToStart();
    });
    
    $('#sendemail').click(function(){
        var emailaddy = $('#useremail').val();
        
        
        if(emailaddy != null && emailaddy.length > 5){
            setUserEmail(cacheKey, emailaddy);
        }else{
            alert('email address not set');
        }
        
    })
    
});
