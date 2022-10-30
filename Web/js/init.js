function init() {
    const $ = go.GraphObject.make;  // for conciseness in defining templates
    myDiagram = $(go.Diagram, "myDiagramDiv",  // must name or refer to the DIV HTML element
        {
            allowDelete: true,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            layout: $(go.ForceDirectedLayout, { isInitial: false, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "clickCreatingTool.archetypeNodeData": { name:"new node",from:true,to:true},
            "undoManager.isEnabled": true,
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
                doubleClick: function(e, node) {
                    // . . . now node is the Node that was double-clicked
                    var data = node.data;
                    // now data has all of the custom app-specific properties that you have
                    // supplied for that node, in the Model.nodeDataArray
                }

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
                return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 1;
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
            new go.Binding("text","name")
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

    myDiagram.addDiagramListener("TextEdited",(e) => {
        if ("relation" in e.subject.part.qb) { // identify the changed textBlock
            const id = e.subject.part.qb.key;
            const firstCardinality = e.subject.part.qb.fromText;
            const firstEntityID = e.subject.part.qb.from;
            const secondEntityID = e.subject.part.qb.to;
            const secondCardinality = e.subject.part.qb.toText;
            const name = e.subject.part.qb.relation;
            //todo 检测两个节点类型是不是一样 why?
            //modifyRelation(id,firstEntityID,secondEntityID,firstCardinality,secondCardinality,name);
            console.log(e.subject.text);
        }else{
            const id = e.subject.part.qb.key;
            const name =  e.subject.part.qb.name;
            const layoutX = e.subject.part.qb.location.x;
            const layoutY = e.subject.part.qb.location.y;
            //updateEntity(id,name,layoutX,layoutY);
        }
    });

    myDiagram.addDiagramListener("SelectionMoved",(e) => {
    //
    //     const selectNode = e.diagram.selection.first();
    //     const id = selectNode.key; //TODO:cannot get the name
    //     const entityLocationX = selectNode.location.x;
    //     const entityLocationY = selectNode.location.y;
    //     updateEntity(id,entityLocationX,entityLocationY);
    //
     //   console.log("true");
    });

    myDiagram.addModelChangedListener(function(evt) {
        // ignore unimportant Transaction events
        if (!evt.isTransactionFinished) return;
        var txn = evt.object;  // a Transaction
        if (txn === null) return;

        // iterate over all of the actual ChangedEvents of the Transaction
        txn.changes.each(function(e) {
            if (e.change === go.ChangedEvent.Insert && e.modelChange === "linkDataArray") {
                e.newValue.relation = "has";
                //create relation
                const firstEntityID = e.newValue.from;
                const secondEntityID = e.newValue.to;
                const name = e.newValue.relation;
                // e.newValue.key = createRelation(firstEntityID,secondEntityID,name);
                console.log(e.newValue.key);
            } else if (e.change === go.ChangedEvent.Remove && e.modelChange === "linkDataArray") {
                // delete relation
                const id = e.oldValue.key;
                // deleteRelation(id);
                console.log(evt.propertyName + " removed link: " + e.oldValue);
            } else if (e.change === go.ChangedEvent.Insert && e.modelChange === "nodeDataArray") {
                //create entity
                const name = e.newValue.name;
                const layoutX = e.newValue.location.x;
                const layoutY = e.newValue.location.y;
                // e.newValue.key = createEntity(name,layoutX,layoutY);
            } else if (e.change === go.ChangedEvent.Remove && e.modelChange === "nodeDataArray") {
                const id = e.oldValue.key;
                //todo 需要判断node类型（attribute & entity
                deleteEntity(id);
            }
        });
    });

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


    // add attribute
    function addAttr(){
        var tmpNodes = new go.List();
        myDiagram.startTransaction("add attributes");
        myDiagram.nodes.each(function (node){
            if(node.isSelected){
                tmpNodes.push(node);
            }
        })
        tmpNodes.each(function (part){
            var selectedEntity = part;
            var selectedEData =part.data;
            // new attribute
            var attributeData = {name:"NewA",category:"Attribute"};
            var pos = selectedEntity.location.copy();
            var angle = Math.random()*Math.PI*2;
            pos.x+=Math.cos(angle)*120;
            pos.y+=Math.sin(angle)*120;
            attributeData.location = pos;
            attributeData.isPrimary = 0;
            attributeData.dataType = 0;
            //todo for test
            attributeData.id = 678;
            myDiagram.model.addNodeData(attributeData);
            // new link
            var link = {
                from:myDiagram.model.getKeyForNodeData(selectedEData),
                to:myDiagram.model.getKeyForNodeData(attributeData),category: "normalLink"
            };
            myDiagram.model.addLinkData(link);
            const viewId =  location.href.substring(location.href.indexOf("id=")+1);
            var info ={
                "viewID":viewId,
                "entityID":"345",
                "name":"newA",
                "dataType":0,
                "layoutInfo":{
                    "x":pos.x,
                    "y":pos.y
                }
            }
            info = JSON.stringify(info);
            // $.ajax({
            //     type : "POST",
            //     url : "http://localhost:8000/er/attribute/create",
            //     traditional : true,
            //     data : info,
            //     withCredentials:false,
            //     dataType : 'json',
            //     success : function(result) {
            //         if(result.code == 0) {
            //             $(function(){
            //                 var node=myDiagram.model.findNodeForData(attributeData);
            //                 node.data.keyDB = result.data.id;
            //             });
            //         }
            //     }, error : function(res) {
            //     }
            // });
        });
        myDiagram.commitTransaction("add attributes");
    }

    // edit attribute
    // var inspector = new Inspector('myInfo', myDiagram,
    //     {
    //         includesOwnProperties: false,
    //         properties: {
    //             "name": { show: Inspector.showIfPresent },
    //             "PrimaryKey": { show: Inspector.showIfPresent  },
    //             "DataType": { show: Inspector.showIfPresent }
    //         }
    //     });

    load()
}  // end init



/*
Entity functions
*/
function createEntity(name,layoutX,layoutY){
    /*
    create function
     */
    //todo:getViewID
}

function deleteEntity(id){
    /*
    delete function
     */
}

function updateEntity(id,name,layoutX,layoutY){
    /*
     update function
     */
}


/*
Relation functions
 */

function createRelation(firstEntityID, secondEntityID,name) { //return request ID

    //todo:getViewID

    const viewID =  location.href.substring(location.href.indexOf("id=")+1);
    var relationID;

    $.getJSON("http://localhost:8000/er/relationship/create?" + "&viewID=" + viewID +
        "&firstEntityID" + firstEntityID+
        "&secondEntityID"+secondEntityID+
        "name"+name,function (res) {
        //todo get the relationId
        relationID = res.id;
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });
    return relationID;
}

function modifyRelation(id,firstEntityID,secondEntityID,fromCardinality,secondCardinality,name) {

    $.getJSON("http://localhost:8000/er/relationship/update?" +
        "&id = " + id +
        "&firstEntityID=" + firstEntityID +
        "&secondEntityID=" + secondEntityID+
        "&firstCardinality=" + firstCardinality+
        "&secondCardinality=" + secondCardinality+
        "&name=" + name,function (res) {
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });
}

function deleteRelation(id) {
    $.getJSON("http://localhost:8000/er/relationship/delete?" + "id=" + id, function (res) {
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });
}

/*
    attribute functions
*/

function deleteAttribute(attributeID){

}
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
        document.getElementById("selectedAttributeKey").value = selectedAData.key;
        console.log(document.getElementById("selectedAttributeKey").value);
        if(part.data.category == 'Attribute'){
            document.getElementById("attributeNameInfo").value = selectedAData.name;
            //todo 现在假设直接是字符串
            document.getElementById("datatypeChoices").value = 'int';
            //todo 还是要统一（01 or true，false）先用01
            var isPrimary = document.getElementById("isPrimaryKey");
            if(part.data.isPrimary==0){
                isPrimary.checked = false;
            }else {isPrimary.checked = true;}
        }

    });
    myDiagram.commitTransaction("edit attributes");
}

//submit updates on attributes
function modifyAttribute(){
    // get name, isPrimary, datatype
    const name = document.getElementById("attributeNameInfo").value;
    const isPrimary = document.getElementById("selectedAttributeKey").checked; //0 is not a primary key
    const datatype = document.getElementById("datatypeChoices").value;
    const key = document.getElementById("selectedAttributeKey").value;
    // update model
    var node = myDiagram.findNodeForKey(key);
    node.data.name = name;
    node.data.isPrimary = isPrimary;
    node.data.dataType = datatype;
    var newJson = myDiagram.model.toJSON();
    myDiagram.model = go.Model.fromJson(newJson);
    document.getElementById("mySavedModel").value = myDiagram.model.toJson();

    //todo: send data to backend
    var info ={
        "attributeID": node.data.id,
        "name": name,
        "dataType": datatype,
        "isPrimay": isPrimary,
        "layoutInfo": {
            "layoutX": node.data.location.x,
            "layoutY": node.data.location.y
        }
    }
}
