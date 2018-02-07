var myDiagram =
    go.GraphObject.make(go.Diagram, "relationshipDiagram",
        {
            initialContentAlignment: go.Spot.Center, // center Diagram contents
            "undoManager.isEnabled": true, // enable Ctrl-Z to undo and Ctrl-Y to redo
            layout: go.GraphObject.make(go.TreeLayout, // specify a Diagram.layout that arranges trees
                {angle: 90, layerSpacing: 75})
        });

// the template we defined earlier
myDiagram.nodeTemplate =
    go.GraphObject.make(go.Node, "Horizontal",
        {background: "#44CCFF", cursor: "pointer"},
        go.GraphObject.make(go.TextBlock, "",
            {margin: 12, stroke: "white", font: "bold 18px Open Sans"},
            new go.Binding("text", "hashtag")),
        go.GraphObject.make(go.TextBlock, "",
            {margin: 12, stroke: "white", font: "12px Open Sans"},
            new go.Binding("text", "totalNumber"))
    );

// define a Link template that routes orthogonally, with no arrowhead
myDiagram.linkTemplate =
    go.GraphObject.make(go.Link,
        {routing: go.Link.Orthogonal, corner: 6},
        go.GraphObject.make(go.Shape, {strokeWidth: 5, stroke: "silver"})); // the link shape

myDiagram.addDiagramListener("ObjectSingleClicked",
    function (e) {
        var part = e.subject.part;
        angular.element(document.getElementById('searchByDateControllerId')).scope().openRelationDiagram(part.data.hashtag);
    });