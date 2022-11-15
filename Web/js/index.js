/*
index.js is for the index html

showModel is used to show the model at right dashboard,
    input: selected id and name
    output: model shown at right div

anonymous function:
    1. show all view model: output: all view name and id in the list
    2. slide down and up function
 */

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


// Common color
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
        editable: false,
    }
}


var entityCounter = 0;
var attributeCounter = 0;
var weakEntityCounter = 0;
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
const ERLinkCard = "1:1"
const ERLinkCategory = "entityLink";
const EWLinkCategory = "weakLink";
const relationNodeName = "test";
const relationNodeCategory = "relation";
let ERLinkCreateVerify =new Set(); // Value:"fromEntityIDRelationID"

/*
Constant
 */

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
/*
Link validation for index model
 */
function isLinkValidIndex(fromNode, fromGraphObject, toNode, toGraphObject) {
    return fromNode.findLinksTo(toNode).count + toNode.findLnksTo(fromNode).count < 1;}

/*
define model
 */
function defineModel(){

    const $ = go.GraphObject.make;  // for conciseness in defining templates
    myDiagram = $(go.Diagram, "model",  // must name or refer to the DIV HTML element
        {
            allowDelete: false,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            layout: $(go.ForceDirectedLayout, {isInitial: true, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "undoManager.isEnabled": false,
            "maxSelectionCount": 1,
            "linkingTool.linkValidation": isLinkValidIndex,
            allowMove:false
        })


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
    myDiagram.linkTemplateMap = linkTemplateMap;

    myDiagram.model = new go.GraphLinksModel(
        {
            copiesArrays: true,
            copiesArrayObjects: true,
            nodeDataArray: [],
            linkDataArray: []
        });
}

/*
get view id
 */
function getId(){
    const selected_name =  $('#vInput').val();
    return $('#viewsList option[value="' + selected_name + '"]').attr('id');
}

/*
Show model at rignt
 */
function showSchema() {
    // Get the model name and id from list
    const id = getId();
    myDiagram.model = go.Model.fromJson(getSchema(id));
    console.log(getSchema(id));

}



/*
Continue Edit, editModel()
get view id and view name
output: redirect to the drawing.html with the name and id
 */
function editSchema(){
    const selected_name =  $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    window.location.href = "drawingView.html?name="+selected_name+"&id="+id;
}

/*
Rename as, rename the current model, and the model list will also be changed
renameModel():
get view id and view name
output: model list will be refresh
 */
function renameModel(){
    const selected_name =  $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    const name=prompt("Please enter new view name",selected_name);

    if (name!==""&& name!=null&&selected_name!==name) {
        let Obj ={
            viewID:id,
            name: name
        }
        Obj = JSON.stringify(Obj);

        $.ajax({
            type : "POST",
            headers: { "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
            url : "http://146.169.52.81:8080/er/schema/update",
            contentType:"application/json",
            data : Obj,
            success : function(result) {
                window.location.reload();
            }, error : function(result) {
            }
        });
    }
}

/*
delete: delete this model
deleteModel():
get view id and view name
output: model list will be refresh
*/
function deleteSchema() {
    const selected_name = $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');

    var Obj ={
        id: id
    }
    Obj = JSON.stringify(Obj);
    $.ajax({
        type : "POST",
        url : "http://146.169.52.81:8080/er/schema/delete",
        data : Obj,
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        success : function(result) {
            console.log("true")
            window.location.reload();
        }, error : function(result) {
            console.log("false");
        }
    });
}


/*
Create new... jump to drawing html and create the new model
createModel():
input:name
output: redirect to the html and start drawing

 */
function createSchema() {
    const name = prompt("Please enter new view name", "Draco");
    if (name != null && name !== "") {
        var Obj ={
            name: name
        }
        Obj = JSON.stringify(Obj);
        $.ajax({
            type : "POST",
            url : "http://146.169.52.81:8080/er/schema/create",
            data : Obj,
            headers: { "Access-Control-Allow-Origin": "*",
                "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
            contentType: "application/json",
            success : function(result) {
                window.location.href = "drawingView.html?name="+name+"&id="+result.data.id;
            }, error : function(result) {
                console.log("false");
            }
        });
    }
}

/*
Append views into view list
 */
function appendModel(){
    $.ajax({
        type : "GET",
        async: false,
        url : "http://146.169.52.81:8080/er/schema/query_all_schemas",
        headers: { "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":"Origin, X-Requested-With, Content-Type, Accept"},
        contentType: "application/json",
        data : {},
        success : function(result) {
            let options='';
            const views= result.data.schemaList;
             for (let i = 0; i < views.length; i++) {
                 options += '<option id =' + views[i].id+ '  value="' + views[i].name + '" />';

        }
             console.log(options)
            document.getElementById('viewsList').innerHTML = options;
        }, error : function(result) {
            console.log("false");
        }
    });

}

window.addEventListener('DOMContentLoaded', appendModel);
window.addEventListener('DOMContentLoaded', defineModel);

/*
HTML list slide down
 */
$(function (){
    //hide all subtitle
    $(".nav_menu").each(function (){
        $(this).children(".nav_content").hide();

    });
    //add the click event of all the content
    $(".nav_title").each(function (){
        $(this).click(function (){
            var nav = $(this).parent(".nav_menu").children(".nav_content");
            if (nav.css("display")!=="none"){
                nav.slideUp();
            }else{
                nav.slideDown();
            }
        });
    });

});
