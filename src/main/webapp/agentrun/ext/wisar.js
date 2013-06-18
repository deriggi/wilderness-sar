/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function getBoard(){
    
    $.post('/wisar/q/board', function(data){
        nwlon = data.Nwlon;
        nwlat = data.Nwlat;
        
        selon = data.Selon;
        selat = data.Selat;
        
    });
    
}