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


