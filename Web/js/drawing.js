


function getView() {
    // const selected_name =  $('#vInput').val();
    // const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    //
    //
    // $.getJSON("http://localhost:8000/er/view/get_by_id?" + "id=" + id, function (res) {
    //     //resolve the json format
    //     var modelStr = "{ \"class\": \"GraphLinksModel\",\n" +
    //         " \"copiesArrays\": true,\n" +
    //         " \"copiesArrayObjects\": true,\n" +
    //         " \"nodeDataArray\": ["
    //     const viewName = result.data.view.name;
    //     const viewId = id;
    //
    //
    //     const entityList = result.data.view.entityList;
    //
    //     for(let i = 0; i < entityList.length; i++) {
    //         var entity  = entityList[i];
    //         var node = "{\"key\":"+entity.id+
    //                    ",\"name\":"+entity.name+
    //                    ",\"location\":{\"class\":\"go.Point\",\"x\":"+parseFloat(entity.layoutInfo.layoutX)+
    //                                                         ",\"y\":"+parseFloat(entity.layoutInfo.layoutY)+
    //             "}, \"from\": true, \"to\":true}";
    //         if (i !== entityList.length-1){
    //             modelStr = modelStr+node+",";
    //         }else {
    //             modelStr = modelStr + node;
    //         }
    //     };
    //
    //     modelStr = modelStr+"],\"linkDataArray\": [";
    //     //relationship
    //     const relationshipList = result.data.view.relationshipList;
    //
    //     for(let i = 0; i < relationshipList.length; i++) {
    //         var relation  = relationshipList[i];
    //         var link = "{\"key\":"+ relation.id+"," +
    //                     "\"from\":"+ relation.firstEntityID+"," +
    //                     "\"to\":"+ relation.secondEntityID+"," +
    //                     "\"fromText\":"+ relation.firstCardinality+"," +
    //                     "\"toText\":"+ relation.secondCardinality+"," +
    //                     "\"relation\":"+ relation.name +
    //                     "}";
    //         if (i !== relationshipList.length-1){
    //             modelStr = modelStr+link+",";
    //         }else {
    //             modelStr = modelStr + link+"]}";
    //         }
    //     };
    //     //todo replace with the model
    //     window.location.replace("drawingView.html?name="+ selected_name+"&id="+selected_id);
    // }).fail(function (failure) {
    //     if (failure.status == 400) {
    //         console.log("fail status:" + failure.status);
    //     }
    // });

    return "{ \"class\": \"GraphLinksModel\"," +
        "  \"copiesArrays\": true," +
        "  \"copiesArrayObjects\": true," +
        "  \"nodeDataArray\": [" +
        "{\"key\":3435,\"name\":\"Products\",\"location\":{\"class\":\"go.Point\",\"x\":-905.441681610523,\"y\":-29.922811407391464}, \"from\": true, \"to\":true}," +
        "{\"key\":34,\"name\":\"Suppliers\",\"location\":{\"class\":\"go.Point\",\"x\":-1472.350018298561,\"y\":-105.79224133183979}, \"from\": true, \"to\":true}," +
        "{\"key\":9340,\"name\":\"Categories\",\"location\":{\"class\":\"go.Point\",\"x\":-987.4442912524521,\"y\":-381.1624322383951}, \"from\": true, \"to\":true}" +
        "],\"linkDataArray\": [" +
        "{\"key\": 123, \"from\":3435,\"to\":34,\"fromText\":\"0..N\",\"toText\":\"1\",\"relation\":\"has\"}," +
        "{\"key\": 456,\"from\":3435,\"to\":9340,\"fromText\":\"0..N\",\"toText\":\"1\",\"relation\":\"with\"}" +
        "]}"

}

function RenameView() {

    const selected_name =  $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    const name=prompt("Please enter new view name",selected_name);

    if (name!="" && new_name!=null)
    {
        $.getJSON("http://localhost:8000/er/view/update?" + "id=" + id + "&name=" + name, function (res) {
        }).fail(function (failure) {
            if (failure.status == 400) {
                console.log("fail status:" + failure.status);
            }
        });
        location.reload();
    }
}
function deleteView() {

    const selected_name =  $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    $.getJSON("http://localhost:8000/er/view/delete" + "id=" + id, function (res) {
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });

    //reload page
    location.reload();
}

function drawView() {
    var name = prompt("Please enter new view name", "Draco");
    if (name != null && name != "") {

        $.getJSON("http://localhost:8000/er/relationship/create?" + "&name=" + name, function (res) {
            //todo get the viewId
            const id = res.id
            window.location.replace("drawingView.html?name=" + name + "&id=" + id);
        }).fail(function (failure) {
            if (failure.status == 400) {
                console.log("fail status:" + failure.status);
            }
        });
    }
}


//添加Entity的选项
function addEntityOptions(content){
    var listString ="";
    var tmpString="";
    //todo url
    // $.getJSON(baseUrl + "test", function(entities){
    $.getJSON("http://localhost:8000/test/", function(entities){
        $.each(entities.allEntities, function(i, entity){
            tmpString = '<option>'+entity.id+':'+entity.name+'</option>/';
            $("datalist").prepend(tmpString);
        });
    });
    $(content).prepend(listString);
}

//edit entity
function editEntity(){
    var newName = document.getElementById("newEntityName").value;
    var entityId = document.getElementById("entityNameChoice").value;
    //original key
    var originalName = entityId.split(":")[1];
    // send new entity name to backend
    //todo status
    $.getJSON("http://localhost:8000/editEntity?"+"id="+entityId+"&newName="+newName+"&originalName="+originalName, function(res){
        //todo return state

    }).fail(function (failure){
        if(failure.status == 400){
            console.log("fail status:"+failure.status);
        }
    });
    // get the node
    var node = myDiagram.findNodeForKey(originalName);
    //edit link info
    //找到所有和这个node连接的nodes并且修改link
    node.findNodesConnected().each(
        function(otherNode){
            //todo 删除旧的link
            // node.findLinksTo(otherNode).each(function (l){});
            // console.log(otherNode.data.key);
            myDiagram.model.addLinkData({from:newName,to:otherNode.data.key});
        }
    )
    //edit node info
    node.data.key = newName;
    var newJson = myDiagram.model.toJSON();
    myDiagram.model = go.Model.fromJson(newJson);
    document.getElementById("mySavedModel").value = myDiagram.model.toJson();
    closeEditEntity();
}

//delete entity
function openDeleteEntity(){
    var popBox = document.getElementById("popDeleteDiv");
    popBox.style.display = "block";
}

function closeDeleteEntity(){
    let popDiv = document.getElementById("popDeleteDiv");
    popDiv.style.display = "none";
}

function deleteEntity(){
    var entityName = document.getElementById("deleteEntityNameChoice").value;
    if(entityName){
        // console.log("the entity to delete is "+entityName);
        //todo 解析entity名称
        const originalName = entityName.split(":")[1];
        const entityId = entityName.split(":")[0];
        $.getJSON("http://localhost:8000/deleteEntity?"+"id="+entityId+"&callback=?", function(res){
            //todo return state
            console.log("delete entity status:"+res.testInfo);
        });
        console.log("delete");
        var node = myDiagram.findNodeForKey(originalName);
        myDiagram.remove(node);
        var newJson = myDiagram.model.toJSON();
        myDiagram.model = go.Model.fromJson(newJson);
        document.getElementById("mySavedModel").value = myDiagram.model.toJson();
        closeDeleteEntity();
    }
}

/*
    attributes
 */
// add attribute
//delete entity

function openAddAttribute(){
    var popBox = document.getElementById("addAttributeDiv");
    popBox.style.display = "block";
}

function closeAddAttribute(){
    let popDiv = document.getElementById("addAttributeDiv");
    popDiv.style.display = "none";
}

function addAttribute(){
    //entity name & attribute name
    var entityNameWithId = document.getElementById("entityForAttributeNameChoice").value;
    var entityName = entityNameWithId.split(":")[1];
    var attributeName = document.getElementById("newAttributeName").value;

    //todo 判断
    if(attributeName){
        console.log("the new attribute name is "+attributeName);
        //todo input check
        //1. 重复

        // get new entity id
        $.getJSON("http://localhost:8000/returnNewAttributeId/", function(newAttributeId){
            newId = newAttributeId.id;
            //add attribute node
            var location=new go.Point(-1000+100*Math.random(),100*Math.random());
            var newAttr={"key":attributeName,"location":location,category:"Attribute",id:newId};
            myDiagram.model.addNodeData(newAttr);

            //add link between the node and the attribute
            attributeNode = myDiagram.findNodeForKey(attributeName);
            entityNode = myDiagram.findNodeForKey(entityName);
            var link = {from:attributeName,to:entityName,category: "normalLink"};
            myDiagram.model.addLinkData(link);

            var newJson = myDiagram.model.toJSON();
            document.getElementById("mySavedModel").value = newJson;
        });
    }
    closeAddAttribute();
}

function test(){
    var Obj ={
        "id":"123",
        "name":"345",
        "location":{
            "x":123,
            "y":678
        }
    }

    Obj = JSON.stringify(Obj);

    $.ajax({
        type : "GET",
        url : "http://localhost:8000/er/view/get_by_id",
        traditional : true,
        data : {
            "Obj":Obj,
        },
        withCredentials:false,
        dataType : 'json',
        success : function(result) {
            if(result.code == 0) {
                $(function(){
                    var view = result.data.view;
                    console.log("name"+result.data.view.name);
                });
            }
        }, error : function(res) {

        }
    });
}