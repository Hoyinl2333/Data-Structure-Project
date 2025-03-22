# Data-Structure-Project
Data Structure Project: Taxi Trajectory Analysis

## Be Attention
- download data and store it under data.
- maven configuration and reloading
- bug existence

## Project Structure
```text
taxitrajectory/
│── src/
│   ├── main/
│   │   ├── java/com/codex/taxitrajectory/
│   │   │   ├── controller/      # 控制层（处理 HTTP 请求）
│   │   │   ├── service/         # 业务逻辑层（数据处理与算法）
│   │   │   ├── repository/      # 数据访问层（数据管理，如 CSV 解析）
│   │   │   ├── model/           # 实体类（数据结构）
│   │   │   ├── utils/           # 工具类（CSV 解析、坐标转换、exception等） 
│   │   │   ├── TaxitrajectoryApplication.java  # 入口类
│   ├── resources/
│   │   ├── data/                # 存放 CSV 数据文件
│   │   ├── application.yml      # 配置文件
│   ├── test/                    # 测试代码
│── pom.xml                       # Maven 依赖管理
│── README.md                     # 项目说明
```