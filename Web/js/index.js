/*
index.js is for the index html

showModel is used to show the model at right dashboard,
    input: selected id and name
    output: model shown at right div

anonymous function:
    1. show all view model: output: all view name and id in the list
    2. slide down and up function
 */

function showModel() {
    /*
    Model template
     */
    const $ = go.GraphObject.make;  // for conciseness in defining templates
    myDiagram = $(go.Diagram, "model",  // must name or refer to the DIV HTML element
        {
            allowDelete: true,
            allowCopy: false,
            initialAutoScale: go.Diagram.Uniform,
            layout: $(go.ForceDirectedLayout, { isInitial: false, isOngoing: false}),
            "draggingTool.dragsLink": false,
            "draggingTool.isGridSnapEnabled": false,
            "undoManager.isEnabled": false,
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
    // entity
    var entityTemplate =
        $(go.Node, "Auto",  // the whole node panel
            {
                locationSpot: go.Spot.Center,
                selectionAdorned: true,
                resizable: false,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                isShadowed: true,
                shadowOffset: new go.Point(3, 3),
                shadowColor: '#afd4fe',
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
            new go.Binding("desiredSize", "visible", v => new go.Size(NaN, NaN)).ofObject("LIST"),
            // define the node's outer shape, which will surround the Table
            $(go.Shape, "RoundedRectangle",
                {
                    fill: 'white',
                    portId: "",
                    stroke: '#afd4fe',
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
        );

    // attribute template
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
                stroke: '#afd4fe',
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
            new go.Binding("text","name")
        )
    );

    // add all template
    // default template
    myDiagram.nodeTemplate = entityTemplate;
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

    /*
    Get the model name and id from list
     */
    const id = getId();
    myDiagram.model = go.Model.fromJson(getView(id));
}

/*
Continue Edit, editModel()
get view id and view name
output: redirect to the drawing.html with the name and id
 */
function editModel(){
    const selected_name =  $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    window.location.href = "drawingView.html?name="+name+"&id="+id;
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

    if (name!=="" && name!=null&&selected_name!==name) {
        $.getJSON("http://localhost:8000/er/view/update?"+"id="+id+"&name="+name, function (res) {
            location.reload();
        }).fail(function (failure) {
            if (failure.status == 400) {
                console.log("fail status:" + failure.status);
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
function deleteModel() {
    const selected_name = $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    $.getJSON("http://localhost:8000/er/view/delete"+"id=" + id, function (res) {
        //reload page
        location.reload();
    }).fail(function (failure) {
        if (failure.status == 400) {
            console.log("fail status:" + failure.status);
        }
    });
}
/*
Create new... jump to drawing html and create the new model
createModel():
input:name
output: redirect to the html and start drawing

 */
function createModel() {
    const name = prompt("Please enter new view name", "Draco");
    if (name != null && name !== "") {
        console.log("test");
        var Obj ={
            name: "teacher"
        }
        Obj = JSON.stringify(Obj);

        $.ajax({
            type : "POST",
            url : "http://127.0.0.1:8000/er/view/create",
            data : Obj,
            success : function(result) {
                console.log("true");
                console.log(result.id);
            }, error : function(result) {
                console.log("false");
            }
        });
    }
}

$(function (){
    // $.getJSON("http://146.169.52.81:8080/er/view/query_all_views", function(result){
    //     const viewList = result.data.viewList;
    //     for (let i = 0; i < viewList.length; ++i) {
    //         tmpString = '<option id = '+viewList[i].id+' value = '+viewList[i].name+'></option>';
    //         $("viewsList").prepend(tmpString);
    //     }
    // });
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

/*
get id
 */
function getId(){
    const selected_name =  $('#vInput').val();
    const id = $('#viewsList option[value="' + selected_name +'"]').attr('id');
    return id;
}