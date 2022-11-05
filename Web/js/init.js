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
            layout: $(go.ForceDirectedLayout, { isInitial: false, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "clickCreatingTool.archetypeNodeData": { name:"New Entity",from:true,to:true},
            "undoManager.isEnabled": true,
            "maxSelectionCount": 1,
            "ChangedSelection":changedSelection,

        });

    // Common color
    const colors =
        {'lightblue': '#afd4fe',
        'lightgrey':'#a4a8ad',
        'lightyellow':'#fcffbe'}
    // Common text styling
    function textStyle() {
        return {
            margin: 6,
            wrap: go.TextBlock.WrapFit,
            textAlign: "center",
            editable: true,
        }
    }

    /*
    All adornment
    */
    // adornment for strong entity
    const normalEntityAdornment =
        $(go.Adornment, "Spot",
            $(go.Panel, "Auto",
                $(go.Shape, { fill: null, stroke: "dodgerblue", strokeWidth: 3 }),
                $(go.Placeholder)),
            $("Button", {alignment: go.Spot.TopRight, click: addAttr},
                $(go.Shape, "PlusLine", { desiredSize: new go.Size(6, 6) })),
            $("Button", {alignment: go.Spot.TopLeft, click: createWeakEntity},
                $(go.Shape, "PlusLine", { desiredSize: new go.Size(6, 6) })),
            // todo add subset
            $("Button", {alignment: go.Spot.BottomLeft, click: createSubset},
                $(go.Shape, "PlusLine", { desiredSize: new go.Size(6, 6) })));

    // adornment for weak entity
    const weakEntityAdornment =
        $(go.Adornment, "Spot",
            $(go.Panel, "Auto",
                $(go.Shape, { fill: null, stroke: "dodgerblue", strokeWidth: 3 }),
                $(go.Placeholder)),
            $("Button", {alignment: go.Spot.TopRight, click: addAttr},
                $(go.Shape, "PlusLine", { desiredSize: new go.Size(6, 6) })));

    // adornment for attribute
    const attributeAdornment =
        $(go.Adornment, "Spot",
            $(go.Panel, "Auto",
                $(go.Shape, { fill: null, stroke: "dodgerblue", strokeWidth: 3 }),
                $(go.Placeholder)),
            $("Button", {alignment: go.Spot.TopRight, click: modifyAttributeClick},
                $(go.Shape, "MinusLine", { desiredSize: new go.Size(6, 6) })),
            );
/*
    All Node(Entity+Attribute) templates
 */
    // strong entity template
    const entityTemplate =
        $(go.Node, "Auto",  // the whole node panel
            {
                locationSpot: go.Spot.Center,
                selectionAdornmentTemplate: normalEntityAdornment,
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

    //relationNodeTemplate

    const relationTemplate =
        $(go.Node, "Auto",  // the whole node panel
            {
                locationSpot: go.Spot.Center,
                selectionAdornmentTemplate: normalEntityAdornment,
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


    // weak entity template
    const weakEntityTemplate =
        $(go.Node, "Auto",  // the whole node panel
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
                    return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 1;
                },
            },
            new go.Binding("location", "location").makeTwoWay(),
            // whenever the PanelExpanderButton changes the visible property of the "LIST" panel,
            // clear out any desiredSize set by the ResizingTool.
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            // define the node's outer shape, which will surround the Table
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

    // subset template
    const subsetTemplate =
        $(go.Node, "Auto",  // the whole node panel
            {
                locationSpot: go.Spot.Center,
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
            curve: go.Link.JumpOver
        },
        $(go.Shape,  // the link shape
            {stroke: "#e8c446", strokeWidth: 2.5 }),
        $(go.Shape,   // the arrowhead
            { toArrow: "OpenTriangle", fill: null }),
    );

    var entityLink = $(go.Link,
        {
            selectionAdorned: true,
            layerName: "Foreground",
            reshapable: true,
            routing: go.Link.AvoidsNodes,
            corner: 5,
            curve: go.Link.JumpOver
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

                    const relationNodeX = (myDiagram.findNodeForKey(e.newValue.from).location.x +
                        myDiagram.findNodeForKey(e.newValue.to).location.x)/2

                    const relationNodeY = (myDiagram.findNodeForKey(e.newValue.from).location.y +
                        myDiagram.findNodeForKey(e.newValue.to).location.y)/2;

                    myDiagram.rollbackTransaction(); //rollback transcation and create new node between e-e
                    myDiagram.model.addNodeData({"key": -1, "name":"test",
                        "location":{"class":"go.Point","x":relationNodeX,"y":relationNodeY},category:"RelationTemplate"});
                    //TODO: change the addNodeDate template with the new designed relation node
                    //TODO: API add node function will be catched by the listener

                    myDiagram.model.addLinkData({"from":firstEntityID,"to":-1, fromText:"1:1",category:"entityLink"});
                    myDiagram.model.addLinkData({"from":secondEntityID,"to":-1, fromText:"1:1",category:"entityLink"})

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
                // todo 需要判断删的是哪个节点
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
    myDiagram.model = go.Model.fromJson(getView(id));
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
        weakEntityData.key = Math.ceil(Math.random()*1000);;
        myDiagram.model.addNodeData(weakEntityData);
        // save();
        // load();
        // new link
        var link = {
            from:myDiagram.model.getKeyForNodeData(selectedEData),
            to:myDiagram.model.getKeyForNodeData(weakEntityData),
            toText:"1:1",
            relation:"for",category: "relationLink"
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
            to:myDiagram.model.getKeyForNodeData(selectedEData),category: "subsetLink"
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