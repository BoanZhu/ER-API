

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


    // Common text styling
    function textStyle() {
        return {
            margin: 6,
            wrap: go.TextBlock.WrapFit,
            textAlign: "center",
            editable: true,
        }
    }

    // define the Node template, representing an entity
    var entityTemplate =
        $(go.Node, "Auto",  // the whole node panel
            {
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
                linkValidation: function(fromNode, fromGraphObject, toNode, toGraphObject){
                    return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 1;
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
                        editable: false
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
            $(go.Shape, "Diamond", { fill: "yellow", stroke: "gray",width: 100, height: 40 }),
            $(go.TextBlock, textStyle(),
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

            // TODO: test API access
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

                // TODO: test API access
                // e.newValue.key = createRelation(fromEntityName,toEntityName);
                console.log(e.newValue.key);

            } else if (e.change === go.ChangedEvent.Remove && e.modelChange === "linkDataArray") {

                const fromEntityName = e.oldValue.from;
                const toEntityName = e.oldValue.to;
                const relationId = e.oldValue.key;

                // TODO: test API access
                // deleteRelation(relationId,fromEntityName,toEntityName);
                console.log(evt.propertyName + " removed link: " + e.oldValue);
            }

        });
    });

    function addAttribute(e, obj) {
        var adorn = obj.part;
        if (adorn === null) return;
        e.handled = true;
        var arr = adorn.adornedPart.data.items;
        myDiagram.startTransaction("add item");
        myDiagram.model.addArrayItem(arr, {});
        myDiagram.commitTransaction("add item");
    }

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

    load()
}  // end init

/*
Relation functions
 */
function modifyRelation(relationId,fromEntityName,toEntityName,fromCardinality,toCardinality,relationName) {

    const viewId =  location.href.substring(location.href.indexOf("id=")+1);

    $.getJSON("http://localhost:8000/createRelation?" + "&viewId=" + viewId + "&relationId = " + relationId +
        "&fromEntityName=" + fromEntityName +
        "&toEntityName=" + toEntityName+
        "&fromCardinality=" + fromCardinality+
        "&toCardinality=" + toCardinality+
        "&relationName=" + relationName,function (res) {
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });
}

function createRelation(fromEntityId, toEntityId) { //return request ID

    const viewId =  location.href.substring(location.href.indexOf("id=")+1);
    var relationId;

    $.getJSON("http://localhost:8000/createRelation?" + "&viewId=" + viewId + "&fromEntityId" + fromEntityId+"toEntityId"+toEntityId,function (res) {
        //todo get the relationId
        relationId = res.id;
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });
    return relationId;
}

function deleteRelation(relationId) {

    const viewId = location.href.substring(location.href.indexOf("id=") + 1);
    $.getJSON("http://localhost:8000/deleteRelation?" + "id=" + relationId + "&viewId=" + viewId, function (res) {
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });
}




