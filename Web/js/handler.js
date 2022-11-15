
function handleDeleteRelationNode(id, name, fromEntity) {
    let is_success = deleteRelationNode(id, name)
    const relationNode = myDiagram.findNodeForKey(id)
    if(!fromEntity){
        relationNode.findLinksConnected().each(function (link){
            is_success = deleteEdge(link.data.key);
        });
    }
    return is_success;
}

//strong entity need to delete all related subset, attribute and weak entity  API:done Test:
function handleDeleteStrongEntity(id,name){
    //delete this strong entity
    let is_success = true;
    is_success = deleteEntity(id,name)

    const strongEntity = myDiagram.findNodeForKey(id)
    strongEntity.findNodesConnected().each(function (node){
        if (category===relationNodeCategory && node.findNodesConnected().count<3){
            is_success = handleDeleteRelationNode(node.data.key, node.data.name,true) && is_success;
            myDiagram.model.removeNodeData(node.data.data);
        }else if (category==="Attribute"){
            //delete Attribute
            is_success = deleteAttribute(node.data.key, node.data.name) && is_success;
        }else{
            //delete subset/weak entity
            is_success = handleDeleteOtherEntity(node.data.key, node.data.name,node.data.category) && is_success;
        }
    });
    strongEntity.findNodesConnected().each(function (link){
        //delete all link connected
        is_success = deleteEdge(link.data.key) && is_success;
    });
    return is_success;
}


// weak entity and subset
function handleDeleteOtherEntity(id,name,category){
    //delete this strong entity
    let is_success = deleteEntity(id,name)
    const node = myDiagram.findNodeForKey(id)
    if (category===weakEntityNodeCategory){
        node.findLinksConnected().each(function (link){
            // console.log("handleDeleteOtherEntity");
            console.log(link.data);
            is_success = deleteRelationNode(prefixRelationNodeKey+link.data.key) && is_success;
            is_success = deleteEdge(link.data.edgeIDFirst) && is_success;
            is_success = deleteEdge(link.data.edgeIDSecond) && is_success;
        });
        return is_success;
    }
    // delete links connected
    // node.findLinksConnected().each(function (link){
    //     is_success = deleteEdge(link.data.key) && is_success;
    // });
    return is_success;
}

//function handle process from port change, toEnd true, then change the toPort
/*
from: entity    entity        entity    relation     subset
to: relation  weak entity   attribute   attribute    entity
 */
function handleChangPort(link,toEnd,newPort){
    const fromNode =  myDiagram.findNodeForKey(link.from);
    const toNode =  myDiagram.findNodeForKey(link.to);
    let is_success = true;
    const fromCategory= fromNode.category;
    const toCategory = toNode.category;
    if(fromCategory=== relationNodeCategory && !toEnd){ //relation to attribute
        //relation-attribute link port change only change the from port (Attribute only have one port)
        modifyAttributeToPort(toNode,newPort);
    }else if(fromCategory === subsetEntityNodeCategory && toEnd){
        // subset-entity link port change only change the to port (subset only have one port)
        updateEntity(fromNode.key,fromCategory.name,fromNode.data.location.x,fromNode.data.location.y,newPort,true,true);
    }else if(toCategory==="Attribute"&&!toEnd){
        //entity-attribute link port change only change the from port (Attribute only have one port)
        modifyAttributeToPort(toNode,newPort);
    }
    else if(toCategory===weakEntityNodeCategory && !toEnd){
        updateEdge(link.edgeIDFirst,link.from,link.fromText,default_null,newPort,true);
    }
    else if(toCategory===weakEntityNodeCategory && toEnd){
        updateEdge(link.edgeIDSecond,link.to,link.toText,newPort,default_null,true);
    }
    else if(toCategory===relationNodeCategory && toEnd){
        //relation-entity chang the to side
        updateEdge(link.key,link.from,link.fromText,newPort,link.fromPort,true);
    }
    else if(toCategory===relationNodeCategory && !toEnd){
        //relation-entity chang the to side
        updateEdge(link.key,link.from,link.fromText,link.toPort,newPort,true);
    }

    return is_success;
}

function modifyAttributeToPort(attributNode,newPort){
    var info ={
        "attributeID": attributNode.data.key.split("_")[0],
        "name": attributNode.data.name,
        "dataType": attributNode.data.dataType,
        isPrimay: attributNode.data.isPrimay,
        nullable:attributNode.data.allowNotNull,
        "aimPort": newPort,
        "layoutInfo": {
            layoutX: attributNode.data.location.x.toFixed(1),
            layoutY: attributNode.data.location.y.toFixed(1)
        }
    }
    // console.log(info);
    info = JSON.stringify(info);
    $.ajax({
        type : "POST",
        url: "http://146.169.52.81:8080/er/attribute/update",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        traditional : true,
        data : info,
        withCredentials:false,
        dataType : "json",
        contentType : 'application/json',
        success : function(result) {
            if(result.code === 0) {
                console.log("change port");
            }
        }, error : function() {
            console.log("update fail");
        }
    });
}