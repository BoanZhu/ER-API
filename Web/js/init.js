var entityCounter = 46;
var attributeCounter = 1;
var weakEntityCounter = 1;
var subsetCounter = 1;
var entityCounter = 24;
var attributeCounter = 5;
var weakEntityCounter = 4;
var subsetCounter = 0;
/*
Node
 */
const entityNodeCategory = "entity"
const weakEntityNodeCategory = "weakEntity"
const subsetEntityNodeCategory = "subset"
/*
lINk
 */
const ERLinkCard = "1:N"
const ERLinkCategory = "entityLink";
const EWLinkCategory = "weakLink";
const relationNodeName = "test";
const relationNodeCategory = "relation";
const prefixRelationNodeKey = "relation_"
let ERLinkCreateVerify =new Set(); // Value:"fromEntityIDRelationID"
const edgeIDFirst = "edgeIDFirst"; //from: strong entity side
const edgeIDSecond = "edgeIDSecond";  //to:weak entity side

/*
Constant
 */

const weakEntityLinkPort = -1;
const defaultWeakFromCard = "0:N";
const defaultWeakToCard = "1:1";

/*
Ports
 */
const PORTS = {
    "U":1,  // up
    "B":2,  // bottom
    "L":3,  // left
    "R":4,  // right
    "M":5,  // middle
}
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
            layout: $(go.ForceDirectedLayout, {isInitial: true, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "clickCreatingTool.archetypeNodeData": {name: "New Entity", category:entityNodeCategory, from: true, to: true},
            "undoManager.isEnabled": true,
            "maxSelectionCount": 1,
            "ChangedSelection": changedSelection,
            "linkingTool.linkValidation": isLinkValid,
            "relinkingTool.reconnectLink": isAllowReconnect
        });

    function makeButton(text, action) {
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

    // relation context Menu
    const relation_menu =  // context menu for each relation
        $("ContextMenu",
            makeButton("Add Attribute",
                (e, obj) => addAttr()),
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

    /*
     4 ports
     */

    function leftPort(){
        // L port
        return $(go.Panel, "Vertical", {row: 1, column: 0},
                $(go.Shape, {width: 3, height: 3, portId: 3, toSpot: go.Spot.Left,fromSpot:go.Spot.Left,
                    fromLinkable: true,toLinkable: true
                }));
    }
    function rightPort(){
        // R port
        return $(go.Panel, "Vertical", {row: 1, column: 2},
            $(go.Shape,  {width: 3, height: 3, portId: 4, toSpot: go.Spot.Right,fromSpot:go.Spot.Right,
                    fromLinkable: true,toLinkable: true}));
    }
    function bottomPort(){
        // B port
        return $(go.Panel, "Horizontal", {row:2, column: 1},
            $(go.Shape, {width: 3, height: 3, portId: 2, toSpot: go.Spot.Bottom,fromSpot:go.Spot.Bottom,
                    fromLinkable: true,toLinkable: true}));
    }
    function topPort(){
        // U port
        return $(go.Panel, "Vertical",{row: 0, column: 1},
            $(go.Shape, {width: 3, height: 3, portId: 1, toSpot: go.Spot.Top,fromSpot:go.Spot.Top,
                    fromLinkable: true,toLinkable: true}));
    }

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
            },
            new go.Binding("location", "location").makeTwoWay(),
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
            ),
            //port
            leftPort(),rightPort(),topPort(),bottomPort()
        );

    //relationNodeTemplate
    const relationTemplate =
        $(go.Node, "Table",
            {
                locationObject: "BODY",
                locationSpot:go.Spot.Center,
                selectionObjectName: "BODY"
            },
            {
                locationSpot: go.Spot.Center,
                selectionAdorned: true,
                resizable: false,
                contextMenu: relation_menu,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue,
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
                        // width: 100,
                        height: 50,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false
                    },
                    new go.Binding("fromLinkable", "from").makeTwoWay(), new go.Binding("toLinkable", "to").makeTwoWay()),
                $(go.Panel, "Table",
                    { margin: 8, stretch: go.GraphObject.Fill },
                    $(go.RowColumnDefinition, { row: 0, sizing: go.RowColumnDefinition.None }),
                    $(go.TextBlock,textStyle(),
                        {
                            row: 0,
                            alignment: go.Spot.Center,
                            font: "bold 16px sans-serif",
                            editable: true
                        },
                        new go.Binding("text", "name").makeTwoWay()))
            ),
            //port
            leftPort(),rightPort(),topPort(),bottomPort()
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
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            $(go.Panel,"Auto",
                {
                    row: 1, column: 1, name: "BODY",
                },
                $(go.Shape, weakEntityNodeCategory,
                    {
                        fill: 'white',
                        portId: "",
                        stroke: colors.lightgrey,
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 3,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false,
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
            //port
            leftPort(),rightPort(),topPort(),bottomPort()
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
                shadowColor: colors.lightgrey,
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            // the table header
            $(go.Panel, "Auto",
                {row: 1, column: 1, name: "BODY"},
                $(go.Shape, "RoundedRectangle",
                    {
                        fill:"#e8c446",
                        portId: "",
                        stroke: "#e8c446",
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
            //port
            $(go.Panel, "Vertical", {row: 1, column: 1},
                $(go.Shape, {width: 0, height: 0, portId: 5,
                    fromLinkable: true,toLinkable: true,
                    fill: "#e8c446",stroke: "#e8c446",
                })),
        );

    // attribute template
    var attributeTemplate=$(go.Node, "Table",
        {
            locationObject: "MAINBODY",
            locationSpot:go.Spot.Center,
            selectionObjectName: "MAINBODY"
        },
        {
            selectionAdorned: true,
            // selectionAdornmentTemplate: attributeAdornment,
            resizable: false,
            layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
            // movable: false,
        },
        new go.Binding("location", "location").makeTwoWay(),
        // textbox
        $(go.Panel,"Auto",
            {row: 0, column: 0, name: "AttributeName"},
            $(go.TextBlock,{
                    font: "bold 12px monospace",
                    margin: new go.Margin(0, 0, 0, 0),  // leave room for Button
                },
                new go.Binding("text","name").makeTwoWay(),
                new go.Binding("isUnderline", "underline")
            )
        ),
        $(go.Panel,"Table",
            {row: 0, column: 1, name: "MAINBODY"},
            $(go.Panel,"Auto",
                {row: 1, column: 1},
                $(go.Shape, "Circle",
                    {
                        fill: colors.lightblue,
                        portId: "",
                        stroke: colors.lightblue,
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 2,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false,
                        desiredSize: new go.Size(10, 10),
                    }
                ),
            ),
            //port
            $(go.Panel, "Vertical", {row: 1, column: 1},
                $(go.Shape, {width: 3, height: 3, portId: 5,
                    fromLinkable: true,toLinkable: true,
                    fill: colors.lightblue,stroke: colors.lightblue,
                })),
        )
        );

    // add all node template
    var templateMap = new go.Map();
    // default template
    myDiagram.nodeTemplate = entityTemplate;
    templateMap.add("",entityTemplate);
    templateMap.add(entityNodeCategory, entityTemplate);
    templateMap.add(weakEntityNodeCategory, weakEntityTemplate);
    templateMap.add(subsetEntityNodeCategory,subsetTemplate);
    templateMap.add("Attribute",attributeTemplate);
    templateMap.add("relation_attribute",attributeTemplate);

    templateMap.add(relationNodeCategory ,relationTemplate);

    myDiagram.nodeTemplateMap = templateMap;

    // relation
    var weakLink = $(go.Link,  // the whole link panel
        {
            deletable: false,
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
                editable:false,
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
            // reshapable: true,
            // routing: go.Link.AvoidsNodes,
            // corner: 5,
            // curve: go.Link.JumpOver,
            relinkableFrom: true,
            relinkableTo: true
        },
        $(go.Shape,  // the link shape
            {stroke: colors.lightblue, strokeWidth: 2.5 }),
        $(go.TextBlock, textStyle(), // the "from" label
            {
                textAlign: "center",
                font: "bold 14px sans-serif",
                stroke: colors.lightblue,
                segmentIndex: 0,
                segmentOffset: new go.Point(NaN, NaN),
                segmentOrientation: go.Link.OrientUpright
            },
            new go.Binding("text", "fromText").makeTwoWay()),
    );

    var subsetLink = $(go.Link,
        {
            deletable: false,
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
    linkTemplateMap.add(EWLinkCategory, weakLink);
    linkTemplateMap.add("normalLink",normalLink);
    linkTemplateMap.add("subsetLink",subsetLink);
    linkTemplateMap.add(ERLinkCategory,entityLink);
    // default
    linkTemplateMap.add("",entityLink);
    myDiagram.linkTemplateMap = linkTemplateMap;

    myDiagram.model = new go.GraphLinksModel(
        {
            copiesArrays: true,
            copiesArrayObjects: true,
            nodeDataArray: [],
            linkDataArray: []
        });


    function changedSelection(e){
        var tmpNodes = new go.List();
        myDiagram.nodes.each(function (node) {
            if (node.data.category === "Attribute" && node.isSelected) {
                tmpNodes.push(node);
                document.getElementById('infoDraggableHandle').style.display = "block";
                document.getElementById('AttributeIndo').style.display = "block";
                modifyAttributeClick();
            }else{
                document.getElementById('infoDraggableHandle').style.display = "hide";
                document.getElementById('AttributeIndo').style.display = "hide";

            }
        });
    }

    //listen edit the relation and entity name and cardinality
    myDiagram.addDiagramListener("TextEdited",(e) => {

        const item = e.subject.part.qb;
        const category = item.category;
        const id = item.key;
        const name = item.name;

        switch(category){
            case entityNodeCategory:
                updateEntity(id,name,item.location.x,item.location.y,default_null,false,false,true);
                break;
            case weakEntityNodeCategory:
                var portNum = -1
                // 找到与weak entity相连的entity的link上的toPort
                const weakEntityNode = myDiagram.findNodeForKey(id);
                weakEntityNode.findLinksOutOf().each(function (link){
                    if(link.category === EWLinkCategory)
                        portNum = link.toPort;
                });
                updateEntity(id,name,item.location.x,item.location.y,portNum,false,false,true);
                break;
            case subsetEntityNodeCategory:
                updateEntity(id,name,item.location.x,item.location.y, portNum,false,true,true);
                break;
            case relationNodeCategory:
                updateRelationNode(id,name,item.location.x,item.location.y,false,true);
                break;
            case ERLinkCategory:
                let cardinality = findRelationCode(item.fromText)
                if(cardinality===undefined) {
                    alert("only accept following cardinality: null, 0:N, 1:1, 1:N, 0:1");
                    myDiagram.rollbackTransaction();
                }else {
                    updateEdge(id,item.from,item.fromText,item.toPort,item.fromPort,false)
                }
                break;
            case EWLinkCategory:
                 const enteredText = e.subject.cc;
                if (item.relation === enteredText){
                    // edit the weakEntity name
                    updateRelationNode(id,enteredText,'','',true,true);
                }else{
                    let cardinality = findRelationCode(item.fromText)
                    if(cardinality===undefined) {
                        alert("only accept following cardinality: null, 0:N, 1:1, 1:N, 0:1");
                        myDiagram.rollbackTransaction();
                    }else {
                        updateEdge(id,item.from,item.fromText,default_null,default_null,false)
                    }
                }
            default:break;
        }
    });

    //listen node movement
    myDiagram.addDiagramListener("SelectionMoved",(e) => {

        const item= e.diagram.selection.first();
        const name = item.name;
        const id = item.key;
        const category = item.category;

        switch(category){
            case entityNodeCategory:
                updateEntity(id,name,item.location.x,item.location.y,default_null,false,false,false);
                break;
            case weakEntityNodeCategory:
                var portNum = -1
                // 找到与weak entity相连的entity的link上的toPort
                const weakEntityNode = myDiagram.findNodeForKey(id);
                weakEntityNode.findLinksOutOf().each(function (link){
                    if(link.category === EWLinkCategory)
                        portNum = link.toPort;
                });
                updateEntity(id,name,item.location.x,item.location.y,portNum,false,false,false);
                break;
            case subsetEntityNodeCategory:
                updateEntity(id,name,item.location.x,item.location.y,default_null,false,true,false);
                break;
            case relationNodeCategory:
                updateRelationNode(id,name,item.location.x,item.location.y,false,false);
                break;
            case "Attribute"://delete attribute
                //TODO:function update Attribute
                break;
            default:break;
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
            if (typeof e ==='undefined') {return;}
            else if (e.change === go.ChangedEvent.Insert && e.modelChange === "linkDataArray") {

                const fromPort = e.newValue.fromPort;
                const toPort = e.newValue.toPort;
                const node1 = myDiagram.findNodeForKey(e.newValue.from);
                const node2 = myDiagram.findNodeForKey(e.newValue.to);
                const category = e.newValue.category;


                // case1 : entity relation link
                if ((node1.category === relationNodeCategory  && node2.category === entityNodeCategory) || (
                    node1.category === entityNodeCategory && node2.category === relationNodeCategory )) {
                    // entity relation link
                    const is_node1_relation = (node1.category === relationNodeCategory);
                    e.newValue.from = is_node1_relation ? node2.key : node1.key;
                    e.newValue.to = is_node1_relation ? node1.key : node2.key;
                    e.newValue.fromPort = is_node1_relation ? toPort : fromPort;
                    e.newValue.toPort = is_node1_relation ? fromPort : toPort;

                    const er_id = createEdge(e.newValue.from, e.newValue.to, ERLinkCard,
                        e.newValue.fromPort, e.newValue.toPort,ERLinkCreateVerify);
                    if (er_id === -1) {
                        alert("can't create relation between this entity and this relation");
                        myDiagram.rollbackTransaction();
                        return;
                    } else {
                        e.newValue.fromText = "1:N";
                        e.newValue.category = ERLinkCategory;
                        e.newValue.key = er_id;
                        save();
                        load();
                    }
                }
                // case 2: entity-entity link, create new node
                else if (node1.category === entityNodeCategory && node2.category === entityNodeCategory) {
                    myDiagram.rollbackTransaction();
                    console.log(fromPort);
                    console.log(toPort);
                    const relationNodeX = (node1.location.x + node2.location.x) / 2;
                    const relationNodeY = (node1.location.y + node2.location.y) / 2;
                    let relation_id_list = createRelationNode(relationNodeName,node1.key,node2.key,
                        ERLinkCard,fromPort,toPort,ERLinkCard,toPort,fromPort, relationNodeX,relationNodeY);
                    if (relation_id_list[0] === -1) {
                        alert("can't build relation between two entity");
                        return;
                    } else {
                        const relation_id = prefixRelationNodeKey+relation_id_list[0];
                        ERLinkCreateVerify.add(node1.key+relation_id);
                        ERLinkCreateVerify.add(node2.key+relation_id);

                        myDiagram.model.addNodeData({
                            key: relation_id,
                            name: relationNodeName,
                            "location": {"class": "go.Point", "x": relationNodeX, "y": relationNodeY},
                            category: relationNodeCategory,
                            from: true,
                            to: true,
                        });

                        myDiagram.model.addLinkData(
                            {
                                "from": node1.key, "to": relation_id, fromText: ERLinkCard,
                                category: ERLinkCategory, fromPort: fromPort, toPort: toPort, key: relation_id_list[1]
                            });
                        myDiagram.model.addLinkData(
                            {
                                "from": node2.key, "to": relation_id, fromText: ERLinkCard, category: ERLinkCategory,
                                fromPort: toPort, toPort: fromPort, key: relation_id_list[2]
                            });
                    }
                    save();
                    load();
                }
            }
            else if (e.change === go.ChangedEvent.Insert && e.modelChange === "nodeDataArray") {
                switch(e.newValue.category){
                    case entityNodeCategory: //create new strong entity
                        e.newValue.name = e.newValue.name + entityCounter.toString();
                        entityCounter++;
                        const id = createStrongEntity(e.newValue.name, e.newValue.location.x, e.newValue.location.y);
                        if (id===-1){
                            alert("create entity fail!");
                            myDiagram.rollbackTransaction();
                        } else {e.newValue.key = id;}
                        save();
                        load();
                        break;
                    default:break; //create new relation node already handled by the insert Link Array
                    }
                }
            else if (e.change === go.ChangedEvent.Property && e.modelChange === "linkFromPortId"){
                const is_success= handleChangPort(e.xn,false,e.newValue);
                if (!is_success){
                    alert("change from port API fail!");
                    myDiagram.rollbackTransaction();
                }
            }
            else if (e.change === go.ChangedEvent.Property && e.modelChange === "linkToPortId"){
                const is_success= handleChangPort(e.xn,true,e.newValue);
                if (!is_success){
                    alert("change to port API fail!");
                    myDiagram.rollbackTransaction();
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

    var temporal_counter = 0;
    let temporal_is_success = true;

    myDiagram.addDiagramListener('SelectionDeleting',function(e) {
        const category = e.subject.first().qb.category;
        const id =  e.subject.first().qb.key;

        switch(category) {
            case entityNodeCategory:
                temporal_is_success = handleDeleteStrongEntity(id,name);
                break;
            case relationNodeCategory:
                temporal_is_success = handleDeleteRelationNode(id,name,false);
                break;
            case weakEntityNodeCategory:
                temporal_is_success = handleDeleteOtherEntity(id,name,weakEntityNodeCategory);
                break;
            case "Attribute":
                temporal_is_success = deleteAttribute(id)
                break;
            case subsetEntityNodeCategory:
                temporal_is_success = handleDeleteOtherEntity(id,name,subsetEntityNodeCategory);
                break;
            case ERLinkCategory:
                temporal_is_success = deleteEdge(id);
                break;
            case EWLinkCategory:
                temporal_is_success = deleteEWLink(id);
                break;
            default:break;
        }


        console.log("true");

    });
    myDiagram.addDiagramListener('SelectionDeleted',function(e) {
        if(!temporal_is_success) myDiagram.rollbackTransaction();
        temporal_is_success=true;
    });

    /*
    Get the current View Id and load the model
     */
    const id =  location.href.substring(location.href.indexOf("id=")+3);
    myDiagram.model = new go.GraphLinksModel(
        { linkFromPortIdProperty: "fromPort",
            linkToPortIdProperty: "toPort",
            nodeDataArray: [],
            linkDataArray: []});

    defineModel();
    document.getElementById("mySavedModel").value = getSchema(schemaID);
    myDiagram.model = go.Model.fromJson(getSchema(schemaID));
}