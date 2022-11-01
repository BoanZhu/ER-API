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
            modelStr = "{ \"class\": \"GraphLinksModel\",\n" +
                " \"copiesArrays\": true,\n" +
                " \"copiesArrayObjects\": true,\n" +
                " \"nodeDataArray\": ["
            const viewName = result.data.view.name;
            const viewId = id;
            var LinkModelStr = "],\"linkDataArray\": [";
            const entityList = result.data.view.entityList;
            const relationshipList = result.data.view.relationshipList;
            for(let i = 0; i < entityList.length; i++) {
                var entity  = entityList[i];
                var node = "{\"key\":"+entity.id+
                    ",\"name\":"+entity.name+
                    ",\"location\":{\"class\":\"go.Point\",\"x\":"+entity.layoutInfo.layoutX+
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
                    var attributeNode = "\"category\":\"Attribute\",\"name\":"+attribute.name+","+
                        "\"location\":{\"class\":\"go.Point\",\"x\":"+attribute.layoutInfo.layoutX+","+
                        "\"y\":"+attribute.layoutInfo.layoutY+
                        "},\"isPrimary\":"+isPrimary+
                        ",\"dataType”:"+attribute.dataType+
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
                        LinkModelStr = LinkModelStr+currentNodeLink+"]}";
                    } else {
                        LinkModelStr = LinkModelStr+currentNodeLink+","
                    }
                }
            };

            //relationship
            for(let i = 0; i < relationshipList.length; i++) {
                var relation  = relationshipList[i];
                var link = "{\"key\":"+ relation.id+"," +
                    "\"from\":"+ relation.firstEntityID+"," +
                    "\"to\":"+ relation.secondEntityID+"," +
                    "\"fromText\":"+ relation.firstCardinality+"," +
                    "\"toText\":"+ relation.secondCardinality+"," +
                    "\"relation\":"+ relation.name +
                    "}";
                if (i !== relationshipList.length-1){
                    LinkModelStr = LinkModelStr+link+",";
                }else {
                    LinkModelStr = LinkModelStr + link+"]}";
                }
            };
            if (relationshipList.length===0){
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
    // return { "class": "GraphLinksModel",
    //     "copiesArrays": true,
    //     "copiesArrayObjects": true,
    //     "nodeDataArray": [
    //         {"key":3435,"name":"Products","location":{"class":"go.Point","x":-905.441681610523,"y":-29.922811407391464},"from":true,"to":true},
    //         {"key":34,"name":"Suppliers","location":{"class":"go.Point","x":-1472.350018298561,"y":-105.79224133183979},"from":true,"to":true},
    //         {"key":9340,"name":"Categories","location":{"class":"go.Point","x":-987.4442912524521,"y":-381.1624322383951},"from":true,"to":true},
    //         {"name":"NewA","category":"Attribute","location":{"class":"go.Point","x":-885.0581974373832,"y":-318.57578994715294},"isPrimary":false,"dataType":1,"entityId":9340,"allowNotNull":false,"key":4090}
    //     ],
    //     "linkDataArray": [
    //         {"key":123,"from":3435,"to":34,"fromText":"0..N","toText":"1","relation":"has"},
    //         {"key":456,"from":3435,"to":9340,"fromText":"0..N","toText":"1","relation":"with"},
    //         {"from":9340,"to":4090,"category":"normalLink"}
    //     ]};

}