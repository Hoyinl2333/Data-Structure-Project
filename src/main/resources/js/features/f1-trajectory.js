/**
 * F1: 出租车轨迹查询
 * 根据出租车ID获取轨迹数据，并在地图上显示
 */
function fetchTaxiTrajectory() {
    var taxiId = document.getElementById('taxiId').value;
    if (taxiId === '') {
        alert('请输入出租车ID');
        return;
    }

    var resultDiv = document.getElementById('result');
    resultDiv.innerHTML = '<p>正在加载数据...</p>';

    clearOverlays();

    var url = `http://localhost:8080/taxi/${taxiId}`;
    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error('网络响应异常');
            return response.json();
        })
        .then(data => {
            resultDiv.innerHTML = `<p>成功获取出租车 ${taxiId} 的轨迹数据，共 ${data.length} 个点</p>`;

            var points = [];
            data.forEach(record => {
                points.push(new BMapGL.Point(record.longitude, record.latitude));
            });

            convertCoordinates(points, function(data) {
                if (data.status === 0) {
                    data.points.forEach(point => {
                        addDotToMap(point);
                    });

                    if (data.points.length > 0) {
                        map.setCenter(data.points[0]);
                    }
                }
            });
        })
        .catch(error => {
            resultDiv.innerHTML = `<p>查询出错：${error.message}</p>`;
            clearOverlays();
        });
}

// 添加事件监听器
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('queryTrajectoryBtn').addEventListener('click', fetchTaxiTrajectory);
});