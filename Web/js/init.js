

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
            "undoManager.isEnabled": true,
        });

    var colors = {
        'red': '#be4b15',
        'green': '#52ce60',
        'blue': '#6ea5f8',
        'lightred': '#fd8852',
        'lightblue': '#afd4fe',
        'lightgreen': '#b9e986',
        'pink': '#faadc1',
        'purple': '#d689ff',
        'orange': '#fdb400',
    }

    // Common text styling
    function textStyle() {
        return {
            margin: 6,
            wrap: go.TextBlock.WrapFit,
            textAlign: "center",
            editable: true,
        }
    }
    var addNodeAdornment =
        $(go.Adornment, "Spot",
            $(go.Panel, "Auto",
                $(go.Shape, { fill: null, stroke: "dodgerblue", strokeWidth: 3 }),
                $(go.Placeholder)),
            // the button to create a "next" node, at the top-right corner
            $("Button",
                {
                    alignment: go.Spot.TopRight,
                    click: addAttr()  // this function is defined below
                },
                $(go.Shape, "PlusLine", { desiredSize: new go.Size(6, 6) })
            )
        );

    // define the Node template, representing an entity
    var entityTemplate =
        $(go.Node, "Auto",  // the whole node panel
            {
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
            new go.Binding("fromLinkable", "from").makeTwoWay(),
            new go.Binding("toLinkable", "to").makeTwoWay()),

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
                    new go.Binding("text", "key").makeTwoWay())
            ) // end Table Panel
        );
    // default template
    myDiagram.nodeTemplate = entityTemplate;

    // define the Link template, representing a relationship
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
            const relationId = e.subject.part.qb.key;
            const fromEntityName = e.subject.part.qb.from;
            const toEntityName = e.subject.part.qb.to;
            const fromCardinality = e.subject.part.qb.fromText;
            const toCardinality = e.subject.part.qb.toText;
            const relationName = e.subject.part.qb.relation;
            //todo 检测两个节点类型是不是一样

            // modifyRelation(relationId,fromEntityName,toEntityName,fromCardinality,toCardinality,relationName);
            console.log(e.subject.text);
        }
    });

    // myDiagram.addDiagramListener("SelectionMoved",(e) => {
    //
    //     const selectNode = e.diagram.selection.first();
    //     const entityId = selectNode.key;
    //     const entityLocationX = selectNode.location.x;
    //     const entityLocationY = selectNode.location.y;
    //
    //     moveEntity(entityId,entityLocationX,entityLocationY);
    //
    // });

    myDiagram.addModelChangedListener(function(evt) {
        // ignore unimportant Transaction events
        if (!evt.isTransactionFinished) return;
        var txn = evt.object;  // a Transaction
        if (txn === null) return;

        // iterate over all of the actual ChangedEvents of the Transaction
        txn.changes.each(function(e) {
            //record relation insertions and removals
            if (e.change === go.ChangedEvent.Insert && e.modelChange === "linkDataArray") {

                const fromEntityName = e.newValue.from;
                const toEntityName = e.newValue.to;

                // e.newValue.key = createRelation(fromEntityName,toEntityName);
                console.log(e.newValue.key);

            } else if (e.change === go.ChangedEvent.Remove && e.modelChange === "linkDataArray") {

                const fromEntityName = e.oldValue.from;
                const toEntityName = e.oldValue.to;
                const relationId = e.oldValue.key;

                // deleteRelation(relationId,fromEntityName,toEntityName);
                console.log(evt.propertyName + " removed link: " + e.oldValue);
            }
        });
    });

    myDiagram.addDiagramListener("BackgroundDoubleClicked",
        function(e) { //e.diagram.lastInput.documentPoint
        entity={key:"text",location:e.diagram.lastInput.documentPoint,from:true,to:true};
        myDiagram.model.addNodeData(entity);

        console.log(myDiagram.model.nodeDataArray.last);

            // var tmp = myDiagram.findNodesByExample("text");
    });

    // attribute node template
    var attributeTemplate =$(go.Node, "Auto",
        {
            selectionAdorned: true,
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
                strokeWidth: 3,
                fromLinkableDuplicates: false, toLinkableDuplicates: false
            },
            new go.Binding("fromLinkable", "from").makeTwoWay(),
            new go.Binding("toLinkable", "to").makeTwoWay()),
        // the table header
        $(go.TextBlock,
            new go.Binding("text","key")
        )
    );

    // add all template
    var templateMap = new go.Map();
    templateMap.add("Entity", entityTemplate);
    templateMap.add("Attribute",attributeTemplate);
    // default
    templateMap.add("",entityTemplate);

    myDiagram.nodeTemplateMap = templateMap;

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
    function addAttr(){

    }

    load()
}  // end init

/*
Relation functions
 */
function modifyRelation(id,firstEntityID,secondEntityID,firstCardinality,secondCardinality,name) {

    const viewId =  location.href.substring(location.href.indexOf("id=")+1);

    $.getJSON("http://localhost:8000/er/relationship/update?" + "&viewId=" + viewId + "&id = " + id +
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

function createRelation(firstEntityID, secondEntityID) { //return request ID

    const viewID =  location.href.substring(location.href.indexOf("id=")+1);
    var relationID;

    $.getJSON("http://localhost:8000/er/relationship/create?" + "&viewID=" + viewID + "&fromEntityId" + firstEntityID+"toEntityId"+secondEntityID,function (res) {
        //todo get the relationId
        relationID = res.id;
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });
    return relationID;
}

function deleteRelation(id) {

    $.getJSON("http://localhost:8000/er/relationship/delete?" + "id=" + id, function (res) {
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });
}




