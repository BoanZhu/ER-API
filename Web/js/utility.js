function getView(id) {
    var modelStr;
    $.ajax({
        type : "GET",
        async: false,
        url : "http://146.169.52.81:8080/er/view/get_by_id",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        data : {ID:id},
        success : function(result) {
            let flag = 1;
            modelStr = "{ \"class\": \"GraphLinksModel\",\n" +
                " \"copiesArrays\": true,\n" +
                " \"copiesArrayObjects\": true,\n" +
                " \"nodeDataArray\": ["
            var LinkModelStr = "],\"linkDataArray\": [";
            const entityList = result.data.view.entityList;
            const relationshipList = result.data.view.relationshipList;
            for(let i = 0; i < entityList.length; i++) {
                var entity  = entityList[i];
                var node = "{\"key\":"+entity.id+
                    ",\"name\":\""+entity.name+
                    "\",\"location\":{\"class\":\"go.Point\",\"x\":"+entity.layoutInfo.layoutX+
                    ",\"y\":"+entity.layoutInfo.layoutY+
                    "}, \"from\": true, \"to\":true}";

                var attributeList = entity.attributeList;
                if (i === entityList.length-1 && attributeList.length===0){
                    modelStr = modelStr + node;
                }else{
                    modelStr = modelStr + node+",";
                }
                for(let j = 0; j < attributeList.length; j++) {
                    var attribute = attributeList[j]
                    var isPrimary = 0;
                    if (attribute.isPrimary){
                        isPrimary=1
                    }
                    var attributeNode = "{\"category\":\"Attribute\",\"name\":\""+attribute.name+"\","+
                        "\"location\":{\"class\":\"go.Point\",\"x\":"+attribute.layoutInfo.layoutX+","+
                        "\"y\":"+attribute.layoutInfo.layoutY+
                        "},\"isPrimary\":"+isPrimary+
                        ",\"dataType\":"+attribute.dataType+
                        ",\"key\":"+attribute.id+"}";

                    if (i === entityList.length-1 && j === attributeList.length-1){
                        modelStr = modelStr + attributeNode;
                    }else{
                        modelStr = modelStr + attributeNode+",";
                    }

                    //add node,attribute Link
                    var currentNodeLink = "{\"from\":"+entity.id+
                        ",\"to\":"+attribute.id+
                        ",\"category\":\"normalLink\"}";
                    if (i === entityList.length-1 && j === attributeList.length-1 && relationshipList.length===0){
                        flag=0;
                        LinkModelStr = LinkModelStr+currentNodeLink+"]}";
                    } else {
                        LinkModelStr = LinkModelStr+currentNodeLink+","
                    }
                }
            }

            //relationship
            for(let i = 0; i < relationshipList.length; i++) {
                var relation  = relationshipList[i];
                var link = "{\"key\":"+ relation.id+"," +
                    "\"from\":"+ relation.firstEntityID+"," +
                    "\"to\":"+ relation.secondEntityID+"," +
                    "\"fromText\":\""+ findRelationName(relation.firstCardinality)+"\"," +
                    "\"toText\":\""+ findRelationName(relation.secondCardinality)+"\"," +
                    "\"relation\":\""+ relation.name +
                    "\"}";
                if (i !== relationshipList.length-1){
                    LinkModelStr = LinkModelStr+link+",";
                }else {
                    LinkModelStr = LinkModelStr + link+"]}";
                }
            }
            if (relationshipList.length===0&& flag){
                modelStr = modelStr+LinkModelStr+"]}";
            }else{
                modelStr = modelStr+LinkModelStr;
            }
            console.log(modelStr);
            return modelStr;
        }, error : function(result) {
            console.log("false");
        }
    });
    return modelStr;
}

const RELATION = {
    UNKNOWN:"",
    ZeroToOne:"0:1",
    ZeroToMany:"0:N",
    OneToOne:"1:1",
    OneToMany:"1:N"
}
function findRelationName(indexs){
    return Object.values(RELATION)[indexs];
}
const ENTITYTYPE=["","entity","weakEntity","subset","attribute"]

function getSchema(id) {
    var modelStr;
    const x = go.GraphObject.make;
    myDiagram = x(go.Diagram,
        {
            allowDelete: false,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            // layout: $(go.ForceDirectedLayout, {isInitial: false, isOngoing: false}),
            // "draggingTool.dragsLink": false,
            // "draggingTool.isGridSnapEnabled": false,
            // "undoManager.isEnabled": false,
            // "maxSelectionCount": 1,
        });
    $.ajax({
        type : "GET",
        async: false,
        // url : "http://146.169.52.81:8080/er/schema/get_by_id",
        url:"http://127.0.0.1:8000/er/schema/get_by_id",
        // headers: { "Access-Control-Allow-Origin": "*",
        //     "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        withCredentials:false,
        contentType : 'application/json',
        dataType:'json',
        data : {ID:id},
        success : function(result) {
            // entity list
            var entityList = result.data.schema.entityList;
            // relationship list
            var relationshipList = result.data.schema.relationshipList;
            var attributeList = []
            entityList.forEach(
                function (entityNode){
                // add entity node
                var entityData = {key:entityNode.id,name:entityNode.name,category:ENTITYTYPE[entityNode.entityType],
                    location: {"class": "go.Point", "x": entityNode.layoutInfo.layoutX, "y": entityNode.layoutInfo.layoutY},
                    from:true, to:true}
                if(ENTITYTYPE[entityNode.entityType]!=="entity"){
                    entityData.parentId = entityNode.strongEntityID;
                }
                myDiagram.model.addNodeData(entityData);
                if(entityNode.entityType!==3){
                    attributeList=entityNode.attributeList;
                    attributeList.forEach(
                        function (attributeNode){
                        // add attribute node
                        var attributeNodeData = {"name":attributeNode.name,"category":"Attribute",
                            "location":{"class":"go.Point","x":attributeNode.layoutInfo.layoutX,"y":attributeNode.layoutInfo.layoutY},
                            "dataType":attributeNode.dataType,"parentId":entityNode.id,"allowNotNull":entityNode.nullable,
                            "isPrimary":attributeNode.isPrimary,"key":attributeNode.id+"_"+attributeNode.name,"underline":attributeNode.isPrimary};
                        myDiagram.model.addNodeData(attributeNodeData);
                        // add link between node and attribute
                        //todo 万一老师说可以乱挪attribute，那么entity给的port在json中放哪里
                        var linkData = {"from":entityData.key,"to":attributeNodeData.key,"category":"normalLink","fromPort":entityNode.aimPort,"toPort":5}
                        myDiagram.model.addLinkData(linkData);
                    });
                }
            });

            relationshipList.forEach(
                function (relationNode){
                const edgeList = relationNode.edgeList;
                const firstType = myDiagram.findNodeForKey(edgeList[0].entityID).category;
                const secondType = myDiagram.findNodeForKey(edgeList[1].entityID).category;
                // both strong entity
                if(firstType === "entity" && secondType=== "entity"){
                    // create relation node
                    var relationNodeData = {"key":"relation_"+relationNode.id,"name":relationNode.name,
                        "location":{"class":"go.Point","x":relationNode.layoutInfo.layoutX,"y":relationNode.layoutInfo.layoutY},
                        "category":"relation","from":true,"to":true};
                    myDiagram.model.addNodeData(relationNodeData);
                    // two links
                    var firstLinkData = {"from":edgeList[0].entityID,"to":relationNodeData.key,"fromText":findRelationName(edgeList[0].cardinality),
                        "category":"entityLink","fromPort":edgeList[0].portAtEntity,"toPort":edgeList[0].portAtRelationship};
                    var secondLinkData = {"from":edgeList[1].entityID,"to":relationNodeData.key,"fromText":findRelationName(edgeList[1].cardinality),
                        "category":"entityLink","fromPort":edgeList[1].portAtEntity,"toPort":edgeList[1].portAtRelationship};
                    myDiagram.model.addLinkData(firstLinkData);
                    myDiagram.model.addLinkData(secondLinkData);
                    // add attribute
                    const relationAttributeList = relationNode.attributeList;
                    relationAttributeList.forEach(
                        function (relationAttributeNode){
                        // node
                        var rAttrNodeData = {"name":relationNode.name,"category":"relation_attribute",
                            "location":{"class":"go.Point","x":relationAttributeNode.layoutInfo.layoutX,"y":relationAttributeNode.layoutInfo.layoutY},
                            "dataType":relationAttributeNode.dataType,"parentId":relationNodeData.key,
                            "allowNotNull":relationAttributeNode.nullable,"key":relationAttributeNode.id+"_"+relationAttributeNode.name};
                        // link
                        var linkNodeData = {"from":relationNodeData.key,"to":rAttrNodeData.key,"category":"normalLink",
                            "fromPort":relationAttributeNode.aimPort,"toPort":5};
                        myDiagram.model.addNodeData(rAttrNodeData);
                        myDiagram.model.addLinkData(linkNodeData);
                    });
                }
                // strong entity and weak entity
                if(firstType === "weakEntity" || secondType === "weakEntity"){
                    // add parentID to weak entity node
                    var tmp = firstType==="weakEntity"?0:1;
                    var weakEntityNode = myDiagram.findNodeForKey(edgeList[tmp].entityID);
                    var linkData = {"from":edgeList[1-tmp].entityID,"to":weakEntityNode.key,"toText":findRelationName(edgeList[tmp].cardinality),
                        "fromText":findRelationName(edgeList[1-tmp].cardinality),"relation":relationNode.name,"category":"weakLink",
                        "fromPort":edgeList[1-tmp].portAtEntity,"toPort":edgeList[1-tmp].portAtEntity,"key":relationNode.id,
                        "edgeIDFirst":edgeList[0].ID,"edgeIDSecond":edgeList[1].ID};
                    myDiagram.model.addLinkData(linkData);
                }
                // strong entity and subset
                if(firstType === "subset" || secondType === "subset"){
                    // add parentID to subset node
                    var tmp = firstType==="subset"?0:1;
                    var subsetNode = myDiagram.findNodeForKey(edgeList[tmp].entityID);
                    var linkData ={"from":edgeList[1-tmp].entityID,"to":subsetNode.key,
                        "category":"subsetLink","fromPort":edgeList[1-tmp].portAtEntity,"toPort":edgeList[tmp].portAtEntity};
                    myDiagram.model.addLinkData(linkData);
                }
                else{
                    console.log("load fails");
                }
            });
            modelStr = myDiagram.model.toJSON();
            console.log(modelStr);
            return modelStr;
        }, error : function(result) {
            console.log("false");
        }
    });
    return modelStr;
}
