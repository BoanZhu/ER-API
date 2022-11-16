/*
index.js is for the index html

showModel is used to show the model at right dashboard,
    input: selected id and name
    output: model shown at right div

anonymous function:
    1. show all view model: output: all view name and id in the list
    2. slide down and up function
 */
/*
get view id
 */
function getId(){
    const selected_name =  $('#vInput').val();
    return $('#viewsList option[value="' + selected_name + '"]').attr('id');
}

/*
Show model at rignt
 */
function showSchema() {
    // Get the model name and id from list
    const id = getId();
    let isInitial = true;
    $.ajax({
        type : "GET",
        async: false,
        url : "http://146.169.52.81:8080/er/schema/get_by_id",
        withCredentials:false,
        contentType : 'application/json',
        dataType:'json',
        data : {ID:id},
        success : function(result) {
            // entity list
            var entityList = result.data.schema.entityList;
            entityList.forEach(
                function (entityNode){
                    if(entityNode.layoutInfo!==null){
                        isInitial=false;
                    }
                });
        }, error : function(result) {
            console.log("false");
        }
    });
    defineModel(isInitial);
    var schema = getSchema(id);
    // indexDiagram.model = go.Model.fromJSON(schema);
}


/*
Continue Edit, editModel()
get view id and view name
output: redirect to the drawing.html with the name and id
 */
function editSchema(){
    const selected_name =  $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    window.location.href = "drawingView.html?name="+selected_name+"&id="+id;
}

/*
Rename as, rename the current model, and the model list will also be changed
renameModel():
get view id and view name
output: model list will be refresh
 */
function renameModel(){
    const selected_name =  $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    const name=prompt("Please enter new view name",selected_name);

    if (name!==""&& name!=null&&selected_name!==name) {
        let Obj ={
            viewID:id,
            name: name
        }
        Obj = JSON.stringify(Obj);

        $.ajax({
            type : "POST",
            headers: { "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
            url : "http://146.169.52.81:8080/er/schema/update",
            contentType:"application/json",
            data : Obj,
            success : function(result) {
                window.location.reload();
            }, error : function(result) {
            }
        });
    }
}

/*
delete: delete this model
deleteModel():
get view id and view name
output: model list will be refresh
*/
function deleteSchema() {
    const selected_name = $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');

    var Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        type : "POST",
        url : "http://146.169.52.81:8080/er/schema/delete",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function(result) {
            console.log("true")
            window.location.reload();
        }, error : function(result) {
            console.log("false");
        }
    });
}

/*
Create new... jump to drawing html and create the new model
createModel():
input:name
output: redirect to the html and start drawing

 */
function createSchema() {
    const name = prompt("Please enter new view name", "Draco");
    if (name != null && name !== "") {
        var Obj ={
            name: name
        }
        Obj = JSON.stringify(Obj);
        $.ajax({
            type : "POST",
            url : "http://146.169.52.81:8080/er/schema/create",
            data : Obj,
            headers: { "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
            contentType: "application/json",
            success : function(result) {
                window.location.href = "drawingView.html?name="+name+"&id="+result.data.id;
            }, error : function(result) {
                console.log("false");
            }
        });
    }
}

/*
Append views into view list
 */
function appendModel(){
    $.ajax({
        type : "GET",
        async: false,
        url : "http://146.169.52.81:8080/er/schema/query_all_schemas",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        data : {},
        success : function(result) {
            let options='';
            const views= result.data.schemaList;
             for (let i = 0; i < views.length; i++) {
                 options += '<option id =' + views[i].id+ '  value="' + views[i].name + '" />';

        }
            document.getElementById('viewsList').innerHTML = options;
        }, error : function(result) {
            console.log("false");
        }
    });

}

window.addEventListener('DOMContentLoaded', appendModel);

// window.addEventListener('DOMContentLoaded', function (){
//
//     defineModel(true);
//
// });

/*
HTML list slide down
 */
$(function (){
    //hide all subtitle
    $(".nav_menu").each(function (){
        $(this).children(".nav_content").hide();

    });
    //add the click event of all the content
    $(".nav_title").each(function (){
        $(this).click(function (){
            var nav = $(this).parent(".nav_menu").children(".nav_content");
            if (nav.css("display")!=="none"){
                nav.slideUp();
            }else{
                nav.slideDown();
            }
        });
    });

});
