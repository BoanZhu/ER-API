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
        editable: true,
    }
}

const limitConnectNode = new Set([subsetEntityNodeCategory,weakEntityNodeCategory,"Attribute","relation_attribute"]);

function isAllowReconnect(existinglink, newnode, newport, toend){
    const toNodeCategory = existinglink.toNode.category;
    const fromNodeCategory = existinglink.fromNode.category;
    const newNodeCategory = newnode.category;
    if (limitConnectNode.has(toNodeCategory)||limitConnectNode.has(fromNodeCategory)) {
        if (existinglink.fromNode.key !== newnode.key && existinglink.toNode.key !== newnode.key) {
            return false;
        }
    } else if(toNodeCategory==="relation"){
        if (newnode.findLinksTo(existinglink.toNode).count ===1 && !toend && existinglink.fromNode.key!==newnode.key){
            //entity already has a link
            return false;
        }
        else if (newNodeCategory !=='entity' && !toend){
            return false;
        }
        else if(toend&&newNodeCategory==='relation'){
            //the number of connected entity
             let counter = 0;
            existinglink.toNode.findNodesConnected().each(function (node){
                if (node.category ===entityNodeCategory) counter = counter+1;
            });
            if (counter<=2){
                const deteleNode = myDiagram.findNodeForKey(existinglink.toNode.key);
                go.RelinkingTool.prototype.reconnectLink.call(this, existinglink, newnode, newport, toend);
                myDiagram.startTransaction();
                myDiagram.remove(deteleNode);
                myDiagram.commitTransaction("deleted node");
                return;
            }
        } else if(toend&&newNodeCategory!=='relation'){
            return false;
        }
    }
    return go.RelinkingTool.prototype.reconnectLink.call(this, existinglink, newnode, newport, toend);
}

function isLinkValid(fromNode, fromGraphObject, toNode, toGraphObject) {
    var fromNodeCategory = fromNode.category;
    var toNodeCategory = toNode.category;
    var flag=true;

    if (fromNodeCategory==="relation"&&toNodeCategory==="relation"){return false}
    if (limitConnectNode.has(fromNodeCategory) || limitConnectNode.has(toNodeCategory)) return false;
    if (fromNodeCategory===entityNodeCategory&&toNodeCategory===entityNodeCategory){
        fromNode.findNodesConnected().each(function(n) {
            if (n.category === "relation") {
                n.findNodesInto().each(function (t) {
                    if(t.key === toNode.key){
                         flag = false;
                         return false;
                    }
            });}
        });
    }
    return fromNode.findLinksTo(toNode).count + toNode.findLinksTo(fromNode).count < 1 &&flag;
}

//
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


