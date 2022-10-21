//添加Entity的选项
function addEntityOptions(content){
    var listString ="";
    var tmpString="";
    // $.getJSON(baseUrl + "test", function(entities){
    $.getJSON("http://localhost:8000/test/", function(entities){
        $.each(entities.allEntities, function(i, entity){
            tmpString = '<option>'+entity.id+':'+entity.name+'</option>/';
            $("datalist").prepend(tmpString);
        });
    });
    $(content).prepend(listString);
}