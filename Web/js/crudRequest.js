
const default_null = -1
/*
Top right: rename and delete model
 */
const schemaID= parseInt(location.href.substring(location.href.indexOf("id=")+3));

//rename, get the new schema name and replace the new url
function renameSchema() {
    const name=prompt("Please enter new view name");
    if (name!=="" &&name!=null)
    {
        let Obj ={
            schemaID:schemaID,
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
            success : function() {
                window.location.replace("drawingView.html?name="+name+"&id="+id);
            }, error : function() {
                alert("rename fail");
            }
        });
    }
}

//delete this schema and return to index page
function deleteSchema() {
    let Obj ={
        id: schemaID
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        type : "POST",
        url : "http://146.169.52.81:8080/er/schema/delete",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function() {
            window.location.href = "index_dev.html";
        }, error : function() {
            alert("fail to delete schema")
        }
    });
}

/*
Entity functions
*/

//create Strong entity: API:done Test: done
function createStrongEntity(name,layoutX,layoutY){
    // return Math.ceil(Math.random()*1000);
    let id;
    const Obj =JSON.stringify({
        schemaID:schemaID,
        name: name,
        // aimPort:aimPort,
        aimPort:1,
        layoutInfo: {
            layoutX: layoutX,
            layoutY: layoutY
        }
    });

    $.ajax({
        async: false,
        type : "POST",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        url:"http://146.169.52.81:8080/er/entity/create_strong",
        contentType:"application/json",
        data : Obj,
        success : function(result) {
            id=result.data.id;
        }, error : function() {
            id = -1;
        }
    });
    return id;
}

//delete all entities API:done Test:
function deleteEntity(id,name){
    let is_success = true;
    let Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        async:false,
        type : "POST",
        url:"http://146.169.52.81:8080/er/entity/delete",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function() {
        }, error : function() {
            is_success = false;
            alert("can't delete"+ name)
        }
    });
    return is_success;
}

//update entities
function updateEntity(entityID,name,layoutX,layoutY,fromPort,isPortChange,isSubset){
    //if the change is about port then true
    if (!isPortChange && isSubset){
        const subsetNode = myDiagram.findNodeForKey(entityID);
        subsetNode.findLinksOutOf().each(function (link){
            fromPort = link.toPort;
        });
    }
    //if no port pass -1
    let Obj ={
        "entityID": entityID,
        "name": name,
        "aimPort":fromPort,
        layoutInfo: {
            layoutX: layoutX,
            layoutY: layoutY
        }

    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        type : "POST",
        url : "http://146.169.52.81:8080/er/entity/update",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function() {
        }, error : function() {
            alert("update subset"+name+"fail!");
            myDiagram.rollbackTransaction();

        }
    });

}

/*
Relation Node functions
 */

//get relation_id
function getRelationId(id){
    return id.substr(id.indexOf(("_"))+1);

}

//create Relation Node API:done Test: done
function createRelationNode(name,firstEntityID,secondEntityID,firstCardinality,
                            firstEntityPort,firstEntityRelationPort,
                            secondCardinality, secondEntityPort,secondEntityRelationPort,
                            layoutX,layoutY) {
    let id;
    firstCardinality = findRelationCode(firstCardinality);
    secondCardinality = findRelationCode(secondCardinality);
    let Obj = {
        "schemaID": schemaID,
        "name": name,
        "entityWithCardinalityList": [
            {
                "entityID": firstEntityID,
                "cardinality": firstCardinality,
                "portAtRelationship": firstEntityRelationPort,
                "portAtEntity": firstEntityPort
            },

            {
                "entityID": secondEntityID,
                "cardinality": secondCardinality,
                "portAtRelationship": secondEntityRelationPort,
                "portAtEntity": secondEntityPort
            }
        ],
        "layoutInfo": {
            "layoutX": layoutX,
            "layoutY": layoutY
        }
    };
    Obj = JSON.stringify(Obj);
    $.ajax({
        async: false,
        type : "POST",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        url : "http://146.169.52.81:8080/er/relationship/create_nary",
        contentType:"application/json",
        data : Obj,
        success : function(result) {
            id=result.data.id;
        }, error : function() {
            id = -1
        }
    });
    return id;
}

//update the name of the relation API done Test://TODO:internal server error
function updateRelationNode(id,name,layoutX,layoutY) {
    const dbId = getRelationId(id)
    let Obj ={
        "relationshipID": dbId,
        "name": name,
        "layoutInfo": {
            "layoutX": layoutX,
            "layoutY": layoutY
        }
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        async: false,
        type : "POST",
        url : "http://146.169.52.81:8080/er/relationship/update",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function() {
        }, error : function() {
            myDiagram.rollbackTransaction();
            alert("rename"+name+"fail");
        }
    });

}

//delete Relation Node API:done Test:
function deleteRelationNode(id,name) {
    id = getRelationId(id);
    let is_success = true;
    let Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        async:false,
        type : "POST",
        url : "http://146.169.52.81:8080/er/relationship/delete",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function() {
        }, error : function() {
            alert("delete"+name+"fail");
            is_success=false;
        }
    });
    return is_success;
}

// delete edge
function deleteEdge(id){
    let is_success = true;
    let Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        async: false,
        type : "POST",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        url : "http://146.169.52.81:8080/er/relationship/delete_edge",
        contentType:"application/json",
        data : Obj,
        success : function(result) {
        }, error : function() {
            is_success = false;
        }
    });
    return is_success;
}

/*
ER relation functions;
 */

//create Entity_relation link API:done Test:done
function createEdge(entityID,relationshipID,cardinality,portAtEntity,portAtRelationship,ERLinkCreateVerify){
    if(ERLinkCreateVerify.has(entityID+relationshipID)) return;

    let id;
    relationshipID = getRelationId(relationshipID);

    cardinality=findRelationCode(cardinality);
    let Obj = JSON.stringify({
        entityID: entityID,
        relationshipID: relationshipID,
        cardinality: cardinality,
        portAtEntity: portAtEntity,
        portAtRelationship:portAtRelationship
    });
    $.ajax({
        async: false,
        type : "POST",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        url : "http://146.169.52.81:8080/er/relationship/link_entity",
        contentType:"application/json",
        data : Obj,
        success : function(result) {
            id=result.data.id;
        }, error : function() {
            id = -1;
        }
    });
    return id;
}

//update Entity_relation API done: test:
function updateEdge(relationshipEdgeID,entityID,cardinality,portAtRelationship,portAtEntity){
    cardinality = findRelationCode(cardinality);
    if (cardinality === undefined){

    }
    let Obj ={
        relationshipEdgeID: relationshipEdgeID,
        entityID:entityID,
        cardinality:cardinality,
        portAtRelationship:portAtRelationship,
        portAtEntity:portAtEntity
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        async: false,
        type : "POST",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        url : "http://146.169.52.81:8080/er/relationship/update_edge",
        contentType:"application/json",
        data : Obj,
        success : function() {
        }, error : function() {
            myDiagram.rollbackTransaction();
            alert("modify Entity Relation Link fail")
        }
    });

}

/*
    EW Link
 */
function deleteEWLink(id){
    // TODO：EWLink应该有name
    const linkNode = myDiagram.findLinkForKey(id);

    let is_success = deleteRelationNode(id,linkNode.name);
    const firstEdge = linkNode.edgeIDFirst;
    const secondEdge = linkNode.edgeIDSecond;
    // delete relation node in backend
    //delete edges
    is_success = deleteEdge(firstEdge) && is_success;
    is_success = deleteEdge(secondEdge) && is_success;
    return is_success;
}



/*
    attribute functions
*/

//delete attribute
function deleteAttribute(id){
    let is_success = true;
    let info ={
        "id":id
    }
    info = JSON.stringify(info);
    $.ajax({
        async: false,
        type : "POST",
        // url : "http://127.0.0.1:8000/er/attribute/delete",
        url: "http://146.169.52.81:8080/er/attribute/delete",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        traditional : true,
        data : info,
        withCredentials:false,
        dataType : "json",
        contentType : 'application/json',
        success : function(result) {
            if(result.code === 0) {
                console.log(result);
            }
        }, error : function() {
            is_success = false;
        }
    });
    return is_success;
}

// add attribute
function addAttr(){
    var tmpNodes = new go.List();
    var category;
    myDiagram.startTransaction("add attributes");
    myDiagram.nodes.each(function (node){
        if(node.isSelected){
            tmpNodes.push(node);
            category = node.category;
        }
    });
    tmpNodes.each(function (part){
        var selectedNode = part;
        // new attribute
        var attributeData = {name:"NewA"+attributeCounter.toString(),category:"Attribute"};
        attributeCounter++;
        var pos = selectedNode.location.copy();
        // var angle = Math.random()*Math.PI*2;
        // pos.x+=Math.cos(angle)*120;
        // pos.y+=Math.sin(angle)*120;
        pos.x-=120;
        // decide the attribute position
        // save key
        var connectedAttr = [];
        selectedNode.findNodesConnected().each(
            e=>{
                if(e.data.category==="Attribute" || e.data.category==="relation_attribute") connectedAttr.push(e.data.key);
            }
        );
        // change pos of related attributes
        for(var i=0;i<connectedAttr.length;i++){
            var tmp = myDiagram.findNodeForKey(connectedAttr[i]);
            tmp.data.location.y = pos.y-((connectedAttr.length-1)/2-i)*20;
            tmp.data.location.x = pos.x;

            updateInfo = {
                "attributeID": tmp.data.key.replace(/[^\d]/g)[0],
                "name": tmp.data.name,
                "dataType": tmp.data.dataType,
                "isPrimary": tmp.data.isPrimary,
                "nullable": tmp.data.allowNotNull,
                "aimPort": 5,
                "layoutInfo": {
                    "layoutX": tmp.data.location.x,
                    "layoutY": tmp.data.location.y
                }
            };
            info = JSON.stringify(updateInfo);
            $.ajax({
                type : "POST",
                // url : "http://127.0.0.1:8000/er/attribute/update",
                url: "hhttp://146.169.52.81:8080/er/attribute/update",
                headers: { "Access-Control-Allow-Origin": "*",
                    "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
                traditional : true,
                data : info,
                withCredentials:false,
                dataType : "json",
                contentType : 'application/json',
                success : function(result) {
                    if(result.code === 0) {
                        console.log(result);
                    }
                }, error : function() {
                    console.log("update in (addAttr) fails");
                }
            });
        }
        pos.y+=(connectedAttr.length/2)*25;
        attributeData.location = pos;
        attributeData.dataType = 1;
        attributeData.parentId = selectedNode.data.key;
        attributeData.allowNotNull = false; //default value false：NOT allow null
        // send data to backend
        var info;
        if (category==="entity") {
            attributeData.isPrimary = false;
            info = {
                "belongObjID": attributeData.parentId,
                "belongObjType": 2,
                "name": attributeData.name,
                "dataType": 1, // default
                "isPrimary": false,
                "nullable": false,
                "aimPort": 5,
                "layoutInfo": {
                    "layoutX": pos.x.toFixed(1),
                    "layoutY": pos.y.toFixed(1)
                },
            }
        }else {
            attributeData.category = "relation_attribute";
            info = {
                "belongObjID": attributeData.parentId,
                "belongObjType": 3,
                "name": attributeData.name,
                "dataType": 1, // default
                "isPrimary": true,
                "nullable": false,
                "aimPort": 5,
                "layoutInfo": {
                    "layoutX": pos.x.toFixed(1),
                    "layoutY": pos.y.toFixed(1)
                },
            }

        }
        // info = JSON.stringify(info);
        // $.ajax({
        //     type : "POST",
        //     // url : "http://127.0.0.1:8000/er/attribute/create",
        //     url: "http://146.169.52.81:8080/er/attribute/create",
        //     headers: { "Access-Control-Allow-Origin": "*",
        //         "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        //     traditional : true,
        //     data : info,
        //     withCredentials:false,
        //     contentType : 'application/json',
        //     dataType:'json',
        //     success : function(result) {
        //         if(result.code === 0) {
        //             $(function(){
                        var tmp = Math.ceil(Math.random()*1000);
                        const attributeKey = tmp+"_"+attributeData.name;
                        // const attributeKey = result.data.id+"_"+attributeData.name;
                        attributeData.key = attributeKey;
                        myDiagram.model.addNodeData(attributeData);
                        // save();
                        // load();
                        // new link
                        var link = {
                            from:myDiagram.model.getKeyForNodeData(selectedNode.part.data),
                            to:myDiagram.model.getKeyForNodeData(attributeData),category: "normalLink",
                            fromPort:3,toPort:5
                        };
                        myDiagram.model.addLinkData(link);
                        // save();
                        // load();
    //                 });
    //             }
    //         }, error : function(res) {
    //             alert("creating attribute fails");
    //         }
    //     });
    });
    myDiagram.commitTransaction("add attributes");
}

// set value
function modifyAttributeClick() {
    var tmpNodes = new go.List();
    myDiagram.startTransaction("edit attributes");
    myDiagram.nodes.each(function (node) {
        if (node.isSelected) {
            tmpNodes.push(node);
        }
    })
    tmpNodes.each(function (part) {
        // attribute node
        var selectedAttribute = part;
        var selectedAData = part.data;
        // get attribute key and entity id
        document.getElementById("selectedAttributeKey").value = selectedAData.key;
        document.getElementById("entityNameInfo").value = myDiagram.findNodeForKey(selectedAData.parentId).data.name;
        if(part.data.category === 'Attribute'){
            document.getElementById("attributeNameInfo").value = selectedAData.name;
            //
            const dataType = parseInt(selectedAData.dataType);
            document.getElementById("datatypeChoices").value = findDataType(dataType);
            //is primary key
            var isPrimary = document.getElementById("isPrimaryKey");
            if(part.data.isPrimary.toString() === "false"){
                isPrimary.checked = false;
            }else {isPrimary.checked = true;}
            // allow not null
            var allowNotNull = document.getElementById("allowNotNull");
            if(part.data.allowNotNull.toString() === "false"){
                allowNotNull.checked = false;
            }else {allowNotNull.checked = true;}
        }

    });
    myDiagram.commitTransaction("edit attributes");
}

//SUBMIT updates on attributes
function modifyAttribute(){
    // get name, isPrimary, datatype
    const name = document.getElementById("attributeNameInfo").value;
    const isPrimary = document.getElementById("isPrimaryKey").checked;
    const datatype = document.getElementById("datatypeChoices").value;
    const key = document.getElementById("selectedAttributeKey").value;
    const dbId = key.replace(/[^\d]/g)[0];  // id in database
    const allowNotNull = document.getElementById("allowNotNull").checked;
    // update model
    //check primary key
    var node = myDiagram.findNodeForKey(key); // attribute
    var entityNode = myDiagram.findNodeForKey(node.data.parentId);
    var flag = false;
    const entityId = node.data.parentId;
    // check underline
    if(isPrimary === true){
        // check whether there is another primaryKey
        entityNode.findNodesConnected().each(function (linkedNode){
            if(linkedNode.data!==node.data && linkedNode.data.category === "Attribute"){
                if(linkedNode.data.isPrimary === true && myDiagram.findNodeForKey(entityNode.data.key).data.category==="entity"){
                    flag = true;
                }
            }
        });
        if (flag === true){
            alert("Submit failed, this entity already has a primary key");
            return;
        }
        // add underline
        node.data.underline = true;
        node.data.isPrimary = isPrimary;
    }else {
        // remove underline
        node.data.underline = false;
    }
    node.data.name = name;
    node.data.dataType = DATATYPE[datatype];
    node.data.allowNotNull = allowNotNull;
    // update key
    node.data.key = dbId+"_"+node.data.name;
    //update link with the entity
    node.findLinksBetween(myDiagram.findNodeForKey(node.data.parentId)).each(l=>l.data.to=node.data.key);
    const viewID = parseInt(location.href.substring(location.href.indexOf("id=")+3));
    save();
    load();

    var info ={
        "attributeID": dbId,
        "name": name,
        "dataType": DATATYPE[datatype],
        isPrimay: isPrimary,
        nullable:allowNotNull,
        "aimPort": 5,
        "layoutInfo": {
            layoutX: node.data.location.x.toFixed(1),
            layoutY: node.data.location.y.toFixed(1)
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
                console.log(result);
            }
        }, error : function() {
            console.log("update fail");
        }
    });
}

/*
operations about weak entity
 */

//create weak entity
function createWeakEntity(){
    var tmpNodes = new go.List();
    myDiagram.startTransaction("add weakEntity");
    myDiagram.nodes.each(function (node){
        if(node.isSelected){
            tmpNodes.push(node);
        }
    });
    var pk = "";
    var hasPrimaryKey = false;
    tmpNodes.each(function (part){
        var selectedEntity = part;
        var selectedEData =part.data;
        //get strong entity's primary key
        selectedEntity.findNodesConnected().each(
            function(connectedNode){
                if(connectedNode.data.category==="Attribute"){
                    if(connectedNode.data.isPrimary === true){
                        hasPrimaryKey = true;
                        pk = connectedNode.data.name;
                    }
                }
            }
        );
        // whether the entity has a primary key
        if(!hasPrimaryKey){
            alert("the entity doesn't have pk!");
            return;
        }
        // new weak entity
        var weakEntityData = {name:"WeakE"+weakEntityCounter.toString(),category:weakEntityNodeCategory,primaryKey:pk};
        weakEntityCounter++;
        var pos = selectedEntity.location.copy();
        var angle = Math.random()*Math.PI/2;
        pos.x+=Math.cos(Math.random()*Math.PI*2)*120;
        pos.y-=Math.sin(angle)*200;
        weakEntityData.location = pos;
        weakEntityData.parentId = selectedEData.key;
        const schemaID = parseInt(location.href.substring(location.href.indexOf("id=")+3));
        var info = {
            "schemaID": schemaID,
            "weakEntityName": weakEntityData.name,
            "weakEntityCardinality": findRelationCode(defaultWeakToCard),
            "strongEntityID": weakEntityData.parentId,
            "strongEntityCardinality": findRelationCode(defaultWeakFromCard),
            "relationshipName": "has",
            // "portAtRelationship": 1,
            // "portAtEntity": 1,
            "weakEntityLayoutInfo": {
                "layoutX": pos.x,
                "layoutY": pos.y
            },
            "relationshipLayoutInfo": {
                "layoutX": "",
                "layoutY": ""
            }
        }
        info = JSON.stringify(info);
        $.ajax({
            type : "POST",
            url: "http://146.169.52.81:8080/er/entity/create_weak_entity",
            headers: { "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
            traditional : true,
            data : info,
            withCredentials:false,
            dataType : "json",
            contentType : 'application/json',
            success : function(result) {
                if(result.code === 0) {
                    weakEntityData.key = result.data.weakEntityID;
                    myDiagram.model.addNodeData(weakEntityData);
                    // new link
                    var link = {
                        from:myDiagram.model.getKeyForNodeData(selectedEData),
                        to:myDiagram.model.getKeyForNodeData(weakEntityData),
                        toText:"1:1",
                        fromText:"0:N",
                        relation:"for",
                        category: EWLinkCategory,
                        fromPort:1, //result.data.relationshipEdgeList[0].portAtEntity
                        toPort:2,//result.data.relationshipEdgeList[1].portAtEntity
                        key:result.data.relationshipID,
                        // key:Math.ceil(Math.random()*1000),
                        edgeIDFirst:result.data.relationshipEdgeList[0].ID,
                        edgeIDSecond:result.data.relationshipEdgeList[1].ID
                        // edgeIDFirst:Math.ceil(Math.random()*1000),
                        // edgeIDSecond:Math.ceil(Math.random()*1000),
                    };
                    myDiagram.model.addLinkData(link);
                }
            }, error : function() {
                alert("Creating weak entity fails");
            }
        });
    });
    myDiagram.commitTransaction("add weakEntity");
    save();
    load();
}

/*
operations about subset
 */

//TODO:add link API与subset API中间经过了catch Transaction 检查事件发生的先后顺序
function createSubset(){
    var tmpNodes = new go.List();
    myDiagram.startTransaction("add subset");
    myDiagram.nodes.each(function (node){
        if(node.isSelected){
            tmpNodes.push(node);
        }
    });
    tmpNodes.each(function (part){
        var selectedEntity = part;
        var selectedEData =part.data;
        var pk = "";
        var hasPrimaryKey =false;
        //get strong entity's primary key
        selectedEntity.findNodesConnected().each(
            function(connectedNode){
                if(connectedNode.data.category==="Attribute"){
                    if(connectedNode.data.isPrimary === true){
                        hasPrimaryKey = true;
                        pk = connectedNode.data.name;
                    }
                }
            }
        );
        if(!hasPrimaryKey){
            alert("the entity doesn't have pk!");
            return;
        }
        var subsetData = {name:"Subset"+weakEntityCounter.toString(),category:subsetEntityNodeCategory};
        subsetCounter++;
        var pos = selectedEntity.location.copy();
        var angle = Math.random()*Math.PI*2;
        pos.x+=Math.cos(angle)*120;
        pos.y+=Math.sin(Math.random()*Math.PI/2)*200;
        subsetData.location = pos;
        subsetData.parentId = selectedEData.key;
        //todo: need id from backend
        var info = {
            "name": subsetData.name,
            "belongStrongEntityID": subsetData.parentId,
            "aimPort": 5,
            "schemaID": schemaID,
            "layoutInfo": {
                "layoutX": pos.x,
                "layoutY": pos.y
            }
        }
        info = JSON.stringify(info);
        $.ajax({
            type : "POST",
            url: "http://146.169.52.81:8080/er/entity/create_strong",
            headers: { "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
            traditional : true,
            data : info,
            withCredentials:false,
            dataType : "json",
            contentType : 'application/json',
            success : function(result) {
                if(result.code === 0) {
                    subsetData.key = result.data.ID;
                    // subsetData.key = Math.ceil(Math.random()*1000);
                    myDiagram.model.addNodeData(subsetData);
                    // new link
                    var link = {
                        from:myDiagram.model.getKeyForNodeData(subsetData),
                        to:myDiagram.model.getKeyForNodeData(selectedEData),category: "subsetLink",
                        fromPort:5,
                        toPort:2
                    };
                    myDiagram.model.addLinkData(link);
                }
            }, error : function() {
                alert("Creating subset fails");
            }
        });
    });
    myDiagram.commitTransaction("add subset");
    save();
    load();
}

/*
    export files
 */
function callback(blob) {
    var url = window.URL.createObjectURL(blob);
    var filename = "ERModel.png";

    var res = document.createElement("a");
    res.style = "display: none";
    res.href = url;
    res.download = filename;
    document.body.appendChild(res);
    requestAnimationFrame(() => {
        res.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(res);
    });
}

function exportPng() {
    var png = myDiagram.makeImageData({ background: "white", returnType: "blob", callback: callback});
    // var testPNG = myDiagram.makeImage({ background: "white",type: "image/jpeg"});
    // console.log(testPNG);
}
