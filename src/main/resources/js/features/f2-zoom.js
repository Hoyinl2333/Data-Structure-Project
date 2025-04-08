/**
 * F2: 地图缩放功能
 * 根据地图缩放级别调整轨迹点的大小，实现缩放时轨迹的自适应展示
 * 当地图放大时，点变小；当地图缩小时，点变大，保证在不同缩放级别下的可见性
 * @param {number} zoomLevel - 地图当前的缩放级别
 */
function adjustDotsSize(zoomLevel) {
    var size = Math.max(4, 12 - zoomLevel); // 根据缩放级别动态计算点的大小
    for (var i = 0; i < overlays.length; i++) {
        if (overlays[i].setSize) {
            overlays[i].setSize(size);
        }
    }
}