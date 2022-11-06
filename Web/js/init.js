var entityCounter = 0;
var attributeCounter = 0;
var weakEntityCounter = 0;
var subsetCounter = 0;
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
            layout: $(go.ForceDirectedLayout, {isInitial: false, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "clickCreatingTool.archetypeNodeData": {name: "New Entity", from: true, to: true},
            "undoManager.isEnabled": true,
            "maxSelectionCount": 1,
            "ChangedSelection": changedSelection,

        });

    function makeButton(text, action, visiblePredicate) {
        return $("ContextMenuButton",
            $(go.TextBlock, text),
            { click: action },
            // visiblePredicate ? new go.Binding("visible", "", (o, e) => o.diagram ? visiblePredicate(o, e) : false).ofObject() : {}
        );
    }
    // strong entity node menu
    const nodeMenu =  // context menu for each Node
        $("ContextMenu",
            makeButton("Add Attribute",
                (e, obj) => addAttr()),
            makeButton("Add Subset",
                (e, obj) => createSubset()),
            makeButton("Add Weak Entity",
                (e, obj) => createWeakEntity()),
        );

    /*
    All adornment
    */
    // adornment for weak entity
    const weakEntityAdornment =
        $(go.Adornment, "Spot",
            $(go.Panel, "Auto",
                $(go.Shape, {fill: null, stroke: "dodgerblue", strokeWidth: 3}),
                $(go.Placeholder)),
            $("Button", {alignment: go.Spot.TopRight, click: addAttr},
                $(go.Shape, "PlusLine", {desiredSize: new go.Size(6, 6)})));

    // // adornment for attribute
    // const attributeAdornment =
    //     $(go.Adornment, "Spot",
    //         $(go.Panel, "Auto",
    //             $(go.Shape, {fill: null, stroke: "dodgerblue", strokeWidth: 3}),
    //             $(go.Placeholder)),
    //         $("Button", {alignment: go.Spot.TopRight, click: modifyAttributeClick},
    //             $(go.Shape, "MinusLine", {desiredSize: new go.Size(6, 6)})),
    //     );
    /*
     4 ports
     */
    const leftPort = $(go.Panel, "Vertical", {row: 1, column: 0},
        $(go.Shape,
            {width: 3, height: 3, portId: "L",
                toSpot: go.Spot.Left,fromSpot:go.Spot.Left,
                fromLinkable: true,toLinkable: true}));
    const rightPort = $(go.Panel, "Vertical", {row: 1, column: 2},
        $(go.Shape,  // the "B" port
            {width: 3, height: 3, portId: "R", toSpot: go.Spot.Right,fromSpot:go.Spot.Right,
                fromLinkable: true,toLinkable: true}));
    const bottomPort = $(go.Panel, "Horizontal", {row:2, column: 1},
        $(go.Shape,  // the "B" port
            {width: 3, height: 3, portId: "B", toSpot: go.Spot.Bottom,fromSpot:go.Spot.Bottom,
                fromLinkable: true,toLinkable: true}));
    const topPort = $(go.Panel, "Vertical",{row: 0, column: 1},
        $(go.Shape,  // the "B" port
            {width: 3, height: 3, portId: "U", toSpot: go.Spot.Top,fromSpot:go.Spot.Top,
                fromLinkable: true,toLinkable: true}));
    /*
        All Node(Entity+Attribute) templates
     */
    //strong entity template
    const entityTemplate =
        $(go.Node, "Table",  // the whole node panel
            {
                locationObject: "BODY",
                locationSpot: go.Spot.Center,
                selectionObjectName: "BODY",
                //contextMenu
            },
            {
                locationSpot: go.Spot.Center,
                contextMenu:nodeMenu,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
                linkValidation: function (fromNode, fromGraphObject, toNode, toGraphObject) {
                    // todo 必须要允许俩，才能改port，啊
                    return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 2;
                },
            },
            new go.Binding("location", "location").makeTwoWay(),
            // whenever the PanelExpanderButton changes the visible property of the "LIST" panel,
            // clear out any desiredSize set by the ResizingTool.
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            // the body
            $(go.Panel, "Auto",
                {
                    row: 1, column: 1, name: "BODY",
                },
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
                    }),
                $(go.TextBlock, textStyle(),
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(5, 24, 5, 2),  // leave room for Button
                        font: "bold 16px sans-serif",
                        editable: true
                    },
                    new go.Binding("text", "name").makeTwoWay()),
                // new go.Binding("fromLinkable", "from").makeTwoWay(), new go.Binding("toLinkable", "to").makeTwoWay()
            ),// end Auto Panel Body
            // left port
            leftPort,rightPort,topPort,bottomPort
        );

    //relationNodeTemplate
    const relationTemplate =
        $(go.Node, "Table",  // the whole node panel
            {
                locationObject: "BODY",
                locationSpot:go.Spot.Center,
                selectionObjectName: "BODY"
            },
            {
                locationSpot: go.Spot.Center,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
                linkValidation: function(fromNode, fromGraphObject, toNode, toGraphObject){
                    return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 2;
                },
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            $(go.Panel,"Auto",
                {row: 1, column: 1, name: "BODY"},
                $(go.Shape, "Diamond",
                    {
                        fill: colors.lightyellow,
                        portId: "",
                        stroke: colors.lightblue,
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 3,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false
                    },
                    new go.Binding("fromLinkable", "from").makeTwoWay(), new go.Binding("toLinkable", "to").makeTwoWay()),
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
                )
            ),
            $(go.Panel, "Vertical", {row: 1, column: 0},
                $(go.Shape,
                    {width: 3, height: 3, portId: "L",
                        toSpot: go.Spot.Left,fromSpot:go.Spot.Left,
                        fromLinkable: true,toLinkable: true})),
            $(go.Panel, "Vertical", {row: 1, column: 2},
                $(go.Shape,  // the "B" port
                    {width: 3, height: 3, portId: "R", toSpot: go.Spot.Right,fromSpot:go.Spot.Right,
                        fromLinkable: true,toLinkable: true})),
            $(go.Panel, "Horizontal", {row:2, column: 1},
                $(go.Shape,  // the "B" port
                    {width: 3, height: 3, portId: "B", toSpot: go.Spot.Bottom,fromSpot:go.Spot.Bottom,
                        fromLinkable: true,toLinkable: true})),
            $(go.Panel, "Vertical",{row: 0, column: 1},
                $(go.Shape,  // the "B" port
                    {width: 3, height: 3, portId: "U", toSpot: go.Spot.Top,fromSpot:go.Spot.Top,
                        fromLinkable: true,toLinkable: true}))
        );

    // weak entity template
    const weakEntityTemplate =
        $(go.Node, "Table",  // the whole node panel
            {
                locationObject: "BODY",
                locationSpot:go.Spot.Center,
                selectionObjectName: "BODY"
            },
            {
                locationSpot: go.Spot.Center,
                selectionAdornmentTemplate: weakEntityAdornment,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
                linkValidation: function(fromNode, fromGraphObject, toNode, toGraphObject){
                    return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 2;
                },
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            $(go.Panel,"Auto",
                {
                    row: 1, column: 1, name: "BODY",
                },
                $(go.Shape, "WeakEntity",
                    {
                        fill: 'white',
                        portId: "",
                        stroke: colors.lightgrey,
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 3,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false
                    }),
                $(go.TextBlock, textStyle(),
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(5, 24, 5, 2),  // leave room for Button
                        font: "bold 16px sans-serif",
                        editable: true
                    },
                    new go.Binding("text", "name").makeTwoWay()),
            ), //end Auto Panel Body
                // new go.Binding("fromLinkable", "from").makeTwoWay(), new go.Binding("toLinkable", "to").makeTwoWay()),
        $(go.Panel, "Vertical", {row: 1, column: 0},
            $(go.Shape,
            {width: 3, height: 3, portId: "L",
                toSpot: go.Spot.Left,fromSpot:go.Spot.Left,
                fromLinkable: true,toLinkable: true})),
        $(go.Panel, "Vertical", {row: 1, column: 2},
            $(go.Shape,  // the "B" port
            {width: 3, height: 3, portId: "R", toSpot: go.Spot.Right,fromSpot:go.Spot.Right,
                fromLinkable: true,toLinkable: true})),
        $(go.Panel, "Horizontal", {row:2, column: 1},
            $(go.Shape,  // the "B" port
            {width: 3, height: 3, portId: "B", toSpot: go.Spot.Bottom,fromSpot:go.Spot.Bottom,
                fromLinkable: true,toLinkable: true})),
        $(go.Panel, "Vertical",{row: 0, column: 1},
            $(go.Shape,  // the "B" port
            {width: 3, height: 3, portId: "U", toSpot: go.Spot.Top,fromSpot:go.Spot.Top,
                fromLinkable: true,toLinkable: true}))
);

    // subset template
    const subsetTemplate =
        $(go.Node, "Table",  // the whole node panel
            {
                locationObject: "BODY",
                locationSpot: go.Spot.Center,
                selectionObjectName: "BODY",
            },
            {
                locationSpot: go.Spot.Center,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
                linkValidation: function(fromNode, fromGraphObject, toNode, toGraphObject){
                    return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 2;
                },
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            // the table header
            $(go.Panel, "Auto",
                {row: 1, column: 1, name: "BODY"},
                $(go.Shape, "RoundedRectangle",
                    {
                        fill: colors.lightyellow,
                        portId: "",
                        stroke: colors.lightyellow,
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 3,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false
                    },
                    new go.Binding("fromLinkable", "from").makeTwoWay(), new go.Binding("toLinkable", "to").makeTwoWay()),
                $(go.TextBlock,textStyle(),
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(5, 24, 5, 2),  // leave room for Button
                        font: "bold 16px sans-serif",
                        editable: true
                    },
                    new go.Binding("text", "name").makeTwoWay()),
                $(go.RowColumnDefinition, { row: 0, sizing: go.RowColumnDefinition.None }),
            ), // end Table Panel
            $(go.Panel, "Vertical", {row: 1, column: 0},
                $(go.Shape,
                    {width: 3, height: 3, portId: "L",
                        toSpot: go.Spot.Left,fromSpot:go.Spot.Left,
                        fromLinkable: true,toLinkable: true})),
            $(go.Panel, "Vertical", {row: 1, column: 2},
                $(go.Shape,  // the "B" port
                    {width: 3, height: 3, portId: "R", toSpot: go.Spot.Right,fromSpot:go.Spot.Right,
                        fromLinkable: true,toLinkable: true})),
            $(go.Panel, "Horizontal", {row:2, column: 1},
                $(go.Shape,  // the "B" port
                    {width: 3, height: 3, portId: "B", toSpot: go.Spot.Bottom,fromSpot:go.Spot.Bottom,
                        fromLinkable: true,toLinkable: true})),
            $(go.Panel, "Vertical",{row: 0, column: 1},
                $(go.Shape,  // the "B" port
                    {width: 3, height: 3, portId: "U", toSpot: go.Spot.Top,fromSpot:go.Spot.Top,
                        fromLinkable: true,toLinkable: true}))
        );

    // attribute template
    var attributeTemplate =$(go.Node, "Table",
        {
            locationObject: "BODY",
            locationSpot:go.Spot.Center,
            selectionObjectName: "BODY"
        },
        {
            selectionAdorned: true,
            // selectionAdornmentTemplate: attributeAdornment,
            resizable: false,
            layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
            linkValidation: function(fromNode, fromGraphObject, toNode, toGraphObject){
                return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 2;
            }
        },
        new go.Binding("location", "location").makeTwoWay(),
        $(go.Panel,"Auto",
            {row: 1, column: 1, name: "BODY"},
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
            $(go.TextBlock,{
                    font: "bold 12px monospace",
                    margin: new go.Margin(0, 0, 0, 0),  // leave room for Button
                },
                new go.Binding("text","name").makeTwoWay(),
                new go.Binding("isUnderline", "underline")
            )),
        $(go.Panel, "Vertical", {row: 1, column: 0},
            $(go.Shape,
                {width: 3, height: 3, portId: "L",
                    toSpot: go.Spot.Left,fromSpot:go.Spot.Left,
                    fromLinkable: true,toLinkable: true})),
        $(go.Panel, "Vertical", {row: 1, column: 2},
            $(go.Shape,  // the "B" port
                {width: 3, height: 3, portId: "R", toSpot: go.Spot.Right,fromSpot:go.Spot.Right,
                    fromLinkable: true,toLinkable: true})),
        $(go.Panel, "Horizontal", {row:2, column: 1},
            $(go.Shape,  // the "B" port
                {width: 3, height: 3, portId: "B", toSpot: go.Spot.Bottom,fromSpot:go.Spot.Bottom,
                    fromLinkable: true,toLinkable: true})),
        $(go.Panel, "Vertical",{row: 0, column: 1},
            $(go.Shape,  // the "B" port
                {width: 3, height: 3, portId: "U", toSpot: go.Spot.Top,fromSpot:go.Spot.Top,
                    fromLinkable: true,toLinkable: true}))
    );

    // add all node template
    var templateMap = new go.Map();
    // default template
    myDiagram.nodeTemplate = entityTemplate;
    templateMap.add("",entityTemplate);
    templateMap.add("Entity", entityTemplate);
    templateMap.add("WeakEntity", weakEntityTemplate);
    templateMap.add("Subset",subsetTemplate);
    templateMap.add("Attribute",attributeTemplate);

    templateMap.add("RelationTemplate",relationTemplate);

    myDiagram.nodeTemplateMap = templateMap;

    // relation
    var relationLink = $(go.Link,  // the whole link panel
        {
            // deletable: false,
            selectionAdorned: true,
            layerName: "Foreground",
            reshapable: true,
            routing: go.Link.AvoidsNodes,
            corner: 5,
            curve: go.Link.JumpOver,
            relinkableFrom: true,
            relinkableTo: true
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
            curve: go.Link.JumpOver,
            relinkableFrom: true,
            relinkableTo: true
        },
        $(go.Shape,  // the link shape
            {stroke: "#e8c446", strokeWidth: 2.5 }),
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
    );

    var subsetLink = $(go.Link,
        {
            selectionAdorned: true,
            layerName: "Foreground",
            reshapable: true,
            routing: go.Link.AvoidsNodes,
            corner: 5,
            curve: go.Link.JumpOver,
            relinkableFrom: true,
            relinkableTo: true
        },
        $(go.Shape,  // the link shape
            {stroke: "#e8c446", strokeWidth: 2.5 }),
        $(go.Shape,   // the arrowhead
            { toArrow: "OpenTriangle", fill: null, stroke:"#e8c446",strokeWidth:2.5}),
    );

    var entityLink = $(go.Link,
        {
            selectionAdorned: true,
            layerName: "Foreground",
            reshapable: true,
            routing: go.Link.AvoidsNodes,
            corner: 5,
            curve: go.Link.JumpOver,
            relinkableFrom: true,
            relinkableTo: true
        },
        $(go.Shape,  // the link shape
            {stroke: "#000000", strokeWidth: 2.5 }),
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

    );


    var linkTemplateMap = new go.Map();
    linkTemplateMap.add("relationLink", relationLink);
    linkTemplateMap.add("normalLink",normalLink);
    linkTemplateMap.add("subsetLink",subsetLink);
    linkTemplateMap.add("entityLink",entityLink);
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

            if (typeof secondCardinality == "undefined" || typeof firstCardinality == "undefined") {
                alert("only accept following cardinality: null, 0:N, 1:1, 1:N, 0:1");
            }else {
                modifyRelation(id, firstEntityID, secondEntityID, firstCardinality, secondCardinality, name);
            }

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
                if (!("category" in e.newValue)) {

                    const firstEntityID = e.newValue.from;
                    const secondEntityID = e.newValue.to;
                    var fromPort = e.newValue.fromPort;
                    var toPort = e.newValue.toPort;

                    const relationNodeX = (myDiagram.findNodeForKey(e.newValue.from).location.x +
                        myDiagram.findNodeForKey(e.newValue.to).location.x)/2

                    const relationNodeY = (myDiagram.findNodeForKey(e.newValue.from).location.y +
                        myDiagram.findNodeForKey(e.newValue.to).location.y)/2;

                    myDiagram.rollbackTransaction(); //rollback transcation and create new node between e-e
                    myDiagram.model.addNodeData({"key": 123, "name":"test",
                        "location":{"class":"go.Point","x":relationNodeX,"y":relationNodeY},category:"RelationTemplate"});
                    //TODO: change the addNodeDate template with the new designed relation node
                    //TODO: API add node function will be caught by the listener

                    myDiagram.model.addLinkData({"from":firstEntityID,"to":123, fromText:"1:1",category:"entityLink",fromPort: fromPort,toPort: toPort});
                    myDiagram.model.addLinkData({"from":secondEntityID,"to":123, fromText:"1:1",category:"entityLink",fromPort: toPort,toPort: fromPort})

                    // // identity if it is normal link
                    // // myDiagram.commandHandler.undo();
                    // e.newValue.relation = "has";
                    // e.newValue.fromText = "1:1";
                    // e.newValue.toText = "1:1";
                    // //create relation
                    // const firstEntityID = e.newValue.from;
                    // const secondEntityID = e.newValue.to;
                    // const name = e.newValue.relation;
                    // let firstCardinality = e.newValue.fromText;
                    // let secondCardinality = e.newValue.toText;
                    //
                    // firstCardinality = findRelationCode(firstCardinality);
                    // secondCardinality = findRelationCode(secondCardinality);
                    // console.log(secondCardinality);
                    // console.log(firstCardinality);
                    // e.newValue.key = createRelation(name, firstEntityID, secondEntityID, firstCardinality, secondCardinality);
                    save();
                    load();
                }
                } else if (e.change === go.ChangedEvent.Remove && e.modelChange === "linkDataArray") {
                    if (!("category" in e.oldValue)) {
                        //delete attribute
                        const id = e.oldValue.key;
                        deleteRelation(id);
                    }
                } else if (e.change === go.ChangedEvent.Insert && e.modelChange === "nodeDataArray") {
                    e.newValue.name = e.newValue.name + entityCounter.toString();
                    const entityName = e.newValue.name;
                    const layoutX = e.newValue.location.x;
                    const layoutY = e.newValue.location.y;
                    entityCounter++;
                    if (!("category" in e.newValue)) {
                        //create entity
                        e.newValue.key = createEntity(entityName, layoutX, layoutY);
                        save();
                        load();
                    }
                } else if (e.change === go.ChangedEvent.Remove && e.modelChange === "nodeDataArray") {
                    const id = e.oldValue.key;
                    if ("category" in e.oldValue) {
                        // Attribute, WeakEntity, Subset
                        switch(e.oldValue.category){
                            case "Attribute"://delete attribute
                                deleteAttribute(id);
                                break;
                            case "WeakEntity":
                                deleteWeakEntity(id);
                                break;
                            case "Subset":
                                deleteSubset(id);
                                break;
                            default:break;
                        }
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
    const id =  location.href.substring(location.href.indexOf("id=")+3);
    // myDiagram.model = go.Model.fromJson(getView(id));
    myDiagram.model = new go.GraphLinksModel(
        { linkFromPortIdProperty: "fromPort",
            linkToPortIdProperty: "toPort",
            nodeDataArray: [],
            linkDataArray: []});
}  // end init

