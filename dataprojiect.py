import os
import time
import pickle
from collections import namedtuple, defaultdict
import multiprocessing
from typing import Dict, Optional
import msgpack  # 需要先安装: pip install msgpack

# 定义轨迹点数据结构
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
    
    def is_valid(self) -> bool:
        """检查数据是否有效，添加更多过滤条件"""
        return (
            # 基本检查
            self.longitude != 0 and 
            self.latitude != 0 and
            
            # 中国范围检查
            73 <= self.longitude <= 135 and
            18 <= self.latitude <= 53 
        )

class TaxiDataManager:
    """使用哈希表存储出租车轨迹数据"""
    def __init__(self):
        # 使用嵌套字典存储数据: {taxi_id: {timestamp: TrajectoryPoint}}
        self.data: Dict[int, Dict[str, TrajectoryPoint]] = defaultdict(dict)
        self.total_points = 0
        self.filtered_points = 0  # 记录被过滤的点数
   
    def get_all_trajectory_points(self, taxi_id: int) -> Optional[Dict[str, TrajectoryPoint]]:
        """获取某个出租车编号的所有轨迹点"""
        if taxi_id in self.data:
            return self.data[taxi_id]
        else:
            return None
    
    def insert(self, taxi_id: int, timestamp: str, longitude: float, latitude: float):
        """插入轨迹点数据，并进行数据清洗"""
        point = TrajectoryPoint(timestamp, longitude, latitude)
        
        # 只插入有效数据
        if point.is_valid():
            # 检查是否已存在该时间戳的点
            if timestamp not in self.data[taxi_id]:
                self.total_points += 1  # 只在新增点时增加计数
            self.data[taxi_id][timestamp] = point
        else:
            self.filtered_points += 1
    
    def search(self, taxi_id: int, timestamp: str) -> Optional[TrajectoryPoint]:
        """查询轨迹点"""
        return self.data.get(taxi_id, {}).get(timestamp)

    def save_to_file(self, filename: str):
        """保存数据到文件"""
        print(f"正在保存数据到文件: {filename}")
        
        # 验证点数
        self.update_total_points()        
        # 保存数据
        with open(filename, 'wb') as f:
            saved_data = dict(self.data)
            pickle.dump(saved_data, f, protocol=pickle.HIGHEST_PROTOCOL)
            
            # 验证保存的数据

        print(f"数据保存完成！共保存 {self.total_points} 个有效轨迹点")

    @classmethod
    def load_from_file(cls, filename: str) -> 'TaxiDataManager':
        """从文件加载数据"""
        print(f"正在从文件加载数据: {filename}")
        manager = cls()
        
        try:
            with open(filename, 'rb') as f:
                raw_data = pickle.load(f)
                print(f"从文件读取的原始数据点数: {sum(len(points) for points in raw_data.values())}")
                
                # 转换为defaultdict并检查数据
                manager.data = defaultdict(dict)
                for taxi_id, points in raw_data.items():
                    for timestamp, point in points.items():
                        if isinstance(point, TrajectoryPoint):
                            manager.data[taxi_id][timestamp] = point
                        else:
                            print(f"发现无效数据点: taxi_id={taxi_id}, timestamp={timestamp}, point={point}")
                
                # 更新实际的点数

            return manager
        
        except Exception as e:
            print(f"加载数据时出错: {str(e)}")
            raise

    def update_total_points(self):
        """更新总点数（仅在需要验证时使用）"""
        actual_total = sum(len(points) for points in self.data.values())
        if actual_total != self.total_points:
            print(f"警告：点数不一致！计数器：{self.total_points}，实际点数：{actual_total}")
            print("发现重复轨迹点，正在修正计数...")
            self.total_points = actual_total

def process_file(file_path: str, manager: TaxiDataManager):
    """处理单个文件"""
    try:
        with open(file_path, "r", encoding='utf-8') as file:
            for line in file:
                try:
                    parts = line.strip().split(",")
                    if len(parts) == 4:
                        taxi_id = int(parts[0])
                        timestamp = parts[1]
                        longitude = float(parts[2])
                        latitude = float(parts[3])
                        manager.insert(taxi_id, timestamp, longitude, latitude)
                except ValueError:
                    continue
    except Exception as e:
        print(f"处理文件出错 {file_path}: {str(e)}")

def load_taxi_data(data_folder: str, save_path: str = None) -> TaxiDataManager:
    """加载出租车数据"""
    print("开始加载出租车数据...")
    
    # 如果存在已保存的数据文件，直接加载
    if save_path and os.path.exists(save_path):
        start_time = time.time()
        manager = TaxiDataManager.load_from_file(save_path)
        print(f"从文件加载完成！耗时: {time.time() - start_time:.2f}秒")
        return manager

    # 创建新的数据管理器
    manager = TaxiDataManager()
    
    try:
        if not os.path.exists(data_folder):
            raise FileNotFoundError(f"数据文件夹不存在: {data_folder}")

        file_paths = [os.path.join(data_folder, f) for f in os.listdir(data_folder)]
        total_files = len(file_paths)
        
        start_time = time.time()
        print(f"开始处理 {total_files} 个文件...")

        # 处理所有文件
        for i, file_path in enumerate(file_paths, 1):
            process_file(file_path, manager)
            
            # 显示进度
            if i % 100 == 0:
                elapsed_time = time.time() - start_time
                speed = manager.total_points / elapsed_time if elapsed_time > 0 else 0
                print(f"已处理 {i}/{total_files} 个文件，"
                      f"有效点数: {manager.total_points}，"
                      f"过滤点数: {manager.filtered_points}，"
                      f"速度: {speed:.2f} 点/秒")

        total_time = time.time() - start_time
        print(f"数据加载完成！共处理 {total_files} 个文件")
        print(f"有效轨迹点: {manager.total_points}")
        print(f"过滤轨迹点: {manager.filtered_points}")
        print(f"总耗时: {total_time:.2f} 秒")

        # 在保存前更新总点数
        manager.update_total_points()
        print(f"数据加载完成！共处理 {total_files} 个文件")
        print(f"有效轨迹点: {manager.total_points}")
        print(f"过滤轨迹点: {manager.filtered_points}")
        print(f"总耗时: {total_time:.2f} 秒")

        # 保存处理后的数据
        if save_path:
            manager.save_to_file(save_path)

        return manager

    except Exception as e:
        print(f"加载数据时发生错误: {str(e)}")
        raise

if __name__ == "__main__":
    data_folder = "C:\\Users\\61082\\Desktop\\project\\release\\taxi_log_2008_by_id"
    save_path = "taxi_data_hash.pkl"  # 数据持久化文件路径
    
    # 加载数据
    manager = load_taxi_data(data_folder, save_path)
    
    # 测试查询
    try:
        taxi_id = 9757
        timestamp = '2008-02-02 13:40:00'
        
        start_time = time.time()
        result = manager.search(taxi_id, timestamp)
        query_time = time.time() - start_time
        
        if result:
            print(f"查询结果: {result}")  # 现在会显示更友好的格式
        else:
            print("未找到匹配的轨迹点")
        print(f"查询耗时: {query_time:.6f}秒")
        
        # 测试查询出租车的所有轨迹点
        print("\n查询出租车编号 9757 的所有轨迹点：")
        all_points = manager.get_all_trajectory_points(taxi_id)
        if all_points:
            sorted_points = sorted(all_points.items(), key=lambda x: x[0])  # 按时间戳排序
            for ts, point in sorted_points:
                print(f"{ts}: {point}")
        else:
            print("未找到该出租车编号的轨迹点")
    except Exception as e:
        print(f"查询出错: {str(e)}")