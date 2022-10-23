//添加Entity的选项
function addEntityOptions(content){
    var listString ="";
    var tmpString="";
    //todo edit url
    // $.getJSON(baseUrl + "test", function(entities){
    $.getJSON("http://localhost:8000/test/", function(entities){
        $.each(entities.allEntities, function(i, entity){
            tmpString = '<option>'+entity.id+':'+entity.name+'</option>/';
            $("datalist").prepend(tmpString);
        });
    });
    $(content).prepend(listString);
}

//create entity
function createEntity(){
    var entityName = prompt("Please Enter the Entity Name:","");

    if(entityName){
        console.log("the new entity name is "+entityName);
        //todo input check
        //1. 重复

        // add node
        var location=new go.Point(-1000+100*Math.random(),100*Math.random());
        var entity={key:entityName,location:location};
        // console.log(entity);
        myDiagram.model.addNodeData(entity);

        var newJson = myDiagram.model.toJSON();
        document.getElementById("mySavedModel").value = newJson;

        //todo
        // send info to backend(name & pos)

    }
}

function openEditEntity(){
    var popBox = document.getElementById("popDiv");
    popBox.style.display = "block";
}

function closeEditEntity(){
    let popDiv = document.getElementById("popDiv");
    popDiv.style.display = "none";
}

//edit entity
function editEntity(){
    var newName = document.getElementById("newEntityName").value;
    var entityId = document.getElementById("entityNameChoice").value;
    //original key
    var originalName = entityId.split(":")[1];
    // send new entity name to backend
    $.getJSON("http://localhost:8000/editEntity?"+"id="+entityId+"&newName="+newName+"&originalName="+originalName, function(res){
        //todo return state
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
        const originalName = entityName.split(":")[1];
        var node = myDiagram.findNodeForKey(originalName);
        myDiagram.remove(node);
        var newJson = myDiagram.model.toJSON();
        console.log(newJson);
        myDiagram.model = go.Model.fromJson(newJson);
        document.getElementById("mySavedModel").value = myDiagram.model.toJson();
        closeDeleteEntity();
    }
}