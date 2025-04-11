/**
 * F4: 区域车流密度分析
 * 分析指定区域和时间段内的车流密度
 */
function analyzeDensity() {
    var startTime = document.getElementById('densityStartTime').value;
    var endTime = document.getElementById('densityEndTime').value;
    var topLeftLng = document.getElementById('densityTopLeftLng').value;
    var topLeftLat = document.getElementById('densityTopLeftLat').value;
    var bottomRightLng = document.getElementById('densityBottomRightLng').value;
    var bottomRightLat = document.getElementById('densityBottomRightLat').value;
    var gridRadius = document.getElementById('gridRadius').value;

    // 验证输入
    if (!startTime || !endTime || !topLeftLng || !topLeftLat || !bottomRightLng || !bottomRightLat || !gridRadius) {
        alert('请填写完整的分析条件');
        return;
    }

    var resultDiv = document.getElementById('result');
    resultDiv.innerHTML = '<p>正在分析区域车流密度...</p>';

    // 构建请求参数
    var params = {
        startTime: startTime,
        endTime: endTime,
        topLeftLongitude: topLeftLng,
        topLeftLatitude: topLeftLat,
        bottomRightLongitude: bottomRightLng,
        bottomRightLatitude: bottomRightLat,
        gridRadius: gridRadius
    };

    // 构建查询字符串
    var queryString = Object.keys(params)
       .map(key => encodeURIComponent(key) + '=' + encodeURIComponent(params[key]))
       .join('&');

    // 后端 API 的 URL
    var apiUrl = `http://localhost:8080/taxi/densityAnalysis?${queryString}`;

    // 发起 API 请求
    fetch(apiUrl)
      .then(response => {
            if (!response.ok) {
                throw new Error('网络响应异常');
            }
            return response.json();
        })
      .then(data => {
            // 假设服务器返回的是一个包含每个网格车流密度的对象
            // 这里简单地将结果以 JSON 字符串的形式显示在页面上
            resultDiv.innerHTML = `<p>区域车流密度分析结果: ${JSON.stringify(data)}</p>`;
        })
      .catch(error => {
            resultDiv.innerHTML = `<p>查询出错：${error.message}</p>`;
        });
}

// 添加事件监听器
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('densityAnalysisBtn').addEventListener('click', analyzeDensity);
});