
function handleDeleteRelationNode(id, name, fromEntity) {
    let is_success = deleteRelationNode(id, name)
    const relationNode = myDiagram.findNodeForKey(id)
    if(!fromEntity){
        relationNode.findLinksConnected().each(function (link){
            is_success = deleteEdge(link.key);
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
            is_success = handleDeleteRelationNode(node.key, node.name,true) && is_success;
            myDiagram.model.removeNodeData(node.data);
        }else if (category==="Attribute"){
            //delete Attribute
            is_success = deleteAttribute(node.key, node.name) && is_success;
        }else{
            //delete subset/weak entity
            is_success = handleDeleteOtherEntity(node.key, node.name,node.category) && is_success;
        }
    });
    strongEntity.findNodesConnected().each(function (link){
        //delete all link connected
        is_success = deleteEdge(link.key) && is_success;
    });
    return is_success;
}


// weak entity abd subset
function handleDeleteOtherEntity(id,name,category){
    //delete this strong entity
    let is_success = deleteEntity(id,name)
    const node = myDiagram.findNodeForKey(id)
    if (category===weakEntityNodeCategory){
        node.findLinksConnected().each(function (link){
            is_success = deleteRelationNode(link.key) && is_success;
            is_success = deleteEdge(link.fromText) && is_success;
            is_success = deleteEdge(link.toText) && is_success;
        });
        return is_success;
    }
    // delete links connected
    node.findLinksConnected().each(function (link){
        is_success = deleteEdge(link.key) && is_success;
    });
    return is_success;
}
