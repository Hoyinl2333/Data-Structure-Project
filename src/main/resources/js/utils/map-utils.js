// 全局变量
var map;
var overlays = [];

// 自定义覆盖物类
function CustomOverlay(point, color = 'red'){
    this._point = point;
    this._color = color;
}

CustomOverlay.prototype = new BMapGL.Overlay();

CustomOverlay.prototype.initialize = function(map){
    this._map = map;
    var div = document.createElement("div");
    div.className = "dot";
    div.style.position = "absolute";
    div.style.backgroundColor = this._color;
    map.getPanes().labelPane.appendChild(div);
    this._div = div;
    return div;
};

CustomOverlay.prototype.draw = function(){
    var position = this._map.pointToOverlayPixel(this._point);
    this._div.style.left = position.x - 4 + "px";
    this._div.style.top = position.y - 4 + "px";
};

CustomOverlay.prototype.setSize = function(size){
    this._div.style.width = size + "px";
    this._div.style.height = size + "px";
    this.draw();
};

// 初始化地图
function initMap() {
    map = new BMapGL.Map("container");
    var centerPoint = new BMapGL.Point(116.404, 39.915);
    map.centerAndZoom(centerPoint, 11);
    map.enableScrollWheelZoom(true);

    map.addControl(new BMapGL.ScaleControl());
    map.addControl(new BMapGL.ZoomControl());
    map.addControl(new BMapGL.NavigationControl());

    // 监听地图缩放事件
    map.addEventListener("zoomend", function () {
        adjustDotsSize(map.getZoom());
    });
}

// 坐标转换
function convertCoordinates(points, callback) {
    if (!points || points.length === 0) {
        callback({ status: -1, points: [] });
        return;
    }

    var convertor = new BMapGL.Convertor();
    var batchPoints = [];
    var results = [];

    for (let i = 0; i < points.length; i += 10) {
        batchPoints.push(points.slice(i, Math.min(i + 10, points.length)));
    }

    function processBatch(index) {
        if (index >= batchPoints.length) {
            callback({ status: 0, points: results });
            return;
        }

        convertor.translate(batchPoints[index], 3, 5, function(data) {
            if (data.status === 0) {
                results = results.concat(data.points);
            } else {
                results = results.concat(batchPoints[index]);
            }

            processBatch(index + 1);
        });
    }

    processBatch(0);
}

// 清除覆盖物
function clearOverlays() {
    for (var i = 0; i < overlays.length; i++) {
        map.removeOverlay(overlays[i]);
    }
    overlays = [];
}

// 添加点到地图
function addDotToMap(point, color = 'red') {
    var dotOverlay = new CustomOverlay(point, color);
    map.addOverlay(dotOverlay);
    overlays.push(dotOverlay);
}