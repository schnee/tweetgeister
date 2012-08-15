<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${hashtag} Tweetgeist</title>
    <link type="text/css" rel="stylesheet" href="ex.css?3.2"/>
    <script type="text/javascript" src="./protovis-r3.2.js"></script>
    <script type="text/javascript" src="${jsname}.js"></script>
    <style type="text/css">

#fig {
  width: 800px;
  height: 800px;
}

    </style>
  </head>
  <body>
  
    <div><span>A clustering of ${tweetCount} tweets related to the "${hashtag}" hashtag</span><br />
  <span>You must use modern versions of Firefox, Chrome or Safari to view the visualization</span><br />
  <span>Mousewheel to zoom, hover in <em>circle</em> to see the Tweet, <em>double-click</em> dot to open the Tweet</span><br /></div>
  <div><a href="http://www.schneeworld.com/roller/schnee/entry/what_the_tweetgeister">More information</a></div>
  <div id="center"><div id="fig">
    <script type="text/javascript+protovis">

/* For pretty number formatting. */
var format = pv.Format.number();

var legend = new pv.Panel()
    .width(800)
    .height(20)
  .add(pv.Bar)
    .data(pv.range(0, 1, 1/100))
    .left(function() this.index * 8)
    .width(8)
    .height(20)
    .fillStyle(pv.Scale.linear(0, .5, 1).range('red', 'yellow', 'green'))
	.root.render();

var txt = new pv.Panel()
	.width(800)
	.height(20)
.add(pv.Label)
    .left(0)
    .bottom(2)
    .text("${oldest}")
.add(pv.Label)
	.left(800)
	.bottom(2)
	.textAlign("right")
    .text("${youngest}")
	.root.render();

var vis = new pv.Panel()
    .width(800)
    .height(800)
    .event("mousewheel", pv.Behavior.zoom())
	.cursor("pointer")
	.margin(2);

var pack = vis.add(pv.Layout.Pack)
    .nodes(pv.dom(root).leaf(function(d) d.text).root("${root}").nodes());

var c =  pv.Scale.linear(0, 50, 100).range('red', 'yellow', 'green');

pack.node.add(pv.Dot)
    .fillStyle(function(d) d.firstChild ? "rgba(54,50,52, .25)" : c(d.nodeValue.age))
    .title(function (d) { return d.firstChild ? d.nodeName : d.nodeValue.text; })
	.event("dblclick", function(d) { if(!d.firstChild) window.open('http://www.twitter.com/#!/' + d.nodeValue.fromUser + '/status/' + d.nodeName);} )
    .lineWidth(1);

vis.render();

    </script>
  </div></div></body>
</html>
