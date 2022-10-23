function init() {
    const $ = go.GraphObject.make;  // for conciseness in defining templates
    myDiagram = $(go.Diagram, "myDiagramDiv",  // must name or refer to the DIV HTML element
        {
            allowDelete: true,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            layout: $(go.ForceDirectedLayout, { isInitial: false, isOngoing: false}),
            "undoManager.isEnabled": true,
            "draggingTool.dragsLink": true,
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
            editable: false,
        }
    }

    // the template for each attribute in a node's array of item data
    // var itemTempl =
    //     $(go.Panel, "Horizontal",
    //         $(go.Shape,
    //             { desiredSize: new go.Size(15, 15), strokeJoin: "round", strokeWidth: 3, stroke: null, margin: 2 },
    //             new go.Binding("figure", "figure"),
    //             new go.Binding("fill", "color"),
    //             new go.Binding("stroke", "color")),
    //         $(go.TextBlock, textStyle(),
    //             {
    //                 stroke: "#333333",
    //                 font: "bold 14px sans-serif"
    //             },
    //             new go.Binding("text", "name").makeTwoWay())
    //     );

    // define the Node template, representing an entity
    myDiagram.nodeTemplate =
        $(go.Node, "Auto",  // the whole node panel
            {
                selectionAdorned: true,
                resizable: true,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                fromSpot: go.Spot.AllSides,
                toSpot: go.Spot.AllSides,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: colors.lightblue
            },
            new go.Binding("location", "location").makeTwoWay(),
            // whenever the PanelExpanderButton changes the visible property of the "LIST" panel,
            // clear out any desiredSize set by the ResizingTool.
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            // define the node's outer shape, which will surround the Table
            $(go.Shape, "RoundedRectangle",
                { fill: 'white', stroke: colors.lightblue, strokeWidth: 3 }),
            // $("Button",
            //     {
            //         alignment: go.Spot.BottomRight,
            //         click: addAttribute
            //     },  // this function is defined below
            //     new go.Binding("visible", "", a => !a.diagram.isReadOnly).ofObject(),
            //     $(go.Shape, "PlusLine", { desiredSize: new go.Size(10, 10)})
            // ),

            // the table header
            //todo list去掉了
            $(go.Panel, "Table",
                { margin: 8, stretch: go.GraphObject.Fill },
                $(go.RowColumnDefinition, { row: 0, sizing: go.RowColumnDefinition.None }),
                $(go.TextBlock,textStyle(),
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(5, 24, 5, 2),  // leave room for Button
                        font: "bold 16px sans-serif"
                    },
                    new go.Binding("text", "key").makeTwoWay()),
                // // the collapse/expand button
                // $("PanelExpanderButton", "LIST",  // the name of the element whose visibility this button toggles
                //     { row: 0, alignment: go.Spot.TopLeft }),
                // // the list of Panels, each showing an attribute
                // $(go.Panel, "Vertical",
                //     {
                //         name: "LIST",
                //         row: 1,
                //         padding: 3,
                //         alignment: go.Spot.TopLeft,
                //         defaultAlignment: go.Spot.Left,
                //         stretch: go.GraphObject.Horizontal,
                //         itemTemplate: itemTempl
                //     },
                //     new go.Binding("itemArray", "items").makeTwoWay())

            ) // end Table Panel
        );


    // define the Link template, representing a relationship
    myDiagram.linkTemplate = $(go.Link,  // the whole link panel
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
        $(go.TextBlock, textStyle(), // the "from" label
            {
                textAlign: "center",
                font: "bold 14px sans-serif",
                stroke: "#1967B3",
                segmentIndex: 0,
                segmentOffset: new go.Point(NaN, NaN),
                segmentOrientation: go.Link.OrientUpright
            },
            new go.Binding("text", "text").makeTwoWay()),
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

    myDiagram.model = new go.GraphLinksModel(
        {
            copiesArrays: true,
            copiesArrayObjects: true,
            nodeDataArray: [],
            linkDataArray: []
        });

    function addRelationship(){

    }

    function addAttribute(e, obj) {
        var adorn = obj.part;
        if (adorn === null) return;
        e.handled = true;
        var arr = adorn.adornedPart.data.items;
        myDiagram.startTransaction("add item");
        myDiagram.model.addArrayItem(arr, {});
        myDiagram.commitTransaction("add item");
    }

    //palette
    // myDiagram.nodeTemplateMap.add("Entity",myDiagram.nodeTemplate);

    // var palette =
    //     $(go.Palette, "myPaletteDiv",  // create a new Palette in the HTML DIV element
    //         {
    //             // share the template map with the Palette
    //             nodeTemplateMap: myDiagram.nodeTemplateMap,
    //             autoScale: go.Diagram.Uniform  // everything always fits in viewport
    //         });
    //
    // // bind node and relation to the left
    // palette.model.nodeDataArray = [
    //     //entity
    //     {key: "Entity", items: [{"name":"template","isKey":true,"figure":"Decision","color":"#be4b15"}]},
    //     //relation
    // ];

    load()
}  // end init
