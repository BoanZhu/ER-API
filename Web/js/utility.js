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

/*
index.js is for the index html

showModel is used to show the model at right dashboard,
    input: selected id and name
    output: model shown at right div

anonymous function:
    1. show all view model: output: all view name and id in the list
    2. slide down and up function
 */



// Common text styling
function textStyleFake() {
    return {
        margin: 6,
        wrap: go.TextBlock.WrapFit,
        textAlign: "center",
        editable: false,
    }
}
/*
define model
 */

function isLinkValidIndex(fromNode, fromGraphObject, toNode, toGraphObject) {
    return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 1;}

function defineModel(){
    const $ = go.GraphObject.make;  // for conciseness in defining templates
    indexDiagram = $(go.Diagram, "model",  // must name or refer to the DIV HTML element
        {
            allowDelete: false,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            layout: $(go.LayeredDigraphLayout, {isInitial: false, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "undoManager.isEnabled": false,
            "maxSelectionCount": 1,
            "linkingTool.linkValidation": isLinkValidIndex,
            allowMove:false
        });

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

    /*
     4 ports
     */

    function leftPort(){
        // L port
        return $(go.Panel, "Vertical", {row: 1, column: 0},
            $(go.Shape, {width: 3, height: 3, portId: 3, toSpot: go.Spot.Left,fromSpot:go.Spot.Left,
                fromLinkable: false,toLinkable: false
            }));
    }
    function rightPort(){
        // R port
        return $(go.Panel, "Vertical", {row: 1, column: 2},
            $(go.Shape,  {width: 3, height: 3, portId: 4, toSpot: go.Spot.Right,fromSpot:go.Spot.Right,
                fromLinkable: false,toLinkable: false}));
    }
    function bottomPort(){
        // B port
        return $(go.Panel, "Horizontal", {row:2, column: 1},
            $(go.Shape, {width: 3, height: 3, portId: 2, toSpot: go.Spot.Bottom,fromSpot:go.Spot.Bottom,
                fromLinkable: false,toLinkable: false}));
    }
    function topPort(){
        // U port
        return $(go.Panel, "Vertical",{row: 0, column: 1},
            $(go.Shape, {width: 3, height: 3, portId: 1, toSpot: go.Spot.Top,fromSpot:go.Spot.Top,
                fromLinkable: false,toLinkable: false}));
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
                        editable: false
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
            },
            {
                locationSpot: go.Spot.Center,
                resizable: false,
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
                        width: 100,
                        height: 40,
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
                            editable: false
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
            },
            {
                locationSpot: go.Spot.Center,
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
                        editable: false
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
            },
            {
                locationSpot: go.Spot.Center,
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
                        editable: false
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
    indexDiagram.nodeTemplate = entityTemplate;
    templateMap.add("",entityTemplate);
    templateMap.add(entityNodeCategory, entityTemplate);
    templateMap.add(weakEntityNodeCategory, weakEntityTemplate);
    templateMap.add(subsetEntityNodeCategory,subsetTemplate);
    templateMap.add("Attribute",attributeTemplate);
    templateMap.add("relation_attribute",attributeTemplate);

    templateMap.add(relationNodeCategory ,relationTemplate);

    indexDiagram.nodeTemplateMap = templateMap;

    // relation
    var weakLink = $(go.Link,  // the whole link panel
        {
            deletable: false,
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
    indexDiagram.linkTemplateMap = linkTemplateMap;

    indexDiagram.model = new go.GraphLinksModel(
        {
            copiesArrays: true,
            copiesArrayObjects: true,
            nodeDataArray: [],
            linkDataArray: []
        });
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
const ENTITYTYPE=["","entity","weakEntity","subset","attribute"]

function getSchema(id) {
    let modelStr = "{ \"class\": \"GraphLinksModel\",\n" +
        "  \"copiesArrays\": true,\n" +
        "  \"copiesArrayObjects\": true,\n" +
        "  \"nodeDataArray\": [],\n" +
        "  \"linkDataArray\": []}";
    const x = go.GraphObject.make;
    indexDiagram.model = new go.GraphLinksModel(
        {
            copiesArrays: true,
            copiesArrayObjects: true,
            allowDelete: false,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            layout: x(go.LayeredDigraphLayout, {isInitial: true, isOngoing: false}),
        });
    indexDiagram.model = new go.GraphLinksModel(
        { linkFromPortIdProperty: "fromPort",
            linkToPortIdProperty: "toPort",
            nodeDataArray: [],
            linkDataArray: []
        });
    $.ajax({
        type : "GET",
        async: false,
        url : "http://146.169.52.81:8080/er/schema/get_by_id",
        withCredentials:false,
        contentType : 'application/json',
        dataType:'json',
        data : {ID:id},
        success : function(result) {
            // entity list
            var entityList = result.data.schema.entityList;
            // relationship list
            var relationshipList = result.data.schema.relationshipList;
            var attributeList = []
            entityList.forEach(
                function (entityNode){
                // add entity node
                var entityData = {key:entityNode.id,name:entityNode.name,category:ENTITYTYPE[entityNode.entityType],
                    from:true, to:true};
                if(entityNode.layoutInfo!==null){
                        entityData.location = {"class": "go.Point", "x": entityNode.layoutInfo.layoutX, "y": entityNode.layoutInfo.layoutY};
                }
                if(ENTITYTYPE[entityNode.entityType]!=="entity"){
                    entityData.parentId = entityNode.strongEntityID;
                }
                indexDiagram.model.addNodeData(entityData);
                if(entityNode.entityType!==3){
                    //weak and strong entity
                    attributeList=entityNode.attributeList;
                    attributeList.forEach(
                        function (attributeNode){
                        // add attribute node
                        var attributeNodeData = {"name":attributeNode.name,"category":"Attribute",
                            "dataType":attributeNode.dataType,"parentId":entityNode.id,"allowNotNull":entityNode.nullable,
                            "isPrimary":attributeNode.isPrimary,"key":attributeNode.id+"_"+attributeNode.name,"underline":attributeNode.isPrimary,
                            "allowNotNull":attributeNode.nullable};
                        if(attributeNode.layoutInfo!==null){
                            attributeNodeData.location={"class":"go.Point","x":attributeNode.layoutInfo.layoutX,"y":attributeNode.layoutInfo.layoutY};
                        }
                        indexDiagram.model.addNodeData(attributeNodeData);
                        // add link between node and attribute
                        //todo 万一老师说可以乱挪attribute，那么entity给的port在json中放哪里
                            // all attribute is from the left of the node(default)
                        var linkData = {"from":entityData.key,"to":attributeNodeData.key,"category":"normalLink","fromPort":4,"toPort":5}
                        if(attributeNode.aimPort!==-1){linkData.fromPort=attributeNode.aimPort;}
                        indexDiagram.model.addLinkData(linkData);
                    });
                }
            });

            relationshipList.forEach(
                function (relationNode){
                const edgeList = relationNode.edgeList;
                // if the relation is strong&weak or strong&subset, only 2
                const firstType = indexDiagram.findNodeForKey(edgeList[0].entityID).category;
                const secondType = indexDiagram.findNodeForKey(edgeList[1].entityID).category;
                // all strong entity
                if((firstType === "entity" && secondType=== "entity")||firstType === "" && secondType=== ""){
                    // create relation node
                    var relationNodeData = {"key":"relation_"+relationNode.id,"name":relationNode.name,
                        "category":"relation","from":true,"to":true};
                    if(relationNode.layoutInfo!==null){
                        relationNodeData.location={"class":"go.Point","x":relationNode.layoutInfo.layoutX,"y":relationNode.layoutInfo.layoutY};
                    }
                    indexDiagram.model.addNodeData(relationNodeData);
                    // 2 or more links
                    var i=1;
                    edgeList.forEach(
                        function (edge){
                            var edgeLinkData = {"from":edge.entityID,"to":relationNodeData.key,"fromText":findRelationName(edge.cardinality),
                                "category":"entityLink"};
                            if(edge.portAtEntity!==-1&&edge.portAtRelationship!==-1){
                                edgeLinkData.fromPort=edge.portAtEntity;
                                edgeLinkData.toPort=edge.portAtRelationship;
                            }else{
                                edgeLinkData.fromPort=4;
                                edgeLinkData.toPort=i;
                                i=(i+1)%4+1;
                            }
                            indexDiagram.model.addLinkData(edgeLinkData);
                        }
                    );
                    // add attribute
                    const relationAttributeList = relationNode.attributeList;
                    relationAttributeList.forEach(
                        function (relationAttributeNode){
                            // node
                            var rAttrNodeData = {"name":relationAttributeNode.name,"category":"relation_attribute",
                                "dataType":relationAttributeNode.dataType,"parentId":relationNodeData.key,
                                "allowNotNull":relationAttributeNode.nullable,"key":relationAttributeNode.id+"_"+relationAttributeNode.name,
                                "isPrimary":false,"underline":false};
                            if(relationAttributeNode.layoutInfo!==null){
                                rAttrNodeData.location={"class":"go.Point","x":relationAttributeNode.layoutInfo.layoutX,"y":relationAttributeNode.layoutInfo.layoutY};
                            }
                            // link
                            var linkNodeData = {"from":relationNodeData.key,"to":rAttrNodeData.key,"category":"normalLink","toPort":5};
                            if(relationAttributeNode.aimPort!==-1){
                                linkNodeData.fromPort = relationAttributeNode.aimPort;
                            }else{linkNodeData.fromPort = 4;}
                            indexDiagram.model.addNodeData(rAttrNodeData);
                            indexDiagram.model.addLinkData(linkNodeData);
                        });
                }
                // strong entity and weak entity
                if(firstType === "weakEntity" || secondType === "weakEntity"){
                    // add parentID to weak entity node
                    var tmp = firstType==="weakEntity"?0:1;
                    var weakEntityNode = indexDiagram.findNodeForKey(edgeList[tmp].entityID);
                    // only preview, no need for primary key
                    var linkData = {"from":edgeList[1-tmp].entityID,"to":weakEntityNode.key,"toText":findRelationName(edgeList[tmp].cardinality),
                        "fromText":findRelationName(edgeList[1-tmp].cardinality),"relation":relationNode.name,"category":"weakLink",
                        "key":relationNode.id,"edgeIDFirst":edgeList[1-tmp].ID,"edgeIDSecond":edgeList[tmp].ID};
                    if(edgeList[1-tmp].portAtEntity!==-1&&edgeList[tmp].portAtEntity!==-1){
                        linkData.fromPort=edgeList[1-tmp].portAtEntity;
                        linkData.toPort = edgeList[tmp].portAtEntity;
                    }else {
                        linkData.fromPort=1;
                        linkData.toPort = 2;
                    }
                    indexDiagram.model.addLinkData(linkData);
                }
                // strong entity and subset
                if(firstType === "subset" || secondType === "subset"){
                    // add parentID to subset node
                    var tmp = firstType==="subset"?0:1;
                    var subsetNode = indexDiagram.findNodeForKey(edgeList[tmp].entityID);
                    var subLinkData ={"from":edgeList[1-tmp].entityID,"to":subsetNode.key,
                        "category":"subsetLink","toPort":5};
                    if(edgeList[1-tmp].portAtEntity!==-1){
                        subLinkData.fromPort=edgeList[1-tmp].portAtEntity;
                    }else{
                        subLinkData.fromPort=1;
                    }
                    indexDiagram.model.addLinkData(subLinkData);
                }
                else{
                    // console.log("load fails");
                }
            });
            modelStr = indexDiagram.model.toJSON();
            console.log(modelStr);
        }, error : function(result) {
            console.log("false");
        }
    });
    return modelStr;
}
