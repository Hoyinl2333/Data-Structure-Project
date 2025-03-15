import pickle
from collections import defaultdict
from typing import Dict, Optional

# 修改为类定义而不是namedtuple
class TrajectoryPoint:
    __slots__ = ['timestamp', 'longitude', 'latitude']
    
    def __init__(self, timestamp: str, longitude: float, latitude: float):
        self.timestamp = timestamp
        self.longitude = longitude
        self.latitude = latitude
    
    def __str__(self):
        return f"时间: {self.timestamp}, 经度: {self.longitude:.6f}, 纬度: {self.latitude:.6f}"
    
    def __repr__(self):
        return self.__str__()

class TaxiDataManager:
    def __init__(self):
        self.data = defaultdict(dict)
        self.total_points = 0

    @classmethod
    def load_from_file(cls, filename: str):
        print(f"正在从文件加载数据: {filename}")
        manager = cls()
        with open(filename, 'rb') as f:
            manager.data = defaultdict(dict, pickle.load(f))
            manager.total_points = sum(len(timestamps) for timestamps in manager.data.values())
        print(f"数据加载完成！共加载 {manager.total_points} 个轨迹点")
        return manager

    def count_taxis_in_area(self, top_left: tuple, bottom_right: tuple, start_time: str, end_time: str) -> int:
        count = set()  # 使用集合来避免重复计数
        for taxi_id, timestamps in self.data.items():
            for timestamp, point in timestamps.items():
                if start_time <= timestamp <= end_time:
                    if (top_left[0] <= point.longitude <= bottom_right[0] and
                            top_left[1] >= point.latitude >= bottom_right[1]):
                        count.add(taxi_id)
        return len(count)

# 加载数据
if __name__ == "__main__":
    file_path = "taxi_data_hash.pkl"
    manager = TaxiDataManager.load_from_file(file_path)

    # 查询函数示例
    def query_taxis_in_area(manager: TaxiDataManager, top_left: tuple, bottom_right: tuple, start_time: str, end_time: str):
        count = manager.count_taxis_in_area(top_left, bottom_right, start_time, end_time)
        print(f"在时间范围 {start_time} 到 {end_time} 内，")
        print(f"通过区域 {top_left} 到 {bottom_right} 的出租车数量: {count}")

    # 示例查询
    top_left = (116.5, 39.8)  # 矩形左上角坐标 (经度, 纬度)
    bottom_right = (116.8, 39.6)  # 矩形右下角坐标 (经度, 纬度)
    start_time = '2008-02-02 15:00:00'  # 开始时间
    end_time = '2008-02-02 16:00:00'  # 结束时间

    query_taxis_in_area(manager, top_left, bottom_right, start_time, end_time)