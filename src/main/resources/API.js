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

const entityMap = new Map();

const ENTITYTYPE=["","entity","weakEntity","subset","attribute"]

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

function is_empty(item){
    return typeof item ==='undefined' || item === null;
}

const weakEntityNodeCategory = ENTITYTYPE[2]
const entityNodeCategory = ENTITYTYPE[1]

const subsetEntityNodeCategory = ENTITYTYPE[3]
const relationNodeCategory = "relation"
const ERLinkCategory = "entityLink";
const EWLinkCategory = "weakLink";
const relationNodeName = "test";



const colors =
    {
        'lightblue': '#afd4fe',
        'lightgrey': '#a4a8ad',
        'lightyellow': '#fcffbe'
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


function showSchema(data){
    let isInitial = getInitialParameter(data);
    defineModel(isInitial);
    getSchema(data);
}

function getInitialParameter(data){
    let flag = false;
    let entities = data.schema.entityList;
    if (!is_empty(entities)) {
        for (let i = 0; i < entities.length; i++) {
            let entityNode = entities[i];
            if (is_empty(entityNode.layoutInfo)) {
                flag = true;
            }
            let attributeList = entityNode.attributeList;
            if (!is_empty(attributeList)) {
                for (let j = 0; j < attributeList.length; j++) {
                    let attributeNode = attributeList[j];
                    if (is_empty(attributeNode.layoutInfo)) {
                        flag = true;
                    }
                }
            }
        }
    }

    let relationList = data.schema.relationshipList;
    if (!is_empty(relationList)) {
        for (let i = 0; i < relationList.length; i++) {
            let relationNode = relationList[i];
            if (is_empty(relationNode.layoutInfo)) {
                flag = true;
            }

            let attributeList = relationNode.attributeList;

            if (!is_empty(attributeList)) {
                for (let j = 0; j < attributeList.length; j++) {
                    let attributeNode = attributeList[j];
                    if (is_empty(attributeNode.layoutInfo)) {
                        flag = true;
                    }
                }
            }
        }
    }
    return flag;
}

function defineModel(isInitial){

    const $ = go.GraphObject.make;  // for conciseness in defining templates
    APIDiagram = $(go.Diagram, "model",  // must name or refer to the DIV HTML element
        {
            allowDelete: true,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            layout: $(go.LayeredDigraphLayout, {isInitial: isInitial, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "undoManager.isEnabled": true,
            "maxSelectionCount": 1
        });

    /*
    All adornment
    */

    function leftPort() {
        // L port
        return $(go.Panel, "Vertical", {row: 1, column: 0},
            $(go.Shape, {
                width: 3, height: 3, portId: 3, toSpot: go.Spot.Left, fromSpot: go.Spot.Left,
                fromLinkable: true, toLinkable: true
            }));
    }

    function rightPort() {
        // R port
        return $(go.Panel, "Vertical", {row: 1, column: 2},
            $(go.Shape, {
                width: 3, height: 3, portId: 4, toSpot: go.Spot.Right, fromSpot: go.Spot.Right,
                fromLinkable: true, toLinkable: true
            }));
    }

    function bottomPort() {
        // B port
        return $(go.Panel, "Horizontal", {row: 2, column: 1},
            $(go.Shape, {
                width: 3, height: 3, portId: 2, toSpot: go.Spot.Bottom, fromSpot: go.Spot.Bottom,
                fromLinkable: true, toLinkable: true
            }));
    }

    function topPort() {
        // U port
        return $(go.Panel, "Vertical", {row: 0, column: 1},
            $(go.Shape, {
                width: 3, height: 3, portId: 1, toSpot: go.Spot.Top, fromSpot: go.Spot.Top,
                fromLinkable: true, toLinkable: true
            }));
    }

    /*
        All Node(Entity+Attribute) templates
     */

    go.Shape.defineFigureGenerator("WeakEntity", function(shape, w, h) {
        var geo = new go.Geometry();
        var fig = new go.PathFigure(0.05*w,0.05*w, true);  // clockwise
        geo.add(fig);
        if (w>h){
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.05*w,h-0.05*w)); //下划线到h点
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.95*w,h-0.05*w));//在0.h 画到 1.6w,h
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.95*w,0.05*w));
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.05*w,0.05*w).close());

        }else{
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.05*h,w-0.05*h)); //下划线到h点
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.95*h,w-0.05*h));//在0.h 画到 1.6w,h
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.95*h,0.05*h));
            fig.add(new go.PathSegment(go.PathSegment.Line, 0.05*h,0.05*h).close());

        }

        fig.add(new go.PathSegment(go.PathSegment.Move, 0,0));
        fig.add(new go.PathSegment(go.PathSegment.Line, 0,h));
        fig.add(new go.PathSegment(go.PathSegment.Line, w,h));
        fig.add(new go.PathSegment(go.PathSegment.Line, w,0));
        fig.add(new go.PathSegment(go.PathSegment.Line, 0,0));
        return geo;
    });

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
            leftPort(), rightPort(), topPort(), bottomPort()
        );

    //relationNodeTemplate
    const relationTemplate =
        $(go.Node, "Table",
            {
                locationObject: "BODY",
                locationSpot: go.Spot.Center,
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
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            $(go.Panel, "Auto",
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
                    {margin: 8, stretch: go.GraphObject.Fill},
                    $(go.RowColumnDefinition, {row: 0, sizing: go.RowColumnDefinition.None}),
                    $(go.TextBlock, textStyle(),
                        {
                            row: 0,
                            alignment: go.Spot.Center,
                            font: "bold 16px sans-serif",
                            editable: true
                        },
                        new go.Binding("text", "name").makeTwoWay()))
            ),
            //port
            leftPort(), rightPort(), topPort(), bottomPort()
        );

    // weak entity template
    const weakEntityTemplate =
        $(go.Node, "Table",  // the whole node panel
            {
                locationObject: "BODY",
                locationSpot: go.Spot.Center,
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
            },
            new go.Binding("location", "location").makeTwoWay(),
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            $(go.Panel, "Auto",
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
            leftPort(), rightPort(), topPort(), bottomPort()
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
                        fill: "#e8c446",
                        portId: "",
                        stroke: "#e8c446",
                        cursor: "pointer",
                        fromSpot: go.Spot.AllSides,
                        toSpot: go.Spot.AllSides,
                        strokeWidth: 3,
                        fromLinkableDuplicates: false, toLinkableDuplicates: false
                    },
                    new go.Binding("fromLinkable", "from").makeTwoWay(), new go.Binding("toLinkable", "to").makeTwoWay()),
                $(go.TextBlock, textStyle(),
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(5, 24, 5, 2),  // leave room for Button
                        font: "bold 16px sans-serif",
                        editable: true
                    },
                    new go.Binding("text", "name").makeTwoWay()),
                $(go.RowColumnDefinition, {row: 0, sizing: go.RowColumnDefinition.None}),
            ), // end Table Panel
            //port
            $(go.Panel, "Vertical", {row: 1, column: 1},
                $(go.Shape, {
                    width: 0, height: 0, portId: 5,
                    fromLinkable: true, toLinkable: true,
                    fill: "#e8c446", stroke: "#e8c446",
                })),
        );

    // attribute template
    var attributeTemplate = $(go.Node, "Table",
        {
            locationObject: "MAINBODY",
            locationSpot: go.Spot.Center,
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
        $(go.Panel, "Auto",
            {row: 0, column: 1, name: "AttributeName"},
            $(go.TextBlock, {
                    font: "bold 12px monospace",
                    margin: new go.Margin(0, 0, 0, 0),  // leave room for Button
                },
                new go.Binding("text", "name").makeTwoWay(),
                new go.Binding("isUnderline", "underline")
            )
        ),
        $(go.Panel, "Table",
            {row: 0, column: 0, name: "MAINBODY"},
            $(go.Panel, "Auto",
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
                $(go.Shape, {
                    width: 3, height: 3, portId: 5,
                    fromLinkable: true, toLinkable: true,
                    fill: colors.lightblue, stroke: colors.lightblue,
                })),
        )
    );

    // add all node template
    var templateMap = new go.Map();
    // default template
    APIDiagram.nodeTemplate = entityTemplate;
    templateMap.add("", entityTemplate);
    templateMap.add(entityNodeCategory, entityTemplate);
    templateMap.add(weakEntityNodeCategory, weakEntityTemplate);
    templateMap.add(subsetEntityNodeCategory, subsetTemplate);
    templateMap.add("Attribute", attributeTemplate);
    templateMap.add("relation_attribute", attributeTemplate);

    templateMap.add(relationNodeCategory, relationTemplate);

    APIDiagram.nodeTemplateMap = templateMap;

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
            {stroke: "#303B45", strokeWidth: 2.5}),
        $(go.Panel, "Auto",  // this whole Panel is a link label
            $(go.Shape, "Diamond", {
                fill: "yellow",
                stroke: "gray",
                width: 100,
                height: 40
            }),
            $(go.TextBlock, textStyle(),
                {
                    margin: 3,
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
                editable: false,
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
            {stroke: colors.lightblue, strokeWidth: 2.5}),
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
            {stroke: "#e8c446", strokeWidth: 2.5}),
        $(go.Shape,   // the arrowhead
            {toArrow: "OpenTriangle", fill: null, stroke: "#e8c446", strokeWidth: 2.5}),
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
            {stroke: "#000000", strokeWidth: 2.5}),
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
    linkTemplateMap.add("normalLink", normalLink);
    linkTemplateMap.add("subsetLink", subsetLink);
    linkTemplateMap.add(ERLinkCategory, entityLink);
    // default
    linkTemplateMap.add("", entityLink);
    APIDiagram.linkTemplateMap = linkTemplateMap;

    APIDiagram.model = new go.GraphLinksModel(
        {
            copiesArrays: true,
            copiesArrayObjects: true,
            nodeDataArray: [],
            linkDataArray: []
        });
}

function getSchema(data){
    let modelStr = "{ \"class\": \"GraphLinksModel\",\n" +
        "  \"copiesArrays\": true,\n" +
        "  \"copiesArrayObjects\": true,\n" +
        "  \"nodeDataArray\": [],\n" +
        "  \"linkDataArray\": []}";
    APIDiagram.model = new go.GraphLinksModel(
        { linkFromPortIdProperty: "fromPort",
            linkToPortIdProperty: "toPort",
            nodeDataArray: [],
            linkDataArray: []
        });
    const entityList = data.schema.entityList;
    const relationshipList = data.schema.relationshipList;
    if(!is_empty(entityList)) {
        for (let i = 0; i < entityList.length; i++) {
            // add entity node
            let entityNode = entityList[i]
            let entityData = {
                key: entityNode.id, name: entityNode.name, category: ENTITYTYPE[entityNode.entityType],
                from: true, to: true
            };
            entityMap.set(entityData.key, entityData.category);
            if (!is_empty(entityNode.layoutInfo)) {
                entityData.location = {
                    "class": "go.Point",
                    "x": entityNode.layoutInfo.layoutX,
                    "y": entityNode.layoutInfo.layoutY
                };
            }
            if (ENTITYTYPE[entityNode.entityType] !== "entity") {
                entityData.parentId = entityNode.strongEntityID;
            }
            APIDiagram.model.addNodeData(entityData);

            if(entityNode.entityType===3){
                // subset
                let linkData = {"from":entityNode.id,"to":entityNode.belongStrongEntityID,"category":"subsetLink","toPort":5};
                if(entityNode.aimPort!==-1){
                    linkData.fromPort = entityNode.aimPort;
                }else{
                    linkData.fromPort = 2;
                }
                APIDiagram.model.addLinkData(linkData);
            }

            let attributeList = entityNode.attributeList;

            if (!is_empty(attributeList)) {
                for (let j = 0; j < attributeList.length; j++) {
                    let attributeNode = attributeList[j];
                    let attributeNodeData = {
                        "name": attributeNode.name,
                        "category": "Attribute",
                        "dataType": attributeNode.dataType,
                        "parentId": entityNode.id,
                        "isPrimary": attributeNode.isPrimary,
                        "key": attributeNode.id + "_" + attributeNode.name,
                        "underline": attributeNode.isPrimary,
                        "allowNotNull": attributeNode.nullable
                    };

                    if (!is_empty(attributeNode.layoutInfo)) {
                        attributeNodeData.location = {
                            "class": "go.Point",
                            "x": attributeNode.layoutInfo.layoutX,
                            "y": attributeNode.layoutInfo.layoutY
                        };
                    }
                    APIDiagram.model.addNodeData(attributeNodeData);
                    // add link between node and attribute
                    let linkData = {
                        "from": entityData.key,
                        "to": attributeNodeData.key,
                        "category": "normalLink",
                        "fromPort": 4,
                        "toPort": 5
                    }
                    if (attributeNode.aimPort !== -1) {
                        linkData.fromPort = attributeNode.aimPort;
                    }
                    APIDiagram.model.addLinkData(linkData);
                }

            }
        }
    }
        if(!is_empty(relationshipList)) {
            for (let i = 0; i < relationshipList.length; i++) {
                let relationNode = relationshipList[i];
                let edgeList = relationNode.edgeList;
                let firstType = entityMap.get(edgeList[0].entityID);
                let secondType = entityMap.get(edgeList[1].entityID);

                if ((firstType === "entity" && secondType === "entity") || firstType === "" && secondType === "") {
                    var relationNodeData = {
                        "key": "relation_" + relationNode.id, "name": relationNode.name,
                        "category": "relation", "from": true, "to": true
                    };

                    if (!is_empty(relationNode.layoutInfo)) {
                        relationNodeData.location = {
                            "class": "go.Point",
                            "x": relationNode.layoutInfo.layoutX,
                            "y": relationNode.layoutInfo.layoutY
                        };
                    }
                    APIDiagram.model.addNodeData(relationNodeData);
                    // 2 or more links
                    var j = 1;
                    for (let k = 0; k < edgeList.length && typeof (edgeList) !== 'undefined'; k++) {
                        let edge = edgeList[k];
                        let edgeLinkData = {
                            "from": edge.entityID,
                            "to": relationNodeData.key,
                            "fromText": findRelationName(edge.cardinality),
                            "category": "entityLink"
                        };
                        if (edge.portAtEntity !== -1 && edge.portAtRelationship !== -1) {
                            edgeLinkData.fromPort = edge.portAtEntity;
                            edgeLinkData.toPort = edge.portAtRelationship;
                        } else {
                            edgeLinkData.fromPort = 4;
                            edgeLinkData.toPort = j;
                            j = (j + 1) % 4 + 1;
                        }
                        APIDiagram.model.addLinkData(edgeLinkData);
                    }

                    const relationAttributeList = relationNode.attributeList;
                    for (let k = 0; k < relationAttributeList.length; k++) {
                        let relationAttributeNode = relationAttributeList[k];
                        let rAttrNodeData = {
                            "name": relationAttributeNode.name,
                            "category": "relation_attribute",
                            "dataType": relationAttributeNode.dataType,
                            "parentId": relationNodeData.key,
                            "allowNotNull": relationAttributeNode.nullable,
                            "key": relationAttributeNode.id + "_" + relationAttributeNode.name,
                            "isPrimary": false,
                            "underline": false
                        };

                        if (!is_empty(relationAttributeNode.layoutInfo)) {
                            rAttrNodeData.location = {
                                "class": "go.Point",
                                "x": relationAttributeNode.layoutInfo.layoutX,
                                "y": relationAttributeNode.layoutInfo.layoutY
                            };
                        }
                        // link
                        let linkNodeData = {
                            "from": relationNodeData.key,
                            "to": rAttrNodeData.key,
                            "category": "normalLink",
                            "toPort": 5
                        };
                        if (relationAttributeNode.aimPort !== -1) {
                            linkNodeData.fromPort = relationAttributeNode.aimPort;
                        } else {
                            linkNodeData.fromPort = 4;
                        }
                        APIDiagram.model.addNodeData(rAttrNodeData);
                        APIDiagram.model.addLinkData(linkNodeData);

                    }

                }

                // strong entity and weak entity
                if (firstType === "weakEntity" || secondType === "weakEntity") {
                    // add parentID to weak entity node
                    var tmp = firstType === "weakEntity" ? 0 : 1;
                    var weakEntityNode = APIDiagram.findNodeForKey(edgeList[tmp].entityID);
                    // only preview, no need for primary key
                    var linkData = {
                        "from": edgeList[1 - tmp].entityID,
                        "to": weakEntityNode.key,
                        "toText": findRelationName(edgeList[tmp].cardinality),
                        "fromText": findRelationName(edgeList[1 - tmp].cardinality),
                        "relation": relationNode.name,
                        "category": "weakLink",
                        "key": relationNode.id,
                        "edgeIDFirst": edgeList[1 - tmp].ID,
                        "edgeIDSecond": edgeList[tmp].ID
                    };
                    if (edgeList[1 - tmp].portAtEntity !== -1 && edgeList[tmp].portAtEntity !== -1) {
                        linkData.fromPort = edgeList[1 - tmp].portAtEntity;
                        linkData.toPort = edgeList[tmp].portAtEntity;
                    } else {
                        linkData.fromPort = 1;
                        linkData.toPort = 2;
                    }
                    APIDiagram.model.addLinkData(linkData);
                }
                // strong entity and subset
                if (firstType === "subset" || secondType === "subset") {
                    // add parentID to subset node
                    var tmp = firstType === "subset" ? 0 : 1;
                    var subsetNode = APIDiagram.findNodeForKey(edgeList[tmp].entityID);
                    var subLinkData = {
                        "from": edgeList[1 - tmp].entityID, "to": subsetNode.key,
                        "category": "subsetLink", "toPort": 5
                    };
                    if (edgeList[1 - tmp].portAtEntity !== -1) {
                        subLinkData.fromPort = edgeList[1 - tmp].portAtEntity;
                    } else {
                        subLinkData.fromPort = 1;
                    }
                    APIDiagram.model.addLinkData(subLinkData);
                }
            }
        }
    modelStr = APIDiagram.model.toJSON();
    console.log(modelStr)
    return modelStr
}