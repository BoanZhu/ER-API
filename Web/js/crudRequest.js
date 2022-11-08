/*
Top right: rename and delete model
 */

//rename, get the new name and replace the new url
function renameView() {
    const name=prompt("Please enter new view name");
    const id =  location.href.substring(location.href.indexOf("id=")+3);
    if (name!=="" &&name!=null)
    {
        var Obj ={
            viewID:id,
            name: name
        }
        Obj = JSON.stringify(Obj);

        $.ajax({
            type : "POST",
            headers: { "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
            url : "http://146.169.52.81:8080/er/view/update",
            contentType:"application/json",
            data : Obj,
            success : function(result) {
                window.location.replace("drawingView.html?name="+name+"&id="+id);
            }, error : function(result) {
            }
        });
    }
}

//deleteView(), delete this view and return to index
function deleteView() {
    const id =  location.href.substring(location.href.indexOf("id=")+3);
    let Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        type : "POST",
        url : "http://146.169.52.81:8080/er/view/delete",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function(result) {
            console.log("true");
            window.location.href = "index_dev.html";
        }, error : function(result) {
            console.log("false");
        }
    });
}

/*
Entity functions
*/
function createEntity(name,layoutX,layoutY){
    const viewID = parseInt(location.href.substring(location.href.indexOf("id=")+3));
    let id;
    var Obj ={
        viewID:viewID,
        name: name,
        layoutInfo: {
            layoutX: layoutX,
            layoutY: layoutY
        }
    }

    Obj = JSON.stringify(Obj);
    $.ajax({
        async: false,
        type : "POST",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        // url : "http://146.169.52.81:8080/er/entity/create",
        url:"http://127.0.0.1:8080/er/entity/create",
        contentType:"application/json",
        data : Obj,
        success : function(result) {
            id=result.data.id;
        }, error : function(result) {
        }
    });
    return id;
}

function deleteEntity(id){
    myDiagram.nodes.each(function (node){
        // if(("category" in node.data) && ((node.data.entityId === id) || (id in node.data.entityList))){ //all linked attributes,subsets and weak entities
        if("category" in node.data){
            if(node.data.entityId === id){
                //todo 需要重新调delete subset/weak entity func吗（backend
                myDiagram.model.removeNodeData(node.data);
            }
            // relation
            if(node.data.entityList !== undefined && node.data.entityList.includes(id)){
                if(node.data.entityList.length===2){
                    // delete another link
                    node.findNodesConnected().each(n=>node.findLinksBetween(n).each(l=>myDiagram.model.removeLinkData(l.data)));
                    // todo delete relation http
                    myDiagram.model.removeNodeData(node.data);
                }
            }
        }
    });
    let Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    // $.ajax({
    //     type : "POST",
    //     // url : "http://146.169.52.81:8080/er/entity/delete",
    //     url:"http://127.0.0.1:8080/er/entity/delete",
    //     data : Obj,
    //     headers: { "Access-Control-Allow-Origin": "*",
    //         "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
    //     contentType: "application/json",
    //     success : function(result) {
    //
    //     }, error : function(result) {
    //     }
    // });
}

function updateEntity(id,name,layoutX,layoutY){
    var Obj ={
        entityID:id,
        name: name,
        layoutInfo: {
            layoutX: layoutX,
            layoutY: layoutY
        }
    }
    Obj = JSON.stringify(Obj);

    $.ajax({
        type : "POST",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        url : "http://146.169.52.81:8080/er/view/update",
        contentType:"application/json",
        data : Obj,
        success : function(result) {
        }, error : function(result) {
        }
    });
}


/*
Relation functions
 */

function createRelation(name,firstEntityID,secondEntityID,firstCardinality,secondCardinality) { //return request ID
    const viewID =  parseInt(location.href.substring(location.href.indexOf("id=")+3));
    let id;
    let Obj = {
        viewID: viewID,
        name: name,
        firstEntityID: firstEntityID,
        secondEntityID: secondEntityID,
        firstCardinality: firstCardinality,
        secondCardinality: secondCardinality
    };
    Obj = JSON.stringify(Obj);
    $.ajax({
        async: false,
        type : "POST",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        url : "http://146.169.52.81:8080/er/relationship/create",
        contentType:"application/json",
        data : Obj,
        success : function(result) {
            id=result.data.id;
        }, error : function(result) {
        }
    });
    return id;
}

function modifyRelation(id,firstEntityID,secondEntityID,firstCardinality,secondCardinality,name) {

    let Obj ={
        "relationshipID": id,
        "name": name,
        "firstEntityID": firstEntityID,
        "secondEntityID": secondEntityID,
        "firstCardinality": firstCardinality,
        "secondCardinality": secondCardinality
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        type : "POST",
        url : "http://146.169.52.81:8080/er/relationship/update",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function(result) {
        }, error : function(result) {
        }
    });


}

function deleteRelation(id) {
    let Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        type : "POST",
        url : "http://146.169.52.81:8080/er/relationship/delete",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function(result) {
        }, error : function(result) {
        }
    });
}

/*
    attribute functions
*/

function deleteAttribute(id){
    var info ={
        "id":id
    }
    info = JSON.stringify(info);
    $.ajax({
        type : "POST",
        // url : "http://127.0.0.1:8000/er/attribute/delete",
        url: "http://146.169.52.81:8080/er/attribute/delete",
        // headers: { "Access-Control-Allow-Origin": "*",
        //     "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
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
            console.log("delete fail");
        }
    });
}


// add attribute
function addAttr(){
    var tmpNodes = new go.List();
    myDiagram.startTransaction("add attributes");
    myDiagram.nodes.each(function (node){
        if(node.isSelected){
            tmpNodes.push(node);
        }
    });
    tmpNodes.each(function (part){
        var selectedEntity = part;
        var selectedEData =part.data;
        // new attribute
        var attributeData = {name:"NewA"+attributeCounter.toString(),category:"Attribute"};
        attributeCounter++;
        var pos = selectedEntity.location.copy();
        var angle = Math.random()*Math.PI*2;
        pos.x+=Math.cos(angle)*120;
        pos.y+=Math.sin(angle)*120;
        attributeData.location = pos;
        attributeData.isPrimary = false;
        attributeData.dataType = 1;
        attributeData.entityId = selectedEData.key;
        attributeData.allowNotNull = false; //default value false：NOT allow null
        // send data to backend
        const viewId =  location.href.substring(location.href.indexOf("id=")+1);
        var info ={
            "viewID":viewId,
            "entityID":attributeData.entityId,
            "nullable":false,
            "name":attributeData.name,
            "dataType":1, //default
            "isPrimary":false,
            "layoutInfo":{
                "layoutX":pos.x.toFixed(1),
                "layoutY":pos.y.toFixed(1)
            }
        }
        info = JSON.stringify(info);
        $.ajax({
            type : "POST",
            url : "http://127.0.0.1:8000/er/attribute/create",
            // url: "http://146.169.52.81:8080/er/attribute/create",
            // headers: { "Access-Control-Allow-Origin": "*",
            //     "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
            traditional : true,
            data : info,
            withCredentials:false,
            contentType : 'application/json',
            dataType:'json',
            success : function(result) {
                if(result.code === 0) {
                    $(function(){
                        const attributeKey = result.data.id+"_"+ attributeData.name;
                        attributeData.key = attributeKey;
                        myDiagram.model.addNodeData(attributeData);
                        save();
                        load();
                        // new link
                        var link = {
                            from:myDiagram.model.getKeyForNodeData(selectedEData),
                            to:myDiagram.model.getKeyForNodeData(attributeData),category: "normalLink",
                            fromPort:"L",toPort:"U"
                        };
                        myDiagram.model.addLinkData(link);
                    });
                }
            }, error : function(res) {
                alert("creating attribute fails");
            }
        });
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
        document.getElementById("entityNameInfo").value = myDiagram.findNodeForKey(selectedAData.entityId).data.name;
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
    const allowNotNull = document.getElementById("allowNotNull").checked;
    // update model
    //check primary key
    var node = myDiagram.findNodeForKey(key); // attribute
    var entityNode = myDiagram.findNodeForKey(node.data.entityId);
    var flag = false;
    const entityId = node.data.entityId;
    // check underline
    if(isPrimary === true){
        // check whether there is another primaryKey
        entityNode.findNodesConnected().each(function (linkedNode){
            if(linkedNode.data!==node.data && linkedNode.data.category === "Attribute"){
                if(linkedNode.data.isPrimary === true){flag = true;}
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
    const viewID = parseInt(location.href.substring(location.href.indexOf("id=")+3));

    save();
    load();

    var info ={
        "attributeID": node.key,
        "name": name,
        "dataType": DATATYPE[datatype],
        isPrimay: isPrimary,
        nullable:allowNotNull,
        layoutInfo: {
            layoutX: node.data.location.x.toFixed(1),
            layoutY: node.data.location.y.toFixed(1)
        }
    }
    console.log(info);
    info = JSON.stringify(info);
    $.ajax({
        type : "POST",
        // url : "http://127.0.0.1:8000/er/attribute/update",
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
    tmpNodes.each(function (part){
        var selectedEntity = part;
        var selectedEData =part.data;
        // new weak entity
        var weakEntityData = {name:"WeakE"+weakEntityCounter.toString(),category:"WeakEntity"};
        weakEntityCounter++;
        var pos = selectedEntity.location.copy();
        var angle = Math.random()*Math.PI*2;
        pos.x+=Math.cos(angle)*120;
        pos.y+=Math.sin(angle)*120;
        weakEntityData.location = pos;
        weakEntityData.entityId = selectedEData.key;
        //todo: need id from backend

        // weakEntityData.key = result.data.id;
        weakEntityData.key = Math.ceil(Math.random()*1000);
        myDiagram.model.addNodeData(weakEntityData);
        // save();
        // load();
        // new link
        var link = {
            from:myDiagram.model.getKeyForNodeData(selectedEData),
            to:myDiagram.model.getKeyForNodeData(weakEntityData),
            toText:"1:1",
            relation:"for",category: "relationLink",
            fromPort:"L",toPort:"U"
        };
        myDiagram.model.addLinkData(link);
    });
    myDiagram.commitTransaction("add weakEntity");
}
//delete weak entity
function deleteWeakEntity(id){
    // delete all attributes of weak entity
    myDiagram.nodes.each(function (node){
        if(("category" in node.data) && node.data.category === "Attribute" && node.data.entityId === id){
            myDiagram.model.removeNodeData(node.data);
        }
    });
    console.log("delete weak entity"+id);
    let Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    // $.ajax({
    //     type : "POST",
    //     url : "http://146.169.52.81:8080/er/entity/delete",
    //     data : Obj,
    //     headers: { "Access-Control-Allow-Origin": "*",
    //         "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
    //     contentType: "application/json",
    //     success : function(result) {
    //     }, error : function(result) {
    //     }
    // });
}
/*
operations about subset
 */

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
        // new weak entity
        var subsetData = {name:"Subset"+weakEntityCounter.toString(),category:"Subset"};
        subsetCounter++;
        var pos = selectedEntity.location.copy();
        var angle = Math.random()*Math.PI*2;
        pos.x+=Math.cos(angle)*120;
        pos.y+=Math.sin(angle)*120;
        subsetData.location = pos;
        subsetData.entityId = selectedEData.key;
        //todo: need id from backend

        // weakEntityData.key = result.data.id;
        subsetData.key = Math.ceil(Math.random()*1000);;
        myDiagram.model.addNodeData(subsetData);
        // save();
        // load();
        // new link
        var link = {
            from:myDiagram.model.getKeyForNodeData(subsetData),
            to:myDiagram.model.getKeyForNodeData(selectedEData),category: "subsetLink",
            fromPort:"L",toPort:"U"
        };
        myDiagram.model.addLinkData(link);
    });
    myDiagram.commitTransaction("add subset");
}

function deleteSubset(id){
    console.log("delete subset"+id);
    let Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    // $.ajax({
    //     type : "POST",
    //     url : "http://146.169.52.81:8080/er/entity/delete",
    //     data : Obj,
    //     headers: { "Access-Control-Allow-Origin": "*",
    //         "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
    //     contentType: "application/json",
    //     success : function(result) {
    //     }, error : function(result) {
    //     }
    // });
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

/*
port
 */
