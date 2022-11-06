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


