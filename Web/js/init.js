var entityCounter = 0;
var attributeCounter=0;
function init() {
    /*
    Get the editable model Template
     */
    const $ = go.GraphObject.make;  // for conciseness in defining templates
    myDiagram = $(go.Diagram, "myDiagramDiv",  // must name or refer to the DIV HTML element
        {
            allowDelete: true,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            layout: $(go.ForceDirectedLayout, { isInitial: false, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "clickCreatingTool.archetypeNodeData": { name:"New Entity",from:true,to:true},
            "undoManager.isEnabled": true,
            "maxSelectionCount": 1,
            "ChangedSelection":changedSelection,
        });

    // Common color
    const colors = {'lightblue': '#afd4fe',}
    // Common text styling
    function textStyle() {
        return {
            margin: 6,
            wrap: go.TextBlock.WrapFit,
            textAlign: "center",
            editable: true,
        }
    }
    const addNodeAdornment =
        $(go.Adornment, "Spot",
            $(go.Panel, "Auto",
                $(go.Shape, { fill: null, stroke: "dodgerblue", strokeWidth: 3 }),
                $(go.Placeholder)),
            $("Button", {alignment: go.Spot.TopRight, click: addAttr},
                $(go.Shape, "PlusLine", { desiredSize: new go.Size(6, 6) })));

    const attributeAdornment =
        $(go.Adornment, "Spot",
            $(go.Panel, "Auto",
                $(go.Shape, { fill: null, stroke: "dodgerblue", strokeWidth: 3 }),
                $(go.Placeholder)),
            $("Button", {alignment: go.Spot.TopRight, click: modifyAttributeClick},
                $(go.Shape, "MinusLine", { desiredSize: new go.Size(6, 6) })));

    // entity
    var entityTemplate =
        $(go.Node, "Auto",  // the whole node panel
            {
                locationSpot: go.Spot.Center,
                selectionAdornmentTemplate: addNodeAdornment,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
                linkValidation: function(fromNode, fromGraphObject, toNode, toGraphObject){
                    return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 1;
                },
            },
            new go.Binding("location", "location").makeTwoWay(),
            // whenever the PanelExpanderButton changes the visible property of the "LIST" panel,
            // clear out any desiredSize set by the ResizingTool.
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            // define the node's outer shape, which will surround the Table
            $(go.Shape, "RoundedRectangle",
                {
                    fill: 'white',
                    portId: "",
                    stroke: colors.lightblue,
                    cursor: "pointer",
                    fromSpot: go.Spot.AllSides,
                    toSpot: go.Spot.AllSides,
                    strokeWidth: 3,
                    fromLinkableDuplicates: false, toLinkableDuplicates: false
                },
            new go.Binding("fromLinkable", "from").makeTwoWay(), new go.Binding("toLinkable", "to").makeTwoWay()),

            // the table header
            $(go.Panel, "Table",
                { margin: 8, stretch: go.GraphObject.Fill },
                $(go.RowColumnDefinition, { row: 0, sizing: go.RowColumnDefinition.None }),
                $(go.TextBlock,textStyle(),
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(5, 24, 5, 2),  // leave room for Button
                        font: "bold 16px sans-serif",
                        editable: true
                    },
                    new go.Binding("text", "name").makeTwoWay())
            ) // end Table Panel
        );

    // default template
    myDiagram.nodeTemplate = entityTemplate;

    // attribute template
    var attributeTemplate =$(go.Node, "Auto",
        {
            selectionAdorned: true,
            selectionAdornmentTemplate: attributeAdornment,
            resizable: false,
            layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
            linkValidation: function(fromNode, fromGraphObject, toNode, toGraphObject){
                return fromNode.findLinksTo(toNode).count + toNode.findLnksTo(fromNode).count < 1;
            }
        },
        new go.Binding("location", "location").makeTwoWay(),
        $(go.Shape, "Circle",
            {
                fill: 'lightblue',
                portId: "",
                stroke: colors.lightblue,
                cursor: "pointer",
                fromSpot: go.Spot.AllSides,
                toSpot: go.Spot.AllSides,
                strokeWidth: 2,
                fromLinkableDuplicates: false, toLinkableDuplicates: false
            },
                new go.Binding("fromLinkable", "from").makeTwoWay(),
                new go.Binding("toLinkable", "to").makeTwoWay()),
        // the table header
        $(go.TextBlock,{
                font: "bold 12px monospace",
                margin: new go.Margin(0, 0, 0, 0),  // leave room for Button
            },
            new go.Binding("text","name").makeTwoWay(),
            new go.Binding("isUnderline", "underline")
        )
    );

    // add all template
    var templateMap = new go.Map();
    templateMap.add("Entity", entityTemplate);
    templateMap.add("Attribute",attributeTemplate);
    // default
    templateMap.add("",entityTemplate);

    myDiagram.nodeTemplateMap = templateMap;

    // relation
    var relationLink = $(go.Link,  // the whole link panel
        {
            selectionAdorned: true,
            layerName: "Foreground",
            reshapable: true,
            routing: go.Link.AvoidsNodes,
            corner: 5,
            curve: go.Link.JumpOver
        },
        $(go.Shape,  // the link shape
            {stroke: "#303B45", strokeWidth: 2.5 }),
        $(go.Panel, "Auto",  // this whole Panel is a link label
            $(go.Shape, "Diamond", {
                fill: "yellow",
                stroke: "gray",
                width: 100,
                height: 40}),
            $(go.TextBlock,  textStyle(),
                {   margin: 3,
                    textAlign: "center",
                    segmentIndex: -2,
                    segmentOffset: new go.Point(NaN, NaN),
                    segmentOrientation: go.Link.OrientUpright,
                    font: "bold 14px sans-serif",
                    stroke: "#1967B3",
                },
                new go.Binding("text", "relation").makeTwoWay())
        ),
        $(go.TextBlock, textStyle(), // the "from" label
            {
                textAlign: "center",
                font: "bold 14px sans-serif",
                stroke: "#1967B3",
                segmentIndex: 0,
                segmentOffset: new go.Point(NaN, NaN),
                segmentOrientation: go.Link.OrientUpright
            },
            new go.Binding("text", "fromText").makeTwoWay()),
        $(go.TextBlock, textStyle(), // the "to" label
            {
                textAlign: "center",
                font: "bold 14px sans-serif",
                stroke: "#1967B3",
                segmentIndex: -1,
                segmentOffset: new go.Point(NaN, NaN),
                segmentOrientation: go.Link.OrientUpright
            },
            new go.Binding("text", "toText").makeTwoWay())
    );

    var normalLink = $(go.Link,
        {
            selectionAdorned: true,
            layerName: "Foreground",
            reshapable: true,
            routing: go.Link.AvoidsNodes,
            corner: 5,
            curve: go.Link.JumpOver
        },
        $(go.Shape,  // the link shape
            {stroke: "#e8c446", strokeWidth: 2.5 }),
        //todo to delete
        $(go.TextBlock, textStyle(), // the "from" label
            {
                textAlign: "center",
                font: "bold 14px sans-serif",
                stroke: "#1967B3",
                segmentIndex: 0,
                segmentOffset: new go.Point(NaN, NaN),
                segmentOrientation: go.Link.OrientUpright
            },
            new go.Binding("text", "fromText").makeTwoWay()),
        $(go.TextBlock, textStyle(), // the "to" label
            {
                textAlign: "center",
                font: "bold 14px sans-serif",
                stroke: "#1967B3",
                segmentIndex: -1,
                segmentOffset: new go.Point(NaN, NaN),
                segmentOrientation: go.Link.OrientUpright
            },
            new go.Binding("text", "toText").makeTwoWay())
    );

    var linkTemplateMap = new go.Map();
    linkTemplateMap.add("relationLink", relationLink);
    linkTemplateMap.add("normalLink",normalLink);
    // default
    linkTemplateMap.add("",relationLink);
    myDiagram.linkTemplateMap = linkTemplateMap;

    myDiagram.model = new go.GraphLinksModel(
        {
            copiesArrays: true,
            copiesArrayObjects: true,
            nodeDataArray: [],
            linkDataArray: []
        });

    //listen edit the entity and relation name
    myDiagram.addDiagramListener("TextEdited",(e) => {
        if ("relation" in e.subject.part.qb) { // identify the changed textBlock
            const id = e.subject.part.qb.key;
            let firstCardinality = e.subject.part.qb.fromText;
            const firstEntityID = e.subject.part.qb.from;
            const secondEntityID = e.subject.part.qb.to;
            let secondCardinality = e.subject.part.qb.toText;
            const name = e.subject.part.qb.relation;

            firstCardinality = findRelationCode(firstCardinality);
            secondCardinality = findRelationCode(secondCardinality);

            modifyRelation(id,firstEntityID,secondEntityID,firstCardinality,secondCardinality,name);
        }else{
            const id = e.subject.part.qb.key;
            const name =  e.subject.part.qb.name;
            const layoutX = e.subject.part.qb.location.x;
            const layoutY = e.subject.part.qb.location.y;
            updateEntity(id,name,layoutX,layoutY);
        }
    });

    //listen node movement
    myDiagram.addDiagramListener("SelectionMoved",(e) => {
        const selectNode = e.diagram.selection.first();
        const entityLocationX = selectNode.location.x;
        const entityLocationY = selectNode.location.y;
        const id = selectNode.key;
        if (selectNode.category === "Attribute"){
            const dataType = selectNode.qb.dataType;
            const isPrimay = selectNode.qb.isPrimary;
            const name = selectNode.qb.name;
            //TODO:function update Attribue
        }else{
            const name = selectNode.qb.name;
            updateEntity(id,name,entityLocationX,entityLocationY);
        }
    });

    // 1. insert, delete entity and relation 2. delete attribute
    myDiagram.addModelChangedListener(function(evt) {
        // ignore unimportant Transaction events
        if (!evt.isTransactionFinished) return;
        var txn = evt.object;  // a Transaction
        if (txn === null) return;
        // iterate over all of the actual ChangedEvents of the Transaction
        txn.changes.each(function(e) {
            if (e.change === go.ChangedEvent.Insert && e.modelChange === "linkDataArray") {
                if  (!("category" in e.newValue)) {
                    // identity if it is normal link
                    e.newValue.relation = "has";
                    e.newValue.fromText = "1:1";
                    e.newValue.toText = "1:1";
                    //create relation
                    const firstEntityID = e.newValue.from;
                    const secondEntityID = e.newValue.to;
                    const name = e.newValue.relation;
                    let firstCardinality = e.newValue.fromText;
                    let secondCardinality = e.newValue.toText;

                    firstCardinality = findRelationCode(firstCardinality);
                    secondCardinality = findRelationCode(secondCardinality);
                    console.log(secondCardinality);
                    console.log(firstCardinality);
                    e.newValue.key = createRelation(name,firstEntityID,secondEntityID,firstCardinality,secondCardinality);
                }
            } else if (e.change === go.ChangedEvent.Remove && e.modelChange === "linkDataArray") {
                if  (!("category" in e.oldValue)){
                    //delete attribute
                    const id = e.oldValue.key;
                    deleteRelation(id);
                }
            } else if (e.change === go.ChangedEvent.Insert && e.modelChange === "nodeDataArray") {
                e.newValue.name = e.newValue.name+entityCounter.toString();
                const entityName = e.newValue.name;
                const layoutX = e.newValue.location.x;
                const layoutY = e.newValue.location.y;
                entityCounter++;
                if (!("category" in e.newValue)){
                    //create entity
                    e.newValue.key = createEntity(entityName,layoutX,layoutY);
                    save();
                    load();
                }
            } else if (e.change === go.ChangedEvent.Remove && e.modelChange === "nodeDataArray") {
                const id = e.oldValue.key;
                if ("category" in e.oldValue){
                    //delete attribute
                    deleteAttribute(id);
                } else {
                    deleteEntity(id);
                }
            }
        });
    });

    // 1. show if the model is changed(By adding * at the url)
    myDiagram.addDiagramListener("Modified", e => {
        var button = document.getElementById("SaveButton");
        if (button) button.disabled = !myDiagram.isModified;
        var idx = document.title.indexOf("*");
        if (myDiagram.isModified) {
            if (idx < 0) document.title += "*";
        } else {
            if (idx >= 0) document.title = document.title.slice(0, idx);
        }
    });

    function changedSelection(e){
        var tmpNodes = new go.List();
        myDiagram.nodes.each(function (node) {
            if (node.data.category == "Attribute" && node.isSelected) {
                tmpNodes.push(node);
                modifyAttributeClick();
            }
        });
    }
    /*
    Get the current View Id and load the model
     */
    // const id =  location.href.substring(location.href.indexOf("id=")+3);
    // myDiagram.model = go.Model.fromJson(getView(id));
    load()
}  // end init
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
            id:id,
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
        url : "http://146.169.52.81:8080/er/entity/create",
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
        if(("category" in node.data) && node.data.entityId === id){
            myDiagram.model.removeNodeData(node.data);
        }
    });
    let Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        type : "POST",
        url : "http://146.169.52.81:8080/er/entity/delete",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function(result) {
        }, error : function(result) {
        }
    });
}

function updateEntity(id,name,layoutX,layoutY){
    var Obj ={
        id:id,
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
        "id": id,
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
                "layoutX":pos.x,
                "layoutY":pos.y
            }
        }
        info = JSON.stringify(info);
        $.ajax({
            type : "POST",
            // url : "http://127.0.0.1:8000/er/attribute/create",
            url: "http://146.169.52.81:8080/er/attribute/create",
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
                        attributeData.key = result.data.id;
                        myDiagram.model.addNodeData(attributeData);
                        save();
                        load();
                        // new link
                        var link = {
                            from:myDiagram.model.getKeyForNodeData(selectedEData),
                            to:myDiagram.model.getKeyForNodeData(attributeData),category: "normalLink"
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

const RCODE = {
    UNKNOWN:0,
    ZeroToOne:1,
    ZeroToMany:2,
    OneToOne:3,
    OneToMany:4
}

const RNAME = {
    UNKNOWN:"",
    ZeroToOne:"0:1",
    ZeroToMany:"0:N",
    OneToOne:"1:1",
    OneToMany:"1:N"
}

function findRelationCode(relationText){
    const index = Object.values(RNAME).indexOf(relationText);
    return Object.values(RCODE)[index];
}


const DATATYPE = {
    UNKNOWN:0,
    CHAR:1,
    VARCHAR:2,
    TEXT:3,
    TINYINT:4,
    SMALLINT:5,
    INT:6,
    BIGINT:7,
    FLOAT:8,
    DOUBLE:9,
    DATETIME:10
}
let findDataType = (value, compare = (a, b) => a === b) => {
    return Object.keys(DATATYPE).find(k => compare(DATATYPE[k], value))
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
        // "attributeID": node.data.id,
        "viewID":viewID,
        "entityID":entityId,
        "name": name,
        "dataType": datatype,
        "isPrimay": isPrimary,
        "allowNotNull":allowNotNull,
        "layoutInfo": {
            "layoutX": node.data.location.x,
            "layoutY": node.data.location.y
        }
    }
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
